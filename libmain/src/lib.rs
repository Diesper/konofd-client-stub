#![allow(unsafe_op_in_unsafe_fn, dead_code)]

mod asm;
mod jni_util;
mod maps;
mod off_thread;
mod util;

use std::arch::naked_asm;
use std::collections::{HashMap, HashSet};
use std::ffi::{CStr, CString};
use std::os::raw::{c_char, c_int, c_void};
use std::sync::Mutex;
use std::sync::atomic::{AtomicBool, AtomicPtr, Ordering};
use std::time::{Duration, Instant};
use std::{iter, mem, panic, ptr, str, thread};

use android_logger::Config;
use jni_sys::{
  JNI_TRUE, JNI_VERSION_1_6, JNIEnv, JNINativeMethod, JavaVM, jboolean, jclass, jint, jstring,
};
use log::{LevelFilter, debug, error, info, trace, warn};

use crate::asm::{encode_add_immediate, encode_adrp, encode_br};
use crate::jni_util::show_toast;
use crate::maps::{
  MemoryRegion, get_suitable_regions, read_memory_regions, search_byte_sequence, write_to_memory,
};
use crate::off_thread::{OFF_FIELD, OFF_STATIC_FIELDS, OFF_TYPEINFO};
use crate::util::{
  bytes_to_human_readable, pad_to_utf16_bytes, split_bytes_by_sizes, split_string_by_sizes,
  str_to_utf16_bytes,
};

#[allow(non_camel_case_types)]
type JNI_OnLoad_t = unsafe extern "C" fn(vm: *mut JavaVM, reserved: *mut c_void) -> jint;

#[allow(non_camel_case_types)]
type JNI_OnUnload_t = unsafe extern "C" fn(vm: *mut JavaVM, reserved: *mut c_void);

static LIBUNITY_ADDR: AtomicPtr<c_void> = AtomicPtr::new(ptr::null_mut());
static LIBIL2CPP_ADDR: AtomicPtr<c_void> = AtomicPtr::new(ptr::null_mut());

unsafe fn open_and_call_onload(
  env: *mut JNIEnv,
  path: *const c_char,
  name: *const c_char,
  module_address: &AtomicPtr<c_void>,
) {
  let path_str = CStr::from_ptr(path).to_string_lossy();
  let name_str = CStr::from_ptr(name).to_string_lossy();

  debug!("open_and_call_onload({}, {})", path_str, name_str);

  if module_address.load(Ordering::Acquire).is_null() {
    let mut vm: *mut JavaVM = ptr::null_mut();
    if ((**env).v1_6.GetJavaVM)(env, &mut vm) != 0 {
      ((**env).v1_6.FatalError)(env, c"Unable to retrieve Java VM".as_ptr());
    }

    let mut full_path = Vec::with_capacity(2048);
    let format_str = format!("{}/{}", path_str, name_str);
    full_path.extend_from_slice(format_str.as_bytes());

    let mut module = libc::dlopen(full_path.as_ptr() as *const c_char, libc::RTLD_NOW);

    if module.is_null() {
      // Try loading just by name
      module = libc::dlopen(name, libc::RTLD_NOW);

      if module.is_null() {
        let error = CStr::from_ptr(libc::dlerror()).to_string_lossy();
        let error_msg = format!("Unable to load library: {} [{}]", path_str, error);

        let c_error_msg = CString::new(error_msg).unwrap();
        ((**env).v1_6.FatalError)(env, c_error_msg.as_ptr());
      }
    }

    let on_load: JNI_OnLoad_t = std::mem::transmute(libc::dlsym(module, c"JNI_OnLoad".as_ptr()));
    let result = on_load(vm, ptr::null_mut());
    if result >= 65543 {
      ((**env).v1_6.FatalError)(env, c"Unsupported VM version".as_ptr());
    }

    info!(
      "loaded module {} with handle: {:p}",
      name_str, module as *const c_void
    );
    module_address.store(module, Ordering::Release);
  }
}

pub unsafe extern "C" fn load(
  env: *mut JNIEnv,
  _activity_object: jni_sys::jobject,
  path: jstring,
) -> jboolean {
  panic::set_hook(Box::new(|panic_info| {
    let payload = panic_info.payload();

    let msg = if let Some(s) = payload.downcast_ref::<&str>() {
      *s
    } else if let Some(s) = payload.downcast_ref::<String>() {
      s.as_str()
    } else {
      "unknown panic payload"
    };

    let location = panic_info
      .location()
      .map(|loc| format!("{}:{}", loc.file(), loc.line()))
      .unwrap_or_else(|| "unknown location".to_string());

    error!("panic occurred at {}: {}", location, msg);
  }));

  let c_len = ((**env).v1_6.GetStringUTFLength)(env, path) + 1;
  let c_path = libc::malloc(c_len as usize) as *mut c_char;
  let c_chars = ((**env).v1_6.GetStringUTFChars)(env, path, ptr::null_mut());

  libc::memcpy(
    c_path as *mut c_void,
    c_chars as *const c_void,
    c_len as usize,
  );

  ((**env).v1_6.ReleaseStringUTFChars)(env, path, c_chars);

  open_and_call_onload(env, c_path, c"libunity.so".as_ptr(), &LIBUNITY_ADDR);
  open_and_call_onload(env, c_path, c"libil2cpp.so".as_ptr(), &LIBIL2CPP_ADDR);

  libc::free(c_path as *mut c_void);

  *EXISTING_REGIONS.lock().unwrap() = read_memory_regions().unwrap();

  // ptr::write_volatile(0x1 as *mut usize, 0x4e000000);

  // Breaks the game: Wonder.UI.Title.TitleScene$$.cctor set _IsFirst_k__BackingField to 0
  // unsafe {
  //   let address = get_virtual_address(0x000000000473F4E8) as *const u32;
  //   // MOV w9, #0
  //   ptr::write_volatile(address as *mut u32, 0x1200002A);
  //   info!("wrote MOVZ instruction to address: 0x{:x}", address as usize);
  // }

  let skip_logo = CONFIGURATION.lock().unwrap().as_ref().unwrap().skip_logo;
  if skip_logo {
    info!("applying skip logo patch");

    unsafe {
      // Skip Sesisoft logo: MOVI V0.16B, #0
      let address = get_virtual_address(0x0000000004742bd8) as *mut u32;
      ptr::write_volatile(address, 0x4e000000);
      info!("wrote 'movi v0.16b, #0' at {:p}", address);
    }

    unsafe {
      // Skip Sumzap logo: MOVI V0.16B, #0
      let address = get_virtual_address(0x0000000004742c88) as *mut u32;
      ptr::write_volatile(address, 0x4e000000);
      info!("wrote 'movi v0.16b, #0' at {:p}", address);
    }
  }

  let method = CONFIGURATION.lock().unwrap().as_ref().unwrap().method;
  match method {
    PATCH_METHOD_NONE => {
      info!("not patching");
    }
    PATCH_METHOD_HOOK => {
      do_shit_as_hook();
    }
    PATCH_METHOD_OFF_THREAD_SCAN => {
      do_shit_as_off_thread_scan();
    }
    _ => {
      panic!("unknown patch method: {method}")
    }
  }

  JNI_TRUE
}

fn do_shit_as_off_thread_scan() {
  // let indirect_address = get_virtual_address(OFF_INDIRECT_TYPEINFO) as *const *const usize;
  // info!("OFF_INDIRECT_TYPEINFO: 0x{:x}", indirect_address);
  let direct_address = get_virtual_address(OFF_TYPEINFO);
  info!("OFF_TYPEINFO: 0x{:x}", direct_address);

  thread::spawn(move || {
    info!("I am altering the deal (off-thread)");
    show_toast("I am altering the deal (off-thread)");

    let start = Instant::now();
    let timeout = Duration::from_secs(30);
    loop {
      if Instant::now() > start + timeout {
        error!("time out waiting for type initialization");
        break;
      }

      thread::sleep(Duration::from_millis(100));
      unsafe {
        // let direct_address = ptr::read_volatile(indirect_address);
        let type_info_address = ptr::read_volatile(direct_address as *const usize);
        let static_fields_address =
          ptr::read_volatile((type_info_address + OFF_STATIC_FIELDS) as *const usize);
        if static_fields_address == 0 {
          trace!("waiting for type initialization");
          continue;
        }
        debug!("read type_info_address: 0x{:x}", type_info_address);
        debug!("read static_fields_address: 0x{:x}", static_fields_address);
        let field_address = ptr::read_volatile((static_fields_address + OFF_FIELD) as *const usize);
        debug!("read field_address: 0x{:x}", field_address);

        if field_address != 0 {
          info!("initialized");
          show_toast("Static field initialized, starting scanning");
          patch();
          break;
        }

        // let virtual_address = get_virtual_address(0x000000000700B528);
        // // let virtual_address = get_virtual_address(0x0000000006D01F90);
        // let string_literal_16028_address = ptr::read_volatile(virtual_address as *const usize);
        // debug!(".got address: 0x{:x}", string_literal_16028_address);
        // // let string_literal_16028_address = ptr::read_volatile(string_literal_16028_address as *const usize);
        // // debug!(".data address: 0x{:x}", string_literal_16028_address);
        // if string_literal_16028_address == 0xa0007d49 {
        //   trace!("waiting for type initialization");
        //   continue;
        // }
        // patch();
        // break;

        // let actual_address = ptr::read_volatile(string_literal_16028_address as *const usize);
        // debug!("read actual address: 0x{:x}", actual_address);

        // if field_address != 0 {
        //   info!("initialized");
        //   show_toast("Static field initialized, starting scanning").unwrap();
        //   patch();
        //   break;
        // }
      }
      // debug!("trying to patch static url off-thread");
      // if off_thread::patch_static_url() {
      //   info!("static url patched off-thread");
      //   break;
      // }
    }
  });
}

static EXISTING_REGIONS: Mutex<Vec<MemoryRegion>> = Mutex::new(Vec::new());

static ORIGINAL_STATIC_URL: &str = "https://static-prd-wonder.sesisoft.com/";
// static ORIGINAL_STATIC_URL: &str = "https://axel.assasans.dev/static///////";
// static NEW_STATIC_URL: &str = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
// static NEW_STATIC_URL: &str = "https://axel.assasans.dev/static///////";

// static TARGET_ADDRESS: usize = 0x0000000001CC763C; // Wonder.Util.TextUtil$$.cctor (stack restore)
// static TARGET_ADDRESS: usize = 0x00000000050CFAD4; // r[lyk_G$$.cctor (stack restore)
static TARGET_ADDRESS: usize = 0x00000000050ce468; // r[lyk_G$$hn_wYJn (stack restore) - key ok, url 1
// static TARGET_ADDRESS: usize = 0x000000000473F4F4; // Wonder.UI.Title.TitleScene$$.cctor (stack restore) - too early
// static TARGET_ADDRESS: usize = 0x000000000473F4A8; // Wonder.UI.Title.TitleScene$$.ctor (tail) - too early

// static CALL_ADDRESS: usize = 0x00000000017df7bc; // sub_17DF7BC

static mut LIFETIME_SUB: usize = 0xdead;

#[unsafe(naked)]
pub extern "C" fn trampoline() {
  #[rustfmt::skip]
  naked_asm!(
    // ".inst 0x42424242",

    // "mov x1, xzr",
    // "mov x23, x30",
    // "ldp x21, x20, [sp], #0x20",
    // "ldr x22, {}",
    // "str x30, [sp, #-0x10]!",
    // "blr x22",
    // "ldr x30, [sp], #0x10",

    "str x30, [sp, #-0x10]!",
    "bl {}",
    "ldr x30, [sp], #0x10",

    // Restore stack for original function
    "ldp x29, x30, [sp, #0x60]",
    "ldp x20, x19, [sp, #0x50]",
    "ldp x22, x21, [sp, #0x40]",
    "ldp x24, x23, [sp, #0x30]",
    "ldp x26, x25, [sp, #0x20]",
    "ldp x28, x27, [sp, #0x10]",
    "add sp, sp, #0x70",

    // "ldp x19, x30, [sp, #0x10]",
    // "ldr x20, [sp], #0x20",

    "ldr x18, {}",
    "br x18",

    // "ret",
    sym hook,
    sym LIFETIME_SUB,
  );
}

/// Allocates executable memory and copies the machine code of `trampoline` into it.
/// Returns a function pointer to the new executable memory.
unsafe fn copy_trampoline_to_executable() -> Option<extern "C" fn()> {
  // Estimate the size of the function by getting the difference between two symbols.
  // Normally you would define a symbol after the trampoline and measure length, but for simplicity:
  let size = 128; // Adjust based on your actual trampoline size

  // Get a pointer to the function
  let src = trampoline as *const u8;

  // Allocate RWX memory
  let mem = libc::mmap(
    ptr::null_mut(),
    size,
    libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
    libc::MAP_PRIVATE | libc::MAP_ANON,
    -1,
    0,
  );

  if mem == libc::MAP_FAILED {
    eprintln!("mmap failed");
    return None;
  }

  let result = libc::mprotect(
    mem,
    size,
    libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
  );
  if result != 0 {
    error!(
      "mprotect failed for {:?}: {}",
      mem,
      std::io::Error::last_os_error()
    );
    return None;
  }
  debug!("mprotect succeeded for {:?}", mem);

  // Copy function code into the allocated region
  ptr::copy_nonoverlapping(src, mem as *mut u8, size);

  // let mut dst = mem as *mut u8;
  // for _ in 0..size {
  //   unsafe {
  //     *dst = 0x90;
  //     dst = dst.add(1);
  //   }
  // }

  // Cast the memory to a function pointer
  Some(mem::transmute::<*mut c_void, extern "C" fn()>(mem))
}

fn hook() {
  if IS_PATCHED
    .compare_exchange(false, true, Ordering::SeqCst, Ordering::SeqCst)
    .is_err()
  {
    return;
  }

  info!("I am altering the deal (hooking)");
  show_toast("I am altering the deal (hooking)");
  patch();

  // let virtual_address = get_virtual_address(0x700B538);
  // debug!("virtual address: 0x{:x}", virtual_address);
  // let address = virtual_address as *mut usize;
  // let indirect_address = unsafe { address.read_volatile() };
  // debug!("indirect address: 0x{:x}", indirect_address);

  // thread::spawn(|| {
  //   info!("thread patch started");
  //   thread::sleep(Duration::from_secs(1));
  //   info!("delay ok");
  //   patch();
  // });
}

static IS_PATCHED: AtomicBool = AtomicBool::new(false);

pub fn make_region_writable(virtual_address: usize) {
  for region in read_memory_regions().unwrap() {
    if !(region.start..region.end).contains(&virtual_address) {
      continue;
    }

    debug!("found target region {:?}", region);
    unsafe {
      let result = libc::mprotect(
        region.start as *mut c_void,
        region.size(),
        libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
      );
      if result != 0 {
        error!(
          "mprotect failed for region {:?}: {}",
          region,
          std::io::Error::last_os_error()
        );
        continue;
      }
      debug!("mprotect succeeded for target region {:?}", region);
    }
  }
}

pub fn get_virtual_address(address: usize) -> usize {
  for region in read_memory_regions().unwrap() {
    if !region
      .pathname
      .as_ref()
      .is_some_and(|name| name.contains("libil2cpp.so"))
    {
      continue;
    }

    let offset_in_region = address as i64 - region.offset as i64;
    if offset_in_region < 0 {
      debug!("region starts {} bytes too late", -offset_in_region);
      continue;
    }

    debug!("found region {:?}", region);
    unsafe {
      let result = libc::mprotect(
        region.start as *mut c_void,
        region.size(),
        libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
      );
      if result != 0 {
        error!(
          "mprotect failed for region {:?}: {}",
          region,
          std::io::Error::last_os_error()
        );
        continue;
      }
      debug!("mprotect succeeded for region {:?}", region);
    }

    let virtual_address = region.start + address;
    return virtual_address;
  }
  panic!("virtual address not found for address: 0x{:x}", address);
}

fn do_shit_as_hook() {
  for region in read_memory_regions().unwrap() {
    if !region
      .pathname
      .as_ref()
      .is_some_and(|name| name.contains("libil2cpp.so"))
    {
      continue;
    }

    let offset_in_region = TARGET_ADDRESS as i64 - region.offset as i64;
    if offset_in_region < 0 {
      debug!("region starts {} bytes too late", -offset_in_region);
      continue;
    }

    info!("found code region {:?}", region);
    unsafe {
      let result = libc::mprotect(
        region.start as *mut c_void,
        region.size(),
        libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
      );
      if result != 0 {
        error!(
          "mprotect failed for region {:?}: {}",
          region,
          std::io::Error::last_os_error()
        );
        continue;
      }
      info!("mprotect succeeded for region {:?}", region);
    }

    unsafe {
      let lifetime_sub = &raw mut LIFETIME_SUB;
      *lifetime_sub = get_virtual_address(0x00000000017df7bc);
      // *lifetime_sub = get_virtual_address(0x0000000004B4BBEC);
      debug!("LIFETIME_SUB: 0x{:x}", *lifetime_sub);

      let mut virtual_address = region.start + TARGET_ADDRESS;

      // let trampoline_address = copy_trampoline_to_executable().unwrap() as *const () as u64;

      let trampoline_address = trampoline as *const () as u64;
      // let trampoline_address = virtual_address as u64;
      info!("trampoline address: 0x{:x}", trampoline_address);
      // make_region_writable(trampoline_address as usize);

      let region_bytes = std::slice::from_raw_parts(trampoline_address as *const u8, 32);
      debug!(
        "trampoline bytes: {}",
        region_bytes
          .iter()
          .map(|b| format!("{:02x}", b))
          .collect::<Vec<_>>()
          .join(" ")
      );

      let instruction = ptr::read_volatile(virtual_address as *const u32);
      info!("existing instruction: 0x{:x}", instruction);
      assert_eq!(instruction, 0xa9467bfd);

      // let trampoline_address = 0x12345678;

      // let mov = encode_mov_reg(20, 31);
      // info!("0. MOV: 0x{:x}", mov);

      let adrp = encode_adrp(16, trampoline_address, (virtual_address + 4) as u64);
      info!("1. ADRP: 0x{:x}", adrp);

      let add_imm = encode_add_immediate(16, 16, (trampoline_address & 0xfff) as u16, false);
      info!("2. ADD: 0x{:x}", add_imm);

      let br = encode_br(16);
      info!("3. BR: 0x{:x}", br);

      // let start = virtual_address as *const c_void;

      // ptr::write_volatile(virtual_address as *mut u32, mov);
      // virtual_address += 4;
      ptr::write_volatile(virtual_address as *mut u32, adrp);
      virtual_address += 4;
      ptr::write_volatile(virtual_address as *mut u32, add_imm);
      virtual_address += 4;
      ptr::write_volatile(virtual_address as *mut u32, br);
      // virtual_address += 4;

      // let end = virtual_address as *const c_void;

      // let result = os_arch_clear_cache(start, end);
      // unsafe {
      //   core::arch::asm!("dmb sy");
      //   core::arch::asm!("dsb sy");
      //   core::arch::asm!("isb sy");
      // }
      // debug!("instruction cache flushed");
      info!("patched instructions written to memory");

      // let patched_instruction = encode_b(virtual_address as u64, trampoline as *const () as u64);
      // debug!("patched instruction: 0x{:x}", patched_instruction);
    }
  }
}

unsafe fn os_arch_clear_cache<T>(start: *const T, end: *const T) -> bool {
  let start = start as u64;
  let end = end as u64;

  let mut ctr_el0: u64;
  // Get Cache Type Info.
  unsafe {
    core::arch::asm!("mrs {0}, ctr_el0", out(reg) ctr_el0);
  }

  // If CTR_EL0.IDC is set, data cache cleaning to the point of unification
  // is not required for instruction to data coherence.
  if ((ctr_el0 >> 28) & 0x1) == 0x0 {
    let dcache_line_size = 4 << ((ctr_el0 >> 16) & 15);
    let mut addr = start & !(dcache_line_size - 1);
    while addr < end {
      unsafe {
        core::arch::asm!(
        "dc cvau, {0}",
        in(reg) addr,
        );
      }
      addr += dcache_line_size;
    }
  }
  unsafe {
    core::arch::asm!("dsb ish");
  }
  // If CTR_EL0.DIC is set, instruction cache invalidation to the point of
  // unification is not required for instruction to data coherence.
  if ((ctr_el0 >> 29) & 0x1) == 0x0 {
    let icache_line_size = 4 << ((ctr_el0 >> 0) & 15);
    let mut addr = start & !(icache_line_size - 1);
    while addr < end {
      unsafe {
        core::arch::asm!(
        "ic ivau, {0}",
        in(reg) addr,
        );
      }
      addr += icache_line_size;
    }
    unsafe {
      core::arch::asm!("dsb ish");
    }
  }
  unsafe {
    core::arch::asm!("isb sy");
  }

  true
}

// See [CodeStage.AntiCheat.ObscuredTypes.ObscuredString$$.cctor]
static OBSCURED_STRING_KEY: &str = "4441";

fn obscured_string_xor(value: &str) -> Vec<u8> {
  let key_bytes = OBSCURED_STRING_KEY.as_bytes();
  value
    .bytes()
    .enumerate()
    .map(|(index, byte)| byte ^ key_bytes[index % key_bytes.len()])
    .collect()
}

fn patch() {
  let new_static_url = CONFIGURATION
    .lock()
    .unwrap()
    .as_ref()
    .unwrap()
    .server_url
    .clone();
  let new_static_url = format!(
    "{:/<width$}",
    new_static_url,
    width = ORIGINAL_STATIC_URL.len()
  );

  let new_public_key = CONFIGURATION
    .lock()
    .unwrap()
    .as_ref()
    .unwrap()
    .public_key
    .clone();

  let existing_regions = EXISTING_REGIONS.lock().unwrap();
  let current_regions = get_suitable_regions();

  let excluded_regions: HashSet<_> = existing_regions
    .iter()
    .map(|region| (region.start, region.end))
    .collect();
  let regions: Vec<_> = current_regions
    .into_iter()
    .filter(|region| !excluded_regions.contains(&(region.start, region.end)))
    // .filter(|region| region.size() >= 5242880)
    .filter(|region| region.size() < 33554432)
    .filter(|region| {
      region.pathname.as_deref() == Some("[anon:libc_malloc]") || region.pathname.is_none()
    })
    .collect();
  info!(
    "{} regions to scan, {}",
    regions.len(),
    bytes_to_human_readable(regions.iter().map(|r| r.size() as u64).sum())
  );

  for region in &regions {
    debug!(
      "region: {:?} (size: {})",
      region,
      bytes_to_human_readable(region.size() as u64)
    );
  }

  let key_parts_plain = split_string_by_sizes(
    // "z7VeNsuYLX//cx/oMxEMxKgvo4/4wusB3QbV3ZLw61AIClNSjvMNbIFBpZxJJ9AinyGXe6C1lmqChfrlhBtODwZugqKeFaAMRN9UGlDRySkhJZkMyeufU8kBBEvHk440KMvYTgCXbwYJKjqXyM9KOgXno8O6g3OEsLXIwGMevg0=",
    &new_public_key,
    &[
      "6dNRoG04n56HX2LiOA",
      "kpCC9fgjxvMKDyZGyx",
      "35Owh/sOU8HjpOdGHB",
      "y96ytzw9WMxzyvJkl2",
      "9Q82mc4y7zKy3SkchV",
      "C16mnckCO26kf6Wn4X",
      "e5lN0i7Ot5kIueWY2i",
      "oo8iRudj/EbNdumTU8",
      "I7oC7dWuvIEovK4eDJ",
      "dFJO2tzZ8=",
    ],
  )
  .unwrap();

  let key_parts = split_bytes_by_sizes(
    // b"z7VeNsuYLX//cx/oMxEMxKgvo4/4wusB3QbV3ZLw61AIClNSjvMNbIFBpZxJJ9AinyGXe6C1lmqChfrlhBtODwZugqKeFaAMRN9UGlDRySkhJZkMyeufU8kBBEvHk440KMvYTgCXbwYJKjqXyM9KOgXno8O6g3OEsLXIwGMevg0=",
    new_public_key.as_bytes(),
    &[
      &obscured_string_xor("6dNRoG04n56HX2LiOA"),
      &obscured_string_xor("kpCC9fgjxvMKDyZGyx"),
      &obscured_string_xor("35Owh/sOU8HjpOdGHB"),
      &obscured_string_xor("y96ytzw9WMxzyvJkl2"),
      &obscured_string_xor("9Q82mc4y7zKy3SkchV"),
      &obscured_string_xor("C16mnckCO26kf6Wn4X"),
      &obscured_string_xor("e5lN0i7Ot5kIueWY2i"),
      &obscured_string_xor("oo8iRudj/EbNdumTU8"),
      &obscured_string_xor("I7oC7dWuvIEovK4eDJ"),
      &obscured_string_xor("dFJO2tzZ8="),
    ],
  )
  .unwrap();

  let mut key_parts_plain = key_parts_plain
    .iter()
    .map(|(original_part, patched_part)| (original_part, (patched_part, 0)))
    .collect::<HashMap<_, _>>();

  let mut key_parts = key_parts
    .iter()
    .map(|(original_part, patched_part)| {
      (
        original_part,
        (
          obscured_string_xor(str::from_utf8(&patched_part).unwrap()),
          0,
        ),
      )
    })
    .collect::<HashMap<_, _>>();

  let mut found_original = 0;
  for region in regions {
    trace!(
      "searching for static url in {} of {region:?}",
      bytes_to_human_readable(region.size() as u64)
    );

    let addresses = match search_byte_sequence(&region, &str_to_utf16_bytes(&ORIGINAL_STATIC_URL)) {
      Ok(addresses) => addresses,
      Err(error) => {
        warn!("error searching memory region {:?}: {}", region, error);
        continue;
      }
    };

    for address in &addresses {
      debug!(
        "found static url at address: 0x{:x} in region {:?} (size: {})",
        address,
        region,
        bytes_to_human_readable(region.size() as u64)
      );

      let region_bytes =
        unsafe { std::slice::from_raw_parts(region.start as *const u8, region.size()) };
      trace!(
        "region bytes: {}",
        region_bytes
          .iter()
          .take(32)
          .map(|b| format!("{:02x}", b))
          .collect::<Vec<_>>()
          .join(" ")
      );

      let virtual_address = region.start + address;
      match write_to_memory(virtual_address, &str_to_utf16_bytes(&new_static_url)) {
        Ok(_) => {
          trace!(
            "successfully wrote to memory at address 0x{:x}",
            virtual_address
          );
          found_original += 1;
        }
        Err(error) => {
          error!("error writing to memory: {}", error);
        }
      }
    }

    for (index, (original_part, (patched_part, times))) in key_parts_plain.iter_mut().enumerate() {
      trace!("searching for public key part {}", index + 1);

      let addresses = match search_byte_sequence(&region, &str_to_utf16_bytes(&original_part)) {
        Ok(addresses) => addresses,
        Err(error) => {
          warn!("error searching memory region {:?}: {}", region, error);
          continue;
        }
      };

      for address in &addresses {
        debug!(
          "found public key part {} at address: 0x{:x} in region {:?} (size: {})",
          index + 1,
          address,
          region,
          bytes_to_human_readable(region.size() as u64)
        );

        let virtual_address = region.start + address;
        match write_to_memory(virtual_address, &str_to_utf16_bytes(patched_part)) {
          Ok(_) => {
            trace!(
              "successfully wrote public key part {} to memory at address 0x{:x}",
              index + 1,
              virtual_address
            );
            *times += 1;
          }
          Err(error) => {
            error!("error writing to memory: {}", error);
          }
        }
      }
    }

    for (index, (original_part, (patched_part, times))) in key_parts.iter_mut().enumerate() {
      trace!("searching for public key part {}", index + 1);

      let addresses = match search_byte_sequence(&region, &pad_to_utf16_bytes(&original_part)) {
        Ok(addresses) => addresses,
        Err(error) => {
          warn!("error searching memory region {:?}: {}", region, error);
          continue;
        }
      };

      for address in &addresses {
        debug!(
          "found public key part {} sequence at address: 0x{:x} in region {:?} (size: {})",
          index + 1,
          address,
          region,
          bytes_to_human_readable(region.size() as u64)
        );

        let virtual_address = region.start + address;
        match write_to_memory(virtual_address, &pad_to_utf16_bytes(patched_part)) {
          Ok(_) => {
            trace!(
              "successfully wrote public key part {} to memory at address 0x{:x}",
              index + 1,
              virtual_address
            );
            *times += 1;
          }
          Err(error) => {
            error!("error writing to memory: {}", error);
          }
        }
      }
    }
  }

  info!("replaced static url {} times", found_original);
  info!("'{}' -> '{}'", ORIGINAL_STATIC_URL, new_static_url);

  for (index, (original_part, (patched_part, times))) in key_parts_plain.iter().enumerate() {
    info!(
      "replaced public key plain part {} '{}' -> '{}', {} times",
      index + 1,
      original_part,
      patched_part,
      times
    );
  }

  for (index, (original_part, (patched_part, times))) in key_parts.iter().enumerate() {
    info!(
      "replaced public key part {} '{:?}' -> '{:?}', {} times",
      index + 1,
      original_part,
      patched_part,
      times
    );
  }

  let report = iter::once(found_original)
    .chain(key_parts_plain.iter().map(|(_, (_, times))| *times))
    .chain(key_parts.iter().map(|(_, (_, times))| *times))
    .map(|count| count.to_string())
    .collect::<Vec<_>>()
    .join("/");
  show_toast(&format!("Patch finished: {}", report));
}

pub unsafe extern "C" fn unload(env: *mut JNIEnv, _activity_object: jni_sys::jclass) -> jboolean {
  let mut vm: *mut JavaVM = ptr::null_mut();

  let unity_module = LIBUNITY_ADDR.load(Ordering::Acquire);
  if !unity_module.is_null() {
    LIBUNITY_ADDR.store(ptr::null_mut(), Ordering::Release);

    if ((**env).v1_6.GetJavaVM)(env, &mut vm) != 0 {
      ((**env).v1_6.FatalError)(env, c"Unable to retrieve Java VM".as_ptr());
    } else {
      let on_unload: JNI_OnUnload_t =
        std::mem::transmute(libc::dlsym(unity_module, c"JNI_OnUnload".as_ptr()));
      on_unload(vm, ptr::null_mut());

      libc::dlclose(unity_module);
    }
  }

  let il2cpp_module = LIBIL2CPP_ADDR.load(Ordering::Acquire);
  if !il2cpp_module.is_null() {
    LIBIL2CPP_ADDR.store(ptr::null_mut(), Ordering::Release);

    if ((**env).v1_6.GetJavaVM)(env, &mut vm) != 0 {
      ((**env).v1_6.FatalError)(env, c"Unable to retrieve Java VM".as_ptr());
    } else {
      let on_unload: JNI_OnUnload_t =
        std::mem::transmute(libc::dlsym(il2cpp_module, c"JNI_OnUnload".as_ptr()));
      on_unload(vm, ptr::null_mut());

      libc::dlclose(il2cpp_module);
    }
  }

  JNI_TRUE
}

struct Configuration {
  pub server_url: String,
  pub public_key: String,
  pub method: i32,
  pub skip_logo: bool,
  pub bridge_class: jclass,
}

unsafe impl Send for Configuration {}

const PATCH_METHOD_NONE: i32 = 0;
const PATCH_METHOD_HOOK: i32 = 1;
const PATCH_METHOD_OFF_THREAD_SCAN: i32 = 2;

static CONFIGURATION: Mutex<Option<Configuration>> = Mutex::new(None);

pub unsafe extern "C" fn supplemental_verify(env: *mut JNIEnv, _activity_object: jclass) -> jboolean {
  info!("running custom libmain.so!");
  JNI_TRUE
}

pub unsafe extern "C" fn configure(
  env: *mut JNIEnv,
  _activity_object: jclass,
  server_url: jstring,
  public_key: jstring,
  method: jint,
  skip_logo: jboolean,
) -> jboolean {
  let server_url = unsafe {
    let c_len = ((**env).v1_6.GetStringUTFLength)(env, server_url) + 1;
    let c_server_url = libc::malloc(c_len as usize) as *mut c_char;
    let c_chars = ((**env).v1_6.GetStringUTFChars)(env, server_url, ptr::null_mut());

    libc::memcpy(
      c_server_url as *mut c_void,
      c_chars as *const c_void,
      c_len as usize,
    );

    ((**env).v1_6.ReleaseStringUTFChars)(env, server_url, c_chars);

    CStr::from_ptr(c_server_url).to_string_lossy().into_owned()
  };

  let public_key = unsafe {
    let c_len = ((**env).v1_6.GetStringUTFLength)(env, public_key) + 1;
    let c_public_key = libc::malloc(c_len as usize) as *mut c_char;
    let c_chars = ((**env).v1_6.GetStringUTFChars)(env, public_key, ptr::null_mut());

    libc::memcpy(
      c_public_key as *mut c_void,
      c_chars as *const c_void,
      c_len as usize,
    );

    ((**env).v1_6.ReleaseStringUTFChars)(env, public_key, c_chars);

    CStr::from_ptr(c_public_key).to_string_lossy().into_owned()
  };

  info!(
    "configure(server_url='{}', public_key='{}', method={})",
    server_url, public_key, method
  );

  let bridge_class =
    ((**env).v1_6.FindClass)(env, c"jp/assasans/konofd/stub/NativeLoaderBridge".as_ptr());

  *CONFIGURATION.lock().unwrap() = Some(Configuration {
    server_url,
    public_key,
    method,
    skip_logo,
    bridge_class: ((**env).v1_6.NewGlobalRef)(env, bridge_class),
  });

  JNI_TRUE
}

pub struct SafeJavaVM(pub *mut JavaVM);

unsafe impl Send for SafeJavaVM {}

pub static JAVA_VM: Mutex<Option<SafeJavaVM>> = Mutex::new(None);

#[allow(non_snake_case)]
#[unsafe(no_mangle)]
pub unsafe extern "C" fn JNI_OnLoad(vm: *mut JavaVM, _reserved: *mut c_void) -> c_int {
  android_logger::init_once(
    Config::default()
      .with_tag("libmain-rs")
      .with_max_level(LevelFilter::Debug),
  );

  debug!("JNI_OnLoad(vm={:p})", vm);

  let mut env: *mut JNIEnv = ptr::null_mut();
  ((**vm).v1_4.AttachCurrentThread)(
    vm,
    &mut env as *mut *mut JNIEnv as *mut *mut c_void,
    ptr::null_mut(),
  );

  *JAVA_VM.lock().unwrap() = Some(SafeJavaVM(vm));

  {
    let jni_methods = [
      JNINativeMethod {
        name: c"load".as_ptr().cast_mut(),
        signature: c"(Ljava/lang/String;)Z".as_ptr().cast_mut(),
        fnPtr: load as *mut c_void,
      },
      JNINativeMethod {
        name: c"unload".as_ptr().cast_mut(),
        signature: c"()Z".as_ptr().cast_mut(),
        fnPtr: unload as *mut c_void,
      },
    ];

    let native_loader = ((**env).v1_6.FindClass)(env, c"com/unity3d/player/NativeLoader".as_ptr());

    if ((**env).v1_6.RegisterNatives)(env, native_loader, jni_methods.as_ptr(), 2) != 0 {
      ((**env).v1_6.FatalError)(env, c"com/unity3d/player/NativeLoader".as_ptr());
    }
  }

  {
    let jni_methods = [
      JNINativeMethod {
        name: c"supplemental_verify".as_ptr().cast_mut(),
        signature: c"()Z".as_ptr().cast_mut(),
        fnPtr: supplemental_verify as *mut c_void,
      },
      JNINativeMethod {
        name: c"configure".as_ptr().cast_mut(),
        signature: c"(Ljava/lang/String;Ljava/lang/String;IZ)Z"
          .as_ptr()
          .cast_mut(),
        fnPtr: configure as *mut c_void,
      },
    ];

    let native_loader = ((**env).v1_6.FindClass)(
      env,
      c"jp/assasans/konofd/stub/NativeLoaderSupplemental".as_ptr(),
    );

    if ((**env).v1_6.RegisterNatives)(env, native_loader, jni_methods.as_ptr(), 2) != 0 {
      ((**env).v1_6.FatalError)(
        env,
        c"jp/assasans/konofd/stub/NativeLoaderSupplemental".as_ptr(),
      );
    }
  }

  JNI_VERSION_1_6
}

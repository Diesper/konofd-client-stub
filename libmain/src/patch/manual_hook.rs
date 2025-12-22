use std::arch::naked_asm;
use std::os::raw::c_void;
use std::{mem, ptr};
use std::sync::atomic::{AtomicBool, Ordering};
use log::{debug, error, info};
use crate::maps::read_memory_regions;
use crate::{get_virtual_address, patch};
use crate::asm::{encode_add_immediate, encode_adrp, encode_br};
use crate::jni_util::show_toast;

// static TARGET_ADDRESS: usize = 0x0000000001CC763C; // Wonder.Util.TextUtil$$.cctor (stack restore)
// static TARGET_ADDRESS: usize = 0x00000000050CFAD4; // r[lyk_G$$.cctor (stack restore)
static TARGET_ADDRESS: usize = 0x00000000050ce468; // r[lyk_G$$hn_wYJn (stack restore) - key ok, url 1
// static TARGET_ADDRESS: usize = 0x000000000473F4F4; // Wonder.UI.Title.TitleScene$$.cctor (stack restore) - too early
// static TARGET_ADDRESS: usize = 0x000000000473F4A8; // Wonder.UI.Title.TitleScene$$.ctor (tail) - too early

// static CALL_ADDRESS: usize = 0x00000000017df7bc; // sub_17DF7BC

static mut LIFETIME_SUB: usize = 0xdead;

static IS_PATCHED: AtomicBool = AtomicBool::new(false);

pub fn manual_hook() {
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
#[unsafe(naked)]
extern "C" fn trampoline() {
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

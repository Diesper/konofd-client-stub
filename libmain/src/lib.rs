#![allow(unsafe_op_in_unsafe_fn, dead_code)]

mod and64_inline_hook;
mod global_metadata;
mod jni_util;
mod maps;
mod patch;
mod public_key_processor;
mod util;

use std::ffi::{CStr, CString};
use std::os::raw::{c_char, c_int, c_void};
use std::sync::Mutex;
use std::sync::atomic::{AtomicPtr, Ordering};
use std::{mem, panic, ptr, str};

use android_logger::Config;
use jni_sys::{
  JNI_TRUE, JNI_VERSION_1_6, JNIEnv, JNINativeMethod, JavaVM, jboolean, jclass, jint, jstring,
};
use log::{LevelFilter, debug, error, info};

use crate::maps::{MemoryRegion, read_memory_regions};

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

    let on_load: JNI_OnLoad_t = mem::transmute(libc::dlsym(module, c"JNI_OnLoad".as_ptr()));
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
  #[allow(deprecated)]
  match method {
    PATCH_METHOD_NONE => {
      info!("not patching");
    }
    PATCH_METHOD_HOOK => panic!("manual hooking method was removed"),
    PATCH_METHOD_OFF_THREAD_SCAN => panic!("off-thread scan method was removed"),
    PATCH_METHOD_LOAD_METADATA_FILE_HOOK => patch::load_metadata_file_hook(),
    _ => panic!("unknown patch method: {method}"),
  }

  JNI_TRUE
}

static EXISTING_REGIONS: Mutex<Vec<MemoryRegion>> = Mutex::new(Vec::new());

static ORIGINAL_STATIC_URL: &str = "https://static-prd-wonder.sesisoft.com/";
// static ORIGINAL_STATIC_URL: &str = "https://axel.assasans.dev/static///////";
// static NEW_STATIC_URL: &str = "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
// static NEW_STATIC_URL: &str = "https://axel.assasans.dev/static///////";

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

pub unsafe extern "C" fn unload(env: *mut JNIEnv, _activity_object: jni_sys::jclass) -> jboolean {
  let mut vm: *mut JavaVM = ptr::null_mut();

  let unity_module = LIBUNITY_ADDR.load(Ordering::Acquire);
  if !unity_module.is_null() {
    LIBUNITY_ADDR.store(ptr::null_mut(), Ordering::Release);

    if ((**env).v1_6.GetJavaVM)(env, &mut vm) != 0 {
      ((**env).v1_6.FatalError)(env, c"Unable to retrieve Java VM".as_ptr());
    } else {
      let on_unload: JNI_OnUnload_t =
        mem::transmute(libc::dlsym(unity_module, c"JNI_OnUnload".as_ptr()));
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
        mem::transmute(libc::dlsym(il2cpp_module, c"JNI_OnUnload".as_ptr()));
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
#[deprecated(note = "removed")]
const PATCH_METHOD_HOOK: i32 = 1;
#[deprecated(note = "removed")]
const PATCH_METHOD_OFF_THREAD_SCAN: i32 = 2;
const PATCH_METHOD_LOAD_METADATA_FILE_HOOK: i32 = 3;

static CONFIGURATION: Mutex<Option<Configuration>> = Mutex::new(None);

pub unsafe extern "C" fn supplemental_verify(
  _env: *mut JNIEnv,
  _activity_object: jclass,
) -> jboolean {
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

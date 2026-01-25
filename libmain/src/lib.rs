#![allow(unsafe_op_in_unsafe_fn, clippy::missing_safety_doc)]

mod and64_inline_hook;
mod global_metadata;
mod jni_util;
mod maps;
mod patch;
mod public_key_processor;
mod versions;

use std::ffi::{CStr, CString};
use std::os::raw::{c_char, c_int, c_void};
use std::sync::atomic::{AtomicPtr, Ordering};
use std::sync::{Arc, Mutex};
use std::{mem, panic, ptr, str};

use android_logger::Config;
use jni_sys::{
  JNI_TRUE, JNI_VERSION_1_6, JNIEnv, JNINativeMethod, JavaVM, jboolean, jclass, jint, jobject,
  jstring,
};
use log::{LevelFilter, debug, error, info};

use crate::maps::{MemoryRegion, read_memory_regions};
use crate::patch::PatchMethod;

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

  if get_configuration_params().skip_logo {
    info!("applying skip logo patch");

    for &(offset, patch) in get_version_patches().skip_logo {
      unsafe {
        let address = get_virtual_address(offset) as *mut u32;
        ptr::write_volatile(address, patch);
        info!("wrote patch 0x{:08x} to address {:p}", patch, address);
      }
    }
  }

  #[allow(deprecated)]
  match get_configuration_params().method {
    PatchMethod::None => {
      info!("not patching");
    }
    PatchMethod::Hook => panic!("manual hooking method was removed"),
    PatchMethod::OffThreadScan => panic!("off-thread scan method was removed"),
    PatchMethod::LoadMetadataFileHook => patch::load_metadata_file_hook(),
  }

  JNI_TRUE
}

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
  pub params: Arc<ConfigureParams>,
  pub bridge_class: jclass,
}

unsafe impl Send for Configuration {}

static CONFIGURATION: Mutex<Option<Configuration>> = Mutex::new(None);

pub fn get_configuration_params() -> Arc<ConfigureParams> {
  CONFIGURATION
    .lock()
    .unwrap()
    .as_ref()
    .unwrap()
    .params
    .clone()
}

pub fn get_version_patches() -> &'static versions::VersionPatches {
  let params = get_configuration_params();
  for (version, patches) in versions::VERSIONS {
    if *version == params.version {
      info!("found matching version patches: {:?}", patches);
      return patches;
    }
  }
  panic!(
    "no matching version patches found for version: {}",
    params.version
  );
}

pub unsafe extern "C" fn supplemental_verify(
  _env: *mut JNIEnv,
  _activity_object: jclass,
) -> jboolean {
  info!("running custom libmain.so!");
  JNI_TRUE
}

#[derive(Debug)]
pub struct ConfigureParams {
  pub version: String,
  pub server_url: String,
  pub public_key: String,
  pub method: PatchMethod,
  pub skip_logo: bool,
}

pub unsafe extern "C" fn configure(
  env: *mut JNIEnv,
  _activity_object: jclass,
  object: jobject,
) -> jboolean {
  let class = ((**env).v1_6.GetObjectClass)(env, object);
  let get_field_ref =
    |name: &CStr, sig: &CStr| ((**env).v1_6.GetFieldID)(env, class, name.as_ptr(), sig.as_ptr());
  let get_string_field = |field_id: jni_sys::jfieldID| -> String {
    let j_str = ((**env).v1_6.GetObjectField)(env, object, field_id) as jstring;
    let c_len = ((**env).v1_6.GetStringUTFLength)(env, j_str) + 1;
    let c_str = libc::malloc(c_len as usize) as *mut c_char;
    let c_chars = ((**env).v1_6.GetStringUTFChars)(env, j_str, ptr::null_mut());

    libc::memcpy(
      c_str as *mut c_void,
      c_chars as *const c_void,
      c_len as usize,
    );

    ((**env).v1_6.ReleaseStringUTFChars)(env, j_str, c_chars);

    let result = CStr::from_ptr(c_str).to_string_lossy().into_owned();
    libc::free(c_str as *mut c_void);
    result
  };
  let get_int_field =
    |field_id: jni_sys::jfieldID| -> i32 { ((**env).v1_6.GetIntField)(env, object, field_id) };
  let get_bool_field =
    |field_id: jni_sys::jfieldID| -> bool { ((**env).v1_6.GetBooleanField)(env, object, field_id) };

  let params = ConfigureParams {
    version: get_string_field(get_field_ref(c"version", c"Ljava/lang/String;")),
    server_url: get_string_field(get_field_ref(c"serverUrl", c"Ljava/lang/String;")),
    public_key: get_string_field(get_field_ref(c"publicKey", c"Ljava/lang/String;")),
    method: {
      let method = get_int_field(get_field_ref(c"method", c"I"));
      method
        .try_into()
        .unwrap_or_else(|_| panic!("invalid patch method: {}", method))
    },
    skip_logo: get_bool_field(get_field_ref(c"skipLogo", c"Z")),
  };
  info!("configuration received: {:?}", params);

  let bridge_class =
    ((**env).v1_6.FindClass)(env, c"jp/assasans/konofd/stub/NativeLoaderBridge".as_ptr());
  *CONFIGURATION.lock().unwrap() = Some(Configuration {
    params: Arc::new(params),
    bridge_class: ((**env).v1_6.NewGlobalRef)(env, bridge_class),
  });

  JNI_TRUE
}

pub struct SafeJavaVM(pub *mut JavaVM);

/// SAFETY: [JavaVM] pointer is safe to send between threads.
/// https://docs.oracle.com/en/java/javase/11/docs/specs/jni/invocation.html#invocation
/// > The JNI interface pointer (JNIEnv) is valid only in the current thread.
/// > **Should another thread need to access the Java VM, it must first call AttachCurrentThread()**
/// > **to attach itself to the VM and obtain a JNI interface pointer.** Once attached to the VM,
/// > a native thread works just like an ordinary Java thread running inside a native method.
/// > The native thread remains attached to the VM until it calls DetachCurrentThread() to detach itself.
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
    if ((**env).v1_6.RegisterNatives)(
      env,
      native_loader,
      jni_methods.as_ptr(),
      jni_methods.len() as i32,
    ) != 0
    {
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
        signature: c"(Ljp/assasans/konofd/stub/ConfigureParams;)Z"
          .as_ptr()
          .cast_mut(),
        fnPtr: configure as *mut c_void,
      },
    ];

    let native_loader = ((**env).v1_6.FindClass)(
      env,
      c"jp/assasans/konofd/stub/NativeLoaderSupplemental".as_ptr(),
    );
    if ((**env).v1_6.RegisterNatives)(
      env,
      native_loader,
      jni_methods.as_ptr(),
      jni_methods.len() as i32,
    ) != 0
    {
      ((**env).v1_6.FatalError)(
        env,
        c"jp/assasans/konofd/stub/NativeLoaderSupplemental".as_ptr(),
      );
    }
  }

  JNI_VERSION_1_6
}

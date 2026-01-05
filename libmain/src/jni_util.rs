use std::ffi::CString;
use std::os::raw::c_void;
use std::ptr;

use jni_sys::{JNI_FALSE, JNIEnv};
use log::{trace, warn};

use crate::{CONFIGURATION, JAVA_VM};

pub fn show_toast(message: &str) {
  if let Err(error) = try_show_toast(message) {
    warn!("failed to show toast: {}", error);
  }
}

pub fn try_show_toast(message: &str) -> Result<(), &'static str> {
  unsafe {
    let bridge_class = CONFIGURATION.lock().unwrap().as_ref().unwrap().bridge_class;

    let vm = JAVA_VM.lock().unwrap().as_ref().unwrap().0;
    let mut env: *mut JNIEnv = ptr::null_mut();
    ((**vm).v1_4.AttachCurrentThread)(
      vm,
      &mut env as *mut *mut JNIEnv as *mut *mut c_void,
      ptr::null_mut(),
    );
    trace!("env={:p}", env);

    // Convert Rust string to Java string
    let message_c = CString::new(message).map_err(|_| "Failed to create CString from message")?;
    let j_message = ((**env).v1_6.NewStringUTF)(env, message_c.as_ptr());

    if j_message.is_null() {
      return Err("Failed to create Java string");
    }

    // Get Toast.makeText static method
    let make_text_method = ((**env).v1_6.GetStaticMethodID)(
      env,
      bridge_class,
      c"showToast".as_ptr(),
      c"(Ljava/lang/CharSequence;)V".as_ptr(),
    );

    if make_text_method.is_null() {
      ((**env).v1_6.ExceptionDescribe)(env);
      ((**env).v1_6.ExceptionClear)(env);
      return Err("Failed to get showToast method");
    }

    // Call Toast.makeText(context, message, duration)
    ((**env).v1_6.CallStaticVoidMethod)(env, bridge_class, make_text_method, j_message);

    // Check if an exception occurred
    ((**env).v1_6.ExceptionDescribe)(env);
    if ((**env).v1_6.ExceptionCheck)(env) == JNI_FALSE {
      Ok(())
    } else {
      // This may be a spurious exception: seen on libndk, a toast is actually shown
      ((**env).v1_6.ExceptionDescribe)(env);
      ((**env).v1_6.ExceptionClear)(env);
      Err("Exception occurred while showing toast")
    }
  }
}

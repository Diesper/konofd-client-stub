use std::arch::asm;
use std::ffi::CStr;
use std::os::raw::{c_char, c_void};
use std::path::PathBuf;
use std::sync::atomic::{AtomicPtr, Ordering};
use std::{mem, ptr};

use log::*;

use crate::and64_inline_hook::Hook;
use crate::global_metadata::MetadataEditor;
use crate::jni_util::show_toast;
use crate::public_key_processor::PublicKeyProcessor;
use crate::{CONFIGURATION, get_virtual_address};

pub fn load_metadata_file_hook() {
  // We don't really care about original implementation, but store it just in case
  #[allow(non_upper_case_globals)]
  static LoadMetadataFile_original: AtomicPtr<c_void> = AtomicPtr::new(ptr::null_mut());

  // Original function type
  // void* MetadataLoader::LoadMetadataFile(const char* fileName)
  // unsigned __int64 __fastcall il2cpp::vm::MetadataLoader::LoadMetadataFile(const char *fileName)
  type TargetFn = extern "C" fn(*const c_char) -> u64;

  unsafe {
    // il2cpp::vm::MetadataLoader::LoadMetadataFile
    let target = get_virtual_address(0x00000000017b6774) as *mut c_void;
    info!("target open address: {:p}", target as *const c_void);
    if let Some(hook) = Hook::new(
      target as *mut TargetFn,
      LoadMetadataFile_patched as *mut TargetFn,
    ) {
      // Hook is active
      let original: TargetFn = mem::transmute(hook.trampoline::<TargetFn>());
      LoadMetadataFile_original.store(original as *mut c_void, Ordering::SeqCst);
    } else {
      panic!("failed to create hook for LoadMetadataFile");
    }
  }

  fn get_il2cpp_data_dir() -> String {
    // Ideally this should be reimplemented in this library, but I have no idea how /proc/self/exe
    // (i.e. "/system/bin/arm64/app_process64") becomes
    // "/storage/emulated/0/Android/data/jp.assasans.konofd.stub/files/il2cpp".

    // void __usercall il2cpp::utils::Runtime::GetDataDir(unsigned __int64 *a1@<X8>)
    #[allow(non_snake_case)]
    let GetDataDir_address = get_virtual_address(0x0000000001811764);
    let get_data_dir: extern "C" fn() = unsafe { mem::transmute(GetDataDir_address) };

    let mut a1: [u8; 24] = [0; 24];
    unsafe {
      asm!(
        "mov x8, {arg}",
        "blr {func}",
        arg = in(reg) a1.as_mut_ptr(),
        func = in(reg) get_data_dir,
        out("x8") _,
        clobber_abi("C"),
      );
    }
    debug!("DataDir: {:?}", a1);

    // See /usr/include/c++/v1/__cxx03/string
    // NDK root: Android/Sdk/ndk/29.0.14206865/toolchains/llvm/prebuilt/linux-x86_64/sysroot
    // This is absolutely not portable, but the game is not going to be recompiled anymore.
    #[derive(Debug)]
    #[repr(C)]
    struct StdString {
      packed: u64,
      len: u64,
      ptr: u64,
    }

    let data_dir = unsafe {
      let data_dir = mem::transmute::<[u8; 24], StdString>(a1);
      debug!("DataDir: {:?}", data_dir);

      // "/storage/emulated/0/Android/data/jp.assasans.konofd.stub/files/il2cpp"
      // We assume that this is always __long variant of std::string
      let data_dir = std::slice::from_raw_parts(data_dir.ptr as *const u8, data_dir.len as usize);
      debug!("DataDir: {:?}", data_dir);

      str::from_utf8(data_dir).unwrap()
    };
    debug!("DataDir: {:?}", data_dir);

    // This leaks memory because we don't call std::string destructor
    data_dir.to_owned()
  }

  #[allow(non_snake_case)]
  unsafe fn LoadMetadataFile_patched(file_name: *const c_char) -> u64 {
    let file_name = CStr::from_ptr(file_name);
    info!("LoadMetadataFile called with file_name: {:?}", file_name);
    show_toast("I am altering the deal (LoadMetadataFile)");

    let data_dir = get_il2cpp_data_dir();
    info!("il2cpp data dir: {}", data_dir);

    let metadata_path = PathBuf::from(data_dir).join("Metadata").join(
      file_name
        .to_str()
        .expect("failed to convert file name to str"),
    );
    info!("loading metadata file from path: {:?}", metadata_path);

    let metadata_bytes = std::fs::read(&metadata_path).expect("failed to read metadata file");

    let mut editor =
      MetadataEditor::from_bytes(metadata_bytes).expect("failed to parse metadata file");
    info!(
      "loaded metadata file, {} string literals",
      editor.metadata().string_literal_count()
    );

    let matches = editor
      .metadata()
      .find_strings_containing("https://static-prd-wonder.sesisoft.com/")
      .expect("failed to search for strings");
    info!("found {} static url strings:", matches.len());
    for m in &matches {
      info!(
        "  [{}] {} (offset: {}, len: {})",
        m.index, m.value, m.data_offset, m.length
      );
    }

    let static_url = CONFIGURATION
      .lock()
      .unwrap()
      .as_ref()
      .unwrap()
      .server_url
      .clone();
    let replaced = editor
      .replace_strings_containing("https://static-prd-wonder.sesisoft.com/", |string| {
        string.replace("https://static-prd-wonder.sesisoft.com/", &static_url)
      })
      .expect("failed to replace static url");
    info!("replaced {} occurrences of static url", replaced);

    let new_public_key = CONFIGURATION
      .lock()
      .unwrap()
      .as_ref()
      .unwrap()
      .public_key
      .clone();
    //     let public_key = r#"-----BEGIN PUBLIC KEY-----
    // MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDPtV42y5gtf/9zH+gzEQzEqC+j
    // j/jC6wHdBtXdkvDrUAgKU1KO8w1sgUGlnEkn0CKfIZd7oLWWaoKF+uWEG04PBm6C
    // op4VoAxE31QaUNHJKSElmQzJ659TyQEES8eTjjQoy9hOAJdvBgkqOpfIz0o6Beej
    // w7qDc4SwtcjAYx6+DQIDAQAB
    // -----END PUBLIC KEY-----
    // "#;
    // let processor = PublicKeyProcessor::from_pem_string(public_key).unwrap();
    let processor = PublicKeyProcessor::from_modulus_base64(&new_public_key);
    info!("using public key: {}", processor.modulus_base64());

    let replacements = processor.generate_replacements().unwrap();
    for (original, replacement) in &replacements {
      info!("replacing public key part {:?} -> {:?}", original, replacement);
      editor
        .replace_string(original, replacement)
        .expect("failed to replace public key part");
    }

    let patched_global_metadata = editor.into_bytes();

    let leaked_bytes = Box::leak(patched_global_metadata.into_boxed_slice());
    let ptr = leaked_bytes.as_ptr() as u64;
    info!(
      "patched global-metadata.dat, size: {}, ptr: 0x{:x}",
      leaked_bytes.len(),
      ptr
    );
    show_toast(&format!("global-metadata.dat patched pointer: 0x{:x}", ptr));

    ptr
  }
}

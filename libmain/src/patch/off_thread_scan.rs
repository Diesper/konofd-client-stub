use std::{ptr, thread};
use std::time::{Duration, Instant};
use log::{debug, error, info, trace};
use crate::{get_virtual_address, patch};
use crate::jni_util::show_toast;

static OFF_INDIRECT_TYPEINFO: usize = 0x0000000006CFD0A0; // off_6CFD0A0  DCQ r_lyk_G_TypeInfo
static OFF_TYPEINFO: usize = 0x0000000006F66378; // r_lyk_G_TypeInfo  DCQ 0x20017BBF
static OFF_STATIC_FIELDS: usize = 0x000000B8;
static OFF_FIELD: usize = 0x00000038;

pub fn off_thread_scan() {
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

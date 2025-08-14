pub static OFF_INDIRECT_TYPEINFO: usize = 0x0000000006CFD0A0; // off_6CFD0A0  DCQ r_lyk_G_TypeInfo
pub static OFF_TYPEINFO: usize = 0x0000000006F66378; // r_lyk_G_TypeInfo  DCQ 0x20017BBF
pub static OFF_STATIC_FIELDS: usize = 0x000000B8;
pub static OFF_FIELD: usize = 0x00000038;

// pub fn get_regions_of_interest() -> Vec<MemoryRegion> {
//   let existing_regions = EXISTING_REGIONS.lock().unwrap();
//   let current_regions = get_suitable_regions();
//
//   let excluded_regions: HashSet<_> = existing_regions
//     .iter()
//     .map(|region| (region.start, region.end))
//     .collect();
//   current_regions
//     .into_iter()
//     .filter(|region| !excluded_regions.contains(&(region.start, region.end)))
//     // .filter(|region| region.size() >= 5242880)
//     .filter(|region| {
//       region.pathname.as_deref() == Some("[anon:libc_malloc]") || region.pathname.is_none()
//     })
//     .collect()
// }

// pub fn patch_static_url() -> bool {
//   let regions = get_regions_of_interest();
//   info!(
//     "{} regions to scan, {}",
//     regions.len(),
//     bytes_to_human_readable(regions.iter().map(|r| r.size() as u64).sum())
//   );
//
//   let mut found_original = 0;
//   let mut found_patched = 0;
//   for region in regions {
//     trace!(
//       "searching {} in {region:?} for static URL",
//       bytes_to_human_readable(region.size() as u64)
//     );
//
//     let addresses = match search_byte_sequence(&region, &str_to_utf16_bytes(&ORIGINAL_STATIC_URL)) {
//       Ok(addresses) => addresses,
//       Err(error) => {
//         warn!("error searching memory region {:?}: {}", region, error);
//         continue;
//       }
//     };
//     for address in &addresses {
//       debug!(
//         "found static url at address: 0x{:x} in region {:?} (size: {})",
//         address,
//         region,
//         bytes_to_human_readable(region.size() as u64)
//       );
//
//       let virtual_address = region.start + address;
//       match write_to_memory(virtual_address, &str_to_utf16_bytes(NEW_STATIC_URL)) {
//         Ok(_) => {
//           trace!(
//             "successfully wrote to memory at address 0x{:x}",
//             virtual_address
//           );
//           found_original += 1;
//         }
//         Err(error) => {
//           error!("error writing to memory: {}", error);
//         }
//       }
//     }
//
//     let addresses = match search_byte_sequence(&region, &str_to_utf16_bytes(&NEW_STATIC_URL)) {
//       Ok(addresses) => addresses,
//       Err(error) => {
//         warn!("error searching memory region {:?}: {}", region, error);
//         continue;
//       }
//     };
//     for address in &addresses {
//       debug!(
//         "found patched domain sequence at address: 0x{:x} in region {:?} (size: {})",
//         address,
//         region,
//         bytes_to_human_readable(region.size() as u64)
//       );
//
//       found_patched += 1;
//     }
//   }
//
//   found_original > 0 || found_patched > 0
// }

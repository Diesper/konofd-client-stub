use std::collections::HashMap;

pub fn str_to_utf16_bytes(input: &str) -> Vec<u8> {
  let utf16: Vec<u16> = input.encode_utf16().collect();
  let mut utf16_bytes = Vec::with_capacity(utf16.len() * 2);

  for word in utf16 {
    utf16_bytes.push((word >> 8) as u8);
    utf16_bytes.push((word & 0xFF) as u8);
  }

  utf16_bytes
}

pub fn pad_to_utf16_bytes(input: &[u8]) -> Vec<u8> {
  let mut utf16_bytes = Vec::with_capacity(input.len() * 2);

  for word in input {
    utf16_bytes.push(0);
    utf16_bytes.push(*word);
  }

  utf16_bytes
}

pub fn bytes_to_human_readable(bytes: u64) -> String {
  let units = ["B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"];
  let mut size = bytes as f64;
  let mut unit_index = 0;

  while size >= 1024.0 && unit_index < units.len() - 1 {
    size /= 1024.0;
    unit_index += 1;
  }

  if unit_index == 0 {
    format!("{} {}", size as u64, units[unit_index])
  } else {
    format!("{:.2} {}", size, units[unit_index])
  }
}

pub fn split_bytes_by_sizes(
  input: &[u8],
  sizes: &[&[u8]],
) -> Result<HashMap<Vec<u8>, Vec<u8>>, String> {
  // Get the lengths from the size templates
  let lengths: Vec<usize> = sizes.iter().map(|s| s.len()).collect();

  let mut result = HashMap::new();
  let mut start = 0;

  for (i, &length) in lengths.iter().enumerate() {
    if start + length > input.len() {
      return Err(format!(
        "Not enough bytes in input. Need {} more bytes.",
        start + length - input.len()
      ));
    }

    let sub_slice = &input[start..start + length];
    result.insert(sizes[i].to_vec(), sub_slice.to_vec());
    start += length;
  }

  // Check if we've used all bytes
  if start != input.len() {
    return Err(format!(
      "Input has {} extra bytes that weren't used.",
      input.len() - start
    ));
  }

  Ok(result)
}

pub fn split_string_by_sizes(input: &str, sizes: &[&str]) -> Result<HashMap<String, String>, String> {
  // Convert size strings to actual lengths
  let lengths: Vec<usize> = sizes.iter().map(|s| s.len()).collect();

  let mut result = HashMap::new();
  let mut start = 0;

  for (i, &length) in lengths.iter().enumerate() {
    if start + length > input.len() {
      return Err(format!(
        "Not enough characters in input string. Need {} more characters.",
        start + length - input.len()
      ));
    }

    let substring = &input[start..start + length];
    result.insert(sizes[i].to_string(), substring.to_string());
    start += length;
  }

  // Check if we've used all characters
  if start != input.len() {
    return Err(format!(
      "Input string has {} extra characters that weren't used.",
      input.len() - start
    ));
  }

  Ok(result)
}

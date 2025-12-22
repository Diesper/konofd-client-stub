//! # Global Metadata Editor
//!
//! A Rust library for parsing and modifying Unity IL2CPP global-metadata.dat files (version 29).
//! Supports string replacement with automatic offset/length adjustment.

mod editor;
mod error;
mod header;
mod metadata;
mod string_literal;

pub use editor::*;
pub use error::*;
pub use header::*;
pub use metadata::*;
pub use string_literal::*;

#[cfg(test)]
mod tests {
  use super::*;

  #[test]
  fn test_bit_perfect() {
    // Cannot do fs::read since tests run on another device
    let original_data = include_bytes!("/home/assasans/dev/axel/global-4.5.11-trimmed-libc_malloc-0x00007f496b400000-0x00007f496e000000-global-metadata.dat").to_vec();

    let editor = MetadataEditor::from_bytes(original_data.clone()).unwrap();
    assert_eq!(editor.into_bytes(), original_data);

    let mut editor = MetadataEditor::from_bytes(original_data.clone()).unwrap();
    editor
      .replace_strings_containing("https://static-prd-wonder.sesisoft.com/", |string| {
        string.to_owned()
      })
      .unwrap();
    assert_eq!(editor.into_bytes(), original_data);
  }
}

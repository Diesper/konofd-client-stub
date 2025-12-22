//!  Metadata editor for string replacement with offset adjustment

use std::io::Cursor;

use super::error::{MetadataError, Result};
use super::header::GlobalMetadataHeader;
use super::metadata::GlobalMetadata;
use super::string_literal::StringLiteral;

/// Editor for modifying global-metadata.dat files
pub struct MetadataEditor {
  metadata: GlobalMetadata,
}

impl MetadataEditor {
  /// Create a new editor from parsed metadata
  pub fn new(metadata: GlobalMetadata) -> Self {
    Self { metadata }
  }

  /// Load metadata from bytes
  pub fn from_bytes(data: Vec<u8>) -> Result<Self> {
    let metadata = GlobalMetadata::parse(data)?;
    Ok(Self::new(metadata))
  }

  /// Get reference to the metadata
  pub fn metadata(&self) -> &GlobalMetadata {
    &self.metadata
  }

  /// Replace a string literal by index with a new value
  /// This handles different length strings by adjusting all affected offsets
  pub fn replace_string_by_index(&mut self, index: usize, new_value: &str) -> Result<()> {
    let string_literals = self.metadata.string_literals().to_vec();

    if index >= string_literals.len() {
      return Err(MetadataError::InvalidStringIndex(index));
    }

    let old_literal = string_literals[index];
    let old_length = old_literal.length as usize;
    let new_length = new_value.len();
    let length_diff = new_length as i32 - old_length as i32;

    let data_section_offset = self.metadata.header.string_literal_data_offset as usize;
    let old_string_offset = data_section_offset + old_literal.data_offset as usize;

    // Create new data with the replacement
    let mut new_data =
      Vec::with_capacity((self.metadata.data().len() as i32 + length_diff) as usize);

    // Copy everything before the string
    new_data.extend_from_slice(&self.metadata.data()[..old_string_offset]);

    // Write the new string
    new_data.extend_from_slice(new_value.as_bytes());

    // Copy everything after the old string
    let after_old_string = old_string_offset + old_length;
    new_data.extend_from_slice(&self.metadata.data()[after_old_string..]);

    // Update the header
    let mut new_header = self.metadata.header.clone();

    // Adjust string_literal_data_size
    new_header.string_literal_data_size += length_diff;

    // Adjust all offsets that come after the string literal data section
    let affected_position = self.metadata.header.string_literal_data_offset;
    new_header.adjust_offsets_after(affected_position, length_diff);

    // Don't adjust string_literal_data_offset itself since we're modifying within it
    new_header.string_literal_data_offset = self.metadata.header.string_literal_data_offset;

    // Update string literal entries
    // 1. Update the modified string's length
    // 2. Adjust data_offset for all strings that come after in the data section
    let literal_section_offset = new_header.string_literal_offset as usize;

    for (i, literal) in string_literals.iter().enumerate() {
      let entry_offset = literal_section_offset + i * StringLiteral::SIZE;

      let new_literal = if i == index {
        // This is the string we're replacing
        StringLiteral {
          length: new_length as u32,
          data_offset: literal.data_offset,
        }
      } else if literal.data_offset > old_literal.data_offset {
        // This string comes after the replaced one, adjust its offset
        StringLiteral {
          length: literal.length,
          data_offset: (literal.data_offset as i32 + length_diff) as u32,
        }
      } else {
        // This string comes before, no change needed
        *literal
      };

      // Write the updated literal entry
      let mut cursor = Cursor::new(&mut new_data[entry_offset..]);
      new_literal.write(&mut cursor)?;
    }

    // Write updated header
    let mut header_cursor = Cursor::new(&mut new_data[..GlobalMetadataHeader::SIZE]);
    new_header.write(&mut header_cursor)?;

    // Re-parse the modified metadata
    self.metadata = GlobalMetadata::parse(new_data)?;

    Ok(())
  }

  /// Replace all occurrences of a string with a new value
  pub fn replace_string(&mut self, old_value: &str, new_value: &str) -> Result<usize> {
    let matches = self.metadata.find_strings_exact(old_value)?;
    let count = matches.len();

    if count == 0 {
      return Err(MetadataError::StringNotFound(old_value.to_string()));
    }

    // Replace in reverse order to maintain valid indices
    let mut indices: Vec<_> = matches.iter().map(|s| s.index).collect();
    indices.sort_by(|a, b| b.cmp(a));

    for index in indices {
      self.replace_string_by_index(index, new_value)?;
    }

    Ok(count)
  }

  /// Replace strings containing a substring
  pub fn replace_strings_containing(
    &mut self,
    needle: &str,
    replacer: impl Fn(&str) -> String,
  ) -> Result<usize> {
    let matches = self.metadata.find_strings_containing(needle)?;
    let count = matches.len();

    if count == 0 {
      return Ok(0);
    }

    // Collect indices and new values
    let replacements: Vec<_> = matches
      .iter()
      .map(|s| (s.index, replacer(&s.value)))
      .collect();

    // Sort by index descending to maintain valid indices during replacement
    let mut sorted_replacements = replacements;
    sorted_replacements.sort_by(|a, b| b.0.cmp(&a.0));

    for (index, new_value) in sorted_replacements {
      self.replace_string_by_index(index, &new_value)?;
    }

    Ok(count)
  }

  /// Get the modified data as bytes
  pub fn into_bytes(self) -> Vec<u8> {
    self.metadata.data
  }

  /// Get reference to the current data
  pub fn as_bytes(&self) -> &[u8] {
    self.metadata.data()
  }

  /// Save to a file
  pub fn save_to_file(&self, path: &std::path::Path) -> Result<()> {
    std::fs::write(path, self.metadata.data())?;
    Ok(())
  }
}

#[cfg(test)]
mod tests {
  use super::*;

  // Helper to create a minimal valid metadata file for testing
  fn create_test_metadata(strings: &[&str]) -> Vec<u8> {
    let mut data = Vec::new();

    // Calculate sizes
    let header_size = GlobalMetadataHeader::SIZE;
    let string_literal_count = strings.len();
    let string_literal_size = string_literal_count * StringLiteral::SIZE;

    // Calculate string data
    let mut string_data = Vec::new();
    let mut string_offsets = Vec::new();

    for s in strings {
      string_offsets.push(string_data.len() as u32);
      string_data.extend_from_slice(s.as_bytes());
    }

    let string_literal_offset = header_size as i32;
    let string_literal_data_offset = (header_size + string_literal_size) as i32;
    let total_size = header_size + string_literal_size + string_data.len();

    // Write header
    data.extend_from_slice(&0xfab11bafu32.to_le_bytes()); // magic
    data.extend_from_slice(&29i32.to_le_bytes()); // version
    data.extend_from_slice(&string_literal_offset.to_le_bytes());
    data.extend_from_slice(&(string_literal_size as i32).to_le_bytes());
    data.extend_from_slice(&string_literal_data_offset.to_le_bytes());
    data.extend_from_slice(&(string_data.len() as i32).to_le_bytes());

    // Pad remaining header fields with zeros
    while data.len() < header_size {
      data.push(0);
    }

    // Write string literals
    for (i, s) in strings.iter().enumerate() {
      data.extend_from_slice(&(s.len() as u32).to_le_bytes());
      data.extend_from_slice(&string_offsets[i].to_le_bytes());
    }

    // Write string data
    data.extend_from_slice(&string_data);

    data
  }

  #[test]
  fn test_replace_same_length() {
    let test_data = create_test_metadata(&["hello", "world"]);
    let mut editor = MetadataEditor::from_bytes(test_data).unwrap();

    editor.replace_string_by_index(0, "HELLO").unwrap();

    let result = editor.metadata().get_string_literal(0).unwrap();
    assert_eq!(result.value, "HELLO");

    let result2 = editor.metadata().get_string_literal(1).unwrap();
    assert_eq!(result2.value, "world");
  }

  #[test]
  fn test_replace_longer_string() {
    let test_data = create_test_metadata(&["hi", "world"]);
    let mut editor = MetadataEditor::from_bytes(test_data).unwrap();

    editor.replace_string_by_index(0, "hello there").unwrap();

    let result = editor.metadata().get_string_literal(0).unwrap();
    assert_eq!(result.value, "hello there");

    // Verify the second string is still intact
    let result2 = editor.metadata().get_string_literal(1).unwrap();
    assert_eq!(result2.value, "world");
  }

  #[test]
  fn test_replace_shorter_string() {
    let test_data = create_test_metadata(&["hello world", "test"]);
    let mut editor = MetadataEditor::from_bytes(test_data).unwrap();

    editor.replace_string_by_index(0, "hi").unwrap();

    let result = editor.metadata().get_string_literal(0).unwrap();
    assert_eq!(result.value, "hi");

    let result2 = editor.metadata().get_string_literal(1).unwrap();
    assert_eq!(result2.value, "test");
  }
}

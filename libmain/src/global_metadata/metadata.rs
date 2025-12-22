//! Global metadata parsing and structures

use super::error::{MetadataError, Result};
use super::header::GlobalMetadataHeader;
use super::string_literal::{ParsedString, StringLiteral};

/// Parsed global-metadata. dat file
#[derive(Debug, Clone)]
pub struct GlobalMetadata {
  /// The file header
  pub header: GlobalMetadataHeader,
  /// Raw file data
  pub data: Vec<u8>,
  /// Parsed string literals
  string_literals: Vec<StringLiteral>,
  /// String literal data (raw bytes)
  string_literal_data: Vec<u8>,
}

impl GlobalMetadata {
  /// Parse a global-metadata.dat file from bytes
  pub fn parse(data: Vec<u8>) -> Result<Self> {
    let header = GlobalMetadataHeader::parse(&data)?;

    // Parse string literals
    let string_literals = Self::parse_string_literals(&data, &header)?;

    // Extract string literal data
    let data_start = header.string_literal_data_offset as usize;
    let data_end = data_start + header.string_literal_data_size as usize;

    if data_end > data.len() {
      return Err(MetadataError::OffsetOutOfBounds {
        offset: data_end,
        max: data.len(),
      });
    }

    let string_literal_data = data[data_start..data_end].to_vec();

    Ok(Self {
      header,
      data,
      string_literals,
      string_literal_data,
    })
  }

  /// Parse string literal entries from the metadata
  fn parse_string_literals(
    data: &[u8],
    header: &GlobalMetadataHeader,
  ) -> Result<Vec<StringLiteral>> {
    let offset = header.string_literal_offset as usize;
    let size = header.string_literal_size as usize;
    let count = size / StringLiteral::SIZE;

    let mut literals = Vec::with_capacity(count);

    for i in 0..count {
      let entry_offset = offset + i * StringLiteral::SIZE;
      if entry_offset + StringLiteral::SIZE > data.len() {
        return Err(MetadataError::OffsetOutOfBounds {
          offset: entry_offset + StringLiteral::SIZE,
          max: data.len(),
        });
      }

      let literal = StringLiteral::parse(&data[entry_offset..])?;
      literals.push(literal);
    }

    Ok(literals)
  }

  /// Get the number of string literals
  pub fn string_literal_count(&self) -> usize {
    self.string_literals.len()
  }

  /// Get a string literal by index
  pub fn get_string_literal(&self, index: usize) -> Result<ParsedString> {
    if index >= self.string_literals.len() {
      return Err(MetadataError::InvalidStringIndex(index));
    }

    let literal = &self.string_literals[index];
    let start = literal.data_offset as usize;
    let end = start + literal.length as usize;

    if end > self.string_literal_data.len() {
      return Err(MetadataError::OffsetOutOfBounds {
        offset: end,
        max: self.string_literal_data.len(),
      });
    }

    let bytes = &self.string_literal_data[start..end];
    let value = String::from_utf8(bytes.to_vec()).map_err(|_| MetadataError::InvalidUtf8(start))?;

    Ok(ParsedString {
      index,
      value,
      data_offset: literal.data_offset,
      length: literal.length,
    })
  }

  /// Get all string literals as parsed strings
  pub fn get_all_string_literals(&self) -> Result<Vec<ParsedString>> {
    let mut strings = Vec::with_capacity(self.string_literals.len());

    for i in 0..self.string_literals.len() {
      match self.get_string_literal(i) {
        Ok(s) => strings.push(s),
        Err(MetadataError::InvalidUtf8(_)) => {
          // Skip non-UTF8 strings (binary data)
          continue;
        }
        Err(e) => return Err(e),
      }
    }

    Ok(strings)
  }

  /// Find string literals containing the given substring
  pub fn find_strings_containing(&self, needle: &str) -> Result<Vec<ParsedString>> {
    let all_strings = self.get_all_string_literals()?;
    Ok(
      all_strings
        .into_iter()
        .filter(|s| s.value.contains(needle))
        .collect(),
    )
  }

  /// Find string literals matching exactly
  pub fn find_strings_exact(&self, needle: &str) -> Result<Vec<ParsedString>> {
    let all_strings = self.get_all_string_literals()?;
    Ok(
      all_strings
        .into_iter()
        .filter(|s| s.value == needle)
        .collect(),
    )
  }

  /// Get reference to string literals
  pub fn string_literals(&self) -> &[StringLiteral] {
    &self.string_literals
  }

  /// Get reference to string literal data
  pub fn string_literal_data(&self) -> &[u8] {
    &self.string_literal_data
  }

  /// Get mutable reference to internal data
  pub fn data_mut(&mut self) -> &mut Vec<u8> {
    &mut self.data
  }

  /// Get reference to internal data
  pub fn data(&self) -> &[u8] {
    &self.data
  }
}

/// Get a null-terminated string from the string table (used for type/method names)
pub fn get_string_from_table(
  data: &[u8],
  string_offset: i32,
  string_size: i32,
  index: i32,
) -> Result<String> {
  if index < 0 {
    return Ok(String::new());
  }

  let start = string_offset as usize + index as usize;
  if start >= data.len() {
    return Err(MetadataError::OffsetOutOfBounds {
      offset: start,
      max: data.len(),
    });
  }

  let end_max = (string_offset + string_size) as usize;
  let mut end = start;

  while end < end_max && end < data.len() && data[end] != 0 {
    end += 1;
  }

  String::from_utf8(data[start..end].to_vec()).map_err(|_| MetadataError::InvalidUtf8(start))
}

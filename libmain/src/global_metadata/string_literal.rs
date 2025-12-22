//! String literal structures and parsing

use std::io::{Cursor, Write};

use byteorder::{LittleEndian, ReadBytesExt, WriteBytesExt};

use super::error::{MetadataError, Result};

/// String literal entry - references data in the string literal data section
#[derive(Debug, Clone, Copy)]
pub struct StringLiteral {
  /// Length of the string in bytes
  pub length: u32,
  /// Offset into the string literal data section
  pub data_offset: u32,
}

impl StringLiteral {
  /// Size of a StringLiteral entry in bytes
  pub const SIZE: usize = 8;

  /// Parse a StringLiteral from bytes
  pub fn parse(data: &[u8]) -> Result<Self> {
    if data.len() < Self::SIZE {
      return Err(MetadataError::OffsetOutOfBounds {
        offset: Self::SIZE,
        max: data.len(),
      });
    }

    let mut cursor = Cursor::new(data);
    Ok(Self {
      length: cursor.read_u32::<LittleEndian>()?,
      data_offset: cursor.read_u32::<LittleEndian>()?,
    })
  }

  /// Write a StringLiteral to bytes
  pub fn write(&self, writer: &mut impl Write) -> Result<()> {
    writer.write_u32::<LittleEndian>(self.length)?;
    writer.write_u32::<LittleEndian>(self.data_offset)?;
    Ok(())
  }
}

/// Parsed string with its metadata
#[derive(Debug, Clone)]
pub struct ParsedString {
  /// Index in the string literal array
  pub index: usize,
  /// The string value
  pub value: String,
  /// Original offset in string literal data
  pub data_offset: u32,
  /// Original length
  pub length: u32,
}

//!  Error types for the global-metadata parser

use std::io;

use thiserror::Error;

#[derive(Error, Debug)]
pub enum MetadataError {
  #[error("IO error: {0}")]
  Io(#[from] io::Error),

  #[error("Invalid magic number. Expected 0xFAB11BAF, got 0x{0:08X}")]
  InvalidMagic(u32),

  #[error("Unsupported metadata version: {0}. Only version 29 is supported")]
  UnsupportedVersion(i32),

  #[error("String not found: {0}")]
  StringNotFound(String),

  #[error("Invalid string index: {0}")]
  InvalidStringIndex(usize),

  #[error("Invalid UTF-8 string at offset {0}")]
  InvalidUtf8(usize),

  #[error("Offset out of bounds: {offset} (max: {max})")]
  OffsetOutOfBounds { offset: usize, max: usize },
}

pub type Result<T> = std::result::Result<T, MetadataError>;

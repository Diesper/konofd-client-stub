use std::fs;
use std::path::Path;

use base64::Engine;
use base64::prelude::BASE64_STANDARD;
use rsa::RsaPublicKey;
use rsa::pkcs8::DecodePublicKey;
use rsa::traits::PublicKeyParts;

#[derive(Debug, thiserror::Error)]
pub enum PublicKeyError {
  #[error("Public key file does not exist: {0}")]
  FileNotFound(String),
  #[error("Failed to read file: {0}")]
  IoError(#[from] std::io::Error),
  #[error("Failed to parse public key: {0}")]
  ParseError(#[from] rsa::pkcs8::spki::Error),
  #[error("Modulus length mismatch: expected {expected}, got {actual}")]
  ModulusLengthMismatch { expected: usize, actual: usize },
}

pub struct PublicKeyProcessor {
  modulus_base64: String,
}

impl PublicKeyProcessor {
  pub fn from_pem_file(path: impl AsRef<Path>) -> Result<Self, PublicKeyError> {
    let path = path.as_ref();

    if !path.exists() {
      return Err(PublicKeyError::FileNotFound(path.display().to_string()));
    }

    let pem_content = fs::read_to_string(path)?;
    Self::from_pem_string(&pem_content)
  }

  pub fn from_pem_string(pem_content: &str) -> Result<Self, PublicKeyError> {
    let public_key = RsaPublicKey::from_public_key_pem(pem_content)?;

    let modulus_bytes = public_key.n().to_bytes_be();
    let modulus_base64 = BASE64_STANDARD.encode(&modulus_bytes);

    Ok(Self { modulus_base64 })
  }

  pub fn from_modulus_base64(modulus_base64: &str) -> Self {
    Self {
      modulus_base64: modulus_base64.to_string(),
    }
  }

  pub fn modulus_base64(&self) -> &str {
    &self.modulus_base64
  }

  pub fn generate_replacements(&self) -> Result<Vec<(String, String)>, PublicKeyError> {
    const ORIGINAL_PARTS: &[&str] = &[
      "6dNRoG04n56HX2LiOA",
      "kpCC9fgjxvMKDyZGyx",
      "35Owh/sOU8HjpOdGHB",
      "y96ytzw9WMxzyvJkl2",
      "9Q82mc4y7zKy3SkchV",
      "C16mnckCO26kf6Wn4X",
      "e5lN0i7Ot5kIueWY2i",
      "oo8iRudj/EbNdumTU8",
      "I7oC7dWuvIEovK4eDJ",
      "dFJO2tzZ8=",
    ];

    let expected_len: usize = ORIGINAL_PARTS.iter().map(|s| s.len()).sum();
    let actual_len = self.modulus_base64.len();

    if expected_len != actual_len {
      return Err(PublicKeyError::ModulusLengthMismatch {
        expected: expected_len,
        actual: actual_len,
      });
    }

    let mut replacements = Vec::new();
    let mut offset = 0;
    for original_part in ORIGINAL_PARTS {
      let new_part = &self.modulus_base64[offset..offset + original_part.len()];
      replacements.push((original_part.to_string(), new_part.to_string()));
      offset += original_part.len();
    }

    Ok(replacements)
  }
}

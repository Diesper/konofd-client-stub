use std::fs;
use std::path::Path;

use base64::Engine;
use base64::prelude::BASE64_STANDARD;
use rsa::RsaPublicKey;
use rsa::pkcs8::DecodePublicKey;
use rsa::traits::PublicKeyParts;

use crate::get_version_patches;

#[derive(Debug, thiserror::Error)]
pub enum PublicKeyError {
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
    let original_parts = get_version_patches().rsa_key_parts;

    let expected_len: usize = original_parts.iter().map(|s| s.len()).sum();
    let actual_len = self.modulus_base64.len();

    if expected_len != actual_len {
      return Err(PublicKeyError::ModulusLengthMismatch {
        expected: expected_len,
        actual: actual_len,
      });
    }

    let mut replacements = Vec::new();
    let mut offset = 0;
    for original_part in original_parts {
      let new_part = &self.modulus_base64[offset..offset + original_part.len()];
      replacements.push((original_part.to_string(), new_part.to_string()));
      offset += original_part.len();
    }

    Ok(replacements)
  }
}

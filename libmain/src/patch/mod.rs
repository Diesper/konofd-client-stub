mod load_metadata_file;

pub use load_metadata_file::*;

#[repr(i32)]
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum PatchMethod {
  None = 0,
  #[deprecated(note = "removed")]
  Hook = 1,
  #[deprecated(note = "removed")]
  OffThreadScan = 2,
  LoadMetadataFileHook = 3,
}

impl TryFrom<i32> for PatchMethod {
  type Error = i32;

  #[allow(deprecated)]
  fn try_from(value: i32) -> Result<Self, Self::Error> {
    match value {
      0 => Ok(PatchMethod::None),
      1 => Ok(PatchMethod::Hook),
      2 => Ok(PatchMethod::OffThreadScan),
      3 => Ok(PatchMethod::LoadMetadataFileHook),
      other => Err(other),
    }
  }
}

impl From<PatchMethod> for i32 {
  fn from(method: PatchMethod) -> Self {
    method as i32
  }
}

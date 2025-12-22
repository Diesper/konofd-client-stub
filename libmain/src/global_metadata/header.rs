//! Global metadata header structure for version 29

use std::io::{Cursor, Write};

use byteorder::{LittleEndian, ReadBytesExt, WriteBytesExt};

use super::error::{MetadataError, Result};

/// Magic number for global-metadata.dat: 0xFAB11BAF
pub const METADATA_MAGIC: u32 = 0xfab11baf;

/// Supported metadata version
pub const METADATA_VERSION: i32 = 29;

/// Header structure for global-metadata.dat version 29
#[derive(Debug, Clone)]
pub struct GlobalMetadataHeader {
  pub magic: u32,
  pub version: i32,

  // String literals
  pub string_literal_offset: i32,
  pub string_literal_size: i32,
  pub string_literal_data_offset: i32,
  pub string_literal_data_size: i32,

  // String (for type names, method names, etc.)
  pub string_offset: i32,
  pub string_size: i32,

  // Events
  pub events_offset: i32,
  pub events_size: i32,

  // Properties
  pub properties_offset: i32,
  pub properties_size: i32,

  // Methods
  pub methods_offset: i32,
  pub methods_size: i32,

  // Parameter default values
  pub parameter_default_values_offset: i32,
  pub parameter_default_values_size: i32,

  // Field default values
  pub field_default_values_offset: i32,
  pub field_default_values_size: i32,

  // Field and parameter default value data
  pub field_and_parameter_default_value_data_offset: i32,
  pub field_and_parameter_default_value_data_size: i32,

  // Field marshaled sizes
  pub field_marshaled_sizes_offset: i32,
  pub field_marshaled_sizes_size: i32,

  // Parameters
  pub parameters_offset: i32,
  pub parameters_size: i32,

  // Fields
  pub fields_offset: i32,
  pub fields_size: i32,

  // Generic parameters
  pub generic_parameters_offset: i32,
  pub generic_parameters_size: i32,

  // Generic parameter constraints
  pub generic_parameter_constraints_offset: i32,
  pub generic_parameter_constraints_size: i32,

  // Generic containers
  pub generic_containers_offset: i32,
  pub generic_containers_size: i32,

  // Nested types
  pub nested_types_offset: i32,
  pub nested_types_size: i32,

  // Interfaces
  pub interfaces_offset: i32,
  pub interfaces_size: i32,

  // VTables
  pub vtable_methods_offset: i32,
  pub vtable_methods_size: i32,

  // Interface offsets
  pub interface_offsets_offset: i32,
  pub interface_offsets_size: i32,

  // Type definitions
  pub type_definitions_offset: i32,
  pub type_definitions_size: i32,

  // Images
  pub images_offset: i32,
  pub images_size: i32,

  // Assemblies
  pub assemblies_offset: i32,
  pub assemblies_size: i32,

  // Field refs
  pub field_refs_offset: i32,
  pub field_refs_size: i32,

  // Referenced assemblies
  pub referenced_assemblies_offset: i32,
  pub referenced_assemblies_size: i32,

  // Attribute data
  pub attribute_data_offset: i32,
  pub attribute_data_size: i32,

  // Attribute data range
  pub attribute_data_range_offset: i32,
  pub attribute_data_range_size: i32,

  // Unresolved indirect call parameter types
  pub unresolved_indirect_call_parameter_types_offset: i32,
  pub unresolved_indirect_call_parameter_types_size: i32,

  // Unresolved indirect call parameter ranges
  pub unresolved_indirect_call_parameter_ranges_offset: i32,
  pub unresolved_indirect_call_parameter_ranges_size: i32,

  // Windows runtime type names
  pub windows_runtime_type_names_offset: i32,
  pub windows_runtime_type_names_size: i32,

  // Windows runtime strings
  pub windows_runtime_strings_offset: i32,
  pub windows_runtime_strings_size: i32,

  // Exported type definitions
  pub exported_type_definitions_offset: i32,
  pub exported_type_definitions_size: i32,
}

impl GlobalMetadataHeader {
  /// Size of the header in bytes
  pub const SIZE: usize = 264;

  /// Parse header from bytes
  pub fn parse(data: &[u8]) -> Result<Self> {
    if data.len() < Self::SIZE {
      return Err(MetadataError::OffsetOutOfBounds {
        offset: Self::SIZE,
        max: data.len(),
      });
    }

    let mut cursor = Cursor::new(data);

    let magic = cursor.read_u32::<LittleEndian>()?;
    if magic != METADATA_MAGIC {
      return Err(MetadataError::InvalidMagic(magic));
    }

    let version = cursor.read_i32::<LittleEndian>()?;
    if version != METADATA_VERSION {
      return Err(MetadataError::UnsupportedVersion(version));
    }

    Ok(Self {
      magic,
      version,
      string_literal_offset: cursor.read_i32::<LittleEndian>()?,
      string_literal_size: cursor.read_i32::<LittleEndian>()?,
      string_literal_data_offset: cursor.read_i32::<LittleEndian>()?,
      string_literal_data_size: cursor.read_i32::<LittleEndian>()?,
      string_offset: cursor.read_i32::<LittleEndian>()?,
      string_size: cursor.read_i32::<LittleEndian>()?,
      events_offset: cursor.read_i32::<LittleEndian>()?,
      events_size: cursor.read_i32::<LittleEndian>()?,
      properties_offset: cursor.read_i32::<LittleEndian>()?,
      properties_size: cursor.read_i32::<LittleEndian>()?,
      methods_offset: cursor.read_i32::<LittleEndian>()?,
      methods_size: cursor.read_i32::<LittleEndian>()?,
      parameter_default_values_offset: cursor.read_i32::<LittleEndian>()?,
      parameter_default_values_size: cursor.read_i32::<LittleEndian>()?,
      field_default_values_offset: cursor.read_i32::<LittleEndian>()?,
      field_default_values_size: cursor.read_i32::<LittleEndian>()?,
      field_and_parameter_default_value_data_offset: cursor.read_i32::<LittleEndian>()?,
      field_and_parameter_default_value_data_size: cursor.read_i32::<LittleEndian>()?,
      field_marshaled_sizes_offset: cursor.read_i32::<LittleEndian>()?,
      field_marshaled_sizes_size: cursor.read_i32::<LittleEndian>()?,
      parameters_offset: cursor.read_i32::<LittleEndian>()?,
      parameters_size: cursor.read_i32::<LittleEndian>()?,
      fields_offset: cursor.read_i32::<LittleEndian>()?,
      fields_size: cursor.read_i32::<LittleEndian>()?,
      generic_parameters_offset: cursor.read_i32::<LittleEndian>()?,
      generic_parameters_size: cursor.read_i32::<LittleEndian>()?,
      generic_parameter_constraints_offset: cursor.read_i32::<LittleEndian>()?,
      generic_parameter_constraints_size: cursor.read_i32::<LittleEndian>()?,
      generic_containers_offset: cursor.read_i32::<LittleEndian>()?,
      generic_containers_size: cursor.read_i32::<LittleEndian>()?,
      nested_types_offset: cursor.read_i32::<LittleEndian>()?,
      nested_types_size: cursor.read_i32::<LittleEndian>()?,
      interfaces_offset: cursor.read_i32::<LittleEndian>()?,
      interfaces_size: cursor.read_i32::<LittleEndian>()?,
      vtable_methods_offset: cursor.read_i32::<LittleEndian>()?,
      vtable_methods_size: cursor.read_i32::<LittleEndian>()?,
      interface_offsets_offset: cursor.read_i32::<LittleEndian>()?,
      interface_offsets_size: cursor.read_i32::<LittleEndian>()?,
      type_definitions_offset: cursor.read_i32::<LittleEndian>()?,
      type_definitions_size: cursor.read_i32::<LittleEndian>()?,
      images_offset: cursor.read_i32::<LittleEndian>()?,
      images_size: cursor.read_i32::<LittleEndian>()?,
      assemblies_offset: cursor.read_i32::<LittleEndian>()?,
      assemblies_size: cursor.read_i32::<LittleEndian>()?,
      field_refs_offset: cursor.read_i32::<LittleEndian>()?,
      field_refs_size: cursor.read_i32::<LittleEndian>()?,
      referenced_assemblies_offset: cursor.read_i32::<LittleEndian>()?,
      referenced_assemblies_size: cursor.read_i32::<LittleEndian>()?,
      attribute_data_offset: cursor.read_i32::<LittleEndian>()?,
      attribute_data_size: cursor.read_i32::<LittleEndian>()?,
      attribute_data_range_offset: cursor.read_i32::<LittleEndian>()?,
      attribute_data_range_size: cursor.read_i32::<LittleEndian>()?,
      unresolved_indirect_call_parameter_types_offset: cursor.read_i32::<LittleEndian>()?,
      unresolved_indirect_call_parameter_types_size: cursor.read_i32::<LittleEndian>()?,
      unresolved_indirect_call_parameter_ranges_offset: cursor.read_i32::<LittleEndian>()?,
      unresolved_indirect_call_parameter_ranges_size: cursor.read_i32::<LittleEndian>()?,
      windows_runtime_type_names_offset: cursor.read_i32::<LittleEndian>()?,
      windows_runtime_type_names_size: cursor.read_i32::<LittleEndian>()?,
      windows_runtime_strings_offset: cursor.read_i32::<LittleEndian>()?,
      windows_runtime_strings_size: cursor.read_i32::<LittleEndian>()?,
      exported_type_definitions_offset: cursor.read_i32::<LittleEndian>()?,
      exported_type_definitions_size: cursor.read_i32::<LittleEndian>()?,
    })
  }

  /// Write header to bytes
  pub fn write(&self, writer: &mut impl Write) -> Result<()> {
    writer.write_u32::<LittleEndian>(self.magic)?;
    writer.write_i32::<LittleEndian>(self.version)?;
    writer.write_i32::<LittleEndian>(self.string_literal_offset)?;
    writer.write_i32::<LittleEndian>(self.string_literal_size)?;
    writer.write_i32::<LittleEndian>(self.string_literal_data_offset)?;
    writer.write_i32::<LittleEndian>(self.string_literal_data_size)?;
    writer.write_i32::<LittleEndian>(self.string_offset)?;
    writer.write_i32::<LittleEndian>(self.string_size)?;
    writer.write_i32::<LittleEndian>(self.events_offset)?;
    writer.write_i32::<LittleEndian>(self.events_size)?;
    writer.write_i32::<LittleEndian>(self.properties_offset)?;
    writer.write_i32::<LittleEndian>(self.properties_size)?;
    writer.write_i32::<LittleEndian>(self.methods_offset)?;
    writer.write_i32::<LittleEndian>(self.methods_size)?;
    writer.write_i32::<LittleEndian>(self.parameter_default_values_offset)?;
    writer.write_i32::<LittleEndian>(self.parameter_default_values_size)?;
    writer.write_i32::<LittleEndian>(self.field_default_values_offset)?;
    writer.write_i32::<LittleEndian>(self.field_default_values_size)?;
    writer.write_i32::<LittleEndian>(self.field_and_parameter_default_value_data_offset)?;
    writer.write_i32::<LittleEndian>(self.field_and_parameter_default_value_data_size)?;
    writer.write_i32::<LittleEndian>(self.field_marshaled_sizes_offset)?;
    writer.write_i32::<LittleEndian>(self.field_marshaled_sizes_size)?;
    writer.write_i32::<LittleEndian>(self.parameters_offset)?;
    writer.write_i32::<LittleEndian>(self.parameters_size)?;
    writer.write_i32::<LittleEndian>(self.fields_offset)?;
    writer.write_i32::<LittleEndian>(self.fields_size)?;
    writer.write_i32::<LittleEndian>(self.generic_parameters_offset)?;
    writer.write_i32::<LittleEndian>(self.generic_parameters_size)?;
    writer.write_i32::<LittleEndian>(self.generic_parameter_constraints_offset)?;
    writer.write_i32::<LittleEndian>(self.generic_parameter_constraints_size)?;
    writer.write_i32::<LittleEndian>(self.generic_containers_offset)?;
    writer.write_i32::<LittleEndian>(self.generic_containers_size)?;
    writer.write_i32::<LittleEndian>(self.nested_types_offset)?;
    writer.write_i32::<LittleEndian>(self.nested_types_size)?;
    writer.write_i32::<LittleEndian>(self.interfaces_offset)?;
    writer.write_i32::<LittleEndian>(self.interfaces_size)?;
    writer.write_i32::<LittleEndian>(self.vtable_methods_offset)?;
    writer.write_i32::<LittleEndian>(self.vtable_methods_size)?;
    writer.write_i32::<LittleEndian>(self.interface_offsets_offset)?;
    writer.write_i32::<LittleEndian>(self.interface_offsets_size)?;
    writer.write_i32::<LittleEndian>(self.type_definitions_offset)?;
    writer.write_i32::<LittleEndian>(self.type_definitions_size)?;
    writer.write_i32::<LittleEndian>(self.images_offset)?;
    writer.write_i32::<LittleEndian>(self.images_size)?;
    writer.write_i32::<LittleEndian>(self.assemblies_offset)?;
    writer.write_i32::<LittleEndian>(self.assemblies_size)?;
    writer.write_i32::<LittleEndian>(self.field_refs_offset)?;
    writer.write_i32::<LittleEndian>(self.field_refs_size)?;
    writer.write_i32::<LittleEndian>(self.referenced_assemblies_offset)?;
    writer.write_i32::<LittleEndian>(self.referenced_assemblies_size)?;
    writer.write_i32::<LittleEndian>(self.attribute_data_offset)?;
    writer.write_i32::<LittleEndian>(self.attribute_data_size)?;
    writer.write_i32::<LittleEndian>(self.attribute_data_range_offset)?;
    writer.write_i32::<LittleEndian>(self.attribute_data_range_size)?;
    writer.write_i32::<LittleEndian>(self.unresolved_indirect_call_parameter_types_offset)?;
    writer.write_i32::<LittleEndian>(self.unresolved_indirect_call_parameter_types_size)?;
    writer.write_i32::<LittleEndian>(self.unresolved_indirect_call_parameter_ranges_offset)?;
    writer.write_i32::<LittleEndian>(self.unresolved_indirect_call_parameter_ranges_size)?;
    writer.write_i32::<LittleEndian>(self.windows_runtime_type_names_offset)?;
    writer.write_i32::<LittleEndian>(self.windows_runtime_type_names_size)?;
    writer.write_i32::<LittleEndian>(self.windows_runtime_strings_offset)?;
    writer.write_i32::<LittleEndian>(self.windows_runtime_strings_size)?;
    writer.write_i32::<LittleEndian>(self.exported_type_definitions_offset)?;
    writer.write_i32::<LittleEndian>(self.exported_type_definitions_size)?;
    Ok(())
  }

  /// Get all offset/size pairs as mutable references for adjustment
  #[rustfmt::skip]
  pub fn get_offset_size_pairs(&self) -> Vec<(i32, i32)> {
    vec![
      (self.string_literal_offset, self.string_literal_size),
      (self.string_literal_data_offset, self.string_literal_data_size),
      (self.string_offset, self.string_size),
      (self.events_offset, self.events_size),
      (self.properties_offset, self.properties_size),
      (self.methods_offset, self.methods_size),
      (self.parameter_default_values_offset, self.parameter_default_values_size),
      (self.field_default_values_offset, self.field_default_values_size),
      (self.field_and_parameter_default_value_data_offset, self.field_and_parameter_default_value_data_size),
      (self.field_marshaled_sizes_offset, self.field_marshaled_sizes_size),
      (self.parameters_offset, self.parameters_size),
      (self.fields_offset, self.fields_size),
      (self.generic_parameters_offset, self.generic_parameters_size),
      (self.generic_parameter_constraints_offset, self.generic_parameter_constraints_size),
      (self.generic_containers_offset, self.generic_containers_size),
      (self.nested_types_offset, self.nested_types_size),
      (self.interfaces_offset, self.interfaces_size),
      (self.vtable_methods_offset, self.vtable_methods_size),
      (self.interface_offsets_offset, self.interface_offsets_size),
      (self.type_definitions_offset, self.type_definitions_size),
      (self.images_offset, self.images_size),
      (self.assemblies_offset, self.assemblies_size),
      (self.field_refs_offset, self.field_refs_size),
      (self.referenced_assemblies_offset, self.referenced_assemblies_size),
      (self.attribute_data_offset, self.attribute_data_size),
      (self.attribute_data_range_offset, self.attribute_data_range_size),
      (self.unresolved_indirect_call_parameter_types_offset, self.unresolved_indirect_call_parameter_types_size),
      (self.unresolved_indirect_call_parameter_ranges_offset, self.unresolved_indirect_call_parameter_ranges_size),
      (self.windows_runtime_type_names_offset, self.windows_runtime_type_names_size),
      (self.windows_runtime_strings_offset, self.windows_runtime_strings_size),
      (self.exported_type_definitions_offset, self.exported_type_definitions_size),
    ]
  }

  /// Adjust offsets that come after a given position by a delta amount
  pub fn adjust_offsets_after(&mut self, position: i32, delta: i32) {
    macro_rules! adjust_if_after {
      ($field:expr) => {
        if $field > position {
          $field += delta;
        }
      };
    }

    adjust_if_after!(self.string_literal_offset);
    adjust_if_after!(self.string_literal_data_offset);
    adjust_if_after!(self.string_offset);
    adjust_if_after!(self.events_offset);
    adjust_if_after!(self.properties_offset);
    adjust_if_after!(self.methods_offset);
    adjust_if_after!(self.parameter_default_values_offset);
    adjust_if_after!(self.field_default_values_offset);
    adjust_if_after!(self.field_and_parameter_default_value_data_offset);
    adjust_if_after!(self.field_marshaled_sizes_offset);
    adjust_if_after!(self.parameters_offset);
    adjust_if_after!(self.fields_offset);
    adjust_if_after!(self.generic_parameters_offset);
    adjust_if_after!(self.generic_parameter_constraints_offset);
    adjust_if_after!(self.generic_containers_offset);
    adjust_if_after!(self.nested_types_offset);
    adjust_if_after!(self.interfaces_offset);
    adjust_if_after!(self.vtable_methods_offset);
    adjust_if_after!(self.interface_offsets_offset);
    adjust_if_after!(self.type_definitions_offset);
    adjust_if_after!(self.images_offset);
    adjust_if_after!(self.assemblies_offset);
    adjust_if_after!(self.field_refs_offset);
    adjust_if_after!(self.referenced_assemblies_offset);
    adjust_if_after!(self.attribute_data_offset);
    adjust_if_after!(self.attribute_data_range_offset);
    adjust_if_after!(self.unresolved_indirect_call_parameter_types_offset);
    adjust_if_after!(self.unresolved_indirect_call_parameter_ranges_offset);
    adjust_if_after!(self.windows_runtime_type_names_offset);
    adjust_if_after!(self.windows_runtime_strings_offset);
    adjust_if_after!(self.exported_type_definitions_offset);
  }
}

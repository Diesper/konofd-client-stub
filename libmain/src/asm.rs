use log::error;

pub fn encode_b(origin_address: u64, target_address: u64) -> u32 {
  // Offset from the instruction *after* the branch
  let pc = origin_address + 4;
  let offset = (target_address as i64 - pc as i64) / 4;

  // Make sure offset fits in signed 26-bit
  if offset < -(1 << 25) || offset >= (1 << 25) {
    error!(
      "target (0x{target_address:x}) out of range for B instruction, origin: 0x{origin_address:x}"
    );
    todo!();
  }

  // Opcode for B is 0b000101 << 26 == 0x14000000
  let imm26 = (offset as u32) & 0x03FF_FFFF;
  let instruction = 0x14000000 | imm26;

  instruction
}

pub fn encode_adrp(rd: u8, target_addr: u64, pc: u64) -> u32 {
  assert!(rd < 32, "invalid index");

  // Compute page-relative offset
  let imm = (target_addr & !0xfff).wrapping_sub(pc & !0xfff);
  let imm_pages = (imm as i64) >> 12; // Signed

  // Extract immlo and immhi
  let immlo = (imm_pages & 0x3) as u32;
  let immhi = ((imm_pages >> 2) & 0x7ffff) as u32;

  // Encode instruction
  let opcode: u32 = 0b10010000 << 24; // bits 31-24
  let immhi_part = immhi << 5;
  let immlo_part = immlo << 29;
  let rd_part = (rd as u32) & 0x1f;

  opcode | immlo_part | immhi_part | rd_part
}

pub fn encode_add_immediate(rd: u8, rn: u8, imm: u16, shift_12: bool) -> u32 {
  // Validate register numbers (0-31 for AArch64)
  if rd > 31 {
    panic!("Destination register must be 0-31");
  }
  if rn > 31 {
    panic!("Source register must be 0-31");
  }

  // Validate immediate value (12-bit max)
  if imm > 0xFFF {
    panic!("Immediate value must be 12-bit (0-4095)");
  }

  // Build the instruction
  let mut instruction: u32 = 0;

  // Set the base opcode for ADD (immediate) with sf=1 (64-bit)
  instruction |= 0b1001_0001_0000_0000_0000_0000_0000_0000;

  // Set shift bit (bit 22)
  if shift_12 {
    instruction |= 1 << 22;
  }

  // Set immediate value (bits 21-10)
  instruction |= ((imm as u32) & 0xFFF) << 10;

  // Set source register Rn (bits 9-5)
  instruction |= ((rn as u32) & 0x1F) << 5;

  // Set destination register Rd (bits 4-0)
  instruction |= (rd as u32) & 0x1F;

  instruction
}

pub fn encode_add_imm(rd: u8, rn: u8, imm: u16) -> u32 {
  assert!(rd < 32, "invalid destination register");
  assert!(rn < 32, "invalid source register");
  assert!(imm <= 0xFFF, "immediate out of range (must be <= 4095)");

  // Encoding for 32-bit ADD (immediate):
  // 31   30 29 28 27 26 | 25 24 23 22 21 20 19 18 17 16 | 15 14 13 12 11 10 9 8  | 7 6 5 | 4 3 2 1 0
  // sf | op | S | shift |      imm12                    |     Rn                |  Rd
  // For ADD (immediate), sf = 0 (32-bit), op = 0, S = 0, opcode=0b100010

  let sf = 0; // 0 for 32-bit
  let op = 0; // ADD
  let s = 0; // Don't set flags
  let shift = 0; // Shift amount (0 = no shift)
  let opcode = 0b100010; // Bits [31:24] for ADD (immediate)

  ((sf as u32) << 31)
    | ((op as u32) << 30)
    | ((s as u32) << 29)
    | ((opcode as u32) << 24)
    | ((shift as u32) << 22)
    | ((imm as u32) << 10)
    | ((rn as u32) << 5)
    | (rd as u32)
}

pub fn encode_br(xn: u8) -> u32 {
  assert!(xn < 32, "invalid register");
  0xD61F0000 | ((xn as u32) << 5)
}

pub fn encode_blr(xn: u8) -> u32 {
  assert!(xn < 32, "invalid register");
  0xD63F0000 | ((xn as u32) << 5)
}

pub fn encode_mov_reg(xd: u8, xm: u8) -> u32 {
  assert!(xd < 32, "invalid destination register");
  assert!(xm < 32, "invalid source register");

  let sf = 1 << 31; // 64-bit
  let opc = 0b01 << 29; // ORR
  let fixed = 0b010100 << 23;
  let shift = 0b00 << 22; // LSL
  let rm = (xm as u32) << 16;
  let imm6 = 0 << 10; // No shift
  let rn = 31 << 5; // XZR
  let rd = xd as u32;

  sf | opc | fixed | shift | rm | imm6 | rn | rd
}

pub fn encode_xor_zero(reg: u8) -> u32 {
  assert!(reg < 32, "invalid register");

  let sf = 1 << 31;         // 64-bit
  let opcode = 0b01010 << 21; // EOR (register)
  let rm = (reg as u32) << 16;
  let shift = 0 << 22;      // shift = LSL
  let imm6 = 0 << 10;       // no shift amount
  let rn = (reg as u32) << 5;
  let rd = reg as u32;

  sf | opcode | shift | rm | imm6 | rn | rd
}

pub fn encode_ldr_imm(rt: u8, rn: u8, imm: u16) -> u32 {
  assert!(rt < 32, "invalid destination register");
  assert!(rn < 32, "invalid base register");
  assert!(imm % 8 == 0, "immediate must be a multiple of 8");
  assert!(imm <= 0xFFF, "immediate out of range (must be <= 4095)");

  let size = 0b11 << 30;                  // 64-bit load
  let opcode = 0b11111000010 << 21;       // Base opcode
  let imm12 = ((imm / 8) as u32) << 10;   // Scale = 8, so imm/8
  let rn = (rn as u32) << 5;
  let rt = rt as u32;

  size | opcode | imm12 | rn | rt
}

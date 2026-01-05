// Copyright (c) 2018 Rprop (r_prop@outlook.com). Licensed under the MIT Licence.
// See the LICENCE file in the repository root for full licence text.

//! ARM64 Inline Hook Library for Android
//!
//! Original C++ implementation by Rprop (r_prop@outlook.com)
//! https://github.com/Rprop/And64InlineHook

#![allow(clippy::identity_op)]

use std::ptr;
use std::sync::atomic::{AtomicI32, Ordering};

#[cfg(target_arch = "aarch64")]
mod arm64_hook {
  use super::*;

  // Constants
  const A64_MAX_INSTRUCTIONS: usize = 5;
  const A64_MAX_REFERENCES: usize = A64_MAX_INSTRUCTIONS * 2;
  const A64_MAX_BACKUPS: usize = 256;
  const A64_NOP: u32 = 0xd503201f;
  const PAGE_SIZE: usize = 4096;

  /// Fix information for instruction relocation
  #[derive(Clone, Copy, Default)]
  struct FixInfo {
    bp: *mut u32,
    ls: u32, // left-shift counts
    ad: u32, // & operand
  }

  /// Information about relocated instructions
  #[derive(Clone, Copy)]
  struct InsnsInfo {
    ins: i64,
    fmap: [FixInfo; A64_MAX_REFERENCES],
  }

  impl Default for InsnsInfo {
    fn default() -> Self {
      Self {
        ins: 0,
        fmap: [FixInfo::default(); A64_MAX_REFERENCES],
      }
    }
  }

  /// Context for instruction fixing during hook installation
  struct Context {
    basep: i64,
    endp: i64,
    dat: [InsnsInfo; A64_MAX_INSTRUCTIONS],
  }

  impl Context {
    fn new() -> Self {
      Self {
        basep: 0,
        endp: 0,
        dat: [InsnsInfo::default(); A64_MAX_INSTRUCTIONS],
      }
    }

    #[inline]
    fn is_in_fixing_range(&self, absolute_addr: i64) -> bool {
      absolute_addr >= self.basep && absolute_addr < self.endp
    }

    #[inline]
    fn get_ref_ins_index(&self, absolute_addr: i64) -> isize {
      ((absolute_addr - self.basep) / 4) as isize
    }

    #[inline]
    fn get_and_set_current_index(&mut self, inp: *const u32, outp: *mut u32) -> isize {
      let current_idx = self.get_ref_ins_index(inp as i64);
      self.dat[current_idx as usize].ins = outp as i64;
      current_idx
    }

    #[inline]
    fn reset_current_ins(&mut self, idx: isize, outp: *mut u32) {
      self.dat[idx as usize].ins = outp as i64;
    }

    fn insert_fix_map(&mut self, idx: isize, bp: *mut u32, ls: u32, ad: u32) {
      for f in &mut self.dat[idx as usize].fmap {
        if f.bp.is_null() {
          f.bp = bp;
          f.ls = ls;
          f.ad = ad;
          return;
        }
      }
      // Overflow - should not happen in normal usage
    }

    fn process_fix_map(&mut self, idx: isize) {
      for f in &mut self.dat[idx as usize].fmap {
        if f.bp.is_null() {
          break;
        }
        unsafe {
          let offset = (self.dat[idx as usize].ins - f.bp as i64) >> 2;
          *f.bp |= (((offset as i32) << f.ls) as u32) & f.ad;
        }
        f.bp = ptr::null_mut();
      }
    }
  }

  // Helper macros as functions
  #[inline]
  fn page_align(n: usize) -> usize {
    (n + PAGE_SIZE - 1) & !(PAGE_SIZE - 1)
  }

  #[inline]
  fn ptr_align(x: usize) -> usize {
    x & !(PAGE_SIZE - 1)
  }

  #[inline]
  fn align_down(x: usize, n: usize) -> usize {
    x & (!(n - 1))
  }

  /// Make memory region read-write-executable
  unsafe fn make_rwx(p: *mut u8, n: usize) -> i32 {
    let aligned_ptr = ptr_align(p as usize) as *mut libc::c_void;
    let aligned_size = if page_align(p as usize + n) != page_align(p as usize) {
      page_align(n) + PAGE_SIZE
    } else {
      page_align(n)
    };

    libc::mprotect(
      aligned_ptr,
      aligned_size,
      libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
    )
  }

  /// Flush instruction cache
  #[inline]
  unsafe fn flush_cache(c: *mut u8, n: usize) {
    // Use inline assembly for cache flush on ARM64
    std::arch::asm!(
    "dc cvau, {addr}",
    "dsb ish",
    "ic ivau, {addr}",
    "dsb ish",
    "isb",
    addr = in(reg) c,
    options(nostack)
    );

    // For larger regions, we need to flush each cache line
    let cache_line_size: usize = 64; // Typical ARM64 cache line size
    let mut addr = c as usize;
    let end = addr + n;
    while addr < end {
      std::arch::asm!(
      "dc cvau, {addr}",
      "ic ivau, {addr}",
      addr = in(reg) addr,
      options(nostack)
      );
      addr += cache_line_size;
    }
    std::arch::asm!("dsb ish", "isb", options(nostack));
  }

  /// Fix branch immediate instructions (B, BL)
  unsafe fn fix_branch_imm(inpp: &mut *mut u32, outpp: &mut *mut u32, ctxp: &mut Context) -> bool {
    const MBITS: u32 = 6;
    const MASK: u32 = 0xfc000000;
    const RMASK: u32 = 0x03ffffff;
    const OP_B: u32 = 0x14000000;
    const OP_BL: u32 = 0x94000000;

    let ins = **inpp;
    let opc = ins & MASK;

    match opc {
      OP_B | OP_BL => {
        let current_idx = ctxp.get_and_set_current_index(*inpp, *outpp);
        let absolute_addr = (*inpp as i64) + (((ins << MBITS) as i32 >> (MBITS - 2)) as i64);
        let mut new_pc_offset = (absolute_addr - (*outpp as i64)) >> 2;
        let special_fix_type = ctxp.is_in_fixing_range(absolute_addr);

        if !special_fix_type && new_pc_offset.abs() >= (RMASK >> 1) as i64 {
          let b_aligned = ((*outpp as usize).wrapping_add(8) & 7) == 0;

          if opc == OP_B {
            if !b_aligned {
              **outpp = A64_NOP;
              *outpp = (*outpp).add(1);
              ctxp.reset_current_ins(current_idx, *outpp);
            }
            *(*outpp).add(0) = 0x58000051; // LDR X17, #0x8
            *(*outpp).add(1) = 0xd61f0220; // BR X17
            ptr::copy_nonoverlapping(
              &absolute_addr as *const i64 as *const u8,
              (*outpp).add(2) as *mut u8,
              8,
            );
            *outpp = (*outpp).add(4);
          } else {
            if b_aligned {
              **outpp = A64_NOP;
              *outpp = (*outpp).add(1);
              ctxp.reset_current_ins(current_idx, *outpp);
            }
            *(*outpp).add(0) = 0x58000071; // LDR X17, #12
            *(*outpp).add(1) = 0x1000009e; // ADR X30, #16
            *(*outpp).add(2) = 0xd61f0220; // BR X17
            ptr::copy_nonoverlapping(
              &absolute_addr as *const i64 as *const u8,
              (*outpp).add(3) as *mut u8,
              8,
            );
            *outpp = (*outpp).add(5);
          }
        } else {
          if special_fix_type {
            let ref_idx = ctxp.get_ref_ins_index(absolute_addr);
            if ref_idx <= current_idx {
              new_pc_offset = (ctxp.dat[ref_idx as usize].ins - (*outpp as i64)) >> 2;
            } else {
              ctxp.insert_fix_map(ref_idx, *outpp, 0, RMASK);
              new_pc_offset = 0;
            }
          }

          **outpp = opc | ((new_pc_offset as u32) & !MASK);
          *outpp = (*outpp).add(1);
        }

        *inpp = (*inpp).add(1);
        ctxp.process_fix_map(current_idx);
        true
      }
      _ => false,
    }
  }

  /// Fix conditional branch, compare and test branch instructions
  unsafe fn fix_cond_comp_test_branch(
    inpp: &mut *mut u32,
    outpp: &mut *mut u32,
    ctxp: &mut Context,
  ) -> bool {
    const LSB: u32 = 5;
    const LMASK01: u32 = 0xff00001f;
    const MASK0: u32 = 0xff000010;
    const OP_BC: u32 = 0x54000000;
    const MASK1: u32 = 0x7f000000;
    const OP_CBZ: u32 = 0x34000000;
    const OP_CBNZ: u32 = 0x35000000;
    const LMASK2: u32 = 0xfff8001f;
    const MASK2: u32 = 0x7f000000;
    const OP_TBZ: u32 = 0x36000000;
    const OP_TBNZ: u32 = 0x37000000;

    let ins = **inpp;
    let mut lmask = LMASK01;

    if (ins & MASK0) != OP_BC {
      let opc = ins & MASK1;
      if opc != OP_CBZ && opc != OP_CBNZ {
        let opc = ins & MASK2;
        if opc != OP_TBZ && opc != OP_TBNZ {
          return false;
        }
        lmask = LMASK2;
      }
    }

    let msb = (!lmask).leading_zeros();
    let current_idx = ctxp.get_and_set_current_index(*inpp, *outpp);
    let absolute_addr =
      (*inpp as i64) + ((((ins & !lmask) << msb) as i32 >> (LSB - 2 + msb)) as i64);
    let mut new_pc_offset = (absolute_addr - (*outpp as i64)) >> 2;
    let special_fix_type = ctxp.is_in_fixing_range(absolute_addr);

    if !special_fix_type && new_pc_offset.abs() >= (!lmask >> (LSB + 1)) as i64 {
      if ((*outpp as usize).wrapping_add(16) & 7) != 0 {
        **outpp = A64_NOP;
        *outpp = (*outpp).add(1);
        ctxp.reset_current_ins(current_idx, *outpp);
      }
      *(*outpp).add(0) = (((8 >> 2) << LSB) & !lmask) | (ins & lmask);
      *(*outpp).add(1) = 0x14000005; // B #0x14
      *(*outpp).add(2) = 0x58000051; // LDR X17, #0x8
      *(*outpp).add(3) = 0xd61f0220; // BR X17
      ptr::copy_nonoverlapping(
        &absolute_addr as *const i64 as *const u8,
        (*outpp).add(4) as *mut u8,
        8,
      );
      *outpp = (*outpp).add(6);
    } else {
      if special_fix_type {
        let ref_idx = ctxp.get_ref_ins_index(absolute_addr);
        if ref_idx <= current_idx {
          new_pc_offset = (ctxp.dat[ref_idx as usize].ins - (*outpp as i64)) >> 2;
        } else {
          ctxp.insert_fix_map(ref_idx, *outpp, LSB, !lmask);
          new_pc_offset = 0;
        }
      }

      **outpp = ((new_pc_offset as u32) << LSB & !lmask) | (ins & lmask);
      *outpp = (*outpp).add(1);
    }

    *inpp = (*inpp).add(1);
    ctxp.process_fix_map(current_idx);
    true
  }

  /// Fix PC-relative load literal instructions
  unsafe fn fix_loadlit(inpp: &mut *mut u32, outpp: &mut *mut u32, ctxp: &mut Context) -> bool {
    let ins = **inpp;

    // Memory prefetch (prfm), just skip it
    if (ins & 0xff000000) == 0xd8000000 {
      let index = ctxp.get_and_set_current_index(*inpp, *outpp);
      ctxp.process_fix_map(index);
      *inpp = (*inpp).add(1);
      return true;
    }

    const MSB: u32 = 8;
    const LSB: u32 = 5;
    const MASK_30: u32 = 0x40000000;
    const MASK_31: u32 = 0x80000000;
    const LMASK: u32 = 0xff00001f;
    const MASK_LDR: u32 = 0xbf000000;
    const OP_LDR: u32 = 0x18000000;
    const MASK_LDRV: u32 = 0x3f000000;
    const OP_LDRV: u32 = 0x1c000000;
    const MASK_LDRSW: u32 = 0xff000000;
    const OP_LDRSW: u32 = 0x98000000;

    let mut mask = MASK_LDR;
    let mut faligned: usize = if (ins & MASK_30) != 0 { 7 } else { 3 };

    if (ins & MASK_LDR) != OP_LDR {
      mask = MASK_LDRV;
      if faligned != 7 {
        faligned = if (ins & MASK_31) != 0 { 15 } else { 3 };
      }
      if (ins & MASK_LDRV) != OP_LDRV {
        if (ins & MASK_LDRSW) != OP_LDRSW {
          return false;
        }
        mask = MASK_LDRSW;
        faligned = 7;
      }
    }

    let current_idx = ctxp.get_and_set_current_index(*inpp, *outpp);
    let absolute_addr = (*inpp as i64) + ((((ins << MSB) as i32 >> (MSB + LSB - 2)) & !3) as i64);
    let mut new_pc_offset = (absolute_addr - (*outpp as i64)) >> 2;
    let special_fix_type = ctxp.is_in_fixing_range(absolute_addr);

    if special_fix_type
      || (new_pc_offset.abs() as u64 + (faligned + 1 - 4) as u64 / 4)
        >= (!LMASK >> (LSB + 1)) as u64
    {
      while ((*outpp as usize).wrapping_add(8) & faligned) != 0 {
        **outpp = A64_NOP;
        *outpp = (*outpp).add(1);
      }
      ctxp.reset_current_ins(current_idx, *outpp);

      let ns = ((faligned + 1) / 4) as u32;
      *(*outpp).add(0) = (((8 >> 2) << LSB) & !mask) | (ins & LMASK);
      *(*outpp).add(1) = 0x14000001 + ns;
      ptr::copy_nonoverlapping(
        absolute_addr as *const u8,
        (*outpp).add(2) as *mut u8,
        faligned + 1,
      );
      *outpp = (*outpp).add(2 + ns as usize);
    } else {
      let faligned_shifted = faligned >> 2;
      while (new_pc_offset as usize & faligned_shifted) != 0 {
        **outpp = A64_NOP;
        *outpp = (*outpp).add(1);
        new_pc_offset = (absolute_addr - (*outpp as i64)) >> 2;
      }
      ctxp.reset_current_ins(current_idx, *outpp);

      **outpp = ((new_pc_offset as u32) << LSB & !mask) | (ins & LMASK);
      *outpp = (*outpp).add(1);
    }

    *inpp = (*inpp).add(1);
    ctxp.process_fix_map(current_idx);
    true
  }

  /// Fix PC-relative address instructions (ADR, ADRP)
  unsafe fn fix_pcreladdr(inpp: &mut *mut u32, outpp: &mut *mut u32, ctxp: &mut Context) -> bool {
    const MSB: u32 = 8;
    const LSB: u32 = 5;
    const MASK: u32 = 0x9f000000;
    const RMASK: u32 = 0x0000001f;
    const LMASK: u32 = 0xff00001f;
    const FMASK: u32 = 0x00ffffff;
    const MAX_VAL: u32 = 0x001fffff;
    const OP_ADR: u32 = 0x10000000;
    const OP_ADRP: u32 = 0x90000000;

    let ins = **inpp;
    let current_idx;

    match ins & MASK {
      OP_ADR => {
        current_idx = ctxp.get_and_set_current_index(*inpp, *outpp);
        let lsb_bytes = ((ins << 1) >> 30) as i64;
        let absolute_addr =
          (*inpp as i64) + (((((ins << MSB) as i32 >> (MSB + LSB - 2)) & !3) as i64) | lsb_bytes);
        let mut new_pc_offset = absolute_addr - (*outpp as i64);
        let special_fix_type = ctxp.is_in_fixing_range(absolute_addr);

        if !special_fix_type && new_pc_offset.abs() >= (MAX_VAL >> 1) as i64 {
          if ((*outpp as usize).wrapping_add(8) & 7) != 0 {
            **outpp = A64_NOP;
            *outpp = (*outpp).add(1);
            ctxp.reset_current_ins(current_idx, *outpp);
          }

          *(*outpp).add(0) = 0x58000000 | (((8 >> 2) << LSB) & !MASK) | (ins & RMASK);
          *(*outpp).add(1) = 0x14000003; // B #0xc
          ptr::copy_nonoverlapping(
            &absolute_addr as *const i64 as *const u8,
            (*outpp).add(2) as *mut u8,
            8,
          );
          *outpp = (*outpp).add(4);
        } else {
          if special_fix_type {
            let ref_idx = ctxp.get_ref_ins_index(absolute_addr & !3);
            if ref_idx <= current_idx {
              new_pc_offset = ctxp.dat[ref_idx as usize].ins - (*outpp as i64);
            } else {
              ctxp.insert_fix_map(ref_idx, *outpp, LSB, FMASK);
              new_pc_offset = 0;
            }
          }

          **outpp = ((new_pc_offset as u32) << (LSB - 2) & FMASK) | (ins & LMASK);
          *outpp = (*outpp).add(1);
        }
      }
      OP_ADRP => {
        current_idx = ctxp.get_and_set_current_index(*inpp, *outpp);
        let lsb_bytes = ((ins << 1) >> 30) as i32;
        let absolute_addr = ((*inpp as i64) & !0xfff)
          + ((((((ins << MSB) as i32 >> (MSB + LSB - 2)) & !3) | lsb_bytes) as i64) << 12);

        if ctxp.is_in_fixing_range(absolute_addr) {
          let ref_idx = ctxp.get_ref_ins_index(absolute_addr);
          if ref_idx > current_idx {
            // Error case - should not happen
            log::error!("ref_idx must be less than or equal to current_idx!");
          }
          **outpp = ins;
          *outpp = (*outpp).add(1);
        } else {
          if ((*outpp as usize).wrapping_add(8) & 7) != 0 {
            **outpp = A64_NOP;
            *outpp = (*outpp).add(1);
            ctxp.reset_current_ins(current_idx, *outpp);
          }

          *(*outpp).add(0) = 0x58000000 | (((8 >> 2) << LSB) & !MASK) | (ins & RMASK);
          *(*outpp).add(1) = 0x14000003; // B #0xc
          ptr::copy_nonoverlapping(
            &absolute_addr as *const i64 as *const u8,
            (*outpp).add(2) as *mut u8,
            8,
          );
          *outpp = (*outpp).add(4);
        }
      }
      _ => return false,
    }

    ctxp.process_fix_map(current_idx);
    *inpp = (*inpp).add(1);
    true
  }

  /// Fix instructions during trampoline generation
  unsafe fn fix_instructions(inp: *mut u32, count: i32, outp: *mut u32) {
    let mut ctx = Context::new();
    ctx.basep = inp as i64;
    ctx.endp = inp.add(count as usize) as i64;

    let outp_base = outp;
    let mut inp = inp;
    let mut outp = outp;
    let mut count = count;

    while count > 0 {
      count -= 1;

      if fix_branch_imm(&mut inp, &mut outp, &mut ctx) {
        continue;
      }
      if fix_cond_comp_test_branch(&mut inp, &mut outp, &mut ctx) {
        continue;
      }
      if fix_loadlit(&mut inp, &mut outp, &mut ctx) {
        continue;
      }
      if fix_pcreladdr(&mut inp, &mut outp, &mut ctx) {
        continue;
      }

      // Without PC-relative offset
      let index = ctx.get_and_set_current_index(inp, outp);
      ctx.process_fix_map(index);
      *outp = *inp;
      outp = outp.add(1);
      inp = inp.add(1);
    }

    const MASK: u64 = 0x03ffffff;
    let callback = inp as i64;
    let pc_offset = (callback - outp as i64) >> 2;

    if pc_offset.abs() >= (MASK >> 1) as i64 {
      if (outp.add(2) as usize & 7) != 0 {
        *outp = A64_NOP;
        outp = outp.add(1);
      }
      *outp.add(0) = 0x58000051; // LDR X17, #0x8
      *outp.add(1) = 0xd61f0220; // BR X17
      ptr::copy_nonoverlapping(
        &callback as *const i64 as *const u8,
        outp.add(2) as *mut u8,
        8,
      );
      outp = outp.add(4);
    } else {
      *outp = 0x14000000 | ((pc_offset as u32) & MASK as u32);
      outp = outp.add(1);
    }

    let total = outp as usize - outp_base as usize;
    flush_cache(outp_base as *mut u8, total);
  }

  /// Static instruction pool for trampolines
  #[repr(C, align(4096))]
  struct InsnsPool {
    data: [[u32; A64_MAX_INSTRUCTIONS * 10]; A64_MAX_BACKUPS],
  }

  static mut INSNS_POOL: InsnsPool = InsnsPool {
    data: [[0u32; A64_MAX_INSTRUCTIONS * 10]; A64_MAX_BACKUPS],
  };

  static POOL_INITIALIZED: std::sync::Once = std::sync::Once::new();
  static POOL_INDEX: AtomicI32 = AtomicI32::new(-1);

  /// Initialize the instruction pool
  fn init_pool() {
    POOL_INITIALIZED.call_once(|| unsafe {
      let insns_pool_ref = &raw mut INSNS_POOL.data;
      make_rwx(insns_pool_ref as *mut u8, std::mem::size_of::<InsnsPool>());
      log::info!("insns pool initialized.");
    });
  }

  /// Allocate a trampoline from the pool
  fn fast_allocate_trampoline() -> Option<*mut u32> {
    init_pool();

    let i = POOL_INDEX.fetch_add(1, Ordering::SeqCst) + 1;
    if i >= 0 && (i as usize) < A64_MAX_BACKUPS {
      unsafe { Some(INSNS_POOL.data[i as usize].as_mut_ptr()) }
    } else {
      log::error!("failed to allocate trampoline!");
      None
    }
  }

  /// Hook a function with a custom RWX buffer
  ///
  /// # Safety
  /// This function is unsafe because it:
  /// - Modifies executable memory
  /// - Requires valid pointers to executable code
  /// - May cause crashes if used incorrectly
  ///
  /// # Arguments
  /// * `symbol` - Pointer to the function to hook
  /// * `replace` - Pointer to the replacement function
  /// * `rwx` - Pointer to RWX memory for the trampoline
  /// * `rwx_size` - Size of the RWX buffer
  ///
  /// # Returns
  /// Pointer to the trampoline (original function), or null on failure
  pub unsafe fn a64_hook_function_v(
    symbol: *mut u8,
    replace: *mut u8,
    rwx: *mut u8,
    rwx_size: usize,
  ) -> *mut u8 {
    const MASK: u64 = 0x03ffffff;

    let trampoline = rwx as *mut u32;
    let original = symbol as *mut u32;

    let pc_offset = ((replace as i64) - (symbol as i64)) >> 2;

    if pc_offset.abs() >= (MASK >> 1) as i64 {
      let count = if (original.add(2) as usize & 7) != 0 {
        5
      } else {
        4
      };

      if !trampoline.is_null() {
        if rwx_size < count * 10 {
          log::error!(
            "rwx size is too small to hold {} bytes backup instructions! ",
            count * 10
          );
          return ptr::null_mut();
        }
        fix_instructions(original, count as i32, trampoline);
      }

      if make_rwx(original as *mut u8, 5 * 4) == 0 {
        let mut original = original;
        if count == 5 {
          *original = A64_NOP;
          original = original.add(1);
        }
        *original.add(0) = 0x58000051; // LDR X17, #0x8
        *original.add(1) = 0xd61f0220; // BR X17
        ptr::copy_nonoverlapping(
          &replace as *const *mut u8 as *const u8,
          original.add(2) as *mut u8,
          8,
        );
        flush_cache(symbol, 5 * 4);

        log::info!(
          "inline hook {:p}->{:p} successfully! {} bytes overwritten",
          symbol,
          replace,
          5 * 4
        );

        trampoline as *mut u8
      } else {
        log::error!(
          "mprotect failed with errno = {}, p = {:p}, size = {}",
          *libc::__errno(),
          original,
          5 * 4
        );
        ptr::null_mut()
      }
    } else {
      if !trampoline.is_null() {
        if rwx_size < 10 {
          log::error!(
            "rwx size is too small to hold {} bytes backup instructions!",
            10
          );
          return ptr::null_mut();
        }
        fix_instructions(original, 1, trampoline);
      }

      if make_rwx(original as *mut u8, 4) == 0 {
        // Atomic compare and swap
        let expected = *original;
        let new_val = 0x14000000 | ((pc_offset as u32) & MASK as u32);

        #[cfg(target_has_atomic = "32")]
        {
          use std::sync::atomic::AtomicU32;
          let atomic = &*(original as *const AtomicU32);
          let _ = atomic.compare_exchange(expected, new_val, Ordering::SeqCst, Ordering::SeqCst);
        }

        flush_cache(symbol, 4);

        log::info!(
          "inline hook {:p}->{:p} successfully! {} bytes overwritten",
          symbol,
          replace,
          4
        );

        trampoline as *mut u8
      } else {
        log::error!(
          "mprotect failed with errno = {}, p = {:p}, size = {}",
          *libc::__errno(),
          original,
          4
        );
        ptr::null_mut()
      }
    }
  }

  /// Hook a function using the built-in trampoline pool
  ///
  /// # Safety
  /// This function is unsafe because it:
  /// - Modifies executable memory
  /// - Requires valid pointers to executable code
  /// - May cause crashes if used incorrectly
  ///
  /// # Arguments
  /// * `symbol` - Pointer to the function to hook
  /// * `replace` - Pointer to the replacement function
  /// * `result` - Optional pointer to store the trampoline address
  pub unsafe fn a64_hook_function(
    symbol: *mut u8,
    replace: *mut u8,
    mut result: Option<&mut *mut u8>,
  ) {
    let mut trampoline: *mut u8 = ptr::null_mut();

    if let Some(result_ptr) = &mut result {
      if let Some(t) = fast_allocate_trampoline() {
        trampoline = t as *mut u8;
        **result_ptr = trampoline;
      } else {
        **result_ptr = ptr::null_mut();
        return;
      }
    }

    // Fix Android 10 . text segment is read-only by default
    make_rwx(symbol, 5 * std::mem::size_of::<usize>());

    let result_trampoline =
      a64_hook_function_v(symbol, replace, trampoline, A64_MAX_INSTRUCTIONS * 10);

    if result_trampoline.is_null() {
      if let Some(result_ptr) = &mut result {
        **result_ptr = ptr::null_mut();
      }
    }
  }
}

#[cfg(target_arch = "aarch64")]
pub use arm64_hook::*;

/// High-level safe wrapper for hooking functions
#[cfg(target_arch = "aarch64")]
pub struct Hook {
  original: *mut u8,
  trampoline: *mut u8,
}

#[cfg(target_arch = "aarch64")]
impl Hook {
  /// Create a new hook
  ///
  /// # Safety
  /// The caller must ensure that:
  /// - `target` points to a valid function
  /// - `replacement` has the same signature as `target`
  /// - The hook is not removed while the replacement function may be called
  pub unsafe fn new<T>(target: *mut T, replacement: *mut T) -> Option<Self> {
    let mut trampoline: *mut u8 = ptr::null_mut();
    a64_hook_function(
      target as *mut u8,
      replacement as *mut u8,
      Some(&mut trampoline),
    );

    if trampoline.is_null() {
      None
    } else {
      Some(Self {
        original: target as *mut u8,
        trampoline,
      })
    }
  }

  /// Get the trampoline (original function)
  pub fn trampoline<T>(&self) -> *mut T {
    self.trampoline as *mut T
  }

  /// Get the original function address
  pub fn original<T>(&self) -> *mut T {
    self.original as *mut T
  }
}

#[cfg(not(target_arch = "aarch64"))]
compile_error!("This library only supports aarch64 architecture");

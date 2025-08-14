use log::{info, trace};
use std::fs::{File, OpenOptions};
use std::io;
use std::io::{BufRead, Read, Seek, SeekFrom, Write};
use memmem::{Searcher, TwoWaySearcher};
use crate::util::bytes_to_human_readable;

#[derive(Debug)]
pub struct MemoryRegion {
  pub start: usize,
  pub end: usize,
  pub offset: usize,
  pub perms: String,
  pub inode: String,
  pub dev: String,
  pub pathname: Option<String>,
}

impl MemoryRegion {
  pub fn size(&self) -> usize {
    self.end - self.start
  }
}

pub fn read_memory_regions() -> io::Result<Vec<MemoryRegion>> {
  let path = "/proc/self/maps";
  let file = File::open(&path)?;
  let reader = io::BufReader::new(file);

  let mut regions = Vec::new();

  for line in reader.lines() {
    let line = line?;
    let parts: Vec<&str> = line.split_whitespace().collect();
    if parts.len() < 5 {
      continue;
    }

    trace!("{:?}", parts);
    let (start, end) = parts[0].split_once('-').unwrap();
    let (start, end) = (
      usize::from_str_radix(start, 16).unwrap(),
      usize::from_str_radix(end, 16).unwrap(),
    );
    let perms = parts[1].to_string();
    let offset = usize::from_str_radix(parts[2], 16).unwrap();
    let dev = parts[3].to_string();
    let inode = parts[4].to_string();
    let pathname = if parts.len() > 5 {
      Some(parts[5..].join(" "))
    } else {
      None
    };

    regions.push(MemoryRegion {
      start,
      end,
      offset,
      perms,
      dev,
      inode,
      pathname,
    });
  }

  Ok(regions)
}

pub fn get_suitable_regions() -> Vec<MemoryRegion> {
  let regions = match read_memory_regions() {
    Ok(regions) => regions,
    Err(error) => {
      panic!("Failed to read memory regions: {}", error);
    }
  };

  let mut suitable_regions = Vec::new();
  for region in regions {
    if !region.perms.contains("w") {
      trace!("region {region:?} is not writable");
      continue;
    }

    suitable_regions.push(region);
  }

  let total_size: u64 = suitable_regions
    .iter()
    .map(|region| region.size() as u64)
    .sum();
  info!("{} to scan", bytes_to_human_readable(total_size));

  suitable_regions
}

pub fn search_byte_sequence(
  region: &MemoryRegion,
  sequence: &[u8],
) -> io::Result<Vec<usize>> {
  let mem_path = "/proc/self/mem";
  let mut mem_file = OpenOptions::new().read(true).open(&mem_path)?;

  let mut buffer = vec![0; region.size()];
  mem_file.seek(SeekFrom::Start(region.start as u64))?;
  let read = mem_file.read(&mut buffer)?;
  trace!("read {} / {} bytes", read, buffer.len());

  let mut start = 0;
  let mut addresses = Vec::new();
  let searcher = TwoWaySearcher::new(sequence);
  loop {
    trace!("searching from {}", start);
    let position = searcher.search_in(&buffer[start..]);
    if let Some(position) = position {
      addresses.push(start + position);
      start += position + sequence.len();
    } else {
      break;
    }
  }

  Ok(addresses)
}

pub fn write_to_memory(address: usize, data: &[u8]) -> io::Result<()> {
  let mem_path = "/proc/self/mem";
  let mut mem_file = OpenOptions::new().read(true).write(true).open(&mem_path)?;

  mem_file.seek(SeekFrom::Start(address as u64))?;
  mem_file.write_all(data)?;

  Ok(())
}

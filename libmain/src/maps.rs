use std::fs::File;
use std::io;
use std::io::BufRead;

use log::trace;

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
  let file = File::open(path)?;
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

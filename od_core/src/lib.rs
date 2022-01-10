#![feature(once_cell)]

#[link(name = "od_interface")] extern "C"{}

use std::fs::File;
use std::lazy::SyncOnceCell;
use xlang_abi::io::ReadAdapter;
use xlang_abi::traits::DynMut;

#[repr(C)]
pub struct Tree;

pub mod abi_safe {
    pub use super::Tree;
    use xlang_abi::traits::DynMut;

    pub enum Error {
        InvalidFile(xlang_abi::string::String),
        Io(xlang_abi::io::Error),
        MoreInfoNeeded(xlang_abi::string::String), // Where the string represents a regex of a file that could provide the additional information
        Unrecognized,
    }

    pub type Result<T> = xlang_abi::result::Result<T, Error>;

    pub trait Disassembler {
        fn can_read<'a>(&self, file: DynMut<dyn xlang_abi::io::Read + 'a>) -> Result<bool>;
        fn disassemble<'a>(&self, file: DynMut<dyn xlang_abi::io::Read + 'a>) -> Result<Tree>;
    }
}

pub enum Error {
    InvalidFile(String),
    Io(std::io::Error),
    MoreInfoNeeded(String),
    Unrecognized,
}

pub type Result<T> = std::result::Result<T, Error>;

impl From<abi_safe::Error> for Error {
    fn from(err: abi_safe::Error) -> Self {
        match err {
            abi_safe::Error::InvalidFile(x) => Self::InvalidFile(x.to_string()),
            abi_safe::Error::Io(x) => Self::Io(x.into()),
            abi_safe::Error::MoreInfoNeeded(x) => Self::MoreInfoNeeded(x.to_string()),
            abi_safe::Error::Unrecognized => Self::Unrecognized,
        }
    }
}

static DISASSEMBLERS: SyncOnceCell<Vec<Box<dyn abi_safe::Disassembler + Send + Sync>>> = SyncOnceCell::new();

#[allow(clippy::let_and_return)]
pub fn init_list() -> Vec<Box<dyn abi_safe::Disassembler + Send + Sync>> {
    let result = Vec::new();
    // TODO
    result
}

pub fn load_file(file: File) -> Result<Tree> {
    let disassemblers = DISASSEMBLERS.get_or_init(init_list);
    for disassembler in disassemblers {
        if Result::from(disassembler.can_read(DynMut::unsize_mut(&mut ReadAdapter::new(&file))).map_err(Into::into))? {
            return Result::from(disassembler.disassemble(DynMut::unsize_mut(&mut ReadAdapter::new(file))).map_err(Into::into));
        }
    }
    Err(Error::Unrecognized)
}

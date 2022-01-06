use std::fs::File;

#[repr(C)]
pub struct Tree;

pub mod abi_safe {
    pub use super::Tree;
use xlang_abi::traits::DynPtrSafe;

    pub enum Error {
        InvalidFile(xlang_abi::string::String),
        Io(xlang_abi::io::Error),
        MoreInfoNeeded(xlang_abi::string::String), // Where the string represents a regex of a file that could provide the additional information
    }

    pub type Result<T> = xlang_abi::result::Result<T, Error>;

    pub trait Disassembler {
        fn can_read(file: dyn DynPtrSafe<dyn xlang_abi::io::Read>) -> Result<bool>;
        fn disassemble(file: dyn DynPtrSafe<dyn xlang_abi::io::Read>) -> Result<Tree>;
    }
}

pub enum Error {
    InvalidFile(String),
    Io(std::io::Error),
    MoreInfoNeeded(String),
}

pub type Result<T> = std::result::Result<T, Error>;

impl From<abi_safe::Error> for Error {
    fn from(err: abi_safe::Error) -> Self {
        match err {
            abi_safe::Error::InvalidFile(x) => Self::InvalidFile(x.to_string()),
            abi_safe::Error::Io(x) => Self::Io(x.into()),
            abi_safe::Error::MoreInfoNeeded(x) => Self::MoreInfoNeeded(x.to_string()),
        }
    }
}

pub fn load_file(_: File) -> crate::Result<Tree> {
    Ok(Tree)
}

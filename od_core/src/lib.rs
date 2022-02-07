#![feature(once_cell)]

#[link(name = "od_interface")]
extern "C" {}

pub mod structure;

use std::fs::File;
use std::lazy::SyncOnceCell;
use structure::TreeNode;
use xlang_abi::io::ReadAdapter;
use xlang_abi::traits::DynMut;
use xlang_host::{dso::Handle, rustcall};

pub mod abi_safe {
    pub use super::structure::TreeNode;
    use xlang_abi::alloc::Allocator;
    use xlang_abi::io::Read;
    use xlang_abi::traits::{AbiSafeTrait, AbiSafeVTable, DynBox, DynMut, DynPtrSafe};
    use xlang_host::rustcall;

    pub enum Error {
        InvalidFile(xlang_abi::string::String),
        Io(xlang_abi::io::Error),
        MoreInfoNeeded(xlang_abi::string::String), // Where the string represents a regex of a file that could provide the additional information
        Unrecognized,
    }

    pub type Result<T> = xlang_abi::result::Result<T, Error>;

    pub trait Disassembler {
        fn can_read<'a>(&self, file: DynMut<dyn Read + 'a>) -> Result<bool>;
        fn disassemble<'a>(&self, file: DynMut<dyn Read + 'a>) -> Result<TreeNode>;
    }

    #[repr(C)]
    pub struct DisassemblerVTable {
        size: usize,
        align: usize,
        destructor: Option<unsafe extern "C" fn(*mut ())>,
        reserved_dealloc: Option<unsafe extern "C" fn(*mut ())>,
        can_read: rustcall!(extern "rustcall" fn(*const (), DynMut<dyn Read>) -> Result<bool>),
        disassemble: rustcall!(extern "rustcall" fn(*const (), DynMut<dyn Read>) -> Result<TreeNode>),
    }

    unsafe impl<'a> AbiSafeVTable<dyn Disassembler + 'a> for DisassemblerVTable {}

    unsafe impl AbiSafeTrait for dyn Disassembler {
        type VTable = DisassemblerVTable;
    }

    unsafe impl AbiSafeTrait for dyn Disassembler + Send {
        type VTable = DisassemblerVTable;
    }

    unsafe impl AbiSafeTrait for dyn Disassembler + Sync {
        type VTable = DisassemblerVTable;
    }

    unsafe impl AbiSafeTrait for dyn Disassembler + Send + Sync {
        type VTable = DisassemblerVTable;
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            unsafe { (self.vtable().can_read)(self.as_raw_mut(), file) }
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            unsafe { (self.vtable().disassemble)(self.as_raw_mut(), file) }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Send + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            unsafe { (self.vtable().can_read)(self.as_raw_mut(), file) }
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            unsafe { (self.vtable().disassemble)(self.as_raw_mut(), file) }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Sync + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            unsafe { (self.vtable().can_read)(self.as_raw_mut(), file) }
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            unsafe { (self.vtable().disassemble)(self.as_raw_mut(), file) }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            unsafe { (self.vtable().can_read)(self.as_raw_mut(), file) }
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            unsafe { (self.vtable().disassemble)(self.as_raw_mut(), file) }
        }
    }

    impl<'lt, T: ?Sized + AbiSafeTrait> Disassembler for DynMut<'lt, T>
    where
        dyn DynPtrSafe<T> + 'lt: Disassembler,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            <dyn DynPtrSafe<T> as Disassembler>::can_read(&**self, file)
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            <dyn DynPtrSafe<T> as Disassembler>::disassemble(&**self, file)
        }
    }

    impl<'lt, T: ?Sized + AbiSafeTrait + 'static, A: Allocator> Disassembler for DynBox<T, A>
    where
        dyn DynPtrSafe<T> + 'lt: Disassembler,
    {
        fn can_read(&self, file: DynMut<dyn Read>) -> Result<bool> {
            <dyn DynPtrSafe<T> as Disassembler>::can_read(&**self, file)
        }

        fn disassemble(&self, file: DynMut<dyn Read>) -> Result<TreeNode> {
            <dyn DynPtrSafe<T> as Disassembler>::disassemble(&**self, file)
        }
    }
}

use abi_safe::Disassembler;

#[derive(Debug)]
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

static DISASSEMBLER_NAMES: [&str; 1] = ["elf"];

static DISASSEMBLERS: SyncOnceCell<Vec<xlang_abi::traits::DynBox<dyn abi_safe::Disassembler + Send + Sync>>> =
    SyncOnceCell::new();

#[allow(clippy::let_and_return)]
pub fn init_list() -> Vec<xlang_abi::traits::DynBox<dyn abi_safe::Disassembler + Send + Sync>> {
    let search_paths = Vec::new();
    search_paths.push(std::env::current_exe().unwrap());

    let disassembler_paths = Vec::new();
    for library in DISASSEMBLER_NAMES {
        let library_name = if cfg!(windows) {
            String::from("od_module_") + library + ".dll"
        } else if cfg!(target_os = "linux") {
            String::from("libod_module_") + library + ".so"
        } else if cfg!(target_os = "macos") {
            String::from("libod_module_") + library + ".dylib"
        } else {
            panic!("unrecognized target OS; can't get frontend library name")
        };

        let mut path = None;
        for search_path in search_paths {
            let mut library_path = search_path.clone();
            library_path.push(&library_name);
            if library_path.exists() {
                path = Some(library_path);
                break;
            }
        }

        if let Some(path) = path {
            disassembler_paths.push(path);
        } else {
            eprintln!("warning: couldn't locate disassembler \"{}\"", library);
        }
    }

    let mut disassembler_handles = Vec::new();
    for path in &disassembler_paths {
        disassembler_handles.push(Handle::open(path).expect("couldn't load disassembler library"));
    }

    let mut result = Vec::new();
    for handle in disassembler_handles {
        let init: rustcall!(
            extern "rustcall" fn() -> xlang_abi::traits::DynBox<dyn abi_safe::Disassembler + Send + Sync>
        ) = unsafe { handle.function_sym("od_module_main") }
            .expect("disassembler library missing required entry point");
        result.push(init());
    }

    result
}

pub fn load_file(file: File) -> Result<TreeNode> {
    let disassemblers = DISASSEMBLERS.get_or_init(init_list);
    for disassembler in disassemblers {
        if Result::from(
            disassembler
                .can_read(DynMut::unsize_mut(&mut ReadAdapter::new(&file)))
                .map_err(Into::into),
        )? {
            return Result::from(
                disassembler
                    .disassemble(DynMut::unsize_mut(&mut ReadAdapter::new(file)))
                    .map_err(Into::into),
            );
        }
    }
    Err(Error::Unrecognized)
}

#![feature(once_cell)]

#[link(name = "od_interface")]
extern "C" {}

pub mod structure;

use std::fs::File;
use std::io::Seek;
use std::sync::OnceLock;
use structure::TreeNode;
use xlang_abi::io::ReadSeekAdapter;
use xlang_abi::traits::DynMut;
use xlang_host::{dso::Handle, rustcall};

pub mod abi_safe {
    pub use super::structure::{Leaf, NodeId, NodeState, TreeNode};
    use xlang_abi::alloc::Allocator;
    use xlang_abi::io::ReadSeek;
    use xlang_abi::traits::{
        AbiSafeTrait, AbiSafeUnsize, AbiSafeVTable, DynBox, DynMut, DynPtrSafe,
    };
    use xlang_host::rustcall;

    #[repr(u8)]
    pub enum Error {
        InvalidFile(xlang_abi::string::String),
        Io(xlang_abi::io::Error),
        MoreInfoNeeded(xlang_abi::string::String), // Where the string represents a regex of a file that could provide the additional information
        Unrecognized,
    }

    pub type Result<T> = xlang_abi::result::Result<T, Error>;

    pub trait Disassembler {
        fn can_read<'a>(&self, file: DynMut<dyn ReadSeek + 'a>) -> Result<bool>;
        fn disassemble<'a>(&self, file: DynMut<dyn ReadSeek + 'a>) -> Result<TreeNode>;
    }

    #[repr(C)]
    pub struct DisassemblerVTable {
        size: usize,
        align: usize,
        destructor: Option<unsafe extern "C" fn(*mut ())>,
        reserved_dealloc: Option<unsafe extern "C" fn(*mut ())>,
        can_read:
            rustcall!(unsafe extern "rustcall" fn(*const (), DynMut<dyn ReadSeek>) -> Result<bool>),
        disassemble: rustcall!(
            unsafe extern "rustcall" fn(*const (), DynMut<dyn ReadSeek>) -> Result<TreeNode>
        ),
    }

    unsafe impl<'a> AbiSafeVTable<dyn Disassembler + 'a> for DisassemblerVTable {}
    unsafe impl<'a> AbiSafeVTable<dyn Disassembler + Send + 'a> for DisassemblerVTable {}
    unsafe impl<'a> AbiSafeVTable<dyn Disassembler + Sync + 'a> for DisassemblerVTable {}
    unsafe impl<'a> AbiSafeVTable<dyn Disassembler + Send + Sync + 'a> for DisassemblerVTable {}

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

    rustcall! {
        unsafe extern "rustcall" fn vtbl_destroy<T: Disassembler>(this: *mut ()) {
            core::ptr::drop_in_place(this.cast::<T>());
        }
    }

    rustcall! {
        unsafe extern "rustcall" fn vtbl_can_read<T: Disassembler>(this: *const (), file: DynMut<dyn ReadSeek>) -> Result<bool> {
            <T as Disassembler>::can_read(&*(this.cast::<T>()), file)
        }
    }

    rustcall! {
        unsafe extern "rustcall" fn vtbl_disassemble<T: Disassembler>(this: *const (), file: DynMut<dyn ReadSeek>) -> Result<TreeNode> {
            <T as Disassembler>::disassemble(&*(this.cast::<T>()), file)
        }
    }

    unsafe impl<T: Disassembler> AbiSafeUnsize<T> for dyn Disassembler {
        fn construct_vtable_for() -> &'static Self::VTable {
            &DisassemblerVTable {
                size: std::mem::size_of::<T>(),
                align: std::mem::align_of::<T>(),
                destructor: Some(vtbl_destroy::<T>),
                reserved_dealloc: None,
                can_read: vtbl_can_read::<T>,
                disassemble: vtbl_disassemble::<T>,
            }
        }
    }

    unsafe impl<T: Disassembler> AbiSafeUnsize<T> for dyn Disassembler + Send {
        fn construct_vtable_for() -> &'static Self::VTable {
            &DisassemblerVTable {
                size: std::mem::size_of::<T>(),
                align: std::mem::align_of::<T>(),
                destructor: Some(vtbl_destroy::<T>),
                reserved_dealloc: None,
                can_read: vtbl_can_read::<T>,
                disassemble: vtbl_disassemble::<T>,
            }
        }
    }

    unsafe impl<T: Disassembler> AbiSafeUnsize<T> for dyn Disassembler + Sync {
        fn construct_vtable_for() -> &'static Self::VTable {
            &DisassemblerVTable {
                size: std::mem::size_of::<T>(),
                align: std::mem::align_of::<T>(),
                destructor: Some(vtbl_destroy::<T>),
                reserved_dealloc: None,
                can_read: vtbl_can_read::<T>,
                disassemble: vtbl_disassemble::<T>,
            }
        }
    }

    unsafe impl<T: Disassembler> AbiSafeUnsize<T> for dyn Disassembler + Send + Sync {
        fn construct_vtable_for() -> &'static Self::VTable {
            &DisassemblerVTable {
                size: std::mem::size_of::<T>(),
                align: std::mem::align_of::<T>(),
                destructor: Some(vtbl_destroy::<T>),
                reserved_dealloc: None,
                can_read: vtbl_can_read::<T>,
                disassemble: vtbl_disassemble::<T>,
            }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().can_read)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().disassemble)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Send + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().can_read)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().disassemble)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Sync + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().can_read)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().disassemble)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }
    }

    impl<'a, 'lt> Disassembler for dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'a> + 'lt
    where
        'a: 'lt,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().can_read)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
            unsafe {
                let this = std::mem::transmute::<
                    _,
                    &dyn DynPtrSafe<dyn Disassembler + Send + Sync + 'static>,
                >(self);
                (this.vtable().disassemble)(
                    this.as_raw(),
                    std::mem::transmute::<_, DynMut<dyn ReadSeek + 'static>>(file),
                )
            }
        }
    }

    impl<'lt, T: ?Sized + AbiSafeTrait> Disassembler for DynMut<'lt, T>
    where
        dyn DynPtrSafe<T> + 'lt: Disassembler,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            <dyn DynPtrSafe<T> as Disassembler>::can_read(&**self, file)
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
            <dyn DynPtrSafe<T> as Disassembler>::disassemble(&**self, file)
        }
    }

    impl<'lt, T: ?Sized + AbiSafeTrait + 'static, A: Allocator> Disassembler for DynBox<T, A>
    where
        dyn DynPtrSafe<T> + 'lt: Disassembler,
    {
        fn can_read<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<bool> {
            <dyn DynPtrSafe<T> as Disassembler>::can_read(&**self, file)
        }

        fn disassemble<'b>(&self, file: DynMut<dyn ReadSeek + 'b>) -> Result<TreeNode> {
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

static DISASSEMBLERS: OnceLock<
    Vec<xlang_abi::traits::DynBox<dyn abi_safe::Disassembler + Send + Sync>>,
> = OnceLock::new();

#[allow(clippy::let_and_return)]
pub fn init_list() -> Vec<xlang_abi::traits::DynBox<dyn abi_safe::Disassembler + Send + Sync>> {
    let mut search_paths = Vec::new();
    search_paths.push({
        let mut my_path = std::env::current_exe().unwrap();
        my_path.pop();
        my_path.push("deps");
        my_path
    });

    let mut disassembler_paths = Vec::new();
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
        for search_path in &search_paths {
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
        let mut handle = core::mem::ManuallyDrop::new(handle); // TODO: Do something proper here
        let init: rustcall!(
            extern "rustcall" fn() -> xlang_abi::traits::DynBox<
                dyn abi_safe::Disassembler + Send + Sync,
            >
        ) = unsafe { handle.function_sym("od_module_main") }
            .expect("disassembler library missing required entry point");
        result.push(init());
    }

    result
}

pub fn load_file(file: File) -> Result<TreeNode> {
    let disassemblers = DISASSEMBLERS.get_or_init(init_list);
    for disassembler in disassemblers {
        if {
            let result = Result::from(
                disassembler
                    .can_read(DynMut::unsize_mut(&mut ReadSeekAdapter::new(&file)))
                    .map_err(Into::into),
            )?;
            (&file).rewind().unwrap();
            result
        } {
            return Result::from(
                disassembler
                    .disassemble(DynMut::unsize_mut(&mut ReadSeekAdapter::new(file)))
                    .map_err(Into::into),
            );
        }
    }
    Err(Error::Unrecognized)
}

#[cfg(test)]
mod tests {
    use crate::load_file;
    use std::fs::File;
    use std::path::PathBuf;

    #[test]
    fn infra_is_sane() {
        let mut d = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
        d.push("test/empty_elf.bin");
        assert!(load_file(File::open(d).expect("couldn't load file")).is_ok());
    }
}

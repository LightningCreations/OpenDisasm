use xlang_abi::prelude::v1::*;
use xlang_host::rustcall;

use od_core::abi_safe::Disassembler;
use od_core::abi_safe::TreeNode;

struct ElfDisassembler {}

impl ElfDisassembler {
    #[must_use]
    fn new() -> ElfDisassembler {
        ElfDisassembler {}
    }
}

impl Disassembler for ElfDisassembler {
    fn can_read<'a>(&self, _: DynMut<dyn xlang_abi::io::Read + 'a>) -> xlang_abi::result::Result<bool, od_core::abi_safe::Error> { todo!() }
    fn disassemble<'a>(&self, _: DynMut<dyn xlang_abi::io::Read + 'a>) -> xlang_abi::result::Result<TreeNode, od_core::abi_safe::Error> { todo!() }
}

rustcall! {
#[no_mangle]
pub extern "rustcall" fn od_module_main() -> xlang_abi::traits::DynBox<dyn Disassembler + Send + Sync> {
    DynBox::unsize_box(Box::new(ElfDisassembler::new()))
}
}

use xlang_abi::prelude::v1::*;

use lazy_static::lazy_static;

use xlang_abi::io::Read;
use xlang_abi::result::Result::Err;
use xlang_abi::result::Result::Ok;
use xlang_abi::span::SpanMut;
use xlang_host::rustcall;

use od_core::abi_safe::{Disassembler, NodeId, NodeState, TreeNode};

struct ElfDisassembler {}

impl ElfDisassembler {
    #[must_use]
    fn new() -> ElfDisassembler {
        ElfDisassembler {}
    }
}

lazy_static! {
    static ref ELF_CLASS: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfClassNone".into());
        m.insert(1, "ElfClass32".into());
        m.insert(2, "ElfClass64".into());
        m
    };

    static ref ELF_DATA: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfDataNone".into());
        m.insert(1, "ElfData32".into());
        m.insert(2, "ElfData64".into());
        m
    };

    static ref ELF_OSABI: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfOsAbiNone".into());
        m.insert(1, "ElfOsAbiHpUx".into());
        m
    };

    static ref ELF_VERSION: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfVersionNone".into());
        m.insert(1, "ElfVersionCurrent".into());
        m
    };
}

impl Disassembler for ElfDisassembler {
    fn can_read<'a>(
        &self,
        mut file: DynMut<dyn xlang_abi::io::Read + 'a>,
    ) -> xlang_abi::result::Result<bool, od_core::abi_safe::Error> {
        let mut buf = [0; 4];
        if {
            match file.read(SpanMut::new(&mut buf)) {
                Ok(x) => x,
                Err(x) => return Err(od_core::abi_safe::Error::Io(x)),
            }
        } != 4
        {
            return Ok(false);
        }
        Ok(buf[0] == 0x7F && buf[1] == b'E' && buf[2] == b'L' && buf[3] == b'F')
    }

    fn disassemble<'a>(
        &self,
        mut input: DynMut<dyn xlang_abi::io::Read + 'a>,
    ) -> xlang_abi::result::Result<TreeNode, od_core::abi_safe::Error> {
        let mut result = HashMap::new();
        let mut order = Vec::new();
        let mut e_ident = [0u8; 16];
        input.read(SpanMut::new(&mut e_ident));
        let ei_class = e_ident[4];
        println!("{:?}", *ELF_CLASS);
        println!("{:?}", ELF_CLASS.clone());
        result.insert(
            String::from("ei_class"),
            TreeNode {
                state: NodeState::new_enum("ElfClass", ei_class as u128, ELF_CLASS.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_class"));
        let ei_data = e_ident[5];
        result.insert(
            String::from("ei_data"),
            TreeNode {
                state: NodeState::new_enum("ElfData", ei_data as u128, ELF_DATA.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_data"));
        let ei_version = e_ident[6];
        result.insert(
            String::from("ei_version"),
            TreeNode {
                state: NodeState::new_enum("ElfVersion", ei_version as u128, ELF_VERSION.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_version"));
        let ei_osabi = e_ident[7];
        result.insert(
            String::from("ei_osabi"),
            TreeNode {
                state: NodeState::new_enum("ElfOsAbi", ei_osabi as u128, ELF_OSABI.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_osabi"));
        let ei_abiversion = e_ident[8];
        result.insert(
            String::from("ei_abiversion"),
            TreeNode {
                state: ei_abiversion.into(),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_abiversion"));
        Ok(TreeNode {
            state: NodeState::Object {
                typename: String::from("ElfHeader"),
                value: result,
                order,
            },
            disasm_id: String::from("elf"),
            format: Some(String::from("elf")),
            id: NodeId::new(),
            has_incomplete_children: false,
        })
    }
}

rustcall! {
    #[no_mangle]
    pub extern "rustcall" fn od_module_main() -> xlang_abi::traits::DynBox<dyn Disassembler + Send + Sync> {
        DynBox::unsize_box(Box::new(ElfDisassembler::new()))
    }
}

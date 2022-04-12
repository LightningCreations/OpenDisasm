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
    static ref ELF_MACHINE: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfMachineNone".into());
        m.insert(3, "ElfMachineX86".into());
        m.insert(0x28, "ElfMachineArm".into());
        m.insert(0x32, "ElfMachineIA64".into());
        m.insert(0x3E, "ElfMachineAmd64".into());
        m.insert(0xB7, "ElfMachineArm64".into());
        m.insert(0xF3, "ElfMachineRiscV".into());
        m.insert(0x101, "ElfMachineW65".into());
        m
    };
    static ref ELF_OSABI: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfOsAbiNone".into());
        m.insert(1, "ElfOsAbiHpUx".into());
        m
    };
    static ref ELF_TYPE: HashMap<u128, String> = {
        let mut m = HashMap::new();
        m.insert(0, "ElfTypeNone".into());
        m.insert(1, "ElfTypeRelocatable".into());
        m.insert(2, "ElfTypeExecutable".into());
        m.insert(3, "ElfTypeDynamic".into());
        m.insert(4, "ElfTypeCore".into());
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
        result.insert(
            String::from("ei_class"),
            TreeNode {
                state: NodeState::new_enum("ElfClass", ei_class.into(), ELF_CLASS.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_class"));
        let ei_data = e_ident[5];
        result.insert(
            String::from("ei_data"),
            TreeNode {
                state: NodeState::new_enum("ElfData", ei_data.into(), ELF_DATA.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_data"));
        let ei_version = e_ident[6];
        result.insert(
            String::from("ei_version"),
            TreeNode {
                state: NodeState::new_enum("ElfVersion", ei_version.into(), ELF_VERSION.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("ei_version"));
        let ei_osabi = e_ident[7];
        result.insert(
            String::from("ei_osabi"),
            TreeNode {
                state: NodeState::new_enum("ElfOsAbi", ei_osabi.into(), ELF_OSABI.clone()),
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
        let read_u16 = |mut input: DynMut<dyn xlang_abi::io::Read + '_>| {
            let mut buffer = [0u8; 2];
            input.read(SpanMut::new(&mut buffer));
            if ei_data == 2 {
                (u16::from(buffer[0]) << 8) | u16::from(buffer[1])
            } else {
                u16::from(buffer[0]) | (u16::from(buffer[1]) << 8)
            }
        };
        let read_u32 = |mut input: DynMut<dyn xlang_abi::io::Read + '_>| {
            let mut buffer = [0u8; 4];
            input.read(SpanMut::new(&mut buffer));
            if ei_data == 2 {
                (u32::from(buffer[0]) << 24)
                    | (u32::from(buffer[1]) << 16)
                    | (u32::from(buffer[2]) << 8)
                    | u32::from(buffer[3])
            } else {
                u32::from(buffer[0])
                    | (u32::from(buffer[1]) << 8)
                    | (u32::from(buffer[2]) << 16)
                    | (u32::from(buffer[3]) << 24)
            }
        };
        let e_type = read_u16(input.reborrow_mut());
        result.insert(
            String::from("e_type"),
            TreeNode {
                state: NodeState::new_enum("ElfType", e_type.into(), ELF_TYPE.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("e_type"));
        let e_machine = read_u16(input.reborrow_mut());
        result.insert(
            String::from("e_machine"),
            TreeNode {
                state: NodeState::new_enum("ElfMachine", e_machine.into(), ELF_MACHINE.clone()),
                disasm_id: String::from("elf"),
                ..TreeNode::default()
            },
        );
        order.push(String::from("e_machine"));
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

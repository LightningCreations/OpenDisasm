use xlang_abi::prelude::v1::*;

use lazy_static::lazy_static;

use xlang_abi::io::{Read, Seek};
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
        m.insert(3, "ElfMachineIA32".into());
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
        mut file: DynMut<dyn xlang_abi::io::ReadSeek + 'a>,
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
        mut input: DynMut<dyn xlang_abi::io::ReadSeek + 'a>,
    ) -> xlang_abi::result::Result<TreeNode, od_core::abi_safe::Error> {
        let mut result = HashMap::new();
        let mut order = Vec::new();
        let mut e_ident = [0u8; 16];
        input.read(SpanMut::new(&mut e_ident));
        let ei_class = e_ident[4];
        result.insert(
            "ei_class".into(),
            TreeNode {
                state: NodeState::new_enum("ElfClass", ei_class.into(), ELF_CLASS.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("ei_class".into());
        let ei_data = e_ident[5];
        result.insert(
            "ei_data".into(),
            TreeNode {
                state: NodeState::new_enum("ElfData", ei_data.into(), ELF_DATA.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("ei_data".into());
        let ei_version = e_ident[6];
        result.insert(
            "ei_version".into(),
            TreeNode {
                state: NodeState::new_enum("ElfVersion", ei_version.into(), ELF_VERSION.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("ei_version".into());
        let ei_osabi = e_ident[7];
        result.insert(
            "ei_osabi".into(),
            TreeNode {
                state: NodeState::new_enum("ElfOsAbi", ei_osabi.into(), ELF_OSABI.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("ei_osabi".into());
        let ei_abiversion = e_ident[8];
        result.insert(
            "ei_abiversion".into(),
            TreeNode {
                state: ei_abiversion.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("ei_abiversion".into());
        let read_u16 = |mut input: DynMut<dyn xlang_abi::io::ReadSeek + '_>| {
            let mut buffer = [0u8; 2];
            input.read(SpanMut::new(&mut buffer));
            if ei_data == 2 {
                (u16::from(buffer[0]) << 8) | u16::from(buffer[1])
            } else {
                u16::from(buffer[0]) | (u16::from(buffer[1]) << 8)
            }
        };
        let read_u32 = |mut input: DynMut<dyn xlang_abi::io::ReadSeek + '_>| {
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
        let read_u64 = |mut input: DynMut<dyn xlang_abi::io::ReadSeek + '_>| {
            let mut buffer = [0u8; 8];
            input.read(SpanMut::new(&mut buffer));
            if ei_data == 2 {
                (u64::from(buffer[0]) << 56)
                    | (u64::from(buffer[1]) << 48)
                    | (u64::from(buffer[2]) << 40)
                    | (u64::from(buffer[3]) << 32)
                    | (u64::from(buffer[4]) << 24)
                    | (u64::from(buffer[5]) << 16)
                    | (u64::from(buffer[6]) << 8)
                    | u64::from(buffer[7])
            } else {
                u64::from(buffer[0])
                    | (u64::from(buffer[1]) << 8)
                    | (u64::from(buffer[2]) << 16)
                    | (u64::from(buffer[3]) << 24)
                    | (u64::from(buffer[4]) << 32)
                    | (u64::from(buffer[5]) << 40)
                    | (u64::from(buffer[6]) << 48)
                    | (u64::from(buffer[7]) << 56)
            }
        };
        let read_addr = |input: DynMut<dyn xlang_abi::io::ReadSeek + '_>| {
            if ei_class == 1 {
                read_u32(input) as u64
            } else {
                read_u64(input)
            }
        };
        let e_type = read_u16(input.reborrow_mut());
        result.insert(
            "e_type".into(),
            TreeNode {
                state: NodeState::new_enum("ElfType", e_type.into(), ELF_TYPE.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_type".into());
        let e_machine = read_u16(input.reborrow_mut());
        result.insert(
            "e_machine".into(),
            TreeNode {
                state: NodeState::new_enum("ElfMachine", e_machine.into(), ELF_MACHINE.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_machine".into());
        let e_version = read_u32(input.reborrow_mut());
        result.insert(
            "e_version".into(),
            TreeNode {
                state: NodeState::new_enum("ElfVersion", e_version.into(), ELF_VERSION.clone()),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_version".into());
        let e_entry = read_addr(input.reborrow_mut());
        result.insert(
            "e_entry".into(),
            TreeNode {
                state: if ei_class == 1 {
                    (e_entry as u32).into()
                } else {
                    e_entry.into()
                },
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_entry".into());
        let e_phoff = read_addr(input.reborrow_mut());
        result.insert(
            "e_phoff".into(),
            TreeNode {
                state: if ei_class == 1 {
                    (e_phoff as u32).into()
                } else {
                    e_phoff.into()
                },
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_phoff".into());
        let e_shoff = read_addr(input.reborrow_mut());
        result.insert(
            "e_shoff".into(),
            TreeNode {
                state: if ei_class == 1 {
                    (e_shoff as u32).into()
                } else {
                    e_shoff.into()
                },
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_shoff".into());
        let e_flags = read_u32(input.reborrow_mut());
        result.insert(
            "e_flags".into(),
            TreeNode {
                state: e_flags.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_flags".into());
        let e_ehsize = read_u16(input.reborrow_mut());
        result.insert(
            "e_ehsize".into(),
            TreeNode {
                state: e_ehsize.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_ehsize".into());
        let e_phentsize = read_u16(input.reborrow_mut());
        result.insert(
            "e_phentsize".into(),
            TreeNode {
                state: e_phentsize.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_phentsize".into());
        let e_phnum = read_u16(input.reborrow_mut());
        result.insert(
            "e_phnum".into(),
            TreeNode {
                state: e_phnum.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_phnum".into());
        let e_shentsize = read_u16(input.reborrow_mut());
        result.insert(
            "e_shentsize".into(),
            TreeNode {
                state: e_shentsize.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_shentsize".into());
        let e_shnum = read_u16(input.reborrow_mut());
        result.insert(
            "e_shnum".into(),
            TreeNode {
                state: e_shnum.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_shnum".into());
        let e_shstrndx = read_u16(input.reborrow_mut());
        result.insert(
            "e_shstrndx".into(),
            TreeNode {
                state: e_shstrndx.into(),
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push("e_shstrndx".into());
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

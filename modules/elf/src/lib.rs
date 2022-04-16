use xlang_abi::prelude::v1::*;

use xlang_abi::io::{Read, Seek, SeekFrom};
use xlang_abi::result::Result::Err;
use xlang_abi::result::Result::Ok;
use xlang_abi::span::SpanMut;
use xlang_host::rustcall;

use od_core::abi_safe::{Disassembler, NodeId, NodeState, TreeNode};

#[repr(u32)]
enum ProgramHeaderType {
    Null,
    Load,
    Dynamic,
    Interp,
    Note,
    Shlib,
    Phdr,
    Tls,
}

impl TryFrom<u32> for ProgramHeaderType {
    type Error = ();
    fn try_from(val: u32) -> std::result::Result<Self, ()> {
        match val {
            0 => std::result::Result::Ok(Self::Null),
            1 => std::result::Result::Ok(Self::Load),
            2 => std::result::Result::Ok(Self::Dynamic),
            3 => std::result::Result::Ok(Self::Interp),
            4 => std::result::Result::Ok(Self::Note),
            5 => std::result::Result::Ok(Self::Shlib),
            6 => std::result::Result::Ok(Self::Phdr),
            7 => std::result::Result::Ok(Self::Tls),
            _ => std::result::Result::Err(()),
        }
    }
}

struct ProgramHeader {
    ty: ProgramHeaderType,
    flags: u32,
    offset: u64,
    vaddr: u64,
    paddr: u64,
    filesz: u64,
    memsz: u64,
    align: u64,
}

struct SectionHeader {
    name: u32,
    ty: u32, // TODO: replace with enum
    flags: u64,
    addr: u64,
    offset: u64,
    size: u64,
    link: u32,
    info: u32,
    addralign: u64,
    entsize: u64,
}

struct ElfDisassembler;

impl ElfDisassembler {
    #[must_use]
    fn new() -> ElfDisassembler {
        ElfDisassembler
    }
}

impl ElfDisassembler {
    fn insert<S: AsRef<str> + ?Sized>(
        name: &S,
        state: NodeState,
        result: &mut HashMap<String, TreeNode>,
        order: &mut Vec<String>,
    ) {
        result.insert(
            name.into(),
            TreeNode {
                state,
                disasm_id: "elf".into(),
                ..TreeNode::default()
            },
        );
        order.push(name.into());
    }
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
        let ei_data = e_ident[5];
        let ei_version = e_ident[6];
        if ei_version != 1 {
            Self::insert(
                "WARNING (ei_version)",
                "`ei_version` was not ElfVersionCurrent. This could mean the file was invalid."
                    .into(),
                &mut result,
                &mut order,
            );
        }
        let ei_osabi = e_ident[7];
        let _ei_abiversion = e_ident[8];
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
        let e_machine = read_u16(input.reborrow_mut());
        let e_version = read_u32(input.reborrow_mut());
        let e_entry = read_addr(input.reborrow_mut());
        let e_phoff = read_addr(input.reborrow_mut());
        let e_shoff = read_addr(input.reborrow_mut());
        let e_flags = read_u32(input.reborrow_mut());
        let e_ehsize = read_u16(input.reborrow_mut());
        let e_phentsize = read_u16(input.reborrow_mut());
        let e_phnum = read_u16(input.reborrow_mut());
        let e_shentsize = read_u16(input.reborrow_mut());
        let e_shnum = read_u16(input.reborrow_mut());
        let e_shstrndx = read_u16(input.reborrow_mut());

        input.seek(SeekFrom::Start(e_phoff));
        let mut program_headers = Vec::new();
        for _ in 0..e_phnum {
            let ty = read_u32(input.reborrow_mut());
            let mut flags = 0;
            if ei_class != 1 {
                flags = read_u32(input.reborrow_mut());
            }
            let offset = read_addr(input.reborrow_mut());
            let vaddr = read_addr(input.reborrow_mut());
            let paddr = read_addr(input.reborrow_mut());
            let filesz = read_addr(input.reborrow_mut());
            let memsz = read_addr(input.reborrow_mut());
            if ei_class == 1 {
                flags = read_u32(input.reborrow_mut());
            }
            let align = read_addr(input.reborrow_mut());
            program_headers.push(ProgramHeader {
                ty: ty.try_into().unwrap(),
                flags,
                offset,
                vaddr,
                paddr,
                filesz,
                memsz,
                align,
            });
        }

        let mut section_headers = Vec::new();
        for _ in 0..e_shnum {
            let name = read_u32(input.reborrow_mut());
            let ty = read_u32(input.reborrow_mut());
            let flags = read_addr(input.reborrow_mut());
            let addr = read_addr(input.reborrow_mut());
            let offset = read_addr(input.reborrow_mut());
            let size = read_addr(input.reborrow_mut());
            let link = read_u32(input.reborrow_mut());
            let info = read_u32(input.reborrow_mut());
            let addralign = read_addr(input.reborrow_mut());
            let entsize = read_addr(input.reborrow_mut());
            section_headers.push(SectionHeader {
                name,
                ty,
                flags,
                addr,
                offset,
                size,
                link,
                info,
                addralign,
                entsize,
            });
        }

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

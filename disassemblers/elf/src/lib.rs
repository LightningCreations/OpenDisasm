use mappable_rc::Marc;
use opendisasm_core::{DisassemblerFormat, DisassemblerState, NodeRef, RecognitionHit};

pub struct ElfFormat;

pub fn format() -> impl DisassemblerFormat {
    ElfFormat
}

impl DisassemblerFormat for ElfFormat {
    fn format_name(&self) -> &'static str {
        "ELF"
    }

    #[rustfmt::skip] // I like where my comments are
    fn recognize(&self, data: &[u8]) -> Vec<RecognitionHit> {
        let mut result = Vec::new();

        // 0x34 = minimum valid ELF header size
        for i in 0..(data.len() - 0x34) {
            if &data[i..(i + 4)] == b"\x7fELF"            // Magic
                && (data[i + 4] == 1 || data[i + 4] == 2) // 32/64-bit format
                && (data[i + 5] == 1 || data[i + 5] == 2) // little/big endianness
                && data[i + 6] == 1                       // ELF version 1
            {
                result.push(RecognitionHit {
                    start: i,
                    len: None, // Can't reliably determine size from ELF header
                });
            }
        }

        result
    }

    fn supercedes(&self, _: &'static str) -> bool {
        false
    }

    fn go(&self, data: Marc<[u8]>) -> Box<dyn DisassemblerState> {
        Box::new(ElfDisassemblerState { data })
    }
}

#[allow(dead_code)]
#[derive(Debug)]
struct ElfDisassemblerState {
    data: Marc<[u8]>,
}

impl DisassemblerState for ElfDisassemblerState {
    fn cont(&mut self, _node: NodeRef) {
        todo!()
    }
}

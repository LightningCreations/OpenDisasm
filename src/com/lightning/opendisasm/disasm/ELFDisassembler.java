package com.lightning.opendisasm.disasm;

public class ELFDisassembler extends Disassembler {
    public DisassembledFile disassemble(byte[] file) {
        DisassembledFile result = new DisassembledFile();
        result.addHeaderField("EI_CLASS", file[4]);
        
        return result;
    }
}

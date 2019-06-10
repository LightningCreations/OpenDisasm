package com.lightning.opendisasm.disasm;

public abstract class Disassembler {
    public abstract DisassembledFile disassemble(byte[] file);
}

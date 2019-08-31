package com.lightning.opendisasm.disasm;

import com.lightning.opendisasm.tree.Node;

public abstract class Disassembler {
    public abstract DisassembledFile disassemble(byte[] file);
    public abstract Node disassembleTree(byte[] file);
}

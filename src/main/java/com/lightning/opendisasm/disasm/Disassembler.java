package com.lightning.opendisasm.disasm;

import java.io.InputStream;

import com.lightning.opendisasm.tree.Node;

public abstract class Disassembler {
    public abstract DisassembledFile disassemble(InputStream file);
    public abstract Node disassembleTree(InputStream file);
}

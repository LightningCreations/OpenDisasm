package com.lightning.opendisasm.disasm;

import java.io.InputStream;

import com.lightning.opendisasm.tree.Node;

public abstract class Disassembler {
	@Deprecated
    public DisassembledFile disassemble(InputStream file) {
		return null;
	}
    public abstract Node disassembleTree(InputStream file);
}

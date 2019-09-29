package com.lightning.opendisasm.disasm;

import java.io.InputStream;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.OctetStreamNode;
import com.lightning.opendisasm.tree.TransformerNode;

public abstract class Disassembler {
	@Deprecated
    public DisassembledFile disassemble(InputStream file) {
		return null;
	}
    public abstract Node disassembleTree(InputStream file);
    
    public void applyTo(TransformerNode node) {
    	if(node.getChild() instanceof OctetStreamNode)
    		node.replaceChild(disassembleTree(((OctetStreamNode)node.getChild()).newInputStream()));
    }
}

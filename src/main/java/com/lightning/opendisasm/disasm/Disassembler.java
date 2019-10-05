package com.lightning.opendisasm.disasm;

import java.io.InputStream;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.OctetStreamNode;
import com.lightning.opendisasm.tree.TransformerNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Disassembler {
    @Deprecated
    public @Nullable DisassembledFile disassemble(@NonNull InputStream file) {
        return null;
    }
    public @NonNull abstract Node disassembleTree(@NonNull InputStream file);
    
    public void applyTo(@NonNull TransformerNode node) {
    	if(node.getChild() instanceof OctetStreamNode)
    		node.replaceChild(disassembleTree(((OctetStreamNode)node.getChild()).newInputStream()));
    }
}

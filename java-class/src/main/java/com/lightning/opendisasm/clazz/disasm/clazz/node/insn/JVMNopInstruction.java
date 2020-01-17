package com.lightning.opendisasm.clazz.disasm.clazz.node.insn;

import com.lightning.opendisasm.clazz.disasm.clazz.node.attribute.CodeAttributeNode;
import com.lightning.opendisasm.tree.InstructionNode;
import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.util.Collections;
import java.util.List;

public class JVMNopInstruction implements InstructionNode {
    private final Node parent;
    public JVMNopInstruction(CodeAttributeNode node, BytewiseReader reader){
        this.parent = node;
    }
    @Override
    public Node getParent() {
        return parent;
    }

    @NonNull
    @Override
    public List<? extends Node> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "nop";
    }
}

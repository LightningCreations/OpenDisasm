package com.lightning.opendisasm.disasm.swf;

import java.util.ArrayList;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.BytewiseReader;

import javax.annotation.Nonnull;

public class SWFFileNode implements Node {
    public ArrayList<ValueNode> children;
    
    public SWFFileNode(BytewiseReader r) {
        
    }
    
    public Node getParent() {
        return null;
    }
    
    @Nonnull
    public List<? extends Node> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return null;
    }
}

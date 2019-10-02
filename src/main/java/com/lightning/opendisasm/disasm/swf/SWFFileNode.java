package com.lightning.opendisasm.disasm.swf;

import java.util.ArrayList;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.BytewiseReader;

public class SWFFileNode implements Node {
    public ArrayList<ValueNode> children;
    
    public SWFFileNode(BytewiseReader r) {
        
    }
    
    public Node getParent() {
        return null;
    }
    
    public List<? extends Node> getChildren() {
        return children;
    }
}

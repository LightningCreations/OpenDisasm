package com.lightning.opendisasm.disasm.swf;

import java.util.ArrayList;
import java.util.List;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.BytewiseReader;
import org.checkerframework.checker.nullness.qual.NonNull;


public class SWFFileNode implements Node {
    public ArrayList<ValueNode> children;
    
    public SWFFileNode(BytewiseReader r) {
        
    }
    
    public Node getParent() {
        return null;
    }

    @NonNull
    public List<? extends @NonNull Node> getChildren() {
        return children;
    }

    @Override
    public String getName() {
        return null;
    }
}

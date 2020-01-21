package com.lightning.opendisasm.visit;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.tree.ValueNode;
import com.lightning.opendisasm.util.StringUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public class StringifyVisitor implements NodeVisitor {
    private StringBuilder builder;
    private int tabs;
    public StringifyVisitor(){
        this(new StringBuilder(),0);
    }
    protected StringifyVisitor(StringBuilder builder,int tabs){
        this.builder = builder;
        this.tabs = tabs;
    }
    @Override
    public Optional<NodeVisitor> visitChild(Node child) {
        if(child instanceof ValueNode)
            return Optional.of(new StringifyVisitor(builder,1));
        builder.append(System.lineSeparator());
        return Optional.of(new StringifyVisitor(builder,tabs+1));
    }

    @Override
    public void visitTree(@NonNull Node root) {
        builder.append(StringUtil.getSpaces(tabs*4)).append(root.stringify());
        NodeVisitor.super.visitTree(root);
    }

    public String toString(){
        return builder.toString();
    }
}

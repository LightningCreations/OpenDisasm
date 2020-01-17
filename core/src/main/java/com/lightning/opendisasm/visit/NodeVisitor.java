package com.lightning.opendisasm.visit;

import com.lightning.opendisasm.tree.Node;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

public interface NodeVisitor {
    public Optional<NodeVisitor> visitChild(Node child);

    public default NodeVisitor andThen(@Nullable NodeVisitor other){
        if(other==null)
            return this;
        return child->visitChild(child).map(n->n.andThen(other.visitChild(child).orElse(null)));
    }


    public default void visitTree(@NonNull Node root){
        for(Node child:root.getChildren())
            visitChild(child).ifPresent(n->n.visitTree(child));
    }
}

package com.lightning.opendisasm.visit.clazz;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.visit.NodeVisitor;

import java.util.Optional;

public class ClassFileVisitor implements NodeVisitor {


    @Override
    public Optional<NodeVisitor> visitChild(Node child) {
        return Optional.empty();
    }
}

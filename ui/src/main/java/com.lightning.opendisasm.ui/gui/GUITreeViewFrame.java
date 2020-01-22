package com.lightning.opendisasm.ui.gui;

import javax.swing.*;

import com.lightning.opendisasm.tree.Node;
import com.lightning.opendisasm.visit.NodeVisitor;

import java.util.Optional;

public class GUITreeViewFrame extends JInternalFrame {
    private static final long serialVersionUID = 188837711915504218L;
    private JTree tree;

    public GUITreeViewFrame() {
        super("Tree View", true, true, true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        add(new JTree());
        setLocation(0, 0);
    }

    private static class TreeGenVisitor implements NodeVisitor {
        public Optional<NodeVisitor> visitChild(Node child) {
            return Optional.of(this);
        }
    }
    
    public void setTree(Node node) {
        new TreeGenVisitor().visitTree(node);
    }
}

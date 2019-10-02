package com.lightning.opendisasm.ui.gui;

import javax.swing.JInternalFrame;

import com.lightning.opendisasm.tree.Node;

public class GUITreeViewFrame extends JInternalFrame {
    private static final long serialVersionUID = 188837711915504218L;

    public GUITreeViewFrame() {
        super("Tree View", true, true, true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(100, 100);
        setLocation(0, 0);
    }
    
    public void setTree(Node tree) {
        System.out.println(tree);
    }
}

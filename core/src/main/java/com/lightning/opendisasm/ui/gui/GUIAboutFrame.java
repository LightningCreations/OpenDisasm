package com.lightning.opendisasm.ui.gui;

import javax.swing.JInternalFrame;

public class GUIAboutFrame extends JInternalFrame {
    private static final long serialVersionUID = -6846738865461898580L;
    
    public GUIAboutFrame() {
        super("About", true, true, true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(100, 100);
        setLocation(0, 0);
    }
}

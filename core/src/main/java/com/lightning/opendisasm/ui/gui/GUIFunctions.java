package com.lightning.opendisasm.ui.gui;

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;

import com.lightning.opendisasm.tree.Node;

public class GUIFunctions {
    public static GUIFrame mainFrame;
    public static GUIAboutFrame aboutFrame;
    public static GUITreeViewFrame treeViewFrame;
    public static JDesktopPane desktop;
    private static boolean framesAdded = false;
    
    static {
        aboutFrame = new GUIAboutFrame();
        treeViewFrame = new GUITreeViewFrame();
        desktop = new JDesktopPane();
    }
    
    public static void cleanup() {
        // Clean up
        
        System.exit(0);
    }
    
    public static void openAbout() {
        addCommonFrames();
        aboutFrame.setVisible(true);
        try {
            aboutFrame.setSelected(true);
            //IntelliJ wants to know why this is empty
        } catch (PropertyVetoException e) {}
    }
    
    public static void openTreeView(Node tree) {
        addCommonFrames();
        treeViewFrame.setVisible(true);
        treeViewFrame.setTree(tree);
        try {
            treeViewFrame.setSelected(true);
            //IntelliJ wants to know why this is empty
        } catch (PropertyVetoException e) {}
    }
    
    private static void addCommonFrames() {
        if(mainFrame != null && !framesAdded) {
            mainFrame.add(desktop);
            desktop.add(aboutFrame);
            framesAdded = true;
        }
    }
}

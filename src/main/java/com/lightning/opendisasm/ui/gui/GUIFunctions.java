package com.lightning.opendisasm.ui.gui;

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;

public class GUIFunctions {
    public static GUIFrame mainFrame;
    public static GUIAboutFrame aboutFrame;
    public static JDesktopPane desktop;
    private static boolean framesAdded = false;
    
    static {
        aboutFrame = new GUIAboutFrame();
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

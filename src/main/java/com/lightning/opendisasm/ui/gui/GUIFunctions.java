package com.lightning.opendisasm.ui.gui;

import java.beans.PropertyVetoException;

public class GUIFunctions {
    public static GUIFrame mainFrame;
    public static GUIAboutFrame aboutFrame;
    private static boolean framesAdded = false;
    
    static {
        aboutFrame = new GUIAboutFrame();
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
            mainFrame.add(aboutFrame);
            framesAdded = true;
        }
    }
}

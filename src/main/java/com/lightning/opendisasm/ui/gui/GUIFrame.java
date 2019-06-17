package com.lightning.opendisasm.ui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import static com.lightning.opendisasm.ui.gui.GUIFunctions.*;

public class GUIFrame extends JFrame {
    private static final long serialVersionUID = -854188747747807219L;
    
    public GUIFrame() {
        super("OpenDisasm v1.0.0");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        try {
            setIconImage(ImageIO.read(getClass().getResource("/logo.png")));
        } catch (IOException e) {
            // We couldn't load our amazing image? Oh well.
        }
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem fileExit = new JMenuItem("Quit");
        fileExit.setMnemonic(KeyEvent.VK_Q);
        fileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cleanup();
            }
        });
        fileMenu.add(fileExit);
        
        menuBar.add(fileMenu);
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem helpVersion = new JMenuItem("About...");
        helpVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAbout();
            }
        });
        helpMenu.add(helpVersion);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

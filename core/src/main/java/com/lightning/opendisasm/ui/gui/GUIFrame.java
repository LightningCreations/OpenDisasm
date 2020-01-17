package com.lightning.opendisasm.ui.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import com.lightning.opendisasm.detector.Detector;
import com.lightning.opendisasm.tree.Node;

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
        
        JMenuItem fileOpen = new JMenuItem("Open...");
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnCode = fc.showOpenDialog(GUIFrame.this);
                if(returnCode == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    FileInputStream inputFile = null;
                    try {
                        inputFile = new FileInputStream(file);
                    } catch (FileNotFoundException err) {
                        return;
                    }
                    
                    Node result = Detector.disassembleTreeFromStream(inputFile);
                    if(result==null) {
                        return;
                    }
                    openTreeView(result);
                } else {
                    // Cancelled
                }
            }
        });
        fileMenu.add(fileOpen);
        
        JMenuItem fileExit = new JMenuItem("Quit");
        fileExit.setMnemonic(KeyEvent.VK_Q);
        //Lambdafy this
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
        //Lambdafy this
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

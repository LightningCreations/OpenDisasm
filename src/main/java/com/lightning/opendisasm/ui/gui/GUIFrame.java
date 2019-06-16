package com.lightning.opendisasm.ui.gui;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class GUIFrame extends JFrame {
    private static final long serialVersionUID = -854188747747807219L;
    
    public GUIFrame() {
        super("OpenDisasm v1.0.0");
        try {
            setIconImage(ImageIO.read(getClass().getResource("/logo.png")));
        } catch (IOException e) {
            // We couldn't load our amazing image? Oh well.
        }
        setVisible(true);
    }
}

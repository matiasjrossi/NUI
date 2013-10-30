/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

    
/**
 *
 * @author matias
 */
public class DeviceSelectorDialog extends JDialog {

    private JComboBox list;
    private JLabel thumbnail, resolution;
    private int iconWidth, iconHeight;
    private JButton okButton;

    public DeviceSelectorDialog(JFrame parent, int detectedDevices, ItemListener listener) {
        /* super class constructor */
        super(parent, "Please select an input device", ModalityType.APPLICATION_MODAL);
        
        /* initialize combobox */
        list = new JComboBox();
        String[] names = new String[detectedDevices];
        for (int i=0; i<detectedDevices; ++i)
            list.addItem("Device " + i);
        list.addItemListener(listener);
        list.setAlignmentX(CENTER_ALIGNMENT);
        
        /* initialize thumbnail */
        try {
            Icon icon = new ImageIcon(ImageIO.read(DeviceSelectorDialog.class.getResource("resources/blank.png")));
            thumbnail = new JLabel(icon);
            iconWidth = icon.getIconWidth();
            iconHeight = icon.getIconHeight();
        } catch (IOException ex) {
            thumbnail = new JLabel();
            iconWidth = 256;
            iconHeight = 256;
            Logger.getLogger(DeviceSelectorDialog.class.getName()).log(Level.SEVERE, "Failed to load resource", ex);
        }
        thumbnail.setAlignmentX(CENTER_ALIGNMENT);
        
        /* initialize resolution */
        resolution = new JLabel(" ");
        resolution.setAlignmentX(CENTER_ALIGNMENT);
        
        /* add button */
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                DeviceSelectorDialog.this.setVisible(false);
                DeviceSelectorDialog.this.dispatchEvent(
                        new WindowEvent(DeviceSelectorDialog.this,
                        WindowEvent.WINDOW_CLOSING));
            }
        });
        okButton.setAlignmentX(CENTER_ALIGNMENT);
        
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(list);
        panel.add(thumbnail);
        panel.add(resolution);
        panel.add(okButton);
        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    public void setLastCapturedFrame(BufferedImage lastCapturedFrames) {
        thumbnail.setIcon(new ImageIcon(lastCapturedFrames.getScaledInstance(iconWidth, iconHeight, Image.SCALE_SMOOTH)));
        resolution.setText(lastCapturedFrames.getWidth() + "x" + lastCapturedFrames.getHeight());
    }
    
    public int getSelectedDevice() {
        return list.getSelectedIndex();
    }        
}
        
 
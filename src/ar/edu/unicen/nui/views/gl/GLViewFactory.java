/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import ar.edu.unicen.nui.Main;
import ar.edu.unicen.nui.controller.Controller;
import ar.edu.unicen.nui.common.EventListener;
import ar.edu.unicen.nui.model.ModelConstants;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


    
/**
 *
 * @author matias
 */
public class GLViewFactory {
        
    private GLViewFactory() {
        
    }
    
    public static void makeGLView(final Controller controller, int framesPerSecond) {
        /* Create window frame */
        final JFrame frame = new JFrame(Main.APPLICATION_NAME);
        frame.setIconImage(
                Toolkit.getDefaultToolkit().getImage(
                GLViewFactory.class.getResource("resources/app-icon.png")));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
        /* Create renderer, canvas, animator */
        final GLRenderer renderer = new GLRenderer(frame);

        GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
        canvas.addGLEventListener(renderer);
        
        final FPSAnimator animator = new FPSAnimator(canvas, framesPerSecond);
        animator.start();

        /* Setup frame menus and actions */
        JMenuBar menuBar = new JMenuBar();
        JMenu tracksMenu = new JMenu("Tracks");
        
        for (int i=0; i < ModelConstants.NUMBER_OF_TRACKS; ++i) {
            final int id = i;
            final JMenuItem trackItem = new JMenuItem(trackMenuLabel(id));
            trackItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
//                    fd.setDirectory("C:\\");
                    fd.setFile("*.mp3");
                    fd.setVisible(true);
                    String filename = fd.getFile();
                    if (filename != null) {
                        controller.getModel().getTracksContext().setTrackFilename(id, filename);
                        trackItem.setText(trackMenuLabel(id, filename));
                    }
                }
            });
            tracksMenu.add(trackItem);
        }
        
        menuBar.add(tracksMenu);
        JMenu viewMenu = new JMenu("View");
        JMenuItem resetWindowSize = new JMenuItem("Reset window size");
        resetWindowSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                renderer.setAutoResizeOnNextFrame(true);
            }
        });
        viewMenu.add(resetWindowSize);
        menuBar.add(viewMenu);
        
        frame.setJMenuBar(menuBar);
        

        /* Configure frame to use the renderer */
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                animator.stop();
                controller.terminate();
            }
        });

        /* Finally show the frame */
        frame.setResizable(true);
        frame.setSize(500, 500);
        frame.setVisible(true);

        /* Add event listeners to make the UI reflect what the controller captures */
        controller.addListener(Controller.Events.ON_FRAME_PROCESSED, new EventListener() {
            @Override
            public void handleEvent() {
                renderer.setLastCapturedFrame(controller.getLastCapturedFrame());
//                renderer.setDetectedMarkers(controller.getMarkers());
                renderer.setTiles(controller.getModel().getTilesContext().getTiles());
                renderer.setMode(GLRenderer.Mode.READY);
            }
        });
        
        controller.addListener(Controller.Events.ON_DEVICE_SELECTOR_FRAME_PROCESSED, new EventListener() {
            @Override
            public void handleEvent() {
                renderer.setLastAvailableFrames(controller.getLastAvailableFrames());
                renderer.setMode(GLRenderer.Mode.DEVICE_SELECTOR);
            }
        });
        
        controller.addListener(Controller.Events.ON_DEVICE_SELECTED, new EventListener() {
            @Override
            public void handleEvent() {
                renderer.setAutoResizeOnNextFrame(true);
            }
        });
        
        controller.addListener(Controller.Events.ON_NO_DEVICES_DETECTED, new EventListener() {
            @Override
            public void handleEvent() {
                renderer.setMode(GLRenderer.Mode.NO_DEVICES_AVAILABLE);
            }
        });
        
        

        
        
    }

    private static String trackMenuLabel(int id) {
        return trackMenuLabel(id, "none");
    }
    
    private static String trackMenuLabel(int id, String filename) {
        return 
                ModelConstants.TRACK_NAMES[id].substring(0, 1).toUpperCase() +
                ModelConstants.TRACK_NAMES[id].substring(1).toLowerCase() +
                " Track... [" + filename + "]";
    }
}

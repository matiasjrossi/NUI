/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import ar.edu.unicen.nui.Main;
import ar.edu.unicen.nui.controller.Controller;
import ar.edu.unicen.nui.common.EventListener;
import ar.edu.unicen.nui.model.Model;
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
        final GLRenderer renderer = new GLRenderer();

        GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
        canvas.addGLEventListener(renderer);
        
        final FPSAnimator animator = new FPSAnimator(canvas, framesPerSecond);
        animator.start();

        final JFrame frame = new JFrame(Main.APPLICATION_NAME);
        frame.setIconImage(
                Toolkit.getDefaultToolkit().getImage(
                GLViewFactory.class.getResource("resources/app-icon.png")));
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                animator.stop();
                controller.terminate();
            }
        });
        
        JMenuBar menuBar = new JMenuBar();
        JMenu tracksMenu = new JMenu("Tracks");
        
        for (int i=0; i < Model.NUMBER_OF_TRACKS; ++i) {
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
                        controller.getModel().setTrackFilename(id, filename);
                        trackItem.setText(trackMenuLabel(id, filename));
                    }
                }
            });
            tracksMenu.add(trackItem);
        }
        
        menuBar.add(tracksMenu);
        
        frame.setJMenuBar(menuBar);
        
        controller.addListener(Controller.Events.ON_FRAME_PROCESSED, new EventListener() {
            @Override
            public void handleEvent() {
                BufferedImage image = controller.getLastCapturedFrame();
                frame.setSize(image.getWidth(), image.getHeight());
                renderer.setBackgroundImage(image);
                renderer.setDetectedMarkers(controller.getTilesContext().getMarkers());
                renderer.setTiles(controller.getTilesContext().getTiles());
            }
        });

        
        
    }

    private static String trackMenuLabel(int id) {
        return trackMenuLabel(id, "none");
    }
    
    private static String trackMenuLabel(int id, String filename) {
        return 
                Model.TRACK_NAMES[id].substring(0, 1).toUpperCase() +
                Model.TRACK_NAMES[id].substring(1).toLowerCase() +
                " Track... [" + filename + "]";
    }
}

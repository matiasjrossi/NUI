/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import ar.edu.unicen.nui.controller.Controller;
import ar.edu.unicen.nui.controller.OnChangeListener;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;


    
/**
 *
 * @author matias
 */
public class GLViewFactory {
    
    private static final String TITLE = "Some title";
    
    private GLViewFactory() {
        
    }
    
    public static void makeGLView(final Controller controller, int framesPerSecond) {        
        final GLRenderer renderer = new GLRenderer();

        GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
        canvas.addGLEventListener(renderer);
        
        final FPSAnimator animator = new FPSAnimator(canvas, framesPerSecond);
        animator.start();

        final JFrame frame = new JFrame(TITLE);
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
        
        controller.getEventManager().addOnChangeEventListener(new OnChangeListener() {
            @Override
            public void onChanged() {
                BufferedImage image = controller.getLastCapturedFrame();
                frame.setSize(image.getWidth(), image.getHeight());
                renderer.setBackgroundImage(image);
                renderer.setDetectedMarkers(controller.getTilesContext().getMarkers());
                renderer.setTiles(controller.getTilesContext().getTiles());
            }
        });        
    }
}

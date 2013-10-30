/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import ar.edu.unicen.nui.Main;
import ar.edu.unicen.nui.common.EventListener;
import ar.edu.unicen.nui.controller.Controller;
import ar.edu.unicen.nui.model.ModelConstants;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


/**
 *
 * @author matias
 */
public class GLView extends JFrame {
    
    private Controller controller;
    private GLRenderer renderer;
    private FPSAnimator animator;
    private DeviceSelectorDialog dsd;
    private EventListener onFrameCapturedListener;
    private boolean autoResizeOnNextFrame;

    public GLView(Controller controller, int framesPerSecond) {
        super(Main.APPLICATION_NAME);
        
        this.controller = controller;
        
        autoResizeOnNextFrame = false;
        
        /* Configure window frame */
        setIconImage(
                Toolkit.getDefaultToolkit().getImage(
                GLView.class.getResource("resources/app-icon.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
        /* Create renderer, canvas, animator */
        renderer = new GLRenderer();

        GLCanvas canvas = new GLCanvas(new GLCapabilities(GLProfile.getDefault()));
        canvas.addGLEventListener(renderer);
        
        animator = new FPSAnimator(canvas, framesPerSecond);
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
                    FileDialog fd = new FileDialog(GLView.this, "Choose a file", FileDialog.LOAD);
//                    fd.setDirectory("C:\\");
                    fd.setFile("*.mp3");
                    fd.setVisible(true);
                    String filename = fd.getFile();
                    if (filename != null) {
                        GLView.this.controller.getModel().getTracksContext().setTrackFilename(id, filename);
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
                autoResizeOnNextFrame = true;
            }
        });
        viewMenu.add(resetWindowSize);
        menuBar.add(viewMenu);
        
        setJMenuBar(menuBar);
        

        /* Configure frame to use the renderer */
        getContentPane().add(canvas, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                animator.stop();
                GLView.this.controller.terminate();
            }
        });

        /* Position the frame */
        setResizable(true);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);

        controller.addListener(Controller.Events.ON_READY, new EventListener() {
            @Override
            public void handleEvent() {
                controllerReady();
            }
        });


        /* Add event listeners to make the UI reflect what the controller captures */
        controller.addListener(Controller.Events.ON_FRAME_PROCESSED, new EventListener() {
            @Override
            public void handleEvent() {
                if (autoResizeOnNextFrame) {
                    setSize(
                        GLView.this.controller.getLastCapturedFrame().getWidth() +
                        getInsets().left +
                        getInsets().right,
                        GLView.this.controller.getLastCapturedFrame().getHeight() +
                        getInsets().top +
                        getInsets().bottom);
                    setLocationRelativeTo(null);
                    autoResizeOnNextFrame = false;
                }
                renderer.setLastCapturedFrame(GLView.this.controller.getLastCapturedFrame());
//                renderer.setDetectedMarkers(controller.getMarkers());
                renderer.setTiles(GLView.this.controller.getModel().getTilesContext().getTiles());
                renderer.setMode(GLRenderer.Mode.READY);
            }
        });
        
    }
    
    private void controllerReady() {
        switch (controller.getDeviceCount()) {
            case 0:
                renderer.setMode(GLRenderer.Mode.NO_DEVICES_AVAILABLE);
                break;
            case 1:
                controller.resumeProcessing();
                autoResizeOnNextFrame = true;
                break;
            default:
                dsd = new DeviceSelectorDialog(this, controller.getDeviceCount(), new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        if (ie.getStateChange() == ItemEvent.SELECTED)
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    controller.switchDevice(dsd.getSelectedDevice());
                                }
                            });
                    }
                });
                onFrameCapturedListener = new EventListener() {
                    @Override
                    public void handleEvent() {
                        dsd.setLastCapturedFrame(controller.getLastCapturedFrame());
                    }
                };
                dsd.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        controller.removeListener(Controller.Events.ON_FRAME_CAPTURED, onFrameCapturedListener);
                        controller.resumeProcessing();
                    }
                });
                showDeviceSelectorDialog();
                JMenu settingsMenu = new JMenu("Settings");
                JMenuItem chooseCameraItem = new JMenuItem("Choose camera...");
                chooseCameraItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        showDeviceSelectorDialog();
                    }
                });
                settingsMenu.add(chooseCameraItem);
                getJMenuBar().add(settingsMenu);
                autoResizeOnNextFrame = true;
                break;
        }
    }
        
    private void showDeviceSelectorDialog() {
        controller.pauseProcessing();
        controller.addListener(Controller.Events.ON_FRAME_CAPTURED, onFrameCapturedListener);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dsd.setVisible(true);
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

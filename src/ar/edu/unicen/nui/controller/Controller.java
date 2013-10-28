/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import ar.edu.unicen.nui.common.EventSource;
import ar.edu.unicen.nui.model.Model;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.Marker;
import com.googlecode.javacv.MarkerDetector;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class Controller extends EventSource {

    private static int CAPTURES_PER_SECOND = 10;
    private Model model;
    private Looper controlLooper;
    private FrameGrabber grabber;
    private boolean isGrabberStarted;
    private ArrayList<FrameGrabber> availableGrabbers;
    private ArrayList<BufferedImage> availableCaptures;
    private MarkerDetector markerDetector;
    private BufferedImage lastCapturedFrame;
    private boolean terminated;
    
    public static enum Events implements EventSource.EventType {
        ON_FRAME_PROCESSED,
        ON_DEVICE_SELECTOR_FRAME_PROCESSED,
        ON_DEVICE_SELECTED,
        ON_NO_DEVICES_DETECTED,
        ON_START,
        ON_TERMINATE
    };

    /* Public interface */
    public void run() {
        controlLooper.run();
        fireEvent(Events.ON_START);
    }
    
    public void terminate() {
        this.terminated = true;
        fireEvent(Events.ON_TERMINATE);
    }

    public BufferedImage getLastCapturedFrame() {
        return lastCapturedFrame;
    }
    
    public ArrayList<BufferedImage> getLastAvailableFrames() {
        return availableCaptures;
    }

    public Model getModel() {
        return model;
    }
    
    public void selectDevice(int i) {
        grabber = availableGrabbers.get(i);
        availableGrabbers.clear();
        availableCaptures.clear();
        fireEvent(Events.ON_DEVICE_SELECTED);
    }


    /*****************/
    public Controller(Model model) {
        this.model = model;
        markerDetector = new MarkerDetector();
        terminated = false;
        isGrabberStarted = false;
        prepareControlLoop();
    }
        
    private void prepareControlLoop() {
        controlLooper = new Looper(CAPTURES_PER_SECOND, new Loop() {
            @Override
            public void init() {
                availableGrabbers = new ArrayList<FrameGrabber>();
                availableCaptures = new ArrayList<BufferedImage>();
                while (true) {
                    try {
                        Logger.getLogger(Controller.class.getName()).log(Level.INFO, "Attempting to capture from device " + availableGrabbers.size());
                        FrameGrabber aux = OpenCVFrameGrabber.createDefault(availableGrabbers.size());
                        aux.start();
                        IplImage frame = aux.grab();
                        availableCaptures.add(frame.getBufferedImage());
//                        frame.release();
                        aux.stop();
                        availableGrabbers.add(aux);
                    } catch (FrameGrabber.Exception ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.INFO, "Detected " + availableGrabbers.size() + " cameras.");
                        break;
                    }
                };
                switch (availableGrabbers.size()) {
                    case 0: // No cameras detected
                        fireEvent(Events.ON_NO_DEVICES_DETECTED);
                        break;
                    case 1: // Single camera
                        selectDevice(0);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void loop() {
                if (grabber != null)
                    processFrame();
                else if (availableGrabbers.size() != 0)
                    processDeviceSelectorFrame();
            }

            @Override
            public boolean isDone() {
                return terminated;
            }

            @Override
            public void end() {
                if (grabber != null) {
                    try {
                        grabber.stop();
                    } catch (FrameGrabber.Exception ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to stop grabber", ex);
                    }
                }
            }
        });
    }

    private void processDeviceSelectorFrame() {
        for (int i = 0; i < availableGrabbers.size(); ++i) {
            try {
                availableGrabbers.get(i).start();
                IplImage frame = availableGrabbers.get(i).grab();
                availableCaptures.set(i, frame.getBufferedImage());
                availableGrabbers.get(i).stop();
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to capture from grabber " + i, ex);
            }
        }
        fireEvent(Events.ON_DEVICE_SELECTOR_FRAME_PROCESSED);
    }
    
    private void processFrame() {
        try {
            if (!isGrabberStarted) {
                grabber.start();
                isGrabberStarted = true;
            }
            IplImage image = grabber.grab();
            Marker[] markers = markerDetector.detect(image, false);
    //        for (Marker marker: markers) {
    //            marker.draw(image, CvScalar.CYAN, 1.0f, null);
    //        }
    //        markerDetector.draw(image, markers);
            lastCapturedFrame = image.getBufferedImage();
            model.update(image.width(), image.height(), markers);
            fireEvent(Events.ON_FRAME_PROCESSED);
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to process frame", ex);
        }

    }


}

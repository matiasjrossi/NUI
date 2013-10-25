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
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.image.BufferedImage;
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
    private MarkerDetector markerDetector;
    private TilesContext tilesContext;
    private BufferedImage lastCapturedFrame;
    private boolean terminated;
    
    public static enum Events implements EventSource.EventType {
        ON_FRAME_PROCESSED,
        ON_START,
        ON_TERMINATE
    };

    public Controller(Model model) {
        this.model = model;
        markerDetector = new MarkerDetector();
        tilesContext = new TilesContext();
        terminated = false;
        try {
            this.grabber = FrameGrabber.createDefault(0);
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to get a FrameGrabber", ex);
        }
        prepareControlLoop();
    }

    public void run() {
        controlLooper.run();
        fireEvent(Events.ON_START);
    }
    
    public void terminate() {
        this.terminated = true;
        fireEvent(Events.ON_TERMINATE);
    }

    public Model getModel() {
        return model;
    }

    public BufferedImage getLastCapturedFrame() {
        return lastCapturedFrame;
    }

    private void prepareControlLoop() {
        controlLooper = new Looper(CAPTURES_PER_SECOND, new Loop() {
            @Override
            public void init() {
                try {
                    grabber.start();
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to start grabber", ex);
                }
            }

            @Override
            public void loop() {
                try {
                    processImage(grabber.grab());
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to process frame", ex);
                }
            }

            @Override
            public boolean isDone() {
                return terminated;
            }

            @Override
            public void end() {
                try {
                    grabber.stop();
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, "Failed to stop grabber", ex);
                }
            }
        });
    }

    private void processImage(IplImage image) {
        Marker[] markers = markerDetector.detect(image, false);
//        for (Marker marker: markers) {
//            marker.draw(image, CvScalar.CYAN, 1.0f, null);
//        }
//        markerDetector.draw(image, markers);
        lastCapturedFrame = image.getBufferedImage();
        tilesContext.update(markers);
        tilesContext.updateModel(model);
        fireEvent(Events.ON_FRAME_PROCESSED);
    }

    public TilesContext getTilesContext() {
        return tilesContext;
    }
}

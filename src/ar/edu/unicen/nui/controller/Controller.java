/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import ar.edu.unicen.nui.model.Model;
import com.googlecode.javacv.FrameGrabber;
//import com.googlecode.javacv.Marker;
//import com.googlecode.javacv.MarkerDetector;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class Controller {

    private static int CAPTURES_PER_SECOND = 10;
    private Model model;
    private EventManager eventManager;
    private Looper controlLooper;
    private FrameGrabber grabber;
    private MarkerDetector markerDetector;
    private TilesContext tilesContext;
    private BufferedImage lastCapturedFrame;
    private boolean terminated;

    public Controller(Model model) {
        this.model = model;
        eventManager = new EventManager();
        markerDetector = new MarkerDetector();
        tilesContext = new TilesContext();
        terminated = false;
        try {
            this.grabber = FrameGrabber.createDefault(0);
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        prepareControlLoop();
    }

    public void run() {
        controlLooper.run();
    }
    
    public void terminate() {
        this.terminated = true;
    }

    public EventManager getEventManager() {
        return eventManager;
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
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void loop() {
                try {
                    processImage(grabber.grab());
                } catch (FrameGrabber.Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void processImage(IplImage image) {
        Marker[] markers = markerDetector.detect(image, false);
//        for (Marker marker: markers) {
//            marker.draw(image, CvScalar.CYAN, 1.0f, null);
//        }
        markerDetector.draw(image, markers);
        lastCapturedFrame = image.getBufferedImage();
        tilesContext.update(markers);
        tilesContext.updateModel(model);
        eventManager.changed();
    }

    public TilesContext getTilesContext() {
        return tilesContext;
    }
}

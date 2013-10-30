/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import ar.edu.unicen.nui.common.EventSource;
import ar.edu.unicen.nui.model.Model;
import com.googlecode.javacv.MarkerDetector;
import java.awt.image.BufferedImage;

/**
 *
 * @author matias
 */
public class Controller extends EventSource {

    private static int CAPTURES_PER_SECOND = 10;
    private Model model;
    private Looper controlLooper;
    private MarkerDetector markerDetector;
    private Camera camera;
    private boolean terminated;
    private boolean paused;

    public static enum Events implements EventSource.EventType {
        ON_FRAME_CAPTURED,
        ON_FRAME_PROCESSED,
        ON_RUN,
        ON_READY,
        ON_PAUSED,
        ON_RESUMED,
        ON_TERMINATING,
        ON_TERMINATED
    };

    /* Public interface */
    public void run() {
        fireEvent(Events.ON_RUN);
        controlLooper.run();
    }
    
    public void terminate() {
        this.terminated = true;
        fireEvent(Events.ON_TERMINATING);
    }

    public Model getModel() {
        return model;
    }
    
    public synchronized void resumeProcessing() {
        paused = false;
        fireEvent(Events.ON_RESUMED);
    }
    
    public synchronized void pauseProcessing() {
        paused = true;
        fireEvent(Events.ON_PAUSED);
    }
    
    public synchronized void switchDevice(int device) {
        if (!paused)
            throw new IllegalStateException("Controller must be paused to switch devices");
        else
            camera.setActiveDevice(device);
    }
    
    public synchronized int getDeviceCount() {
        return camera.deviceCount();
    }
    
    public synchronized BufferedImage getLastCapturedFrame() {
        return camera.getLastCapturedFrame();
    }
    
    /*****************/
    public Controller(Model model) {
        this.model = model;
        markerDetector = new MarkerDetector();
        terminated = false;
        paused = true;
        prepareControlLoop();
    }
        
    private void prepareControlLoop() {
        controlLooper = new Looper(CAPTURES_PER_SECOND, new Loop() {
            @Override
            public void init() {
                camera = new Camera();
                fireEvent(Events.ON_READY);
            }

            @Override
            public void loop() {
                processFrame();
            }

            @Override
            public boolean isDone() {
                return terminated;
            }

            @Override
            public void end() {
                camera.release();
                fireEvent(Events.ON_TERMINATED);
            }
        });
    }

    private synchronized void processFrame() {
        camera.capture();
        fireEvent(Events.ON_FRAME_CAPTURED);
        if (!paused) {
            model.update(camera.getFrameWidth(), camera.getFrameHeight(), 
                    markerDetector.detect(camera.getIplImage(), false));
            fireEvent(Events.ON_FRAME_PROCESSED);
        }
    }
}

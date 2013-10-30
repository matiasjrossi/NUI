/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import com.googlecode.javacv.FrameGrabber;
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
public final class Camera {
    
    private ArrayList<FrameGrabber> devices;
    private int activeDevice;
    private IplImage lastCapturedFrame;
    
    /* Public interface */
    public int deviceCount() {
        return devices.size();
    }

    public int getActiveDevice() {
        return activeDevice;
    }

    public synchronized void setActiveDevice(int device) {
        try {
            devices.get(device);
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, "Ïnvalid device index: " + device);
            return;
        }
        
        try {
            if (activeDevice != -1) devices.get(activeDevice).stop();
            activeDevice = device;
            devices.get(activeDevice).start();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, "Failed to switch devices");
        }
    }

    public synchronized IplImage getIplImage() {
        return lastCapturedFrame;
    }
    
    public synchronized BufferedImage getLastCapturedFrame() {
        return lastCapturedFrame.getBufferedImage();
    }
    
    public synchronized void capture() {
        try {
            lastCapturedFrame = devices.get(activeDevice).grab();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, "Failed to grab a new frame");
        }
    }

    public synchronized void release() {
        try {
            devices.get(activeDevice).stop();
        } catch (FrameGrabber.Exception ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, "Failed to release camera");
        }
        devices.clear();
        activeDevice = -1;
    }
    
    public synchronized int getFrameWidth() {
        return lastCapturedFrame.width();
    }
    
    public synchronized int getFrameHeight() {
        return lastCapturedFrame.height();
    }

    
    /********/

    public Camera() {
        devices = new ArrayList<FrameGrabber>();
        activeDevice = -1;
        
        // Probe for cameras
        while (true) {
            try {
                Logger.getLogger(Camera.class.getName()).log(Level.INFO, "Attempting to capture from device " + devices.size());
                FrameGrabber aux = OpenCVFrameGrabber.createDefault(devices.size());
                aux.start();
                aux.grab();
                aux.stop();
                devices.add(aux);
            } catch (FrameGrabber.Exception ex) {
//                Logger.getLogger(Capturer.class.getName()).log(Level.SEVERE, ex.toString());
                break;
            }
        };
        
        Logger.getLogger(Camera.class.getName()).log(Level.INFO, "Detected " + deviceCount() + " cameras.");

        if (deviceCount() > 0)
            setActiveDevice(0);
        
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matias
 */
public class Looper {

    private int frame_duration_millis;
    private Loop loop;
        
    public Looper(int fps, Loop loopable) {
        this.loop = loopable;
        this.frame_duration_millis = 1000 / fps;
    }
    
    public void run() {
        
        loop.init();
        
        long next_frame_millis = System.currentTimeMillis();
        int sleep_time_millis;
        
        while (!loop.isDone()) {
            
            loop.loop();
            
            next_frame_millis += frame_duration_millis;
            sleep_time_millis = (int) (next_frame_millis - System.currentTimeMillis());
            
            if (sleep_time_millis <= 0) {
                Logger.getLogger(getClass().getName()).warning(String.format("Next loop is %sms late.", sleep_time_millis * -1));
            } else {
                try {
                    Thread.sleep(sleep_time_millis);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Looper.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        loop.end();
    }
    
    
}

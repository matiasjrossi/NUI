/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.beads;

import ar.edu.unicen.nui.common.EventListener;
import ar.edu.unicen.nui.controller.Controller;
import net.beadsproject.beads.core.AudioContext;

/**
 *
 * @author matias
 */
public class BeadsView {
    
    private Controller controller;
    private AudioContext audioContext;
    
    public BeadsView(Controller controller) {
        this.controller = controller;
        audioContext = new AudioContext();
        
        suscribeEventHandlers();
    }
    
    private void suscribeEventHandlers(){
        controller.addListener(Controller.Events.ON_RESUMED, new EventListener() {
            @Override
            public void handleEvent() {
                audioContext.start();
            }
        });
        
        controller.addListener(Controller.Events.ON_PAUSED, new EventListener() {
            @Override
            public void handleEvent() {
                audioContext.stop();
            }
        });
    }
}

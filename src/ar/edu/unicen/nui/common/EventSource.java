/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.common;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author matias
 */
public abstract class EventSource{
    
    protected interface EventType {}
        
    private HashMap<EventType, ArrayList<EventListener>> suscribers = new HashMap<EventType, ArrayList<EventListener>>();
    
    public void addListener(EventType event, EventListener listener) {
        if (!suscribers.containsKey(event))
            suscribers.put(event, new ArrayList<EventListener>());
        suscribers.get(event).add(listener);
    }
    
    public boolean removeListener(EventType event, EventListener listener) {
        return (suscribers.containsKey(event) && suscribers.get(event).remove(listener));
    }
    
    public void fireEvent(EventType event) {
        try {
            for (EventListener listener: suscribers.get(event))
                listener.handleEvent();
        } catch (Exception ex) {
        }
    }    


}

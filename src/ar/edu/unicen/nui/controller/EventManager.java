/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import java.util.ArrayList;

/**
 *
 * @author matias
 */
public class EventManager {
        
    private ArrayList<OnChangeListener> onChangeEventListeners;
    
    public EventManager() {
        onChangeEventListeners = new ArrayList<OnChangeListener>();
    }
    
    public void addOnChangeEventListener(OnChangeListener listener) {
        onChangeEventListeners.add(listener);
    }
    
    public void removeOnChangeEventListener(OnChangeListener listener) {
        onChangeEventListeners.remove(listener);
    }
    
    public void changed() {
        for (OnChangeListener listener: onChangeEventListeners) {
            listener.onChanged();
        }
    }    
}

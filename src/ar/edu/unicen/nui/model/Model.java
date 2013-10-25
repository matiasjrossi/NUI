/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

import ar.edu.unicen.nui.common.EventSource;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author matias
 */
public class Model extends EventSource {
    
    public enum Events implements EventType {
        
    };
    
    public static final int NUMBER_OF_TRACKS = 4;
    public static final String[] TRACK_NAMES = new String[]{"green", "blue", "red", "yellow"};
    
    private HashMap<Integer, ModelItem> items;
    private HashMap<Integer, String> tracksFilenames;
        
    public Model() {
        items = new HashMap<Integer, ModelItem>();
        tracksFilenames = new HashMap<Integer, String>();
    }
    
    public boolean hasItem(int i) {
        return items.containsKey(i);
    }
    
    public ModelItem getItem(int i) {
        return items.get(i);
    }
    
    public void putItem(int i, ModelItem item) {
        items.put(i, item);
    }
    
    public String getTrackFilename(int i) {
        if (i >= NUMBER_OF_TRACKS)
            Logger.getLogger(Model.class.getName()).log(Level.WARNING, "Index " + i + " is out of bounds");
        return tracksFilenames.get(i);
    }
    
    public void setTrackFilename(int i, String filename) {
        if (i >= NUMBER_OF_TRACKS) {
            Logger.getLogger(Model.class.getName()).log(Level.WARNING, "Index " + i + " is out of bounds");
            return;
        }
        tracksFilenames.put(i, filename);
    }
}

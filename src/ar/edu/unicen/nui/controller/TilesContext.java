/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import ar.edu.unicen.nui.model.Model;
import ar.edu.unicen.nui.model.ModelItem;
import com.googlecode.javacv.Marker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
//import com.googlecode.javacv.Marker;

/**
 *
 * @author matias
 */
public class TilesContext {
    
    private HashMap<Integer, Tile> tiles;
    private Marker[] markers;
    
    private class UnknownTrackerIDException extends Exception {
        
    }
    
    private ModelItem buildItemFromId(int id) throws UnknownTrackerIDException {
        if (id < 4) { // IDs 0, 1, 2, 3 are tracks
//            return new TrackItem();
        }
        throw new UnknownTrackerIDException();
    }
    

    public TilesContext() {
        tiles = new HashMap<Integer, Tile>();
    }
    
    public void update(Marker[] markers) {
        
        this.markers = markers;
        
        for (Marker marker: markers) {
            if (tiles.containsKey(marker.id)) {
                tiles.get(marker.id).syncToMarker(marker);
            } else {
                tiles.put(marker.id, Tile.buildFromMarker(marker));
            }
        }
        
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (Map.Entry<Integer, Tile> entry: tiles.entrySet()) {
            entry.getValue().decreaseRemainingLifetime();
            if (!entry.getValue().isAlive())
                toRemove.add(entry.getKey());
        }
        for (Integer i: toRemove) {
            tiles.remove(i);
        }
    }
    
    public void updateModel(Model model) {
        for (Tile t: tiles.values()) {
            try {
                if (!model.hasItem(t.getId()))
                    model.putItem(t.getId(), buildItemFromId(t.getId()));
                model.getItem(t.getId()).setPosition(t.getCenterX(), t.getCenterY());
                model.getItem(t.getId()).setRotation(t.getAngle());
            } catch (UnknownTrackerIDException e) {
                
            }
        }
    }

    public Collection<Tile> getTiles() {
        return tiles.values();
    }

    public Marker[] getMarkers() {
        return markers;
    }
    
}

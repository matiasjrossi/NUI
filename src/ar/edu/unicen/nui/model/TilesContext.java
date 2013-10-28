/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

import com.googlecode.javacv.Marker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author matias
 */
public class TilesContext {
    
    private Model model;
    private HashMap<Integer, Tile> tiles;
    
    public TilesContext(Model model) {
        this.model = model;
        tiles = new HashMap<Integer, Tile>();
    }
    
    public void update(Marker[] markers) {
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

    public Collection<Tile> getTiles() {
        return tiles.values();
    }
    
    public Tile getTile(int i) {
        return tiles.get(i);
    }
    
}

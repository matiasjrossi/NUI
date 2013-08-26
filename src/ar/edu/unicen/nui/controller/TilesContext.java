/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

import ar.edu.unicen.nui.model.Model;
//import com.googlecode.javacv.Marker;
import java.util.HashSet;

/**
 *
 * @author matias
 */
public class TilesContext {
    
    private HashSet<Tile> tiles;
    private Marker[] markers;
    

    public TilesContext() {
        tiles = new HashSet<Tile>();
    }
    
    public void update(Marker[] markers) {
        
        this.markers = markers;
        
        for (Marker marker: markers) {
            Tile tile = Tile.createTileFromMarker(marker);
            tiles.remove(tile);
            tiles.add(tile);
        }
        
        for (Tile tile: tiles) {
            tile.decreaseRemainingLifetime();
            if (!tile.isAlive())
                tiles.remove(tile);
        }
    }
    
    public void updateModel(Model model) {
        
    }

    public HashSet<Tile> getTiles() {
        return tiles;
    }

    public Marker[] getMarkers() {
        return markers;
    }
    
}

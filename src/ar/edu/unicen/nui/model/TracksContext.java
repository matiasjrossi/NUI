/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author matias
 */
public class TracksContext {

    private Model model;
    private HashMap<Integer, ArrayList<Effect>> trackEffects;
    private HashMap<Integer, String> tracksFilenames;
        
    public TracksContext(Model model) {
        this.model = model;
        trackEffects = new HashMap<Integer, ArrayList<Effect>>();
        tracksFilenames = new HashMap<Integer, String>();
    }
        
    public Collection<Effect> getEffects(int i) { // Could be null
        return trackEffects.get(i); 
    }    
    
    public boolean isTrackSet(int i) {
        return (trackEffects.containsKey(i));
    }
    
    public String getTrackFilename(int i) { // Could be null
        if (i >= ModelConstants.NUMBER_OF_TRACKS)
            Logger.getLogger(TracksContext.class.getName()).log(Level.WARNING, "Index " + i + " is out of bounds");
        return tracksFilenames.get(i);
    }

    /* Modifying operations */
    public void setTrackFilename(int i, String filename) {
        if (i >= ModelConstants.NUMBER_OF_TRACKS) {
            Logger.getLogger(TracksContext.class.getName()).log(Level.WARNING, "Index " + i + " is out of bounds");
            return;
        }
        tracksFilenames.put(i, filename);
    }
    
    void update(TilesContext tilesContext) {
        trackEffects.clear();
        ArrayList<Tile> sortedTiles = new ArrayList<Tile>(tilesContext.getTiles());
        Collections.sort(sortedTiles);
        for (Tile t: sortedTiles) {
            if (ModelConstants.TRACK_IDS.contains(t.getId())) {
                // It's a trap! -- *ehem* -- track!
                trackEffects.put(t.getId(), new ArrayList<Effect>());
            } else if(ModelConstants.EFFECT_GAIN_IDS.contains(t.getId())) {
                // Volume/gain effect
            } else if (ModelConstants.EFFECT_REVERB_IDS.contains(t.getId())) {
                
            }
            
        }
    }
        
    private float angleToPercent(float angle) {
        // From [-PI, PI] to [0.25, 1.25]
        angle = (float) ((Math.PI - angle) / (2*Math.PI) + 0.25);
        // From [0.25, 1.0) to [25.0, 100.0), and [1.0, 1.25) to [0.0, 25.0)
        return (float) (100.0 * (angle < 1.0 ? angle : angle - 1.0));
    }
    
}

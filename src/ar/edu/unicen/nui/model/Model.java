/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

import com.googlecode.javacv.Marker;
import java.util.ArrayList;

/**
 *
 * @author matias
 */
public class Model {
    
    private TracksContext tracksContext;
    private TilesContext tilesContext;
    private int width, height;

    public Model() {
        tracksContext = new TracksContext(this);
        tilesContext = new TilesContext(this);
    }

    public void update(int width, int height, Marker[] markers) {
        this.width = width;
        this.height = height;        
        tilesContext.update(markers);
        tracksContext.update(tilesContext);
    }

    public TracksContext getTracksContext() {
        return tracksContext;
    }

    public TilesContext getTilesContext() {
        return tilesContext;
    }
    
    public ArrayList<Integer> effectAssociatedTracks(Tile effectTile) {
        ArrayList<Integer> tracks = new ArrayList<Integer>();
        for (int track: ModelConstants.TRACK_IDS) {
            if (tracksContext.isTrackSet(track)) {
                Tile trackTile = tilesContext.getTile(track);
                if ((
                        ((trackTile.getCenterX() < width/2) && (effectTile.getCenterX() < trackTile.getCenterX())) ||
                        ((trackTile.getCenterX() > width/2) && (effectTile.getCenterX() > trackTile.getCenterX()))
                    ) && (
                        ((trackTile.getCenterY() < height/2) && (effectTile.getCenterY() < trackTile.getCenterY())) ||
                        ((trackTile.getCenterY() > height/2) && (effectTile.getCenterY() > trackTile.getCenterY()))         
                   )) {
                    tracks.add(track);
                }
            }
        }
        return tracks;
    }

}

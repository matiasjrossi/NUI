/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

//import com.googlecode.javacv.Marker;

/**
 *
 * @author matias
 */
public final class Tile {
    
    private static final int TILE_LIFETIME = 10;
    
    private float centerX, centerY;
    private float upX, upY;
    private int remainingLifetime;
    private int id;
    
    public static Tile createTileFromMarker(Marker marker) {
        Tile tile = new Tile(marker.id);
        tile.centerX = (float) (marker.getCenter()[0]);
        tile.centerY = (float) (marker.getCenter()[1]);
        tile.upX = (float) marker.corners[0] + ((float) marker.corners[2] - (float) marker.corners[0]) / 2.0f - tile.centerX;
        tile.upY = (float) marker.corners[1] + ((float) marker.corners[3] - (float) marker.corners[1]) / 2.0f - tile.centerX;
        return tile;
    }
    
    private Tile(int id) {
        this.id = id;
        this.remainingLifetime = TILE_LIFETIME;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getUpX() {
        return upX;
    }

    public float getUpY() {
        return upY;
    }

    public int getId() {
        return id;
    }
    
    public void decreaseRemainingLifetime() {
        this.remainingLifetime--;
    }
    
    public boolean isAlive() {
        return (this.remainingLifetime > 0);
    }
    
    @Override
    public int hashCode() {
        return id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Tile)) return false;
        return (((Tile)o).id == this.id);
    }
}

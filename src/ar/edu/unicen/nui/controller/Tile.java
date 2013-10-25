/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

//import com.googlecode.javacv.Marker;

import com.googlecode.javacv.Marker;
import java.util.Arrays;


/**
 *
 * @author matias
 */
public final class Tile {
    
    private static final int TILE_LIFETIME = 10;
    
    private float centerX, centerY;
    private float upX, upY;
    private float angle;

    private int remainingLifetime;
    private int id;    
        
    private Tile(int id) {
        this.id = id;
    }

    public static Tile buildFromMarker(Marker marker) {
        Tile tile = new Tile(marker.id);
        tile.syncToMarker(marker);
        return tile;
    }
    
    public void syncToMarker(Marker marker) {
        this.remainingLifetime = TILE_LIFETIME;
        this.centerX = (float) (marker.getCenter()[0]);
        this.centerY = (float) (marker.getCenter()[1]);
        this.upX = 
                Math.min((float) marker.corners[0], (float) marker.corners[2]) +
                Math.abs((float) marker.corners[0] - (float) marker.corners[2]) / 2.0f -
                this.centerX;
        this.upY = 
                Math.min((float) marker.corners[1], (float) marker.corners[3]) +
                Math.abs((float) marker.corners[1] - (float) marker.corners[3]) / 2.0f -
                this.centerY;
        double hypothenuse = Math.sqrt(Math.pow(this.upX, 2.0d) + Math.pow(this.upY, 2.0));
        this.angle = (float) Math.asin(upY / hypothenuse);
        if (upX < 0) {
            if (angle < 0) {
                angle = (float) (-Math.PI - angle);
            } else {
                angle = (float)  (Math.PI - angle);
            }
        }
    }

    public float getAngle() {
        return angle;
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
//    
//    @Override
//    public int hashCode() {
//        return id;
//    }
//    
//    @Override
//    public boolean equals(Object o) {
//        if (o == this) return true;
//        if (!(o instanceof Tile)) return false;
//        return (((Tile)o).id == this.id);
//    }

}

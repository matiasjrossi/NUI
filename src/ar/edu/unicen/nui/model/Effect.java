/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

/**
 *
 * @author matias
 */
public class Effect {    
    
    protected float x, y, percent;
    
    public Effect(float x, float y, float percent) {
        this.x = x;
        this.y = y;
        this.percent = percent;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getPercent() {
        return percent;
    }
    
}

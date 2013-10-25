/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

/**
 *
 * @author matias
 */
public abstract class ModelItem {    
    
    protected int id;
    protected float x, y, a;
    
    public ModelItem() {
    }
    
    public void setPosition(float x, float y) {
        newPosition(x, y);
        this.x = x;
        this.y = y;
    }
    
    public void setRotation(float a) {
        newRotation(a);
        this.a = a;
    }
    
    protected void newPosition(float x, float y){};
    protected void newRotation(float a){};
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.controller;

/**
 *
 * @author matias
 */
public interface Loop {

    public void init();

    public void loop();
    
    public boolean isDone();

    public void end();
    
}

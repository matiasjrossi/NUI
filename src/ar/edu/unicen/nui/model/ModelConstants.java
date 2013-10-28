/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author matias
 */
public final class ModelConstants {
    
    public static final int NUMBER_OF_TRACKS = 4;
    
    public static final String[] TRACK_NAMES = new String[]{"green", "blue", "red", "yellow"};
    
    public static final ArrayList<Integer> TRACK_IDS =
            new ArrayList<Integer>(Arrays.asList(
            new Integer[]{0, 1, 2, 3}
            ));

    public static final ArrayList<Integer> EFFECT_GAIN_IDS = 
            new ArrayList<Integer>(Arrays.asList(
            new Integer[]{4, 5, 6, 7, 8}
            ));
    
    public static final ArrayList<Integer> EFFECT_REVERB_IDS = 
            new ArrayList<Integer>(Arrays.asList(
            new Integer[]{9, 10, 11, 12, 13}
            ));

    private ModelConstants() {
    }
}

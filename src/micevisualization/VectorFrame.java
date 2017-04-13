/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import javafx.scene.paint.Color;

/**
 *
 * @author jmwan
 */
public class VectorFrame {
    
    GridSector first;
    GridSector second;
    Color color;
    private int animation_state;

    public VectorFrame(GridSector first, GridSector second, Color color, int animation_state){
        this.first = first;
        this.second = second;
        this.color = color;
        this.animation_state = animation_state;
    }
    
    public void increment(){
        this.animation_state ++;
    }
    
    public int get_animation_state(){
        return this.animation_state;
    }    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

/**
 *
 * @author parker
 */
public class GridSector {
    double x; // x coordinate
    double y; // y coordinate
    double w; // width
    double h; // height
    double center_x; // center region's x coordinate 
    double center_y; // center region's y coordinate
    int gridIndex; // index in the physical representation of the grid
    
    int finalTotalDuration; // the cumulative total event duration, taken from the total Start to Stop range of dataset rows
    int currentTotalDuration; // the current total event duration so far, used during the animated heat map for calculating change over time
    
    GridSector(double x_p, double y_p, double w_p, double h_p, int gi) {
        this.x = x_p;
        this.y = y_p;
        this.w = w_p;
        this.h = h_p;
        this.center_x = this.x + (this.w * .5);
        this.center_y = this.y + (this.h * .5);
        this.gridIndex = gi;
    }
}

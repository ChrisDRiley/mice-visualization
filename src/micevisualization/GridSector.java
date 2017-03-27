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
    int gridIndex; // index in the physical representation of the grid
    int totalDuration; // the cumulative total event duration, taken from the dataset rows
    
    GridSector(double x_p, double y_p, double w_p, double h_p, int gi) {
        this.x = x_p;
        this.y = y_p;
        this.w = w_p;
        this.h = h_p;
        this.gridIndex = gi;
    }
}

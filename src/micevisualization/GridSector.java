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
    double x;
    double y;
    double w;
    double h;
    int gridIndex;
    int totalDuration;
    
    GridSector(double x_p, double y_p, double w_p, double h_p, int gi) {
        this.x = x_p;
        this.y = y_p;
        this.w = w_p;
        this.h = h_p;
        this.gridIndex = gi;
    }
}

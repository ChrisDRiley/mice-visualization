/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author parker
 */
public class Grid {
    ArrayList<GridSector> sectors;
    
    Grid() {
        this.sectors = new ArrayList<GridSector>();
    }
    
    Boolean addSector(GridSector gs) {
        return this.sectors.add(gs);
    }
    
    void drawSectorsBackground(Canvas c) {
        for (int i = 0; i < this.sectors.size(); ++i) {
            GraphicsContext gc = c.getGraphicsContext2D();
            gc.setFill(Color.WHITE);
            gc.fillRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
    
    void drawSectorsGridlines(Canvas c) {
        for (int i = 0; i < this.sectors.size(); ++i) {
            GraphicsContext gc = c.getGraphicsContext2D();
            gc.setFill(Color.BLACK);
            gc.strokeRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
}

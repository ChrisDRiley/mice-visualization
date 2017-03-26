/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import java.util.ArrayList;
import java.util.Date;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author parker
 */
public class Grid {
    ArrayList<GridSector> sectors;
    Canvas background;
    Canvas gridlines;
    Canvas gridnumbers;
    
    Grid() {
        this.sectors = new ArrayList<GridSector>();
    }
    
    Boolean addSector(GridSector gs) {
        return this.sectors.add(gs);
    }
    
    void drawSectorsBackground(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.fillRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
    
    void drawSectorsGridlines(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.strokeRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
    
    void drawSectorsNumbers(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setStroke(Color.GRAY);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.strokeText(String.valueOf(this.sectors.get(i).gridIndex + 1), this.sectors.get(i).x + this.sectors.get(i).w*0.5, this.sectors.get(i).y + this.sectors.get(i).h*0.9);
        }
    }
    
    void makeGridLinesCanvas(StackPane viewerPane, double width, double height) {
            this.gridlines = new Canvas(width, height);
            this.gridlines.setId("gridlines");
            GraphicsContext gridlinesCanvasContext = this.gridlines.getGraphicsContext2D();
            this.drawSectorsGridlines(this.gridlines);
            viewerPane.getChildren().add(this.gridlines);   
    }
    
    void makeGridNumbersCanvas(StackPane viewerPane, double width, double height) {
            this.gridnumbers = new Canvas(width, height);
            this.gridnumbers.setId("gridnumbers");
            GraphicsContext gridNumbersCanvasContext = this.gridnumbers.getGraphicsContext2D();
            this.drawSectorsNumbers(this.gridnumbers);
            viewerPane.getChildren().add(this.gridnumbers);   
    }
    
    void toggleGridLines(StackPane viewerPane) {
        String id = "#gridlines";
        if ((Canvas)viewerPane.lookup(id) == null) {
            double width = calculateDimensions(viewerPane).w;
            double height = calculateDimensions(viewerPane).h;
            makeGridLinesCanvas(viewerPane, width, height);
        }
        else {
            viewerPane.getChildren().remove(viewerPane.lookup(id));
        }
    }
    
    void toggleGridNumbers(StackPane viewerPane) {
        String id = "#gridnumbers";
        
        if ((Canvas)viewerPane.lookup(id) == null) {
            System.out.println("toggling grid numbers on");
            double width = calculateDimensions(viewerPane).w;
            double height = calculateDimensions(viewerPane).h;
            makeGridNumbersCanvas(viewerPane, width, height);
        }
        else {
            System.out.println("toggling grid numbers off");
            viewerPane.getChildren().remove(viewerPane.lookup(id));
        }
    }
    
    Dimensions calculateDimensions(StackPane viewerPane) {
        // (Parker 3/25/17): calculate values to create a margin around the canvas:
        double width = viewerPane.getWidth() - 30;
        double height = viewerPane.getHeight() - 30;
        
        // (Parker 3/25/17): maintain a 7:4 aspect ratio in the canvas dimensions:
        if (width > height) {
            width = height;
        }
        else if (width < height) {
            height = width;
        }
        
        height = (height/7)*4;
        
        Dimensions d = new Dimensions(width, height);
        return d;
    }
    
    void redraw(StackPane viewerPane, Boolean showGridNumbers, Boolean showGridLines, Boolean isGenerated) {
         // (Parker 3/25/17): clear out the old content in the viewerPane object and reset the Grid data members:
        viewerPane.getChildren().clear();
        this.sectors.clear();
        this.background = null;
        this.gridlines = null;
        this.gridnumbers = null;
        
        double width = calculateDimensions(viewerPane).w;
        double height = calculateDimensions(viewerPane).h;
        
        int ROWS = 4;
        // 6 columns containing 4 sectors each, plus one column containing the feeding station sector
        int COLS = 7;
        
        double currentY = 0.0;
        double currentX = 0.0;
        
        // (Parker 3/25/17): create a 7x4 grid representing the experiment enclosure;
        // grid sectors in the first 6x4 spaces are regular sectors, while column 7x4
        // contains one grid sector, offset from the others, that represents the feeding station:
        for (int i = 0; i < ROWS; ++i) {
            currentY = (height / ROWS) * i;
            for (int j = 0; j < COLS-1; ++j) {
                currentX = (width / COLS) * j;
                GridSector gs = new GridSector(currentX, currentY, width/COLS, height/ROWS, j*4 + i);
                this.addSector(gs);
            }
        }
        GridSector gs = new GridSector((width / COLS) * 6, height/2 - ((height/ROWS)/2), width/COLS, height/ROWS, 24);  
        this.addSector(gs);
        
        // (Parker 3/25/17): setup the visualization with several layers of canvas objects:
        // layer 0: background
        // layer 1, 2, 3 ... : drawing
        // layer n-1: grid sector numbers overlay
        // layer n: gridlines overlay
        this.background = new Canvas(width, height);
        this.background.setId("background");
        GraphicsContext backgroundCanvasContext = this.background.getGraphicsContext2D();
        this.drawSectorsBackground(this.background);
        viewerPane.getChildren().add(this.background);
        
        if (isGenerated) {
            // render the computationally intensive portion of the visualization here
        }
        
        if (showGridNumbers) {
            makeGridNumbersCanvas(viewerPane, width, height);
        }   
        
        if (showGridLines) {
            makeGridLinesCanvas(viewerPane, width, height);
        }   
    }
    
    void staticHeatMap(ArrayList<Mouse> mice, Date start, Date stop) {
        Date currentDate = start;
//        while (currentDate.compareTo(stop) <= 0) {
//            for (int j = 0; j < mice.size(); ++j) {
//                
//            }
//        }
    }
}

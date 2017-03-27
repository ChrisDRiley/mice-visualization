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
 * 
 * contains the necessary Canvas objects and logic for rendering all types of supported
 * visualizations. One grid class is instantiated as a global variable in the controller.
 * 
 */
public class Grid {
    ArrayList<GridSector> sectors; // the grid sectors
    Canvas background; // the background layer (white)
    ArrayList<Canvas> datalayers; // the layers for representing computationally intensive data
    Canvas gridlines; // the grid lines layer (black lines)
    Canvas gridnumbers; // the grid sector numbers (gray numbers)
    
    // (Parker 3/26/17): allocate the arrays during grid object construction:
    Grid() {
        this.sectors = new ArrayList<GridSector>();
        this.datalayers = new ArrayList<Canvas>();
    }
    
    // (Parker 3/26/17): add a GridSector object to the sectors array
    Boolean addSector(GridSector gs) {
        return this.sectors.add(gs);
    }
    
    // (Parker 3/26/17): return the matching grid sector by its grid index. The grid is set up like this:
    // 1 5 9  13 17 21 
    // 2 6 10 14 18 22 25 (25 is displayed as vertically centered)
    // 3 7 11 15 19 23
    // 4 8 12 16 20 24
    GridSector getSectorByGridIndex(int index) {
        for (int i = 0; i < this.sectors.size(); ++i) {
            if (this.sectors.get(i).gridIndex == index) {
                return this.sectors.get(i);
            }
        }
        return null;
    }
    
    // (Parker 3/26/17): draw a white background for each grid sector:
    void drawSectorsBackground(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.fillRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
    
    // (Parker 3/26/17): draw black gridlines around each grid sector:
    void drawSectorsGridlines(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.strokeRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
    }
    
    // (Parker 3/26/17): draw sector numbers in each grid sector:
    void drawSectorsNumbers(Canvas c) {
        GraphicsContext gc = c.getGraphicsContext2D();
        gc.setStroke(Color.GRAY);
        for (int i = 0; i < this.sectors.size(); ++i) {
            gc.strokeText(String.valueOf(this.sectors.get(i).gridIndex), this.sectors.get(i).x + this.sectors.get(i).w*0.5, this.sectors.get(i).y + this.sectors.get(i).h*0.9);
        }
    }
    
    // (Parker 3/26/17): create a new this.gridlines Canvas object. use setId for the purpose of selecting
    // it (the object) in the list of viewerPane's children at some point in the future. Call this.drawSectorsGridlines on 
    // the Canvas object and add it to viewerPane's children.
    void makeGridLinesCanvas(StackPane viewerPane, double width, double height) {
            this.gridlines = new Canvas(width, height);
            this.gridlines.setId("gridlines");
            GraphicsContext gridlinesCanvasContext = this.gridlines.getGraphicsContext2D();
            this.drawSectorsGridlines(this.gridlines);
            viewerPane.getChildren().add(this.gridlines);   
    }
    
    // (Parker 3/26/17): create a new this.gridnumbers Canvas object. Use setId for the purpose of selecting
    // it (the object) in the list of viewerPane's children at some point in the future. Call this.drawSectorsNumbers on 
    // the Canvas object and add it to viewerPane's children.
    void makeGridNumbersCanvas(StackPane viewerPane, double width, double height) {
            this.gridnumbers = new Canvas(width, height);
            this.gridnumbers.setId("gridnumbers");
            GraphicsContext gridNumbersCanvasContext = this.gridnumbers.getGraphicsContext2D();
            this.drawSectorsNumbers(this.gridnumbers);
            viewerPane.getChildren().add(this.gridnumbers);   
    }
    
    // (Parker 3/26/17): search viewerPane's children for the #gridlines Canvas object. If the gridlines Canvas
    // object is not a child of viewerPane, create it by calculating current width and height and using
    // the makeGridLinesCanvas function. If it is a child, remove it from viewerPane by its selector id.
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

    // (Parker 3/26/17): search viewerPane's children for the #gridnumbers Canvas object. If the gridnumbers Canvas
    // object is not a child of viewerPane, create it by calculating current width and height and using
    // the makeGridNumbersCanvas function. If it is a child, remove it from viewerPane by its selector id.    
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
    
    // (Parker 3/26/17): calculate the width and height of the canvas layers, maintaining a 7:4 aspect ratio,
    // and return the width and height as a Dimensions object
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
    
    // (Parker 3/26/17): reset the rendered visualization. Redraw() draws all of the non-computationally intensive layers
    // of the visualization, including the background, gridlines, and gridnumbers Canvas objects.
    // The children of viewerPane, the visualization's parent object, are cleared, and so are the
    // sectors and datalayers. The newly created Canvas objects are then attached as children nodes
    // to viewerPane.
    /**
     * 
     * @param viewerPane the parent object that will contain the various Canvas layers
     * @param showGridNumbers the Boolean value taken from the showGridLinesCheckBox control
     * @param showGridLines the Boolean value taken from the showGridNumbersCheckBox control
     * @param isGenerated unused 
     */
    void redraw(StackPane viewerPane, Boolean showGridNumbers, Boolean showGridLines, Boolean isGenerated) {
         // (Parker 3/25/17): clear out the old content in the viewerPane object and reset the Grid data members:
        viewerPane.getChildren().clear();
        this.sectors.clear();
        this.background = null;
        this.datalayers.clear();
        this.gridlines = null;
        this.gridnumbers = null;
        
        // (Parker 3/26/17): calculate the dimensions of the Canvas layers, based on viewerPane
        double width = calculateDimensions(viewerPane).w;
        double height = calculateDimensions(viewerPane).h;
        
        // (Parker 3/26/17): the rows and columns of the physical grid:
        // 1 5 9  13 17 21 
        // 2 6 10 14 18 22 25 (25 is displayed as vertically centered)
        // 3 7 11 15 19 23
        // 4 8 12 16 20 24
        int ROWS = 4;
        // 6 columns containing 4 sectors each, plus one column containing the feeding station sector
        int COLS = 7;
        
        // (Parker 3/25/17): create a 7x4 grid representing the experiment enclosure;
        // grid sectors in the first 6x4 spaces are regular sectors, while column 7x4
        // contains one grid sector, offset from the others, which represents the feeding station.
        // Add the gridSectors to the grid's gridSectors array with addSector().
        double currentY = 0.0;
        double currentX = 0.0;
        for (int i = 0; i < ROWS; ++i) {
            currentY = (height / ROWS) * i;
            for (int j = 0; j < COLS-1; ++j) { // handle the first 24 grid sectors (#s 0 - 23)
                currentX = (width / COLS) * j;
                GridSector gs = new GridSector(currentX, currentY, width/COLS, height/ROWS, j*4 + i);
                this.addSector(gs);
            }
        }
        // handle the last grid sector, the feeding station (# 24):
        GridSector gs = new GridSector((width / COLS) * 6, height/2 - ((height/ROWS)/2), width/COLS, height/ROWS, 24);  
        this.addSector(gs);
        
        // (Parker 3/25/17): setup the visualization with several layers of canvas objects:
        // layer 0: background
        // layer 1, 2, 3 ... : drawing layers for computationally intensive work
        // layer n-1: grid sector numbers overlay
        // layer n: gridlines overlay
        this.background = new Canvas(width, height);
        this.background.setId("background");
        GraphicsContext backgroundCanvasContext = this.background.getGraphicsContext2D();
        this.drawSectorsBackground(this.background);
        viewerPane.getChildren().add(this.background);
        
        if (showGridNumbers) {
            makeGridNumbersCanvas(viewerPane, width, height);
        }   
        
        if (showGridLines) { 
            makeGridLinesCanvas(viewerPane, width, height);
        }   
    }
    
    /**
     * 
     * @author: Parker
     * 
     * create a heat map based on the selected mice and starting and stopping timestamp indices.
     * 
     * 
     * @param viewerPane the parent object that will contain the Canvas objects as its children
     * @param mice the mice to visualize
     * @param start a starting index from the range of data in the dataset
     * @param stop an ending index from the range of data in the dataset
     */
    void staticHeatMap(StackPane viewerPane, ArrayList<Mouse> mice, Date start, Date stop) {
        Date currentDate = start;
        // (Parker 3/26/17): store the cumulative event durations of the most active
        // grid sector, for the purposes of calibrating the heat map colors:
        double maxDuration = 0;
        // (Parker 3/26/17): loop through the selected mice and process their 
        // timestamp, event duration, and grid sector data. Ensure that only data within
        // the range between the start and stop Date parameters are processed. 
        for (int i = 0; i < mice.size(); ++i) {
            for (int j = 0; j < mice.get(i).locTimeData.size() && currentDate.compareTo(stop) <= 0; ++j) {
                Date mouseDate = mice.get(i).locTimeData.get(j).timestamp;
                if (mouseDate.compareTo(currentDate) >= 0) {
                    // perform string manipulation to get the integer gridIndex from the unitLabel locTimeData parameter:
                    int gridSectorIndex = Integer.parseInt(mice.get(i).locTimeData.get(j).unitLabel.substring(4));
                    System.out.println("Mouse: " + mice.get(i).IdRFID + ", gridSector: " + String.valueOf(gridSectorIndex));
                    // use the gridIndex to retrieve the matching gridSector object from the grid's gridSector array:
                    GridSector gs = getSectorByGridIndex(gridSectorIndex);
                    // add the current record's event duration to the selected GridSector's totalDuration parameter
                    gs.totalDuration += mice.get(i).locTimeData.get(j).eventDuration;
                    // calculate the largest gridSector totalDuration, for the purpose of calibrating the heatmap colors:
                    if (gs.totalDuration > maxDuration) {
                        maxDuration = gs.totalDuration;
                    }
                }
            }
        }
        
        // generate the heat map based on the data collected above:
        
        // calculate width and height of the Canvas object:
        double width = calculateDimensions(viewerPane).w;
        double height = calculateDimensions(viewerPane).h;
        Canvas data = new Canvas(width, height);
        
        GraphicsContext dataCanvasContext = data.getGraphicsContext2D();
        
        // loop through the grid sectors and draw its totalDuration as a shade of color: 
        for (int i = 0; i < this.sectors.size(); ++i) {
            double opacity = 0.0000000;
            // calculate the opacity of the gridSector's color:
            if (this.sectors.get(i).totalDuration != 0) {
                // the gridSector with the maxDuration will have the darkest shade, so 
                // gridSectors with less activity will have lighter shades:
                opacity = this.sectors.get(i).totalDuration / maxDuration;
                System.out.println(String.valueOf(this.sectors.get(i).totalDuration) + "/" + String.valueOf(maxDuration) + " = " + String.valueOf(this.sectors.get(i).totalDuration / maxDuration));
            }
            System.out.println("Opacity: " + String.valueOf(opacity));
            
            // perform the drawing of the shade onto the Canvas for this gridSector
            dataCanvasContext.setFill(Color.rgb(0, 0, 255, opacity));
            dataCanvasContext.fillRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
            // IMPORTANT: reset the totalDuration to 0, since we are done rendering this sector:
            sectors.get(i).totalDuration = 0;
        }
        // add the Canvas layer containing the heatmap to the grid object itself, and then to the viewerPane:
        this.datalayers.add(data);
        for (int j = 0; j < this.datalayers.size(); ++j) {
            viewerPane.getChildren().add(this.datalayers.get(j));
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

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
    Canvas data; //the data on the map
    Canvas viewerPaneGridNumbers; //checks if user has gridnumbers active
    Canvas viewerPaneGridLines; //checks if user has gridlines active
    
    Boolean animationCancelled; // for storing if the current animation (if applicable) has been cancelled or not
    
    // (Parker 3/26/17): allocate the arrays during grid object construction:
    Grid() {
        this.sectors = new ArrayList<GridSector>();
        this.datalayers = new ArrayList<Canvas>();
        this.animationCancelled = true;
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
            System.out.println("toggling grid lines on");
            double width = calculateDimensions(viewerPane).w;
            double height = calculateDimensions(viewerPane).h;
            makeGridLinesCanvas(viewerPane, width, height);
        }
        else {
            System.out.println("toggling grid lines off");
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
        
        /* (Parker 3/26/17): the rows and columns of the physical grid:
        1 5 9  13 17 21 
        2 6 10 14 18 22 25 (25 is displayed as vertically centered)
        3 7 11 15 19 23
        4 8 12 16 20 24 */
        int ROWS = 4;
        // 6 columns containing 4 sectors each, plus one column containing the feeding station sector
        int COLS = 7;
        
        /* (Parker 3/25/17): create a 7x4 grid representing the experiment enclosure;
        grid sectors in the first 6x4 spaces are regular sectors, while column 7x4
        contains one grid sector, offset from the others, which represents the feeding station.
        Add the gridSectors to the grid's gridSectors array with addSector(). */
        double currentY = 0.0;
        double currentX = 0.0;
        for (int i = 0; i < ROWS; ++i) {
            currentY = (height / ROWS) * i;
            for (int j = 0; j < COLS-1; ++j) { // handle the first 24 grid sectors (#s 0 - 23)
                currentX = (width / COLS) * j;
                GridSector gs = new GridSector(currentX, currentY, width/COLS, height/ROWS, (j*4 + i) + 1); // unitLabel (the gridSector id# from the data set) ranges from 1 - 25, hence (j*4 + i) + 1
                this.addSector(gs);
            }
        }
        // handle the last grid sector, the feeding station (# 24):
        GridSector gs = new GridSector((width / COLS) * 6, height/2 - ((height/ROWS)/2), width/COLS, height/ROWS, 25);  
        this.addSector(gs);
        
        /* (Parker 3/25/17): setup the visualization with several layers of canvas objects:
        layer 0: background
        layer 1, 2, 3 ... : drawing layers for computationally intensive work
        layer n-1: grid sector numbers overlay
        layer n: gridlines overlay */
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
     * @author Parker
     * 
     * the purpose of this function is to reset any data contained within GridSector objects
     * of the GridSector array of the Grid class. This is NOT for resetting map-independent parameters
     * such as x,y,w,h, and gridIndex, but instead for reseting map-specific parameters such as
     * finalTotalDuration 
     * 
     */
    void resetGridSectorsInfo() {
        for (int i = 0; i < this.sectors.size(); ++i) {
            this.sectors.get(i).finalTotalDuration = 0;
            this.sectors.get(i).currentTotalDuration = 0;
        }
    }
    
    /**
     * 
     * @author Parker
     * 
     * Calculate each individual GridSector's finalTotalDuration, write each finalTotalDuration to
     * the GridSector's finalTotalDuration parameter, and return the maxDuration.
     * 
     * @param viewerPane
     * @param mice
     * @param start
     * @param stop 
     * @return maxDuration
     */
    double calculateGridSectorHeatMapInfo(StackPane viewerPane, ArrayList<Mouse> mice, Date start, Date stop) {
        Date mouseDate = start;
        // (Parker 3/26/17): store the cumulative event durations of the most active
        // grid sector, for the purposes of calibrating the heat map colors:
        double maxDuration = 0;
        // (Parker 3/26/17): loop through the selected mice and process their 
        // timestamp, event duration, and grid sector data. Ensure that only data within
        // the range between the start and stop Date parameters are processed. 
        for (int i = 0; i < mice.size(); ++i) {
            for (int j = 0; j < mice.get(i).locTimeData.size() && mice.get(i).locTimeData.get(j).timestamp.compareTo(stop) <= 0; ++j) {
                mouseDate = mice.get(i).locTimeData.get(j).timestamp;
                if (mouseDate.compareTo(start) >= 0) {
                    /* perform string manipulation to get the integer gridIndex from the unitLabel locTimeData parameter: */
                    int gridSectorIndex = Integer.parseInt(mice.get(i).locTimeData.get(j).unitLabel.substring(4));
                    /* use the gridIndex to retrieve the matching gridSector object from the grid's gridSector array: */
                    GridSector gs = getSectorByGridIndex(gridSectorIndex);
                    /* add the current record's event duration to the selected GridSector's finalTotalDuration parameter */
                    gs.finalTotalDuration += mice.get(i).locTimeData.get(j).eventDuration;
                    /* calculate the largest gridSector finalTotalDuration, for the purpose of calibrating the heatmap colors: */
                    if (gs.finalTotalDuration > maxDuration) {
                        maxDuration = gs.finalTotalDuration;
                    }
                }
            }
        }
        return maxDuration;
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
        // (Parker 4/3/17): calculate the maximumDuration of mouse activity in the most active GridSector,
        // and calculate each GridSector's finalTotalDuration:
        double maxDuration = calculateGridSectorHeatMapInfo(viewerPane, mice, start, stop);
        
        // calculate width and height of the Canvas object:
        double width = calculateDimensions(viewerPane).w;
        double height = calculateDimensions(viewerPane).h;
        data = new Canvas(width, height);
        
        // get the graphics context of the data Canvas in order to perform drawing:
        GraphicsContext dataCanvasContext = data.getGraphicsContext2D();
        
        // loop through the grid sectors and draw its finalTotalDuration as a shade of color: 
        for (int i = 0; i < this.sectors.size(); ++i) {
            double opacity = 0.0000000;
            /* calculate the opacity of the gridSector's color: */
            if (this.sectors.get(i).finalTotalDuration != 0) {
                /* the gridSector with the maxDuration will have the darkest shade, so 
                gridSectors with less activity will have lighter shades: */
                opacity = this.sectors.get(i).finalTotalDuration / maxDuration;
            }
            
            /* perform the drawing of the shade onto the Canvas for this gridSector */
            dataCanvasContext.setFill(Color.rgb(0, 0, 255, opacity));
            dataCanvasContext.fillRect(sectors.get(i).x, sectors.get(i).y, sectors.get(i).w, sectors.get(i).h);
        }
        
        // check for and remove any pre-existing heat map layer:
        String heatMapId = "heatmap";
        Canvas viewerPaneHeatMapLayer = (Canvas)viewerPane.lookup("#" + heatMapId);
        if (viewerPaneHeatMapLayer != null) {
            viewerPane.getChildren().remove(viewerPane.lookup("#" + heatMapId));
        }
        
        // add the Canvas layer containing the heatmap to the grid object itself, and then to the viewerPane:
        data.setId(heatMapId);
        this.datalayers.add(data);
        viewerPane.getChildren().add(data);
        
        //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
        moveInfoLayersToFront(viewerPane);
    }
    
    /**
     * 
     * author Parker
     * 
     * Reset the state of relevant GUI items when an animation is cancelled or 
     * naturally stops. Currently only the playback controls are reset when this happens
     * (not the visualization itself).
     * 
     * @param button 
     */
    void stopAnimation(Button button) {
        button.setText("Play Animation");
        this.animationCancelled = true;
        Image buttonIcon = new Image("resources/play.png", 16, 16, true, true);
        button.setGraphic(new ImageView(buttonIcon));
    }
    
    /**
     * 
     * @author Parker
     * 
     * Generate an animated Heat map. Use the coordination of several GUI controls
     * in the Visualization options, the Grid class, and Mouse objects to achieve an
     * animation. The drawing of the animation must occur in a separate thread in order
     * to show constant progress updates in the GUI, so Service and Task objects are
     * used to achieve concurrency. The speed of the animation is controlled by
     * delay introduced between frames, ranging from 1 - 1000 milliseconds.
     * 
     * @param viewerPane
     * @param generateButton
     * @param currentAnimationFrame
     * @param leftStatus
     * @param mice
     * @param start
     * @param stop
     * @param speed
     * @throws InterruptedException 
     */
    void animatedHeatMap(StackPane viewerPane, Button generateButton, TextArea currentAnimationFrame, Label leftStatus, ArrayList<Mouse> mice, Date start, Date stop, double speed) throws InterruptedException {
        // (Parker 4/3/17): calculate the maximumDuration of mouse activity in the most active GridSector,
        // and calculate each GridSector's finalTotalDuration:
        double maxDuration = calculateGridSectorHeatMapInfo(viewerPane, mice, start, stop);
        System.out.println("MAX DURATION: " + String.valueOf(maxDuration));
        
        //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
        moveInfoLayersToFront(viewerPane);

        // Establish a new service:
        Service<Void> service = new Service<Void>() {
            long elapsedTs = 0; // Create a timestamp based timer to measure the duration of the animation
            
            @Override
            // create a new task that will comprise the new thread of the animation:
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        /* PUT LOOPING LOGIC HERE */
                        
                        long startTs = System.currentTimeMillis(); // begin a timer to record the amount of time the generation takes
                        
                        Date mouseDate = start;
                        
                        /* (Parker): in order to display the data rows in order, extract all the MouseLocTime data from 
                        the Mouse objects in the selected mice function parameter and store the collective MouseLocTime data in
                        a new ArrayList. */
                        ArrayList<MouseLocTime> locTimeData = new ArrayList<MouseLocTime>();
                        for (int i = 0; i < mice.size(); ++i) {
                            for (int j = 0; j < mice.get(i).locTimeData.size(); ++j) {
                                locTimeData.add(mice.get(i).locTimeData.get(j));
                            }
                        }
                        // (Parker): sort the collective MouseLocTime data so that it is in order from earliest Date timestamp
                        // to latest Date timestamp:
                        Collections.sort(locTimeData);
                        
                        // (Parker): begin looping through the sorted locTimeData (the data row entries). Ensure the loop
                        // stops at the stopping index:
                        for (int j = 0; j < locTimeData.size() && locTimeData.get(j).timestamp.compareTo(stop) <= 0; ++j) {
                            // if the animation was cancelled (by setting the Grid class' animationCancelled property),
                            // then cancel the animation thread:
                            if (animationCancelled == true) {
                                this.cancel(true);
                            }
                           if (isCancelled()) {
                                break;
                            }
                           // since we need to reference the 'j' incrementor inside the Platform.runlater code, 
                           // we need to make a new final variable that is a copy of j's current value:
                            final int finalJ = j;
                            // determine if the current data row (MouseLocTime object) falls within the range of the Start and Stop indicies:
                            mouseDate = locTimeData.get(j).timestamp;
                            // if the current data row is within the Start and Stop indicies, perform the
                            // color shade calculations and update the GUI inside the Platform.runLater() function:
                            if (mouseDate.compareTo(start) >= 0) {
                                
                                // Create a Platform to run code in the background on the new thread;
                                // this is where the data layer of the Grid's Canvas objects gets updated:
                                Platform.runLater(new Runnable() {                          
                                    @Override
                                    public void run() {
                                        /* PUT GUI UPDATE LOGIC HERE */

                                        /* (Parker 4/3/17): display to the user the current frame being rendered: */
                                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                                        currentAnimationFrame.setText(formatter.format(locTimeData.get(finalJ).timestamp));

                                        /* perform string manipulation to get the integer gridIndex from the unitLabel locTimeData parameter: */
                                        int gridSectorIndex = Integer.parseInt(locTimeData.get(finalJ).unitLabel.substring(4));
                                        /* use the gridIndex to retrieve the matching gridSector object from the grid's gridSector array: */
                                        GridSector gs = getSectorByGridIndex(gridSectorIndex);

                                        /* add the current record's event duration to the selected GridSector's currentTotalDuration parameter */
                                        gs.currentTotalDuration += locTimeData.get(finalJ).eventDuration;

                                        /* get the maximum opacity for this grid sector (AKA this GridSector's value in the static heat map) */
                                        double baseOpacity = gs.finalTotalDuration / maxDuration;

                                        /* now create a shade of the baseOpactiy by applying the ratio of the finalTotalDuration vs currentTotalDuration */
                                        double currentOpacity = ((double)gs.currentTotalDuration / (double)gs.finalTotalDuration) * baseOpacity;
                                        
                                        /* attempt to reference the heatmap data layer of the viewerPane's children;
                                        if this is not possible, create a new heatmap data layer and add it to the viewerPane: */
                                        String heatMapId = "heatmap";
                                        Canvas viewerPaneHeatMapLayer = (Canvas)viewerPane.lookup("#" + heatMapId);
                                        if (viewerPaneHeatMapLayer == null) {
                                            // calculate width and height of the Canvas object:
                                            double width = calculateDimensions(viewerPane).w;
                                            double height = calculateDimensions(viewerPane).h;
                                            data = new Canvas(width, height);
                                            data.setId(heatMapId);
                                            //this.datalayers.add(data);
                                            viewerPane.getChildren().add(data);
                                            //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
                                            moveInfoLayersToFront(viewerPane);
                                        }
                                        
                                        viewerPaneHeatMapLayer = (Canvas)viewerPane.lookup("#" + heatMapId);

                                        // get the graphics context of the heat map data layer for the purpose of drawing:
                                        GraphicsContext dataCanvasContext = viewerPaneHeatMapLayer.getGraphicsContext2D();

                                        /* perform the drawing of the shade onto the Canvas for this gridSector: */
                                        // clear out the current GridSector's old shade:
                                        dataCanvasContext.clearRect(gs.x, gs.y, gs.w, gs.h);
                                        // fill the current GridSector with the calculated shade:
                                        dataCanvasContext.setFill(Color.rgb(0, 0, 255, currentOpacity));
                                        dataCanvasContext.fillRect(gs.x, gs.y, gs.w, gs.h);
                                    }
                                });
                                // delay the animation thread by "speed" number of milliseconds;
                                // this value should come directly from the Frame Delay GUI slider control:
                                Thread.sleep((int)speed);
                            }
                        }
                        long endTs = System.currentTimeMillis(); // stop the timer
                        elapsedTs = endTs - startTs; // get the elapsed time of the generation duration
                        return null; // we aren't returning a specific value from the task, so return null
                    }
                    // if the animation was cancelled by the user, respond gracefully:
                    @Override protected void cancelled() {
                        super.cancelled();
                        updateMessage("Cancelled!");
                    }
                    // if the animation succeeded, respond gracefully:
                    @Override protected void succeeded() {
                        stopAnimation(generateButton);
                        // output a text status update to the user containing the duration of the animation:
                        String describeMice = (mice.size() > 1) ? "mice" : "mouse";
                        leftStatus.setText("Finished generating an animated heat map of " + mice.size() + " " + describeMice + " in " + elapsedTs + " milliseconds.");
                    }
                };
            }
        };
        // start the animation thread:
        service.start();
    }
    
    /**
     * @author Joshua
     * 
     * Creates a vector map based on the selected mice and the indicated starting and stopping times
     * 
     * @param viewerPane the parent object that will contain the Canvas objects as its children
     * @param mice the mice to visualize
     * @param start a starting index from the range of data in the dataset
     * @param stop an ending index from the range of data in the dataset 
     */
    
    void staticVectorMap(StackPane viewerPane, ArrayList<Mouse> mice, Date start, Date stop) {
        Date mouseDate = start;
        HashMap<Mouse, ArrayList<Integer>> Mouse_positions = new HashMap<Mouse, ArrayList<Integer>>(); 
        /*Creates a hashmap that stores mouse labels with that paticular mouse's grid positions stored as the value */
        
        for (int i = 0; i < mice.size(); ++i) {
            ArrayList<Integer> UnitLabels = new ArrayList<Integer>();
            //creates an arraylist to store all the mouse postions for a particulars mouse 
            
            for (int j = 0; j < mice.get(i).locTimeData.size() && mice.get(i).locTimeData.get(j).timestamp.compareTo(stop) <= 0; ++j) {
                //System.out.println("currentDate.compareTo(stop) <= 0 ?: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(mouseDate) + " compareTo " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(stop) + " = " + String.valueOf(mouseDate.compareTo(stop)));
                mouseDate = mice.get(i).locTimeData.get(j).timestamp;
                if (mouseDate.compareTo(start) >= 0) {
                    /* perform string manipulation to get the integer gridIndex from the unitLabel locTimeData parameter: */
                    int gridSectorIndex = Integer.parseInt(mice.get(i).locTimeData.get(j).unitLabel.substring(4));
                    UnitLabels.add(gridSectorIndex);
                    }
                }
            Mouse_positions.put(mice.get(i), UnitLabels);
        }
        
        double width = calculateDimensions(viewerPane).w;
        double height = calculateDimensions(viewerPane).h;
        this.data = new Canvas(width, height);
            
    
        GraphicsContext dataCanvasContext = data.getGraphicsContext2D();
        
        
        for(Map.Entry<Mouse, ArrayList<Integer>> entry: Mouse_positions.entrySet()){
            dataCanvasContext.setLineWidth(5);
            dataCanvasContext.setStroke(entry.getKey().mouse_color);
            
            for (int i =1; i< entry.getValue().size(); i++){
                
                
                GridSector GS1 = getSectorByGridIndex(entry.getValue().get(i-1));
                GridSector GS2 = getSectorByGridIndex(entry.getValue().get(i));
                dataCanvasContext.strokeLine(
                    GS1.center_x ,
                    GS1.center_y,
                    GS2.center_x,
                    GS2.center_y);
                //Draws the line segments connecting all the sectors that the a mouse traveled
            }
        }
        
        
        // check for and remove any pre-existing heat map layer:
        String vectorMapId = "vectormap";
        Canvas viewerPaneHeatMapLayer = (Canvas)viewerPane.lookup("#" + vectorMapId);
        if (viewerPaneHeatMapLayer != null) {
            viewerPane.getChildren().remove(viewerPane.lookup("#" + vectorMapId));
        }
        
        // add the Canvas layer containing the heatmap to the grid object itself, and then to the viewerPane:
        data.setId(vectorMapId);
        this.datalayers.add(data);
        viewerPane.getChildren().add(data);
        
        //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
        moveInfoLayersToFront(viewerPane);
    }
    
    /**
     * 
     * @author Joshua
     * 
     * Generate an animated Heat map. Use the coordination of several GUI controls
     * in the Visualization options, the Grid class, and Mouse objects to achieve an
     * animation. The drawing of the animation must occur in a separate thread in order
     * to show constant progress updates in the GUI, so Service and Task objects are
     * used to achieve concurrency. The speed of the animation is controlled by
     * delay introduced between frames, ranging from 1 - 1000 milliseconds.
     * 
     * @param viewerPane
     * @param generateButton
     * @param currentAnimationFrame
     * @param leftStatus
     * @param mice
     * @param start
     * @param stop
     * @param speed
     * @throws InterruptedException 
     */
    void animatedVectorMap(StackPane viewerPane, Button generateButton, TextArea currentAnimationFrame, Label leftStatus, ArrayList<Mouse> mice, Date start, Date stop, double speed) throws InterruptedException {


        //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
        moveInfoLayersToFront(viewerPane);

        // Establish a new service:
        Service<Void> service;
        service = new Service<Void>() {
            long elapsedTs = 0; // Create a timestamp based timer to measure the duration of the animation
            
            @Override
            // create a new task that will comprise the new thread of the animation:
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        /* PUT LOOPING LOGIC HERE */
                        
                        long startTs = System.currentTimeMillis(); // begin a timer to record the amount of time the generation takes
                        
                        Date mouseDate = start;
                        
                        /* (Parker): in order to display the data rows in order, extract all the MouseLocTime data from
                        the Mouse objects in the selected mice function parameter and store the collective MouseLocTime data in
                        a new ArrayList. */
                        
                        HashMap<Date, Mouse> mouse_color = new HashMap<Date, Mouse>();
                        ArrayList<MouseLocTime> locTimeData = new ArrayList<MouseLocTime>();
                        for (int i = 0; i < mice.size(); ++i) {
                            for (int j = 0; j < mice.get(i).locTimeData.size(); ++j) {
                                locTimeData.add(mice.get(i).locTimeData.get(j));
                                mouse_color.put(mice.get(i).locTimeData.get(j).timestamp,mice.get(i));
                            }
                        }
                        // (Parker): sort the collective MouseLocTime data so that it is in order from earliest Date timestamp
                        // to latest Date timestamp:
                        Collections.sort(locTimeData);
                        
                        //A list stores lines and the animatation frame state of each line
                        ArrayList<VectorFrame> line_frames = new ArrayList<VectorFrame>();
                        
                        // (Parker): begin looping through the sorted locTimeData (the data row entries). Ensure the loop
                        // stops at the stopping index:
                        for (int j = 0; j+1 < locTimeData.size() && locTimeData.get(j).timestamp.compareTo(stop) <= 0; ++j) {
                            // if the animation was cancelled (by setting the Grid class' animationCancelled property),
                            // then cancel the animation thread:
                            if (animationCancelled == true) {
                                this.cancel(true);
                            }
                            if (isCancelled()) {
                                break;
                            }
                            // since we need to reference the 'j' incrementor inside the Platform.runlater code,
                            // we need to make a new final variable that is a copy of j's current value:
                            final int finalJ = j;
                            // determine if the current data row (MouseLocTime object) falls within the range of the Start and Stop indicies:
                            mouseDate = locTimeData.get(j).timestamp;
                            // if the current data row is within the Start and Stop indicies, perform the
                            // color shade calculations and update the GUI inside the Platform.runLater() function:
                            
                            for (int position = 0; position < line_frames.size(); position ++){
                                line_frames.get(position).increment();

                            }
                            
                            
                            if (mouseDate.compareTo(start) >= 0) {
                                
                                // Create a Platform to run code in the background on the new thread;
                                // this is where the data layer of the Grid's Canvas objects gets updated:
                                Platform.runLater(new Runnable() {                          
                                    @Override
                                    public void run() {
                                        /* PUT GUI UPDATE LOGIC HERE */
                                        
                                        //Draws the line segments connecting all the sectors that the a mouse traveled

                                        /* (Parker 4/3/17): display to the user the current frame being rendered: */
                                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                                        currentAnimationFrame.setText(formatter.format(locTimeData.get(finalJ).timestamp));

                                        /* perform string manipulation to get the integer gridIndex from the unitLabel locTimeData parameter: */
                                        int gridSectorIndex_previous = Integer.parseInt(locTimeData.get(finalJ).unitLabel.substring(4));
                                        int gridSectorIndex_current = Integer.parseInt(locTimeData.get(finalJ+1).unitLabel.substring(4));
                                        /* use the gridIndex to retrieve the matching gridSector object from the grid's gridSector array: */
                                        
                                        GridSector previous_grid = getSectorByGridIndex(gridSectorIndex_previous);
                                        GridSector current_grid = getSectorByGridIndex(gridSectorIndex_current);
                                        
                                        /* attempt to reference the heatmap data layer of the viewerPane's children;
                                        if this is not possible, create a new heatmap data layer and add it to the viewerPane: */
                                        String vectorMapId = "vectormap";
                                        Canvas viewerPaneVectorMapLayer = (Canvas)viewerPane.lookup("#" + vectorMapId);
                                        if (viewerPaneVectorMapLayer == null) {
                                            // calculate width and height of the Canvas object:
                                            double width = calculateDimensions(viewerPane).w;
                                            double height = calculateDimensions(viewerPane).h;
                                            data = new Canvas(width, height);
                                            data.setId(vectorMapId);
                                            //this.datalayers.add(data);
                                            viewerPane.getChildren().add(data);
                                            //Checks grid line/numbers if they are active or not; if they are active, move them to the front of the Canvas layers
                                            moveInfoLayersToFront(viewerPane);
                                        }
                                        
                                        viewerPaneVectorMapLayer = (Canvas)viewerPane.lookup("#" + vectorMapId);

                                        // get the graphics context of the heat map data layer for the purpose of drawing:
                                        GraphicsContext dataCanvasContext = viewerPaneVectorMapLayer.getGraphicsContext2D();
                                        
                                        // Store the current transformation matrix
                                        dataCanvasContext.save();

                                        // Use the identity matrix while clearing the canvas
                                        dataCanvasContext.setTransform(1, 0, 0, 1, 0, 0);
                                        dataCanvasContext.clearRect(0, 0, viewerPaneVectorMapLayer.getWidth(), viewerPaneVectorMapLayer.getHeight());

                                        // Restore the transform
                                        dataCanvasContext.restore();
                                        
                                        dataCanvasContext.setLineWidth(5);
                                                                                
                                        int frame_number = 1;
                                        VectorFrame vectorFrame = new VectorFrame(previous_grid, current_grid, mouse_color.get(locTimeData.get(finalJ).timestamp).mouse_color, frame_number);
                                        line_frames.add(vectorFrame);
                                        
                                        for (int position = 0; position < line_frames.size(); position ++){
                                            
                                            if(line_frames.get(position).get_animation_state() == 1){
                                            
                                                dataCanvasContext.setStroke(line_frames.get(position).color);
                                                 dataCanvasContext.setLineDashes(0d);
                                                dataCanvasContext.strokeLine(
                                                line_frames.get(position).first.center_x,
                                                line_frames.get(position).first.center_y,
                                                line_frames.get(position).second.center_x,
                                                line_frames.get(position).second.center_y);
                                                System.out.print(line_frames.get(position).get_animation_state());
                                                
                                            }else if(line_frames.get(position).get_animation_state() == 2){
                                                
                                                dataCanvasContext.setStroke(line_frames.get(position).color);
                                                dataCanvasContext.setLineDashes(10d);
                                                dataCanvasContext.strokeLine(
                                                line_frames.get(position).first.center_x,
                                                line_frames.get(position).first.center_y,
                                                line_frames.get(position).second.center_x,
                                                line_frames.get(position).second.center_y);
                                                
                                                
                                            }else if(line_frames.get(position).get_animation_state() >= 3){ 
                                                
                                                dataCanvasContext.setStroke(Color.TRANSPARENT);
                                                dataCanvasContext.setLineDashes(0d);
                                                dataCanvasContext.strokeLine(
                                                line_frames.get(position).first.center_x,
                                                line_frames.get(position).first.center_y,
                                                line_frames.get(position).second.center_x,
                                                line_frames.get(position).second.center_y);
                                            }
                                        }
                                    }
                                });
                                // delay the animation thread by "speed" number of milliseconds;
                                // this value should come directly from the Frame Delay GUI slider control:
                                Thread.sleep((int)speed);
                            }
                        }
                        long endTs = System.currentTimeMillis(); // stop the timer
                        elapsedTs = endTs - startTs; // get the elapsed time of the generation duration
                        return null; // we aren't returning a specific value from the task, so return null
                    }
                    // if the animation was cancelled by the user, respond gracefully:
                    @Override protected void cancelled() {
                        super.cancelled();
                        updateMessage("Cancelled!");
                    }
                    // if the animation succeeded, respond gracefully:
                    @Override protected void succeeded() {
                        stopAnimation(generateButton);

                        // output a text status update to the user containing the duration of the animation:
                        String describeMice = (mice.size() > 1) ? "mice" : "mouse";
                        leftStatus.setText("Finished generating an animated heat map of " + mice.size() + " " + describeMice + " in " + elapsedTs + " milliseconds.");
                    }
                };
            }
        };
        // start the animation thread:
        service.start();
    }
    
    /*
    Alex (3/29/17):
        This is parkers code, I just put it in a function since it was repeated in both static and vector functions.
            Current problem with exporting -> it'll erase the screen after exporting, but won't reset checkboxes
                for grid lines and numbers, so unchecking them enables them instead. Need to fix that.
    */
    /**
     * 
     * @author: Parker, Alex
     * 
     * check if gridnumbers and/or gridlines Canvas objects exist in the viewerPane
     * parent object. If they do, move them to the front of the Grid's Canvas layers.
     * This is useful for ensuring that the grid overlay and grid sector numbers
     * will appear on top of any generated map layers.
     * 
     * @param viewerPane 
     */
    void moveInfoLayersToFront(StackPane viewerPane) {
        
        // (Parker 3/27/17): Get the child gridnumbers and gridlines Canvas layers that have been added to the viewerPane object as children:
        viewerPaneGridNumbers = (Canvas)viewerPane.lookup("#gridnumbers");
        viewerPaneGridLines = (Canvas)viewerPane.lookup("#gridlines");
        
        // (Parker 3/27/17): if they exist (meaning if the user has chosen to display them), shift the layers to the front on top of the datalayers:
        if (viewerPaneGridNumbers != null)
            viewerPaneGridNumbers.toFront();
        
        if (viewerPaneGridLines != null)
            viewerPaneGridLines.toFront();
    }
}
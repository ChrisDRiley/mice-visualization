package micevisualization;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.net.URISyntaxException;
import java.net.URL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.ButtonSkin;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import java.awt.AWTException;
import java.text.ParseException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.scene.text.Text;

public class AppStageController {
    // Parker (3/19/17): access certain GUI elements from the XML:
    @FXML private Stage stage;
    @FXML private Label leftStatus;
    // @FXML private ProgressBar progressBar;
    @FXML private ListView sessionsListView;
    @FXML private AnchorPane visualizationOptionsAnchorPane;
    @FXML private AnchorPane sessionsAnchorPane;
    @FXML private ChoiceBox visualizationTypeChoiceBox;
    @FXML private CustomMenuItem saveMenuItem;
    @FXML private CustomMenuItem exportMenuItem;
    @FXML private StackPane viewerPane;
    @FXML private ListView selectedMiceListView;
    @FXML private TextArea startDataRangeTextArea;
    @FXML private TextArea stopDataRangeTextArea;
    @FXML private ChoiceBox mapTypeChoiceBox;
    @FXML private CheckBox showGridLinesCheckBox;
    @FXML private CheckBox showGridNumbersCheckBox;
    @FXML private Button generateButton;
    @FXML private ScrollPane visualizationOptionsScrollPane;
    @FXML private VBox animationOptionsVBox;
    @FXML private Slider animationSpeedSlider;
    @FXML private TextArea currentAnimationFrame;
    @FXML private ChoiceBox stopDataRangeChoiceBox;
    @FXML private ChoiceBox startDataRangeChoiceBox;
    @FXML private HBox gridOptionsCheckBoxesHBox;
    @FXML private VBox dataRangeControlVBox;
    @FXML private CustomMenuItem exportAnimationItem;
    @FXML private CustomMenuItem About;
  
    // Parker (3/17/17)
    // Create a session variable to store the state of the program:
    Session session = new Session();
    
    // Parker (3/22/17)
    // Create a mice variable to store the mice read in from the data file:
    Mice mice = new Mice();

    // (Parker 3/26/17): create a new Grid object, representing the grid and its sectors
    Grid grid = new Grid();
    
    // Parker (3/19/17): The name of the folder for storing session data in:
    final String SESSIONS_FOLDER = "\\mice-sessions";
    
    
    /**
     * 
     * @author: Parker
     * 
     * Show a help documentation window to the user. This function generates the
     * necessary GUI objects that will represent the text and images of the documentation.
     * 
     * @param event 
     */
    @FXML protected void showHelpDocumentationAction(ActionEvent event) {
            final Stage helpDoc = new Stage();
            
            // create the root node:
            TabPane tabPane = new TabPane();
            tabPane.setPrefSize(700, 700);
            tabPane.setMinSize(TabPane.USE_PREF_SIZE, TabPane.USE_PREF_SIZE);
            tabPane.setMaxSize(TabPane.USE_PREF_SIZE, TabPane.USE_PREF_SIZE);
            
            // disallow the closing of individual tabs:
            tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

            // Generate the GUI objects of the File Menu documentation:
            GenerateFileMenuTab(tabPane);
            GenerateSessionTab(tabPane);
            GenerateVisualizationTab(tabPane);
            
 
            //create scene with set width, height and color
            Scene scene = new Scene(tabPane, 700, 700, Color.WHITESMOKE);
 
            //set scene to stage
            helpDoc.setScene(scene);
 
            //set title to stage
            helpDoc.setTitle("Help Documentation");
 
            //center stage on screen
            helpDoc.centerOnScreen();
 
            //show the stage
            helpDoc.show();
    }
    
    /*
    Parker(4/18/17):  This function contains the necessary code for setting up objects that will comprise
    the File Menu documentation. The tabPane parameter object is the parent of the content generated in this function.
    This function is called in the showHelpDocumentationAction() function.
    */
    public void GenerateFileMenuTab(TabPane tabPane) {
       String vboxStyle = "-fx-padding: 15px; -fx-spacing: 15px;";
            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 18px";
            String boldStyle = "-fx-font-weight: bold";
            
            Tab tab = new Tab();
            tab.setText("File Menu");
            
            ScrollPane contentScroll = new ScrollPane();
            
            VBox contentArea = new VBox();
            contentArea.setPrefWidth(660.0);
            contentArea.setStyle(vboxStyle);
            
            Label headerLabel = new Label("File Menu");
            headerLabel.setStyle(headerStyle);
            
            Image contentImage = new Image("resources/filemenu.png");
            ImageView iv = new ImageView(contentImage);
            
            Label bodyLabel = new Label("The main menu located at the upper top edge of the program screen contains options that "
                    + "correspond to certain file-related commands that the user can execute.");
            bodyLabel.setWrapText(true);
            
            Label item1Header = new Label("1) Main Menu options");
            item1Header.setStyle(boldStyle);
            Label item1Description = new Label("The main menu contains two categories of options: those relating to file "
                    + "operations ('File'), and those relating to the help documentation ('Help'). The options within the File "
                    + "category are explained below; the options in the Help category allow the user to either view the program's "
                    + "documentation or the contact information of the original developers.");
            item1Description.setWrapText(true);
            
            Label item2Header = new Label("2) Open");
            item2Header.setStyle(boldStyle);
            Label item2Description = new Label("The Open option opens a file explorer, enabling the user to select either a "
                    + ".CSV data set file or a .JSON session data file. \nThe program validates CSV data sets according to certain rules. In the CSV file, the data must conform to the following format:\n" +
                    "\n" +
                    "Column headers ...\n" +
                    "10/10/2010 10:10:10.100,mouseIdString,mouseLabelString,RFID10,1010\n" +
                    "...\n" +
                    "\n" +
                    "The column headers are ignored. The first column in the data row contains the timestamp, which must be in the format MM/dd/yyyy HH:mm:ss.SSS, the second column represents the unique identifier that each mouse should have throughout the data set, the third column contains the label associated with the identifier in the preceding column, the fourth column contains the label for the grid sector, which must be in the format \"XXXX00\" (where 'X' is an alphanumeric character and '0' is a digit), and the fifth column contains the duration, in milliseconds, of how long the mouse remained in the grid sector. There is no limit on the number of data rows that the program can process.");
            item2Description.setWrapText(true);
            
            Label item3Header = new Label("3) Save");
            item3Header.setStyle(boldStyle);
            Label item3Description = new Label("The Save option saves the current state of the program to its session file. "
                    + "If the current session does not have an associated file, the program will ask the user if they would "
                    + "like to create a new session file.");
            item3Description.setWrapText(true);
            
            Label item4Header = new Label("4) Export Image");
            item4Header.setStyle(boldStyle);
            Label item4Description = new Label("The Export option exports the current visualization as either a PNG or JPEG file. "
                    + "The program displays a directory chooser in order for the user to specify the save location of the "
                    + "export file.");
            item4Description.setWrapText(true);
            
            Label item5Header = new Label("5) Export Animation");
            item5Header.setStyle(boldStyle);
            Label item5Description = new Label("The Export Animation option, enabled only when the 'Visualization Type' is "
                    + "set to 'Animated', exports an animation of the data. The export consists of individual frames of the "
                    + "animation, saved as PNG files. Since it is possible for the animation export to generate a large "
                    + "amount of data, the program will perform a memory check on the export location selected by the user. "
                    + "While rendering, the export happens in 'real time'; the current frame displayed in the viewer area "
                    + "shows the most recently exported frame.");
            item5Description.setWrapText(true);
            
            Label item6Header = new Label("6) Exit");
            item6Header.setStyle(boldStyle);
            Label item6Description = new Label("The Exit option closes the program. "
                    + "If the current session of the program has an associated session file, "
                    + "the file is overwritten with the program's current state before the program closes.");
            item6Description.setWrapText(true);
            
            contentArea.getChildren().add(headerLabel);
            contentArea.getChildren().add(bodyLabel);
            contentArea.getChildren().add(iv);
            contentArea.getChildren().add(item1Header);
            contentArea.getChildren().add(item1Description);
            contentArea.getChildren().add(item2Header);
            contentArea.getChildren().add(item2Description);
            contentArea.getChildren().add(item3Header);
            contentArea.getChildren().add(item3Description);
            contentArea.getChildren().add(item4Header);
            contentArea.getChildren().add(item4Description);
            contentArea.getChildren().add(item5Header);
            contentArea.getChildren().add(item5Description);
            contentArea.getChildren().add(item6Header);
            contentArea.getChildren().add(item6Description);
            
            contentScroll.setContent(contentArea);
            
            tab.setContent(contentScroll);
            tabPane.getTabs().add(tab);
    }//end GenerateFileMenuTab
    
    
    /*
    Alex(4/18/17): Handles all information pertaining to the Session Manager
    */

    /**
     *
     * @param tabPane
     */

    
    public void GenerateSessionTab(TabPane tabPane) {
        
        String vboxStyle = "-fx-padding: 15px; -fx-spacing: 15px;";
            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 18px";
            String boldStyle = "-fx-font-weight: bold";
            
            Tab tab = new Tab();
            tab.setText("Session Manager");
            
            ScrollPane contentScroll = new ScrollPane();
            
            VBox contentArea = new VBox();
            contentArea.setPrefWidth(660.0);
            contentArea.setStyle(vboxStyle);
            
            Label headerLabel = new Label("Session Manager");
            headerLabel.setStyle(headerStyle);
            
            Image contentImage = new Image("resources/Session.png");
            ImageView iv = new ImageView(contentImage);
            
            Label bodyLabel = new Label("The Session Manager handles all JSON session files that are the data sets loaded into the program, "
                    + "which you can switch between at any time.");
            bodyLabel.setWrapText(true);
            
            Label explanationHeader = new Label("How sessions work");
            explanationHeader.setStyle(boldStyle);
            
            Label explanationBody = new Label("The session management capability of the system works based on file paths that link the system to two types of data: JSON session data, and CSV data set data. If two people, person A and person B, would like to share a program session across two computers, then the follow steps must occur:"
                                                + "\n\n1. Person A must choose to create a session file when loading a data set. The program will automatically save the state of the program to the session file during program execution."
                                                + "\n\n2. The CSV data set file that Person A loaded needs to be copied to the program's mice-sessions folder (which is automatically created by the program on startup) in order for Person B to use the session file on their computer. This is due to the program's use of relative paths: the program will attempt to look for the original location of the data set, which was somewhere on Person A's computer, but if this fails on Person B's computer (since Person B's computer will most likely have a different file path tree), the program will look in the mice-sessions folder for the data set, which is relative to the location of the program's executable JAR file."
                                                + "\n\n3. Person B needs to have the following on their computer: a copy of the program's executable JAR file, a copy of the session file, and a copy of the data set file which should reside in the mice-sessions folder in the same directory as Person B's executable JAR file."
                                                + "\n\n4. When Person B loads the session file, either through File -> Open or the Session Manager, the program's visualization options should be restored to what they were on Person A's computer, and the data set should be automatically loaded."
                                                + "\n\n.");
            explanationBody.setWrapText(true);
            
            Label item1Header = new Label("1) Recent Sessions");
            item1Header.setStyle(boldStyle);
            Label item1Description = new Label("This will display all current JSON files loaded into the current program, which you can switch between at any time.");
            item1Description.setWrapText(true);
            
            Label item2Header = new Label("2) Load Selected");
            item2Header.setStyle(boldStyle);
            Label item2Description = new Label("Clicking on a JSON session file will load the data into the visualization tools on the right hand side.");
            item2Description.setWrapText(true);
            
            Label item3Header = new Label("3) Delete Selected");
            item3Header.setStyle(boldStyle);
            Label item3Description = new Label("Deletes the highlighted session, with two prompts to prevent accidently deletion.");
            item3Description.setWrapText(true);
            
            // Add everything to the ContentArea
            contentArea.getChildren().add(headerLabel);
            contentArea.getChildren().add(bodyLabel);
            
            contentArea.getChildren().add(explanationHeader);
            contentArea.getChildren().add(explanationBody);
            
            contentArea.getChildren().add(iv);
            
            contentArea.getChildren().add(item1Header);
            contentArea.getChildren().add(item1Description);
            
            contentArea.getChildren().add(item2Header);
            contentArea.getChildren().add(item2Description);
            
            contentArea.getChildren().add(item3Header);
            contentArea.getChildren().add(item3Description);
            
            contentScroll.setContent(contentArea);
            
            //Finish setting up tab
            tab.setContent(contentScroll);
            tabPane.getTabs().add(tab);
        
    }//end GenerateSessionTab
    
    /*
    Alex(4/18/17): Handles all information pertaining to the visualization options
    */

    /**
     *
     * @param tabPane
     */

    
    public void GenerateVisualizationTab(TabPane tabPane) {
        
        String vboxStyle = "-fx-padding: 15px; -fx-spacing: 15px;";
            String headerStyle = "-fx-font-weight: bold; -fx-font-size: 18px";
            String boldStyle = "-fx-font-weight: bold";
            
            Tab tab = new Tab();
            tab.setText("Visualization Options");
            
            ScrollPane contentScroll = new ScrollPane();
            
            VBox contentArea = new VBox();
            contentArea.setPrefWidth(660.0);
            contentArea.setStyle(vboxStyle);
            
            Label headerLabel = new Label("Visualization Options");
            headerLabel.setStyle(headerStyle);
            
            Image contentImage = new Image("resources/Visualize.png");
            ImageView iv = new ImageView(contentImage);
            
            Image contentImage2 = new Image("resources/PlayAnimation.png");
            ImageView iv2 = new ImageView(contentImage2);
            
            Label bodyLabel = new Label("The Visualization Options are shown on the right side of the program, "
                    + "which allows you to create a heat and vector map, even both if needed, along with either static or animated visuals. "
                    + "You can also choose a specific frame set yourself or from a preset list.");
            bodyLabel.setWrapText(true);
            
            Label item1Header = new Label("1) Visualization Types");
            item1Header.setStyle(boldStyle);
            Label item1Description = new Label("There are two options to pick from: Static or Animated. Static will produce a visualization which has no animations. "
                    + "Animated will display the visualization in real-time through several animations.");
            item1Description.setWrapText(true);
            
            Label item2Header = new Label("2) Map Types");
            item2Header.setStyle(boldStyle);
            Label item2Description = new Label("Three options to choose from: Heat, Vector, and Overlay. A heat map will display the percentage of the mice on each grid square. "
                    + "The darker the square, the more mice congregated there. A Vector map will show where each mouse has been individually, outlined by a color-coded line. "
                    + "For the overlay, it is simply a combination of the heat and vector maps.");
            item2Description.setWrapText(true);
            
            Label item3Header = new Label("3) Grid Lines");
            item3Header.setStyle(boldStyle);
            Label item3Description = new Label("This will toggle on or off the grid lines on the visualization canvas.");
            item3Description.setWrapText(true);
            
            Label item4Header = new Label("4) Grid Numbers");
            item4Header.setStyle(boldStyle);
            Label item4Description = new Label("This will toggle on or off the grid numbers on the visualization canvas.");
            item4Description.setWrapText(true);
            
            Label item5Header = new Label("5) Selected Mice");
            item5Header.setStyle(boldStyle);
            Label item5Description = new Label("Pick and choose which mice you want to be displayed to the screen. You need to select at least one, however.");
            item5Description.setWrapText(true);
            
            Label item6Header = new Label("6) Start & Stop");
            item6Header.setStyle(boldStyle);
            Label item6Description = new Label("These paramters are the frame indices which are used to bound the range of the visualization. "
                    + "You can enter your own selection or choose from a preset selection. By default, the program prepopulates the Start index with the timestamp of the first data row and the Stop index with the timestamp of the last data row.");
            item6Description.setWrapText(true);
            
            Label item7Header = new Label("7) Animation Options");
            item7Header.setStyle(boldStyle);
            Label item7Description = new Label("If 'animated' is selected as the current Visualization Type, then the animation options will be enabled. The slider sets the amount of delay in milliseconds introduced by the program inbetween each frame of the animation. The Current Frame window displays the most recently rendered frame in the animation.");
            item7Description.setWrapText(true);
            
            Label item8Header = new Label("8) Generate Map");
            item8Header.setStyle(boldStyle);
            Label item8Description = new Label("Under the static option, you'll see 'Generate Static Map', which executes your choices and displays them to the screen. "
                    + "If you have 'animated' selected, the 'Generate Static Map' button will instead act as a play / stop button for controlling the animation.");
            item8Description.setWrapText(true);
            
            // Add everything to the ContentArea
            contentArea.getChildren().add(headerLabel);
            contentArea.getChildren().add(bodyLabel);
            contentArea.getChildren().add(iv);
            
            contentArea.getChildren().add(item1Header);
            contentArea.getChildren().add(item1Description);
            
            contentArea.getChildren().add(item2Header);
            contentArea.getChildren().add(item2Description);
            
            contentArea.getChildren().add(item3Header);
            contentArea.getChildren().add(item3Description);
            
            contentArea.getChildren().add(item4Header);
            contentArea.getChildren().add(item4Description);
            
            contentArea.getChildren().add(item5Header);
            contentArea.getChildren().add(item5Description);
            
            contentArea.getChildren().add(item6Header);
            contentArea.getChildren().add(item6Description);
            
            contentArea.getChildren().add(item7Header);
            contentArea.getChildren().add(item7Description);
            
            contentArea.getChildren().add(item8Header);
            contentArea.getChildren().add(item8Description);
            contentArea.getChildren().add(iv2);
            
            contentScroll.setContent(contentArea);
            
            //Finish setting up tab
            tab.setContent(contentScroll);
            tabPane.getTabs().add(tab);
        
    }//end GenerateVisualizationTab
    
    /*
    Alex(4/14/17):
        Exporting function code moved to the Grid class to make saving animation frames a lot easier to do.
    
    --------------------------------------------------------------------------------------------------------
    
    Parker (4/14/17):
    Refactored the export code for the purpose of separating the various types of activities involved into separate functions:
    
    1) promptExportFile() / promptExportFolder() prepares and displays a file / directory chooser window to the user
    for the purpose of getting the "Export to" location from the user. They both return the
    File object representing the user's selection.
    
    2) grid.exportFrame() renders the actual image data and will save the image file to the location provided
    by promptExportFile(). This code exists in the Grid class since the Grid's dimensions and data are
    required to render the image. It also makes this function possible to be executed each frame during
    animation exports.
    
    3) exportImage() is fired when the user activates the export menu "Export Image" option. It checks to ensure
    that the grid object contains data to be rendered. If so, the function then calls promptExportFile().
    If the result of promptExportFile() is not null (implying the user selected a file location), then
    grid.exportFrame() is called. The export operation ends. 
    
    4) exportAnimation() is fired when the user activates the export menu "Export Animation" option.
    
    */
    public File promptExportFile() throws AWTException, IOException {
        // Creates file options
        FileChooser fc = new FileChooser();

        //Set extension filters for PNG and JPEG
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"),
            new FileChooser.ExtensionFilter("JPEG (*.jpeg)", "*.jpeg")
        ); //end file extensions

        //Show save file dialog
        File file = fc.showSaveDialog(stage);
        return file;
    }//end promptExportFile
    
    /*
    Alex (4/12/17):
    This function uses the export animation button option in the GUI to export all the animation frames
    selected from the start and end range until a .png folder.
    */
    /**
     * 
     * @author Parker
     * 
     * This function exports a range of data rows, bound by the startDataRangeTextArea and stopDataRangeTextArea timestamps,
     * as png image files into a user-selected directory. This function is broken into roughly three steps:
     * 
     * 1) get the range of data rows to render / export
     * 2) calculate an estimated storage size of the export, by using the static map image export as a reference file size
     * 3) display estimation information to the user, await user confirmation, and export the animation.
     * 
     * The code achieves a frame-by-frame image export by using an exportFolder parameter in each of the
     * animation visualization functions; if a File object is supplied, the function will save each frame
     * of the animation to the location specified in the File object. 
     * 
     * @param event
     * @throws IOException
     * @throws URISyntaxException
     * @throws AWTException
     * @throws InterruptedException 
     */
    @FXML protected void exportAnimation(ActionEvent event) throws IOException, URISyntaxException, AWTException, InterruptedException {           
        
        // Get the range of data to render -------------------------------------------------------------------
        Date startIndex = null;
        Date stopIndex = null;
        
        /* (Parker 4/2/17): perform error checking of the basic animation parameters
        (map and visualization type, selected mice, start and stop indices). Returns false
        if a validation error is encountered. */
        if (checkCommonAnimationParameters() == false) return;
        
        /* (Parker 4/2/17): If the validation check passed, proceed with processing the user options: */
        
        // (Parker 4/1/17): get the mice that the user has selected in the mice list of the Visualization Options:
        ObservableList<String> selectedMiceIds = selectedMiceListView.getSelectionModel().getSelectedItems();
        
        // create a Mouse array of selected Mice based on the String Ids from the selected mice list:
        ArrayList<Mouse> selectedMice = mice.getMicebyIdsLabels(selectedMiceIds);
        
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        // attempt to coerce the content of the Stop text area into the required format:
        try {
            startIndex = formatter.parse(startDataRangeTextArea.getText());
            stopIndex = formatter.parse(stopDataRangeTextArea.getText());
        } catch (ParseException ex) {
            Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // (Parker 4/14/17): Extract a range of locTimeData "data rows", bounded by the startIndex and stopIndex, from the total number of 
        // locTimeData in each Mouse object contained within the selectedMice arrayList
        ArrayList<MouseLocTime> locTimeData = Mice.getMiceDataRowsFromRange(selectedMice, startIndex, stopIndex);
        
         // Calculate estimated export size --------------------------------------------------------------------
         
        //(Parker 4/15/17): Generate a temporary export of the animation's corresponding static map in order to obtain its
        // file size. The file size will be used to produce an estimate of the total animation size. 
        
        // reset the visual content of the grid before generating the map:
        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);

        switch (mapTypeChoiceBox.getValue().toString()) {
            case "Heat":
                grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex);
                break;
            case "Vector":
                grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                break;
            case "Overlay":
                // in order to create a static overlay of both heat an vector maps, simply call each static mapping method:
                grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex);
                grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                break;
            default:
                break;
        }//end switch
        
        // Create the File object to save the export to; this will be a temporary file -----------------------------
        
        // Parker (3-19-17): get the path of the currently executing JAR file without the program filename:
        Path folderContainingJarPath = getPathOfCurrentlyExecutingJarFile();
        // Create a new temporary file for the purposes of saving the static heat map. Help increaase the likelihood that
        // this filename is unique by appending the current milliseconds timestamp to the filename. 
        long currentTime = System.currentTimeMillis(); 
        String tempFilePath = folderContainingJarPath.toString() + "//temp" + String.valueOf(currentTime) + ".png";
        File tempFile = new File(tempFilePath);
        
        // -----------------------------------------------------------------------------------------------------
        
        
        
        
        // Get the export location and get confirmation from user ----------------------------------------------
        
        // (Parker 4/15/17): create a DirectoryChooser object, which will display the Operating System's file explorer to the user
        DirectoryChooser directoryChooser = new DirectoryChooser();
        // configure the DirectoryChooser object:
        directoryChooser.setTitle("Select a folder to save the animation export to");
        directoryChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
        
        // Prompt the user to select a folder that will contain the exported animation
        File exportFolder = directoryChooser.showDialog(stage);
        
        // (Parker 4/15/17): Sleep the thread for a small duration so that the directoryChooser window
        // can fully close (and not obscure the viewerPane visualization area). This is necessary due to a bug with
        // the current use of the bufferedImage that takes a snapshot of the current screen:
        Thread.sleep(500);
        
        // Export the static map to the temporary file, and get its file size:
        grid.exportFrame(viewerPane, tempFile, getFileExtension(tempFilePath));
        
        double tempFileSize = 0.0;
        // if the file exists, get its size and delete it:
        if(tempFile.exists()){
            tempFileSize = tempFile.length();
            System.out.println(tempFile.length() + " Bytes (" + tempFile.length()/1000.00 + " Kilobytes (metric)).");
            //tempFile.delete();
        }//end if
        
        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
        
        // Calculate an estimated total storage size of the animated frame image files, by extrapolating based on
        // a reference image (the temporary image of the static map) multiplied by the number of frames. -----------------------------
        double estimatedStorageSize = tempFileSize * locTimeData.size();
        String estimateString = getByteSizeSummary(estimatedStorageSize);
        
        if (exportFolder != null ) {
            
            //(Parker 4/15/17): Get the drive of the folder that the user selected, and calculate its available storage:
            File hardDrive = new File(exportFolder.getAbsolutePath().substring(0, exportFolder.getAbsolutePath().indexOf("\\")) + "\\");
            System.out.println(hardDrive.getPath());
            String usableSpaceString = getByteSizeSummary(hardDrive.getUsableSpace());
            System.out.println("Usable space: " + usableSpaceString);

            Alert alert2 = new Alert(AlertType.CONFIRMATION);
            alert2.setTitle("Program Notification");
            alert2.setHeaderText("Confirm animation export of " + locTimeData.size() + " frames.");
            alert2.setContentText("You are about to export " + locTimeData.size() + " .png files.\n\nThe estimated total storage size required for the export is \n" + estimateString + ".\n\nThe available space on drive " + hardDrive.getPath() + " is \n" + usableSpaceString + ".\n\nDo you want to proceed?");
            Optional<ButtonType> confirm = alert2.showAndWait();

            //(Parker 4/15/17): If the user chooses to proceed with the export, notify the user if there is insufficient disk space.
            // If there is enough disk space, execute the visualization animation function that corresponds to the current map type and
            // pass the exportFolder File object as a parameter.
            if(confirm.get() == ButtonType.OK) {
                if (estimatedStorageSize > hardDrive.getUsableSpace()) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Program Notification");
                    alert.setHeaderText("Error: The animation can not be exported.");
                    alert.setContentText("The animation can not be exported, since its estimated export size is larger than the unused capacity of your selected folder's disk.\n\nPlease free up some disk space or choose another disk and try again.");
                    alert.showAndWait();
                }//end if
                // else, begin the export operation:
                
                else {
                    // alter the GUI so that the user is aware of how to stop the animation:
                    grid.animationCancelled = false;
                    Image buttonIcon = new Image("resources/stop.png", 20, 20, true, true);
                    generateButton.setGraphic(new ImageView(buttonIcon));
                    generateButton.setText("Stop Animation");
                    
                    switch (mapTypeChoiceBox.getValue().toString()) {
                        case "Heat":
                            grid.animatedHeatMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), exportFolder);
                            break;
                        case "Vector":
                            grid.animatedVectorMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), exportFolder);
                            break;
                        case "Overlay":
                            grid.animatedOverlayMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), exportFolder);
                            break;
                        default:
                            break;
                    }//end switch
                }//end else
            }//end if
        }//end if
    }//end exportAnimation
    
        /*
    Alex (3/27/17):
    This function uses the save button option in the GUI to export the current image
    to your computer as a .png or .jpeg file. Will be modified later once we work on
    animation export options
    
    ----------------------------------------------------------------------------------------------
    
    Parker (4/14/17):
    Refactored the export code for the purpose of separating the various types of activities involved into separate functions:
    
    1) promptExportFile() / promptExportFolder() prepares and displays a file / directory chooser window to the user
    for the purpose of getting the "Export to" location from the user. They both return the
    File object representing the user's selection.
    
    2) grid.exportFrame() renders the actual image data and will save the image file to the location provided
    by promptExportFile(). This code exists in the Grid class since the Grid's dimensions and data are
    required to render the image. It also makes this function possible to be executed each frame during
    animation exports.
    
    3) exportImage() is fired when the user activates the export menu "Export Image" option. It checks to ensure
    that the grid object contains data to be rendered. If so, the function then calls promptExportFile().
    If the result of promptExportFile() is not null (implying the user selected a file location), then
    grid.exportFrame() is called. The export operation ends. 
    
    4) exportAnimation() is fired when the user activates the export menu "Export Animation" option.
    */
    @FXML protected void exportImage(ActionEvent event) throws IOException, AWTException, InterruptedException {
        
        //If nothing has been generated to the canvas screen, ERROR!
        if(grid.data == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Program Notification");
            alert.setHeaderText("Error: No data to render.");
            alert.setContentText("The visualization currently has no data. Please click the Generate Static Map / Play Animation button to create data for exporting.");
            alert.showAndWait();
        }//end if
        else {
            //Calls exporting function
            File exportLocation = promptExportFile();
            // (Parker 4/15/17): Sleep the thread for a small duration so that the directoryChooser window
            // can fully close (and not obscure the viewerPane visualization area). This is necessary due to a bug with
            // the current use of the bufferedImage that takes a snapshot of the current screen:
            Thread.sleep(500);
            
            if (exportLocation != null) {
                String exportExtension = getFileExtension(exportLocation.toString());
                grid.exportFrame(viewerPane, exportLocation, exportExtension);
                //***[[1]] Prevents grid lines/numbers from disappearing. See above notes for more details.
                drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
            }//end if
        }//end else
    }//end exportImage
    
    /* 
    Alex (4/13/17):
    Simply adding prompts to tutorial and help menus since they are not operational for now
    */
    
    @FXML protected void About(ActionEvent event) {
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About the Mice Visualization program");
        alert.setHeaderText("Project information:");
        alert.setContentText("Project GitHub repository: https://github.com/parkercode/mice-visualization\n\nParker Rowland - heat map, session handling, data parsing, animation code skeleton, animation export, user interface\nEmail: parkertrowland@gmail.com\n\nJosh Mwandu - vector map\nEmail: jmwandu@mail.umw.edu\n\nAlex Brown - image export\nEmail: abrown6@mail.umw.edu\n\n");
        alert.showAndWait(); 
    }
    
    
    
    /**
     * 
     * @author Parker
     * 
     * This function adds an event handler to a specified ChoiceBox control. The event handler
     * responds to when the user presses the Enter key while a particular ChoiceBox is focused.
     * When the Enter key is pressed, the ChoiceBox options display will be toggled as show/hide.
     * In order for the event handler to be added to the ChoiceBox, this function is called
     * in the initialize() function with the ChoiceBox object passed as a parameter.
     * 
     * @param cb the ChoiceBox object to attach the event handler to
     */
    void addEnterKeyDisplayToChoiceBox(ChoiceBox cb) {
        cb.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.getSource() instanceof ChoiceBox) {
                    if (cb.showingProperty().get())
                        cb.hide();
                    else
                        cb.show();
                    event.consume();
                }//end if
            }//end if
        } //end handle
        );
    }//end addEnterKeyDisplayToChoiceBox
    
    /**
     * 
     * @author Parker
     * 
     * This function adds an event handler to a specified CheckBox control. The event handler
     * responds to when the user presses the Enter key while a particular CheckBox is focused.
     * When the Enter key is pressed, the CheckBox will be toggled as selected/unselected.
     * In order for the event handler to be added to the CheckBox, this function is called
     * in the initialize() function with the CheckBox object passed as a parameter.
     * 
     * @param cb the CheckBox object to attach the event handler to
     */
    void addEnterKeyToggleToCheckBox(CheckBox cb) {
        cb.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (event.getSource() instanceof CheckBox) {
                    CheckBox currentCheckBox = (CheckBox) event.getSource();
                    if (currentCheckBox.isSelected())
                        currentCheckBox.setSelected(false);
                    else
                        currentCheckBox.setSelected(true);
                    event.consume();
                }//end if
            }//end if
        } //end handle
        );
    }//end addEnterKeyToggleToCheckBox
    
    /**
     * 
     * This function adds an event handler to a specified TextArea control. The event handler
     * responds to when the user presses the Tab key while a particular TextArea is focused.
     * When the Tab key is pressed, the GUI will navigate to the next (Tab) or previous (Shift-Tab) GUI control.
     * In order for the event handler to be added to the TextArea, this function is called
     * in the initialize() function with the TextArea object passed as a parameter.
     * 
     * @param cb the TextArea object to attach the event handler to
     */
    void addTabKeyNavigationToTextArea(TextArea ta) {
        ta.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) ta.getSkin();
                if (skin.getBehavior() instanceof TextAreaBehavior) {
                    TextAreaBehavior behavior = (TextAreaBehavior) skin.getBehavior();
                    if (event.isShiftDown())
                        behavior.callAction("TraversePrevious");
                    else
                        behavior.callAction("TraverseNext");
                    event.consume();
                }//end if
            }//end if
        });
    }//end addTabKeyNavigationToTextArea
    
    /**
     * 
     * @author Parker
     * 
     * This function adds an event handler to the parameter ChoiceBox object cb. Whenever a
     * selection is made in cb, copy the selected item's value into the parameter TextArea object ta's 
     * text property. This function is called in the initialize() function.
     * 
     * @param cb the ChoiceBox that will be the source of the TextArea's new content
     * @param ta the TextArea that will have the ChoiceBox's selection written to its text property
     */
    void applyDataRangeChoiceBoxSelectionToTextArea(ChoiceBox cb, TextArea ta) {
        cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ta.setText(newValue);
            }
        });
    }//end applyDataRangeChoiceBoxSelectionToTextArea
    
    /**
     * 
     * @author Parker
     * 
     * Adds an event handler to the parameter CheckBox object cb.
     * Whenever cb's value changes (selected / unselected), fire the appropriate grid function based on the targeted CheckBox's id,
     * update the session's state, and try to save the current session.
     * 
     * @param cb the CheckBox object to assign the event handler to
     */
    void addToggleGridOptionActionToCheckBox(CheckBox cb) {
        cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            if ((CheckBox)gridOptionsCheckBoxesHBox.lookup("#" + cb.getId()) != null) {
                if (cb.getId().equals("showGridLinesCheckBox")) {
                    grid.toggleGridLines(viewerPane);
                    session.showGridLines = new_val;
                }//end if
                
                else if (cb.getId().equals("showGridNumbersCheckBox")) {
                    grid.toggleGridNumbers(viewerPane);
                    session.showGridNumbers = new_val;
                }//end else if
                
                try {
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
            }//end if
        });
    }//end addToggleGridOptionActionToCheckBox
    
    /**
     * 
     * @author Parker
     * 
     * This function adds an event handler to the parameter TextArea object ta.
     * When the user enters a new value into ta, the system will use ta's id to execute
     * particular system commands and update the session.
     * The system will then attempt to save the session state.
     * 
     * @param ta the TextArea object to assign the event handler to
     */
    void updateSessionTextAreaContent(TextArea ta) {
        ta.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if ((TextArea) dataRangeControlVBox.lookup("#" + ta.getId()) != null) {
                if (ta.getId().equals("startDataRangeTextArea"))
                    session.startingIndex = newValue;
                else if (ta.getId().equals("stopDataRangeTextArea"))
                    session.stoppingIndex = newValue;
                
                try {
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
            }//end if
        });
    }//end updateSessionTextAreaContent
    
    /**
     * 
     * @author Parker
     * 
     * Trigger a redraw of the Canvas objects contained in the visualization. 
     * This function is used inside the event handler that reacts to when the user
     * resizes the program window.
     * 
     */
    void redrawGridUponWindowResize() {
         // if the session is not a new session, then there is data available to draw:
        if (session.isNewSession == false)
            drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
        
        // if the user resizes the window during an animation, cancel the animation:
        if (grid.animationCancelled == false)
            grid.stopAnimation(generateButton);
    }//end redrawGridUponWindowResize
    
    /**
     * 
     * @author: Parker
     * 
     * this is called once after the controller is loaded. It's purpose is to perform
     * any code at the very beginning of program execution once the GUI is ready.
     * Most of the code contained within this function assigns event handlers to specific
     * GUI control objects; a few lines deal with setting specific properties on certain objects
     * that need to be set only once per system lifecycle.
     * 
     * @throws URISyntaxException 
     */
    @FXML
    private void initialize() throws URISyntaxException {
        // (Parker 3/26/17): When the user resizes the window, trigger a redraw of the Canvas objects in the visualization
        viewerPane.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) -> {
            redrawGridUponWindowResize();           
        });
        
        // (Parker 3/26/17): When the user resizes the window, trigger a redraw of the Canvas objects in the visualization
        viewerPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) -> {
            redrawGridUponWindowResize();           
        });
        
        animationSpeedSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) -> {
            session.animationSpeed = newValue.doubleValue();
            try {
                session.saveState();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
            }           
        });
        
        // (Parker 3/26/17): When the user changes their selection in the visualization type choice box,
        // respond to that change
        visualizationTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                /* (Parker 4/2/17): Depending on the visualization options selected by the user,
                there may be options that are irrelevant to the current select (such as animation options
                for a static map). When the user changes their visualization options, enable or disable any
                options whose state needs to change: */
                if (newValue.equals("Static") || newValue.equals("Overlay")) {
                    // if the user changes a visualization option during an animation, cancel the animation:
                    if (grid.animationCancelled == false)
                        grid.stopAnimation(generateButton);
                    
                    animationOptionsVBox.setDisable(true);
                    generateButton.setText("Generate Static Map");
                    generateButton.setGraphic(null);
                    lockExportAnimationOption(); //locks out animation export since it's now static view
                }//end if
                
                else if (newValue.equals("Animated")) {
                    grid.stopAnimation(generateButton);
                    animationOptionsVBox.setDisable(false);
                    unlockExportAnimationOption(); //unlocks animation exporting option
                }//end else if
                
                try {
                    session.visualizationType = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
            }//end change
        });
        
         // (Parker 3/27/17): When the user changes their selection in the map type choice box,
        // respond to that change by changing the state of the visualization options and saving the current session.  
        mapTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // if the user changes a visualization option during an animation, cancel the animation:
                if (grid.animationCancelled == false)
                    grid.stopAnimation(generateButton);
                
                try {
                    session.mapType = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
            }//end changed
        });

        // (Parker): Get the selected mice from the selectedMiceListView GUI control, and save their indicies to 
        // the session whenever a change in the selection is made:
        ObservableList<Integer> selectedMice = selectedMiceListView.getSelectionModel().getSelectedIndices();
        selectedMice.addListener((ListChangeListener.Change<? extends Integer> c) -> {
            String selectedIndices = "";
            for (int i = 0; i < selectedMice.size(); ++i) {
                selectedIndices += String.valueOf(selectedMice.get(i));
                if (i < selectedMice.size() - 1) selectedIndices += ",";
            }//end for
            
            session.selectedMiceIndices = selectedIndices;
            
            try {
                session.saveState();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
            }//end catch
        });
        
        // (Parker 4/10/17): When the user has the Generate Map or Play/Stop Animation button
        // selected, make it possible to trigger the button's associated onAction function via the
        // Enter key:
        generateButton.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                ButtonSkin skin = (ButtonSkin) generateButton.getSkin();
                if (event.getSource() instanceof Button) {
                    try {
                        generateMapFunction();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                    }//end catch
                    finally {
                        event.consume();
                    }//end finally
                }//end if
            }//end if
        } //end handle
        );
        
        // (Parker): check if the directory for storing sessions exists:
        if (checkIfSessionsFolderExists() == false)
            // create the directory:
            getSessionsFolderPath().mkdir(); 
        
        // (Parker): Refresh the list of available session files that appear in the left hand
        // sessions listView GUI control:
        refreshListOfSessions();
        
        // (Parker 4/11/17): when the user enters a new value into a TextArea, use that TextArea's id to
        // execute particular functions, modify the state of the current session, and save the session.
        updateSessionTextAreaContent(startDataRangeTextArea);
        updateSessionTextAreaContent(stopDataRangeTextArea);
        
        // (Parker 4/10/17): when the user selects/deselects a grid options Check Box in the visualization options (sector numbers,
        // grid lines), respond to the new state of the Check Box by executing associated grid functions:
        addToggleGridOptionActionToCheckBox(showGridLinesCheckBox);
        addToggleGridOptionActionToCheckBox(showGridNumbersCheckBox);
        
        // (Parker 4/10/17): when the user selects a timestamp from a Start/Stop ChoiceBox dropdown in the Visualization Options,
        // replace the text property of the specified TextArea with the selected item in the ChoiceBox
        applyDataRangeChoiceBoxSelectionToTextArea(startDataRangeChoiceBox, startDataRangeTextArea);
        applyDataRangeChoiceBoxSelectionToTextArea(stopDataRangeChoiceBox, stopDataRangeTextArea);
        
        // (Parker 4/10/17): Add Tab key navigation to the TextArea GUI controls so that when
        // a particular TextArea is focused, the Tab key will cause the GUI to navigate to 
        // the next (Tab) or previous (Shift-Tab) GUI control.
        addTabKeyNavigationToTextArea(startDataRangeTextArea);
        addTabKeyNavigationToTextArea(stopDataRangeTextArea);
        
        // (Parker 4/10/17): Make the CheckBoxes in the Visualization Options able to be
        // toggled via the Enter key when focused:
        addEnterKeyToggleToCheckBox(showGridLinesCheckBox);
        addEnterKeyToggleToCheckBox(showGridNumbersCheckBox);
        
        // (Parker 4/10/17): While a ChoiceBox is focused in the visualization options, toggle the display of its drop-down options
        // when the enter key is pressed
        addEnterKeyDisplayToChoiceBox(stopDataRangeChoiceBox);
        addEnterKeyDisplayToChoiceBox(startDataRangeChoiceBox);
        addEnterKeyDisplayToChoiceBox(visualizationTypeChoiceBox);
        addEnterKeyDisplayToChoiceBox(mapTypeChoiceBox);
        
        // (Parker 4/3/17): initialize certain GUI components with certain settings:
        visualizationOptionsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        visualizationTypeChoiceBox.getSelectionModel().selectFirst();
        mapTypeChoiceBox.getSelectionModel().selectFirst();
    }//end initialize
    
    /**
     * 
     * @author: Parker
     * 
     * draw several Canvas objects to create the visualization 2D grid. 
     * 
     * @param width intended for use with the parent node's width
     * @param height intended for use with the parent node's height
     */
    public void drawCanvas(double width, double height) {
        grid.data = null;
        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
    }//end drawCanvas
    
    /**
     * 
     * @author: parker
     * 
     * restores the state of the program by setting GUI control values equal to their
     * values contained within the session object. This function is called after
     * a user successfully loads a session json file.
     */
    public void restoreState() {
        visualizationTypeChoiceBox.getSelectionModel().select(session.visualizationType);
        mapTypeChoiceBox.getSelectionModel().select(session.mapType);
        
        showGridLinesCheckBox.selectedProperty().set(session.showGridLines);
        showGridNumbersCheckBox.selectedProperty().set(session.showGridNumbers);
        
        animationSpeedSlider.setValue(session.animationSpeed);
        
        startDataRangeTextArea.setText(session.startingIndex);
        stopDataRangeTextArea.setText(session.stoppingIndex);
        
        
        // (Parker): Attempt to select the mice based on their indices from the session data:
        List<String> selectedMiceArray = Arrays.asList(session.selectedMiceIndices.split(",")); 
        int[] indices = new int[selectedMiceArray.size()];
        
        try {
            for (int i = 0; i < selectedMiceArray.size(); ++i) {
                indices[i] = (Integer.parseInt(selectedMiceArray.get(i)));
                System.out.println(selectedMiceArray.get(i));
                System.out.println(indices[i]);
            }//end for
            selectedMiceListView.getSelectionModel().selectIndices(indices[0], indices);
        }//end try
        catch (NumberFormatException e) {

        }//end catch
    }//end restoreState
    
    /**
     * 
     * @author: parker
     * 
     * locks the visualization options anchor pane and disables relevant menu options
     * 
     */
    public void lockVisualizationOptions() {
        visualizationOptionsAnchorPane.setDisable(true);
        saveMenuItem.setDisable(true);
        exportMenuItem.setDisable(true);
        viewerPane.getChildren().clear();
        selectedMiceListView.getItems().clear();
        startDataRangeTextArea.clear();
        stopDataRangeTextArea.clear();
    }//end lockVisualizationOptions
    
    /**
     * 
     * @author: parker
     * 
     * unlocks the visualization options anchor pane and disables relevant menu options
     * 
     */
    public void unlockVisualizationOptions() {
        visualizationOptionsAnchorPane.setDisable(false);
        saveMenuItem.setDisable(false);
        exportMenuItem.setDisable(false);
        drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());   
    }//end unlockVisualizationOptions
    
    /*
    Alex (4/12/17):
    Lock and unlock animation export. Not in same functions above due to
    being slightly different when exporting.
    */
    public void unlockExportAnimationOption() {
        exportAnimationItem .setDisable(false);
    }//end unlockExportAnimationOption
    
    public void lockExportAnimationOption() {
        exportAnimationItem .setDisable(true);
    }//end lockExportAnimationOption
    
    /**
     * @author: parker
     * 
     * resets the session variable and GUI to default.
     */
    public void resetToDefaultState() {
        session = new Session();
        visualizationTypeChoiceBox.getSelectionModel().clearSelection();
        lockVisualizationOptions();
    }//end resetToDefaultState
    
    /**
     * 
     * @author: parker
     * 
     * Refreshes the list of session files in the sessionsListView control.
     * The files within the sessions folder are counted and their names are
     * stored in an array. If there are no files, then the sessionsDeleteButton
     * and the sessionsLoadButton are disabled.
     * 
     * @throws URISyntaxException 
     */
    public void refreshListOfSessions() throws URISyntaxException {
        // any session files created from previous sessions will exist in the designated sessions folder:
        if (checkIfSessionsFolderExists()) {
            // create a list to store the file names of session files in the sessions folder:
            ObservableList<String> sessionFiles = FXCollections.observableArrayList();
            File[] files = getSessionsFolderPath().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile())
                        sessionFiles.add(file.getName());
                }//end for
                
                //if there were no files in the sessions folder, disable the sessions manager pane:
                if (sessionFiles.size() > 0)
                    sessionsAnchorPane.setDisable(false);
                // if at least one sessions file exists in the sessions folder, enable the sessions manager pane:
                else
                    sessionsAnchorPane.setDisable(true);
                
                // update the contents of the recent sessions list to reflect the files in the sessions folder:
                sessionsListView.setItems(sessionFiles);
            }//end if
        }//end if
    }//end refreshListOfSessions
    
    //Gets the file extension of a file name
    public String getFileExtension(String name) {
        int i = name.lastIndexOf('.');
        String ext;
        if (i > 0) {
            ext = name.substring(i+1);
            return ext;
        }//end if
        return null;
    }//end getFileExtension
    
    /**
     * 
     * @author: Parker
     * 
     * Generate a string representation of the quantity of the bytes parameter
     * 
     * @param bytes
     * @return 
     */
    public String getByteSizeSummary(double bytes) {
        String summaryString = bytes + " Bytes";
        if (bytes > 1000) summaryString = bytes/1000.00 + " Kilobytes (metric)";
        if (bytes > 1000000) summaryString = bytes/1000000.00 + " Megabytes (metric)";
        if (bytes > 1000000000) summaryString = bytes/1000000000.00 + " Gigabytes (metric)";
        
        return summaryString;
    }//end getByteSizeSummary

    /**
     * 
     * @author: parker
     * 
     * openFileAction is the event handler called when the user clicks on the 
     * File -> Open menu option. The user's OS will then present its file system viewer,
     * allowing the user to pick a data set file to load. This implementation is
     * cross-platform and should work on Mac, Windows, and Unix OSes.
     * 
     * @param event
     * @throws URISyntaxException
     * @throws IOException 
     */
    @FXML protected void openFileAction(ActionEvent event) throws URISyntaxException, IOException {
        FileChooser fileChooser = new FileChooser();
        
        fileChooser.setTitle("Select a data set file (CSV) or session file (JSON)");
        fileChooser.setInitialDirectory(
            new File(System.getProperty("user.home"))
        );
        // prepopulate the file chooser with allowable file extensions:
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("CSV", "*.csv"),
            new FileChooser.ExtensionFilter("JSON", "*.json")
        );

        // show the file explorer window:
        File fileToOpen = fileChooser.showOpenDialog(stage); 
        // check if the user selected a file:
        if (fileToOpen != null) {
            // get the extension of the user's selected file:
            String extension = getFileExtension(fileToOpen.toString()); 
            // if the file selected has an extension of CSV, assume it is a data set file:
            switch (extension) {
                case "csv":
                    // assume that the user is opening a new data set file, so disregard any previously saved
                    // starting and stopping indices associated with the current session's previous data set:
                    session.stoppingIndex = "";
                    session.startingIndex = "";
                    // attempt to open the file and proceed if successful:
                    if (openFile(fileToOpen)) {
                        
                        // update the program's state via the session variable:
                        session.dataSetFileLoaded(fileToOpen.getPath(), fileToOpen.getName());
                        // update the state of the GUI to enable visualization options:
                        unlockVisualizationOptions();
                        
                        // Check if the current session is new:
                        if (session.isNewSession)
                            // prompt the user to create a new session file:
                            promptUserToCreateNewSessionFile(session);
                    }   break;
                
                case "json":
                    // attempt to load the session file and restore its state to the current system session:
                    loadSessionFile(fileToOpen);
                    break;
                
                default:
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("Program Notification");
                    alert.setHeaderText("Invalid file type");
                    alert.setContentText("Allowable file types are:\n\n.csv data files\n.json session files");
                    alert.showAndWait();
                    break;
            }//end switch
        }//end if
    }//end openFileAction
    
    /**
     * @author parker
     * 
     * event handler for when user activates the sessionsDeleteButton control.
     * The system asks the user for a confirmation before proceeding with the deletion.
     * 
     * @param event
     * @throws URISyntaxException 
     */
    @FXML protected void deleteSessionFileAction(ActionEvent event) throws URISyntaxException {
        
        // get the selected session file name from the recent sessions list in the session manager pane:
        String currentListViewItem = sessionsListView.getSelectionModel().getSelectedItem().toString();
        
        // notify the user of the impending delete action:
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete session file?");
        alert.setHeaderText("You are about to delete the session file '" + currentListViewItem + "'.\n (Data set files will not be deleted)");
        alert.setContentText("Do you want to continue?");

        // wait for the user to either confirm or cancel the operation via the alert dialog options:
        Optional<ButtonType> retryFileCreationAnswer = alert.showAndWait();
        
        if (retryFileCreationAnswer.get() == ButtonType.OK) {
            // check if the file the user is trying to delete is the current session file
            
            // get the filename from the currentSessionFilePath:
            int i = session.currentSessionFilePath.lastIndexOf("\\"); 
            
            if (currentListViewItem.equals(session.currentSessionFilePath.substring(i + 1))) { // compare the filename to the selected session name
                // if the user has chosen to delete the session file that corresponds to the current session, obtain
                // additional confirmation from the user via another alert dialog:
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Warning! You are trying to delete the current session.");
                alert.setHeaderText("If you delete the current session file, then the current session will be reset to default.");
                alert.setContentText("Do you want to continue?");
                
                // wait for the user to either confirm or cancel the operation via the alert dialog options:
                retryFileCreationAnswer = alert.showAndWait();
                
                // if the user confirmed the delete operation, proceed to delete the selected session file:
                if (retryFileCreationAnswer.get() == ButtonType.OK) {
                    // turn the file URL into a File object
                    File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
                    
                    // attempt to delete the current session file:
                    if (!deleteFile.delete())
                        simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                    else {
                        leftStatus.setText("Session file successfully deleted.");
                        resetToDefaultState(); // create a new Session object and reset the GUI
                    }//end else
                }//end if
            }//end if
            
            else { // delete a file that is not associated with the current session::
                File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
                if (!deleteFile.delete())
                    simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                else leftStatus.setText("Session file successfully deleted.");
            }//end else
            // update the list of sessions appearing in the recent sessions list after an attempted delete operation:
            refreshListOfSessions();
        }//end if
    }//end deleteSessionFileAction
    
    /**
     * 
     * @author: parker
     * 
     * load a session file from the list of files available in the session manager list.
     * Search for the file in the sessions directory by filename, then attempt to 
     * load its contents by calling loadSessionFile(sessionFile).
     * 
     * @param event
     * @throws URISyntaxException
     * @throws FileNotFoundException 
     */
    @FXML protected void loadSessionFromManagerAction(ActionEvent event) throws URISyntaxException, FileNotFoundException {
        // if there was a session name selected in the recent sessions list:
        if (sessionsListView.getSelectionModel().getSelectedItem() != null) {
            // get the selected name from the Recent sessions listView:
            String selectedSession = sessionsListView.getSelectionModel().getSelectedItem().toString();
            
            // we need to trim the extension off the filename, so get the index of the last period:
            int i = selectedSession.lastIndexOf('.'); 
            
            // trim the extension off of the selectedSession string:
            selectedSession = selectedSession.substring(0, i); 
            
            // create an Optional for use with constructSessionFilePath():
            Optional<String> sessionName = Optional.of(selectedSession); 
            // recreate the path to the session file, which should reside in the sessions folder:
            File sessionFile = constructSessionFilePath(sessionName); 
            
            if (sessionFile.exists())
                // load the session file:
                loadSessionFile(sessionFile);
        }//end if
        else
            simpleAlert("Please select a session filename from the list of sessions.", null);
    }//end loadSessionFromManagerAction
    
    /**
     * 
     * @author parker
     * 
     * Validates several visualization parameters common to each type of map the program produces
     * (map and visualization type, selected mice, start and stop indices).
     * 
     * @return Returns false if a validation error is encountered, true otherwise.
     */
    public Boolean checkCommonAnimationParameters() {
        Date startIndex;
        Date stopIndex;
        
        // Parker (4/1/17): check for the correct date format in the Start and Stop text areas of the Visualization Options:
        try {
            // attempt to coerce the content of the Start text area into the required format:
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            startIndex = formatter.parse(startDataRangeTextArea.getText());
        }//end try
        catch (ParseException pe) {
            simpleAlert("Invalid starting index", "Please ensure the value entered into the Start field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return false;
        }//end catch
        
        try {
            // attempt to coerce the content of the Stop text area into the required format:
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            stopIndex = formatter.parse(stopDataRangeTextArea.getText());
        }//end try
        catch (ParseException pe) {
            simpleAlert("Invalid stopping index", "Please ensure the value entered into the Stop field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return false;
        }//end catch
        
        // (Parker 4/1/17): Check that the Start and Stop values are in range:
        if (startIndex.compareTo(stopIndex) > 0) {
            simpleAlert("Out-of-bounds starting index", "The starting index in the Start field must be less than or equal to the stopping index in the Stop field.");
            return false;
        }//end if
        else if (stopIndex.compareTo(startIndex) < 0) {
            simpleAlert("Out-of-bounds stopping index", "The stopping index in the Stop field must be greater than or equal to the starting index in the Start field.");
            return false;            
        }//end else if
        
        // (Parker 4/1/17): get the mice that the user has selected in the mice list of the Visualization Options:
        ObservableList<String> selectedMiceIds = selectedMiceListView.getSelectionModel().getSelectedItems();
        // create a Mouse array of selected Mice based on the String Ids from the selected mice list:
        ArrayList<Mouse> selectedMice = mice.getMicebyIdsLabels(selectedMiceIds);
        
        if (selectedMice == null) {
            simpleAlert("No mice selected!", "Please select at least one mouse to visualize.");
            return false;
        }//end if
        // if there were no validation errors, return true:
        return true;
    }//end checkCommonAnimationParameters
    
    /**
     * 
     * @author: parker
     * 
     * generate a visualization based on the user's selection of visualization options.
     * This function has two main stages: the error checking stage, followed by the series
     * of conditionals that determine which classification of map the user has chosen.
     * Inside each map's branch, the following general code is executed:
     * 
     * 1) the grid is redrawn
     * 2) a timer is started (in the form of a timestamp), for timing the duration of the generation
     * 3) the function corresponding to the specified map type (ex. Static Heat map) is executed
     * 4) the timer is stopped, and the resulting time is output to the lower left status area
     * 
     * 
     * @param event 
     */
    void generateMapFunction() throws InterruptedException {
        Date startIndex = null;
        Date stopIndex = null;
        
        /* (Parker 4/2/17): perform error checking of the basic animation parameters
        (map and visualization type, selected mice, start and stop indices). Returns false
        if a validation error is encountered. */
        if (checkCommonAnimationParameters() == false) return;
        
        /* (Parker 4/2/17): If the validation check passed, proceed with processing the user options: */
        
        // (Parker 4/1/17): get the mice that the user has selected in the mice list of the Visualization Options:
        ObservableList<String> selectedMiceIds = selectedMiceListView.getSelectionModel().getSelectedItems();
        
        // create a Mouse array of selected Mice based on the String Ids from the selected mice list:
        ArrayList<Mouse> selectedMice = mice.getMicebyIdsLabels(selectedMiceIds);
        
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        // attempt to coerce the content of the Stop text area into the required format:
        try {
            startIndex = formatter.parse(startDataRangeTextArea.getText());
            stopIndex = formatter.parse(stopDataRangeTextArea.getText());
        } catch (ParseException ex) {
            Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
        }//end catch

        // (Parker 3/27/17): Check to ensure the value of the visualizationTypeChoiceBox is not null:
        if (visualizationTypeChoiceBox.getValue() != null) {
            if (visualizationTypeChoiceBox.getValue().toString().equals("Static")) {
                
                // (Parker 3/27/17): Check to ensure the value of the mapTypeChoiceBox is not null:
                if (mapTypeChoiceBox.getValue() != null) {
                    
                    // reset the visual content of the grid before generating the map:
                    grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
                    // begin a timer to record the amount of time the generation takes
                    long start = System.currentTimeMillis();
                    
                    switch (mapTypeChoiceBox.getValue().toString()) {
                        case "Heat":
                            grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex);
                            break;
                        case "Vector":
                            grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                            break;
                        case "Overlay":
                            // in order to create a static overlay of both heat an vector maps, simply call each static mapping method:
                            grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex);
                            grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                            break;
                        default:
                            break;
                    }//end switch
                    
                    // stop the timer:
                    long end = System.currentTimeMillis();
                    
                    // get the elapsed time of the generation duration:
                    long elapsed = end - start;
                    
                    // output a summary of the map generation duration to the user as a text status:
                    String describeMice = (selectedMice.size() > 1) ? "mice" : "mouse";
                    String visualizationType = visualizationTypeChoiceBox.getValue().toString();
                    String mapType = mapTypeChoiceBox.getValue().toString();
                    leftStatus.setText("Finished generating a " + visualizationType + " " + mapType + " map of " + selectedMice.size() + " " + describeMice + " in " + elapsed + " milliseconds.");
                }//end if
                else
                    simpleAlert("No map type selected!", "Please select a map type from the dropdown.");
            }//end if
            
            else if (visualizationTypeChoiceBox.getValue().toString().equals("Animated")) {
                // (Parker 3/27/17): Check to ensure the value of the mapTypeChoiceBox is not null:
                if (mapTypeChoiceBox.getValue() != null) {
                    // if there is a current animation running and it has not been cancelled,
                    // stop the animation
                    if (grid.animationCancelled == false)
                        grid.stopAnimation(generateButton);
                    
                    // else, the current animation has been cancelled (or there is no current animation),
                    // so proceed to generate one:
                    else {
                        // alter the GUI so that the user is aware of how to stop the animation:
                        grid.animationCancelled = false;
                        Image buttonIcon = new Image("resources/stop.png", 20, 20, true, true);
                        generateButton.setGraphic(new ImageView(buttonIcon));
                        generateButton.setText("Stop Animation");
                        
                        // reset the visual content of the grid before generating the animation:
                        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);

                        switch (mapTypeChoiceBox.getValue().toString()) {
                            case "Heat":
                                grid.animatedHeatMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), null);
                                break;
                            case "Vector":
                                grid.animatedVectorMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), null);
                                break;
                            case "Overlay":
                                grid.animatedOverlayMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue(), null);
                                break;
                            default:
                                break;
                        }//end switch
                    }//end else
                }//end if
                else
                    simpleAlert("No map type selected!", "Please select a map type from the dropdown.");
            }//end else if
        }//end if
        
        else
            simpleAlert("No visualization type selected!", "Please select a visualization type from the dropdown.");
    }//end generateMapFunction
    
    /**
     * 
     * @author parker
     * 
     * Note: this function is simply a wrapper for the generateMapFunction; 
     * generateMapAction is called when the user manually clicks on the generate
     * visualization/ play animation button.
     * 
     * @param event
     * @throws InterruptedException 
     */
    @FXML protected void generateMapAction(ActionEvent event) throws InterruptedException {
        generateMapFunction();
    }//end generateMapAction
    
    /**
     * 
     * author: parker
     * 
     * save the current session's state to file. If a file representing the current session does not exist,
     * prompt the user to create one.
     * 
     * @param event
     * @throws FileNotFoundException
     * @throws URISyntaxException
     * @throws IOException 
     */
    public void saveFileAction(ActionEvent event) throws FileNotFoundException, URISyntaxException, IOException {
        // Check if the current session is new:
        if (session.isNewSession) 
            // prompt the user to create a new session file:
            promptUserToCreateNewSessionFile(session);
        
        // if the session is not new (already associated with a session file), save 
        // the current state of the session and alert the user via a dialog window:
        else {
            session.saveState();
            simpleAlert("File saved!", "The session data was saved to disk.");
        }//end else
    }//end saveFileAction
   
    /**
     * 
     * @throws java.io.FileNotFoundException
     * @author: parker
     * 
     * exitApplication is called when the user exits the application, currently by
     * selecting the File -> Exit menu option. Any cleanup code, if necessary, needs
     * to go either here or in the AppStage.java file's stop() function.
     * 
     * @param event 
     */
    @FXML
    public void exitApplication(ActionEvent event) throws FileNotFoundException {
       System.out.println("Platform is closing");
       if (session.isNewSession == false)
           // save the session's state before exiting:
           session.saveState();
       Platform.exit();
    }//end exitApplication
    
    /**
     * 
     * @author: parker
     * 
     * debugAlert is an easy way to show a pop-up containing a string of info (DEBUGGING ONLY)
     * 
     * @param info a string of information to display in the pop-up dialog
     */
    public void debugAlert(String info) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Debug Alert");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }//end debugAlert
    
    /**
     * 
     * @author: parker
     * 
     * simpleAlert displays a string to the user in a popup dialog. Both header and content strings
     * can be passed as parameters, and both can be set to null.
     * 
     * @param header string that will appear in the dialog header
     * @param info  string that will appear in the dialog content
     */
    public void simpleAlert(String header, String info) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Program Notification");
        alert.setHeaderText(header);
        alert.setContentText(info);
        alert.showAndWait();
    }//end simpleAlert
    
    /**
     * 
     * @author: parker
     * 
     * displays a prompt to the user asking for the name of a new session file.
     * 
     * @return returns a textInputDialog object, for capturing the response of the user.
     */
    public TextInputDialog showSessionFilePrompt() {
        // Parker (3-17-17): Create a default name, consisting of the
        // current date and time, for the potential new session:
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
        Date date = new Date();
        String defaultSessionName = "Session " + dateFormat.format(date);

        // Parker (3-17-17): Prompt the user asking if they would like
        // to save the currently loaded data set file within a new session:
        TextInputDialog dialog = new TextInputDialog(defaultSessionName);
        dialog.setTitle("Create new session?");
        dialog.setHeaderText("Sessions save program settings and data, maintaining your current work.");
        dialog.setContentText("Please enter a name for this session:");
        
        return dialog;
    }//end showSessionFilePrompt
    
    /**
     * 
     * @throws java.io.FileNotFoundException
     * @throws java.net.URISyntaxException
     * @author: parker
     * 
     * loads a JSON session file, updates the program GUI, and restores the state
     * of the program contained within the JSON file.
     * 
     * @param sessionFile the session file to load
     * @return true if session file was loaded, false if otherwise
     */
    public Boolean loadSessionFile(File sessionFile) throws FileNotFoundException, URISyntaxException {
        if (openFile(sessionFile)) {
            // if the selected session file contains a different session filename than the one listed in the 
            // currentSessionFilePath property of the session object, update the session object:
            if (!session.currentSessionFilePath.equals(sessionFile.getPath()))
                session.currentSessionFilePath = sessionFile.getPath();
            
            // create a File object based on the filepath string of the current data set file:
            File dataFile = new File(session.currentDataSetFilePath);
            
            //debugAlert("Attempting to open file " + dataFile.getPath().toString() + ", " + dataFile.exists());
            if (!dataFile.exists())
                dataFile = new File(getSessionsFolderPath() + "\\" + session.relativeDataSetFilePath);
            
            if (dataFile.exists()) {
                // attempt to parse the data set file associated with the session file being loaded:
                if (openFile(dataFile)) {
                    // update the program's state via the session variable:
                    session.sessionLoaded(sessionFile.getPath());
                    session.dataSetFileLoaded(dataFile.getPath(), dataFile.getName()); 
                    
                    // write the session state to file:
                    session.saveState();
                    
                    // update the System's GUI to enable the visualization options:
                    unlockVisualizationOptions();
                    
                    // draw the basic appearance of the grid in the viewer pane:
                    drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
                    // restore the user's GUI control settings from the loaded session file:
                    restoreState();
                    return true;
                }//end if
            }//end if
        }//end if
        leftStatus.setText("Error: The session file could not be loaded.");
        return false;
    }//end loadSessionFile
    
    /**
     * 
     * @author: parker
     * 
     * checks if the 'name' parameter consists of only alphanumeric characters, '-', ' ', '.', and/or '_'.
     * returns true if so, false if not.
     * 
     * @param name the filename string to check
     * @return whether or not the name has only allowable characters
     */
    public Boolean isFileNameValid(String name) {
        // loop through the characters of the name argument:
        for (int i = 0; i < name.length(); ++i) {
            // inspect the current character and judge if it is valid:
            char c = name.charAt(i);
            if (!Character.isDigit(c) && !Character.isAlphabetic(c) && c != '-' && c != '_' && c != ' ' && c != '.')
                return false;
        }//end for
        
        // return true if the string passed the validation check
        return true;
    }//end isFileNameValid
    
    /**
     * 
     * @author: parker
     * 
     * displays to the user a dialog warning about an invalid filename that was entered for a file.
     * a list of allowable characters is provided.
     * 
     */
    public void showInvalidFileNameWarning() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Program Notification");
        alert.setHeaderText("Invalid filename");
        alert.setContentText("Allowable characters in the filename include:\nAlphanumeric characters\nSpaces\n'-', '_', and '.' (dashes, underscores, and periods.");
        alert.showAndWait();
    }//end showInvalidFileNameWarning
    
    /**
     * 
     * @author Parker
     * 
     * Get the location of the program's currently executing .JAR file
     * 
     * @return
     * @throws URISyntaxException 
     */
    public Path getPathOfCurrentlyExecutingJarFile() throws URISyntaxException {
        // Parker (3-19-17): get the path of the folder containing the currently executing .jar file: 
        URL folderContainingJar = getClass().getProtectionDomain().getCodeSource().getLocation();
        // Parker (3-19-17): get the above path without the filename:
        Path folderContainingJarPath = Paths.get(folderContainingJar.toURI()).getParent();
        return folderContainingJarPath;
    }//end getPathOfCurrentlyExecutingJarFile
    
    /**
     * 
     * @author: parker
     * 
     * constructs the sessions folder by getting the directory that the executable jar
     * file of the program is running in and appending the name of the sessions folder.
     * 
     * @return the file object representing the sessions folder
     * @throws URISyntaxException 
     */
    public File getSessionsFolderPath() throws URISyntaxException {
        // Parker (3-19-17): get the path of the currently executing JAR file without the program filename:
        Path folderContainingJarPath = getPathOfCurrentlyExecutingJarFile();
        // Parker (3-19-17): create the path of the sessions folder, using the above path:
        String sessionsFolderPath = folderContainingJarPath.toString() + SESSIONS_FOLDER;
        // Parker (3-19-17): create a File object of the sessionsFolderPath for checking existence:
        File miceVizSessionsFolder = new File(sessionsFolderPath);
        return miceVizSessionsFolder;
    }//end getSessionsFolderPath
    
    /**
     * 
     * @author: parker
     * 
     * checks if a folder for storing the sessions exists or not.
     * The sessions folder is expected to exist in the same directory as the executing .jar file of the program.
     * 
     * @return whether or not the sessions folder exists
     * @throws URISyntaxException 
     */
    public Boolean checkIfSessionsFolderExists() throws URISyntaxException {
        // Parker (3-19-17): check to see if there is an existing sessions folder 
        // (in the same directory as the program's executable .JAR file):
        File miceVizSessionsFolder = getSessionsFolderPath();
        return miceVizSessionsFolder.isDirectory();
    }//end checkIfSessionsFolderExists
    
    /**
     * 
     * @author: parker
     * 
     * creates a session file path based on a filename and the sessions folder path.
     * 
     * @param sessionName the name of the session file
     * @return the file object representing the session file
     * @throws URISyntaxException 
     */
    public File constructSessionFilePath(Optional sessionName) throws URISyntaxException {
        File newSessionFile = new File(getSessionsFolderPath().toString() + "\\" + sessionName.get() + ".json");
        return newSessionFile;
    }//end constructSessionFilePath
    
    /**
     * 
     * @author: parker
     * 
     * creates a session file. There are three possible outcomes:
     * 1 the file is able to be created
     * 2 the file already exists
     * 3 some other reason prevented the file from being created
     * the function returns a status message; "true" means that the file was created.
     * 
     * @param sessionName the name of the session file
     * @return the status message reflecting the success of the file creation
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public String createSessionFile(Optional sessionName) throws IOException, URISyntaxException {
        File newSessionFile = constructSessionFilePath(sessionName);
        if (!newSessionFile.exists()) {
            if (newSessionFile.createNewFile()){
                simpleAlert("Session file created!", "File location:\n\n" + newSessionFile.toString());
                return "true";
            }//end if
            else
                return "Could not create the session file.";
        }//end if
        else
            return "Session file already exists.";
    }//end createSessionFile

    /**
     * 
     * @author: parker
     * 
     * displays a message to the user stating that the file they just attempted to create 
     * could not be created for a specific reason.
     * Returns true if the user wants to retry creating the file, returns false if not.
     * 
     * @param reason the reason that the file could not be created
     * @return the user's decision of whether or not to retry file creation
     */
    public Boolean showRetryFileCreationPrompt(String reason) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Program Notification");
        alert.setHeaderText("File could not be created for the following reason: " + reason);
        alert.setContentText("Do you want to retry creating a session file?");

        Optional<ButtonType> retryFileCreationAnswer = alert.showAndWait();
        
        return retryFileCreationAnswer.get() == ButtonType.OK;
    }//end showRetryFileCreationPrompt
    
    /**
     * 
     * @author: parker
     * 
     * Asks the user to create a new session file, with error handling provisions.
     * 
     * @param session the session object representing the program's state
     * @throws URISyntaxException
     * @throws IOException 
     */
    public void promptUserToCreateNewSessionFile(Session session) throws URISyntaxException, IOException {
        // store the session name entered by the user during the prompt:
        Optional<String> sessionName = null;
        // store if the user-entered file name is valid:
        Boolean isValidFileName;
        // store if the user has canceled the new session file creation operation:
        Boolean userCanceled = false;

        // loop while the user has not cancelled the operation:
        while (!userCanceled) { 
            isValidFileName = false;

            // loop while the user has not entered a valid session file name:
            while (!isValidFileName) {
                userCanceled = false;

                // prompt the user to save the current session as a file:
                TextInputDialog dialog = showSessionFilePrompt();
                // show the dialog and await the user's response:
                sessionName = dialog.showAndWait();

                // if the user submitted a file name::
                if (sessionName.isPresent()) { 
                    // if the file name entered is invalid:
                    if (!isFileNameValid(sessionName.get()))
                        // alert the user the file name is invalid:
                        showInvalidFileNameWarning(); 
                    else
                        // valid file name; break out of the !isValidFileName while loop:
                        isValidFileName = true;
                }//end if
                
                // else the user cancelled (meaning that sessionName was not entered):
                else {
                    // the user has canceled the operation:
                    userCanceled = true;
                    // break out of the !userCanceled while loop:
                    break;
                }//end else
            }//end while

            // the program has passed the file name entry step, proceed to further steps:
            if (!userCanceled) {
                // check if the directory for storing sessions exists:
                if (checkIfSessionsFolderExists() == false)
                    // create the directory:
                    getSessionsFolderPath().mkdir(); 

                // attempt to create the session file within the session directory:
                String sessionFileCreated = createSessionFile(sessionName);
                // if the session file was created successfully:
                if ("true".equals(sessionFileCreated)) {
                    leftStatus.setText("New session file '" + sessionName.get() + "' was created.");
                    String name = constructSessionFilePath(sessionName).toString();
                    
                    // update the program's state via the session variable:
                    session.sessionLoaded(name); 
                    // write the session state to file:
                    session.saveState(); 
                    refreshListOfSessions();
                    // the save new session operation is complete; break out of the !userCanceled while loop :
                    break; 
                }//end if
                
                else {
                    // Parker (3-19-17): If the user wants to retry creating the file
                    // (which results in showRetryFileCreationPrompt returning true),
                    // then the user has NOT canceled the operation (setting userCanceled to false)
                    userCanceled = !(showRetryFileCreationPrompt(sessionFileCreated));
                }//end else
            }//end if
        }//end while
    }//end promptUserToCreateNewSessionFile
    
    /**
     * 
     * @author: parker
     * 
     * opens the File passed as a parameter. This class is called after the user picks
     * a file from the FileChooser display in the openFileAction event handler.
     * 
     * @param file the file selected by the user from the file system
     * @return whether or not the file was able to be read from completely
     */
    private Boolean openFile(File file) {
        try {
            // update the status text:
            leftStatus.setText("Opening " + file.getName() + " ...");
            // create the necessary FileInputStream and Scanner objects for reading the file:
            FileInputStream inputStream = null;
            Scanner sc = null;
            // track how many lines have been processed during the data parsing operation:
            int linesProcessed = 0;
            // attempt to open the file for reading:
            try {
                inputStream = new FileInputStream(file.getPath());
                sc = new Scanner(inputStream, "UTF-8");
                
                // start timing the file processing action:
                long start = System.currentTimeMillis(); 
                
                String extension = getFileExtension(file.toString());
                
                // process .csv data files:
                if (extension.equals("csv")) {
                    
                    // (Parker): Since the session data may contain selected mice indices from the previous session,
                    // and since there is an event listener attached to the selectedMiceListView that overwrites the
                    // session data when selectedMiceListView's selection changes, save that data in a temporary variable 
                    // since selectedMiceListView's selection will be cleared after data parsing has finished:
                    String selectedMiceIndices = session.selectedMiceIndices;
                    
                    // reset the grid object
                    grid = new Grid();
                    // reset the mice object
                    mice = new Mice();
                    
                    // (Parker) these represent the indices of specific columns of data in the data set file.
                    // Note: an enum also could have worked here, but this serves the same purpose:
                    int TIMESTAMP = 0;
                    int ID_RFID = 1;
                    int ID_LABEL = 2;
                    int UNIT_LABEL = 3;
                    int EVENT_DURATION = 4;
                    
                    // (Parker): dateRange is used for storing the first and last timestamps from the data set:
                    Date dateRange = null;
                    
                    // (Parker): in order to obtain a set of timestamp presets for use in stopDataRangeChoiceBox
                    // and startDataRangeChoiceBox, store all timestamps in the data set and later use the ArrayList
                    // to select the timestamps that represent the 0/5, 1/5, 2/5, 3/5, 4/5, and 5/5 divisions of the 
                    // data set:
                    ArrayList<Date> dataTimestampsList = new ArrayList<>();
                    
                    // (Parker): define a string format for converting Dates to Strings:
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                    
                    // (Parker 5/23/17) Since there may be a number of header lines that need to be skipped (before the parsing of valid data rows occurs),
                    // use a Boolean to keep track of when the first valid data row is encountered:
                    Boolean validDataRowRead = false;
                    
                    // while there are lines to be read in the data file:
                    //Whitney Post 5/29/17: Create a counter to keep track of the number of mice
                    int mouseCount = 0;
                    while (sc.hasNextLine()) {
                        linesProcessed++;
                         //pulls next line of input:
                        String line = sc.nextLine();
                        //splits up line using commas:
                        List<String> items = null;
                        if(line.contains(";")){
                            line.replaceAll(",", "");
                            items = Arrays.asList(line.split(";"));
                            if(!items.get(TIMESTAMP).contains("ID")){
                                Date format = sdf.parse(items.get(TIMESTAMP));
                                String newFormat = sdf.format(format);
                                items.set(TIMESTAMP, newFormat);
                            }
                        }
                        else{
                                items = Arrays.asList(line.split(",")); 
                        }
                        /*
                        Parker (3/22/17):
                        For each row of data in the data set, take each tokenized string of data
                        representing each column of data. Since there are only five columns of
                        data within the data set that are relevant (DateTime,IdRFID,IdLabel,unitLabel,eventDuration), 
                        only consider those columns. Next, check if the mice object contains a
                        mouse with the current row's IdRFID. If not, create a new mouse object,
                        add the current row's location and timestamp data to the object,
                        and add the mouse to the mice object. If the mouse is already in the
                        mice object, retreive the matching mouse from mice based on the current IdRFID
                        and add the current's rows's location and timestamp data.
                        */
                        
                        // extract the location and timestamp data for the current row:
                        MouseLocTime mlt = new MouseLocTime(items.get(TIMESTAMP), items.get(UNIT_LABEL), items.get(EVENT_DURATION));
                        
                        // (Parker 4/16/17): if the current data row has a timestamp that cannot be read, skip this data row
                        if (mlt.timestamp == null) continue;
                        
                        // (Parker 4/16/17): since the timestamp passed validation (it was not null after
                        // going through the MouseLocTime constructor), add it to the list of timestamps from the data set:
                        dataTimestampsList.add(mlt.timestamp);
                        
                        // update the dateRange variable:
                        dateRange = mlt.timestamp;
                        
                        // (Parker 5/23/17): the first MouseLocTime timestamp value that is validated as non-null should represent the first valid row of data,
                        // so prepopulate the Start field with its timestamp value:
                        if (validDataRowRead == false && mlt.timestamp != null) {
                            validDataRowRead = true;
                            // if there is no previous Start timestamp session value (if one does exist, it will be restored at a later point),
                            // proceed with prepopulating the Start TextArea:
                            if (session.stoppingIndex.equals(""))
                                startDataRangeTextArea.setText(sdf.format(dateRange));
                        }//end if
                        
                        // check if the mice object contains a mouse with the current row's IdRFID:
                        //Whitney Post 5/29/17: Create a color list for all the mice in the data set
                        final Color ColorList[] = {Color.BLUE, Color.BLACK, Color.CORAL, Color.BROWN, Color.DARKGREEN, Color.RED, Color.YELLOW, Color.DARKGREY};
                        
                        if (mice.hasMouse(items.get(ID_RFID)) == false) {
                            // if the current mouse in the data set does not have a corresponding Mouse object, create one:
                            Mouse m = new Mouse(items.get(ID_RFID), items.get(ID_LABEL));
                            // add the current row's location and timestamp info to the new mouse object:
                            m.addLocTime(mlt);
                            // add the mouse object to the mice array:
                            mice.add(m);
                            mouseCount++;
                        }//end if
                        
                        // else, the mouse of the current row already has a corresponding Mouse object;
                        // get the Mouse object by IdRFID and add the current row's location and timestamp data:
                        else
                            mice.getMouseByIdRFID(items.get(ID_RFID)).addLocTime(mlt);
                    }//end while
                    
                    // (Parker 3/26/17): Prepopulate the stop visualization option 
                    // with the timestamp from the last row processed:
                    
                    // if there is no previous session value (if one does exist, it will be restored at a later point),
                    // proceed with prepopulating the Stop TextArea:
                    if (session.stoppingIndex.equals(""))
                        stopDataRangeTextArea.setText(sdf.format(dateRange));
                    
                    /// (Parker): Create an observable list for the purpose of populating the stopDataRangeChoiceBox and
                    // startDataRangeChoiceBox options. Add to the observable list 6 timestamps from the data, representing
                    // ranges in the dataset.
                    ObservableList<String> timestampsObservableList = FXCollections.observableArrayList();
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get(0)));
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get((int)dataTimestampsList.size()/5)));
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get(((int)dataTimestampsList.size()/5)*2)));
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get(((int)dataTimestampsList.size()/5)*3)));
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get(((int)dataTimestampsList.size()/5)*4)));
                    timestampsObservableList.add(sdf.format(dataTimestampsList.get(dataTimestampsList.size()-1)));
                    
                    stopDataRangeChoiceBox.setItems(timestampsObservableList);
                    startDataRangeChoiceBox.setItems(timestampsObservableList);

                    // (Parker 3/26/17): Add the mice IdRFIDs and Labels to the visualization options mice listView:
                    
                    selectedMiceListView.getItems().clear();
                    selectedMiceListView.setItems(mice.getMouseIdsLabelsObservableList());
                    selectedMiceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    
                    // Attempt to re-select the mice selection based on indices in the session data:
                    session.selectedMiceIndices = selectedMiceIndices;
                    
                    mice.print();
                }//end if
                
                // else if the user chose a JSON file, assume it is a session file:
                else if (extension.equals("json")) {
                    String jsonData = "";
                    // read the JSON data, which should be contained on one line due to how the GSON library works:
                    while (sc.hasNextLine())
                        jsonData = sc.nextLine();
                    
                    // attempt to recreate the session from the data contained within the session file by using GSON:
                    Gson gson = new GsonBuilder().create();
                    
                    try {
                        Session loadedSession = gson.fromJson(jsonData, Session.class);
                        session = loadedSession; // replace the current session's info with the loaded session's info
                    }//end try
                    catch (JsonSyntaxException e) {
                        return false;
                    }//end catch
                }//end else if
                
                // file processing finished; calculate the time spent:
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                System.out.println("done reading file! It took " + elapsed + " milliseconds");
                System.out.println("Lines Processed = " + linesProcessed);
                leftStatus.setText("Finished opening " + file.getName() + " in " + elapsed + " milliseconds.");
                
                // an error occurred during the file data parsing operation:
                if (sc.ioException() != null) {
                    leftStatus.setText("An ioException from the Scanner object was thrown.");
                    throw sc.ioException();
                }//end if
            }//end try
            
            // finally, perform cleanup on the file reading objects:
            finally {
                if (inputStream != null)
                    inputStream.close();
                if (sc != null)
                    sc.close();
                return true;
            }//end finally
        }//end try
        catch(IOException e) {
            System.out.println(e);
            return false;
        }//end catch
    }//end openFile
    
}//end AppStageController

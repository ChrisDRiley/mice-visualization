package micevisualization;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import java.nio.file.Files;
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
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import static java.lang.System.out;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;

public class AppStageController {
    // Parker (3/19/17): access certain GUI elements from the XML:
    @FXML private Stage stage;
    @FXML private Label leftStatus;
    // @FXML private ProgressBar progressBar;
    @FXML private ListView sessionsListView;
    @FXML private Button sessionsLoadButton;
    @FXML private Button sessionsDeleteButton;
    @FXML private AnchorPane visualizationOptionsAnchorPane;
    @FXML private AnchorPane sessionsAnchorPane;
    @FXML private ChoiceBox visualizationTypeChoiceBox;
    @FXML private CustomMenuItem saveMenuItem;
    @FXML private CustomMenuItem exportMenuItem;
    @FXML private Canvas canvasBasePane;
    @FXML private StackPane viewerPane;
    @FXML private SplitPane mainSplitPane;
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
    @FXML private Button generateAnimatedMapButton;
    @FXML private TextArea currentAnimationFrame;
    @FXML private ChoiceBox stopDataRangeChoiceBox;
    @FXML private ChoiceBox startDataRangeChoiceBox;
    
    // Parker (3/2/17): the fileChooser variable can be reused throughout the
    // system's event handlers, so we create a global within the controller.
    final FileChooser fileChooser = new FileChooser();
    
    // Parker (3/17/17)
    // Create a session variable to store the state of the program:
    Session session = new Session();
    
    // Parker (3/22/17)
    // Create a mice variable to store the mice read in from the data file:
    Mice mice = new Mice();

    // (Parker 3/26/17): create a new Grid object, representing the grid and its sectors
    Grid grid = new Grid();
    
    // Parker (3/19/17): The name of the folder for storing session data in:
    final String SESSIONS_FOLDER = "\\miceVizSessions";
    
        /*
    Alex (3/27/17):
    This function uses the save button option in the GUI to export the current image
    to your computer as a .png or .jpeg file. [[Currently does everything except capturing image]]
    */
    @FXML protected void exportImage(ActionEvent event) throws IOException {

        // Creates file options
        FileChooser fc = new FileChooser();
        
        //Set extension filters for PNG and JPEG
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"),
            new FileChooser.ExtensionFilter("JPEG (*.jpeg)", "*.jpeg")
            );
        
        //Show save file dialog
        File file = fc.showSaveDialog(stage);
        
        //Creates image file to computer
        if (file != null) {  
            
            /****************************Known Bugs******************************** 
                1) Error when you try to export without generating data 
                    (currently fixed but need to throw error message instead)
                
                2) After exporting, canvas disappears. Fixed grid lines/grid numbers 
                    from disappearing; working on restoring the data to screen.
            */
            
            // Creates a group to store all the layers in
            Group group = new Group();

            // Adds grid data image to group
            //***[[1]] prevents error if you try to export image before ever generating (will properly fix later)
            if (grid.data != null)
                group.getChildren().add(grid.data);

            // Adds grid line image to group if it's been selected
            if (grid.viewerPaneGridLines != null)
                group.getChildren().add(grid.gridlines);

            // Adds grid numbers image to group if it's been selected
            if (grid.viewerPaneGridNumbers != null)
                group.getChildren().add(grid.gridnumbers);
            
            // Image Dimensions (default resolution)
            int IMG_W = 455;
            int IMG_H = 260;
            
            // (Parker 3/31/17 Attempt to get the dimensions of the visualization based on the background layer:
            Canvas viewerPaneBackground = (Canvas)viewerPane.lookup("#background");
            if (viewerPaneBackground != null) {
                IMG_W = (int) viewerPaneBackground.getWidth();
                IMG_H = (int) viewerPaneBackground.getHeight();
            }

            try {                
                // Create an image and snapshot the group to that image
                WritableImage image = new WritableImage(IMG_W, IMG_H);
                
                // For Jpeg image
                BufferedImage bi;
                
                // Pull file extension (either png or jpeg)
                String ext = getFileExtension(file.toString());
                
                // Captures GUI image to export
                group.snapshot(null,image);
                
                // If user wants jpeg extension, need to fix jpeg background
                if(ext.equals("jpeg")) {
                    
                    // Using bufferedImage prevents background from being a distorted orange color.
                    bi = SwingFXUtils.fromFXImage(image, null);
                    BufferedImage jpeg = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_RGB);
                    jpeg.getGraphics().drawImage(bi, 0, 0, null);
                    
                    // write the image to users computer
                    ImageIO.write(jpeg, ext, file);
                }//end if
                else
                     // write the image to users computer
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), ext, file);
                
                //***[[2]] Prevents grid lines/numbers from disappearing. See bug #2 above for additional info (line 155).
                drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
                
                //Gets filepath of image saved
                String path = file.getAbsolutePath();
       
                //Prints out alert message saying it was successful.
                //AlertTypes -> CONFIRMATION    ERROR   INFORMATION   WARNING
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Program Notification");
                alert.setHeaderText("Image saved to " + path);
                alert.showAndWait();

                }//end try
                catch (IOException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }//end catch
        }//end if
    }//end exportImage
    
    
    
    /**
     * 
     * @author: parker
     * 
     * this is called once after the controller is loaded. It's purpose is to perform
     * any code at the very beginning of program execution, once the GUI is ready.
     * 
     * @throws URISyntaxException 
     */
    @FXML
    private void initialize() throws URISyntaxException {
        //fileMenuOption.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        
        refreshListOfSessions();
        
        // (Parker 3/26/17): When the user resizes the window, trigger a redraw of the Canvas objects
        viewerPane.widthProperty().addListener(new ChangeListener<Number>() {
           @Override
           public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
           {
               // if the session is not a new session, then there is data available to draw:
               if (session.isNewSession == false) {
                   drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
               }
               // if the user resizes the window during an animation, cancel the animation:
               if (grid.animationCancelled == false) {
                   grid.stopAnimation(generateButton);
               }
               
           }           
        });
        
        // (Parker 3/26/17): When the user resizes the window, trigger a redraw of the Canvas objects
        viewerPane.heightProperty().addListener(new ChangeListener<Number>() {
           @Override
           public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
           {
               // if the session is not a new session, then there is data available to draw:
               if (session.isNewSession == false) {
                   drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
               }
               // if the user resizes the window during an animation, cancel the animation:
               if (grid.animationCancelled == false) {
                   grid.stopAnimation(generateButton);
               }
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
                    if (grid.animationCancelled == false) {
                        grid.stopAnimation(generateButton);
                    }
                    
                    animationOptionsVBox.setDisable(true);
                    //generateButton.setDisable(false);
                    generateButton.setText("Generate Static Map");
                    generateButton.setGraphic(null);
                }
                else if (newValue.equals("Animated")) {
                    grid.stopAnimation(generateButton);
                    animationOptionsVBox.setDisable(false);
                    //generateButton.setDisable(true);
                }
                try {
                    session.visualizationType = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
         // (Parker 3/27/17): When the user changes their selection in the map type choice box,
        // respond to that change       
        mapTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // if the user changes a visualization option during an animation, cancel the animation:
                if (grid.animationCancelled == false) {
                    grid.stopAnimation(generateButton);
                }
                try {
                    session.mapType = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

         // (Parker 3/27/17): When the user changes their selection in the Start data range text field,
        // respond to that change          
        startDataRangeTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    session.startingIndex = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // (Parker 3/27/17): When the user changes their selection in the Stop data range text field,
        // respond to that change       
        stopDataRangeTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    session.stoppingIndex = newValue;
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // (Parker 3/27/17): When the user changes their selection in the toggle grid lines check box,
        // respond to that change  
        showGridLinesCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean old_val, Boolean new_val) {
                    grid.toggleGridLines(viewerPane);
                    session.showGridLines = new_val;
                try {
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // (Parker 3/27/17): When the user changes their selection in the toggle grid sector numbers checkbox,
        // respond to that change  
        showGridNumbersCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean old_val, Boolean new_val) {
                    grid.toggleGridNumbers(viewerPane);
                    session.showGridNumbers = new_val;
                try {
                    session.saveState();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AppStageController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        // (Parker): when the user selects a timestamp from the Start index dropdown, replace the content of the
        // Start textArea with the user selection
        startDataRangeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                startDataRangeTextArea.setText(newValue);
            }
        });
        
        // (Parker): when the user selects a timestamp from the Stop index dropdown, replace the content of the
        // Stop textArea with the user selection
        stopDataRangeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                stopDataRangeTextArea.setText(newValue);
            }
        });
        
        // (Parker 4/3/17): initialize certain GUI components with certain settings:
        visualizationOptionsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        visualizationTypeChoiceBox.getSelectionModel().selectFirst();
        mapTypeChoiceBox.getSelectionModel().selectFirst();
    }
    
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
        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
    }
    
    /**
     * 
     * @author: parker
     * 
     * restores the state of the program by setting GUI control values equal to their
     * values contained within the session object. This function is called after
     * a user successfully loads a session json file.
     * 
     * @param s the session object.
     */
    public void restoreState(Session s) {
        visualizationTypeChoiceBox.getSelectionModel().select(s.visualizationType);
        mapTypeChoiceBox.getSelectionModel().select(s.mapType);
        
        showGridLinesCheckBox.selectedProperty().set(s.showGridLines);
        showGridNumbersCheckBox.selectedProperty().set(s.showGridNumbers);
        
        startDataRangeTextArea.setText(s.startingIndex);
        stopDataRangeTextArea.setText(s.stoppingIndex);
    }
    
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
    }
    
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
        
    }
    
    /**
     * @author: parker
     * 
     * resets the session variable and GUI to default.
     */
    public void resetToDefaultState() {
        session = new Session();
        visualizationTypeChoiceBox.getSelectionModel().clearSelection();
        lockVisualizationOptions();
    }
    
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
                    if (file.isFile()) {
                        sessionFiles.add(file.getName());
                    }
                }
                //if there were no files in the sessions folder, disable the sessions manager pane:
                if (sessionFiles.size() > 0) {
                    sessionsAnchorPane.setDisable(false);
                }
                // if at least one sessions file exists in the sessions folder, enable the sessions manager pane:
                else {
                    sessionsAnchorPane.setDisable(true);
                }
                // update the contents of the recent sessions list to reflect the files in the sessions folder:
                sessionsListView.setItems(sessionFiles);
            }
        }
    }

    // track the number of times the openFile file explorer has been configured (ensure it is configured
    // only once after the system begins execution for the current session):
    int timesConfigured = 0;
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
        // prevent the file chooser from being configured multiple times
        if (timesConfigured == 0) {
            configureFileChooser(fileChooser);
            timesConfigured++;
        }
        // show the file explorer window:
        File fileToOpen = fileChooser.showOpenDialog(stage); 
        // check if the user selected a file:
        if (fileToOpen != null) {
            // get the extension of the user's selected file:
            String extension = getFileExtension(fileToOpen.toString()); 
            // if the file selected has an extension of CSV, assume it is a data set file:
            if (extension.equals("csv")) { 
                
                // assume that the user is opening a new data set file, so disregard any previously saved
                // starting and stopping indices associated with the current session's previous data set:
                session.stoppingIndex = "";
                session.startingIndex = "";
                
                // attempt to open the file and proceed if successful:
                if (openFile(fileToOpen)) {
                    // update the program's state via the session variable:
                    session.dataSetFileLoaded(fileToOpen.getPath()); 
                    // update the state of the GUI to enable visualization options:
                    unlockVisualizationOptions(); 
                    // Check if the current session is new:
                    if (session.isNewSession) {
                        // prompt the user to create a new session file:
                        promptUserToCreateNewSessionFile(session);
                    }
                }  
            }
            // if the user selected a file with an extension of type JSON, assume it is a session file:
            else if (extension.equals("json")) {
                // attempt to load the session file and restore its state to the current system session:
                loadSessionFile(fileToOpen);
            }
            // else the user attempted to open a file with an invalid extension, show an alert dialog:
            else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Program Notification");
                alert.setHeaderText("Invalid file type");
                alert.setContentText("Allowable file types are:\n\n.csv data files\n.json session files");
                alert.showAndWait();
            }
        }
    }
    
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
                    if (!deleteFile.delete()) {
                        simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                    }
                    else {
                        leftStatus.setText("Session file successfully deleted.");
                        resetToDefaultState(); // create a new Session object and reset the GUI
                    }
                }
            }
            else { // delete a file that is not associated with the current session::
                File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
                if (!deleteFile.delete()) {
                    simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                }
                else leftStatus.setText("Session file successfully deleted.");
            }
            // update the list of sessions appearing in the recent sessions list after an attempted delete operation:
            refreshListOfSessions();
        }
    }
    
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
            if (sessionFile.exists()) {
                // load the session file:
                loadSessionFile(sessionFile);
            }
        } else {
            simpleAlert("Please select a session filename from the list of sessions.", null);
        }
    }
    
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
        }
        catch (ParseException pe) {
            simpleAlert("Invalid starting index", "Please ensure the value entered into the Start field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return false;
        }
        
        try {
            // attempt to coerce the content of the Stop text area into the required format:
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            stopIndex = formatter.parse(stopDataRangeTextArea.getText());
        }
        catch (ParseException pe) {
            simpleAlert("Invalid stopping index", "Please ensure the value entered into the Stop field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return false;
        }
        
        // (Parker 4/1/17): Check that the Start and Stop values are in range:
        if (startIndex.compareTo(stopIndex) > 0) {
            simpleAlert("Out-of-bounds starting index", "The starting index in the Start field must be less than or equal to the stopping index in the Stop field.");
            return false;
        }
        else if (stopIndex.compareTo(startIndex) < 0) {
            simpleAlert("Out-of-bounds stopping index", "The stopping index in the Stop field must be greater than or equal to the starting index in the Start field.");
            return false;            
        }
        
        // (Parker 4/1/17): get the mice that the user has selected in the mice list of the Visualization Options:
        ObservableList<String> selectedMiceIds = selectedMiceListView.getSelectionModel().getSelectedItems();
        // create a Mouse array of selected Mice based on the String Ids from the selected mice list:
        ArrayList<Mouse> selectedMice = mice.getMicebyIdsLabels(selectedMiceIds);
        if (selectedMice == null) {
            simpleAlert("No mice selected!", "Please select at least one mouse to visualize.");
            return false;
        }
        // if there were no validation errors, return true:
        return true;
    }
    
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
    @FXML protected void generateMapAction(ActionEvent event) throws InterruptedException {
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

        // (Parker 3/27/17): Check to ensure the value of the visualizationTypeChoiceBox is not null:
        if (visualizationTypeChoiceBox.getValue() != null) {
            if (visualizationTypeChoiceBox.getValue().toString().equals("Static")) {
                
                // (Parker 3/27/17): Check to ensure the value of the mapTypeChoiceBox is not null:
                if (mapTypeChoiceBox.getValue() != null) {
                    
                    // reset the visual content of the grid before generating the map:
                    grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
                    // begin a timer to record the amount of time the generation takes
                    long start = System.currentTimeMillis();
                    
                    if (mapTypeChoiceBox.getValue().toString().equals("Heat")) {
                        grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex); 
                    }
                    else if (mapTypeChoiceBox.getValue().toString().equals("Vector")) {
                        grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                    }
                    else if (mapTypeChoiceBox.getValue().toString().equals("Overlay")) {
                        // in order to create a static overlay of both heat an vector maps, simply call each static mapping method:
                        grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex);
                        grid.staticVectorMap(viewerPane, selectedMice, startIndex, stopIndex);
                    }
                    
                    // stop the timer:
                    long end = System.currentTimeMillis();
                    // get the elapsed time of the generation duration:
                    long elapsed = end - start;
                    // output a summary of the map generation duration to the user as a text status:
                    String describeMice = (selectedMice.size() > 1) ? "mice" : "mouse";
                    String visualizationType = visualizationTypeChoiceBox.getValue().toString();
                    String mapType = mapTypeChoiceBox.getValue().toString();
                    leftStatus.setText("Finished generating a " + visualizationType + " " + mapType + " map of " + selectedMice.size() + " " + describeMice + " in " + elapsed + " milliseconds.");
                }
                else {
                    simpleAlert("No map type selected!", "Please select a map type from the dropdown.");
                    return;
                }
            }
            else if (visualizationTypeChoiceBox.getValue().toString().equals("Animated")) {
                // (Parker 3/27/17): Check to ensure the value of the mapTypeChoiceBox is not null:
                if (mapTypeChoiceBox.getValue() != null) {
                    // if there is a current animation running and it has not been cancelled,
                    // stop the animation
                    if (grid.animationCancelled == false) {
                        grid.stopAnimation(generateButton);
                    }
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

                        if (mapTypeChoiceBox.getValue().toString().equals("Heat")) {
                            grid.animatedHeatMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue());
                        }
                        else if (mapTypeChoiceBox.getValue().toString().equals("Vector")) {
                            grid.animatedVectorMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue());
                        }
                        else if (mapTypeChoiceBox.getValue().toString().equals("Overlay")) {
                            grid.animatedOverlayMap(viewerPane, generateButton, currentAnimationFrame, leftStatus, selectedMice, startIndex, stopIndex, animationSpeedSlider.getValue());
                        }
                    }
                }
                else {
                    simpleAlert("No map type selected!", "Please select a map type from the dropdown.");
                    return;
                }
            }
        }
        else {
            simpleAlert("No visualization type selected!", "Please select a visualization type from the dropdown.");
            return;           
        }
    }
    
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
        if (session.isNewSession) { 
            // prompt the user to create a new session file:
            promptUserToCreateNewSessionFile(session);
        }
        // if the session is not new (already associated with a session file), save 
        // the current state of the session and alert the user via a dialog window:
        else {
            session.saveState();
            simpleAlert("File saved!", "The session data was saved to disk.");
        }
    }
   
    /**
     * 
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
       if (session.isNewSession == false) {
           // save the session's state before exiting:
           session.saveState();
       }
       Platform.exit();
    }
    
    /**
     * 
     * @author: parker
     * 
     * debugAlert is an easy way to show a popup containing a string of info (DEBUGGING ONLY)
     * 
     * @param info a string of information to display in the popup dialog
     */
    public void debugAlert(String info) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Debug Alert");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }
    
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
        //Image image = new Image(getClass().getResourceAsStream("checkmark.jpg"));
        //alert.setGraphic(new ImageView(image));
    }
    
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
    }
    
    /**
     * 
     * @author: parker
     * 
     * loads a json session file, updates the program GUI, and restores the state
     * of the program contained within the json file.
     * 
     * @param sessionFile the session file to load
     * @return true if session file was loaded, false if otherwise
     */
    public Boolean loadSessionFile(File sessionFile) throws FileNotFoundException {
        if (openFile(sessionFile)) {
            // if the selected session file contains a different session filename than the one listed in the 
            // currentSessionFilePath property of the session object, update the session object:
            if (session.currentSessionFilePath != sessionFile.getPath().toString()) {
                session.currentSessionFilePath = sessionFile.getPath().toString();
            }
            // create a File object based on the filepath string of the current data set file:
            File dataFile = new File(session.currentDataSetFilePath);
            // attempt to parse the data set file associated with the session file being loaded:
            if (openFile(dataFile)) {
                // update the program's state via the session variable:
                session.sessionLoaded(sessionFile.getPath().toString());
                // write the session state to file:
                session.saveState();
                // update the System's GUI to enable the visualization options:
                unlockVisualizationOptions();
                // draw the basic appearance of the grid in the viewer pane:
                drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
                // restore the user's GUI control settings from the loaded session file:
                restoreState(session);
                // give the user a status update via the lower left text Label
                leftStatus.setText("Session file loaded.");
                return true;
            } 
        }
        leftStatus.setText("Error: The session file could not be loaded.");
        return false;
    }
    
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
            if (!Character.isDigit(c) && !Character.isAlphabetic(c) && c != '-' && c != '_' && c != ' ' && c != '.') {
                return false;
            }
        }
        // return true if the string passed the validation check
        return true;
    }
    
    
    public String getFileExtension(String name) {
        int i = name.lastIndexOf('.');
        String ext;
        if (i > 0) {
            ext = name.substring(i+1);
            return ext;
        }
        return null;
    }
    
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
    }
    
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
        // Parker (3-19-17): get the path of the folder containing the currently executing .jar file: 
        URL folderContainingJar = getClass().getProtectionDomain().getCodeSource().getLocation();
        // Parker (3-19-17): get the above path without the filename:
        Path folderContainingJarPath = Paths.get(folderContainingJar.toURI()).getParent();
        // Parker (3-19-17): create the path of the sessions folder, using the above path:
        String sessionsFolderPath = folderContainingJarPath.toString() + SESSIONS_FOLDER;
        // Parker (3-19-17): create a File object of the sessionsFolderPath for checking existence:
        File miceVizSessionsFolder = new File(sessionsFolderPath);
        return miceVizSessionsFolder;
    }
    
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
        if (miceVizSessionsFolder.isDirectory()) {
           return true;
        }
        else {
            return false;
        }
    }
    
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
    }
    
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
            }
            else {
                return "Could not create the session file.";
            }
        }
        else {
            return "Session file already exists.";
        }
    }

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
        if (retryFileCreationAnswer.get() == ButtonType.OK){
            return true;
        }
        else {
            return false;
        }
    }
    
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
        Boolean isValidFileName = false;
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
                    if (!isFileNameValid(sessionName.get())) { 
                        // alert the user the file name is invalid:
                        showInvalidFileNameWarning(); 
                    }
                    else {
                        // valid file name; break out of the !isValidFileName while loop:
                        isValidFileName = true;
                    }
                }
                // else the user cancelled (meaning that sessionName was not entered):
                else {
                    // the user has canceled the operation:
                    userCanceled = true;
                    // break out of the !userCanceled while loop:
                    break;
                }
            }

            // the program has passed the file name entry step, proceed to further steps:
            if (!userCanceled) {
                // check if the directory for storing sessions exists:
                if (checkIfSessionsFolderExists() == false) {
                    // create the directory:
                    getSessionsFolderPath().mkdir(); 
                }

                // attempt to create the session file within the session directory:
                String sessionFileCreated = createSessionFile(sessionName);
                // if the session file was created successfully:
                if (sessionFileCreated == "true") {
                    leftStatus.setText("New session file '" + sessionName.get() + "' was created.");
                    String name = constructSessionFilePath(sessionName).toString();
                    // update the program's state via the session variable:
                    session.sessionLoaded(name); 
                    // write the session state to file:
                    session.saveState(); 
                    refreshListOfSessions();
                    // the save new session operation is complete; break out of the !userCanceled while loop :
                    break; 
                }
                else {
                    // Parker (3-19-17): If the user wants to retry creating the file
                    // (which results in showRetryFileCreationPrompt returning true),
                    // then the user has NOT canceled the operation (setting userCanceled to false)
                    userCanceled = !(showRetryFileCreationPrompt(sessionFileCreated));
                }
            }
        }
    }
    
    /**
     * 
     * @author: parker
     * 
     * configures the file system viewer to have a specific title, initial directory, and accepted file types.
     * This function is called using an instantiated FileChooser object as its parameter.
     * 
     * @param fileChooser the fileChooser object to configure
     */
    private static void configureFileChooser(
    final FileChooser fileChooser) {
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
    }
    
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
                    ArrayList<Date> dataTimestampsList = new ArrayList<Date>();
                    
                    // (Parker): define a string format for converting Dates to Strings:
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
                    
                    while (sc.hasNextLine()) {
                        linesProcessed++;
                         //pulls next line of input:
                        String line = sc.nextLine();
                        //splits up line using commas:
                        List<String> items = Arrays.asList(line.split(",")); 
                        
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
                        
                        // skip the header line:
                        if (linesProcessed == 1) continue; 
                        
                        // extract the location and timestamp data for the current row:
                        MouseLocTime mlt = new MouseLocTime(items.get(TIMESTAMP), items.get(UNIT_LABEL), items.get(EVENT_DURATION));
                        
                        dataTimestampsList.add(mlt.timestamp);
                        
                        // update the dateRange variable:
                        dateRange = mlt.timestamp;
                        // the 2nd line processed should be the first row of data,
                        // so prepopulate the Start field with this date:
                        if (linesProcessed == 2) {
                            // if there is no previous session value (if one does exist, it will be restored at a later point),
                            // proceed with prepopulating the Start TextArea:
                            if (session.stoppingIndex.equals("")) {
                                startDataRangeTextArea.setText(sdf.format(dateRange));
                            }
                        }
                        
                        // check if the mice object contains a mouse with the current row's IdRFID:
                        if (mice.hasMouse(items.get(ID_RFID)) == false) {
                            // if the current mouse in the data set does not have a corresponding Mouse object, create one:
                            Mouse m = new Mouse(items.get(ID_RFID), items.get(ID_LABEL));
                            // add the current row's location and timestamp info to the new mouse object:
                            m.addLocTime(mlt);
                            // add the mouse object to the mice array:
                            mice.add(m);
                        }
                        // else, the mouse of the current row already has a corresponding Mouse object;
                        // get the Mouse object by IdRFID and add the current row's location and timestamp data:
                        else {
                            mice.getMouseByIdRFID(items.get(ID_RFID)).addLocTime(mlt);
                        }
                    }
                    // (Parker 3/26/17): Prepopulate the stop visualization option 
                    // with the timestamp from the last row processed:
                    
                    // if there is no previous session value (if one does exist, it will be restored at a later point),
                    // proceed with prepopulating the Stop TextArea:
                    if (session.stoppingIndex.equals("")) {
                        stopDataRangeTextArea.setText(sdf.format(dateRange));
                    }
                    
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
                    selectedMiceListView.setItems(mice.getMouseIdsLabelsObservableList());
                    selectedMiceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    mice.print();
                }
                // else if the user chose a JSON file, assume it is a session file:
                else if (extension.equals("json")) {
                    String jsonData = "";
                    // read the JSON data, which should be contained on one line due to how the GSON library works:
                    while (sc.hasNextLine()) {
                        jsonData = sc.nextLine();
                    }
                    // attempt to recreate the session from the data contained within the session file by using GSON:
                    Gson gson = new GsonBuilder().create();
                    try {
                        Session loadedSession = gson.fromJson(jsonData, Session.class);
                        session = loadedSession; // replace the current session's info with the loaded session's info
                    }
                    catch (Exception e) {
                        return false;
                    }
                }
                
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
                }
            }
            // finally, perform cleanup on the file reading objects:
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
                return true;
            }
        } 
        catch(Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
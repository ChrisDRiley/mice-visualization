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
import javafx.scene.SnapshotParameters;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
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
    @FXML protected void exportImage(ActionEvent event) {
        
        //Image dimensions (fits map perfectly -> can be 455/260 but cuts off a tiny bit of map)
        final int IMG_W = 457;
        final int IMG_H = 262;

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
            try {
                
                //Writes the image
                WritableImage image = new WritableImage(IMG_W, IMG_H);
                
                //Snapshot of what you're saving [[currently wrong]]
                //can save each layer by itself as image, just need to combine them!
                //Pick one to save to your computer, comment others (otherwise it overwrites above)
                
                grid.data.snapshot(null, image); //saves the data to file
                //grid.gridlines.snapshot(null, image); //saves gridlines to file
                //grid.gridnumbers.snapshot(null, image); //saves gridnumbers to file
                
                //NEED TO COMBINE INTO ONE -> only 1 can be active else it gets overwritten
  
                //Renders image using swingFX
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(image, null);
                
                //Picks extension chosen to save as
                String extension = getFileExtension(file.toString());
                
                //Writes file to your specified location
                ImageIO.write(renderedImage, extension, file);
            } catch (IOException ex) {
                //empty for now
            }//end catch
        }//end if
//        
        //Future error checking, just ignore for now
//        Alert alert = new Alert(AlertType.WARNING);
//        alert.setTitle("Program Notification");
//        alert.setHeaderText("Feature coming soon!");
//        alert.showAndWait();
    }
    
    
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
               if (session.isNewSession == false) {
                   drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
               }
           }           
        });
        
        // (Parker 3/26/17): When the user resizes the window, trigger a redraw of the Canvas objects
        viewerPane.heightProperty().addListener(new ChangeListener<Number>() {
           @Override
           public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
           {
               if (session.isNewSession == false) {
                   drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
               }
           }           
        });
        
        // (Parker 3/26/17): When the user changes their selection in the visualization type choice box,
        // respond to that change
        visualizationTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
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
        
        //selectedMiceListView
        
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
        if (checkIfSessionsFolderExists()) {
            ObservableList<String> sessionFiles = FXCollections.observableArrayList();
            File[] files = getSessionsFolderPath().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        sessionFiles.add(file.getName());
                    }
                }
                if (sessionFiles.size() > 0) {
                    sessionsAnchorPane.setDisable(false);
                }
                else {
                    sessionsAnchorPane.setDisable(true);
                }
                sessionsListView.setItems(sessionFiles);
            }
        }
    }

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
        File fileToOpen = fileChooser.showOpenDialog(stage);
        if (fileToOpen != null) { 
            String extension = getFileExtension(fileToOpen.toString());
            if (extension.equals("csv")) {
                if (openFile(fileToOpen)) {
                    session.dataSetFileLoaded(fileToOpen.getPath()); // update the program's state via the session variable
                    unlockVisualizationOptions();
                    if (session.isNewSession) { // Check if the current session is new
                        promptUserToCreateNewSessionFile(session); // prompt the user to create a new session file
                    }
                }  
            }
            else if (extension.equals("json")) {
                loadSessionFile(fileToOpen);
            }
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
        String currentListViewItem = sessionsListView.getSelectionModel().getSelectedItem().toString();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete session file?");
        alert.setHeaderText("You are about to delete the session file '" + currentListViewItem + "'.\n (Data set files will not be deleted)");
        alert.setContentText("Do you want to continue?");

        Optional<ButtonType> retryFileCreationAnswer = alert.showAndWait();
        if (retryFileCreationAnswer.get() == ButtonType.OK) { // check if the file the user is trying to delete is the current session file:
            int i = session.currentSessionFilePath.lastIndexOf("\\"); // get the filename from the currentSessionFilePath
            if (currentListViewItem.equals(session.currentSessionFilePath.substring(i + 1))) { // compare the filename to the selected session name
                alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Warning! You are trying to delete the current session.");
                alert.setHeaderText("If you delete the current session file, then the current session will be reset to default.");
                alert.setContentText("Do you want to continue?");
                
                retryFileCreationAnswer = alert.showAndWait();
                if (retryFileCreationAnswer.get() == ButtonType.OK) { // delete the current session file:
                    File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
                    if (!deleteFile.delete()) {
                        simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                    }
                    else {
                        leftStatus.setText("Session file successfully deleted.");
                        resetToDefaultState(); // create a new Session object and reset the GUI
                    }
                }
            }
            else { // delete the file and maintain the current session:
                File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
                if (!deleteFile.delete()) {
                    simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
                }
                else leftStatus.setText("Session file successfully deleted.");
            }
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
        if (sessionsListView.getSelectionModel().getSelectedItem() != null) { // if there was a session name selected
            String selectedSession = sessionsListView.getSelectionModel().getSelectedItem().toString(); // get the selected name from the Recent sessions listView
            int i = selectedSession.lastIndexOf('.'); // we need to trim the extension off the filename, so get the index of the last period
            selectedSession = selectedSession.substring(0, i); // trim the extension off of the selectedSession string
            Optional<String> sessionName = Optional.of(selectedSession); // create an optional for use with constructSessionFilePath()
            File sessionFile = constructSessionFilePath(sessionName); // recreate the path to the session file, which should reside in the sessions folder
            if (sessionFile.exists()) {
                loadSessionFile(sessionFile); // load the session file
            }
        } else {
            simpleAlert("Please select a session filename from the list of sessions.", null);
        }
    }
    
    @FXML protected void generateButtonAction(ActionEvent event) {
        Date startIndex;
        Date stopIndex;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            startIndex = formatter.parse(startDataRangeTextArea.getText());
        }
        catch (ParseException pe) {
            simpleAlert("Invalid starting index", "Please ensure the value entered into the Start field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return;
        }
        
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            stopIndex = formatter.parse(stopDataRangeTextArea.getText());
        }
        catch (ParseException pe) {
            simpleAlert("Invalid stopping index", "Please ensure the value entered into the Stop field is a Date in the format: MM/dd/yyyy HH:mm:ss.SSS");
            return;
        }
        
        if (startIndex.compareTo(stopIndex) > 0) {
            simpleAlert("Out-of-bounds starting index", "The starting index in the Start field must be less than or equal to the stopping index in the Stop field.");
            return;
        }
        else if (stopIndex.compareTo(startIndex) < 0) {
            simpleAlert("Out-of-bounds stopping index", "The stopping index in the Stop field must be greater than or equal to the starting index in the Start field.");
            return;            
        }
        
        ObservableList<String> selectedMiceIds = selectedMiceListView.getSelectionModel().getSelectedItems();
        ArrayList<Mouse> selectedMice = mice.getMicebyIdsLabels(selectedMiceIds);
        if (selectedMice == null) {
            simpleAlert("No mice selected!", "Please select at least one mouse to visualize.");
            return;
        }
        
        // (Parker 3/27/17): Check to ensure the value of the visualizationTypeChoiceBox is not null:
        if (visualizationTypeChoiceBox.getValue() != null) {
            if (visualizationTypeChoiceBox.getValue().toString().equals("Static")) {
                // (Parker 3/27/17): Check to ensure the value of the mapTypeChoiceBox is not null:
                if (mapTypeChoiceBox.getValue() != null) {
                    if (mapTypeChoiceBox.getValue().toString().equals("Heat Map")) {
                        grid.redraw(viewerPane, showGridNumbersCheckBox.isSelected(), showGridLinesCheckBox.isSelected(), false);
                        
                        long start = System.currentTimeMillis(); // begin a timer to record the amount of time the generation takes
                        
                        grid.staticHeatMap(viewerPane, selectedMice, startIndex, stopIndex); // generate the static heat map
                        
                        long end = System.currentTimeMillis(); // stop the timer
                        long elapsed = end - start; // get the elapsed time of the generation duration
                        String describeMice = (selectedMice.size() > 1) ? "mice" : "mouse";
                        leftStatus.setText("Finished generating a static heat map of " + selectedMice.size() + " " + describeMice + " in " + elapsed + " milliseconds.");
                    }
                    else if (mapTypeChoiceBox.getValue().toString().equals("Vector Map")) {

                    }
                    else if (mapTypeChoiceBox.getValue().toString().equals("Overlay Map")) {

                    }
                }
                else {
                    simpleAlert("No map type selected!", "Please select a map type from the dropdown.");
                    return;
                }
            }
            else if (visualizationTypeChoiceBox.getValue().toString().equals("Animated")) {

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
        if (session.isNewSession) { // Check if the current session is new
            promptUserToCreateNewSessionFile(session); // prompt the user to create a new session file
        }
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
        // to save the currently loaded data set file within a new
        // session:
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
            File dataFile = new File(session.currentDataSetFilePath);
            if (openFile(dataFile)) {
                session.sessionLoaded(sessionFile.getPath().toString()); // update the program's state via the session variable
                session.saveState(); // write the session state to file
                unlockVisualizationOptions();
                drawCanvas(viewerPane.getWidth(), viewerPane.getHeight());
                restoreState(session);
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
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (!Character.isDigit(c) && !Character.isAlphabetic(c) && c != '-' && c != '_' && c != ' ' && c != '.') {
                return false;
            }
        }
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
        File miceVizSessionsFolder = getSessionsFolderPath();
        // Parker (3-19-17): Now check to see if there is an existing
        // sessions folder in the same directory as the program executable file:
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
     * creates a session file path based on a filename and the sessions folder.
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
                //debugAlert("Session file created!");
                simpleAlert("Session file created!", "File location:\n\n" + newSessionFile.toString());
                return "true";
            }
            else {
                //debugAlert("Could not create the session file.");
                return "Could not create the session file.";
            }
        }
        else {
            //debugAlert("Session file already exists.");
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
        Optional<String> sessionName = null; // store the session name entered by the user during the prompt
        Boolean isValidFileName = false;
        Boolean userCanceled = false; // store if the user has canceled the new session file creation operation

        while (!userCanceled) { // loop while the user has not cancelled the operation
            isValidFileName = false;

            while (!isValidFileName) { // loop while the user has not entered a valid session file name
                userCanceled = false;

                TextInputDialog dialog = showSessionFilePrompt(); // prompt the user to save the current session as a file
                sessionName = dialog.showAndWait(); // show the dialog and await the user's response

                if (sessionName.isPresent()) { // if the user submitted a file name:
                    if (!isFileNameValid(sessionName.get())) { // if the file name entered is invalid
                        showInvalidFileNameWarning(); // alert the user the file name is invalid
                    }
                    else {
                        isValidFileName = true; // valid file name; break out of the !isValidFileName while loop
                    }
                }
                else { // the user cancelled (meaning that sessionName was not entered)
                    userCanceled = true; // the user has canceled the operation
                    break; // break out of the !userCanceled while loop
                }
            }

            if (!userCanceled) { // the program has passed the file name entry step, proceed to further steps
                if (checkIfSessionsFolderExists()) { // check if the directory for storing sessions exists
                    //debugAlert("Dir exists"); 
                }
                else { // if the directory for storing sessions does not exist
                    //debugAlert("Dir does not exist, creating dir ...");
                    getSessionsFolderPath().mkdir(); // create the directory
                }

                String sessionFileCreated = createSessionFile(sessionName); // attempt to create the session file within the session directory
                if (sessionFileCreated == "true") { // if the session file was created successfully
                    leftStatus.setText("New session file '" + sessionName.get() + "' was created.");
                    String name = constructSessionFilePath(sessionName).toString();
                    session.sessionLoaded(name); // update the program's state via the session variable
                    session.saveState(); // write the session state to file
                    refreshListOfSessions();
                    break; // save new session operation is complete; break out of the !userCanceled while loop 
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
            leftStatus.setText("Opening " + file.getName() + " ...");
            FileInputStream inputStream = null;
            Scanner sc = null;
            int linesProcessed = 0;
            try {
                inputStream = new FileInputStream(file.getPath());
                sc = new Scanner(inputStream, "UTF-8");
                
                long start = System.currentTimeMillis(); // start timing the file processing action
                
                String extension = getFileExtension(file.toString());
                if (extension.equals("csv")) { // process .csv data files:
                    
                    // (Parker) these represent the indices of specific columns of data in the data set file.
                    // Note: an enum also could have worked here, but this serves the same purpose:
                    int TIMESTAMP = 0;
                    int ID_RFID = 1;
                    int ID_LABEL = 2;
                    int UNIT_LABEL = 3;
                    int EVENT_DURATION = 4;
                    
                    Date dateRange = null;
                    
                    while (sc.hasNextLine()) {
                        linesProcessed++;
                        String line = sc.nextLine(); //pulls next line of input
                        //System.out.println(line); //testing purposes, prints out line
                        List<String> items = Arrays.asList(line.split(",")); //splits up line using commas
                        
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
                        
                        if (linesProcessed == 1) continue; // skip the header line
                        
                        // extract the location and timestamp data for the current row:
                        MouseLocTime mlt = new MouseLocTime(items.get(TIMESTAMP), items.get(UNIT_LABEL), items.get(EVENT_DURATION));
                        
                        // update the dateRange variable:
                        dateRange = mlt.timestamp;
                        // the 2nd line processed should be the first row of data,
                        // so prepopulate the Start field with this date
                        if (linesProcessed == 2) {
                            startDataRangeTextArea.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(dateRange));
                        }
                        
                        // check if the mice object contains a mouse with the current row's IdRFID:
                        if (mice.hasMouse(items.get(ID_RFID)) == false) {
                            Mouse m = new Mouse(items.get(ID_RFID), items.get(ID_LABEL));
                            m.addLocTime(mlt);
                            mice.add(m);
                        }
                        else {
                            mice.getMouseByIdRFID(items.get(ID_RFID)).addLocTime(mlt);
                        }
                    }
                    // (Parker 3/26/17): Prepopulate the stop visualization option 
                    // with the timestamp from the last row processed:
                    stopDataRangeTextArea.setText(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS").format(dateRange));

                    // (Parker 3/26/17): Add the mice IdRFIDs and Labels to the visualization options mice listView:
                    selectedMiceListView.setItems(mice.getMouseIdsLabelsObservableList());
                    selectedMiceListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    mice.print();
                }
                else if (extension.equals("json")) { // process .json session files:
                    String jsonData = "";
                    while (sc.hasNextLine()) {
                        jsonData = sc.nextLine();
                    }
                    Gson gson = new GsonBuilder().create();
                    try {
                        Session loadedSession = gson.fromJson(jsonData, Session.class);
                        session = loadedSession; // replace the current session's info with the loaded session's info
                    }
                    catch (Exception e) {
                        return false;
                    }
                }
                
                long end = System.currentTimeMillis(); // file processing finished; calculate the time spent
                long elapsed = end - start;
                System.out.println("done reading file! It took " + elapsed + " milliseconds");
                System.out.println("Lines Processed = " + linesProcessed);
                leftStatus.setText("Finished opening " + file.getName() + " in " + elapsed + " milliseconds.");
                
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    leftStatus.setText("An ioException from the Scanner object was thrown.");
                    throw sc.ioException();
                }
            } 
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
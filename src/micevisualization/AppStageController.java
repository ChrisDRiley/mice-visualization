package micevisualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintStream;
import static java.lang.System.out;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;



public class AppStageController {
    // Parker (3/19/17): access certain GUI elements from the XML:
    @FXML private Stage stage;
    @FXML private Label leftStatus;
    @FXML private ProgressBar progressBar;
    @FXML private ListView sessionsListView;
    @FXML private Button sessionsLoadButton;
    @FXML private Button sessionsDeleteButton;
    
    // Parker (3/2/17): the fileChooser variable can be reused throughout the
    // system's event handlers, so we create a global within the controller.
    final FileChooser fileChooser = new FileChooser();
    
    // Parker (3/17/17)
    // Create a session variable to store the state of the program:
    Session session = new Session();
    
    // Parker (3/19/17): The name of the folder for storing session data in:
    final String SESSIONS_FOLDER = "\\miceVizSessions";
    
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
        refreshListOfSessions();
    }
    
    /**
     * 
     * @author parker
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
            ObservableList<String> sessionFiles =FXCollections.observableArrayList();
            File[] files = getSessionsFolderPath().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        sessionFiles.add(file.getName());
                    }
                }
                if (sessionFiles.size() > 0) {
                    sessionsLoadButton.setDisable(false);
                    sessionsDeleteButton.setDisable(false);
                }
                else {
                    sessionsLoadButton.setDisable(true);
                    sessionsDeleteButton.setDisable(true);
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
            if (openFile(fileToOpen)) {
                session.dataSetFileLoaded(fileToOpen.getPath()); // update the program's state via the session variable
                if (session.isNewSession) { // Check if the current session is new
                    promptUserToCreateNewSessionFile(session); // prompt the user to create a new session file
                }
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
        if (retryFileCreationAnswer.get() == ButtonType.OK){
            File deleteFile = new File(getSessionsFolderPath().toString() + "\\" + currentListViewItem);
            if (!deleteFile.delete()) {
                simpleAlert("Error: File '" + currentListViewItem + "' could not be deleted.", null);
            }
            refreshListOfSessions();
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
    public void exitApplication(ActionEvent event) {
       System.out.println("Platform is closing");
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
                    session.sessionLoaded(constructSessionFilePath(sessionName).toString()); // update the program's state via the session variable
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
            /* Parker (3-15-17):
            * FileInputStream implementation below from 
            * http://www.baeldung.com/java-read-lines-large-file */
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(file.getPath());
                sc = new Scanner(inputStream, "UTF-8");
                
                long start = System.currentTimeMillis(); // start timing the file processing action
                
                int linesProcessed = 0;
                while (sc.hasNextLine()) {
                    String line = sc.nextLine();
                    linesProcessed++;
                    //System.out.println(line);
                }
                
                long end = System.currentTimeMillis(); // file processing finished; calculate the time spent
                long elapsed = end - start;
                System.out.println("done reading file! It took " + elapsed + " milliseconds");
                
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
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
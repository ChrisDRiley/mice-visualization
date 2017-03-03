package micevisualization;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;

public class AppStageController {
    // Parker (3/2/17): access AppStage.java's stage variable during file IO
    @FXML private Stage stage;
    
    // Parker (3/2/17): access the GUI's left status label
    @FXML private Label leftStatus;
    
    // Parker (3/2/17): the fileChooser variable can be reused throughout the
    // system's event handlers, so we create a global within the controller.
    final FileChooser fileChooser = new FileChooser();
    
    /* 
    Parker (3/2/17): 
    openFileAction is the event handler called when the user clicks on the 
    File -> Open menu option. Its purpose is to call the operating system's 
    file system viewer. This implementation supports Windows, Mac, and Unix
    operating systems (http://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm).
    */
    @FXML protected void openFileAction(ActionEvent event) {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openFile(file);
        }
    }
    
    /*
     Parker (3/2/17):
    exitApplication is called when the user exits the application, currently by
    selecting the File -> Exit menu option. Any cleanup code, if necessary, needs
    to go either here or in the AppStage.java file's stop() function.
    */
    @FXML
    public void exitApplication(ActionEvent event) {
       System.out.println("Platform is closing");
       Platform.exit();
    }
    
    /*
     Parker (3/2/17):
    configureFileChooser configures the file system viewer to have a specific title,
    initial directory, and accepted file types. This function is called using an
    instantiated FileChooser object as its parameter.
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
    
    /*
    Parker (3/2/17):
    openFile opens the File passed as a parameter. This class is called after
    the user picks a file from the FileChooser display in the openFileAction
    event handler.
    */
    private void openFile(File file) {
        try {
            leftStatus.setText("Opening " + file.getName() + " ...");
        } catch(Exception e) { 
            System.out.println(e); 
        }
    }
}
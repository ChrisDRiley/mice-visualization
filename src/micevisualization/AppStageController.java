package micevisualization;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AppStageController {
    @FXML private Text actionTarget;
    @FXML private TextField userName;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        String message = "Sign in button pressed; user = " + userName.getText();
        actionTarget.setText(message);
    }
    
    @FXML protected void openFileAction(ActionEvent event) {
        Desktop desktop = null;
        // on Windows, retrieve the path of the "Program Files" folder
        File file = new File(System.getenv("programfiles"));

        try {
          if (Desktop.isDesktopSupported()) {
             desktop = Desktop.getDesktop();
             desktop.open(file);
          }
          else {
             System.out.println("desktop is not supported");
          }
        }
        catch (IOException e){  }
    }
    
    @FXML
    public void exitApplication(ActionEvent event) {
       Platform.exit();
    }
}
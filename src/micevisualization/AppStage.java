package micevisualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppStage extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUIScaffold.fxml"));
        
        Scene scene = new Scene(root, 1000, 760);
        
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        // Save file and perform clean up
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author parker
 * 
 * This class represents the current state of the program. Rather than using getters
 * and setters, Session's methods manipulate its parameters based on events.
 */
public class Session {
    Boolean isDataSetFileLoaded = false;
    // currentDataSetFilePath represents the absolute path to the selected data set file
    String currentDataSetFilePath = "";
    // relativeDataSetFilePath represents the relative path to the selected data set file,
    // which by default is set to: the mice-sessions directory path + the selected file's name.
    // This way, if a session file is shared across two or more computers, the new users
    // will simply need to copy their data set to the mice-sessions folder for the program to
    // find the path of the data set.
    String relativeDataSetFilePath = "";
    Boolean isSessionLoaded = false;
    String currentSessionFilePath = "";
    Boolean isNewSession = true;
    
    String visualizationType = "";
    String mapType = "";
    String selectedMiceIndices = "";
    Boolean showGridLines = false;
    Boolean showGridNumbers = false;
    String startingIndex = "";
    String stoppingIndex = "";
    Double animationSpeed = 0.1;
    
    /**
     * @author: parker
     * 
     * If a dataset is loaded, reflect that change in the state
     * 
     * @param path 
     */
    public void dataSetFileLoaded(String absolute, String relative) {
        isDataSetFileLoaded = true;
        currentDataSetFilePath = absolute;
        relativeDataSetFilePath = relative;
    }
    
    /**
     * 
     * @author: parker
     * 
     * If a session was loaded (old or new), update the state
     * 
     * @param path 
     */
    public void sessionLoaded(String path) {
        isSessionLoaded = true;
        currentSessionFilePath = path;
        isNewSession = false;
    }
    
    /**
     * @author: parker
     * 
     * create a json string of data using the Gson package. The json String contains a 
     * text representation of the Session object.
     * 
     * @throws FileNotFoundException 
     */
    public void saveState() throws FileNotFoundException {
        if (this.isNewSession == false) {
            Gson gson = new Gson();
            String json = gson.toJson(this); 
            PrintStream ps = new PrintStream(currentSessionFilePath);
            ps.println(json);
            ps.close();
        }

    }

}

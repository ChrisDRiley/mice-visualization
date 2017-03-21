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
    String currentDataSetFilePath = "";
    Boolean isSessionLoaded = false;
    String currentSessionFilePath = "";
    Boolean isNewSession = true;
    
    String visualizationType = "";
    
    /**
     * @author: parker
     * 
     * If a dataset is loaded, reflect that change in the state
     * 
     * @param path 
     */
    public void dataSetFileLoaded(String path) {
        isDataSetFileLoaded = true;
        currentDataSetFilePath = path;
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
        Gson gson = new Gson();
        String json = gson.toJson(this); 
        PrintStream ps = new PrintStream(currentSessionFilePath);
        ps.println(json);
        ps.close();
    }

}

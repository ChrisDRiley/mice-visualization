/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;

import java.util.ArrayList;

/**
 *
 * @author Alex
 * This class stores information pertaining to Mouse5 of the data file
 */
public class Mouse5 {
    ArrayList<String> mouse_five; //date time for date?
    
    //Currently unused due to using ArrayList (Never really used it before so been experimenting)
    private String date; //date for mouse activity
    private String rfid; //identifer (10 letters and numbers)
    private String idLabel; //mouse identifier
    private String unitLabel; //Location of grid where mouse is at
    private String duration; //Duration mouse is at grid location
    
    //Default Constructor
    public Mouse5() {
        mouse_five = new ArrayList<>();
    }
    
}

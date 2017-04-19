/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author parker
 * 
 * MouseLocTime stands for "Mouse Location and Timestamp" information.
 */
public class MouseLocTime implements Comparable<MouseLocTime> {
    Date timestamp;
    String unitLabel;
    int eventDuration;
    
    public MouseLocTime(String ts, String ul, String ed) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
            this.timestamp = formatter.parse(ts);
        }
        catch (ParseException pe) {
            this.timestamp = null;
        }
        
        this.unitLabel = ul;
        
        try {
            this.eventDuration = Integer.parseInt(ed);
        }
        catch (NumberFormatException e) {
            this.eventDuration = 0;
        }
    }
    
    @Override
    public int compareTo(MouseLocTime other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}

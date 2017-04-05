/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 *
 * @author parker
 */
public class Mouse {
    ArrayList<MouseLocTime> locTimeData;
    
    String IdRFID;
    String IdLabel;
    Color mouse_color;
    
    public Mouse(String idr, String idl) {
        this.IdRFID = idr;
        this.IdLabel = idl;
        this.locTimeData = new ArrayList<MouseLocTime>();
        
        Random rand = new Random();
        this.mouse_color = Color.rgb(rand.nextInt(256),
                rand.nextInt(256),
                rand.nextInt(256));
    }
    
    public Boolean addLocTime(MouseLocTime mlt) {
        return locTimeData.add(mlt);
    }
}

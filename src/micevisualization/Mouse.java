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
    double x;
    double y;
    
    public Mouse(String idr, String idl, Color clr, double X, double Y) {
        this.IdRFID = idr;
        this.IdLabel = idl;
        this.locTimeData = new ArrayList<>();
        this.mouse_color = clr;
         //Whitney Post 5/29/17: Removed randomly generated mouse colors        
        this.mouse_color = clr;
        this.x = X;
        this.y = Y;
    }
    
    public Boolean addLocTime(MouseLocTime mlt) {
        return locTimeData.add(mlt);
    }
       //whitney post 5/23/17 adding method to return the mouse color
    public Color getColor() {
        return this.mouse_color;
    }
    
}

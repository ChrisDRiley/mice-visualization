/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;
import java.util.ArrayList;

/**
 *
 * @author parker
 */
public class Mice {
    ArrayList<Mouse> mice;
    
    public Mice() {
        this.mice = new ArrayList<Mouse>();
    }
    
    public void print() {
        for (int i = 0; i < mice.size(); ++i) {
            System.out.println("IdRFID: " + mice.get(i).IdRFID + ", IdLabel: " + mice.get(i).IdLabel);
        }
    }
    
    public Boolean add(Mouse m) {
        return mice.add(m);
    }
    
    public Boolean hasMouse(String IdRFID) {
        for (int i = 0; i < mice.size(); ++i) {
            if (mice.get(i).IdRFID.equals(IdRFID)) return true;
        }
        return false;
    }
    
    public Mouse getMouseByIdRFID(String IdRFID) {
        for (int i = 0; i < mice.size(); ++i) {
            if (mice.get(i).IdRFID.equals(IdRFID)) return mice.get(i);
        }
        return null;       
    }
}

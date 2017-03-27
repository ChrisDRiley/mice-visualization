/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package micevisualization;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    
    public ObservableList<String> getMouseIdsLabelsObservableList() {
        ObservableList<String> miceIds = FXCollections.observableArrayList();
        for (int i = 0; i < this.mice.size(); ++i) {
            String info = mice.get(i).IdRFID + " (" + mice.get(i).IdLabel + ")";
            miceIds.add(info);
        }
        return miceIds;
    }
    
    public ArrayList<Mouse> getMicebyIdsLabels(ObservableList<String> ids) {
        ArrayList<Mouse> returnMice = new ArrayList<Mouse>();
        for (int i = 0; i < ids.size(); ++i) {
            int cutoff = ids.get(i).indexOf(' ');
            String rfid = ids.get(i).substring(0, cutoff);
            System.out.println("getMicebyIdsLables: " + rfid);
            if (getMouseByIdRFID(rfid) != null) {
                returnMice.add(getMouseByIdRFID(rfid));
            }
        }
        if (!returnMice.isEmpty()) {
            return returnMice;
        }
        else {
            return null;
        }
    }
}

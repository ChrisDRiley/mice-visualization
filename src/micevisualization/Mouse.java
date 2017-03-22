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
public class Mouse {
    ArrayList<MouseLocTime> locTimeData;
    
    String IdRFID;
    String IdLabel;
    
    public Mouse(String idr, String idl) {
        this.IdRFID = idr;
        this.IdLabel = idl;
        this.locTimeData = new ArrayList<MouseLocTime>();
    }
    
    public Boolean addLocTime(MouseLocTime mlt) {
        return locTimeData.add(mlt);
    }
}

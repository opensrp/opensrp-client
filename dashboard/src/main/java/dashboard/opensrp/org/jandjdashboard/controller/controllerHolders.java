package dashboard.opensrp.org.jandjdashboard.controller;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by raihan on 1/22/18.
 */

public class controllerHolders implements Serializable {
    HashMap<String,dashboardControllers> controllersHashMap;

    public HashMap<String, dashboardControllers> getControllersHashMap() {
        return controllersHashMap;
    }

    public void setControllersHashMap(HashMap<String, dashboardControllers> controllersHashMap) {
        this.controllersHashMap = controllersHashMap;
    }

}

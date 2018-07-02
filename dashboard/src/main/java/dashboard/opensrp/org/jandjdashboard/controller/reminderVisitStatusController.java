package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class reminderVisitStatusController extends dashboardControllers{
    public String ancVisitTableName;
    public String pncVisitTableName;
    public String neonatalVisitTableName;


    public void setAncVisitTableName(String ancVisitTableName) {
        this.ancVisitTableName = ancVisitTableName;
    }

    public void setPncVisitTableName(String pncVisitTableName) {
        this.pncVisitTableName = pncVisitTableName;
    }

    public void setNeonatalVisitTableName(String neonatalVisitTableName) {
        this.neonatalVisitTableName = neonatalVisitTableName;
    }



    public abstract HashMap<String, HashMap<String, String>> ancVisitQuery(Date from, Date to, String riskflag);
    public abstract HashMap<String, HashMap<String, String>> pncVisitQuery(Date from, Date to, String riskflag);
    public abstract HashMap<String, HashMap<String, String>> neonatalVisitQuery(Date from, Date to, String riskflag);

}

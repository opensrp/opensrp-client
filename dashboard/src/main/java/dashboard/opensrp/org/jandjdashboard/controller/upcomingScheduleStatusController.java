package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class upcomingScheduleStatusController extends dashboardControllers{
    public String householdTableName;
    public String elcoVisitTableName;
    public String eddTableName;
    public String ancVisitTableName;
    public String pncVisitTableName;
    public String neonatalVisitTableName;
    public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    public void setEddTableName(String eddTableName) {
        this.eddTableName = eddTableName;
    }

    public void setAncVisitTableName(String ancVisitTableName) {
        this.ancVisitTableName = ancVisitTableName;
    }

    public void setPncVisitTableName(String pncVisitTableName) {
        this.pncVisitTableName = pncVisitTableName;
    }

    public void setNeonatalVisitTableName(String neonatalVisitTableName) {
        this.neonatalVisitTableName = neonatalVisitTableName;
    }

    public void setElcoVisitTableName(String elcoVisitTableName) {
        this.elcoVisitTableName = elcoVisitTableName;
    }
    public void setHouseholdTableName(String householdTableName) {
        this.householdTableName = householdTableName;
    }

    public abstract String houseHoldVisitQuery(Date from, Date to);
    public abstract String elcoVisitQuery(Date from, Date to);
    public abstract String eddQuery(Date from, Date to);
    public abstract String ancVisitQuery(Date from, Date to);
    public abstract String pncVisitQuery(Date from, Date to);
    public abstract String neonatalVisitQuery(Date from, Date to);

}

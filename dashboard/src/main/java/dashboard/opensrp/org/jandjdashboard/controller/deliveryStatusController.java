package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class deliveryStatusController extends dashboardControllers{



    public abstract String numberofLiveBirth(Date from, Date to);
    public abstract String numberofNewBornswithLowBirthWeight(Date from, Date to);
    public abstract String numberofImmatureBirth(Date from, Date to);
    public abstract String numberofStillBirth(Date from, Date to);
    public abstract String numberofDeathUnder7Days(Date from, Date to);
    public abstract String numberofDeathBetween8to28Days(Date from, Date to);
    public abstract String numberofDeathBetween29daysto1year(Date from, Date to);
    public abstract String numberofTotalDeath(Date from, Date to);
    public abstract String numberofChildDeathBetween1to5year(Date from, Date to);
    public abstract String numberofMotherDeath(Date from, Date to);
    public abstract String numberofOtherDeath(Date from, Date to);


    public abstract String totalnumberofLiveBirth(Date fromdate, Date todate);

    public abstract String overallnumberofTotalDeath(Date fromdate, Date todate);
}

package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class reproductiveHealthServiceController extends dashboardControllers{


    public abstract String ecpReceptors(Date from, Date to);
    public abstract HashMap<String,String> ttquery(Date from, Date to);
    public abstract HashMap<String,String> ancVisitQuery(Date from, Date to);
    public abstract HashMap<String,String> pncVisitQuery(Date from, Date to);
    public abstract HashMap<String,String> neonatalVisitQuery(Date from, Date to);

}

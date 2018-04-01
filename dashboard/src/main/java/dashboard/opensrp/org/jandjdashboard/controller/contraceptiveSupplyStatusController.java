package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class contraceptiveSupplyStatusController extends dashboardControllers{

    public abstract String oralpillshukhiCurrentMonthQuery(Date from, Date to);
    public abstract String oralpillAponCurrentMonthQuery(Date from, Date to);
    public abstract String condomNirapodCurrentMonthQuery(Date from, Date to);


}

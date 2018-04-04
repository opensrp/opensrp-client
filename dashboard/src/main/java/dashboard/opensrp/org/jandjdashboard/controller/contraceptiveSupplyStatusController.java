package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class contraceptiveSupplyStatusController extends dashboardControllers{

    public abstract String total_elco_Query(Date from, Date to);

    public abstract String total_new_elco_Query(Date fromdate, Date todate);

    public abstract String total_elco_visited_Query(Date from, Date to);

    public abstract String contraceptive_acceptance_rate_Query(Date from, Date to);

    public abstract String referred_for_contraceptive_side_effects_Query(Date from, Date to);

    public abstract String oralpillshukhiCurrentMonthQuery(Date from, Date to);
    public abstract String oralpillAponCurrentMonthQuery(Date from, Date to);
    public abstract String condomNirapodCurrentMonthQuery(Date from, Date to);


}

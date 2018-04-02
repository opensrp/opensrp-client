package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class familyPlanningStatusController extends dashboardControllers{

    public abstract String pill_old_Query(Date from, Date to);
    public abstract String pill_new_Query(Date from, Date to);
    public abstract String pill_unit_totalQuery(Date from, Date to);
    public abstract String pill_not_using_any_methodQuery(Date from, Date to);
    public abstract String pill_using_other_methodQuery(Date from, Date to);
    public abstract String pill_referred_for_methodQuery(Date from, Date to);
    public abstract String pill_referred_for_side_effectsQuery(Date from, Date to);

}

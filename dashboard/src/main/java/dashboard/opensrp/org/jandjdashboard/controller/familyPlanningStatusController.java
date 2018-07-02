package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class familyPlanningStatusController extends dashboardControllers{

    public abstract String total_elco_Query(Date from, Date to);
    public abstract String total_new_elco_Query(Date fromdate, Date todate);
    public abstract String total_elco_visited_Query(Date from, Date to);
    public abstract String contraceptive_acceptance_rate_Query(Date from, Date to);
    public abstract String referred_for_contraceptive_side_effects_Query(Date from, Date to);

    public abstract String pill_old_Query(Date from, Date to);
    public abstract String pill_new_Query(Date from, Date to);
    public abstract String pill_unit_totalQuery(Date from, Date to);
    public abstract String pill_not_using_any_methodQuery(Date from, Date to);
    public abstract String pill_using_other_methodQuery(Date from, Date to);
    public abstract String pill_referred_for_methodQuery(Date from, Date to);
    public abstract String pill_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String condom_old_Query(Date from, Date to);
    public abstract String condom_new_Query(Date from, Date to);
    public abstract String condom_unit_totalQuery(Date from, Date to);
    public abstract String condom_not_using_any_methodQuery(Date from, Date to);
    public abstract String condom_using_other_methodQuery(Date from, Date to);
    public abstract String condom_referred_for_methodQuery(Date from, Date to);
    public abstract String condom_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String injectable_old_Query(Date from, Date to);
    public abstract String injectable_new_Query(Date from, Date to);
    public abstract String injectable_unit_totalQuery(Date from, Date to);
    public abstract String injectable_not_using_any_methodQuery(Date from, Date to);
    public abstract String injectable_using_other_methodQuery(Date from, Date to);
    public abstract String injectable_referred_for_methodQuery(Date from, Date to);
    public abstract String injectable_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String iud_old_Query(Date from, Date to);
    public abstract String iud_new_Query(Date from, Date to);
    public abstract String iud_unit_totalQuery(Date from, Date to);
    public abstract String iud_not_using_any_methodQuery(Date from, Date to);
    public abstract String iud_using_other_methodQuery(Date from, Date to);
    public abstract String iud_referred_for_methodQuery(Date from, Date to);
    public abstract String iud_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String implant_old_Query(Date from, Date to);
    public abstract String implant_new_Query(Date from, Date to);
    public abstract String implant_unit_totalQuery(Date from, Date to);
    public abstract String implant_not_using_any_methodQuery(Date from, Date to);
    public abstract String implant_using_other_methodQuery(Date from, Date to);
    public abstract String implant_referred_for_methodQuery(Date from, Date to);
    public abstract String implant_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String pm_male_old_Query(Date from, Date to);
    public abstract String pm_male_new_Query(Date from, Date to);
    public abstract String pm_male_unit_totalQuery(Date from, Date to);
    public abstract String pm_male_not_using_any_methodQuery(Date from, Date to);
    public abstract String pm_male_using_other_methodQuery(Date from, Date to);
    public abstract String pm_male_referred_for_methodQuery(Date from, Date to);
    public abstract String pm_male_referred_for_side_effectsQuery(Date from, Date to);

    public abstract String pm_female_old_Query(Date from, Date to);
    public abstract String pm_female_new_Query(Date from, Date to);
    public abstract String pm_female_unit_totalQuery(Date from, Date to);
    public abstract String pm_female_not_using_any_methodQuery(Date from, Date to);
    public abstract String pm_female_using_other_methodQuery(Date from, Date to);
    public abstract String pm_female_referred_for_methodQuery(Date from, Date to);
    public abstract String pm_female_referred_for_side_effectsQuery(Date from, Date to);


}

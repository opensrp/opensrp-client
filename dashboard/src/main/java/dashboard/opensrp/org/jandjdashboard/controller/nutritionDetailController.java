package dashboard.opensrp.org.jandjdashboard.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by raihan on 1/22/18.
 */

public abstract class nutritionDetailController extends dashboardControllers{


    public abstract String numberofLiveBirth(Date from, Date to);
    public abstract String totalnumberofLiveBirth(Date fromdate, Date todate);
    public abstract String overallnumberofTotalDeath(Date fromdate, Date todate);
    public abstract String numberofTotalDeath(Date from, Date to);

    public abstract String iron_and_folic_acid_pregnant_woman_info(Date fromdate, Date todate);
    public abstract String iron_and_folic_acid_mother_info(Date fromdate, Date todate);
    public abstract String distributed_iron_and_folic_acid_pregnant_woman_info(Date fromdate, Date todate);
    public abstract String distributed_iron_and_folic_acid_mother_info(Date fromdate, Date todate);
    public abstract String counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info(Date fromdate, Date todate);
    public abstract String counselling_on_breastfeeding_and_complimentary_food_mother_info(Date fromdate, Date todate);
    public abstract String counselling_on_feeding_mnp_to_children_pregnant_woman_info(Date fromdate, Date todate);
    public abstract String counselling_on_feeding_mnp_to_children_mother_info(Date fromdate, Date todate);
    public abstract String feed_colostrum_milk_zero_to_six_month_info(Date fromdate, Date todate);
    public abstract String feed_colostrum_milk_six_to_24_month_info(Date fromdate, Date todate);
    public abstract String feed_colostrum_milk_twentyfour_to_fifty_month_info(Date fromdate, Date todate);
    public abstract String breastfeeding_up_to_6_months_zero_to_six_months_info(Date fromdate, Date todate);
    public abstract String breastfeeding_up_to_6_months_six_to_twentyfour_months_info(Date fromdate, Date todate);
    public abstract String breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info(Date fromdate, Date todate);
    public abstract String providing_extra_complimentary_food_zero_to_6_months_info(Date fromdate, Date todate);
    public abstract String providing_extra_complimentary_food_6_to_24_months_info(Date fromdate, Date todate);
    public abstract String providing_extra_complimentary_food_24_to_50_months_info(Date fromdate, Date todate);
    public abstract String received_multiple_mnr_0_to_6months_info(Date fromdate, Date todate);
    public abstract String received_multiple_mnr_6_to_24months_info(Date fromdate, Date todate);
    public abstract String received_multiple_mnr_24_to_50months_info(Date fromdate, Date todate);
}

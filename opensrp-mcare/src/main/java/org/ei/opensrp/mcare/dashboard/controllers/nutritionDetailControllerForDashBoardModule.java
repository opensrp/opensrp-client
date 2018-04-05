package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;
import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;
import dashboard.opensrp.org.jandjdashboard.controller.reproductiveHealthServiceController;

/**
 * Created by raihan on 1/22/18.
 */

public class nutritionDetailControllerForDashBoardModule extends nutritionDetailController {


    @Override
    public String iron_and_folic_acid_pregnant_woman_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String iron_and_folic_acid_mother_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String distributed_iron_and_folic_acid_pregnant_woman_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String distributed_iron_and_folic_acid_mother_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String counselling_on_breastfeeding_and_complimentary_food_mother_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String counselling_on_feeding_mnp_to_children_pregnant_woman_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String counselling_on_feeding_mnp_to_children_mother_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String feed_colostrum_milk_zero_to_six_month_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String feed_colostrum_milk_six_to_24_month_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String feed_colostrum_milk_twentyfour_to_fifty_month_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String breastfeeding_up_to_6_months_zero_to_six_months_info(Date fromdate, Date todate) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(Distinct instanceId) from form_submission where (formName like '%encc_visit%') and (instance like '%{\"name\":\"FWENC1BFINTN\",\"value\":\"1\",\"source\":\"mcarechild.FWENC1BFINTN\"}%' or instance like '%{\"name\":\"FWENC2BFINTN\",\"value\":\"1\",\"source\":\"mcarechild.FWENC2BFINTN\"}%' or instance like '%{\"name\":\"FWENC3BFINTN\",\"value\":\"1\",\"source\":\"mcarechild.FWENC3BFINTN\"}%')  and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(fromdate) + "') and date('" + format.format(todate) + "'))");
        cursor.moveToFirst();
        try {
            String countofecpreceptor = cursor.getString(0);

            cursor.close();
            return countofecpreceptor;
        } catch (Exception e) {
            cursor.close();
            return "0";
        }
    }

    @Override
    public String breastfeeding_up_to_6_months_six_to_twentyfour_months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String providing_extra_complimentary_food_zero_to_6_months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String providing_extra_complimentary_food_6_to_24_months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String providing_extra_complimentary_food_24_to_50_months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String received_multiple_mnr_0_to_6months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String received_multiple_mnr_6_to_24months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String received_multiple_mnr_24_to_50months_info(Date fromdate, Date todate) {
        return "";
    }

    @Override
    public String totalnumberofLiveBirth(Date fromdate, Date todate) {
        return "N/A";
    }

    @Override
    public String overallnumberofTotalDeath(Date fromdate, Date todate) {
        return "N/A";
    }


    @Override
    public String numberofTotalDeath(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofLiveBirth(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'birthnotificationpregnancystatusfollowup' and form_submission.instance like '%{\"name\":\"FWBNFSTS\",\"value\":\"3\",\"source\":\"mcaremother.FWBNFSTS\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofLiveBirth = cursor.getString(0);

            cursor.close();
            return numberofLiveBirth;
        } catch (Exception e) {
            cursor.close();
            return "0";
        }
    }
}

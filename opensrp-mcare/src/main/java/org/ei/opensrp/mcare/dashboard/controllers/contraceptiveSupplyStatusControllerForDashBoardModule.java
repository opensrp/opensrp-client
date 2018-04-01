package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.controller.contraceptiveSupplyStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;

/**
 * Created by raihan on 1/22/18.
 */

public class contraceptiveSupplyStatusControllerForDashBoardModule extends contraceptiveSupplyStatusController {


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
    public String oralpillshukhiCurrentMonthQuery(Date from, Date to) {

        return "0";
    }

    @Override
    public String oralpillAponCurrentMonthQuery(Date from, Date to) {
        return "0";
    }

    @Override
    public String condomNirapodCurrentMonthQuery(Date from, Date to) {
        return "0";
    }
}

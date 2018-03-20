package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;
import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.reproductiveHealthServiceController;
import dashboard.opensrp.org.jandjdashboard.controller.upcomingScheduleStatusController;

/**
 * Created by raihan on 1/22/18.
 */

public class reproductiveHealthServiceControllerForDashBoardModule extends reproductiveHealthServiceController {

    @Override
    public String ecpReceptors(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'mis_census' and form_submission.instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"08\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
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
    public HashMap<String, String> ttquery(Date from, Date to) {
        HashMap<String,String> ttdoses = new HashMap<String, String>();
        String ttnumber = ""+1;
        for(int i = 1;i<6;i++) {
            if(i != 1){
             ttnumber = ttnumber+" "+i;
            }
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'mis_census' and form_submission.instance like '%{\"source\":\"elco.FWMISCENTTDOSE\",\"value\":\"1 2 3 4 5\",\"bind\":\"/model/instance/MIS_Census/FWMISCENTTDOSE\",\"name\":\"FWMISCENTTDOSE\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                String countoftt1 = cursor.getString(0);
                ttdoses.put("tt"+i+"given", countoftt1);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
        }

        return ttdoses;
    }

    @Override
    public HashMap<String, String> ancVisitQuery(Date from, Date to) {
        HashMap<String,String> ancVisits = new HashMap<String, String>();
        for(int i = 1;i<5;i++) {
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName = 'anc_reminder_visit_"+i+"' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                String countofancvisit = cursor.getString(0);
                ancVisits.put("anc"+i+"visit", countofancvisit);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
        }
        return ancVisits;
    }

    @Override
    public HashMap<String, String> pncVisitQuery(Date from, Date to) {
        HashMap<String,String> pncVisits = new HashMap<String, String>();
        for(int i = 1;i<4;i++) {
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName = 'pnc_reminder_visit_"+i+"' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                String countofancvisit = cursor.getString(0);
                pncVisits.put("pnc"+i+"visit", countofancvisit);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
        }
        return pncVisits;
    }

    @Override
    public HashMap<String, String> neonatalVisitQuery(Date from, Date to) {
        HashMap<String,String> enncVisits = new HashMap<String, String>();
        for(int i = 1;i<4;i++) {
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName = 'encc_visit_"+i+"' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                String countofancvisit = cursor.getString(0);
                enncVisits.put("encc"+i+"visit", countofancvisit);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
        }
        return enncVisits;
    }
}

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
        String giventtnumber = ""+1;
        for(int i = 1;i<6;i++) {
            if(i != 1){
             ttnumber = ttnumber+" "+i;
             giventtnumber = giventtnumber+" "+i;
            }
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'mis_census' and form_submission.instance like '%{\"source\":\"elco.FWMISCENTTDOSE\",\"value\":\""+giventtnumber+"\",\"bind\":\"/model/instance/MIS_Census/FWMISCENTTDOSE\",\"name\":\"FWMISCENTTDOSE\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            Cursor cursor2 = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'mis_census' and form_submission.instance like '%{\"name\":\"FWMISCENTTDOSE\",\"value\":\""+ttnumber+"\",\"source\":\"elco.FWMISCENTTDOSE\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor2.moveToFirst();
            cursor.moveToFirst();
            try {
                String countoftt1 = cursor.getString(0);
                String countoftt1_2 = cursor2.getString(0);
                ttdoses.put("tt"+i+"given", ""+(Integer.parseInt(countoftt1)+Integer.parseInt(countoftt1_2)));
                cursor.close();
                cursor2.close();
            } catch (Exception e) {
                cursor.close();
                cursor2.close();

            }
        }

        return ttdoses;
    }

    @Override
    public HashMap<String, String> ancVisitQuery(Date from, Date to) {
        HashMap<String,String> ancVisits = new HashMap<String, String>();
        int i = 1;
//        for(int i = 1;i<5;i++) {
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName like '%anc_reminder_visit%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))  group by formName");
            cursor.moveToFirst();
            try {
                while(!cursor.isAfterLast()) {
                    String countofancvisit = cursor.getString(0);
                    ancVisits.put("anc" + i + "visit", countofancvisit);
                    i++;
                    cursor.moveToNext();
                }
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        }
        return ancVisits;
    }

    @Override
    public HashMap<String, String> pncVisitQuery(Date from, Date to) {

        HashMap<String,String> pncVisits = new HashMap<String, String>();
        int i = 1;
//        for(int i = 1;i<5;i++) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName like '%pnc_reminder_visit%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))  group by formName");
        cursor.moveToFirst();
        try {
            while(!cursor.isAfterLast()) {
                String countofpncvisit = cursor.getString(0);
                pncVisits.put("pnc" + i + "visit", countofpncvisit);
                i++;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
//        }
        return pncVisits;
    }

    @Override
    public HashMap<String, String> neonatalVisitQuery(Date from, Date to) {

        HashMap<String,String> enccVisits = new HashMap<String, String>();
        int i = 1;
//        for(int i = 1;i<5;i++) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where formName like '%encc_visit_%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))  group by formName");
        cursor.moveToFirst();
        try {
            while(!cursor.isAfterLast()) {
                String countofenccvisit = cursor.getString(0);
                enccVisits.put("encc" + i + "visit", countofenccvisit);
                i++;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
//        }
        return enccVisits;
    }
}

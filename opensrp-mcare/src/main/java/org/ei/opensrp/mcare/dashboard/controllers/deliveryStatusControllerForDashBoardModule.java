package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;
import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.deliveryStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.reproductiveHealthServiceController;

/**
 * Created by raihan on 1/22/18.
 */

public class deliveryStatusControllerForDashBoardModule extends deliveryStatusController {

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

    @Override
    public String numberofNewBornswithLowBirthWeight(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofImmatureBirth(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        String coreQuery = "select count(*) from form_submission where form_submission.formName = 'birthnotificationpregnancystatusfollowup' and form_submission.instance like '%{\"name\":\"FWBNFSTS\",\"value\":\"3\",\"source\":\"mcaremother.FWBNFSTS\"}%'";
        String immaturebirthiteration = "and (";
        for(int i = 0;i<37;i++){
            immaturebirthiteration = immaturebirthiteration+"form_submission.instance like '%{\"name\":\"FWGESTATIONALAGE\",\"value\":\""+i+"\",\"source\":\"mcaremother.FWGESTATIONALAGE\"}%'";
            if(i==36){
                immaturebirthiteration = immaturebirthiteration+")";
            }else{
                immaturebirthiteration = immaturebirthiteration+" or ";
            }
        }
        String dateInBetweenQuery = " and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))";
        Cursor cursor = commonRepository.RawCustomQueryForAdapter(coreQuery+immaturebirthiteration+dateInBetweenQuery);
        cursor.moveToFirst();
        try {
            String numberofimmaturebirth = cursor.getString(0);

            cursor.close();
            return numberofimmaturebirth;
        } catch (Exception e) {
            cursor.close();
            return "0";
        }
    }

    @Override
    public String numberofStillBirth(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'birthnotificationpregnancystatusfollowup' and form_submission.instance like '%{\"name\":\"FWBNFSTS\",\"value\":\"4\",\"source\":\"mcaremother.FWBNFSTS\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofStillBirth = cursor.getString(0);

            cursor.close();
            return numberofStillBirth;
        } catch (Exception e) {
            cursor.close();
            return "0";
        }
    }

    @Override
    public String numberofDeathUnder7Days(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofDeathBetween8to28Days(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofDeathBetween29daysto1year(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofTotalDeath(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofChildDeathBetween1to5year(Date from, Date to) {
        return "";
    }

    @Override
    public String numberofMotherDeath(Date from, Date to) {
        int numberofMotherDeath = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'birthnotificationpregnancystatusfollowup' and (form_submission.instance like '%{\"name\":\"FWBNFSTS\",\"value\":\"9\",\"source\":\"mcaremother.FWBNFSTS\"}%' or form_submission.instance like '%{\"name\":\"FWBNFWOMVITSTS\",\"value\":\"0\",\"source\":\"mcaremother.FWBNFWOMVITSTS\"}%')and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofdeath = cursor.getString(0);
            numberofMotherDeath = Integer.parseInt(numberofdeath);
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
        cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'pnc_reminder_visit_1' and form_submission.instance like '%{\"name\":\"FWPNC1REMSTS\",\"value\":\"8\",\"source\":\"mcaremother.FWPNC1REMSTS\"}%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofdeath = cursor.getString(0);
            numberofMotherDeath = numberofMotherDeath+Integer.parseInt(numberofdeath);
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
        cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'pnc_reminder_visit_2' and form_submission.instance like '%{\"name\":\"FWPNC2REMSTS\",\"value\":\"8\",\"source\":\"mcaremother.FWPNC2REMSTS\"}%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofdeath = cursor.getString(0);
            numberofMotherDeath = numberofMotherDeath+Integer.parseInt(numberofdeath);
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
        cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'pnc_reminder_visit_3' and form_submission.instance like '%{\"name\":\"FWPNC3REMSTS\",\"value\":\"8\",\"source\":\"mcaremother.FWPNC3REMSTS\"}%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
        cursor.moveToFirst();
        try {
            String numberofdeath = cursor.getString(0);
            numberofMotherDeath = numberofMotherDeath+Integer.parseInt(numberofdeath);
            cursor.close();
        } catch (Exception e) {
            cursor.close();
        }
        return ""+numberofMotherDeath;
    }

    @Override
    public String numberofOtherDeath(Date from, Date to) {
        return null;
    }
}

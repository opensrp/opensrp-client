package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.controller.contraceptiveSupplyStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;

/**
 * Created by raihan on 1/22/18.
 */

public class contraceptiveSupplyStatusControllerForDashBoardModule extends contraceptiveSupplyStatusController {

    @Override
    public String total_elco_Query(Date from, Date to) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = null;
        try {
            cursor =commonRepository.RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("elco","elco.FWWOMFNAME NOT NULL and elco.FWWOMFNAME !=''  AND elco.details  LIKE '%\"FWELIGIBLE\":\"1\"%'"));
            cursor.moveToFirst();
            String countofelcovisited = cursor.getString(0);
            cursor.close();
            return countofelcovisited;
        } catch (Exception e) {
            if(cursor!=null) {
                cursor.close();
            }
            return "0";
        }
    }

    @Override
    public String total_new_elco_Query(Date fromdate, Date todate) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = null;
        try {
            int countofelcovisited = 0;
            cursor = commonRepository.RawCustomQueryForAdapter("Select count(*) from (SELECT FWWOMFNAME,details,SUBSTR(replaced, pos+4, 10) AS WomanREGDATE from (SELECT *,FWWOMFNAME,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT FWWOMFNAME,details,replace(elco.details,'WomanREGDATE','^') as replaced\n" +
                    "   FROM elco where elco.details like '%WomanREGDATE%')) where (details like '%\"FWELIGIBLE\":\"1\"%' and FWWOMFNAME != '') and (date(WomanREGDATE) BETWEEN date('" + format.format(fromdate) + "') and date('" + format.format(todate) + "')))");
            cursor.moveToFirst();
            countofelcovisited = Integer.parseInt(cursor.getString(0));

            cursor.close();
            return ""+countofelcovisited;
        } catch (Exception e) {
            if(cursor!=null) {
                cursor.close();
            }
            return "0";
        }
    }

    @Override
    public String total_elco_visited_Query(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = null;
        try {
            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'psrf_form' and form_submission.instance like '%{\"name\":\"user_type\",\"value\":\"FWA\",\"source\":\"elco.user_type\"}%'and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            String countofelcovisited = cursor.getString(0);
            cursor.close();
            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from form_submission where form_submission.formName = 'mis_elco' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            countofelcovisited = ""+(Integer.parseInt(countofelcovisited)+Integer.parseInt(cursor.getString(0)));
            cursor.close();

            return countofelcovisited;
        } catch (Exception e) {
            if(cursor!=null) {
                cursor.close();
            }
            return "0";
        }
    }

    @Override
    public String contraceptive_acceptance_rate_Query(Date from, Date to) {
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = null;
        try {
            cursor = commonRepository.RawCustomQueryForAdapter("select count (distinct form_submission.entityId) from form_submission where form_submission.formName = 'mis_elco' and instance not like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and (date(strftime('%Y-%m-%d', datetime(serverVersion/1000, 'unixepoch'))) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            String countofelcovisited = cursor.getString(0);
            cursor.close();
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.add(Calendar.DATE, -(365*10));
            Date yesterday = cal.getTime();

            Float totalelcoQuery = Float.parseFloat(total_new_elco_Query(yesterday,to));
            if(totalelcoQuery >0) {
                Float contraceptiveacceptanerate = (Integer.parseInt(countofelcovisited) / totalelcoQuery) * 100;
                return String.format("%.2f", contraceptiveacceptanerate) + "%";
            }else{
                return "0%";
            }
        } catch (Exception e) {
            if(cursor!=null) {
                cursor.close();
            }
            return "0 %";
        }
    }

    @Override
    public String referred_for_contraceptive_side_effects_Query(Date from, Date to) {
        return "N/A";
    }

    @Override
    public String oralpillshukhiCurrentMonthQuery(Date from, Date to) {
        int pillsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"01\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISOPT\",\"value\":\"1\",\"source\":\"elco.FWMISOPT\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            try {
                boolean isgivenInBetweenDate = false;
                String instance = cursor.getString(0);
                JSONObject instanceObject = new JSONObject(instance);
                JSONObject formObject = instanceObject.getJSONObject("form");
                JSONArray fields = formObject.getJSONArray("fields");
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISPILLGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date pillgivenDate = format.parse(date);
                        if(pillgivenDate.after(from)&&pillgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISPILLGIVENNO")){
                        String pillsgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            pillsgiven = pillsgiven+ Integer.parseInt(pillsgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pillsgiven;
    }

    @Override
    public String oralpillAponCurrentMonthQuery(Date from, Date to) {
        int pillsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"01\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISOPT\",\"value\":\"2\",\"source\":\"elco.FWMISOPT\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            try {
                boolean isgivenInBetweenDate = false;
                String instance = cursor.getString(0);
                JSONObject instanceObject = new JSONObject(instance);
                JSONObject formObject = instanceObject.getJSONObject("form");
                JSONArray fields = formObject.getJSONArray("fields");
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISPILLGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date pillgivenDate = format.parse(date);
                        if(pillgivenDate.after(from)&&pillgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISPILLGIVENNO")){
                        String pillsgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            pillsgiven = pillsgiven+ Integer.parseInt(pillsgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pillsgiven;
    }

    @Override
    public String condomNirapodCurrentMonthQuery(Date from, Date to) {
        int condomsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"02\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            try {
                boolean isgivenInBetweenDate = false;
                String instance = cursor.getString(0);
                JSONObject instanceObject = new JSONObject(instance);
                JSONObject formObject = instanceObject.getJSONObject("form");
                JSONArray fields = formObject.getJSONArray("fields");
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISCONDGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date condomgivenDate = format.parse(date);
                        if(condomgivenDate.after(from)&&condomgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                for (int i = 0;i<fields.length();i++){
                    JSONObject instancefield = fields.getJSONObject(i);
                    String name = instancefield.getString("name");
                    if(name.equalsIgnoreCase("FWMISCONDGIVENNO")){
                        String condomgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            condomsgiven = condomsgiven+ Integer.parseInt(condomgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+condomsgiven;
    }
}

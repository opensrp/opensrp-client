package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.json.JSONArray;
import org.json.JSONObject;

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

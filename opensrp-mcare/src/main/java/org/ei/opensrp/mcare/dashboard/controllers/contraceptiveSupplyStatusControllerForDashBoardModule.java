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

package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.controller.familyPlanningStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;

/**
 * Created by raihan on 1/22/18.
 */

public class familyPlanningStatusControllerForDashBoardModule extends familyPlanningStatusController {

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
            cursor = commonRepository.RawCustomQueryForAdapter("select details from elco where details like '%\"FWELIGIBLE\":\"1\"%' and elco.FWWOMFNAME != ''");
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
            String details = cursor.getString(0);
            JSONObject detailobject = new JSONObject(details);
            String womanRegdate = detailobject.getString("WomanREGDATE");
            Date datewomanregdate = format.parse(womanRegdate);
            if(datewomanregdate.after(fromdate) && datewomanregdate.before(todate)){
                countofelcovisited++;
            }
            cursor.moveToNext();
            }
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
    public String pill_old_Query(Date from, Date to) {
        int pillsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"01\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
    public String pill_new_Query(Date from, Date to) {
        int pillsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"01\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
    public String pill_unit_totalQuery(Date from, Date to) {
        int pillsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"01\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
    public String pill_not_using_any_methodQuery(Date from, Date to) {
        String pillsgiven = "0";
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        pillsgiven = cursor.getString(0);
        cursor.close();
        return ""+pillsgiven;
    }

    @Override
    public String pill_using_other_methodQuery(Date from, Date to) {
        String pillsgiven = "0";
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        pillsgiven = cursor.getString(0);
        cursor.close();
        return ""+pillsgiven;
    }

    @Override
    public String pill_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String pill_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String condom_old_Query(Date from, Date to) {
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
                        String condomsgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            condomsgiven = condomsgiven+ Integer.parseInt(condomsgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+condomsgiven;
    }

    @Override
    public String condom_new_Query(Date from, Date to) {
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
                        String condomsgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            condomsgiven = condomsgiven+ Integer.parseInt(condomsgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+condomsgiven;
    }

    @Override
    public String condom_unit_totalQuery(Date from, Date to) {
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
                        String condomsgivenstring = instancefield.getString("value");
                        if(isgivenInBetweenDate){
                            condomsgiven = condomsgiven+ Integer.parseInt(condomsgivenstring);
                        }
                    }
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+condomsgiven;
    }

    @Override
    public String condom_not_using_any_methodQuery(Date from, Date to) {
        String condomsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        condomsgiven = cursor.getString(0);
        cursor.close();
        return ""+condomsgiven;
    }

    @Override
    public String condom_using_other_methodQuery(Date from, Date to) {
        String condomsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        condomsgiven = cursor.getString(0);
        cursor.close();
        return ""+condomsgiven;
    }

    @Override
    public String condom_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String condom_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String injectable_old_Query(Date from, Date to) {
        int injectablesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"03\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISINJGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date injectablegivenDate = format.parse(date);
                        if(injectablegivenDate.after(from)&&injectablegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if(isgivenInBetweenDate){
                  injectablesgiven = injectablesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+injectablesgiven;
    }

    @Override
    public String injectable_new_Query(Date from, Date to) {
        int injectablesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"03\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISINJGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date injectablegivenDate = format.parse(date);
                        if(injectablegivenDate.after(from)&&injectablegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if (isgivenInBetweenDate) {
                        injectablesgiven = injectablesgiven + 1;
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+injectablesgiven;
    }

    @Override
    public String injectable_unit_totalQuery(Date from, Date to) {
        int injectablesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"03\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISINJGIVENDATE")){
                        String date = instancefield.getString("value");
                        Date injectablegivenDate = format.parse(date);
                        if(injectablegivenDate.after(from)&&injectablegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }

                if(isgivenInBetweenDate){
                 injectablesgiven = injectablesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+injectablesgiven;
    }

    @Override
    public String injectable_not_using_any_methodQuery(Date from, Date to) {
        String injectablesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        injectablesgiven = cursor.getString(0);
        cursor.close();
        return ""+injectablesgiven;
    }

    @Override
    public String injectable_using_other_methodQuery(Date from, Date to) {
        String injectablesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        injectablesgiven = cursor.getString(0);
        cursor.close();
        return ""+injectablesgiven;
    }

    @Override
    public String injectable_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String injectable_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String iud_old_Query(Date from, Date to) {
        int iudsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"04\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date iudgivenDate = format.parse(date);
                        if(iudgivenDate.after(from)&&iudgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if(isgivenInBetweenDate){
                    iudsgiven = iudsgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+iudsgiven;
    }

    @Override
    public String iud_new_Query(Date from, Date to) {
        int iudsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"04\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date iudgivenDate = format.parse(date);
                        if(iudgivenDate.after(from)&&iudgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if (isgivenInBetweenDate) {
                    iudsgiven = iudsgiven + 1;
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+iudsgiven;
    }

    @Override
    public String iud_unit_totalQuery(Date from, Date to) {
        int iudsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"04\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date iudgivenDate = format.parse(date);
                        if(iudgivenDate.after(from)&&iudgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }

                if(isgivenInBetweenDate){
                    iudsgiven = iudsgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+iudsgiven;
    }

    @Override
    public String iud_not_using_any_methodQuery(Date from, Date to) {
        String iudsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        iudsgiven = cursor.getString(0);
        cursor.close();
        return ""+iudsgiven;
    }

    @Override
    public String iud_using_other_methodQuery(Date from, Date to) {
        String iudsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        iudsgiven = cursor.getString(0);
        cursor.close();
        return ""+iudsgiven;
    }

    @Override
    public String iud_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String iud_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String implant_old_Query(Date from, Date to) {
        int implantsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"05\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date implantgivenDate = format.parse(date);
                        if(implantgivenDate.after(from)&&implantgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if(isgivenInBetweenDate){
                    implantsgiven = implantsgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+implantsgiven;
    }

    @Override
    public String implant_new_Query(Date from, Date to) {
        int implantsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"05\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date implantgivenDate = format.parse(date);
                        if(implantgivenDate.after(from)&&implantgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if (isgivenInBetweenDate) {
                    implantsgiven = implantsgiven + 1;
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+implantsgiven;
    }

    @Override
    public String implant_unit_totalQuery(Date from, Date to) {
        int implantsgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"05\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date implantgivenDate = format.parse(date);
                        if(implantgivenDate.after(from)&&implantgivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }

                if(isgivenInBetweenDate){
                    implantsgiven = implantsgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+implantsgiven;
    }

    @Override
    public String implant_not_using_any_methodQuery(Date from, Date to) {
        String implantsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        implantsgiven = cursor.getString(0);
        cursor.close();
        return ""+implantsgiven;
    }

    @Override
    public String implant_using_other_methodQuery(Date from, Date to) {
        String implantsgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        implantsgiven = cursor.getString(0);
        cursor.close();
        return ""+implantsgiven;
    }

    @Override
    public String implant_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String implant_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String pm_male_old_Query(Date from, Date to) {
        int pm_malesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"06\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_malegivenDate = format.parse(date);
                        if(pm_malegivenDate.after(from)&&pm_malegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if(isgivenInBetweenDate){
                    pm_malesgiven = pm_malesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_malesgiven;
    }

    @Override
    public String pm_male_new_Query(Date from, Date to) {
        int pm_malesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"06\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_malegivenDate = format.parse(date);
                        if(pm_malegivenDate.after(from)&&pm_malegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if (isgivenInBetweenDate) {
                    pm_malesgiven = pm_malesgiven + 1;
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_malesgiven;
    }

    @Override
    public String pm_male_unit_totalQuery(Date from, Date to) {
        int pm_malesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"06\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_malegivenDate = format.parse(date);
                        if(pm_malegivenDate.after(from)&&pm_malegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }

                if(isgivenInBetweenDate){
                    pm_malesgiven = pm_malesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_malesgiven;
    }

    @Override
    public String pm_male_not_using_any_methodQuery(Date from, Date to) {
        String pm_malesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        pm_malesgiven = cursor.getString(0);
        cursor.close();
        return ""+pm_malesgiven;
    }

    @Override
    public String pm_male_using_other_methodQuery(Date from, Date to) {
        String pm_malesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        pm_malesgiven = cursor.getString(0);
        cursor.close();
        return ""+pm_malesgiven;
    }

    @Override
    public String pm_male_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String pm_male_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String pm_female_old_Query(Date from, Date to) {
        int pm_femalesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"07\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_femalegivenDate = format.parse(date);
                        if(pm_femalegivenDate.after(from)&&pm_femalegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if(isgivenInBetweenDate){
                    pm_femalesgiven = pm_femalesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_femalesgiven;
    }

    @Override
    public String pm_female_new_Query(Date from, Date to) {
        int pm_femalesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"07\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_femalegivenDate = format.parse(date);
                        if(pm_femalegivenDate.after(from)&&pm_femalegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }
                if (isgivenInBetweenDate) {
                    pm_femalesgiven = pm_femalesgiven + 1;
                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_femalesgiven;
    }

    @Override
    public String pm_female_unit_totalQuery(Date from, Date to) {
        int pm_femalesgiven = 0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select instance from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"07\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
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
                    if(name.equalsIgnoreCase("FWMISELCODATE")){
                        String date = instancefield.getString("value");
                        Date pm_femalegivenDate = format.parse(date);
                        if(pm_femalegivenDate.after(from)&&pm_femalegivenDate.before(to)){
                            isgivenInBetweenDate = true;
                        }
                    }
                }

                if(isgivenInBetweenDate){
                    pm_femalesgiven = pm_femalesgiven+ 1;

                }
            }catch (Exception e){

            }
            cursor.moveToNext();
        }
        return ""+pm_femalesgiven;
    }

    @Override
    public String pm_female_not_using_any_methodQuery(Date from, Date to) {
        String pm_femalesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"99\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        cursor.moveToFirst();
        pm_femalesgiven = cursor.getString(0);
        cursor.close();
        return ""+pm_femalesgiven;
    }

    @Override
    public String pm_female_using_other_methodQuery(Date from, Date to) {
        String pm_femalesgiven = ""+0;
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select Count(*) from form_submission where formName = 'mis_elco' and instance like '%{\"name\":\"FWPMISBIRTHCTRL\",\"value\":\"88\",\"source\":\"elco.FWPMISBIRTHCTRL\"}%' and instance like '%{\"name\":\"FWMISBCSOURCE\",\"value\":\"1\",\"source\":\"elco.FWMISBCSOURCE\"}%';");
        cursor.moveToFirst();
        pm_femalesgiven = cursor.getString(0);
        cursor.close();
        return ""+pm_femalesgiven;
    }

    @Override
    public String pm_female_referred_for_methodQuery(Date from, Date to) {
        return "";
    }

    @Override
    public String pm_female_referred_for_side_effectsQuery(Date from, Date to) {
        return "";
    }
}
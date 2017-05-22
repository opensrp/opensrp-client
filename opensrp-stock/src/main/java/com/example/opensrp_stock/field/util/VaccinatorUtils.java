/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.opensrp_stock.field.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.example.opensrp_stock.R;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.domain.Address;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.domain.Obs;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.util.IntegerUtil;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Years;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.Size;
import static org.ei.opensrp.core.utils.Utils.addAsInts;
import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.addToList;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.formatValue;
import static org.ei.opensrp.core.utils.Utils.getColorValue;
import static org.ei.opensrp.core.utils.Utils.getPreference;
import static org.ei.opensrp.core.utils.Utils.getValue;

/**
 * Class containing some static utility methods.
 */
public class VaccinatorUtils {
    public static String providerRolesList(){
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
        try {
            JSONObject u = new JSONObject(context.allSettings().fetchUserInformation());
            return u.getJSONArray("roles").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int totalHHMembers(Map<String, String> household, List<CommonPersonObject> members){
        int originalNum = IntegerUtil.tryParse(getValue(household, "num_household_members", false), -1);
        if (originalNum == -1) return originalNum;

        if (originalNum < members.size()+1){
            return members.size()+1;// plus hhhead
        }
        return originalNum;
    }

    public static int totalHHMembers(Map<String, String> household, int members){
        int originalNum = IntegerUtil.tryParse(getValue(household, "num_household_members", false), -1);
        if (originalNum == -1) return originalNum;

        if (originalNum < members+1){
            return members+1;// plus hhhead
        }
        return originalNum;
    }

    public static int numOfWomenOfRepAge(List<CommonPersonObject> list){
        int i = 0;
        for (CommonPersonObject o: list) {
            int age = -1;
            String gender = getValue(o.getColumnmaps(), "gender", false);
            try{
                age = Years.yearsBetween(new DateTime(getValue(o.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
            }
            catch (Exception e){}
            if (age >= 15 && age <= 45 && (gender.equalsIgnoreCase("female")||gender.equalsIgnoreCase("f"))){
                i++;
            }
        }
        return i;
    }

    public static int numOfChildrenOfImmAge(List<CommonPersonObject> list){
        int i = 0;
        for (CommonPersonObject o: list) {
            int age = -1;
            try{
                age = Years.yearsBetween(new DateTime(getValue(o.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
            }
            catch (Exception e){}
            if (age >= 0 && age <= 5){
                i++;
            }
        }
        return i;
    }

    public static PromptView makePromptable(final Context context, ImageView view, int icon, String label, String goButtonText, String validatorRegex, boolean confirmValue, final DialogInterface.OnClickListener okListener){
        final PromptView prompt = new PromptView(context, label, goButtonText, "Cancel", validatorRegex, confirmValue, okListener);
        view.setImageResource(icon);
        view.setAdjustViewBounds(true);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prompt.show();
            }
        });
        return prompt;
    }

    public static PromptView getPrompt(final Context context, String label, String goButtonText, String validatorRegex, boolean confirmValue, final DialogInterface.OnClickListener okListener){
        return new PromptView(context, label, goButtonText, "Cancel", validatorRegex, confirmValue, okListener);
    }

    public static HashMap<String, String> providerDetails(){
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();

        String locationJson = context.anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationJson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap = locationTree.getLocationsHierarchy();
        Map<String, String> locations = new HashMap<>();
        addToList(locations, locationMap, "country");
        addToList(locations, locationMap, "province");
        addToList(locations, locationMap, "city");
        addToList(locations, locationMap, "town");
        addToList(locations, locationMap, "uc");
        addToList(locations, locationMap, "vaccination center");

        HashMap<String, String> map = new HashMap<>();
        map.put("provider_uc", locations.get("uc"));
        map.put("provider_town", locations.get("town"));
        map.put("provider_city", locations.get("city"));
        map.put("provider_province", locations.get("province"));
        map.put("provider_location_id", locations.get("vaccination center"));
        map.put("provider_location_name", locations.get("vaccination center"));
        map.put("provider_id", context.anmService().fetchDetails().name());

        try {
            JSONObject tm = new JSONObject(getPreference(context.applicationContext(), "team", "{}"));
            map.put("provider_name", tm.getJSONObject("person").getString("display"));
            map.put("provider_identifier", tm.getString("identifier"));
            map.put("provider_team", tm.getJSONObject("team").getString("teamName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static JSONObject providerFullDetails() throws JSONException {
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();

        String locationJson = context.anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationJson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap = locationTree.getLocationsHierarchy();
        Map<String, String> locations = new HashMap<>();
        addToList(locations, locationMap, "country");
        addToList(locations, locationMap, "province");
        addToList(locations, locationMap, "city");
        addToList(locations, locationMap, "town");
        addToList(locations, locationMap, "uc");
        addToList(locations, locationMap, "vaccination center");

        JSONObject map = new JSONObject();
        map.put("provider_uc", locations.get("uc"));
        map.put("provider_town", locations.get("town"));
        map.put("provider_city", locations.get("city"));
        map.put("provider_province", locations.get("province"));
        map.put("provider_location_id", locations.get("vaccination center"));
        map.put("provider_location_name", locations.get("vaccination center"));
        map.put("provider_id", context.anmService().fetchDetails().name());

        JSONObject tm = new JSONObject(getPreference(context.applicationContext(), "team", "{}"));
        if(tm.has("person"))    map.put("provider_name", tm.getJSONObject("person").getString("display"));
        if(tm.has("identifier"))    map.put("provider_identifier", tm.getString("identifier"));
        if(tm.has("team"))    map.put("provider_team", tm.getJSONObject("team").getString("teamName"));

        map.put("anm", new JSONObject(context.anmController().get()));

        String userStr = context.allSettings().fetchUserInformation();
        map.put("user", new JSONObject(userStr));

        return map;
    }

    public static boolean isEligibleWoman(Client client){
        if(StringUtils.isNotBlank(client.getGender()) && client.getGender().toLowerCase().startsWith("f")
                && client.getBirthdate().plusYears(15).isBeforeNow() && client.getBirthdate().plusYears(49).isAfterNow()){
            return true;
        }
        return false;
    }

    public static int profilePicIcon(int ageYears, String gender){
        int profilePic = -1;
        if (StringUtils.isBlank(gender) || ageYears < 0){
            return profilePic;
        }

        gender = gender.toLowerCase();

        if (ageYears < 7 && gender.startsWith("m"))
            profilePic = R.drawable.infant_male;
        else if (ageYears < 7 && gender.startsWith("f"))
            profilePic = R.drawable.infant_female;
        else if (ageYears < 7 && (gender.startsWith("t") || gender.startsWith("i")))
            profilePic = R.drawable.infant_intersex;
        else if (ageYears >= 7 && gender.startsWith("m")) {
            profilePic = R.drawable.household_profile;
        }
        else if (ageYears >= 7 && (gender.startsWith("t") || gender.startsWith("i"))) {
            profilePic = R.drawable.general_intersex;
        }
        else if (ageYears >= 7 && gender.startsWith("f"))
            profilePic = R.drawable.pk_woman_icon;

        return profilePic;
    }

    public static void fillWomanTTEnrollmentOverrides(Map<String, String> map, ClientEvent data){
        Client client = data.getClient();
        String birthdate = client.getBirthdate().toString("yyyy-MM-dd");

        map.put("first_name", client.getFirstName());
        map.put("father_name", data.findObsValue(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        map.put("birth_date", birthdate);
        map.put("gender", "female");
        map.put("ethnicity", data.findObsValue(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        map.put("contact_phone_number", data.findObsValue(null, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        Address ad = client.getAddress("usual_residence");

        map.put("province", ad.getStateProvince());
        map.put("city_village", ad.getCityVillage());
        map.put("town", ad.getTown());
        map.put("union_council", ad.getSubTown());
        map.put("address1", ad.getAddressField("address1"));

        String husb = data.findObsValue(null, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        map.put("husband_name", husb);
        map.put("marriage", StringUtils.isNotBlank(husb)?"yes":"no");
    }

    public static void fillChildVaccineEnrollmentOverrides(Map<String, String> map, ClientEvent data){
        Client client = data.getClient();
        String birthdate = client.getBirthdate().toString("yyyy-MM-dd");

        map.put("first_name", client.getFirstName());
        map.put("mother_name", data.findObsValue(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        map.put("father_name", data.findObsValue(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        map.put("birth_date", birthdate);
        map.put("gender", StringUtils.isNotBlank(client.getGender())?client.getGender().toLowerCase():"");
        map.put("ethnicity", data.findObsValue(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
        map.put("contact_phone_number", data.findObsValue(null, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

        Address ad = client.getAddress("usual_residence");

        map.put("province", ad.getStateProvince());
        map.put("city_village", ad.getCityVillage());
        map.put("town", ad.getTown());
        map.put("union_council", ad.getSubTown());
        map.put("address1", ad.getAddressField("address1"));
    }

    private static HashMap<String, String> getPreloadTTVaccineData(ClientEvent data) throws JSONException, ParseException {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", data.findObsValue(null, true, "tt1", "tt1_retro"));
        map.put("e_tt2", data.findObsValue(null, true, "tt2", "tt2_retro"));
        map.put("e_tt3", data.findObsValue(null, true, "tt3", "tt3_retro"));
        map.put("e_tt4", data.findObsValue(null, true, "tt4", "tt4_retro"));
        map.put("e_tt5", data.findObsValue(null, true, "tt5", "tt5_retro"));

        return map;
    }

    public static void fillWomanTTOffsiteOverrides(Map<String, String> map, ClientEvent data){
        Client client = data.getClient();
        map.put("existing_full_address", client.getAddress("usual_residence").getAddressField("address1")+
                ", UC: "+client.getAddress("usual_residence").getSubTown()+
                ", Town: "+client.getAddress("usual_residence").getTown()+
                ", City: "+client.getAddress("usual_residence").getCityVillage()+
                " - "+client.getAddress("usual_residence").getAddressField("landmark"));

        map.put("existing_program_client_id", client.getIdentifier("Program Client ID"));
        map.put("program_client_id", client.getIdentifier("Program Client ID"));

        String birthdate = client.getBirthdate().toString("yyyy-MM-dd");
        map.put("existing_first_name", client.getFirstName());
        map.put("existing_birth_date", birthdate);
        int years = 0;
        try{
            years = Years.yearsBetween(client.getBirthdate(), DateTime.now()).getYears();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        map.put("existing_age", years+"");
        Object epi = client.getAttribute("EPI Card Number");
        map.put("existing_epi_card_number", epi == null ? "" : epi.toString());

        try{
            String fatherName = data.findObsValue(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            map.put("first_name", client.getFirstName());
            map.put("father_name", fatherName);
            map.put("birth_date", birthdate);
            map.put("gender", "female");

            String ethnicity = data.findObsValue(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            map.put("ethnicity", ethnicity);

            Address ad = client.getAddress("usual_residence");

            map.put("province", ad.getStateProvince());
            map.put("city_village", ad.getCityVillage());
            map.put("town", ad.getTown());
            map.put("union_council", ad.getSubTown());
            map.put("address1", ad.getAddressField("address1"));

            String husb = data.findObsValue(null, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            map.put("husband_name", husb);
            map.put("marriage", StringUtils.isNotBlank(husb)?"yes":"no");
            map.put("reminders_approval", data.findObsValue(null, true, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("contact_phone_number", data.findObsValue(null, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.putAll(getPreloadTTVaccineData(data));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void fillChildVaccineOffsiteOverrides(Map<String, String> map, ClientEvent data){
        Client client = data.getClient();
        map.put("existing_full_address", client.getAddress("usual_residence").getAddressField("address1")+
                ", UC: "+client.getAddress("usual_residence").getSubTown()+
                ", Town: "+client.getAddress("usual_residence").getTown()+
                ", City: "+client.getAddress("usual_residence").getCityVillage()+
                " - "+client.getAddress("usual_residence").getAddressField("landmark"));

        map.put("existing_program_client_id", client.getIdentifier("Program Client ID"));
        map.put("program_client_id", client.getIdentifier("Program Client ID"));

        String birthdate = client.getBirthdate().toString("yyyy-MM-dd");
        map.put("existing_first_name", client.getFirstName());
        map.put("existing_birth_date", birthdate);
        int days = 0;
        try{
            days = Days.daysBetween(client.getBirthdate(), DateTime.now()).getDays();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        map.put("existing_age", days+"");
        Object epi = client.getAttribute("EPI Card Number");
        map.put("existing_epi_card_number", epi == null ? "" : epi.toString());

        try{
            String fatherName = data.findObsValue(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            map.put("first_name", client.getFirstName());
            map.put("existing_first_name", client.getFirstName());
            map.put("father_name", fatherName);
            map.put("mother_name", data.findObsValue(null, true, "mother_name", "1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("birth_date", birthdate);
            map.put("dob", birthdate);

            if (client.getGender() != null) {
                map.put("gender", client.getGender().toLowerCase());
            }

            String ethnicity = data.findObsValue(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            map.put("ethnicity", ethnicity);

            Address ad = client.getAddress("usual_residence");

            map.put("province", ad.getStateProvince());
            map.put("city_village", ad.getCityVillage());
            map.put("town", ad.getTown());
            map.put("union_council", ad.getSubTown());
            map.put("address1", ad.getAddressField("address1"));

            map.put("reminders_approval", data.findObsValue(null, true, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("contact_phone_number", data.findObsValue(null, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.putAll(getPreloadChildVaccineData(data));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Map<? extends String,? extends String> getPreloadChildVaccineData(ClientEvent data) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_bcg", data.findObsValue(null, true, "bcg", "bcg_retro"));
        map.put("e_opv0", data.findObsValue(null, true, "opv0", "opv0_retro"));
        map.put("e_penta1", data.findObsValue(null, true, "penta1", "penta1_retro"));
        map.put("e_opv1", data.findObsValue(null, true, "opv1", "opv1_retro"));
        map.put("e_pcv1", data.findObsValue(null, true, "pcv1", "pcv1_retro"));
        map.put("e_penta2", data.findObsValue(null, true, "penta2", "penta2_retro"));
        map.put("e_opv2", data.findObsValue(null, true, "opv2", "opv2_retro"));
        map.put("e_pcv2", data.findObsValue(null, true, "pcv2", "pcv2_retro"));
        map.put("e_penta3", data.findObsValue(null, true, "penta3", "penta3_retro"));
        map.put("e_opv3", data.findObsValue(null, true, "opv3", "opv3_retro"));
        map.put("e_pcv3", data.findObsValue(null, true, "pcv3", "pcv3_retro"));
        map.put("e_ipv", data.findObsValue(null, true, "ipv", "ipv_retro"));
        map.put("e_measles1", data.findObsValue(null, true, "measles1", "measles1_retro"));
        map.put("e_measles2", data.findObsValue(null, true, "measles2", "measles2_retro"));

        return map;
    }

    public static String getObsValue(CESQLiteHelper cedb, Client client, boolean humanize, String... fields) throws JSONException, ParseException {
        List<Obs> ol = cedb.getObs(client.getBaseEntityId(), null, "eventDate DESC", fields);
        if(ol == null || ol.size() == 0){
            return "";
        }
        return formatValue(ol.get(0).getValue(true), humanize);
    }

    public static ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type){
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    public static int getWasted(String startDate, String endDate, String type, String... variables){
        List<CommonPersonObject> cl = org.ei.opensrp.Context.getInstance().commonrepository("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE `report` ='" + type + "' and `date` between '" + startDate + "' and '" + endDate + "'", null, "stock");
        int total = 0;
        for (CommonPersonObject c : cl) {
            for (String v : variables){
                String val = getValue(c.getDetails(), v, "0", false);
                total += IntegerUtil.tryParse(val, 0);
            }
        }
        return total;
    }

    public static String calculateWasted(int balanceInHand, int received, int used, List<CommonPersonObject> nextMonthMap, String... types){
        if (nextMonthMap == null || nextMonthMap.isEmpty()) return "N/A";

        int inhandNextMonth = addAsInts(true, null, "_balance_in_hand", nextMonthMap.get(0).getColumnmaps(), types);
        int endingBalance = balanceInHand+received-used;
        //inhand+received-used-inhand.of.next.month
        int wasted = endingBalance-inhandNextMonth;

        return wasted+ " <font color='silver'> = ["+endingBalance+"-"+inhandNextMonth+"]";
    }

    public static String calculateEndingBalance(int balanceInHand, int received, int used){
        int endingBalance = balanceInHand+received-used;
        return endingBalance+ " <font color='silver'> = ["+(received+balanceInHand)+"-"+used +"]";
    }

    public static String calculateStartingBalance(int balanceInHand, int received){
        int strtingBalance = balanceInHand+received;
        return strtingBalance+ " <font color='silver'> = ["+received+"+"+balanceInHand +"]";
    }

    public static ArrayList<HashMap<String, String>> getUsed(String startDate, String endDate, String table, String... vaccines){
        String q = "SELECT * FROM (";
        for (String v: vaccines) {
            q += " (select count(*) "+v+" from "+table+" where "+v+" between '" + startDate + "' and '" + endDate + "') "+v+" , ";
        }
        q = q.trim().substring(0, q.trim().lastIndexOf(","));
        q += " ) e ";

        Log.i("DD", q);
        return org.ei.opensrp.Context.getInstance().commonrepository(table).rawQuery(q);
    }

    public static int getTotalUsed(String startDate, String endDate, String table, String... vaccines) {
        int totalUsed = 0;

        for (HashMap<String, String> v : getUsed(startDate, endDate, table, vaccines)) {
            for (String k : v.keySet()) {
                totalUsed += Integer.parseInt(v.get(k) == null ? "0" : v.get(k));
            }
        }
        Log.i("", "TOTAL USED: " + totalUsed);

        return totalUsed;
    }

    public static void addStatusTag(Activity context, TableLayout table, String tag, boolean hrLine){
        TableRow tr = new TableRow(context);
        if(hrLine) {
            tr.setBackgroundColor(Color.LTGRAY);
            tr.setPadding(1, 1, 1, 1);
            table.addView(tr);
        }
       addRow(context, table, tag, 2, Size.MEDIUM);
    }
    public static void addVaccineDetail(Activity context, TableLayout table, String status, VaccineRepo.Vaccine vaccine, DateTime vaccineDate, Alert alert, boolean compact) {
        addVaccineDetail(context, table, status, null, vaccine.display(), vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "", alert, Size.MEDIUM);
    }

    public static void addVaccineDetail(Activity context, TableLayout table, String status, VaccineRepo.Vaccine vaccine, DateTime vaccineDate,
                                        Alert alert, Size size) {
        addVaccineDetail(context, table, status, null, vaccine.display(), vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "",
                alert, size);
    }

    public static void addVaccineDetail(Activity context, TableLayout table, String status, String milestone, String vaccine, String vaccineDate,
                                        Alert alert, Size size){
        String color = "#ffffff";
        if(status.equalsIgnoreCase("due")) {
            if(alert != null){
                color = getColorValue(context, alert.status());
                vaccineDate = "due: "+convertDateFormat(vaccineDate, true)+"";
            }
            else if(StringUtils.isNotBlank(vaccineDate)){
                color = getColorValue(context, AlertStatus.inProcess);
                vaccineDate = "due: "+convertDateFormat(vaccineDate, true)+"";
            }
        }
        else if(status.equalsIgnoreCase("done")){
            color = "#31B404";
            vaccineDate = convertDateFormat(vaccineDate, true);
        }
        else if(status.equalsIgnoreCase("expired")){
            color = getColorValue(context, AlertStatus.inProcess);
            vaccineDate = "exp: "+convertDateFormat(vaccineDate, true)+"";
        }

        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setVerticalGravity(Gravity.CENTER_VERTICAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        l.setPadding(10, 4, 0, 4);

        Button s = new Button(context);
        TableRow.LayoutParams blp = new TableRow.LayoutParams(15, 15);
        blp.setMargins(30, 6, 5, 5);
        s.setLayoutParams(blp);
        s.setGravity(Gravity.CENTER_VERTICAL);
        s.setBackgroundColor(StringUtils.isBlank(color)?Color.WHITE:Color.parseColor(color));
        l.addView(s);

        TextView v = new TextView(context);
        v.setText(vaccineDate);
        v.setTextSize(17);
        v.setPadding(10, 4, 20, 5);
        v.setTextColor(Color.BLACK);
        v.setBackgroundColor(Color.WHITE);
        l.addView(v);

        if (milestone == null) {
            addRow(context, table, vaccine, l, size);
        }
        else{
            addVaccineRow(context, table, color, milestone, vaccine, vaccineDate, Size.MEDIUM);
        }
    }


    public static void addVaccineRow(Activity activity, TableLayout table, String color, String milestone, String label, String vaccineDate, Size size){
        if(size.equals(Size.MEDIUM)){
            TableRow v = (TableRow) activity.getLayoutInflater().inflate(R.layout.child_vaccine_row, null);
            ((TextView)v.findViewById(R.id.milestone)).setText(milestone);
            ((TextView)v.findViewById(R.id.vaccine)).setText(label);
            ((TextView)v.findViewById(R.id.value)).setText(vaccineDate);
            v.findViewById(R.id.status).setBackgroundColor(StringUtils.isBlank(color)?Color.WHITE:Color.parseColor(color));

            table.addView(v);
        }
    }

    private static DateTime getReceivedDate(Map<String, String> received, VaccineRepo.Vaccine v){
        if (received.get(v.name()) != null){
            return new DateTime(received.get(v.name()));
        }
        else if(received.get(v.name()+"_retro") != null){
            return new DateTime(received.get(v.name()+"_retro"));
        }

        return null;
    }

    public static List<Map<String, Object>> generateSchedule(CommonPersonObjectClient client, String category, DateTime milestoneDate, Map<String, String> received, List<Alert> alerts){
        ArrayList<VaccineRepo.Vaccine> vl = VaccineRepo.getVaccines(category);
        List<Map<String, Object>> schedule = new ArrayList();
        for (VaccineRepo.Vaccine v: vl) {
            Map<String, Object> m = new HashMap<>();
            DateTime recDate = getReceivedDate(received, v);
            if(recDate == null){
                recDate = getOAVaccineDate(client, v);
            }
            if (recDate != null) {
                m = createVaccineMap("done", null, recDate, v);
            }
            else if(milestoneDate != null && milestoneDate.plusDays(v.expiryDays()).isBefore(DateTime.now())){
                m = createVaccineMap("expired", null, milestoneDate.plusDays(v.expiryDays()), v);
            }
            else if (alerts.size() > 0) {
                for (Alert a : alerts) {
                    if (a.scheduleName().replaceAll(" ", "").equalsIgnoreCase(v.name())
                            || a.visitCode().replaceAll(" ", "").equalsIgnoreCase(v.name())) {
                        m = createVaccineMap("due", a, new DateTime(a.startDate()), v);
                    }
                }
            }

            if (m.isEmpty()) {
                if (v.prerequisite() != null) {
                    DateTime prereq = getReceivedDate(received, v.prerequisite());
                    if (prereq != null) {
                        prereq = prereq.plusDays(v.prerequisiteGapDays());
                        m = createVaccineMap("due", null, prereq, v);
                    }
                    else {
                        m = createVaccineMap("na", null, null, v);//it was due before
                    }
                } else if(milestoneDate != null){
                    m = createVaccineMap("due", null, milestoneDate.plusDays(v.milestoneGapDays()), v);
                }
                else {
                    m = createVaccineMap("na", null, null, v);
                }
            }

            schedule.add(m);
        }

        return schedule;
    }

    private static DateTime getOAVaccineDate(CommonPersonObjectClient client, VaccineRepo.Vaccine v) {
        if (StringUtils.isNotBlank(client.getColumnmaps().get("e_"+v.name()))){
            return new DateTime(client.getColumnmaps().get("e_"+v.name()));
        }
        else if(StringUtils.isNotBlank(client.getDetails().get("e_"+v.name()))){
            return new DateTime(client.getDetails().get("e_"+v.name()));
        }

        return null;
    }

    public static final Comparator<Map<String, ?>> VACCINE_SCHEDULE_COMPARATOR = new Comparator<Map<String, ?>>() {
        @Override
        public int compare(Map<String, ?> lhs, Map<String, ?> rhs) {
            VaccineRepo.Vaccine v1 = (VaccineRepo.Vaccine) lhs.get("vaccine");
            VaccineRepo.Vaccine v2 = (VaccineRepo.Vaccine) rhs.get("vaccine");
            if (v1.milestoneGapDays() == v2.milestoneGapDays()) return 0;
            if (v1.milestoneGapDays() > v2.milestoneGapDays()) return 1;

            return -1;
        }
    };

    public static Map<String, ?> getVaccination(List<Map<String, Object>> sch, VaccineRepo.Vaccine vaccine){
        for (Map<String, ?> m : sch){
            if (((VaccineRepo.Vaccine)m.get("vaccine")).name().equalsIgnoreCase(vaccine.name())){
                return m;
            }
        }
        return null;
    }

    public static void addVaccineRow(Activity activity, TableLayout parent, String milestone, List<Map<String, Object>> schedule, Size size, VaccineRepo.Vaccine... vaccines){
        if(size.equals(Size.MEDIUM)){
            int i = 0;
            for (VaccineRepo.Vaccine vac: vaccines) {
                Map<String, ?> map = getVaccination(schedule, vac);
                addVaccineDetail(activity, parent, map.get("status").toString(), i==0?milestone:" ",
                        ((VaccineRepo.Vaccine)map.get("vaccine")).display(), map.get("date")==null?null:((DateTime)map.get("date")).toString("yyyy-MM-dd"), (Alert)map.get("alert"), Size.MEDIUM);
                i++;
            }
        }
    }

    private static Map<String, Object> createVaccineMap(String status, Alert a, DateTime date, VaccineRepo.Vaccine v){
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("alert", a);
        m.put("date", date);
        m.put("vaccine", v);

        return m;
    }

    public static Map<String, Object> nextVaccineDue(List<Map<String, Object>> schedule, Date lastVisit){
        Map<String, Object> v = null;
        for (Map<String, Object> m: schedule) {
            if(m != null && m.get("status") != null && m.get("status").toString().equalsIgnoreCase("due")){
                if (v == null) {
                    v = m;
                } else if (m.get("date") != null && v.get("date") != null
                        && ((DateTime) m.get("date")).isBefore((DateTime) v.get("date"))
                        && (lastVisit == null
                            || lastVisit.before(((DateTime) m.get("date")).toDate()))) {
                    v = m;
                }
            }
        }
        return v;
    }
}

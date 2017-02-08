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

package org.ei.opensrp.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.Obs;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.immunization.application.VaccineRepo;

import static org.ei.opensrp.immunization.application.VaccineRepo.*;
import org.joda.time.DateTime;
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

import static org.ei.opensrp.core.utils.Utils.*;

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
        return null;
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
        map.put("provider_name", tm.getJSONObject("person").getString("display"));
        map.put("provider_identifier", tm.getString("identifier"));
        map.put("provider_team", tm.getJSONObject("team").getString("teamName"));

        map.put("anm", new JSONObject(context.anmController().get()));

        String userStr = context.allSettings().fetchUserInformation();
        map.put("user", new JSONObject(userStr));

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
    public static void addVaccineDetail(Activity context, TableLayout table, String status, Vaccine vaccine, DateTime vaccineDate, Alert alert, boolean compact) {
        addVaccineDetail(context, table, status, null, vaccine.display(), vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "", alert, Size.MEDIUM);
    }

    public static void addVaccineDetail(Activity context, TableLayout table, String status, Vaccine vaccine, DateTime vaccineDate,
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

    public static List<Map<String, Object>> generateSchedule(String category, DateTime milestoneDate, Map<String, String> received, List<Alert> alerts){
        ArrayList<Vaccine> vl = VaccineRepo.getVaccines(category);
        List<Map<String, Object>> schedule = new ArrayList();
        for (Vaccine v: vl) {
            Map<String, Object> m = new HashMap<>();
            DateTime recDate = getReceivedDate(received, v);
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

    public static final Comparator<Map<String, ?>> VACCINE_SCHEDULE_COMPARATOR = new Comparator<Map<String, ?>>() {
        @Override
        public int compare(Map<String, ?> lhs, Map<String, ?> rhs) {
            Vaccine v1 = (Vaccine) lhs.get("vaccine");
            Vaccine v2 = (Vaccine) rhs.get("vaccine");
            if (v1.milestoneGapDays() == v2.milestoneGapDays()) return 0;
            if (v1.milestoneGapDays() > v2.milestoneGapDays()) return 1;

            return -1;
        }
    };

    public static Map<String, ?> getVaccination(List<Map<String, Object>> sch, Vaccine vaccine){
        for (Map<String, ?> m : sch){
            if (((Vaccine)m.get("vaccine")).name().equalsIgnoreCase(vaccine.name())){
                return m;
            }
        }
        return null;
    }

    public static void addVaccineRow(Activity activity, TableLayout parent, String milestone, List<Map<String, Object>> schedule, Size size, Vaccine... vaccines){
        if(size.equals(Size.MEDIUM)){
            int i = 0;
            for (Vaccine vac: vaccines) {
                Map<String, ?> map = getVaccination(schedule, vac);
                addVaccineDetail(activity, parent, map.get("status").toString(), i==0?milestone:" ",
                        ((VaccineRepo.Vaccine)map.get("vaccine")).display(), map.get("date")==null?null:((DateTime)map.get("date")).toString("yyyy-MM-dd"), (Alert)map.get("alert"), Size.MEDIUM);
                i++;
            }
        }
    }

    private static Map<String, Object> createVaccineMap(String status, Alert a, DateTime date, Vaccine v){
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

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

package util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.db.VaccineRepo.Vaccine;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.fragment.UndoVaccinationDialogFragment;
import org.ei.opensrp.path.fragment.VaccinationDialogFragment;
import org.ei.opensrp.util.IntegerUtil;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static util.Utils.addToList;
import static util.Utils.addToRow;
import static util.Utils.convertDateFormat;
import static util.Utils.getColorValue;
import static util.Utils.getPreference;
import static util.Utils.getValue;

/**
 * Class containing some static utility methods.
 */
public class VaccinatorUtils {
    private static final String TAG = "VaccinatorUtils";

    public static HashMap<String, String> providerDetails() {
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
        org.ei.opensrp.util.Log.logDebug("ANM DETAILS" + context.anmController().get());
        org.ei.opensrp.util.Log.logDebug("USER DETAILS" + context.allSettings().fetchUserInformation());
        org.ei.opensrp.util.Log.logDebug("TEAM DETAILS" + getPreference(context.applicationContext(), "team", "{}"));

        String locationJson = context.anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationJson, LocationTree.class);

        HashMap<String, String> map = new HashMap<>();

        if (locationTree != null) {
            Map<String, TreeNode<String, Location>> locationMap = locationTree.getLocationsHierarchy();
            Map<String, String> locations = new HashMap<>();
            addToList(locations, locationMap, "country");
            addToList(locations, locationMap, "province");
            addToList(locations, locationMap, "city");
            addToList(locations, locationMap, "town");
            addToList(locations, locationMap, "uc");
            addToList(locations, locationMap, "vaccination center");

            map.put("provider_uc", locations.get("uc"));
            map.put("provider_town", locations.get("town"));
            map.put("provider_city", locations.get("city"));
            map.put("provider_province", locations.get("province"));
            map.put("provider_location_id", locations.get("vaccination center"));
            map.put("provider_location_name", locations.get("vaccination center"));
            map.put("provider_id", context.anmService().fetchDetails().name());
        }

        try {
            JSONObject tm = new JSONObject(getPreference(context.applicationContext(), "team", "{}"));
            map.put("provider_name", tm.getJSONObject("person").getString("display"));
            map.put("provider_identifier", tm.getString("identifier"));
            map.put("provider_team", tm.getJSONObject("team").getString("teamName"));
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }

        return map;
    }

    public static ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type) {
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='" + type + "' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    public static int getWasted(String startDate, String endDate, String type, String... variables) {
        List<CommonPersonObject> cl = org.ei.opensrp.Context.getInstance().commonrepository("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE `report` ='" + type + "' and `date` between '" + startDate + "' and '" + endDate + "'", null, "stock");
        int total = 0;
        for (CommonPersonObject c : cl) {
            for (String v : variables) {
                String val = getValue(c.getDetails(), v, "0", false);
                total += IntegerUtil.tryParse(val, 0);
            }
        }
        return total;
    }

    public static ArrayList<HashMap<String, String>> getUsed(String startDate, String endDate, String table, String... vaccines) {
        String q = "SELECT * FROM (";
        for (String v : vaccines) {
            q += " (select count(*) " + v + " from " + table + " where " + v + " between '" + startDate + "' and '" + endDate + "') " + v + " , ";
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

    public static void addStatusTag(Context context, TableLayout table, String tag, boolean hrLine) {
        TableRow tr = new TableRow(context);
        if (hrLine) {
            tr.setBackgroundColor(Color.LTGRAY);
            tr.setPadding(1, 1, 1, 1);
            table.addView(tr);
        }
        tr = addToRow(context, Html.fromHtml("<b>" + tag + "</b>"), new TableRow(context), true, 1);
        tr.setPadding(15, 5, 0, 0);
        table.addView(tr);
    }


    public static void addVaccineDetail(final Context context, TableLayout table, final VaccineWrapper vaccineWrapper) {
        TableRow tr = (TableRow) ((Activity) context).getLayoutInflater().inflate(R.layout.vaccinate_row_view, null);
        tr.setGravity(Gravity.CENTER_VERTICAL);
        tr.setTag(vaccineWrapper.getId());

        RelativeLayout relativeLayout = (RelativeLayout) tr.findViewById(R.id.vacc_status_layout);
        if (vaccineWrapper.isCompact()) {
            relativeLayout.setPadding(dpToPx(context, 0f), dpToPx(context, 7.5f), dpToPx(context, 0f), dpToPx(context, 7.5f));
        } else {
            relativeLayout.setPadding(dpToPx(context, 0f), dpToPx(context, 10f), dpToPx(context, 0f), dpToPx(context, 10f));
        }


        TextView label = (TextView) tr.findViewById(R.id.vaccine);
        label.setText(vaccineWrapper.getVaccine().display());

        String vaccineDate = "";
        String color = "#ffffff";
        if (vaccineWrapper.getStatus().equalsIgnoreCase("due")) {
            if (vaccineWrapper.getAlert() != null) {
                color = getColorValue(context, vaccineWrapper.getAlert().status());
                vaccineDate = "due: " + convertDateFormat(vaccineWrapper.getVaccineDateAsString(), true) + "";
                addVaccinationDialogHook(context, tr, vaccineWrapper, color, vaccineDate);
            } else if (StringUtils.isNotBlank(vaccineWrapper.getVaccineDateAsString())) {
                color = getColorValue(context, AlertStatus.inProcess);
                vaccineDate = "due: " + convertDateFormat(vaccineWrapper.getVaccineDateAsString(), true) + "";
                addVaccinationDialogHook(context, tr, vaccineWrapper, color, vaccineDate);
            }
        } else if (vaccineWrapper.getStatus().equalsIgnoreCase("done")) {
            color = "#31B404";
            vaccineDate = convertDateFormat(vaccineWrapper.getVaccineDateAsString(), true);
        } else if (vaccineWrapper.getStatus().equalsIgnoreCase("expired")) {
            color = getColorValue(context, AlertStatus.inProcess);
            vaccineDate = "exp: " + convertDateFormat(vaccineWrapper.getVaccineDateAsString(), true) + "";
            addVaccinationDialogHook(context, tr, vaccineWrapper, color, vaccineDate);
        }

        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setVerticalGravity(Gravity.CENTER_VERTICAL);
        l.setGravity(Gravity.CENTER_VERTICAL);

        Button s = (Button) tr.findViewById(R.id.status);
        s.setBackgroundColor(StringUtils.isBlank(color) ? Color.WHITE : Color.parseColor(color));

        TextView v = (TextView) tr.findViewById(R.id.date);
        v.setText(vaccineDate);

        Button u = (Button) tr.findViewById(R.id.undo);
        FrameLayout.LayoutParams ulp = (FrameLayout.LayoutParams) u.getLayoutParams();
        if (vaccineWrapper.isCompact()) {
            ulp.width = dpToPx(context, 70f);
            ulp.height = dpToPx(context, 35f);
            u.setLayoutParams(ulp);
        } else {
            ulp.width = dpToPx(context, 65f);
            ulp.height = dpToPx(context, 40f);
            u.setLayoutParams(ulp);
        }

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(UndoVaccinationDialogFragment.DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                UndoVaccinationDialogFragment undoVaccinationDialogFragment = UndoVaccinationDialogFragment.newInstance(vaccineWrapper);
                undoVaccinationDialogFragment.show(ft, UndoVaccinationDialogFragment.DIALOG_TAG);
            }
        });

        if (table.getChildCount() > 0 && StringUtils.isNotBlank(vaccineWrapper.getId()) && StringUtils.isNotBlank(vaccineWrapper.getPreviousVaccineId())) {
            if (!vaccineWrapper.getId().equals(vaccineWrapper.getId())) {
                View view = new View(context);
                view.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, dpToPx(context, 10f)));

                table.addView(view);
            }
        }

        table.addView(tr);
    }

    private static void addVaccinationDialogHook(final Context context, TableRow tr, final VaccineWrapper vaccineWrapper, String color, String formattedDate) {

        if (VaccinateActionUtils.addDialogHookCustomFilter(vaccineWrapper)) {
            vaccineWrapper.setColor(color);
            vaccineWrapper.setFormattedVaccineDate(formattedDate);

            tr.setOnClickListener(new TableRow.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(VaccinationDialogFragment.DIALOG_TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    ArrayList<VaccineWrapper> list = new ArrayList<VaccineWrapper>();
                    list.add(vaccineWrapper);
                    VaccinationDialogFragment vaccinationDialogFragment = VaccinationDialogFragment.newInstance(null, list);
                    vaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);

                }
            });
        }
    }


    private static DateTime getReceivedDate(Map<String, String> received, Vaccine v) {
        if (received.get(v.name()) != null) {
            return new DateTime(received.get(v.name()));
        } else if (received.get(v.name() + "_retro") != null) {
            return new DateTime(received.get(v.name() + "_retro"));
        }
        return null;
    }

    public static List<Map<String, Object>> generateSchedule(String category, DateTime milestoneDate, Map<String, String> received, List<Alert> alerts) {
        ArrayList<Vaccine> vl = VaccineRepo.getVaccines(category);
        List<Map<String, Object>> schedule = new ArrayList();
        for (Vaccine v : vl) {
            Map<String, Object> m = new HashMap<>();
            DateTime recDate = getReceivedDate(received, v);
            if (recDate != null) {
                m = createVaccineMap("done", null, recDate, v);
            } else if (milestoneDate != null && v.expiryDays() > 0 && milestoneDate.plusDays(v.expiryDays()).isBefore(DateTime.now())) {
                m = createVaccineMap("expired", null, milestoneDate.plusDays(v.expiryDays()), v);
            } else if (alerts.size() > 0) {
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
                    } else {
                        m = createVaccineMap("due", null, null, v);
                    }
                } else if (milestoneDate != null) {
                    m = createVaccineMap("due", null, milestoneDate.plusDays(v.milestoneGapDays()), v);
                } else {
                    m = createVaccineMap("na", null, null, v);
                }
            }

            schedule.add(m);
        }
        return schedule;
    }

    public static List<Map<String, Object>> generateScheduleList(String category, DateTime milestoneDate, Map<String, Date> received, List<Alert> alerts) {
        ArrayList<Vaccine> vl = VaccineRepo.getVaccines(category);
        List<Map<String, Object>> schedule = new ArrayList();
        for (Vaccine v : vl) {
            Map<String, Object> m = new HashMap<>();
            Date recDate = received.get(v.display().toLowerCase());
            if (recDate != null) {
                m = createVaccineMap("done", null, new DateTime(recDate), v);
            } else if (alerts.size() > 0) {
                for (Alert a : alerts) {
                    if (a.scheduleName().replaceAll(" ", "").equalsIgnoreCase(v.name())
                            || a.visitCode().replaceAll(" ", "").equalsIgnoreCase(v.name())) {
                        m = createVaccineMap("due", a, new DateTime(a.startDate()), v);
                    }
                }
            }

            if (m.isEmpty()) {
                if (v.prerequisite() != null) {
                    Date prereq = received.get(v.prerequisite().display().toLowerCase());
                    if (prereq != null) {
                        DateTime prereqDateTime = new DateTime(prereq);
                        prereqDateTime = prereqDateTime.plusDays(v.prerequisiteGapDays());
                        m = createVaccineMap("due", null, prereqDateTime, v);
                    } else {
                        m = createVaccineMap("due", null, null, v);
                    }
                } else if (milestoneDate != null) {
                    m = createVaccineMap("due", null, milestoneDate.plusDays(v.milestoneGapDays()), v);
                } else {
                    m = createVaccineMap("na", null, null, v);
                }
            }

            schedule.add(m);
        }
        return schedule;
    }

    private static Map<String, Object> createVaccineMap(String status, Alert a, DateTime date, Vaccine v) {
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("alert", a);
        m.put("date", date);
        m.put("vaccine", v);

        return m;
    }

    public static Map<String, Object> nextVaccineDue(List<Map<String, Object>> schedule, Date lastVisit) {
        Map<String, Object> v = null;
        for (Map<String, Object> m : schedule) {
            if (m != null && m.get("status") != null && m.get("status").toString().equalsIgnoreCase("due")) {
                if (m.get("vaccine") != null && (((Vaccine) m.get("vaccine")).equals(Vaccine.bcg2) || ((Vaccine) m.get("vaccine")).equals(Vaccine.ipv))) {
                    // bcg2 is a special alert and should not be considered as the next vaccine
                    continue;
                }

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

    public static Map<String, Object> nextVaccineDue(List<Map<String, Object>> schedule, List<Vaccine> vaccineList) {
        Map<String, Object> v = null;
        for (Map<String, Object> m : schedule) {
            if (m != null && m.get("status") != null && m.get("status").toString().equalsIgnoreCase("due")) {
                if (m.get("vaccine") != null && (((Vaccine) m.get("vaccine")).equals(Vaccine.bcg2) || ((Vaccine) m.get("vaccine")).equals(Vaccine.ipv))) {
                    // bcg2 is a special alert and should not be considered as the next vaccine
                    continue;
                }

                if (v == null) {
                    if (m.get("vaccine") != null && vaccineList.contains((Vaccine) m.get("vaccine"))) {
                        v = m;
                    }
                } else if (v.get("alert") == null && m.get("alert") != null) {
                    if (m.get("vaccine") != null && vaccineList.contains((Vaccine) m.get("vaccine"))) {
                        v = m;
                    }
                } else if (v.get("alert") != null && m.get("alert") != null) {
                    if (m.get("vaccine") != null && vaccineList.contains((Vaccine) m.get("vaccine"))) {
                        Alert vAlert = (Alert) v.get("alert");
                        Alert mAlert = (Alert) m.get("alert");
                        if (!vAlert.status().equals(AlertStatus.urgent)) {
                            if (vAlert.status().equals(AlertStatus.upcoming)) {
                                if (mAlert.status().equals(AlertStatus.normal) || mAlert.status().equals(AlertStatus.urgent)) {
                                    v = m;
                                }
                            } else if (vAlert.status().equals(AlertStatus.normal)) {
                                if (mAlert.status().equals(AlertStatus.urgent)) {
                                    v = m;
                                }
                            }
                        }
                    }
                }
            }
        }
        return v;
    }

    public static int dpToPx(Context context, float dpValue) {
        Resources r = context.getResources();
        float val = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        return new Float(val).intValue();
    }

    /**
     * Returns a JSON String containing a list of supported vaccines
     *
     * @param context Current valid context to be used
     * @return JSON String with the supported vaccines or NULL if unable to obtain the list
     */
    public static String getSupportedVaccines(Context context) {
        String supportedVaccinesString = Utils.readAssetContents(context, "vaccines.json");
        return supportedVaccinesString;
    }

    /**
     * Returns a JSON String containing a list of supported services
     *
     * @param context Current valid context to be used
     * @return JSON String with the supported vaccines or NULL if unable to obtain the list
     */
    public static String getSupportedServices(Context context) {
        String supportedServicesString = Utils.readAssetContents(context, "services.json");
        return supportedServicesString;
    }

    public static int getVaccineCalculation(Context context, String vaccineName)
            throws JSONException {
        JSONArray supportedVaccines = new JSONArray(getSupportedVaccines(context));
        for (int i = 0; i < supportedVaccines.length(); i++) {
            JSONObject curGroup = supportedVaccines.getJSONObject(i);
            for (int j = 0; j < curGroup.getJSONArray("vaccines").length(); j++) {
                JSONObject curVaccine = curGroup.getJSONArray("vaccines").getJSONObject(j);
                if (curVaccine.getString("name").equalsIgnoreCase(vaccineName)) {
                    return curVaccine.getJSONObject("openmrs_calculate").getInt("calculation");
                }
            }
        }
        return -1;
    }

    public static Map<String, Date> receivedVaccines(List<org.ei.opensrp.domain.Vaccine> vaccines) {
        Map<String, Date> map = new LinkedHashMap<>();
        if (vaccines != null) {
            for (org.ei.opensrp.domain.Vaccine vaccine : vaccines) {
                if (vaccine.getDate() != null) {
                    map.put(vaccine.getName(), vaccine.getDate());
                }
            }
        }
        return map;

    }

    /**
     * This method retrieves the human readable name corresponding to the vaccine name from {@code Vaccine.getName()}
     *
     * @param vaccineDbName
     * @return
     */
    public static String getVaccineDisplayName(Context context, final String vaccineDbName) {
        String readableName = vaccineDbName;

        boolean found = false;
        try {
            JSONArray availableVaccines = new JSONArray(getSupportedVaccines(context));
            for (int i = 0; i < availableVaccines.length(); i++) {
                JSONObject currVaccineGroup = availableVaccines.getJSONObject(i);
                for (int j = 0; j < currVaccineGroup.getJSONArray("vaccines").length(); j++) {
                    JSONObject curVaccine = currVaccineGroup.getJSONArray("vaccines").getJSONObject(j);
                    if (curVaccine.getString("name").toLowerCase().equals(vaccineDbName.toLowerCase())) {
                        readableName = curVaccine.getString("name");
                        found = true;
                    }

                    if (found) break;
                }
                if (found) break;
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return readableName;
    }
}

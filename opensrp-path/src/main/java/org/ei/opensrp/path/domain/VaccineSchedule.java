package org.ei.opensrp.path.domain;

import android.text.TextUtils;
import android.util.Log;

import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.service.AlertService;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public class VaccineSchedule {

    private final ArrayList<VaccineTrigger> dueTriggers;
    private final ArrayList<VaccineTrigger> expiryTriggers;
    private final VaccineRepo.Vaccine vaccine;
    private final ArrayList<VaccineCondition> conditions;

    private static HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules;

    public static void init(JSONArray vaccines, JSONArray specialVaccines, String vaccineCategory) throws JSONException {
        if (vaccineSchedules == null) {
            vaccineSchedules = new HashMap<>();
        }
        vaccineSchedules.put(vaccineCategory, new HashMap<String, VaccineSchedule>());

        for (int groupIndex = 0; groupIndex < vaccines.length(); groupIndex++) {
            JSONObject curGroup = vaccines.getJSONObject(groupIndex);
            JSONArray curVaccines = curGroup.getJSONArray("vaccines");
            for (int vaccineIndex = 0; vaccineIndex < curVaccines.length(); vaccineIndex++) {
                JSONObject curVaccine = curVaccines.getJSONObject(vaccineIndex);
                initVaccine(vaccineCategory, curVaccine);
            }
        }

        if (specialVaccines != null) {
            for (int vaccineIndex = 0; vaccineIndex < specialVaccines.length(); vaccineIndex++) {
                initVaccine(vaccineCategory, specialVaccines.getJSONObject(vaccineIndex));
            }
        }
    }

    private static void initVaccine(String vaccineCategory, JSONObject curVaccine) throws JSONException {
        if (TextUtils.isEmpty(curVaccine.optString("vaccine_separator"))) {
            String vaccineName = curVaccine.getString("name");
            VaccineSchedule vaccineSchedule = getVaccineSchedule(vaccineName,
                    vaccineCategory, curVaccine.getJSONObject("schedule"));
            if (vaccineSchedule != null) {
                vaccineSchedules.get(vaccineCategory).put(vaccineName.toUpperCase(), vaccineSchedule);
            }
        } else {
            String[] splitNames = curVaccine.getString("name")
                    .split(curVaccine.getString("vaccine_separator"));
            for (int nameIndex = 0; nameIndex < splitNames.length; nameIndex++) {
                String vaccineName = splitNames[nameIndex];
                VaccineSchedule vaccineSchedule = getVaccineSchedule(vaccineName,
                        vaccineCategory,
                        curVaccine.getJSONObject("schedule").getJSONObject(vaccineName));
                if (vaccineSchedule != null) {
                    vaccineSchedules.get(vaccineCategory).put(vaccineName.toUpperCase(), vaccineSchedule);
                }
            }
        }
    }

    /**
     * Updates offline alerts for the provided person entity id
     *
     * @param application
     * @param baseEntityId
     * @param dob
     * @param vaccineCategory
     * @return The list of updated vaccines
     */
    public static List<String> updateOfflineAlerts(String baseEntityId,
                                                   DateTime dob, String vaccineCategory) {
        try {
            VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
            AlertService alertService = VaccinatorApplication.getInstance().context().alertService();

            List<Alert> newAlerts = new ArrayList<>();
            List<Alert> oldAlerts = new ArrayList<>();
            if (vaccineSchedules.containsKey(vaccineCategory)) {
                List<String> alertNames = new ArrayList<>();
                for (String curVaccineName : vaccineSchedules.get(vaccineCategory).keySet()) {
                    alertNames.add(curVaccineName.toLowerCase().replace(" ", ""));
                }

                // Get all the administered vaccines for the child
                List<Vaccine> issuedVaccines = vaccineRepository.findByEntityId(baseEntityId);
                if (issuedVaccines == null) {
                    issuedVaccines = new ArrayList<>();
                }

                oldAlerts = alertService.findByEntityIdAndOffline(baseEntityId, alertNames.toArray(new String[0]));
                alertService.deleteOfflineAlerts(baseEntityId, alertNames.toArray(new String[0]));

                // Get existing alerts
                List<Alert> existingAlerts = alertService
                        .findByEntityIdAndAlertNames(baseEntityId,
                                alertNames.toArray(new String[0]));

                for (VaccineSchedule curSchedule : vaccineSchedules.get(vaccineCategory).values()) {
                    Alert curAlert = curSchedule.getOfflineAlert(baseEntityId, dob.toDate(), issuedVaccines);

                    if (curAlert != null) {
                        // Check if the current alert already exists for the entityId
                        boolean exists = false;
                        for (Alert curExistingAlert : existingAlerts) {
                            if (curExistingAlert.scheduleName().equalsIgnoreCase(curAlert.scheduleName())
                                    && curExistingAlert.caseId().equalsIgnoreCase(curAlert.caseId())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            // Insert alert into table
                            newAlerts.add(curAlert);
                            alertService.create(curAlert);
                            alertService.updateFtsSearch(curAlert, true);
                        }
                    }
                }
            }

            List<String> allVaccineNames = new ArrayList<>();
            List<String> oldVaccineNames = new ArrayList<>();
            HashMap<String, Alert> oldAlertsMap = new HashMap<>();
            for (Alert curAlert : oldAlerts) {
                if (!oldVaccineNames.contains(curAlert.scheduleName())) {
                    oldVaccineNames.add(curAlert.scheduleName());
                    oldAlertsMap.put(curAlert.scheduleName(), curAlert);
                }

                if (!allVaccineNames.contains(curAlert.scheduleName()))
                    allVaccineNames.add(curAlert.scheduleName());
            }

            List<String> newVaccineNames = new ArrayList<>();
            HashMap<String, Alert> newAlertsMap = new HashMap<>();
            for (Alert curAlert : newAlerts) {
                if (!newVaccineNames.contains(curAlert.scheduleName())) {
                    newVaccineNames.add(curAlert.scheduleName());
                    newAlertsMap.put(curAlert.scheduleName(), curAlert);
                }

                if (!allVaccineNames.contains(curAlert.scheduleName()))
                    allVaccineNames.add(curAlert.scheduleName());
            }

            // Get list of vaccines that are not in both
            List<String> notInOld = new ArrayList<>(newVaccineNames);
            notInOld.removeAll(oldVaccineNames);
            List<String> notInNew = new ArrayList<>(oldVaccineNames);
            notInNew.removeAll(newVaccineNames);
            notInNew.addAll(notInOld);

            allVaccineNames.removeAll(notInNew);

            // For the alerts in both, check if similar
            for (String curVaccineName : allVaccineNames) {
                Alert oldAlert = oldAlertsMap.get(curVaccineName);
                Alert newAlert = newAlertsMap.get(curVaccineName);

                if (!oldAlert.equals(newAlert)) {
                    notInNew.add(oldAlert.scheduleName());
                }
            }

            return notInNew;
        } catch (Exception e) {
            Log.e(VaccineSchedule.class.getName(), e.toString(), e);
            return new ArrayList<>();
        }
    }

    public static VaccineSchedule getVaccineSchedule(String vaccineCategory, String vaccineName) {
        if (vaccineSchedules.containsKey(vaccineCategory)) {
            if (vaccineSchedules.get(vaccineCategory).containsKey(vaccineName.toUpperCase())) {
                return vaccineSchedules.get(vaccineCategory).get(vaccineName.toUpperCase());
            }
        }

        return null;
    }

    private static VaccineSchedule getVaccineSchedule(String vaccineName, String vaccineCategory, JSONObject schedule)
            throws JSONException {
        ArrayList<VaccineTrigger> dueTriggers = new ArrayList<>();
        JSONArray due = schedule.getJSONArray("due");
        for (int i = 0; i < due.length(); i++) {
            VaccineTrigger curTrigger = VaccineTrigger.init(vaccineCategory, due.getJSONObject(i));
            if (curTrigger != null) {
                dueTriggers.add(curTrigger);
            }
        }

        ArrayList<VaccineTrigger> expiryTriggers = new ArrayList<>();
        if (schedule.has("expiry")) {
            JSONArray expiry = schedule.getJSONArray("expiry");
            for (int i = 0; i < expiry.length(); i++) {
                VaccineTrigger curTrigger = VaccineTrigger.init(vaccineCategory, expiry.getJSONObject(i));
                if (curTrigger != null) {
                    expiryTriggers.add(curTrigger);
                }
            }
        }

        VaccineRepo.Vaccine vaccine = VaccineRepo.getVaccine(vaccineName, vaccineCategory);
        if (vaccine != null) {
            ArrayList<VaccineCondition> conditions = new ArrayList<>();
            if (schedule.has("conditions")) {
                JSONArray conditionsData = schedule.getJSONArray("conditions");
                for (int conditionIndex = 0; conditionIndex < conditionsData.length(); conditionIndex++) {
                    VaccineCondition curCondition = VaccineCondition.init(vaccineCategory,
                            conditionsData.getJSONObject(conditionIndex));
                    if (curCondition != null) {
                        conditions.add(curCondition);
                    }
                }
            }

            return new VaccineSchedule(dueTriggers, expiryTriggers, vaccine, conditions);
        }

        return null;
    }

    public VaccineSchedule(ArrayList<VaccineTrigger> dueTriggers,
                           ArrayList<VaccineTrigger> expiryTriggers,
                           VaccineRepo.Vaccine vaccine,
                           ArrayList<VaccineCondition> conditions) {
        this.dueTriggers = dueTriggers;
        this.expiryTriggers = expiryTriggers;
        this.vaccine = vaccine;
        this.conditions = conditions;
    }

    /**
     * Returns the offline alert for a vaccine, if one exists. Currently, the only alert status
     * returned is {@code AlertStatus.normal}
     *
     * @return An {@link Alert} object if one exists, or {@code NULL} if non exists
     */
    public Alert getOfflineAlert(final String baseEntityId, final Date dateOfBirth,
                                 List<Vaccine> issuedVaccines) {
        Alert defaultAlert = null;

        // Check if all conditions pass
        for (VaccineCondition curCondition : conditions) {
            if (!curCondition.passes(issuedVaccines)) {
                return defaultAlert;
            }
        }

        // Use the trigger date as a reference, since that is what is mostly used
        AlertStatus alertStatus = calculateAlertStatus(
                getDueDate(issuedVaccines, dateOfBirth));

        if (alertStatus != null) {
            Date dueDate = getDueDate(issuedVaccines, dateOfBirth);
            Date expiryDate = getExpiryDate(issuedVaccines, dateOfBirth);
            Alert offlineAlert = new Alert(baseEntityId,
                    vaccine.display(),
                    vaccine.name(),
                    alertStatus,
                    dueDate == null ? null : DateUtil.yyyyMMdd.format(dueDate),
                    expiryDate == null ? null : DateUtil.yyyyMMdd.format(expiryDate),
                    true);

            return offlineAlert;
        }

        return defaultAlert;
    }

    /**
     * Calculates the alert status based on the reference date provided. Currently, the only alert
     * status returned is {@code AlertStatus.normal}
     *
     * @param referenceDate The reference date to use to
     * @return {@link AlertStatus} if able to calculate or {@code NULL} if unable
     */
    private AlertStatus calculateAlertStatus(Date referenceDate) {
        if (referenceDate != null) {
            Calendar refCalendarDate = Calendar.getInstance();
            refCalendarDate.setTime(referenceDate);
            standardiseCalendarDate(refCalendarDate);

            Calendar today = Calendar.getInstance();
            standardiseCalendarDate(today);

            if (refCalendarDate.getTimeInMillis() <= today.getTimeInMillis()) {// Due
                return AlertStatus.normal;
            }
        }

        return null;
    }

    public Date getDueDate(List<Vaccine> issuedVaccines, Date dob) {
        Date dueDate = null;
        for (VaccineTrigger curTrigger : dueTriggers) {
            if (dueDate == null) {
                dueDate = curTrigger.getFireDate(issuedVaccines, dob);
            } else {
                Date curDate = curTrigger.getFireDate(issuedVaccines, dob);
                if (curDate != null && curDate.getTime() < dueDate.getTime()) {
                    dueDate = curDate;
                }
            }
        }

        return dueDate;
    }

    public Date getExpiryDate(List<Vaccine> issuedVaccines, Date dob) {
        Date expiryDate = null;
        for (VaccineTrigger curTrigger : expiryTriggers) {
            if (expiryDate == null) {
                expiryDate = curTrigger.getFireDate(issuedVaccines, dob);
            } else {
                Date curDate = curTrigger.getFireDate(issuedVaccines, dob);
                if (curDate != null && curDate.getTime() < expiryDate.getTime()) {
                    expiryDate = curDate;
                }
            }
        }

        return expiryDate;
    }

    public static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    /**
     * This method adds an offset to the provided calendar.
     * Offsets can look like:
     * "+5y,3m,2d" : Plus 5 years, 3 months, and 2 days
     * "-2d" : Minus 2 days
     * <p/>
     * Accepted time units for the offset are:
     * d : Days
     * m : Months
     * y : Years
     * <p/>
     * Accepted operators for the offset are:
     * - : Minus
     * + : Plus
     *
     * @param calendar The calendar to add the offset
     * @param offset   The offset in the format above to add to the calendar
     * @return
     */
    public static Calendar addOffsetToCalendar(Calendar calendar, String offset) {
        if (calendar != null && offset != null) {
            offset = offset.replace(" ", "").toLowerCase();
            Pattern p1 = Pattern.compile("([-+]{1})(.*)");
            Matcher m1 = p1.matcher(offset);
            if (m1.find()) {
                String operatorString = m1.group(1);
                String valueString = m1.group(2);

                int operator = 1;
                if (operatorString.equals("-")) {
                    operator = -1;
                }

                String[] values = valueString.split(",");
                for (int i = 0; i < values.length; i++) {
                    Pattern p2 = Pattern.compile("(\\d+)([dwmy]{1})");
                    Matcher m2 = p2.matcher(values[i]);

                    if (m2.find()) {
                        int curValue = operator * Integer.parseInt(m2.group(1));
                        String fieldString = m2.group(2);
                        int field = Calendar.DATE;
                        if (fieldString.equals("d")) {
                            field = Calendar.DATE;
                        } else if (fieldString.equals("m")) {
                            field = Calendar.MONTH;
                        } else if (fieldString.equals("y")) {
                            field = Calendar.YEAR;
                        }

                        calendar.add(field, curValue);
                    }
                }
            }
        }

        return calendar;
    }
}

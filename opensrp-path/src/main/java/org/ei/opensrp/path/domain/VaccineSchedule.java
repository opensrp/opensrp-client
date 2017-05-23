package org.ei.opensrp.path.domain;

import android.content.Context;
import android.text.TextUtils;

import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.domain.Vaccine;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Utils;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public class VaccineSchedule {

    private final VaccineTrigger trigger;
    private final VaccineRepo.Vaccine vaccine;
    private final int dueDays;
    private final ArrayList<VaccineCondition> conditions;

    private static HashMap<String, HashMap<String, VaccineSchedule>> vaccineSchedules;

    public static void init(JSONArray vaccines, String vaccineCategory) throws JSONException {
        if (vaccineSchedules == null) {
            vaccineSchedules = new HashMap<>();
        }
        vaccineSchedules.put(vaccineCategory, new HashMap<String, VaccineSchedule>());

        for (int groupIndex = 0; groupIndex < vaccines.length(); groupIndex++) {
            JSONObject curGroup = vaccines.getJSONObject(groupIndex);
            JSONArray curVaccines = curGroup.getJSONArray("vaccines");
            for (int vaccineIndex = 0; vaccineIndex < curVaccines.length(); vaccineIndex++) {
                JSONObject curVaccine = curVaccines.getJSONObject(vaccineIndex);

                if (TextUtils.isEmpty(curVaccine.optString("vaccine_separator"))) {
                    String vaccineName = curVaccine.getString("name");
                    VaccineSchedule vaccineSchedule = getVaccineSchedule(vaccineName,
                            vaccineCategory, curVaccine.getJSONObject("schedule"));
                    if (vaccineSchedule != null) {
                        vaccineSchedules.get(vaccineCategory).put(vaccineName, vaccineSchedule);
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
                            vaccineSchedules.get(vaccineCategory).put(vaccineName, vaccineSchedule);
                        }
                    }
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
     *
     * @return  The list of updated {@link Alert}s
     */
    public static List<String> updateOfflineAlerts(VaccinatorApplication application, String baseEntityId,
                                           DateTime dob, String vaccineCategory) {
        List<Alert> newAlerts = new ArrayList<>();
        List<Alert> oldAlerts = new ArrayList<>();
        if (vaccineSchedules.containsKey(vaccineCategory)) {
            // Get all the administered vaccines for the child
            List<Vaccine> issuedVaccines = application.vaccineRepository().findByEntityId(baseEntityId);
            if (issuedVaccines == null) {
                issuedVaccines = new ArrayList<>();
            }

            oldAlerts = application.context().alertService().findByEntityIdAndOffline(baseEntityId, true);
            application.context().alertService().deleteOfflineAlerts(baseEntityId);

            // Get existing alerts
            List<String> alertNames = new ArrayList<>();
            for (String curVaccineName : vaccineSchedules.get(vaccineCategory).keySet()) {
                alertNames.add(curVaccineName.toLowerCase().replace(" ", ""));
            }

            List<Alert> existingAlerts = application.context().alertService()
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
                        application.context().alertService().create(curAlert);
                        application.context().alertService().updateFtsSearch(curAlert, true);
                    }
                }
            }
        }

        List<String> allVaccineNames = new ArrayList<>();
        List<String> oldVaccineNames = new ArrayList<>();
        HashMap<String, Alert> oldAlertsMap = new HashMap<>();
        for (Alert curAlert : oldAlerts) {
            if(!oldVaccineNames.contains(curAlert.scheduleName())) {
                oldVaccineNames.add(curAlert.scheduleName());
                oldAlertsMap.put(curAlert.scheduleName(), curAlert);
            }

            if(!allVaccineNames.contains(curAlert.scheduleName()))
                allVaccineNames.add(curAlert.scheduleName());
        }

        List<String> newVaccineNames = new ArrayList<>();
        HashMap<String, Alert> newAlertsMap = new HashMap<>();
        for (Alert curAlert : newAlerts) {
            if(!newVaccineNames.contains(curAlert.scheduleName())) {
                newVaccineNames.add(curAlert.scheduleName());
                newAlertsMap.put(curAlert.scheduleName(), curAlert);
            }

            if(!allVaccineNames.contains(curAlert.scheduleName()))
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
    }

    private static VaccineSchedule getVaccineSchedule(String vaccineName, String vaccineCategory, JSONObject schedule)
            throws JSONException {
        int dueDays = schedule.getJSONObject("trigger").getInt("due_days");
        VaccineTrigger trigger = VaccineTrigger.init(vaccineCategory, schedule);
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

            return new VaccineSchedule(trigger, vaccine, dueDays, conditions);
        }

        return null;
    }

    public VaccineSchedule(VaccineTrigger trigger, VaccineRepo.Vaccine vaccine, int dueDays,
                           ArrayList<VaccineCondition> conditions) {
        this.trigger = trigger;
        this.vaccine = vaccine;
        this.dueDays = dueDays;
        this.conditions = conditions;
    }

    /**
     * Returns the offline alert for a vaccine, if one exists. Currently, the only alert status
     * returned is {@link AlertStatus.normal}
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
                trigger.getFireDate(issuedVaccines, dateOfBirth));

        if (alertStatus != null) {
            Alert offlineAlert = new Alert(baseEntityId, vaccine.display(), vaccine.name(),
                    alertStatus, DateUtil.yyyyMMdd.format(dateOfBirth), null, true);

            return offlineAlert;
        }

        return defaultAlert;
    }

    /**
     * Calculates the alert status based on the reference date provided. Currently, the only alert
     * status returned is {@link AlertStatus.normal}
     *
     * @param referenceDate The reference date to use to
     * @return {@link AlertStatus} if able to calculate or {@code NULL} if unable
     */
    private AlertStatus calculateAlertStatus(Date referenceDate) {
        if (referenceDate != null) {
            Calendar refCalendarDate = Calendar.getInstance();
            refCalendarDate.setTime(referenceDate);
            refCalendarDate.add(Calendar.DATE, dueDays);
            standardiseCalendarDate(refCalendarDate);

            Calendar today = Calendar.getInstance();
            standardiseCalendarDate(today);

            if (refCalendarDate.getTimeInMillis() <= today.getTimeInMillis()) {// Due
                return AlertStatus.normal;
            }
        }

        return null;
    }

    public static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }
}

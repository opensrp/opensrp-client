package org.ei.opensrp.path.domain;

import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.domain.Vaccine;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public class VaccineTrigger {
    private enum Type {
        DOB,
        PREREQUISITE
    }

    private final Type type;
    private final VaccineRepo.Vaccine prerequisite;

    public static VaccineTrigger init(String vaccineCategory, JSONObject scheduleData) throws JSONException {
        JSONObject trigger = scheduleData.getJSONObject("trigger");
        if (trigger.getString("type").equalsIgnoreCase(Type.DOB.name())) {
            return new VaccineTrigger();
        } else if (trigger.getString("type").equalsIgnoreCase(Type.PREREQUISITE.name())) {
            VaccineRepo.Vaccine prerequisite = VaccineRepo.getVaccine(trigger.getString("prerequisite"),
                    vaccineCategory);
            if (prerequisite != null) {
                return new VaccineTrigger(prerequisite);
            }
        }

        return null;
    }

    public VaccineTrigger() {
        this.type = Type.DOB;
        this.prerequisite = null;
    }

    public VaccineTrigger(VaccineRepo.Vaccine prerequisite) {
        this.type = Type.PREREQUISITE;
        this.prerequisite = prerequisite;
    }

    /**
     * Get the date the trigger will fire
     *
     * @return {@link Date} if able to get trigger date, or {@code null} if an error occurs
     */
    public Date getFireDate(final List<Vaccine> issuedVaccines, final Date dob) {
        if (type.equals(Type.DOB)) {
            if (dob != null) {
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dob);
                VaccineSchedule.standardiseCalendarDate(dobCalendar);

                return dobCalendar.getTime();
            }
        } else if (type.equals(Type.PREREQUISITE)) {
            // Check if prerequisite is in the list of issued vaccines
            Vaccine issuedPrerequisite = null;
            for (Vaccine curVaccine : issuedVaccines) {
                if (curVaccine.getName().equalsIgnoreCase(prerequisite.display())) {
                    issuedPrerequisite = curVaccine;
                    break;
                }
            }

            if (issuedPrerequisite != null) {
                // Check if the date given is in the past
                Calendar issuedDate = Calendar.getInstance();
                issuedDate.setTime(issuedPrerequisite.getDate());
                VaccineSchedule.standardiseCalendarDate(issuedDate);

                Calendar today = Calendar.getInstance();
                VaccineSchedule.standardiseCalendarDate(today);
                if (issuedDate.getTimeInMillis() <= today.getTimeInMillis()) {
                    return issuedDate.getTime();
                }
            }
        }

        return null;
    }
}

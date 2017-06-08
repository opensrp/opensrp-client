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
    private enum Reference {
        DOB,
        PREREQUISITE
    }

    private final Reference reference;
    private final String offset;
    private final VaccineRepo.Vaccine prerequisite;

    public static VaccineTrigger init(String vaccineCategory, JSONObject data) throws JSONException {
        if (data != null) {
            if (data.getString("reference").equalsIgnoreCase(Reference.DOB.name())) {
                return new VaccineTrigger(data.getString("offset"));
            } else if (data.getString("reference").equalsIgnoreCase(Reference.PREREQUISITE.name())) {
                VaccineRepo.Vaccine prerequisite = VaccineRepo.getVaccine(data.getString("prerequisite"),
                        vaccineCategory);
                if (prerequisite != null) {
                    return new VaccineTrigger(data.getString("offset"), prerequisite);
                }
            }
        }

        return null;
    }

    public VaccineTrigger(String offset) {
        this.reference = Reference.DOB;
        this.offset = offset;
        this.prerequisite = null;
    }

    public VaccineTrigger(String offset, VaccineRepo.Vaccine prerequisite) {
        this.reference = Reference.PREREQUISITE;
        this.offset = offset;
        this.prerequisite = prerequisite;
    }

    /**
     * Get the date the trigger will fire
     *
     * @return {@link Date} if able to get trigger date, or {@code null} if prerequisite hasn't been
     *          administered yet
     */
    public Date getFireDate(final List<Vaccine> issuedVaccines, final Date dob) {
        if (reference.equals(Reference.DOB)) {
            if (dob != null) {
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dob);
                VaccineSchedule.standardiseCalendarDate(dobCalendar);

                dobCalendar = VaccineSchedule.addOffsetToCalendar(dobCalendar, offset);
                return dobCalendar.getTime();
            }
        } else if (reference.equals(Reference.PREREQUISITE)) {
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

                issuedDate = VaccineSchedule.addOffsetToCalendar(issuedDate, offset);
                return issuedDate.getTime();
            }
        }

        return null;
    }
}

package org.ei.opensrp.path.domain;

import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineSchedule;
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
        PREREQUISITE,
        LMP
    }

    private final Reference reference;
    private final String offset;
    private final String window;
    private final VaccineRepo.Vaccine prerequisite;

    public static VaccineTrigger init(String vaccineCategory, JSONObject data) throws JSONException {
        if (data != null) {
            final String REFERENCE = "reference";
            final String OFFSET = "offset";
            final String PREREQUISITE = "prerequisite";
            final String WINDOW = "window";
            final String LMP = "lmp";
            if (data.getString(REFERENCE).equalsIgnoreCase(Reference.DOB.name())) {
                return new VaccineTrigger(data.getString(OFFSET), data.has(WINDOW) ? data.getString(WINDOW) : null , Reference.DOB);
            }else if (data.getString(REFERENCE).equalsIgnoreCase(Reference.LMP.name())) {
                return new VaccineTrigger(data.getString(OFFSET),data.has(WINDOW) ? data.getString(WINDOW) : null, Reference.LMP);
            }else if (data.getString(REFERENCE).equalsIgnoreCase(Reference.PREREQUISITE.name())) {
                VaccineRepo.Vaccine prerequisite = VaccineRepo.getVaccine(data.getString(PREREQUISITE),
                        vaccineCategory);
                if (prerequisite != null) {
                    return new VaccineTrigger(data.getString(OFFSET), data.has(WINDOW) ? data.getString(WINDOW) : null, prerequisite);
                }
            }
        }

        return null;
    }

    public VaccineTrigger(String offset, String window ,Reference reference) {
        this.reference = reference;
        this.offset = offset;
        this.prerequisite = null;
        this.window = window;
    }

    public VaccineTrigger(String offset, String window, VaccineRepo.Vaccine prerequisite) {
        this.reference = Reference.PREREQUISITE;
        this.offset = offset;
        this.prerequisite = prerequisite;
        this.window = window;
    }

    /**
     * Get the date the trigger will fire
     *
     * @return {@link Date} if able to get trigger date, or {@code null} if prerequisite hasn't been
     * administered yet
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
        }else if (reference.equals(Reference.LMP)) {
            if (dob != null) {
                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(dob);
                VaccineSchedule.standardiseCalendarDate(dobCalendar);

                dobCalendar = VaccineSchedule.addOffsetToCalendar(dobCalendar, offset);
                return dobCalendar.getTime();
            }
        }else if (reference.equals(Reference.PREREQUISITE)) {
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

    public String getWindow() {
        return window;
    }
}
package org.ei.opensrp.path.domain;

import org.ei.opensrp.path.db.VaccineRepo;

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
            // TODO: Check if prerequisite was given

            // TODO: Check if the date given is in the past
        }

        return null;
    }
}

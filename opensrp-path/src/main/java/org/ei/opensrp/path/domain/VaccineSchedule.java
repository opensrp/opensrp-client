package org.ei.opensrp.path.domain;

import org.ei.opensrp.Context;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.path.db.VaccineRepo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public class VaccineSchedule {

    private final VaccineTrigger trigger;
    private final VaccineRepo.Vaccine vaccine;
    private final int dueDays;
    private final ArrayList<VaccineRepo.Vaccine> prerequisite;
    private final ArrayList<VaccineCondition> conditions;

    public VaccineSchedule(VaccineTrigger trigger, VaccineRepo.Vaccine vaccine,
                           Date dateOfBirth, int dueDays,
                           ArrayList<VaccineRepo.Vaccine> prerequisite,
                           ArrayList<VaccineCondition> conditions) {
        this.trigger = trigger;
        this.vaccine = vaccine;
        this.dueDays = dueDays;
        this.prerequisite = prerequisite;
        this.conditions = conditions;
    }

    /**
     * Returns the offline alert for a vaccine, if one exists. Currently, the only alert status
     * returned is {@link AlertStatus.normal}
     *
     * @param context
     * @param openSrpContext
     * @return An {@link Alert} object if one exists, or {@code NULL} if non exists
     */
    public Alert getOfflineAlert(android.content.Context context, Context openSrpContext,
                                 final String baseEntityId, final Date dateOfBirth,
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
                    alertStatus, DateUtil.yyyyMMdd.format(dateOfBirth), null);

            return offlineAlert;
        }

        return defaultAlert;
    }

    /**
     * Calculates the alert status based on the reference date provided. Currently, the only alert
     * status returned is {@link AlertStatus.normal}
     *
     * @param referenceDate The reference date to use to
     * @return  {@link AlertStatus} if able to calculate or {@code NULL} if unable
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

package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import util.PathConstants;
import util.Utils;


/**
 * Created by keyman on 16/06/2017.
 */
public class HIA2StatusRefreshIntentService extends IntentService {
    private static final String TAG = HIA2StatusRefreshIntentService.class.getCanonicalName();

    private VaccineRepository vaccineRepository;
    private CommonRepository commonRepository;
    private static final int DAYS_BEFORE_OVERDUE = 10;


    public HIA2StatusRefreshIntentService() {
        super("HIA2StatusRefreshIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Vaccine> vaccines = vaccineRepository.findWithNullHia2Status();
            if (!vaccines.isEmpty()) {
                for (Vaccine vaccine : vaccines) {
                    Long daysAfter = countDaysAfterDueDate(vaccine);
                    if (daysAfter == null) {
                        continue;
                    }
                    String hia2Status;
                    if (daysAfter <= DAYS_BEFORE_OVERDUE) {
                        hia2Status = VaccineRepository.HIA2_Within;
                    } else {
                        hia2Status = VaccineRepository.HIA2_Overdue;
                    }
                    vaccineRepository.updateHia2Status(vaccine, hia2Status);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
        commonRepository = VaccinatorApplication.getInstance().context().commonrepository(PathConstants.CHILD_TABLE_NAME);

        return super.onStartCommand(intent, flags, startId);
    }

    private Long countDaysAfterDueDate(Vaccine vaccine) {
        if (vaccine == null || vaccine.getBaseEntityId() == null || vaccine.getDate() == null) {
            return null;
        }

        if (vaccineRepository == null || commonRepository == null) {
            return null;
        }

        CommonPersonObject rawDetails = commonRepository.findByBaseEntityId(vaccine.getBaseEntityId());
        if (rawDetails == null) {
            return null;
        }

        CommonPersonObjectClient childDetails = MotherLookUpSmartClientsProvider.convert(rawDetails);
        List<Vaccine> vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());

        DateTime dateTime = Utils.dobToDateTime(childDetails);
        if (dateTime == null) {
            return null;
        }

        Date dob = dateTime.toDate();

        Date dueDate = getDueVaccineDate(vaccine, vaccineList, dob);
        Date doneDate = vaccine.getDate();

        if (dueDate == null) {
            return null;
        }

        long diff = doneDate.getTime() - dueDate.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        Log.i(TAG, "Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        return days;

    }

    private Date getDueVaccineDate(Vaccine vaccine, List<Vaccine> issuedVaccines, Date dateOfBirth) {
        VaccineSchedule curVaccineSchedule = VaccineSchedule.getVaccineSchedule("child",
                vaccine.getName());
        Date minDate = null;

        if (curVaccineSchedule != null) {
            minDate = curVaccineSchedule.getDueDate(issuedVaccines, dateOfBirth);
        }

        return minDate;
    }
}

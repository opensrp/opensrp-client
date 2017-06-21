package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.DailyTally;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.provider.MotherLookUpSmartClientsProvider;
import org.ei.opensrp.path.repository.DailyTalliesRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.service.HIA2Service;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.PathConstants;
import util.ReportUtils;
import util.Utils;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class HIA2IntentService extends IntentService {
    private static final String TAG = HIA2IntentService.class.getCanonicalName();
    public static final String GENERATE_REPORT = "GENERATE_REPORT";
    private DailyTalliesRepository dailyTalliesRepository;
    private PathRepository pathRepository;
    private HIA2Service hia2Service;
    private boolean generateReport;

    //HIA2 Status
    private VaccineRepository vaccineRepository;
    private static final int DAYS_BEFORE_OVERDUE = 10;

    public HIA2IntentService() {
        super("HIA2IntentService");
    }

    /**
     * Build indicators,save them to the db and generate report
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "Started HIA2 service");
        try {
            generateReport = intent.getBooleanExtra(GENERATE_REPORT, false);

            //Update H1A2 status (Within or Overdue)
            updateVaccineHIA2Status();

            // Generate HIA2 report (daily, monthly tallies)
            generateReport();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        Log.e(TAG, "Finishing HIA2 service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dailyTalliesRepository = VaccinatorApplication.getInstance().dailyTalliesRepository();
        pathRepository = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        hia2Service = new HIA2Service();

        vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();

        return super.onStartCommand(intent, flags, startId);
    }

    private void generateReport() {
        try {

            SQLiteDatabase db = VaccinatorApplication.getInstance().getRepository().getWritableDatabase();
            //get previous dates if shared preferences is null meaning reports for previous months haven't been generated
            String lastProcessedDate = Context.getInstance().allSharedPreferences().getPreference(HIA2Service.HIA2_LAST_PROCESSED_DATE);
            ArrayList<HashMap<String, String>> reportDates;
            if (lastProcessedDate == null || lastProcessedDate.isEmpty()) {
                reportDates = pathRepository.rawQuery(db, HIA2Service.PREVIOUS_REPORT_DATES_QUERY.concat(" order by eventDate asc"));

            } else {
                reportDates = pathRepository.rawQuery(db, HIA2Service.PREVIOUS_REPORT_DATES_QUERY.concat(" where " + PathRepository.event_column.updatedAt + " >'" + lastProcessedDate + "'" + " order by eventDate asc"));
            }
            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            for (Map<String, String> dates : reportDates) {
                String date = dates.get(PathRepository.event_column.eventDate.name());
                String updatedAt = dates.get(PathRepository.event_column.updatedAt.name());

                Map<String, Object> hia2Report = hia2Service.generateIndicators(db, date);
                dailyTalliesRepository.save(date, hia2Report);
                if (generateReport) {
                    List<DailyTally> tallies = dailyTalliesRepository.findByProviderIdAndDay(userName, date);
                    List<JSONObject> tallyReports = new ArrayList<>();
                    for (DailyTally curTally : tallies) {
                        tallyReports.add(curTally.getJsonObject());
                    }
                    ReportUtils.createReport(this, tallyReports, HIA2Service.REPORT_NAME);
                }
                Context.getInstance().allSharedPreferences().savePreference(HIA2Service.HIA2_LAST_PROCESSED_DATE, updatedAt);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void updateVaccineHIA2Status() {
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

    private Long countDaysAfterDueDate(Vaccine vaccine) {

        CommonRepository commonRepository = Context.getInstance().commonrepository(PathConstants.CHILD_TABLE_NAME);
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

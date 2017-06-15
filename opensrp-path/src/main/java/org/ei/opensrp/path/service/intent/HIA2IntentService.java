package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Tally;
import org.ei.opensrp.path.repository.DailyTalliesRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.service.HIA2Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ReportUtils;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class HIA2IntentService extends IntentService {
    private static final String TAG = HIA2IntentService.class.getCanonicalName();
    private DailyTalliesRepository dailyTalliesRepository;
    private PathRepository pathRepository;
    private HIA2Service hia2Service;
    private boolean generateReport;

    public HIA2IntentService(final boolean _generateReport) {

        super("HIA2IntentService");
        generateReport = _generateReport;

    }

    /**
     * Build indicators,save them to the db and generate report
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            SQLiteDatabase db = VaccinatorApplication.getInstance().getRepository().getWritableDatabase();
            //get previous dates if shared preferences is null meaning reports for previous months haven't been generated
            String lastProcessedDate = Context.getInstance().allSharedPreferences().getPreference(HIA2Service.HIA2_LAST_PROCESSED_DATE);
            ArrayList<HashMap<String, String>> reportDates;
            if (lastProcessedDate == null || lastProcessedDate.isEmpty()) {
                reportDates = pathRepository.rawQuery(db, String.format(HIA2Service.PREVIOUS_REPORT_DATES_QUERY, ""));

            } else {
                reportDates = pathRepository.rawQuery(db, String.format(HIA2Service.PREVIOUS_REPORT_DATES_QUERY, "where updated_at >'" + lastProcessedDate + "'"));
            }
            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            for (Map<String, String> dates : reportDates) {
                String date = dates.get(PathRepository.event_column.eventDate);
                String updatedAt = dates.get(PathRepository.event_column.updatedAt);

                Map<String, Map<String, Object>> hia2Report = hia2Service.generateIndicators(db, date);
                dailyTalliesRepository.save(date, hia2Report);
                if (generateReport) {
                    List<Tally> tallies = dailyTalliesRepository.findByProviderIdAndDay(userName, date);
                    ReportUtils.createReport(this, Tally.getJsonObjects(tallies), HIA2Service.REPORT_NAME);
                }
                Context.getInstance().allSharedPreferences().savePreference(HIA2Service.HIA2_LAST_PROCESSED_DATE, updatedAt);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dailyTalliesRepository = VaccinatorApplication.getInstance().dailyTalliesRepository();
        pathRepository = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        hia2Service = new HIA2Service();
        return super.onStartCommand(intent, flags, startId);
    }
}

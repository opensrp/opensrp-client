package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.repository.HIA2Repository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.service.HIA2Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import util.ReportUtils;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class HIA2IntentService extends IntentService {
    private static final String TAG = HIA2IntentService.class.getCanonicalName();
    private HIA2Repository hia2Repository;
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

            List<String> reportDates = pathRepository.getDbValues(db,HIA2Service.PREVIOUS_REPORT_DATES_QUERY);
            String lastProcessedDate=Context.getInstance().allSharedPreferences().getPreference(HIA2Service.HIA2_LAST_PROCESSED_DATE);
            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            String day = HIA2Service.dfyymmdd.format(new Date());
            Map<String, Map<String, Object>> hia2Report = hia2Service.generateIndicators(db);
            hia2Repository.save(hia2Report);
            if (generateReport) {
                List<Hia2Indicator> hia2Indicators = hia2Repository.findByProviderIdAndMonth(userName, day);
                ReportUtils.createReport(this, hia2Indicators,HIA2Service.REPORT_NAME);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        hia2Repository = VaccinatorApplication.getInstance().hia2Repository();
        pathRepository = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        hia2Service = new HIA2Service();
        return super.onStartCommand(intent, flags, startId);
    }
}

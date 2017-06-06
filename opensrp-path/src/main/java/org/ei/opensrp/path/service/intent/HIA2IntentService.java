package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.HIA2Repository;
import org.ei.opensrp.path.service.HIA2Service;
import org.ei.opensrp.repository.AllSharedPreferences;

import java.util.Map;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class HIA2IntentService extends IntentService {
    private static final String TAG = HIA2IntentService.class.getCanonicalName();
    private HIA2Repository hia2Repository;
    private HIA2Service hia2Service;
    SharedPreferences preferences;
    AllSharedPreferences allSharedPreferences;


    public HIA2IntentService() {

        super("HIA2IntentService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Map<String, Map<String, Object>> hia2Report = hia2Service.generateIndicators(VaccinatorApplication.getInstance().getRepository().getWritableDatabase());
            hia2Repository.save(hia2Report);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        hia2Repository = VaccinatorApplication.getInstance().hia2Repository();
        hia2Service = new HIA2Service();
        return super.onStartCommand(intent, flags, startId);
    }
}

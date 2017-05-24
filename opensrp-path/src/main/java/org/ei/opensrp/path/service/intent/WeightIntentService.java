package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.WeightRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import util.JsonFormUtils;
import util.PathConstants;


/**
 * Created by keyman on 3/01/2017.
 */
public class WeightIntentService extends IntentService {
    private static final String TAG = WeightIntentService.class.getCanonicalName();
    public static final String EVENT_TYPE = "Growth Monitoring";
    public static final String EVENT_TYPE_OUT_OF_CATCHMENT = "Out of Area Service - Growth Monitoring";
    public static final String ENTITY_TYPE = "weight";
    private WeightRepository weightRepository;


    public WeightIntentService() {
        super("WeightService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            List<Weight> weights = weightRepository.findUnSyncedBeforeTime(PathConstants.VACCINE_SYNC_TIME);
            if (!weights.isEmpty()) {
                for (Weight weight : weights) {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JsonFormUtils.KEY, "Weight_Kgs");
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, "concept");
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, "");
                    jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, "decimal");
                    jsonObject.put(JsonFormUtils.VALUE, weight.getKg());


                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);

                    JsonFormUtils.createWeightEvent(getApplicationContext(), weight, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    if (weight.getBaseEntityId() == null || weight.getBaseEntityId().isEmpty()) {
                        JsonFormUtils.createWeightEvent(getApplicationContext(), weight, EVENT_TYPE_OUT_OF_CATCHMENT, ENTITY_TYPE, jsonArray);

                    }
                    weightRepository.close(weight.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        weightRepository = VaccinatorApplication.getInstance().weightRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}

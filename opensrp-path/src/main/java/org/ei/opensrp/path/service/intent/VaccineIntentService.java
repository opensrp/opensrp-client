package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import util.JsonFormUtils;
import util.PathConstants;
import util.VaccinatorUtils;


/**
 * Created by keyman on 3/01/2017.
 */
public class VaccineIntentService extends IntentService {
    private static final String TAG = VaccineIntentService.class.getCanonicalName();
    public static final String EVENT_TYPE = "Vaccination";
    public static final String ENTITY_TYPE = "vaccination";
    private VaccineRepository vaccineRepository;
    private JSONArray availableVaccines;

    public VaccineIntentService() {
        super("VaccineService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (availableVaccines == null) {
            try {
                availableVaccines = new JSONArray(VaccinatorUtils.getSupportedVaccines(this));
            } catch (JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        final String entityId = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        final String calId = "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        final String dateDataType = "date";
        final String calculationDataType = "calculate";
        final String concept = "concept";

        try {
            List<Vaccine> vaccines = vaccineRepository.findUnSyncedBeforeTime(PathConstants.VACCINE_SYNC_TIME);
            if (!vaccines.isEmpty()) {
                for (Vaccine vaccine : vaccines) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = simpleDateFormat.format(vaccine.getDate());

                    JSONArray jsonArray = new JSONArray();

                    String vaccineName = vaccine.getName().replace(" ", "_");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JsonFormUtils.KEY, vaccineName);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, concept);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, entityId);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, getParentId(vaccine.getName()));
                    jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, dateDataType);
                    jsonObject.put(JsonFormUtils.VALUE, formattedDate);
                    jsonArray.put(jsonObject);

                    if (vaccine.getCalculation() == null || vaccine.getCalculation() < 0) {
                        vaccine.setCalculation(1);
                    }

                    jsonObject = new JSONObject();
                    jsonObject.put(JsonFormUtils.KEY, vaccineName + "_dose");
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, concept);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, calId);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, getParentId(vaccine.getName()));
                    jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, calculationDataType);
                    jsonObject.put(JsonFormUtils.VALUE, vaccine.getCalculation());
                    jsonArray.put(jsonObject);

                    JsonFormUtils.createVaccineEvent(getApplicationContext(), vaccine, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    vaccineRepository.close(vaccine.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String getParentId(String name) {
        String parentEntityId = "";
        try {

            for (int i = 0; i < availableVaccines.length(); i++) {
                JSONObject curVaccineGroup = availableVaccines.getJSONObject(i);
                JSONArray vaccines = curVaccineGroup.getJSONArray("vaccines");
                for (int j = 0; j < vaccines.length(); j++) {
                    JSONObject vaccine = vaccines.getJSONObject(j);
                    if (StringUtils.containsIgnoreCase(vaccine.getString("name"), name)) {
                        parentEntityId = vaccine.getJSONObject("openmrs_date").getString("parent_entity");
                        if (parentEntityId.contains("/")) {
                            String[] parentEntityArray = parentEntityId.split("/");
                            if (StringUtils.containsIgnoreCase(name, "measles")) {
                                parentEntityId = parentEntityArray[0];
                            } else if (StringUtils.containsIgnoreCase(name, "mr")) {
                                parentEntityId = parentEntityArray[1];
                            }
                            return parentEntityId;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return parentEntityId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}

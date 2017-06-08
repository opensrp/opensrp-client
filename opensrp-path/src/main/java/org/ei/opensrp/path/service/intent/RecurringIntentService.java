package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import util.JsonFormUtils;
import util.PathConstants;


/**
 * Created by keyman on 3/01/2017.
 */
public class RecurringIntentService extends IntentService {
    private static final String TAG = RecurringIntentService.class.getCanonicalName();
    public static final String ITN_PROVIDED = "ITN_Provided";
    public static final String CHILD_HAS_NET = "Child_Has_Net";

    public static final String EVENT_TYPE = "Recurring Service";
    public static final String ENTITY_TYPE = "recurring_service";
    private RecurringServiceTypeRepository recurringServiceTypeRepository;
    private RecurringServiceRecordRepository recurringServiceRecordRepository;

    final String YES = "yes";
    final String NO = "no";
    final String VALUES = "values";
    final String OPENMRS_CHOICES_IDS = "openmrs_choice_ids";
    final String OPENMRS_YES = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    final String OPENMRS_NO = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";


    public RecurringIntentService() {
        super("RecurringService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final String SELECT_DATA_TYPE = "coded";

        final String CALC_ID = "1639AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        final String CALCULATION_DATA_TYPE = "numeric";

        final String CONCEPT = "concept";
        final String ENCOUNTER = "encounter";

        final String ENCOUNTER_DATE = "encounter_date";
        final String DATE_DATA_TYPE = "date";

        try {
            List<ServiceRecord> serviceRecordList = recurringServiceRecordRepository.findUnSyncedBeforeTime(PathConstants.VACCINE_SYNC_TIME);
            if (!serviceRecordList.isEmpty()) {
                for (ServiceRecord serviceRecord : serviceRecordList) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = simpleDateFormat.format(serviceRecord.getDate());

                    ServiceType serviceType = recurringServiceTypeRepository.find(serviceRecord.getRecurringServiceId());
                    if (serviceType == null) {
                        continue;
                    }

                    Integer calculation = null;
                    String calculationString = serviceType.getName().replace(serviceType.getType(), "");
                    if (StringUtils.isNotBlank(calculationString)) {
                        if (StringUtils.isNumeric(calculationString.trim())) {
                            calculation = Integer.valueOf(calculationString.trim());
                        } else if (StringUtils.containsIgnoreCase(calculationString, "ifc")) {
                            calculation = 0;
                        }
                    } else {
                        calculation = -1;
                    }

                    boolean itnHasNet = false;

                    String serviceRecordName = serviceType.getName().replace(" ", "_").toLowerCase();
                    JSONArray jsonArray = new JSONArray();


                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JsonFormUtils.KEY, serviceRecordName);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, serviceType.getServiceNameEntity());
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, serviceType.getServiceNameEntityId());
                    jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, SELECT_DATA_TYPE);
                    jsonObject.put(JsonFormUtils.VALUE, YES);
                    if (serviceType.getType().equalsIgnoreCase("ITN")) {
                        if (StringUtils.isNotBlank(serviceRecord.getValue()) && serviceRecord.getValue().equalsIgnoreCase(CHILD_HAS_NET)) {
                            jsonObject.put(JsonFormUtils.VALUE, NO);
                            itnHasNet = true;
                        }
                    }
                    addYesNoChoices(jsonObject);
                    jsonArray.put(jsonObject);

                    jsonObject = new JSONObject();
                    jsonObject.put(JsonFormUtils.KEY, serviceRecordName + "_dose");
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, CONCEPT);
                    jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, CALC_ID);
                    jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, CALCULATION_DATA_TYPE);
                    jsonObject.put(JsonFormUtils.VALUE, calculation);
                    jsonArray.put(jsonObject);

                    if (!(serviceType.getDateEntity().equalsIgnoreCase(ENCOUNTER) && serviceType.getDateEntityId().equalsIgnoreCase(ENCOUNTER_DATE))) {
                        jsonObject = new JSONObject();
                        jsonObject.put(JsonFormUtils.KEY, serviceRecordName + "_date");
                        jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, CONCEPT);
                        jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, serviceType.getDateEntityId());
                        jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, DATE_DATA_TYPE);
                        jsonObject.put(JsonFormUtils.VALUE, formattedDate);
                        jsonArray.put(jsonObject);
                    }

                    if (itnHasNet) {
                        jsonObject = new JSONObject();
                        jsonObject.put(JsonFormUtils.KEY, CHILD_HAS_NET);
                        jsonObject.put(JsonFormUtils.OPENMRS_ENTITY, "");
                        jsonObject.put(JsonFormUtils.OPENMRS_ENTITY_ID, "");
                        jsonObject.put(JsonFormUtils.OPENMRS_DATA_TYPE, SELECT_DATA_TYPE);
                        jsonObject.put(JsonFormUtils.VALUE, YES);
                        addYesNoChoices(jsonObject);
                        jsonArray.put(jsonObject);
                    }

                    JsonFormUtils.createServiceEvent(getApplicationContext(), serviceRecord, EVENT_TYPE, ENTITY_TYPE, jsonArray);
                    recurringServiceRecordRepository.close(serviceRecord.getId());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
        recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    private void addYesNoChoices(JSONObject jsonObject) {
        try {
            JSONArray valuesArray = new JSONArray();
            valuesArray.put(YES);
            valuesArray.put(NO);

            jsonObject.put(VALUES, valuesArray);

            JSONObject choices = new JSONObject();
            choices.put(YES, OPENMRS_YES);
            choices.put(NO, OPENMRS_NO);

            jsonObject.put(OPENMRS_CHOICES_IDS, choices);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}

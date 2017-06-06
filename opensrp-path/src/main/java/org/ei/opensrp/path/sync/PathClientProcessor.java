package org.ei.opensrp.path.sync;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.ServiceSchedule;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.service.intent.RecurringIntentService;
import org.ei.opensrp.path.service.intent.VaccineIntentService;
import org.ei.opensrp.path.service.intent.WeightIntentService;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.sync.ClientProcessor;
import org.ei.opensrp.sync.CloudantDataHandler;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.MoveToMyCatchmentUtils;

public class PathClientProcessor extends ClientProcessor {

    private static final String TAG = "PathClientProcessor";
    private static PathClientProcessor instance;

    public PathClientProcessor(Context context) {
        super(context);
    }

    public static PathClientProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new PathClientProcessor(context);
        }
        return instance;
    }

    @Override
    public synchronized void processClient() throws Exception {
        CloudantDataHandler handler = CloudantDataHandler.getInstance(getContext());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        long lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        String clientClassificationStr = getFileContents("ec_client_classification.json");
        String clientVaccineStr = getFileContents("ec_client_vaccine.json");
        String clientWeightStr = getFileContents("ec_client_weight.json");
        String clientServiceStr = getFileContents("ec_client_service.json");

        //this seems to be easy for now cloudant json to events model is crazy
        List<JSONObject> events = handler.getUpdatedEventsAndAlerts(lastSyncDate);
        if (!events.isEmpty()) {
            List<JSONObject> unsyncEvents = new ArrayList<>();
            for (JSONObject event : events) {
                String type = event.has("eventType") ? event.getString("eventType") : null;
                if (type == null) {
                    continue;
                }

                if (type.equals(VaccineIntentService.EVENT_TYPE)) {
                    JSONObject clientVaccineClassificationJson = new JSONObject(clientVaccineStr);
                    if (isNullOrEmptyJSONObject(clientVaccineClassificationJson)) {
                        continue;
                    }

                    processVaccine(event, clientVaccineClassificationJson);
                } else if (type.equals(WeightIntentService.EVENT_TYPE)) {
                    JSONObject clientWeightClassificationJson = new JSONObject(clientWeightStr);
                    if (isNullOrEmptyJSONObject(clientWeightClassificationJson)) {
                        continue;
                    }

                    processWeight(event, clientWeightClassificationJson);
                } else if (type.equals(RecurringIntentService.EVENT_TYPE)) {
                    JSONObject clientServiceClassificationJson = new JSONObject(clientServiceStr);
                    if (isNullOrEmptyJSONObject(clientServiceClassificationJson)) {
                        continue;
                    }
                    processService(event, clientServiceClassificationJson);
                } else if (type.equals(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)) {
                    unsyncEvents.add(event);
                } else {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }
                    //iterate through the events
                    processEvent(event, clientClassificationJson);
                }
            }

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
                unSync(unsyncEvents);
            }
        }

        allSharedPreferences.saveLastSyncDate(lastSyncDate.getTime());
    }

    @Override
    public synchronized void processClient(List<JSONObject> events) throws Exception {

        String clientClassificationStr = getFileContents("ec_client_classification.json");
        String clientVaccineStr = getFileContents("ec_client_vaccine.json");
        String clientWeightStr = getFileContents("ec_client_weight.json");
        String clientServiceStr = getFileContents("ec_client_service.json");

        if (!events.isEmpty()) {
            List<JSONObject> unsyncEvents = new ArrayList<>();
            for (JSONObject event : events) {

                String eventType = event.has("eventType") ? event.getString("eventType") : null;
                if (eventType == null) {
                    continue;
                }

                if (eventType.equals(VaccineIntentService.EVENT_TYPE)) {
                    JSONObject clientVaccineClassificationJson = new JSONObject(clientVaccineStr);
                    if (isNullOrEmptyJSONObject(clientVaccineClassificationJson)) {
                        continue;
                    }

                    processVaccine(event, clientVaccineClassificationJson);
                } else if (eventType.equals(WeightIntentService.EVENT_TYPE)) {
                    JSONObject clientWeightClassificationJson = new JSONObject(clientWeightStr);
                    if (isNullOrEmptyJSONObject(clientWeightClassificationJson)) {
                        continue;
                    }

                    processWeight(event, clientWeightClassificationJson);
                } else if (eventType.equals(RecurringIntentService.EVENT_TYPE)) {
                    JSONObject clientServiceClassificationJson = new JSONObject(clientServiceStr);
                    if (isNullOrEmptyJSONObject(clientServiceClassificationJson)) {
                        continue;
                    }
                    processService(event, clientServiceClassificationJson);
                } else if (eventType.equals(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)) {
                    unsyncEvents.add(event);
                } else {
                    JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
                    if (isNullOrEmptyJSONObject(clientClassificationJson)) {
                        continue;
                    }
                    //iterate through the events
                    if (event.has("client")) {
                        processEvent(event, event.getJSONObject("client"), clientClassificationJson);
                    }
                }
            }

            // Unsync events that are should not be in this device
            if (!unsyncEvents.isEmpty()) {
                unSync(unsyncEvents);
            }
        }

    }

    public Boolean processVaccine(JSONObject vaccine, JSONObject clientVaccineClassificationJson) throws Exception {

        try {

            if (vaccine == null || vaccine.length() == 0) {
                return false;
            }

            if (clientVaccineClassificationJson == null || clientVaccineClassificationJson.length() == 0) {
                return false;
            }

            ContentValues contentValues = processCaseModel(vaccine, clientVaccineClassificationJson);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(contentValues.getAsString(VaccineRepository.DATE));

                VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                Vaccine vaccineObj = new Vaccine();
                vaccineObj.setBaseEntityId(contentValues.getAsString(VaccineRepository.BASE_ENTITY_ID));
                vaccineObj.setName(contentValues.getAsString(VaccineRepository.NAME));
                if (contentValues.containsKey(VaccineRepository.CALCULATION)) {
                    vaccineObj.setCalculation(parseInt(contentValues.getAsString(VaccineRepository.CALCULATION)));
                }
                vaccineObj.setDate(date);
                vaccineObj.setAnmId(contentValues.getAsString(VaccineRepository.ANMID));
                vaccineObj.setLocationId(contentValues.getAsString(VaccineRepository.LOCATIONID));
                vaccineObj.setSyncStatus(VaccineRepository.TYPE_Synced);
                vaccineObj.setFormSubmissionId(vaccine.has(VaccineRepository.FORMSUBMISSION_ID) ? vaccine.getString(VaccineRepository.FORMSUBMISSION_ID) : null);
                vaccineObj.setEventId(vaccine.getString("id"));//FIXME hard coded id

                vaccineRepository.add(vaccineObj);
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean processWeight(JSONObject weight, JSONObject clientWeightClassificationJson) throws Exception {

        try {

            if (weight == null || weight.length() == 0) {
                return false;
            }

            if (clientWeightClassificationJson == null || clientWeightClassificationJson.length() == 0) {
                return false;
            }

            ContentValues contentValues = processCaseModel(weight, clientWeightClassificationJson);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {
                Date date = DateUtil.getDateFromString(contentValues.getAsString(WeightRepository.DATE));
                if (date == null) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        date = dateFormat.parse(contentValues.getAsString(WeightRepository.DATE));
                    } catch (Exception e) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        date = dateFormat.parse(contentValues.getAsString(WeightRepository.DATE));
                    }
                }

                WeightRepository weightRepository = VaccinatorApplication.getInstance().weightRepository();
                Weight weightObj = new Weight();
                weightObj.setBaseEntityId(contentValues.getAsString(WeightRepository.BASE_ENTITY_ID));
                if (contentValues.containsKey(WeightRepository.KG)) {
                    weightObj.setKg(parseFloat(contentValues.getAsString(WeightRepository.KG)));
                }
                weightObj.setDate(date);
                weightObj.setAnmId(contentValues.getAsString(WeightRepository.ANMID));
                weightObj.setLocationId(contentValues.getAsString(WeightRepository.LOCATIONID));
                weightObj.setSyncStatus(WeightRepository.TYPE_Synced);
                weightObj.setFormSubmissionId(weight.has(WeightRepository.FORMSUBMISSION_ID) ? weight.getString(WeightRepository.FORMSUBMISSION_ID) : null);
                weightObj.setEventId(weight.getString("id"));//FIXME hard coded id

                weightRepository.add(weightObj);
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }

    public Boolean processService(JSONObject service, JSONObject clientVaccineClassificationJson) throws Exception {

        try {

            if (service == null || service.length() == 0) {
                return false;
            }

            if (clientVaccineClassificationJson == null || clientVaccineClassificationJson.length() == 0) {
                return false;
            }

            ContentValues contentValues = processCaseModel(service, clientVaccineClassificationJson);

            // save the values to db
            if (contentValues != null && contentValues.size() > 0) {

                String name = contentValues.getAsString(RecurringServiceTypeRepository.NAME);
                if (StringUtils.isNotBlank(name)) {
                    name = name.replaceAll("_", " ").replace("dose", "").trim();
                }

                Date date = null;
                String eventDateStr = contentValues.getAsString(RecurringServiceRecordRepository.DATE);
                if (StringUtils.isNotBlank(eventDateStr)) {
                    date = DateUtil.getDateFromString(eventDateStr);
                    if (date == null) {
                        try {
                            date = DateUtil.parseDate(eventDateStr);
                        } catch (ParseException e) {
                            Log.e(TAG, e.toString(), e);
                        }
                    }
                }

                String value = null;

                if (StringUtils.containsIgnoreCase(name, "ITN")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String itnDateString = contentValues.getAsString("itn_date");
                    if (StringUtils.isNotBlank(itnDateString)) {
                        date = simpleDateFormat.parse(itnDateString);
                    }


                    value = RecurringIntentService.ITN_PROVIDED;
                    if (contentValues.getAsString("itn_has_net") != null) {
                        value = RecurringIntentService.CHILD_HAS_NET;
                    }

                }

                RecurringServiceTypeRepository recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
                List<ServiceType> serviceTypeList = recurringServiceTypeRepository.searchByName(name);
                if (serviceTypeList == null || serviceTypeList.isEmpty()) {
                    return false;
                }

                if (date == null) {
                    return false;
                }

                RecurringServiceRecordRepository recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();
                ServiceRecord serviceObj = new ServiceRecord();
                serviceObj.setBaseEntityId(contentValues.getAsString(RecurringServiceRecordRepository.BASE_ENTITY_ID));
                serviceObj.setName(name);
                serviceObj.setDate(date);
                serviceObj.setAnmId(contentValues.getAsString(RecurringServiceRecordRepository.ANMID));
                serviceObj.setLocationId(contentValues.getAsString(RecurringServiceRecordRepository.LOCATIONID));
                serviceObj.setSyncStatus(RecurringServiceRecordRepository.TYPE_Synced);
                serviceObj.setFormSubmissionId(service.has(RecurringServiceRecordRepository.FORMSUBMISSION_ID) ? service.getString(RecurringServiceRecordRepository.FORMSUBMISSION_ID) : null);
                serviceObj.setEventId(service.getString("id"));//FIXME hard coded id
                serviceObj.setValue(value);
                serviceObj.setRecurringServiceId(serviceTypeList.get(0).getId());

                recurringServiceRecordRepository.add(serviceObj);
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }


    public ContentValues processCaseModel(JSONObject entity, JSONObject clientClassificationJson) {
        try {
            JSONArray columns = clientClassificationJson.getJSONArray("columns");

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < columns.length(); i++) {
                JSONObject colObject = columns.getJSONObject(i);
                String columnName = colObject.getString("column_name");
                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                String dataSegment = null;
                String fieldName = jsonMapping.getString("field");
                String fieldValue = null;
                String responseKey = null;
                String valueField = jsonMapping.has("value_field") ? jsonMapping.getString("value_field") : null;
                if (fieldName != null && fieldName.contains(".")) {
                    String fieldNameArray[] = fieldName.split("\\.");
                    dataSegment = fieldNameArray[0];
                    fieldName = fieldNameArray[1];
                    fieldValue = jsonMapping.has("concept") ? jsonMapping.getString("concept") : (jsonMapping.has("formSubmissionField") ? jsonMapping.getString("formSubmissionField") : null);
                    if (fieldValue != null) {
                        responseKey = VALUES_KEY;
                    }
                }

                Object jsonDocSegment = null;

                if (dataSegment != null) {
                    //pick data from a specific section of the doc
                    jsonDocSegment = entity.has(dataSegment) ? entity.get(dataSegment) : null;

                } else {
                    //else the use the main doc as the doc segment
                    jsonDocSegment = entity;

                }

                if (jsonDocSegment instanceof JSONArray) {

                    JSONArray jsonDocSegmentArray = (JSONArray) jsonDocSegment;

                    for (int j = 0; j < jsonDocSegmentArray.length(); j++) {
                        JSONObject jsonDocObject = jsonDocSegmentArray.getJSONObject(j);
                        String columnValue = null;
                        if (fieldValue == null) {
                            //this means field_value and response_key are null so pick the value from the json object for the field_name
                            if (jsonDocObject.has(fieldName)) {
                                columnValue = jsonDocObject.getString(fieldName);
                            }
                        } else {
                            //this means field_value and response_key are not null e.g when retrieving some value in the events obs section
                            String expectedFieldValue = jsonDocObject.getString(fieldName);
                            //some events can only be differentiated by the event_type value eg pnc1,pnc2, anc1,anc2

                            if (expectedFieldValue.equalsIgnoreCase(fieldValue)) {
                                if (StringUtils.isNotBlank(valueField) && jsonDocObject.has(valueField)) {
                                    columnValue = jsonDocObject.getString(valueField);
                                } else {
                                    List<String> values = getValues(jsonDocObject.get(responseKey));
                                    if (!values.isEmpty()) {
                                        columnValue = values.get(0);
                                    }
                                }
                            }
                        }
                        // after successfully retrieving the column name and value store it in Content value
                        if (columnValue != null) {
                            columnValue = getHumanReadableConceptResponse(columnValue, jsonDocObject);
                            contentValues.put(columnName, columnValue);
                        }
                    }

                } else {
                    //e.g client attributes section
                    String columnValue = null;
                    JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                    columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject.getString(fieldName) : "";
                    // after successfully retrieving the column name and value store it in Content value
                    if (columnValue != null) {
                        columnValue = getHumanReadableConceptResponse(columnValue, jsonDocSegmentObject);
                        contentValues.put(columnName, columnValue);
                    }

                }


            }

            return contentValues;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    @Override
    public void updateFTSsearch(String tableName, String entityId, ContentValues contentValues) {
        super.updateFTSsearch(tableName, entityId, contentValues);

        if (contentValues != null && StringUtils.containsIgnoreCase(tableName, "child")) {
            String dob = contentValues.getAsString("dob");

            if (StringUtils.isBlank(dob)) {
                return;
            }

            DateTime birthDateTime = new DateTime(dob);
            VaccineSchedule.updateOfflineAlerts(entityId, birthDateTime, "child");
            ServiceSchedule.updateOfflineAlerts(entityId, birthDateTime);
        }
    }

    public boolean unSync(List<JSONObject> events) {
        try {

            if (events == null || events.isEmpty()) {
                return false;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String registeredAnm = allSharedPreferences.fetchRegisteredANM();

            String clientClassificationStr = getFileContents("ec_client_fields.json");
            JSONObject clientClassificationJson = new JSONObject(clientClassificationStr);
            JSONArray bindObjects = clientClassificationJson.getJSONArray("bindobjects");

            DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(getContext());

            for (JSONObject event : events) {
                unSync(ecUpdater, detailsRepository, bindObjects, event, registeredAnm);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }

        return false;
    }

    private boolean unSync(ECSyncUpdater ecUpdater, DetailsRepository detailsRepository, JSONArray bindObjects, JSONObject event, String registeredAnm) {
        try {
            String baseEntityId = event.getString(baseEntityIdJSONKey);
            String providerId = event.getString(providerIdJSONKey);

            if (providerId.equals(registeredAnm)) {
                boolean eventDeleted = ecUpdater.deleteEventsByBaseEntityId(baseEntityId);
                boolean clientDeleted = ecUpdater.deleteClient(baseEntityId);
                Log.d(getClass().getName(), "EVENT_DELETED: " + eventDeleted);
                Log.d(getClass().getName(), "ClIENT_DELETED: " + clientDeleted);

                boolean detailsDeleted = detailsRepository.deleteDetails(baseEntityId);
                Log.d(getClass().getName(), "DETAILS_DELETED: " + detailsDeleted);

                for (int i = 0; i < bindObjects.length(); i++) {

                    JSONObject bindObject = bindObjects.getJSONObject(i);
                    String tableName = bindObject.getString("name");

                    boolean caseDeleted = deleteCase(tableName, baseEntityId);
                    Log.d(getClass().getName(), "CASE_DELETED: " + caseDeleted);
                }

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }

    private Integer parseInt(String string) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    private Float parseFloat(String string) {
        try {
            return Float.valueOf(string);
        } catch (NumberFormatException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }
}

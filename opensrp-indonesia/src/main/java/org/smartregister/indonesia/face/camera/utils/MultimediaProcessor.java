package org.smartregister.indonesia.face.camera.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.CloudantDataHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by sid on 2/22/17.
 */
public class MultimediaProcessor extends ClientProcessor {

    private static final String TAG = MultimediaProcessor.class.getSimpleName();
    private static MultimediaProcessor instance;

    public MultimediaProcessor(Context context) {
        super(context);
    }

    public static MultimediaProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new MultimediaProcessor(context);
        }
        return instance;
    }

    public synchronized void processMultimediaClient() throws Exception {
        CloudantDataHandler handler = CloudantDataHandler.getInstance(mContext);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        long lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        String multimediaClassificationStr = getFileContents("ec_client_fields.json");

        List<JSONObject> multimedias = handler.getUpdatedEventsAndAlerts(lastSyncDate);

        fetchData("ec_multimedia");

        if (!multimedias.isEmpty()) {
            Log.e(TAG, "processMultimediaClient: "+"exist" );
            for (JSONObject multimedia : multimedias) {
                String type = multimedia.has("type") ? multimedia.getString("type") : null;

                if (type != null && type.equals("Multimedia")) {
                    JSONObject clientClassificationJson = new JSONObject(multimediaClassificationStr);
                    if(isNullOrEmptyJSONObject(clientClassificationJson)){
                        continue;
                    }
                    //iterate through the events
                    processMultimedia(multimedia, clientClassificationJson);
                }
//                else if (type.equals("Action")) {
//                    JSONObject clientAlertClassificationJson = new JSONObject(clientAlertsStr);
//                    if(isNullOrEmptyJSONObject(clientAlertClassificationJson)){
//                        continue;
//                    }
//
//                    processAlert(eventOrAlert, clientAlertClassificationJson);
//                }
            }
        }

        allSharedPreferences.saveLastSyncDate(lastSyncDate.getTime());
    }

    private JSONObject fetchData(String tableName) throws JSONException {
        Log.e(TAG, "fetchData: "+"start" );
        String multimediaClassificationStr = getFileContents("ec_client_fields.json");

        JSONObject jsonObject = new JSONObject(multimediaClassificationStr);
        JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

        for(int i = 0 ; i < bindtypeObjects.length(); i++){
            JSONObject bo = bindtypeObjects.getJSONObject(i);
            if (bo.getString("name").equals(tableName)){

                JSONArray columnsJsonArray = bo.getJSONArray("columns");
                String [] columnNames = new String[columnsJsonArray.length()];
                for(int j = 0 ; j < columnNames.length; j++){
                    JSONObject columnObject = columnsJsonArray.getJSONObject(j);
                    columnNames[j] =  columnObject.getString("column_name");
                }
                Log.e(TAG, "processMultimediaClient: "+ Arrays.toString(columnNames));
            }

        }

        return null;
    }


    public Boolean processMultimedia(JSONObject multimedia, JSONObject multimediaClassificationJson) throws Exception {

        try {

            if(multimedia == null || multimedia.length() == 0){
                return false;
            }

            if (multimediaClassificationJson == null || multimediaClassificationJson.length() == 0) {
                return false;
            }

            JSONArray columns = multimediaClassificationJson.getJSONArray("columns");

            ContentValues contentValues = new ContentValues();

            for (int i = 0; i < columns.length(); i++) {
                JSONObject colObject = columns.getJSONObject(i);
                String columnName = colObject.getString("column_name");
                JSONObject jsonMapping = colObject.getJSONObject("json_mapping");
                String dataSegment = null;
                String fieldName = jsonMapping.getString("field");
                if (fieldName != null && fieldName.contains(".")) {
                    String fieldNameArray[] = fieldName.split("\\.");
                    dataSegment = fieldNameArray[0];
                    fieldName = fieldNameArray[1];
                }

                Object jsonDocSegment;

                if (dataSegment != null) {
                    //pick data from a specific section of the doc
                    jsonDocSegment = multimedia.get(dataSegment);

                } else {
                    //else the use the main doc as the doc segment
                    jsonDocSegment = multimedia;

                }

                //e.g client attributes section
                String columnValue;
                JSONObject jsonDocSegmentObject = (JSONObject) jsonDocSegment;
                columnValue = jsonDocSegmentObject.has(fieldName) ? jsonDocSegmentObject.getString(fieldName) : "";
                // after successfully retrieving the column name and value store it in Content value
                if (columnValue != null) {
                    columnValue = getHumanReadableConceptResponse(columnValue, jsonDocSegmentObject);
                    contentValues.put(columnName, columnValue);
                }
            }

            // save the values to db
            if(contentValues.size() > 0) {
                executeInsertAlert(contentValues);
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            return null;
        }
    }


}

package org.ei.opensrp.mcare.sync;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.util.Pair;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Map;

/**
 * Created by keyman on 03/10/16.
 */

public class BaseClientProcessorTest extends AndroidTestCase {

    protected static final String baseEntityIdJSONKey = "baseEntityId";

    protected static final String detailsUpdated = "detailsUpdated";

    @Before
    protected void setUp() throws Exception {
        //super.setUp();
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().getPath());
    }

    protected JSONObject createEmptyJsonObject() {
        try {
            return new JSONObject("{}");

        } catch (JSONException e) {
            return null;
        }
    }

    protected JSONObject createJsonObject(String key, Object value) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(key, value);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    protected JSONObject updateJsonObject(JSONObject jsonObject, String key, Object value) {
        try {
            if (jsonObject == null) {
                jsonObject = new JSONObject();
            }
            jsonObject.put(key, value);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    protected JSONArray updateJsonArray(JSONArray jsonArray, Object value) {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        jsonArray.put(value);
        return jsonArray;
    }

    protected String getBaseEntityId() {
        return String.valueOf(DateTime.now().getMillis());
    }

    protected JSONObject createClient(String baseEntityId) {
        return createJsonObject(baseEntityIdJSONKey, baseEntityId);
    }

    protected JSONObject createEvent(String baseEntityId, boolean details) {
        JSONObject event = createJsonObject(baseEntityIdJSONKey, baseEntityId);
        if (details) {
            updateJsonObject(event, detailsUpdated, true);
        }
        return event;
    }

    protected JSONObject createClassification() {

        JSONArray cases1 = new JSONArray();
        updateJsonArray(cases1, "case1");
        updateJsonArray(cases1, "case2");
        updateJsonArray(cases1, "case3");

        JSONObject field1 = new JSONObject();
        updateJsonObject(field1, "field", "eventType");
        updateJsonObject(field1, "field_value", "Test Document Type");
        updateJsonObject(field1, "creates_case", cases1);

        JSONArray fields1 = new JSONArray();
        fields1.put(field1);

        JSONObject rule1 = new JSONObject();
        updateJsonObject(rule1, "type", "event");
        updateJsonObject(rule1, "fields", fields1);


        JSONObject classificationRule1 = new JSONObject();
        updateJsonObject(classificationRule1, "comment", "Test Comment");
        updateJsonObject(classificationRule1, "rule", rule1);

        JSONArray cases2 = new JSONArray();
        updateJsonArray(cases2, "case4");
        updateJsonArray(cases2, "case5");

        JSONObject field2 = new JSONObject();
        updateJsonObject(field2, "field", "eventType");
        updateJsonObject(field2, "field_value", "Test Document Type 2");
        updateJsonObject(field2, "creates_case", cases2);

        JSONArray fields2 = new JSONArray();
        fields2.put(field2);

        JSONObject rule2 = new JSONObject();
        updateJsonObject(rule2, "type", "event");
        updateJsonObject(rule2, "fields", fields2);


        JSONObject classificationRule2 = new JSONObject();
        updateJsonObject(classificationRule2, "comment", "Test Comment 2");
        updateJsonObject(classificationRule2, "rule", rule2);


        JSONArray classificationRules = new JSONArray();
        updateJsonArray(classificationRules, classificationRule1);
        updateJsonArray(classificationRules, classificationRule2);

        JSONObject classification = createJsonObject("case_classification_rules", classificationRules);
        return classification;
    }


    protected JSONObject createField(String field, String fieldValue, JSONArray createCases, JSONArray closesCases) {

        JSONObject fieldJson = createJsonObject("field", field);
        updateJsonObject(fieldJson, "field_value", fieldValue);

        if (createCases != null) {
            updateJsonObject(fieldJson, "creates_case", createCases);
        }

        if (closesCases != null) {
            updateJsonObject(fieldJson, "closes_case", closesCases);
        }

        return fieldJson;
    }

    protected JSONObject createField(String field, String concept, JSONArray values, JSONArray createCases, JSONArray closesCases) {

        JSONObject fieldJson = createJsonObject("field", field);
        updateJsonObject(fieldJson, "concept", concept);
        updateJsonObject(fieldJson, "values", values);

        if (createCases != null) {
            updateJsonObject(fieldJson, "creates_case", createCases);
        }

        if (closesCases != null) {
            updateJsonObject(fieldJson, "closes_case", closesCases);
        }

        return fieldJson;
    }

    protected JSONObject createField() {
        JSONArray cases = createCases("case1", "case2", "case3");
        return createField("eventType", "Test Event Type", cases, null);
    }

    protected JSONArray createCases(String... cases) {
        JSONArray casesArray = new JSONArray();
        for (String caseString : cases) {
            updateJsonArray(casesArray, caseString);
        }
        return casesArray;
    }

    protected long getEventDate(Object eventDate) {
        if (eventDate instanceof Long) {
            return (Long) eventDate;
        } else {
            Date date = DateUtil.toDate(eventDate);
            if (date != null)
                return date.getTime();
        }
        return new Date().getTime();
    }

    protected JSONObject createAlertClassification(String columnName, String field) {

        JSONObject column = createColumn(columnName, field);

        JSONArray columns = new JSONArray();
        columns.put(column);

        JSONObject clientAlert = createJsonObject("name", "alerts");
        updateJsonObject(clientAlert, "columns", columns);

        return clientAlert;
    }

    private JSONObject createColumn(String columnName, String field) {
        return createColumn(columnName, field, null, null);
    }

    private JSONObject createColumn(String columnName, String field, String concept, String type) {
        JSONObject jsonMapping = createJsonObject("field", field);
        if (StringUtils.isNotBlank(concept)) {
            updateJsonObject(jsonMapping, "concept", concept);
        }

        JSONObject column = createJsonObject("column_name", columnName);
        updateJsonObject(column, "json_mapping", jsonMapping);



        if (StringUtils.isNotBlank(type)) {
            updateJsonObject(column, "type", type);
        }

        return column;
    }

    protected JSONObject createAlert(String caseID, Map<String, String> data) {
        JSONObject dataObject = new JSONObject();
        if (data != null && !data.isEmpty()) {
            for (String key : data.keySet()) {
                updateJsonObject(dataObject, key, data.get(key));
            }
        }

        JSONObject alert = createJsonObject("baseEntityID", caseID);
        updateJsonObject(alert, "data", dataObject);

        return alert;
    }

    protected JSONObject createBindObject(String caseString, Map<String, Pair<String, String>> columns) {
        JSONObject bind = createJsonObject("name", caseString);
        JSONArray columnArray = new JSONArray();
        if (columns != null && !columns.isEmpty()) {
            for (String key : columns.keySet()) {
                Pair<String, String> pair = columns.get(key);
                columnArray.put(createColumn(key, pair.first, pair.second, StringUtils.isBlank(pair.second)? "Client": "Event"));
            }
        }
        updateJsonObject(bind, "columns", columnArray);
        return bind;
    }

    @Test
    public void testSample(){
        Assert.assertTrue(true);
    }
}
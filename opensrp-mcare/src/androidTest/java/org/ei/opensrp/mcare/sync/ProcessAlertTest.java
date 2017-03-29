package org.ei.opensrp.mcare.sync;

import android.content.ContentValues;

import org.ei.opensrp.sync.ClientProcessor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by keyman on 05/10/16.
 */
public class ProcessAlertTest extends BaseClientProcessorTest {

    @Test
    public void testProcessAlertWhenClientClassificationIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            Map<String, String> data = new HashMap<>();
            data.put("schedule", "Test1");

            JSONObject alert = createAlert(baseEntityId, data);

            Boolean processed = ClientProcessor.getInstance(getContext()).processAlert(alert, null);
            assertFalse("Alert should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processAlert(alert, createEmptyJsonObject());
            assertFalse("Alert should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessAlertWhenAlertIsNullOrEmpty() {
        try {

            JSONObject clientClassification = createAlertClassification("column 1", "Test Field");

            Boolean processed = ClientProcessor.getInstance(getContext()).processAlert(null, clientClassification);
            assertFalse("Alert should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processAlert(createEmptyJsonObject(), clientClassification);
            assertFalse("Alert should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessAlertWhenColumnNameIsMissingInClientClassification() {
        try {

            String baseEntityId = getBaseEntityId();

            Map<String, String> data = new HashMap<>();
            data.put("schedule", "Test1");

            JSONObject alert = createAlert(baseEntityId, data);

            JSONObject clientClassification = createAlertClassification("column test", "Test Field");
            JSONArray columns = clientClassification.getJSONArray("columns");
            JSONObject column = columns.getJSONObject(0);
            column.remove("column_name");

            Boolean processed = ClientProcessor.getInstance(getContext()).processAlert(alert, clientClassification);
            assertNull("Alert should not be processed, Exception thrown", processed);


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessAlertWhenFieldIsMissingInClientClassification() {
        try {

            String baseEntityId = getBaseEntityId();

            Map<String, String> data = new HashMap<>();
            data.put("schedule", "Test1");

            JSONObject alert = createAlert(baseEntityId, data);

            JSONObject clientClassification = createAlertClassification("column test", "Test Field");
            JSONArray columns = clientClassification.getJSONArray("columns");
            JSONObject column = columns.getJSONObject(0);
            JSONObject mapping = column.getJSONObject("json_mapping");
            mapping.remove("field");

            Boolean processed = ClientProcessor.getInstance(getContext()).processAlert(alert, clientClassification);
            assertNull("Alert should not be processed, Exception thrown", processed);


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessAlertWithoutSegment() {
        try {

            String baseEntityId = getBaseEntityId();


            JSONObject alert = createAlert(baseEntityId, null);

            JSONObject clientClassification = createAlertClassification("caseID", "baseEntityID");

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doNothing().when(spy).executeInsertAlert(Mockito.any(ContentValues.class));
            Boolean processed = spy.processAlert(alert, clientClassification);
            assertTrue("Alert should be processed", processed);

            ArgumentCaptor<ContentValues> contentValuesArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);
            Mockito.verify(spy, Mockito.times(1)).executeInsertAlert(contentValuesArgumentCaptor.capture());

            ContentValues contentValues = contentValuesArgumentCaptor.getValue();
            assertEquals(baseEntityId, contentValues.get("caseID"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessAlertWithSegment() {
        try {

            String baseEntityId = getBaseEntityId();


            Map<String, String> data = new HashMap<String, String>();
            data.put("schedule", "Testing Segment");
            JSONObject alert = createAlert(baseEntityId, data);

            JSONObject clientClassification = createAlertClassification("mySchedule", "data.schedule");

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doNothing().when(spy).executeInsertAlert(Mockito.any(ContentValues.class));
            Boolean processed = spy.processAlert(alert, clientClassification);
            assertTrue("Alert should be processed", processed);

            ArgumentCaptor<ContentValues> contentValuesArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);
            Mockito.verify(spy, Mockito.times(1)).executeInsertAlert(contentValuesArgumentCaptor.capture());

            ContentValues contentValues = contentValuesArgumentCaptor.getValue();
            assertEquals("Testing Segment", contentValues.getAsString("mySchedule"));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

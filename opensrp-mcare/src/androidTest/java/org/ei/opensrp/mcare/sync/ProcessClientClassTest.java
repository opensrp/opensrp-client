package org.ei.opensrp.mcare.sync;

import org.ei.opensrp.sync.ClientProcessor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by keyman on 30/09/16.
 */
public class ProcessClientClassTest extends BaseClientProcessorTest {


    @Test
    public void testProcessClientClassWhenClassIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(null, event, client);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(createEmptyJsonObject(), event, client);
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClientClassWhenEventIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject classification = createClassification();

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, null, client);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, createEmptyJsonObject(), client);
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClientClassWhenClientIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, null);
            assertFalse("Client class should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, createEmptyJsonObject());
            assertFalse("Client class should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClientClassWhenClassHasNoRules() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            classification.remove("rule");

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, client);
            assertNull("Client class should not be processed, Exception thrown", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClientClassWhenRuleHasNoFields() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification();
            classification.remove("rule");
            updateJsonObject(classification, "rule", createEmptyJsonObject());
            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClientClass(classification, event, client);
            assertNull("Client class should not be processed, Exception thrown", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClient() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject classification = createClassification().getJSONArray("case_classification_rules").getJSONObject(0);
            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());

            ClientProcessor spy = Mockito.spy(clientProcessor);
            Mockito.doReturn(true).when(spy).processField(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONObject.class));

            Boolean processed = spy.processClientClass(classification, event, client);
            assertTrue("Client class should have been processed", processed);

            ArgumentCaptor<JSONObject> fieldCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> eventCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> clientCaptor = ArgumentCaptor.forClass(JSONObject.class);

            Mockito.verify(spy, Mockito.times(1)).processField(fieldCaptor.capture(), eventCaptor.capture(), clientCaptor.capture());

            JSONObject ruleObject = classification.getJSONObject("rule");
            JSONArray fields = ruleObject.getJSONArray("fields");

            assertEquals(fields.get(0), fieldCaptor.getValue());
            assertEquals(event, eventCaptor.getValue());
            assertEquals(client, clientCaptor.getValue());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

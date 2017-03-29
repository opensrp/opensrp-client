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
 * Created by keyman on 04/10/16.
 */
public class ProcessFieldTest extends BaseClientProcessorTest {

    @Test
    public void testProcessFieldWhenFieldJsonIsNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processField(null, event, client);
            assertFalse("Field should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processField(createEmptyJsonObject(), event, client);
            assertFalse("Field should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWhenFieldJsonHasIrrelevantValues() {
        try {

            JSONObject fieldJson = createJsonObject("TEst", "TestIng");

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processField(fieldJson, event, client);
            assertTrue("Field should be processed, processCaseModel() and closeCase() should not be called", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWhenFieldValueIsMissingInEvent() {
        try {

            JSONObject fieldJson = createField();

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processField(fieldJson, event, client);
            assertTrue("Field should be processed, processCaseModel() and closeCase() should not be called", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWithCreateCaseForFieldValue() {
        try {

            JSONArray caseArray = createCases("table1", "table2");
            JSONObject fieldJson = createField("eventType", "FieldValue", caseArray, null);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);
            updateJsonObject(event, "eventType", "FieldValue");

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(true).when(spy).processCaseModel(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));
            Mockito.doReturn(true).when(spy).closeCase(Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));

            Boolean processed = spy.processField(fieldJson, event, client);
            assertTrue("Field should be processed", processed);

            ArgumentCaptor<JSONObject> eventCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> clientCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONArray> caseCaptor = ArgumentCaptor.forClass(JSONArray.class);

            Mockito.verify(spy, Mockito.times(1)).processCaseModel(eventCaptor.capture(), clientCaptor.capture(), caseCaptor.capture());

            assertEquals(event, eventCaptor.getValue());
            assertEquals(client, clientCaptor.getValue());
            assertEquals(caseArray, caseCaptor.getValue());

            Mockito.verify(spy, Mockito.times(1)).closeCase(clientCaptor.capture(), caseCaptor.capture());

            assertEquals(client, clientCaptor.getValue());
            assertNull("close clases should be null", caseCaptor.getValue());


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWithCreateAndCloseCaseForFieldValue() {
        try {

            JSONArray createCaseArray = createCases("table1", "table2");
            JSONArray closeCaseArray = createCases("table3", "table4");

            JSONObject fieldJson = createField("eventType", "Test Value", createCaseArray, closeCaseArray);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);
            updateJsonObject(event, "eventType", "Test Value");

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(true).when(spy).processCaseModel(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));
            Mockito.doReturn(true).when(spy).closeCase(Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));

            Boolean processed = spy.processField(fieldJson, event, client);
            assertTrue("Field should be processed", processed);

            ArgumentCaptor<JSONObject> eventCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> clientCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONArray> caseCaptor = ArgumentCaptor.forClass(JSONArray.class);

            Mockito.verify(spy, Mockito.times(1)).processCaseModel(eventCaptor.capture(), clientCaptor.capture(), caseCaptor.capture());

            assertEquals(event, eventCaptor.getValue());
            assertEquals(client, clientCaptor.getValue());
            assertEquals(createCaseArray, caseCaptor.getValue());

            Mockito.verify(spy, Mockito.times(1)).closeCase(clientCaptor.capture(), caseCaptor.capture());

            assertEquals(client, clientCaptor.getValue());
            assertEquals(closeCaseArray, caseCaptor.getValue());


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWhenConceptIsMissingDataSegmentInEvent() {
        try {

            JSONArray values = createCases("123AAA", "124AAAA");

            JSONArray createCaseArray = createCases("table1", "table2");

            JSONObject fieldJson = createField("obs.fieldCode", "12AAAAA", values, createCaseArray, null);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            Boolean processed = ClientProcessor.getInstance(getContext()).processField(fieldJson, event, client);
            assertTrue("Field should be processed,  processCaseModel() and closeCase() should not be called", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWhenConceptIsMissingFieldValueInEvent() {
        try {

            JSONArray values = createCases("123AAA", "124AAAA");

            JSONArray createCaseArray = createCases("table1", "table2");

            JSONObject fieldJson = createField("obs.fieldCode", "12AAAAA", values, createCaseArray, null);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            JSONObject obs1 = createJsonObject("fieldCode", "154AAAA");

            JSONArray obsArray = new JSONArray();
            updateJsonArray(obsArray, obs1);

            updateJsonObject(event, "obs", obsArray);

            Boolean processed = ClientProcessor.getInstance(getContext()).processField(fieldJson, event, client);
            assertTrue("Field should be processed,  processCaseModel() and closeCase() should not be called", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWithCreateCaseForConcept() {
        try {

            JSONArray values = createCases("125AAA", "126AAAA");

            JSONArray createCaseArray = createCases("table1", "table2");

            JSONObject fieldJson = createField("obs.fieldCode", "12AAAAA", values, createCaseArray, null);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            JSONArray obsValues = createCases("125AAA");
            JSONObject obs1 = createJsonObject("fieldCode", "12AAAAA");
            updateJsonObject(obs1, "values", obsValues);

            JSONArray obsArray = new JSONArray();
            updateJsonArray(obsArray, obs1);

            updateJsonObject(event, "obs", obsArray);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(true).when(spy).processCaseModel(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));
            Mockito.doReturn(true).when(spy).closeCase(Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));

            Boolean processed = spy.processField(fieldJson, event, client);
            assertTrue("Field should be processed", processed);

            ArgumentCaptor<JSONObject> eventCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> clientCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONArray> caseCaptor = ArgumentCaptor.forClass(JSONArray.class);

            Mockito.verify(spy, Mockito.times(1)).processCaseModel(eventCaptor.capture(), clientCaptor.capture(), caseCaptor.capture());

            assertEquals(event, eventCaptor.getValue());
            assertEquals(client, clientCaptor.getValue());
            assertEquals(createCaseArray, caseCaptor.getValue());

            Mockito.verify(spy, Mockito.times(1)).closeCase(clientCaptor.capture(), caseCaptor.capture());

            assertEquals(client, clientCaptor.getValue());
            assertNull("close clases should be null", caseCaptor.getValue());


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessFieldWithCreateAndCloseCaseForConcept() {
        try {

            JSONArray values = createCases("121AAA", "122AAAA");

            JSONArray createCaseArray = createCases("table1", "table2");
            JSONArray closeCaseArray = createCases("table3", "table4");


            JSONObject fieldJson = createField("obs.fieldCode", "13AAAAA", values, createCaseArray, closeCaseArray);

            String baseEntityId = getBaseEntityId();

            JSONObject client = createClient(baseEntityId);
            JSONObject event = createEvent(baseEntityId, true);

            JSONArray obsValues = createCases("121AAA");
            JSONObject obs1 = createJsonObject("fieldCode", "13AAAAA");
            updateJsonObject(obs1, "values", obsValues);

            JSONArray obsArray = new JSONArray();
            updateJsonArray(obsArray, obs1);

            updateJsonObject(event, "obs", obsArray);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(true).when(spy).processCaseModel(Mockito.any(JSONObject.class), Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));
            Mockito.doReturn(true).when(spy).closeCase(Mockito.any(JSONObject.class), Mockito.any(JSONArray.class));

            Boolean processed = spy.processField(fieldJson, event, client);
            assertTrue("Field should be processed", processed);

            ArgumentCaptor<JSONObject> eventCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONObject> clientCaptor = ArgumentCaptor.forClass(JSONObject.class);
            ArgumentCaptor<JSONArray> caseCaptor = ArgumentCaptor.forClass(JSONArray.class);

            Mockito.verify(spy, Mockito.times(1)).processCaseModel(eventCaptor.capture(), clientCaptor.capture(), caseCaptor.capture());

            assertEquals(event, eventCaptor.getValue());
            assertEquals(client, clientCaptor.getValue());
            assertEquals(createCaseArray, caseCaptor.getValue());

            Mockito.verify(spy, Mockito.times(1)).closeCase(clientCaptor.capture(), caseCaptor.capture());

            assertEquals(client, clientCaptor.getValue());
            assertEquals(closeCaseArray, caseCaptor.getValue());


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}

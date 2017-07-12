package org.ei.opensrp.mcare.sync;

import android.content.ContentValues;
import android.util.Pair;

import org.ei.opensrp.sync.ClientProcessor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keyman on 05/10/16.
 */
public class ProcessCaseModelTest extends BaseClientProcessorTest {

    @Test
    public void testProcessCloseCaseModelWhenNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            Boolean processed = ClientProcessor.getInstance(getContext()).closeCase(client, null);
            assertFalse("Case Model should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).closeCase(client, new JSONArray());
            assertFalse("Case Model should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCloseCaseModelWhenClientBaseEntityIdIsNull() {
        try {

            JSONObject client = createEmptyJsonObject();
            updateJsonObject(client, "Test", "test");

            Boolean processed = ClientProcessor.getInstance(getContext()).closeCase(client, createCases("case1"));
            assertNull("Case Model should not be processed, Exception thrown", processed);


        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCloseCaseModel() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doNothing().when(spy).closeCase("case1", client.getString(baseEntityIdJSONKey));

            Mockito.doNothing().when(spy).updateFTSsearch("case1", client.getString(baseEntityIdJSONKey), Mockito.any(ContentValues.class));

            Boolean processed = spy.closeCase(client, createCases("case1"));
            assertTrue("Case Model should not be processed", processed);

            Mockito.verify(spy, Mockito.times(1)).closeCase("case1", client.getString(baseEntityIdJSONKey));

            Mockito.verify(spy, Mockito.times(1)).updateFTSsearch("case1", client.getString(baseEntityIdJSONKey), Mockito.any(ContentValues.class));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCloseMultipleCasesModel() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONArray cases = createCases("case2", "case3");

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());
            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doNothing().when(spy).closeCase(Mockito.anyString(), Mockito.anyString());

            Mockito.doNothing().when(spy).updateFTSsearch(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class));

            Boolean processed = spy.closeCase(client, cases);
            assertTrue("Case Model should not be processed", processed);

            ArgumentCaptor<String> caseCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> baseEntityCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);

            Mockito.verify(spy, Mockito.times(2)).closeCase(caseCaptor.capture(), baseEntityCaptor.capture());

            List<String> capturedList = caseCaptor.getAllValues();
            for (int i = 0; i < cases.length(); i++) {
                assertEquals(cases.get(i), capturedList.get(i));
            }

            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            Mockito.verify(spy, Mockito.times(2)).updateFTSsearch(caseCaptor.capture(), baseEntityCaptor.capture(), contentValuesCaptor.capture());

            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            capturedList = caseCaptor.getAllValues();
            for (int i = 0; i < cases.length(); i++) {
                assertEquals(cases.get(i), capturedList.get(i));
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    public void testProcessCaseModelWhenNullOrEmpty() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONObject event = createEvent(baseEntityId, false);

            Boolean processed = ClientProcessor.getInstance(getContext()).processCaseModel(event, client, null);
            assertFalse("Case Model should not be processed", processed);

            processed = ClientProcessor.getInstance(getContext()).processCaseModel(event, client, new JSONArray());
            assertFalse("Case Model should not be processed", processed);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCaseModelForSimpleField() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONObject event = createEvent(baseEntityId, false);
            String eventDate = "2016-10-10T00:00:00.000+0300";
            updateJsonObject(event, "eventDate", eventDate);

            Map<String, Pair<String, String>> columns = new HashMap<String, Pair<String, String>>();
            columns.put("my_base_id", Pair.create("baseEntityId", ""));

            JSONObject bindObject = createBindObject("case5", columns);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());

            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(bindObject).when(spy).getColumnMappings("case5");

            Mockito.doReturn(1l).when(spy).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.any(String.class));

            Mockito.doNothing().when(spy).updateFTSsearch(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class));

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong());

            Boolean processed = spy.processCaseModel(event, client, createCases("case5"));
            assertTrue("Case Model should be processed", processed);

            Mockito.verify(spy).getColumnMappings("case5");

            ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
            ArgumentCaptor<String> tableNameCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(spy).executeInsertStatement(contentValuesCaptor.capture(), tableNameCaptor.capture());

            ContentValues contentValues = contentValuesCaptor.getValue();
            assertEquals(baseEntityId, contentValues.getAsString("my_base_id"));
            assertEquals(new Integer(0), contentValues.getAsInteger("is_closed"));
            assertEquals(baseEntityId, contentValues.getAsString("base_entity_id"));

            assertEquals("case5", tableNameCaptor.getValue());

            ArgumentCaptor<String> caseCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> baseEntityCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(spy).updateFTSsearch(caseCaptor.capture(), baseEntityCaptor.capture(), contentValuesCaptor.capture());
            assertEquals("case5", caseCaptor.getValue());
            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

            ArgumentCaptor<Long> timeStampCaptor = ArgumentCaptor.forClass(Long.class);
            Mockito.verify(spy, Mockito.times(3)).saveClientDetails(baseEntityCaptor.capture(), keyCaptor.capture(), valueCaptor.capture(), timeStampCaptor.capture());

            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

            List<String> keysCaptured = keyCaptor.getAllValues();
            List<String> valuesCaptured = valueCaptor.getAllValues();

            assertTrue("Key should be updated in details table", keysCaptured.contains("is_closed"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("my_base_id"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("base_entity_id"));


            assertEquals(new Integer(0), Integer.valueOf(valuesCaptured.get(keysCaptured.indexOf("is_closed"))));
            assertEquals(baseEntityId, valuesCaptured.get(keysCaptured.indexOf("my_base_id")));
            assertEquals(baseEntityId, valuesCaptured.get(keysCaptured.indexOf("base_entity_id")));


            Mockito.verify(spy, Mockito.times(4)).saveClientDetails(baseEntityCaptor.capture(), Mockito.anyMap(), timeStampCaptor.capture());
            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCaseModelForConceptField() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONObject event = createEvent(baseEntityId, false);
            String eventDate = "2016-10-10T00:00:00.000+0300";
            updateJsonObject(event, "eventDate", eventDate);

            JSONObject field = createJsonObject("fieldCode", "130AAA");
            updateJsonObject(field, "fieldType", "concept");
            updateJsonObject(field, "value", "134AAA");
            updateJsonObject(field, "values", createCases("134AAA"));
            updateJsonObject(field, "humanReadableValues", createCases("4"));

            JSONArray obs = new JSONArray();
            obs.put(field);
            updateJsonObject(event, "obs", obs);


            Map<String, Pair<String, String>> columns = new HashMap<String, Pair<String, String>>();
            columns.put("ABC", Pair.create("obs.fieldCode", "130AAA"));

            JSONObject bindObject = createBindObject("case6", columns);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());

            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(bindObject).when(spy).getColumnMappings("case6");

            Mockito.doReturn(1l).when(spy).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.anyString());

            Mockito.doNothing().when(spy).updateFTSsearch(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class));

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong());

            Boolean processed = spy.processCaseModel(event, client, createCases("case6"));
            assertTrue("Case Model should be processed", processed);

            Mockito.verify(spy).getColumnMappings("case6");

            ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
            ArgumentCaptor<String> tableNameCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(spy).executeInsertStatement(contentValuesCaptor.capture(), tableNameCaptor.capture());

            ContentValues contentValues = contentValuesCaptor.getValue();
            assertEquals("4", contentValues.getAsString("ABC"));
            assertEquals(new Integer(0), contentValues.getAsInteger("is_closed"));
            assertEquals(baseEntityId, contentValues.getAsString("base_entity_id"));

            assertEquals("case6", tableNameCaptor.getValue());

            ArgumentCaptor<String> caseCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> baseEntityCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(spy).updateFTSsearch(caseCaptor.capture(), baseEntityCaptor.capture(), contentValuesCaptor.capture());
            assertEquals("case6", caseCaptor.getValue());
            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

            ArgumentCaptor<Long> timeStampCaptor = ArgumentCaptor.forClass(Long.class);
            Mockito.verify(spy, Mockito.times(3)).saveClientDetails(baseEntityCaptor.capture(), keyCaptor.capture(), valueCaptor.capture(), timeStampCaptor.capture());

            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

            List<String> keysCaptured = keyCaptor.getAllValues();
            List<String> valuesCaptured = valueCaptor.getAllValues();

            assertTrue("Key should be updated in details table", keysCaptured.contains("is_closed"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("ABC"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("base_entity_id"));

            assertEquals(new Integer(0), Integer.valueOf(valuesCaptured.get(keysCaptured.indexOf("is_closed"))));
            assertEquals("4", valuesCaptured.get(keysCaptured.indexOf("ABC")));
            assertEquals(baseEntityId, valuesCaptured.get(keysCaptured.indexOf("base_entity_id")));

            Mockito.verify(spy, Mockito.times(4)).saveClientDetails(baseEntityCaptor.capture(), Mockito.anyMap(), timeStampCaptor.capture());
            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCaseModelForAddressField() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONObject addressField = createJsonObject("address1", "testAddress1");
            updateJsonObject(addressField, "address4", "testAddress4");

            JSONObject address = createJsonObject("addressFields", addressField);
            updateJsonObject(address, "addressType", "usual_residence");
            updateJsonObject(address, "cityVillage", "test city village");

            JSONArray addresses = new JSONArray();
            addresses.put(address);
            updateJsonObject(client, "addresses", addresses);

            JSONObject event = createEvent(baseEntityId, false);
            String eventDate = "2016-10-10T00:00:00.000+0300";
            updateJsonObject(event, "eventDate", eventDate);

            Map<String, Pair<String, String>> columns = new HashMap<String, Pair<String, String>>();
            columns.put("my_address_4", Pair.create("addresses.address4", ""));
            columns.put("my_city_village", Pair.create("addresses.cityVillage", ""));

            JSONObject bindObject = createBindObject("case7", columns);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());

            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(bindObject).when(spy).getColumnMappings("case7");

            Mockito.doReturn(1l).when(spy).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.any(String.class));

            Mockito.doNothing().when(spy).updateFTSsearch(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class));

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong());

            Boolean processed = spy.processCaseModel(event, client, createCases("case7"));
            assertTrue("Case Model should be processed", processed);

            Mockito.verify(spy).getColumnMappings("case7");

            ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
            ArgumentCaptor<String> tableNameCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(spy).executeInsertStatement(contentValuesCaptor.capture(), tableNameCaptor.capture());

            ContentValues contentValues = contentValuesCaptor.getValue();
            assertEquals("testAddress4", contentValues.getAsString("my_address_4"));
            assertEquals("test city village", contentValues.getAsString("my_city_village"));
            assertEquals(new Integer(0), contentValues.getAsInteger("is_closed"));
            assertEquals(baseEntityId, contentValues.getAsString("base_entity_id"));

            assertEquals("case7", tableNameCaptor.getValue());

            ArgumentCaptor<String> caseCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> baseEntityCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(spy).updateFTSsearch(caseCaptor.capture(), baseEntityCaptor.capture(), contentValuesCaptor.capture());
            assertEquals("case7", caseCaptor.getValue());
            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

            ArgumentCaptor<Long> timeStampCaptor = ArgumentCaptor.forClass(Long.class);
            Mockito.verify(spy, Mockito.times(4)).saveClientDetails(baseEntityCaptor.capture(), keyCaptor.capture(), valueCaptor.capture(), timeStampCaptor.capture());

            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

            List<String> keysCaptured = keyCaptor.getAllValues();
            List<String> valuesCaptured = valueCaptor.getAllValues();

            assertTrue("Key should be updated in details table", keysCaptured.contains("is_closed"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("my_address_4"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("my_city_village"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("base_entity_id"));


            assertEquals(new Integer(0), Integer.valueOf(valuesCaptured.get(keysCaptured.indexOf("is_closed"))));
            assertEquals("testAddress4", valuesCaptured.get(keysCaptured.indexOf("my_address_4")));
            assertEquals("test city village", valuesCaptured.get(keysCaptured.indexOf("my_city_village")));
            assertEquals(baseEntityId, valuesCaptured.get(keysCaptured.indexOf("base_entity_id")));


            Mockito.verify(spy, Mockito.times(4)).saveClientDetails(baseEntityCaptor.capture(), Mockito.anyMap(), timeStampCaptor.capture());
            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessCaseModelForRelationShipField() {
        try {

            String baseEntityId = getBaseEntityId();
            JSONObject client = createClient(baseEntityId);

            JSONArray houseHoldHead = createCases("123-321-123-321");
            JSONObject relationships = createJsonObject("householdHead", houseHoldHead);

            updateJsonObject(client, "relationships", relationships);

            JSONObject event = createEvent(baseEntityId, false);
            String eventDate = "2016-10-10T00:00:00.000+0300";
            updateJsonObject(event, "eventDate", eventDate);

            Map<String, Pair<String, String>> columns = new HashMap<String, Pair<String, String>>();
            columns.put("my_relational_id", Pair.create("relationships.householdHead", ""));

            JSONObject bindObject = createBindObject("case8", columns);

            ClientProcessor clientProcessor = ClientProcessor.getInstance(getContext());

            ClientProcessor spy = Mockito.spy(clientProcessor);

            Mockito.doReturn(bindObject).when(spy).getColumnMappings("case8");

            Mockito.doReturn(1l).when(spy).executeInsertStatement(Mockito.any(ContentValues.class), Mockito.any(String.class));

            Mockito.doNothing().when(spy).updateFTSsearch(Mockito.anyString(), Mockito.anyString(), Mockito.any(ContentValues.class));

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());

            Mockito.doNothing().when(spy).saveClientDetails(Mockito.anyString(), Mockito.anyMap(), Mockito.anyLong());

            Boolean processed = spy.processCaseModel(event, client, createCases("case8"));
            assertTrue("Case Model should be processed", processed);

            Mockito.verify(spy).getColumnMappings("case8");

            ArgumentCaptor<ContentValues> contentValuesCaptor = ArgumentCaptor.forClass(ContentValues.class);
            ArgumentCaptor<String> tableNameCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(spy).executeInsertStatement(contentValuesCaptor.capture(), tableNameCaptor.capture());

            ContentValues contentValues = contentValuesCaptor.getValue();
            assertEquals("123-321-123-321", contentValues.getAsString("my_relational_id"));
            assertEquals(new Integer(0), contentValues.getAsInteger("is_closed"));
            assertEquals(baseEntityId, contentValues.getAsString("base_entity_id"));

            assertEquals("case8", tableNameCaptor.getValue());

            ArgumentCaptor<String> caseCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> baseEntityCaptor = ArgumentCaptor.forClass(String.class);

            Mockito.verify(spy).updateFTSsearch(caseCaptor.capture(), baseEntityCaptor.capture(), contentValuesCaptor.capture());
            assertEquals("case8", caseCaptor.getValue());
            assertEquals(baseEntityId, baseEntityCaptor.getValue());

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

            ArgumentCaptor<Long> timeStampCaptor = ArgumentCaptor.forClass(Long.class);
            Mockito.verify(spy, Mockito.times(3)).saveClientDetails(baseEntityCaptor.capture(), keyCaptor.capture(), valueCaptor.capture(), timeStampCaptor.capture());

            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

            List<String> keysCaptured = keyCaptor.getAllValues();
            List<String> valuesCaptured = valueCaptor.getAllValues();

            assertTrue("Key should be updated in details table", keysCaptured.contains("is_closed"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("my_relational_id"));
            assertTrue("Key should be updated in details table", keysCaptured.contains("base_entity_id"));


            assertEquals(new Integer(0), Integer.valueOf(valuesCaptured.get(keysCaptured.indexOf("is_closed"))));
            assertEquals("123-321-123-321", valuesCaptured.get(keysCaptured.indexOf("my_relational_id")));
            assertEquals(baseEntityId, valuesCaptured.get(keysCaptured.indexOf("base_entity_id")));


            Mockito.verify(spy, Mockito.times(4)).saveClientDetails(baseEntityCaptor.capture(), Mockito.anyMap(), timeStampCaptor.capture());
            for (String capturedBaseEntityId : baseEntityCaptor.getAllValues()) {
                assertEquals(baseEntityId, capturedBaseEntityId);
            }

            for (Long capturedTimeStamp : timeStampCaptor.getAllValues()) {
                assertEquals(getEventDate(eventDate), capturedTimeStamp.longValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}

package util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.ei.opensrp.clientandeventmodel.Address;
import org.ei.opensrp.clientandeventmodel.Client;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.clientandeventmodel.Event;
import org.ei.opensrp.clientandeventmodel.FormEntityConstants;
import org.ei.opensrp.clientandeventmodel.Obs;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.BaseRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.UniqueIdRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.ei.opensrp.path.sync.PathClientProcessor;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.AssetHandler;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

/**
 * Created by keyman on 08/02/2017.
 */
public class JsonFormUtils {
    private static final String TAG = "JsonFormUtils";

    public static final String OPENMRS_ENTITY = "openmrs_entity";
    public static final String OPENMRS_ENTITY_ID = "openmrs_entity_id";
    public static final String OPENMRS_ENTITY_PARENT = "openmrs_entity_parent";
    public static final String OPENMRS_CHOICE_IDS = "openmrs_choice_ids";
    public static final String OPENMRS_DATA_TYPE = "openmrs_data_type";
    public static final String MOTHER_DEFAULT_DOB = "01-01-1960";

    private static final String PERSON_ATTRIBUTE = "person_attribute";
    private static final String PERSON_INDENTIFIER = "person_identifier";
    private static final String PERSON_ADDRESS = "person_address";

    private static final String CONCEPT = "concept";
    private static final String ENCOUNTER = "encounter";
    public static final String VALUE = "value";
    private static final String VALUES = "values";
    public static final String FIELDS = "fields";
    public static final String KEY = "key";
    public static final String ENTITY_ID = "entity_id";
    public static final String RELATIONAL_ID = "relational_id";
    private static final String ENCOUNTER_TYPE = "encounter_type";
    public static final String CURRENT_ZEIR_ID = "current_zeir_id";
    public static final String STEP1 = "step1";
    public static final String SECTIONS = "sections";
    public static final String READ_ONLY = "read_only";
    private static final String METADATA = "metadata";
    public static final String ZEIR_ID = "ZEIR_ID";
    public static final String M_ZEIR_ID = "M_ZEIR_ID";
    public static final String attributes = "attributes";
    public static final String encounterType = "Update Birth Registration";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static final SimpleDateFormat dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");
    //public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    //2007-03-31T04:00:00.000Z
    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();


    public static void saveForm(Context context, org.ei.opensrp.Context openSrpContext,
                                String jsonString, String providerId) {
        try {
            JSONObject form = new JSONObject(jsonString);
            if (form.getString("encounter_type").equals("Out of Catchment Service")) {
                saveOutOfAreaService(context, openSrpContext, jsonString);
            } else if (form.getString("encounter_type").equals("Birth Registration")) {
                saveBirthRegistration(context, openSrpContext, jsonString, providerId, "Child_Photo", "child", "mother");
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void saveAdverseEvent(String jsonString, String locationId, String baseEntityId,
                                        String providerId) {
        Utils.startAsyncTask(
                new SaveAdverseEventTask(jsonString, locationId, baseEntityId, providerId), null);
    }

    private static void saveBirthRegistration(Context context, org.ei.opensrp.Context openSrpContext,
                                              String jsonString, String providerId, String imageKey, String bindType,
                                              String subBindType) {
        if (context == null || openSrpContext == null || StringUtils.isBlank(providerId)
                || StringUtils.isBlank(jsonString)) {
            return;
        }

        try {
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            JSONObject jsonForm = new JSONObject(jsonString);

            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            JSONArray fields = fields(jsonForm);
            if (fields == null) {
                return;
            }

            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);

            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            // Replace values for location questions with their corresponding location IDs
            for (int i = 0; i < fields.length(); i++) {
                String key = fields.getJSONObject(i).getString("key");
                if (key.equals("Home_Facility")
                        || key.equals("Birth_Facility_Name")
                        || key.equals("Residential_Area")) {
                    try {
                        String rawValue = fields.getJSONObject(i).getString("value");
                        JSONArray valueArray = new JSONArray(rawValue);
                        if (valueArray.length() > 0) {
                            String lastLocationName = valueArray.getString(valueArray.length() - 1);
                            String lastLocationId = getOpenMrsLocationId(openSrpContext, lastLocationName);
                            fields.getJSONObject(i).put("value", lastLocationId);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                } else if (key.equals("Mother_Guardian_Date_Birth")) {
                    if (TextUtils.isEmpty(fields.getJSONObject(i).optString("value"))) {
                        fields.getJSONObject(i).put("value", MOTHER_DEFAULT_DOB);
                    }
                }
            }

            JSONObject lookUpJSONObject = getJSONObject(metadata, "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }

            Client c = JsonFormUtils.createBaseClient(fields, entityId);
            Event e = JsonFormUtils.createEvent(openSrpContext, fields, metadata, entityId, encounterType, providerId, bindType);

            Client s = null;
            Event se = null;
            if (lookUpEntityId.equals("mother") && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client ss = new Client(lookUpBaseEntityId);
                addRelationship(context, ss, c);
            } else {

                if (StringUtils.isNotBlank(subBindType)) {
                    s = JsonFormUtils.createSubformClient(context, fields, c, subBindType, null);
                }

                if (s != null && e != null) {
                    JSONObject subBindTypeJson = getJSONObject(jsonForm, subBindType);
                    if (subBindTypeJson != null) {
                        String subBindTypeEncounter = getString(subBindTypeJson, ENCOUNTER_TYPE);
                        if (StringUtils.isNotBlank(subBindTypeEncounter)) {
                            se = JsonFormUtils.createSubFormEvent(null, metadata, e, s.getBaseEntityId(), subBindTypeEncounter, providerId, subBindType);

                        }
                    }
                }
            }


            if (c != null) {
                JSONObject clientJson = new JSONObject(gson.toJson(c));

                ecUpdater.addClient(c.getBaseEntityId(), clientJson);

            }

            if (e != null) {
                JSONObject eventJson = new JSONObject(gson.toJson(e));
                ecUpdater.addEvent(e.getBaseEntityId(), eventJson);
            }

            if (s != null) {
                JSONObject clientJson = new JSONObject(gson.toJson(s));

                ecUpdater.addClient(s.getBaseEntityId(), clientJson);

            }

            if (se != null) {
                JSONObject eventJson = new JSONObject(gson.toJson(se));
                ecUpdater.addEvent(se.getBaseEntityId(), eventJson);
            }

            String zeirId = c.getIdentifier(ZEIR_ID);
            //mark zeir id as used
            VaccinatorApplication.getInstance().uniqueIdRepository().close(zeirId);

            String imageLocation = getFieldValue(fields, imageKey);
            saveImage(context, providerId, entityId, imageLocation);

            long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            PathClientProcessor.getInstance(context).processClient(ecUpdater.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public static void editsave(Context context, org.ei.opensrp.Context openSrpContext, String jsonString, String providerId, String imageKey, String bindType, String subBindType) {
        if (context == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(jsonString)) {
            return;
        }

        try {
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

            JSONObject jsonForm = new JSONObject(jsonString);

            String entityId = getString(jsonForm, ENTITY_ID);
            String relationalId = getString(jsonForm, RELATIONAL_ID);

            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            JSONArray fields = fields(jsonForm);
            if (fields == null) {
                return;
            }

            for (int i = 0; i < fields.length(); i++) {
                String key = fields.getJSONObject(i).getString("key");
                if (key.equals("Home_Facility")
                        || key.equals("Birth_Facility_Name")
                        || key.equals("Residential_Area")) {
                    try {
                        String rawValue = fields.getJSONObject(i).getString("value");
                        JSONArray valueArray = new JSONArray(rawValue);
                        if (valueArray.length() > 0) {
                            String lastLocationName = valueArray.getString(valueArray.length() - 1);
                            String lastLocationId = getOpenMrsLocationId(openSrpContext, lastLocationName);
                            fields.getJSONObject(i).put("value", lastLocationId);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                } else if (key.equals("Mother_Guardian_Date_Birth")) {
                    if (TextUtils.isEmpty(fields.getJSONObject(i).optString("value"))) {
                        fields.getJSONObject(i).put("value", MOTHER_DEFAULT_DOB);
                    }
                }
            }

            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            Client baseClient = JsonFormUtils.createBaseClient(fields, entityId);
            Event e = JsonFormUtils.createEvent(openSrpContext, fields, metadata, entityId, encounterType, providerId, bindType);

            Client subFormClient = null;

            JSONObject lookUpJSONObject = getJSONObject(metadata, "look_up");
            String lookUpEntityId = "";
            String lookUpBaseEntityId = "";
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, "entity_id");
                lookUpBaseEntityId = getString(lookUpJSONObject, "value");
            }

            if (lookUpEntityId.equals("mother") && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client ss = new Client(lookUpBaseEntityId);
                addRelationship(context, ss, baseClient);
            }

            if (StringUtils.isNotBlank(subBindType)) {
                subFormClient = JsonFormUtils.createSubformClient(context, fields, baseClient, subBindType, relationalId);
            }
            Event se = null;
            if (subFormClient != null && e != null) {
                JSONObject subBindTypeJson = getJSONObject(jsonForm, subBindType);
                if (subBindTypeJson != null) {
                    String subBindTypeEncounter = getString(subBindTypeJson, ENCOUNTER_TYPE);
                    if (StringUtils.isNotBlank(subBindTypeEncounter)) {
                        se = JsonFormUtils.createSubFormEvent(null, metadata, e, subFormClient.getBaseEntityId(), subBindTypeEncounter, providerId, subBindType);
                    }
                }
            }
            if (baseClient != null) {
                mergeAndSaveClient(context, baseClient);

            }
            if (e != null) {

                JSONObject eventJson = new JSONObject(gson.toJson(e));
                ecUpdater.addEvent(e.getBaseEntityId(), eventJson);

            }
            if (subFormClient != null) {
                mergeAndSaveClient(context, subFormClient);

            }
            if (se != null) {
                JSONObject eventJson = new JSONObject(gson.toJson(se));
                ecUpdater.addEvent(se.getBaseEntityId(), eventJson);
            }


            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            PathClientProcessor.getInstance(context).processClient(ecUpdater.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());

            String imageLocation = getFieldValue(fields, imageKey);
            if (!TextUtils.isEmpty(imageLocation)) {
                saveImage(context, providerId, entityId, imageLocation);
            }

            // Unassign current id
            if (baseClient != null) {
                String newZeirId = baseClient.getIdentifier(ZEIR_ID).replace("-", "");
                String currentZeirId = getString(jsonForm, "current_zeir_id").replace("-", "");
                if (!newZeirId.equals(currentZeirId)) {
                    //ZEIR_ID was changed
                    VaccinatorApplication.getInstance().uniqueIdRepository().open(currentZeirId);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public static void mergeAndSaveClient(Context context, Client baseClient) throws Exception {
        ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

        JSONObject updatedClientJson = new JSONObject(gson.toJson(baseClient));

        JSONObject originalClientJsonObject = ecUpdater.getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log

        //save the updated client (the one updated and generated from the form) as EditClient to keep an edit log of the client doc
        // originalClient.setType("PristineClient");
        //originalClient.setRev(null);
        //cloudantDataHandler.addClient(originalClient);

        ecUpdater.addClient(baseClient.getBaseEntityId(), mergedJson);


    }

    public static void saveOutOfAreaService(Context context, org.ei.opensrp.Context openSrpContext,
                                            String jsonString) {
        SaveOutOfAreaServiceTask saveOutOfAreaServiceTask = new SaveOutOfAreaServiceTask(context,
                openSrpContext, jsonString);

        Utils.startAsyncTask(saveOutOfAreaServiceTask, null);
    }

    /**
     * Constructs a weight object using the out of service area form
     *
     * @param openSrpContext The context to work with
     * @param outOfAreaForm  Out of area form to extract the weight form
     * @return A weight object if weight recorded in form, or {@code null} if weight not recorded
     * @throws Exception
     */
    private static Weight getWeightObject(org.ei.opensrp.Context openSrpContext, JSONObject outOfAreaForm) throws Exception {
        Weight weight = null;
        JSONArray fields = outOfAreaForm.getJSONObject("step1").getJSONArray("fields");
        String serviceDate = null;
        String zeirId = null;

        int foundFields = 0;
        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.getString("key").equals("Weight_Kg")) {
                foundFields++;
                if (StringUtils.isNotEmpty(curField.getString("value"))) {
                    weight = new Weight();
                    weight.setBaseEntityId("");
                    weight.setKg(Float.parseFloat(curField.getString("value")));
                    weight.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                    weight.setLocationId(outOfAreaForm.getJSONObject("metadata")
                            .getString("encounter_location"));
                    weight.setUpdatedAt(null);
                }
            } else if (curField.getString("key").equals("OA_Service_Date")) {
                foundFields++;
                serviceDate = curField.getString("value");
            } else if (curField.getString("key").equals("ZEIR_ID")) {
                foundFields++;
                zeirId = formatChildUniqueId(curField.getString("value"));
            }

            if (foundFields == 3) {
                break;
            }
        }

        if (weight != null && serviceDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            weight.setDate(dateFormat.parse(serviceDate));
        }

        if (weight != null && zeirId != null) {
            weight.setProgramClientId(zeirId);
        }

        return weight;
    }

    /**
     * Constructs a list of recorded vaccines from the out of area form provided
     *
     * @param openSrpContext The context to use
     * @param outOfAreaForm  Out of area form to extract recorded vaccines from
     * @return A list of recorded vaccines
     */
    private static ArrayList<Vaccine> getVaccineObjects(Context context,
                                                        org.ei.opensrp.Context openSrpContext,
                                                        JSONObject outOfAreaForm) throws Exception {
        ArrayList<Vaccine> vaccines = new ArrayList<>();
        JSONArray fields = outOfAreaForm.getJSONObject("step1").getJSONArray("fields");
        String serviceDate = null;
        String zeirId = null;

        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.has("is_vaccine_group")
                    && curField.getBoolean("is_vaccine_group")
                    && curField.getString("type").equals("check_box")) {
                JSONArray options = curField.getJSONArray("options");
                for (int j = 0; j < options.length(); j++) {
                    JSONObject curOption = options.getJSONObject(j);
                    if (curOption.getString("value").toLowerCase().equals(Boolean.TRUE.toString())) {
                        Vaccine curVaccine = new Vaccine();
                        curVaccine.setBaseEntityId("");
                        curVaccine.setName(curOption.getString("key"));
                        curVaccine.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                        curVaccine.setLocationId(outOfAreaForm.getJSONObject("metadata")
                                .getString("encounter_location"));
                        curVaccine.setCalculation(VaccinatorUtils.getVaccineCalculation(context,
                                curVaccine.getName()));
                        curVaccine.setUpdatedAt(null);

                        vaccines.add(curVaccine);
                    }
                }
            } else if (curField.getString("key").equals("OA_Service_Date")) {
                serviceDate = curField.getString("value");
            } else if (curField.getString("key").equals("ZEIR_ID")) {
                zeirId = formatChildUniqueId(curField.getString("value"));
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        for (Vaccine curVaccine : vaccines) {
            if (serviceDate != null) {
                curVaccine.setDate(dateFormat.parse(serviceDate));
            }

            if (zeirId != null) {
                curVaccine.setProgramClientId(zeirId);
            }
        }

        return vaccines;
    }

    public static void saveImage(Context context, String providerId, String entityId, String imageLocation) {
        if (StringUtils.isBlank(imageLocation)) {
            return;
        }


        File file = new File(imageLocation);

        if (!file.exists()) {
            return;
        }

        Bitmap compressedImageFile = Compressor.getDefault(context).compressToBitmap(file);
        saveStaticImageToDisk(compressedImageFile, providerId, entityId);

    }

    public static void saveStaticImageToDisk(Bitmap image, String providerId, String entityId) {
        if (image == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(entityId)) {
            return;
        }
        OutputStream os = null;
        try {

            if (entityId != null && !entityId.isEmpty()) {
                final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = new File(absoluteFileName);
                os = new FileOutputStream(outputFile);
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                if (compressFormat != null) {
                    image.compress(compressFormat, 100, os);
                } else {
                    throw new IllegalArgumentException("Failed to save static image, could not retrieve image compression format from name "
                            + absoluteFileName);
                }
                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory("profilepic");
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = (ImageRepository) org.ei.opensrp.Context.getInstance().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to save static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close static images output stream after attempting to write image");
                }
            }
        }

    }

    public static Client createBaseClient(JSONArray fields, String entityId) {

        String firstName = getFieldValue(fields, FormEntityConstants.Person.first_name);
        String middleName = getFieldValue(fields, FormEntityConstants.Person.middle_name);
        String lastName = getFieldValue(fields, FormEntityConstants.Person.last_name);
        String bd = getFieldValue(fields, FormEntityConstants.Person.birthdate);
        Date birthdate = formatDate(bd, true);
        String dd = getFieldValue(fields, FormEntityConstants.Person.deathdate);
        Date deathdate = formatDate(dd, true);
        String aproxbd = getFieldValue(fields, FormEntityConstants.Person.birthdate_estimated);
        Boolean birthdateApprox = false;
        if (!StringUtils.isEmpty(aproxbd) && NumberUtils.isNumber(aproxbd)) {
            int bde = 0;
            try {
                bde = Integer.parseInt(aproxbd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            birthdateApprox = bde > 0 ? true : false;
        }
        String aproxdd = getFieldValue(fields, FormEntityConstants.Person.deathdate_estimated);
        Boolean deathdateApprox = false;
        if (!StringUtils.isEmpty(aproxdd) && NumberUtils.isNumber(aproxdd)) {
            int dde = 0;
            try {
                dde = Integer.parseInt(aproxdd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            deathdateApprox = dde > 0 ? true : false;
        }
        String gender = getFieldValue(fields, FormEntityConstants.Person.gender);

        List<Address> addresses = new ArrayList<>(extractAddresses(fields).values());

        Client c = (Client) new Client(entityId)
                .withFirstName(firstName)
                .withMiddleName(middleName)
                .withLastName(lastName)
                .withBirthdate((birthdate != null ? birthdate : null), birthdateApprox)
                .withDeathdate(deathdate != null ? deathdate : null, deathdateApprox)
                .withGender(gender).withDateCreated(new Date());

        c.withAddresses(addresses)
                .withAttributes(extractAttributes(fields))
                .withIdentifiers(extractIdentifiers(fields));
        return c;

    }

    public static Event createEvent(org.ei.opensrp.Context openSrpContext,
                                    JSONArray fields, JSONObject metadata, String entityId,
                                    String encounterType, String providerId, String bindType) {

        String encounterDateField = getFieldValue(fields, FormEntityConstants.Encounter.encounter_date);
        String encounterLocation = null;

        Date encounterDate = new Date();
        if (StringUtils.isNotBlank(encounterDateField)) {
            Date dateTime = formatDate(encounterDateField, false);
            if (dateTime != null) {
                encounterDate = dateTime;
            }
        }
        try {
            encounterLocation = metadata.getString("encounter_location");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        Event e = (Event) new Event()
                .withBaseEntityId(entityId)//should be different for main and subform
                .withEventDate(encounterDate)
                .withEventType(encounterType)
                .withLocationId(encounterLocation)
                .withProviderId(providerId)
                .withEntityType(bindType)
                .withFormSubmissionId(generateRandomUUIDString())
                .withDateCreated(new Date());

        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            String value = getString(jsonObject, VALUE);
            if (StringUtils.isNotBlank(value)) {
                addObservation(e, jsonObject);
            }
        }

        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = getJSONObject(metadata, key);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                    if (entityVal != null) {
                        if (entityVal.equals(CONCEPT)) {
                            addToJSONObject(jsonObject, KEY, key);
                            addObservation(e, jsonObject);
                        } else if (entityVal.equals(ENCOUNTER)) {
                            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eDate = formatDate(value, false);
                                if (eDate != null) {
                                    e.setEventDate(eDate);
                                }
                            }
                        }
                    }
                }
            }
        }

        return e;

    }

    private static void addObservation(Event e, JSONObject jsonObject) {
        String value = getString(jsonObject, VALUE);
        String entity = CONCEPT;
        if (StringUtils.isNotBlank(value)) {
            List<Object> vall = new ArrayList<>();

            String formSubmissionField = getString(jsonObject, KEY);

            String dataType = getString(jsonObject, OPENMRS_DATA_TYPE);
            if (StringUtils.isBlank(dataType)) {
                dataType = "text";
            }

            if (dataType.equals("date") && StringUtils.isNotBlank(value)) {
                String newValue = convertToOpenMRSDate(value);
                if (newValue != null) {
                    value = newValue;
                }
            }

            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equals(entity)) {
                String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                String entityParentVal = getString(jsonObject, OPENMRS_ENTITY_PARENT);

                List<Object> humanReadableValues = new ArrayList<>();

                JSONArray values = getJSONArray(jsonObject, VALUES);
                if (values != null && values.length() > 0) {
                    JSONObject choices = getJSONObject(jsonObject, OPENMRS_CHOICE_IDS);
                    String chosenConcept = getString(choices, value);
                    vall.add(chosenConcept);
                    humanReadableValues.add(value);
                } else {
                    vall.add(value);
                }

                e.addObs(new Obs(CONCEPT, dataType, entityIdVal,
                        entityParentVal, vall, humanReadableValues, null, formSubmissionField));
            } else if (StringUtils.isBlank(entityVal)) {
                vall.add(value);

                e.addObs(new Obs("formsubmissionField", dataType, formSubmissionField,
                        "", vall, new ArrayList<>(), null, formSubmissionField));
            }
        }
    }


    private static Map<String, String> extractIdentifiers(JSONArray fields) {
        Map<String, String> pids = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillIdentifiers(pids, jsonObject);
        }
        return pids;
    }

    private static Map<String, Object> extractAttributes(JSONArray fields) {
        Map<String, Object> pattributes = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillAttributes(pattributes, jsonObject);
        }

        return pattributes;
    }

    private static Map<String, Address> extractAddresses(JSONArray fields) {
        Map<String, Address> paddr = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillAddressFields(jsonObject, paddr);
        }
        return paddr;
    }


    private static void fillIdentifiers(Map<String, String> pids, JSONObject jsonObject) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
            return;
        }

        String entity = PERSON_INDENTIFIER;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);

            if (entityIdVal.equals(ZEIR_ID)) {
                value = formatChildUniqueId(value);
            }

            pids.put(entityIdVal, value);
        }


    }

    /**
     * This method formats the child unique id obtained from a JSON Form to something that is useable
     *
     * @param unformattedId The unformatted unique identifier
     * @return A formatted ID or the original id if method is unable to format
     */
    private static String formatChildUniqueId(String unformattedId) {
        if (StringUtils.isNotBlank(unformattedId) && !unformattedId.contains("-")) {
            StringBuilder stringBuilder = new StringBuilder(unformattedId);
            stringBuilder.insert(unformattedId.length() - 1, '-');
            unformattedId = stringBuilder.toString();
        }

        return unformattedId;
    }


    private static void fillAttributes(Map<String, Object> pattributes, JSONObject jsonObject) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
            return;
        }

        String entity = PERSON_ATTRIBUTE;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            pattributes.put(entityIdVal, value);
        }
    }


    private static void fillAddressFields(JSONObject jsonObject, Map<String, Address> addresses) {

        if (jsonObject == null) {
            return;
        }

        try {

            String value = getString(jsonObject, VALUE);
            if (StringUtils.isBlank(value)) {
                return;
            }

            if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
                return;
            }

            String entity = PERSON_ADDRESS;
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equalsIgnoreCase(entity)) {
                String addressType = getString(jsonObject, OPENMRS_ENTITY_PARENT);
                String addressField = getString(jsonObject, OPENMRS_ENTITY_ID);

                Address ad = addresses.get(addressType);
                if (ad == null) {
                    ad = new Address(addressType, null, null, null, null, null, null, null, null);
                }

                if (addressField.equalsIgnoreCase("startDate") || addressField.equalsIgnoreCase("start_date")) {
                    ad.setStartDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("endDate") || addressField.equalsIgnoreCase("end_date")) {
                    ad.setEndDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("latitude")) {
                    ad.setLatitude(value);
                } else if (addressField.equalsIgnoreCase("longitute")) {
                    ad.setLongitude(value);
                } else if (addressField.equalsIgnoreCase("geopoint")) {
                    // example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
                    String geopoint = value;
                    if (!StringUtils.isEmpty(geopoint)) {
                        String[] g = geopoint.split(" ");
                        ad.setLatitude(g[0]);
                        ad.setLongitude(g[1]);
                        ad.setGeopoint(geopoint);
                    }
                } else if (addressField.equalsIgnoreCase("postal_code") || addressField.equalsIgnoreCase("postalCode")) {
                    ad.setPostalCode(value);
                } else if (addressField.equalsIgnoreCase("sub_town") || addressField.equalsIgnoreCase("subTown")) {
                    ad.setSubTown(value);
                } else if (addressField.equalsIgnoreCase("town")) {
                    ad.setTown(value);
                } else if (addressField.equalsIgnoreCase("sub_district") || addressField.equalsIgnoreCase("subDistrict")) {
                    ad.setSubDistrict(value);
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county")
                        || addressField.equalsIgnoreCase("county_district") || addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village")
                        || addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") || addressField.equalsIgnoreCase("stateProvince")) {
                    ad.setStateProvince(value);
                } else if (addressField.equalsIgnoreCase("country")) {
                    ad.setCountry(value);
                } else {
                    ad.addAddressField(addressField, value);
                }
                addresses.put(addressType, ad);
            }
        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }
    }

    // Helper functions

    public static JSONArray fields(JSONObject jsonForm) {
        try {

            JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    /**
     * return field values that are in sections e.g for the hia2 monthly draft form which has sections
     *
     * @param jsonForm
     * @return
     */
    public static Map<String, String> sectionFields(JSONObject jsonForm) {
        try {

            JSONObject step1 = jsonForm.has(STEP1) ? jsonForm.getJSONObject(STEP1) : null;
            if (step1 == null) {
                return null;
            }

            JSONArray sections = step1.has(SECTIONS) ? step1.getJSONArray(SECTIONS) : null;
            if (sections == null) {
                return null;
            }

            Map<String, String> result = new HashMap<>();
            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionsJSONObject = sections.getJSONObject(i);
                if (sectionsJSONObject.has(FIELDS)) {
                    JSONArray fieldsArray = sectionsJSONObject.getJSONArray(FIELDS);
                    for (int j = 0; j < fieldsArray.length(); j++) {
                        JSONObject fieldJsonObject = fieldsArray.getJSONObject(j);
                        String key = fieldJsonObject.getString(KEY);
                        String value = fieldJsonObject.getString(VALUE);
                        result.put(key, value);

                    }
                }

            }
            return result;

        } catch (JSONException e) {
            Log.e(TAG, "", e);
            return null;
        }

    }

    private static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Person person) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (person == null) {
            return null;
        }

        return value(jsonArray, person.entity(), person.entityId());
    }

    private static String getFieldValue(JSONArray jsonArray, FormEntityConstants.Encounter encounter) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (encounter == null) {
            return null;
        }

        return value(jsonArray, encounter.entity(), encounter.entityId());
    }

    private static String value(JSONArray jsonArray, String entity, String entityId) {

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            if (StringUtils.isNotBlank(getString(jsonObject, ENTITY_ID))) {
                continue;
            }
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            if (entityVal != null && entityVal.equals(entity) && entityIdVal != null && entityIdVal.equals(entityId)) {
                return getString(jsonObject, VALUE);
            }

        }
        return null;
    }

    public static String getFieldValue(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return getString(jsonObject, VALUE);
            }
        }
        return null;
    }

    private static JSONObject getJSONObject(JSONArray jsonArray, int index) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            return null;

        }
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String field) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return null;
        }

        try {
            return jsonObject.getJSONArray(field);
        } catch (JSONException e) {
            return null;

        }
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String field) {
        if (jsonObject == null || jsonObject.length() == 0) {
            return null;
        }

        try {
            return jsonObject.getJSONObject(field);
        } catch (JSONException e) {
            return null;

        }
    }

    public static String getString(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getString(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    public static Long getLong(JSONObject jsonObject, String field) {
        if (jsonObject == null) {
            return null;
        }

        try {
            return jsonObject.has(field) ? jsonObject.getLong(field) : null;
        } catch (JSONException e) {
            return null;

        }
    }

    public static Date formatDate(String dateString, boolean startOfToday) {
        try {

            if (StringUtils.isBlank(dateString)) {
                return null;
            }

            if (dateString.matches("\\d{2}-\\d{2}-\\d{4}")) {
                return dd_MM_yyyy.parse(dateString);
            } else if (dateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return DateUtil.parseDate(dateString);
            }

        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }

        return null;
    }

    public static String generateRandomUUIDString() {
        return UUID.randomUUID().toString();
    }

    public static void addToJSONObject(JSONObject jsonObject, String key, String value) {
        try {
            if (jsonObject == null) {
                return;
            }

            jsonObject.put(key, value);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
    }

    public static Client createSubformClient(Context context, JSONArray fields, Client parent, String bindType, String relationalId) throws ParseException {

        if (StringUtils.isBlank(bindType)) {
            return null;
        }

        String entityId = relationalId == null ? generateRandomUUIDString() : relationalId;
        String firstName = getSubFormFieldValue(fields, FormEntityConstants.Person.first_name, bindType);
        String gender = getSubFormFieldValue(fields, FormEntityConstants.Person.gender, bindType);
        String bb = getSubFormFieldValue(fields, FormEntityConstants.Person.birthdate, bindType);

        Map<String, String> idents = extractIdentifiers(fields, bindType);
        String parentIdentifier = parent.getIdentifier(ZEIR_ID);
        if (StringUtils.isNotBlank(parentIdentifier)) {
            String identifier = parentIdentifier.concat("_").concat(bindType);
            idents.put(M_ZEIR_ID, identifier);
        }

        String middleName = getSubFormFieldValue(fields, FormEntityConstants.Person.middle_name, bindType);
        String lastName = getSubFormFieldValue(fields, FormEntityConstants.Person.last_name, bindType);
        Date birthdate = formatDate(bb, true);
        String dd = getSubFormFieldValue(fields, FormEntityConstants.Person.deathdate, bindType);
        Date deathdate = formatDate(dd, true);
        String aproxbd = getSubFormFieldValue(fields, FormEntityConstants.Person.birthdate_estimated, bindType);
        Boolean birthdateApprox = false;
        if (!StringUtils.isEmpty(aproxbd) && NumberUtils.isNumber(aproxbd)) {
            int bde = 0;
            try {
                bde = Integer.parseInt(aproxbd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            birthdateApprox = bde > 0 ? true : false;
        }
        String aproxdd = getSubFormFieldValue(fields, FormEntityConstants.Person.deathdate_estimated, bindType);
        Boolean deathdateApprox = false;
        if (!StringUtils.isEmpty(aproxdd) && NumberUtils.isNumber(aproxdd)) {
            int dde = 0;
            try {
                dde = Integer.parseInt(aproxdd);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
            deathdateApprox = dde > 0 ? true : false;
        }

        List<Address> addresses = new ArrayList<>(extractAddresses(fields, bindType).values());

        Client c = (Client) new Client(entityId)
                .withFirstName(firstName)
                .withMiddleName(middleName)
                .withLastName(lastName)
                .withBirthdate(birthdate, birthdateApprox)
                .withDeathdate(deathdate, deathdateApprox)
                .withGender(gender).withDateCreated(new Date());

        c.withAddresses(addresses)
                .withAttributes(extractAttributes(fields, bindType))
                .withIdentifiers(idents);

        if (addresses.isEmpty()) {
            c.withAddresses(parent.getAddresses());
        }

        addRelationship(context, c, parent);

        return c;
    }

    public static JSONObject merge(JSONObject original, JSONObject updated) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(original, getNames(original));
            for (String key : getNames(updated)) {
                mergedJSON.put(key, updated.get(key));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return mergedJSON;
    }

    private static String[] getNames(JSONObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator i = jo.keys();
        String[] names = new String[length];
        int j = 0;
        while (i.hasNext()) {
            names[j] = (String) i.next();
            j += 1;
        }
        return names;
    }

    public static Event createSubFormEvent(JSONArray fields, JSONObject metadata, Event parent, String entityId, String encounterType, String providerId, String bindType) {


        Event e = (Event) new Event()
                .withBaseEntityId(entityId)//should be different for main and subform
                .withEventDate(parent.getEventDate())
                .withEventType(encounterType)
                .withLocationId(parent.getLocationId())
                .withProviderId(providerId)
                .withEntityType(bindType)
                .withFormSubmissionId(generateRandomUUIDString())
                .withDateCreated(new Date());

        if (fields != null && fields.length() != 0)
            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(e, jsonObject);
                }
            }

        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = getJSONObject(metadata, key);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                    if (entityVal != null) {
                        if (entityVal.equals(CONCEPT)) {
                            addToJSONObject(jsonObject, KEY, key);
                            addObservation(e, jsonObject);
                        } else if (entityVal.equals(ENCOUNTER)) {
                            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eDate = formatDate(value, false);
                                if (eDate != null) {
                                    e.setEventDate(eDate);
                                }
                            }
                        }
                    }
                }
            }
        }

        return e;

    }


    private static Map<String, String> extractIdentifiers(JSONArray fields, String bindType) {
        Map<String, String> pids = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormIdentifiers(pids, jsonObject, bindType);
        }
        return pids;
    }

    private static Map<String, Object> extractAttributes(JSONArray fields, String bindType) {
        Map<String, Object> pattributes = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormAttributes(pattributes, jsonObject, bindType);
        }
        return pattributes;
    }

    private static Map<String, Address> extractAddresses(JSONArray fields, String bindType) {
        Map<String, Address> paddr = new HashMap<>();
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            fillSubFormAddressFields(jsonObject, paddr, bindType);
        }
        return paddr;
    }


    private static void addRelationship(Context context, Client parent, Client child) {
        try {
            String relationships = AssetHandler.readFileFromAssetsFolder(FormUtils.ecClientRelationships, context);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field") && getString(rObject, "field").equals(ENTITY_ID)) {
                    child.addRelationship(rObject.getString("client_relationship"), parent.getBaseEntityId());
                } else {
                    //TODO how to add other kind of relationships
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private static String getSubFormFieldValue(JSONArray jsonArray, FormEntityConstants.Person person, String bindType) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        if (person == null) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String bind = getString(jsonObject, ENTITY_ID);
            if (bind == null || !bind.equals(bindType)) {
                continue;
            }
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            if (entityVal != null && entityVal.equals(person.entity()) && entityIdVal != null && entityIdVal.equals(person.name())) {
                return getString(jsonObject, VALUE);
            }

        }
        return null;
    }

    private static void fillSubFormIdentifiers(Map<String, String> pids, JSONObject jsonObject, String bindType) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        String bind = getString(jsonObject, ENTITY_ID);
        if (bind == null || !bind.equals(bindType)) {
            return;
        }

        String entity = PERSON_INDENTIFIER;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);

            if (entityIdVal.equals(ZEIR_ID) && StringUtils.isNotBlank(value) && !value.contains("-")) {
                StringBuilder stringBuilder = new StringBuilder(value);
                stringBuilder.insert(value.length() - 1, '-');
                value = stringBuilder.toString();
            }

            pids.put(entityIdVal, value);
        }
    }

    private static void fillSubFormAttributes(Map<String, Object> pattributes, JSONObject jsonObject, String bindType) {

        String value = getString(jsonObject, VALUE);
        if (StringUtils.isBlank(value)) {
            return;
        }

        String bind = getString(jsonObject, ENTITY_ID);
        if (bind == null || !bind.equals(bindType)) {
            return;
        }

        String entity = PERSON_ATTRIBUTE;
        String entityVal = getString(jsonObject, OPENMRS_ENTITY);

        if (entityVal != null && entityVal.equals(entity)) {
            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
            pattributes.put(entityIdVal, value);
        }
    }

    private static void fillSubFormAddressFields(JSONObject jsonObject, Map<String, Address> addresses, String bindType) {

        if (jsonObject == null) {
            return;
        }

        try {
            String value = getString(jsonObject, VALUE);
            if (StringUtils.isBlank(value)) {
                return;
            }

            String bind = getString(jsonObject, ENTITY_ID);
            if (bind == null || !bind.equals(bindType)) {
                return;
            }

            String entity = PERSON_ADDRESS;
            String entityVal = getString(jsonObject, OPENMRS_ENTITY);

            if (entityVal != null && entityVal.equalsIgnoreCase(entity)) {
                String addressType = getString(jsonObject, OPENMRS_ENTITY_PARENT);
                String addressField = getString(jsonObject, OPENMRS_ENTITY_ID);

                Address ad = addresses.get(addressType);
                if (ad == null) {
                    ad = new Address(addressType, null, null, null, null, null, null, null, null);
                }

                if (addressField.equalsIgnoreCase("startDate") || addressField.equalsIgnoreCase("start_date")) {
                    ad.setStartDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("endDate") || addressField.equalsIgnoreCase("end_date")) {
                    ad.setEndDate(DateUtil.parseDate(value));
                } else if (addressField.equalsIgnoreCase("latitude")) {
                    ad.setLatitude(value);
                } else if (addressField.equalsIgnoreCase("longitute")) {
                    ad.setLongitude(value);
                } else if (addressField.equalsIgnoreCase("geopoint")) {
                    // example geopoint 34.044494 -84.695704 4 76 = lat lon alt prec
                    String geopoint = value;
                    if (!StringUtils.isEmpty(geopoint)) {
                        String[] g = geopoint.split(" ");
                        ad.setLatitude(g[0]);
                        ad.setLongitude(g[1]);
                        ad.setGeopoint(geopoint);
                    }
                } else if (addressField.equalsIgnoreCase("postal_code") || addressField.equalsIgnoreCase("postalCode")) {
                    ad.setPostalCode(value);
                } else if (addressField.equalsIgnoreCase("sub_town") || addressField.equalsIgnoreCase("subTown")) {
                    ad.setSubTown(value);
                } else if (addressField.equalsIgnoreCase("town")) {
                    ad.setTown(value);
                } else if (addressField.equalsIgnoreCase("sub_district") || addressField.equalsIgnoreCase("subDistrict")) {
                    ad.setSubDistrict(value);
                } else if (addressField.equalsIgnoreCase("district") || addressField.equalsIgnoreCase("county")
                        || addressField.equalsIgnoreCase("county_district") || addressField.equalsIgnoreCase("countyDistrict")) {
                    ad.setCountyDistrict(value);
                } else if (addressField.equalsIgnoreCase("city") || addressField.equalsIgnoreCase("village")
                        || addressField.equalsIgnoreCase("cityVillage") || addressField.equalsIgnoreCase("city_village")) {
                    ad.setCityVillage(value);
                } else if (addressField.equalsIgnoreCase("state") || addressField.equalsIgnoreCase("state_province") || addressField.equalsIgnoreCase("stateProvince")) {
                    ad.setStateProvince(value);
                } else if (addressField.equalsIgnoreCase("country")) {
                    ad.setCountry(value);
                } else {
                    ad.addAddressField(addressField, value);
                }
                addresses.put(addressType, ad);
            }
        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }
    }

    public static JSONArray generateLocationHierarchyTree(org.ei.opensrp.Context context, boolean withOtherOption, ArrayList<String> allowedLevels) {
        JSONArray array = new JSONArray();
        try {
            JSONObject locationData = new JSONObject(context.anmLocationController().get());
            if (locationData.has("locationsHierarchy")
                    && locationData.getJSONObject("locationsHierarchy").has("map")) {
                JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    getFormJsonData(array, map.getJSONObject(curKey), allowedLevels);
                }
            }

            array = sortTreeViewQuestionOptions(array);
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        if (withOtherOption) {
            try {
                JSONObject other = new JSONObject();
                other.put("name", "Other");
                other.put("key", "Other");
                other.put("level", "");
                array.put(other);
            } catch (JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return array;
    }

    public static JSONArray generateDefaultLocationHierarchy(org.ei.opensrp.Context context, ArrayList<String> allowedLevels) {
        try {
            String defaultLocationUuid = context.allSharedPreferences()
                    .fetchDefaultLocalityId(context.allSharedPreferences().fetchRegisteredANM());
            JSONObject locationData = new JSONObject(context.anmLocationController().get());
            if (locationData.has("locationsHierarchy")
                    && locationData.getJSONObject("locationsHierarchy").has("map")) {
                JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    JSONArray curResult = getDefaultLocationHierarchy(defaultLocationUuid, map.getJSONObject(curKey), new JSONArray(), allowedLevels);
                    if (curResult != null) {
                        return curResult;
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private static JSONArray getDefaultLocationHierarchy(String defaultLocationUuid, JSONObject openMrsLocationData, JSONArray parents, ArrayList<String> allowedLevels) throws JSONException {
        if (allowedLevels.contains(openMrsLocationData.getJSONObject("node").getJSONArray("tags").getString(0))) {
            parents.put(openMrsLocationData.getJSONObject("node").getString("name"));
        }

        if (openMrsLocationData.getJSONObject("node").getString("locationId").equals(defaultLocationUuid)) {
            return parents;
        }

        if (openMrsLocationData.has("children")) {
            Iterator<String> childIterator = openMrsLocationData.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                JSONArray curResult = getDefaultLocationHierarchy(defaultLocationUuid, openMrsLocationData.getJSONObject("children").getJSONObject(curChildKey), new JSONArray(parents.toString()), allowedLevels);
                if (curResult != null) return curResult;
            }
        }

        return null;
    }

    private static void getFormJsonData(JSONArray allLocationData, JSONObject openMrsLocationData, ArrayList<String> allowedLevels) throws JSONException {
        JSONObject jsonFormObject = new JSONObject();
        String name = openMrsLocationData.getJSONObject("node").getString("name");
        jsonFormObject.put("name", getOpenMrsReadableName(name));
        jsonFormObject.put("key", name);
        String level = "";
        try {
            level = openMrsLocationData.getJSONObject("node").getJSONArray("tags").getString(0);
        } catch (JSONException e) {
        }
        jsonFormObject.put("level", "");
        JSONArray children = new JSONArray();
        if (openMrsLocationData.has("children")) {
            Iterator<String> childIterator = openMrsLocationData.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                getFormJsonData(children, openMrsLocationData.getJSONObject("children").getJSONObject(curChildKey), allowedLevels);
            }
            if (allowedLevels.contains(level)) {
                jsonFormObject.put("nodes", children);
            } else {
                for (int i = 0; i < children.length(); i++) {
                    allLocationData.put(children.getJSONObject(i));
                }
            }
        }
        if (allowedLevels.contains(level)) {
            allLocationData.put(jsonFormObject);
        }
    }

    public static String getOpenMrsReadableName(String name) {
        String readableName = new String(name);

        Pattern prefixPattern = Pattern.compile("^[a-z]{2} (.*)$");
        Matcher prefixMatcher = prefixPattern.matcher(readableName);
        if (prefixMatcher.find()) {
            readableName = prefixMatcher.group(1);
        }

        if (readableName.contains(":")) {
            String[] splitName = readableName.split(":");
            readableName = splitName[splitName.length - 1].trim();
        }

        return readableName;
    }

    public static void addChildRegLocHierarchyQuestions(JSONObject form,
                                                        org.ei.opensrp.Context context) {
        try {
            JSONArray questions = form.getJSONObject("step1").getJSONArray("fields");
            ArrayList<String> allLevels = new ArrayList<>();
            allLevels.add("Country");
            allLevels.add("Province");
            allLevels.add("District");
            allLevels.add("Health Facility");
            allLevels.add("Zone");
            allLevels.add("Residential Area");

            ArrayList<String> healthFacilities = new ArrayList<>();
            healthFacilities.add("Country");
            healthFacilities.add("Province");
            healthFacilities.add("District");
            healthFacilities.add("Health Facility");

            JSONArray defaultLocation = generateDefaultLocationHierarchy(context, allLevels);
            JSONArray defaultFacility = generateDefaultLocationHierarchy(context, healthFacilities);
            JSONArray upToFacilities = generateLocationHierarchyTree(context, false, healthFacilities);
            JSONArray upToFacilitiesWithOther = generateLocationHierarchyTree(context, true, healthFacilities);
            JSONArray entireTree = generateLocationHierarchyTree(context, true, allLevels);

            for (int i = 0; i < questions.length(); i++) {
                if (questions.getJSONObject(i).getString("key").equals("Home_Facility")) {
                    questions.getJSONObject(i).put("tree", new JSONArray(upToFacilities.toString()));
                    if (defaultFacility != null) {
                        questions.getJSONObject(i).put("default", defaultFacility.toString());
                    }
                } else if (questions.getJSONObject(i).getString("key").equals("Birth_Facility_Name")) {
                    questions.getJSONObject(i).put("tree", new JSONArray(upToFacilitiesWithOther.toString()));
                    if (defaultFacility != null) {
                        questions.getJSONObject(i).put("default", defaultFacility.toString());
                    }
                } else if (questions.getJSONObject(i).getString("key").equals("Residential_Area")) {
                    questions.getJSONObject(i).put("tree", new JSONArray(entireTree.toString()));
                    if (defaultLocation != null) {
                        questions.getJSONObject(i).put("default", defaultLocation.toString());
                    }
                }
            }
        } catch (JSONException e) {
            //Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void addAddAvailableVaccines(Context context, JSONObject form) {
        String supportedVaccinesString = VaccinatorUtils.getSupportedVaccines(context);
        if (StringUtils.isNotEmpty(supportedVaccinesString) && form != null) {
            // For each of the vaccine groups, create a checkbox question
            try {
                JSONArray questionList = form.getJSONObject("step1").getJSONArray("fields");
                JSONObject vaccinationLabel = new JSONObject();
                vaccinationLabel.put("key", "Vaccines_Provided_Label");
                vaccinationLabel.put("type", "label");
                vaccinationLabel.put("text", "Which vaccinations were provided?");
                vaccinationLabel.put("openmrs_entity_parent", "-");
                vaccinationLabel.put("openmrs_entity", "-");
                vaccinationLabel.put("openmrs_entity_id", "-");
                questionList.put(vaccinationLabel);
                JSONArray supportedVaccines = new JSONArray(supportedVaccinesString);

                HashMap<String, ArrayList<JSONObject>> vaccineTypeConstraints = new HashMap<>();
                for (int i = 0; i < supportedVaccines.length(); i++) {
                    JSONObject curVaccineGroup = supportedVaccines.getJSONObject(i);
                    JSONArray vaccines = curVaccineGroup.getJSONArray("vaccines");
                    for (int j = 0; j < vaccines.length(); j++) {
                        JSONObject curVaccine = vaccines.getJSONObject(j);
                        if (!vaccineTypeConstraints.containsKey(curVaccine.getString("type"))) {
                            vaccineTypeConstraints.put(curVaccine.getString("type"),
                                    new ArrayList<JSONObject>());
                        }
                        ArrayList<String> vaccineNamesDefined = new ArrayList<>();
                        if (curVaccine.has("vaccine_separator")) {
                            String unsplitNames = curVaccine.getString("name");
                            String separator = curVaccine.getString("vaccine_separator");
                            String[] splitValues = unsplitNames.split(separator);
                            for (int k = 0; k < splitValues.length; k++) {
                                vaccineNamesDefined.add(splitValues[k]);
                            }
                        } else {
                            vaccineNamesDefined.add(curVaccine.getString("name"));
                        }

                        for (String curVaccineName : vaccineNamesDefined) {
                            JSONObject curConstraint = new JSONObject();
                            curConstraint.put("vaccine", curVaccineName);
                            curConstraint.put("type", "array");
                            curConstraint.put("ex",
                                    "notEqualTo(step1:" + curVaccineGroup.getString("id") + ", \"[\"" + curVaccineName + "\"]\")");
                            curConstraint.put("err", "Cannot be given with the other " + curVaccine.getString("type") + " dose");
                            vaccineTypeConstraints.get(curVaccine.getString("type")).add(curConstraint);
                        }
                    }
                }

                for (int i = 0; i < supportedVaccines.length(); i++) {
                    JSONObject curVaccineGroup = supportedVaccines.getJSONObject(i);
                    JSONObject curQuestion = new JSONObject();
                    curQuestion.put("key", curVaccineGroup.getString("id"));
                    curQuestion.put("type", "check_box");
                    curQuestion.put("is_vaccine_group", true);
                    curQuestion.put("label", curVaccineGroup.getString("name"));
                    curQuestion.put("openmrs_entity_parent", "-");
                    curQuestion.put("openmrs_entity", "-");
                    curQuestion.put("openmrs_entity_id", "-");

                    JSONArray vaccines = curVaccineGroup.getJSONArray("vaccines");
                    JSONArray options = new JSONArray();
                    for (int j = 0; j < vaccines.length(); j++) {
                        ArrayList<String> definedVaccineNames = new ArrayList<>();
                        if (vaccines.getJSONObject(j).has("vaccine_separator")) {
                            String rawNames = vaccines.getJSONObject(j).getString("name");
                            String separator = vaccines.getJSONObject(j).getString("vaccine_separator");
                            String[] split = rawNames.split(separator);
                            for (int k = 0; k < split.length; k++) {
                                definedVaccineNames.add(split[k]);
                            }
                        } else {
                            definedVaccineNames.add(vaccines.getJSONObject(j).getString("name"));
                        }

                        for (String curVaccineName : definedVaccineNames) {
                            JSONObject curVaccines = new JSONObject();
                            curVaccines.put("key", curVaccineName);
                            curVaccines.put("text", curVaccineName);
                            curVaccines.put("value", "false");
                            JSONArray constraints = new JSONArray();

                            // Add the constraints
                            if (vaccineTypeConstraints.containsKey(vaccines.getJSONObject(j).getString("type"))) {
                                for (JSONObject curConstraint : vaccineTypeConstraints.get(vaccines.getJSONObject(j).getString("type"))) {
                                    if (!curConstraint.getString("vaccine")
                                            .equals(curVaccineName)) {
                                        JSONObject constraintClone = new JSONObject(curConstraint.toString());
                                        constraintClone.remove("vaccine");
                                        constraints.put(constraintClone);
                                    }
                                }
                            }

                            if (constraints.length() > 0) {
                                curVaccines.put("constraints", constraints);
                            }

                            options.put(curVaccines);
                        }
                    }

                    curQuestion.put("options", options);
                    questionList.put(curQuestion);
                }
            } catch (JSONException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

    public static void createWeightEvent(Context context, Weight weight, String eventType, String entityType, JSONArray fields) {
        try {
            PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();

            Event event = (Event) new Event()
                    .withBaseEntityId(weight.getBaseEntityId())
                    .withIdentifiers(weight.getIdentifiers())
                    .withEventDate(weight.getDate())
                    .withEventType(eventType)
                    .withLocationId(weight.getLocationId())
                    .withProviderId(weight.getAnmId())
                    .withEntityType(entityType)
                    .withFormSubmissionId(weight.getFormSubmissionId() == null ? generateRandomUUIDString() : weight.getFormSubmissionId())
                    .withDateCreated(new Date());


            if (fields != null && fields.length() != 0)
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject jsonObject = getJSONObject(fields, i);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        addObservation(event, jsonObject);
                    }
                }


            if (event != null) {

                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

                //check if an event already exists and update instead
                if (weight.getEventId() != null) {
                    JSONObject existingEvent = db.getEventsByEventId(weight.getEventId());
                    eventJson = merge(existingEvent, eventJson);
                }

                //merge if event exists
                db.addEvent(event.getBaseEntityId(), eventJson);

            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static void createVaccineEvent(Context context, Vaccine vaccine, String eventType, String entityType, JSONArray fields) {
        try {
            PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();

            Event event = (Event) new Event()
                    .withBaseEntityId(vaccine.getBaseEntityId())
                    .withIdentifiers(vaccine.getIdentifiers())
                    .withEventDate(vaccine.getDate())
                    .withEventType(eventType)
                    .withLocationId(vaccine.getLocationId())
                    .withProviderId(vaccine.getAnmId())
                    .withEntityType(entityType)
                    .withFormSubmissionId(vaccine.getFormSubmissionId() == null ? generateRandomUUIDString() : vaccine.getFormSubmissionId())
                    .withDateCreated(new Date());

            if (fields != null && fields.length() != 0)
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject jsonObject = getJSONObject(fields, i);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        addObservation(event, jsonObject);
                    }
                }


            if (event != null) {

                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

                //check if an event already exists and update instead
                if (vaccine.getEventId() != null) {
                    JSONObject existingEvent = db.getEventsByEventId(vaccine.getEventId());
                    eventJson = merge(existingEvent, eventJson);
                }

                //merge if event exists
                db.addEvent(event.getBaseEntityId(), eventJson);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static void createServiceEvent(Context context, ServiceRecord serviceRecord, String eventType, String entityType, JSONArray fields) {
        try {
            PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();

            Event event = (Event) new Event()
                    .withBaseEntityId(serviceRecord.getBaseEntityId())
                    .withIdentifiers(serviceRecord.getIdentifiers())
                    .withEventDate(serviceRecord.getDate())
                    .withEventType(eventType)
                    .withLocationId(serviceRecord.getLocationId())
                    .withProviderId(serviceRecord.getAnmId())
                    .withEntityType(entityType)
                    .withFormSubmissionId(serviceRecord.getFormSubmissionId() == null ? generateRandomUUIDString() : serviceRecord.getFormSubmissionId())
                    .withDateCreated(new Date());

            if (fields != null && fields.length() != 0)
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject jsonObject = getJSONObject(fields, i);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        addObservation(event, jsonObject);
                    }
                }


            if (event != null) {

                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

                //check if an event already exists and update instead
                if (serviceRecord.getEventId() != null) {
                    JSONObject existingEvent = db.getEventsByEventId(serviceRecord.getEventId());
                    eventJson = merge(existingEvent, eventJson);
                }

                //merge if event exists
                db.addEvent(event.getBaseEntityId(), eventJson);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    public static String getOpenMrsLocationId(org.ei.opensrp.Context context,
                                              String locationName) throws JSONException {
        String response = locationName;

        if (locationName != null) {
            JSONObject locationData = new JSONObject(context.anmLocationController().get());
            if (locationData.has("locationsHierarchy")
                    && locationData.getJSONObject("locationsHierarchy").has("map")) {
                JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    String curResult = getOpenMrsLocationId(locationName, map.getJSONObject(curKey));

                    if (curResult != null) {
                        response = curResult;
                        break;
                    }
                }
            }
        }

        return response;
    }

    private static String getOpenMrsLocationId(String locationName, JSONObject openMrsLocations)
            throws JSONException {
        String name = openMrsLocations.getJSONObject("node").getString("name");

        if (locationName.equals(name)) {
            return openMrsLocations.getJSONObject("node").getString("locationId");
        }

        if (openMrsLocations.has("children")) {
            Iterator<String> childIterator = openMrsLocations.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                String curResult = getOpenMrsLocationId(locationName,
                        openMrsLocations.getJSONObject("children").getJSONObject(curChildKey));
                if (curResult != null) {
                    return curResult;
                }
            }
        }

        return null;
    }

    /**
     * This method returns the name hierarchy of a location given it's id
     *
     * @param context
     * @param locationId The ID for the location we want the hierarchy for
     * @return The name hierarchy (starting with the top-most parent) for the location or {@code NULL}
     * if location id is not found
     */
    public static JSONArray getOpenMrsLocationHierarchy(org.ei.opensrp.Context context,
                                                        String locationId) {
        JSONArray response = null;

        try {
            if (locationId != null) {
                JSONObject locationData = new JSONObject(context.anmLocationController().get());
                Log.d(TAG, "Location data is " + locationData);
                if (locationData.has("locationsHierarchy")
                        && locationData.getJSONObject("locationsHierarchy").has("map")) {
                    JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                    Iterator<String> keys = map.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();
                        JSONArray curResult = getOpenMrsLocationHierarchy(locationId, map.getJSONObject(curKey), new JSONArray());

                        if (curResult != null) {
                            response = curResult;
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "locationData doesn't have locationHierarchy");
                }
            } else {
                Log.e(TAG, "Location id is null");
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return response;
    }

    private static JSONArray getOpenMrsLocationHierarchy(String locationId,
                                                         JSONObject openMrsLocation,
                                                         JSONArray parents) throws JSONException {
        JSONArray hierarchy = new JSONArray(parents.toString());
        hierarchy.put(openMrsLocation.getJSONObject("node").getString("name"));
        String id = openMrsLocation.getJSONObject("node").getString("locationId");
        Log.d(TAG, "Current location id is " + id);
        if (locationId.equals(id)) {
            return hierarchy;
        }

        if (openMrsLocation.has("children")) {
            Iterator<String> childIterator = openMrsLocation.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                JSONArray curResult = getOpenMrsLocationHierarchy(locationId,
                        openMrsLocation.getJSONObject("children").getJSONObject(curChildKey),
                        hierarchy);
                if (curResult != null) return curResult;
            }
        } else {
            Log.d(TAG, id + " does not have children");
        }

        return null;
    }

    public static String getOpenMrsLocationName(org.ei.opensrp.Context context,
                                                String locationId) {
        String response = locationId;
        try {
            if (locationId != null) {
                JSONObject locationData = new JSONObject(context.anmLocationController().get());
                Log.d(TAG, "Location data is " + locationData);
                if (locationData.has("locationsHierarchy")
                        && locationData.getJSONObject("locationsHierarchy").has("map")) {
                    JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                    Iterator<String> keys = map.keys();
                    while (keys.hasNext()) {
                        String curKey = keys.next();
                        String curResult = getOpenMrsLocationName(locationId, map.getJSONObject(curKey));

                        if (curResult != null) {
                            response = curResult;
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "locationData doesn't have locationHierarchy");
                }
            } else {
                Log.e(TAG, "Location id is null");
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return response;
    }

    private static String getOpenMrsLocationName(String locationId, JSONObject openMrsLocations)
            throws JSONException {
        String id = openMrsLocations.getJSONObject("node").getString("locationId");
        Log.d(TAG, "Current location id is " + id);
        if (locationId.equals(id)) {
            return openMrsLocations.getJSONObject("node").getString("name");
        }

        if (openMrsLocations.has("children")) {
            Iterator<String> childIterator = openMrsLocations.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                String curResult = getOpenMrsLocationName(locationId,
                        openMrsLocations.getJSONObject("children").getJSONObject(curChildKey));
                if (curResult != null) {
                    return curResult;
                }
            }
        } else {
            Log.d(TAG, id + " does not have children");
        }

        return null;
    }


    public static void saveReportDeceased(Context context, org.ei.opensrp.Context openSrpContext,
                                          String jsonString, String providerId, String locationId, String entityId) {

        try {
            PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();

            JSONObject jsonForm = new JSONObject(jsonString);

            JSONArray fields = fields(jsonForm);
            if (fields == null) {
                return;
            }

            String bindType = "child";
            String encounterDateField = getFieldValue(fields, "Date_of_Death");

            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            Date encounterDate = new Date();
            if (StringUtils.isNotBlank(encounterDateField)) {
                Date dateTime = formatDate(encounterDateField, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId)//should be different for main and subform
                    .withEventDate(encounterDate)
                    .withEventType(encounterType)
                    .withLocationId(locationId)
                    .withProviderId(providerId)
                    .withEntityType(bindType)
                    .withFormSubmissionId(generateRandomUUIDString())
                    .withDateCreated(new Date());

            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(event, jsonObject);
                }
            }

            if (metadata != null) {
                Iterator<?> keys = metadata.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject jsonObject = getJSONObject(metadata, key);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                        if (entityVal != null) {
                            if (entityVal.equals(CONCEPT)) {
                                addToJSONObject(jsonObject, KEY, key);
                                addObservation(event, jsonObject);
                            } else if (entityVal.equals(ENCOUNTER)) {
                                String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                                if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                    Date eDate = formatDate(value, false);
                                    if (eDate != null) {
                                        event.setEventDate(eDate);
                                    }
                                }
                            }
                        }
                    }
                }
            }


            if (event != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                db.addEvent(event.getBaseEntityId(), eventJson);

            }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    private static String convertToOpenMRSDate(String value) {
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) { // already in openmrs date format
            return value;
        }

        try {
            Date date = formatDate(value, false);
            if (date != null) {
                return DateUtil.yyyyMMdd.format(date);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    private static class SaveOutOfAreaServiceTask extends AsyncTask<Void, Void, Void> {

        private final Context context;
        private final org.ei.opensrp.Context openSrpContext;
        private final String formString;

        public SaveOutOfAreaServiceTask(Context context, org.ei.opensrp.Context openSrpContext, String formString) {
            this.context = context;
            this.openSrpContext = openSrpContext;
            this.formString = formString;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONObject form = new JSONObject(formString);

                // Create a weight object if weight was recorded
                Weight weight = getWeightObject(openSrpContext, form);
                if (weight != null) {
                    WeightRepository weightRepository = VaccinatorApplication.getInstance().weightRepository();
                    weightRepository.add(weight);
                }

                // Create a vaccine object for all recorded vaccines
                ArrayList<Vaccine> vaccines = getVaccineObjects(context, openSrpContext, form);
                if (vaccines.size() > 0) {
                    VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                    for (Vaccine curVaccine : vaccines) {
                        vaccineRepository.add(curVaccine);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            return null;
        }
    }

    private static class SaveAdverseEventTask extends AsyncTask<Void, Void, Void> {
        private final String jsonString;
        private final String locationId;
        private final String baseEntityId;
        private final String providerId;

        public SaveAdverseEventTask(String jsonString, String locationId, String baseEntityId,
                                    String providerId) {
            this.jsonString = jsonString;
            this.locationId = locationId;
            this.baseEntityId = baseEntityId;
            this.providerId = providerId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                PathRepository db = (PathRepository) VaccinatorApplication.getInstance().getRepository();

                JSONObject jsonForm = new JSONObject(jsonString);

                JSONArray fields = fields(jsonForm);
                if (fields == null) {
                    return null;
                }

                String bindType = "child";
                String encounterDateField = getFieldValue(fields, "Date_Reaction");

                String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
                JSONObject metadata = getJSONObject(jsonForm, METADATA);

                Date encounterDate = new Date();
                if (StringUtils.isNotBlank(encounterDateField)) {
                    Date dateTime = formatDate(encounterDateField, false);
                    if (dateTime != null) {
                        encounterDate = dateTime;
                    }
                }

                Event event = (Event) new Event()
                        .withBaseEntityId(baseEntityId)//should be different for main and subform
                        .withEventDate(encounterDate)
                        .withEventType(encounterType)
                        .withLocationId(locationId)
                        .withProviderId(providerId)
                        .withEntityType(bindType)
                        .withFormSubmissionId(generateRandomUUIDString())
                        .withDateCreated(new Date());

                for (int i = 0; i < fields.length(); i++) {
                    JSONObject jsonObject = getJSONObject(fields, i);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        addObservation(event, jsonObject);
                    }
                }

                if (metadata != null) {
                    Iterator<?> keys = metadata.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        JSONObject jsonObject = getJSONObject(metadata, key);
                        String value = getString(jsonObject, VALUE);
                        if (StringUtils.isNotBlank(value)) {
                            String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                            if (entityVal != null) {
                                if (entityVal.equals(CONCEPT)) {
                                    addToJSONObject(jsonObject, KEY, key);
                                    addObservation(event, jsonObject);
                                } else if (entityVal.equals(ENCOUNTER)) {
                                    String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                                    if (entityIdVal.equals(
                                            FormEntityConstants.Encounter.encounter_date.name())) {
                                        Date eDate = formatDate(value, false);
                                        if (eDate != null) {
                                            event.setEventDate(eDate);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                if (event != null) {
                    JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                    db.addEvent(event.getBaseEntityId(), eventJson);

                }

            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return null;
        }
    }


    /**
     * Starts an instance of JsonFormActivity with the provided form details
     *
     * @param context                     The activity form is being launched from
     * @param openSrpContext              Current OpenSRP context
     * @param jsonFormActivityRequestCode The request code to be used to launch {@link PathJsonFormActivity}
     * @param formName                    The name of the form to launch
     * @param entityId                    The unique entity id for the form (e.g child's ZEIR id)
     * @param metaData                    The form's meta data
     * @param currentLocationId           OpenMRS id for the current device's location
     * @throws Exception
     */
    public static void startForm(Activity context, org.ei.opensrp.Context openSrpContext,
                                 int jsonFormActivityRequestCode,
                                 String formName, String entityId, String metaData,
                                 String currentLocationId) throws Exception {
        Intent intent = new Intent(context, PathJsonFormActivity.class);

        JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
        if (form != null) {
            form.getJSONObject("metadata").put("encounter_location", currentLocationId);

            if (formName.equals("child_enrollment")) {
                if (StringUtils.isBlank(entityId)) {
                    UniqueIdRepository uniqueIdRepo = VaccinatorApplication.getInstance().uniqueIdRepository();
                    entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                    if (entityId.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.no_openmrs_id), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                }

                JsonFormUtils.addChildRegLocHierarchyQuestions(form, openSrpContext);

                // Inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY)
                            .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                        continue;
                    }
                }
            } else if (formName.equals("out_of_catchment_service")) {
                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                } else {
                    JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        if (fields.getJSONObject(i).getString("key").equals("ZEIR_ID")) {
                            fields.getJSONObject(i).put(READ_ONLY, false);
                            break;
                        }
                    }
                }

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY)
                            .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                        continue;
                    }
                }

                JsonFormUtils.addAddAvailableVaccines(context, form);
            } else {
                Log.w(TAG, "Unsupported form requested for launch " + formName);
            }

            intent.putExtra("json", form.toString());
            Log.d(TAG, "form is " + form.toString());
            context.startActivityForResult(intent, jsonFormActivityRequestCode);
        }
    }

    public static Event addMetaData(Context context, Event event, Date start) throws JSONException {
        Map<String, String> metaFields = new HashMap<String, String>();
        metaFields.put("deviceid", "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Calendar calendar = Calendar.getInstance();

        String end = DATE_TIME_FORMAT.format(calendar.getTime());

        Obs obs = new Obs();
        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(DATE_TIME_FORMAT.format(start));
        obs.setFieldType("concept");
        obs.setFieldDataType("start");
        event.addObs(obs);


        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(end);
        obs.setFieldDataType("end");
        event.addObs(obs);

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = mTelephonyManager.getSimSerialNumber();

        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(deviceId);
        obs.setFieldDataType("deviceid");
        event.addObs(obs);
        return event;
    }

    /**
     * This method sorts the options provided for a native form tree view question
     *
     * @return The sorted options
     */
    private static JSONArray sortTreeViewQuestionOptions(JSONArray treeViewOptions) throws JSONException {
        JSONArray sortedTree = new JSONArray();

        HashMap<String, JSONObject> sortMap = new HashMap<>();
        for (int i = 0; i < treeViewOptions.length(); i++) {
            sortMap.put(treeViewOptions.getJSONObject(i).getString("name"), treeViewOptions.getJSONObject(i));
        }

        ArrayList<String> sortedKeys = new ArrayList<>(sortMap.keySet());
        Collections.sort(sortedKeys);

        for (String curOptionName : sortedKeys) {
            JSONObject curOption = sortMap.get(curOptionName);
            if (curOption.has("nodes")) {
                curOption.put("nodes", sortTreeViewQuestionOptions(curOption.getJSONArray("nodes")));
            }

            sortedTree.put(curOption);
        }

        return sortedTree;
    }

    public static Event createMoveToCatchmentEvent(Context context, org.ei.opensrp.path.db.Event referenceEvent, String fromLocationId, String toProviderId, String toLocationId) {

        try {
            final String FORM_SUBMISSION_FIELD = "formsubmissionField";
            final String DATA_TYPE = "text";

            Event event = (Event) new Event()
                    .withBaseEntityId(referenceEvent.getBaseEntityId())
                    .withEventDate(new Date())
                    .withEventType(MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT)
                    .withLocationId(fromLocationId)
                    .withProviderId(referenceEvent.getProviderId())
                    .withEntityType("child")
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());


            String formSubmissionField = "From_ProviderId";
            List<Object> vall = new ArrayList<>();
            vall.add(referenceEvent.getProviderId());
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField,
                    "", vall, new ArrayList<>(), null, formSubmissionField));

            formSubmissionField = "From_LocationId";
            vall = new ArrayList<>();
            vall.add(fromLocationId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField,
                    "", vall, new ArrayList<>(), null, formSubmissionField));

            formSubmissionField = "To_ProviderId";
            vall = new ArrayList<>();
            vall.add(toProviderId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField,
                    "", vall, new ArrayList<>(), null, formSubmissionField));

            formSubmissionField = "To_LocationId";
            vall = new ArrayList<>();
            vall.add(toLocationId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField,
                    "", vall, new ArrayList<>(), null, formSubmissionField));

            addMetaData(context, event, new Date());

            return event;

        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }
}

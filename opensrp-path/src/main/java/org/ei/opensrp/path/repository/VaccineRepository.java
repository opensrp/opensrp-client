package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.service.AlertService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VaccineRepository extends BaseRepository {
    private static final String TAG = VaccineRepository.class.getCanonicalName();
    private static final String VACCINE_SQL = "CREATE TABLE vaccines (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,base_entity_id VARCHAR NOT NULL,program_client_id VARCHAR NULL,name VARCHAR NOT NULL,calculation INTEGER,date DATETIME NOT NULL,anmid VARCHAR NULL,location_id VARCHAR NULL,sync_status VARCHAR,updated_at INTEGER NULL, UNIQUE(base_entity_id, program_client_id, name) ON CONFLICT IGNORE)";
    public static final String VACCINE_TABLE_NAME = "vaccines";
    public static final String ID_COLUMN = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String EVENT_ID = "event_id";
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String PROGRAM_CLIENT_ID = "program_client_id";
    public static final String NAME = "name";
    public static final String CALCULATION = "calculation";
    public static final String DATE = "date";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String SYNC_STATUS = "sync_status";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String[] VACCINE_TABLE_COLUMNS = {ID_COLUMN, BASE_ENTITY_ID, PROGRAM_CLIENT_ID, NAME, CALCULATION, DATE, ANMID, LOCATIONID, SYNC_STATUS, UPDATED_AT_COLUMN,EVENT_ID,FORMSUBMISSION_ID};

    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + VACCINE_TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + VACCINE_TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String UPDATED_AT_INDEX = "CREATE INDEX " + VACCINE_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + VACCINE_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";

    public static final String UPDATE_TABLE_ADD_EVENT_ID_COL = "ALTER TABLE "+ VACCINE_TABLE_NAME +" ADD COLUMN "+EVENT_ID+" VARCHAR;";
    public static final String EVENT_ID_INDEX = "CREATE INDEX " + VACCINE_TABLE_NAME + "_" + EVENT_ID + "_index ON " + VACCINE_TABLE_NAME + "(" + EVENT_ID + " COLLATE NOCASE);";

    public static final String UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL = "ALTER TABLE "+ VACCINE_TABLE_NAME +" ADD COLUMN "+FORMSUBMISSION_ID+" VARCHAR;";
    public static final String FORMSUBMISSION_INDEX = "CREATE INDEX " + VACCINE_TABLE_NAME + "_" + FORMSUBMISSION_ID + "_index ON " + VACCINE_TABLE_NAME + "(" + FORMSUBMISSION_ID + " COLLATE NOCASE);";

    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";

    private CommonFtsObject commonFtsObject;
    private AlertService alertService;

    public VaccineRepository(PathRepository pathRepository, CommonFtsObject commonFtsObject, AlertService alertService) {
        super(pathRepository);
        this.commonFtsObject = commonFtsObject;
        this.alertService = alertService;
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(VACCINE_SQL);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(UPDATED_AT_INDEX);
    }

    public void add(Vaccine vaccine) {
        if (vaccine == null) {
            return;
        }
        if (StringUtils.isBlank(vaccine.getSyncStatus())) {
            vaccine.setSyncStatus(TYPE_Unsynced);
        }
        if (StringUtils.isBlank(vaccine.getFormSubmissionId())) {
            vaccine.setFormSubmissionId(generateRandomUUIDString());
        }

        if (vaccine.getUpdatedAt() == null) {
            vaccine.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }

        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        if (vaccine.getId() == null) {
            vaccine.setId(database.insert(VACCINE_TABLE_NAME, null, createValuesFor(vaccine)));
        } else {
            //mark the vaccine as unsynced for processing as an updated event
            vaccine.setSyncStatus(TYPE_Unsynced);
            String idSelection = ID_COLUMN + " = ?";
            database.update(VACCINE_TABLE_NAME, createValuesFor(vaccine), idSelection, new String[]{vaccine.getId().toString()});
        }
        updateFtsSearch(vaccine);
    }

    public List<Vaccine> findUnSyncedBeforeTime(int hours) {
        List<Vaccine> vaccines = new ArrayList<Vaccine>();
        Cursor cursor = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -hours);

            Long time = calendar.getTimeInMillis();

            cursor = getPathRepository().getReadableDatabase().query(VACCINE_TABLE_NAME, VACCINE_TABLE_COLUMNS, UPDATED_AT_COLUMN + " < ? AND " + SYNC_STATUS + " = ?", new String[]{time.toString(), TYPE_Unsynced}, null, null, null, null);
            vaccines = readAllVaccines(cursor);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return vaccines;
    }


    public List<Vaccine> findByEntityId(String entityId) {
        SQLiteDatabase database = getPathRepository().getReadableDatabase();
        Cursor cursor = database.query(VACCINE_TABLE_NAME, VACCINE_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? ORDER BY " + UPDATED_AT_COLUMN, new String[]{entityId}, null, null, null, null);
        return readAllVaccines(cursor);
    }

    public Vaccine find(Long caseId) {
        Vaccine vaccine = null;
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(VACCINE_TABLE_NAME, VACCINE_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId.toString()}, null, null, null, null);
            List<Vaccine> vaccines = readAllVaccines(cursor);
            if (!vaccines.isEmpty()) {
                vaccine = vaccines.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return vaccine;
    }

    public void deleteVaccine(Long caseId) {
        Vaccine vaccine = find(caseId);
        if(vaccine != null) {
            getPathRepository().getWritableDatabase().delete(VACCINE_TABLE_NAME, ID_COLUMN + "= ?", new String[]{caseId.toString()});

            updateFtsSearch(vaccine.getBaseEntityId(), vaccine.getName());
        }
    }

    public void close(Long caseId) {
        ContentValues values = new ContentValues();
        values.put(SYNC_STATUS, TYPE_Synced);
        getPathRepository().getWritableDatabase().update(VACCINE_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId.toString()});
    }

    private List<Vaccine> readAllVaccines(Cursor cursor) {
        List<Vaccine> vaccines = new ArrayList<Vaccine>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String vaccineName = cursor.getString(cursor.getColumnIndex(NAME));
                    if (vaccineName != null) {
                        vaccineName = removeHyphen(vaccineName);
                    }
                    vaccines.add(
                            new Vaccine(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)),
                                    cursor.getString(cursor.getColumnIndex(PROGRAM_CLIENT_ID)),
                                    vaccineName,
                                    cursor.getInt(cursor.getColumnIndex(CALCULATION)),
                                    new Date(cursor.getLong(cursor.getColumnIndex(DATE))),
                                    cursor.getString(cursor.getColumnIndex(ANMID)),
                                    cursor.getString(cursor.getColumnIndex(LOCATIONID)),
                                    cursor.getString(cursor.getColumnIndex(SYNC_STATUS)),
                                    cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)),cursor.getString(cursor.getColumnIndex(EVENT_ID)),cursor.getString(cursor.getColumnIndex(FORMSUBMISSION_ID))
                            ));

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return vaccines;
    }


    private ContentValues createValuesFor(Vaccine vaccine) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, vaccine.getId());
        values.put(BASE_ENTITY_ID, vaccine.getBaseEntityId());
        values.put(PROGRAM_CLIENT_ID, vaccine.getProgramClientId());
        values.put(NAME, vaccine.getName() != null ? addHyphen(vaccine.getName().toLowerCase()): null);
        values.put(CALCULATION, vaccine.getCalculation());
        values.put(DATE, vaccine.getDate() != null ? vaccine.getDate().getTime() : null);
        values.put(ANMID, vaccine.getAnmId());
        values.put(LOCATIONID, vaccine.getLocationId());
        values.put(SYNC_STATUS, vaccine.getSyncStatus());
        values.put(UPDATED_AT_COLUMN, vaccine.getUpdatedAt() != null ? vaccine.getUpdatedAt() : null);
        values.put(EVENT_ID, vaccine.getEventId() != null ? vaccine.getEventId() : null);
        values.put(FORMSUBMISSION_ID, vaccine.getFormSubmissionId() != null ? vaccine.getFormSubmissionId() : null);
        return values;
    }

    //-----------------------
    // FTS methods
    public void updateFtsSearch(Vaccine vaccine) {
        try{
            if (commonFtsObject != null && alertService() != null) {
                String entityId = vaccine.getBaseEntityId();
                String vaccineName = vaccine.getName();
                if(vaccineName != null){
                    vaccineName = removeHyphen(vaccineName);
                }
                String scheduleName = commonFtsObject.getAlertScheduleName(vaccineName);

                String bindType = commonFtsObject.getAlertBindType(scheduleName);

                if (StringUtils.isNotBlank(bindType) && StringUtils.isNotBlank(scheduleName) && StringUtils.isNotBlank(entityId)) {
                    String field = addHyphen(scheduleName);
                    // update vaccine status
                    alertService().updateFtsSearchInACR(bindType, entityId, field, AlertStatus.complete.value());
                }
            }
        }catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    public void updateFtsSearch(String entityId, String vaccineName) {
        try {
            if (commonFtsObject != null && alertService() != null) {
                if (vaccineName != null) {
                    vaccineName = removeHyphen(vaccineName);
                }

                String scheduleName = commonFtsObject.getAlertScheduleName(vaccineName);
                if (StringUtils.isNotBlank(entityId) && StringUtils.isNotBlank(scheduleName)) {
                    Alert alert = alertService().findByEntityIdAndScheduleName(entityId, scheduleName);
                    alertService().updateFtsSearch(alert, true);
                }
            }
        } catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public AlertService alertService() {
        if(alertService == null){
            alertService = Context.getInstance().alertService();
        };
        return alertService;
    }

    public static String addHyphen(String s) {
        if(StringUtils.isNotBlank(s)){
            return  s.replace(" ", "_");
        }
        return s;
    }

    public static String removeHyphen(String s) {
        if(StringUtils.isNotBlank(s)){
            return  s.replace("_", " ");
        }
        return s;
    }
}

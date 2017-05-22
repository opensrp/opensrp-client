package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.service.AlertService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecurringServiceRecordRepository extends BaseRepository {
    private static final String TAG = RecurringServiceRecordRepository.class.getCanonicalName();
    private static final String CREATE_TABLE_SQL = "CREATE TABLE recurring_service_records (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,base_entity_id VARCHAR NOT NULL,recurring_service_id INTERGER NOT NULL,value VARCHAR, date DATETIME NOT NULL,anmid VARCHAR NULL,location_id VARCHAR NULL,sync_status VARCHAR, event_id VARCHAR, formSubmissionId VARCHAR, updated_at INTEGER NULL, UNIQUE(base_entity_id, recurring_service_id) ON CONFLICT IGNORE)";
    public static final String TABLE_NAME = "recurring_service_records";
    public static final String ID_COLUMN = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String VALUE = "value";
    public static final String EVENT_ID = "event_id";
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String RECURRING_SERVICE_ID = "recurring_service_id";
    public static final String DATE = "date";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String SYNC_STATUS = "sync_status";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String[] TABLE_COLUMNS = {ID_COLUMN, BASE_ENTITY_ID, RECURRING_SERVICE_ID, VALUE, DATE, ANMID, LOCATIONID, SYNC_STATUS, EVENT_ID, FORMSUBMISSION_ID, UPDATED_AT_COLUMN};

    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";
    public static final String RECURRING_SERVICE_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + RECURRING_SERVICE_ID + "_index ON " + TABLE_NAME + "(" + RECURRING_SERVICE_ID + ");";
    public static final String EVENT_ID_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + EVENT_ID + "_index ON " + TABLE_NAME + "(" + EVENT_ID + " COLLATE NOCASE);";
    public static final String FORMSUBMISSION_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + FORMSUBMISSION_ID + "_index ON " + TABLE_NAME + "(" + FORMSUBMISSION_ID + " COLLATE NOCASE);";
    private static final String UPDATED_AT_INDEX = "CREATE INDEX " + TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";

    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";
    private CommonFtsObject commonFtsObject;
    private AlertService alertService;

    public RecurringServiceRecordRepository(PathRepository pathRepository, CommonFtsObject commonFtsObject, AlertService alertService) {
        super(pathRepository);
        this.commonFtsObject = commonFtsObject;
        this.alertService = alertService;
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(RECURRING_SERVICE_ID_INDEX);
        database.execSQL(EVENT_ID_INDEX);
        database.execSQL(FORMSUBMISSION_INDEX);
        database.execSQL(UPDATED_AT_INDEX);
    }

    public void add(ServiceRecord serviceRecord) {
        if (serviceRecord == null) {
            return;
        }
        if (StringUtils.isBlank(serviceRecord.getSyncStatus())) {
            serviceRecord.setSyncStatus(TYPE_Unsynced);
        }
        if (StringUtils.isBlank(serviceRecord.getFormSubmissionId())) {
            serviceRecord.setFormSubmissionId(generateRandomUUIDString());
        }

        if (serviceRecord.getUpdatedAt() == null) {
            serviceRecord.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }

        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        if (serviceRecord.getId() == null) {
            serviceRecord.setId(database.insert(TABLE_NAME, null, createValuesFor(serviceRecord)));
        } else {
            //mark the recurring service as unsynced for processing as an updated event
            serviceRecord.setSyncStatus(TYPE_Unsynced);
            String idSelection = ID_COLUMN + " = ?";
            database.update(TABLE_NAME, createValuesFor(serviceRecord), idSelection, new String[]{serviceRecord.getId().toString()});
        }
    }

    public List<ServiceRecord> findUnSyncedBeforeTime(int hours) {
        List<ServiceRecord> serviceRecords = new ArrayList<ServiceRecord>();
        Cursor cursor = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -hours);

            Long time = calendar.getTimeInMillis();

            cursor = getPathRepository().getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS, UPDATED_AT_COLUMN + " < ? AND " + SYNC_STATUS + " = ?", new String[]{time.toString(), TYPE_Unsynced}, null, null, null, null);
            serviceRecords = readAllServiceRecords(cursor);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return serviceRecords;
    }

    public List<ServiceRecord> findByEntityId(String entityId) {
        SQLiteDatabase database = getPathRepository().getReadableDatabase();
        String sql = " SELECT " + TABLE_NAME + ".*, " + RecurringServiceTypeRepository.TABLE_NAME + ".name, " + RecurringServiceTypeRepository.TABLE_NAME + ".type FROM " + TABLE_NAME + " LEFT JOIN " + RecurringServiceTypeRepository.TABLE_NAME +
                " ON " + TABLE_NAME + "." + RECURRING_SERVICE_ID + " = " + RecurringServiceTypeRepository.TABLE_NAME + "." + RecurringServiceTypeRepository.ID_COLUMN +
                " WHERE " + TABLE_NAME + "." + BASE_ENTITY_ID + " = ? ORDER BY " + TABLE_NAME + "." + UPDATED_AT_COLUMN;
        Cursor cursor = database.rawQuery(sql, new String[]{entityId});
        return readAllServiceRecords(cursor);
    }

    public ServiceRecord find(Long caseId) {
        ServiceRecord serviceRecord = null;
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId.toString()}, null, null, null, null);
            List<ServiceRecord> serviceRecords = readAllServiceRecords(cursor);
            if (!serviceRecords.isEmpty()) {
                serviceRecord = serviceRecords.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return serviceRecord;
    }

    public void deleteServiceRecord(Long caseId) {
        ServiceRecord serviceRecord = find(caseId);
        if (serviceRecord != null) {
            getPathRepository().getWritableDatabase().delete(TABLE_NAME, ID_COLUMN + "= ?", new String[]{caseId.toString()});
        }
    }

    public void close(Long caseId) {
        ContentValues values = new ContentValues();
        values.put(SYNC_STATUS, TYPE_Synced);
        getPathRepository().getWritableDatabase().update(TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId.toString()});
    }

    private List<ServiceRecord> readAllServiceRecords(Cursor cursor) {
        List<ServiceRecord> serviceRecords = new ArrayList<ServiceRecord>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    ServiceRecord serviceRecord = new ServiceRecord(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)),
                            cursor.getLong(cursor.getColumnIndex(RECURRING_SERVICE_ID)),
                            cursor.getString(cursor.getColumnIndex(VALUE)),
                            new Date(cursor.getLong(cursor.getColumnIndex(DATE))),
                            cursor.getString(cursor.getColumnIndex(ANMID)),
                            cursor.getString(cursor.getColumnIndex(LOCATIONID)),
                            cursor.getString(cursor.getColumnIndex(SYNC_STATUS)),
                            cursor.getString(cursor.getColumnIndex(EVENT_ID)),
                            cursor.getString(cursor.getColumnIndex(FORMSUBMISSION_ID)),
                            cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)));

                    if (cursor.getColumnIndex(RecurringServiceTypeRepository.TYPE) > -1) {
                        serviceRecord.setType(cursor.getString(cursor.getColumnIndex(RecurringServiceTypeRepository.TYPE)));
                    }

                    if (cursor.getColumnIndex(RecurringServiceTypeRepository.NAME) > -1) {
                        serviceRecord.setName(cursor.getString(cursor.getColumnIndex(RecurringServiceTypeRepository.NAME)));
                    }
                    serviceRecords.add(serviceRecord);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return serviceRecords;
    }


    private ContentValues createValuesFor(ServiceRecord serviceRecord) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, serviceRecord.getId());
        values.put(BASE_ENTITY_ID, serviceRecord.getBaseEntityId());
        values.put(RECURRING_SERVICE_ID, serviceRecord.getRecurringServiceId());
        values.put(VALUE, serviceRecord.getValue());
        values.put(DATE, serviceRecord.getDate() != null ? serviceRecord.getDate().getTime() : null);
        values.put(ANMID, serviceRecord.getAnmId());
        values.put(LOCATIONID, serviceRecord.getLocationId());
        values.put(SYNC_STATUS, serviceRecord.getSyncStatus());
        values.put(EVENT_ID, serviceRecord.getEventId() != null ? serviceRecord.getEventId() : null);
        values.put(FORMSUBMISSION_ID, serviceRecord.getFormSubmissionId() != null ? serviceRecord.getFormSubmissionId() : null);
        values.put(UPDATED_AT_COLUMN, serviceRecord.getUpdatedAt() != null ? serviceRecord.getUpdatedAt() : null);
        return values;
    }

    public AlertService alertService() {
        if (alertService == null) {
            alertService = Context.getInstance().alertService();
        }
        ;
        return alertService;
    }

    public static String addHyphen(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replace(" ", "_");
        }
        return s;
    }

    public static String removeHyphen(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replace("_", " ");
        }
        return s;
    }
}

package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.domain.ZScore;
import org.opensrp.api.constants.Gender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeightRepository extends BaseRepository {
    private static final String TAG = WeightRepository.class.getCanonicalName();
    private static final String WEIGHT_SQL = "CREATE TABLE weights (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,base_entity_id VARCHAR NOT NULL,program_client_id VARCHAR NULL,kg REAL NOT NULL,date DATETIME NOT NULL,anmid VARCHAR NULL,location_id VARCHAR NULL,sync_status VARCHAR,updated_at INTEGER NULL)";
    public static final String WEIGHT_TABLE_NAME = "weights";
    public static final String ID_COLUMN = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String EVENT_ID = "event_id";
    public static final String PROGRAM_CLIENT_ID = "program_client_id";// ID to be used to identify entity when base_entity_id is unavailable
    public static final String FORMSUBMISSION_ID = "formSubmissionId";
    public static final String OUT_OF_AREA = "out_of_area";

    public static final String KG = "kg";
    public static final String DATE = "date";
    public static final String ANMID = "anmid";
    public static final String LOCATIONID = "location_id";
    public static final String SYNC_STATUS = "sync_status";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String Z_SCORE = "z_score";
    public static final double DEFAULT_Z_SCORE = 999999d;
    public static final String[] WEIGHT_TABLE_COLUMNS = {
            ID_COLUMN, BASE_ENTITY_ID, PROGRAM_CLIENT_ID, KG, DATE, ANMID, LOCATIONID, SYNC_STATUS,
            UPDATED_AT_COLUMN, EVENT_ID, FORMSUBMISSION_ID, Z_SCORE,OUT_OF_AREA};

    private static final String BASE_ENTITY_ID_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";
    private static final String SYNC_STATUS_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + SYNC_STATUS + "_index ON " + WEIGHT_TABLE_NAME + "(" + SYNC_STATUS + " COLLATE NOCASE);";
    private static final String UPDATED_AT_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + WEIGHT_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";
    public static final String UPDATE_TABLE_ADD_EVENT_ID_COL = "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + EVENT_ID + " VARCHAR;";
    public static final String EVENT_ID_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + EVENT_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" + EVENT_ID + " COLLATE NOCASE);";
    public static final String UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL = "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + FORMSUBMISSION_ID + " VARCHAR;";
    public static final String FORMSUBMISSION_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + FORMSUBMISSION_ID + "_index ON " + WEIGHT_TABLE_NAME + "(" + FORMSUBMISSION_ID + " COLLATE NOCASE);";
    public static final String UPDATE_TABLE_ADD_OUT_OF_AREA_COL = "ALTER TABLE "+ WEIGHT_TABLE_NAME +" ADD COLUMN "+OUT_OF_AREA+" VARCHAR;";
    public static final String UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX = "CREATE INDEX " + WEIGHT_TABLE_NAME + "_" + OUT_OF_AREA + "_index ON " + WEIGHT_TABLE_NAME + "(" + OUT_OF_AREA + " COLLATE NOCASE);";

    public static final String ALTER_ADD_Z_SCORE_COLUMN = "ALTER TABLE " + WEIGHT_TABLE_NAME + " ADD COLUMN " + Z_SCORE + " REAL NOT NULL DEFAULT " + String.valueOf(DEFAULT_Z_SCORE);


    public WeightRepository(PathRepository pathRepository) {
        super(pathRepository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(WEIGHT_SQL);
        database.execSQL(BASE_ENTITY_ID_INDEX);
        database.execSQL(SYNC_STATUS_INDEX);
        database.execSQL(UPDATED_AT_INDEX);
    }

    /**
     * This method sets the weight's z-score, before adding it to the database
     *
     * @param dateOfBirth
     * @param gender
     * @param weight
     */
    public void add(Date dateOfBirth, Gender gender, Weight weight) {
        weight.setZScore(ZScore.calculate(gender, dateOfBirth, weight.getDate(), weight.getKg()));
        add(weight);
    }

    public void add(Weight weight) {
        try {
            if (weight == null) {
                return;
            }
            if (StringUtils.isBlank(weight.getSyncStatus())) {
                weight.setSyncStatus(TYPE_Unsynced);
            }
            if (StringUtils.isBlank(weight.getFormSubmissionId())) {
                weight.setFormSubmissionId(generateRandomUUIDString());
            }


            if (weight.getUpdatedAt() == null) {
                weight.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
            }

            SQLiteDatabase database = getPathRepository().getWritableDatabase();
            if (weight.getId() == null) {
                Weight sameWeight = findUnique(database, weight);
                if (sameWeight != null) {
                    weight.setUpdatedAt(sameWeight.getUpdatedAt());
                    weight.setId(sameWeight.getId());
                    update(database, weight);
                } else {
                    weight.setId(database.insert(WEIGHT_TABLE_NAME, null, createValuesFor(weight)));
                }
            } else {
                weight.setSyncStatus(TYPE_Unsynced);
                update(database, weight);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void update(SQLiteDatabase database, Weight weight) {
        if (weight == null || weight.getId() == null) {
            return;
        }

        try {
            if (database == null) {
                database = getPathRepository().getWritableDatabase();
            }

            String idSelection = ID_COLUMN + " = ?";
            database.update(WEIGHT_TABLE_NAME, createValuesFor(weight), idSelection, new String[]{weight.getId().toString()});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public List<Weight> findUnSyncedBeforeTime(int hours) {
        List<Weight> weights = new ArrayList<>();
        Cursor cursor = null;
        try {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -hours);

            Long time = calendar.getTimeInMillis();

            cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, UPDATED_AT_COLUMN + " < ? AND " + SYNC_STATUS + " = ?", new String[]{time.toString(), TYPE_Unsynced}, null, null, null, null);
            weights = readAllWeights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weights;
    }

    public Weight findUnSyncedByEntityId(String entityId) {
        Weight weight = null;
        Cursor cursor = null;
        try {

            cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? AND " + SYNC_STATUS + " = ?", new String[]{entityId, TYPE_Unsynced}, null, null, null, null);
            List<Weight> weights = readAllWeights(cursor);
            if (!weights.isEmpty()) {
                weight = weights.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weight;
    }

    public List<Weight> findByEntityId(String entityId) {
        List<Weight> weights = null;
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? ", new String[]{entityId}, null, null, null, null);
            weights = readAllWeights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return weights;
    }

    public List<Weight> findWithNoZScore() {
        List<Weight> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME,
                    WEIGHT_TABLE_COLUMNS, Z_SCORE + " = " + DEFAULT_Z_SCORE, null, null, null, null, null);
            result = readAllWeights(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public Weight find(Long caseId) {
        Weight weight = null;
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId.toString()}, null, null, null, null);
            List<Weight> weights = readAllWeights(cursor);
            if (!weights.isEmpty()) {
                weight = weights.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return weight;
    }

    public List<Weight> findLast5(String entityid) {
        Cursor cursor = getPathRepository().getReadableDatabase().query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, BASE_ENTITY_ID + " = ?", new String[]{entityid}, null, null, UPDATED_AT_COLUMN + " DESC", null);
        List<Weight> weights = readAllWeights(cursor);
//        if (!weights.isEmpty()) {
        return weights;
//        }

//        return null;
    }

    public Weight findUnique(SQLiteDatabase database, Weight weight) {

        if (weight == null || (StringUtils.isBlank(weight.getFormSubmissionId()) && StringUtils.isBlank(weight.getEventId()))) {
            return null;
        }

        try {
            if (database == null) {
                database = getPathRepository().getReadableDatabase();
            }

            String selection = null;
            String[] selectionArgs = null;
            if (StringUtils.isNotBlank(weight.getFormSubmissionId()) && StringUtils.isNotBlank(weight.getEventId())) {
                selection = FORMSUBMISSION_ID + " = ? OR " + EVENT_ID + " = ? ";
                selectionArgs = new String[]{weight.getFormSubmissionId(), weight.getEventId()};
            } else if (StringUtils.isNotBlank(weight.getEventId())) {
                selection = EVENT_ID + " = ? ";
                selectionArgs = new String[]{weight.getEventId()};
            } else if (StringUtils.isNotBlank(weight.getFormSubmissionId())) {
                selection = FORMSUBMISSION_ID + " = ? ";
                selectionArgs = new String[]{weight.getFormSubmissionId()};
            }

            Cursor cursor = database.query(WEIGHT_TABLE_NAME, WEIGHT_TABLE_COLUMNS, selection, selectionArgs, null, null, ID_COLUMN + " DESC ", null);
            List<Weight> weightList = readAllWeights(cursor);
            if (!weightList.isEmpty()) {
                return weightList.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    public void delete(String entityID) {
        try {
            getPathRepository().getWritableDatabase().delete(WEIGHT_TABLE_NAME, BASE_ENTITY_ID + " = ? AND " + SYNC_STATUS + " = ?", new String[]{entityID, TYPE_Unsynced});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void close(Long caseId) {
        try {
            ContentValues values = new ContentValues();
            values.put(SYNC_STATUS, TYPE_Synced);
            getPathRepository().getWritableDatabase().update(WEIGHT_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId.toString()});
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private List<Weight> readAllWeights(Cursor cursor) {
        List<Weight> weights = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Double zScore = cursor.getDouble(cursor.getColumnIndex(Z_SCORE));
                    if (zScore != null && zScore.equals(new Double(DEFAULT_Z_SCORE))) {
                        zScore = null;
                    }

                    weights.add(
                            new Weight(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)),
                                    cursor.getString(cursor.getColumnIndex(PROGRAM_CLIENT_ID)),
                                    cursor.getFloat(cursor.getColumnIndex(KG)),
                                    new Date(cursor.getLong(cursor.getColumnIndex(DATE))),
                                    cursor.getString(cursor.getColumnIndex(ANMID)),
                                    cursor.getString(cursor.getColumnIndex(LOCATIONID)),
                                    cursor.getString(cursor.getColumnIndex(SYNC_STATUS)),
                                    cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)),cursor.getString(cursor.getColumnIndex(EVENT_ID)),cursor.getString(cursor.getColumnIndex(FORMSUBMISSION_ID)),zScore,cursor.getInt(cursor.getColumnIndex(OUT_OF_AREA))

                            ));

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return weights;

    }


    private ContentValues createValuesFor(Weight weight) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, weight.getId());
        values.put(BASE_ENTITY_ID, weight.getBaseEntityId());
        values.put(PROGRAM_CLIENT_ID, weight.getProgramClientId());
        values.put(KG, weight.getKg());
        values.put(DATE, weight.getDate() != null ? weight.getDate().getTime() : null);
        values.put(ANMID, weight.getAnmId());
        values.put(LOCATIONID, weight.getLocationId());
        values.put(SYNC_STATUS, weight.getSyncStatus());
        values.put(UPDATED_AT_COLUMN, weight.getUpdatedAt() != null ? weight.getUpdatedAt() : null);
        values.put(EVENT_ID, weight.getEventId() != null ? weight.getEventId() : null);
        values.put(FORMSUBMISSION_ID, weight.getFormSubmissionId() != null ? weight.getFormSubmissionId() : null);
        values.put(OUT_OF_AREA, weight.getOutOfCatchment() != null ? weight.getOutOfCatchment() : null);
        values.put(Z_SCORE, weight.getZScore() == null ? DEFAULT_Z_SCORE : weight.getZScore());
        return values;
    }
}

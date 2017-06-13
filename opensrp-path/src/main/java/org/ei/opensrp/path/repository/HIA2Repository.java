package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.service.HIA2Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HIA2Repository extends BaseRepository {
    private static final String TAG = HIA2Repository.class.getCanonicalName();
    private static final String HIA2_SQL = "CREATE TABLE hia2 (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,provider_id VARCHAR NOT NULL,indicator_code VARCHAR NOT NULL,dhis_id VARCHAR NOT NULL, value VARCHAR NOT NULL,month VARCHAR NOT NULL,status VARCHAR,created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";
    public static final String HIA2_TABLE_NAME = "hia2";
    public static final String ID_COLUMN = "_id";
    public static final String PROVIDER_ID = "provider_id";
    public static final String INDICATOR_CODE = "indicator_code";
    public static final String VALUE = "value";// ID to be used to identify entity when base_entity_id is unavailable
    public static final String DHIS_ID = "dhis_id";
    public static final String MONTH = "month";

    public static final String STATUS = "status";
    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String[] HIA2_TABLE_COLUMNS = {ID_COLUMN, PROVIDER_ID, INDICATOR_CODE, VALUE, DHIS_ID, STATUS, MONTH, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};

    private static final String PROVIDER_ID_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + PROVIDER_ID + "_index ON " + HIA2_TABLE_NAME + "(" + PROVIDER_ID + " COLLATE NOCASE);";
    private static final String KEY_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + INDICATOR_CODE + "_index ON " + HIA2_TABLE_NAME + "(" + INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String VALUE_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + HIA2_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";
    private static final String DHIS_ID_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + DHIS_ID + "_index ON " + HIA2_TABLE_NAME + "(" + DHIS_ID + ");";
    private static final String MONTH_INDEX = "CREATE INDEX " + HIA2_TABLE_NAME + "_" + MONTH + "_index ON " + HIA2_TABLE_NAME + "(" + MONTH + ");";


    public HIA2Repository(PathRepository pathRepository) {
        super(pathRepository);

    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(HIA2_SQL);
        database.execSQL(PROVIDER_ID_INDEX);
        database.execSQL(KEY_INDEX);
        database.execSQL(VALUE_INDEX);
        database.execSQL(DHIS_ID_INDEX);
        database.execSQL(MONTH_INDEX);
    }

    public void save(Map<String, Map<String, Object>> hia2Report) {
        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        try {
            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            String month = HIA2Service.dfyymm.format(new Date());
            database.beginTransaction();
            for (String key : hia2Report.keySet()) {
                Map<String, Object> indicatorMap = hia2Report.get(key);
                if (!indicatorMap.isEmpty()) {
                    String dhisId = (String) indicatorMap.keySet().toArray()[0];
                    String indicatorValue = (String) indicatorMap.get(dhisId);
                    Long id = checkIfExists(key, userName, month);

                    ContentValues cv = new ContentValues();
                    cv.put(HIA2Repository.INDICATOR_CODE, key);
                    cv.put(HIA2Repository.DHIS_ID, dhisId);
                    cv.put(HIA2Repository.VALUE, indicatorValue);
                    cv.put(HIA2Repository.PROVIDER_ID, userName);
                    cv.put(HIA2Repository.MONTH, month);
                    if (id != null) {
                        // cv.put(HIA2Repository.ID_COLUMN, id);
                        database.update(HIA2_TABLE_NAME, cv, ID_COLUMN + " = ?", new String[]{id.toString()});

                    } else {
                        database.insert(HIA2_TABLE_NAME, null, cv);
                    }
                }
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }

    public List<Hia2Indicator> findByProviderIdAndMonth(String providerId, String month) {
        List<Hia2Indicator> hia2Indicators = null;
        Cursor cursor = null;
        try {
            cursor = getPathRepository().getReadableDatabase().query(HIA2_TABLE_NAME, HIA2_TABLE_COLUMNS, PROVIDER_ID + " = ? AND " + MONTH + "=?", new String[]{providerId, month}, null, null, null, null);
            hia2Indicators = readAllDataElements(cursor);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return hia2Indicators;
    }

    private List<Hia2Indicator> readAllDataElements(Cursor cursor) {
        List<Hia2Indicator> hia2Indicators = new ArrayList<>();
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    Hia2Indicator hia2Indicator = new Hia2Indicator();
                    hia2Indicator.setDhisId(cursor.getString(cursor.getColumnIndex(DHIS_ID)));
                    hia2Indicator.setIndicatorCode(cursor.getString(cursor.getColumnIndex(INDICATOR_CODE)));
                    hia2Indicator.setValue(cursor.getString(cursor.getColumnIndex(VALUE)));
                    hia2Indicators.add(hia2Indicator);

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return hia2Indicators;

    }

    private Long checkIfExists(String indicatorCode, String providerId, String month) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + ID_COLUMN + " FROM " + HIA2_TABLE_NAME + " WHERE " + INDICATOR_CODE + " = '" + indicatorCode + "' and " + PROVIDER_ID + "='" + providerId + "' and " + MONTH + "='" + month + "'";
            mCursor = getPathRepository().getWritableDatabase().rawQuery(query, null);
            if (mCursor != null && mCursor.moveToFirst()) {

                return mCursor.getLong(0);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) mCursor.close();
        }
        return null;
    }

}

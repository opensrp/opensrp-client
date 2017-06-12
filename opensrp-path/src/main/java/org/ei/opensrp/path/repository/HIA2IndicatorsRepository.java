package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;
import java.util.Map;

public class HIA2IndicatorsRepository extends BaseRepository {
    private static final String TAG = HIA2IndicatorsRepository.class.getCanonicalName();
    public static final String INDICATORS_CSV_FILE="Zambia-EIR-DataDictionaryReporting-HIA2.csv";
    private static final String HIA2_INDICATORS_SQL = "CREATE TABLE hia2_indicators (_id INTEGER NOT NULL,provider_id VARCHAR,indicator_code VARCHAR NOT NULL,label VARCHAR,dhis_id VARCHAR ,description VARCHAR,category VARCHAR ,created_at DATETIME NULL,updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";
    public static final String HIA2_INDICATORS_TABLE_NAME = "hia2_indicators";
    public static final String ID_COLUMN = "_id";
    public static final String PROVIDER_ID = "provider_id";
    public static final String INDICATOR_CODE = "indicator_code";
    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    public static final String DHIS_ID = "dhis_id";
    public static final String CATEGORY = "category";

    public static final String CREATED_AT_COLUMN = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    public static final String[] HIA2_TABLE_COLUMNS = {ID_COLUMN, PROVIDER_ID, INDICATOR_CODE,LABEL, DHIS_ID,DESCRIPTION,CATEGORY, CREATED_AT_COLUMN, UPDATED_AT_COLUMN};

    private static final String PROVIDER_ID_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + PROVIDER_ID + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + PROVIDER_ID + " COLLATE NOCASE);";
    private static final String KEY_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + INDICATOR_CODE + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + INDICATOR_CODE + " COLLATE NOCASE);";
    private static final String VALUE_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + UPDATED_AT_COLUMN + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + UPDATED_AT_COLUMN + ");";
    private static final String DHIS_ID_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + DHIS_ID + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + DHIS_ID + ");";
    private static final String LABEL_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + LABEL + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + LABEL + ");";
    private static final String DESCRIPTION_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + DESCRIPTION + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + DESCRIPTION + ");";
    private static final String CATEGORY_INDEX = "CREATE INDEX " + HIA2_INDICATORS_TABLE_NAME + "_" + CATEGORY + "_index ON " + HIA2_INDICATORS_TABLE_NAME + "(" + CATEGORY + ");";


    public HIA2IndicatorsRepository(PathRepository pathRepository) {
        super(pathRepository);

    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(HIA2_INDICATORS_SQL);
        database.execSQL(PROVIDER_ID_INDEX);
        database.execSQL(KEY_INDEX);
        database.execSQL(VALUE_INDEX);
        database.execSQL(DHIS_ID_INDEX);
        database.execSQL(LABEL_INDEX);
        database.execSQL(DESCRIPTION_INDEX);
        database.execSQL(CATEGORY_INDEX);
    }

    public void save(SQLiteDatabase database,List<Map<String, String>> hia2Indicators) {
        try {

            database.beginTransaction();
            for (Map<String, String> hia2Indicator : hia2Indicators) {
                ContentValues cv = new ContentValues();

                for (String column : hia2Indicator.keySet()) {

                    String value = hia2Indicator.get(column);
                    cv.put(column, value);

                }
                Long id = checkIfExists(database,cv.getAsString(INDICATOR_CODE));

                if (id != null) {
                    database.update(HIA2_INDICATORS_TABLE_NAME, cv, ID_COLUMN + " = ?", new String[]{id.toString()});

                } else {
                    database.insert(HIA2_INDICATORS_TABLE_NAME, null, cv);
                }
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }


    private Long checkIfExists(SQLiteDatabase db,String indicatorCode) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + ID_COLUMN + " FROM " + HIA2_INDICATORS_TABLE_NAME + " WHERE " + INDICATOR_CODE + " = '" + indicatorCode + "'";
            mCursor = db.rawQuery(query, null);
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

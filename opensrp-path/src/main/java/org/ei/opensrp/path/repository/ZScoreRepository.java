package org.ei.opensrp.path.repository;

import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.path.domain.ZScore;
import org.opensrp.api.constants.Gender;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores child z-scores obtained from:
 * - http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt
 * - http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt
 * <p/>
 * Created by Jason Rogena - jrogena@ona.io on 29/05/2017.
 */

public class ZScoreRepository extends BaseRepository {
    private static final String TAG = ZScoreRepository.class.getName();
    public static final String TABLE_NAME = "z_scores";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_L = "l";
    public static final String COLUMN_M = "m";
    public static final String COLUMN_S = "s";
    public static final String COLUMN_SD3NEG = "sd3neg";
    public static final String COLUMN_SD2NEG = "sd2neg";
    public static final String COLUMN_SD1NEG = "sd1neg";
    public static final String COLUMN_SD0 = "sd0";
    public static final String COLUMN_SD1 = "sd1";
    public static final String COLUMN_SD2 = "sd2";
    public static final String COLUMN_SD3 = "sd3";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
            " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SEX + " VARCHAR NOT NULL, " +
            COLUMN_MONTH + " INTEGER NOT NULL, " +
            COLUMN_L + " REAL NOT NULL, " +
            COLUMN_M + " REAL NOT NULL, " +
            COLUMN_S + " REAL NOT NULL, " +
            COLUMN_SD3NEG + " REAL NOT NULL, " +
            COLUMN_SD2NEG + " REAL NOT NULL, " +
            COLUMN_SD1NEG + " REAL NOT NULL, " +
            COLUMN_SD0 + " REAL NOT NULL, " +
            COLUMN_SD1 + " REAL NOT NULL, " +
            COLUMN_SD2 + " REAL NOT NULL, " +
            COLUMN_SD3 + " REAL NOT NULL, " +
            "UNIQUE(" + COLUMN_SEX + ", " + COLUMN_MONTH + ") ON CONFLICT REPLACE)";

    private static final String CREATE_INDEX_SEX_QUERY = "CREATE INDEX " + COLUMN_SEX + "_index ON " + TABLE_NAME + "(" + COLUMN_SEX + " COLLATE NOCASE);";
    private static final String CREATE_INDEX_MONTH_QUERY = "CREATE INDEX " + COLUMN_MONTH + "_index ON " + TABLE_NAME + "(" + COLUMN_MONTH + " COLLATE NOCASE);";

    public ZScoreRepository(PathRepository pathRepository) {
        super(pathRepository);
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(CREATE_INDEX_SEX_QUERY);
        database.execSQL(CREATE_INDEX_MONTH_QUERY);
    }

    /**
     * @param query
     * @return
     */
    public boolean runRawQuery(String query) {
        try {
            getPathRepository().getWritableDatabase().execSQL(query);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    public List<ZScore> findByGender(Gender gender) {
        List<ZScore> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getPathRepository().getReadableDatabase();
            cursor = database.query(TABLE_NAME,
                    null,
                    COLUMN_SEX + " = ? " + COLLATE_NOCASE,
                    new String[]{gender.name()}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    result.add(new ZScore(gender,
                            cursor.getInt(cursor.getColumnIndex(COLUMN_MONTH)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_L)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_M)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_S)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1NEG)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD0)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD1)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD2)),
                            cursor.getDouble(cursor.getColumnIndex(COLUMN_SD3))));
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) cursor.close();
        }

        return result;
    }
}

package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.DailyTally;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.service.HIA2Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyTalliesRepository extends BaseRepository {
    private static final String TAG = DailyTalliesRepository.class.getCanonicalName();
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String TABLE_NAME = "daily_tallies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROVIDER_ID = "provider_id";
    public static final String COLUMN_INDICATOR_ID = "indicator_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_INDICATOR_ID, COLUMN_PROVIDER_ID,
            COLUMN_VALUE, COLUMN_DAY, COLUMN_UPDATED_AT
    };
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            COLUMN_INDICATOR_ID + " INTEGER NOT NULL," +
            COLUMN_PROVIDER_ID + " VARCHAR NOT NULL," +
            COLUMN_VALUE + " VARCHAR NOT NULL," +
            COLUMN_DAY + " DATETIME NOT NULL," +
            COLUMN_UPDATED_AT + " TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
    private static final String INDEX_PROVIDER_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_PROVIDER_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_PROVIDER_ID + " COLLATE NOCASE);";
    private static final String INDEX_INDICATOR_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_ID + " COLLATE NOCASE);";
    private static final String INDEX_UPDATED_AT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_UPDATED_AT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_UPDATED_AT + ");";
    private static final String INDEX_DAY = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_DAY + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_DAY + ");";
    private static final String INDEX_UNIQUE = "CREATE UNIQUE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_ID + "_" + COLUMN_DAY + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_ID + "," + COLUMN_DAY + ");";
    public static final ArrayList<String> IGNORED_INDICATOR_CODES;

    static {
        IGNORED_INDICATOR_CODES = new ArrayList<>();
        IGNORED_INDICATOR_CODES.add(HIA2Service.CHN3_027);
        IGNORED_INDICATOR_CODES.add(HIA2Service.CHN3_027_O);
        IGNORED_INDICATOR_CODES.add(HIA2Service.CHN3_090);
    }

    public DailyTalliesRepository(PathRepository pathRepository) {
        super(pathRepository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(INDEX_PROVIDER_ID);
        database.execSQL(INDEX_INDICATOR_ID);
        database.execSQL(INDEX_UPDATED_AT);
        database.execSQL(INDEX_DAY);
        database.execSQL(INDEX_UNIQUE);
    }

    /**
     * Saves a set of tallies
     *
     * @param day        The day the tallies correspond to
     * @param hia2Report Object holding the tallies, the first key in the map holds the indicator
     *                   code, and the second the DHIS id for the indicator. It's expected that
     *                   the inner most map will always hold one value
     */
    public void save(String day, Map<String, Object> hia2Report) {
        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        try {
            database.beginTransaction();
            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            for (String indicatorCode : hia2Report.keySet()) {
                Integer indicatorValue = (Integer) hia2Report.get(indicatorCode);

                // Get the HIA2 Indicator corresponding to the current tally
                Hia2Indicator indicator = VaccinatorApplication.getInstance()
                        .hIA2IndicatorsRepository()
                        .findByIndicatorCode(indicatorCode);

                if (indicator != null) {
                    ContentValues cv = new ContentValues();
                    cv.put(DailyTalliesRepository.COLUMN_INDICATOR_ID, indicator.getId());
                    cv.put(DailyTalliesRepository.COLUMN_VALUE, indicatorValue);
                    cv.put(DailyTalliesRepository.COLUMN_PROVIDER_ID, userName);
                    cv.put(DailyTalliesRepository.COLUMN_DAY, DAY_FORMAT.parse(day).getTime());
                    cv.put(DailyTalliesRepository.COLUMN_UPDATED_AT, Calendar.getInstance().getTimeInMillis());

                    database.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Returns a list of dates for distinct months with daily tallies
     *
     * @param dateFormat The format to use to format the months' dates
     * @param startDate  The first date to consider. Set argument to null if you
     *                   don't want this enforced
     * @param endDate    The last date to consider. Set argument to null if you
     *                   don't want this enforced
     * @return A list of months that have daily tallies
     */
    public List<String> findAllDistinctMonths(SimpleDateFormat dateFormat, Date startDate, Date endDate) {
        Cursor cursor = null;
        try {
            String selectionArgs = "";
            if (startDate != null) {
                selectionArgs = COLUMN_DAY + " >= " + startDate.getTime();
            }

            if (endDate != null) {
                if (!TextUtils.isEmpty(selectionArgs)) {
                    selectionArgs = selectionArgs + " AND ";
                }

                selectionArgs = selectionArgs + COLUMN_DAY + " <= " + endDate.getTime();
            }

            cursor = getPathRepository().getReadableDatabase().query(true, TABLE_NAME,
                    new String[]{COLUMN_DAY},
                    selectionArgs, null, null, null, null, null);

            return getUniqueMonths(dateFormat, cursor);
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns a list of unique months formatted in the provided {@link SimpleDateFormat}
     *
     * @param dateFormat The date format to format the months
     * @param cursor     Cursor to get the dates from
     * @return
     */
    private List<String> getUniqueMonths(SimpleDateFormat dateFormat, Cursor cursor) {
        List<String> months = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Date curMonth = new Date(cursor.getLong(0));
                String month = dateFormat.format(curMonth);
                if (!months.contains(month)) {
                    months.add(month);
                }
            }
        }

        return months;
    }

    public Map<Long, List<DailyTally>> findTalliesInMonth(Date month) {
        Map<Long, List<DailyTally>> talliesFromMonth = new HashMap<>();
        Cursor cursor = null;
        try {
            HashMap<Long, Hia2Indicator> indicatorMap = VaccinatorApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();

            Calendar startDate = Calendar.getInstance();
            startDate.setTime(month);
            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);

            Calendar endDate = Calendar.getInstance();
            endDate.setTime(month);
            endDate.add(Calendar.MONTH, 1);
            endDate.set(Calendar.DAY_OF_MONTH, 1);
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 999);
            endDate.add(Calendar.DATE, -1);

            cursor = getPathRepository().getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    getDayBetweenDatesSelection(startDate.getTime(), endDate.getTime()),
                    null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    DailyTally curTally = extractDailyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        if (!talliesFromMonth.containsKey(curTally.getIndicator().getId())) {
                            talliesFromMonth.put(
                                    curTally.getIndicator().getId(),
                                    new ArrayList<DailyTally>());
                        }

                        talliesFromMonth.get(curTally.getIndicator().getId()).add(curTally);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return talliesFromMonth;
    }

    private String getDayBetweenDatesSelection(Date startDate, Date endDate) {
        return COLUMN_DAY + " >= " + String.valueOf(startDate.getTime()) +
                " AND " + COLUMN_DAY + " <= " + String.valueOf(endDate.getTime());
    }

    public List<DailyTally> findByProviderIdAndDay(String providerId, String date) {
        List<DailyTally> tallies = new ArrayList<>();
        try {
            Cursor cursor = getPathRepository().getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    COLUMN_DAY + " = Datetime(?) AND " + COLUMN_PROVIDER_ID + " = ? COLLATE NOCASE ",
                    new String[]{date, providerId}, null, null, null, null);
            tallies = readAllDataElements(cursor);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return tallies;
    }

    public HashMap<String, ArrayList<DailyTally>> findAll(SimpleDateFormat dateFormat, Date minDate, Date maxDate) {
        HashMap<String, ArrayList<DailyTally>> tallies = new HashMap<>();
        Cursor cursor = null;
        try {
            HashMap<Long, Hia2Indicator> indicatorMap = VaccinatorApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();
            cursor = getPathRepository().getReadableDatabase()
                    .query(TABLE_NAME, TABLE_COLUMNS,
                            getDayBetweenDatesSelection(minDate, maxDate),
                            null, null, null, COLUMN_DAY + " DESC", null);
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    DailyTally curTally = extractDailyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        final String dayString = dateFormat.format(curTally.getDay());
                        if (!TextUtils.isEmpty(dayString)) {
                            if (!tallies.containsKey(dayString) ||
                                    tallies.get(dayString) == null) {
                                tallies.put(dayString, new ArrayList<DailyTally>());
                            }

                            tallies.get(dayString).add(curTally);
                        } else {
                            Log.w(TAG, "There appears to be a daily tally with a null date");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (NullPointerException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    private List<DailyTally> readAllDataElements(Cursor cursor) {
        List<DailyTally> tallies = new ArrayList<>();
        try {
            HashMap<Long, Hia2Indicator> indicatorMap = VaccinatorApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    DailyTally curTally = extractDailyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        tallies.add(curTally);
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    private DailyTally extractDailyTally(HashMap<Long, Hia2Indicator> indicatorMap, Cursor cursor) {
        long indicatorId = cursor.getLong(cursor.getColumnIndex(COLUMN_INDICATOR_ID));
        if (indicatorMap.containsKey(indicatorId)) {
            Hia2Indicator indicator = indicatorMap.get(indicatorId);
            if (!IGNORED_INDICATOR_CODES.contains(indicator.getIndicatorCode())) {
                DailyTally curTally = new DailyTally();
                curTally.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                curTally.setProviderId(
                        cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDER_ID)));
                curTally.setIndicator(indicator);
                curTally.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
                curTally.setDay(
                        new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DAY))));
                curTally.setUpdatedAt(
                        new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)))
                );

                return curTally;
            }
        }

        return null;
    }

    private Long checkIfExists(long indicatorId, String day) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_INDICATOR_ID + " = " + String.valueOf(indicatorId) + " COLLATE NOCASE "
                    + " AND " + COLUMN_DAY + "='" + day + "'";
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

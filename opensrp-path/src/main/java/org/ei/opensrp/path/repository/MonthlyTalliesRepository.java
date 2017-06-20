package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.DailyTally;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.domain.MonthlyTally;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jason Rogena - jrogena@ona.io on 15/06/2017.
 */

public class MonthlyTalliesRepository extends BaseRepository {
    private static final String TAG = MonthlyTalliesRepository.class.getCanonicalName();
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
    public static final String TABLE_NAME = "monthly_tallies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PROVIDER_ID = "provider_id";
    public static final String COLUMN_INDICATOR_ID = "indicator_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_EDITED = "edited";
    public static final String COLUMN_DATE_SENT = "date_sent";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String[] TABLE_COLUMNS = {
            COLUMN_ID, COLUMN_INDICATOR_ID, COLUMN_PROVIDER_ID,
            COLUMN_VALUE, COLUMN_MONTH, COLUMN_EDITED, COLUMN_DATE_SENT, COLUMN_UPDATED_AT
    };

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            COLUMN_INDICATOR_ID + " INTEGER NOT NULL," +
            COLUMN_PROVIDER_ID + " VARCHAR NOT NULL," +
            COLUMN_VALUE + " VARCHAR NOT NULL," +
            COLUMN_MONTH + " DATETIME NOT NULL," +
            COLUMN_EDITED + " INTEGER NOT NULL DEFAULT 0," +
            COLUMN_DATE_SENT + " DATETIME NULL," +
            COLUMN_UPDATED_AT + " TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP)";

    private static final String INDEX_PROVIDER_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_PROVIDER_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_PROVIDER_ID + " COLLATE NOCASE);";
    private static final String INDEX_INDICATOR_ID = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_INDICATOR_ID + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_INDICATOR_ID + " COLLATE NOCASE);";
    private static final String INDEX_UPDATED_AT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_UPDATED_AT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_UPDATED_AT + ");";
    private static final String INDEX_MONTH = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_MONTH + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_MONTH + ");";
    private static final String INDEX_EDITED = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_EDITED + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_EDITED + ");";
    private static final String INDEX_DATE_SENT = "CREATE INDEX " + TABLE_NAME + "_" + COLUMN_DATE_SENT + "_index" +
            " ON " + TABLE_NAME + "(" + COLUMN_DATE_SENT + ");";

    public MonthlyTalliesRepository(PathRepository pathRepository) {
        super(pathRepository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_QUERY);
        database.execSQL(INDEX_PROVIDER_ID);
        database.execSQL(INDEX_INDICATOR_ID);
        database.execSQL(INDEX_UPDATED_AT);
        database.execSQL(INDEX_MONTH);
        database.execSQL(INDEX_EDITED);
        database.execSQL(INDEX_DATE_SENT);
    }

    /**
     * Returns a list of all months that have corresponding daily tallies by unsent monthly tallies
     *
     * @return List of months with unsent monthly tallies
     */
    public List<Date> findAllUnsentMonths() {
        List<String> allTallyMonths = VaccinatorApplication.getInstance().dailyTalliesRepository()
                .findAllDistinctMonths(MONTH_FORMAT);
        Cursor cursor = null;
        try {
            if (allTallyMonths != null) {
                String monthsString = "";
                for (String curMonthString : allTallyMonths) {
                    if (!TextUtils.isEmpty(monthsString)) {
                        monthsString = monthsString + ", ";
                    }

                    monthsString = monthsString + "'" + curMonthString + "'";
                }

                String query = "SELECT DISTINCT " + COLUMN_MONTH +
                        " FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_DATE_SENT + " IS NOT NULL" +
                        " AND " + COLUMN_MONTH + " IN(" + monthsString + ")";
                cursor = getPathRepository().getReadableDatabase().rawQuery(query, null);

                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        String curMonth = MONTH_FORMAT.format(
                                new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_MONTH))));
                        allTallyMonths.remove(curMonth);
                    }
                }

                List<Date> unsentMonths = new ArrayList<>();
                for (String curMonthString : allTallyMonths) {
                    unsentMonths.add(MONTH_FORMAT.parse(curMonthString));
                }

                return unsentMonths;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return new ArrayList<>();
    }

    /**
     * Returns a list of draft monthly tallies corresponding to the provided month
     *
     * @param month The month to get the draft tallies for
     * @return
     */
    public List<MonthlyTally> findDrafts(Date month) {
        // Check if there exists any sent tally in the database for the month provided
        Cursor cursor = null;
        List<MonthlyTally> monthlyTallies = new ArrayList<>();
        try {
            cursor = getPathRepository().getReadableDatabase().query(TABLE_NAME, TABLE_COLUMNS,
                    COLUMN_MONTH + " = '" + MONTH_FORMAT.format(month) +
                            "' AND " + COLUMN_DATE_SENT + " IS NOT NULL",
                    null, null, null, null, null);
            List<MonthlyTally> tallies = readAllDataElements(cursor);

            if (tallies.size() == 0) {// No tallies generated yet
                Map<Long, List<DailyTally>> dailyTallies = VaccinatorApplication.getInstance()
                        .dailyTalliesRepository().findTalliesInMonth(month);
                for (List<DailyTally> curList : dailyTallies.values()) {
                    MonthlyTally curTally = addUpDailyTallies(curList);
                    if (curTally != null) {
                        monthlyTallies.add(curTally);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return monthlyTallies;
    }

    private MonthlyTally addUpDailyTallies(List<DailyTally> dailyTallies) {
        String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        MonthlyTally monthlyTally = null;
        double value = 0d;
        for (int i = 0; i < dailyTallies.size(); i++) {
            if (i == 0) {
                monthlyTally = new MonthlyTally();
                monthlyTally.setIndicator(dailyTallies.get(i).getIndicator());
            }

            value = value + Double.valueOf(dailyTallies.get(i).getValue());
        }

        if (monthlyTally != null) {
            monthlyTally.setUpdatedAt(Calendar.getInstance().getTime());
            monthlyTally.setValue(String.valueOf(Math.round(value)));
            monthlyTally.setProviderId(userName);
        }

        return monthlyTally;
    }

    public HashMap<String, ArrayList<MonthlyTally>> findAllSent(SimpleDateFormat dateFormat) {
        HashMap<String, ArrayList<MonthlyTally>> tallies = new HashMap<>();
        Cursor cursor = null;
        try {
            HashMap<Long, Hia2Indicator> indicatorMap = VaccinatorApplication.getInstance()
                    .hIA2IndicatorsRepository().findAll();
            cursor = getPathRepository().getReadableDatabase()
                    .query(TABLE_NAME, TABLE_COLUMNS,
                            COLUMN_DATE_SENT + " IS NOT NULL", null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    MonthlyTally curTally = extractMonthlyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        if (!tallies.containsKey(dateFormat.format(curTally.getMonth()))) {
                            tallies.put(dateFormat.format(curTally.getMonth()),
                                    new ArrayList<MonthlyTally>());
                        }

                        tallies.get(dateFormat.format(curTally.getMonth())).add(curTally);
                    }
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    public boolean save(MonthlyTally tally) {
        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        try {
            if (tally != null) {
                Long id = checkIfExists(tally.getIndicator().getId(), MONTH_FORMAT.format(tally.getMonth()));

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_INDICATOR_ID, tally.getIndicator().getId());
                cv.put(COLUMN_VALUE, tally.getValue());
                cv.put(COLUMN_PROVIDER_ID, tally.getProviderId());
                cv.put(COLUMN_MONTH, tally.getMonth().getTime());
                cv.put(COLUMN_DATE_SENT,
                        tally.getDateSent() == null ? null : tally.getDateSent().getTime());
                cv.put(COLUMN_EDITED, tally.isEdited() ? 1 : 0);

                if (id != null) {
                    database.update(TABLE_NAME, cv, COLUMN_ID + " = ?",
                            new String[]{id.toString()});
                } else {
                    database.insert(TABLE_NAME, null, cv);
                }

                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            database.endTransaction();
        }

        return false;
    }

    private List<MonthlyTally> readAllDataElements(Cursor cursor) {
        List<MonthlyTally> tallies = new ArrayList<>();
        HashMap<Long, Hia2Indicator> indicatorMap = VaccinatorApplication.getInstance()
                .hIA2IndicatorsRepository().findAll();

        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    MonthlyTally curTally = extractMonthlyTally(indicatorMap, cursor);
                    if (curTally != null) {
                        tallies.add(curTally);
                    }

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return tallies;
    }

    private MonthlyTally extractMonthlyTally(HashMap<Long, Hia2Indicator> indicatorMap, Cursor cursor) {
        long indicatorId = cursor.getLong(cursor.getColumnIndex(COLUMN_INDICATOR_ID));
        if (indicatorMap.containsKey(indicatorId)) {
            MonthlyTally curTally = new MonthlyTally();
            curTally.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            curTally.setProviderId(
                    cursor.getString(cursor.getColumnIndex(COLUMN_PROVIDER_ID)));
            curTally.setIndicator(indicatorMap.get(indicatorId));
            curTally.setValue(cursor.getString(cursor.getColumnIndex(COLUMN_VALUE)));
            curTally.setMonth(
                    new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_MONTH))));
            curTally.setEdited(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_EDITED)) == 0 ?
                            false : true
            );
            curTally.setDateSent(
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE_SENT)) == null ?
                            null : new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_SENT)))
            );
            curTally.setUpdatedAt(
                    new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)))
            );

            return curTally;
        }

        return null;
    }

    private Long checkIfExists(long indicatorId, String month) {
        Cursor mCursor = null;
        try {
            String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME +
                    " WHERE " + COLUMN_INDICATOR_ID + " = " + String.valueOf(indicatorId)
                    + " and " + COLUMN_MONTH + "='" + month + "'";
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

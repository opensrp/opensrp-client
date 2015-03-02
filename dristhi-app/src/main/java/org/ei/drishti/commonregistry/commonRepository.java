package org.ei.drishti.commonregistry;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;



import org.ei.drishti.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;

public class commonRepository extends DrishtiRepository {
    private String common_SQL = "CREATE TABLE common(id VARCHAR PRIMARY KEY,details VARCHAR)";
    public static final String ID_COLUMN = "id";
    public static final String Relational_ID = "relationalid";
    public static final String DETAILS_COLUMN = "details";
    public String TABLE_NAME = "common";
    public static final String[] common_TABLE_COLUMNS = new String[]{ID_COLUMN,Relational_ID,DETAILS_COLUMN};

    public commonRepository(String tablename) {
        super();
        TABLE_NAME = tablename;
        common_SQL = "CREATE TABLE "+ TABLE_NAME + "(id VARCHAR PRIMARY KEY,relationalid VARCHAR,details VARCHAR)";
    }

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(common_SQL);
    }

    public void add(PersonObject common) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(TABLE_NAME, null, createValuesFor(common));
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        PersonObject common = findByCaseID(caseId);
        if (common == null) {
            return;
        }

        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(details));
        database.update(TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public void mergeDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        PersonObject common = findByCaseID(caseId);
        if (common == null) {
            return;
        }

        Map<String, String> mergedDetails = new HashMap<String, String>(common.getDetails());
        mergedDetails.putAll(details);
        ContentValues valuesToUpdate = new ContentValues();
        valuesToUpdate.put(DETAILS_COLUMN, new Gson().toJson(mergedDetails));
        database.update(TABLE_NAME, valuesToUpdate, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<PersonObject> allcommon() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, common_TABLE_COLUMNS, null, null, null, null, null, null);
        return readAllcommon(cursor);
    }

    public List<PersonObject> findByCaseIDs(String... caseIds) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", TABLE_NAME, ID_COLUMN,
                insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAllcommon(cursor);
    }

    public PersonObject findByCaseID(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, common_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId},
                null, null, null, null);
        List<PersonObject> commons = readAllcommon(cursor);
        if (commons.isEmpty()) {
            return null;
        }
        return commons.get(0);
    }

    public long count() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + TABLE_NAME
                , new String[0]);
    }



    public void close(String caseId) {
//        ContentValues values = new ContentValues();
//        masterRepository.getWritableDatabase().update(EC_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(PersonObject common) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, common.getCaseId());
        values.put(Relational_ID, common.getRelationalId());
        values.put(DETAILS_COLUMN, new Gson().toJson(common.getDetails()));
        return values;
    }

    private List<PersonObject> readAllcommon(Cursor cursor) {
        cursor.moveToFirst();
        List<PersonObject> commons = new ArrayList<PersonObject>();
        while (!cursor.isAfterLast()) {
            PersonObject common = new PersonObject(cursor.getString(0),cursor.getString(1),new Gson().<Map<String, String>>fromJson(cursor.getString(2), new TypeToken<Map<String, String>>() {
                    }.getType()),TABLE_NAME);

            commons.add(common);
            cursor.moveToNext();
        }
        cursor.close();
        return commons;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }





    private List<Map<String, String>> readDetailsList(Cursor cursor) {
        cursor.moveToFirst();
        List<Map<String, String>> detailsList = new ArrayList<Map<String, String>>();
        while (!cursor.isAfterLast()) {
            String detailsJSON = cursor.getString(0);
            detailsList.add(new Gson().<Map<String, String>>fromJson(detailsJSON, new TypeToken<HashMap<String, String>>() {
            }.getType()));
            cursor.moveToNext();
        }
        cursor.close();
        return detailsList;
    }
}

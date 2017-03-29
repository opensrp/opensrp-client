package org.ei.opensrp.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.List;

/**
 + * Created by Dimas on 9/7/2015.
 + */
public class UniqueIdRepository extends DrishtiRepository {

    private static final String UNIQUE_ID_SQL = "CREATE TABLE unique_id(id INTEGER PRIMARY KEY AUTOINCREMENT, uniqueId Varchar)";

    private static final String UNIQUE_ID_TABLE_NAME = "unique_id";
    private static final String UNIQUE_ID_COLUMN = "uniqueId";
    public static final String[] UNIQUE_ID_COLUMNS = {"id", UNIQUE_ID_COLUMN};

    public static final String[] UNIQUE_ID_TABLE_COLUMNS = new String[] {UNIQUE_ID_COLUMN};

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(UNIQUE_ID_SQL);
    }

    public void saveUniqueId(String uniqueId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UNIQUE_ID_COLUMN, uniqueId);
        database.insert(UNIQUE_ID_TABLE_NAME, null, values);
    }

    public String getUniqueIdFromLastUsedId(String lastUsedId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + UNIQUE_ID_COLUMN +
                    " FROM " + UNIQUE_ID_TABLE_NAME +
                    " WHERE " + UNIQUE_ID_COLUMN + " > ?" +
                    " ORDER BY " + UNIQUE_ID_COLUMN + " ASC  " +
                    " LIMIT 1", new String[]{lastUsedId});
        String uniqueId = null;
        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                    uniqueId = cursor.getString(0);
                }
            cursor.close();
        }
        return uniqueId;
    }

    public List<Long> getAllUniqueId() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT id, " + UNIQUE_ID_COLUMN +
                    " FROM " + UNIQUE_ID_TABLE_NAME , new String[]{});
        cursor.moveToFirst();
        List<Long> uids = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            uids.add(cursor.getLong(1));
            cursor.moveToNext();
        }
        cursor.close();
        return uids;
    }


}
package org.ei.opensrp.repository;

import android.content.ContentValues;
import android.database.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class SettingsRepository extends DrishtiRepository {
    static final String SETTINGS_SQL = "CREATE TABLE settings(key VARCHAR PRIMARY KEY, value BLOB)";
    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(SETTINGS_SQL);
    }

    public void updateSetting(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public void updateBLOB(String key, byte[] value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public String querySetting(String key, String defaultValue) {
        Cursor cursor = getCursor(key);
        if (cursor == null) {
            return defaultValue;
        }

        String value = cursor.getString(0);
        cursor.close();
        return value;
    }

    public byte[] queryBLOB(String key) {
        Cursor cursor = getCursor(key);
        if (cursor == null) {
            return null;
        }

        byte[] value = cursor.getBlob(0);
        cursor.close();
        return value;
    }

    private void replace(ContentValues values) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.replace(SETTINGS_TABLE_NAME, null, values);
    }

    private Cursor getCursor(String key) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN}, SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}

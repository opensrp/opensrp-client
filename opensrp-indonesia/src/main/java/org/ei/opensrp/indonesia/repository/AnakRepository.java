package org.ei.opensrp.indonesia.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.ei.opensrp.indonesia.repository.IbuRepository.IBU_TABLE_COLUMNS;
import static org.ei.opensrp.indonesia.repository.IbuRepository.IBU_TABLE_NAME;
import static org.ei.opensrp.indonesia.repository.KartuIbuRepository.KI_TABLE_COLUMNS;
import static org.ei.opensrp.indonesia.repository.KartuIbuRepository.KI_TABLE_NAME;

/**
 * Created by Dimas Ciputra on 4/7/15.
 */
public class AnakRepository extends DrishtiRepository {

    private static final String ANAK_SQL = "CREATE TABLE anak(id VARCHAR PRIMARY KEY, ibuCaseId VARCHAR, tanggalLahirAnak VARCHAR, jenisKelamin VARCHAR, details VARCHAR, isClosed VARCHAR, photoPath VARCHAR)";
    public static final String ANAK_TABLE_NAME = "anak";
    private static final String ID_COLUMN = "id";
    private static final String IBU_ID_COLUMN = "ibuCaseId";
    private static final String DATE_OF_BIRTH_COLUMN = "tanggalLahirAnak";
    private static final String GENDER_COLUMN = "jenisKelamin";
    private static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String PHOTO_PATH_COLUMN = "photoPath";
    public static final String[] ANAK_TABLE_COLUMNS = {ID_COLUMN, IBU_ID_COLUMN, DATE_OF_BIRTH_COLUMN, GENDER_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
    public static final String NOT_CLOSED = "false";
    public static final String ANAK_NAME = "namaBayi";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(ANAK_SQL);
    }

    public void add(Anak anak) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(ANAK_TABLE_NAME, null, createValuesFor(anak));
    }

    public void update(Anak child) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.update(ANAK_TABLE_NAME, createValuesFor(child), ID_COLUMN + " = ?", new String[]{child.getCaseId()});
    }

    public List<Anak> all() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(ANAK_TABLE_NAME, ANAK_TABLE_COLUMNS, IS_CLOSED_COLUMN + " = ?", new String[]{NOT_CLOSED}, null, null, null, null);
        return readAll(cursor);
    }

    public Anak find(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(ANAK_TABLE_NAME, ANAK_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        List<Anak> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }

    public List<Anak> findAnakByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", ANAK_TABLE_NAME, ID_COLUMN, insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DETAILS_COLUMN, new Gson().toJson(details));
        database.update(ANAK_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<Anak> findByIbuCaseId(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(ANAK_TABLE_NAME, ANAK_TABLE_COLUMNS, IBU_ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        return readAll(cursor);
    }

    public long count() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + ANAK_TABLE_NAME + " WHERE " + IS_CLOSED_COLUMN + " = '" + NOT_CLOSED + "'", new String[0]);
    }

    public void close(String caseId) {
        markAsClosed(caseId);
    }

    public List<Anak> allAnakWithIbuAndKI() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " +
                        tableColumnsForQuery(ANAK_TABLE_NAME, ANAK_TABLE_COLUMNS) + ", " +
                        tableColumnsForQuery(IBU_TABLE_NAME, IBU_TABLE_COLUMNS) + ", " +
                        tableColumnsForQuery(KI_TABLE_NAME, KI_TABLE_COLUMNS) +
                        " FROM " + ANAK_TABLE_NAME + ", " + IBU_TABLE_NAME + ", " + KI_TABLE_NAME +
                        " WHERE " + ANAK_TABLE_NAME + "." + IS_CLOSED_COLUMN + "= '" + NOT_CLOSED + "' AND " +
                        ANAK_TABLE_NAME + "." + IBU_ID_COLUMN + " = " + IBU_TABLE_NAME + "." + IbuRepository.ID_COLUMN
                        + " AND " + IBU_TABLE_NAME + "." + IbuRepository.KI_ID_COLUMN + " = " + KI_TABLE_NAME + "." + KartuIbuRepository.ID_COLUMN,
                null);
        return readAllAnakWithIbuAndKI(cursor);
    }

    private Anak anakFromCursor(Cursor cursor) {
        return new Anak(
                getColumnValueByAlias(cursor, ANAK_TABLE_NAME, ID_COLUMN),
                getColumnValueByAlias(cursor, ANAK_TABLE_NAME, IBU_ID_COLUMN),
                getColumnValueByAlias(cursor, ANAK_TABLE_NAME, DATE_OF_BIRTH_COLUMN),
                getColumnValueByAlias(cursor, ANAK_TABLE_NAME, GENDER_COLUMN),
                new Gson().<Map<String, String>>fromJson(getColumnValueByAlias(cursor, ANAK_TABLE_NAME, DETAILS_COLUMN), new TypeToken<Map<String, String>>() {
                }.getType()))
                .setIsClosed(Boolean.valueOf(getColumnValueByAlias(cursor, ANAK_TABLE_NAME, IS_CLOSED_COLUMN)))
                .withPhotoPath(getColumnValueByAlias(cursor, ANAK_TABLE_NAME, PHOTO_PATH_COLUMN));
    }

    private KartuIbu kiFromCursor(Cursor cursor) {
        return new KartuIbu(
                getColumnValueByAlias(cursor, KI_TABLE_NAME, KartuIbuRepository.ID_COLUMN),
                new Gson().<Map<String, String>>fromJson(getColumnValueByAlias(cursor, KI_TABLE_NAME, KartuIbuRepository.DETAILS_COLUMN), new TypeToken<Map<String, String>>() {
                }.getType()),
                getColumnValueByAlias(cursor, KI_TABLE_NAME, KartuIbuRepository.DUSUN_COLUMN));
    }

    private Ibu ibuFromCursor(Cursor cursor) {
        return new Ibu(
                getColumnValueByAlias(cursor, IBU_TABLE_NAME, IbuRepository.ID_COLUMN),
                getColumnValueByAlias(cursor, IBU_TABLE_NAME, IbuRepository.KI_ID_COLUMN),
                getColumnValueByAlias(cursor, IBU_TABLE_NAME, IbuRepository.REF_DATE_COLUMN))
                .withDetails(new Gson().<Map<String, String>>fromJson(getColumnValueByAlias(cursor, IBU_TABLE_NAME, IbuRepository.DETAILS_COLUMN), new TypeToken<Map<String, String>>() {
                }.getType()));
    }

    private String getColumnValueByAlias(Cursor cursor, String table, String column) {
        return cursor.getString(cursor.getColumnIndex(table + column));
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return StringUtils.join(prepend(tableColumns, tableName), ", ");
    }

    private String[] prepend(String[] input, String tableName) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = tableName + "." + input[index] + " as " + tableName + input[index];
        }
        return output;
    }

    private void markAsClosed(String caseId) {
        ContentValues values = new ContentValues();
        values.put(IS_CLOSED_COLUMN, TRUE.toString());
        masterRepository.getWritableDatabase().update(ANAK_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(Anak anak) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, anak.getCaseId());
        values.put(IBU_ID_COLUMN, anak.getIbuCaseId());
        values.put(DATE_OF_BIRTH_COLUMN, anak.getDateOfBirth());
        values.put(GENDER_COLUMN, anak.getGender());
        values.put(DETAILS_COLUMN, new Gson().toJson(anak.getDetails()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(anak.isClosed()));
        values.put(PHOTO_PATH_COLUMN, anak.getPhotoPath());
        return values;
    }

    private List<Anak> readAllAnakWithIbuAndKI(Cursor cursor) {
        cursor.moveToFirst();
        List<Anak> children = new ArrayList<Anak>();
        while (!cursor.isAfterLast()) {
            children.add(anakFromCursor(cursor)
                    .withIbu(ibuFromCursor(cursor))
                    .withKI(kiFromCursor(cursor)));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private List<Anak> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Anak> children = new ArrayList<Anak>();
        while (!cursor.isAfterLast()) {
            children.add(new Anak(
                            cursor.getString(cursor.getColumnIndex(ID_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(IBU_ID_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(DATE_OF_BIRTH_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(GENDER_COLUMN)),
                            new Gson().<Map<String, String>>fromJson(cursor.getString(cursor.getColumnIndex(DETAILS_COLUMN)),
                                    new TypeToken<Map<String, String>>() {
                                    }.getType()))
                            .setIsClosed(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IS_CLOSED_COLUMN))))
                            .withPhotoPath(cursor.getString(cursor.getColumnIndex(PHOTO_PATH_COLUMN)))
            );
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private List<Anak> readAllAnak(Cursor cursor) {
        cursor.moveToFirst();
        List<Anak> children = new ArrayList<Anak>();
        while (!cursor.isAfterLast()) {
            children.add(anakFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public List<Anak> findAllByKIId(String kiId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " +
                tableColumnsForQuery(ANAK_TABLE_NAME, ANAK_TABLE_COLUMNS) +
                " FROM " + ANAK_TABLE_NAME + ", " + IBU_TABLE_NAME + ", " + KI_TABLE_NAME +
                " WHERE " + ANAK_TABLE_NAME + "." + IS_CLOSED_COLUMN + "= '" + NOT_CLOSED + "' AND " +
                ANAK_TABLE_NAME + "." + IBU_ID_COLUMN + " = " + IBU_TABLE_NAME + "." + IbuRepository.ID_COLUMN
                + " AND " + IBU_TABLE_NAME + "." + IbuRepository.KI_ID_COLUMN + " = " + KI_TABLE_NAME + "." +
                KartuIbuRepository.ID_COLUMN + " AND " + KI_TABLE_NAME + "." + KartuIbuRepository.ID_COLUMN +
                "= ? ", new String[]{kiId});
        return readAllAnak(cursor);
    }

}

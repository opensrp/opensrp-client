package org.ei.opensrp.indonesia.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.Context;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.ei.opensrp.indonesia.repository.KartuIbuRepository.KI_TABLE_COLUMNS;
import static org.ei.opensrp.indonesia.repository.KartuIbuRepository.KI_TABLE_NAME;

/**
 * Created by Dimas Ciputra on 3/3/15.
 */
public class IbuRepository extends DrishtiRepository {
    private static final String IBU_SQL = "CREATE TABLE ibu(id VARCHAR PRIMARY KEY, kartuIbuId VARCHAR, type VARCHAR, referenceDate VARCHAR, details VARCHAR, isClosed VARCHAR, bidanId VARCHAR)";
    private static final String IBU_TYPE_INDEX_SQL = "CREATE INDEX ibu_type_index ON ibu(type);";
    private static final String IBU_REFERENCE_DATE_INDEX_SQL = "CREATE INDEX ibu_referenceDate_index ON ibu(referenceDate);";
    public static final String IBU_TABLE_NAME = "ibu";
    public static final String ID_COLUMN = "id";
    public static final String KI_ID_COLUMN = "kartuIbuId";
    private static final String TYPE_COLUMN = "type";
    public static final String REF_DATE_COLUMN = "referenceDate";
    public static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    private static final String BIDAN_ID = "bidanId";
    public static final String[] IBU_TABLE_COLUMNS = {ID_COLUMN, KI_ID_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, BIDAN_ID};

    public static final String TYPE_ANC = "anc";
    private static final String NOT_CLOSED = "false";
    public static final String TYPE_PNC = "pnc";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(IBU_SQL);
        database.execSQL(IBU_TYPE_INDEX_SQL);
        database.execSQL(IBU_REFERENCE_DATE_INDEX_SQL);
    }

    public void add(Ibu ibu) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(IBU_TABLE_NAME, null, createValuesFor(ibu, TYPE_ANC));
    }

    public void switchToPNC(String caseId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        ContentValues ibuValuesToBeUpdated = new ContentValues();
        ibuValuesToBeUpdated.put(TYPE_COLUMN, TYPE_PNC);

        database.update(IBU_TABLE_NAME, ibuValuesToBeUpdated, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public void switchToANC(String caseId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        ContentValues ibuValuesToBeUpdated = new ContentValues();
        ibuValuesToBeUpdated.put(TYPE_COLUMN, TYPE_ANC);

        database.update(IBU_TABLE_NAME, ibuValuesToBeUpdated, ID_COLUMN + " = ?", new String[]{caseId});
    }

    public List<Ibu> allANCs() {
        return allWithType(TYPE_ANC);
    }

    public List<Ibu> allPNCs() {
        return allWithType(TYPE_PNC);
    }

    public List<Ibu> allWithType(String  type) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(IBU_TABLE_NAME, IBU_TABLE_COLUMNS, TYPE_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?", new String[]{type, NOT_CLOSED}, null, null, null, null);
        return readAll(cursor);
    }

    public Ibu findOpenCaseByCaseID(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(IBU_TABLE_NAME, IBU_TABLE_COLUMNS, ID_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?", new String[]{caseId, NOT_CLOSED}, null, null, null, null);
        List<Ibu> mothers = readAll(cursor);

        if (mothers.isEmpty()) {
            return null;
        }
        return mothers.get(0);
    }

    public Ibu findIbuWithOpenStatusByKIId(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(IBU_TABLE_NAME, IBU_TABLE_COLUMNS, KI_ID_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?", new String[]{caseId, NOT_CLOSED}, null, null, null, null);
        List<Ibu> mothers = readAll(cursor);
        return mothers.isEmpty() ? null : mothers.get(0);
    }

    public Ibu findById(String id) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(IBU_TABLE_NAME, IBU_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{id},
                null, null, null, null);
        return readAll(cursor).get(0);
    }

    public List<Ibu> findByCaseIds(String... caseIds) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", IBU_TABLE_NAME, ID_COLUMN, insertPlaceholdersForInClause(caseIds.length)), caseIds);
        return readAll(cursor);
    }

    public List<Pair<Ibu, KartuIbu>> allIbuOfATypeWithKartuIbu(String type) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT " + tableColumnsForQuery(IBU_TABLE_NAME, IBU_TABLE_COLUMNS) + ", " + tableColumnsForQuery(KI_TABLE_NAME, KI_TABLE_COLUMNS) +
                " FROM " + IBU_TABLE_NAME + ", " + KI_TABLE_NAME +
                " WHERE " + TYPE_COLUMN + "='" + type +
                "' AND " + IBU_TABLE_NAME + "." + IS_CLOSED_COLUMN + "= '" + NOT_CLOSED + "' AND " +
                IBU_TABLE_NAME + "." + KI_ID_COLUMN + " = " + KI_TABLE_NAME + "." + KartuIbuRepository.ID_COLUMN, null);
        return readAllIbuWithKartuIbu(cursor);
    }

    private List<Pair<Ibu, KartuIbu>> readAllIbuWithKartuIbu(Cursor cursor) {
        cursor.moveToFirst();
        List<Pair<Ibu, KartuIbu>> ancOrPncsWithKartuIbu = new ArrayList<Pair<Ibu, KartuIbu>>();
        while (!cursor.isAfterLast()) {
            Ibu ibu = new Ibu(cursor.getString(0), cursor.getString(1), cursor.getString(2))
                    .setIsClosed(Boolean.valueOf(cursor.getString(5)))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN)))
                    .withDetails(new Gson().<Map<String, String>>fromJson(cursor.getString(4),
                            new TypeToken<Map<String, String>>() {}.getType()));

            KartuIbu kartuIbu = new KartuIbu(cursor.getString(7),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(8), new TypeToken<Map<String, String>>() {
                    }.getType()), cursor.getString(9));

            ancOrPncsWithKartuIbu.add(Pair.of(ibu, kartuIbu));
            cursor.moveToNext();
        }
        cursor.close();
        return ancOrPncsWithKartuIbu;
    }

    public List<Ibu> findAllCasesForKartuIbu(String kartuIbuid) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(IBU_TABLE_NAME, IBU_TABLE_COLUMNS, KI_ID_COLUMN + " = ?", new String[]{kartuIbuid}, null, null, null, null);
        return readAll(cursor);
    }

    public void update(Ibu ibu) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.update(IBU_TABLE_NAME, createValuesFor(ibu, TYPE_ANC), ID_COLUMN + " = ?", new String[]{ibu.getId()});
    }

    public void close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(IS_CLOSED_COLUMN, TRUE.toString());
        masterRepository.getWritableDatabase().update(IBU_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return join(prepend(tableColumns, tableName + "."), ", ");
    }

    private String[] prepend(String[] input, String textToPrepend) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = textToPrepend + input[index];
        }
        return output;
    }

    public long ancCount() {
        return DatabaseUtils.longForQuery(masterRepository.getReadableDatabase(),
                "SELECT COUNT(1) FROM " + IBU_TABLE_NAME + " WHERE  " + TYPE_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ? ",
                new String[]{TYPE_ANC, NOT_CLOSED});
    }

    public long pncCount() {
        return DatabaseUtils.longForQuery(masterRepository.getReadableDatabase(),
                "SELECT COUNT(1) FROM " + IBU_TABLE_NAME + " WHERE " + TYPE_COLUMN + " = ? AND " + IS_CLOSED_COLUMN
        + " = ?", new String[]{TYPE_PNC, NOT_CLOSED});
    }

    /*
        Private Methods
     */

    private ContentValues createValuesFor(Ibu ibu, String type) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, ibu.getId());
        values.put(KI_ID_COLUMN, ibu.getKartuIbuId());
        values.put(TYPE_COLUMN, type);
        values.put(REF_DATE_COLUMN, ibu.getReferenceDate());
        values.put(DETAILS_COLUMN, new Gson().toJson(ibu.getDetails()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(ibu.isClosed()));
        values.put(BIDAN_ID, Context.getInstance().allSharedPreferences().fetchRegisteredANM());
        return values;
    }

    private List<Ibu> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Ibu> ibus = new ArrayList<Ibu>();
        while (!cursor.isAfterLast()) {
            Map<String, String> details = new Gson().fromJson(cursor.getString(4), new TypeToken<Map<String, String>>() {
            }.getType());

            ibus.add(new Ibu(cursor.getString(0), cursor.getString(1), cursor.getString(3))
                    .withDetails(details)
                    .setIsClosed(Boolean.valueOf(cursor.getString(5)))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return ibus;
    }

    public boolean isPregnant(String kiId) {
        return longForQuery(masterRepository.getReadableDatabase(),
                "SELECT COUNT(1) FROM " + IBU_TABLE_NAME
                        + " WHERE " +
                        KI_ID_COLUMN + " = ? AND " +
                        IS_CLOSED_COLUMN + " = ? AND " +
                        TYPE_COLUMN + " = ?",
                new String[]{kiId, NOT_CLOSED, TYPE_ANC}) > 0;
    }

}

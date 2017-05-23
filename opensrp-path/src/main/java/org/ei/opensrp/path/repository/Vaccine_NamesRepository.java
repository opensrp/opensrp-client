package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.path.domain.Vaccine_names;
import org.ei.opensrp.path.domain.Vaccine_types;
import org.ei.opensrp.service.AlertService;

import java.util.ArrayList;
import java.util.List;

public class Vaccine_NamesRepository extends BaseRepository {
    private static final String TAG = Vaccine_typesRepository.class.getCanonicalName();
    private static final String VACCINE_Names_SQL = "CREATE TABLE Vaccines_names (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,name VARCHAR NOT NULL,vaccine_type_id VARCHAR NULL,reference_vaccine_id VARCHAR NULL,due_days VARCHAR ,Client_type VARCHAR ,Dose_no VARCHAR)";
    public static final String VACCINE_Names_TABLE_NAME = "Vaccines_names";
    public static final String ID_COLUMN = "_id";
    public static final String NAME = "name";
    public static final String VACCINE_TYPE_ID = "vaccine_type_id";
    public static final String REFERENCE_VACCINE_ID = "reference_vaccine_id";
    public static final String DUE_DAYS = "due_days";
    public static final String CLIENT_TYPE = "Client_type";
    public static final String DOSE_NO = "Dose_no";

    public static final String[] VACCINE_Names_TABLE_COLUMNS = {ID_COLUMN,  NAME,VACCINE_TYPE_ID, REFERENCE_VACCINE_ID, DUE_DAYS, CLIENT_TYPE, DOSE_NO};



    private CommonFtsObject commonFtsObject;
    private AlertService alertService;

    public Vaccine_NamesRepository(PathRepository pathRepository, CommonFtsObject commonFtsObject, AlertService alertService) {
        super(pathRepository);
        this.commonFtsObject = commonFtsObject;
        this.alertService = alertService;
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(VACCINE_Names_SQL);
    }

    public void add(Vaccine_names vaccine_names) {
        if (vaccine_names == null) {
            return;
        }




        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        if (vaccine_names.getId() == null) {
            vaccine_names.setId(database.insert(VACCINE_Names_TABLE_NAME, null, createValuesFor(vaccine_names)));
        } else {
            //mark the vaccine as unsynced for processing as an updated event

            String idSelection = ID_COLUMN + " = ?";
            database.update(VACCINE_Names_SQL, createValuesFor(vaccine_names), idSelection, new String[]{vaccine_names.getId().toString()});
        }
    }




//    public List<Vaccine> findByEntityId(String entityId) {
//        SQLiteDatabase database = getPathRepository().getReadableDatabase();
//        Cursor cursor = database.query(VACCINE_TABLE_NAME, VACCINE_TABLE_COLUMNS, BASE_ENTITY_ID + " = ? ORDER BY " + UPDATED_AT_COLUMN, new String[]{entityId}, null, null, null, null);
//        return readAllVaccines(cursor);
//    }



//    public void deleteVaccine(Long caseId) {
//        Vaccine vaccine = find(caseId);
//        if(vaccine != null) {
//            getPathRepository().getWritableDatabase().delete(VACCINE_TABLE_NAME, ID_COLUMN + "= ?", new String[]{caseId.toString()});
//
//            updateFtsSearch(vaccine.getBaseEntityId(), vaccine.getName());
//        }
//    }

//    public void close(Long caseId) {
//        ContentValues values = new ContentValues();
//        values.put(SYNC_STATUS, TYPE_Synced);
//        getPathRepository().getWritableDatabase().update(VACCINE_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId.toString()});
//    }

    private List<Vaccine_names> readAllVaccines(Cursor cursor) {
        List<Vaccine_names> vaccines = new ArrayList<Vaccine_names>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    vaccines.add(
                            new Vaccine_names(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                                    cursor.getString(cursor.getColumnIndex(NAME)),
                                    cursor.getString(cursor.getColumnIndex(VACCINE_TYPE_ID)),
                                    cursor.getString(cursor.getColumnIndex(DUE_DAYS)),
                                    cursor.getString(cursor.getColumnIndex(REFERENCE_VACCINE_ID)),
                                    cursor.getString(cursor.getColumnIndex(CLIENT_TYPE)),
                                    cursor.getString(cursor.getColumnIndex(DOSE_NO))
                            )
                    );

                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return vaccines;
    }


    private ContentValues createValuesFor(Vaccine_names vaccine_names) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, vaccine_names.getId());
        values.put(NAME, vaccine_names.getName());
        values.put(VACCINE_TYPE_ID, vaccine_names.getVaccine_type_id());
        values.put(REFERENCE_VACCINE_ID, vaccine_names.getReference_vaccine_id());
        values.put(DUE_DAYS, vaccine_names.getDue_days());
        values.put(CLIENT_TYPE,vaccine_names.getClient_type());
        values.put(DUE_DAYS, vaccine_names.getDue_days());
        values.put(DOSE_NO, vaccine_names.getDose_no());
        return values;
    }


}

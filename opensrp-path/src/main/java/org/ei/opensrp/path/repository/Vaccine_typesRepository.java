package org.ei.opensrp.path.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.domain.Vaccine_names;
import org.ei.opensrp.path.domain.Vaccine_types;
import org.ei.opensrp.service.AlertService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Vaccine_typesRepository extends BaseRepository {
    private static final String TAG = Vaccine_NamesRepository.class.getCanonicalName();
    private static final String VACCINE_Types_SQL = "CREATE TABLE vaccine_types (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,doses INTEGER,name VARCHAR NOT NULL,openmrs_parent_entity_id VARCHAR NULL,openmrs_date_concept_id VARCHAR NULL,openmrs_dose_concept_id VARCHAR)";
    public static final String VACCINE_Types_TABLE_NAME = "vaccine_types";
    public static final String ID_COLUMN = "_id";
    public static final String DOSES = "doses";
    public static final String NAME = "name";
    public static final String OPENMRS_PARENT_ENTITIY_ID = "openmrs_parent_entity_id";
    public static final String OPENMRS_DATE_CONCEPT_ID = "openmrs_date_concept_id";
    public static final String OPENMRS_DOSE_CONCEPT_ID = "openmrs_dose_concept_id";

    public static final String[] VACCINE_Types_TABLE_COLUMNS = {ID_COLUMN, DOSES, NAME, OPENMRS_PARENT_ENTITIY_ID, OPENMRS_DATE_CONCEPT_ID, OPENMRS_DOSE_CONCEPT_ID};



    private CommonFtsObject commonFtsObject;
    private AlertService alertService;

    public Vaccine_typesRepository(PathRepository pathRepository, CommonFtsObject commonFtsObject, AlertService alertService) {
        super(pathRepository);
        this.commonFtsObject = commonFtsObject;
        this.alertService = alertService;
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(VACCINE_Types_SQL);
    }

    public void add(Vaccine_types vaccine_types) {
        if (vaccine_types == null) {
            return;
        }




        SQLiteDatabase database = getPathRepository().getWritableDatabase();
        if (vaccine_types.getId() == null) {
            vaccine_types.setId(database.insert(VACCINE_Types_TABLE_NAME, null, createValuesFor(vaccine_types)));
        } else {
            //mark the vaccine as unsynced for processing as an updated event

            String idSelection = ID_COLUMN + " = ?";
            database.update(VACCINE_Types_TABLE_NAME, createValuesFor(vaccine_types), idSelection, new String[]{vaccine_types.getId().toString()});
        }
    }




    public List<Vaccine_types> findIDByName(String Name) {
        SQLiteDatabase database = getPathRepository().getReadableDatabase();
        Cursor cursor = database.query(VACCINE_Types_TABLE_NAME, VACCINE_Types_TABLE_COLUMNS, this.NAME + " = ? ", new String[]{Name}, null, null, null, null);
        return readAllVaccines(cursor);
    }
    public List<Vaccine_types> getAllVaccineTypes() {
        SQLiteDatabase database = getPathRepository().getReadableDatabase();
        Cursor cursor = database.query(VACCINE_Types_TABLE_NAME, VACCINE_Types_TABLE_COLUMNS,null,null, null, null, null, null);
        return readAllVaccines(cursor);
    }
    public int getDosesPerVial(String name){
        int dosespervaccine = 1;
        ArrayList<Vaccine_types> vaccine_types = (ArrayList<Vaccine_types>) findIDByName(name);
        for(int i = 0;i<vaccine_types.size();i++){
            dosespervaccine = vaccine_types.get(0).getDoses();
        }
        return dosespervaccine;
    }



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

    private List<Vaccine_types> readAllVaccines(Cursor cursor) {
        List<Vaccine_types> vaccines = new ArrayList<Vaccine_types>();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    vaccines.add(
                            new Vaccine_types(cursor.getLong(cursor.getColumnIndex(ID_COLUMN)),
                                    cursor.getInt(cursor.getColumnIndex(DOSES)),
                                    cursor.getString(cursor.getColumnIndex(NAME)),
                                    cursor.getString(cursor.getColumnIndex(OPENMRS_PARENT_ENTITIY_ID)),
                                    cursor.getString(cursor.getColumnIndex(OPENMRS_DATE_CONCEPT_ID)),
                                    cursor.getString(cursor.getColumnIndex(OPENMRS_DOSE_CONCEPT_ID))
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


    private ContentValues createValuesFor(Vaccine_types vaccine_types) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, vaccine_types.getId());
        values.put(NAME, vaccine_types.getName());
        values.put(DOSES, vaccine_types.getDoses());
        values.put(OPENMRS_DATE_CONCEPT_ID,vaccine_types.getOpenmrs_date_concept_id());
        values.put(OPENMRS_DOSE_CONCEPT_ID, vaccine_types.getOpenmrs_dose_concept_id());
        values.put(OPENMRS_PARENT_ENTITIY_ID, vaccine_types.getOpenmrs_parent_entity_id());
        return values;
    }
}

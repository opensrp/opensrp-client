package org.ei.opensrp.vaccinator.repository;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.repository.DrishtiRepository;

import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 17-Nov-15.
 */
public class ExtendedCommonRepositoryForField extends DrishtiRepository {


    public ExtendedCommonRepositoryForField() {
        super();
    }


    @Override
    protected void onCreate(SQLiteDatabase database) {

    }


    public List<CommonPersonObject> findDailyDataByMonth(String month) {
/*
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", "field", "date",
                insertPlaceholdersForInClause(caseIds.length)), caseIds);*//*
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", "field", "date",
                insertPlaceholdersForInClause(caseIds.length)), caseIds);*/
    return null;
    }

    public void calculateTotalUsedByMonth(String monthYear){
        SQLiteDatabase database=masterRepository.getReadableDatabase();
        Cursor cursor=database.rawQuery("select * from ec_child where date like "+monthYear+"%",null);

        if(cursor.isAfterLast())
        {


        }
    }



}

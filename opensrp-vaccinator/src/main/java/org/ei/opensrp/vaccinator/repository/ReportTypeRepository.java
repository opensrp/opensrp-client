package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;
import org.ei.opensrp.vaccinator.domain.ReportType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class ReportTypeRepository extends DrishtiRepository {
    private static final String ID_COLUMN="_id";
    private static final String TYPE_COLUMN="type";
    private static final String TABLE_NAME="report";
    private static final String[] COLUMNS={ID_COLUMN,TYPE_COLUMN};
    private static String REPORT_TABLE="create table report (_id int primary key , type varchar )";

    @Override
    protected void onCreate(SQLiteDatabase database) {
    database.execSQL(REPORT_TABLE);
    }

    public long add(String name){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TYPE_COLUMN, name);
        return database.insert(TABLE_NAME,null ,contentValues);


    }

    public ReportType getReportTypeById(int id){

        SQLiteDatabase database = masterRepository.getWritableDatabase();

        // database.
        Cursor cursor=database.query(TABLE_NAME, COLUMNS,"id=?" ,new String[]{String.valueOf(id)},null,null,null);
        return readAll(cursor).get(0);
    }

    public List<ReportType> getAllReportTypes(){
        List<ReportType> list=new ArrayList<ReportType>();
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        // database.
        Cursor cursor=database.rawQuery("Select * from report;" , null);

        return readAll(cursor);

    }

    private List<ReportType> readAll(android.database.Cursor cursor) {
        cursor.moveToFirst();
        List<ReportType> children = new ArrayList<ReportType>();
        while (!cursor.isAfterLast()) {

            children.add(new ReportType(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(TYPE_COLUMN) )
                     ));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }


}

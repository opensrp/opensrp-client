package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;
import org.ei.opensrp.vaccinator.domain.Vaccine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class VaccineRepository extends DrishtiRepository {
    private static final String VACCINE_COLUMN="vaccine_name";
    private static final String ID_COLUMN="_id";
    public static final String TABLE_NAME="vaccine";
    private static final String[] COLUMNS={ID_COLUMN,VACCINE_COLUMN};
    private static String VACCINE_TABLE="CREATE TABLE vaccine(_id int primary key , vaccine_name VARCHAR(100) )";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(VACCINE_TABLE);
    }

    public long add(String name){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VACCINE_COLUMN, name);
        return database.insert(TABLE_NAME,null ,contentValues);


    }

    public List<Vaccine> getAllVaccines(){

        SQLiteDatabase database = masterRepository.getWritableDatabase();

       // database.
        Cursor cursor=database.query(TABLE_NAME, COLUMNS,null ,null,null,null,null);
        return readAll(cursor);

    }

    public Vaccine getVaccineById(int id){

        SQLiteDatabase database = masterRepository.getWritableDatabase();

        // database.
        Cursor cursor=database.query(TABLE_NAME, COLUMNS,"id=?" ,new String[]{String.valueOf(id)},null,null,null);
        return readAll(cursor).get(0);
    }

    private List<Vaccine> readAll(android.database.Cursor cursor) {
        cursor.moveToFirst();
        List<Vaccine> children = new ArrayList<Vaccine>();
        while (!cursor.isAfterLast()) {

            children.add(new Vaccine(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(VACCINE_COLUMN) )
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }


}

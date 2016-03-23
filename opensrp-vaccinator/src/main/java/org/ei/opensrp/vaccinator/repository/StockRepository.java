package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;
import org.ei.opensrp.vaccinator.domain.Stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class StockRepository extends DrishtiRepository {
    private  final static String ID_COLUMN="_id";
    private  final static String WASTED_COLUMN="wasted";
    private  final static String RECEIVED_COLUMN="received";
    private  final static String BALANCE_INHAND_COLUMN="balance";
    private  final static String USE_COLUMN="used";
    private  final static String VID_COLUMN="v_id";
    private  final static String REPORT_ID_COLUMN="report_id";
    private final static String EVENTDATE_COLUMN ="eventdate";
    private final static String TARGET_COLUMN ="target";
    private static String[] COLUMNS={ID_COLUMN,VID_COLUMN,WASTED_COLUMN,RECEIVED_COLUMN,BALANCE_INHAND_COLUMN,USE_COLUMN,EVENTDATE_COLUMN ,TARGET_COLUMN};
    private  final static String TABLE_NAME="stock";

    private  final static String STOCK_TABLE="create table stock (_id int primary key ,v_id int not null ,report_id int not null,wasted int , received int , balance int ,used int ,eventdate varchar , int target)";


    @Override
    protected void onCreate(SQLiteDatabase database) {
    database.execSQL(STOCK_TABLE);
    }

    /**
     * adding stock by using variables
     * @param vaccine_id
     * @param reportId
     * @param wasted
     * @param received
     * @param balance
     * @param used
     * @param eventDate
     * @return Long
     */
    public long add(int vaccine_id ,int reportId , int wasted ,int received , int balance , int used ,int target,String eventDate ){
        try{
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VID_COLUMN, vaccine_id);
        contentValues.put(REPORT_ID_COLUMN, reportId);
        contentValues.put(WASTED_COLUMN, wasted);
        contentValues.put(RECEIVED_COLUMN, received);
        contentValues.put(BALANCE_INHAND_COLUMN, balance);
        contentValues.put(USE_COLUMN, used);
        contentValues.put(TARGET_COLUMN, target);
        contentValues.put(EVENTDATE_COLUMN,eventDate);
        return database.insert(TABLE_NAME,null ,contentValues);
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return -1;
    }

    }

    /**
     * adding stock using stock Pojo
     * @param stock
     * @return Long
     */
    public long add(Stock stock){
        try {
            SQLiteDatabase database = masterRepository.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VID_COLUMN, stock.getVaccine().getId());
            contentValues.put(REPORT_ID_COLUMN, stock.getTypeReport());
            contentValues.put(WASTED_COLUMN, stock.getWasted());
            contentValues.put(RECEIVED_COLUMN, stock.getReceived());
            contentValues.put(BALANCE_INHAND_COLUMN, stock.getBalanced());
            contentValues.put(USE_COLUMN, stock.getUsed());
            contentValues.put(TARGET_COLUMN, stock.getTarget());
            contentValues.put(EVENTDATE_COLUMN,stock.getEventDate());
            return database.insert(TABLE_NAME, null, contentValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * updating stock using variables
     * @param id
     * @param vaccine_id
     * @param wasted
     * @param received
     * @param balance
     * @param used
     * @return Long
     */
    public long update(int id,int vaccine_id,int wasted ,int received , int balance , int used){
        try{
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VID_COLUMN, vaccine_id);
        contentValues.put(WASTED_COLUMN, wasted);
        contentValues.put(RECEIVED_COLUMN, received);
        contentValues.put(BALANCE_INHAND_COLUMN, balance);
        contentValues.put(USE_COLUMN, used);
       //final String _id=String.valueOf(id);

       return database.update(TABLE_NAME, contentValues, ID_COLUMN+"=?", new String[]{String.valueOf(id)});
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return -1;
    }

    }


    /**
     * Updating stock using stock pojo
     * @param stock
     * @return Long
     */
    public long update(Stock stock) {
        try {
            SQLiteDatabase database = masterRepository.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VID_COLUMN, stock.getVaccine().getId());
            contentValues.put(WASTED_COLUMN, stock.getWasted());
            contentValues.put(RECEIVED_COLUMN, stock.getReceived());
            contentValues.put(BALANCE_INHAND_COLUMN, stock.getBalanced());
            contentValues.put(USE_COLUMN, stock.getUsed());
            final String _id = String.valueOf(stock.getId());

            return database.update(TABLE_NAME, contentValues, ID_COLUMN+"=?", new String[]{_id});
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Stock> getAll(){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, null, null, null, null, null);

        return readAll(cursor);

    }


    public List<Stock> getStockByVaccineID(int vid){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, VID_COLUMN+"=?", new String[]{String.valueOf(vid)}, null, null, null);

        return readAll(cursor);


    }

    /**
     *
     * @param monthYear date format should be like year-month-day e.g 2015-11-19
     * @return List<Stock>
     */
    public List<Stock> getAllStockByMonthAndYear(String monthYear){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN+" LIKE ? ", new String[]{monthYear+"%"}, null, null, null);

        return readAll(cursor);



    }


    /**
     *
     * @param date date format should be like year-month-day e.g 2015-11-19
     * @return List<Stock>
     */
    public List<Stock> getStockByDate(String date){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN+"=?", new String[]{date}, null, null, null);

        return readAll(cursor);

    }


    /**
    *
    *@param monthAndYear  monthAndYear should be like year-month in numeric e.g 2015-11
    *@return int
    */
    public int calculateWastedByMonthAndYear(String monthAndYear){

        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN + " Like ? ", new String[]{monthAndYear + "%"}, null, null, null);
        List<Stock> list=readAll(cursor);
        int result=0;
        for (Stock s: list) {
            result+=s.getWasted();

        }

        return result;
    }

    /**
     *
     *@param monthAndYear  monthAndYear should be like year-month in numeric e.g 2015-11
     *@return int
     */
    public int calculateBalancedByMonthAndYear(String monthAndYear){

        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN + " Like ? ", new String[]{monthAndYear + "%"}, null, null, null);
        List<Stock> list=readAll(cursor);
        int result=0;
        for (Stock s: list) {
            result+=s.getBalanced();

        }

        return result;
    }

    /**
     *
     *@param monthAndYear  monthAndYear should be like year-month in numeric e.g 2015-11
     *@return int
     */
    public int calculateReceivedByMonthAndYear(String monthAndYear){

        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN + " Like ? ", new String[]{monthAndYear + "%"}, null, null, null);
        List<Stock> list=readAll(cursor);
        int result=0;
        for (Stock s: list) {
            result+=s.getReceived();

        }

        return result;
    }
    /**
     *
     *@param monthAndYear  monthAndYear should be like year-month in numeric e.g 2015-11
     *@return int
     */
    public int calculateUsedByMonthAndYear(String monthAndYear){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        Cursor cursor=database.query(TABLE_NAME, COLUMNS, EVENTDATE_COLUMN + " Like ? ", new String[]{monthAndYear + "%"}, null, null, null);
        List<Stock> list=readAll(cursor);
        int result=0;
        for (Stock s: list) {
            result+=s.getUsed();

        }

        return result;

    }

    private List<Stock> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Stock> children = new ArrayList<Stock>();
        while (!cursor.isAfterLast()) {

            children.add(new Stock(cursor.getInt(cursor.getColumnIndex(ID_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(VID_COLUMN) ),
                    cursor.getInt(cursor.getColumnIndex(WASTED_COLUMN)),

                    cursor.getInt(cursor.getColumnIndex(BALANCE_INHAND_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(RECEIVED_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(USE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(EVENTDATE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(TARGET_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(REPORT_ID_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

}

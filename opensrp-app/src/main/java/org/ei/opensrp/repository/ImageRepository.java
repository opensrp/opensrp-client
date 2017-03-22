package org.ei.opensrp.repository;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.ProfileImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ImageRepository extends DrishtiRepository {
    private static final String TAG = ImageRepository.class.getCanonicalName();
    private static final String Image_SQL = "CREATE TABLE ImageList(imageid VARCHAR PRIMARY KEY, anmId VARCHAR, entityID VARCHAR, contenttype VARCHAR, filepath VARCHAR, syncStatus VARCHAR, filecategory VARCHAR, filevector TEXT, bfrStatus VARCHAR)";
    public static final String Image_TABLE_NAME = "ImageList";
    public static final String ID_COLUMN = "imageid";
    public static final String anm_ID_COLUMN = "anmId";
    public static final String entityID_COLUMN = "entityID";
    public static final String numberUser = "numberUser";
    private static final String contenttype_COLUMN = "contenttype";
    public static final String filepath_COLUMN = "filepath";
    public static final String syncStatus_COLUMN = "syncStatus";
    public static final String bfrStatus_COLUMN = "bfrStatus";
    public static final String filecategory_COLUMN = "filecategory";

    public static final String filevector_COLUMN = "filevector";
    public static final String[] Image_TABLE_COLUMNS = {ID_COLUMN, anm_ID_COLUMN, entityID_COLUMN, contenttype_COLUMN, filepath_COLUMN, syncStatus_COLUMN,filecategory_COLUMN, filevector_COLUMN, bfrStatus_COLUMN};
    public static final String Vector_TABLE_NAME = "VectorList";
    public static final String VID_COLUMN = "headerVector";
    private static final String Vector_SQL = "CREATE TABLE VectorList("+ numberUser +" integer PRIMARY KEY, "+ VID_COLUMN +" TEXT NOT NULL UNIQUE)";
    public static final String[] Vector_TABLE_COLUMNS = {
            numberUser,
            VID_COLUMN,
    };

    public static final String TYPE_ANC = "ANC";
    public static final String TYPE_PNC = "PNC";
    private static final String NOT_CLOSED = "false";
    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";

    public static String TYPE_Unbuffered = "Unbuffered";
    public static String TYPE_Buffered = "Buffered";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(Image_SQL);

        database.execSQL(Vector_SQL);
        importCSV(Context.getInstance(), database);
    }


    // If no record yet insert new, if exist just update
    public void add(ProfileImage profileImage, String entityId) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        long id = database.update(Image_TABLE_NAME, createValuesFor(profileImage, TYPE_ANC), ID_COLUMN + "=?", new String[]{String.valueOf(entityId)});
        if (id == 0) {
        id = database.insertWithOnConflict(Image_TABLE_NAME, null, createValuesFor(profileImage, TYPE_ANC), SQLiteDatabase.CONFLICT_IGNORE);
        }

        database.close();
    }

    public List<ProfileImage> allProfileImages() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, syncStatus_COLUMN + " = ?", new String[]{TYPE_Unsynced}, null, null, null, null);
        return readAll(cursor);
    }

    public ProfileImage findByEntityId(String entityId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, entityID_COLUMN + " = ?", new String[]{entityId}, null, null, null, null);
        List<ProfileImage> allcursor = readAll(cursor);
        return (!allcursor.isEmpty()) ? allcursor.get(0) : null;
    }

    public List<ProfileImage> findAllUnSynced() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, syncStatus_COLUMN + " = ?", new String[]{TYPE_Unsynced}, null, null, null, null);
        return readAll(cursor);
    }

    public void close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(syncStatus_COLUMN, TYPE_Synced);
        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(ProfileImage image, String type) {
        ContentValues values = new ContentValues();
//        values.put(ID_COLUMN, image.getImageid());
        if (image.getEntityID() != null)
        values.put(ID_COLUMN, image.getEntityID());
        if (image.getAnmId() != null)
        values.put(anm_ID_COLUMN, image.getAnmId());
        if (image.getContenttype() != null)
        values.put(contenttype_COLUMN, image.getContenttype());
        if (image.getEntityID() != null)
        values.put(entityID_COLUMN, image.getEntityID());
        if (image.getFilepath() != null)
        values.put(filepath_COLUMN, image.getFilepath());
        if (image.getSyncStatus() != null)
        values.put(syncStatus_COLUMN, image.getSyncStatus());
        if (image.getFilecategory() != null)
        values.put(filecategory_COLUMN, image.getFilecategory());
        if (image.getFilevector() != null)
        values.put(filevector_COLUMN, image.getFilevector());
        return values;
    }

    protected List<ProfileImage> readAll(Cursor cursor) {
        List<ProfileImage> profileImages = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount()>0 && cursor.moveToFirst()) {
                while (cursor.getCount() > 0 && !cursor.isAfterLast()) {

                    profileImages.add(new ProfileImage(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));

                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return profileImages;
    }


//    public void update(Mother mother) {
//        SQLiteDatabase database = masterRepository.getWritableDatabase();
//        database.update(MOTHER_TABLE_NAME, createValuesFor(mother, TYPE_ANC), ID_COLUMN + " = ?", new String[]{mother.caseId()});
//    }

    public List<ProfileImage> allVectorImages() {
//        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, null, null, null, null, null, null);
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, syncStatus_COLUMN + " IS NOT NULL", null, null, null, null, null);
        return readAll(cursor);
    }

    public void updateByEntityId(String entityId, String faceVector) {
        ContentValues values = new ContentValues();
        values.put(filevector_COLUMN, faceVector);
        Log.e(TAG, "updateByEntityId: "+values );
        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, "entityID" + " = ?", new String[]{entityId});
        close(entityId);
    }


    public void updateByEntityIdNull(String entityId, String faceVector) {
        ContentValues values = new ContentValues();
        values.put(filevector_COLUMN, faceVector);
        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, "entityID" + " = ? AND filevector is null or filevector =?", new String[]{entityId, ""});
        close(entityId);

    }

    public void insertOrUpdate(String entityId, String faceVector) {
        Log.e(TAG, "insertOrUpdate: "+"start "+entityId );
        SQLiteDatabase db = masterRepository.getReadableDatabase();
        ContentValues values = new ContentValues();

//        ProfileImage profileImage= new ProfileImage();
//        profileImage.setImageid(UUID.randomUUID().toString());

//        values.put(ID_COLUMN, UUID.randomUUID().toString());

        values.put(ID_COLUMN, entityId);
//        values.put(anm_ID_COLUMN, );
        values.put(entityID_COLUMN, entityId);
        values.put(filevector_COLUMN, faceVector);
//        values.put(syncStatus_COLUMN, TYPE_Unsynced);
        values.put(bfrStatus_COLUMN, TYPE_Unbuffered);
//        db.insertWithOnConflict(Vector_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE );
//        masterRepository.getWritableDatabase().update(Vector_TABLE_NAME, values, "entityID" + " = ? AND filevector is null or filevector =?", new String[]{entityId, ""});

//        long id = db.insertWithOnConflict(Vector_TABLE_NAME, null, values,  SQLiteDatabase.CONFLICT_IGNORE);
        long id = db.insertWithOnConflict(Image_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.e(TAG, "insertOrUpdate: id "+ id );
        if (id == -1) {
//            db.update(Vector_TABLE_NAME, values, entityID_COLUMN + "=?" , new String[]{String.valueOf(entityId)});
            db.update(Image_TABLE_NAME, values, entityID_COLUMN + "=?", new String[]{String.valueOf(entityId)});
        }

        close(entityId);

    }

    public List<ProfileImage> getAllVectorImages() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM "+Vector_TABLE_NAME, null);
//        Cursor cursor = database.query(Vector_TABLE_NAME, Vector_TABLE_COLUMNS, syncStatus_COLUMN + " IS NOT NULL", null, null, null, null, null);
//        Cursor cursor = database.query(Vector_TABLE_NAME, Vector_TABLE_COLUMNS, null, null, null, null, null);
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);
    }


    public void insertOrUpdate(ProfileImage profileImage, String uid) {
        SQLiteDatabase db = masterRepository.getReadableDatabase();

        long id = db.insertWithOnConflict(Image_TABLE_NAME, null, createValuesFor(profileImage, TYPE_ANC), SQLiteDatabase.CONFLICT_IGNORE);
        Log.e(TAG, "insertOrUpdate: id insert new "+ id );
        if (id == -1) {
            Log.e(TAG, "insertOrUpdate: "+ "UPDATED" );
            db.update(Vector_TABLE_NAME, createValuesFor(profileImage, TYPE_ANC), ID_COLUMN + "=?" , new String[]{profileImage.getEntityID()});
        }

        close(profileImage.getEntityID());

    }

    public void importCSV(Context context, SQLiteDatabase database) {

        InputStream is = context.applicationContext().getResources().openRawResource(R.raw.header_vectors1000);

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line;
        String numUsr ;
        String headerVector ;
        try {
            while ((line = in.readLine()) != null){
                String[] rowdata = line.split("; ");
                numUsr = rowdata[0];
                headerVector = rowdata[1];

                ContentValues values = new ContentValues();
                values.put(numberUser, numUsr);
                values.put(VID_COLUMN, headerVector);
                database.insert(Vector_TABLE_NAME, null, values);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String findByUserCount(int i) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Vector_TABLE_NAME, Vector_TABLE_COLUMNS, numberUser + " = ?", new String[]{String.valueOf(i)}, null, null, null, null);

//        if (cursor != null){
//            Log.e(TAG, "findByUserCount: cursor not null" );
//        }
        cursor.moveToFirst();
        String vectorHeader = cursor.getString(1);
        cursor.close();
        return vectorHeader;

    }
}

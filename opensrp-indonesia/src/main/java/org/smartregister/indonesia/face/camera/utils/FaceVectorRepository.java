package org.smartregister.indonesia.face.camera.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sid on 2/21/17.
 */
public class FaceVectorRepository extends DrishtiRepository {
    private static final String TAG = FaceVectorRepository.class.getCanonicalName();
    public static final String Image_TABLE_NAME = "ImageList";
    public static final String ID_COLUMN = "imageid";
    public static final String anm_ID_COLUMN = "anmId";
    public static final String entityID_COLUMN = "entityID";
    private static final String contenttype_COLUMN = "contenttype";
    public static final String filepath_COLUMN = "filepath";
    public static final String syncStatus_COLUMN = "syncStatus";
    public static final String filecategory_COLUMN = "filecategory";

    public static final String filevector_COLUMN = "filevector";
    public static final String[] Image_TABLE_COLUMNS = {ID_COLUMN, anm_ID_COLUMN, entityID_COLUMN, contenttype_COLUMN, filepath_COLUMN, syncStatus_COLUMN,filecategory_COLUMN, filevector_COLUMN};
    public static final String Vector_TABLE_NAME = "VectorList";
    public static final String VID_COLUMN = "vectorID";
    private static final String Vector_SQL = "CREATE TABLE VectorList("+VID_COLUMN+" VARCHAR PRIMARY KEY, "+entityID_COLUMN+" VARCHAR, syncStatus VARCHAR )";
    public static final String[] Vector_TABLE_COLUMNS = {
            VID_COLUMN,
            entityID_COLUMN,
            syncStatus_COLUMN
    };

    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";


    @Override
    protected void onCreate(SQLiteDatabase database) {

    }

    private List<ProfileImage> readAll(Cursor cursor) {
        List<ProfileImage> profileImages = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount()>0 && cursor.moveToFirst()) {
                while (cursor.getCount() > 0 && !cursor.isAfterLast()) {

                    profileImages.add(new ProfileImage(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7)
                    )
                    );

                    cursor.moveToNext();
                }
            }

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        } finally {
            assert cursor != null;
            cursor.close();
        }
        return profileImages;
    }


    public ProfileImage findVectorByEntityId(String entityId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Vector_TABLE_NAME,
                Vector_TABLE_COLUMNS,
                entityID_COLUMN + " = ?",
                new String[]{entityId}, null, null, null, null);
        List<ProfileImage> allcursor = readAll(cursor);
        return (!allcursor.isEmpty()) ? allcursor.get(0) : null;
    }

    public List<ProfileImage> findVectorAllUnSynced() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Vector_TABLE_NAME, Vector_TABLE_COLUMNS, syncStatus_COLUMN + " = ?", new String[]{TYPE_Unsynced}, null, null, null, null);
        return readAll(cursor);
    }

    public void vector_close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(syncStatus_COLUMN, TYPE_Synced);
        masterRepository.getWritableDatabase().update(Vector_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

}

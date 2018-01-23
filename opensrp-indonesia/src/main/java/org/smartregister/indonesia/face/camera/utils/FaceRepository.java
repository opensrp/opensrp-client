package org.smartregister.indonesia.face.camera.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.ImageRepository;

import java.util.List;

/**
 * Created by sid on 2/23/17.
 */
public class FaceRepository extends ImageRepository {

    private static String TAG = FaceRepository.class.getSimpleName();
    static FaceRepository faceRepository;

    public DrishtiRepository faceRepository() {
        Log.e(TAG, "faceRepository: "+faceRepository );
        if (faceRepository == null) {
            faceRepository = new FaceRepository();
        }
        Log.e(TAG, "faceRepository: "+faceRepository );
        return faceRepository;
    }

    public List<ProfileImage> allVectorImages() {
        Log.e(TAG, "allVectorImages: "+masterRepository);
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, null, null, null, null, null, null);
        return readAll(cursor);
    }


    public ProfileImage findVectorByEntityId(String entityId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Vector_TABLE_COLUMNS, entityID_COLUMN + " = ?", new String[]{entityId}, null, null, null, null);
        List<ProfileImage> allcursor = readAll(cursor);
        return (!allcursor.isEmpty()) ? allcursor.get(0) : null;
    }

    public List<ProfileImage> findVectorAllUnSynced() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, filevector_COLUMN + " = ?", null , null, null, null, null);
        return readAll(cursor);
    }

    public void vector_close(String caseId) {
        ContentValues values = new ContentValues();
        values.put(syncStatus_COLUMN, TYPE_Synced);
        masterRepository.getWritableDatabase().update(Vector_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }


    public void updateByEntityId(String entityId, String faceVector) {
//        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, entityID_COLUMN + " = ?", new String[]{entityId}, null, null, null, null);
//        List<ProfileImage> allcursor = readAll(cursor);
//        return (!allcursor.isEmpty()) ? allcursor.get(0) : null;

        ContentValues values = new ContentValues();
        values.put(filevector_COLUMN, faceVector);
        Log.e(TAG, "updateByEntityId: "+values );
        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, "entityID" + " = ?", new String[]{entityId});
        close(entityId);
    }

    public void updateByEntityIdNull(String entityId, String faceVector) {
        ContentValues values = new ContentValues();
        values.put(filevector_COLUMN, faceVector);
        Log.e(TAG, "updateByEntityIdNull: "+values );
        Log.e(TAG, "updateByEntityIdNull: "+masterRepository );
//        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, "entityID" + " = ? && faceVector == null ", new String[]{entityId});
//        close(entityId);

    }


}

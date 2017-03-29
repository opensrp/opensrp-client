package org.ei.opensrp.indonesia.face.camera.util;

import android.content.ContentValues;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;

import java.util.List;

/**
 * Created by sid on 2/23/17.
 */
public class FaceRepository extends ImageRepository {

    public void updateByEntityId(String entityId, String faceVector) {
//        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, entityID_COLUMN + " = ?", new String[]{entityId}, null, null, null, null);
//        List<ProfileImage> allcursor = readAll(cursor);
//        return (!allcursor.isEmpty()) ? allcursor.get(0) : null;

        ContentValues values = new ContentValues();
        values.put(filevector_COLUMN, faceVector);
        masterRepository.getWritableDatabase().update(Image_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{entityId});
    }

}

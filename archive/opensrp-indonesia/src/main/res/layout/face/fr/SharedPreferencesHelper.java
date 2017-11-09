package layout.face.fr;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wildan on 12/22/16.
 */
public class SharedPreferencesHelper {
    private static final String HASH_NAME = "HashMap";
    private static final String TAG = SharedPreferencesHelper.class.getSimpleName();

    public HashMap<String, String> retrieveHash(Context context) {
        SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);
        HashMap<String, String> hash = new HashMap<String, String>();
        hash.putAll((Map<? extends String, ? extends String>) settings.getAll());

        return hash;
    }

    public void saveHash(HashMap<String, String> hash, Context context) {
        SharedPreferences settings = context.getSharedPreferences(HASH_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
//        Log.e(TAG, "Hash Save Size = " + hash.size());
        for (String s : hash.keySet()) {
            editor.putString(s, hash.get(s));
        }
        editor.apply();

    }

    public static boolean StoredAsFile(Context context, Bitmap storedBitmap, String inputName) {
        File pictureFile = getOutputMediaFile(inputName);
        if (pictureFile == null) {
            Log.e(TAG, "Error creating media file, check storage permissions ");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            storedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e(TAG, "Wrote image to " + pictureFile);

            MediaScannerConnection.scanFile(context, new String[] { pictureFile.toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            String pathName = pictureFile.toString();
            Log.e(TAG, "Path Name = "+pathName);
            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return false;
    }

    private static File getOutputMediaFile(String inputName) {

//        Check if storage Exist

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "SID-OpenSRP");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.e(TAG,	"failed to find directory "	+ mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG,"failed to create directory "+ mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

//		SID TODO DONE
//		String pref = "IMG";
        String pref = inputName;
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator+ pref+ ".jpg");
        return mediaFile;
    }
}

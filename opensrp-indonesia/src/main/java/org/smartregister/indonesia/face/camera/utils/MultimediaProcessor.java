package org.smartregister.indonesia.face.camera.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.CloudantDataHandler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by sid on 2/22/17.
 */
public class MultimediaProcessor extends ClientProcessor {

    private static final String TAG = MultimediaProcessor.class.getSimpleName();
    private static MultimediaProcessor instance;

    public MultimediaProcessor(Context context) {
        super(context);
    }

    public static MultimediaProcessor getInstance(Context context) {
        if (instance == null) {
            instance = new MultimediaProcessor(context);
        }
        return instance;
    }


}

package org.ei.opensrp;

import android.content.res.AssetManager;
import android.util.Log;

import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.IntegerUtil;

import java.io.IOException;
import java.util.Properties;

public class DristhiConfiguration {

    private static final String DRISHTI_BASE_URL = "DRISHTI_BASE_URL";
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    private static final String SHOULD_VERIFY_CERTIFICATE = "SHOULD_VERIFY_CERTIFICATE";
    private static final String SYNC_DOWNLOAD_BATCH_SIZE = "SYNC_DOWNLOAD_BATCH_SIZE";
    public static AllSharedPreferences preferences;
    private Properties properties = new Properties();

    public DristhiConfiguration(AssetManager assetManager) {
        preferences=Context.getInstance().allSharedPreferences();
        try {
            properties.load(assetManager.open("app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String get(String key) {
        return properties.getProperty(key);
    }

    public String host() {



        return preferences.fetchHost();

    }

    public int port() {


        return preferences.fetchPort();
    }

    public boolean shouldVerifyCertificate() {
        return Boolean.parseBoolean(this.get(SHOULD_VERIFY_CERTIFICATE));
    }

    public String dristhiBaseURL() {

        return preferences.fetchBaseURL();
    }

    public int syncDownloadBatchSize() {
        return IntegerUtil.tryParse(this.get(SYNC_DOWNLOAD_BATCH_SIZE), 100);
    }
}

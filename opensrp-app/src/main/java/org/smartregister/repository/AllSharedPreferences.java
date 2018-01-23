package org.ei.opensrp.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

import static org.ei.opensrp.AllConstants.*;
import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logInfo;

public class AllSharedPreferences {
    public static final String ANM_IDENTIFIER_PREFERENCE_KEY = "anmIdentifier";
    private static final String HOST = "http://118.91.130.18/opensrp";
    private static final String PORT = "9090";
    private static final String LAST_SYNC_DATE = "LAST_SYNC_DATE";
    private SharedPreferences preferences;

    public AllSharedPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public void updateANMUserName(String userName) {
        preferences.edit().putString(ANM_IDENTIFIER_PREFERENCE_KEY, userName).commit();
    }

    public String fetchRegisteredANM() {
        return preferences.getString(ANM_IDENTIFIER_PREFERENCE_KEY, "").trim();
    }

    public String fetchLanguagePreference() {
        return preferences.getString(LANGUAGE_PREFERENCE_KEY, DEFAULT_LOCALE).trim();
    }

    public void saveLanguagePreference(String languagePreference) {
        preferences.edit().putString(LANGUAGE_PREFERENCE_KEY, languagePreference).commit();
    }

    public Boolean fetchIsSyncInProgress() {
        return preferences.getBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, false);
    }

    public void saveIsSyncInProgress(Boolean isSyncInProgress) {
        preferences.edit().putBoolean(IS_SYNC_IN_PROGRESS_PREFERENCE_KEY, isSyncInProgress).commit();
    }

    public String fetchBaseURL(String baseurl){

        return   preferences.getString(DRISHTI_BASE_URL,baseurl);
    }

    public String fetchHost(String host){
        if((host == null || host.isEmpty()) && preferences.getString(HOST,host).equals(host)){
            updateUrl(fetchBaseURL(""));
        }
        return  preferences.getString(HOST,host);
    }

    public void saveHost(String host){
        preferences.edit().putString(HOST,host).commit();
    }

    public Integer fetchPort(Integer port){

        return  Integer.parseInt( preferences.getString(PORT,""+port));
    }

    public Long fetchLastSyncDate(long lastSyncDate){

        return  preferences.getLong(LAST_SYNC_DATE, lastSyncDate);
    }

    public void saveLastSyncDate(long lastSyncDate){
        preferences.edit().putLong(LAST_SYNC_DATE, lastSyncDate).commit();
    }

    public void savePort(Integer port){
        preferences.edit().putString(PORT, String.valueOf(port)).commit();
    }
    public void updateUrl(String baseUrl){
        try {

            URL url = new URL(baseUrl);

            String base = url.getProtocol() + "://" + url.getHost();
            int port = url.getPort();

            logInfo("Base URL: " + base);
            logInfo("Port: " + port);

            saveHost(base);
            savePort(port);

        }catch (MalformedURLException e){
            logError("Malformed Url: " + baseUrl);
        }
    }
    public String getPreference(String key) {
        return preferences.getString(key, "").trim();
    }
    public void setPreference(String key, String value) {
        preferences.edit().putString(key, value).commit();
    }

}

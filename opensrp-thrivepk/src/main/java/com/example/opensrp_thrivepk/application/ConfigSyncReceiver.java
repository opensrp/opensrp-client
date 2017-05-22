package com.example.opensrp_thrivepk.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.service.HTTPAgent;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;

public class ConfigSyncReceiver extends BroadcastReceiver {
    public static final String LAST_SYNC_SERVER_DATETIME = "LAST_SYNC_SERVER_DATETIME";
    private static final String SYNC_URL = "/security/configuration";

    @Override
    public void onReceive(Context context, Intent arg1) {
        AsyncCallWS task = new AsyncCallWS(context);
        task.execute();
    }

    public static void scheduleNextSync(Context context, boolean executedSuccessfully){
        long intervalFromNow = 1000*60L;
        int hh = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(executedSuccessfully){
            intervalFromNow *= 60*24;//incase of success schedule it after 24 hours
        }
        else {
        //if hour b/w 8-18 and service ran successfully run after 4 hours
            if(hh  >= 8 && hh <= 18){
                intervalFromNow *= 1;//30;
            }
            else {
                intervalFromNow *= 2;//360;
            }
        }
        schedule(context, intervalFromNow);
    }

    public static void scheduleFirstSync(Context context){
        schedule(context, 3000 * 1000);
    }

    private static void schedule(Context context, long delayInMillis){
        Intent intent = new Intent(context, ConfigSyncReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.i("ConfigSyncReceiver", "NEXT TRIGGER DATE:"+new DateTime(System.currentTimeMillis()+delayInMillis).toString("yyyy-MM-dd HH:mm:ss"));
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pintent);
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "Config SYNC";
        private Context context;

        public AsyncCallWS(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "starting syncing");
            boolean success = true;
            if(Utils.isConnectedToNetwork(context)){
                try{
                    fetchConfig(context, null, null);
                }
                catch (Exception e){
                    success = false;
                    e.printStackTrace();
                }
            }
            else {
                success = false;
            }

            scheduleNextSync(context, success);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "Config Syncing DONE");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }
    }

    private static JSONObject fetchAsJson(Context context, String lastSyncProperty, String serviceUrl, String user, String password) throws JSONException {
        HTTPAgent httpAgent = org.ei.opensrp.Context.getInstance().getHttpAgent();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if(baseUrl.endsWith("/")){
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        Long lastSyncDatetime = Long.parseLong(Utils.getPreference(context, lastSyncProperty, "0"));
        Log.i(ConfigSyncReceiver.class.getName(),"LAST SYNC DT :"+new DateTime(lastSyncDatetime));


        Response resp = null;
        if(StringUtils.isNotBlank(user)){
            resp = httpAgent.fetchWithCredentials(baseUrl + SYNC_URL, user, password);
        }
        else {
            resp = httpAgent.fetch(baseUrl + SYNC_URL);
        }
        if(resp.isFailure()){
            throw new RuntimeException(serviceUrl+" not returned data");
        }

        JSONObject jo = new JSONObject((String)resp.payload());
        Log.i(ConfigSyncReceiver.class.getName(), "FETCHED" + jo.toString());
        return jo;
    }

    public static void fetchConfig(Context context, String user, String password) throws JSONException {
        JSONObject jo = fetchAsJson(context, LAST_SYNC_SERVER_DATETIME, SYNC_URL, user, password);
        Iterator<String> it = jo.keys();
        while (it.hasNext()){
            String k = it.next();
            Utils.writePreference(context, k, jo.get(k).toString());
        }

        Utils.writePreference(context, LAST_SYNC_SERVER_DATETIME, System.currentTimeMillis() + "");
    }
}
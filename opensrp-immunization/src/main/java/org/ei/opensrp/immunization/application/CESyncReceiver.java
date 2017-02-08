package org.ei.opensrp.immunization.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.ei.opensrp.domain.Response;
import org.ei.opensrp.service.HTTPAgent;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.Event;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.db.utils.FilterType;
import org.ei.opensrp.core.db.utils.Query;
import org.ei.opensrp.core.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class CESyncReceiver extends BroadcastReceiver {
    public static final String CLIENT_SEARCH_URL = "/rest/client";
    public static final String EVENT_SEARCH_URL = "/rest/event";

    public static final String LAST_SYNC_DATETIME_CLIENT = "LAST_SYNC_DATETIME_CLIENT";
    public static final String LAST_SYNC_DATETIME_EVENT = "LAST_SYNC_DATETIME_EVENT";
    public static final String LAST_SYNC_SERVER_DATETIME = "LAST_SYNC_SERVER_DATETIME";

    private CESQLiteHelper db;

    @Override
    public void onReceive(Context context, Intent arg1) {
        db = new CESQLiteHelper(context);
        AsyncCallWS task = new AsyncCallWS(context);
        task.execute();
    }

    public static void scheduleNextSync(Context context, boolean executedSuccessfully){
        long intervalFromNow = 1000*60L;
        int hh = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(executedSuccessfully){
            //if hour b/w 8-18 and service ran successfully run after 4 hours
            if(hh  >= 8 && hh <= 18){
                intervalFromNow *= 1;//180;
            }
            else {
                intervalFromNow *= 2;///360;
            }
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
        schedule(context, 30 * 1000);
    }

    private static void schedule(Context context, long delayInMillis){
        Intent intent = new Intent(context, CESyncReceiver.class);
        PendingIntent pintent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.i("CESyncReceiver", "NEXT TRIGGER DATE:"+new DateTime(System.currentTimeMillis()+delayInMillis).toString("yyyy-MM-dd HH:mm:ss"));
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayInMillis, pintent);
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "CE SYNC";
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
                    fetchAllClients(db, context);
                    fetchAllEvents(db, context);
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
            Log.i(TAG, "CE Syncing DONE");
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

    private JSONArray fetchAsJson(Context context, String lastSyncProperty, String serviceUrl) throws JSONException, UnsupportedEncodingException {
        HTTPAgent httpAgent = org.ei.opensrp.Context.getInstance().getHttpAgent();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if(baseUrl.endsWith("/")){
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        Long lastSyncDatetime = Long.parseLong(Utils.getPreference(context, lastSyncProperty, "0"));
        Log.i(CESyncReceiver.class.getName(),"LAST SYNC DT :"+new DateTime(lastSyncDatetime));

        Query tq = new Query(FilterType.AND).between("lastEdited", new DateTime(lastSyncDatetime), DateTime.now());
        String url = baseUrl + serviceUrl + "?q=" + URLEncoder.encode(tq.query(), "UTF-8");
        Log.i(CESyncReceiver.class.getName(), "URL: "+url);

        Response resp = httpAgent.fetch(url);
        if(resp.isFailure()){
            throw new RuntimeException(serviceUrl+" not returned data");
        }

        JSONArray jarr = new JSONArray((String)resp.payload());
        Log.i(CESyncReceiver.class.getName(), "FETCHED"+ jarr.toString());
        return jarr;
    }

    public void fetchAllClients(CESQLiteHelper dbhandler, Context context) throws JSONException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, UnsupportedEncodingException {
        JSONArray jarr = fetchAsJson(context, LAST_SYNC_DATETIME_CLIENT, CLIENT_SEARCH_URL);
        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jo = jarr.getJSONObject(i);

            Client c = Utils.getLongDateAwareGson().fromJson(jo.toString(), Client.class);
            dbhandler.insert(c);
        }

        Utils.writePreference(context, LAST_SYNC_DATETIME_CLIENT, System.currentTimeMillis()+"");
    }

    public void fetchAllEvents(CESQLiteHelper dbhandler, Context context) throws JSONException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, UnsupportedEncodingException {
        JSONArray jarr = fetchAsJson(context, LAST_SYNC_DATETIME_EVENT, EVENT_SEARCH_URL);
        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jo = jarr.getJSONObject(i);

            Event e = Utils.getLongDateAwareGson().fromJson(jo.toString(), Event.class);
            dbhandler.insert(e);
        }

        Utils.writePreference(context, LAST_SYNC_DATETIME_EVENT, System.currentTimeMillis()+"");
    }
}
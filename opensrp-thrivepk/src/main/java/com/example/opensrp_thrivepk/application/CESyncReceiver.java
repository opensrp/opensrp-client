package com.example.opensrp_thrivepk.application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.domain.Event;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.db.utils.FilterType;
import org.ei.opensrp.core.db.utils.Query;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.service.HTTPAgent;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CESyncReceiver extends BroadcastReceiver {
    //todo should there be last edit date in event get
    // how to fetch updated or new ecvent for already registered clients i.e. event is on basenetity id
    // limit client and event records differently to avoid lost
    public static final String CLIENT_SEARCH_URL = "/rest/client";
    public static final String EVENT_SEARCH_URL = "/rest/event";

    public static final String LAST_SYNC_DATETIME_CLIENT = "LAST_SYNC_DATETIME_CLIENT";
    public static final String LAST_SYNC_DATETIME_EVENT = "LAST_SYNC_DATETIME_EVENT";
    public static final String LAST_SYNC_SERVER_DATETIME = "LAST_SYNC_SERVER_DATETIME";
    private static final String SYNC_LOCATION = "SYNC_LOCATION";

    public static String SYNC_LOCATION(Context context){
        location = Utils.getPreference(context, SYNC_LOCATION, "");
        if (StringUtils.isBlank(location)){
            String providerRole = VaccinatorUtils.providerRolesList().toLowerCase();
            location = providerRole.contains("vaccinator")?
                    VaccinatorUtils.providerDetails().get("provider_city"):
                    VaccinatorUtils.providerDetails().get("provider_town") ;
            Utils.writePreference(context, SYNC_LOCATION, location);
        }
        return location;
    }

    private CESQLiteHelper db;
    private String clientLocationProperty;
    private static String location;

    @Override
    public void onReceive(Context context, Intent arg1) {
        db = new CESQLiteHelper(context);

        if (!org.ei.opensrp.Context.getInstance().IsUserLoggedOut()){
            String providerRole = VaccinatorUtils.providerRolesList().toLowerCase();
            clientLocationProperty = providerRole.contains("vaccinator")? "cityVillage":"town";

            location = SYNC_LOCATION(context);

            AsyncCallWS task = new AsyncCallWS(context);
            task.execute();
        }
        else {
            scheduleNextSync(context, false);
        }
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
                    // keep fetching data until there is no new client or its 10 times iterated; stop and fetch events and leave it for next sync
                    int i = 0;
                    ArrayList<String> clientIds = new ArrayList<>();
                    do {
                        clientIds = fetchAllClients(db, context);
                        i++;
                    } while (clientIds.size() > 0 && i < 10);

                    // FETCH EVENTS
                    i = 0;
                    ArrayList<String> eventsIds = new ArrayList<>();
                    do {
                        // +1 to make sure that last client doesnot come up again
                        Long lastSyncDatetime = Long.parseLong(Utils.getPreference(context, LAST_SYNC_DATETIME_EVENT, "0"))+1;

                        ArrayList<HashMap<String, String>> ids = db.rawQuery("SELECT baseEntityId, MAX(dateCreated, IFNULL(dateEdited, dateCreated)) version FROM client WHERE MAX(dateCreated, IFNULL(dateEdited, dateCreated)) >= '" + new DateTime(lastSyncDatetime).toString("yyyy-MM-dd HH:mm:ss") + "' ORDER BY version LIMIT 25");
                        if (ids.size() > 0) {
                            eventsIds = fetchAllEvents(db, context, ids);
                            Utils.writePreference(context, LAST_SYNC_DATETIME_EVENT, Utils.toDate(ids.get(ids.size() - 1).get("version"), false).getTime() + "");
                        }
                    } while (eventsIds.size() > 0 && i < 40);
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

    private JSONArray fetchAsJson(Context context, String lastSyncProperty, String serviceUrl, String property, Object filter) throws JSONException, UnsupportedEncodingException {
        HTTPAgent httpAgent = org.ei.opensrp.Context.getInstance().getHttpAgent();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if(baseUrl.endsWith("/")){
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        Long lastSyncDatetime = Long.parseLong(Utils.getPreference(context, lastSyncProperty, "0"));
        Log.i(CESyncReceiver.class.getName(),"LAST SYNC DT :"+new DateTime(lastSyncDatetime));

        int limit = 2000;
        // +1 in sync param range to make sure that last record doesnot come again
        Query tq = new Query(FilterType.AND)
                .eq("type","full")
                .between("lastEdited", lastSyncDatetime+1, DateTime.now().getMillis());
        if (filter instanceof List){
            tq.in(property, (List<String>) filter);
            limit = limit * 10;
        }
        else {
            tq.eq(property, filter.toString());
        }
        String url = baseUrl + serviceUrl + "?sort=lastEdited&limit="+limit+"&q=" + URLEncoder.encode(tq.query(), "UTF-8");
        Log.i(CESyncReceiver.class.getName(), "URL: "+url);

        Response resp = httpAgent.fetch(url);
        if(resp.isFailure()){
           throw new RuntimeException(serviceUrl+" not returned data");
        }

        JSONArray jarr = new JSONArray((String)resp.payload());
        Log.i(CESyncReceiver.class.getName(), "FETCHED"+ jarr.toString());
        return jarr;
    }

    public ArrayList<String> fetchAllClients(CESQLiteHelper dbhandler, Context context) throws JSONException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, UnsupportedEncodingException {
        JSONArray jarr = fetchAsJson(context, LAST_SYNC_DATETIME_CLIENT, CLIENT_SEARCH_URL, clientLocationProperty, location);

        DateTime lastDate = null;//dont update if no results were returned

        ArrayList<String> clientIds = new ArrayList<>();

        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jo = jarr.getJSONObject(i);

            Client c = Utils.getLongDateAwareGson().fromJson(jo.toString(), Client.class);
            try{
                if (dbhandler.getClient(c.getBaseEntityId()) != null){
                    dbhandler.update(c);
                }
                else {
                    dbhandler.insert(c);
                }
                clientIds.add(c.getBaseEntityId());
            }
            catch (Exception e){
                e.printStackTrace();//todo do something to notify
            }

            DateTime cdate = c.getDateEdited() == null ? c.getDateCreated() : c.getDateEdited();
            if(lastDate == null || cdate.isAfter(lastDate)){
                lastDate = cdate;
            }
        }
        if(lastDate != null) {
            Log.v(getClass().getName(), "Updating last date for client "+lastDate);
            Utils.writePreference(context, LAST_SYNC_DATETIME_CLIENT, (lastDate.getMillis()) + "");
        }

        return clientIds;
    }

    public ArrayList<String> fetchAllEvents(CESQLiteHelper dbhandler, Context context, ArrayList<HashMap<String, String>> ids) throws JSONException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException, UnsupportedEncodingException {
        ArrayList l = new ArrayList();
        for (HashMap<String, String>  m: ids) {
            l.add(m.get(CESQLiteHelper.client_column.baseEntityId.name()));
        }

        JSONArray jarr = fetchAsJson(context, LAST_SYNC_DATETIME_EVENT, EVENT_SEARCH_URL, clientLocationProperty, location);

        ArrayList<String> eventIds = new ArrayList();
        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jo = jarr.getJSONObject(i);

            Event e = Utils.getLongDateAwareGson().fromJson(jo.toString(), Event.class);
            try {
                dbhandler.insert(e);

                eventIds.add(e.getId());
            }
            catch (Exception ex){
                ex.printStackTrace();//todo do something to notify
            }

            // todo update if event exists
        }
        return eventIds;
    }
}
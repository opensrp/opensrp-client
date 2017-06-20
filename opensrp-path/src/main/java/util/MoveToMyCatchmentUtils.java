package util;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.db.Event;
import org.ei.opensrp.path.db.Obs;
import org.ei.opensrp.path.repository.BaseRepository;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.ei.opensrp.path.sync.PathClientProcessor;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by keyman on 26/01/2017.
 */
public class MoveToMyCatchmentUtils {
    public static String MOVE_TO_CATCHMENT_EVENT = "Move To Catchment";

    public static void moveToMyCatchment(final List<String> ids, final Listener<JSONObject> listener, final ProgressDialog progressDialog) {

        Utils.startAsyncTask(new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                publishProgress();
                Response<String> response = move(ids);
                if (response.isFailure()) {
                    return null;
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.payload());
                        return jsonObject;
                    } catch (Exception e) {
                        Log.e(getClass().getName(), "", e);
                        return null;
                    }
                }
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                listener.onEvent(result);
                progressDialog.dismiss();
            }
        }, null);
    }

    public static Response<String> move(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new Response<String>(ResponseStatus.failure, "entityId doesn't exist");
        }

        Context context = Context.getInstance();
        DristhiConfiguration configuration = context.configuration();

        String baseUrl = configuration.dristhiBaseURL();
        String idString = StringUtils.join(ids, ",");

        String paramString = "?baseEntityId=" + urlEncode(idString.trim()) + "&limit=1000";
        String uri = baseUrl + ECSyncUpdater.SEARCH_URL + paramString;

        Response<String> response = context.getHttpAgent().fetch(uri);
        return response;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static boolean processMoveToCatchment(android.content.Context context, AllSharedPreferences allSharedPreferences, JSONObject jsonObject) {

        try {
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);
            int eventsCount = jsonObject.has("no_of_events") ? jsonObject.getInt("no_of_events") : 0;
            if (eventsCount == 0) {
                return false;
            }

            JSONArray events = jsonObject.has("events") ? jsonObject.getJSONArray("events") : new JSONArray();
            JSONArray clients = jsonObject.has("clients") ? jsonObject.getJSONArray("clients") : new JSONArray();

            ecUpdater.batchSave(events, clients);

            final String BIRTH_REGISTRATION_EVENT = "Birth Registration";
            final String NEW_WOMAN_REGISTRATION_EVENT = "New Woman Registration";
            final String HOME_FACILITY = "Home_Facility";

            String toProviderId = allSharedPreferences.fetchRegisteredANM();

            String toLocationId = allSharedPreferences
                    .fetchDefaultLocalityId(toProviderId);

            for (int i = 0; i < events.length(); i++) {
                JSONObject jsonEvent = events.getJSONObject(i);
                Event event = ecUpdater.convert(jsonEvent, Event.class);
                if (event == null) {
                    continue;
                }

                String fromLocationId = null;
                if (event.getEventType().equals(BIRTH_REGISTRATION_EVENT)) {
                    // Update home facility
                    for (Obs obs : event.getObs()) {
                        if (obs.getFormSubmissionField().equals(HOME_FACILITY)) {
                            fromLocationId = obs.getValue().toString();
                            List<Object> values = new ArrayList<>();
                            values.add(toLocationId);
                            obs.setValues(values);
                        }
                    }
                }

                if (event.getEventType().equals(BIRTH_REGISTRATION_EVENT) || event.getEventType().equals(NEW_WOMAN_REGISTRATION_EVENT)) {
                    //Create move to catchment event;
                    org.ei.opensrp.clientandeventmodel.Event moveToCatchmentEvent = JsonFormUtils.createMoveToCatchmentEvent(context, event, fromLocationId, toProviderId, toLocationId);
                    JSONObject moveToCatchmentJsonEvent = ecUpdater.convertToJson(moveToCatchmentEvent);
                    if (moveToCatchmentEvent != null) {
                        ecUpdater.addEvent(moveToCatchmentEvent.getBaseEntityId(), moveToCatchmentJsonEvent);
                    }
                }

                // Update providerId and Save unsynced event
                event.setProviderId(toProviderId);
                JSONObject updatedJsonEvent = ecUpdater.convertToJson(event);
                jsonEvent = JsonFormUtils.merge(jsonEvent, updatedJsonEvent);

                ecUpdater.addEvent(event.getBaseEntityId(), jsonEvent);
            }

            long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            PathClientProcessor.getInstance(context).processClient(ecUpdater.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());

            return true;
        } catch (Exception e) {
            Log.e(MoveToMyCatchmentUtils.class.getName(), "Exception", e);
        }

        return false;
    }
}

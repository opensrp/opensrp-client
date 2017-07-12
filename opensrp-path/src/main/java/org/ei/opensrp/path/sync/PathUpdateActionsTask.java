package org.ei.opensrp.path.sync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.domain.DownloadStatus;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.receiver.SyncStatusBroadcastReceiver;
import org.ei.opensrp.path.receiver.VaccinatorAlarmReceiver;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.service.intent.PullUniqueIdsIntentService;
import org.ei.opensrp.path.service.intent.ZScoreRefreshIntentService;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.ActionService;
import org.ei.opensrp.service.AllFormVersionSyncService;
import org.ei.opensrp.service.FormSubmissionSyncService;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.service.ImageUploadSyncService;
import org.ei.opensrp.sync.AdditionalSyncService;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import util.NetworkUtils;
import util.PathConstants;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.domain.FetchStatus.fetched;
import static org.ei.opensrp.domain.FetchStatus.fetchedFailed;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;
import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logInfo;

public class PathUpdateActionsTask {
    private static final String EVENTS_SYNC_PATH = "/rest/event/add";
    private static final String REPORTS_SYNC_PATH = "/rest/reports/add";
    private static final String STOCK_Add_PATH = "/rest/stock/add/";
    private static final String STOCK_SYNC_PATH = "/rest/stock/sync/";
    private final LockingBackgroundTask task;
    private ActionService actionService;
    private Context context;
    private FormSubmissionSyncService formSubmissionSyncService;
    private AllFormVersionSyncService allFormVersionSyncService;
    private AdditionalSyncService additionalSyncService;
    private PathAfterFetchListener pathAfterFetchListener;
    private PathRepository db;
    HTTPAgent httpAgent;


    public PathUpdateActionsTask(Context context, ActionService actionService, FormSubmissionSyncService formSubmissionSyncService, ProgressIndicator progressIndicator,
                                 AllFormVersionSyncService allFormVersionSyncService) {
        this.actionService = actionService;
        this.context = context;
        this.formSubmissionSyncService = formSubmissionSyncService;
        this.allFormVersionSyncService = allFormVersionSyncService;
        this.additionalSyncService = null;
        task = new LockingBackgroundTask(progressIndicator);
        this.db = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        this.httpAgent = org.ei.opensrp.Context.getInstance().getHttpAgent();
    }

    public void setAdditionalSyncService(AdditionalSyncService additionalSyncService) {
        this.additionalSyncService = additionalSyncService;
    }

    public void updateFromServer(final PathAfterFetchListener pathAfterFetchListener) {
        this.pathAfterFetchListener = pathAfterFetchListener;

        sendSyncStatusBroadcastMessage(context, FetchStatus.fetchStarted);
        if (org.ei.opensrp.Context.getInstance().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {
                if (NetworkUtils.isNetworkAvailable()) {
                    FetchStatus fetchStatusForForms = sync();
                    FetchStatus fetchStatusForActions = actionService.fetchNewActions();
                    pathAfterFetchListener.partialFetch(fetchStatusForActions);

                    startImageUploadIntentService(context);
                    startPullUniqueIdsIntentService(context);

                    FetchStatus fetchStatusAdditional = additionalSyncService == null ? nothingFetched : additionalSyncService.sync();

                    if (org.ei.opensrp.Context.getInstance().configuration().shouldSyncForm()) {

                        allFormVersionSyncService.verifyFormsInFolder();
                        FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                        DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();

                        if (downloadStatus == DownloadStatus.downloaded) {
                            allFormVersionSyncService.unzipAllDownloadedFormFile();
                        }

                        if (fetchVersionStatus == fetched || downloadStatus == DownloadStatus.downloaded) {
                            return fetched;
                        }
                    }

                    if (fetchStatusForActions == fetched || fetchStatusForForms == fetched || fetchStatusAdditional == fetched)
                        return fetched;

                    return fetchStatusForForms;
                }

                return FetchStatus.noConnection;
            }

            public void postExecuteInUIThread(FetchStatus result) {
                Intent intent = new Intent(context, ZScoreRefreshIntentService.class);
                context.startService(intent);
                if (result.equals(FetchStatus.nothingFetched) || result.equals(FetchStatus.fetched)) {
                    ECSyncUpdater ecSyncUpdater = ECSyncUpdater.getInstance(context);
                    ecSyncUpdater.updateLastCheckTimeStamp(Calendar.getInstance().getTimeInMillis());
                }
                pathAfterFetchListener.afterFetch(result);
                sendSyncStatusBroadcastMessage(context, result);
            }
        });
    }

    private FetchStatus sync() {
        try {
            int totalCount = 0;
            pushToServer();
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

            // Retrieve database host from preferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            while (true) {
                long startSyncTimeStamp = ecUpdater.getLastSyncTimeStamp();

                int eCount = ecUpdater.fetchAllClientsAndEvents(AllConstants.SyncFilters.FILTER_PROVIDER, allSharedPreferences.fetchRegisteredANM());
                totalCount += eCount;

                if (eCount <= 0) {
                    if (eCount < 0) totalCount = eCount;
                    break;
                }

                long lastSyncTimeStamp = ecUpdater.getLastSyncTimeStamp();
                PathClientProcessor.getInstance(context).processClient(ecUpdater.allEvents(startSyncTimeStamp, lastSyncTimeStamp));
                Log.i(getClass().getName(), "!!!!! Sync count:  " + eCount);
                pathAfterFetchListener.partialFetch(fetched);
            }
            pushStockToServer();
            pullStockFromServer();

            if (totalCount == 0) {
                return nothingFetched;
            } else if (totalCount < 0) {
                return fetchedFailed;
            } else {
                return fetched;
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
            return fetchedFailed;
        }

    }
    public void pushToServer() {
        pushECToServer();
        pushReportsToServer();
    }
    public void pushECToServer() {
        boolean keepSyncing = true;
        int limit = 50;
        try {
            // db.markAllAsUnSynced();

            while (keepSyncing) {
                Map<String, Object> pendingEvents = null;
                pendingEvents = db.getUnSyncedEvents(limit);

                if (pendingEvents.isEmpty()) {
                    return;
                }

                String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                }
                // create request body
                JSONObject request = new JSONObject();
                if (pendingEvents.containsKey("clients")) {
                    request.put("clients", pendingEvents.get("clients"));
                }
                if (pendingEvents.containsKey("events")) {
                    request.put("events", pendingEvents.get("events"));
                }
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        format("{0}/{1}",
                                baseUrl,
                                EVENTS_SYNC_PATH),
                        jsonPayload);
                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Events sync failed.");
                    return;
                }
                db.markEventsAsSynced(pendingEvents);
                Log.i(getClass().getName(), "Events synced successfully.");
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (ParseException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), e.getMessage());
        }

    }

    private void pullStockFromServer() {
        boolean keepSyncing = true;
        int limit = 50;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        String anmId = allSharedPreferences.fetchRegisteredANM();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        while (true) {
            long timestamp = preferences.getLong("last_stock_sync",0);
            String uri = format("{0}/{1}?providerid={2}&timestamp={3}",
                    baseUrl,
                    STOCK_SYNC_PATH,
                    anmId,
                    timestamp
                    );
            Response<String> response = httpAgent.fetch(uri);
            if (response.isFailure()) {
                logError(format("Form submissions pull failed."));
                return ;
            }
            String jsonPayload = response.payload();
            ArrayList <Stock> Stock_arrayList = getStockFromPayload(jsonPayload);
            Long highestTimestamp = getHighestTimestampFromStockPayLoad(jsonPayload);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("last_stock_sync", highestTimestamp);
            editor.commit();
            if (Stock_arrayList.isEmpty()) {
                return ;
            } else {
                StockRepository stockRepository = new StockRepository(db,VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
                for(int j = 0;j<Stock_arrayList.size();j++){
                    stockRepository.add(Stock_arrayList.get(j));
                }

            }
        }
    }

    private Long getHighestTimestampFromStockPayLoad(String jsonPayload) {
        Long toreturn = 0l;
        try {
            JSONObject stockcontainer = new JSONObject(jsonPayload);
            if (stockcontainer.has("stocks")) {
                JSONArray stockArray = stockcontainer.getJSONArray("stocks");
                for(int i = 0;i<stockArray.length();i++){

                    JSONObject stockobject = stockArray.getJSONObject(i);
                    if(stockobject.getLong("serverVersion")>toreturn){
                        toreturn = stockobject.getLong("serverVersion");
                    }

                }
            }
        }catch (Exception e){

        }
        return toreturn;
    }

    private ArrayList<Stock> getStockFromPayload(String jsonPayload) {
        ArrayList <Stock> Stock_arrayList = new ArrayList<Stock>();
        try {
            JSONObject stockcontainer = new JSONObject(jsonPayload);
            if (stockcontainer.has("stocks")) {
                JSONArray stockArray = stockcontainer.getJSONArray("stocks");
                for(int i = 0;i<stockArray.length();i++){
                    JSONObject stockobject = stockArray.getJSONObject(i);
                    Stock stock = new Stock(stockobject.getLong("identifier"),
                                            stockobject.getString("transaction_type"),
                                            stockobject.getString("providerid"),
                                            stockobject.getInt("value"),
                                            stockobject.getLong("date_created"),
                                            stockobject.getString("to_from"),
                                            stockobject.getString("sync_status"),
                                            stockobject.getLong("date_updated"),
                                            stockobject.getString("vaccine_type_id"));
                    Stock_arrayList.add(stock);
                }
            }
        }catch (Exception e){

        }
        return Stock_arrayList;
    }

    public void pushStockToServer() {
        boolean keepSyncing = true;
        int limit = 50;

        try {

            while (keepSyncing) {
                StockRepository stockRepository = new StockRepository(db,VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
                ArrayList<Stock> stocks = (ArrayList<Stock>) stockRepository.findUnSyncedWithLimit(limit);
                JSONArray stocksarray = createJsonArrayFromStockArray(stocks);
                if (stocks.isEmpty()) {
                    return;
                }

                String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                }
                // create request body
                JSONObject request = new JSONObject();

                request.put("stocks", stocksarray);

                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        format("{0}/{1}",
                                baseUrl,
                                STOCK_Add_PATH),
                        jsonPayload);
                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Events sync failed.");
                    return;
                }
                stockRepository.markEventsAsSynced(stocks);
                Log.i(getClass().getName(), "Events synced successfully.");
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private JSONArray createJsonArrayFromStockArray(ArrayList<Stock> stocks) {
        JSONArray array = new JSONArray();
        for(int i = 0;i<stocks.size();i++){
            JSONObject stock = new JSONObject();
            try {
                stock.put("identifier",stocks.get(i).getId());
                stock.put("vaccine_type_id",stocks.get(i).getVaccine_type_id());
                stock.put("transaction_type",stocks.get(i).getTransaction_type());
                stock.put("providerid",stocks.get(i).getProviderid());
                stock.put("date_created",stocks.get(i).getDate_created());
                stock.put("value",stocks.get(i).getValue());
                stock.put("to_from",stocks.get(i).getTo_from());
                stock.put("sync_status",stocks.get(i).getSyncStatus());
                stock.put("date_updated",stocks.get(i).getUpdatedAt());
                array.put(stock);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    private void pushReportsToServer() {
        try {
            boolean keepSyncing = true;
            int limit = 50;
            while (keepSyncing) {
                List<JSONObject> pendingReports = null;
                pendingReports = db.getUnSyncedReports(limit);

                if (pendingReports.isEmpty()) {
                    return;
                }

                String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                }
                // create request body
                JSONObject request = new JSONObject();

                request.put("reports", pendingReports);
                String jsonPayload = request.toString();
                Response<String> response = httpAgent.post(
                        MessageFormat.format("{0}/{1}",
                                baseUrl,
                                REPORTS_SYNC_PATH),
                        jsonPayload);
                if (response.isFailure()) {
                    Log.e(getClass().getName(), "Reports sync failed.");
                    return;
                }
                db.markReportsAsSynced(pendingReports);
                Log.i(getClass().getName(), "Reports synced successfully.");
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (ParseException e) {
            Log.e(getClass().getName(), e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private void startImageUploadIntentService(Context context) {
        Intent intent = new Intent(context, ImageUploadSyncService.class);
        context.startService(intent);
    }

    private void startPullUniqueIdsIntentService(Context context) {
        Intent intent = new Intent(context, PullUniqueIdsIntentService.class);
        context.startService(intent);
    }


    private void sendSyncStatusBroadcastMessage(Context context, FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        context.sendBroadcast(intent);
    }

    public static void setAlarms(Context context) {
        VaccinatorAlarmReceiver.setAlarm(context, 2, PathConstants.ServiceType.DAILY_TALLIES_GENERATION);
        VaccinatorAlarmReceiver.setAlarm(context, 2, PathConstants.ServiceType.WEIGHT_SYNC_PROCESSING);
        VaccinatorAlarmReceiver.setAlarm(context, 2, PathConstants.ServiceType.VACCINE_SYNC_PROCESSING);
        VaccinatorAlarmReceiver.setAlarm(context, 2, PathConstants.ServiceType.RECURRING_SERVICES_SYNC_PROCESSING);
    }

}
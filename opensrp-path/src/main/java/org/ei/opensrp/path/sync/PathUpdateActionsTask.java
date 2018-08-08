package org.ei.opensrp.path.sync;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.DownloadStatus;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.receiver.SyncStatusBroadcastReceiver;
import org.ei.opensrp.path.receiver.VaccinatorAlarmReceiver;
import org.ei.opensrp.path.repository.BaseRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.service.intent.PullUniqueIdsIntentService;
import org.ei.opensrp.path.service.intent.ZScoreRefreshIntentService;
import org.ei.opensrp.repository.AlertRepository;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.DetailsRepository;
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
import java.util.Date;
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
import static org.ei.opensrp.util.Log.timeStampLog;
import static util.VaccinatorUtils.receivedVaccines;

import java.text.SimpleDateFormat;
public class PathUpdateActionsTask {
    private static final String EVENTS_SYNC_PATH = "/rest/event/add";
    private static final String REPORTS_SYNC_PATH = "/rest/report/add";
    private static final String STOCK_Add_PATH = "/rest/stockresource/add/";
    private static final String STOCK_SYNC_PATH = "rest/stockresource/sync/";
    private static final String anouncement_PATH = "message-announcement";
    private static final String send_anouncement_PATH = "send-announcement";
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
                timeStampLog("start of a single batch sync");
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
                timeStampLog("end of a single batch sync");
            }
//            pullStockFromServer();

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
//        pushReportsToServer();
//        pushStockToServer();
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
        final String LAST_STOCK_SYNC = "last_stock_sync";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
        String anmId = allSharedPreferences.fetchRegisteredANM();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        while (true) {
            long timestamp = preferences.getLong(LAST_STOCK_SYNC, 0);
            String timeStampString = String.valueOf(timestamp);
            String uri = format("{0}/{1}?providerid={2}&serverVersion={3}",
                    baseUrl,
                    STOCK_SYNC_PATH,
                    anmId,
                    timeStampString
            );
            Response<String> response = httpAgent.fetch(uri);
            if (response.isFailure()) {
                logError(format("Stock pull failed."));
                return;
            }
            String jsonPayload = response.payload();
            ArrayList<Stock> Stock_arrayList = getStockFromPayload(jsonPayload);
            Long highestTimestamp = getHighestTimestampFromStockPayLoad(jsonPayload);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(LAST_STOCK_SYNC, highestTimestamp);
            editor.commit();
            if (Stock_arrayList.isEmpty()) {
                return;
            } else {
                StockRepository stockRepository = new StockRepository(db, VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
                for (int j = 0; j < Stock_arrayList.size(); j++) {
                    Stock fromServer = Stock_arrayList.get(j);
                    List<Stock> existingStock = stockRepository.findUniqueStock(fromServer.getVaccine_type_id(), fromServer.getTransaction_type(), fromServer.getProviderid(),
                            String.valueOf(fromServer.getValue()), String.valueOf(fromServer.getDate_created()), fromServer.getTo_from());
                    if (!existingStock.isEmpty()) {
                        for (Stock stock : existingStock) {
                            fromServer.setId(stock.getId());
                        }
                    }
                    stockRepository.add(fromServer);
                }

            }
        }
    }

    private Long getHighestTimestampFromStockPayLoad(String jsonPayload) {
        Long toreturn = 0l;
        try {
            JSONObject stockContainer = new JSONObject(jsonPayload);
            if (stockContainer.has("stocks")) {
                JSONArray stockArray = stockContainer.getJSONArray("stocks");
                for (int i = 0; i < stockArray.length(); i++) {

                    JSONObject stockObject = stockArray.getJSONObject(i);
                    if (stockObject.getLong("serverVersion") > toreturn) {
                        toreturn = stockObject.getLong("serverVersion");
                    }

                }
            }
        } catch (Exception e) {

        }
        return toreturn;
    }

    private ArrayList<Stock> getStockFromPayload(String jsonPayload) {
        ArrayList<Stock> Stock_arrayList = new ArrayList<>();
        try {
            JSONObject stockcontainer = new JSONObject(jsonPayload);
            if (stockcontainer.has("stocks")) {
                JSONArray stockArray = stockcontainer.getJSONArray("stocks");
                for (int i = 0; i < stockArray.length(); i++) {
                    JSONObject stockObject = stockArray.getJSONObject(i);
                    Stock stock = new Stock(null,
                            stockObject.getString("transaction_type"),
                            stockObject.getString("providerid"),
                            stockObject.getInt("value"),
                            stockObject.getLong("date_created"),
                            stockObject.getString("to_from"),
                            BaseRepository.TYPE_Synced,
                            stockObject.getLong("date_updated"),
                            stockObject.getString("vaccine_type_id"));
                    Stock_arrayList.add(stock);
                }
            }
        } catch (Exception e) {

        }
        return Stock_arrayList;
    }

    public void pushStockToServer() {
        boolean keepSyncing = true;
        int limit = 50;

        try {

            while (keepSyncing) {
                StockRepository stockRepository = new StockRepository(db, VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
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
                    Log.e(getClass().getName(), "Stocks sync failed.");
                    return;
                }
                stockRepository.markEventsAsSynced(stocks);
                Log.i(getClass().getName(), "Stocks synced successfully.");
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), e.getMessage());
        }
    }

    private JSONArray createJsonArrayFromStockArray(ArrayList<Stock> stocks) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < stocks.size(); i++) {
            JSONObject stock = new JSONObject();
            try {
                stock.put("identifier", stocks.get(i).getId());
                stock.put("vaccine_type_id", stocks.get(i).getVaccine_type_id());
                stock.put("transaction_type", stocks.get(i).getTransaction_type());
                stock.put("providerid", stocks.get(i).getProviderid());
                stock.put("date_created", stocks.get(i).getDate_created());
                stock.put("value", stocks.get(i).getValue());
                stock.put("to_from", stocks.get(i).getTo_from());
                stock.put("date_updated", stocks.get(i).getUpdatedAt());
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

    public void sendAnouncement() {
        while (true) {
//            (new AsyncTask() {
//                boolean sentmessages = false;
//                @Override
//                protected Object doInBackground(Object[] params) {
//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//                    AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
//                    String anmId = allSharedPreferences.fetchRegisteredANM();
//                    String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
//                    if (baseUrl.endsWith("/")) {
//                        baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
//                    }
//                    String uri = format("{0}/{1}?provider={2}",
//                            baseUrl,
//                            anouncement_PATH,
//                            anmId
//                    );
//                    Response<String> response = httpAgent.fetch(uri);
//                    if(response.isFailure())
//
//                    {
//                        sentmessages = false;
//                        logError(format("error returned in calling anouncement"));
//                    }
//
//                    else if(response.status()==ResponseStatus.success)
//
//                    {
//                        sentmessages = true;
//                        logError(format("called anouncement"));
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Object o) {
//                    super.onPostExecute(o);
//                    if(sentmessages){
//                        Toast.makeText(context,"anouncement messages sent",Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//            ).execute();
            sendAnnouncementForBenificiaries();
            return;
        }
    }
    public void sendAnnouncementForBenificiaries(){
        (new AsyncTask() {
            boolean sentmessages = false;
            @Override
            protected Object doInBackground(Object[] params) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
                String anmId = allSharedPreferences.fetchRegisteredANM();
                String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                }
                String uri = format("{0}/{1}?provider={2}",
                        baseUrl,
                        send_anouncement_PATH,
                        anmId
                );
                String jsonPayload = createPayloadForAnouncement();
                Response<String> response = httpAgent.post(uri,jsonPayload);
                if(response.isFailure())

                {
                    sentmessages = false;
                    logError(format("error returned in calling anouncement"));
                }

                else if(response.status()==ResponseStatus.success)

                {
                    sentmessages = true;
                    logError(format("called anouncement"));
                }
                return null;
            }

            private String createPayloadForAnouncement() {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date today = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(today);
                cal.add(Calendar.DATE,-(20*365));
                today = cal.getTime();
                cal.add(Calendar.DATE,1+(20*365));
                Date tomorrow = cal.getTime();
                String entityID = "";
                JSONArray payloadArray = new JSONArray();
                CommonRepository commonRepository = org.ei.opensrp.Context.getInstance().commonrepository("ec_child");
                Cursor cursor = commonRepository.RawCustomQueryForAdapter("select * from alerts where alerts.caseID in (Select id from ec_child) and (date(startDate) < date('now')) and (scheduleName not like '%ITN%' and scheduleName not like '%Deworming%' and scheduleName not like '%Vit A%' and scheduleName not like '%ROTA%' and scheduleName not like '%OPV 4%') and (alerts.status = 'normal' or alerts.status = 'urgent' or alerts.status = 'upcoming') order by caseID ");
                cursor.moveToFirst();
                String vaccines = "";
                String mobileno = "";
                JSONObject anouncementObject = new JSONObject();
                Map<String, Date> recievedVaccinesmap = null;
                try {
                    while (!cursor.isAfterLast()) {

                                String previousEntityID = entityID;
                        entityID = cursor.getString(0);
                        String startdate = format.format(tomorrow.getTime());
                        if(!entityID.equalsIgnoreCase(previousEntityID)){
                            if(previousEntityID.equalsIgnoreCase("")){
                                previousEntityID = entityID;
                                VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                                List<Vaccine> vaccinesgiven = vaccineRepository.findByEntityId(entityID);
                                recievedVaccinesmap = receivedVaccines(vaccinesgiven);

                                Cursor phonenumbercursor = commonRepository.RawCustomQueryForAdapter("select ec_details.value from ec_details where key = 'Mother_Guardian_Number' and ec_details.base_entity_id = '"+entityID+"'");
                                phonenumbercursor.moveToFirst();
                                if(!phonenumbercursor.isAfterLast()){
                                    mobileno = phonenumbercursor.getString(0);
                                }
                                phonenumbercursor.close();

                                vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                            }else{
                                if(!vaccines.equalsIgnoreCase("")) {
                                    anouncementObject.put("baseEntityId", previousEntityID);
                                    anouncementObject.put("mobileNo", mobileno);
                                    anouncementObject.put("clientType", "child");
                                    anouncementObject.put("vaccinationDate", startdate);
                                    anouncementObject.put("vaccineDue", vaccines);
                                    payloadArray.put(anouncementObject);
                                }
                                vaccines = "";
                                startdate = format.format(tomorrow.getTime());
                                previousEntityID = entityID;
                                Cursor phonenumbercursor = commonRepository.RawCustomQueryForAdapter("select ec_details.value from ec_details where key = 'Mother_Guardian_Number' and ec_details.base_entity_id = '"+entityID+"'");
                                phonenumbercursor.moveToFirst();
                                if(!phonenumbercursor.isAfterLast()){
                                    mobileno = phonenumbercursor.getString(0);
                                }
                                phonenumbercursor.close();
                                VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                                List<Vaccine> vaccinesgiven = vaccineRepository.findByEntityId(entityID);
                                recievedVaccinesmap = receivedVaccines(vaccinesgiven);
                                anouncementObject = new JSONObject();
                                vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                            }
                        }else{
                            vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                        }
                        cursor.moveToNext();
                        if(cursor.isAfterLast() && !vaccines.equalsIgnoreCase("")){
                            anouncementObject.put("baseEntityId",previousEntityID);
                            anouncementObject.put("mobileNo",mobileno);
                            anouncementObject.put("clientType","child");
                            anouncementObject.put("vaccinationDate",startdate);
                            anouncementObject.put("vaccineDue",vaccines);
                            payloadArray.put(anouncementObject);
                        }
                    }
                    cursor.close();

                    entityID = "";
                    cursor = commonRepository.RawCustomQueryForAdapter("select * from alerts where alerts.caseID in (Select id from ec_mother) and (date(startDate) < date('now')) and (scheduleName not like '%ITN%' and scheduleName not like '%Deworming%' and scheduleName not like '%Vit A%' and scheduleName not like '%ROTA%' and scheduleName not like '%OPV 4%') and (alerts.status = 'normal' or alerts.status = 'urgent' or alerts.status = 'upcoming') order by caseID ");
                    cursor.moveToFirst();
                    vaccines = "";
                    mobileno = "";
                    anouncementObject = new JSONObject();
                        while (!cursor.isAfterLast()) {
                            String previousEntityID = entityID;
                            entityID = cursor.getString(0);
                            String startdate = format.format(tomorrow.getTime());
                            if(!entityID.equalsIgnoreCase(previousEntityID)){
                                if(previousEntityID.equalsIgnoreCase("")){
                                    previousEntityID = entityID;
                                    VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                                    List<Vaccine> vaccinesgiven = vaccineRepository.findByEntityId(entityID);
                                    recievedVaccinesmap = receivedVaccines(vaccinesgiven);
                                    Cursor phonenumbercursor = commonRepository.RawCustomQueryForAdapter("select ec_details.value from ec_details where key = 'phoneNumber' and ec_details.base_entity_id = '"+entityID+"'");
                                    phonenumbercursor.moveToFirst();
                                    if(!phonenumbercursor.isAfterLast()){
                                        mobileno = phonenumbercursor.getString(0);
                                    }
                                    phonenumbercursor.close();
                                    vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                                }else{
                                    if(!vaccines.equalsIgnoreCase("")) {
                                        anouncementObject.put("baseEntityId", previousEntityID);
                                        anouncementObject.put("mobileNo", mobileno);
                                        anouncementObject.put("clientType", "mother");
                                        anouncementObject.put("vaccinationDate", startdate);
                                        anouncementObject.put("vaccineDue", vaccines);
                                        payloadArray.put(anouncementObject);
                                    }
                                    vaccines = "";
                                    startdate = format.format(tomorrow.getTime());
                                    previousEntityID = entityID;
                                    Cursor phonenumbercursor = commonRepository.RawCustomQueryForAdapter("select ec_details.value from ec_details where key = 'phoneNumber' and ec_details.base_entity_id = '"+entityID+"'");
                                    phonenumbercursor.moveToFirst();
                                    if(!phonenumbercursor.isAfterLast()){
                                        mobileno = phonenumbercursor.getString(0);
                                    }
                                    phonenumbercursor.close();
                                    VaccineRepository vaccineRepository = VaccinatorApplication.getInstance().vaccineRepository();
                                    List<Vaccine> vaccinesgiven = vaccineRepository.findByEntityId(entityID);
                                    recievedVaccinesmap = receivedVaccines(vaccinesgiven);
                                    anouncementObject = new JSONObject();
                                    vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                                }
                            }else{
                                vaccines = addToVaccine(vaccines,recievedVaccinesmap,cursor.getString(2));
                            }
                            cursor.moveToNext();
                            if(cursor.isAfterLast() && !vaccines.equalsIgnoreCase("")){
                                anouncementObject.put("baseEntityId",previousEntityID);
                                anouncementObject.put("mobileNo",mobileno);
                                anouncementObject.put("clientType","child");
                                anouncementObject.put("vaccinationDate",startdate);
                                anouncementObject.put("vaccineDue",vaccines);
                                payloadArray.put(anouncementObject);
                            }
                        }
                        cursor.close();
                }catch (Exception e){
                    cursor.close();
                }
                Logger.largeLog("anouncement payload",payloadArray.toString());
                return payloadArray.toString();
            }

            private String addToVaccine(String vaccines, Map<String, Date> recievedVaccinesmap, String string) {
                String givenstring = string.replaceAll("(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " ");
                JSONObject vaccinemap = new JSONObject(recievedVaccinesmap);
                Logger.largeLog("anouncement vaccineString",string);

                Logger.largeLog("anouncement vaccinemap",vaccinemap.toString());

                if(vaccines.equalsIgnoreCase("")){
                    if(recievedVaccinesmap.get(string.replaceAll("(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " "))!=null || string.equalsIgnoreCase("bcg2")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("measles2")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("mr2")){

                    }else if(recievedVaccinesmap.get("mr 1")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("measles 1")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("fipv 2")!=null&&string.equalsIgnoreCase("fipv1")){

                    }else{
                        vaccines = string;
                    }
                }else{
                    if(recievedVaccinesmap.get(string.replaceAll("(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " "))!=null|| string.equalsIgnoreCase("bcg2")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("mr 2")!=null&&string.equalsIgnoreCase("measles2")){

                    }else if(recievedVaccinesmap.get("measles 2")!=null&&string.equalsIgnoreCase("mr2")){

                    }else if(recievedVaccinesmap.get("mr 1")!=null&&string.equalsIgnoreCase("measles1")){

                    }else if(recievedVaccinesmap.get("measles 1")!=null&&string.equalsIgnoreCase("mr1")){

                    }else if(recievedVaccinesmap.get("fipv 2")!=null&&string.equalsIgnoreCase("fipv1")){

                    }else {
                        vaccines = vaccines + ","+string;
                    }
                }
                return vaccines;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(sentmessages){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setTitle("Announcement");
                    builder1.setMessage("anouncement messages sent");
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
//                    Toast.makeText(context,"anouncement messages sent",Toast.LENGTH_LONG).show();
                }
            }
        }


        ).execute();
    }
}
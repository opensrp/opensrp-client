package util.uniqueIdGenerator;

import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.gizi.LoginActivity;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.sync.AdditionalSyncService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.domain.FetchStatus.fetched;
import static org.ei.opensrp.domain.FetchStatus.fetchedFailed;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;
import static org.ei.opensrp.domain.ResponseStatus.failure;
import static org.ei.opensrp.util.Log.logDebug;
import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logInfo;

/**
  * Created by Dimas on 9/7/2015.
  */
public class UniqueIdService implements AdditionalSyncService {

    public static final String UNIQUE_ID_PATH = "unique-id";
    public static final String LAST_USED_ID_PATH = "last-used-id";
    public static final String REFILL_UNIQUE_ID = "refill-unique-id";
    private final HTTPAgent httpAgent;
    private DristhiConfiguration configuration;
    private UniqueIdController uniqueIdController;
    private AllSettingsINA allSettings;
    private AllSharedPreferences allSharedPreferences;

    public UniqueIdService(HTTPAgent httpAgent, DristhiConfiguration configuration,
                           UniqueIdController uniqueIdController, AllSettingsINA allSettings,
                           AllSharedPreferences allSharedPreferences) {
        this.httpAgent = httpAgent;
        this.configuration = configuration;
        this.uniqueIdController = uniqueIdController;
        this.allSettings = allSettings;
        this.allSharedPreferences = allSharedPreferences;
    }

    public FetchStatus sync() {
        FetchStatus dataStatus = nothingFetched;
        int lastUsedId = Integer.parseInt(allSettings.fetchLastUsedId());
        int currentId = Integer.parseInt(allSettings.fetchCurrentId());

            if (currentId > lastUsedId) {
            lastUsedId = currentId;
            pushLastUsedIdToServer(lastUsedId+ "");
            allSettings.saveLastUsedId(lastUsedId+ "");
        } else if (lastUsedId > currentId) {
            currentId = lastUsedId;
            allSettings.saveCurrentId(currentId+ "");
        }

            if (currentId == 0) {
            ResponseStatus status = getLastUsedId(allSharedPreferences.fetchRegisteredANM(), allSettings.fetchANMPassword());
            if (status == ResponseStatus.success) {
                    dataStatus = fetched;
                }
        }

            if (uniqueIdController.needToRefillUniqueId()) {
            dataStatus = refillUniqueId();
        }

            return dataStatus;
    }

    public Response<String> pullUniqueIdFromServer(String username, String password) {
//        String baseURL = configuration.dristhiBaseURL();
        while (true) {
            String uri = "http://118.91.130.18:8080/openmrs/module/idgen/exportIdentifiers.form?source=1&numberToGenerate="+Integer.toString(LoginActivity.generator.UNIQUE_ID_LENGTH_REQUEST)+"&username="+username+"&password="+password;
            Response<String> response = httpAgent.fetchWithCredentials(uri, username, password);
            if (response.isFailure()) {
                    logError(format("Unique id pull failed"));
                    return new Response<>(failure, "");
                }
            logDebug(format("Unique id fetched"));
            return new Response<>(response.status(), response.payload() == null ? "" : response.payload());
        }
    }

    public ResponseStatus getLastUsedId(String username, String password) {
        String baseURL = configuration.dristhiBaseURL();
        while (true) {
            String uri = format("{0}/{1}",
                            baseURL,
                            LAST_USED_ID_PATH);
            Response<String> response = httpAgent.fetchWithCredentials(uri, username, password);
            if (response.isFailure()) {
                    logError(format("Last used id pull failed"));
                    return ResponseStatus.failure;
                } else if (response.payload().isEmpty()) {
                    logError(format("Last used Id empty"));
                    return ResponseStatus.failure;
                }
            logDebug(format("Unique id fetched"));

            try {
                JSONObject jsonObject = new JSONObject(response.payload());
                String lastUsedId = jsonObject.getString("lastUsedId");
                allSettings.saveLastUsedId(lastUsedId);
                allSettings.saveCurrentId(lastUsedId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ResponseStatus.success;
        }
    }

    public void pushLastUsedIdToServer(String lastUsedId) {
        JSONObject json = new JSONObject();
        try {
            json.put("lastUsedId", lastUsedId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        String jsonPayload = json.toString();
        Response<String> response = httpAgent.post(
                    format("{0}/{1}",
                                    configuration.dristhiBaseURL(),
                                    LAST_USED_ID_PATH),
                    jsonPayload);
        if (response.isFailure()) {
            logError(format("Last used id sync failed. Unique Id:  {0}", lastUsedId));
            return;
        }
        logInfo(format("Last used id sync successfully. Unique Id:  {0}", lastUsedId));
    }

    public FetchStatus refillUniqueId() {
        Response<String> response = httpAgent.post(
                    format("{0}/{1}",
                                    configuration.dristhiBaseURL(),
                                    REFILL_UNIQUE_ID), "");
        if (response.isFailure()) {
            logError(format("Refill unique id sync failed"));
            return fetchedFailed;
        } else {
            String baseURL = configuration.dristhiBaseURL();
            while (true) {
                    String uri = format("{0}/{1}",
                                    baseURL,
                                    UNIQUE_ID_PATH);
                    Response<String> refillResponse = httpAgent.fetch(uri);
                    //System.out.println(refillResponse.toString());
                    if (refillResponse.isFailure()) {
                            logError(format("Unique id pull failed"));
                            return fetchedFailed;
                        }
                    logDebug(format("Unique id fetched"));
                    saveJsonResponseToUniqueId(refillResponse.payload());
                    return fetched;
                }
        }

    }

//    public void refillUniqueId(String username, String password){
//        this.syncUniqueIdFromServer(username,password);
//    }
//
//    public boolean uniqueIdReachLimit(int limit){
//        return uniqueIdController.needToRefillUniqueId();
//    }

    public void saveJsonResponseToUniqueId(String payload) {
        if (payload != null) {
            try {
                    JSONObject ids = new JSONObject(payload);
                    JSONArray uniqueId = ids.getJSONArray("identifiers");
                    for (int i = 0; i < uniqueId.length(); i++) {
                        //System.out.println("unique id "+i+", : "+uniqueId.getString(i));
                        }
                    for (int i = 0; i < uniqueId.length(); i++) {
                        uniqueIdController.saveUniqueId(uniqueId.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    public FetchStatus syncUniqueIdFromServer(String username, String password) {
        Response<String> uniqueIds = pullUniqueIdFromServer(username, password);
        if (uniqueIds.isFailure()) {
            return fetchedFailed;
        }
        if (uniqueIds.payload().isEmpty()) {
            return nothingFetched;
        }
        saveJsonResponseToUniqueId(uniqueIds.payload());
        return fetched;
    }

}
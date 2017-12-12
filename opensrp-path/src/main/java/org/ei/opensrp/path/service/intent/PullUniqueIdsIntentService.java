package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.UniqueIdRepository;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.util.FileUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import util.PathConstants;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class PullUniqueIdsIntentService extends IntentService {
    private static final String TAG = PullUniqueIdsIntentService.class.getCanonicalName();

    public static final String ID_URL = "/uniqueids/get";
    public static final String IDENTIFIERS = "identifiers";
    private UniqueIdRepository uniqueIdRepo;


    public PullUniqueIdsIntentService() {

        super("PullUniqueOpenMRSUniqueIdsService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            int numberToGenerate;
            if (uniqueIdRepo.countUnUsedIds() == 0) { // first time pull no ids at all
                numberToGenerate = PathConstants.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
            } else if (uniqueIdRepo.countUnUsedIds() <= 250) { //maintain a minimum of 250 else skip this pull
                numberToGenerate = PathConstants.OPENMRS_UNIQUE_ID_BATCH_SIZE;
            } else {
                return;
            }
            JSONObject ids = fetchOpenMRSIds(PathConstants.openMRS_ID_Source(), numberToGenerate);
            if (ids != null && ids.has(IDENTIFIERS)) {
                parseResponse(ids);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private JSONObject fetchOpenMRSIds(int source, int numberToGenerate) throws Exception {
        HTTPAgent httpAgent = VaccinatorApplication.getInstance().context().getHttpAgent();
        String baseUrl = VaccinatorApplication.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + ID_URL + "?source=" + source + "&numberToGenerate=" + numberToGenerate;
        Log.i(PullUniqueIdsIntentService.class.getName(), "URL: " + url);

        if (httpAgent == null) {
            throw new Exception(ID_URL + " http agent is null");
        }


        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {

            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            String password = Context.getInstance().allSettings().fetchANMPassword();

            String localUrlString = PathConstants.openmrsUrl() +  PathConstants.OPENMRS_IDGEN_URL + "?source="+PathConstants.openMRS_ID_Source()+"&numberToGenerate=" + numberToGenerate + "&username=" + userName + "&password=" + password;
//           // Convert the incoming data string to a URL.
            resp = httpAgent.fetch(localUrlString);
//            localURL = new URL(localUrlString);
            if (resp.isFailure()) {
                throw new Exception( ID_URL + " not returned data");
            }
        }



        return new JSONObject((String) resp.payload());
    }
    /**
     * @param connection object; note: before calling this function,
     *                   ensure that the connection is already be open, and any writes to
     *                   the connection's output stream should have already been completed.
     * @return String containing the body of the connection response or
     * null if the input stream could not be read correctly
     */
    private String readInputStreamToString(HttpURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "Error reading InputStream");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i(TAG, "Error closing InputStream");
                }
            }
        }

        return result;
    }

    private void parseResponse(JSONObject idsFromOMRS) throws Exception {
        JSONArray jsonArray = idsFromOMRS.getJSONArray(IDENTIFIERS);
        if (jsonArray != null && jsonArray.length() > 0) {
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                ids.add(jsonArray.getString(i));
            }
            uniqueIdRepo.bulkInserOpenmrsIds(ids);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        uniqueIdRepo = VaccinatorApplication.getInstance().uniqueIdRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}

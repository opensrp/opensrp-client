package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.UniqueIdRepository;
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
    private UniqueIdRepository uniqueIdRepo;


    public PullUniqueIdsIntentService() {

        super("PullUniqueOpenMRSUniqueIdsService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        URL localURL;
        try {
            int numberToGenerate = 0;

            if (uniqueIdRepo.countUnUsedIds() == 0) {// first time pull no ids at all
                numberToGenerate = PathConstants.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
            } else if (uniqueIdRepo.countUnUsedIds() <= 250) { //maintain a minimum of 250 else skip this pull
                numberToGenerate = PathConstants.OPENMRS_UNIQUE_ID_BATCH_SIZE;
            } else {
                return;
            }

            String userName = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            String password = Context.getInstance().allSettings().fetchANMPassword();

            String localUrlString = PathConstants.openmrsUrl() +  PathConstants.OPENMRS_IDGEN_URL + "?source="+PathConstants.OPENMRS_UNIQUE_ID_SOURCE+"&numberToGenerate=" + numberToGenerate + "&username=" + userName + "&password=" + password;
//           // Convert the incoming data string to a URL.

            localURL = new URL(localUrlString);
             /*
             * Tries to open a connection to the URL. If an IO error occurs, this throws an
             * IOException
             */
            URLConnection localURLConnection = localURL.openConnection();

            // If the connection is an HTTP connection, continue
            if ((localURLConnection instanceof HttpURLConnection)) {


                // Casts the connection to a HTTP connection
                HttpURLConnection localHttpURLConnection = (HttpURLConnection) localURLConnection;

                // Sets the user agent for this request.
                localHttpURLConnection.setRequestProperty("User-Agent", FileUtilities.getUserAgent(Context.getInstance().applicationContext()));


                // Gets a response code from the RSS server
                int responseCode = localHttpURLConnection.getResponseCode();

                switch (responseCode) {

                    // If the response is OK
                    case HttpURLConnection.HTTP_OK:
                        // Gets the last modified data for the URL
                        parseResponse(localHttpURLConnection);

                        break;
                    default:
                        Log.e(TAG, "Error when fetching unique ids from openmrs server " + localUrlString + " Response code" + responseCode);

                }

                // Reports that the feed retrieval is complete.
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }


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

    private void parseResponse(HttpURLConnection connection) throws Exception {
        String response = readInputStreamToString(connection);
        JSONObject responseJson = new JSONObject(response);
        JSONArray jsonArray = responseJson.getJSONArray("identifiers");
        if (jsonArray != null && jsonArray.length() > 0) {
            List<String> ids = new ArrayList<String>();
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

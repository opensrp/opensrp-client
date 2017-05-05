package util;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.fragment.AdvancedSearchFragment;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

/**
 * Created by keyman on 26/01/2017.
 */
public class MoveToMyCatchmentUtils {

    public static void moveToMyCatchment(final List<String> ids, final Listener<JSONObject> listener, final ProgressBar progressBar) {

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
                progressBar.setVisibility(VISIBLE);
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                listener.onEvent(result);
                progressBar.setVisibility(View.GONE);
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

        String paramString = "?baseEntityId=" + urlEncode(idString.trim()) + "&serverVersion=0";
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
}

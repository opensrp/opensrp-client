package util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.ei.opensrp.path.application.VaccinatorApplication;

/**
 * Created by Jason Rogena - jrogena@ona.io on 08/06/2017.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getName();
    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) VaccinatorApplication
                    .getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return false;
    }
}

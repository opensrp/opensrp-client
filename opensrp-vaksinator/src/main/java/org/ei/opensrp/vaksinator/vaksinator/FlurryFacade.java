package org.ei.opensrp.vaksinator.vaksinator;


import android.content.Context;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import java.util.Map;

/**
 * Created by Null on 2016-09-19.
 */

public class FlurryFacade {

    //vaksinator EC prototype 1
    private static final String flurry_key = "QQNP4YT9BYBD8289J2BD";

    /*//testing key
    private static final String flurry_key = "QRQZ6KSKBWYNKJ5ZPDNR";*/

    public static void logEvent(String event) {
        FlurryAgent.logEvent(event);
    }

        public static void logEvent(String event, Map<String, String> map) {
        FlurryAgent.logEvent(event, map);
    }

        public static void setUserId(String userId) {
        FlurryAgent.setUserId(userId);
    }

    public static void init(Context context) {
// Configure Flurry
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogEvents(true);
        FlurryAgent.setLogLevel(Log.VERBOSE);

// init Flurry
        FlurryAgent.init(context, flurry_key);
    }

    
    }
package org.ei.opensrp.gizi.gizi;


import android.content.Context;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import java.util.Map;

/**
 * Created by Null on 2016-09-19.
 */

public class FlurryFacade {

//    private static final String flurry_key = "3VPFC3PXZQ43PND539DR";

    //flurry key gizi 2.1
    //        private static final String flurry_key = "HTV5HBKKNCNCCBWT338M";

    //flurry key Gizi EC prototype 1
    private static final String flurry_key = "SQD6SRG84PZ94DM3BM38";

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
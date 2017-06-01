package org.ei.opensrp.logger;

import android.util.Log;

/**
 * Created by habib on 5/30/17.
 */
public class Logger {

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
}

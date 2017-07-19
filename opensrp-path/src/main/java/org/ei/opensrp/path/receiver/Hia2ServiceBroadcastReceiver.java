package org.ei.opensrp.path.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens for broadcast responses from {@link org.ei.opensrp.path.service.intent.HIA2IntentService}
 * service
 *
 * Created by Jason Rogena - jrogena@ona.io on 10/07/2017.
 */

public class Hia2ServiceBroadcastReceiver  extends BroadcastReceiver {
    private static final String TAG = Hia2ServiceListener.class.getCanonicalName();
    public static final String TYPE = "TYPE";
    public static final String ACTION_SERVICE_DONE = "HIA2_SERVICE_DONE";
    public static final String TYPE_GENERATE_DAILY_INDICATORS = "GENERATE_DAILY_INDICATORS";
    public static final String TYPE_GENERATE_MONTHLY_REPORT = "GENERATE_MONTHLY_REPORT";
    private static Hia2ServiceBroadcastReceiver singleton;
    private final List<Hia2ServiceListener> listeners;

    public static void init(Context context) {
        if (singleton != null) {
            destroy(context);
        }

        singleton = new Hia2ServiceBroadcastReceiver();
        context.registerReceiver(singleton, new IntentFilter(ACTION_SERVICE_DONE));
    }

    public static void destroy(Context context) {
        try {
            if (singleton != null) {
                context.unregisterReceiver(singleton);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static Hia2ServiceBroadcastReceiver getInstance() {
        return singleton;
    }

    public Hia2ServiceBroadcastReceiver() {
        this.listeners = new ArrayList<>();
    }

    public void addHia2ServiceListener(Hia2ServiceListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeHia2ServiceListener(Hia2ServiceListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(TYPE);
        for (Hia2ServiceListener curListener : listeners) {
            curListener.onServiceFinish(type);
        }
    }

    public interface Hia2ServiceListener {
        void onServiceFinish(String actionType);
    }
}

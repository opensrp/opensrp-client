package org.ei.opensrp.path.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import org.ei.opensrp.domain.FetchStatus;

import java.util.ArrayList;

/**
 * Created by Jason Rogena - jrogena@ona.io on 12/05/2017.
 */

public class SyncStatusBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC_STATUS = "sync_status";
    public static final String EXTRA_FETCH_STATUS = "fetch_status";
    private static SyncStatusBroadcastReceiver singleton;
    private boolean isSyncing;

    private final ArrayList<SyncStatusListener> syncStatusListeners;

    public static void init(Context context) {
        if (singleton != null) {
            destroy(context);
        }

        singleton = new SyncStatusBroadcastReceiver();
        context.registerReceiver(singleton,
                new IntentFilter(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS));
    }

    public static void destroy(Context context) {
        if (singleton != null) {
            context.unregisterReceiver(singleton);
        }
    }

    public static SyncStatusBroadcastReceiver getInstance() {
        return singleton;
    }

    public SyncStatusBroadcastReceiver() {
        syncStatusListeners = new ArrayList<>();
    }

    public void addSyncStatusListener(SyncStatusListener syncStatusListener) {
        if (!syncStatusListeners.contains(syncStatusListener)) {
            syncStatusListeners.add(syncStatusListener);
        }
    }

    public void removeSyncStatusListener(SyncStatusListener syncStatusListener) {
        if (syncStatusListeners.contains(syncStatusListener)) {
            syncStatusListeners.remove(syncStatusListener);
        }
    }

    public interface SyncStatusListener {
        void onSyncStart();

        void onSyncComplete(FetchStatus fetchStatus);
    }

    public boolean isSyncing() {
        return isSyncing;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            FetchStatus fetchStatus = (FetchStatus) data.getSerializable(EXTRA_FETCH_STATUS);
            if (fetchStatus.equals(FetchStatus.fetchStarted)) {
                isSyncing = true;
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncStart();
                }
            } else {
                isSyncing = false;
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncComplete(fetchStatus);
                }
            }
        }
    }
}

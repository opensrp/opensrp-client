package org.ei.opensrp.path.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.ei.opensrp.domain.FetchStatus;

import java.util.ArrayList;

/**
 * Created by Jason Rogena - jrogena@ona.io on 12/05/2017.
 */

public class SyncStatusBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC_STATUS = "sync_status";
    public static final String EXTRA_FETCH_STATUS = "fetch_status";

    private final ArrayList<SyncStatusListener> syncStatusListeners;

    public SyncStatusBroadcastReceiver() {
        syncStatusListeners = new ArrayList<>();
    }

    public void addSyncStatusListener(SyncStatusListener syncStatusListener) {
        syncStatusListeners.add(syncStatusListener);
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


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        if (data != null) {
            FetchStatus fetchStatus = (FetchStatus) data.getSerializable(EXTRA_FETCH_STATUS);
            if (fetchStatus.equals(FetchStatus.fetchStarted)) {
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncStart();
                }
            } else {
                for (SyncStatusListener syncStatusListener : syncStatusListeners) {
                    syncStatusListener.onSyncComplete(fetchStatus);
                }
            }
        }
    }
}

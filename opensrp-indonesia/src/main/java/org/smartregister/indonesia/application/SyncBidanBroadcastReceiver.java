package org.smartregister.indonesia.application;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.smartregister.indonesia.LoginActivity;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;

import static org.smartregister.util.Log.logInfo;

/**
 + * Created by Dimas on 9/17/2015.
 + */
public class SyncBidanBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                        context,
                        org.smartregister.Context.getInstance().actionService(),
                        org.smartregister.Context.getInstance().formSubmissionSyncService(),
                        new SyncProgressIndicator(),
                        org.smartregister.Context.getInstance().allFormVersionSyncService());

        updateActionsTask.setAdditionalSyncService(LoginActivity.generator.uniqueIdService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}
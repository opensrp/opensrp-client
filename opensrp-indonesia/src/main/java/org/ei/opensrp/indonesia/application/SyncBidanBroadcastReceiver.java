package org.ei.opensrp.indonesia.application;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ei.opensrp.indonesia.LoginActivity;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;

import static org.ei.opensrp.util.Log.logInfo;

/**
 + * Created by Dimas on 9/17/2015.
 + */
public class SyncBidanBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                        context,
                        org.ei.opensrp.Context.getInstance().actionService(),
                        org.ei.opensrp.Context.getInstance().formSubmissionSyncService(),
                        new SyncProgressIndicator(),
                        org.ei.opensrp.Context.getInstance().allFormVersionSyncService());

        updateActionsTask.setAdditionalSyncService(LoginActivity.generator.uniqueIdService());

        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }
}
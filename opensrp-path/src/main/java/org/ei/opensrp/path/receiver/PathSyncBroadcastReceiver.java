package org.ei.opensrp.path.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ei.opensrp.path.sync.PathAfterFetchListener;
import org.ei.opensrp.path.sync.PathUpdateActionsTask;
import org.ei.opensrp.sync.SyncProgressIndicator;

import static org.ei.opensrp.util.Log.logInfo;

public class PathSyncBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo("Sync alarm triggered. Trying to Sync.");

        PathUpdateActionsTask pathUpdateActionsTask = new PathUpdateActionsTask(
                context,
                org.ei.opensrp.Context.getInstance().actionService(),
                org.ei.opensrp.Context.getInstance().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                org.ei.opensrp.Context.getInstance().allFormVersionSyncService());

        pathUpdateActionsTask.updateFromServer(new PathAfterFetchListener());

    }

}


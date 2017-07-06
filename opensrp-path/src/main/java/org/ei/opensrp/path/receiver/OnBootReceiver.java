package org.ei.opensrp.path.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ei.opensrp.path.sync.PathUpdateActionsTask;

public class OnBootReceiver extends BroadcastReceiver {
    private Intent serviceIntent;

    {
        serviceIntent = new Intent();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            serviceIntent.putExtra(Intent.ACTION_BOOT_COMPLETED, true);
            this.restartAlarms(context);
        }
    }

    public void restartAlarms(Context context) {
        PathUpdateActionsTask.setAlarms(context);
    }

}

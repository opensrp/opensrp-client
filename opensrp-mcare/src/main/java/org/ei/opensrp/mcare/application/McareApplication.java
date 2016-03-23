package org.ei.opensrp.mcare.application;

import android.content.Intent;
import android.content.res.Configuration;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.ei.opensrp.Context;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;

import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by koros on 1/22/16.
 */
@ReportsCrashes(
        formKey = "",
        formUri = "https://drishtiapp.cloudant.com/acra-drishtiapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        formUriBasicAuthLogin = "sompleakereepeavoldiftle",
        formUriBasicAuthPassword = "ecUMrMeTKf1X1ODxHqo3b43W",
        mode = ReportingInteractionMode.SILENT
)
public class McareApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);
        super.onCreate();
        ACRA.init(this);

        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        applyUserLanguagePreference();
        cleanUpSyncState();
    }

    @Override
    public void logoutCurrentUser(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logInfo("Application is terminating. Stopping Dristhi Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
    }

    private void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}

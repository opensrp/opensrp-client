package org.ei.opensrp.immunization.application;

import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.ihs.odkate.base.Odkate;

import org.ei.opensrp.Context;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;

import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by koros on 2/3/16.
 */
public class VaccinatorApplication extends Odkate implements DrishtiApplication{
    private Locale locale = null;
    private Context context;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "Starting vaccinator application");
        super.onCreate();
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        applyUserLanguagePreference();
        cleanUpSyncState();
        startCESyncService(getApplicationContext());
        ConfigSyncReceiver.scheduleFirstSync(getApplicationContext());
        Log.i(getClass().getName(), "Loaded vaccinator application");
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    private void startCESyncService(android.content.Context context){
        CESyncReceiver.scheduleFirstSync(context);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        super.onTerminate();
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

    @Override
    public void logoutCurrentUser() {
        Log.i(getClass().getName(), "Logging out");
    }
}

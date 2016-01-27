package org.ei.opensrp.indonesia.view.activity;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.indonesia.LoginActivity;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.lib.ErrorReportingFacade;
import org.ei.opensrp.indonesia.view.receiver.SyncBidanBroadcastReceiver;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;

import java.util.Locale;

import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by Dimas Ciputra on 9/18/15.
 */
public class BidanApplication extends DrishtiApplication {
    private Locale locale = null;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DrishtiSyncScheduler.setReceiverClass(SyncBidanBroadcastReceiver.class);

        ErrorReportingFacade.initErrorHandler(getApplicationContext());
        FlurryFacade.init(this);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        applyUserLanguagePreference();
        cleanUpSyncState();

        //error handling
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Bidan Sync scheduler and resetting isSyncInProgress setting.");
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
    public void logoutCurrentUser(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        //context.userService().logoutSession();
    }
}

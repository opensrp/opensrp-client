package org.smartregister.indonesia.application;

import android.content.Intent;
import android.content.res.Configuration;

import com.crashlytics.android.Crashlytics;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.indonesia.LoginActivity;
import org.smartregister.indonesia.lib.ErrorReportingFacade;
import org.smartregister.indonesia.lib.FlurryFacade;
import org.smartregister.indonesia.repository.BidanRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.sync.DrishtiSyncScheduler;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;
@ReportsCrashes(
        formKey = "",
        formUri = "https://drishtiapp.cloudant.com/acra-drishtiapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        formUriBasicAuthLogin = "sompleakereepeavoldiftle",
        formUriBasicAuthPassword = "ecUMrMeTKf1X1ODxHqo3b43W",
        mode = ReportingInteractionMode.SILENT
)

public class BidanApplication extends DrishtiApplication {

    private static final String TAG = BidanApplication.class.getName();
    private static SettingsRepository settingsRepository;
//    private UniqueIdRepository uniqueIdRepository;

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals("ec_kartu_ibu")) {
            return new String[]{"namalengkap", "namaSuami"};
        } else if (tableName.equals("ec_anak")) {
            return new String[]{"namaBayi"};
        } else if (tableName.equals("ec_ibu")) {
            return new String[]{"namalengkap", "namaSuami"};
        } else if (tableName.equals("ec_pnc")) {
            return new String[]{"namalengkap", "namaSuami"};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals("ec_kartu_ibu")) {
            return new String[]{"namalengkap", "umur", "noIbu", "htp"};
        } else if (tableName.equals("ec_anak")) {
            return new String[]{"namaBayi", "tanggalLahirAnak"};
        } else if (tableName.equals("ec_ibu")) {
            return new String[]{"namalengkap", "umur", "noIbu", "pptest", "htp"};
        } else if (tableName.equals("ec_pnc")) {
            return new String[]{"namalengkap", "umur", "noIbu", "keadaanIbu"};
        }
        return null;
    }

    private static String[] getFtsMainConditions(String tableName) {
        if (tableName.equals("ec_kartu_ibu")) {
            return new String[]{"is_closed", "jenisKontrasepsi"};
        } else if (tableName.equals("ec_anak")) {
            return new String[]{"is_closed", "relational_id"};
        } else if (tableName.equals("ec_ibu")) {
            return new String[]{"is_closed", "type", "pptest", "kartuIbuId"};
        } else if (tableName.equals("ec_pnc")) {
            return new String[]{"is_closed", "keadaanIbu", "type"};
        }
        return null;
    }

    private static String[] getFtsTables() {
        return new String[]{"ec_kartu_ibu", "ec_anak", "ec_ibu", "ec_pnc"};
    }

    public static CommonFtsObject createCommonFtsObject() {
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for (String ftsTable : commonFtsObject.getTables()) {
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            commonFtsObject.updateMainConditions(ftsTable, getFtsMainConditions(ftsTable));
        }
        return commonFtsObject;
    }

    public static Repository initializeRepositoryForUniqueId() {
        return null;
    }

    public static synchronized BidanApplication getInstance() {
        return (BidanApplication) mInstance;
    }

    public static SettingsRepository getSettingsRepositoryforUniqueId() {
        return settingsRepository();
    }

    protected static SettingsRepository settingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new SettingsRepository();
        }
        return settingsRepository;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mInstance = this;

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context());

        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        applyUserLanguagePreference();
        cleanUpSyncState();

    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context());

            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }

    @Override
    public void logoutCurrentUser() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

  /*  public UniqueIdRepository uniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository((BidanRepository) getRepository());
        }
        return uniqueIdRepository;
    }*/

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

   /* @Override
    public Repository getRepository() {

        try {
            if (repository == null) {
                repository = new BidanRepository(getInstance().getApplicationContext(), context());
                uniqueIdRepository();
//                eventClientRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }*/

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public Context context() {
        return context;
    }


}

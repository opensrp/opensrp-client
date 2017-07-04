package org.ei.opensrp.path.application;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.LoginActivity;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.receiver.PathSyncBroadcastReceiver;
import org.ei.opensrp.path.receiver.SyncStatusBroadcastReceiver;
import org.ei.opensrp.path.repository.HIA2IndicatorsRepository;
import org.ei.opensrp.path.repository.DailyTalliesRepository;
import org.ei.opensrp.path.repository.MonthlyTalliesRepository;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.ei.opensrp.path.repository.UniqueIdRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.repository.ZScoreRepository;
import org.ei.opensrp.path.sync.PathUpdateActionsTask;
import org.ei.opensrp.repository.Repository;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.TimeChangedBroadcastReceiver;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import util.PathConstants;
import util.VaccinateActionUtils;
import util.VaccinatorUtils;

import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by koros on 2/3/16.
 */
public class VaccinatorApplication extends DrishtiApplication
        implements TimeChangedBroadcastReceiver.OnTimeChangedListener {

    private static final String TAG = "VaccinatorApplication";
    private Locale locale = null;
    private Context context;
    private static CommonFtsObject commonFtsObject;
    private WeightRepository weightRepository;
    private UniqueIdRepository uniqueIdRepository;
    private DailyTalliesRepository dailyTalliesRepository;
    private MonthlyTalliesRepository monthlyTalliesRepository;
    private HIA2IndicatorsRepository hIA2IndicatorsRepository;
    private VaccineRepository vaccineRepository;
    private ZScoreRepository zScoreRepository;
    private RecurringServiceRecordRepository recurringServiceRecordRepository;
    private RecurringServiceTypeRepository recurringServiceTypeRepository;
    private boolean lastModified;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        DrishtiSyncScheduler.setReceiverClass(PathSyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        SyncStatusBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.init(this);
        TimeChangedBroadcastReceiver.getInstance().addOnTimeChangedListener(this);

        applyUserLanguagePreference();
        cleanUpSyncState();
        initOfflineSchedules();
        setCrashlyticsUser(context);
        PathUpdateActionsTask.setAlarms(this);

    }

    public static synchronized VaccinatorApplication getInstance() {
        return (VaccinatorApplication) mInstance;
    }

    @Override
    public void logoutCurrentUser() {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

    public void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }


    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Bidan Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        SyncStatusBroadcastReceiver.destroy(this);
        TimeChangedBroadcastReceiver.destroy(this);
        super.onTerminate();
    }

    public void applyUserLanguagePreference() {
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

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(PathConstants.CHILD_TABLE_NAME)) {
            String[] ftsSearchFileds = {"zeir_id", "epi_card_number", "first_name", "last_name"};
            return ftsSearchFileds;
        } else if (tableName.equals(PathConstants.MOTHER_TABLE_NAME)) {
            String[] ftsSearchFileds = {"zeir_id", "epi_card_number", "first_name", "last_name", "father_name", "husband_name", "contact_phone_number"};
            return ftsSearchFileds;
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {


        if (tableName.equals(PathConstants.CHILD_TABLE_NAME)) {
            ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");
            List<String> names = new ArrayList<>();
            names.add("first_name");
            names.add("dob");
            names.add("zeir_id");
            names.add("last_interacted_with");
            names.add("inactive");
            names.add("lost_to_follow_up");

            for (VaccineRepo.Vaccine vaccine : vaccines) {
                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.display()));
            }

            return names.toArray(new String[names.size()]);
        } else if (tableName.equals(PathConstants.MOTHER_TABLE_NAME)) {
            String[] sortFields = {"first_name", "dob", "zeir_id", "last_interacted_with"};
            return sortFields;
        }
        return null;
    }

    private static String[] getFtsTables() {
        String[] ftsTables = {PathConstants.CHILD_TABLE_NAME, PathConstants.MOTHER_TABLE_NAME};
        return ftsTables;
    }

    public static Map<String, Pair<String, Boolean>> getAlertScheduleMap() {
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");
        Map<String, Pair<String, Boolean>> map = new HashMap<String, Pair<String, Boolean>>();
        for (VaccineRepo.Vaccine vaccine : vaccines) {
            map.put(vaccine.display(), Pair.create(PathConstants.CHILD_TABLE_NAME, false));
        }
        return map;
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        commonFtsObject.updateAlertScheduleMap(getAlertScheduleMap());
        return commonFtsObject;
    }

    /**
     * This method sets the Crashlytics user to whichever username was used to log in last. It only
     * does so if the app is not built for debugging
     *
     * @param context The user's context
     */
    public static void setCrashlyticsUser(Context context) {
        if (!BuildConfig.DEBUG
                && context != null && context.userService() != null
                && context.userService().getAllSharedPreferences() != null) {
            Crashlytics.setUserName(context.userService().getAllSharedPreferences().fetchRegisteredANM());
        }
    }

    private void grantPhotoDirectoryAccess() {
        Uri uri = FileProvider.getUriForFile(this,
                "com.vijay.jsonwizard.fileprovider",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        grantUriPermission("com.vijay.jsonwizard", uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new PathRepository(getInstance().getApplicationContext());
                weightRepository();
                vaccineRepository();
                uniqueIdRepository();
                recurringServiceTypeRepository();
                recurringServiceRecordRepository();
            }
        } catch (UnsatisfiedLinkError e) {
            logError("Error on getRepository: " + e);

        }
        return repository;
    }


    public WeightRepository weightRepository() {
        if (weightRepository == null) {
            weightRepository = new WeightRepository((PathRepository) getRepository());
        }
        return weightRepository;
    }

    public Context context() {
        return context;
    }

    public VaccineRepository vaccineRepository() {
        if (vaccineRepository == null) {
            vaccineRepository = new VaccineRepository((PathRepository) getRepository(), createCommonFtsObject(), context.alertService());
        }
        return vaccineRepository;
    }

    public ZScoreRepository zScoreRepository() {
        if (zScoreRepository == null) {
            zScoreRepository = new ZScoreRepository((PathRepository) getRepository());
        }

        return zScoreRepository;
    }

    public UniqueIdRepository uniqueIdRepository() {
        if (uniqueIdRepository == null) {
            uniqueIdRepository = new UniqueIdRepository((PathRepository) getRepository());
        }
        return uniqueIdRepository;
    }

    public DailyTalliesRepository dailyTalliesRepository() {
        if (dailyTalliesRepository == null) {
            dailyTalliesRepository = new DailyTalliesRepository((PathRepository) getRepository());
        }
        return dailyTalliesRepository;
    }

    public MonthlyTalliesRepository monthlyTalliesRepository() {
        if (monthlyTalliesRepository == null) {
            monthlyTalliesRepository = new MonthlyTalliesRepository((PathRepository) getRepository());
        }

        return monthlyTalliesRepository;
    }

    public HIA2IndicatorsRepository hIA2IndicatorsRepository() {
        if (hIA2IndicatorsRepository == null) {
            hIA2IndicatorsRepository = new HIA2IndicatorsRepository((PathRepository) getRepository());
        }
        return hIA2IndicatorsRepository;
    }

    public RecurringServiceTypeRepository recurringServiceTypeRepository() {
        if (recurringServiceTypeRepository == null) {
            recurringServiceTypeRepository = new RecurringServiceTypeRepository((PathRepository) getRepository());
        }
        return recurringServiceTypeRepository;
    }

    public RecurringServiceRecordRepository recurringServiceRecordRepository() {
        if (recurringServiceRecordRepository == null) {
            recurringServiceRecordRepository = new RecurringServiceRecordRepository((PathRepository) getRepository());
        }
        return recurringServiceRecordRepository;
    }

    public boolean isLastModified() {
        return lastModified;
    }

    public void setLastModified(boolean lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public void onTimeChanged() {
        Toast.makeText(this, R.string.device_time_changed, Toast.LENGTH_LONG).show();
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    @Override
    public void onTimeZoneChanged() {
        Toast.makeText(this, R.string.device_timezone_changed, Toast.LENGTH_LONG).show();
        context.userService().forceRemoteLogin();
        logoutCurrentUser();
    }

    private void initOfflineSchedules() {
        try {
            JSONArray childVaccines = new JSONArray(VaccinatorUtils.getSupportedVaccines(this));
            JSONArray specialVaccines = new JSONArray(VaccinatorUtils.getSpecialVaccines(this));
            VaccineSchedule.init(childVaccines, specialVaccines, "child");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

}

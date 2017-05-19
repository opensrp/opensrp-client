package util;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.path.BuildConfig;

/**
 * Created by coder on 2/14/17.
 */
public class PathConstants extends AllConstants {
    public static final String OPENMRS_URL = BuildConfig.OPENMRS_URL;
    public static final int DATABASE_VERSION = BuildConfig.DATABASE_VERSION;

    public static String openmrsUrl() {
        String baseUrl = org.ei.opensrp.Context.getInstance().allSharedPreferences().fetchBaseURL("");
        int lastIndex = baseUrl.lastIndexOf("/");
        baseUrl = baseUrl.substring(0, lastIndex) + "/openmrs";
        return OPENMRS_URL.isEmpty() || OPENMRS_URL == null ? baseUrl : OPENMRS_URL;
    }

    public static final String OPENMRS_IDGEN_URL = BuildConfig.OPENMRS_IDGEN_URL;
    public static final int OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_SOURCE = BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;
    public static final int VACCINE_SYNC_TIME = BuildConfig.VACCINE_SYNC_TIME;
    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
}
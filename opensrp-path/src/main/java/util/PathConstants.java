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
    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String ZSCORE_MALE_URL = "http://www.who.int/childgrowth/standards/wfa_boys_0_5_zscores.txt";
    public static final String ZSCORE_FEMALE_URL = "http://www.who.int/childgrowth/standards/wfa_girls_0_5_zscores.txt";
}
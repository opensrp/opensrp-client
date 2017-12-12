package util;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.repository.AllSharedPreferences;

/**
 * Created by coder on 2/14/17.
 */
public class PathConstants extends AllConstants {
    public static final String OPENMRS_URL = BuildConfig.OPENMRS_URL;
    public static final int DATABASE_VERSION = BuildConfig.DATABASE_VERSION;

    public static String openmrsUrl() {
        String baseUrl = org.ei.opensrp.Context.getInstance().allSharedPreferences().fetchBaseURL("");
        String openmrsUrl = org.ei.opensrp.Context.getInstance().allSharedPreferences().getPreference("OPENMRS_URL");

        int lastIndex = baseUrl.lastIndexOf("/");
        baseUrl = baseUrl.substring(0, lastIndex) + "/openmrs";
        if(!(openmrsUrl.isEmpty() || openmrsUrl == null)){
            return openmrsUrl;
        }
        return OPENMRS_URL.isEmpty() || OPENMRS_URL == null ? baseUrl : OPENMRS_URL;
    }
    public static int openMRS_ID_Source(){
        String openmrs_UNIQUE_ID_SOURCE_preference = org.ei.opensrp.Context.getInstance().allSharedPreferences().getPreference("OPENMRS_UNIQUE_ID_SOURCE");
        try{
            return Integer.parseInt(openmrs_UNIQUE_ID_SOURCE_preference);
        }catch (Exception e){
            return OPENMRS_UNIQUE_ID_SOURCE;
        }
    }
    public static int vaccine_Sync_Time(){
        String openmrs_Vaccine_sync_time_preference = org.ei.opensrp.Context.getInstance().allSharedPreferences().getPreference("VACCINE_SYNC_TIME");
        try{
            return Integer.parseInt(openmrs_Vaccine_sync_time_preference);
        }catch (Exception e){
            return VACCINE_SYNC_TIME;
        }
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

    public static final String CHILD_TABLE_NAME = "ec_child";
    public static final String MOTHER_TABLE_NAME = "ec_mother";
    public static final String CURRENT_LOCATION_ID = "CURRENT_LOCATION_ID";

//    public static String source(){
//        AllSharedPreferences allSharedPreferences = Context.getInstance().allSharedPreferences();
//        allSharedPreferences.
//        return OPENMRS_UNIQUE_ID_SOURCE.isEmpty() || OPENMRS_UNIQUE_ID_SOURCE == null ? baseUrl : OPENMRS_URL;
//    }

    public static final class ServiceType {

        public static final int DATA_SYNCHRONIZATION = 1;
        public static final int DAILY_TALLIES_GENERATION = 2;
        public static final int MONTHLY_TALLIES_GENERATION = 3;
        public static final int PULL_UNIQUE_IDS = 4;
        public static final int VACCINE_SYNC_PROCESSING = 5;
        public static final int WEIGHT_SYNC_PROCESSING = 6;
        public static final int RECURRING_SERVICES_SYNC_PROCESSING = 7;
    }


    public static final class EventType {

        public static final String DEATH = "Death";
    }

    public static final class EntityType {

        public static final String CHILD = "child";
    }

    public static final class EC_CHILD_TABLE {

        public static final String DOD = "dod";
    }

    public static final String DEFAULT_DATE_STRING = "1970-1-1";
}
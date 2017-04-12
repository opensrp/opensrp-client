package util;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.domain.EntityLookUp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;

/**
 * Created by keyman on 26/01/2017.
 */
public class MotherLookUpUtils {
    private static final String TAG = MotherLookUpUtils.class.getName();

    private static final String firstName = "first_name";
    private static final String lastName = "last_name";
    private static final String birthDate = "date_birth";
    private static final String dob = "dob";

    public static void motherLookUp(final Context context, final Map<String, EntityLookUp> map, final Listener<Map<String, List<CommonPersonObject>>> listener, final ProgressBar progressBar) {

        Utils.startAsyncTask(new AsyncTask<Void, Void, Map<String, List<CommonPersonObject>>>() {
            @Override
            protected Map<String, List<CommonPersonObject>> doInBackground(Void... params) {
                publishProgress();
                return lookUp(context, map);
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                if (progressBar != null) {
                    progressBar.setVisibility(VISIBLE);
                }
            }

            @Override
            protected void onPostExecute(Map<String, List<CommonPersonObject>> result) {
                listener.onEvent(result);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }, null);
    }

    private static Map<String, List<CommonPersonObject>> lookUp(Context context, Map<String, EntityLookUp> map) {
        Map<String, List<CommonPersonObject>> results = new HashMap<>();
        if (context == null) {
            return results;
        }

        for (Map.Entry<String, EntityLookUp> entry : map.entrySet()) {
            String entityId = entry.getKey();
            EntityLookUp entityLookUp = entry.getValue();

            if (entityLookUp.isEmpty()) {
                return results;
            }

            String tableName = "ec_" + entityId;

            List<CommonPersonObject> commons = new ArrayList<CommonPersonObject>();

            CommonRepository commonRepository = context.commonrepository(tableName);
            String query = lookUpQuery(entityLookUp.getMap(), tableName);

            Cursor cursor = null;
            try {

                cursor = commonRepository.RawCustomQueryForAdapter(query);
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        CommonPersonObject commonPersonObject = commonRepository.readAllcommonforCursorAdapter(cursor);
                        commons.add(commonPersonObject);

                        cursor.moveToNext();
                    }
                }

                results.put(entityId, commons);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return results;
    }

    private static String lookUpQuery(Map<String, String> entityMap, String tableName) {

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]

                        {
                                "relationalid",
                                "details",
                                "zeir_id",
                                "first_name",
                                "last_name",
                                "gender",
                                "dob",
                                "nrc_number",
                                "contact_phone_number"
                        }

        );
        String query = queryBUilder.mainCondition(getMainConditionString(entityMap));
        return queryBUilder.Endquery(query);
    }


    private static String getMainConditionString(Map<String, String> entityMap) {

        String mainConditionString = "";
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (StringUtils.containsIgnoreCase(key, firstName)) {
                key = firstName;
            }

            if (StringUtils.containsIgnoreCase(key, lastName)) {
                key = lastName;
            }

            if (StringUtils.containsIgnoreCase(key, birthDate)) {
                if (!isDate(value)) {
                    continue;
                }
                key = dob;
            }

            if (!key.equals(dob)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + key + " Like '%" + value + "%'";
                } else {
                    mainConditionString += " AND " + key + " Like '%" + value + "%'";

                }
            } else {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";
                } else {
                    mainConditionString += " AND cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";

                }
            }
        }

        return mainConditionString;

    }

    private static boolean isDate(String dobString) {
        try {
            DateUtil.yyyyMMdd.parse(dobString);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }
}

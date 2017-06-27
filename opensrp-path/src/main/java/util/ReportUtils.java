package util;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.path.domain.Report;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by keyman on 08/02/2017.
 */
public class ReportUtils {
    private static final String TAG = ReportUtils.class.getCanonicalName();


    public static void createReport(Context context, List<JSONObject> hia2Indicators, String reportType) {
        try {
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

            String providerId = org.ei.opensrp.Context.getInstance().allSharedPreferences().fetchRegisteredANM();
            String locationId = org.ei.opensrp.Context.getInstance().allSharedPreferences().getPreference(PathConstants.CURRENT_LOCATION_ID);
            Report report = new Report();
            report.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            report.setHia2Indicators(hia2Indicators);
            report.setLocationId(locationId);
            report.setProviderId(providerId);
            report.setReportDate(new DateTime());
            report.setReportType(reportType);
            JSONObject reportJson = new JSONObject(JsonFormUtils.gson.toJson(report));
            ecUpdater.addReport(reportJson);


        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }


}

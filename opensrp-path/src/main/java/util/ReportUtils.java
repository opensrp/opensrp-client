package util;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.path.domain.DataElement;
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


    public static void createReport(Context context, List<DataElement> dataElements,String reportType) {
        try {
            ECSyncUpdater ecUpdater = ECSyncUpdater.getInstance(context);

            String providerId = org.ei.opensrp.Context.getInstance().allSharedPreferences().fetchRegisteredANM();

            Report report = new Report();
            report.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            report.setDataElements(dataElements);
           // report.setLocationId();//FIXME
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

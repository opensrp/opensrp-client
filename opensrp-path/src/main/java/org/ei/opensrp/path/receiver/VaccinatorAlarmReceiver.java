package org.ei.opensrp.path.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.ei.opensrp.path.service.intent.HIA2IntentService;
import org.ei.opensrp.path.service.intent.RecurringIntentService;
import org.ei.opensrp.path.service.intent.VaccineIntentService;
import org.ei.opensrp.path.service.intent.WeightIntentService;
import org.ei.opensrp.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import util.PathConstants;

public class VaccinatorAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = VaccinatorAlarmReceiver.class.getCanonicalName();

    private static final String serviceActionName = "org.ei.opensrp.path.action.START_SERVICE_ACTION";
    private static final String serviceTypeName = "serviceType";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        int serviceType = alarmIntent.getIntExtra(serviceTypeName, 0);
        if (!org.ei.opensrp.Context.getInstance().IsUserLoggedOut()) {
            Intent serviceIntent = null;
            switch (serviceType) {
                case PathConstants.ServiceType.DATA_SYNCHRONIZATION:
                    //handled by pathupdateactionstask
                    android.util.Log.i(TAG, "Started data synchronization service at: " + dateFormatter.format(new Date()));
                    break;
                case PathConstants.ServiceType.DAILY_TALLIES_GENERATION:
                    android.util.Log.i(TAG, "Started DAILY_TALLIES_GENERATION service at: " + dateFormatter.format(new Date()));
                    serviceIntent = new Intent(context, HIA2IntentService.class);
                    break;
                case PathConstants.ServiceType.MONTHLY_TALLIES_GENERATION:
                    android.util.Log.i(TAG, "Started MONTHLY_TALLIES_GENERATION service at: " + dateFormatter.format(new Date()));
                    break;
                case PathConstants.ServiceType.PULL_UNIQUE_IDS:
                    //happens at pathupdateactionstask
                    //serviceIntent = new Intent(context, PullUniqueIdsIntentService.class);
                    android.util.Log.i(TAG, "Started PULL_UNIQUE_IDS service at: " + dateFormatter.format(new Date()));
                    break;
                case PathConstants.ServiceType.WEIGHT_SYNC_PROCESSING:
                    serviceIntent = new Intent(context, WeightIntentService.class);
                    android.util.Log.i(TAG, "Started WEIGHT_SYNC_PROCESSING service at: " + dateFormatter.format(new Date()));
                    break;
                case PathConstants.ServiceType.VACCINE_SYNC_PROCESSING:
                    serviceIntent = new Intent(context, VaccineIntentService.class);
                    android.util.Log.i(TAG, "Started VACCINE_SYNC_PROCESSING service at: " + dateFormatter.format(new Date()));
                    break;
                case PathConstants.ServiceType.RECURRING_SERVICES_SYNC_PROCESSING:
                    serviceIntent = new Intent(context, RecurringIntentService.class);
                    android.util.Log.i(TAG, "Started RECURRING_SERVICES_SYNC_PROCESSING service at: " + dateFormatter.format(new Date()));
                    break;
            }

            if (serviceIntent != null)
                this.startService(context, serviceIntent, serviceType);
        }

    }

    public void startService(Context context, Intent serviceIntent, int serviceType) {
        context.startService(serviceIntent);
    }

    /**
     * @param context
     * @param triggerIteration in minutes
     * @param taskType         a constant from pathconstants denoting the service type
     */
    public static void setAlarm(Context context, long triggerIteration, int taskType) {
        try {
            AlarmManager alarmManager;
            PendingIntent alarmIntent;

            long triggerAt;
            long triggerInterval;
            if (context == null) {
                throw new Exception("Unable to schedule service without app context");
            }

            // Otherwise schedule based on normal interval
            triggerInterval = TimeUnit.MINUTES.toMillis(triggerIteration);
            // set trigger time to be current device time + the interval (frequency). Probably randomize this a bit so that services not launch at exactly the same time
            triggerAt = System.currentTimeMillis() + triggerInterval;

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmReceiverIntent = new Intent(context, VaccinatorAlarmReceiver.class);

            alarmReceiverIntent.setAction(serviceActionName + taskType);
            alarmReceiverIntent.putExtra(serviceTypeName, taskType);
            alarmIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                alarmManager.cancel(alarmIntent);
            } catch (Exception e) {
                Log.logError(TAG, e.getMessage());
            }
            //Elapsed real time uses the "time since system boot" as a reference, and real time clock uses UTC (wall clock) time
            alarmManager.setRepeating(AlarmManager.RTC, triggerAt, triggerInterval, alarmIntent);
        } catch (Exception e) {
            Log.logError(TAG, "Error in setting service Alarm " + e.getMessage());
        }

    }

}

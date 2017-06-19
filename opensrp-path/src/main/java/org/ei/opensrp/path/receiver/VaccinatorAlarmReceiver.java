package org.ei.opensrp.path.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.ei.opensrp.path.service.intent.HIA2IntentService;
import org.ei.opensrp.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import util.PathConstants;

public class VaccinatorAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = VaccinatorAlarmReceiver.class.getCanonicalName();

    private static final String serviceTypeName = "serviceType";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReceive(Context context, Intent alarmIntent) {
        int serviceType = alarmIntent.getIntExtra(serviceTypeName, 0);

        Intent serviceIntent = null;
        switch (serviceType) {
            case PathConstants.ServiceType.DATA_SYNCHRONIZATION:
                android.util.Log.i(TAG, "Started data synchronization service at: " + dateFormatter.format(new Date()));
                break;
            case PathConstants.ServiceType.DAILY_TALLIES_GENERATION:
                android.util.Log.i(TAG, "Started DAILY_TALLIES_GENERATION service at: " + dateFormatter.format(new Date()));
                serviceIntent = new Intent(context, HIA2IntentService.class);
                break;
            case PathConstants.ServiceType.MONTHLY_TALLIES_GENERATION:
                android.util.Log.i(TAG, "Started MONTHLY_TALLIES_GENERATION service at: " + dateFormatter.format(new Date()));
                break;
        }

        if (serviceIntent != null)
            this.startService(context, serviceIntent, serviceType);

    }

    public void startService(Context context, Intent serviceIntent, int serviceType) {
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, serviceIntent);
    }

    /**
     *
     * @param context
     * @param triggerIteration in minutes
     * @param taskType a constant from pathconstants denoting the service type
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
            alarmReceiverIntent.putExtra(serviceTypeName, taskType);
            alarmIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                alarmManager.cancel(alarmIntent);
            } catch (Exception e) {
               Log.logError(TAG,e.getMessage());
            }
            //Elapsed real time uses the "time since system boot" as a reference, and real time clock uses UTC (wall clock) time
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, triggerInterval, alarmIntent);
        } catch (Exception e) {
            Log.logError(TAG, "Error in setting service Alarm " + e.getMessage());
        }

    }

}

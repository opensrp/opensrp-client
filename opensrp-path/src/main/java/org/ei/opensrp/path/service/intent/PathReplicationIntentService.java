package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import static org.ei.opensrp.util.Log.logInfo;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class PathReplicationIntentService extends IntentService {
    private static final String TAG = PathReplicationIntentService.class.getCanonicalName();

    public static final String RECEIVER_TAG = "ReceiverTag";
    public static final String RESULT_TAG = "ResultTag";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PathReplicationIntentService(String name) {
        super(name);
    }

    public PathReplicationIntentService() {

        super("PathReplicationIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean status;


        try {

//            CloudantSyncHandler mCloudantSyncHandler = CloudantSyncHandler.getInstance(Context.getInstance().applicationContext());
//            CountDownLatch mCountDownLatch = new CountDownLatch(1);
//            mCloudantSyncHandler.setCountDownLatch(mCountDownLatch);
//            mCloudantSyncHandler.startPushReplication();
//
//            mCountDownLatch.await();

            status = true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            status = false;
        }

        ResultReceiver rec = intent.getParcelableExtra(RECEIVER_TAG);
        if(rec != null) {
            Bundle b = new Bundle();
            b.putBoolean(RESULT_TAG, status);
            rec.send(0, b);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logInfo("Started path replication service");
        return super.onStartCommand(intent,flags,startId);
    }
}

package org.smartregister.indonesia.device.manager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by sid on 4/25/17.
 */
public class DeviceService extends Service {

    private static final String TAG = DeviceService.class.getSimpleName();
    Thread t = new PeriodicUpdate();

    @Override
    public void onCreate(){
        super.onCreate();
        // start thread
        t.start();
        Log.e(TAG, "onCreate: Service created" );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (!t.isAlive()){
            t.start();
        }
        return Service.START_REDELIVER_INTENT;
    }

//    @Override
//    public void onStart(Intent intent, int startId){
//        Log.e(TAG, "onStart: "+ startId );
//    }
//
    @Override
    public void onDestroy(){

        stopSelf();
        super.onDestroy();
        Log.e(TAG, "onDestroy: Service Stop" );
    }
}

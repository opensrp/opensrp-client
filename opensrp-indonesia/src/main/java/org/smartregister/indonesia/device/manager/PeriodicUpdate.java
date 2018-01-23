package org.smartregister.indonesia.device.manager;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.ihealth.communication.control.Bp7Control;

/**
 * Created by sid on 4/25/17.
 */
public class PeriodicUpdate extends Thread {

    private static final String TAG = PeriodicUpdate.class.getSimpleName();

    @Override
    public void run(){

        while (true){

            try {

                sendStatus();

                Thread.sleep(2*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Bp7Control bp7Control;


    private void sendStatus() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.e(TAG, "sendStatus: BT ON "+mBluetoothAdapter.isEnabled() );

//        angle = bp7Control.getAngle();
        Log.e(TAG, "sendStatus: " );
    }
}

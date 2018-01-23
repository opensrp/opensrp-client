package org.smartregister.indonesia.device.manager;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sid on 4/25/17.
 */
public class BluetoothLE extends Service {

    private static final String TAG = BluetoothLE.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager manager;
    private Handler scanHandler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

        super.onCreate();
        turnOnBLE();
        discoverBLEDevices();
    }

    @SuppressLint("NewApi")
    private void turnOnBLE() {
        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
        mBluetoothAdapter.enable();

        Toast.makeText(BluetoothLE.this, "BTLE On Service", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "turnOnBLE: "+ "Turn On BLE");

    }

    @SuppressLint("NewApi")
    private void discoverBLEDevices() {
        startScan.run();
        Log.e("BLE_Scanner", "DiscoverBLE");
    }


    @SuppressLint("NewApi")
    private Runnable startScan = new Runnable() {
        @Override
        public void run() {
            scanHandler.postDelayed(stopScan, 500);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.e(TAG, "run: scan MAC" );
        }
    };


    // Device scan callback.
    @SuppressLint("NewApi")
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @SuppressLint("NewApi")
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {

            String Address = device.getAddress();
            String Name = device.getName();
            Log.e("mLeScanCallback",""+Address +" : "+Name);
        }
    };


    @SuppressLint("NewApi")
    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scanHandler.postDelayed(startScan, 10);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}

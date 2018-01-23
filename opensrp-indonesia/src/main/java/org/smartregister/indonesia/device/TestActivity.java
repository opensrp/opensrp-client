package org.smartregister.indonesia.device;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ihealth.communication.control.Bp3lControl;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.smartregister.indonesia.R;

//import android.support.v7.app.AppCompatActivity;


public class TestActivity extends Activity implements View.OnClickListener{
//public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TestActivity";
    private static final int EXCEPT_NUM = 30000;
    private int loop_Num    = 0;
    private int success_Num = 0;

    private String mAddress = "D05FB8418B71";//D05FB841892E   7CEC793296AC

    private int clientId;

    private MyHandler myHandler;

    private TextView tv_count_of_loops_value;
    private TextView tv_current_count_value;
    private TextView tv_success_count_value;
    private TextView tv_success_rate_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        clientId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientId, iHealthDevicesManager.TYPE_BP3L);

        myHandler = new MyHandler();

        findViewById(R.id.btn_Connection_With_Scan).setOnClickListener(this);
        findViewById(R.id.btn_Connection_Without_Scan).setOnClickListener(this);

        tv_count_of_loops_value = (TextView)findViewById(R.id.tv_count_of_loops_value);
        tv_current_count_value = (TextView)findViewById(R.id.tv_current_count_value);
        tv_success_count_value = (TextView)findViewById(R.id.tv_success_count_value);
        tv_success_rate_value = (TextView)findViewById(R.id.tv_success_rate_value);
        tv_count_of_loops_value.setText(String.valueOf(EXCEPT_NUM));

//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
////                TestActivity.this.finish();
//                System.exit(0);
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task,2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientId);
        myHandler = null;
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Connection_With_Scan:
                getConnectionLock = false;
                iHealthDevicesManager.getInstance().startDiscovery(iHealthDevicesManager.DISCOVERY_BP3L);
                break;
            case R.id.btn_Connection_Without_Scan:
                myHandler.sendEmptyMessage(CONNECT_DEVICE);
                break;
            default:
                break;
        }
    }

    private boolean connectionLock = false;
    private boolean getConnectionLock = false;
    private boolean isDisconnectLock = false;
    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback(){
        @Override
        public void onScanDevice(String mac, String deviceType, int rssi) {
            Log.i(TAG, "onScanDevice:" + mac + " - " + deviceType);
            if((mac.equals(mAddress)) && (!connectionLock)){
                Log.i(TAG, "mac.equals(mAddress)");
                getConnectionLock = true;
                connectionLock = true;
                myHandler.sendEmptyMessage(CONNECT_DEVICE);
            }
        }

        @Override
        public void onScanFinish() {
            if(!getConnectionLock){
                iHealthDevicesManager.getInstance().startDiscovery(iHealthDevicesManager.DISCOVERY_BP3L);
            }
        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            connectionLock = false;
            getConnectionLock = false;
            if(iHealthDevicesManager.DEVICE_STATE_CONNECTED == status){
                myHandler.sendEmptyMessage(ADD_SUCCESS);
                isDisconnectLock = true;
                SystemClock.sleep(2000);
                myHandler.sendEmptyMessage(DISCONNECT_DEVICE);

            }else if(iHealthDevicesManager.DEVICE_STATE_CONNECTIONFAIL == status){

                myHandler.sendEmptyMessage(ADD_FAIL);
                myHandler.sendEmptyMessage(SCAN_DEVICE);

            }else if(iHealthDevicesManager.DEVICE_STATE_DISCONNECTED == status){
//                SystemClock.sleep(2000);
//                myHandler.sendEmptyMessage(SCAN_DEVICE);
            }
        }

    };

    private static final int ADD_SUCCESS = 101;
    private static final int ADD_FAIL = 102;
    private static final int SCAN_DEVICE = 103;
    private static final int CONNECT_DEVICE = 104;
    private static final int DISCONNECT_DEVICE = 105;

    class MyHandler extends Handler {

        public MyHandler() {}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ADD_SUCCESS:
                    loop_Num += 1;
                    success_Num += 1;
                    tv_current_count_value.setText(String.valueOf(loop_Num));
                    tv_success_count_value.setText(String.valueOf(success_Num));
                    int rate = 100 * success_Num / loop_Num;
                    tv_success_rate_value.setText(String.valueOf(rate) + "%");
                    break;
                case ADD_FAIL:
                    loop_Num += 1;
                    tv_current_count_value.setText(String.valueOf(loop_Num));
                    rate = 100 * success_Num / loop_Num;
                    tv_success_rate_value.setText(String.valueOf(rate) + "%");
                    break;
                case SCAN_DEVICE:
                    iHealthDevicesManager.getInstance().startDiscovery(iHealthDevicesManager.DISCOVERY_BP5);
                    break;
                case CONNECT_DEVICE:
                    Log.e(TAG, "handler connect device");
                    Bp3lControl lastbp3lControl = iHealthDevicesManager.getInstance().getBp3lControl(mAddress);
                    if(lastbp3lControl != null){
                        System.exit(0);
                    }
                    if(loop_Num < EXCEPT_NUM){
                        iHealthDevicesManager.getInstance().stopDiscovery();
                        SystemClock.sleep(300);
                        iHealthDevicesManager.getInstance().connectDevice(null, mAddress, "");
                    }
                    break;
                case DISCONNECT_DEVICE:
                    Bp3lControl bp3lControl = iHealthDevicesManager.getInstance().getBp3lControl(mAddress);
                    bp3lControl.disconnect();
                    break;
                default:
                    break;
            }
        }
    }
}

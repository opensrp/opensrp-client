package org.smartregister.indonesia.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.communication.control.Bp7Control;
import com.ihealth.communication.control.BpProfile;
import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.device.manager.DeviceService;

//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;

public class BP7 extends Activity implements View.OnClickListener {

    private static final String TAG = BP7.class.getSimpleName();
    private Bp7Control bp7Control;
    private String deviceMac;
    private int clientCallbackId;
    private TextView tv_return, tv_systolic, tv_diastolic, tv_sys, tv_dia, tv_pls, tv_pulse;
    private Button btn_done, startStopMeasure_btn,  startMeasure_btn, conformAngle_btn;
//    private Button battery_btn, isOfflineMeasure_btn,enableOfflineMeasure_btn, disableOfflineMeasure_btn, stopMeasure_btn, getOfflineNum_btn, getOfflineData_btn, disconnect_btn;
    private boolean stopMeasured = true;

    private ProgressBar mProgressBar;
    int initValue = 0;
    Handler handler = new Handler();
    private String bpmHigh, bpmLow, bpmAhr, bpmPulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bp7);
        setContentView(R.layout.content_bp7);
//        setContentView(R.layout.main_bp7);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
        Intent intent = getIntent();

        deviceMac = intent.getStringExtra("mac");

        initView();

        initListener();


        clientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
        /* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientCallbackId, iHealthDevicesManager.TYPE_BP7);
		/* Get bp7 controller */
        bp7Control = iHealthDevicesManager.getInstance().getBp7Control(deviceMac);
        // arm position 10 - 29
    }

    private void initView() {

        startStopMeasure_btn = (Button) findViewById(R.id.btn_startStopMeasure);

        btn_done = (Button) findViewById(R.id.btn_done);
        startMeasure_btn = (Button) findViewById(R.id.btn_startMeasure);
        conformAngle_btn = (Button) findViewById(R.id.btn_conform_angle);
        tv_return = (TextView) findViewById(R.id.tv_return);

        tv_sys = (TextView) findViewById(R.id.tv_sys);
        tv_systolic = (TextView) findViewById(R.id.tv_systole);
        tv_dia = (TextView) findViewById(R.id.tv_dia);
        tv_diastolic = (TextView) findViewById(R.id.tv_diastole);
        tv_pls = (TextView) findViewById(R.id.tv_pls);
        tv_pulse = (TextView) findViewById(R.id.tv_pulse);

        pb_mypb = (ProgressBar) findViewById(R.id.pb_bpm);

//        battery_btn = (Button) findViewById(R.id.btn_getbattery);
//        isOfflineMeasure_btn = (Button) findViewById(R.id.btn_isOfflineMeasure);
//        enableOfflineMeasure_btn = (Button) findViewById(R.id.btn_enableOfflineMeasure);
//        disableOfflineMeasure_btn = (Button) findViewById(R.id.btn_disableOfflineMeasure);
//        stopMeasure_btn = (Button) findViewById(R.id.btn_stopMeasure);
//        getOfflineNum_btn = (Button) findViewById(R.id.btn_getOfflineNum);
//        getOfflineData_btn = (Button) findViewById(R.id.btn_getOfflineData);
//        disconnect_btn = (Button) findViewById(R.id.btn_disconnect);


//        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
//        Add Color
//        Drawable ad = mProgressBar.getProgressDrawable();
//        Log.e(TAG, "initView: "+ mProgressBar );
//        Log.e(TAG, "initView: "+ ad );
//        mProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);

//        if (mProgressBar != null) {
//            mProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
//        }

//         Visibility
//        startMeasure_btn.setVisibility(View.GONE);
//        battery_btn.setVisibility(View.GONE);
//        isOfflineMeasure_btn.setVisibility(View.GONE);
//        enableOfflineMeasure_btn.setVisibility(View.GONE);
//        disableOfflineMeasure_btn.setVisibility(View.GONE);
//        getOfflineNum_btn.setVisibility(View.GONE);
//        getOfflineData_btn.setVisibility(View.GONE);
//        disconnect_btn.setVisibility(View.GONE);
//        stopMeasure_btn.setVisibility(View.GONE);

        pb_mypb.setVisibility(View.GONE);

        tv_sys.setVisibility(View.GONE);
        tv_systolic.setVisibility(View.GONE);
        tv_dia.setVisibility(View.GONE);
        tv_diastolic.setVisibility(View.GONE);
        tv_pls.setVisibility(View.GONE);
        tv_pulse.setVisibility(View.GONE);

        btn_done.setVisibility(View.GONE);
//        pb_mypb.setVisibility(View.VISIBLE);
        conformAngle_btn.setVisibility(View.VISIBLE);
//        conformAngle_btn.setVisibility(View.GONE);



    }

    public void initListener(){
        startStopMeasure_btn.setOnClickListener(this);
        conformAngle_btn.setOnClickListener(this);
        tv_return.setOnClickListener(this);
        btn_done.setOnClickListener(this);

//        battery_btn.setOnClickListener(this);
//        isOfflineMeasure_btn.setOnClickListener(this);
//        enableOfflineMeasure_btn.setOnClickListener(this);
//        disableOfflineMeasure_btn.setOnClickListener(this);
//        startMeasure_btn.setOnClickListener(this);
//        stopMeasure_btn.setOnClickListener(this);
//        getOfflineNum_btn.setOnClickListener(this);
//        getOfflineData_btn.setOnClickListener(this);
//        disconnect_btn.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientCallbackId);
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "status: " + status);
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Log.i(TAG, "username: " + username);
            Log.i(TAG, "userState: " + userStatus);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType,
                                   String action, String message) {
            Log.i(TAG, "mac: " + mac);
            Log.i(TAG, "deviceType: " + deviceType);
            Log.i(TAG, "action: " + action);
            Log.i(TAG, "message: " + message);

            if (BpProfile.ACTION_BATTERY_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String battery = info.getString(BpProfile.BATTERY_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (BpProfile.ACTION_DISENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "disable operation is success");

            } else if (BpProfile.ACTION_ENABLE_OFFLINE_BP.equals(action)) {
                Log.i(TAG, "enable operation is success");

            } else if (BpProfile.ACTION_ERROR_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "error num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)) {
                String str = "";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave = obj.getString(BpProfile.PULSE_BP);
                            String ahr = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
                            str = "date:" + date
                                    + "hightPressure:" + hightPressure + "\n"
                                    + "lowPressure:" + lowPressure + "\n"
                                    + "pulseWave" + pulseWave + "\n"
                                    + "ahr:" + ahr + "\n"
                                    + "hsd:" + hsd + "\n";
                        }
                    }
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = str;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String num = info.getString(BpProfile.HISTORICAL_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_IS_ENABLE_OFFLINE.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String isEnableoffline = info.getString(BpProfile.IS_ENABLE_OFFLINE);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "isEnableoffline: " + isEnableoffline;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)) {
                conformAngle_btn.setVisibility(View.GONE);
                pb_mypb.setVisibility(View.VISIBLE);
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure: " + pressure;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)) {
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure = info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure:" + pressure + "\n"
                            + "wave: " + wave + "\n"
                            + " - heartbeat:" + heartbeat;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)) {

                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure = info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure = info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr = info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse = info.getString(BpProfile.PULSE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "highPressure: " + highPressure
                            + "lowPressure: " + lowPressure
                            + "ahr: " + ahr
                            + "pulse: " + pulse;
                    myHandler.sendMessage(msg);

                    updateButtonStatus();

                    //SID
                    showBPMResult(highPressure, lowPressure, ahr, pulse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (BpProfile.ACTION_ZOREING_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "0"; //"zoreing";
                myHandler.sendMessage(msg);

            } else if (BpProfile.ACTION_ZOREOVER_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "0"; //"zoreover";
                myHandler.sendMessage(msg);

            } else if (BpProfile.ACTION_STOP_BP.equals(action)) {
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
                msg.obj = "stop measure from device";
                myHandler.sendMessage(msg);

            }
        }
    };


    private void showBPMResult(String highPressure, String lowPressure, String ahr, String pulse) {
        Log.e(TAG, "showBPMResult: ");
        Intent in = new Intent(this, DeviceService.class);
//        startService(in);
//        stopService(in);
//        tv_systolic = (TextView) findViewById(R.id.tv_sistole);
//        tv_diastolic = (TextView) findViewById(R.id.tv_diastole);

        setBpmHigh(highPressure);
        setBpmLow(lowPressure);
        setBpmAhr(ahr);
        setBpmPulse(pulse);
//        Log.e(TAG, "showBPMResult: " );

        tv_systolic.setVisibility(View.VISIBLE);
        tv_sys.setVisibility(View.VISIBLE);
        tv_diastolic.setVisibility(View.VISIBLE);
        tv_dia.setVisibility(View.VISIBLE);
        tv_pulse.setVisibility(View.VISIBLE);
        tv_pls.setVisibility(View.VISIBLE);

        btn_done.setVisibility(View.VISIBLE);

        tv_systolic.setText(highPressure);
        tv_diastolic.setText(lowPressure);
        tv_pulse.setText(pulse);
        pb_mypb.setVisibility(View.GONE);

        conformAngle_btn.setVisibility(View.VISIBLE);
//        backToDetail( highPressure, lowPressure, ahr, pulse);
    }

    private void backToDetail() {
        Intent i = new Intent();
        i.putExtra("HIGH", getBpmHigh());
        i.putExtra("LOW", getBpmLow());
        i.putExtra("AHR", getBpmAhr());
        i.putExtra("PULSE", getBpmPulse());
        setResult(2, i);
        finish();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_getbattery:
                if (bp7Control != null) {
                    bp7Control.getBattery();
                } else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_isOfflineMeasure:
                if (bp7Control != null)
                    bp7Control.isEnableOffline();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_enableOfflineMeasure:
                if (bp7Control != null)
                    bp7Control.enbleOffline();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_disableOfflineMeasure:
                if (bp7Control != null)
                    bp7Control.disableOffline();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_startMeasure:
                if (stopMeasured) {
//                    startMeasure_btn.setEnabled(false);
                    startMeasure_btn.setText("STOP");
                    pb_mypb.setVisibility(View.VISIBLE);

                    if (bp7Control != null) {

                        Intent i = new Intent(this, DeviceService.class);
                        this.startService(i);
                        bp7Control.conformAngle();

                        bp7Control.startMeasure();


                    } else
                        Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                    stopMeasured = false;
                } else {
                    startStopMeasure_btn.setText(R.string.start);
                    pb_mypb.setVisibility(View.GONE);

                    bp7Control.destroy();
                    stopMeasured = true;
                    startMeasure_btn.setEnabled(true);
                }
                break;

            case R.id.btn_conform_angle:
                if (bp7Control != null)
                    bp7Control.conformAngle();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_stopMeasure:
                if (bp7Control != null)
                    bp7Control.interruptMeasure();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOfflineNum:
                if (bp7Control != null)
                    bp7Control.getOfflineNum();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOfflineData:
                if (bp7Control != null)
                    bp7Control.getOfflineData();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_disconnect:
                if (bp7Control != null)
                    bp7Control.disconnect();
                else
                    Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_startStopMeasure:
                if (stopMeasured) {
//                    startMeasure_btn.setEnabled(false);
                    startStopMeasure_btn.setText("STOP");
                    if (bp7Control != null) {

                        Intent i = new Intent(this, DeviceService.class);
                        this.startService(i);

                        bp7Control.startMeasure();
                    } else
                        Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                    stopMeasured = false;
                    Log.e(TAG, "onClick: START" );

                } else {
                    startStopMeasure_btn.setText("START");

                    if (bp7Control != null)
                        bp7Control.interruptMeasure();
                    else {
                        Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                        stopMeasured = true;
//                    startMeasure_btn.setEnabled(true);
                        Log.e(TAG, "onClick: STOP");
                        Intent i = new Intent(this, DeviceService.class);
                        this.stopService(i);
                    }
                }

                break;

            case R.id.btn_done:
                backToDetail();
                break;

            default:
                break;
        }
    }

    private static final int HANDLER_MESSAGE = 101;

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    tv_return.setText((String) msg.obj);

                    int pls = Integer.parseInt(((String) msg.obj).split("[\\r?\\n]+")[0].replaceAll("[^0-9?!\\.]",""));
                    Log.e(TAG, "handleMessage: Display MSG Split "+pls);
                    myProgress++;
                    pb_mypb.setProgress(pls);


                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void updateButtonStatus(){
        if (stopMeasured) {
//                    startMeasure_btn.setEnabled(false);
            startStopMeasure_btn.setText("STOP");
            if (bp7Control != null) {

//                Intent i = new Intent(this, DeviceService.class);
//                this.startService(i);

//                bp7Control.startMeasure();
            } else
                Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();
            stopMeasured = false;
            Log.e(TAG, "onClick: START" );

        } else {
            startStopMeasure_btn.setText("START");

            if (bp7Control != null) {
//                bp7Control.interruptMeasure();

            }
            else
                Toast.makeText(BP7.this, "bp7Control == null", Toast.LENGTH_LONG).show();

            stopMeasured = true;
//                    startMeasure_btn.setEnabled(true);
            Log.e(TAG, "onClickss: STOP" );
        }

    }

    public void setBpmHigh(String bpmHigh) {
        this.bpmHigh = bpmHigh;
    }

    public void setBpmLow(String bpmLow) {
        this.bpmLow = bpmLow;
    }

    public String getBpmHigh() {
        return bpmHigh;
    }

    public String getBpmLow() {
        return bpmLow;
    }

    public void setBpmAhr(String bpmAhr) {
        this.bpmAhr = bpmAhr;
    }

    public void setBpmPulse(String bpmPulse) {
        this.bpmPulse = bpmPulse;
    }

    public String getBpmAhr() {
        return bpmAhr;
    }

    public String getBpmPulse() {
        return bpmPulse;
    }

    int myProgress =0;
    private ProgressBar pb_mypb;

    private Handler myHandle = new Handler(){

        public void handleMessage(Message msg){
            myProgress++;
            pb_mypb.setProgress(myProgress);
        }
    };
    private Runnable myThread = new Runnable() {
        @Override
        public void run() {
            while (myProgress < 100){
                Log.e(TAG, "run: "+ myProgress );
                try {
                    myHandle.sendMessage(myHandle.obtainMessage());
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

}

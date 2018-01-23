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
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.communication.control.Bp5Control;
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

public class BP5 extends Activity implements View.OnClickListener {

    private static final String TAG = "BP5";
    private Bp5Control bp5Control;
    private String deviceMac;
    private int clientCallbackId;
    private TextView tv_return, tv_sys, tv_dia, tv_pls;
    private TableLayout tabel;

    private Button btn_done, startStopMeasure_btn,  startMeasure_btn, btn_disconnect;
    private boolean stopMeasured = true;

    private ProgressBar mProgressBar;
    int initValue = 0;
    Handler handler = new Handler();
    private String bpmHigh;
    private String bpmLow;
    private String bpmAhr;
    private String bpmPulse;

    Bundle bundle = new Bundle();
    int myProgress =0;
    private ProgressBar pb_mypb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_bp5);
        setContentView(R.layout.content_bp5);
//        setContentView(R.layout.content_bp5_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intent = getIntent();
        deviceMac = intent.getStringExtra("mac");
//        findViewById(R.id.btn_getbattery).setOnClickListener(this);
//        findViewById(R.id.btn_isOfflineMeasure).setOnClickListener(this);
//        findViewById(R.id.btn_enableOfflineMeasure).setOnClickListener(this);
//        findViewById(R.id.btn_disableOfflineMeasure).setOnClickListener(this);
//        findViewById(R.id.btn_startMeasure).setOnClickListener(this);
//        findViewById(R.id.btn_stopMeasure).setOnClickListener(this);
//        findViewById(R.id.btn_getOfflineNum).setOnClickListener(this);
//        findViewById(R.id.btn_getOfflineData).setOnClickListener(this);
//        findViewById(R.id.btn_disconnect).setOnClickListener(this);
//        tv_return = (TextView)findViewById(R.id.tv_return);

        initView();

        initListener();

        clientCallbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);
		/* Limited wants to receive notification specified device */
        iHealthDevicesManager.getInstance().addCallbackFilterForDeviceType(clientCallbackId, iHealthDevicesManager.TYPE_BP5);
		/* Get bp5 controller */
        bp5Control = iHealthDevicesManager.getInstance().getBp5Control(deviceMac);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iHealthDevicesManager.getInstance().unRegisterClientCallback(clientCallbackId);
    }

    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onDeviceConnectionStateChange(String mac,
                                                  String deviceType, int status, int errorID) {
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

            if(BpProfile.ACTION_BATTERY_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String battery =info.getString(BpProfile.BATTERY_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "battery: " + battery;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else if(BpProfile.ACTION_DISENABLE_OFFLINE_BP.equals(action)){
                Log.i(TAG, "disable operation is success");

            }else if(BpProfile.ACTION_ENABLE_OFFLINE_BP.equals(action)){
                Log.i(TAG, "enable operation is success");

            }else if(BpProfile.ACTION_ERROR_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String num =info.getString(BpProfile.ERROR_NUM_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "error num: " + num;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_HISTORICAL_DATA_BP.equals(action)){
                String str = "";
                try {
                    JSONObject info = new JSONObject(message);
                    if (info.has(BpProfile.HISTORICAL_DATA_BP)) {
                        JSONArray array = info.getJSONArray(BpProfile.HISTORICAL_DATA_BP);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String date          = obj.getString(BpProfile.MEASUREMENT_DATE_BP);
                            String hightPressure = obj.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                            String lowPressure   = obj.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                            String pulseWave     = obj.getString(BpProfile.PULSE_BP);
                            String ahr           = obj.getString(BpProfile.MEASUREMENT_AHR_BP);
                            String hsd           = obj.getString(BpProfile.MEASUREMENT_HSD_BP);
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
                    msg.obj =  str;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_HISTORICAL_NUM_BP.equals(action)){
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

            }else if(BpProfile.ACTION_IS_ENABLE_OFFLINE.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String isEnableoffline =info.getString(BpProfile.IS_ENABLE_OFFLINE);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "isEnableoffline: " + isEnableoffline;
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_ONLINE_PRESSURE_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure =info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure: " + pressure;

                    bundle.putString("pressure", pressure);
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_ONLINE_PULSEWAVE_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String pressure =info.getString(BpProfile.BLOOD_PRESSURE_BP);
                    String wave = info.getString(BpProfile.PULSEWAVE_BP);
                    String heartbeat = info.getString(BpProfile.FLAG_HEARTBEAT_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "pressure:" + pressure + "\n"
                            + "wave: " + wave + "\n"
                            + " - heartbeat:" + heartbeat;
                    bundle.putString("pressure", pressure);
                    msg.setData(bundle);

                    myHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_ONLINE_RESULT_BP.equals(action)){
                try {
                    JSONObject info = new JSONObject(message);
                    String highPressure =info.getString(BpProfile.HIGH_BLOOD_PRESSURE_BP);
                    String lowPressure =info.getString(BpProfile.LOW_BLOOD_PRESSURE_BP);
                    String ahr =info.getString(BpProfile.MEASUREMENT_AHR_BP);
                    String pulse =info.getString(BpProfile.PULSE_BP);
                    Message msg = new Message();
                    msg.what = HANDLER_MESSAGE;
                    msg.obj = "highPressure: " + highPressure
                            + "lowPressure: " + lowPressure
                            + "ahr: " + ahr
                            + "pulse: " + pulse;
                    myHandler.sendMessage(msg);

                    //SID
                    updateButtonStatus();
                    showBPMResult(highPressure, lowPressure, ahr, pulse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if(BpProfile.ACTION_ZOREING_BP.equals(action)){
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
//                msg.obj = "zoreing";
                msg.obj = "0";
                myHandler.sendMessage(msg);

            }else if(BpProfile.ACTION_ZOREOVER_BP.equals(action)){
                Message msg = new Message();
                msg.what = HANDLER_MESSAGE;
//                msg.obj = "zoreover";
                msg.obj = "0";
                myHandler.sendMessage(msg);

            }
        }
    };

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_getbattery:
                if(bp5Control != null) {
                    bp5Control.getBattery();
                }
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_isOfflineMeasure:
                if(bp5Control != null)
                    bp5Control.isEnableOffline();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_enableOfflineMeasure:
                if(bp5Control != null)
                    bp5Control.enbleOffline();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_disableOfflineMeasure:
                if(bp5Control != null)
                    bp5Control.disableOffline();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_startMeasure:
                if(bp5Control != null)
                    bp5Control.startMeasure();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_stopMeasure:
                if(bp5Control != null)
                    bp5Control.interruptMeasure();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOfflineNum:
                if(bp5Control != null)
                    bp5Control.getOfflineNum();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_getOfflineData:
                if(bp5Control != null)
                    bp5Control.getOfflineData();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_disconnect:
                if(bp5Control != null)
                    bp5Control.disconnect();
                else
                    Toast.makeText(BP5.this, "bp5Control == null", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_startStopMeasure:
                if (stopMeasured) {
//                    startMeasure_btn.setEnabled(false);
                    pb_mypb.setVisibility(View.VISIBLE);
                    startStopMeasure_btn.setText(R.string.stop);
                    if (bp5Control != null) {

                        Intent i = new Intent(this, DeviceService.class);
                        this.startService(i);

                        bp5Control.startMeasure();
                    } else
                        Toast.makeText(BP5.this, "bp7Control == null", Toast.LENGTH_LONG).show();
                    stopMeasured = false;
                    Log.e(TAG, "onClick: START" );

                } else {
                    startStopMeasure_btn.setText(R.string.start);
                    pb_mypb.setVisibility(View.GONE);

                    if (bp5Control != null)
                        bp5Control.interruptMeasure();
                    else {
                        Toast.makeText(BP5.this, "bp7Control == null", Toast.LENGTH_LONG).show();
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
                    tv_return.setText((String)msg.obj);
//                    Log.e(TAG, "handleMessage: Display MSG "+msg.getData().getString("pulse"));
                    int pls = Integer.parseInt(((String) msg.obj).split("[\\r?\\n]+")[0].replaceAll("[^0-9?!\\.]",""));
                    Log.e(TAG, "handleMessage: Display MSG Split "+pls);
                    myProgress++;
                    pb_mypb.setProgress(pls);

                    break;
            }
            super.handleMessage(msg);
        }
    };

    TextView tv_systolic, tv_diastolic;

    private void initView() {

        startStopMeasure_btn = (Button) findViewById(R.id.btn_startStopMeasure);
//        startMeasure_btn = (Button) findViewById(R.id.btn_startMeasure);
        tv_return = (TextView) findViewById(R.id.tv_return);
        btn_disconnect = (Button) findViewById(R.id.btn_disconnect);
        btn_done = (Button) findViewById(R.id.btn_done);

        tabel = (TableLayout) findViewById(R.id.tabel);

        tv_sys = (TextView) findViewById(R.id.tv_sys);
        tv_dia = (TextView) findViewById(R.id.tv_dia);
        tv_pls = (TextView) findViewById(R.id.tv_pulse);

        tabel.setVisibility(View.GONE);
        tv_sys.setVisibility(View.GONE);
        tv_dia.setVisibility(View.GONE);
        tv_pls.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        pb_mypb = (ProgressBar) findViewById(R.id.pb_bpm);
        pb_mypb.setVisibility(View.GONE);

    }

    public void initListener(){
        startStopMeasure_btn.setOnClickListener(this);
        btn_disconnect.setOnClickListener(this);
        tv_return.setOnClickListener(this);
        btn_done.setOnClickListener(this);

    }

    private void showBPMResult(String highPressure, String lowPressure, String ahr, String pulse) {
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
        tv_sys.setText(highPressure);
        tv_dia.setText(lowPressure);
        tv_pls.setText(pulse);

        tabel.setVisibility(View.VISIBLE);
        tv_sys.setVisibility(View.VISIBLE);
        tv_dia.setVisibility(View.VISIBLE);
        tv_pls.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.VISIBLE);
        pb_mypb.setVisibility(View.GONE);

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

    private void showProgressBar() {

    }

    private void updateButtonStatus(){
        if (stopMeasured) {
//                    startMeasure_btn.setEnabled(false);
            startStopMeasure_btn.setText("STOP");
            if (bp5Control != null) {

//                Intent i = new Intent(this, DeviceService.class);
//                this.startService(i);

//                bp7Control.startMeasure();
            } else
                Toast.makeText(BP5.this, "bp7Control == null", Toast.LENGTH_LONG).show();
            stopMeasured = false;
            Log.e(TAG, "onClick: START" );

        } else {
            startStopMeasure_btn.setText("START");

            if (bp5Control != null) {
//                bp7Control.interruptMeasure();

            }
            else
                Toast.makeText(BP5.this, "bp7Control == null", Toast.LENGTH_LONG).show();

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

    private Handler myHandle = new Handler(){

        public void handleMessage(Message msg){
            myProgress++;
            pb_mypb.setProgress(myProgress);
        }
    };
    private Runnable myThread = new Runnable() {
        @Override
        public void run() {
            while (myProgress < 200){
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

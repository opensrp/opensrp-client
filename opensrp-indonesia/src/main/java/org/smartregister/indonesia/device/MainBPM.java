package org.smartregister.indonesia.device;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ihealth.communication.manager.iHealthDevicesCallback;
import com.ihealth.communication.manager.iHealthDevicesManager;

import org.smartregister.indonesia.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sid on 4/5/17.
 */
public class MainBPM extends Activity implements View.OnClickListener {

    private static final String TAG = MainBPM.class.getSimpleName();
    private static final int HANDLER_SCAN = 101;
    private static final int HANDLER_CONNECTED = 102;
    private static final int HANDLER_DISCONNECT = 103;
    private static final int HANDLER_USER_STATUE = 104;
    /**
     * Id to identify permissions request.
     */
    private static final int REQUEST_PERMISSIONS = 0;
    private static final int ENABLE_REQUEST = 1;
    /*
     * userId the identification of the user, could be the form of email address or mobile phone
     * number (mobile phone number is not supported temporarily). clientID and clientSecret, as the
     * identification of the SDK, will be issued after the iHealth SDK registration. please contact
     * louie@ihealthlabs.com for registration.
     */
//    String userName = "";
//    String clientId = "";
//    String clientSecret = "";
    String userName = "anudroid.apk06@gmail.com";
    String clientId = "708bde5b65884f8d9e579e33e66e8e80";
    String clientSecret = "38ff62374a0d4aacadaf0e4fb4ed1931";

//    long discoveryType = 67108864; // BP7
    long discoveryType = 33554432; // BP5

    private ListView listview_scan, listview_connected;
    private SimpleAdapter sa_scan, sa_connected;
    private Button bt_discover, bt_discover_stop, bt_startStop, bt_certificate, bt_enableBT;
    private TextView tv_discovery, tv_devScan;
    private TextView tv_devConn;
    private List<HashMap<String, String>> list_ScanDevices = new ArrayList<HashMap<String, String>>();
    private List<HashMap<String, String>> list_ConnectedDevices = new ArrayList<HashMap<String, String>>();
    private int callbackId;
    private Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_SCAN:
                    Bundle bundle_scan = msg.getData();
                    String mac_scan = bundle_scan.getString("mac");
                    String type_scan = bundle_scan.getString("type");
                    HashMap<String, String> hm_scan = new HashMap<String, String>();
                    hm_scan.put("mac", mac_scan);
                    hm_scan.put("type", type_scan);
                    list_ScanDevices.add(hm_scan);
                    updateViewForScan();
//                    connectToDev(mac_scan, type_scan);
                    break;

                case HANDLER_CONNECTED:
                    Bundle bundle_connect = msg.getData();
                    String mac_connect = bundle_connect.getString("mac");
                    String type_connect = bundle_connect.getString("type");
                    HashMap<String, String> hm_connect = new HashMap<String, String>();
                    hm_connect.put("mac", mac_connect);
                    hm_connect.put("type", type_connect);
                    list_ConnectedDevices.add(hm_connect);
                    updateViewForConnected();
                    Log.e(TAG, "idps:" + iHealthDevicesManager.getInstance().getDevicesIDPS(mac_connect));
                    list_ScanDevices.remove(hm_connect);
                    updateViewForScan();

                    // SID
//                    connectDevice();

                    break;

                case HANDLER_DISCONNECT:
                    Bundle bundle_disconnect = msg.getData();
                    String mac_disconnect = bundle_disconnect.getString("mac");
                    String type_disconnect = bundle_disconnect.getString("type");
                    HashMap<String, String> hm_disconnect = new HashMap<String, String>();
                    hm_disconnect.put("mac", mac_disconnect);
                    hm_disconnect.put("type", type_disconnect);
                    list_ConnectedDevices.remove(hm_disconnect);

                    updateViewForConnected();

                    break;
                case HANDLER_USER_STATUE:
                    Bundle bundle_status = msg.getData();
                    String username = bundle_status.getString("username");
                    String userstatus = bundle_status.getString("userstatus");
                    String str = "username:" + username + " - userstatus:" + userstatus;
                    Toast.makeText(MainBPM.this, str, Toast.LENGTH_LONG).show();

                    break;

                default:
                    break;
            }
        }
    };


    private iHealthDevicesCallback miHealthDevicesCallback = new iHealthDevicesCallback() {

        @Override
        public void onScanDevice(String mac, String deviceType, int rssi, Map manufactorData) {
            Log.i(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " -manufactorData:" + manufactorData);
            Log.e(TAG, "onScanDevice - mac:" + mac + " - deviceType:" + deviceType + " - rssi:" + rssi + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();
            msg.what = HANDLER_SCAN;
            msg.setData(bundle);
            myHandler.sendMessage(msg);

            connectToDev(mac, deviceType);

        }

        @Override
        public void onDeviceConnectionStateChange(String mac, String deviceType, int status, int errorID, Map manufactorData) {
            Log.e(TAG, "mac:" + mac + " deviceType:" + deviceType + " status:" + status + " errorid:" + errorID + " -manufactorData:" + manufactorData);
            Bundle bundle = new Bundle();
            bundle.putString("mac", mac);
            bundle.putString("type", deviceType);
            Message msg = new Message();
            if (status == iHealthDevicesManager.DEVICE_STATE_CONNECTED) {
                msg.what = HANDLER_CONNECTED;
            } else if (status == iHealthDevicesManager.DEVICE_STATE_DISCONNECTED) {
                msg.what = HANDLER_DISCONNECT;
            }
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        @Override
        public void onUserStatus(String username, int userStatus) {
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putString("userstatus", userStatus + "");
            Message msg = new Message();
            msg.what = HANDLER_USER_STATUE;
            msg.setData(bundle);
            myHandler.sendMessage(msg);
        }

        @Override
        public void onDeviceNotify(String mac, String deviceType, String action, String message) {
        }

        @Override
        public void onScanFinish() {
            Log.e(TAG, "onScanFinish: end"+discoveryStatus );
            tv_discovery.setText(R.string.discover_finish);
            if (discoveryStatus == 0){
                bt_discover_stop.setEnabled(false);
            }
            bt_startStop.setText(R.string.start_discovery);
        }

    };
    private int discoveryStatus;
    private boolean startDiscovering = false;

    private void connectToDev(String mac, String deviceType) {
        boolean req = iHealthDevicesManager.getInstance().connectDevice(userName, mac, deviceType);
        if (!req) {
            Toast.makeText(MainBPM.this, "Haven’t permission to connect this device or the mac is not valid", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.content_main);

        tv_discovery = (TextView) findViewById(R.id.tv_discovery);
        tv_devScan = (TextView) findViewById(R.id.tv_devScan);
        tv_devConn = (TextView) findViewById(R.id.tv_devConnect);


        bt_startStop = (Button) findViewById(R.id.btn_discorvery_startstop);
//        bt_discover = (Button) findViewById(R.id.btn_discorvery);
//        bt_discover_stop = (Button) findViewById(R.id.btn_stopdiscorvery);

        if (startDiscovering) {
            bt_startStop.setText(R.string.stop_discovery);
        }else {
            bt_startStop.setText(R.string.stop_discovery);
        }
//        bt_discover.setVisibility(View.GONE);
//        bt_discover_stop.setVisibility(View.GONE);

        bt_certificate = (Button) findViewById(R.id.btn_Certification);
        listview_scan = (ListView) findViewById(R.id.list_scan);
        listview_connected = (ListView) findViewById(R.id.list_connected);
        bt_enableBT = (Button) findViewById(R.id.enable_bt);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {

            bt_certificate.setVisibility(View.GONE);
            bt_startStop.setOnClickListener(this);
            bt_certificate.setOnClickListener(this);

//            findViewById(R.id.btn_discorvery).setOnClickListener(this);
//            findViewById(R.id.btn_stopdiscorvery).setOnClickListener(this);
//            findViewById(R.id.btn_Certification).setOnClickListener(this);

            // Toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        if (fab != null) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            });
//        }

            Log.e(TAG, "Model:" + Build.MODEL + " api:" + Build.VERSION.SDK_INT + " version:" + Build.VERSION.RELEASE);

            if (list_ConnectedDevices != null)
                list_ConnectedDevices.clear();
            if (list_ScanDevices != null)
                list_ScanDevices.clear();

            sa_scan = new SimpleAdapter(this, this.list_ScanDevices, R.layout.bp_listview_baseview,
                    new String[]{
                            "type", "mac"
                    },
                    new int[]{
                            R.id.tv_type, R.id.tv_mac
                    });

            listview_scan.setAdapter(sa_scan);
            listview_scan.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                    HashMap<String, String> hm = list_ScanDevices.get(position);
                    String type = hm.get("type");
                    String mac = hm.get("mac");
                    Log.i(TAG, "mac = " + mac);
                    boolean req = iHealthDevicesManager.getInstance().connectDevice(userName, mac, type);
                    if (!req) {
                        Toast.makeText(MainBPM.this, "Haven’t permission to connect this device or the mac is not valid", Toast.LENGTH_LONG).show();
                    }
                }
            });

        /*
         * Initializes the iHealth devices manager. Can discovery available iHealth devices nearby
         * and connect these devices through iHealthDevicesManager.
         */
            iHealthDevicesManager.getInstance().init(this);

        /*
         * Register callback to the manager. This method will return a callback Id.
        */

            callbackId = iHealthDevicesManager.getInstance().registerClientCallback(miHealthDevicesCallback);

            iHealthDevicesManager.getInstance().sdkUserInAuthor(MainBPM.this, userName, clientId,
                    clientSecret, callbackId);

//        checkPermissions();
            SharedPreferences mySharedPreferences = getSharedPreferences("preference", MODE_PRIVATE);
            long discoveryType = mySharedPreferences.getLong("discoveryType", 0);
            for (DeviceStruct struct : deviceStructList) {
                struct.isSelected = ((discoveryType & struct.type) != 0);
            }

            startDiscovery();

            if (discoveryStatus == 0){
                bt_discover_stop.setEnabled(false);
            }

//            bt_discover_stop.setEnabled(false);

            bt_enableBT.setVisibility(View.GONE);

        } else {

            Toast.makeText(MainBPM.this, "Bluetooth disabled, enabled first", Toast.LENGTH_SHORT).show();

            bt_startStop.setVisibility(View.GONE);

            tv_discovery.setVisibility(View.GONE);
//            bt_discover.setVisibility(View.GONE);
//            bt_discover_stop.setVisibility(View.GONE);
            bt_certificate.setVisibility(View.GONE);
            listview_scan.setVisibility(View.GONE);
            listview_connected.setVisibility(View.GONE);
            tv_devScan.setVisibility(View.GONE);
            tv_devConn.setVisibility(View.GONE);
            bt_enableBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if (!mBluetoothAdapter.isEnabled()) {
                        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, ENABLE_REQUEST);
                    } else {
                        Toast.makeText(MainBPM.this, "Already Enabled", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            final AlertDialog.Builder builder = new AlertDialog.Builder(co);
//            builder.setTitle("Bluetooth disabled");
//            builder.show();


        }

//        Intent i = new Intent(this, DeviceService.class);
//        Intent i = new Intent(this, BluetoothLE.class);
//        this.startService(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (iHealthDevicesManager.getInstance() != null) {
        /*
         * When the Activity is destroyed , need to call unRegisterClientCallback method to
         * unregister callback
         */
            iHealthDevicesManager.getInstance().unRegisterClientCallback(callbackId);
        /*
         * When the Activity is destroyed , need to call destroy method of iHealthDeivcesManager to
         * release resources
         */
            iHealthDevicesManager.getInstance().destroy();
//        }
    }

    private static class DeviceStruct {
        String name;
        long type;
        boolean isSelected;
    }

    private static ArrayList<DeviceStruct> deviceStructList = new ArrayList<>();

    static {
        Field[] fields = iHealthDevicesManager.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.contains("DISCOVERY_")) {
                DeviceStruct struct = new DeviceStruct();
                struct.name = fieldName.substring(10);
                try {
                    struct.type = field.getLong(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                deviceStructList.add(struct);
            }
        }

    }

    private class SelectDeviceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return deviceStructList.size();
        }

        @Override
        public Object getItem(int position) {
            return deviceStructList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainBPM.this, R.layout.select_device_item_layout, null);
            }
            CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.select_device_checkbox);
            checkBox.setText(deviceStructList.get(position).name);
            checkBox.setChecked(deviceStructList.get(position).isSelected);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    deviceStructList.get(position).isSelected = isChecked;
                }
            });
            return convertView;
        }
    }

    private void startDiscovery() {
        Log.e(TAG, "startDiscovery: start" );
        // Clear
//        listview_scan.setVisibility(View.GONE);
//        listview_connected.setVisibility(View.GONE);
        list_ScanDevices.clear();
        list_ConnectedDevices.clear();

//        long discoveryType = 67108864;
//        long discoveryType = 0;
//        for (DeviceStruct struct : deviceStructList) {
//            if (struct.isSelected) {
//                discoveryType |= struct.type;
//            }
//        }
        SharedPreferences mySharedPreferences = getSharedPreferences("preference", MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putLong("discoveryType", discoveryType);
        editor.apply();

        // Single device discovery
        iHealthDevicesManager.getInstance().startDiscovery(discoveryType);
        tv_discovery.setText(R.string.discovering);
        discoveryStatus = 1;


    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_discorvery_startstop:
                if (!startDiscovering){
                    startDiscovery();
                    bt_startStop.setText(R.string.stop_discovery);
                    startDiscovering = true;
                } else {
                    bt_startStop.setText(R.string.start_discovery);
                    startDiscovering = false;
                }
                break;

//            case R.id.btn_discorvery:
//                startDiscovery();
//                break;

//            case R.id.btn_stopdiscorvery:
//                iHealthDevicesManager.getInstance().stopDiscovery();
//                break;

            case R.id.btn_Certification:
                iHealthDevicesManager.getInstance().sdkUserInAuthor(MainBPM.this, userName, clientId,
                        clientSecret, callbackId);
//                iHealthDevicesManager.getInstance().sdkUserInAuthor(MainBPM.this, userName, clientId,
//                        clientSecret, callbackId, Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/QQfile_recv/idscertificate.p12", "ELPWfWdA");
                break;

//            case R.id.btn_GotoTest:
//                Intent intentTest = new Intent();
//                intentTest.setClass(MainBPM.this, TestActivity.class);
//                startActivity(intentTest);
//                break;
            default:
                break;
        }
    }

    private void updateViewForScan() {
        sa_scan.notifyDataSetChanged();
        ViewGroup.LayoutParams params = listview_scan.getLayoutParams();
        params.height = dp2px(list_ScanDevices.size() * 48 + 5);
        listview_scan.setLayoutParams(params);
    }

    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void updateViewForConnected() {
        sa_connected = new SimpleAdapter(this, this.list_ConnectedDevices, R.layout.bp_listview_baseview,
                new String[]{
                        "type", "mac"
                },
                new int[]{
                        R.id.tv_type, R.id.tv_mac
                });
        sa_connected.notifyDataSetChanged();

        listview_connected.setAdapter(sa_connected);

        listview_connected.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                HashMap<String, String> hm = list_ConnectedDevices.get(position);
                String type = hm.get("type");
                String mac = hm.get("mac");
                Intent intent = new Intent();
                intent.putExtra("mac", mac);
                Log.e(TAG, "onItemClick: "+position );
                if (iHealthDevicesManager.TYPE_BP7.equals(type)) {
                    intent.setClass(MainBPM.this, BP7.class);
//                    startActivity(intent);
                    startActivityForResult(intent, 2 );

                } else if (iHealthDevicesManager.TYPE_BP5.equals(type)) {
                    intent.setClass(MainBPM.this, BP5.class);
//                    startActivity(intent);
                    startActivityForResult(intent, 2 );

                }
            }
        });

    }

    private void checkPermissions() {
        StringBuilder tempRequest = new StringBuilder();

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            tempRequest.append(Manifest.permission.WRITE_EXTERNAL_STORAGE + ",");
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            tempRequest.append(Manifest.permission.RECORD_AUDIO + ",");
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            tempRequest.append(Manifest.permission.ACCESS_FINE_LOCATION + ",");
//        }
//        if (tempRequest.length() > 0) {
//            tempRequest.deleteCharAt(tempRequest.length() - 1);
//            ActivityCompat.requestPermissions(this, tempRequest.toString().split(","), REQUEST_PERMISSIONS);
//        }
    }

    private void connectDevice(){
        HashMap<String, String> hm = list_ConnectedDevices.get(0);
        String type = hm.get("type");
        String mac = hm.get("mac");
        Intent intent = new Intent(MainBPM.this, BP7.class);
        intent.putExtra("mac", mac);
//        if (iHealthDevicesManager.TYPE_BP7.equals(type)) {
//            intent.setClass(MainBPM.this, BP7.class);
//            startActivity(intent);
            startActivityForResult(intent, 2 );

//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.e(TAG, "onActivityResult: "+requestCode+" res: "+resultCode );
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1){
            Toast.makeText(this, "Bluethooth Enabled Succes", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());

        } else if (requestCode == 2){

            if (data != null){

                Intent i = new Intent();
                i.putExtra("HIGH", data.getStringExtra("HIGH"));
                i.putExtra("LOW", data.getStringExtra("LOW"));
                i.putExtra("AHR", data.getStringExtra("AHR"));
                i.putExtra("PULSE", data.getStringExtra("PULSE"));
                setResult(2, i);
                finish();
            } else{
                Log.e(TAG, "onActivityResult: Data Null" );
            }

        }
    }
}

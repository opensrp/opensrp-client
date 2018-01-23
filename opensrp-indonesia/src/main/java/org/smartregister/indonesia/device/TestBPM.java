package org.smartregister.indonesia.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.smartregister.indonesia.R;

import java.util.Locale;

/**
 * Created by sid on 5/9/17.
 */

public class TestBPM extends Activity{

    private static final String TAG = TestBPM.class.getSimpleName();
    private int high, low, pulse;
    private Button btn_done;
    private TextView tv_sys, tv_sys_value, tv_dia, tv_dia_value, tv_pulse, tv_pulse_value;
    private String ahr;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.main_test_bpm);

        initView();

        setValues();

        showValue();

    }

    @Override
    public void onBackPressed(){
        Bundle bundle = new Bundle();

        Intent i = new Intent();
        i.putExtra("HIGH", nol());
        i.putExtra("LOW", nol());
        i.putExtra("AHR", nol());
        i.putExtra("PULSE", nol());

        setResult(RESULT_CANCELED, i);
        super.onBackPressed();
    }

    private String nol() {
        return String.format(Locale.getDefault(), "%2d", 0);
    }

    private void showValue() {
        Log.e(TAG, "showValue: "+ high);
        Log.e(TAG, "showValue: "+ low);
        Log.e(TAG, "showValue: "+ tv_sys_value );
//        tv_sys_value.setText(high);
//        tv_dia_value.setText(low);
        tv_sys_value.setText(String.format(Locale.getDefault(), "%2d", high));
        tv_dia_value.setText(String.format(Locale.getDefault(), "%2d", low));
        tv_pulse_value.setText(String.format(Locale.getDefault(), "%2d", pulse));
    }

    private void initView() {

        tv_sys = (TextView) findViewById(R.id.tv_high_caps);
        tv_sys_value = (TextView) findViewById(R.id.tv_high_value);
        tv_dia = (TextView) findViewById(R.id.tv_low_caps);
        tv_dia_value = (TextView) findViewById(R.id.tv_low_value);
        tv_pulse = (TextView) findViewById(R.id.tv_pulse_caps);
        tv_pulse_value = (TextView) findViewById(R.id.tv_pulse_value);

        btn_done = (Button) findViewById(R.id.bt_done);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("HIGH", String.format(Locale.getDefault(), "%2d", getHigh()));
                i.putExtra("LOW", String.format(Locale.getDefault(), "%2d", getLow()));
                i.putExtra("AHR", getAhr());
                i.putExtra("PULSE", String.format(Locale.getDefault(), "%2d", getPulse()));

                setResult(2, i);
                finish();//finishing activity
            }
        });

    }

    private int getPulse() {
        return pulse;
    }

    private void setValues() {

        setHigh((int) ((1 - Math.random() * 0.25) * 120));
        setLow((int) ((1 - Math.random() * 0.25) * 100));
        setAhr("false");
        setPulse((int) ((1 - Math.random() * 0.25) * 80));
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getHigh() {
        return high;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getLow() {
        return low;
    }

    public void setAhr(String ahr) {
        this.ahr = ahr;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public String getAhr() {
        return ahr;
    }
}

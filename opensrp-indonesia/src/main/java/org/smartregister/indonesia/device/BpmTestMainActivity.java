package org.smartregister.indonesia.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.smartregister.indonesia.R;

public class BpmTestMainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = BpmTestMainActivity.class.getSimpleName();
    TextView tv_systolic, tv_diastolic;
    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.bpm_activity_main);

        initGuiListener();


    }

    private void initGuiListener() {
        tv_systolic = (TextView) findViewById(R.id.tv_sistole);
        tv_diastolic = (TextView) findViewById(R.id.tv_diastole);

        tv_systolic.setOnClickListener(this);
        tv_diastolic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sistole:
                bpmAction();
                break;
            case R.id.tv_diastole:
                bpmAction();
                break;

            default:
                break;
        }
    }

    private void bpmAction() {
        Intent i = new Intent(BpmTestMainActivity.this, MainBPM.class);

        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.e(TAG, "onActivityResult: req "+ requestCode + " res: "+ resultCode );
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2){
            Log.e(TAG, "onActivityResult: "+ data.getStringExtra("HIGH") +
                    data.getStringExtra("LOW"));
            tv_systolic.setText(data.getStringExtra("HIGH"));
            tv_diastolic.setText(data.getStringExtra("LOW"));
        }
    }
}

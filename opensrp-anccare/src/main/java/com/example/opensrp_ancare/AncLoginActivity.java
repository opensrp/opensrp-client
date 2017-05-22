package com.example.opensrp_ancare;

import android.content.Context;
import android.content.Intent;

import com.example.opensrp_ancare.application.ConfigSyncReceiver;

import org.ei.opensrp.core.template.LoginActivity;
import org.json.JSONException;

public class AncLoginActivity extends LoginActivity {


    @Override
    protected String applicationName() {
        return getString(R.string.app_name);
    }



    @Override
    protected void customTaskWithRemoteLogin(Context context, String username, String password) {
        try {
            ConfigSyncReceiver.fetchConfig(context, username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void goToHome() {
        startActivity(new Intent(this, AncHomeActivity.class));
        finish();
    }
}

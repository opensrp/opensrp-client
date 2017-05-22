package com.example.opensrp_thrivepk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.opensrp_thrivepk.application.ConfigSyncReceiver;

import org.ei.opensrp.core.template.LoginActivity;
import org.json.JSONException;

public class ThrivepkLoginActivity extends LoginActivity {

    @Override
    protected String applicationName() {
        return getString(R.string.app_name);
    }

    @Override
    protected void customTaskWithRemoteLogin(android.content.Context context, String username, String password) {
        try {
            ConfigSyncReceiver.fetchConfig(context, username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void goToHome() {
        startActivity(new Intent(this, ThrivepkHomeActivity.class));
        finish();
    }
}

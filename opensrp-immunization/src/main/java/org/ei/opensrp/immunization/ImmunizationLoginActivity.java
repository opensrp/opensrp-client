package org.ei.opensrp.immunization;

import android.content.Intent;

import org.ei.opensrp.immunization.application.ConfigSyncReceiver;
import org.json.JSONException;
import org.ei.opensrp.core.template.LoginActivity;

public class ImmunizationLoginActivity extends LoginActivity {
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
        startActivity(new Intent(this, ImmunizationHomeActivity.class));
        finish();
    }
}

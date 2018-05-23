package org.ei.opensrp.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.view.controller.ANMController;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.NavigationController;
import android.support.v7.app.ActionBarActivity;

import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static org.ei.opensrp.AllConstants.*;
import static org.ei.opensrp.event.Event.ON_LOGOUT;
import static org.ei.opensrp.util.Log.logInfo;

public abstract class SecuredActivity extends ActionBarActivity {
    protected Listener<Boolean> logoutListener;
    protected FormController formController;
    protected ANMController anmController;
    protected NavigationController navigationController;
    protected final int MENU_ITEM_LOGOUT = 2312;
    protected final int MENU_ITEM_LOGOUT_and_refresh_anm = 2313;
    private String metaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logoutListener = new Listener<Boolean>() {
            public void onEvent(Boolean data) {
                finish();
            }
        };
        ON_LOGOUT.addListener(logoutListener);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }

        formController = new FormController(this);
        anmController = context().anmController();
        navigationController = new NavigationController(this, anmController);
        onCreation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }

        onResumption();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.switchLanguageMenuItem) {
            String newLanguagePreference = context().userService().switchLanguagePreference();
            Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();

            return super.onOptionsItemSelected(item);
        } else if (i == MENU_ITEM_LOGOUT) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();

            return super.onOptionsItemSelected(item);
        } else if (i == MENU_ITEM_LOGOUT_and_refresh_anm) {
        DrishtiApplication application = (DrishtiApplication)getApplication();
            application.context.allSharedPreferences().updateANMUserName("");
        application.logoutCurrentUser();

        return super.onOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Attaches a logout menu item to the provided menu
     *
     * @param menu      The menu to attach the logout menu item
     */
    protected void attachLogoutMenuItem(Menu menu) {
        menu.add(0, MENU_ITEM_LOGOUT, menu.size(), R.string.logout_text);
        menu.add(0, MENU_ITEM_LOGOUT_and_refresh_anm, menu.size(),"Logout and Refresh");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        attachLogoutMenuItem(menu);
        return true;
    }

    protected abstract void onCreation();

    protected abstract void onResumption();

    public void startFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, FormActivity.class);
    }

    public void startMicroFormActivity(String formName, String entityId, String metaData) {
        launchForm(formName, entityId, metaData, MicroFormActivity.class);
    }

    private void launchForm(String formName, String entityId, String metaData, Class formType) {
        this.metaData = metaData;

        Intent intent = new Intent(this, formType);
        intent.putExtra(FORM_NAME_PARAM, formName);
        intent.putExtra(ENTITY_ID_PARAM, entityId);
        addFieldOverridesIfExist(intent);
        startActivityForResult(intent, FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
    }

    private void addFieldOverridesIfExist(Intent intent) {
        if (hasMetadata()) {
            Map<String, String> metaDataMap = new Gson().fromJson(
                    this.metaData, new TypeToken<Map<String, String>>() {
                    }.getType());
            if (metaDataMap.containsKey(FIELD_OVERRIDES_PARAM)) {
                intent.putExtra(FIELD_OVERRIDES_PARAM, metaDataMap.get(FIELD_OVERRIDES_PARAM));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSuccessfulFormSubmission(resultCode)) {
            logInfo("Form successfully saved. MetaData: " + metaData);
            if (hasMetadata()) {
                Map<String, String> metaDataMap = new Gson().fromJson(metaData, new TypeToken<Map<String, String>>() {
                }.getType());
                if (metaDataMap.containsKey(ENTITY_ID) && metaDataMap.containsKey(ALERT_NAME_PARAM)) {
                    Context.getInstance().alertService().changeAlertStatusToInProcess(metaDataMap.get(ENTITY_ID), metaDataMap.get(ALERT_NAME_PARAM));
                }
            }
        }
    }

    private boolean isSuccessfulFormSubmission(int resultCode) {
        return resultCode == AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;
    }

    private boolean hasMetadata() {
        return this.metaData != null && !this.metaData.equalsIgnoreCase("undefined");
    }

    protected Context context() {
        return Context.getInstance().updateApplicationContext(this.getApplicationContext());
    }
}

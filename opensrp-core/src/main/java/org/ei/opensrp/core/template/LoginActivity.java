package org.ei.opensrp.core.template;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ihs.odkate.base.Odkate;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.core.BuildConfig;
import org.ei.opensrp.core.R;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.ei.opensrp.view.activity.SettingsActivity;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.ei.opensrp.core.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.ei.opensrp.domain.LoginResponse.SUCCESS;
import static org.ei.opensrp.core.utils.Utils.resetLocale;

public abstract class LoginActivity extends AppCompatActivity {
    private Context context;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getClass().getName(), "Loading Login activity");

        resetLocale(this, true);
        context = Context.getInstance().updateApplicationContext(this.getApplicationContext());

        setContentView(getLoginLayout());

        ((TextView) findViewById(R.id.application_name)).setText(applicationName());

        if(getActionBar() != null){
            getActionBar().setDisplayShowTitleEnabled(false);
        }

        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        getLoginButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
        initializeProgressDialog();

        Log.v(getClass().getName(), "Created login view");
    }

    protected int getLoginLayout(){
        return R.layout.login;
    }

    protected abstract String applicationName();

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(getClass().getName(), "Fully loaded");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().toString().equalsIgnoreCase("Settings")){
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeBuildDetails() {
        try {
            getBuildView().setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error fetching build details: " + e);
            getBuildView().setText("No build and version info found");
        }
    }

    protected TextView getBuildView(){
        return (TextView) findViewById(R.id.login_build);
    }

    protected EditText getUsernameView(){
        return (EditText) findViewById(R.id.username_input);
    }

    protected EditText getPasswordView(){
        return (EditText) findViewById(R.id.password_input);
    }

    protected Button getLoginButton(){
        return (Button) findViewById(R.id.login_button);
    }

    protected ProgressDialog getProgressView(){
        return progressDialog;
    }

    protected View getLogoContainer(){
        return findViewById(R.id.logo_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!context.IsUserLoggedOut()) {
            goToHome();
        }

        fillUserIfExists();
    }

    protected void login(final View view) {
        hideKeyboard();
        view.setClickable(false);

        final String userName = getUsernameView().getText().toString();
        final String password = getPasswordView().getText().toString();

        if (context.userService().hasARegisteredUser()) {
            localLogin(view, userName, password);
        } else {
            remoteLogin(view, userName, password);
        }
        Log.i(getClass().getName(), "Login result finished "+DateTime.now().toString());
    }

    private void setDoneActionHandlerOnPasswordField() {
        getPasswordView().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(findViewById(R.id.login_button));
                }
                return false;
            }
        });
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.login_dialog_title);
        progressDialog.setMessage(getString(R.string.login_dialog_message));
    }

    private void localLogin(final View view, final String userName, final String password) {
        if (context.userService().isValidLocalLogin(userName, password)) {
            localLoginWith(userName, password);
        } else {
            showErrorDialog(getString(R.string.login_failed_dialog_message));
            view.setClickable(true);
        }
    }

    private void remoteLogin(final View view, final String userName, final String password) {
        tryRemoteLogin(this, userName, password, new Listener<LoginResponse>() {
            public void onEvent(LoginResponse loginResponse) {
            if (loginResponse == SUCCESS) {
                remoteLoginWith(userName, password, loginResponse.payload());
            } else {
                if (loginResponse == null) {
                    showErrorDialog("Login failed. Unknown reason. Try Again");
                } else {
                    showErrorDialog(loginResponse.message());
                }
                view.setClickable(true);
            }
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        dialog.show();
    }

    private void tryRemoteLogin(final android.content.Context appContext, final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                progressDialog.show();
            }

            @Override
            public void setInvisible() {
                progressDialog.dismiss();
            }
        });

        task.doActionInBackground(new BackgroundAction<LoginResponse>() {
            public LoginResponse actionToDoInBackgroundThread() {
                LoginResponse lr = context.userService().isValidRemoteLogin(userName, password);
                try {
                    customTaskWithRemoteLogin(appContext, userName, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return lr;
            }

            public void postExecuteInUIThread(LoginResponse result) {
                afterLoginCheck.onEvent(result);
            }
        });
    }

    protected abstract void customTaskWithRemoteLogin(android.content.Context context, String username, String password);

    private void fillUserIfExists() {
        if (context.userService().hasARegisteredUser()) {
            getUsernameView().setText(context.allSharedPreferences().fetchRegisteredANM());
            getUsernameView().setEnabled(false);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void localLoginWith(String userName, String password) {
        Odkate.initDatabase(getApplicationContext(), password);

        context.userService().localLogin(userName, password);
        goToHome();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(getClass().getName(), "Starting DrishtiSyncScheduler "+DateTime.now().toString());
                DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
                Log.i(getClass().getName(), "Started DrishtiSyncScheduler "+DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLoginWith(String userName, String password, String userInfo) {
        Odkate.initDatabase(getApplicationContext(), password);

        context.userService().remoteLogin(userName, password, userInfo);

        try{
            Utils.writePreference(this, "team", new JSONObject(userInfo).getJSONObject("team").toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

        while(StringUtils.isBlank(context.allSettings().fetchUserInformation())){
            // stay hanged until app saves login info in background
            // todo ugly way of handling it.
            // change the original service to allow post execute task in master
        }
        goToHome();

        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    protected abstract void goToHome() ;

    protected String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    protected String getBuildDate() throws PackageManager.NameNotFoundException, IOException {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new java.util.Date(BuildConfig.TIMESTAMP));
    }
}

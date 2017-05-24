package org.ei.opensrp.path.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.domain.ResponseStatus;
import org.ei.opensrp.domain.TimeStatus;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.service.intent.PullUniqueIdsIntentService;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.UserService;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import util.PathConstants;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;
import static org.ei.opensrp.domain.LoginResponse.NO_INTERNET_CONNECTIVITY;
import static org.ei.opensrp.domain.LoginResponse.SUCCESS;
import static org.ei.opensrp.domain.LoginResponse.UNAUTHORIZED;
import static org.ei.opensrp.domain.LoginResponse.UNKNOWN_RESPONSE;
import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logVerbose;

public class LoginActivity extends Activity {
    private Context context;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    public static final String ENGLISH_LOCALE = "en";
    public static final String KANNADA_LOCALE = "kn";
    public static final String URDU_LOCALE = "ur";
    public static final String ENGLISH_LANGUAGE = "English";
    public static final String KANNADA_LANGUAGE = "Kannada";
    public static final String URDU_LANGUAGE = "Urdu";
    android.content.Context appContext;
    private RemoteLoginTask remoteLoginTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logVerbose("Initializing ...");
        try {
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this));
            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(preferredLocale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {

        }

        setContentView(org.ei.opensrp.R.layout.login);

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        appContext=this;
        context = Context.getInstance().updateApplicationContext(this.getApplicationContext());
        positionViews();
        initializeLoginFields();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        initializeProgressDialog();
        // getActionBar().setTitle("");
        // getActionBar().setIcon(getResources().getDrawable(org.ei.opensrp.R.drawable.logo));
        //  getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));
        setLanguage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeBuildDetails() {
        TextView buildDetailsTextView = (TextView) findViewById(org.ei.opensrp.R.id.login_build);
        try {
            buildDetailsTextView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!context.IsUserLoggedOut()) {
            goToHome(false);
        }
    }

    public void login(final View view) {
        login(view, !context.allSharedPreferences().fetchForceRemoteLogin());
    }

    public void login(final View view, boolean localLogin) {
        android.util.Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        hideKeyboard();
        view.setClickable(false);

        final String userName = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
            if (localLogin) {
                localLogin(view, userName, password);
            } else {
                remoteLogin(view, userName, password);
            }
        } else {
            showErrorDialog(getResources().getString(R.string.unauthorized));
            view.setClickable(true);
        }

        android.util.Log.i(getClass().getName(), "Login result finished " + DateTime.now().toString());
    }

    private void initializeLoginFields() {
        userNameEditText = ((EditText) findViewById(org.ei.opensrp.R.id.login_userNameText));
        passwordEditText = ((EditText) findViewById(org.ei.opensrp.R.id.login_passwordText));
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(findViewById(org.ei.opensrp.R.id.login_loginButton));
                }
                return false;
            }
        });
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.ei.opensrp.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.ei.opensrp.R.string.loggin_in_dialog_message));
    }

    private void localLogin(View view, String userName, String password) {
        view.setClickable(true);
        if (context.userService().isUserInValidGroup(userName, password)
                && TimeStatus.OK.equals(context.userService().validateStoredServerTimeZone())) {
            localLoginWith(userName, password);
        } else {
            login(findViewById(org.ei.opensrp.R.id.login_loginButton), false);
        }
    }

    private void remoteLogin(final View view, final String userName, final String password) {
        tryRemoteLogin(userName, password, new Listener<LoginResponse>() {
            public void onEvent(LoginResponse loginResponse) {
                view.setClickable(true);
                if (loginResponse == SUCCESS) {
                    if (context.userService().isUserInPioneerGroup(userName)) {
                        TimeStatus timeStatus = context.userService().validateDeviceTime(
                                loginResponse.payload(), PathConstants.MAX_SERVER_TIME_DIFFERENCE);
                        if (timeStatus.equals(TimeStatus.OK)) {
                            remoteLoginWith(userName, password, loginResponse.payload());
                            Intent intent = new Intent(appContext, PullUniqueIdsIntentService.class);
                            appContext.startService(intent);
                        } else {
                            if (timeStatus.equals(TimeStatus.TIMEZONE_MISMATCH)) {
                                TimeZone serverTimeZone = context.userService()
                                        .getServerTimeZone(loginResponse.payload());
                                showErrorDialog(getString(timeStatus.getMessage(),
                                        serverTimeZone.getDisplayName()));
                            } else {
                                showErrorDialog(getString(timeStatus.getMessage()));
                            }
                        }
                    } else {// Valid user from wrong group trying to log in
                        showErrorDialog(getResources().getString(R.string.unauthorized_group));
                    }
                } else {
                    if (loginResponse == null) {
                        showErrorDialog("Login failed. Unknown reason. Try Again");
                    } else {
                        if (loginResponse == NO_INTERNET_CONNECTIVITY) {
                            showErrorDialog(getResources().getString(R.string.no_internet_connectivity));
                        } else if (loginResponse == UNKNOWN_RESPONSE) {
                            showErrorDialog(getResources().getString(R.string.unknown_response));
                        } else if (loginResponse == UNAUTHORIZED) {
                            showErrorDialog(getResources().getString(R.string.unauthorized));
                        }
//                        showErrorDialog(loginResponse.message());
                    }
                }
            }
        });
    }

    private void showErrorDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.ei.opensrp.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        dialog.show();
    }

    private void showMessageDialog(String message, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.ei.opensrp.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", ok)
                .setNegativeButton("Cancel", cancel)
                .create();

        dialog.show();
    }

    private void getLocation() {
        tryGetLocation(new Listener<Response<String>>() {
            @Override
            public void onEvent(Response<String> data) {
                if (data.status() == ResponseStatus.success) {
                    context.userService().saveAnmLocation(data.payload());
                }
            }
        });
    }

    private void tryGetLocation(final Listener<Response<String>> afterGet) {
        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
            }

            @Override
            public void setInvisible() {
                Log.logInfo("Successfully get location");
            }
        });

        task.doActionInBackground(new BackgroundAction<Response<String>>() {
            @Override
            public Response<String> actionToDoInBackgroundThread() {
                return context.userService().getLocationInformation();
            }

            @Override
            public void postExecuteInUIThread(Response<String> result) {
                afterGet.onEvent(result);
            }
        });
    }

    private void tryRemoteLogin(final String userName, final String password, final Listener<LoginResponse> afterLoginCheck) {
        if (remoteLoginTask != null && !remoteLoginTask.isCancelled()) {
            remoteLoginTask.cancel(true);
        }

        remoteLoginTask = new RemoteLoginTask(userName, password, afterLoginCheck);
        remoteLoginTask.execute();
    }

    private class RemoteLoginTask extends AsyncTask<Void, Void, LoginResponse> {
        private final String userName;
        private final String password;
        private final Listener<LoginResponse> afterLoginCheck;

        public RemoteLoginTask(String userName, String password, Listener<LoginResponse> afterLoginCheck) {
            this.userName = userName;
            this.password = password;
            this.afterLoginCheck = afterLoginCheck;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected LoginResponse doInBackground(Void... params) {
            return context.userService().isValidRemoteLogin(userName, password);
        }

        @Override
        protected void onPostExecute(LoginResponse loginResponse) {
            super.onPostExecute(loginResponse);
            progressDialog.dismiss();
            afterLoginCheck.onEvent(loginResponse);
        }
    }

    private void fillUserIfExists() {
        if (context.userService().hasARegisteredUser()) {
            userNameEditText.setText(context.allSharedPreferences().fetchRegisteredANM());
            userNameEditText.setEnabled(false);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void localLoginWith(String userName, String password) {
        context.userService().localLogin(userName, password);
        goToHome(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.util.Log.i(getClass().getName(), "Starting DrishtiSyncScheduler " + DateTime.now().toString());
                DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
                android.util.Log.i(getClass().getName(), "Started DrishtiSyncScheduler " + DateTime.now().toString());
            }
        }).start();
    }

    private void remoteLoginWith(String userName, String password, String userInfo) {
        context.userService().remoteLogin(userName, password, userInfo);
        goToHome(true);
        DrishtiSyncScheduler.startOnlyIfConnectedToNetwork(getApplicationContext());
    }

    private void goToHome(boolean remote) {
        VaccinatorApplication.setCrashlyticsUser(context);
        Intent intent = new Intent(this, ChildSmartRegisterActivity.class);
        intent.putExtra(BaseRegisterActivity.IS_REMOTE_LOGIN, remote);
        startActivity(intent);
        finish();
    }

    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    private String getBuildDate() throws PackageManager.NameNotFoundException, IOException {
        ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
        ZipFile zf = new ZipFile(applicationInfo.sourceDir);
        ZipEntry ze = zf.getEntry("classes.dex");
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new java.util.Date(ze.getTime()));
    }

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources res = Context.getInstance().applicationContext().getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(preferredLocale);
        res.updateConfiguration(conf, dm);

    }

    public static String switchLanguagePreference() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));

        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if (URDU_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(URDU_LOCALE);
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(URDU_LOCALE);
            res.updateConfiguration(conf, dm);
            return URDU_LANGUAGE;
        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
            Resources res = Context.getInstance().applicationContext().getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            conf.locale = new Locale(ENGLISH_LOCALE);
            res.updateConfiguration(conf, dm);
            return ENGLISH_LANGUAGE;
        }
    }

    private void positionViews() {
        final ScrollView canvasSV = (ScrollView) findViewById(R.id.canvasSV);
        final RelativeLayout canvasRL = (RelativeLayout) findViewById(R.id.canvasRL);
        final LinearLayout logoCanvasLL = (LinearLayout) findViewById(R.id.logoCanvasLL);
        final LinearLayout credentialsCanvasLL = (LinearLayout) findViewById(R.id.credentialsCanvasLL);

        canvasSV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasSV.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int windowHeight = canvasSV.getHeight();
                int topMargin = (windowHeight / 2)
                        - (credentialsCanvasLL.getHeight() / 2)
                        - logoCanvasLL.getHeight();
                topMargin = topMargin / 2;

                RelativeLayout.LayoutParams logoCanvasLP = (RelativeLayout.LayoutParams) logoCanvasLL.getLayoutParams();
                logoCanvasLP.setMargins(0, topMargin, 0, 0);
                logoCanvasLL.setLayoutParams(logoCanvasLP);

                canvasRL.setMinimumHeight(windowHeight);
            }
        });
    }

}

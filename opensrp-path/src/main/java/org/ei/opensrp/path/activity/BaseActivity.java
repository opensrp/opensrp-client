package org.ei.opensrp.path.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.receiver.SyncStatusBroadcastReceiver;
import org.ei.opensrp.path.sync.ECSyncUpdater;
import org.ei.opensrp.path.sync.PathAfterFetchListener;
import org.ei.opensrp.path.sync.PathUpdateActionsTask;
import org.ei.opensrp.path.toolbar.BaseToolbar;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.opensrp.api.constants.Gender;

import java.util.ArrayList;
import java.util.Calendar;

import util.JsonFormUtils;
import util.NetworkUtils;

import static org.ei.opensrp.util.Log.logError;

/**
 * Base activity class for all other PATH activity classes. Implements:
 * - A uniform navigation bar that is launched by swiping from the left
 * - Support for specifying which {@link BaseToolbar} to use
 * <p/>
 * This activity requires that the base view for any child activity be {@link DrawerLayout}
 * Make sure include the navigation view as the last element in the activity's root DrawerLayout
 * like this:
 * <p/>
 * <include layout="@layout/nav_view_base"/>
 * <p/>
 * Created by Jason Rogena - jrogena@ona.io on 16/02/2017.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SyncStatusBroadcastReceiver.SyncStatusListener {
    private static final String TAG = "BaseActivity";
    private BaseToolbar toolbar;
    private Menu menu;
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private PathAfterFetchListener pathAfterFetchListener;
    private Snackbar syncStatusSnackbar;
    private ProgressDialog progressDialog;
    private ArrayList<Notification> notifications;
    private BaseActivityToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        toolbar = (BaseToolbar) findViewById(getToolbarId());
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        toggle = new BaseActivityToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pathAfterFetchListener = new PathAfterFetchListener();
        notifications = new ArrayList<>();

        initializeProgressDialog();
    }

    @Override
    public void onSyncStart() {
        refreshSyncStatusViews(null);
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        refreshSyncStatusViews(fetchStatus);
    }

    private void registerSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
    }

    private void unregisterSyncStatusBroadcastReceiver() {
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }

    public BaseToolbar getBaseToolbar() {
        return toolbar;
    }


    ///////////////////////////////// navigation bar with menu
//    private void refreshSyncStatusViews(FetchStatus fetchStatus) {
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if (navigationView != null && navigationView.getMenu() != null) {
//            MenuItem syncMenuItem = navigationView.getMenu().findItem(R.id.nav_sync);
//            if (syncMenuItem != null) {
//                if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
//                    syncMenuItem.setTitle(R.string.syncing);
//                    ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
//                    if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
//                    syncStatusSnackbar = Snackbar.make(rootView, R.string.syncing,
//                            Snackbar.LENGTH_LONG);
//                    syncStatusSnackbar.show();
//                } else {
//                    if (fetchStatus != null) {
//                        if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
//                        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
//                        if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
//                            syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_failed, Snackbar.LENGTH_INDEFINITE);
//                            syncStatusSnackbar.setAction(R.string.retry, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    startSync();
//                                }
//                            });
//                        } else if (fetchStatus.equals(FetchStatus.fetched)
//                                || fetchStatus.equals(FetchStatus.nothingFetched)) {
//                            syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_complete, Snackbar.LENGTH_LONG);
//                        }
//                        syncStatusSnackbar.show();
//                    }
//                    String lastSync = getLastSyncTime();
//
//                    if (!TextUtils.isEmpty(lastSync)) {
//                        lastSync = " " + String.format(getString(R.string.last_sync), lastSync);
//                    }
//                    syncMenuItem.setTitle(String.format(getString(R.string.sync_), lastSync));
//                }
//            }
//        }
//    }

    /////////////////////////for custom navigation //////////////////////////////////////////////////////
    private void refreshSyncStatusViews(FetchStatus fetchStatus) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null && navigationView.getMenu() != null) {
            LinearLayout syncMenuItem = (LinearLayout) navigationView.findViewById(R.id.nav_sync);
            if (syncMenuItem != null) {
                if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                    ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                    if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
                    syncStatusSnackbar = Snackbar.make(rootView, R.string.syncing,
                            Snackbar.LENGTH_LONG);
                    syncStatusSnackbar.show();
                    ((TextView) syncMenuItem.findViewById(R.id.nav_synctextview)).setText(R.string.syncing);
                } else {
                    if (fetchStatus != null) {
                        if (syncStatusSnackbar != null) syncStatusSnackbar.dismiss();
                        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
                        if (fetchStatus.equals(FetchStatus.fetchedFailed)) {
                            syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_failed, Snackbar.LENGTH_INDEFINITE);
                            syncStatusSnackbar.setActionTextColor(getResources().getColor(R.color.snackbar_action_color));
                            syncStatusSnackbar.setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startSync();
                                }
                            });
                        } else if (fetchStatus.equals(FetchStatus.fetched)
                                || fetchStatus.equals(FetchStatus.nothingFetched)) {
                            syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_complete, Snackbar.LENGTH_LONG);
                        } else if (fetchStatus.equals(FetchStatus.noConnection)) {
                            syncStatusSnackbar = Snackbar.make(rootView, R.string.sync_failed_no_internet, Snackbar.LENGTH_LONG);
                        }
                        syncStatusSnackbar.show();
                    }

//<<<<<<< HEAD
//                    if (!TextUtils.isEmpty(lastSync)) {
//                        lastSync = " " + String.format(getString(R.string.last_sync), lastSync);
//                    }
//                    ((TextView)syncMenuItem.findViewById(R.id.nav_synctextview)).setText(String.format(getString(R.string.sync_), lastSync));
//=======
                    updateLastSyncText();
                }
            }
        }
    }

    protected ActionBarDrawerToggle getDrawerToggle() {
        return toggle;
    }

    protected void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        drawer.openDrawer(Gravity.LEFT);
    }

    private void updateLastSyncText() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null && navigationView.getMenu() != null) {
            TextView syncMenuItem = ((TextView) navigationView.findViewById(R.id.nav_synctextview));
            if (syncMenuItem != null) {
                String lastSync = getLastSyncTime();

                if (!TextUtils.isEmpty(lastSync)) {
                    lastSync = " " + String.format(getString(R.string.last_sync), lastSync);
                }
                syncMenuItem.setText(String.format(getString(R.string.sync_), lastSync));
            }
        }
    }

    private void initializeCustomNavbarLIsteners() {


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout syncMenuItem = (LinearLayout) drawer.findViewById(R.id.nav_sync);
        syncMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSync();
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        LinearLayout addchild = (LinearLayout) drawer.findViewById(R.id.nav_register);
        addchild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonForm("child_enrollment", null);
                drawer.closeDrawer(GravityCompat.START);

            }
        });
        LinearLayout outofcatchment = (LinearLayout) drawer.findViewById(R.id.nav_record_vaccination_out_catchment);
        outofcatchment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startJsonForm("out_of_catchment_service", null);
                drawer.closeDrawer(GravityCompat.START);

            }
        });
        LinearLayout stockregister = (LinearLayout) drawer.findViewById(R.id.stockcontrol);
        stockregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StockActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);

            }
        });
        LinearLayout childregister = (LinearLayout) drawer.findViewById(R.id.child_register);
        childregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VaccinatorApplication.setCrashlyticsUser(VaccinatorApplication.getInstance().context());
                Intent intent = new Intent(getApplicationContext(), ChildSmartRegisterActivity.class);
                intent.putExtra(BaseRegisterActivity.IS_REMOTE_LOGIN, false);
                startActivity(intent);
                finish();
                drawer.closeDrawer(GravityCompat.START);

//                finish();
            }
        });
        LinearLayout hia2 = (LinearLayout) drawer.findViewById(R.id.hia2reports);
        hia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HIA2ReportsActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);

            }
        });

    }

    private String getLastSyncTime() {
        String lastSync = "";
        long milliseconds = ECSyncUpdater.getInstance(this).getLastCheckTimeStamp();
        if (milliseconds > 0) {
            DateTime lastSyncTime = new DateTime(milliseconds);
            DateTime now = new DateTime(Calendar.getInstance());
            Minutes minutes = Minutes.minutesBetween(lastSyncTime, now);
            if (minutes.getMinutes() < 1) {
                Seconds seconds = Seconds.secondsBetween(lastSyncTime, now);
                lastSync = seconds.getSeconds() + "s";
            } else if (minutes.getMinutes() >= 1 && minutes.getMinutes() < 60) {
                lastSync = minutes.getMinutes() + "m";
            } else if (minutes.getMinutes() >= 60 && minutes.getMinutes() < 1440) {
                Hours hours = Hours.hoursBetween(lastSyncTime, now);
                lastSync = hours.getHours() + "h";
            } else {
                Days days = Days.daysBetween(lastSyncTime, now);
                lastSync = days.getDays() + "d";
            }
        }
        return lastSync;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSyncStatusBroadcastReceiver();
        initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSyncStatusBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (toolbar.getSupportedMenu() != 0) {
            this.menu = menu;
            getMenuInflater().inflate(toolbar.getSupportedMenu(), menu);
            toolbar.prepareMenu();
            return super.onCreateOptionsMenu(menu);
        } else {
            toolbar.prepareMenu();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(toolbar.onMenuItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Button logoutButton = (Button) navigationView.findViewById(R.id.logout_b);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrishtiApplication application = (DrishtiApplication) getApplication();
                application.logoutCurrentUser();
                finish();
            }
        });

        ImageButton cancelButton = (ImageButton) navigationView.findViewById(R.id.cancel_b);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) BaseActivity.this.findViewById(getDrawerLayoutId());
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });

        TextView initialsTV = (TextView) navigationView.findViewById(R.id.initials_tv);
        initialsTV.setText(getLoggedInUserInitials());

        try {
            String preferredName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
                    getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
            TextView nameTV = (TextView) navigationView.findViewById(R.id.name_tv);
            nameTV.setText(preferredName);


        } catch (Exception e) {
            logError("Error on initView : Getting Preferences: Getting Initials");
        }
        refreshSyncStatusViews(null);
        initializeCustomNavbarLIsteners();
    }

    //FIXME this method conflicts with raihan's don't know what the difference is
//    public void initViews() {
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        Button logoutButton = (Button) navigationView.findViewById(R.id.logout_b);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DrishtiApplication application = (DrishtiApplication) getApplication();
//                application.logoutCurrentUser();
//                finish();
//            }
//        });
//
//        ImageButton cancelButton = (ImageButton) navigationView.findViewById(R.id.cancel_b);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DrawerLayout drawer = (DrawerLayout) BaseActivity.this.findViewById(getDrawerLayoutId());
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                }
//            }
//        });
//
//        TextView initialsTV = (TextView) navigationView.findViewById(R.id.initials_tv);
//        String preferredName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
//                getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
//        if (!TextUtils.isEmpty(preferredName)) {
//            String[] initialsArray = preferredName.split(" ");
//            String initials = "";
//            if (initialsArray.length > 0) {
//                initials = initialsArray[0].substring(0, 1);
//                if (initialsArray.length > 1) {
//                    initials = initials + initialsArray[1].substring(0, 1);
//                }
//            }
//
//            initialsTV.setText(initials.toUpperCase());
//        }
//
//        TextView nameTV = (TextView) navigationView.findViewById(R.id.name_tv);
//        nameTV.setText(preferredName);
//        refreshSyncStatusViews(null);
//        initializeCustomNavbarLIsteners();
//    }
    protected String getLoggedInUserInitials() {

        try {

            String preferredName = getOpenSRPContext().allSharedPreferences().getANMPreferredName(
                    getOpenSRPContext().allSharedPreferences().fetchRegisteredANM());
            if (!TextUtils.isEmpty(preferredName)) {
                String[] initialsArray = preferredName.split(" ");
                String initials = "";
                if (initialsArray.length > 0) {
                    initials = initialsArray[0].substring(0, 1);
                    if (initialsArray.length > 1) {
                        initials = initials + initialsArray[1].substring(0, 1);
                    }
                }

                return initials.toUpperCase();
            }

        } catch (Exception e) {
            logError("Error on initView : Getting Preferences: Getting Initials");
        }

        return null;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_register) {
            startJsonForm("child_enrollment", null);
        } else if (id == R.id.nav_record_vaccination_out_catchment) {
            startJsonForm("out_of_catchment_service", null);
        } else if (id == R.id.nav_sync) {
            startSync();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startSync() {
        PathUpdateActionsTask pathUpdateActionsTask = new PathUpdateActionsTask(
                this, getOpenSRPContext().actionService(),
                getOpenSRPContext().formSubmissionSyncService(),
                new SyncProgressIndicator(),
                getOpenSRPContext().allFormVersionSyncService());
        pathUpdateActionsTask.updateFromServer(pathAfterFetchListener);
    }

    /**
     * Updates all gender affected views
     *
     * @param gender The gender to update the
     */
    protected int[] updateGenderViews(Gender gender) {
        int darkShade = R.color.gender_neutral_dark_green;
        int normalShade = R.color.gender_neutral_green;
        int lightSade = R.color.gender_neutral_light_green;

        if (gender.equals(Gender.FEMALE)) {
            darkShade = R.color.female_dark_pink;
            normalShade = R.color.female_pink;
            lightSade = R.color.female_light_pink;
        } else if (gender.equals(Gender.MALE)) {
            darkShade = R.color.male_dark_blue;
            normalShade = R.color.male_blue;
            lightSade = R.color.male_light_blue;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(darkShade));
        }
        toolbar.setBackground(new ColorDrawable(getResources().getColor(normalShade)));
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        viewGroup.setBackground(new ColorDrawable(getResources().getColor(lightSade)));

        return new int[]{darkShade, normalShade, lightSade};
    }

    protected void startJsonForm(String formName, String entityId) {
        try {
            if (toolbar instanceof LocationSwitcherToolbar) {
                LocationSwitcherToolbar locationSwitcherToolbar = (LocationSwitcherToolbar) toolbar;
                String locationId = JsonFormUtils.getOpenMrsLocationId(getOpenSRPContext(),
                        locationSwitcherToolbar.getCurrentLocation());

                JsonFormUtils.startForm(this, getOpenSRPContext(), REQUEST_CODE_GET_JSON,
                        formName, entityId, null, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    protected void showNotification(int message, int notificationIcon, int positiveButtonText,
                                    View.OnClickListener positiveButtonClick,
                                    int negativeButtonText,
                                    View.OnClickListener negativeButtonClick,
                                    Object tag) {
        String posBtnText = null;
        if (positiveButtonText != 0 && positiveButtonClick != null) {
            posBtnText = getString(positiveButtonText);
        }

        String negBtnText = null;
        if (negativeButtonText != 0 && negativeButtonClick != null) {
            negBtnText = getString(negativeButtonText);
        }

        showNotification(getString(message), getResources().getDrawable(notificationIcon),
                posBtnText, positiveButtonClick,
                negBtnText, negativeButtonClick, tag);
    }

    protected void showNotification(String message, Drawable notificationIcon, String positiveButtonText,
                                    View.OnClickListener positiveButtonOnClick,
                                    String negativeButtonText,
                                    View.OnClickListener negativeButtonOnClick,
                                    Object tag) {
        Notification notification = new Notification(message, notificationIcon, positiveButtonText,
                positiveButtonOnClick, negativeButtonText, negativeButtonOnClick, tag);

        // Add the notification as the last element in the notification list
        String notificationMessage = notification.message;
        if (notificationMessage == null) notificationMessage = "";
        for (Notification curNotification : notifications) {
            if (notificationMessage.equals(curNotification.message)) {
                notifications.remove(curNotification);
            }
        }
        notifications.add(notification);

        updateNotificationViews(notification);
    }

    private void updateNotificationViews(final Notification notification) {
        TextView notiMessage = (TextView) findViewById(R.id.noti_message);
        notiMessage.setText(notification.message);
        Button notiPositiveButton = (Button) findViewById(R.id.noti_positive_button);
        notiPositiveButton.setTag(notification.tag);
        if (notification.positiveButtonText != null) {
            notiPositiveButton.setVisibility(View.VISIBLE);
            notiPositiveButton.setText(notification.positiveButtonText);
            notiPositiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notifications.size() > 0) {
                        notifications.remove(notifications.size() - 1);
                    }

                    if (notification.positiveButtonOnClick != null) {
                        notification.positiveButtonOnClick.onClick(v);
                    }

                    // Show the second last notification
                    if (notifications.size() > 0) {
                        updateNotificationViews(notifications.get(notifications.size() - 1));
                    }
                }
            });
        } else {
            notiPositiveButton.setVisibility(View.GONE);
        }

        Button notiNegativeButton = (Button) findViewById(R.id.noti_negative_button);
        notiNegativeButton.setTag(notification.tag);
        if (notification.negativeButtonText != null) {
            notiNegativeButton.setVisibility(View.VISIBLE);
            notiNegativeButton.setText(notification.negativeButtonText);
            notiNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notifications.size() > 0) {
                        notifications.remove(notifications.size() - 1);
                    }

                    if (notification.negativeButtonOnClick != null) {
                        notification.negativeButtonOnClick.onClick(v);
                    }

                    // Show the second last notification
                    if (notifications.size() > 0) {
                        updateNotificationViews(notifications.get(notifications.size() - 1));
                    }
                }
            });
        } else {
            notiNegativeButton.setVisibility(View.GONE);
        }

        ImageView notiIcon = (ImageView) findViewById(R.id.noti_icon);
        if (notification.notificationIcon != null) {
            notiIcon.setVisibility(View.VISIBLE);
            notiIcon.setImageDrawable(notification.notificationIcon);
        } else {
            notiIcon.setVisibility(View.GONE);
        }

        final LinearLayout notificationLL = (LinearLayout) findViewById(R.id.notification);

        Animation slideDownAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                notificationLL.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        notificationLL.clearAnimation();
        notificationLL.startAnimation(slideDownAnimation);
    }

    protected void hideNotification() {
        final LinearLayout notification = (LinearLayout) findViewById(R.id.notification);
        if (notification.getVisibility() == View.VISIBLE) {
            Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    notification.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            notification.startAnimation(slideUpAnimation);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String jsonString = data.getStringExtra("json");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            JsonFormUtils.saveForm(this, getOpenSRPContext(), jsonString, allSharedPreferences.fetchRegisteredANM());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected BaseToolbar getToolbar() {
        return toolbar;
    }

    /**
     * The layout resource file to user for this activity
     *
     * @return The resource id for the layout file to use
     */
    protected abstract int getContentView();

    /**
     * The id for the base {@link DrawerLayout} for the activity
     *
     * @return
     */
    protected abstract int getDrawerLayoutId();

    /**
     * The id for the toolbar used in this activity
     *
     * @return The id for the toolbar used
     */
    protected abstract int getToolbarId();

    public Context getOpenSRPContext() {
        return Context.getInstance().updateApplicationContext(this.getApplicationContext());
    }

    public Menu getMenu() {
        return menu;
    }

    /**
     * The activity to go back to
     *
     * @return
     */
    protected abstract Class onBackActivity();

    private class BaseActivityToggle extends ActionBarDrawerToggle {

        public BaseActivityToggle(Activity activity, DrawerLayout drawerLayout, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        public BaseActivityToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
            super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            if (!SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                updateLastSyncText();
            }
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
        }
    }

    public void processInThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(runnable).start();
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.saving_dialog_title));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    protected void showProgressDialog(String title, String message) {
        if (progressDialog != null) {
            if (StringUtils.isNotBlank(title)) {
                progressDialog.setTitle(title);
            }

            if (StringUtils.isNotBlank(message)) {
                progressDialog.setMessage(message);
            }

            progressDialog.show();
        }
    }

    protected void showProgressDialog() {
        showProgressDialog(getString(R.string.saving_dialog_title), getString(R.string.please_wait_message));
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private class Notification {
        public final String message;
        public final Drawable notificationIcon;
        public final String positiveButtonText;
        public final View.OnClickListener positiveButtonOnClick;
        public final String negativeButtonText;
        public final View.OnClickListener negativeButtonOnClick;
        public final Object tag;

        public Notification(String message, Drawable notificationIcon, String positiveButtonText,
                            View.OnClickListener positiveButtonOnClick,
                            String negativeButtonText,
                            View.OnClickListener negativeButtonOnClick,
                            Object tag) {
            this.message = message;
            this.notificationIcon = notificationIcon;
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonOnClick = positiveButtonOnClick;
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonOnClick = negativeButtonOnClick;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Notification) {
                Notification notification = (Notification) o;
                String message = this.message;
                if (message == null) message = "";
                String positiveButtonText = this.positiveButtonText;
                if (positiveButtonText == null) positiveButtonText = "";
                String negativeButtonText = this.negativeButtonText;
                if (negativeButtonText == null) negativeButtonText = "";

                if (message.equals(notification.message)
                        && positiveButtonText.equals(notification.positiveButtonText)
                        && negativeButtonText.equals(notification.negativeButtonText)) {
                    return true;
                }
            }
            return false;
        }
    }

}

package org.ei.opensrp.indonesia.view.activity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.LoginActivity;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.view.contract.BidanHomeContext;
import org.ei.opensrp.indonesia.view.controller.NativeAfterBidanDetailsFetchListener;
import org.ei.opensrp.indonesia.view.controller.NativeUpdateBidanDetailsTask;
import org.ei.opensrp.indonesia.view.controller.NavigationControllerINA;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.AllConstants.ENGLISH_LANGUAGE;
import static org.ei.opensrp.AllConstants.ENGLISH_LOCALE;
import static org.ei.opensrp.AllConstants.KANNADA_LANGUAGE;
import static org.ei.opensrp.AllConstants.KANNADA_LOCALE;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.FEEDBACK_BIDAN;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

/**
 * Created by Dimas Ciputra on 2/27/15.
 */
public class BidanHomeActivity extends org.ei.opensrp.view.activity.SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();
        }
    };

    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };

    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };

    private TextView kartuIbuRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView anakRegisterClientCountView;
    private TextView kohortKbCountView;

    @Override
    protected void onCreation() {
        setContentView(R.layout.smart_registers_home_bidan);
        navigationController = new NavigationControllerINA(this,anmController);
        setupViews();
        initialize();

        //error handling
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }

    private void setupViews() {
        findViewById(R.id.btn_kartu_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anak_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kohort_kb_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.feedback_option).setOnClickListener(onButtonsClickListener);

        kartuIbuRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
    }

    @Override
    protected void onResumption() {
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            FlurryFacade.logEvent("home_dashboard");
        }
    }

    private void updateRegisterCounts() {
        NativeUpdateBidanDetailsTask task = new NativeUpdateBidanDetailsTask(this, Context.getInstance().bidanController());
        task.fetch(new NativeAfterBidanDetailsFetchListener() {
            @Override
            public void afterFetch(BidanHomeContext bidanDetails) {
                updateRegisterCounts(bidanDetails);
            }
        });
    }

    public void updateRegisterCounts(BidanHomeContext homeContext) {
        if(homeContext != null) {
            kartuIbuRegisterClientCountView.setText(valueOf(homeContext.getKartuIbuCount()));
            kartuIbuANCRegisterClientCountView.setText(valueOf(homeContext.getKartuIbuANCCount()));
            kartuIbuPNCRegisterClientCountView.setText(valueOf(homeContext.getKartuIbuPNCCount()));
            anakRegisterClientCountView.setText(valueOf(homeContext.getAnakCount()));
            kohortKbCountView.setText(valueOf(homeContext.getKBCount()));
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
                updateFromServer();
                return true;
            case R.id.switchLanguageMenuItem:
                String newLanguagePreference = switchLanguagePreference();
                Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        FlurryFacade.logEvent("clicked_update_from_server");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context.actionService(), context.formSubmissionSyncService(), new SyncProgressIndicator(), context.allFormVersionSyncService());
        updateActionsTask.setAdditionalSyncService(((Context)context).uniqueIdService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context.allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    private void updateRemainingFormsToSyncCount() {
        if (remainingFormsToSyncMenuItem == null) {
            return;
        }

        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
        if (size > 0) {
            remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
            remainingFormsToSyncMenuItem.setVisible(true);
        } else {
            remainingFormsToSyncMenuItem.setVisible(false);
        }
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_kartu_ibu_register:
                    ((NavigationControllerINA)navigationController).startKartuIbuRegistry();
                    break;
                case R.id.btn_kartu_ibu_anc_register:
                    ((NavigationControllerINA)navigationController).startKartuIbuANCRegistry();
                    break;
                case R.id.btn_kartu_ibu_pnc_register:
                    ((NavigationControllerINA)navigationController).startKartuIbuPNCRegistry();
                    break;
                case R.id.btn_anak_register:
                    ((NavigationControllerINA)navigationController).startAnakBayiRegistry();
                    break;
                case R.id.btn_kohort_kb_register:
                    ((NavigationControllerINA)navigationController).startKBRegistry();
                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    ((NavigationControllerINA)navigationController).startReports();
                    break;

                case R.id.btn_videos:
                    navigationController.startVideos();
                    break;

                case R.id.feedback_option:
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("bidanId", context.allSharedPreferences().fetchRegisteredANM());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FieldOverrides fieldOverrides = new FieldOverrides(obj.toString());
                    startFormActivity(FEEDBACK_BIDAN, null, fieldOverrides.getJSONString());
                    break;
            }
        }
    };

    public String switchLanguagePreference() {
        String preferredLocale = context.allSharedPreferences().fetchLanguagePreference();
        if (ENGLISH_LOCALE.equals(preferredLocale)) {
            context.allSharedPreferences().saveLanguagePreference(AllConstantsINA.BAHASA_LOCALE);
            return AllConstantsINA.BAHASA_LANGUAGE;
        } else {
            context.allSharedPreferences().saveLanguagePreference(ENGLISH_LOCALE);
            return ENGLISH_LANGUAGE;
        }
    }
}

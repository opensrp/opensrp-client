package org.ei.opensrp.mcare;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import android.util.Log;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.mcare.anc.anc1handler;
import org.ei.opensrp.mcare.anc.anc2handler;
import org.ei.opensrp.mcare.anc.anc3handler;
import org.ei.opensrp.mcare.anc.anc4handler;
import org.ei.opensrp.mcare.anc.nbnfhandler;
import org.ei.opensrp.mcare.child.encc1handler;
import org.ei.opensrp.mcare.child.encc2handler;
import org.ei.opensrp.mcare.child.encc3handler;
import org.ei.opensrp.mcare.elco.MIS_elco_form_handler;
import org.ei.opensrp.mcare.elco.PSRFHandler;
import org.ei.opensrp.mcare.household.CensusEnrollmentHandler;
import org.ei.opensrp.mcare.household.HouseholdHandler;
import org.ei.opensrp.mcare.household.tutorial.tutorialCircleViewFlow;
import org.ei.opensrp.mcare.pnc.pnc1handler;
import org.ei.opensrp.mcare.pnc.pnc2handler;
import org.ei.opensrp.mcare.pnc.pnc3handler;
import org.ei.opensrp.service.FormSubmissionService;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.ei.opensrp.view.fragment.DisplayFormFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class NativeHomeActivity extends SecuredActivity {
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
    private TextView formsyncedtime;
    private TextView alertsyncedtime;
    private TextView ecRegisterClientCountView;
    private TextView ancRegisterClientCountView;
    private TextView pncRegisterClientCountView;
    private TextView fpRegisterClientCountView;
    private TextView childRegisterClientCountView;
    public static CommonPersonObjectController hhcontroller;
    public static CommonPersonObjectController anccontroller;
    public static CommonPersonObjectController elcocontroller;
    public static CommonPersonObjectController childcontroller;
    public static CommonPersonObjectController pnccontroller;
    public static int hhcount;
    private int elcocount;
    private int anccount;
    private int pnccount;
    private int childcount;

    @Override
    protected void onCreation() {
        FormSubmissionService.isInRegister = false;
        setContentView(R.layout.smart_registers_home);
        navigationController = new McareNavigationController(this,anmController);
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
        context().formSubmissionRouter().getHandlerMap().put("census_enrollment_form",
                new CensusEnrollmentHandler());
        context().formSubmissionRouter().getHandlerMap().put("psrf_form", new PSRFHandler());
        context().formSubmissionRouter().getHandlerMap().put("anc_reminder_visit_1",
                new anc1handler());
        context().formSubmissionRouter().getHandlerMap().put("anc_reminder_visit_2",
                new anc2handler());
        context().formSubmissionRouter().getHandlerMap().put("anc_reminder_visit_3",
                new anc3handler());
        context().formSubmissionRouter().getHandlerMap().put("anc_reminder_visit_4",
                new anc4handler());
        context().formSubmissionRouter().getHandlerMap().put("pnc_reminder_visit_1",
                new pnc1handler());
        context().formSubmissionRouter().getHandlerMap().put("pnc_reminder_visit_2",
                new pnc2handler());
        context().formSubmissionRouter().getHandlerMap().put("pnc_reminder_visit_3",
                new pnc3handler());
        context().formSubmissionRouter().getHandlerMap().put("encc_visit_1", new encc1handler());
        context().formSubmissionRouter().getHandlerMap().put("encc_visit_2", new encc2handler());
        context().formSubmissionRouter().getHandlerMap().put("encc_visit_3", new encc3handler());

        context().formSubmissionRouter().getHandlerMap().put(
                "mis_elco", new MIS_elco_form_handler());
        context().formSubmissionRouter().getHandlerMap().put(
                "birthnotificationpregnancystatusfollowup", new nbnfhandler());


        context().formSubmissionRouter().getHandlerMap().put(
                "new_household_registration", new HouseholdHandler());
    }

    private void setupViews() {
        formsyncedtime = (TextView)findViewById(R.id.formsyncedtime);
        alertsyncedtime =  (TextView)findViewById(R.id.alertsyncedtime);
        findViewById(R.id.btn_ec_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_fp_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_child_register).setOnClickListener(onRegisterStartListener);

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_ec_register_client_count);
        pncRegisterClientCountView = (TextView) findViewById(R.id.txt_pnc_register_client_count);
        ancRegisterClientCountView = (TextView) findViewById(R.id.txt_anc_register_client_count);
        fpRegisterClientCountView = (TextView) findViewById(R.id.txt_fp_register_client_count);
        childRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
    }

    private void initialize() {
        FormSubmissionService.isInRegister = false;
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.ei.opensrp.mcare.R.mipmap.logo));
        getSupportActionBar().setLogo(org.ei.opensrp.mcare.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();
    }

    @Override
    protected void onResumption() {
        FormSubmissionService.isInRegister = false;
        if(onSyncStartListener!=null){
        SYNC_STARTED.addListener(onSyncStartListener);}
        if(onSyncCompleteListener!=null){
        SYNC_COMPLETED.addListener(onSyncCompleteListener);}
        if(onFormSubmittedListener!=null){
        FORM_SUBMITTED.addListener(onFormSubmittedListener);}
        if(updateANMDetailsListener!=null){
        ACTION_HANDLED.addListener(updateANMDetailsListener);}
        LoginActivity.setLanguage();
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();


    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(HomeContext homeContext) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();

                Cursor hhcountcursor = context().commonrepository("household").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("household", "household.FWHOHFNAME NOT Null and household.FWHOHFNAME != ''"));
                hhcountcursor.moveToFirst();
                hhcount= hhcountcursor.getInt(0);
                hhcountcursor.close();
                Cursor elcocountcursor = context().commonrepository("elco").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("elco","elco.FWWOMFNAME NOT NULL and elco.FWWOMFNAME !=''  AND elco.details  LIKE '%\"FWELIGIBLE\":\"1\"%'"));
                elcocountcursor.moveToFirst();
                elcocount= elcocountcursor.getInt(0);
                elcocountcursor.close();
                Cursor anccountcursor = context().commonrepository("mcaremother").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("mcaremother","(mcaremother.Is_PNC is null or mcaremother.Is_PNC = '0') and mcaremother.FWWOMFNAME is not NUll  AND mcaremother.FWWOMFNAME != \"\"      AND mcaremother.details  LIKE '%\"FWWOMVALID\":\"1\"%'"));
                anccountcursor.moveToFirst();
                anccount= anccountcursor.getInt(0);
                anccountcursor.close();
                Cursor pnccountcursor = context().commonrepository("mcaremother").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("mcaremother","mcaremother.Is_PNC = '1' and mcaremother.FWWOMFNAME is not NUll  AND mcaremother.FWWOMFNAME != \"\"      AND mcaremother.details  LIKE '%\"FWWOMVALID\":\"1\"%'"));
                pnccountcursor.moveToFirst();
                pnccount= pnccountcursor.getInt(0);
                pnccountcursor.close();
                Cursor childcountcursor = context().commonrepository("mcarechild").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("mcarechild"," mcarechild.FWBNFGEN is not NUll AND details NOT LIKE '%\"user_type\":\"FWA\"%' "));
                childcountcursor.moveToFirst();
                childcount= childcountcursor.getInt(0);
                childcountcursor.close();

                Handler mainHandler = new Handler(getMainLooper());

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        pncRegisterClientCountView.setText(valueOf(pnccount));
                        ecRegisterClientCountView.setText(valueOf(hhcount));
                        ancRegisterClientCountView.setText(valueOf(anccount));
                        fpRegisterClientCountView.setText(valueOf(elcocount));
                        childRegisterClientCountView.setText(valueOf(childcount));
                        formsyncedtime.setText("Forms Last Synced: "+getDateCurrentTimeZone(Long.parseLong(Context.getInstance().allSettings().fetchPreviousFormSyncIndex())));
                        alertsyncedtime.setText("Alerts Last Synced: "+getDateCurrentTimeZone(Long.parseLong(Context.getInstance().allSettings().fetchPreviousFetchIndex())));
                    }
                };
                mainHandler.post(myRunnable);
            }
        }).start();
    }
    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("Etc/GMT");
            calendar.setTimeInMillis(timestamp);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        attachLogoutMenuItem(menu);
        return true;
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
                String newLanguagePreference = LoginActivity.switchLanguagePreference();
                LoginActivity.setLanguage();
                Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                this.recreate();
                return true;
            case R.id.help:
                startActivity(new Intent(this, tutorialCircleViewFlow.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
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

    @Override
    protected void onPause() {
        super.onPause();
        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context().allSharedPreferences().fetchIsSyncInProgress()) {
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
                case R.id.btn_ec_register:
                    navigationController.startECSmartRegistry();
                    break;

                case R.id.btn_anc_register:
                    navigationController.startANCSmartRegistry();
                    break;

                case R.id.btn_pnc_register:
                    navigationController.startPNCSmartRegistry();
                    break;

                case R.id.btn_child_register:
                    navigationController.startChildSmartRegistry();
                    break;

                case R.id.btn_fp_register:
                    navigationController.startFPSmartRegistry();
                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    navigationController.startReports();
                    break;

                case R.id.btn_videos:
//                    navigationController.startVideos();
                    break;
            }
        }
    };

}

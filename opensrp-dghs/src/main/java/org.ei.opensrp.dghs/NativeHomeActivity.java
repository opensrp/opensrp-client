package org.ei.opensrp.dghs;

import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.dghs.HH_woman.BirthOutcomeHandler;
import org.ei.opensrp.dghs.healthIDtasks.health_ID_task;
import org.ei.opensrp.dghs.stock.stockhandler;
import org.ei.opensrp.dghs.vaccineTasks.Last_vaccine_missedCount_task;
import org.ei.opensrp.dghs.vaccineTasks.Today_anouncement_task;
import org.ei.opensrp.dghs.vaccineTasks.Today_vaccine_task;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.ei.opensrp.view.fragment.DisplayFormFragment;

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
            Last_vaccine_missedCount_task tdv = new Last_vaccine_missedCount_task(context(),new HTTPAgent(context().applicationContext(),context().allSettings(),context().allSharedPreferences(),context().configuration()),context().configuration(),context().allSettings(),context().allSharedPreferences());
            tdv.execute();
            Today_vaccine_task tvt = new Today_vaccine_task(context(),new HTTPAgent(context().applicationContext(),context().allSettings(),context().allSharedPreferences(),context().configuration()),context().configuration(),context().allSettings(),context().allSharedPreferences());
            tvt.execute();
            health_ID_task health_id_task = new health_ID_task(context(),new HTTPAgent(context().applicationContext(),context().allSettings(),context().allSharedPreferences(),context().configuration()),context().configuration(),context().allSettings(),context().allSharedPreferences());
            health_id_task.execute();
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

    private TextView ecRegisterClientCountView;
    private TextView ancRegisterClientCountView;
    private TextView pncRegisterClientCountView;
    private TextView fpRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private int hhcount;
    private int childcount;
    private int womancount;

    @Override
    protected void onCreation() {
        setContentView(R.layout.smart_registers_home);
        navigationController = new McareNavigationController(this,anmController);
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
        context().formSubmissionRouter().getHandlerMap().put("birthoutcome",new BirthOutcomeHandler());
        context().formSubmissionRouter().getHandlerMap().put("stock",new stockhandler());



    }

    private void setupViews() {
        findViewById(R.id.btn_ec_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_stock_register).setOnClickListener(onRegisterStartListener);
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
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.logo));
        getSupportActionBar().setLogo(org.ei.opensrp.dghs.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();
//        getActionBar().setBackgroundDrawable(getReso
// urces().getDrawable(R.color.action_bar_background));
    }

    @Override
    protected void onResumption() {
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
    public void getMissingVaccineCounts(){

    }

    private void updateRegisterCounts(HomeContext homeContext) {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        Cursor hhcountcursor = context().commonrepository("household").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("household", " HoH_Fname is not null "));
        hhcountcursor.moveToFirst();
        hhcount= hhcountcursor.getInt(0);
        hhcountcursor.close();
        Cursor elcocountcursor = context().commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members"," details like '%\"Is_woman\":\"1\"%' "));
        elcocountcursor.moveToFirst();
        womancount= elcocountcursor.getInt(0);
        elcocountcursor.close();
        Cursor childcountcursor = context().commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members"," details like '%\"Is_child\":\"1\"%' "));
        childcountcursor.moveToFirst();
        childcount = childcountcursor.getInt(0);
        childcountcursor.close();
        ecRegisterClientCountView.setText(valueOf(hhcount));
        fpRegisterClientCountView.setText(valueOf(womancount));
        childRegisterClientCountView.setText(valueOf(childcount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
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
            case R.id.anouncementMenuItem:
                anouncementTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void anouncementTask() {
        Today_anouncement_task tvt = new Today_anouncement_task(this,context(),new HTTPAgent(context().applicationContext(),context().allSettings(),context().allSharedPreferences(),context().configuration()),context().configuration(),context().allSettings(),context().allSharedPreferences());
        tvt.execute();
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

                case R.id.btn_stock_register:
                    ((McareNavigationController)navigationController).startStockSmartRegistry();
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
//                    navigationController.startReports();
                    break;

                case R.id.btn_videos:
//                    navigationController.startVideos();
                    break;
            }
        }
    };
}

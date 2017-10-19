package org.ei.opensrp.dgfp;

import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.dgfp.anc.anc1handler;
import org.ei.opensrp.dgfp.anc.anc2handler;
import org.ei.opensrp.dgfp.anc.anc3handler;
import org.ei.opensrp.dgfp.anc.anc4handler;
import org.ei.opensrp.dgfp.anc.nbnfhandler;
import org.ei.opensrp.dgfp.clientDownload.ClientSearchActivity;
import org.ei.opensrp.dgfp.hh_member.HouseHoldHandler;
import org.ei.opensrp.dgfp.injectables.InjectableHandler;
import org.ei.opensrp.dgfp.pnc.ChildRegistrationhandler;
import org.ei.opensrp.dgfp.pnc.pnc1handler;
import org.ei.opensrp.dgfp.pnc.pnc2handler;
import org.ei.opensrp.dgfp.pnc.pnc3handler;
import org.ei.opensrp.dgfp.pnc.pnc4handler;
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
    private int hhcount;
    private int childcount;
    private int womancount;
    private TextView AncRegisterClientCountView;
    private int anccount;
    private TextView PncRegisterClientCountView;
    private int pnccount;
    private TextView ChildRegisterClientCountView;
    private TextView HouseholdRegisterClientCountView;
    private int nutritioncount;
    private TextView nutritionClientCountView;
    private int adolescentcount;
    private TextView adolescentClientCountView;
    private int injectablecount;
    private TextView injectableClientCountView;
    private int deathcount;
    private TextView deathClientCountView;


    @Override
    protected void onCreation() {
        setContentView(R.layout.smart_registers_home);
        navigationController = new DGFPNavigationController(this,anmController);
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
        context.formSubmissionRouter().getHandlerMap().put("childregistration",new ChildRegistrationhandler(this));
        context.formSubmissionRouter().getHandlerMap().put("birth_notification",new nbnfhandler(this));
        context.formSubmissionRouter().getHandlerMap().put("injectable",new InjectableHandler(this));
        context.formSubmissionRouter().getHandlerMap().put("new_household_registration",new HouseHoldHandler(this));
        context.formSubmissionRouter().getHandlerMap().put("anc_visit1",new anc1handler());
        context.formSubmissionRouter().getHandlerMap().put("anc_visit2",new anc2handler());
        context.formSubmissionRouter().getHandlerMap().put("anc_visit3",new anc3handler());
        context.formSubmissionRouter().getHandlerMap().put("anc_visit4",new anc4handler());
        context.formSubmissionRouter().getHandlerMap().put("pnc_visit1",new pnc1handler());
        context.formSubmissionRouter().getHandlerMap().put("pnc_visit2",new pnc2handler());
        context.formSubmissionRouter().getHandlerMap().put("pnc_visit3",new pnc3handler());
        context.formSubmissionRouter().getHandlerMap().put("pnc_visit4",new pnc4handler());

    }

    private void setupViews() {
        findViewById(R.id.household_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.elco_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_nutrition_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_child_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_adolescent_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_injectable_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_death_register).setOnClickListener(onRegisterStartListener);
//        findViewById(R.id.btn_child_register).setOnClickListener(onRegisterStartListener);
//
//        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);
        HouseholdRegisterClientCountView = (TextView) findViewById(R.id.home_register_client_count);
        fpRegisterClientCountView = (TextView) findViewById(R.id.elco_register_client_count);

        AncRegisterClientCountView = (TextView) findViewById(R.id.txt_anc_register_client_count);
        PncRegisterClientCountView = (TextView) findViewById(R.id.txt_pnc_register_client_count);
        nutritionClientCountView = (TextView) findViewById(R.id.txt_nutrition_register_client_count);
//        pncRegisterClientCountView = (TextView) findViewById(R.id.txt_pnc_register_client_count);
//        ancRegisterClientCountView = (TextView) findViewById(R.id.txt_anc_register_client_count);
        adolescentClientCountView = (TextView) findViewById(R.id.txt_adolescent_register_client_count);
        injectableClientCountView = (TextView) findViewById(R.id.txt_injectable_register_client_count);
        ChildRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        deathClientCountView = (TextView) findViewById(R.id.txt_death_register_client_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.logo));
        getSupportActionBar().setLogo(R.drawable.logo);
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
        Cursor hhcountcursor = context.commonrepository("household").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("household", " HoH_F_Name is not null "));
        hhcountcursor.moveToFirst();
        hhcount= hhcountcursor.getInt(0);
        hhcountcursor.close();
        HouseholdRegisterClientCountView.setText(valueOf(hhcount));

        Cursor elcocountcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Eligible\":\"1\"%' and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%') "));
        elcocountcursor.moveToFirst();
        womancount= elcocountcursor.getInt(0);
        elcocountcursor.close();
        fpRegisterClientCountView.setText(valueOf(womancount));



//        ecRegisterClientCountView.setText(valueOf(hhcount));
//        fpRegisterClientCountView.setText(valueOf(womancount));
        Cursor anccountcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Preg_Status\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')  "));
        anccountcursor.moveToFirst();
        anccount = anccountcursor.getInt(0);
        anccountcursor.close();
        AncRegisterClientCountView.setText(valueOf(anccount));

        Cursor pnccountcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Is_PNC\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')  "));
        pnccountcursor.moveToFirst();
        pnccount = pnccountcursor.getInt(0);
        pnccountcursor.close();
        PncRegisterClientCountView.setText(valueOf(pnccount));

        Cursor childcountcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Child\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')  "));
        childcountcursor.moveToFirst();
        childcount = childcountcursor.getInt(0);
        childcountcursor.close();
        ChildRegisterClientCountView.setText(valueOf(childcount));

        Cursor nutritioncursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null )  AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Nutrition\":\"1\"%' and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')  "));
        nutritioncursor.moveToFirst();
        nutritioncount = nutritioncursor.getInt(0);
        nutritioncursor.close();
        nutritionClientCountView.setText(valueOf(nutritioncount));

        Cursor injectablecursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Is_Eligible_Injectables\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')   "));
        injectablecursor.moveToFirst();
        injectablecount = injectablecursor.getInt(0);
        injectablecursor.close();
        injectableClientCountView.setText(valueOf(injectablecount));

        Cursor deathcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND ((details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%'))"));
        deathcursor.moveToFirst();
        deathcount = deathcursor.getInt(0);
        deathcursor.close();
        deathClientCountView.setText(valueOf(deathcount));




        Cursor adolescentcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members","(members.Mem_F_Name not null ) AND  (members.Mem_F_Name != \"\" ) AND details like '%\"Adolescent\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')  "));
        adolescentcursor.moveToFirst();
        adolescentcount = adolescentcursor.getInt(0);
        adolescentcursor.close();
        adolescentClientCountView.setText(valueOf(adolescentcount));

//        Cursor childcountcursor = context.commonrepository("members").RawCustomQueryForAdapter(sqb.queryForCountOnRegisters("members"," details like '%\"Is_PNC\":\"1\"%' "));
//        childcountcursor.moveToFirst();
//        childcount = childcountcursor.getInt(0);
//        childcountcursor.close();
//        ChildRegisterClientCountView.setText(valueOf(childcount));
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
            case R.id.searchmenu_item:
                startActivity(new Intent(getApplicationContext(), ClientSearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void anouncementTask() {
//        Today_anouncement_task tvt = new Today_anouncement_task(this,context,new HTTPAgent(context.applicationContext(),context.allSettings(),context.allSharedPreferences(),context.configuration()),context.configuration(),context.allSettings(),context.allSharedPreferences());
//        tvt.execute();
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context.actionService(), context.formSubmissionSyncService(),
                new SyncProgressIndicator(), context.allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
//        Last_vaccine_missedCount_task tdv = new Last_vaccine_missedCount_task(context,new HTTPAgent(context.applicationContext(),context.allSettings(),context.allSharedPreferences(),context.configuration()),context.configuration(),context.allSettings(),context.allSharedPreferences());
//        tdv.execute();
//        Today_vaccine_task tvt = new Today_vaccine_task(context,new HTTPAgent(context.applicationContext(),context.allSettings(),context.allSharedPreferences(),context.configuration()),context.configuration(),context.allSettings(),context.allSharedPreferences());
//        tvt.execute();
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
                case R.id.household_register:
                    ((DGFPNavigationController)navigationController).startHHSmartRegistry();
                    break;
//
                case R.id.elco_register:
                    ((DGFPNavigationController)navigationController).startHHElcoSmartRegistry();
                    break;
//
                case R.id.btn_anc_register:
                    navigationController.startANCSmartRegistry();
                    break;
                case R.id.btn_pnc_register:
                    navigationController.startPNCSmartRegistry();
                    break;
                case R.id.btn_child_register:
                    ((DGFPNavigationController)navigationController).startChildSmartRegistry();
                    break;
                case R.id.btn_adolescent_register:
                    ((DGFPNavigationController)navigationController).startadolescentSmartRegistry();
                    break;

                case R.id.btn_injectable_register:
                    ((DGFPNavigationController)navigationController).startinjectableSmartRegistry();
                    break;
//
                case R.id.btn_nutrition_register:
                    ((DGFPNavigationController)navigationController).startnutritionSmartRegistry();
                    break;

                case R.id.btn_death_register:
                    ((DGFPNavigationController)navigationController).startdeathSmartRegistry();
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
//                    startActivity(new Intent(getApplicationContext(), ClientSearchActivity.class));
                    break;
            }
        }
    };
}

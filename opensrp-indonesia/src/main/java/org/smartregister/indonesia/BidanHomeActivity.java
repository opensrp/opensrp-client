package org.smartregister.indonesia;

import android.database.Cursor;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.smartregister.Context;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.event.Listener;
import org.smartregister.indonesia.face.camera.utils.Tools;
import org.smartregister.indonesia.lib.FlurryFacade;
import org.smartregister.service.PendingFormSubmissionService;
import org.smartregister.sync.SyncAfterFetchListener;
import org.smartregister.sync.SyncProgressIndicator;
import org.smartregister.sync.UpdateActionsTask;
import org.smartregister.view.activity.SecuredActivity;
import org.smartregister.view.contract.HomeContext;
import org.smartregister.view.controller.NativeAfterANMDetailsFetchListener;
import org.smartregister.view.controller.NativeUpdateANMDetailsTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.formula.Support;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.smartregister.event.Event.ACTION_HANDLED;
import static org.smartregister.event.Event.FORM_SUBMITTED;
import static org.smartregister.event.Event.SYNC_COMPLETED;
import static org.smartregister.event.Event.SYNC_STARTED;

public class BidanHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private PendingFormSubmissionService pendingFormSubmissionService;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            Support.ONSYNC = true;
            AllConstantsINA.IDLE = false;
            AllConstantsINA.SLEEP_TIME = 15000;
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

            new Tools(context());

//            Tools.setAppContext(context());
            Tools.setVectorfromAPI(getApplicationContext());
//            Tools.download_images();
//            Tools.setVectorsBuffered();
//            AllConstantsINA.SLEEP_TIME = AllConstantsINA.WAITING_TIME;
            flagActivator();

        }
    };

    private void flagActivator(){
        new Thread(){
            public void run(){
                try{
                    while(AllConstantsINA.SLEEP_TIME>0){
                        sleep(1000);
                        if(AllConstantsINA.IDLE)
                            AllConstantsINA.SLEEP_TIME-=1000;
                    }
                    Support.ONSYNC=false;
                }catch (InterruptedException ie){

                }
            }
        }.start();
    }

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

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    private TextView ecRegisterClientCountView;
    private TextView kartuIbuANCRegisterClientCountView;
    private TextView kartuIbuPNCRegisterClientCountView;
    private TextView anakRegisterClientCountView;
    private TextView kohortKbCountView;
    public static int kicount;

    @Override
    protected void onCreation() {
        //home dashboard
        /*FlurryFacade.logEvent("home_dashboard");*/
        String HomeStart = timer.format(new Date());
        Map<String, String> Home = new HashMap<String, String>();
        Home.put("start", HomeStart);
        FlurryAgent.logEvent("home_dashboard", Home, true);

        setContentView(R.layout.smart_registers_home_bidan);
        navigationController = new NavigationControllerINA(this, anmController, context());
        setupViews();
        initialize();
        DisplayFormFragment.formInputErrorMessage = getResources().getString(R.string.forminputerror);
        DisplayFormFragment.okMessage = getResources().getString(R.string.okforminputerror);
      //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new ANChandler());
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
    }

    private void setupViews() {
        findViewById(R.id.btn_kartu_ibu_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_anc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kartu_ibu_pnc_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_anak_register).setOnClickListener(onRegisterStartListener);
        findViewById(R.id.btn_kohort_kb_register).setOnClickListener(onRegisterStartListener);


        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
//        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

        ecRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_register_client_count);
        kartuIbuANCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_anc_register_client_count);
        kartuIbuPNCRegisterClientCountView = (TextView) findViewById(R.id.txt_kartu_ibu_pnc_register_client_count);
        anakRegisterClientCountView = (TextView) findViewById(R.id.txt_anak_client_count);
        kohortKbCountView = (TextView) findViewById(R.id.txt_kohort_kb_register_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.smartregister.indonesia.R.mipmap.logo));
        getSupportActionBar().setLogo(org.smartregister.indonesia.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();
//        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.action_bar_background));
    }

    @Override
    protected void onResumption() {
        LoginActivity.setLanguage();
        updateRegisterCounts();
        updateSyncIndicator();
        updateRemainingFormsToSyncCount();
        initFR();
    }

    private void initFR() {
        new Tools(context());
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
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder();
        Cursor kicountcursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_kartu_ibu_search", "ec_kartu_ibu_search.is_closed=0"));
        kicountcursor.moveToFirst();
        kicount= kicountcursor.getInt(0);
        kicountcursor.close();

        Cursor kbcountcursor = context().commonrepository("ec_kartu_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_kartu_ibu_search", "ec_kartu_ibu_search.is_closed=0 and jenisKontrasepsi !='0'" ));
        kbcountcursor.moveToFirst();
        int kbcount = kbcountcursor.getInt(0);
        kbcountcursor.close();

        Cursor anccountcursor = context().commonrepository("ec_ibu").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_ibu_search", "ec_ibu_search.is_closed=0 "));
        anccountcursor.moveToFirst();
        int anccount = anccountcursor.getInt(0);
        anccountcursor.close();

        Cursor pnccountcursor = context().commonrepository("ec_pnc").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_pnc_search", "ec_pnc_search.is_closed=0 AND (ec_pnc_search.keadaanIbu ='hidup' OR ec_pnc_search.keadaanIbu IS NULL) ")); // and ec_pnc_search.keadaanIbu LIKE '%hidup%'
        pnccountcursor.moveToFirst();
        int pnccount = pnccountcursor.getInt(0);
        pnccountcursor.close();

        Cursor childcountcursor = context().commonrepository("anak").rawCustomQueryForAdapter(sqb.queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount = childcountcursor.getInt(0);
        childcountcursor.close();

        ecRegisterClientCountView.setText(valueOf(kicount));
        kartuIbuANCRegisterClientCountView.setText(valueOf(anccount));
        kartuIbuPNCRegisterClientCountView.setText(valueOf(pnccount));
        anakRegisterClientCountView.setText(valueOf(childcount));
        kohortKbCountView.setText(valueOf(kbcount));
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
                String anmID;
                try {
                    anmID = new JSONObject(context().anmController().get()).get("anmName").toString();
                }catch (org.json.JSONException e){
                    anmID = "undefined";
                }
                Toast.makeText(this, String.format("%s current user = %s",context().getStringResource(R.string.app_name),anmID), LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        FlurryFacade.logEvent("clicked_update_from_server");
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context().actionService(), context().formSubmissionSyncService(),
                new SyncProgressIndicator(), context().allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
        String locationjson = context().anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

//        Map<String,TreeNode<String, Location>> locationMap =
//                locationTree.getLocationsHierarchy();

//        if(LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT))  // unique id part
//            LoginActivity.generator.requestUniqueId();                                                                  // unique id part
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
                case R.id.btn_kartu_ibu_register:
                    navigationController.startECSmartRegistry();
                    break;

                case R.id.btn_kohort_kb_register:
                        navigationController.startFPSmartRegistry();
                        break;

               case R.id.btn_kartu_ibu_anc_register:
                    navigationController.startANCSmartRegistry();
                   break;

               case R.id.btn_anak_register:
                    navigationController.startChildSmartRegistry();
                   break;

               case R.id.btn_kartu_ibu_pnc_register:
                   navigationController.startPNCSmartRegistry();
                   break;
            }
            String HomeEnd = timer.format(new Date());
            Map<String, String> Home = new HashMap<String, String>();
            Home.put("end", HomeEnd);
            FlurryAgent.logEvent("home_dashboard",Home, true);
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    navigationController.startReports();
                    break;

//                case R.id.btn_videos:
//                    navigationController.startVideos();
//                    break;
            }
        }
    };


    public void helpMenu(){
        Toast.makeText(getApplicationContext(), String.valueOf(1), Toast.LENGTH_LONG).show();

    }

}
package org.ei.opensrp.vaccinator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class NativeHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private String locationDialogTAG = "locationDialogTAG";
    private PendingFormSubmissionService pendingFormSubmissionService;
    Activity activity=this;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
            //    updateMenuItem.setActionView(R.layout.progress);
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
    private TextView womanRegisterClientCountView;
    private TextView pncRegisterClientCountView;
    private TextView fpRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private TextView fieldRegisterClientCountView;

    @Override
    protected void onCreation() {
        setContentView(R.layout.smart_registers_home);
        navigationController = new VaccinatorNavigationController(this,anmController);
        setupViews();
        initialize();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
      /*  LocationSelectorDialogFragment
                .newInstance(this, new EditDialogOptionModel(), context.anmLocationController().get(), "new_household_registration")
                .show(ft, locationDialogTAG);*/
    }





    private void setupViews() {
     //  findViewById(R.id.btn_ec_register).setOnClickListener(onRegisterStartListener);
     //   findViewById(R.id.btn_pnc_register).setOnClickListener(onRegisterStartListener);
  //      findViewById(R.id.btn_anc_register).setOnClickListener(onRegisterStartListener);
     //   findViewById(R.id.btn_fp_register).setOnClickListener(onRegisterStartListener);
      // findViewById(R.id.btn_child_register_new).setOnClickListener(onRegisterStartListener);
        ImageButton imgButtonChild=(ImageButton)findViewById(R.id.btn_child_register_new);
        ImageButton imgButtonWoman=(ImageButton)findViewById(R.id.btn_woman_register);
        ImageButton imgButtonField=(ImageButton)findViewById(R.id.btn_field_register);
        if(onRegisterStartListener!=null) {
            imgButtonField.setOnClickListener(onRegisterStartListener);
            imgButtonWoman.setOnClickListener(onRegisterStartListener);
            imgButtonChild.setOnClickListener(onRegisterStartListener);
        }
            findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_videos).setOnClickListener(onButtonsClickListener);

     //   ecRegisterClientCountView = (TextView) findViewById(R.id.txt_ec_register_client_count);
     //   pncRegisterClientCountView = (TextView) findViewById(R.id.txt_pnc_register_client_count);
        womanRegisterClientCountView = (TextView) findViewById(R.id.txt_woman_register_client_count);
     //   fpRegisterClientCountView = (TextView) findViewById(R.id.txt_fp_register_client_count);txt_field_register_client_count
        childRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        fieldRegisterClientCountView = (TextView) findViewById(R.id.txt_field_register_client_count);
    }

    private void initialize() {
        pendingFormSubmissionService = context().pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.ei.opensrp.vaccinator.R.mipmap.logo));
        getSupportActionBar().setLogo(org.ei.opensrp.vaccinator.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();
//        getActionBar().setBackgroundDrawable(getReso
// urces().getDrawable(R.color.action_bar_background));
    }

    @Override
    protected void onResumption() {
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
        CommonPersonObjectController childController = new CommonPersonObjectController(
                context().allCommonsRepositoryobjects("ec_child"),
                context().allBeneficiaries(), context().listCache(),
                context().personObjectClientsCache(), "first_name", "ec_child", "child_reg_date",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails );
        CommonPersonObjectController womanController = new CommonPersonObjectController(
                context().allCommonsRepositoryobjects("ec_woman"),
                context().allBeneficiaries(), context().listCache(),
                context().personObjectClientsCache(), "first_name", "ec_woman", "client_reg_date",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );

        CommonPersonObjectController fieldController = new CommonPersonObjectController(
                context().allCommonsRepositoryobjects("field"),
                context().allBeneficiaries(), context().listCache(),
                context().personObjectClientsCache(), "date", "field", "report",
                CommonPersonObjectController.ByColumnAndByDetails.byColumn );

       // ecRegisterClientCountView.setText(valueOf(hhcontroller.getClients().size()));
        womanRegisterClientCountView.setText(valueOf(womanController.getClients().size()));
     //   pncRegisterClientCountView.setText(valueOf(homeContext.pncCount()));
     //   fpRegisterClientCountView.setText(valueOf(elcocontroller.getClients().size()));
//        Log.d("child count cin ", childController.getClients().size()+"");
        childRegisterClientCountView.setText(valueOf(childController.getClients().size()));
        fieldRegisterClientCountView.setText(valueOf(fieldController.getClients().size()));
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
                /*case R.id.btn_ec_register:
                    navigationController.startECSmartRegistry();
                    break;*/

                /*case R.id.btn_anc_register:
                    navigationController.startANCSmartRegistry();
                    break;*/

                case R.id.btn_field_register:
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.pick_report);
                    builder.setItems(new String[]{"Monthly", "Daily"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //if()
                           //Toast.makeText(activity, "selected item :" + which, Toast.LENGTH_LONG).show();
                            if (which == 0) {
                                FieldMonitorSmartRegisterActivity.sortbymonth = true;
                            } else {
                                FieldMonitorSmartRegisterActivity.sortbymonth = false;
                            } // The 'which' argument contains the index position
                            // of the selected item
                            dialog.dismiss();
                            navigationController.startFPSmartRegistry();
                        }
                    }).show();



                    break;

                case R.id.btn_child_register_new:
                    navigationController.startChildSmartRegistry();
                    break;

                case R.id.btn_woman_register:
                    navigationController.startANCSmartRegistry();
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
                    navigationController.startVideos();
                    break;
            }
        }
    };
}

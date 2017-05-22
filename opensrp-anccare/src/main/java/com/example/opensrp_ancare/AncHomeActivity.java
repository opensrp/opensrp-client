package com.example.opensrp_ancare;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.opensrp_anc.anc.ANCSmartRegisterActivity;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.template.HomeActivity;

public class AncHomeActivity extends HomeActivity {

    Activity activity=this;
    private final String TAG = getClass().getName();
    @Override
    public int smartRegistersHomeLayout() {
        return R.layout.smart_registers_home;
    }

    @Override
    protected void onCreation() {
        super.onCreation();

        navigationController = null; // to make sure that it would be nullified and wont create default navigation
        Log.i(TAG, "Created Home Activity views:");
    }

    @Override
    public void setupViewsAndListeners() {
        setupRegister("", R.id.ancContainer, R.id.btn_anc_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_anc_register_client_count, "pkanc", "", "")});

        setupRegister("View Household Register", R.id.householdContainer, R.id.btn_household_register, onRegisterStartListener,
                new RegisterCountView[]{
                        new RegisterCountView(R.id.txt_household_register_client_count, "pkhousehold", "", "H"),
                        new RegisterCountView(R.id.txt_household_register_client_plus_members_count, "pkindividual", "", "M",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return (int) (Context.getInstance().commonrepository("pkhousehold").count()+
                                        Context.getInstance().commonrepository("pkindividual").count());
                            }
                        }),
                });

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);
    }

    @Override
    protected Integer getHeaderLogo() {
        return null;
    }

    @Override
    protected Integer getHeaderIcon() {
        return R.drawable.opensrp_icon;
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_household_register) {
                activity.startActivity(new Intent(activity, com.example.opensrp_household.household.HouseholdSmartRegisterActivity.class));

            } else if (i == R.id.btn_anc_register) {
                activity.startActivity(new Intent(activity,com.example.opensrp_anc.anc.ANCSmartRegisterActivity.class));

            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.btn_reporting) {
                Boolean isSyncInProgress = Context.getInstance().allSharedPreferences().fetchIsSyncInProgress();

                if (isSyncInProgress != null && isSyncInProgress) {
                    Toast.makeText(activity, "Forms Sync is in progress at the moment... Try visiting reports later when sync has been completed", Toast.LENGTH_LONG).show();
                    return;
                }
                // activity.startActivity(new Intent(activity, VaccineReport.class));

            } else if (i == R.id.btn_provider_profile) {
                 activity.startActivity(new Intent(activity, ProviderProfileActivity.class));

            }
        }
    };
}

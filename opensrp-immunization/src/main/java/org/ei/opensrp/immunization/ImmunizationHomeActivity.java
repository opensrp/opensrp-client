package org.ei.opensrp.immunization;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.immunization.child.ChildSmartRegisterActivity;
import org.ei.opensrp.immunization.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.immunization.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.immunization.report.VaccineReport;
import org.ei.opensrp.immunization.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.core.template.HomeActivity;

public class ImmunizationHomeActivity extends HomeActivity {
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

    public void setupViewsAndListeners() {
        Log.v(getClass().getName(), "Setting up views and listeners");
        setupRegister("View Woman Register", R.id.womanImmunizationContainer, R.id.btn_woman_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_woman_register_client_count, "pkwoman", "", "")});

        setupRegister("View Child Register", R.id.childImmunizationContainer, R.id.btn_child_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_child_register_client_count, "pkchild", "", "")});

        setupRegister("View Stock Register", R.id.stockContainer, R.id.btn_field_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_field_register_client_countm, "stock", "report='monthly'", "")});

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
            switch (view.getId()) {
                case R.id.btn_household_register:
                    activity.startActivity(new Intent(activity, HouseholdSmartRegisterActivity.class));
                    break;
                case R.id.btn_field_register:
                    activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));
                    break;

                case R.id.btn_child_register:
                    activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));
                    break;

                case R.id.btn_woman_register:
                    activity.startActivity(new Intent(activity, WomanSmartRegisterActivity.class));
                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    activity.startActivity(new Intent(activity, VaccineReport.class));
                    break;

                case R.id.btn_provider_profile:
                    activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
                    break;
            }
        }
    };
}

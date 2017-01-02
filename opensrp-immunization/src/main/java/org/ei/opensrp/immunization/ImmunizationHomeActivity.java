package org.ei.opensrp.immunization;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import org.ei.opensrp.immunization.child.ChildSmartRegisterActivity;
import org.ei.opensrp.immunization.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.immunization.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.immunization.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.view.activity.NativeHomeActivity;
import org.ei.opensrp.view.contract.HomeContext;

public class ImmunizationHomeActivity extends NativeHomeActivity {
    Activity activity=this;

    private Register womanRegister;
    private Register childRegister;
    private Register stockRegister;
    private Register householdRegister;

    private final String TAG = getClass().getName();

    @Override
    public int smartRegistersHomeLayout() {
        return R.layout.smart_registers_home;
    }

    @Override
    protected void onCreation() {
        super.onCreation();

        navigationController = new ImmunizationNavigationController(this);//todo refactor and maybe remove this method
        Log.i(TAG, "Created Home Activity views:");

        /*Countly.sharedInstance().init(this, "https://cloud.count.ly", "dc5dfb412bdbd91792b29f66e5a4bd2ee226cfb6");
        new FlurryAgent.Builder().withLogEnabled(false).build(this, "PKD27SF3CWTVPRFPVDVV");*/
    }

    public void setupViewsAndListeners() {
        Log.v(getClass().getName(), Utils.providerDetails().toString());
        womanRegister = initRegister("View Woman Register", R.id.womanImmunizationContainer, R.id.btn_woman_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_woman_register_client_count, "pkwoman", "", "", CountMethod.AUTO)});
        childRegister = initRegister("View Child Register", R.id.childImmunizationContainer, R.id.btn_child_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_child_register_client_count, "pkchild", "", "", CountMethod.AUTO)});
        stockRegister = initRegister("View Stock Register", R.id.stockContainer, R.id.btn_field_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_field_register_client_countm, "stock", "report='monthly'", "", CountMethod.AUTO)});

        householdRegister = initRegister("View Household Register", R.id.householdContainer, R.id.btn_household_register, onRegisterStartListener,
                new RegisterCountView[]{
                   new RegisterCountView(R.id.txt_household_register_client_count, "pkhousehold", "", "H", CountMethod.AUTO),
                   new RegisterCountView(R.id.txt_household_register_client_plus_members_count, "pkindividual", "", "M", CountMethod.AUTO),
                });

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);
    }

    protected void updateRegisterCounts(HomeContext homeContext) {
        if (womanRegister.isAllowed()){
            womanRegister.resetRegisterCounts();
        }
        if (childRegister.isAllowed()){
            childRegister.resetRegisterCounts();
        }
        if (stockRegister.isAllowed()){
            stockRegister.resetRegisterCounts();
        }
        if (householdRegister.isAllowed()){
            householdRegister.resetRegisterCounts();

            int hhCount = householdRegister.getCountView(R.id.txt_household_register_client_count).getCurrentCount();
            int hhMemberCount = householdRegister.getCountView(R.id.txt_household_register_client_plus_members_count).getCurrentCount();

            householdRegister.overrideRegisterCount(R.id.txt_household_register_client_plus_members_count, hhCount+hhMemberCount, "M");
        }
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
                    navigationController.startReports();
                    break;

                case R.id.btn_provider_profile:
                    activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
                    break;
            }
        }
    };

    /*@Override
    protected void onStart(){
        super.onStart();
        HashMap<String,String> segments = new HashMap<String, String>();
        segments.put("user", providerDetails().get("provider_name"));
        CountlyAnalytics.startAnalytics(this, CalendarContract.Events.LOGIN, segments);
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.onStartSession(this);
    }

    protected void onStop(){
        super.onStop();
        CountlyAnalytics.stopAnalytics();
        FlurryAgent.onEndSession(this);
    }*/
}

package com.example.opensrp_thrivepk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.example.opensrp_anc.anc.ANCSmartRegisterActivity;
import com.example.opensrp_child.child.ChildSmartRegisterActivity;
import com.example.opensrp_household.household.HouseholdSmartRegisterActivity;
import com.example.opensrp_stock.field.FieldMonitorSmartRegisterActivity;
import com.example.opensrp_thrivepk.application.CESyncReceiver;
import com.example.opensrp_thrivepk.report.VaccineReport;
import com.example.opensrp_woman.woman.WomanSmartRegisterActivity;
import com.example.opensrp_zm.zm.ZMSmartRegisterActivity;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.template.HomeActivity;

public class ThrivepkHomeActivity extends HomeActivity {

    Activity activity=this;

    private final String TAG = getClass().getName();

    @Override
    public int smartRegistersHomeLayout() {
        return R.layout.smart_registers_home;
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        getSupportActionBar().setTitle("OpenSRP ThrivePK");
        navigationController = null; // to make sure that it would be nullified and wont create default navigation
        Log.i(TAG, "Created Home Activity views:");
    }

    @Override
    public void setupViewsAndListeners() {

        Log.v(getClass().getName(), "Setting up register views and listeners");

        setupRegister("View Woman Register", R.id.womanImmunizationContainer, R.id.btn_woman_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_woman_register_client_count, "pkwoman", "", "")});

        setupRegister("View Child Register", R.id.childImmunizationContainer, R.id.btn_child_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_child_register_client_count, "pkchild", "", "")});

        setupRegister("", R.id.ancContainer, R.id.btn_anc_register, onRegisterStartListener,
                new RegisterCountView[]{new RegisterCountView(R.id.txt_anc_register_client_count, "pkanc", "", "")});

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

        // // TODO: 1/25/2017  something for CE
        setupRegister("View ZM Register", R.id.zmContainer, R.id.btn_zm_register, onRegisterStartListener,
                new RegisterCountView[]{
                        new RegisterCountView(R.id.txt_zm_register_client_count, "", "", "",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                ((TextView)findViewById(R.id.zm_location_filtered)).setText(CESyncReceiver.SYNC_LOCATION(activity));
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client").get(0).get("c"));
                            }
                        }),
                        new RegisterCountView(R.id.zm_register_male_count, "", "", "M",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client WHERE gender LIKE 'M%'").get(0).get("c"));
                            }
                        }),
                        new RegisterCountView(R.id.zm_register_female_count, "", "", "F",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client WHERE gender LIKE 'F%'").get(0).get("c"));
                            }
                        }),
                        new RegisterCountView(R.id.zm_register_intersex_count, "", "", "T",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client WHERE gender LIKE 'T%'").get(0).get("c"));
                            }
                        }),
                        new RegisterCountView(R.id.txt_zm_register_woman_count, "", "", "W",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client WHERE gender LIKE 'F%' AND julianday(DATETIME('now'))-julianday(birthdate) BETWEEN 15*365 AND 49*365 ").get(0).get("c"));
                            }
                        }),
                        new RegisterCountView(R.id.txt_zm_register_child_count, "", "", "C",
                                CountMethod.MANUAL, new CustomCounterHandler() {
                            @Override
                            public int executeCounter() {
                                return Integer.parseInt(CESQLiteHelper.getClientEventDb(activity).rawQuery("SELECT COUNT(1) c FROM client WHERE julianday(DATETIME('now'))-julianday(birthdate) BETWEEN 0 AND 5*365 ").get(0).get("c"));
                            }
                        })
                });

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);
       // findViewById(R.id.btn_learning).setOnClickListener(onButtonsClickListener);
      //  findViewById(R.id.btn_why).setOnClickListener(onButtonsClickListener);
      //  findViewById(R.id.btn_how).setOnClickListener(onButtonsClickListener);
    }

    @Override
    protected Integer getHeaderLogo() {
        return null;
    }

    @Override
    protected Integer getHeaderIcon() {
        return R.drawable.opensrp_icon;
    }
    private RippleView.OnRippleCompleteListener onRegisterStartListener =new RippleView.OnRippleCompleteListener() {
        @Override
        public void onComplete(RippleView rippleView) {
            switch (rippleView.getId()) {
                case R.id.btn_anc_register:
                    activity.startActivity(new Intent(activity, ANCSmartRegisterActivity.class));
                    break;

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
                    activity.startActivity(new Intent(activity,WomanSmartRegisterActivity.class));
                    break;
                case R.id.btn_zm_register:
                    activity.startActivity(new Intent(activity, ZMSmartRegisterActivity.class));
                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    Boolean isSyncInProgress = Context.getInstance().allSharedPreferences().fetchIsSyncInProgress();

                    if (isSyncInProgress != null && isSyncInProgress){
                        Toast.makeText(activity, "Forms Sync is in progress at the moment... Try visiting reports later when sync has been completed", Toast.LENGTH_LONG).show();
                        return;
                    }
                    activity.startActivity(new Intent(activity, VaccineReport.class));
                    break;

                case R.id.btn_provider_profile:
                    activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
                    break;

                /*case R.id.btn_learning:
                    Uri uri = Uri.parse("http://demo.oppia-mobile.org/view?digest=005a54d3b873e19020b7b0458a484fc714496"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case R.id.btn_why:
                    uri = Uri.parse("http://demo.oppia-mobile.org/view?digest=e69aaabbb5460b4a145fb00c11fdf8a412952"); // missing 'http://' will cause crashed
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                case R.id.btn_how:
                    uri = Uri.parse("http://demo.oppia-mobile.org/view?digest=bd7ae89a5393e9d9eb196d516945a31712971"); // missing 'http://' will cause crashed
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;*/
            }
        }
    };
}

package org.ei.opensrp.path.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Vaccine_types;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.AllSharedPreferences;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by raihan on 5/23/17.
 */
public class HouseholdDetailActivity extends BaseActivity {
    ListView listView;
    private LocationSwitcherToolbar toolbar;
    public org.ei.opensrp.Context context;
    public Vaccine_typesRepository VTR;


    private CommonPersonObjectClient householdDetails;
    private static final String EXTRA_HOUSEHOLD_DETAILS = "household_details";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());


        toolbar = (LocationSwitcherToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseholdDetailActivity.this, ChildSmartRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_HOUSEHOLD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                householdDetails = (CommonPersonObjectClient) serializable;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        int toolbarResource = R.drawable.vertical_separator_male;
        toolbar.updateSeparatorView(toolbarResource);
        toolbar.init(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(householdDetails.getName());



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView nameInitials = (TextView)findViewById(R.id.name_inits);
        nameInitials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        AllSharedPreferences allSharedPreferences = org.ei.opensrp.Context.getInstance().allSharedPreferences();
        String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
        if (!preferredName.isEmpty()) {
            String[] preferredNameArray = preferredName.split(" ");
            String initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
            nameInitials.setText(initials);
        }

//        toolbar.setOnLocationChangeListener(this);
//



        listView = (ListView) findViewById(R.id.household_list);
        context = org.ei.opensrp.Context.getInstance().updateApplicationContext(this.getApplicationContext());
        VTR = new Vaccine_typesRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
        //get Household members repository
    }

    private void refreshadapter() {
        //setAdapter data of Household member
        ArrayList<Vaccine_types> allVaccineTypes = (ArrayList) VTR.getAllVaccineTypes();
        Vaccine_types [] allVaccineTypesarray = allVaccineTypes.toArray(new Vaccine_types[allVaccineTypes.size()]);
        HouseholdListAdpater adapter = new HouseholdListAdpater(this,allVaccineTypesarray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshadapter();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout stockregister = (LinearLayout) drawer.findViewById(R.id.stockcontrol);
        stockregister.setBackgroundColor(getResources().getColor(R.color.tintcolor));
    }

    @Override
    protected int getContentView() {
        return  R.layout.household_detail_activity;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_register) {
////            startFormActivity("child_enrollment", null, null);
//        } else if (id == R.id.nav_record_vaccination_out_catchment) {
////            startFormActivity("out_of_catchment_service", null, null);
//        } else if (id == R.id.stock) {
//            Intent intent = new Intent(this, StockActivity.class);
//            startActivity(intent);
//        } else if (id == R.id.nav_sync) {
////            startSync();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    @Override
    protected int getDrawerLayoutId() {
        return  R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return ChildSmartRegisterActivity.class;
    }

    class HouseholdListAdpater extends BaseAdapter {
        private Context context;
        private final Vaccine_types[] vaccine_types;

        public HouseholdListAdpater(Context context, Vaccine_types[] vaccine_types) {
            this.context = context;
            this.vaccine_types = vaccine_types;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View listView;

            if (convertView == null) {

                listView = new View(context);

                // get layout from mobile.xml
                listView = inflater.inflate(R.layout.household_details_list_row, null);

                // set value into textview
                TextView name = (TextView) listView
                        .findViewById(R.id.member_name);
                TextView age = (TextView) listView
                        .findViewById(R.id.member_age);
                TextView address = (TextView) listView
                        .findViewById(R.id.member_address);

                // set image based on selected text


                final Vaccine_types vaccine_type = vaccine_types[position];
                StockRepository stockRepository = new StockRepository((PathRepository)VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
                int currentvials = stockRepository.getBalanceFromNameAndDate(vaccine_type.getName(),System.currentTimeMillis());
                name.setText(vaccine_type.getName());

                age.setText(""+currentvials*vaccine_type.getDoses()+ " doses");

                address.setText(""+currentvials+ " vials");

                listView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HouseholdDetailActivity.this, StockControlActivity.class);
                        intent.putExtra("vaccine_type",vaccine_type);
                        startActivity(intent);
                    }
                });

            } else {
                listView = (View) convertView;
            }

            return listView;
        }

        @Override
        public int getCount() {
            return vaccine_types.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}

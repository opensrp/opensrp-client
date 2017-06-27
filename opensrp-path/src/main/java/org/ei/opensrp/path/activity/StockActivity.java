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
import android.widget.TextView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Vaccine_types;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.AllSharedPreferences;

import java.util.ArrayList;

/**
 * Created by raihan on 5/23/17.
 */
public class StockActivity extends BaseActivity {
    GridView stockGrid;
    private LocationSwitcherToolbar toolbar;
    public org.ei.opensrp.Context context;
    public Vaccine_typesRepository VTR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());


        toolbar = (LocationSwitcherToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockActivity.this, ChildSmartRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        int toolbarResource = R.drawable.vertical_separator_male;
        toolbar.updateSeparatorView(toolbarResource);
        toolbar.init(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stock Control");

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



        stockGrid = (GridView)findViewById(R.id.stockgrid);
        context = org.ei.opensrp.Context.getInstance().updateApplicationContext(this.getApplicationContext());
        VTR = new Vaccine_typesRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
    }

    private void refreshadapter() {
        ArrayList<Vaccine_types> allVaccineTypes = (ArrayList) VTR.getAllVaccineTypes();
        Vaccine_types [] allVaccineTypesarray = allVaccineTypes.toArray(new Vaccine_types[allVaccineTypes.size()]);
        stockGridAdapter adapter = new stockGridAdapter(this,allVaccineTypesarray);
        stockGrid.setAdapter(adapter);
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
        return  R.layout.activity_stock;
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

    class stockGridAdapter extends BaseAdapter {
        private Context context;
        private final Vaccine_types[] vaccine_types;

        public stockGridAdapter(Context context, Vaccine_types[] vaccine_types) {
            this.context = context;
            this.vaccine_types = vaccine_types;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {

                gridView = new View(context);

                // get layout from mobile.xml
                gridView = inflater.inflate(R.layout.stock_grid_block, null);

                // set value into textview
                TextView name = (TextView) gridView
                        .findViewById(R.id.vaccine_type_name);
                TextView doses = (TextView) gridView
                        .findViewById(R.id.doses);
                TextView vials = (TextView) gridView
                        .findViewById(R.id.vials);

                // set image based on selected text


                final Vaccine_types vaccine_type = vaccine_types[position];
                StockRepository stockRepository = new StockRepository((PathRepository)VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(), org.ei.opensrp.Context.getInstance().alertService());
                int currentvials = stockRepository.getBalanceFromNameAndDate(vaccine_type.getName(),System.currentTimeMillis());
                name.setText(vaccine_type.getName());

                doses.setText(""+currentvials*vaccine_type.getDoses()+ " doses");

                vials.setText(""+currentvials+ " vials");

                gridView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StockActivity.this, StockControlActivity.class);
                        intent.putExtra("vaccine_type",vaccine_type);
                        startActivity(intent);
                    }
                });

            } else {
                gridView = (View) convertView;
            }

            return gridView;
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

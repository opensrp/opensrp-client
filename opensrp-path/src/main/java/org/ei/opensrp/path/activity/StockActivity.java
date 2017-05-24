package org.ei.opensrp.path.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Vaccine_types;
import org.ei.opensrp.path.repository.PathRepository;
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
        int toolbarResource = R.drawable.vertical_separator_male;
        toolbar.updateSeparatorView(toolbarResource);
        toolbar.init(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stock Control");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView nameInitials = (TextView)findViewById(R.id.name_inits);

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
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance().updateApplicationContext(this.getApplicationContext());
        Vaccine_typesRepository VTR = new Vaccine_typesRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
        ArrayList<Vaccine_types> allVaccineTypes = (ArrayList) VTR.getAllVaccineTypes();
        Vaccine_types [] allVaccineTypesarray = allVaccineTypes.toArray(new Vaccine_types[allVaccineTypes.size()]);
        stockGridAdapter adapter = new stockGridAdapter(this,allVaccineTypesarray);
        stockGrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getContentView() {
        return  R.layout.activity_stock;
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

            LayoutInflater inflater = (LayoutInflater) context
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


                Vaccine_types vaccine_type = vaccine_types[position];

                name.setText(vaccine_type.getName());

                doses.setText("120 doses");

                vials.setText("6 vials");

                gridView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StockActivity.this, StockControlActivity.class);
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

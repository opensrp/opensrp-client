package org.ei.opensrp.path.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.view.customControls.CustomFontTextView;

import java.io.Serializable;

/**
 * Created by habib on 25/07/17.
 */
public class HouseholdDetailActivity extends BaseActivity {
    ListView householdList;
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
        Logger.largeErrorLog("-------------",householdDetails.getDetails().toString());
        Logger.largeErrorLog("-------------",householdDetails.getDetails().get("_id"));


        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        int toolbarResource = R.drawable.vertical_separator_male;
        toolbar.updateSeparatorView(toolbarResource);
        toolbar.init(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Household Details");


        ((TextView) findViewById(R.id.household_head_name)).setText("Name : " + "Abul");
        ((TextView) findViewById(R.id.household_head_age)).setText("Age : " + "29");


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

        initQueries();


        context = org.ei.opensrp.Context.getInstance().updateApplicationContext(this.getApplicationContext());
        VTR = new Vaccine_typesRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
        //get Household members repository
    }

    private void initQueries(){

    }

    private void refreshadapter() {
        //setAdapter data of Household member
        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
        String mother_id = householdDetails.getDetails().get("_id");
        Cursor cursor = db.rawQuery("SELECT  id as _id,first_name,client_reg_date FROM ec_mother where relational_id = ?",new String[]{mother_id});


        householdList = (ListView) findViewById(R.id.household_list);

        HouseholdCursorAdpater cursorAdpater = new HouseholdCursorAdpater(getApplicationContext(),cursor);

        householdList.setAdapter(cursorAdpater);
        householdList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("-----------","item clicked");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshadapter();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout stockregister = (LinearLayout) drawer.findViewById(R.id.stockcontrol);
        ((CustomFontTextView)findViewById(R.id.title)).setText("Household Details");
        stockregister.setBackgroundColor(getResources().getColor(R.color.tintcolor));
    }

    @Override
    protected int getContentView() {
        return  R.layout.household_detail_activity;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
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

    class HouseholdCursorAdpater extends CursorAdapter {
        private Context context;
        private LayoutInflater inflater = null;

        public HouseholdCursorAdpater(Context context, Cursor c) {
            super(context, c);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.e("------------","bind view call");
            TextView member_name = (TextView) view.findViewById(R.id.mother_name);
            TextView member_age = (TextView) view.findViewById(R.id.mother_age);
            member_name.setText("Name : " + cursor.getString(1));
            member_age.setText("Age : 27");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.e("------------","new view call");
            View view = inflater.inflate(R.layout.household_details_list_row,parent,false);
            LinearLayout household_details_list_row = (LinearLayout) view.findViewById(R.id.child_holder);
            addChild(household_details_list_row,cursor.getString(0));
            return  view;
        }

        public void addChild(LinearLayout household_details_list_row, String mother_id){
            Log.e("--------------",mother_id);


            PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
            net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT  id as _id,first_name,last_name,gender,mother_first_name,mother_last_name,dob,epi_card_number FROM ec_child where relational_id = ?",new String[]{mother_id});

            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                LinearLayout childrenLayout = (LinearLayout)inflater.inflate(R.layout.household_details_child_row, null);
                ((TextView)childrenLayout.findViewById(R.id.child_name)).setText("Name : " + cursor.getString(1));
                ((TextView)childrenLayout.findViewById(R.id.child_age)).setText("Age : " + cursor.getString(6).split("T")[0]);
                household_details_list_row.addView(childrenLayout);
                cursor.moveToNext();
            }
            cursor.close();

        }

    }
}

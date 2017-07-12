package org.ei.opensrp.path.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.fragment.table.ChildVaccineTable;
import org.ei.opensrp.path.fragment.table.FieldStockVaccineTable;
import org.ei.opensrp.path.fragment.table.WomanVaccineTable;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.view.activity.DrishtiApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class VaccineReportActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private CommonPersonObject childObject;
    private CommonPersonObject womanVaccineObjectForField;
    private CommonPersonObject fieldVaccineObjectForField;
    private CommonPersonObject childVaccineForFieldObject;
    private Fragment fieldReport;
    private Fragment childReport;
    private CommonPersonObject pregnantWomanObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }

        setContentView(R.layout.activity_vaccine_report);


        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        Formatter fmt = new Formatter();
        //Calendar cal = Calendar.getInstance();
        //fmt = new Formatter();

        String startDate = year + "-" + month + "-" + "01";
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = year + "-" + month + "-" + cal.get(Calendar.DAY_OF_MONTH);


        String childTablesql = "select (select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) bcg_0," +
                "(select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) bcg_1," +
                "(select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) bcg_2," +
                "(select count(*) c from ec_child where opv0 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) opv0_0," +
                "(select count(*) c from ec_child where opv0 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) opv0_1," +
                "(select count(*) c from ec_child where opv0 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) opv0_2," +
                "(select count(*) c from ec_child where opv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) opv1_0," +
                "(select count(*) c from ec_child where opv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) opv1_1," +
                "(select count(*) c from ec_child where opv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) opv1_2," +
                "(select count(*) c from ec_child where opv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) opv2_0," +
                "(select count(*) c from ec_child where opv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) opv2_1," +
                "(select count(*) c from ec_child where opv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) opv2_2," +
                "(select count(*) c from ec_child where opv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) opv3_0," +
                "(select count(*) c from ec_child where opv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) opv3_1," +
                "(select count(*) c from ec_child where opv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) opv3_2," +
                "(select count(*) c from ec_child where pcv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) pcv1_0," +
                "(select count(*) c from ec_child where pcv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) pcv1_1," +
                "(select count(*) c from ec_child where pcv1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) pcv1_2," +
                "(select count(*) c from ec_child where pcv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) pcv2_0," +
                "(select count(*) c from ec_child where pcv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) pcv2_1," +
                "(select count(*) c from ec_child where pcv2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) pcv2_2," +
                "(select count(*) c from ec_child where pcv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) pcv3_0," +
                "(select count(*) c from ec_child where pcv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) pcv3_1," +
                "(select count(*) c from ec_child where pcv3 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) pcv3_2," +
                "(select count(*) c from ec_child where penta1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1 )pentavalent1_0," +
                "(select count(*) c from ec_child where penta1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) pentavalent1_1," +
                "(select count(*) c from ec_child where penta1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) pentavalent1_2," +
                "(select count(*) c from ec_child where penta2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1 )pentavalent2_0," +
                "(select count(*) c from ec_child where penta2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) pentavalent2_1," +
                "(select count(*) c from ec_child where penta2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) pentavalent2_2," +
                "(select count(*) c from ec_child where measles1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) measles1_0," +
                "(select count(*) c from ec_child where measles1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) measles1_1," +
                "(select count(*) c from ec_child where measles1 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) measles1_2," +
                "(select count(*) c from ec_child where measles2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)<1  ) measles2_0," +
                "(select count(*) c from ec_child where measles2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)=1  ) measles2_1," +
                "(select count(*) c from ec_child where measles2 between '" + startDate + "' and '" + endDate + "' and (date('now')-dob)>2  ) measles2_2  from ec_child limit 1;";
        List<CommonPersonObject> childList = context().allCommonsRepositoryobjects("ec_child").customQuery(childTablesql, new String[]{}, "ec_child");
        if (childList.size() < 1) {
            childObject = null;
        } else {

            childObject = childList.get(0);
        }
//select * from field where field.date like '2015-12%' and report=='monthly'
        String womanVaccineSQlForField = "select " +
                "(select count(*) c from ec_mother where tt1 between '" + startDate + "' and '" + endDate + "' ) tt1," +
                "(select count(*) c from ec_mother where tt2 between '" + startDate + "' and '" + endDate + "' ) tt2," +
                "(select count(*) c from ec_mother where tt3 between '" + startDate + "' and '" + endDate + "' ) tt3," +
                "(select count(*) c from ec_mother where tt4 between '" + startDate + "' and '" + endDate + "' ) tt4," +
                "(select count(*) c from ec_mother where tt5 between '" + startDate + "' and '" + endDate + "' ) tt5 " +
                "from ec_mother limit 1;";
        List<CommonPersonObject> womanVaccineListForField = context().allCommonsRepositoryobjects("ec_mother").customQuery(womanVaccineSQlForField, new String[]{}, "ec_mother");
        if (womanVaccineListForField.size() < 1) {
            womanVaccineObjectForField = null;
        } else {

            womanVaccineObjectForField = womanVaccineListForField.get(0);

        }
        String reportMonth = year + "-" + month + "-";
        String fieldVaccineSQL = "select * from stock where date like '" + reportMonth + "%' and report='monthly'";

        List<CommonPersonObject> fieldVaccineListForField = context().allCommonsRepositoryobjects("stock").customQueryForCompleteRow(fieldVaccineSQL, new String[]{}, "stock");
        if (fieldVaccineListForField.size() < 1) {
            fieldVaccineObjectForField = null;
        } else {

            fieldVaccineObjectForField = fieldVaccineListForField.get(0);

        }
        //fieldVaccineObjectForField= context().allCommonsRepositoryobjects("field").customQueryForCompleteRow(fieldVaccineSQL, new String[]{}, "field").get(0);

        String childVaccineForFieldSQL = "select (" +
                "select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "') bcg," +
                "(select count(*) c from ec_child where opv0 between '" + startDate + "' and '" + endDate + "') opv0," +
                "(select count(*) c from ec_child where opv1 between '" + startDate + "' and '" + endDate + "') opv1," +
                "(select count(*) c from ec_child where opv2 between '" + startDate + "' and '" + endDate + "') opv2," +
                "(select count(*) c from ec_child where opv3 between '" + startDate + "' and '" + endDate + "') opv3, " +
                "(select count(*) c from ec_child where pcv1 between '" + startDate + "' and '" + endDate + "') pcv1," +
                "(select count(*) c from ec_child where pcv2 between '" + startDate + "' and '" + endDate + "') pcv2," +
                "(select count(*) c from ec_child where pcv3 between '" + startDate + "' and '" + endDate + "') pcv3, " +
                "(select count(*) c from ec_child where measles1 between '" + startDate + "' and '" + endDate + "') measles1, " +
                "(select count(*) c from ec_child where measles2 between '" + startDate + "' and '" + endDate + "') measles2," +
                "(select count(*) c from ec_child where penta1 between '" + startDate + "' and '" + endDate + "') penta1," +
                "(select count(*) c from ec_child where penta2 between '" + startDate + "' and '" + endDate + "') penta2," +
                "(select count(*) c from ec_child where penta3 between '" + startDate + "' and '" + endDate + "') penta3 " +
                "from ec_child limit 1 ;";


        List<CommonPersonObject> childVaccineListForField = context().allCommonsRepositoryobjects("ec_child").customQuery(childVaccineForFieldSQL, new String[]{}, "ec_child");
        if (childVaccineListForField.size() < 1) {
            childVaccineForFieldObject = null;

        } else {

            childVaccineForFieldObject = childVaccineListForField.get(0);

        }
        // childVaccineForFieldObject=context().allCommonsRepositoryobjects("ec_child").customQuery(childVaccineForFieldSQL, new String[]{}, "ec_child").get(0);

        String pregnantWomanSQL = "select " +
                "(select count(*) c from ec_mother where tt1 between '" + startDate + "' and '" + endDate + "' and pregnant='yes') tt1," +
                "(select count(*) c from ec_mother where tt2 between '" + startDate + "' and '" + endDate + "' and pregnant='yes') tt2," +
                "(select count(*) c from ec_mother where tt3 between '" + startDate + "' and '" + endDate + "' and pregnant='yes') tt3," +
                "(select count(*) c from ec_mother where tt4 between '" + startDate + "' and '" + endDate + "' and pregnant='yes') tt4," +
                "(select count(*) c from ec_mother where tt5 between '" + startDate + "' and '" + endDate + "' and pregnant='yes') tt5 " +
                "from ec_mother limit 1;    ";
        List<CommonPersonObject> pregnantVaccineList = context().allCommonsRepositoryobjects("ec_mother").customQuery(pregnantWomanSQL, new String[]{}, "ec_mother");
        if (pregnantVaccineList.size() < 1) {

            pregnantWomanObject = null;
        } else {

            pregnantWomanObject = pregnantVaccineList.get(0);

        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //  pregnantWomanObject=context().allCommonsRepositoryobjects("ec_mother").customQuery(pregnantWomanSQL, new String[]{}, "ec_mother").get(0);

        Log.logDebug("Reached fieldVaccineObjectForField ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vaccine_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if(fieldReport==null) {
            Log.logDebug("reached FieldReport " );
            fieldReport = new FieldStockVaccineTable(fieldVaccineObjectForField, childVaccineForFieldObject, womanVaccineObjectForField);
        }if( childReport==null){
            new ChildVaccineTable(childObject);

        }*/
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 0) {

                return new ChildVaccineTable(childObject);
            } else if (position == 1) {
                return new WomanVaccineTable(pregnantWomanObject);

            } else if (position == 2) {
                //return new WomanVaccineTable(pregnantWomanObject);
                return new FieldStockVaccineTable(fieldVaccineObjectForField, childVaccineForFieldObject, womanVaccineObjectForField);

            } /*else {
                return PlaceholderFragment.newInstance(position + 1);

            }*/
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Child Report";
                case 1:
                    return "Woman Report";
                case 2:
                    return "Vaccines";
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_vaccine_report, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("This Feature is not added yet !");
            return rootView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (context().IsUserLoggedOut()) {
            DrishtiApplication application = (DrishtiApplication)getApplication();
            application.logoutCurrentUser();
            return;
        }
    }


    protected Context context() {
        return Context.getInstance().updateApplicationContext(this.getApplicationContext());
    }
}

package org.ei.opensrp.path.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.domain.MonthlyTally;
import org.ei.opensrp.path.fragment.DailyTalliesFragment;
import org.ei.opensrp.path.fragment.DraftMonthlyFragment;
import org.ei.opensrp.path.fragment.SendMonthlyDraftDialogFragment;
import org.ei.opensrp.path.fragment.SentMonthlyFragment;
import org.ei.opensrp.path.repository.HIA2IndicatorsRepository;
import org.ei.opensrp.path.repository.MonthlyTalliesRepository;
import org.ei.opensrp.path.service.HIA2Service;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.util.FormUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import util.JsonFormUtils;
import util.Utils;

/**
 * Created by coder on 6/7/17.
 */
public class HIA2ReportsActivity extends BaseActivity {
    private static String TAG = HIA2ReportsActivity.class.getCanonicalName();
    private static final int REQUEST_CODE_GET_JSON = 3432;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private LocationSwitcherToolbar toolbar;
    private ProgressBar syncProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarDrawerToggle toggle = getDrawerToggle();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(null);

        toolbar = (LocationSwitcherToolbar) getToolbar();
        toolbar.setTitle(getString(R.string.side_nav_hia2));

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);

        TextView initialsTV = (TextView) findViewById(R.id.name_inits);
        initialsTV.setText(getLoggedInUserInitials());
        initialsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

//        syncProgressBar = (ProgressBar) findViewById(R.id.sync_progress_bar);
//        Circle circle = new Circle();
//        syncProgressBar.setIndeterminateDrawable(circle);
//        refreshSyncStatusViews();

        //Update Draft Monthly Title
        Utils.startAsyncTask(new AsyncTask<Void, Void, List<Date>>() {
            @Override
            protected List<Date> doInBackground(Void... params) {
                MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication.getInstance().monthlyTalliesRepository();
                return monthlyTalliesRepository.findAllUnsentMonths();
            }

            @Override
            protected void onPostExecute(List<Date> dates) {
                refreshDraftMonthlyTitle(dates == null ? 0 : dates.size());
            }
        }, null);
    }

//    private void refreshSyncStatusViews() {
//        TextView initialsTV = (TextView) findViewById(R.id.name_inits);
//        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
//            syncProgressBar.setVisibility(View.VISIBLE);
//            initialsTV.setVisibility(View.GONE);
//        } else {
//            initialsTV.setVisibility(View.VISIBLE);
//            syncProgressBar.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onSyncStart() {
        super.onSyncStart();
        // refreshSyncStatusViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //this should go to the base class?
        LinearLayout hia2 = (LinearLayout) drawer.findViewById(R.id.hia2reports);
        hia2.setBackgroundColor(getResources().getColor(R.color.tintcolor));
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
        //refreshSyncStatusViews();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return DailyTalliesFragment.newInstance();
                case 1:
                    return DraftMonthlyFragment.newInstance();
                case 2:
                    return SentMonthlyFragment.newInstance();
            }
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
                    return getString(R.string.hia2_daily_tallies);
                case 1:
                    return getString(R.string.hia2_draft_monthly);
                case 2:
                    return getString(R.string.hia2_sent_monthly);

            }
            return null;
        }
    }

    private Fragment currentFragment() {
        if (mViewPager == null || mSectionsPagerAdapter == null) {
            return null;
        }

        return mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    Date report_month = null;

    public void startMonthlyReportForm(String formName, Date date) {
        try {
            report_month = date;
            Fragment currentFragment = currentFragment();

            if (currentFragment instanceof DraftMonthlyFragment) {
                MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication.getInstance().monthlyTalliesRepository();
                List<MonthlyTally> monthlyTallies = monthlyTalliesRepository.findDrafts(MonthlyTalliesRepository.MONTH_FORMAT.format(date));

                HIA2IndicatorsRepository hIA2IndicatorsRepository = VaccinatorApplication.getInstance().hIA2IndicatorsRepository();
                List<Hia2Indicator> hia2Indicators = hIA2IndicatorsRepository.fetchAll();
                if (hia2Indicators == null || hia2Indicators.isEmpty()) {
                    return;
                }

                JSONObject form = FormUtils.getInstance(this).getFormJson(formName);
                JSONObject step1 = form.getJSONObject("step1");
                String title = MonthlyTalliesRepository.MONTH_FORMAT.format(date).concat(" Draft");
                step1.put("title", title);

//                JSONArray fields1 = new JSONArray();
//                JSONArray fields2 = new JSONArray();
//                JSONArray fields3 = new JSONArray();

                JSONArray sections = step1.getJSONArray(JsonFormConstants.SECTIONS);

                String indicatorCategory = "";
                //this map holds each category as key and all the fields for that category as the value (jsonarray)
                LinkedHashMap<String, JSONArray> fieldsMap = new LinkedHashMap<>();
                for (Hia2Indicator hia2Indicator : hia2Indicators) {
                    JSONObject jsonObject = new JSONObject();
                    if (hia2Indicator.getLabel() == null) {
                        hia2Indicator.setLabel("");
                    }
                    String label = hia2Indicator.getLabel();
                    jsonObject.put("key", hia2Indicator.getId());
                    jsonObject.put("type", "edit_text");
                    jsonObject.put("hint", label);
                    jsonObject.put("value", retrieveValue(monthlyTallies, hia2Indicator));
                    jsonObject.put("openmrs_entity_parent", "");
                    jsonObject.put("openmrs_entity", "");
                    jsonObject.put("openmrs_entity_id", "");
                    indicatorCategory = hia2Indicator.getCategory();
                    JSONArray fields = null;
                    if (fieldsMap.containsKey(indicatorCategory)) {
                        fields = fieldsMap.get(indicatorCategory);
                    } else {
                        fields = new JSONArray();
                    }
                    fields.put(jsonObject);
                    fieldsMap.put(indicatorCategory, fields);

                }
                //build sections in the form based on categories, each key is a category
                for (String key : fieldsMap.keySet()) {
                    JSONObject section = new JSONObject();
                    section.put(JsonFormConstants.NAME, key);
                    section.put(JsonFormConstants.FIELDS, fieldsMap.get(key));
                    sections.put(section);
                }

                Intent intent = new Intent(this, PathJsonFormActivity.class);
                intent.putExtra("json", form.toString());
                intent.putExtra("report_month", HIA2Service.dfyymmdd.format(date));
                Log.d(TAG, "form is " + form.toString());
                startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    boolean showFragment = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON) {
            if (resultCode == RESULT_OK) {

                try {
                    showFragment = true;
                    String jsonString = data.getStringExtra("json");

                    JSONObject monthlyDraftForm = new JSONObject(jsonString);
                    Map<String, String> result = JsonFormUtils.sectionFields(monthlyDraftForm);
                    VaccinatorApplication.getInstance().monthlyTalliesRepository().save(result, report_month);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (showFragment) {
            //showDialog("");
        }
        showFragment=false;
    }

    void showDialog(String date) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("SendMonthlyDraftDialogFragment");
        if (prev != null) {
            ft.remove(prev);
        }

        // Create and show the dialog.
        SendMonthlyDraftDialogFragment newFragment = SendMonthlyDraftDialogFragment.newInstance(date);
        newFragment.show(ft, "SendMonthlyDraftDialogFragment");
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_hia2_reports;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return null;
    }

    public void refreshDraftMonthlyTitle(final int count) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && tab.getText() != null && tab.getText().toString().contains(getString(R.string.hia2_draft_monthly))) {
                        tab.setText(String.format(getString(R.string.hia2_draft_monthly_with_count), count));
                    }
                }
            }
        });

    }

    private String retrieveValue(List<MonthlyTally> monthlyTallies, Hia2Indicator hia2Indicator) {
        String value = "";
        if (hia2Indicator == null || monthlyTallies == null)
            return value;

        for (MonthlyTally monthlyTally : monthlyTallies) {
            if (monthlyTally.getIndicator() != null) {
                if (monthlyTally.getIndicator().getIndicatorCode().equalsIgnoreCase(hia2Indicator.getIndicatorCode())) {
                    return monthlyTally.getValue();
                }
            }
        }
        return "";
    }

}

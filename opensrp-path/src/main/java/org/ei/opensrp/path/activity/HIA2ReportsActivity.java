package org.ei.opensrp.path.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import org.ei.opensrp.path.repository.DailyTalliesRepository;
import org.ei.opensrp.path.repository.HIA2IndicatorsRepository;
import org.ei.opensrp.path.repository.MonthlyTalliesRepository;
import org.ei.opensrp.path.service.HIA2Service;
import org.ei.opensrp.path.service.intent.HIA2IntentService;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.util.FormUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
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
    public static final int MONTH_SUGGESTION_LIMIT = 3;
    private static final String FORM_KEY_CONFIRM = "confirm";

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
    private ProgressDialog progressDialog;

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

        // Update Draft Monthly Title
        refreshDraftMonthlyTitle();
    }

    @Override
    public void onSyncStart() {
        super.onSyncStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // TODO: This should go to the base class?
        LinearLayout hia2 = (LinearLayout) drawer.findViewById(R.id.hia2reports);
        hia2.setBackgroundColor(getResources().getColor(R.color.tintcolor));
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
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

    public void startMonthlyReportForm(String formName, Date date, boolean firstTimeEdit) {
        try {
            Fragment currentFragment = currentFragment();
            if (currentFragment instanceof DraftMonthlyFragment) {
                Utils.startAsyncTask(
                        new StartDraftMonthlyFormTask(this, date, formName, firstTimeEdit), null);
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
                    JSONObject form = new JSONObject(jsonString);
                    String monthString = form.getString("report_month");
                    Date month = HIA2Service.dfyymmdd.parse(monthString);

                    JSONObject monthlyDraftForm = new JSONObject(jsonString);
                    Map<String, String> result = JsonFormUtils.sectionFields(monthlyDraftForm);
                    boolean saveClicked = false;
                    if (result.containsKey(FORM_KEY_CONFIRM)) {
                        saveClicked = Boolean.valueOf(result.get(FORM_KEY_CONFIRM));
                        result.remove(FORM_KEY_CONFIRM);
                    }
                    VaccinatorApplication.getInstance().monthlyTalliesRepository()
                            .save(result, month);
                    if (saveClicked) {
                        sendReport(month);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                } catch (ParseException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (showFragment) {
            //sendReport("");
        }
        showFragment = false;
    }

    void sendReport(final Date month) {
        if (month != null) {
            FragmentTransaction ft = getFragmentManager()
                    .beginTransaction();
            android.app.Fragment prev = getFragmentManager()
                    .findFragmentByTag("SendMonthlyDraftDialogFragment");
            if (prev != null) {
                ft.remove(prev);
            }

            // Create and show the dialog.
            SendMonthlyDraftDialogFragment newFragment = SendMonthlyDraftDialogFragment
                    .newInstance(
                            MonthlyTalliesRepository.DF_DDMMYY.format(Calendar.getInstance().getTime()),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(HIA2ReportsActivity.this,
                                            HIA2IntentService.class);
                                    intent.putExtra(HIA2IntentService.GENERATE_REPORT, true);
                                    intent.putExtra(HIA2IntentService.REPORT_MONTH,
                                            HIA2Service.dfyymm.format(month));
                                    startService(intent);
                                }
                            });
            ft.add(newFragment, "SendMonthlyDraftDialogFragment");
            ft.commitAllowingStateLoss();
        }
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

    public void refreshDraftMonthlyTitle() {
        Utils.startAsyncTask(new FetchEditedMonthlyTalliesTask(new FetchEditedMonthlyTalliesTask.TaskListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(final List<MonthlyTally> monthlyTallies) {
                tabLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                            TabLayout.Tab tab = tabLayout.getTabAt(i);
                            if (tab != null && tab.getText() != null && tab.getText().toString()
                                    .contains(getString(R.string.hia2_draft_monthly))) {
                                tab.setText(String.format(
                                        getString(R.string.hia2_draft_monthly_with_count),
                                        monthlyTallies == null ? 0 : monthlyTallies.size()));
                            }
                        }
                    }
                });
            }
        }), null);
    }

    public static String retrieveValue(List<MonthlyTally> monthlyTallies, Hia2Indicator hia2Indicator) {
        String defaultValue = "0";
        if (hia2Indicator == null || monthlyTallies == null) {
            return defaultValue;
        }

        for (MonthlyTally monthlyTally : monthlyTallies) {
            if (monthlyTally.getIndicator() != null) {
                if (monthlyTally.getIndicator().getIndicatorCode()
                        .equalsIgnoreCase(hia2Indicator.getIndicatorCode())) {
                    return monthlyTally.getValue();
                }
            }
        }

        return defaultValue;
    }

    public static class FetchEditedMonthlyTalliesTask extends AsyncTask<Void, Void, List<MonthlyTally>> {
        private final TaskListener taskListener;

        public FetchEditedMonthlyTalliesTask(TaskListener taskListener) {
            this.taskListener = taskListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            taskListener.onPreExecute();
        }

        @Override
        protected List<MonthlyTally> doInBackground(Void... params) {
            MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication
                    .getInstance().monthlyTalliesRepository();
            Calendar endDate = Calendar.getInstance();
            endDate.set(Calendar.DAY_OF_MONTH, 1);// Set date to first day of this month
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
            endDate.set(Calendar.SECOND, 59);
            endDate.set(Calendar.MILLISECOND, 999);
            endDate.add(Calendar.DATE, -1);// Move the date to last day of last month

            return monthlyTalliesRepository.findEditedDraftMonths(null, endDate.getTime());
        }

        @Override
        protected void onPostExecute(List<MonthlyTally> monthlyTallies) {
            super.onPostExecute(monthlyTallies);
            taskListener.onPostExecute(monthlyTallies);
        }

        public static interface TaskListener {
            void onPreExecute();

            void onPostExecute(List<MonthlyTally> monthlyTallies);
        }
    }

    public static class StartDraftMonthlyFormTask extends AsyncTask<Void, Void, Intent> {
        private HIA2ReportsActivity baseActivity;
        private Date date;
        private String formName;
        private boolean firstTimeEdit;

        public StartDraftMonthlyFormTask(HIA2ReportsActivity baseActivity,
                                         Date date, String formName, boolean firstTimeEdit) {
            this.baseActivity = baseActivity;
            this.date = date;
            this.formName = formName;
            this.firstTimeEdit = firstTimeEdit;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseActivity.showProgressDialog();
        }

        @Override
        protected Intent doInBackground(Void... params) {
            try {
                MonthlyTalliesRepository monthlyTalliesRepository = VaccinatorApplication
                        .getInstance().monthlyTalliesRepository();
                List<MonthlyTally> monthlyTallies = monthlyTalliesRepository
                        .findDrafts(MonthlyTalliesRepository.DF_YYYYMM.format(date));

                HIA2IndicatorsRepository hIA2IndicatorsRepository = VaccinatorApplication
                        .getInstance().hIA2IndicatorsRepository();
                List<Hia2Indicator> hia2Indicators = hIA2IndicatorsRepository.fetchAll();
                if (hia2Indicators == null || hia2Indicators.isEmpty()) {
                    return null;
                }

                JSONObject form = FormUtils.getInstance(baseActivity).getFormJson(formName);
                JSONObject step1 = form.getJSONObject("step1");
                String title = MonthlyTalliesRepository.DF_YYYYMM.format(date).concat(" Draft");
                step1.put("title", title);

                JSONArray sections = step1.getJSONArray(JsonFormConstants.SECTIONS);

                String indicatorCategory = "";
                // This map holds each category as key and all the fields for that category as the
                // value (jsonarray)
                LinkedHashMap<String, JSONArray> fieldsMap = new LinkedHashMap<>();
                for (Hia2Indicator hia2Indicator : hia2Indicators) {
                    JSONObject jsonObject = new JSONObject();
                    if (hia2Indicator.getLabel() == null) {
                        hia2Indicator.setLabel("");
                    }
                    String label = hia2Indicator.getLabel() + " *";

                    JSONObject vRequired = new JSONObject();
                    vRequired.put("value", "true");
                    vRequired.put("err", "Specify: " + hia2Indicator.getLabel());
                    JSONObject vNumeric = new JSONObject();
                    vNumeric.put("value", "true");
                    vNumeric.put("err", "Value should be numeric");

                    jsonObject.put("key", hia2Indicator.getId());
                    jsonObject.put("type", "edit_text");
                    jsonObject.put("hint", label);
                    jsonObject.put("value", retrieveValue(monthlyTallies, hia2Indicator));
                    if (DailyTalliesRepository.IGNORED_INDICATOR_CODES
                            .contains(hia2Indicator.getIndicatorCode()) && firstTimeEdit) {
                        jsonObject.put("value", "");
                    }
                    jsonObject.put("v_required", vRequired);
                    jsonObject.put("v_numeric", vNumeric);
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

                // Build sections in the form based on categories, each key is a category
                for (String key : fieldsMap.keySet()) {
                    JSONObject section = new JSONObject();
                    section.put(JsonFormConstants.NAME, key);
                    section.put(JsonFormConstants.FIELDS, fieldsMap.get(key));
                    sections.put(section);
                }

                // Add the confirm button
                JSONObject buttonObject = new JSONObject();
                buttonObject.put("key", FORM_KEY_CONFIRM);
                buttonObject.put("value", "false");
                buttonObject.put("type", "button");
                buttonObject.put("hint", "Confirm");
                buttonObject.put("openmrs_entity_parent", "");
                buttonObject.put("openmrs_entity", "");
                buttonObject.put("openmrs_entity_id", "");
                JSONObject action = new JSONObject();
                action.put("behaviour", "finish_form");
                buttonObject.put("action", action);

                JSONArray confirmSectionFields = new JSONArray();
                confirmSectionFields.put(buttonObject);
                JSONObject confirmSection = new JSONObject();
                confirmSection.put(JsonFormConstants.FIELDS, confirmSectionFields);
                sections.put(confirmSection);

                form.put("report_month", HIA2Service.dfyymmdd.format(date));

                Intent intent = new Intent(baseActivity, PathJsonFormActivity.class);
                intent.putExtra("json", form.toString());

                return intent;
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            super.onPostExecute(intent);
            baseActivity.hideProgressDialog();
            if (intent != null) {
                baseActivity.startActivityForResult(intent, REQUEST_CODE_GET_JSON);
            }
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            initializeProgressDialog();
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}

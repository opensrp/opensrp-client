package org.ei.opensrp.path.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.ei.opensrp.Context;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.HIA2ReportsActivity;
import org.ei.opensrp.path.activity.ReportSummaryActivity;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.DailyTally;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.receiver.Hia2ServiceBroadcastReceiver;
import org.ei.opensrp.path.repository.DailyTalliesRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import util.Utils;

/**
 * Created by coder on 6/7/17.
 */
public class DailyTalliesFragment extends Fragment
        implements Hia2ServiceBroadcastReceiver.Hia2ServiceListener {
    private static final String TAG = DailyTalliesFragment.class.getCanonicalName();
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
    private ExpandableListView expandableListView;
    private HashMap<String, ArrayList<DailyTally>> dailyTallies;
    private HashMap<String, Hia2Indicator> hia2Indicators;
    private ProgressDialog progressDialog;

    public static DailyTalliesFragment newInstance() {
        DailyTalliesFragment fragment = new DailyTalliesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.startAsyncTask(new GetAllTalliesTask(), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Hia2ServiceBroadcastReceiver.getInstance().addHia2ServiceListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Hia2ServiceBroadcastReceiver.getInstance().removeHia2ServiceListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.expandable_list_fragment, container, false);
        expandableListView = (ExpandableListView) fragmentView.findViewById(R.id.expandable_list_view);

        return fragmentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandableList();
        }
    }

    private void updateExpandableList() {
        updateExpandableList(formatListData());
    }

    @SuppressWarnings("unchecked")
    private void updateExpandableList(final LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> map) {

        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter<String, Date> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.daily_tally_header, R.layout.daily_tally_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag = v.getTag(R.id.item_data);
                if (tag != null) {
                    if (tag instanceof Date) {
                        Date date = (Date) tag;
                        String dayString = DAY_FORMAT.format(date);
                        if (dailyTallies.containsKey(dayString)) {
                            ArrayList<DailyTally> indicators = new ArrayList(dailyTallies.get(dayString));
                            addIgnoredIndicators(date, indicators);
                            String title = String.format(getString(R.string.daily_tally_), dayString);
                            Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TALLIES, indicators);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                            startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    private LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map = new HashMap<>();
        Map<Long, String> sortMap = new TreeMap<>(new Comparator<Comparable>() {
            public int compare(Comparable a, Comparable b) {
                return b.compareTo(a);
            }
        });
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        if (dailyTallies != null) {
            for (ArrayList<DailyTally> curDay : dailyTallies.values()) {
                if (curDay.size() > 0) {
                    Date day = curDay.get(0).getDay();
                    String monthString = monthFormat.format(day);
                    if (!map.containsKey(monthString)) {
                        map.put(monthString,
                                new ArrayList<ExpandedListAdapter.ItemData<String, Date>>());
                    }

                    map.get(monthString).add(
                            new ExpandedListAdapter.ItemData<String, Date>(DAY_FORMAT.format(day),
                                    day)
                    );
                    sortMap.put(day.getTime(), monthString);
                }
            }
        }

        LinkedHashMap<String, List<ExpandedListAdapter.ItemData<String, Date>>> sortedMap = new LinkedHashMap<>();
        for (Long curKey : sortMap.keySet()) {
            List<ExpandedListAdapter.ItemData<String, Date>> list = map.get(sortMap.get(curKey));
            Collections.sort(list, new Comparator<ExpandedListAdapter.ItemData<String, Date>>() {
                @Override
                public int compare(ExpandedListAdapter.ItemData<String, Date> lhs,
                                   ExpandedListAdapter.ItemData<String, Date> rhs) {
                    return lhs.getTagData().compareTo(rhs.getTagData());
                }
            });
            sortedMap.put(sortMap.get(curKey), list);
        }

        return sortedMap;
    }

    /**
     * Adds indicators that are not calculated on a daily basis to the list of provided tallies each
     * with an "N/A" value.
     *
     * @param tallies
     */
    private void addIgnoredIndicators(Date day, ArrayList<DailyTally> tallies) {
        if (hia2Indicators != null && tallies != null) {
            for (String curIgnoredCode : DailyTalliesRepository.IGNORED_INDICATOR_CODES) {
                if (hia2Indicators.containsKey(curIgnoredCode)) {
                    DailyTally curIgnoredTally = new DailyTally();
                    curIgnoredTally.setProviderId(
                            Context.getInstance().allSharedPreferences().fetchRegisteredANM()
                    );
                    curIgnoredTally.setIndicator(hia2Indicators.get(curIgnoredCode));
                    curIgnoredTally.setValue(getString(R.string.n_a));
                    curIgnoredTally.setDay(day);
                    curIgnoredTally.setUpdatedAt(Calendar.getInstance().getTime());

                    tallies.add(curIgnoredTally);
                }
            }
        }
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait_message));
    }

    public void showProgressDialog() {
        try {
            if (progressDialog == null) {
                initializeProgressDialog();
            }

            progressDialog.show();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onServiceFinish(String actionType) {
        if (Hia2ServiceBroadcastReceiver.TYPE_GENERATE_DAILY_INDICATORS.equals(actionType)) {
            Utils.startAsyncTask(new GetAllTalliesTask(), null);
        }
    }

    private class GetAllTalliesTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<DailyTally>>> {

        private HashMap<String, Hia2Indicator> indicatorsMap;

        public GetAllTalliesTask() {
            indicatorsMap = new HashMap<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected HashMap<String, ArrayList<DailyTally>> doInBackground(Void... params) {
            Calendar startDate = Calendar.getInstance();

            List<Hia2Indicator> indicators = VaccinatorApplication.getInstance()
                    .hIA2IndicatorsRepository().fetchAll();
            for (Hia2Indicator curIndicator : indicators) {
                if (curIndicator != null) {
                    indicatorsMap.put(curIndicator.getIndicatorCode(), curIndicator);
                }
            }

            startDate.set(Calendar.DAY_OF_MONTH, 1);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
            startDate.set(Calendar.MINUTE, 0);
            startDate.set(Calendar.SECOND, 0);
            startDate.set(Calendar.MILLISECOND, 0);
            startDate.add(Calendar.MONTH, -1 * HIA2ReportsActivity.MONTH_SUGGESTION_LIMIT);
            return VaccinatorApplication.getInstance().dailyTalliesRepository()
                    .findAll(DAY_FORMAT, startDate.getTime(), Calendar.getInstance().getTime());
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<DailyTally>> tallies) {
            super.onPostExecute(tallies);
            hideProgressDialog();
            DailyTalliesFragment.this.dailyTallies = tallies;
            DailyTalliesFragment.this.hia2Indicators = indicatorsMap;
            updateExpandableList();
        }
    }
}

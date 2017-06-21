package org.ei.opensrp.path.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.HIA2ReportsActivity;
import org.ei.opensrp.path.activity.ReportSummaryActivity;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.MonthlyTally;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class SentMonthlyFragment extends Fragment {
    private static final String TAG = SentMonthlyFragment.class.getCanonicalName();
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy");
    private ExpandableListView expandableListView;
    private HashMap<String, ArrayList<MonthlyTally>> sentMonthlyTallies;
    private ProgressDialog progressDialog;

    public static SentMonthlyFragment newInstance() {
        SentMonthlyFragment fragment = new SentMonthlyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.startAsyncTask(new GetSentTalliesTask(), null);
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
            updateExpandedList();
        }
    }

    private void updateExpandedList() {
        updateExpandedList(formatListData());
    }

    /**
     *
     * @param map
     */
    @SuppressWarnings("unchecked")
    private void updateExpandedList(final LinkedHashMap<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> map) {

        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter<Pair<String, String>, Date> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.sent_monthly_header, R.layout.sent_monthly_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag = v.getTag(R.id.item_data);
                if (tag != null) {
                    if (tag instanceof Date) {
                        Date month = (Date) tag;
                        if (sentMonthlyTallies.containsKey(MONTH_YEAR_FORMAT.format(month))
                                && sentMonthlyTallies.get(MONTH_YEAR_FORMAT.format(month)).size() > 0) {
                            ArrayList<MonthlyTally> indicators = sentMonthlyTallies
                                    .get(MONTH_YEAR_FORMAT.format(month));
                            String dateSubmitted = new SimpleDateFormat("dd/MM/yy").format(indicators.get(0).getDateSent());
                            String subTitle = String.format(getString(R.string.submitted_by_),
                                    dateSubmitted,
                                    indicators.get(0).getProviderId());
                            String monthString = MONTH_YEAR_FORMAT.format(month);
                            String title = String.format(getString(R.string.sent_reports_),
                                    monthString);
                            Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TALLIES, indicators);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                            intent.putExtra(ReportSummaryActivity.EXTRA_SUB_TITLE, subTitle);
                            startActivity(intent);
                        }
                    }
                }
                return true;
            }
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    private LinkedHashMap<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> map = new HashMap<>();
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat dateSentFormat = new SimpleDateFormat("M/d/yy");
        Map<Long, String> sortMap = new TreeMap<>(new Comparator<Comparable>() {
            public int compare(Comparable a, Comparable b) {
                return b.compareTo(a);
            }
        });

        if (sentMonthlyTallies != null) {
            for (List<MonthlyTally> curMonthTallies : sentMonthlyTallies.values()) {
                if (curMonthTallies != null && curMonthTallies.size() > 0) {
                    Date month = curMonthTallies.get(0).getMonth();
                    String year = yearFormat.format(month);
                    if (!map.containsKey(year)) {
                        map.put(year,
                                new ArrayList<ExpandedListAdapter.ItemData<Pair<String, String>,
                                        Date>>());
                    }

                    String details = String.format(getString(R.string.sent_by),
                            dateSentFormat.format(curMonthTallies.get(0).getDateSent()),
                            curMonthTallies.get(0).getProviderId());
                    map.get(year)
                            .add(new ExpandedListAdapter.ItemData<Pair<String, String>, Date>(
                                    Pair.create(MONTH_YEAR_FORMAT.format(month), details), month));
                    sortMap.put(month.getTime(), year);
                }
            }
        }

        LinkedHashMap<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>> sortedMap = new LinkedHashMap<>();
        for (Long curKey : sortMap.keySet()) {
            List<ExpandedListAdapter.ItemData<Pair<String, String>, Date>> list = map.get(sortMap.get(curKey));
            Collections.sort(list, new Comparator<ExpandedListAdapter.ItemData<Pair<String, String>, Date>>() {
                @Override
                public int compare(ExpandedListAdapter.ItemData<Pair<String, String>, Date> lhs, ExpandedListAdapter.ItemData<Pair<String, String>, Date> rhs) {
                    return lhs.getTagData().compareTo(rhs.getTagData());
                }
            });
            sortedMap.put(sortMap.get(curKey), list);
        }

        return sortedMap;

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

    private class GetSentTalliesTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<MonthlyTally>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected HashMap<String, ArrayList<MonthlyTally>> doInBackground(Void... params) {
            return VaccinatorApplication.getInstance().monthlyTalliesRepository().findAllSent(MONTH_YEAR_FORMAT);
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<MonthlyTally>> stringListHashMap) {
            super.onPostExecute(stringListHashMap);
            hideProgressDialog();
            SentMonthlyFragment.this.sentMonthlyTallies = stringListHashMap;
            updateExpandedList();
        }
    }
}

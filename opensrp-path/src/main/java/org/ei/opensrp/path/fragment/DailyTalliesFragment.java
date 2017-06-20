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

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.HIA2ReportsActivity;
import org.ei.opensrp.path.activity.ReportSummaryActivity;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.DailyTally;
import org.ei.opensrp.path.domain.MonthlyTally;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import util.Utils;

/**
 * Created by coder on 6/7/17.
 */
public class DailyTalliesFragment extends Fragment {
    private static final String TAG = DailyTalliesFragment.class.getCanonicalName();
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
    private ExpandableListView expandableListView;
    private HashMap<String, ArrayList<DailyTally>> dailyTallies;
    private ProgressDialog progressDialog;

    public static DailyTalliesFragment newInstance() {
        DailyTalliesFragment fragment = new DailyTalliesFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.expandable_list_fragment, container, false);
        expandableListView = (ExpandableListView) fragmentView.findViewById(R.id.expandable_list_view);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag = v.getTag(R.id.item_data);
                if (tag != null) {
                    if (tag instanceof Date) {
                        // TODO: get data from the repository
                        Date date = (Date) tag;

                    }
                }
                return true;
            }
        });
        updateExpandableList();
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.startAsyncTask(new GetAllTalliesTask(), null);
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
    private void updateExpandableList(final Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map) {

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
                        Date day = (Date) tag;
                        if (dailyTallies.containsKey(DAY_FORMAT.format(day))
                                && dailyTallies.get(DAY_FORMAT.format(day)).size() > 0) {
                            ArrayList<DailyTally> indicators = dailyTallies
                                    .get(DAY_FORMAT.format(day));
                            String title = String.format(getString(R.string.daily_tally_),
                                    DAY_FORMAT.format(day));
                            Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TALLIES, indicators);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                        }
                    }
                }
                return true;
            }
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    private Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> formatListData() {
        Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map = new LinkedHashMap<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        if (dailyTallies != null) {
            for (ArrayList<DailyTally> curDay : dailyTallies.values()) {
                if (curDay.size() > 0) {
                    Date day = curDay.get(0).getDay();
                    if (!map.containsKey(monthFormat.format(day))) {
                        map.put(monthFormat.format(day),
                                new ArrayList<ExpandedListAdapter.ItemData<String, Date>>());
                    }

                    map.get(monthFormat.format(day)).add(
                            new ExpandedListAdapter.ItemData<String, Date>(DAY_FORMAT.format(day),
                                    day)
                    );
                }
            }
        }

        return map;
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

    private class GetAllTalliesTask extends AsyncTask<Void, Void, HashMap<String, ArrayList<DailyTally>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected HashMap<String, ArrayList<DailyTally>> doInBackground(Void... params) {
            return VaccinatorApplication.getInstance().dailyTalliesRepository().findAll(DAY_FORMAT);
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<DailyTally>> tallies) {
            super.onPostExecute(tallies);
            hideProgressDialog();
            dailyTallies = tallies;
            updateExpandableList();
        }
    }
}

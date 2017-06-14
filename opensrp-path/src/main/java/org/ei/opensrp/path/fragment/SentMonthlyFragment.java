package org.ei.opensrp.path.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ReportSummaryActivity;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.service.HIA2Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by coder on 6/7/17.
 */
public class SentMonthlyFragment extends Fragment {
    private static final SimpleDateFormat MONTH_YEAR_FORMAT = new SimpleDateFormat("MMMM yyyy");
    private ExpandableListView expandableListView;

    public static SentMonthlyFragment newInstance() {
        SentMonthlyFragment fragment = new SentMonthlyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.expandable_list_fragment, container, false);
        expandableListView = (ExpandableListView) fragmentView.findViewById(R.id.expandable_list_view);
        updateExpandedList();
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
        updateExpandedList(dummyData());
    }

    /**
     *
     * @param map
     */
    @SuppressWarnings("unchecked")
    private void updateExpandedList(final Map<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Pair<String, Date>>>> map) {

        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter<Pair<String, String>, Pair<String, Date>> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.sent_monthly_header, R.layout.sent_monthly_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Object tag = v.getTag(R.id.item_data);
                if (tag != null) {
                    if (tag instanceof Pair) {
                        Pair<String, Date> pair = (Pair<String, Date>) tag;
                        String providerId = pair.first;
                        Date month = pair.second;
                        ArrayList<Hia2Indicator> indicators = new ArrayList(VaccinatorApplication.getInstance()
                                .hia2Repository()
                                .findByProviderIdAndMonth(providerId, HIA2Service.dfyymm.format(month)));
                        if (indicators.size() > 0) {
                            String dateSubmitted = new SimpleDateFormat("dd/MM/yy").format(indicators.get(0).getUpdatedAt());
                            String subTitle = String.format(getString(R.string.submitted_by_),
                                    dateSubmitted,
                                    providerId);
                            String monthString = new SimpleDateFormat("MMMM yyyy").format(month);
                            String title = String.format(getString(R.string.sent_reports_),
                                    monthString);
                            Intent intent = new Intent(getActivity(), ReportSummaryActivity.class);
                            intent.putExtra(ReportSummaryActivity.EXTRA_INDICATORS, indicators);
                            intent.putExtra(ReportSummaryActivity.EXTRA_TITLE, title);
                            intent.putExtra(ReportSummaryActivity.EXTRA_SUB_TITLE, subTitle);
                        }
                    }
                }
                return true;
            }
        });
        expandableListAdapter.notifyDataSetChanged();
    }

    //TODO REMOVE
    private Map<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Pair<String, Date>>>> dummyData() {
        Map<String, List<ExpandedListAdapter.ItemData<Pair<String, String>, Pair<String, Date>>>> map = new LinkedHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.MONTH, false);
        String one = new SimpleDateFormat("yyyy").format(cal.getTime());

        List<ExpandedListAdapter.ItemData<Pair<String, String>, Pair<String, Date>>> days = new ArrayList<>();

        String day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        String detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal.getTime())));

        Calendar cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));

        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));


        map.put(one, days);

        cal.roll(Calendar.YEAR, false);
        String two = new SimpleDateFormat("yyyy").format(cal.getTime());

        days = new ArrayList<>();

        day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal.getTime())));


        cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));

        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));


        map.put(two, days);


        cal.roll(Calendar.YEAR, false);
        String three = new SimpleDateFormat("yyyy").format(cal.getTime());
        days = new ArrayList<>();

        day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal.getTime())));


        cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));


        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(new ExpandedListAdapter.ItemData(Pair.create(day, detail), Pair.create("HW", cal2.getTime())));

        map.put(three, days);

        return map;

    }
}

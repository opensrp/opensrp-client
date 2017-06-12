package org.ei.opensrp.path.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by coder on 6/7/17.
 */
public class SentMonthlyFragment extends Fragment {

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

    @SuppressWarnings("unchecked")
    private void updateExpandedList(final Map<String, List<Pair<String, String>>> map) {

        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.sent_monthly_header, R.layout.sent_monthly_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListAdapter.notifyDataSetChanged();
    }

    //TODO REMOVE
    private Map<String, List<Pair<String, String>>> dummyData() {
        Map<String, List<Pair<String, String>>> map = new LinkedHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.MONTH, false);
        String one = new SimpleDateFormat("yyyy").format(cal.getTime());

        List<Pair<String, String>> days = new ArrayList<>();

        String day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        String detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(Pair.create(day, detail));

        Calendar cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));

        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));


        map.put(one, days);

        cal.roll(Calendar.YEAR, false);
        String two = new SimpleDateFormat("yyyy").format(cal.getTime());

        days = new ArrayList<>();

        day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(Pair.create(day, detail));


        cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));

        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));


        map.put(two, days);


        cal.roll(Calendar.YEAR, false);
        String three = new SimpleDateFormat("yyyy").format(cal.getTime());
        days = new ArrayList<>();

        day = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal.getTime()), "HW");
        days.add(Pair.create(day, detail));


        cal2 = cal;
        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));


        cal2.roll(Calendar.MONTH, false);

        day = new SimpleDateFormat("MMMM yyyy").format(cal2.getTime());
        detail = String.format(getString(R.string.sent_by), new SimpleDateFormat("M/d/yy").format(cal2.getTime()), "HW");
        days.add(Pair.create(day, detail));

        map.put(three, days);

        return map;

    }
}

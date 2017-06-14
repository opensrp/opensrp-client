package org.ei.opensrp.path.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.adapter.ExpandedListAdapter;

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
public class DailyTalliesFragment extends Fragment {

    private ExpandableListView expandableListView;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandableList();
        }
    }

    private void updateExpandableList() {
        updateExpandableList(dummyData());
    }

    @SuppressWarnings("unchecked")
    private void updateExpandableList(final Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map) {

        if (expandableListView == null) {
            return;
        }

        ExpandedListAdapter<String, Date> expandableListAdapter = new ExpandedListAdapter(getActivity(), map, R.layout.daily_tally_header, R.layout.daily_tally_item);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListAdapter.notifyDataSetChanged();
    }

    //TODO REMOVE
    private Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> dummyData() {
        Map<String, List<ExpandedListAdapter.ItemData<String, Date>>> map = new LinkedHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.MONTH, false);
        String one = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());

        List<ExpandedListAdapter.ItemData<String, Date>> days = new ArrayList<>();

        String day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal.getTime()));

        Calendar cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        map.put(one, days);

        cal.roll(Calendar.MONTH, false);
        String two = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());

        days = new ArrayList<>();

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal.getTime()));

        cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        map.put(two, days);


        cal.roll(Calendar.MONTH, false);
        String three = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        days = new ArrayList<>();

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal.getTime()));

        cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(new ExpandedListAdapter.ItemData(day, cal2.getTime()));

        map.put(three, days);

        return map;
    }

}

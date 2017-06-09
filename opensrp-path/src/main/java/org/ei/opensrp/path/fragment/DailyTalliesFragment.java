package org.ei.opensrp.path.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.ei.opensrp.path.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        final View fragmentview = inflater.inflate(R.layout.daily_tallies_fragment, container, false);
        expandableListView = (ExpandableListView) fragmentview.findViewById(R.id.expandable_list_view);
        updateExpandableList();
        return fragmentview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updateExpandableList();
        }
    }

    private void updateExpandableList() {
        Map<String, List<String>> map = new LinkedHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.roll(Calendar.MONTH, false);
        String one = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());

        List<String> days = new ArrayList<>();

        String day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(day);

        Calendar cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        map.put(one, days);

        cal.roll(Calendar.MONTH, false);
        String two = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());

        days = new ArrayList<>();

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(day);

        cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        map.put(two, days);


        cal.roll(Calendar.MONTH, false);
        String three = new SimpleDateFormat("MMMM yyyy").format(cal.getTime());
        days = new ArrayList<>();

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal.getTime());
        days.add(day);

        cal2 = cal;
        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        cal2.roll(Calendar.DAY_OF_MONTH, false);
        cal2.roll(Calendar.DAY_OF_MONTH, false);

        day = new SimpleDateFormat("dd MMMM yyyy").format(cal2.getTime());
        days.add(day);

        map.put(three, days);

        updateExpandableList(map);
    }

    private void updateExpandableList(final Map<String, List<String>> map) {

        if (expandableListView == null) {
            return;
        }

        final List<String> headers = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            headers.add(entry.getKey());
        }


        BaseExpandableListAdapter expandableListAdapter = new BaseExpandableListAdapter() {
            @Override
            public Object getChild(int groupPosition, int childPosititon) {
                return map.get(headers.get(groupPosition))
                        .get(childPosititon);
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public View getChildView(int groupPosition, final int childPosition,
                                     boolean isLastChild, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                    convertView = inflater.inflate(R.layout.daily_tally_item, null);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                String text = (String) getChild(groupPosition, childPosition);
                tv.setText(text);

                convertView.setTag(text);

                isLastChild = (getChildrenCount(groupPosition) - 1) == childPosition;
                View dividerBottom = convertView.findViewById(R.id.adapter_divider_bottom);
                if (isLastChild) {
                    dividerBottom.setVisibility(View.VISIBLE);
                } else {
                    dividerBottom.setVisibility(View.GONE);
                }


                return convertView;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return map.get(headers.get(groupPosition))
                        .size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return headers.get(groupPosition);
            }

            @Override
            public int getGroupCount() {
                return headers.size();
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded,
                                     View convertView, ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater)
                            getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                    convertView = inflater.inflate(R.layout.daily_tally_header, null);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.tv);
                String text = (String) getGroup(groupPosition);
                tv.setText(text);

                convertView.setTag(text);

                ExpandableListView mExpandableListView = (ExpandableListView) parent;
                mExpandableListView.expandGroup(groupPosition);

                return convertView;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

        };

        expandableListView.setAdapter(expandableListAdapter);
        expandableListAdapter.notifyDataSetChanged();

    }

}

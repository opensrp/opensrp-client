package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.ei.opensrp.path.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keyman on 12/06/2017.
 */
public class ExpandedListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, List<String>> map = new LinkedHashMap<>();
    private List<String> headers = new ArrayList<>();
    private int headerLayout;
    private int childLayout;


    public ExpandedListAdapter(Context context, Map<String, List<String>> map, int headerLayout, int childLayout) {
        this.context = context;

        if (map != null && !map.isEmpty()) {
            this.map = map;
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                this.headers.add(entry.getKey());
            }
        }

        this.headerLayout = headerLayout;
        this.childLayout = childLayout;


    }

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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(childLayout, null);
        }

        View tvView = convertView.findViewById(R.id.tv);
        if (tvView != null) {
            TextView tv = (TextView) tvView;
            String text = (String) getChild(groupPosition, childPosition);
            tv.setText(text);

            convertView.setTag(text);
        }

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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(headerLayout, null);
        }

        View tvView = convertView.findViewById(R.id.tv);
        if (tvView != null) {
            TextView tv = (TextView) tvView;
            String text = (String) getGroup(groupPosition);
            tv.setText(text);

            convertView.setTag(text);
        }

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
}

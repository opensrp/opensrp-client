package org.ei.opensrp.path.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;

import org.ei.opensrp.path.R;

import java.util.ArrayList;
import java.util.HashMap;

import util.JsonFormUtils;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 03/03/2017
 */
public class ServiceLocationsAdapter extends BaseAdapter {
    private String selectedLocation;
    private final ArrayList<String> locationNames;
    private final HashMap<String, View> views;
    private final Context context;

    public ServiceLocationsAdapter(Context context, ArrayList<String> locationNames, String selectedLocation) {
        this.context = context;
        if (locationNames == null) {
            locationNames = new ArrayList<>();
        }
        this.locationNames = locationNames;
        this.views = new HashMap<>();
    }

    @Override
    public int getCount() {
        return locationNames.size();
    }

    @Override
    public Object getItem(int position) {
        return views.get(locationNames.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position + 2321;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (!views.containsKey(locationNames.get(position))) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.location_picker_dropdown_item, null);
            view.setId(position + 2321);

            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
            text1.setText(JsonFormUtils.getOpenMrsReadableName(locationNames.get(position)));
            views.put(locationNames.get(position), view);
        }

        refreshView(views.get(locationNames.get(position)),
                locationNames.get(position).equals(selectedLocation));

        return views.get(locationNames.get(position));
    }

    public void setSelectedLocation(final String locationName) {
        selectedLocation = locationName;
        if (locationName != null
                && locationNames.contains(locationName)
                && views.containsKey(locationName)) {
            for (String curLocation : locationNames) {
                View view = views.get(curLocation);
                refreshView(view, curLocation.equals(locationName));
            }
        }
    }

    private void refreshView(View view, boolean selected) {
        if (selected) {
            //view.setBackgroundColor(context.getResources().getColor(R.color.primary_background));
            ImageView checkbox = (ImageView) view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.VISIBLE);
        } else {
            //view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            ImageView checkbox = (ImageView) view.findViewById(R.id.checkbox);
            checkbox.setVisibility(View.INVISIBLE);
        }
    }

    public String getLocationAt(int position) {
        return locationNames.get(position);
    }
}

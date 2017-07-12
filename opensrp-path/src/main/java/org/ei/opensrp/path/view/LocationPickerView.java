package org.ei.opensrp.path.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.adapter.ServiceLocationsAdapter;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import util.JsonFormUtils;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 03/03/2017
 */
public class LocationPickerView extends CustomFontTextView implements View.OnClickListener {
    private static final String TAG = "LocationPickerView";

    private final Context context;
    private org.ei.opensrp.Context openSrpContext;
    private Dialog locationPickerDialog;
    ServiceLocationsAdapter serviceLocationsAdapter;
    private OnLocationChangeListener onLocationChangeListener;

    private static final ArrayList<String> ALLOWED_LEVELS;
    private static final String DEFAULT_LOCATION_LEVEL = "Health Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add("Health Facility");
        ALLOWED_LEVELS.add("Zone");
    }

    public LocationPickerView(Context context) {
        super(context);
        this.context = context;
    }

    public LocationPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LocationPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void init(final org.ei.opensrp.Context openSrpContext) {
        this.openSrpContext = openSrpContext;
        locationPickerDialog = new Dialog(context);
        locationPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        locationPickerDialog.setContentView(R.layout.dialog_location_picker);

        ListView locationsLV = (ListView) locationPickerDialog.findViewById(R.id.locations_lv);
        serviceLocationsAdapter = new ServiceLocationsAdapter(context, getLocations(), getSelectedItem());
        locationsLV.setAdapter(serviceLocationsAdapter);
        locationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openSrpContext.allSharedPreferences().saveCurrentLocality(serviceLocationsAdapter
                        .getLocationAt(position));
                LocationPickerView.this.setText(JsonFormUtils.getOpenMrsReadableName(
                        serviceLocationsAdapter.getLocationAt(position)));
                if (onLocationChangeListener != null) {
                    onLocationChangeListener.onLocationChange(serviceLocationsAdapter
                            .getLocationAt(position));
                }
                locationPickerDialog.dismiss();
            }
        });
        this.setText(JsonFormUtils.getOpenMrsReadableName(getSelectedItem()));

        setClickable(true);
        setOnClickListener(this);
    }

    public String getSelectedItem() {
        String selectedLocation = openSrpContext.allSharedPreferences().fetchCurrentLocality();
        if (TextUtils.isEmpty(selectedLocation) || !getLocations().contains(selectedLocation)) {
            selectedLocation = getDefaultLocation();
        }
        return selectedLocation;
    }

    public void setOnLocationChangeListener(final OnLocationChangeListener onLocationChangeListener) {
        this.onLocationChangeListener = onLocationChangeListener;
    }

    private ArrayList<String> getLocations() {
        ArrayList<String> locations = new ArrayList<>();
        String defaultLocation = getDefaultLocation();
        try {
            JSONObject locationData = new JSONObject(openSrpContext.anmLocationController().get());
            if (locationData.has("locationsHierarchy")
                    && locationData.getJSONObject("loc" +
                    "ationsHierarchy").has("map")) {
                JSONObject map = locationData.getJSONObject("locationsHierarchy").getJSONObject("map");
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String curKey = keys.next();
                    extractLocations(locations, map.getJSONObject(curKey), defaultLocation);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        if (locations.contains(defaultLocation)) {
            locations.remove(defaultLocation);
        }
        Collections.sort(locations);
        locations.add(0, defaultLocation);

        return locations;
    }

    private void extractLocations(ArrayList<String> locationList, JSONObject rawLocationData,
                                  String defaultLocation)
            throws JSONException {
        String name = rawLocationData.getJSONObject("node").getString("name");
        String level = rawLocationData.getJSONObject("node").getJSONArray("tags").getString(0);

        if (ALLOWED_LEVELS.contains(level)) {
            if (level.equals(DEFAULT_LOCATION_LEVEL) && !name.equals(defaultLocation)) {
                return;
            }

            locationList.add(name);
        }

        if (rawLocationData.has("children")) {
            Iterator<String> childIterator = rawLocationData.getJSONObject("children").keys();
            while (childIterator.hasNext()) {
                String curChildKey = childIterator.next();
                extractLocations(locationList,
                        rawLocationData.getJSONObject("children").getJSONObject(curChildKey), defaultLocation);
            }
        }
    }

    private String getDefaultLocation() {
        JSONArray rawDefaultLocation = JsonFormUtils
                .generateDefaultLocationHierarchy(openSrpContext, ALLOWED_LEVELS);

        if (rawDefaultLocation != null && rawDefaultLocation.length() > 0) {
            try {
                return rawDefaultLocation.getString(rawDefaultLocation.length() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void onClick(View v) {
        showDialog();
    }

    private void showDialog() {
        serviceLocationsAdapter.setSelectedLocation(getSelectedItem());


        Window window = locationPickerDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        int[] coords = new int[2];
        LocationPickerView.this.getLocationInWindow(coords);
        wlp.x = coords[0]
                + (int) (LocationPickerView.this.getWidth() * 0.5)
                - (int) (convertDpToPx(780) * 0.5);

        locationPickerDialog.show();
    }

    public interface OnLocationChangeListener {
        void onLocationChange(String newLocation);
    }

    private int convertDpToPx(int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }
}

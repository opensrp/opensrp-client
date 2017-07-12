package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 23/02/2017.
 */

public class LocationActionView extends LinearLayout {
    private static final String TAG = "LocationActionView";
    private final Context context;
    private final org.ei.opensrp.Context openSrpContext;
    private LocationPickerView itemText;

    public LocationActionView(Context context, org.ei.opensrp.Context openSrpContext) {
        super(context);
        this.context = context;
        this.openSrpContext = openSrpContext;
        init();
    }

    public LocationActionView(Context context, org.ei.opensrp.Context openSrpContext, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.openSrpContext = openSrpContext;
        init();
    }

    public LocationActionView(Context context, org.ei.opensrp.Context openSrpContext, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.openSrpContext = openSrpContext;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LocationActionView(Context context, org.ei.opensrp.Context openSrpContext,
                              AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.openSrpContext = openSrpContext;
        init();
    }

    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.action_location_switcher, this, true);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        itemText = (LocationPickerView) findViewById(R.id.item_text);
        itemText.init(openSrpContext);
    }

    public String getSelectedItem() {
        return itemText.getSelectedItem();
    }

    public LocationPickerView getLocationPickerView() {
        return itemText;
    }
}

package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 27/02/2017.
 */

public class BaseDrawerActionView extends LinearLayout {
    private Context context;

    public BaseDrawerActionView(Context context) {
        super(context);
        init(context);
    }

    public BaseDrawerActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseDrawerActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseDrawerActionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.action_base_drawer, this, true);
    }
}

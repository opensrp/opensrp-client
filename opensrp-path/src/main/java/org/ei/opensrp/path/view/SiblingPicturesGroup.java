package org.ei.opensrp.path.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseActivity;
import org.ei.opensrp.path.adapter.SiblingPictureAdapter;

import java.util.ArrayList;

/**
 * Created by Jason Rogena - jrogena@ona.io on 09/05/2017.
 */

public class SiblingPicturesGroup extends LinearLayout {
    private Context context;
    private HorizontalGridView siblingsGV;
    private SiblingPictureAdapter siblingPictureAdapter;

    public SiblingPicturesGroup(Context context) {
        super(context);
        init(context);
    }

    public SiblingPicturesGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SiblingPicturesGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SiblingPicturesGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_sibling_group, this, true);
        siblingsGV = (HorizontalGridView) findViewById(R.id.siblings_gv);
        siblingsGV.setRowHeight(context.getResources().getDimensionPixelSize(R.dimen.sibling_profile_pic_height));
        //siblingsGV.setExpanded(false);
    }

    public void setSiblingBaseEntityIds(BaseActivity baseActivity, ArrayList<String> baseEntityIds) {
        siblingPictureAdapter = new SiblingPictureAdapter(baseActivity, baseEntityIds);
        siblingsGV.setAdapter(siblingPictureAdapter);
    }
}

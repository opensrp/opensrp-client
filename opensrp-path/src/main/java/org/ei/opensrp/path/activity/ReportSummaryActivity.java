package org.ei.opensrp.path.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.Tally;
import org.ei.opensrp.path.toolbar.SimpleToolbar;
import org.ei.opensrp.path.view.IndicatorCategoryView;
import org.ei.opensrp.view.customControls.CustomFontTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportSummaryActivity extends BaseActivity {
    private static final String TAG = ReportSummaryActivity.class.getName();
    public static final String EXTRA_TALLIES = "tallies";
    public static final String EXTRA_SUB_TITLE = "sub_title";
    public static final String EXTRA_TITLE = "title";
    private HashMap<String, ArrayList<Tally>> tallies;
    private String subTitle;
    private SimpleToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar = (SimpleToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.toolbar_grey)));

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable talliesSerializable = extras.getSerializable(EXTRA_TALLIES);
            if (talliesSerializable != null && talliesSerializable instanceof ArrayList) {
                ArrayList<Tally> tallies = (ArrayList<Tally>) talliesSerializable;
                setTallies(tallies, false);
            }

            Serializable submittedBySerializable = extras.getSerializable(EXTRA_SUB_TITLE);
            if (submittedBySerializable != null && submittedBySerializable instanceof String) {
                subTitle = (String) submittedBySerializable;
            }

            Serializable titleSerializable = extras.getSerializable(EXTRA_TITLE);
            if (titleSerializable != null && titleSerializable instanceof String) {
                toolbar.setTitle((String) titleSerializable);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomFontTextView submittedBy = (CustomFontTextView) findViewById(R.id.submitted_by);
        if (!TextUtils.isEmpty(this.subTitle)) {
            submittedBy.setVisibility(View.VISIBLE);
            submittedBy.setText(String.format(getString(R.string.submitted_by_), this.subTitle));
        } else {
            submittedBy.setVisibility(View.GONE);
        }
        refreshIndicatorViews();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_report_summary;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return SimpleToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return null;
    }

    public void setTallies(ArrayList<Tally> tallies) {
        setTallies(tallies, true);
    }

    private void setTallies(ArrayList<Tally> tallies, boolean refreshViews) {
        this.tallies = new HashMap<>();
        for (Tally curTally : tallies) {
            if (curTally != null && !TextUtils.isEmpty(curTally.getIndicator().getCategory())) {
                if (!this.tallies.containsKey(curTally.getIndicator().getCategory())
                        || this.tallies.get(curTally.getIndicator().getCategory()) == null) {
                    this.tallies.put(curTally.getIndicator().getCategory(), new ArrayList<Tally>());
                }

                this.tallies.get(curTally.getIndicator().getCategory()).add(curTally);
            }
        }

        if (refreshViews) refreshIndicatorViews();
    }

    private void refreshIndicatorViews() {
        LinearLayout indicatorCanvas = (LinearLayout) findViewById(R.id.indicator_canvas);
        indicatorCanvas.removeAllViews();

        if (tallies != null) {
            boolean firstExpanded = false;
            for (String curCategoryName : tallies.keySet()) {
                IndicatorCategoryView curCategoryView = new IndicatorCategoryView(this);
                curCategoryView.setTallies(curCategoryName, tallies.get(curCategoryName));
                indicatorCanvas.addView(curCategoryView);
                if (!firstExpanded) {
                    firstExpanded = true;
                    curCategoryView.setExpanded(true);
                }
            }
        }
    }

}

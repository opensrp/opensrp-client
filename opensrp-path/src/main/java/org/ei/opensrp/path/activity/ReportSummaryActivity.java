package org.ei.opensrp.path.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.Hia2Indicator;
import org.ei.opensrp.path.toolbar.SimpleToolbar;
import org.ei.opensrp.path.view.IndicatorCategoryView;
import org.ei.opensrp.view.customControls.CustomFontTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportSummaryActivity extends BaseActivity {
    private static final String TAG = ReportSummaryActivity.class.getName();
    public static final String EXTRA_INDICATORS = "indicators";
    public static final String EXTRA_SUB_TITLE = "sub_title";
    public static final String EXTRA_TITLE = "title";
    private HashMap<String, ArrayList<Hia2Indicator>> indicators;
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
            Serializable indicatorsSerializable = extras.getSerializable(EXTRA_INDICATORS);
            if (indicatorsSerializable != null && indicatorsSerializable instanceof ArrayList) {
                ArrayList<Hia2Indicator> indicators = (ArrayList<Hia2Indicator>) indicatorsSerializable;
                setIndicators(indicators, false);
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

    public void setIndicators(ArrayList<Hia2Indicator> hia2Indicators) {
        setIndicators(hia2Indicators, true);
    }

    private void setIndicators(ArrayList<Hia2Indicator> hia2Indicators, boolean refreshViews) {
        this.indicators = new HashMap<>();
        for (Hia2Indicator curHIA2Indicator : hia2Indicators) {
            if (curHIA2Indicator != null && !TextUtils.isEmpty(curHIA2Indicator.getCategory())) {
                if (!this.indicators.containsKey(curHIA2Indicator.getCategory())
                        || this.indicators.get(curHIA2Indicator.getCategory()) == null) {
                    this.indicators.put(curHIA2Indicator.getCategory(), new ArrayList<Hia2Indicator>());
                }

                this.indicators.get(curHIA2Indicator.getCategory()).add(curHIA2Indicator);
            }
        }

        if (refreshViews) refreshIndicatorViews();
    }

    private void refreshIndicatorViews() {
        LinearLayout indicatorCanvas = (LinearLayout) findViewById(R.id.indicator_canvas);
        indicatorCanvas.removeAllViews();

        if (indicators != null) {
            boolean firstExpanded = false;
            for (String curCategoryName : indicators.keySet()) {
                IndicatorCategoryView curCategoryView = new IndicatorCategoryView(this);
                curCategoryView.setIndicators(curCategoryName, indicators.get(curCategoryName));
                indicatorCanvas.addView(curCategoryView);
                if (!firstExpanded) {
                    firstExpanded = true;
                    curCategoryView.setExpanded(true);
                }
            }
        }
    }

}

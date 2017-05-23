package org.ei.opensrp.path.activity;

import android.os.Bundle;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;

/**
 * Created by raihan on 5/23/17.
 */
public class StockActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
    }

    @Override
    protected int getContentView() {
        return  R.layout.activity_stock;
    }

    @Override
    protected int getDrawerLayoutId() {
        return  R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return ChildSmartRegisterActivity.class;
    }
}

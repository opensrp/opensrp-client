package org.ei.opensrp.path.toolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MenuItem;
import org.ei.opensrp.path.R;

/**
 * Created by Jason Rogena - jrogena@ona.io on 30/03/2017.
 */

public class ChildDetailsToolbar extends BaseToolbar {
    public ChildDetailsToolbar(Context context) {
        super(context);
    }

    public ChildDetailsToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildDetailsToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getSupportedMenu() {
        return R.menu.menu_child_detail_settings;
    }

    @Override
    public void prepareMenu() {

    }

    @Override
    public MenuItem onMenuItemSelected(MenuItem menuItem) {
        return null;
    }
}

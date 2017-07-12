package org.ei.opensrp.path.toolbar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;

/**
 * Created by Jason Rogena - jrogena@ona.io on 17/02/2017.
 */

public abstract class BaseToolbar extends Toolbar {
    public BaseToolbar(Context context) {
        super(context);
    }

    public BaseToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract int getSupportedMenu();

    public abstract void prepareMenu();

    public abstract MenuItem onMenuItemSelected(MenuItem menuItem);
}

package org.ei.opensrp.path.toolbar;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseActivity;
import org.ei.opensrp.view.customControls.CustomFontTextView;

/**
 * Created by Jason Rogena - jrogena@ona.io on 12/06/2017.
 */

public class SimpleToolbar extends BaseToolbar {
    private static final String TAG = SimpleToolbar.class.getName();
    public static final int TOOLBAR_ID = R.id.simple_toolbar;
    private final Context context;
    private String title;

    public SimpleToolbar(Context context) {
        super(context);
        this.context = context;
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SimpleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setTitle(String title) {
        this.title = title;
        refreshTitleView();
    }

    @Override
    public int getSupportedMenu() {
        return 0;
    }

    @Override
    public void prepareMenu() {
        refreshTitleView();
    }

    private void refreshTitleView() {
        try {

            CustomFontTextView titleTV = (CustomFontTextView) findViewById(R.id.title);
            titleTV.setText(title);
        } catch (Exception e) {
            Log.e(TAG, "***************************************");
            Log.e(TAG, Log.getStackTraceString(e));
            Log.e(TAG, "***************************************");
        }
    }

    @Override
    public MenuItem onMenuItemSelected(MenuItem menuItem) {
        return null;
    }
}

package org.ei.opensrp.core.db.handler;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import org.ei.opensrp.core.db.repository.RegisterContentProvider;
import org.ei.opensrp.core.template.HomeActivity;

/**
 * Created by Maimoona on 1/6/2017.
 */
public class RegisterCountLoaderHandler implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final HomeActivity.RegisterCountView register;
    private final Context context;

    public RegisterCountLoaderHandler(Context context, HomeActivity.RegisterCountView register) {
        this.register = register;
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        register.getContainerView().setText("0000");
        Uri uri = RegisterContentProvider.CONTENT_COUNT_URI(register.getTable());
        Log.v(getClass().getName(), "Loading count from "+uri.getPath());
        return new CursorLoader(context, null, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(register != null && data != null) {
            int c = data.getInt(0);
            Log.v(getClass().getName(), "Loaded count "+c+" for "+register.getTable());
            register.overrideCount(c);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

package org.ei.opensrp.core.db.handler;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.BaseAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.db.repository.RegisterContentProvider;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.db.utils.RegisterQuery;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.cursoradapter.CursorFilterOption;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegisterDataCursorLoaderHandler implements RegisterDataLoaderHandler <Cursor> {
    private RegisterQuery registerQuery;
    private Context context;
    private RegisterCursorAdapter adapter;
    private Integer totalRecords;
    private boolean fullyLoaded;
    private LoadListener loadListener;

    public RegisterDataCursorLoaderHandler(Context context, RegisterQuery registerQuery, RegisterCursorAdapter adapter) {
        this.context = context;
        this.registerQuery = registerQuery;
        this.adapter = adapter;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        if (loadListener != null){
            loadListener.before();
        }
        fullyLoaded = false;

        String village = null;
        String search = null;
        String sort = null;
        Integer offset = bundle==null?null:bundle.getInt("offset");
        Integer pageSize = bundle==null?null:bundle.getInt("pageSize");
        if (bundle != null && StringUtils.isNotBlank(bundle.getString("params")))
        try {
            JSONObject params = new JSONObject(bundle.getString("params"));
            Log.v(getClass().getName(), "PARAMS WERE : "+params);

            if(params.has("village")) {
                village = params.getString("village");
            }
            if(params.has("search")) {
                search = params.getString("search");
            }
            if (params.has("sort")) {
                sort = params.getString("sort");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        registerQuery.resetCondition();
        registerQuery.resetOrder();

        if (village != null) registerQuery.addCondition(village);

        if (search != null) registerQuery.addCondition(search);

        if (sort != null) registerQuery.addOrder(sort);

        if (offset != null) registerQuery.resetOffset(offset);

        if (pageSize != null && pageSize > 0) registerQuery.resetLimit(pageSize);

        totalRecords = RegisterRepository.count(registerQuery.table(), registerQuery.selection(), null);

        Log.v(getClass().getName(), "Fetching a repo of "+totalRecords+" records");

        Uri uri = RegisterContentProvider.CONTENT_URI(registerQuery.table());
        if (StringUtils.isNotBlank(registerQuery.referenceTable())){
            uri = RegisterContentProvider.CONTENT_JOIN_URI(registerQuery.table(), registerQuery.idColumn(), registerQuery.referenceTable(), registerQuery.referenceColumn(), registerQuery.group());
        }

        return new CursorLoader(context, uri,
            registerQuery.makeProjection(true), registerQuery.selection(), null, registerQuery.order(true));
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        Log.v(getClass().getName(), "Cursor would populate the data now");
        cursor.moveToFirst();
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        adapter.swapCursor(cursor);

        if (loadListener != null){
            loadListener.after();
        }
        fullyLoaded = true;
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        adapter.swapCursor(null);
    }

    @Override
    public Pagination pager() {
        return new Pagination() {
            @Override
            public Integer totalCount() {
                return totalRecords;
            }

            @Override
            public Integer pageSize() {
                return registerQuery.pageSize();
            }

            @Override
            public Integer currentOffset() {
                return registerQuery.offset();
            }
        };
    }

    @Override
    public boolean fullyLoaded() {
        return fullyLoaded;
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    @Override
    public List currentPageList() {
        return adapter.getCurrentPage();
    }

    @Override
    public BaseAdapter adapter() {
        return adapter;
    }
}
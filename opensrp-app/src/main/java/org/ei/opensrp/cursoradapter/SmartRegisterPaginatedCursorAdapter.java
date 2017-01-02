package org.ei.opensrp.cursoradapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.repository.db.CESQLiteHelper;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.HashMap;

public class SmartRegisterPaginatedCursorAdapter extends CursorAdapter implements SmartRegisterPaginatedAdapter{
    private final SmartRegisterClientsProvider listItemProvider;
    private final String selection;
    Context context;
    CommonRepository commonRepository;
    String table;
    String mainFilter;
    SmartRegisterQueryBuilder lastQuery;
    SmartRegisterClients clients;
    CESQLiteHelper ceDB;
    SmartRegisterCursorBuilder.DB db;

    public SmartRegisterPaginatedCursorAdapter(Context context, SmartRegisterCursorBuilder cursorBuilder, SmartRegisterClientsProvider listItemProvider, SmartRegisterCursorBuilder.DB db) {
        super(context, cursorBuilder.buildCursor(db), false);
        this.db = db;
        this.listItemProvider = listItemProvider;
        this.context= context;
        this.table = cursorBuilder.query().table();
        this.mainFilter = cursorBuilder.query().mainFilter();
        this.selection = cursorBuilder.query().selection();
        lastQuery = cursorBuilder.query();
        if(db == null || db.equals(SmartRegisterCursorBuilder.DB.DRISHTI)){
            this.commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(table);
        } else
            this.ceDB = org.ei.opensrp.Context.getInstance().ceDB();

        refreshTotalCount();;
    }

    @Override
    public void notifyDataSetInvalidated() {
        Log.i(getClass().getName(), "Invalidating dataset and closing cursors");
        super.notifyDataSetInvalidated();
        //todo if (getCursor() != null && !getCursor().isClosed()) {
        //    getCursor().close();
        //}

        SmartRegisterCursorBuilder.closeCursor();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        clients = new SmartRegisterClients();
        return  listItemProvider.inflateLayoutForAdapter();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(getClass().getName(), "Creating view from cursor");

        CommonPersonObjectClient pClient = new CommonPersonObjectClient("", new HashMap<String, String>(), "");
        pClient.setColumnmaps(new HashMap<String, String>());
        if(getCursor() != null && getCursor().isClosed()==false) {
            CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
            pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
            pClient.setColumnmaps(personinlist.getColumnmaps());
            clients.add(pClient);
            listItemProvider.getView(pClient, view, null/*todo*/);
        }
        listItemProvider.getView(pClient, view, null/*todo*/);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO: WARNING .. IT SHOULD BE CHANGED, IT WAS DONE FOR TRANSITIONING FROM HH REGISTER TO OTHERS
        if (getCursor().isClosed()) {
            return null;
        }
        if (!getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        View v;
        if (convertView == null) {
            v = newView(context, getCursor(), parent);
        } else {
            v = convertView;
        }
        bindView(v, context, getCursor());
        return v;
    }

    public void swapCursorWithNew(Cursor newCursor) {
        Cursor c = super.swapCursor(newCursor);
        if (c != null && !c.isClosed()) c.close();
    }


    private int totalcount = 0;
    public int limitPerPage(){return lastQuery.pageSize();}
    public int currentoffset(){return lastQuery.offset();}

    public int getTotalCount(){
        return totalcount;
    }

    @Override
    public int getCount() {
        if(totalcount < currentoffset()+limitPerPage()){
            return totalcount - currentoffset();
        }
        return limitPerPage();
    }

    public int pageCount() {
        if(totalcount == 0){
            return 1;
        }
        return (int) Math.ceil(1.0*totalcount/limitPerPage());
    }

    public int currentPage() {
        if(currentoffset() != 0) {
            return pageCount()-(int)Math.floor((totalcount-currentoffset())/(1.0*limitPerPage()));
        }else{
            return 1;
        }
    }

    public boolean hasNextPage() {
        return ((totalcount>(currentoffset()+limitPerPage())));
    }

    public boolean hasPreviousPage() {
        return currentoffset()!=0;
    }

    public void gotoNextPage() {
        if(hasNextPage()){
            lastQuery.limitAndOffset(limitPerPage(), currentoffset()+limitPerPage());
            filterandSortExecute();
            notifyDataSetChanged();
        }
    }

    public void goBackToPreviousPage() {
        if(hasPreviousPage()){
            lastQuery.limitAndOffset(limitPerPage(), currentoffset()-limitPerPage());
            filterandSortExecute();
            notifyDataSetChanged();
        }
    }

    public void refreshList(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                            SearchFilterOption searchFilter, SortOption sortOption) {
        filterandSortExecute(villageFilter==null?null:((CursorFilterOption)villageFilter).filter(), searchFilter==null||StringUtils.isBlank(searchFilter.getFilter())?null:searchFilter.getCriteria(),
                sortOption==null?null:((CursorSortOption)sortOption).sort());
        notifyDataSetChanged();
    }

    @Override
    public SmartRegisterClients currentPageList() {
        SmartRegisterClients c = new SmartRegisterClients();
        boolean hasFirst = getCursor().moveToFirst();
        do {
            if (hasFirst) {
                CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(getCursor());
                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
                pClient.setColumnmaps(personinlist.getColumnmaps());
                c.add(pClient);
            }
        }
        while (getCursor().moveToNext());

        getCursor().moveToFirst();
        return c;
    }

    @Override
    public SmartRegisterClientsProvider getListItemProvider() {
        return listItemProvider;
    }

    public void filterandSortExecute(String vilageFilter, String searchFilter, String sort) {
//todo        refresh();
        int pageSize = limitPerPage();

        lastQuery = new SmartRegisterQueryBuilder(table, mainFilter, db == null || db.equals(SmartRegisterCursorBuilder.DB.DRISHTI)?null:"baseEntityId");
        lastQuery.overrideSelection(selection);
        if (StringUtils.isNotBlank(vilageFilter)){
            lastQuery.addCondition(vilageFilter);
        }
        if (StringUtils.isNotBlank(searchFilter)){
            lastQuery.addCondition(searchFilter);
        }
        if(StringUtils.isNotBlank(sort)){
            lastQuery.addOrder(sort);
        }
        lastQuery.limitAndOffset(pageSize, currentoffset());

        filterandSortExecute();
    }

    public void filterandSortExecute() {
        refreshTotalCount();

        clients = new SmartRegisterClients();

        String query = lastQuery.toString();
        Log.d(getClass().getName(), query);

        Cursor c = buildCursor(query);
        swapCursorWithNew(c);
        Log.i(getClass().getName(), "Swaped new cursor");
    }

    public int refreshTotalCount(){
        Cursor c = buildCursor(lastQuery.countQuery());
        c.moveToFirst();
        totalcount= c.getInt(0);
        c.close();

        Log.i(getClass().getName(), "Loaded count "+totalcount);

        return  totalcount;
    }

    private Cursor buildCursor(String query){
        Cursor cursor = null;
        if(db == null || db.equals(SmartRegisterCursorBuilder.DB.DRISHTI)){
            cursor = commonRepository.RawCustomQueryForAdapter(query);
        } else {
            cursor = ceDB.rawQueryForCursor(query);
        }
        return cursor;
    }

}

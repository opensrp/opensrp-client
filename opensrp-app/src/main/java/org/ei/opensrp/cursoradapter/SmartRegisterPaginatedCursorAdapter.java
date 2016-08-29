package org.ei.opensrp.cursoradapter;

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
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.Arrays;
import java.util.List;

public class SmartRegisterPaginatedCursorAdapter extends CursorAdapter implements SmartRegisterPaginatedAdapter{
    private final SmartRegisterClientsProvider listItemProvider;
    private static final int PAGE_SIZE = 20;
    Context context;
    CommonRepository commonRepository;
    String table;
    String mainFilter;
    SmartRegisterQueryBuilder lastQuery;
    SmartRegisterClients clients;


    public SmartRegisterPaginatedCursorAdapter(Context context, SmartRegisterCursorBuilder cursorBuilder, SmartRegisterClientsProvider listItemProvider) {
        super(context, cursorBuilder.buildCursor(), false);
        this.listItemProvider = listItemProvider;
        this.context= context;
        this.table = cursorBuilder.query().table();
        this.mainFilter = cursorBuilder.query().mainFilter();
        lastQuery = cursorBuilder.query();
        this.commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(table);
    }

    @Override
    public void notifyDataSetInvalidated() {
        try {
            Log.i(getClass().getName(), "Invalidating dataset and closing cursors");
            super.notifyDataSetInvalidated();
            if (getCursor() != null && !getCursor().isClosed()) {
                getCursor().close();
            }

            SmartRegisterCursorBuilder.closeCursor();
        }catch (Exception e){
            Log.e(getClass().getName(), "Error in notifyDataSetInvalidated()", e);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        clients = new SmartRegisterClients();
        return  listItemProvider.inflateLayoutForAdapter();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
        pClient.setColumnmaps(personinlist.getColumnmaps());
        clients.add(pClient);
        listItemProvider.getView(pClient, view, null/*todo*/);
    }

    public void swapCursorWithNew(Cursor newCursor) {
        Cursor c = super.swapCursor(newCursor);
        if (c != null && !c.isClosed()) c.close();
    }


    private Integer totalcount = null;
    public int limitPerPage(){return lastQuery.pageSize();}
    public int currentoffset(){return lastQuery.offset();}

    public int getTotalCount(){
        return totalcount;
    }

    @Override
    public int getCount() {
        if(totalcount == null) {
            refreshTotalCount();
        }
        if(totalcount < currentoffset()+limitPerPage()){
            return totalcount - currentoffset();
        }
        return limitPerPage();
    }

    public int pageCount() {
        if(totalcount <= limitPerPage()){
            return 1;
        }
        return (int) Math.round(1.0*totalcount/limitPerPage());
    }

    public int currentPage() {
        int currentPage = 0;
        if(currentoffset() != 0) {
            currentPage =  (int)Math.round(pageCount()-((totalcount-currentoffset())/(1.0*limitPerPage())));
        }
        return currentPage + 1;
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
        filterandSortExecute(villageFilter==null?null:((CursorFilterOption)villageFilter).filter(), searchFilter==null?null:searchFilter.getCriteria(),
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

        // TODO Include village filter in fts, currently no village filters available

        if(commonRepository.isFts() && searchFilter != null && sort != null && (!searchFilter.contains("LIKE") || !searchFilter.contains("="))){
            // FTS way
            lastQuery = new SmartRegisterQueryBuilder(table, mainFilter);
            lastQuery.setIsFTS(true);
            lastQuery.setFtsSearchFilter(searchFilter);
            lastQuery.setFtsSort(sort);
        }else {
            // old way
            lastQuery = new SmartRegisterQueryBuilder(table, mainFilter);
            if (StringUtils.isNotBlank(vilageFilter)){
                lastQuery.addCondition(vilageFilter);
            }

            if (StringUtils.isNotBlank(vilageFilter)) {
                lastQuery.addCondition(searchFilter);
            }

            if (StringUtils.isNotBlank(sort)) {
                lastQuery.addOrder(sort);
            }
        }
        lastQuery.limitAndOffset(limitPerPage(), currentoffset());

        filterandSortExecute();
    }

    public void filterandSortExecute() {
        refreshTotalCount();
        clients = new SmartRegisterClients();

        String query;
        if(lastQuery.isFTS()) {
            String sql = lastQuery.searchQueryFts();
            List<String> ids = commonRepository.findSearchIds(sql);
            query = lastQuery.toStringFts(ids, CommonRepository.ID_COLUMN);
        } else {
            query = lastQuery.toString();
        }
        Log.i(getClass().getName(), query);

        Cursor c = commonRepository.RawCustomQueryForAdapter(query);
        if(c != null) {
            swapCursorWithNew(c);
        }
    }

    public int refreshTotalCount(){
        String countQuery = lastQuery.countQuery();
        if(lastQuery.isFTS()){
            countQuery = lastQuery.countQueryFts();
        }
        Log.i(getClass().getName(), countQuery);
        Cursor c = commonRepository.RawCustomQueryForAdapter(countQuery);
        if(c != null && (c.getCount() > 0)) {
            c.moveToFirst();
            totalcount = c.getInt(0);
        }else{
            totalcount = 0;
        }
        c.close();
        return  totalcount;
    }

}

package org.ei.opensrp.core.db.repository;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Maimoona on 1/5/2017.
 */
public class RegisterContentProvider extends ContentProvider {
    // used for the UriMacher
    private static final int ALL = 1;
    private static final int ID = 2;
    private static final int COUNT = 3;
    private static final int JOIN = 4;

    public static final String AUTHORITY = "org.ei.opensrp.provider.registers";

    public static final Uri CONTENT_URI(String table){
        return Uri.parse("content://" + AUTHORITY + "/"+table);
    }

    public static final Uri CONTENT_ID_URI(String table, String idColumn, String id){
        return Uri.parse("content://" + AUTHORITY + "/"+table+"/"+idColumn+"/"+id);
    }

    public static final Uri CONTENT_COUNT_URI(String table){
        return Uri.parse("content://" + AUTHORITY + "/count/"+table);
    }

    public static final Uri CONTENT_JOIN_URI(String table, String id, String referenceTable, String referenceColumn, String groupBy){
        return Uri.parse("content://" + AUTHORITY + "/"+table+"/"+id+"/join/"+referenceTable+"/"+referenceColumn+"?group="+groupBy);
    }

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // Request register table for all data; table name
        sURIMatcher.addURI(AUTHORITY, "/*", ALL);
        //Request regster table for join; join flag, table name, id, reference table, reference column
        sURIMatcher.addURI(AUTHORITY, "/*/*/join/*/*", JOIN);
        // Request register table for ID; table name, id column, id
        sURIMatcher.addURI(AUTHORITY, "/*/*/*", ID);
        //Request regster table for count; table name, count flag
        sURIMatcher.addURI(AUTHORITY, "/count/*", COUNT);
    }

    private String getTable(Uri uri){
        return uri.getPathSegments().get(0);
    }
    private String getIdFilter(Uri uri, String currentFilter){
        return StringUtils.isBlank(currentFilter)?
                (uri.getPathSegments().get(1)+" = "+uri.getPathSegments().get(2)):
                (" AND "+uri.getPathSegments().get(1)+" = "+uri.getPathSegments().get(2)+" ");
    }

    private String getId(Uri uri){
        return uri.getPathSegments().get(1);
    }

    private String getReferenceTable(Uri uri){
        return uri.getPathSegments().get(3);
    }

    private String getReferenceColumn(Uri uri){
        return uri.getPathSegments().get(4);
    }

    private String getGroup(Uri uri){
        return uri.getQueryParameter("group");
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table =  getTable(uri);
        Cursor c = null;
        Log.v(getClass().getName(), "Register Content Provider reached for "+table+" with type"+sURIMatcher.match(uri));
        Log.v(getClass().getName(), "with query "+uri.getQuery()+" "+uri.getPath()+" "+uri.getAuthority());
        switch (sURIMatcher.match(uri)){
            case ALL:
                c = RegisterRepository.query(table, projection, selection, selectionArgs, sortOrder);
            break;
            case ID:
                c = RegisterRepository.query(table, projection,
                        getIdFilter(uri, selection), selectionArgs, sortOrder);
            break;
            case COUNT:
                c = RegisterRepository.query(table, new String[]{"COUNT(1) c"}, selection, selectionArgs, sortOrder);
            break;
            case JOIN:
                c = RegisterRepository.queryLeftJoin(table, getId(uri), getReferenceTable(uri), getReferenceColumn(uri), projection, selection, getGroup(uri), sortOrder);
            break;
        }
        // make sure that potential listeners are getting notified
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

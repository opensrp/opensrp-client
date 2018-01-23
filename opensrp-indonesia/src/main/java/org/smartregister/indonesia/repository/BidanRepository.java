package org.smartregister.indonesia.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.indonesia.AllConstantsINA;
import org.smartregister.indonesia.application.BidanApplication;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sid-tech on 11/22/17.
 */

public class BidanRepository extends Repository {

    private static final String TAG = BidanRepository.class.getCanonicalName();
    private final Context context;
    private SQLiteDatabase readableDatabase;
    private SQLiteDatabase writableDatabase;

    public BidanRepository(Context context, org.smartregister.Context opensrpContext) {
        super(context, AllConstantsINA.DATABASE_NAME, AllConstantsINA.DATABASE_VERSION, opensrpContext.session(), BidanApplication.createCommonFtsObject(), opensrpContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.address, EventClientRepository.address_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
//        EventClientRepository.createTable(database, EventClientRepository.Table.obs, EventClientRepository.obs_column.values());
        onUpgrade(database, 1, AllConstantsINA.DATABASE_VERSION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BidanRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(BidanApplication.getInstance().getPassword());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(BidanApplication.getInstance().getPassword());
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

    /**
     * Version 2 added some columns to the ec_child table
     *
     * @param database
     */
    private void upgradeToVersion2(SQLiteDatabase database) {
        try {
            // Run insert query
            ArrayList<String> newlyAddedFields = new ArrayList<>();
            newlyAddedFields.add("BCG_2");
            newlyAddedFields.add("inactive");
            newlyAddedFields.add("lost_to_follow_up");

//            addFieldsToFTSTable(database, AllConstantsINA.CHILD_TABLE_NAME, newlyAddedFields);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2 " + Log.getStackTraceString(e));
        }
    }


    private void addFieldsToFTSTable(SQLiteDatabase database, String originalTableName, List<String> newlyAddedFields) {

        // Create the new ec_child table

        String newTableNameSuffix = "_v2";

        Set<String> searchColumns = new LinkedHashSet<>();
        searchColumns.add(CommonFtsObject.idColumn);
        searchColumns.add(CommonFtsObject.relationalIdColumn);
        searchColumns.add(CommonFtsObject.phraseColumn);
        searchColumns.add(CommonFtsObject.isClosedColumn);

        String[] mainConditions = this.commonFtsObject.getMainConditions(originalTableName);
        if (mainConditions != null)
            for (String mainCondition : mainConditions) {
                if (!mainCondition.equals(CommonFtsObject.isClosedColumnName))
                    searchColumns.add(mainCondition);
            }

        String[] sortFields = this.commonFtsObject.getSortFields(originalTableName);
        if (sortFields != null) {
            for (String sortValue : sortFields) {
                if (sortValue.startsWith("alerts.")) {
                    sortValue = sortValue.split("\\.")[1];
                }
                searchColumns.add(sortValue);
            }
        }

        String joinedSearchColumns = StringUtils.join(searchColumns, ",");

        String searchSql = "create virtual table "
                + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                + " using fts4 (" + joinedSearchColumns + ");";
        Log.d(TAG, "Create query is\n---------------------------\n" + searchSql);

        database.execSQL(searchSql);

        ArrayList<String> oldFields = new ArrayList<>();

        for (String curColumn : searchColumns) {
            curColumn = curColumn.trim();
            if (curColumn.contains(" ")) {
                String[] curColumnParts = curColumn.split(" ");
                curColumn = curColumnParts[0];
            }

            if (!newlyAddedFields.contains(curColumn)) {
                oldFields.add(curColumn);
            } else {
                Log.d(TAG, "Skipping field " + curColumn + " from the select query");
            }
        }

        String insertQuery = "insert into "
                + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                + " (" + StringUtils.join(oldFields, ", ") + ")"
                + " select " + StringUtils.join(oldFields, ", ") + " from "
                + CommonFtsObject.searchTableName(originalTableName);

        Log.d(TAG, "Insert query is\n---------------------------\n" + insertQuery);
        database.execSQL(insertQuery);

        // Run the drop query
        String dropQuery = "drop table " + CommonFtsObject.searchTableName(originalTableName);
        Log.d(TAG, "Drop query is\n---------------------------\n" + dropQuery);
        database.execSQL(dropQuery);

        // Run rename query
        String renameQuery = "alter table "
                + CommonFtsObject.searchTableName(originalTableName) + newTableNameSuffix
                + " rename to " + CommonFtsObject.searchTableName(originalTableName);
        Log.d(TAG, "Rename query is\n---------------------------\n" + renameQuery);
        database.execSQL(renameQuery);

    }


}


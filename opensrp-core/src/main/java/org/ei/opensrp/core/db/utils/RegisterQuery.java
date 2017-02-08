package org.ei.opensrp.core.db.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maimoona on 1/5/2017.
 */
public class RegisterQuery {
    private List<String> additionalColumns;
    private String idColumn;
    private String mainFilter;
    private String selection;
    private String table;
    private String referenceTable;
    private String referenceColumn;
    private String group;

    public String referenceTable() {
        return referenceTable;
    }

    public String referenceColumn() {
        return referenceColumn;
    }

    public String group() {
        return group;
    }

    private String order;
    private int pageSize = 20;
    private int offset = 0;

    public RegisterQuery(String table, String idColumn, List<String> additionalColumns, String mainFilter) {
        this.additionalColumns = additionalColumns;
        this.idColumn = idColumn;
        this.mainFilter = mainFilter;
        this.table = table;
    }

    public RegisterQuery(String table, String idColumn, String referenceTable, String referenceColumn, String group,
            List<String> additionalColumns, String mainFilter) {
        this.additionalColumns = additionalColumns;
        this.idColumn = idColumn;
        this.mainFilter = mainFilter;
        this.table = table;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
        this.group = group;
    }

    public String mainFilter(){
        return mainFilter;
    }

    public String table() {
        return table;
    }

    public String idColumn(){
        return idColumn;
    }

    public String selection(){
        String filter = "";
        if (StringUtils.isNotBlank(mainFilter)){// if main filter not blank append AND
            filter += mainFilter;

            if (StringUtils.isNotBlank(selection)){
                filter += " AND "+selection;
            }
        }
        else if (StringUtils.isNotBlank(selection)){ // main filter is blank do it without AND
            filter += selection;
        }

        return filter;
    }

    public String order(boolean addLimitClause) {
        if (addLimitClause){
            if (StringUtils.isBlank(order)){
                order = idColumn;
            }

            order += " LIMIT "+offset+" , "+pageSize;
        }

        return order;
    }

    public int pageSize() {
        return pageSize;
    }

    public int offset() {
        return offset;
    }

    public List<String> additionalColumns(){
        return additionalColumns;
    }

    public String[] makeProjection(boolean autoAppendIdColumn){
        // append * to query to make sure ALL columns are selected
        // then it should add alias with asterik so that query becomes "select *, added_column ...."
        // it should also add _id column for adapters
        List<String> projection = new ArrayList<>();
        projection.add(table+".*");
        if (autoAppendIdColumn && StringUtils.isNotBlank(idColumn)){
            projection.add(table+"."+idColumn+" AS _id");
        }

        if (additionalColumns != null && additionalColumns.size() > 0){
            for (String adc : additionalColumns) {
                projection.add(adc);
            }
        }

        return projection==null?null:projection.toArray(new String[]{});
    }

    public RegisterQuery additionalColumn(String column){
        if (additionalColumns == null){
            additionalColumns = new ArrayList<>();
        }
        additionalColumns.add(column);
        return this;
    }

    public RegisterQuery addCondition(String condition){
        if (StringUtils.isBlank(selection)){
            selection = "";
        }
        if (StringUtils.isNotBlank(selection) && !condition.toLowerCase().trim().startsWith("and")){
            selection += " AND ";
        }
        selection += " "+condition ;
        return this;
    }

    public RegisterQuery addOrder(String orderCondition){
        if (StringUtils.isNotBlank(order)){
            this.order += ", "+orderCondition;
        }
        else {
            this.order = orderCondition;
        }
        return this;
    }

    public void resetCondition(){
        this.selection = "";
    }

    public void resetOrder(){
        this.order = "";
    }

    public RegisterQuery limitAndOffset(int limit, int offset){
        this.pageSize = limit;
        this.offset = offset;
        return this;
    }

    public void resetLimit(int limit){
        this.pageSize = limit;
    }

    public void resetOffset(int offset){
        this.offset = offset;
    }

    public String countQuery(){
        String res = "SELECT COUNT(1) FROM "+table;
        res += " WHERE "+selection;

        return res;
    }
}

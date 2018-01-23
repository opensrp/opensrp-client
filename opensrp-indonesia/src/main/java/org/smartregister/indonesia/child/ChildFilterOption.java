package org.smartregister.indonesia.child;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Iq on 06/02/17.
 */
public class ChildFilterOption implements CursorFilterOption {
    private final String criteria;
    private final String fieldname;
    private final String filterOptionName;
    private final String tablename;

    @Override
    public String filter() {
        if(StringUtils.isNotBlank(fieldname) && !fieldname.equals("location_name")){
            return  " AND " + tablename+ ".base_entity_id IN ( SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH '"+fieldname+"' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
          //  Log.v("fieldoverride", "");
        } else{
            return  " AND " + tablename+ ".base_entity_id IN ( SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
        }
    }

    public ChildFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.filterOptionName = filteroptionname;
        this.tablename = tablename;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return false;
    }
}

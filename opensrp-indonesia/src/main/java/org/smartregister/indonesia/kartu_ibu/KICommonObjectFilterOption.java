package org.smartregister.indonesia.kartu_ibu;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.cursoradapter.CursorFilterOption;
import org.smartregister.view.contract.SmartRegisterClient;

public class KICommonObjectFilterOption implements CursorFilterOption {
    public final String criteria;
    public final String fieldname;
    private final String filterOptionName;
    private final String tablename;

    @Override
    public String filter() {
        if(StringUtils.isNotBlank(fieldname) && !fieldname.equals("location_name")){
            return  " AND " + tablename+ ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH '"+fieldname+"' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
        } else{
            return  " AND " + tablename+ ".base_entity_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
        }
    }



    public KICommonObjectFilterOption(String criteria, String fieldname,String filteroptionname,String tablename) {
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

package org.ei.opensrp.mcare.elco;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorFilterOption;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

import static org.ei.opensrp.util.StringUtil.humanize;

public class ElcoMauzaCommonObjectFilterOption implements CursorFilterOption {
    private final String criteria;
    private final String fieldname;
    private final String filterOptionName;
    private final String tablename;

    @Override
    public String filter() {
        if(StringUtils.isNotBlank(fieldname) && !fieldname.equals("location_name")){
            return  " AND " + tablename+ ".relational_id IN ( SELECT DISTINCT base_entity_id FROM ec_details WHERE key MATCH '"+fieldname+"' INTERSECT SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
        } else{
            return  " AND " + tablename+ ".relational_id IN ( SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"' ) ";
        }
    }

    public ElcoMauzaCommonObjectFilterOption(String criteria, String fieldname, String filteroptionname, String tablename) {
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

package org.ei.opensrp.gizi.gizi;

import org.ei.opensrp.cursoradapter.CursorFilterOption;
import org.ei.opensrp.view.contract.SmartRegisterClient;

public class KICommonObjectFilterOption implements CursorFilterOption {
    public final String criteria;
    public final String fieldname;
    private final String filterOptionName;

    @Override
    public String filter() {


        return " and ec_anak.relational_id IN (SELECT DISTINCT base_entity_id FROM ec_details WHERE value MATCH '"+criteria+"')";

    }



    public KICommonObjectFilterOption(String criteria, String fieldname, String filteroptionname) {
        this.criteria = criteria;
        this.fieldname = fieldname;
        this.filterOptionName = filteroptionname;
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

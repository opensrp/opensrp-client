package org.ei.opensrp.view.dialog;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;

public class ECSearchOption implements SearchFilterOption {
    private String filter;


    public ECSearchOption(String filter) {
        this.filter = filter;
    }

    public String getFilter(){
        return filter;
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.str_ec_search_hint);
    }

    @Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String getCriteria() {
        return filter;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return StringUtils.isBlank(filter) || client.satisfiesFilter(filter);
    }
}

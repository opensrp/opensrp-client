package org.ei.opensrp.view.dialog;

import org.ei.opensrp.view.contract.SmartRegisterClient;

public interface SearchFilterOption extends DialogOption {
    String getFilter();
    void setFilter(String filter);
    String getCriteria();
    boolean filter(SmartRegisterClient client);
}

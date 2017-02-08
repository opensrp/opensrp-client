package org.ei.opensrp.core.template;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.DialogOption;

public interface SearchFilterOption extends DialogOption {
    String getFilter();
    void setFilter(String filter);
    String getCriteria();
    boolean filter(SmartRegisterClient client);
}

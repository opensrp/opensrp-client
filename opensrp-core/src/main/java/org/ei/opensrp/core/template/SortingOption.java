package org.ei.opensrp.core.template;

import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.SortOption;

public interface SortingOption extends DialogOption{
    String sort();
    String name();
}

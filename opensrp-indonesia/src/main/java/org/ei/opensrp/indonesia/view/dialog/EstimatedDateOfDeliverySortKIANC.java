package org.ei.opensrp.indonesia.view.dialog;

import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.Collections;

import static org.ei.opensrp.indonesia.view.contract.SmartRegisterClientINA.EDD_COMPARATOR_KI_ANC;

public class EstimatedDateOfDeliverySortKIANC implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_edd_label);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, EDD_COMPARATOR_KI_ANC);
        return allClients;
    }
}

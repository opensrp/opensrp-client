package org.ei.opensrp.indonesia.view.dialog;

import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.Collections;

import static org.ei.opensrp.indonesia.view.contract.SmartRegisterClientINA.NO_IBU_COMPARATOR;

/**
 * Created by Dimas Ciputra on 4/21/15.
 */
public class NoIbuSort implements SortOption {
    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, NO_IBU_COMPARATOR);
        return allClients;
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_no_ibu_label);
    }
}


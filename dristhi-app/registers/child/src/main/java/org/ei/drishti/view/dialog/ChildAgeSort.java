package org.ei.drishti.view.dialog;

import org.ei.drishti.Context;
import org.ei.drishti.R;
import org.ei.drishti.view.contract.SmartRegisterClients;

import java.util.Collections;

import static org.ei.drishti.view.contract.ChildSmartRegisterClient.AGE_COMPARATOR;

public class ChildAgeSort implements SortOption {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_child_age);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, AGE_COMPARATOR);
        return allClients;
    }
}

package org.ei.opensrp.view.dialog;

import org.ei.opensrp.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;

import static org.ei.opensrp.Context.getInstance;

public class FPPrioritizationHighPriorityServiceMode extends FPPrioritizationAllECServiceMode {

    public FPPrioritizationHighPriorityServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return getInstance().getStringResource(R.string.fp_prioritization_high_priority_service_mode);
    }
}

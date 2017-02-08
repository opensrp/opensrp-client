package org.ei.opensrp.core.template;

import org.ei.opensrp.view.dialog.DialogOption;

public abstract class ServiceModeOption implements DialogOption {

    private RegisterClientsProvider clientsProvider;

    public ServiceModeOption(RegisterClientsProvider clientsProvider) {
        this.clientsProvider = clientsProvider;
    }

    public void apply() {
        clientsProvider.onServiceModeSelected(this);
    }

    public RegisterClientsProvider provider() {
        return clientsProvider;
    }

    public abstract HeaderProvider getHeaderProvider();
}

package org.ei.opensrp.core.template;

import android.view.View;
import android.view.ViewGroup;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

public interface RegisterClientsProvider {

    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup);

    public SmartRegisterClients getClients();

    SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                       SearchFilterOption searchFilter, SortOption sortOption);

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData);

    public View inflateLayoutForAdapter();
}

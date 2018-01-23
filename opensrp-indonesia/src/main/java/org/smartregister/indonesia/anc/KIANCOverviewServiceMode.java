package org.smartregister.indonesia.anc;

import android.view.View;

import org.smartregister.Context;
import org.smartregister.indonesia.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.smartregister.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.smartregister.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.smartregister.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class KIANCOverviewServiceMode extends ServiceModeOption {

    public KIANCOverviewServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.kb_selection);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
                return 7;
            }

            @Override
            public int weightSum() {
                return 1000;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 90, 140, 120, 170, 135, 95};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_name, R.string.header_id, R.string.header_klinis,
                        R.string.header_pemeriksaan, R.string.header_history_anc,
                        R.string.header_status, R.string.header_edit};
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }
}

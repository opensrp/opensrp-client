package org.smartregister.indonesia.kartu_ibu;

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

public class AllKartuIbuServiceMode extends ServiceModeOption {

    public AllKartuIbuServiceMode(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
       return Context.getInstance().getStringResource(R.string.household_entries);
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
                return 1004;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 75, 110, 125, 160, 205, 75};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_nama, R.string.header_id, R.string.header_obsetri,
                        R.string.header_edd, R.string.header_anak, R.string.header_status_b,
                        R.string.header_edit};
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

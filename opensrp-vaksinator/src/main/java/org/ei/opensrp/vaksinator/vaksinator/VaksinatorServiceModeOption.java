package org.ei.opensrp.vaksinator.vaksinator;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaksinator.R;
import org.ei.opensrp.view.contract.ANCSmartRegisterClient;
import org.ei.opensrp.view.contract.ChildSmartRegisterClient;
import org.ei.opensrp.view.contract.FPSmartRegisterClient;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class VaksinatorServiceModeOption extends ServiceModeOption {

    public VaksinatorServiceModeOption(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
       return Context.getInstance().getStringResource(R.string.vaksinator);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() { return 9; }

            @Override
            public int weightSum() {
                return 176;
            }

            @Override
            public int[] weights() {
                return new int[]{12,28,18,18,22,21,25,22,24};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                    R.string.space,
                    R.string.hh_profile,
                    R.string.hb1,
                    R.string.polio1,
                    R.string.polio2,
                    R.string.polio3,
                    R.string.polio4,
                    R.string.measles,
                    R.string.space
                };
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

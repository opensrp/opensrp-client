package org.smartregister.indonesia.kb;

import android.view.View;

import org.smartregister.Context;
import org.smartregister.indonesia.R;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.contract.ANCSmartRegisterClient;
import org.smartregister.view.contract.ChildSmartRegisterClient;
import org.smartregister.view.contract.FPSmartRegisterClient;
import org.smartregister.view.contract.pnc.PNCSmartRegisterClient;
import org.smartregister.view.dialog.ServiceModeOption;

import static org.smartregister.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class AllKBServiceMode extends ServiceModeOption {

    public AllKBServiceMode(SmartRegisterClientsProvider provider) {
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
                return 989;
            }

            @Override
            public int[] weights() {
                return new int[]{244, 75, 110, 140, 170, 120, 120};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.header_nama, R.string.header_id, R.string.header_obsetri,
                        R.string.header_kb_method, R.string.header_risk_factors, R.string.header_update_refill,
                        R.string.header_edit};
            }
        };
    }

}

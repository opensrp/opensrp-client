package org.ei.opensrp.immunization.field;

import org.ei.opensrp.Context;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

/**
 * Created by engrmahmed14@gmail.com on 12/12/15.
 */
public class StockDailyServiceModeOption extends ServiceModeOption {

    public StockDailyServiceModeOption(SmartRegisterClientsProvider clientsProvider) {
        super(clientsProvider);
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return 10;
            }

            @Override
            public int weightSum() {
                return 11;
            }

            @Override
            public int[] weights() {
                return new int[]{2,2,3,1,3,1,1,1,1,1};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.day,
                        R.string.bcg,
                        R.string.opv,
                        R.string.ipv,
                        R.string.penta,
                        R.string.measles,
                        R.string.pcv,
                        R.string.tt,
                        R.string.used,
                        R.string.inhand
                };
            }
        };
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.stock_register_daily_view);
    }
}

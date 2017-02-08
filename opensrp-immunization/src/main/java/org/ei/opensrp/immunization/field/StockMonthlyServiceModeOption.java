package org.ei.opensrp.immunization.field;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.template.HeaderProvider;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 12-Nov-15.
 */
public class StockMonthlyServiceModeOption extends ServiceModeOption {

    public StockMonthlyServiceModeOption(RegisterClientsProvider clientsProvider) {
        super(clientsProvider);
    }

    @Override
    public HeaderProvider getHeaderProvider() {
        return new HeaderProvider() {
            @Override
            public int count() {
                return 8;
            }

            @Override
            public int weightSum() {
                return 24;
            }

            @Override
            public int[] weights() {
                return new int[]{2,2,3,2,4,3,4,4};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{ R.string.month, R.string.month_target, R.string.month_received,
                        R.string.month_used, R.string.month_wasted, R.string.month_inhand, R.string.month_starting, R.string.month_current
                };
            }
        };
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.stock_register_monthly_view);
    }
}

package com.example.opensrp_stock.field;

import com.example.opensrp_stock.R;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.template.HeaderProvider;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.ServiceModeOption;

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
                return 6;
            }

            @Override
            public int weightSum() {
                return 22;
            }

            @Override
            public int[] weights() {
                return new int[]{3,3,4,4,4,4};
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{ R.string.month, R.string.month_target, R.string.month_received,
                       R.string.month_inhand, R.string.month_starting, R.string.month_current
                };
            }
        };
    }

    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.stock_register_monthly_view);
    }
}

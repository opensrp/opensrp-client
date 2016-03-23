package org.ei.opensrp.view.dialog;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;

public class HRPSort extends HighRiskSort {
    @Override
    public String name() {
        return Context.getInstance().getStringResource(R.string.sort_by_high_risk_pregnancy_label);
    }

}

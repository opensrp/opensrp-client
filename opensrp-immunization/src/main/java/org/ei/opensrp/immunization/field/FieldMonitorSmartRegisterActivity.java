package org.ei.opensrp.immunization.field;

import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldMonitorSmartRegisterActivity extends RegisterActivity {

    @Override
    public RegisterDataGridFragment makeBaseFragment() {
        return new FieldMonitorRegisterFragment(new FormController(this));
    }

    protected Map<String, Object> buildFormNameList() {
        Map<String, Object> formNames = new HashMap<>();
        formNames.put("vaccine_stock_position", new String[]{"stock"});

        return formNames;
    }

    @Override
    public String postFormSubmissionRecordFilterField() {
        return "";
    }

    @Override
    protected void onResumption() {

    }

    @Override
    public DetailFragment getDetailFragment() {
        return new FieldMonitorMonthlyDetailFragment();
    }

    //TODO
}

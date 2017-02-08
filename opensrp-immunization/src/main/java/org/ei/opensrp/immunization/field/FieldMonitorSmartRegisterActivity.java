package org.ei.opensrp.immunization.field;

import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.ArrayList;
import java.util.List;

public class FieldMonitorSmartRegisterActivity extends RegisterActivity {

    @Override
    public RegisterDataGridFragment makeBaseFragment() {
        return new FieldMonitorRegisterFragment(new FormController(this));
    }

    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("vaccine_stock_position");

        return formNames.toArray(new String[formNames.size()]);
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

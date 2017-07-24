package org.ei.opensrp.immunization.child;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.immunization.application.common.SmartClientRegisterFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartRegisterActivity extends RegisterActivity {

    private String id;

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        super.onCreateActivity(savedInstanceState);

        Log.v(getClass().getName(), "savedInstanceState bundle : "+savedInstanceState);
        Log.v(getClass().getName(), "intent bundle : "+getIntent().toString());
        id = getIntent().getStringExtra("program_client_id");
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Log.i(getClass().getName(), "Resuming fragments");
    }

    private void filter(){
        RegisterDataGridFragment registerFragment = getBaseFragment();
        if(registerFragment != null && registerFragment.loaderHandler().fullyLoaded()){
            registerFragment.getSearchView().setText(id);
            registerFragment.onFilterManual(id);
            id = "";
        }
        else {
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    filter();
                }
            }, 2000);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(getClass().getName(), "Win focus changed and filtering for ID "+id);
        if(id != null && !id.isEmpty()){
            filter();
        }
    }

    @Override
    public SmartClientRegisterFragment makeBaseFragment() {
        return new ChildSmartRegisterFragment(new FormController(this));
    }

    @Override
    protected Map<String, Object> buildFormNameList() {
        Map<String, Object> formNames = new HashMap<>();

        formNames.put("child_enrollment", new String[]{"pkchild"});
        formNames.put("child_enrollment", new String[]{"pkchild"});
        formNames.put("child_offsite_followup", new String[]{"pkchild"});

        return formNames;
    }

    @Override
    public String postFormSubmissionRecordFilterField() {
        return "existing_program_client_id";
    }

    @Override
    protected void onResumption() {

    }

    @Override
    public DetailFragment getDetailFragment() {
        return new ChildDetailFragment();
    }
}

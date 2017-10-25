package org.ei.opensrp.dgfp.hh_member;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseHoldHandler implements FormSubmissionHandler {

    Context context;
    public HouseHoldHandler(Context context){this.context = context;}

    @Override
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        Log.e("formSubmissionHandler",submission.instance());
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "FW CENSUS");
        if(alertlist_for_client.size() == 0){

        }else{
            for(int i = 0;i<alertlist_for_client.size();i++){
                org.ei.opensrp.Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "FW CENSUS");
            }
        }

    }

}

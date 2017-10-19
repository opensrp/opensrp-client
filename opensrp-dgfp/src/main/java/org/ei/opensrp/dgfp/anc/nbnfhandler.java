package org.ei.opensrp.dgfp.anc;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.dgfp.elco.HH_woman_member_SmartRegisterActivity;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nbnfhandler implements FormSubmissionHandler {

    Context context;
    public nbnfhandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        Log.v("fieldvalue",""+submission.getFieldValue("Visit_Status"));
        if(submission.getFieldValue("Visit_Status").equalsIgnoreCase("3")) {
            AllCommonsRepository allmemberRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");
            Map<String, String> memberDetails = new HashMap<String, String>();

            memberDetails.put("is_child_register_done", "0");

            allmemberRepository.mergeDetails(entityID, memberDetails);
        }else{
            List<Alert> alertlist_for_client = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Birth_Notification");
            if(alertlist_for_client.size() == 0){

            }else{
                for(int i = 0;i<alertlist_for_client.size();i++){
                    org.ei.opensrp.Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "Birth_Notification");
                }
            }
        }
//        CommonPersonObject memberobject = allmemberRepository.findByCaseID(entityID);
//        if(submission.getFieldValue("Visit_Status")!= null){
//            if(submission.getFieldValue("Visit_Status").equalsIgnoreCase("3")){
//                ((mCareANCSmartRegisterActivity) ((Activity) context)).startFormActivity("childregistration",memberobject.getRelationalId(),null);
//
//            }
//        }



    }
}

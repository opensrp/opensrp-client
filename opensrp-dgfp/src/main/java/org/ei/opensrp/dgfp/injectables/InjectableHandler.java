package org.ei.opensrp.dgfp.injectables;

import android.util.Log;

import android.content.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.Map;

public class InjectableHandler implements FormSubmissionHandler {

    android.content.Context context;
    public InjectableHandler(android.content.Context context){this.context = context;}

    @Override
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        CommonPersonObject memberobject = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members").findByCaseID(submission.entityId());
        AllCommonsRepository memberRepo = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap();

        if (memberobject.getDetails().get("Injection_Date_1") == null) {
            ElcoDetails.put("Injection_Date_1", submission.getFieldValue("Injection_Date"));
        }
        else if(memberobject.getDetails().get("Injection_Date_2") == null){
            ElcoDetails.put("Injection_Date_2", submission.getFieldValue("Injection_Date"));
        }
        else if(memberobject.getDetails().get("Injection_Date_3") == null){
            ElcoDetails.put("Injection_Date_3", submission.getFieldValue("Injection_Date"));
        }
        else if(memberobject.getDetails().get("Injection_Date_4") == null){
            ElcoDetails.put("Injection_Date_4", submission.getFieldValue("Injection_Date"));
        }
        else if(memberobject.getDetails().get("Injection_Date_5") == null){
            ElcoDetails.put("Injection_Date_5", submission.getFieldValue("Injection_Date"));
        }

        //memberRepo.mergeDetails(memberobject.getRelationalId(), ElcoDetails);
        memberRepo.mergeDetails(submission.entityId(),ElcoDetails);
    }

}

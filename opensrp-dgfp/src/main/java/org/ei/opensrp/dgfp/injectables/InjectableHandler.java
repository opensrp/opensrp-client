package org.ei.opensrp.dgfp.injectables;

import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.Map;

public class InjectableHandler implements FormSubmissionHandler {
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        CommonPersonObject memberobject = Context.getInstance().allCommonsRepositoryobjects("members").findByCaseID(submission.entityId());
        AllCommonsRepository memberRepo = Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap();
        if (memberobject.getDetails().get("Injection_Date_1") == null) {
            ElcoDetails.put("Injection_Date_1", submission.getFieldValue("Injection_Date"));
        } else {
            ElcoDetails.put("Injection_Date_2", submission.getFieldValue("Injection_Date"));
        }
        memberRepo.mergeDetails(memberobject.getRelationalId(), ElcoDetails);
    }
}

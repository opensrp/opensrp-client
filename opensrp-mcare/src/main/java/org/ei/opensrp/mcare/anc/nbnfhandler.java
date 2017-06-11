package org.ei.opensrp.mcare.anc;

import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class nbnfhandler implements FormSubmissionHandler {


    public nbnfhandler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "BirthNotificationFollowUp");
        if(alertlist_for_client.size() == 0){

        }else{//BirthNotificationPregnancyStatusFollowUp previous_name
            for(int i = 0;i<alertlist_for_client.size();i++){
                Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "BirthNotificationFollowUp");
            }
        }
        Map<String, String> overrideValue = new HashMap<String, String>();
        if(submission.getFieldValue("FWBNFSTS").equalsIgnoreCase("0") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            CommonPersonObject motherObject = Context.getInstance().allCommonsRepositoryobjects("mcaremother").findByCaseID(entityID);
            AllCommonsRepository motherRepo = Context.getInstance().allCommonsRepositoryobjects("mcaremother");
            overrideValue.put("FWWOMVALID","0");
            motherRepo.mergeDetails(entityID,overrideValue);

            overrideValue.clear();

            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(motherObject.getRelationalId());
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            elcoRepo.mergeDetails(motherObject.getRelationalId(),overrideValue);

        }
    }
}

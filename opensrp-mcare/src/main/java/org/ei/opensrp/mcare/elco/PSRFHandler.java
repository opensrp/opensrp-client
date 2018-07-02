package org.ei.opensrp.mcare.elco;

import android.content.ContentValues;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PSRFHandler implements FormSubmissionHandler {


    public PSRFHandler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "ELCO PSRF");
        if(alertlist_for_client.size() == 0){

        }else{
            for(int i = 0;i<alertlist_for_client.size();i++){
                Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "ELCO PSRF");
            }
        }
        CommonPersonObject elcoobject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(entityID);
        AllCommonsRepository householdrep = Context.getInstance().allCommonsRepositoryobjects("household");
        Map<String, String> ElcoDetails = new HashMap<String, String>();
        ElcoDetails.put("ELCO",submission.getFieldValue("ELCO"));
//        ElcoDetails.put("FWELIGIBLE",submission.getFieldValue("FWELIGIBLE"));
       // householdrep.mergeDetails(elcoobject.getRelationalId(),ElcoDetails);
//        submission.getFieldValue("ELCO");
        Map<String, String> overrideValue = new HashMap<String, String>();


        if(submission.getFieldValue("FWPSRSTS").equalsIgnoreCase("999") && submission.getFieldValue("user_type").equalsIgnoreCase("FD")){
            overrideValue.clear();
            CommonPersonObject elcoObject = Context.getInstance().allCommonsRepositoryobjects("elco").findByCaseID(entityID);
            AllCommonsRepository elcoRepo = Context.getInstance().allCommonsRepositoryobjects("elco");
            overrideValue.put("FWPSRPREGSTS","0");
            overrideValue.put("FWELIGIBLE","0");
            elcoRepo.mergeDetails(entityID,overrideValue);

        }

    }


}

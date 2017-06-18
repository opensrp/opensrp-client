package org.ei.opensrp.dghs.HH_woman;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tt1handler implements FormSubmissionHandler {


    public tt1handler() {

    }

    @Override
    public void handle(FormSubmission submission) {

        Logger.largeLog("-------------",submission.toString());

        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Woman_TT1");
        if(alertlist_for_client.size() == 0){

        }else{
            for(int i = 0;i<alertlist_for_client.size();i++){
                Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "Woman_TT1");
            }
        }
        AllCommonsRepository memberrep = Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap<String, String>();
        ElcoDetails.put("Is_Reg_Today","0");
//        ElcoDetails.put("FWELIGIBLE",submission.getFieldValue("FWELIGIBLE"));
        memberrep.mergeDetails(entityID,ElcoDetails);


        AllCommonsRepository alerts = Context.getInstance().allCommonsRepositoryobjects("alerts");
        List<Alert> alerts_list_tt2 = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Woman_TT2");
        List<Alert> alerts_list_tt3 = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Woman_TT3");
        List<Alert> alerts_list_tt4 = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Woman_TT4");
        List<Alert> alerts_list_tt5 = Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Woman_TT5");
        if(alerts_list_tt2.size() > 0){
            // delete tt2 schedule
            alerts.close(entityID);
        }
        if(alerts_list_tt3.size() > 0){
            // delete tt3 schedule
            alerts.close(entityID);
        }
        if(alerts_list_tt4.size() > 0){
            // delete tt4 schedule
            alerts.close(entityID);
        }
        if(alerts_list_tt5.size() > 0){
            // delete tt5 schedule
            alerts.close(entityID);
        }

    }
}

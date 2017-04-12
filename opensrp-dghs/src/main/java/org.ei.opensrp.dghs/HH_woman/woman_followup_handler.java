package org.ei.opensrp.dghs.HH_woman;

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
import java.util.UUID;

public class woman_followup_handler implements FormSubmissionHandler {


    public woman_followup_handler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        AllCommonsRepository memberrep = Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap<String, String>();
        ElcoDetails.put("Is_Reg_Today","0");
//        ElcoDetails.put("FWELIGIBLE",submission.getFieldValue("FWELIGIBLE"));
        memberrep.mergeDetails(entityID,ElcoDetails);


        if(submission.getForm().getFieldValue("tt_2_dose_today")!=null){
            String stockentityID = UUID.randomUUID().toString();
            HashMap<String,String> detailmap = new HashMap<String, String>();
            CommonPersonObject stocktt1given = new CommonPersonObject(stockentityID,"",detailmap,"stock");
            Context.getInstance().commonrepository("stock").add(stocktt1given);
            ContentValues cv = new ContentValues();
            cv.put("date",submission.getForm().getFieldValue("tt2"));
            cv.put("tt_used","1");
            Context.getInstance().commonrepository("stock").updateColumn("stock",cv,stockentityID);
        }

    }
}

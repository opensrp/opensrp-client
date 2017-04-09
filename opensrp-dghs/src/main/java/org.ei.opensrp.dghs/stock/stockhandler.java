package org.ei.opensrp.dghs.stock;

import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class stockhandler implements FormSubmissionHandler {


    public stockhandler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        Log.v("can see this",submission.getForm().getFieldValue("date"));
          AllCommonsRepository stockrep = Context.getInstance().allCommonsRepositoryobjects("stock");
        List<CommonPersonObject> cpo = stockrep.customQuery("Select * from stock where date like ?", new String[]{submission.getForm().getFieldValue("date") + "%"},"stock");
        for(int i = 0;i<cpo.size();i++){
            entityID = cpo.get(i).getCaseId();
        }


        FormSubmission submission1 = new FormSubmission(submission.instanceId(),entityID,submission.formName(),submission.instance(),submission.version(),submission.syncStatus(),submission.formDataDefinitionVersion());
        submission = submission1;


    }
}

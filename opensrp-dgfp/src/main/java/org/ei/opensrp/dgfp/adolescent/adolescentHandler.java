package org.ei.opensrp.dgfp.adolescent;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.List;

public class adolescentHandler implements FormSubmissionHandler {

    Context context;
    public adolescentHandler(Context context){this.context = context;}

    @Override
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        Log.e("formSubmissionHandler",submission.instance());
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Adolescent_Health");
        if(alertlist_for_client.size() == 0){

        }else{
            for(int i = 0;i<alertlist_for_client.size();i++){
                org.ei.opensrp.Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "Adolescent_Health");
            }
        }

    }

}

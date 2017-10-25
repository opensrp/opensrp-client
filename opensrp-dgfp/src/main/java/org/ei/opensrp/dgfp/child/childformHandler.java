package org.ei.opensrp.dgfp.child;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.List;

public class childformHandler implements FormSubmissionHandler {

    Context context;
    public childformHandler(Context context){this.context = context;}

    @Override
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        Log.e("formSubmissionHandler",submission.instance());
        String entityID = submission.entityId();
        List<Alert> alertlist_for_client = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(entityID, "Child_05yr");
        if(alertlist_for_client.size() == 0){

        }else{
            for(int i = 0;i<alertlist_for_client.size();i++){
                org.ei.opensrp.Context.getInstance().alertService().changeAlertStatusToComplete(entityID, "Child_05yr");
            }
        }

    }

}

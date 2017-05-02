package org.ei.opensrp.dgfp.hh_member;

import android.content.Context;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.Map;

public class HouseHoldHandler implements FormSubmissionHandler {

    Context context;
    public HouseHoldHandler(Context context){this.context = context;}

    @Override
    public void handle(FormSubmission submission) {
        Log.d("formSubmissionHandler", "handler");
        Log.e("formSubmissionHandler",submission.instance());
    }

}

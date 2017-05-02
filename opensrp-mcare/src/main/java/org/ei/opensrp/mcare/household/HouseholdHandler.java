package org.ei.opensrp.mcare.household;

import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.List;

public class HouseholdHandler implements FormSubmissionHandler {


    public HouseholdHandler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        Log.e("householdhandler",submission.toString());
    }
}

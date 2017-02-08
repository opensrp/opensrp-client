package org.ei.opensrp.immunization.child;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 29-Oct-15.
 */
public class ChildFollowupHandler implements FormSubmissionHandler {

    private ChildService childService;

    public ChildFollowupHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.followup(submission);
    }
}

package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.service.ChildService;

public class ChildIllnessHandler implements FormSubmissionHandler {
    private ChildService childService;

    public ChildIllnessHandler(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public void handle(FormSubmission submission) {
        childService.updateIllnessStatus(submission);
    }
}

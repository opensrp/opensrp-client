package org.ei.opensrp.path.handler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.path.service.WomanService;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 08-Dec-15.
 */
public class WomanFollowupHandler implements FormSubmissionHandler {

    private WomanService womanService;

    public WomanFollowupHandler(WomanService womanService) {
        this.womanService = womanService;
    }

    @Override
    public void handle(FormSubmission submission) {
        womanService.followup(submission);
    }
}

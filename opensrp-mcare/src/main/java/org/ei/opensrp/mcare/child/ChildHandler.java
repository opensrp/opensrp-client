package org.ei.opensrp.mcare.child;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by habib on 5/31/17.
 */
public class ChildHandler implements FormSubmissionHandler {
    @Override
    public void handle(FormSubmission submission) {
        Logger.largeLog("------childHandler",submission.toString());
    }
}

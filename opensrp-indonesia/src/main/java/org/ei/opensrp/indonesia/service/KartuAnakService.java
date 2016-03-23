package org.ei.opensrp.indonesia.service;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.indonesia.view.controller.UniqueIdController;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.opensrp.indonesia.AllConstantsINA.CommonFormFields.SUBMISSION_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.CommonFormFields.UNIQUE_ID;

/**
 * Created by Dimas Ciputra on 10/28/15.
 */
public class KartuAnakService {

    private final UniqueIdController uniqueIdController;

    public KartuAnakService(UniqueIdController uniqueIdController) {
        this.uniqueIdController = uniqueIdController;
    }

    public void register(FormSubmission submission) {
        if (isNotBlank(submission.getFieldValue(SUBMISSION_DATE))) {
            uniqueIdController.updateCurrentUniqueId(submission.getFieldValue(UNIQUE_ID));
        }
    }

}

package org.ei.opensrp.indonesia.service;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.indonesia.repository.AllKartuIbus;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.controller.UniqueIdController;
import org.ei.opensrp.repository.AllTimelineEvents;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.opensrp.indonesia.AllConstantsINA.CommonFormFields.SUBMISSION_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.CommonFormFields.UNIQUE_ID;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KartuIbuService {
    private final AllKartuIbus allKartuIbus;
    private final AllTimelineEvents allTimelineEvents;
    private final AllKohort allKohort;
    private final UniqueIdController uniqueIdController;

    public KartuIbuService(AllKartuIbus allKartuIbus, AllTimelineEvents allTimelineEvents, AllKohort allKohort, UniqueIdController uniqueIdController) {
        this.allKartuIbus = allKartuIbus;
        this.allTimelineEvents = allTimelineEvents;
        this.allKohort = allKohort;
        this.uniqueIdController = uniqueIdController;
    }

    public void register(FormSubmission submission) {
        if (isNotBlank(submission.getFieldValue(SUBMISSION_DATE))) {
            uniqueIdController.updateCurrentUniqueId(submission.getFieldValue(UNIQUE_ID));
            // TODO : add to timeline event repository
            // allTimelineEvents.add(TimelineEvent.forECRegistered(submission.entityId(), submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE)));
        }
    }

    public void registerPNC(FormSubmission submission) {
        if(isNotBlank(submission.getFieldValue(SUBMISSION_DATE))) {
            uniqueIdController.updateCurrentUniqueId(submission.getFieldValue(UNIQUE_ID));
        }
    }

    public void registerKB(FormSubmission submission) {
        if(isNotBlank(submission.getFieldValue(SUBMISSION_DATE))) {
            uniqueIdController.updateCurrentUniqueId(submission.getFieldValue(UNIQUE_ID));
        }
    }


    public void closeKartuIbu(FormSubmission formSubmission) {
        allKartuIbus.close(formSubmission.entityId());
        allKohort.closeAllIbuForKartuIbu(formSubmission.entityId());
    }
}

package org.ei.opensrp.path.service;

import android.content.ContentValues;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.ei.opensrp.service.AlertService;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 08-Dec-15.
 */
public class WomanService {

    private AllTimelineEvents allTimelines;
    private AllCommonsRepository allCommonsRepository;
    private AlertService alertService;

    public WomanService(AllTimelineEvents allTimelines, AllCommonsRepository allCommonsRepository, AlertService alertService) {
        this.allTimelines = allTimelines;
        this.allCommonsRepository = allCommonsRepository;
        this.alertService = alertService;

    }

    public void followup(FormSubmission submission) {
        HashMap<String, String> map = new HashMap<String, String>();

        //retro
        map.put("tt1_retro", submission.getFieldValue("tt1_retro") != null ? submission.getFieldValue("tt1_retro") : "");
        map.put("tt2_retro", submission.getFieldValue("tt2_retro") != null ? submission.getFieldValue("tt2_retro") : "");
        map.put("tt3_retro", submission.getFieldValue("tt3_retro") != null ? submission.getFieldValue("tt3_retro") : "");
        map.put("tt4_retro", submission.getFieldValue("tt4_retro") != null ? submission.getFieldValue("tt4_retro") : "");


        //today Vaccines
        map.put("tt1", submission.getFieldValue("tt1") != null ? submission.getFieldValue("tt1") : "");
        map.put("tt2", submission.getFieldValue("tt2") != null ? submission.getFieldValue("tt2") : "");
        map.put("tt3", submission.getFieldValue("tt3") != null ? submission.getFieldValue("tt3") : "");
        map.put("tt4", submission.getFieldValue("tt4") != null ? submission.getFieldValue("tt4") : "");


        ContentValues contentValues = new ContentValues();
        for (String key : map.keySet()) {

            if (map.get(key).equalsIgnoreCase("")) {

                continue;
            } else {
                Character vaccineNUmber = key.charAt(2);
                alertService.changeAlertStatusToComplete(submission.entityId(), "TT " + vaccineNUmber);
                contentValues.put(key, map.get(key));

            }
        }

        allCommonsRepository.update("ec_mother", contentValues, submission.entityId());

        //another map for adding other details for timelineevent repository .
        HashMap<String, String> mapOther = new HashMap<String, String>();

        mapOther.put("is_pregnant", submission.getFieldValue("pregnant") != null ? submission.getFieldValue("pregnant") : "");
        mapOther.put("remembered_edd_date", submission.getFieldValue("edd_lmp") != null ? submission.getFieldValue("edd_lmp") : "");
        mapOther.put("final_edd", submission.getFieldValue("final_edd") != null ? submission.getFieldValue("final_edd") : "");
        mapOther.put("retro_vaccines", submission.getFieldValue("vaccines") != null ? submission.getFieldValue("vaccines") : "");
        //today's vaccines
        mapOther.put("current_vaccines", submission.getFieldValue("vaccines_2") != null ? submission.getFieldValue("vaccines_2") : "");
        mapOther.put("current_vaccines_date", submission.getFieldValue("date") != null ? submission.getFieldValue("date") : "");

        JSONObject json = new JSONObject(mapOther);
        Log.d("followup submission : ", json.toString());

//        alertService.changeAlertStatusToComplete(submission.entityId(),);
        allTimelines.add(new TimelineEvent(submission.entityId(), "WOMANFOLLOWUP", LocalDate.parse(submission.getFieldValue("today")), "woman followup", json.toString(), null));

    }
}

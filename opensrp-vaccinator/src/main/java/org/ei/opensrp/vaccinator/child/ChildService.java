package org.ei.opensrp.vaccinator.child;

import android.content.ContentValues;
import android.util.Log;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.AllBeneficiaries;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.ei.opensrp.service.AlertService;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 29-Oct-15.
 */
public class ChildService {

    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelines;
    private AllCommonsRepository allCommonsRepository;
    private AlertService alertService;

    public ChildService(AllBeneficiaries allBeneficiaries,
                        AllTimelineEvents allTimelines, AllCommonsRepository allCommonsRepository, AlertService alertService) {
        this.allBeneficiaries = allBeneficiaries;
        this.alertService = alertService;
        this.allTimelines = allTimelines;
        this.allCommonsRepository = allCommonsRepository;

    }

    public void followup(FormSubmission submission) {
        //#TODO :add details into string then into form submission.
        HashMap<String, String> map = new HashMap<String, String>();
        //retro vaccines
        map.put("bcg_retro", submission.getFieldValue("bcg_retro") != null ? submission.getFieldValue("bcg_retro") : "");
        map.put("opv_0_retro", submission.getFieldValue("opv_0_retro") != null ? submission.getFieldValue("opv_0_retro") : "");
        //map.put("opv_0_dose_retro",submission.getFieldValue("opv_0_dose")!=null ?submission.getFieldValue("opv_0_dose"):"");
        map.put("pcv_1_retro", submission.getFieldValue("pcv_1_retro") != null ? submission.getFieldValue("pcv_1_retro") : "");
        //map.put("pcv_1_dose_retro",submission.getFieldValue("pcv_1_dose")!=null ?submission.getFieldValue("pcv_1_dose"):"");

        map.put("opv_1_retro", submission.getFieldValue("opv_1_retro") != null ? submission.getFieldValue("opv_1_retro") : "");
        //map.put("opv_1_dose_retro",submission.getFieldValue("opv_1_dose")!=null ?submission.getFieldValue("opv_1_dose"):"");

        map.put("pentavalent_1_retro", submission.getFieldValue("pentavalent_1_retro") != null ? submission.getFieldValue("pentavalent_1_retro") : "");
        //  map.put("pentavalent_1_dose_retro",submission.getFieldValue("pentavalent_1_dose")!=null ?submission.getFieldValue("pentavalent_1_dose"):"");

        map.put("pcv_2_retro", submission.getFieldValue("pcv_2_retro") != null ? submission.getFieldValue("pcv_2_retro") : "");
        //  map.put("retro_pcv_2_dose",submission.getFieldValue("pcv_2_dose")!=null ?submission.getFieldValue("pcv_2_dose"):"");

        map.put("opv_2_retro", submission.getFieldValue("opv_2_retro") != null ? submission.getFieldValue("opv_2_retro") : "");
        //map.put("retro_opv_2_dose",submission.getFieldValue("opv_2_dose")!=null ?submission.getFieldValue("opv_2_dose"):"");

        map.put("pcv_3_retro", submission.getFieldValue("pcv_3_retro") != null ? submission.getFieldValue("pcv_3_retro") : "");
        // map.put("retro_pcv_3_dose",submission.getFieldValue("pcv_3_dose")!=null ?submission.getFieldValue("pcv_3_dose"):"");

        map.put("opv_3_retro", submission.getFieldValue("opv_3_retro") != null ? submission.getFieldValue("opv_3_retro") : "");
        // map.put("retro_opv_3_dose",submission.getFieldValue("opv_3_dose")!=null ?submission.getFieldValue("opv_3_dose"):"");

        map.put("pentavalent_3_retro", submission.getFieldValue("pentavalent_3_retro") != null ? submission.getFieldValue("pentavalent_3_retro") : "");
        //    map.put("retro_pentavalent_3_dose",submission.getFieldValue("pentavalent_3_dose")!=null ?submission.getFieldValue("pentavalent_3_dose"):"");

        map.put("measles_1_retro", submission.getFieldValue("measles_1_retro") != null ? submission.getFieldValue("measles_1_retro") : "");
        //  map.put("retro_measles_1_dose",submission.getFieldValue("measles_1_dose")!=null ?submission.getFieldValue("measles_1_dose"):"");

        map.put("measles_2_retro", submission.getFieldValue("measles_2_retro") != null ? submission.getFieldValue("measles_2_retro") : "");
        ///map.put("retro_measles_2_dose",submission.getFieldValue("measles_2_dose")!=null ?submission.getFieldValue("measles_2_dose"):"");


        map.put("bcg", submission.getFieldValue("bcg") != null ? submission.getFieldValue("bcg") : "");
        map.put("opv_0", submission.getFieldValue("opv_0") != null ? submission.getFieldValue("opv_0") : "");
        // map.put("today_opv_0_dose",submission.getFieldValue("opv_0_dose_today")!=null ?submission.getFieldValue("opv_0_dose_today"):"");
        map.put("pcv_1", submission.getFieldValue("pcv_1") != null ? submission.getFieldValue("pcv_1") : "");
        //map.put("today_pcv_1_dose",submission.getFieldValue("pcv_1_dose_today")!=null ?submission.getFieldValue("pcv_1_dose_today"):"");

        map.put("opv_1", submission.getFieldValue("opv_1") != null ? submission.getFieldValue("opv_1") : "");
        //map.put("today_opv_1_dose",submission.getFieldValue("opv_1_dose_today")!=null ?submission.getFieldValue("opv_1_dose_today"):"");

        map.put("pentavalent_1", submission.getFieldValue("pentavalent_1") != null ? submission.getFieldValue("pentavalent_1") : "");
        //map.put("today_pentavalent_1_dose",submission.getFieldValue("pentavalent_1_dose_today")!=null ?submission.getFieldValue("pentavalent_1_dose_today"):"");

        map.put("pcv_2", submission.getFieldValue("pcv_2") != null ? submission.getFieldValue("pcv_2") : "");
//        map.put("today_pcv_2_dose",submission.getFieldValue("pcv_2_dose_today")!=null ?submission.getFieldValue("pcv_2_dose_today"):"");

        map.put("opv_2", submission.getFieldValue("opv_2") != null ? submission.getFieldValue("opv_2") : "");
//        map.put("today_opv_2_dose",submission.getFieldValue("opv_2_dose_today")!=null ?submission.getFieldValue("opv_2_dose_today"):"");

        map.put("pcv_3", submission.getFieldValue("pcv_3") != null ? submission.getFieldValue("pcv_3") : "");
//        map.put("today_pcv_3_dose",submission.getFieldValue("pcv_3_dose_today")!=null ?submission.getFieldValue("pcv_3_dose_today"):"");

        map.put("opv_3", submission.getFieldValue("opv_3") != null ? submission.getFieldValue("opv_3") : "");
        //   map.put("today_opv_3_dose",submission.getFieldValue("opv_3_dose_today")!=null ?submission.getFieldValue("opv_3_dose_today"):"");

        map.put("pentavalent_3", submission.getFieldValue("pentavalent_3") != null ? submission.getFieldValue("pentavalent_3") : "");
        //  map.put("today_pentavalent_3_dose",submission.getFieldValue("pentavalent_3_dose_today")!=null ?submission.getFieldValue("pentavalent_3_dose_today"):"");

        map.put("measles_1", submission.getFieldValue("measles_1") != null ? submission.getFieldValue("measles_1") : "");
        //map.put("today_measles_1_dose",submission.getFieldValue("measles_1_dose_today")!=null ?submission.getFieldValue("measles_1_dose_today"):"");

        map.put("measles_2", submission.getFieldValue("measles_2") != null ? submission.getFieldValue("measles_2") : "");
        //map.put("today_measles_2_dose",submission.getFieldValue("measles_2_dose_today")!=null ?submission.getFieldValue("measles_2_dose_today"):"");


        ContentValues contentValues = new ContentValues();
        for (String key : map.keySet()) {

            if (map.get(key).equalsIgnoreCase("")) {
                continue;
            } else {
                contentValues.put(key, map.get(key));
                //  key
                alertService.changeAlertStatusToComplete(submission.entityId(), key.replace("_", " "));

            }
        }

        allCommonsRepository.update("pkchild", contentValues, submission.entityId());


        //another map for adding other details for timelineevent repository .
        HashMap<String, String> mapOther = new HashMap<String, String>();


        //Other Details
        mapOther.put("diseases_at_birth", submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth") != null ? submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth") : "");

        mapOther.put("side_effects", submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI") != null ? submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI") : "");

        mapOther.put("reminders_approval", submission.getFieldValue("existing_reminders_approval") != null ? submission.getFieldValue("existing_reminders_approval") : "");
        mapOther.put("contact_phone_number", submission.getFieldValue("existing_contact_phone_number") != null ? submission.getFieldValue("existing_contact_phone_number") : "");
        mapOther.put("center_gps", submission.getFieldValue("center_gps") != null ? submission.getFieldValue("center_gps") : "");
        mapOther.put("retro_vaccines", submission.getFieldValue("vaccines") != null ? submission.getFieldValue("vaccines") : "");
        //today's vaccines
        mapOther.put("current_vaccines", submission.getFieldValue("vaccines_2") != null ? submission.getFieldValue("vaccines_2") : "");
        mapOther.put("current_vaccines_date", submission.getFieldValue("vaccination_date") != null ? submission.getFieldValue("vaccination_date") : "");

        //today vaccines today
        mapOther.put("retro_vaccines", submission.getFieldValue("vaccines") != null ? submission.getFieldValue("vaccines") : "");

        //       String gps= submission.getFieldValue("center_gps");
        //  String retrovaccines=submission.getFieldValue("vaccines");
        //  String retrobcg=submission.getFieldValue("bcg");
//        String vaccines2=submission.getFieldValue("vaccines_2");
        //     String vaccination_date=submission.getFieldValue("vaccination_date");
        //    String disease_at_birth=submission.getFieldValue("existing_child_was_suffering_from_a_disease_at_birth");
        //    String vaccines=submission.getFieldValue("the_temporary_side-effects_of_immunization_shots_AEFI");
        //     String sideeffects=submission.getFieldValue("vaccines");
        //     String existing_reminders_approval=submission.getFieldValue("existing_reminders_approval");
        //    String existing_contact_phone_number=submission.getFieldValue("existing_contact_phone_number");
        //String vaccines=submission.getFieldValue("vaccines");
        //  Log.d("followup submission : ",gps);
        //   String detail =
        JSONObject json = new JSONObject(mapOther);
        Log.d("followup submission : ", json.toString());

        allTimelines.add(new TimelineEvent(submission.entityId(), "CHILDFOLLOWUP", LocalDate.parse(submission.getFieldValue("vaccination_date")), "child followup", json.toString(), null));
    }
}

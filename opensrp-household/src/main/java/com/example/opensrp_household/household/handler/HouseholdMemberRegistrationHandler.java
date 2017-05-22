package com.example.opensrp_household.household.handler;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.example.opensrp_child.child.ChildSmartRegisterActivity;
import com.example.opensrp_woman.woman.WomanSmartRegisterActivity;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by Maimoona on 9/6/2016.
 */
public class HouseholdMemberRegistrationHandler implements FormSubmissionHandler
{
    private Activity activity;

    public HouseholdMemberRegistrationHandler(Activity activity){
        this.activity = activity;
    }

    @Override
    public void handle(FormSubmission submission) {
        try {
            Log.v(getClass().getName(), "Handing routed form : "+submission.toString());
            ContentValues cv = new ContentValues();
            String pid = submission.getFieldValue("program_client_id");
            cv.put("program_client_id", pid);
            Context.getInstance().commonrepository("pkindividual").updateColumn("pkindividual", cv, submission.entityId());

            Intent intent = null;
            if(submission.formName().toLowerCase().contains("child")){
                intent = new Intent(activity, ChildSmartRegisterActivity.class);
            }
            else if(submission.formName().toLowerCase().contains("woman")){
                intent = new Intent(activity, WomanSmartRegisterActivity.class);
            }
            intent.putExtra("program_client_id", pid);
            activity.startActivity(intent);
            activity.finish();
        }
        catch (Exception e){e.printStackTrace();}//todo what to do with it
    }
}

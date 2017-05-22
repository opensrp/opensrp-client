package com.example.opensrp_zm.zm.handler;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.opensrp_child.child.ChildSmartRegisterActivity;
import com.example.opensrp_woman.woman.WomanSmartRegisterActivity;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by Maimoona on 2/3/2017.
 */

public class ZMFormSubmissionHandler implements FormSubmissionHandler
{
    private Activity activity;

    public ZMFormSubmissionHandler(Activity activity){
        this.activity = activity;
    }

    @Override
    public void handle(FormSubmission submission) {
        try {
            Log.v(getClass().getName(), "Handing routed form : "+submission.toString());
            String pid = submission.getFieldValue("program_client_id");
            if(StringUtils.isBlank(pid)){
                pid = submission.getFieldValue("existing_program_client_id");
            }
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

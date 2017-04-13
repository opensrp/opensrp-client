package org.ei.opensrp.dghs.HH_woman;

import android.content.ContentValues;
import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class woman_followup_handler implements FormSubmissionHandler {


    public woman_followup_handler() {

    }

    @Override
    public void handle(FormSubmission submission) {
        String entityID = submission.entityId();
        AllCommonsRepository memberrep = Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap<String, String>();
        ElcoDetails.put("Is_Reg_Today", "0");
//        ElcoDetails.put("FWELIGIBLE",submission.getFieldValue("FWELIGIBLE"));
        memberrep.mergeDetails(entityID, ElcoDetails);
        String ttdateKey = tt_given_today(submission);


        if (ttdateKey != null) {
            String stockentityID = UUID.randomUUID().toString();
              CommonRepository stockrep = Context.getInstance().commonrepository("stock");
            Cursor cursor = stockrep.RawCustomQueryForAdapter("Select * from stock where date = '" + submission.getForm().getFieldValue(ttdateKey) + "'");
            if(!checkifAlreadyStockRowPresent(cursor,stockrep)) {
                HashMap<String, String> detailmap = new HashMap<String, String>();
                CommonPersonObject stocktt1given = new CommonPersonObject(stockentityID, "", detailmap, "stock");
                Context.getInstance().commonrepository("stock").add(stocktt1given);
                ContentValues cv = new ContentValues();
                cv.put("date", submission.getForm().getFieldValue(ttdateKey));
                cv.put("tt_used", "1");
                stockrep.updateColumn("stock", cv, stockentityID);
            }

        }
    }
    public boolean checkifAlreadyStockRowPresent(Cursor cursor,CommonRepository stockrep){
        boolean rowpresent = false;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CommonPersonObject tocheck = stockrep.readAllcommonforCursorAdapter(cursor);
                if (tocheck.getColumnmaps().get("total_wasted") != null) {
                    if (tocheck.getColumnmaps().get("tt_used") != null || !tocheck.getColumnmaps().get("tt_used").equalsIgnoreCase("")) {
                        int tt_used = Integer.parseInt(tocheck.getColumnmaps().get("tt_used")) + 1;
                        ContentValues cv = new ContentValues();
                        cv.put("tt_used", tt_used + "");
                        stockrep.updateColumn("stock", cv, tocheck.getCaseId());
                        rowpresent =true;
                    }

                }
                cursor.moveToNext();
            }
            try {
                cursor.close();
            } catch (Exception e) {
            }
            return rowpresent;

        }else{
            return rowpresent;
        }

    }
    public String tt_given_today(FormSubmission submission){
        if (submission.getForm().getFieldValue("tt_1_dose_today") != null) {
            return "tt1";
        }else if (submission.getForm().getFieldValue("tt_2_dose_today") != null) {
            return "tt2";
        }else if (submission.getForm().getFieldValue("tt_3_dose_today") != null) {
            return "tt3";
        }else if (submission.getForm().getFieldValue("tt_4_dose_today") != null) {
            return "tt4";
        }else if (submission.getForm().getFieldValue("tt_5_dose_today") != null) {
            return "tt5";
        }else{
            return null;
        }
    }
}

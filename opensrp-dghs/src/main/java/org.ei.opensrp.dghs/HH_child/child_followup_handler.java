package org.ei.opensrp.dghs.HH_child;

import android.content.ContentValues;
import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class child_followup_handler implements FormSubmissionHandler {


    public child_followup_handler() {

    }

    @Override
    public void handle(FormSubmission submission) {

        Logger.largeLog("-------------",submission.toString());

        String entityID = submission.entityId();
        AllCommonsRepository memberrep = Context.getInstance().allCommonsRepositoryobjects("members");
        Map<String, String> ElcoDetails = new HashMap<String, String>();
        ElcoDetails.put("Is_Reg_Today","0");
//        ElcoDetails.put("FWELIGIBLE",submission.getFieldValue("FWELIGIBLE"));
        memberrep.mergeDetails(entityID,ElcoDetails);

       List <String> VaccdateKey = vacc_given_today(submission);
        List <String> usedColumnForVaccine = usedColumnForVaccineGiveToday(submission);
        if(VaccdateKey.size()>0) {
            for(int i =0;i<VaccdateKey.size();i++) {
                String stockentityID = UUID.randomUUID().toString();
                CommonRepository stockrep = Context.getInstance().commonrepository("stock");
                Cursor cursor = stockrep.RawCustomQueryForAdapter("Select * from stock where date = '" + submission.getForm().getFieldValue(VaccdateKey.get(i)) + "'");
                if (!checkifAlreadyStockRowPresent(cursor, stockrep,usedColumnForVaccine.get(i))) {
                    HashMap<String, String> detailmap = new HashMap<String, String>();
                    CommonPersonObject stocktt1given = new CommonPersonObject(stockentityID, "", detailmap, "stock");
                    Context.getInstance().commonrepository("stock").add(stocktt1given);
                    ContentValues cv = new ContentValues();
                    cv.put("date", submission.getForm().getFieldValue(VaccdateKey.get(i)));
                    cv.put(usedColumnForVaccine.get(i), "1");
                    stockrep.updateColumn("stock", cv, stockentityID);

                }
            }
        }

    }
    public boolean checkifAlreadyStockRowPresent(Cursor cursor,CommonRepository stockrep,String usedString){
        boolean rowpresent = false;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CommonPersonObject tocheck = stockrep.readAllcommonforCursorAdapter(cursor);
                if (tocheck.getColumnmaps().get("total_wasted") != null) {
                    if (tocheck.getColumnmaps().get(usedString) != null ) {
                        if(!tocheck.getColumnmaps().get(usedString).equalsIgnoreCase("")) {
                            int tt_used = Integer.parseInt(tocheck.getColumnmaps().get(usedString)) + 1;
                            ContentValues cv = new ContentValues();
                            cv.put(usedString, tt_used + "");
                            stockrep.updateColumn("stock", cv, tocheck.getCaseId());
                            rowpresent = true;
                        }
                    }else{
                        int tt_used = 1;
                        ContentValues cv = new ContentValues();
                        cv.put(usedString, ""+tt_used);
                        stockrep.updateColumn("stock", cv, tocheck.getCaseId());
                        rowpresent = true;
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

    private List<String> usedColumnForVaccineGiveToday(FormSubmission submission) {
        ArrayList<String> vacctoday = new ArrayList<String>();
        if (submission.getForm().getFieldValue("bcg") != null) {
            vacctoday.add("bcg_used");
        }
        if (submission.getForm().getFieldValue("opv0") != null) {
            vacctoday.add("opv_used");
        }
        if (submission.getForm().getFieldValue("pcv1") != null) {
            vacctoday.add("pcv_used");
        }
        if (submission.getForm().getFieldValue("opv1") != null) {
            vacctoday.add("opv_used");
        }
        if (submission.getForm().getFieldValue("penta1") != null) {
            vacctoday.add("penta_used");
        }
        if (submission.getForm().getFieldValue("pcv2") != null) {
            vacctoday.add("pcv_used");
        }
        if (submission.getForm().getFieldValue("opv2") != null) {
            vacctoday.add("opv_used");
        }
        if (submission.getForm().getFieldValue("penta2") != null) {
            vacctoday.add("penta_used");
        }
        if (submission.getForm().getFieldValue("pcv3") != null) {
            vacctoday.add("pcv_used");
        }
        if (submission.getForm().getFieldValue("opv3") != null) {
            vacctoday.add("opv_used");
        }
        if (submission.getForm().getFieldValue("penta3") != null) {
            vacctoday.add("penta_used");
        }
        if (submission.getForm().getFieldValue("ipv") != null) {
            vacctoday.add("ipv_used");
        }
        if (submission.getForm().getFieldValue("measles1") != null) {
            vacctoday.add("measles_used");
        }
        if (submission.getForm().getFieldValue("measles2") != null) {
            vacctoday.add("measles_used");
        }
        return vacctoday;
    }

    private List<String> vacc_given_today(FormSubmission submission) {
        ArrayList<String> vacctoday = new ArrayList<String>();
        if (submission.getForm().getFieldValue("bcg") != null) {
            vacctoday.add("bcg");
        }
        if (submission.getForm().getFieldValue("opv0") != null) {
            vacctoday.add("opv0");
        }
        if (submission.getForm().getFieldValue("pcv1") != null) {
            vacctoday.add("pcv1");
        }
        if (submission.getForm().getFieldValue("opv1") != null) {
            vacctoday.add("opv1");
        }
        if (submission.getForm().getFieldValue("penta1") != null) {
            vacctoday.add("penta1");
        }
        if (submission.getForm().getFieldValue("pcv2") != null) {
            vacctoday.add("pcv2");
        }
        if (submission.getForm().getFieldValue("opv2") != null) {
            vacctoday.add("opv2");
        }
        if (submission.getForm().getFieldValue("penta2") != null) {
            vacctoday.add("penta2");
        }
        if (submission.getForm().getFieldValue("pcv3") != null) {
            vacctoday.add("pcv3");
        }
        if (submission.getForm().getFieldValue("opv3") != null) {
            vacctoday.add("opv3");
        }
        if (submission.getForm().getFieldValue("penta3") != null) {
            vacctoday.add("penta3");
        }
        if (submission.getForm().getFieldValue("ipv") != null) {
            vacctoday.add("ipv");
        }
        if (submission.getForm().getFieldValue("measles1") != null) {
            vacctoday.add("measles1");
        }
        if (submission.getForm().getFieldValue("measles2") != null) {
            vacctoday.add("measles2");
        }
        return vacctoday;
    }
}

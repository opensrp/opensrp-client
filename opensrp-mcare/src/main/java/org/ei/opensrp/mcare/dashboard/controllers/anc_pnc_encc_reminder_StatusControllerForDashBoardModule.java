package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;
import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.reminderVisitStatusController;

/**
 * Created by raihan on 1/22/18.
 */

public class anc_pnc_encc_reminder_StatusControllerForDashBoardModule extends reminderVisitStatusController {


    @Override
    public HashMap<String, HashMap<String, String>> ancVisitQuery(Date from, Date to, String riskflag) {
       HashMap<String,HashMap<String,String>> anchashMapHashMap = new HashMap<String, HashMap<String, String>>();

       HashMap<String,String> anc1HashMap = new HashMap<String,String>();
       anc1HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"ancrv_1","complete"));
       anc1HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"ancrv_1","upcoming"));
       anc1HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"ancrv_1","urgent"));
       anc1HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"ancrv_1","expired"));
       anchashMapHashMap.put("ANC1",anc1HashMap);

       HashMap<String,String> anc2HashMap = new HashMap<String,String>();
       anc2HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"ancrv_2","complete"));
       anc2HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"ancrv_2","upcoming"));
       anc2HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"ancrv_2","urgent"));
       anc2HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"ancrv_2","expired"));
       anchashMapHashMap.put("ANC2",anc2HashMap);

       HashMap<String,String> anc3HashMap = new HashMap<String,String>();
       anc3HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"ancrv_3","complete"));
       anc3HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"ancrv_3","upcoming"));
       anc3HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"ancrv_3","urgent"));
       anc3HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"ancrv_3","expired"));
       anchashMapHashMap.put("ANC3",anc3HashMap);

       HashMap<String,String> anc4HashMap = new HashMap<String,String>();
       anc4HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"ancrv_4","complete"));
       anc4HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"ancrv_4","upcoming"));
       anc4HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"ancrv_4","urgent"));
       anc4HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"ancrv_4","expired"));
       anchashMapHashMap.put("ANC4",anc4HashMap);

       return anchashMapHashMap;
    }

    @Override
    public HashMap<String, HashMap<String, String>> pncVisitQuery(Date from, Date to, String riskflag) {
        HashMap<String,HashMap<String,String>> pnchashMapHashMap = new HashMap<String, HashMap<String, String>>();

        HashMap<String,String> pnc1HashMap = new HashMap<String,String>();
        pnc1HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"pncrv_1","complete"));
        pnc1HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_1","upcoming"));
        pnc1HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_1","urgent"));
        pnc1HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"pncrv_1","expired"));
        pnchashMapHashMap.put("PNC1",pnc1HashMap);

        HashMap<String,String> pnc2HashMap = new HashMap<String,String>();
        pnc2HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"pncrv_2","complete"));
        pnc2HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_2","upcoming"));
        pnc2HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_2","urgent"));
        pnc2HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"pncrv_2","expired"));
        pnchashMapHashMap.put("PNC2",pnc2HashMap);

        HashMap<String,String> pnc3HashMap = new HashMap<String,String>();
        pnc3HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"pncrv_3","complete"));
        pnc3HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_3","upcoming"));
        pnc3HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_3","urgent"));
        pnc3HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"pncrv_3","expired"));
        pnchashMapHashMap.put("PNC3",pnc3HashMap);



        return pnchashMapHashMap;
    }

    @Override
    public HashMap<String, HashMap<String, String>> neonatalVisitQuery(Date from, Date to, String riskflag) {
        HashMap<String,HashMap<String,String>> encchashMapHashMap = new HashMap<String, HashMap<String, String>>();

        HashMap<String,String> encc1HashMap = new HashMap<String,String>();
        encc1HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"enccrv_1","complete"));
        encc1HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"enccrv_1","upcoming"));
        encc1HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"enccrv_1","urgent"));
        encc1HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"enccrv_1","expired"));
        encchashMapHashMap.put("ENCC1",encc1HashMap);

        HashMap<String,String> encc2HashMap = new HashMap<String,String>();
        encc2HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"enccrv_2","complete"));
        encc2HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"enccrv_2","upcoming"));
        encc2HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"enccrv_2","urgent"));
        encc2HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"enccrv_2","expired"));
        encchashMapHashMap.put("ENCC2",encc2HashMap);

        HashMap<String,String> encc3HashMap = new HashMap<String,String>();
        encc3HashMap.put("Completed",returnCountFromALertQueryForANC(from,to,"enccrv_3","complete"));
        encc3HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"enccrv_3","upcoming"));
        encc3HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"enccrv_3","urgent"));
        encc3HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"enccrv_3","expired"));
        encchashMapHashMap.put("ENCC3",encc3HashMap);

        return encchashMapHashMap;
    }
    public String returnCountFromALertQueryForANC(Date from, Date to, String visitcode, String status){
        String count = "0";
        CommonRepository commonRepository = Context.getInstance().commonrepository("household");
        Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
        cursor.moveToFirst();
        try {
            count = cursor.getString(0);
            cursor.close();
        }catch (Exception e){
            cursor.close();
        }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
        return count;
    }
}

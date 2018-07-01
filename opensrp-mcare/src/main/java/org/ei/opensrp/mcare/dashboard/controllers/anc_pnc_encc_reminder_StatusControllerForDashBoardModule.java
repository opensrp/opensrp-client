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
       anc1HashMap.put("Completed",returnCountFromALertQueryForANC1Complete(from,to,"ancrv_1","complete",riskflag));
       anc1HashMap.put("Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_1","upcoming",riskflag));
       anc1HashMap.put("Post Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_1","urgent",riskflag));
       anc1HashMap.put("Expired",returnCountFromALertQueryForANC1Expired(from,to,"ancrv_1","expired",riskflag));
       anchashMapHashMap.put("ANC1",anc1HashMap);

       HashMap<String,String> anc2HashMap = new HashMap<String,String>();
       anc2HashMap.put("Completed",returnCountFromALertQueryForANC2Complete(from,to,"ancrv_2","complete",riskflag));
       anc2HashMap.put("Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_2","upcoming",riskflag));
       anc2HashMap.put("Post Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_2","urgent",riskflag));
       anc2HashMap.put("Expired",returnCountFromALertQueryForANC2Expired(from,to,"ancrv_2","expired",riskflag));
       anchashMapHashMap.put("ANC2",anc2HashMap);

       HashMap<String,String> anc3HashMap = new HashMap<String,String>();
       anc3HashMap.put("Completed",returnCountFromALertQueryForANC3Complete(from,to,"ancrv_3","complete",riskflag));
       anc3HashMap.put("Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_3","upcoming",riskflag));
       anc3HashMap.put("Post Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_3","urgent",riskflag));
       anc3HashMap.put("Expired",returnCountFromALertQueryForANC3Expired(from,to,"ancrv_3","expired",riskflag));
       anchashMapHashMap.put("ANC3",anc3HashMap);

       HashMap<String,String> anc4HashMap = new HashMap<String,String>();
       anc4HashMap.put("Completed",returnCountFromALertQueryForANC4Complete(from,to,"ancrv_4","complete",riskflag));
       anc4HashMap.put("Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_4","upcoming",riskflag));
       anc4HashMap.put("Post Due",returnCountFromALertQueryForANCdue(from,to,"ancrv_4","urgent",riskflag));
       anc4HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"ancrv_4","expired",riskflag));
       anchashMapHashMap.put("ANC4",anc4HashMap);

       return anchashMapHashMap;
    }

    @Override
    public HashMap<String, HashMap<String, String>> pncVisitQuery(Date from, Date to, String riskflag) {
        HashMap<String,HashMap<String,String>> pnchashMapHashMap = new HashMap<String, HashMap<String, String>>();

        HashMap<String,String> pnc1HashMap = new HashMap<String,String>();
        pnc1HashMap.put("Completed",returnCountFromALertQueryForPNC1Complete(from,to,"pncrv_1","complete",riskflag));
        pnc1HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_1","upcoming",riskflag));
        pnc1HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_1","urgent",riskflag));
        pnc1HashMap.put("Expired",returnCountFromALertQueryForPNC1Expired(from,to,"pncrv_1","expired",riskflag));
        pnchashMapHashMap.put("PNC1",pnc1HashMap);

        HashMap<String,String> pnc2HashMap = new HashMap<String,String>();
        pnc2HashMap.put("Completed",returnCountFromALertQueryForPNC2Complete(from,to,"pncrv_2","complete",riskflag));
        pnc2HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_2","upcoming",riskflag));
        pnc2HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_2","urgent",riskflag));
        pnc2HashMap.put("Expired",returnCountFromALertQueryForPNC2Expired(from,to,"pncrv_2","expired",riskflag));
        pnchashMapHashMap.put("PNC2",pnc2HashMap);

        HashMap<String,String> pnc3HashMap = new HashMap<String,String>();
        pnc3HashMap.put("Completed",returnCountFromALertQueryForPNC3Complete(from,to,"pncrv_3","complete",riskflag));
        pnc3HashMap.put("Due",returnCountFromALertQueryForANC(from,to,"pncrv_3","upcoming",riskflag));
        pnc3HashMap.put("Post Due",returnCountFromALertQueryForANC(from,to,"pncrv_3","urgent",riskflag));
        pnc3HashMap.put("Expired",returnCountFromALertQueryForANC(from,to,"pncrv_3","expired",riskflag));
        pnchashMapHashMap.put("PNC3",pnc3HashMap);



        return pnchashMapHashMap;
    }

    @Override
    public HashMap<String, HashMap<String, String>> neonatalVisitQuery(Date from, Date to, String riskflag) {
        HashMap<String,HashMap<String,String>> encchashMapHashMap = new HashMap<String, HashMap<String, String>>();

        HashMap<String,String> encc1HashMap = new HashMap<String,String>();
        encc1HashMap.put("Completed",returnCountFromALertQueryForENCC1Complete(from,to,"enccrv_1","complete",riskflag));
        encc1HashMap.put("Due",returnCountFromALertQueryForENCC(from,to,"enccrv_1","upcoming",riskflag));
        encc1HashMap.put("Post Due",returnCountFromALertQueryForENCC(from,to,"enccrv_1","urgent",riskflag));
        encc1HashMap.put("Expired",returnCountFromALertQueryForENCC1Expired(from,to,"enccrv_1","expired",riskflag));
        encchashMapHashMap.put("ENCC1",encc1HashMap);

        HashMap<String,String> encc2HashMap = new HashMap<String,String>();
        encc2HashMap.put("Completed",returnCountFromALertQueryForENCC2Complete(from,to,"enccrv_2","complete",riskflag));
        encc2HashMap.put("Due",returnCountFromALertQueryForENCC(from,to,"enccrv_2","upcoming",riskflag));
        encc2HashMap.put("Post Due",returnCountFromALertQueryForENCC(from,to,"enccrv_2","urgent",riskflag));
        encc2HashMap.put("Expired",returnCountFromALertQueryForENCC2Expired(from,to,"enccrv_2","expired",riskflag));
        encchashMapHashMap.put("ENCC2",encc2HashMap);

        HashMap<String,String> encc3HashMap = new HashMap<String,String>();
        encc3HashMap.put("Completed",returnCountFromALertQueryForENCC3Complete(from,to,"enccrv_3","complete",riskflag));
        encc3HashMap.put("Due",returnCountFromALertQueryForENCC(from,to,"enccrv_3","upcoming",riskflag));
        encc3HashMap.put("Post Due",returnCountFromALertQueryForENCC(from,to,"enccrv_3","urgent",riskflag));
        encc3HashMap.put("Expired",returnCountFromALertQueryForENCC(from,to,"enccrv_3","expired",riskflag));
        encchashMapHashMap.put("ENCC3",encc3HashMap);

        return encchashMapHashMap;
    }

    private String returnCountFromALertQueryForENCC(Date from, Date to, String visitcode, String status, String riskstatus) {
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '" + visitcode + "' and status = '" + status + "' and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from mcarechild Left Join alerts on alerts.caseID = mcarechild.id where visitCode = '" + visitcode + "' and status = '" + status + "'and (mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)) and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }

    public String returnCountFromALertQueryForANC(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '" + visitcode + "' and status = '" + status + "' and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id where visitCode = '" + visitcode + "' and status = '" + status + "'and mcaremother.FWSORTVALUE>0 and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }
    public String returnCountFromALertQueryForANCdue(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and visitCode = '" + visitcode + "' and status = '" + status + "' and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and visitCode = '" + visitcode + "' and status = '" + status + "'and mcaremother.FWSORTVALUE>0 and (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }
    public String returnCountFromALertQueryForANC1Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "((visitcode = 'ancrv_1' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-145 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC1DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC1DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC1DATE%' and  mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC3DATE,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC3DATE,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC2_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC2DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "((visitcode = 'ancrv_1' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-145 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'upcoming' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'urgent' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_2' and status = 'expired' and mcaremother.details not like '%ANC1DATE%' and  (date(startDate,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC1DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE,'-84 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC1DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC1DATE%' and  mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC3DATE,'-56 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC3DATE,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC1DATE%' and mcaremother.details  not like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC2_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC2DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }
    public String returnCountFromALertQueryForANC2Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "((visitcode = 'ancrv_2' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-28 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-33 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'upcoming' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'urgent' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-33') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC2DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE,'-28 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC2DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-33 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC2DATE%' and  mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC3DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "((visitcode = 'ancrv_2' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-28 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-33 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-89 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'upcoming' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'urgent' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or (visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC2DATE%' and  (date(startDate,'-33') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC2DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE,'-28 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC2DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-33 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC2DATE%' and  mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC3DATE%' and mcaremother.details  not like '%FWANC2DATE%' and mcaremother.details  not like '%FWANC4DATE%') and mcaremother.details like '%\"ANC3_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC3DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }
    public String returnCountFromALertQueryForANC3Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "((visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC3DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC3DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%' and \n" +
                    "mcaremother.FWSORTVALUE>0 and \n" +
                    "((visitcode = 'ancrv_3' and status = 'expired' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'upcoming' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'urgent' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))) \n" +
                    "or (visitcode = 'ancrv_4' and status = 'expired' and mcaremother.details not like '%ANC3DATE%' and  (date(startDate,'-61 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC3DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"upcoming\"%' )) where (date(completedFWANC4DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWANC4DATE%' and mcaremother.details  not like '%FWANC3DATE%') and mcaremother.details like '%\"ANC4_current_formStatus\":\"urgent\"%' )) where (date(completedFWANC4DATE,'-5 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")  ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
//        if(count.equalsIgnoreCase("0")&&visitcode.equalsIgnoreCase("ancrv_1")&&status.equalsIgnoreCase("complete")){
//            cursor = commonRepository.RawCustomQueryForAdapter("select count(*) from alerts where visitCode = '"+visitcode+"' and status = '"+status+"' and (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))");
//        }
            return count;
        }
    }

    public String returnCountFromALertQueryForANC1Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC1DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC1DATE%' and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC1DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC1DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC1DATE%' and mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC1DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForANC2Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC2DATE%' and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC2DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC2DATE%' and mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC2DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForANC3Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC3DATE%' and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC3DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC3DATE%' and mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC3DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForANC4Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC4DATE%' and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC4DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWANC4DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWANC4DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWANC4DATE%' and mcaremother.FWSORTVALUE>0 and ( Is_PNC is null or Is_PNC = '0') and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%')) where (date(FWANC4DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForPNC1Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC1DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC1DATE%')) where (date(FWPNC1DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC1DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC1DATE%' and mcaremother.FWSORTVALUE>0)) where (date(FWPNC1DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForPNC2Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC2DATE%')) where (date(FWPNC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC2DATE%' and mcaremother.FWSORTVALUE>0)) where (date(FWPNC2DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForPNC3Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC3DATE%')) where (date(FWPNC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "   FROM mcaremother where mcaremother.details like '%FWPNC3DATE%' and mcaremother.FWSORTVALUE>0)) where (date(FWPNC3DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForPNC1Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "(visitcode = 'pncrv_3' and status = 'upcoming' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'urgent' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-6 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'expired' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-7 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'upcoming' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'urgent' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-1 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'expired' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC1DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC3DATE,'-4 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC1DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC3DATE,'-6 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC2DATE%' and mcaremother.details  not like '%FWPNC1DATE%' and mcaremother.details  not like '%FWPNC3DATE%') and mcaremother.details like '%\"pnc2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC2DATE%' and mcaremother.details  not like '%FWPNC1DATE%' and mcaremother.details  not like '%FWPNC3DATE%') and mcaremother.details like '%\"pnc2_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC2DATE,'-1 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))" +
                    ") ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "mcaremother.FWSORTVALUE>0 and \n" +
                    "(visitcode = 'pncrv_3' and status = 'upcoming' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'urgent' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-6 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'expired' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-7 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'upcoming' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'urgent' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-1 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'pncrv_2' and status = 'expired' and mcaremother.details not like '%PNC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC1DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC3DATE,'-4 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC1DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC3DATE,'-6 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC2DATE%' and mcaremother.details  not like '%FWPNC1DATE%' and mcaremother.details  not like '%FWPNC3DATE%') and mcaremother.details like '%\"pnc2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC2DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC2DATE%' and mcaremother.details  not like '%FWPNC1DATE%' and mcaremother.details  not like '%FWPNC3DATE%') and mcaremother.details like '%\"pnc2_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC2DATE,'-1 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))" +
                    ") ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }

            return count;
        }
    }

    public String returnCountFromALertQueryForPNC2Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "(visitcode = 'pncrv_3' and status = 'upcoming' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'urgent' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate,'-2 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'expired' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate,'-3 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC2DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC2DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC3DATE,'-2 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))" +
                    ") ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcaremother Left Join alerts on alerts.caseID = mcaremother.id  where \n" +
                    "mcaremother.FWSORTVALUE>0 and \n" +
                    "(visitcode = 'pncrv_3' and status = 'upcoming' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'urgent' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate,'-2 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'pncrv_3' and status = 'expired' and mcaremother.details not like '%PNC2DATE%' and  (date(startDate,'-3 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC2DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWPNC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcaremother.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWPNC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcaremother.details,'FWPNC3DATE','^') as replaced\n" +
                    "  FROM mcaremother where (mcaremother.details like '%FWPNC3DATE%' and mcaremother.details  not like '%FWPNC2DATE%') and mcaremother.details like '%\"pnc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWPNC3DATE,'-2 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))" +
                    ") ");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }

            return count;
        }
    }

    public String returnCountFromALertQueryForENCC1Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcarechild Left Join alerts on alerts.caseID = mcarechild.id  where \n" +
                    "((visitcode = 'enccrv_3' and status = 'upcoming' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'urgent' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-6 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'expired' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-7 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'upcoming' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'urgent' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-1 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'expired' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-3 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC1DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC3DATE,'-4 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC1DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC3DATE,'-6 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC2DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC2DATE%' and mcarechild.details  not like '%FWENC1DATE%' and mcarechild.details  not like '%FWENC3DATE%') and mcarechild.details like '%\"encc2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC2DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWPNC2DATE%' and mcarechild.details  not like '%FWENC1DATE%' and mcarechild.details  not like '%FWENC3DATE%') and mcarechild.details like '%\"encc2_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC2DATE,'-1 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcarechild Left Join alerts on alerts.caseID = mcarechild.id  where \n" +
                    "(mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)) and\n" +
                    "((visitcode = 'enccrv_3' and status = 'upcoming' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-4 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'urgent' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-6 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'expired' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-7 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'upcoming' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'urgent' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-1 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or (visitcode = 'enccrv_2' and status = 'expired' and mcarechild.details not like '%FWENC1DATE%' and  (date(startDate,'-3 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC1DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC3DATE,'-4 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC1DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC3DATE,'-6 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC2DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC2DATE%' and mcarechild.details  not like '%FWENC1DATE%' and mcarechild.details  not like '%FWENC3DATE%') and mcarechild.details like '%\"encc2_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC2DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC2DATE%' and mcarechild.details  not like '%FWENC1DATE%' and mcarechild.details  not like '%FWENC3DATE%') and mcarechild.details like '%\"encc2_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC2DATE,'-1 day') BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ")");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }

            return count;
        }
    }

    public String returnCountFromALertQueryForENCC2Expired(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcarechild Left Join alerts on alerts.caseID = mcarechild.id  where \n" +
                    "((visitcode = 'enccrv_3' and status = 'upcoming' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'urgent' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'expired' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate,'-2 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC2DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC2DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ") \n");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("select count (*) from mcarechild Left Join alerts on alerts.caseID = mcarechild.id  where \n" +
                    "(mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)) and\n" +
                    "((visitcode = 'enccrv_3' and status = 'upcoming' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'urgent' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"'))) \n" +
                    "or (visitcode = 'enccrv_3' and status = 'expired' and mcarechild.details not like '%FWENC2DATE%' and  (date(startDate,'-2 day') BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))\n" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC2DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"upcoming\"%' )) where (date(completedFWENC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    "or mcarechild.id in (Select id from (SELECT id,SUBSTR(replaced, pos+4, 10) AS completedFWENC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "  FROM (SELECT *,replace(mcarechild.details,'FWENC3DATE','^') as replaced\n" +
                    "  FROM mcarechild where (mcarechild.details like '%FWENC3DATE%' and mcarechild.details  not like '%FWENC2DATE%') and mcarechild.details like '%\"encc3_current_formStatus\":\"urgent\"%' )) where (date(completedFWENC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "'))))" +
                    ") \n");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }

            return count;
        }
    }

    public String returnCountFromALertQueryForENCC1Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC1DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC1DATE%')) where (date(FWENCC1DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC1DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC1DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC1DATE%' and (mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)))) where (date(FWENCC1DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }
    
    public String returnCountFromALertQueryForENCC2Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC2DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC2DATE%')) where (date(FWENCC2DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC2DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC2DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC2DATE%' and (mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)))) where (date(FWENCC2DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }

    public String returnCountFromALertQueryForENCC3Complete(Date from, Date to, String visitcode, String status, String riskstatus){
        if(riskstatus.equalsIgnoreCase("normal")) {
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC3DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC3DATE%')) where (date(FWENCC3DATE) BETWEEN date('" + format.format(from) + "') and date('" + format.format(to) + "')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }else{
            String count = "0";
            CommonRepository commonRepository = Context.getInstance().commonrepository("household");
            Cursor cursor = commonRepository.RawCustomQueryForAdapter("SELECT Count(*) FROM (SELECT SUBSTR(replaced, pos+4, 10) AS FWENCC3DATE from (SELECT *,instr(replaced,'^') AS pos\n" +
                    "   FROM (SELECT replace(mcarechild.details,'FWENCC3DATE','^') as replaced\n" +
                    "   FROM mcarechild where mcarechild.details like '%FWENCC3DATE%' and (mcarechild.relationalid in (select id from mcaremother where mcaremother.FWSORTVALUE>0)))) where (date(FWENCC3DATE) BETWEEN date('"+format.format(from)+"') and date('"+format.format(to)+"')))");
            cursor.moveToFirst();
            try {
                count = cursor.getString(0);
                cursor.close();
            } catch (Exception e) {
                cursor.close();
            }
            return count;
        }
    }
    

}

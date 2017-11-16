package org.ei.opensrp.mcare.child;


import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Raihan Ahmed on 3/22/15.
 */
public class childSort implements SortOption {




    public childSort() {

    }

    @Override
    public String name() {
        return Context.getInstance().applicationContext().getResources().getString(R.string.due_status);
    }

    @Override
    public SmartRegisterClients sort(SmartRegisterClients allClients) {
        Collections.sort(allClients, commoncomparator);
        return allClients;
    }

    Comparator<SmartRegisterClient> commoncomparator = new Comparator<SmartRegisterClient>() {
        @Override
        public int compare(SmartRegisterClient oneClient, SmartRegisterClient anotherClient2) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) oneClient;

            List<Alert> alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient.entityId(), "ELCO PSRF");
            String alertforclient1 = "not synced";
            for(int i = 0;i<alertlist_for_client.size();i++){
                alertforclient1 = alertlist_for_client.get(i).status().value();
            }
//            if((commonPersonObjectClient.getDetails().get("FWPSRSTS")!=null) && !(((commonPersonObjectClient.getDetails().get("psrf_schedule_logic")!=null?commonPersonObjectClient.getDetails().get("psrf_schedule_logic"):"").equalsIgnoreCase("1")) || commonPersonObjectClient.getDetails().get("FWPSRSTS").equalsIgnoreCase("2"))){
//                alertforclient1 = "pregnant";
//            }
            if(commonPersonObjectClient.getDetails().get("FWPSRSTS")!=null && commonPersonObjectClient.getDetails().get("psrf_schedule_logic")!=null && !commonPersonObjectClient.getDetails().get("psrf_schedule_logic").trim().equalsIgnoreCase("")){
                if( ((commonPersonObjectClient.getDetails().get("psrf_schedule_logic").equalsIgnoreCase("0")) || commonPersonObjectClient.getDetails().get("FWPSRSTS").equalsIgnoreCase("01"))){
                    alertforclient1 = "pregnant";
                }
            }







            CommonPersonObjectClient commonPersonObjectClient2 = (CommonPersonObjectClient) anotherClient2;
            List<Alert> alertlist_for_client2 = Context.getInstance().alertService().findByEntityIdAndAlertNames(commonPersonObjectClient2.entityId(), "ELCO PSRF");
            String alertforclient2 = "not synced";
            for(int i = 0;i<alertlist_for_client2.size();i++){
                alertforclient2 = alertlist_for_client2.get(i).status().value();
            }
            if(commonPersonObjectClient2.getDetails().get("FWPSRSTS")!=null && commonPersonObjectClient2.getDetails().get("psrf_schedule_logic")!=null && !commonPersonObjectClient2.getDetails().get("psrf_schedule_logic").trim().equalsIgnoreCase("")){
                if( ((commonPersonObjectClient2.getDetails().get("psrf_schedule_logic").equalsIgnoreCase("0")) || commonPersonObjectClient2.getDetails().get("FWPSRSTS").equalsIgnoreCase("01"))){
                    alertforclient2 = "pregnant";
                }
            }

//            if((commonPersonObjectClient2.getDetails().get("FWPSRSTS")!=null) && !(((commonPersonObjectClient2.getDetails().get("psrf_schedule_logic")!=null?commonPersonObjectClient2.getDetails().get("psrf_schedule_logic"):"").equalsIgnoreCase("1")) || commonPersonObjectClient2.getDetails().get("FWPSRSTS").equalsIgnoreCase("2"))){
//                alertforclient2 = "pregnant";
//            }

            Log.v("alertclient1", alertforclient1);
            Log.v("alertclient2", alertforclient2);
            if(alertforclient1.equalsIgnoreCase(alertforclient2)){
                if(commonPersonObjectClient.getDetails().get("JiVitAHHID").equalsIgnoreCase("")||commonPersonObjectClient2.getDetails().get("JiVitAHHID").equalsIgnoreCase("")){
                    return new Integer("0").compareTo(new Integer("0"));
                }
                return (new Integer(commonPersonObjectClient.getDetails().get("JiVitAHHID")!=null?commonPersonObjectClient.getDetails().get("JiVitAHHID"):"0").compareTo(new Integer(commonPersonObjectClient2.getDetails().get("JiVitAHHID")!=null?commonPersonObjectClient2.getDetails().get("JiVitAHHID"):"0")));

            }

//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("normal") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("upcoming") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("expired") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("not synced") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("complete") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("urgent") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("upcoming") && alertforclient2.equalsIgnoreCase("not synced") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("upcoming") && alertforclient2.equalsIgnoreCase("normal") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("upcoming") && alertforclient2.equalsIgnoreCase("expired") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("upcoming") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("upcoming") && alertforclient2.equalsIgnoreCase("complete") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("normal") && alertforclient2.equalsIgnoreCase("expired") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("normal") && alertforclient2.equalsIgnoreCase("not synced") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("normal") && alertforclient2.equalsIgnoreCase("complete") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("normal") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("expired") && alertforclient2.equalsIgnoreCase("not synced") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("expired") && alertforclient2.equalsIgnoreCase("complete") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("expired") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("not synced") && alertforclient2.equalsIgnoreCase("complete") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("not synced") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }
//            if(alertforclient1.equalsIgnoreCase("complete") && alertforclient2.equalsIgnoreCase("pregnant") ){
//                return -1;
//            }else{
//                return 1;
//            }
            Integer alertweight1 = 0;
            Integer alertweight2 = 0;

            ////////assign alertweights///////////////////////////////////////
            if(alertforclient1.equalsIgnoreCase("urgent")){
                alertweight1 = 7;
            }else if(alertforclient1.equalsIgnoreCase("upcoming")){
                alertweight1 = 6;
            }else if(alertforclient1.equalsIgnoreCase("normal")){
                alertweight1 = 5;
            }else  if(alertforclient1.equalsIgnoreCase("expired")) {
                alertweight1 = 4;
            }else  if(alertforclient1.equalsIgnoreCase("not synced")){
                alertweight1 = 3;
            }else  if(alertforclient1.equalsIgnoreCase("complete")) {
                alertweight1 = 2;
//            }
            }else  if(alertforclient1.equalsIgnoreCase("pregnant")){
                alertweight1 = 1;
            }

            if(alertforclient2.equalsIgnoreCase("urgent")){
                alertweight2 = 7;
            }else if(alertforclient2.equalsIgnoreCase("upcoming")){
                alertweight2 = 6;
            }else if(alertforclient2.equalsIgnoreCase("normal")){
                alertweight2 = 5;
            }else  if(alertforclient2.equalsIgnoreCase("expired")) {
                alertweight2 = 4;
            }else  if(alertforclient2.equalsIgnoreCase("not synced")){
                alertweight2 = 3;
            }else  if(alertforclient2.equalsIgnoreCase("complete")) {
                alertweight2 = 2;
//            }
            }else  if(alertforclient2.equalsIgnoreCase("pregnant")){
                alertweight2 = 1;
            }

            Log.v("alertclient1", alertforclient1);
            Log.v("alertclient2", alertforclient2);

            Log.v("alert-compares","alertclient1 : "+ alertforclient1+ " ; alertclient2 :"+ alertforclient2+ "the alertweight compares to "+ alertweight1.compareTo(alertweight2));
            return alertweight2.compareTo(alertweight1);


        }

    };
}

package org.ei.opensrp.gizi.gizi;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;
import org.ei.opensrp.sync.ClientProcessor;

import java.util.HashMap;
import java.util.Map;

import util.KMS.KmsCalc;
import util.KMS.KmsPerson;
import util.ZScore.ZScoreSystemCalculation;

/**
 * Created by Iq on 15/09/16.
 * This class is used to create preload (prepopulate) data which used for next visit registration,
 * and other calculation that cannot conduct by Form, such as Nutrition status etc.
 */

public class KmsHandler  implements FormSubmissionHandler {
    static String bindobject = "anak";
    private ClientProcessor clientProcessor;
    private  org.ei.opensrp.Context context;
    public KmsHandler() {

    }

    @Override
    public void handle(FormSubmission submission){
        String entityID = submission.entityId();
        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(entityID);
        Long tsLong = System.currentTimeMillis()/1000;

        String[]history = submission.getFieldValue("history_berat")!= null ? split(submission.getFieldValue("history_berat")) : new String []{"0","0"};
        String berats = history[1];
        String[] history_berat = berats.split(",");
        double berat_sebelum = Double.parseDouble((history_berat.length) >=3 ? (history_berat[(history_berat.length)-3]) : "0");
        String umurs = history[0];
        String[] history_umur = umurs.split(",");
        String tinggi = submission.getFieldValue("history_tinggi")!= null ? submission.getFieldValue("history_tinggi") :"0#0";
        String lastVisitDate = submission.getFieldValue("tanggalPenimbangan") != null ? submission.getFieldValue("tanggalPenimbangan") : "-";
        String gender = submission.getFieldValue("gender") != null ? submission.getFieldValue("gender") : "-";
        String tgllahir = submission.getFieldValue("tanggalLahirAnak") != null
                ? submission.getFieldValue("tanggalLahirAnak")
                : "-";
        String dateOfBirth = tgllahir.substring(0, tgllahir.indexOf("T"));

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();

        detailsRepository.add(entityID, "preload_umur", umurs, tsLong);
        detailsRepository.add(entityID, "berat_preload", submission.getFieldValue("history_berat")!= null ? submission.getFieldValue("history_berat") : "0:0", tsLong);
        detailsRepository.add(entityID, "history_umur", umurs, tsLong);

        // detailsRepository.add(entityID, "preload_history_tinggi", submission.getFieldValue("history_tinggi")!= null ? submission.getFieldValue("history_tinggi") :"0#0", tsLong);
        detailsRepository.add(entityID, "preload_history_tinggi", tinggi, tsLong);
        detailsRepository.add(entityID, "kunjunganSebelumnya", lastVisitDate, tsLong);

        if(submission.getFieldValue("tanggalPenimbangan") != null)
        {
            if(new ZScoreSystemCalculation().dailyUnitCalculationOf(dateOfBirth, lastVisitDate) < 1857) {
                double weight = Double.parseDouble(submission.getFieldValue("beratBadan") != null ? submission.getFieldValue("beratBadan") : "0");
                double length = Double.parseDouble(submission.getFieldValue("tinggiBadan") != null ? submission.getFieldValue("tinggiBadan") : "0");

                ZScoreSystemCalculation zScore = new ZScoreSystemCalculation();

                double weight_for_age = zScore.countWFA(gender, dateOfBirth, lastVisitDate, weight);
                String wfaStatus = zScore.getWFAZScoreClassification(weight_for_age);
                if (length != 0) {
                    double heigh_for_age = zScore.countHFA(gender, dateOfBirth, lastVisitDate, length);
                    String hfaStatus = zScore.getHFAZScoreClassification(heigh_for_age);

                    double wight_for_lenght = 0.0;
                    String wflStatus = "";
                    if (zScore.dailyUnitCalculationOf(dateOfBirth, lastVisitDate) < 730) {
                        wight_for_lenght = zScore.countWFL(gender, weight, length);
                    } else {
                        wight_for_lenght = zScore.countWFH(gender, weight, length);
                    }
                    wflStatus = zScore.getWFLZScoreClassification(wight_for_lenght);

                    detailsRepository.add(entityID, "underweight", wfaStatus, tsLong);
                    detailsRepository.add(entityID, "stunting", hfaStatus, tsLong);
                    detailsRepository.add(entityID, "wasting", wflStatus, tsLong);

                } else {
                    detailsRepository.add(entityID, "underweight", wfaStatus, tsLong);
                    detailsRepository.add(entityID, "stunting", "-", tsLong);
                    detailsRepository.add(entityID, "wasting", "-", tsLong);


                }
            }
        }
        /**
         * kms calculation
         * NOTE - Need a better way to handle z-score data to sqllite
         */


        double berat= Double.parseDouble(submission.getFieldValue("beratBadan") != null ? submission.getFieldValue("beratBadan") : "0");
        double beraSebelum = Double.parseDouble((history_berat.length) >=2 ? (history_berat[(history_berat.length)-2]) : "0");
        String tanggal_sebelumnya = (submission.getFieldValue("kunjunganSebelumnya") != null ? submission.getFieldValue("kunjunganSebelumnya") : "0");

        if(submission.getFieldValue("tanggalPenimbangan") != null) {


            //KMS calculation lastVisitDate
            KmsPerson data = new KmsPerson(!gender.toLowerCase().contains("em"), dateOfBirth, berat, beraSebelum, lastVisitDate, berat_sebelum, tanggal_sebelumnya);
            KmsCalc calculator = new KmsCalc();
            ////System.out.println("tanggal penimbangan = "+submission.getFieldValue("tanggalPenimbangan")+", "+lastVisitDate);

            String duat = history_berat.length <= 2  ? "-" : (Integer.parseInt(history_umur[history_umur.length-1])/30) - (Integer.parseInt(history_umur[history_umur.length-2])/30) >=2 ? "-" :calculator.cek2T(data);
            String status = history_berat.length <= 2 ? "No" : calculator.cekWeightStatus(data);

            detailsRepository.add(entityID, "bgm", calculator.cekBGM(data), tsLong);
            detailsRepository.add(entityID, "dua_t", duat, tsLong);
            detailsRepository.add(entityID, "garis_kuning", calculator.cekBawahKuning(data), tsLong);
            detailsRepository.add(entityID, "nutrition_status", status, tsLong);

            if(submission.getFieldValue("vitA") != null){
                if(submission.getFieldValue("vitA").equalsIgnoreCase("yes") || submission.getFieldValue("vitA").equalsIgnoreCase("ya")){
                    detailsRepository.add(entityID, "lastVitA", submission.getFieldValue("tanggalPenimbangan"), tsLong);
                }
            }else{
                detailsRepository.add(entityID, "lastVitA", submission.getFieldValue("lastVitA"), tsLong);
            }
            if(submission.getFieldValue("obatcacing") != null){
                if(submission.getFieldValue("obatcacing").equalsIgnoreCase("yes") || submission.getFieldValue("obatcacing").equalsIgnoreCase("ya")){
                    detailsRepository.add(entityID, "lastAnthelmintic", submission.getFieldValue("tanggalPenimbangan"), tsLong);
                }
            }else{
                detailsRepository.add(entityID, "lastAnthelmintic", submission.getFieldValue("lastAnthelmintic"), tsLong);
            }
        }
    }

    private String[]split(String data){
        if(!data.contains(":"))
            return new String[]{"0","0"};
        String []temp = data.split(",");
        String []result = {"",""};
        for(int i=0;i<temp.length;i++){
            result[0]=result[0]+","+temp[i].split(":")[0];
            result[1]=result[1]+","+temp[i].split(":")[1];
        }

        result[0]=result[0].substring(1,result[0].length());
        result[1]=result[1].substring(1,result[1].length());

        if(result[0].length()>2 && result[1].length()>2){
            result[0] = result[0].substring(0,2).equals("0,")? result[0].substring(2,result[0].length()):result[0];
            result[1] = result[1].substring(0,2).equals("0,")? result[1].substring(2,result[1].length()):result[1];
        }

        return result;
    }
}

package org.ei.opensrp.indonesia.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.indonesia.repository.AllKartuIbus;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.AnakClient;

import java.util.Map;

import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BABY_NO;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BIRTH_CONDITION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BIRTH_PLACE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BIRTH_WEIGHT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.CHILD_CURRENT_WEIGTH;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.CHILD_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.CHILD_VISIT_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.HB_GIVEN;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_BCG_AND_POLIO1;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_DPT_HB1_POLIO2;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_DPT_HB2_POLIO3;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_DPT_HB3_POLIO4;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_HB_0_7_DATES;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IMMUNIZATION_MEASLES;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.IS_HIGH_RISK_CHILD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.SERVICE_AT_BIRTH;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.HUSBAND_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_AGE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NUMBER;

/**
 * Created by Dimas Ciputra on 4/17/15.
 */
public class ProfileChildDetailController {
    private final Context context;
    private final String caseId;
    private final AllKohort allKohort;
    private final AllKartuIbus allKartuIbus;

    public ProfileChildDetailController(Context context, String caseId, AllKartuIbus allKartuIbus, AllKohort allKohort) {
        this.context = context;
        this.caseId = caseId;
        this.allKohort = allKohort;
        this.allKartuIbus = allKartuIbus;
    }

    public AnakClient get() {
        Anak a = allKohort.findAnakWithCaseID(caseId);
        Ibu ibu = allKohort.findIbu(a.getIbuCaseId());
        KartuIbu kartuIbu = allKartuIbus.findByCaseID(ibu.getKartuIbuId());

        AnakClient anakClient = new AnakClient(a.getCaseId(), a.getGender(),
                a.getDetail(BIRTH_WEIGHT),
                a.getDetails())
                .withMotherName(kartuIbu.getDetail(MOTHER_NAME))
                .withMotherAge(kartuIbu.getDetail(MOTHER_AGE))
                .withFatherName(kartuIbu.getDetail(HUSBAND_NAME))
                .withDOB(a.getDateOfBirth())
                .withName(a.getDetail(CHILD_NAME))
                .withKINumber(kartuIbu.getDetail(MOTHER_NUMBER))
                .withBirthCondition(a.getDetail(BIRTH_CONDITION))
                .withServiceAtBirth(a.getDetail(SERVICE_AT_BIRTH))
                .withIsHighRisk(a.getDetail(IS_HIGH_RISK_CHILD));
        anakClient.setHb07(a.getDetail(IMMUNIZATION_HB_0_7_DATES));
        anakClient.setBcgPol1(a.getDetail(IMMUNIZATION_BCG_AND_POLIO1));
        anakClient.setDptHb1Pol2(a.getDetail(IMMUNIZATION_DPT_HB1_POLIO2));
        anakClient.setDptHb2Pol3(a.getDetail(IMMUNIZATION_DPT_HB2_POLIO3));
        anakClient.setDptHb3Pol4(a.getDetail(IMMUNIZATION_DPT_HB3_POLIO4));
        anakClient.setCampak(a.getDetail(IMMUNIZATION_MEASLES));
        anakClient.setBirthPlace(ibu.getDetail(BIRTH_PLACE));
        anakClient.setHbGiven(a.getDetail(HB_GIVEN));
        anakClient.setVisitDate(a.getDetail(CHILD_VISIT_DATE));
        anakClient.setCurrentWeight(a.getDetail(CHILD_CURRENT_WEIGTH));
        anakClient.setBabyNo(a.getDetail(BABY_NO));
        anakClient.setPregnancyAge(ibu.getDetail(AllConstantsINA.KartuPNCFields.PREGNANCY_AGE));
        return anakClient;
    }

    public Map<String, String> detailMap() {
        Anak child = allKohort.findAnakWithCaseID(caseId);
        return child.getDetails();
    }

    public String getClientJson() {
        return new Gson().toJson(get());
    }

}

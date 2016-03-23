package org.ei.opensrp.indonesia.view.controller;

import com.google.common.collect.Iterables;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.ServiceProvided;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.indonesia.repository.AllKartuIbus;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.KIChildClient;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClient;
import org.ei.opensrp.indonesia.view.contract.KartuIbuClients;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.util.EasyMap;
import org.ei.opensrp.util.Log;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.opensrp.domain.ServiceProvided.ANC_1_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_2_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_3_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_4_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.DELIVERY_PLAN_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.HB_TEST_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.IFA_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.TT_1_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.TT_2_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.TT_BOOSTER_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.COMPLICATION_HISTORY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.FETUS_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.FETUS_POSITION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.FETUS_SIZE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.HB_RESULT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.HEIGHT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.LILA_CHECK_RESULT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.PELVIC_DEFORMITY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.SUGAR_BLOOD_LEVEL;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.CHRONIC_DISEASE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.EDD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.HUSBAND_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.IS_HIGH_PRIORITY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.IS_HIGH_RISK;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.IS_HIGH_RISK_LABOUR;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.IS_HIGH_RISK_PREGNANCY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.KABUPATEN;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_ADDRESS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_AGE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_BLOOD_TYPE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_DOB;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_ABORTIONS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_OF_LIVING_CHILDREN;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_OF_PREGNANCIES;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_PARTUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.POSYANDU_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.PROPINSI;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.PUSKESMAS_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.UNIQUE_ID;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.VISITS_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.COMPLICATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.DELIVERY_METHOD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_DIASTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_SISTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.CONTRACEPTION_METHOD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.KELUARGA_BERENCANA;

import static java.lang.String.*;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class KartuIbuRegisterController  extends CommonController{
    private static final String ANC_1_ALERT_NAME = "ANC 1";
    private static final String ANC_2_ALERT_NAME = "ANC 2";
    private static final String ANC_3_ALERT_NAME = "ANC 3";
    private static final String ANC_4_ALERT_NAME = "ANC 4";
    private static final String IFA_1_ALERT_NAME = "IFA 1";
    private static final String IFA_2_ALERT_NAME = "IFA 2";
    private static final String IFA_3_ALERT_NAME = "IFA 3";
    private static final String LAB_REMINDER_ALERT_NAME = "REMINDER";
    private static final String TT_1_ALERT_NAME = "TT 1";
    private static final String TT_2_ALERT_NAME = "TT 2";
    private static final String HB_TEST_1_ALERT_NAME = "Hb Test 1";
    private static final String HB_TEST_2_ALERT_NAME = "Hb Test 2";
    private static final String HB_FOLLOWUP_TEST_ALERT_NAME = "Hb Followup Test";
    private static final String DELIVERY_PLAN_ALERT_NAME = "Delivery Plan";
    private static final String KI_CLIENTS_LIST = "KIClientsList";
    private static final String KB_IUD = "KB IUD";
    private static final String KB_IMPLANT = "KB Implant";
    public static final String STATUS_DATE_FIELD = "date";
    public static final String ANC_STATUS = "anc";
    public static final String PNC_STATUS = "pnc";
    public static final String STATUS_TYPE_FIELD = "type";

    private final AllKartuIbus allKartuIbus;
    private final Cache<String> cache;
    private final Cache<KartuIbuClients> kartuIbuClientsCache;
    private final AllKohort allKohort;
    private final ServiceProvidedService serviceProvidedService;
    private final AlertService alertService;

    public KartuIbuRegisterController(AllKartuIbus allKartuIbus, Cache<String> cache,
                                      ServiceProvidedService serviceProvidedService,
                                      AlertService alertService,
                                      Cache<KartuIbuClients> kartuIbuClientsCache,
                                      AllKohort allKohort) {
        this.allKartuIbus = allKartuIbus;
        this.cache = cache;
        this.kartuIbuClientsCache = kartuIbuClientsCache;
        this.allKohort = allKohort;
        this.serviceProvidedService = serviceProvidedService;
        this.alertService = alertService;
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService.findByEntityIdAndServiceNames(entityId,
                IFA_SERVICE_PROVIDED_NAME,
                TT_1_SERVICE_PROVIDED_NAME,
                TT_2_SERVICE_PROVIDED_NAME,
                TT_BOOSTER_SERVICE_PROVIDED_NAME,
                HB_TEST_SERVICE_PROVIDED_NAME,
                ANC_1_SERVICE_PROVIDED_NAME,
                ANC_2_SERVICE_PROVIDED_NAME,
                ANC_3_SERVICE_PROVIDED_NAME,
                ANC_4_SERVICE_PROVIDED_NAME,
                DELIVERY_PLAN_SERVICE_PROVIDED_NAME);
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs.add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(), serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId,
                ANC_1_ALERT_NAME,
                ANC_2_ALERT_NAME,
                ANC_3_ALERT_NAME,
                ANC_4_ALERT_NAME,
                IFA_1_ALERT_NAME,
                IFA_2_ALERT_NAME,
                IFA_3_ALERT_NAME,
                LAB_REMINDER_ALERT_NAME,
                TT_1_ALERT_NAME,
                TT_2_ALERT_NAME,
                HB_TEST_1_ALERT_NAME,
                HB_FOLLOWUP_TEST_ALERT_NAME,
                HB_TEST_2_ALERT_NAME,
                DELIVERY_PLAN_ALERT_NAME
        );
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    private List<AlertDTO> getKIAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId,
                KB_IUD,
                KB_IMPLANT);
        List<AlertDTO> alertDTOs = new ArrayList<>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    public KartuIbuClients getKartuIbuClients() {
        return kartuIbuClientsCache.get(KI_CLIENTS_LIST, new CacheableData<KartuIbuClients>() {
            @Override
            public KartuIbuClients fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.all();
                KartuIbuClients kartuIbuClients = new KartuIbuClients();

                for (KartuIbu kartuIbu : kartuIbus) {
                    kartuIbuClients.add(readKartuIbu(kartuIbu));
                }
                sortByName(kartuIbuClients);
                return kartuIbuClients;
            }
        });
    }

    public KartuIbuClient readKartuIbu(KartuIbu kartuIbu) {
        KartuIbuClient kartuIbuClient = new KartuIbuClient(kartuIbu.getCaseId(),
                kartuIbu.getDetail(PUSKESMAS_NAME), kartuIbu.getDetail(PROPINSI),
                kartuIbu.getDetail(KABUPATEN), kartuIbu.getDetail(POSYANDU_NAME),
                kartuIbu.getDetail(MOTHER_ADDRESS), kartuIbu.getDetail(MOTHER_NUMBER),
                kartuIbu.getDetail(MOTHER_NAME), kartuIbu.getDetail(MOTHER_AGE),
                kartuIbu.getDetail(MOTHER_BLOOD_TYPE),
                kartuIbu.getDetail(HUSBAND_NAME),
                kartuIbu.dusun())
                .withIsHighRiskPregnancy(kartuIbu.getDetail(IS_HIGH_RISK_PREGNANCY))
                .withDateOfBirth(kartuIbu.getDetail(MOTHER_DOB))
                .withParity(kartuIbu.getDetail(NUMBER_PARTUS))
                .withNumberOfAbortions(kartuIbu.getDetail(NUMBER_ABORTIONS))
                .withNumberOfPregnancies(kartuIbu.getDetail(NUMBER_OF_PREGNANCIES))
                .withNumberOfLivingChildren(kartuIbu.getDetail(NUMBER_OF_LIVING_CHILDREN))
                .withHighPriority(kartuIbu.getDetail(IS_HIGH_PRIORITY))
                .withIsHighRisk(kartuIbu.getDetail(IS_HIGH_RISK))
                .withEdd(kartuIbu.getDetail(EDD))
                .withHighRiskLabour(kartuIbu.getDetail(IS_HIGH_RISK_LABOUR))
                .withUniqueId(kartuIbu.getDetail(UNIQUE_ID));

        updateStatusInformation(kartuIbu, kartuIbuClient);
        updateChildrenInformation(kartuIbuClient);
        return kartuIbuClient;
    }

    private void sortByName(List<?extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.wifeName().compareToIgnoreCase(o2.wifeName());
            }
        });
    }

    private void updateChildrenInformation(KartuIbuClient kartuIbuClient) {
        List<Anak> children = allKohort.findAllAnakByKIId(kartuIbuClient.entityId());
        sortByDateOfBirth(children);
        Iterable<Anak> youngestTwoChildren = Iterables.skip(children, children.size() < 2 ? 0 : children.size() - 2);
        for (Anak child : youngestTwoChildren) {
            kartuIbuClient.addChild(new KIChildClient(child.getCaseId(), child.getGender(), child.getDateOfBirth()));
        }
    }

    private void sortByDateOfBirth(List<Anak> children) {
        sort(children, new Comparator<Anak>() {
            @Override
            public int compare(Anak child, Anak anotherChild) {
                if(isBlank(child.getDateOfBirth())) {
                    return 0;
                } else {
                    try {
                        return LocalDate.parse(child.getDateOfBirth()).compareTo(LocalDate.parse(anotherChild.getDateOfBirth()));
                    }catch (Exception e) {
                        Log.logError("date format error : " + child.getDateOfBirth());
                        return 0;
                    }

                }
            }
        });
    }

    //#TODO: Needs refactoring
    private void updateStatusInformation(KartuIbu kartuIbu, KartuIbuClient kartuIbuClient) {
        Ibu ibu = allKohort.findIbuWithOpenStatusByKIId(kartuIbu.getCaseId());

        kartuIbuClient.withKbMethod(kartuIbu.getDetail(CONTRACEPTION_METHOD));

        if(ibu == null) {
            kartuIbuClient.setIsInPNCorANC(false);
            kartuIbuClient.setIsPregnant(false);
        }

        if( ibu == null && kartuIbu.hasKBMethod()) {

            List<AlertDTO> alerts = getKIAlerts(kartuIbu.getCaseId());
            List<ServiceProvidedDTO> servicesProvided = getServicesProvided(kartuIbu.getCaseId());
            kartuIbuClient.setAlerts(alerts);
            kartuIbuClient.setServicesProvided(servicesProvided);
            kartuIbuClient.ancPreProcess();

            kartuIbuClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, KELUARGA_BERENCANA)
                    .put(STATUS_DATE_FIELD, kartuIbu.getDetail(VISITS_DATE)).map());
            kartuIbuClient.withKbStart(kartuIbu.getDetail(VISITS_DATE));
            return;
        }

        if(ibu!=null) {

            List<ServiceProvidedDTO> servicesProvided = getServicesProvided(ibu.getId());
            List<AlertDTO> alerts = getAlerts(ibu.getId());
            kartuIbuClient.setMotherId(ibu.getId());
            kartuIbuClient.setAlerts(alerts);
            kartuIbuClient.setServicesProvided(servicesProvided);
            kartuIbuClient.ancPreProcess();

            kartuIbuClient.setIsInPNCorANC(true);

            kartuIbuClient.setChronicDisease(ibu.getDetail(CHRONIC_DISEASE));
            kartuIbuClient.setrLila(ibu.getDetail(LILA_CHECK_RESULT));
            kartuIbuClient.setrHbLevels(ibu.getDetail(HB_RESULT));
            kartuIbuClient.setrTdDiastolik(ibu.getDetail(VITAL_SIGNS_TD_DIASTOLIC));
            kartuIbuClient.setrTdSistolik(ibu.getDetail(VITAL_SIGNS_TD_SISTOLIC));
            kartuIbuClient.setrBloodSugar(ibu.getDetail(SUGAR_BLOOD_LEVEL));
            kartuIbuClient.setrAbortus(kartuIbu.getDetail(NUMBER_ABORTIONS));
            kartuIbuClient.setrPartus(kartuIbu.getDetail(NUMBER_PARTUS));
            kartuIbuClient.setrPregnancyComplications(ibu.getDetail(COMPLICATION_HISTORY));
            kartuIbuClient.setrFetusNumber(ibu.getDetail(FETUS_NUMBER));
            kartuIbuClient.setrFetusSize(ibu.getDetail(FETUS_SIZE));
            kartuIbuClient.setrFetusPosition(ibu.getDetail(FETUS_POSITION));
            kartuIbuClient.setrPelvicDeformity(ibu.getDetail(PELVIC_DEFORMITY));
            kartuIbuClient.setrHeight(ibu.getDetail(HEIGHT));
            kartuIbuClient.setrDeliveryMethod(ibu.getDetail(DELIVERY_METHOD));
            kartuIbuClient.setLaborComplication(ibu.getDetail(COMPLICATION));

            if(ibu.isANC()) {
                kartuIbuClient
                        .withStatus(EasyMap.create(STATUS_TYPE_FIELD, ANC_STATUS)
                                .put(STATUS_DATE_FIELD, ibu.getReferenceDate()).map());
                kartuIbuClient.setIsPregnant(true);
            } else if(ibu.isPNC()) {
                kartuIbuClient.withStatus(EasyMap.create(STATUS_TYPE_FIELD, PNC_STATUS)
                        .put(STATUS_DATE_FIELD, ibu.getReferenceDate()).map());

                kartuIbuClient.setIsPregnant(false);
            }
            return;
        }
    }

    public boolean isMotherInANCorPNC(String entityId) {
        Ibu ibu = allKohort.findIbuWithOpenStatusByKIId(entityId);
        if(ibu==null) return false;
        return ibu.isANC() || ibu.isPNC();
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        return onRandomNameChars(client, getKartuIbuClients(), allKartuIbus.randomDummyName(), AllConstantsINA.DIALOG_DOUBLE_SELECTION_NUM);
    }

}

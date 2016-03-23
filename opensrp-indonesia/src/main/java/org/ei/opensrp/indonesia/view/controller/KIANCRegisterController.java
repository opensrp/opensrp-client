package org.ei.opensrp.indonesia.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.ServiceProvided;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.KIANCClient;
import org.ei.opensrp.indonesia.view.contract.KIANCClients;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import static java.util.Collections.sort;
import static org.ei.opensrp.domain.ServiceProvided.ANC_1_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_2_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_3_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.ANC_4_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.ALLERGY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.ANC_VISIT_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.COMPLICATION_HISTORY;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.HEIGHT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.HIGH_RISK_PREGNANCY_REASON;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.HPHT_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.IMMUNIZATION_TT_STATUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.LILA_CHECK_RESULT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.MOTHER_NUTRITION_STATUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.TRIMESTER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.WEIGHT_BEFORE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuANCFields.WEIGHT_CHECK_RESULT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.CHRONIC_DISEASE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.EDD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.HUSBAND_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.IS_HIGH_RISK_LABOUR;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_DOB;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_ABORTIONS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_PARTUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.PUSKESMAS_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.UNIQUE_ID;

import static java.lang.String.*;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIANCRegisterController extends CommonController {

    private static final String KI_ANC_CLIENTS_LIST = "KIANCClientsList";
    private static final String ANC_1_ALERT_NAME = "ANC 1";
    private static final String ANC_2_ALERT_NAME = "ANC 2";
    private static final String ANC_3_ALERT_NAME = "ANC 3";
    private static final String ANC_4_ALERT_NAME = "ANC 4";

    private final AllKohort allKohort;
    private final Cache<String> cache;
    private final Cache<KIANCClients> KIANCClientsCache;
    private final Cache<Villages> villagesCache;
    private final AlertService alertService;
    private final ServiceProvidedService serviceProvidedService;


    public KIANCRegisterController(AllKohort allKohort, AlertService alertService,ServiceProvidedService serviceProvidedService, Cache<String> cache, Cache<KIANCClients> KIANCClientsCache, Cache<Villages> villagesCache) {
        this.allKohort = allKohort;
        this.cache = cache;
        this.villagesCache = villagesCache;
        this.KIANCClientsCache = KIANCClientsCache;
        this.alertService = alertService;
        this.serviceProvidedService = serviceProvidedService;
    }

    public String get() {
        return cache.get(KI_ANC_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                KIANCClients ancClients = new KIANCClients();
                List<Pair<Ibu, KartuIbu>> ancsWithKis = allKohort.allANCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> ancsWithKi : ancsWithKis) {
                    Ibu anc = ancsWithKi.getLeft();
                    KartuIbu ki = ancsWithKi.getRight();

                    KIANCClient kartuIbuClient = new KIANCClient(anc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB));

                    kartuIbuClient.setIsInPNCorANC(true);
                    kartuIbuClient.setIsPregnant(true);

                    kartuIbuClient.setAncVisitNumber(ki.getDetail(ANC_VISIT_NUMBER));
                    kartuIbuClient.setRiwayatKomplikasiKebidanan(anc.getDetail(COMPLICATION_HISTORY));
                    kartuIbuClient.setLaborComplication(anc.getDetail(AllConstantsINA.KartuPNCFields.COMPLICATION));

                    ancClients.add(kartuIbuClient);
                }
                return new Gson().toJson(ancClients);
            }
        });
    }

    public KIANCClients getKIANCClients() {
        return KIANCClientsCache.get(KI_ANC_CLIENTS_LIST, new CacheableData<KIANCClients>() {
            @Override
            public KIANCClients fetch() {
                KIANCClients ancClients = new KIANCClients();
                List<Pair<Ibu, KartuIbu>> ancsWithKis = allKohort.allANCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> ancsWithKi : ancsWithKis) {
                    Ibu anc = ancsWithKi.getLeft();
                    KartuIbu ki = ancsWithKi.getRight();

                    KIANCClient kartuIbuClient = new KIANCClient(anc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB))
                            .withHusband(ki.getDetail(HUSBAND_NAME))
                            .withKINumber(ki.getDetail(MOTHER_NUMBER))
                            .withEDD(ki.getDetail(EDD))
                            .withANCStatus(anc.getDetail(MOTHER_NUTRITION_STATUS))
                            .withKunjunganData(ki.getDetail(TRIMESTER))
                            .withVisitDate(anc.getReferenceDate())
                            .withTTImunisasiData(anc.getDetail(IMMUNIZATION_TT_STATUS))
                            .withTanggalHPHT(anc.getDetail(HPHT_DATE))
                            .withUniqueId(ki.getDetail(UNIQUE_ID));

                    kartuIbuClient.setKartuIbuCaseId(anc.getKartuIbuId());
                    kartuIbuClient.setBB(anc.getDetail(WEIGHT_BEFORE));
                    kartuIbuClient.setTB(anc.getDetail(HEIGHT));
                    kartuIbuClient.setLILA(anc.getDetail(LILA_CHECK_RESULT));
                    kartuIbuClient.setBeratBadan(anc.getDetail(WEIGHT_CHECK_RESULT));
                    kartuIbuClient.setPenyakitKronis(anc.getDetail(CHRONIC_DISEASE));
                    kartuIbuClient.setAlergi(anc.getDetail(ALLERGY));
                    kartuIbuClient.setHighRiskLabour(ki.getDetail(IS_HIGH_RISK_LABOUR));
                    kartuIbuClient.setHigRiskPregnancyReason(ki.getDetail(HIGH_RISK_PREGNANCY_REASON));
                    kartuIbuClient.setRiwayatKomplikasiKebidanan(anc.getDetail(COMPLICATION_HISTORY));

                    kartuIbuClient.setIsInPNCorANC(true);
                    kartuIbuClient.setIsPregnant(true);

                    kartuIbuClient.setChronicDisease(anc.getDetail(CHRONIC_DISEASE));
                    kartuIbuClient.setrLila(anc.getDetail(AllConstantsINA.KartuANCFields.LILA_CHECK_RESULT));
                    kartuIbuClient.setrHbLevels(anc.getDetail(AllConstantsINA.KartuANCFields.HB_RESULT));
                    kartuIbuClient.setrTdDiastolik(anc.getDetail(AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_DIASTOLIC));
                    kartuIbuClient.setrTdSistolik(anc.getDetail(AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_SISTOLIC));
                    kartuIbuClient.setrBloodSugar(anc.getDetail(AllConstantsINA.KartuANCFields.SUGAR_BLOOD_LEVEL));
                    kartuIbuClient.setrAbortus(ki.getDetail(NUMBER_ABORTIONS));
                    kartuIbuClient.setrPartus(ki.getDetail(NUMBER_PARTUS));
                    kartuIbuClient.setrPregnancyComplications(anc.getDetail(AllConstantsINA.KartuANCFields.COMPLICATION_HISTORY));
                    kartuIbuClient.setrFetusNumber(anc.getDetail(AllConstantsINA.KartuANCFields.FETUS_NUMBER));
                    kartuIbuClient.setrFetusSize(anc.getDetail(AllConstantsINA.KartuANCFields.FETUS_SIZE));
                    kartuIbuClient.setrFetusPosition(anc.getDetail(AllConstantsINA.KartuANCFields.FETUS_POSITION));
                    kartuIbuClient.setrPelvicDeformity(anc.getDetail(AllConstantsINA.KartuANCFields.PELVIC_DEFORMITY));
                    kartuIbuClient.setrHeight(anc.getDetail(AllConstantsINA.KartuANCFields.HEIGHT));
                    kartuIbuClient.setrDeliveryMethod(anc.getDetail(AllConstantsINA.KartuPNCFields.DELIVERY_METHOD));
                    kartuIbuClient.setLaborComplication(anc.getDetail(AllConstantsINA.KartuPNCFields.COMPLICATION));

                    // get alerts
                    List<AlertDTO> alertDTOs = getAlerts(anc.getId());
                    List<ServiceProvidedDTO> servicesProvided = getServicesProvided(anc.getId());
                    kartuIbuClient.setAlerts(alertDTOs);
                    kartuIbuClient.ancPreProcess();

                    ancClients.add(kartuIbuClient);
                }
                sortByName(ancClients);
                return ancClients;
            }
        });
    }

    private void sortByName(List<? extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });
    }

    public Villages villages() {
        return villagesCache.get(KI_ANC_CLIENTS_LIST, new CacheableData<Villages>() {
            @Override
            public Villages fetch() {
                List<SmartRegisterClient> clients = new Gson().fromJson(get(), new TypeToken<List<KIANCClient>>() {
                }.getType());
                List<String> villageNameList = new ArrayList<>();
                Villages villagesList = new Villages();

                for (SmartRegisterClient client : clients) {
                    villageNameList.add(client.village());
                }

                villageNameList = new ArrayList<>(new LinkedHashSet<>(villageNameList));
                for (String name : villageNameList) {
                    villagesList.add(new Village(name));
                }
                return villagesList;
            }
        });
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        String clients = get();
        List<SmartRegisterClient> ancClients = new Gson().fromJson(clients, new TypeToken<List<KIANCClient>>() {
        }.getType());
        return onRandomNameChars(
                client,
                ancClients,
                allKohort.randomDummyANCName(),
                AllConstantsINA.DIALOG_DOUBLE_SELECTION_NUM);
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId,
                ANC_1_ALERT_NAME,
                ANC_2_ALERT_NAME,
                ANC_3_ALERT_NAME,
                ANC_4_ALERT_NAME
        );
        List<AlertDTO> alertDTOs = new ArrayList<>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService.findByEntityIdAndServiceNames(entityId,
                ANC_1_SERVICE_PROVIDED_NAME,
                ANC_2_SERVICE_PROVIDED_NAME,
                ANC_3_SERVICE_PROVIDED_NAME,
                ANC_4_SERVICE_PROVIDED_NAME);
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs.add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(), serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

}

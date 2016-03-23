package org.ei.opensrp.indonesia.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.ArrayUtils;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.ServiceProvided;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.AnakClient;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.ServiceProvidedDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import static java.util.Collections.sort;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.opensrp.domain.ServiceProvided.CHILD_ILLNESS_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.PNC_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.VITAMIN_A_SERVICE_PROVIDED_NAME;
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

import static java.lang.String.*;

/**
 * Created by Dimas Ciputra on 4/8/15.
 */
public class AnakRegisterController extends CommonController {
    private static final String ANAK_CLIENTS_LIST = "anakClientsList";

    private final AllKohort allKohort;
    private final AlertService alertService;
    private final ServiceProvidedService serviceProvidedService;
    private final Cache<String> cache;
    private final Cache<SmartRegisterClients> smartRegisterClientsCache;
    private final Cache<Villages> villagesCache;

    public AnakRegisterController(AllKohort allKohort, AlertService alertService, ServiceProvidedService serviceProvidedService, Cache<String> cache, Cache<SmartRegisterClients> smartRegisterClientsCache, Cache<Villages> villageCache) {
        this.allKohort = allKohort;
        this.alertService = alertService;
        this.serviceProvidedService = serviceProvidedService;
        this.cache = cache;
        this.smartRegisterClientsCache = smartRegisterClientsCache;
        this.villagesCache = villageCache;
    }

    public String get() {
        return cache.get(ANAK_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<Anak> anakList = allKohort.allAnakWithIbuAndKI();
                SmartRegisterClients anakClients = new SmartRegisterClients();

                for(Anak a : anakList) {
                    AnakClient anakClient = new AnakClient(a.getCaseId(),
                            a.getGender(),
                            a.getDetail(BIRTH_WEIGHT),
                            a.getDetails())
                            .withName(a.getDetail(CHILD_NAME))
                            .withVillage(a.getKartuIbu().dusun());

                    anakClient.setHb07(a.getDetail(IMMUNIZATION_HB_0_7_DATES));
                    anakClient.setBcgPol1(a.getDetail(IMMUNIZATION_BCG_AND_POLIO1));
                    anakClient.setDptHb1Pol2(a.getDetail(IMMUNIZATION_DPT_HB1_POLIO2));
                    anakClient.setDptHb2Pol3(a.getDetail(IMMUNIZATION_DPT_HB2_POLIO3));
                    anakClient.setDptHb3Pol4(a.getDetail(IMMUNIZATION_DPT_HB3_POLIO4));
                    anakClients.add(anakClient);
                }
                return new Gson().toJson(anakClients);
            }
        });
    }

    public SmartRegisterClients getClient() {
        return  smartRegisterClientsCache.get(ANAK_CLIENTS_LIST, new CacheableData<SmartRegisterClients>() {
            @Override
            public SmartRegisterClients fetch() {
                List<Anak> anakList = allKohort.allAnakWithIbuAndKI();
                SmartRegisterClients anakSmartClient = new SmartRegisterClients();

                for(Anak a : anakList) {
                    String photoPath = isBlank(a.getPhotoPath()) ?
                            (AllConstants.FEMALE_GENDER.equalsIgnoreCase(a.getGender()) ?
                                    AllConstants.DEFAULT_GIRL_INFANT_IMAGE_PLACEHOLDER_PATH :
                                    AllConstants.DEFAULT_BOY_INFANT_IMAGE_PLACEHOLDER_PATH) :
                            a.getPhotoPath();

                    // List<AlertDTO> alerts = getAlerts(a.getCaseId());
                    // List<ServiceProvidedDTO> services = getServicesProvided(a.getCaseId());

                    AnakClient anakClient = new AnakClient(a.getCaseId(), a.getGender(),
                            a.getDetail(BIRTH_WEIGHT),
                            a.getDetails())
                            .withMotherName(a.getKartuIbu().getDetail(MOTHER_NAME))
                            .withMotherAge(a.getKartuIbu().getDetail(MOTHER_AGE))
                            .withFatherName(a.getKartuIbu().getDetail(HUSBAND_NAME))
                            .withDOB(a.getDateOfBirth())
                            .withName(a.getDetail(CHILD_NAME))
                            .withKINumber(a.getKartuIbu().getDetail(MOTHER_NUMBER))
                            .withBirthCondition(a.getDetail(BIRTH_CONDITION))
                            .withServiceAtBirth(a.getDetail(SERVICE_AT_BIRTH))
                            .withPhotoPath(photoPath)
                            .withIsHighRisk(a.getDetail(IS_HIGH_RISK_CHILD))
                            .withVillage(a.getKartuIbu().dusun());
                    anakClient.setHb07(a.getDetail(IMMUNIZATION_HB_0_7_DATES));
                    anakClient.setBcgPol1(a.getDetail(IMMUNIZATION_BCG_AND_POLIO1));
                    anakClient.setDptHb1Pol2(a.getDetail(IMMUNIZATION_DPT_HB1_POLIO2));
                    anakClient.setDptHb2Pol3(a.getDetail(IMMUNIZATION_DPT_HB2_POLIO3));
                    anakClient.setDptHb3Pol4(a.getDetail(IMMUNIZATION_DPT_HB3_POLIO4));
                    anakClient.setCampak(a.getDetail(IMMUNIZATION_MEASLES));
                    anakClient.setBirthPlace(a.getIbu().getDetail(BIRTH_PLACE));
                    anakClient.setHbGiven(a.getDetail(HB_GIVEN));
                    anakClient.setVisitDate(a.getDetail(CHILD_VISIT_DATE));
                    anakClient.setCurrentWeight(a.getDetail(CHILD_CURRENT_WEIGTH));
                    anakClient.setBabyNo(a.getDetail(BABY_NO));
                    anakClient.setPregnancyAge(a.getIbu().getDetail(AllConstantsINA.KartuPNCFields.PREGNANCY_AGE));
                    anakSmartClient.add(anakClient);
                }
                sortByName(anakSmartClient);
                return anakSmartClient;
            }
        });
    }

    private List<AlertDTO> getAlerts(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId, AllConstants.Immunizations.ALL);
        List<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    private List<ServiceProvidedDTO> getServicesProvided(String entityId) {
        List<ServiceProvided> servicesProvided = serviceProvidedService.findByEntityIdAndServiceNames(entityId,
                ArrayUtils.addAll(AllConstants.Immunizations.ALL, VITAMIN_A_SERVICE_PROVIDED_NAME,
                        CHILD_ILLNESS_SERVICE_PROVIDED_NAME, PNC_SERVICE_PROVIDED_NAME));
        List<ServiceProvidedDTO> serviceProvidedDTOs = new ArrayList<ServiceProvidedDTO>();
        for (ServiceProvided serviceProvided : servicesProvided) {
            serviceProvidedDTOs.add(new ServiceProvidedDTO(serviceProvided.name(), serviceProvided.date(), serviceProvided.data()));
        }
        return serviceProvidedDTOs;
    }

    private void sortByName(List<SmartRegisterClient> childrenClient) {
        sort(childrenClient, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient oneChild, SmartRegisterClient anotherChild) {
                return oneChild.compareName(anotherChild);
            }
        });
    }

    private void sortByMotherName(List<AnakClient> childrenClient) {
        sort(childrenClient, new Comparator<AnakClient>() {
            @Override
            public int compare(AnakClient oneChild, AnakClient anotherChild) {
                return oneChild.motherName().compareToIgnoreCase(anotherChild.motherName());
            }
        });
    }

    public Villages villages() {
        return villagesCache.get(ANAK_CLIENTS_LIST, new CacheableData<Villages>() {
            @Override
            public Villages fetch() {
                List<Anak> anakList = allKohort.allAnakWithIbuAndKI();
                List<String> villageNameList = new ArrayList<>();
                Villages villagesList = new Villages();

                for(Anak client : anakList) {
                    villageNameList.add(client.getKartuIbu().dusun());
                }
                villageNameList = new ArrayList<>(new LinkedHashSet<>(villageNameList));
                for(String name : villageNameList) {
                    Village village = new Village(name);
                    villagesList.add(new Village(name));
                }
                return villagesList;
            }
        });
    }


    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        String clients = get();
        List<SmartRegisterClient> anakClients = new Gson().fromJson(clients, new TypeToken<List<AnakClient>>() {
        }.getType());

        return onRandomNameChars(
                client,
                anakClients,
                allKohort.randomDummyAnakName(),
                AllConstantsINA.DIALOG_DOUBLE_SELECTION_NUM);
    }
}

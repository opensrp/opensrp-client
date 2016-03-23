package org.ei.opensrp.indonesia.view.controller;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.indonesia.repository.AllKartuIbus;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.KBClient;
import org.ei.opensrp.indonesia.view.contract.KBClients;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import static java.util.Collections.sort;
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
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.HUSBAND_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_DOB;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_ABORTIONS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_OF_LIVING_CHILDREN;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_OF_PREGNANCIES;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_PARTUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.VISITS_DATE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.COMPLICATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.DELIVERY_METHOD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_DIASTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_SISTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.ALKI_CHRONIC_DISEASE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.CONTRACEPTION_METHOD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.HB;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.IMS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.KB_INFORMATION_1;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.KB_INFORMATION_2;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.LILA;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class KohortKBRegisterController extends CommonController{
    private static final String KB_CLIENTS_LIST = "KBClientsList";

    private final AllKartuIbus allKartuIbus;
    private final Cache<String> cache;
    private final Cache<KBClients> kbClientsCache;
    private final AllKohort allKohort;
    private final Cache<Villages> villagesCache;

    public KohortKBRegisterController(AllKartuIbus allKartuIbus, Cache<String> cache, Cache<KBClients> kbClientsCache, AllKohort allKohort, Cache<Villages> villagesCache) {
        this.allKartuIbus = allKartuIbus;
        this.cache = cache;
        this.kbClientsCache = kbClientsCache;
        this.allKohort = allKohort;
        this.villagesCache = villagesCache;
    }

    public String get() {
        return cache.get(KB_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.allWithOutOfArea();
                KBClients KBs = new KBClients();

                for (KartuIbu kartuIbu : kartuIbus) {
                    if(Strings.isNullOrEmpty(kartuIbu.getDetail(CONTRACEPTION_METHOD))) {
                        continue;
                    }
                    KBClient kbClient = new KBClient(
                            kartuIbu.getCaseId(),
                            kartuIbu.getDetail(MOTHER_NAME),
                            kartuIbu.getDetail(HUSBAND_NAME),
                            kartuIbu.dusun(),
                            kartuIbu.getDetail(MOTHER_NUMBER))
                            .withKBMethod(kartuIbu.getDetail(CONTRACEPTION_METHOD))
                            .withIMS(kartuIbu.getDetails().get(IMS))
                            .withHB(kartuIbu.getDetails().get(HB))
                            .withLila(kartuIbu.getDetails().get(LILA))
                            .withPenyakitKronis(kartuIbu.getDetail(ALKI_CHRONIC_DISEASE))
                            .withGravida(kartuIbu.getDetail(NUMBER_OF_PREGNANCIES))
                            .withParity(kartuIbu.getDetail(NUMBER_PARTUS))
                            .withAbortus(kartuIbu.getDetail(NUMBER_ABORTIONS))
                            .withLiveChild(kartuIbu.getDetail(NUMBER_OF_LIVING_CHILDREN))
                            .withTanggalKunjungan(kartuIbu.getDetail(VISITS_DATE))
                            .withKeterangan1(kartuIbu.getDetail(KB_INFORMATION_1))
                            .withKeterangan2(kartuIbu.getDetail(KB_INFORMATION_2))
                            .withDateOfBirth(kartuIbu.getDetail(MOTHER_DOB));

                    KBs.add(kbClient);
                }
                return new Gson().toJson(KBs);
            }
        });
    }

    public KBClients getKBClients() {
        return kbClientsCache.get(KB_CLIENTS_LIST, new CacheableData<KBClients>() {
            @Override
            public KBClients fetch() {
                List<KartuIbu> kartuIbus = allKartuIbus.allWithOutOfArea();
                KBClients KBs = new KBClients();

                for (KartuIbu kartuIbu : kartuIbus) {

                    if(Strings.isNullOrEmpty(kartuIbu.getDetail(CONTRACEPTION_METHOD))) {
                        continue;
                    }

                    KBClient kbClient = new KBClient(
                            kartuIbu.getCaseId(),
                            kartuIbu.getDetail(MOTHER_NAME),
                            kartuIbu.getDetail(HUSBAND_NAME),
                            kartuIbu.dusun(),
                            kartuIbu.getDetail(MOTHER_NUMBER))
                            .withKBMethod(kartuIbu.getDetail(CONTRACEPTION_METHOD))
                            .withIMS(kartuIbu.getDetails().get(IMS))
                            .withHB(kartuIbu.getDetails().get(HB))
                            .withLila(kartuIbu.getDetails().get(LILA))
                            .withPenyakitKronis(kartuIbu.getDetail(ALKI_CHRONIC_DISEASE))
                            .withGravida(kartuIbu.getDetail(NUMBER_OF_PREGNANCIES))
                            .withParity(kartuIbu.getDetail(NUMBER_PARTUS))
                            .withAbortus(kartuIbu.getDetail(NUMBER_ABORTIONS))
                            .withLiveChild(kartuIbu.getDetail(NUMBER_OF_LIVING_CHILDREN))
                            .withTanggalKunjungan(kartuIbu.getDetail(VISITS_DATE))
                            .withKeterangan1(kartuIbu.getDetail(KB_INFORMATION_1))
                            .withKeterangan2(kartuIbu.getDetail(KB_INFORMATION_2))
                            .withDateOfBirth(kartuIbu.getDetail(MOTHER_DOB));
                    updateStatusInformation(kartuIbu, kbClient);
                    KBs.add(kbClient);
                }
                sortByName(KBs);
                return KBs;
            }
        });
    }

    private void sortByName(List<?extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.wifeName().compareToIgnoreCase(o2.wifeName());
            }
        });
    }

    private void sortByDateOfBirth(List<Anak> children) {
        sort(children, new Comparator<Anak>() {
            @Override
            public int compare(Anak child, Anak anotherChild) {
                return LocalDate.parse(child.getDateOfBirth()).compareTo(LocalDate.parse(anotherChild.getDateOfBirth()));
            }
        });
    }


    public Villages villages() {
        return villagesCache.get(KB_CLIENTS_LIST, new CacheableData<Villages>() {
            @Override
            public Villages fetch() {
                List<SmartRegisterClient> clients = new Gson().fromJson(get(), new TypeToken<List<KBClient>>() {
                }.getType());
                List<String> villageNameList = new ArrayList<>();
                Villages villagesList = new Villages();

                for(SmartRegisterClient client : clients) {
                    villageNameList.add(client.village());
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
        List<SmartRegisterClient> kbClients = new Gson().fromJson(clients, new TypeToken<List<KBClient>>() {
        }.getType());
        return onRandomNameChars(
                client,
                kbClients,
                allKartuIbus.randomDummyName(),
                AllConstantsINA.DIALOG_DOUBLE_SELECTION_NUM);
    }

    private void updateStatusInformation(KartuIbu kartuIbu, KBClient kartuIbuClient) {
        Ibu ibu = allKohort.findIbuWithOpenStatusByKIId(kartuIbu.getCaseId());
        if (ibu == null) {
            kartuIbuClient.setIsInPNCorANC(false);
            kartuIbuClient.setIsPregnant(false);
        }
        if (ibu != null) {
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
        }

    }

}

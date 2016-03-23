package org.ei.opensrp.indonesia.view.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.domain.Anak;
import org.ei.opensrp.indonesia.domain.Ibu;
import org.ei.opensrp.indonesia.domain.KartuIbu;
import org.ei.opensrp.indonesia.repository.AllKohort;
import org.ei.opensrp.indonesia.view.contract.AnakClient;
import org.ei.opensrp.indonesia.view.contract.KIPNCClient;
import org.ei.opensrp.indonesia.view.contract.KIPNCClients;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;

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
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BIRTH_CONDITION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuAnakFields.BIRTH_WEIGHT;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.CHRONIC_DISEASE;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.EDD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.HUSBAND_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_DOB;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.MOTHER_NUMBER;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_ABORTIONS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.NUMBER_PARTUS;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuIbuFields.PUSKESMAS_NAME;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.COMPLICATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.DELIVERY_METHOD;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.MOTHER_CONDITION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.OTHER_COMPLICATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.PLANNING;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_DIASTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TD_SISTOLIC;
import static org.ei.opensrp.indonesia.AllConstantsINA.KartuPNCFields.VITAL_SIGNS_TEMP;
import static org.ei.opensrp.indonesia.AllConstantsINA.KeluargaBerencanaFields.CONTRACEPTION_METHOD;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIPNCRegisterController extends CommonController {

    private static final String KI_PNC_CLIENTS_LIST = "KIPNClientsList";

    private final AllKohort allKohort;
    private final Cache<String> cache;
    private final Cache<KIPNCClients> kartuIbuPNCClientsCache;
    private final Cache<Villages> villagesCache;

    public KIPNCRegisterController(AllKohort allKohort, Cache<String> cache, Cache<KIPNCClients> kartuIbuPNCClientsCache, Cache<Villages> villagesCache) {
        this.allKohort = allKohort;
        this.cache = cache;
        this.villagesCache = villagesCache;
        this.kartuIbuPNCClientsCache = kartuIbuPNCClientsCache;
    }

    public String get() {
        return cache.get(KI_PNC_CLIENTS_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                KIPNCClients pncClients = new KIPNCClients();
                List<Pair<Ibu, KartuIbu>> pncsWithKis = allKohort.allPNCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> pncsWithKi : pncsWithKis) {
                    Ibu pnc = pncsWithKi.getLeft();
                    KartuIbu ki = pncsWithKi.getRight();

                    KIPNCClient kartuIbuClient = new KIPNCClient(
                            pnc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB))
                            .withHusband(ki.getDetail(HUSBAND_NAME))
                            .withKINumber(ki.getDetail(MOTHER_NUMBER))
                            .withEDD(pnc.getDetail(EDD))
                            .withPlan(pnc.getDetail(PLANNING))
                            .withKomplikasi(pnc.getDetail(COMPLICATION))
                            .withOtherKomplikasi(pnc.getDetail(OTHER_COMPLICATION))
                            .withMetodeKontrasepsi(pnc.getDetail(CONTRACEPTION_METHOD))
                            .withChildren(findChildren(pnc))
                            .withTandaVital(ki.getDetail(VITAL_SIGNS_TD_DIASTOLIC),
                                    ki.getDetail(VITAL_SIGNS_TD_SISTOLIC),
                                    pnc.getDetail(VITAL_SIGNS_TEMP));
                    pncClients.add(kartuIbuClient);
                }

                return new Gson().toJson(pncClients);
            }
        });
    }

    public KIPNCClients getKartuIbuPNCClients() {
        return kartuIbuPNCClientsCache.get(KI_PNC_CLIENTS_LIST, new CacheableData<KIPNCClients>() {
            @Override
            public KIPNCClients fetch() {
                KIPNCClients pncClients = new KIPNCClients();
                List<Pair<Ibu, KartuIbu>> pncsWithKis = allKohort.allPNCsWithKartuIbu();

                for (Pair<Ibu, KartuIbu> pncsWithKi : pncsWithKis) {
                    Ibu pnc = pncsWithKi.getLeft();
                    KartuIbu ki = pncsWithKi.getRight();

                    KIPNCClient kartuIbuClient = new KIPNCClient(
                            pnc.getId(),
                            ki.dusun(),
                            ki.getDetail(PUSKESMAS_NAME),
                            ki.getDetail(MOTHER_NAME),
                            ki.getDetail(MOTHER_DOB))
                            .withHusband(ki.getDetail(HUSBAND_NAME))
                            .withKINumber(ki.getDetail(MOTHER_NUMBER))
                            .withEDD(pnc.getDetail(EDD))
                            .withPlan(pnc.getDetail(PLANNING))
                            .withKomplikasi(pnc.getDetail(COMPLICATION))
                            .withOtherKomplikasi(pnc.getDetail(OTHER_COMPLICATION))
                            .withMetodeKontrasepsi(pnc.getDetail(CONTRACEPTION_METHOD))
                            .withChildren(findChildren(pnc))
                            .withTandaVital(ki.getDetail(VITAL_SIGNS_TD_DIASTOLIC),
                                    ki.getDetail(VITAL_SIGNS_TD_SISTOLIC),
                                    pnc.getDetail(VITAL_SIGNS_TEMP));

                    kartuIbuClient.setTempatPersalinan(pnc.getDetail("tempatBersalin"));
                    kartuIbuClient.setTipePersalinan(pnc.getDetail("caraPersalinanIbu"));
                    kartuIbuClient.setMotherCondition(pnc.getDetail(MOTHER_CONDITION));

                    kartuIbuClient.setIsInPNCorANC(true);
                    kartuIbuClient.setIsPregnant(false);

                    kartuIbuClient.setChronicDisease(pnc.getDetail(CHRONIC_DISEASE));
                    kartuIbuClient.setrLila(pnc.getDetail(LILA_CHECK_RESULT));
                    kartuIbuClient.setrHbLevels(pnc.getDetail(HB_RESULT));
                    kartuIbuClient.setrTdDiastolik(pnc.getDetail(VITAL_SIGNS_TD_DIASTOLIC));
                    kartuIbuClient.setrTdSistolik(pnc.getDetail(VITAL_SIGNS_TD_SISTOLIC));
                    kartuIbuClient.setrBloodSugar(pnc.getDetail(SUGAR_BLOOD_LEVEL));
                    kartuIbuClient.setrAbortus(ki.getDetail(NUMBER_ABORTIONS));
                    kartuIbuClient.setrPartus(ki.getDetail(NUMBER_PARTUS));
                    kartuIbuClient.setrPregnancyComplications(pnc.getDetail(COMPLICATION_HISTORY));
                    kartuIbuClient.setrFetusNumber(pnc.getDetail(FETUS_NUMBER));
                    kartuIbuClient.setrFetusSize(pnc.getDetail(FETUS_SIZE));
                    kartuIbuClient.setrFetusPosition(pnc.getDetail(FETUS_POSITION));
                    kartuIbuClient.setrPelvicDeformity(pnc.getDetail(PELVIC_DEFORMITY));
                    kartuIbuClient.setrHeight(pnc.getDetail(HEIGHT));
                    kartuIbuClient.setrDeliveryMethod(pnc.getDetail(DELIVERY_METHOD));
                    kartuIbuClient.setLaborComplication(pnc.getDetail(COMPLICATION));

                    kartuIbuClient.setKartuIbuEntityId(pnc.getKartuIbuId());

                    pncClients.add(kartuIbuClient);
                }
                sortByName(pncClients);
                return pncClients;
            }
        });
    }

    private void sortByName(List<?extends SmartRegisterClient> kiClients) {
        sort(kiClients, new Comparator<SmartRegisterClient>() {
            @Override
            public int compare(SmartRegisterClient o1, SmartRegisterClient o2) {
                return o1.name().compareToIgnoreCase(o2.name());
            }
        });
    }

    public CharSequence[] getRandomNameChars(final SmartRegisterClient client) {
        String clients = get();
        List<SmartRegisterClient> pncClients = new Gson().fromJson(clients, new TypeToken<List<KIPNCClient>>(){}.getType());

        return onRandomNameChars(
                client,
                pncClients,
                allKohort.randomDummyPNCName(),
                AllConstantsINA.DIALOG_DOUBLE_SELECTION_NUM);
    }


    public Villages villages() {
        return villagesCache.get(KI_PNC_CLIENTS_LIST, new CacheableData<Villages>() {
            @Override
            public Villages fetch() {
                List<SmartRegisterClient> clients = new Gson().fromJson(get(), new TypeToken<List<KIPNCClient>>() {
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

    private List<AnakClient> findChildren(Ibu mother) {
        List<Anak> children = allKohort.findAllAnakByIbuId(mother.getId());
        List<AnakClient> childClientList = new ArrayList<AnakClient>();
        for (Anak child : children) {
            childClientList.add(new AnakClient(child.getCaseId(), child.getGender(),
                    child.getDetail(BIRTH_WEIGHT), child.getDetails()).withDOB(child.getDateOfBirth()).withBirthCondition(child.getDetail(BIRTH_CONDITION)));
        }
        return childClientList;
    }
}

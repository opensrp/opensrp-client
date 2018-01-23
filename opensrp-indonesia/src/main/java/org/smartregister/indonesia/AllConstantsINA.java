package org.smartregister.indonesia;

/**
 * Created by Dimas Ciputra on 9/12/15.
 */
public class AllConstantsINA {

    public static final String BOOLEAN_TRUE = "yes";
    public static final String BOOLEAN_FALSE = "no";
    public static final String SPACE = " ";
    public static final String COMMA_WITH_SPACE = ", ";
    public static final int DIALOG_DOUBLE_SELECTION_NUM  = 3;
    public static final String FEMALE_GENDER_INA = "perempuan";
    public static final String ENGLISH_LOCALE = "en";
    public static final String BAHASA_LOCALE = "in";
    public static final String DEFAULT_LOCALE = ENGLISH_LOCALE;
    public static final String ENGLISH_LANGUAGE = "English";
    public static final String BAHASA_LANGUAGE = "Bahasa Indonesia";
    public static final String OUT_OF_AREA = "out_of_area";
    public static final String IN_AREA = "in_area";

    public static final String HIGH_RISK = "high_risk";
    public static final String HIGH_RISK_PREGNANCY = "high_risk_pregnancy";
    public static final String HIGH_RISK_LABOUR = "high_risk_labour";
    public static final String HIGH_RISK_POST_PARTUM = "high_risk_post_partum";

    // Flurry Bidan Testing 2
//     public static final String FLURRY_KEY = "9RDM5TWW4HJ5QH8TNJ3P";

    //    COLOR
    public static final String RED_ORANGE = "#FE642E";
    public static final String ORANGE_YELLOW = "#D7DF01";
    public static final String YELLOW_GREEN = "#86B404";
    public static final String GREEN = "#5FB404";


   //  Flurry Key Bidan EC Prototype 1
    public static final String FLURRY_KEY = "7N8KZP67BP6QRJPB2RXX";

    public class KeluargaBerencanaFields {
        public static final String CONTRACEPTION_METHOD = "jenisKontrasepsi";
        public static final String KELUARGA_BERENCANA = "KB";
        public static final String KB_INFORMATION_1 = "keteranganTentangPesertaKB";
        public static final String KB_INFORMATION_2 = "keteranganTentangPesertaKB2";
        public static final String IMS = "alkiPenyakitIms";
        public static final String HB = "alkihb";
        public static final String LILA = "alkilila";
        public static final String ALKI_CHRONIC_DISEASE = "alkiPenyakitKronis";
    }

    public class CommonFormFields {
        public static final String SUBMISSION_DATE = "submissionDate";
        public static final String UNIQUE_ID = "unique_id";
    }


    public class FormNames {
        public static final String EC_REGISTRATION = "ec_registration";
        public static final String FP_COMPLICATIONS = "fp_complications";
        public static final String FP_CHANGE = "fp_change";
        public static final String RENEW_FP_PRODUCT = "renew_fp_product";
        public static final String EC_CLOSE = "ec_close";
        public static final String ANC_REGISTRATION = "anc_registration";
        public static final String ANC_REGISTRATION_OA = "anc_registration_oa";
        public static final String ANC_VISIT = "anc_visit";
        public static final String ANC_CLOSE = "anc_close";
        public static final String TT = "tt";
        public static final String TT_BOOSTER = "tt_booster";
        public static final String TT_1 = "tt_1";
        public static final String TT_2 = "tt_2";
        public static final String IFA = "ifa";
        public static final String HB_TEST = "hb_test";
        public static final String DELIVERY_OUTCOME = "delivery_outcome";
        public static final String PNC_REGISTRATION_OA = "pnc_registration_oa";
        public static final String PNC_CLOSE = "pnc_close";
        public static final String PNC_VISIT = "pnc_visit";
        public static final String PNC_POSTPARTUM_FAMILY_PLANNING = "postpartum_family_planning";
        public static final String CHILD_IMMUNIZATIONS = "child_immunizations";
        public static final String CHILD_REGISTRATION_EC = "child_registration_ec";
        public static final String CHILD_REGISTRATION_OA = "child_registration_oa";
        public static final String CHILD_CLOSE = "child_close";
        public static final String CHILD_ILLNESS = "child_illness";
        public static final String VITAMIN_A = "vitamin_a";
        public static final String DELIVERY_PLAN = "delivery_plan";
        public static final String EC_EDIT = "ec_edit";
        public static final String ANC_INVESTIGATIONS = "anc_investigations";
        public static final String RECORD_ECPS = "record_ecps";
        public static final String FP_REFERRAL_FOLLOWUP = "fp_referral_followup";
        public static final String FP_FOLLOWUP = "fp_followup";

        // KOHORT IBU
        public static final String KARTU_IBU_REGISTRATION = "kartu_ibu_registration";
        public static final String KARTU_IBU_EDIT = "kartu_ibu_edit";
        public static final String KARTU_IBU_CLOSE = "kartu_ibu_close";

        // ANC
        public static final String KARTU_IBU_ANC_REGISTRATION = "kartu_anc_registration";
        public static final String KARTU_IBU_ANC_OA="kartu_anc_registration_oa";
        public static final String KARTU_IBU_ANC_RENCANA_PERSALINAN = "kartu_anc_rencana_persalinan";
        public static final String KARTU_IBU_ANC_EDIT="kartu_anc_visit_edit";
        public static final String KARTU_IBU_ANC_CLOSE="kartu_anc_close";
        public static final String KARTU_IBU_ANC_VISIT="kartu_anc_visit";
        public static final String KARTU_IBU_ANC_VISIT_INTEGRASI = "kartu_anc_visit_integrasi";
        public static final String KARTU_IBU_ANC_VISIT_LABTEST = "kartu_anc_visit_labTest";

        // PNC
        public static final String KARTU_IBU_PNC_EDIT="kartu_pnc_edit";
        public static final String KARTU_IBU_PNC_REGISTRATION = "kartu_pnc_dokumentasi_persalinan";
        public static final String KARTU_IBU_PNC_CLOSE="kartu_pnc_close";
        public static final String KARTU_IBU_PNC_OA="kartu_pnc_regitration_oa";
        public static final String KARTU_IBU_PNC_VISIT="kartu_pnc_visit";
        public static final String KARTU_IBU_PNC_POSPARTUM_KB="kartu_pnc_pospartum_kb";

        // ANAK
        public static final String KOHORT_BAYI_KUNJUNGAN="kohort_bayi_kunjungan";
        public static final String KARTU_IBU_ANAK_CLOSE="kohort_anak_tutup";
        public static final String BALITA_KUNJUNGAN="kohort_balita_kunjungan";
        public static final String BAYI_IMUNISASI="kohort_bayi_immunization";
        public static final String BAYI_NEONATAL_PERIOD="kohort_bayi_neonatal_period";
        public static final String KOHORT_BAYI_EDIT="kohort_bayi_edit";
        public static final String ANAK_BAYI_REGISTRATION = "kohort_bayi_registration";
        public static final String ANAK_NEW_REGISTRATION="kohort_bayi_registration_oa";

        // KB
        public static final String KOHORT_KB_REGISTER="kohort_kb_registration";
        public static final String KOHORT_KB_PELAYANAN="kohort_kb_pelayanan";
        public static final String KOHORT_KB_CLOSE="kohort_kb_close";
        public static final String KOHORT_KB_UPDATE="kohort_kb_update";
       // public static final String KOHORT_KB_EDIT="kohort_kb_edit";

        public static final String FEEDBACK_BIDAN = "feedback_bidan";
    }
    public class KartuIbuFields {
        public static final String PUSKESMAS_NAME = "puskesmas";
        public static final String POSYANDU_NAME = "posyandu";
        public static final String PROPINSI = "propinsi";
        public static final String KABUPATEN = "kabupaten";
        public static final String MOTHER_ADDRESS = "alamatDomisili";
        public static final String MOTHER_NUMBER = "noIbu";
        public static final String MOTHER_NAME = "namalengkap";
        public static final String MOTHER_AGE = "umur";
        public static final String MOTHER_BLOOD_TYPE = "golonganDarah";
        public static final String MOTHER_DOB = "tanggalLahir";
        public static final String HUSBAND_NAME = "namaSuami";
        public static final String VILLAGE = "dusun";
        public static final String NUMBER_PARTUS = "partus";
        public static final String NUMBER_ABORTIONS = "abortus";
        public static final String NUMBER_OF_PREGNANCIES = "gravida";
        public static final String NUMBER_OF_LIVING_CHILDREN = "hidup";
        public static final String IS_HIGH_PRIORITY = "isHighPriority";
        public static final String IS_HIGH_RISK = "isHighRisk";
        public static final String IS_HIGH_RISK_ANC = "isHighRiskANC";
        public static final String IS_HIGH_RISK_PREGNANCY = "isHighRiskPregnancy";
        public static final String IS_HIGH_RISK_LABOUR = "isHighRiskLabour";
        public static final String UNIQUE_ID = "unique_id";
        public static final String EDD = "htp";
        public static final String VISITS_DATE = "tanggalkunjungan";
        public static final String CHRONIC_DISEASE = "penyakitKronis";
    }

    public class KartuANCFields {
        public static final String MOTHER_NUTRITION_STATUS = "StatusGiziibu";
        public static final String COMPLICATION_HISTORY = "riwayatKomplikasiKebidanan";
        public static final String TRIMESTER = "trimesterKe";
        public static final String IMMUNIZATION_TT_STATUS = "StatusImunisasiTT";
        public static final String CLINICAL_AGE = "UsiaKlinis";
        public static final String WEIGHT_BEFORE = "bbSebelumHamil";
        public static final String WEIGHT_CHECK_RESULT = "bbKg";
        public static final String LILA_CHECK_RESULT = "hasilPemeriksaanLILA";
        public static final String HEIGHT = "tbCM";
        public static final String ALLERGY = "Alergi";
        public static final String HPHT_DATE = "tanggalHPHT";
        public static final String HIGH_RISK_PREGNANCY_REASON = "highRiskPregnancyReason";
        public static final String HB_RESULT = "laboratoriumPeriksaHbHasil";
        public static final String SUGAR_BLOOD_LEVEL = "laboratoriumGulaDarah";
        public static final String PELVIC_DEFORMITY = "kelainanBentuk";
        public static final String FETUS_SIZE = "taksiranBeratJanin";
        public static final String FETUS_NUMBER = "jumlahJanin";
        public static final String FETUS_POSITION = "persentasi_janin";
        public static final String ANC_VISIT_NUMBER = "ancKe";
    }

    public class KartuPNCFields {
        public static final String PLANNING = "Rencana";
        public static final String COMPLICATION = "komplikasi";
        public static final String OTHER_COMPLICATION = "komplikasi_lainnya";
        public static final String VITAL_SIGNS_TD_DIASTOLIC = "tandaVitalTDDiastolik";
        public static final String VITAL_SIGNS_TD_SISTOLIC = "tandaVitalTDSistolik";
        public static final String VITAL_SIGNS_TEMP = "tandaVitalSuhu";
        public static final String MOTHER_CONDITION = "keadaanIbu";
        public static final String PREGNANCY_AGE = "usiaKehamilan";
        public static final String DELIVERY_METHOD = "caraPersalinanIbu";
    }

    public class KartuAnakFields {
        public static final String BABY_NO = "noBayi";
        public static final String BIRTH_WEIGHT = "beratLahir";
        public static final String CHILD_NAME = "namaBayi";
        public static final String BIRTH_CONDITION = "saatLahirsd5JamKondisibayi";
        public static final String SERVICE_AT_BIRTH = "jenisPelayananYangDiberikanSaatLahir";
        public static final String DATE_OF_BIRTH = "tanggalLahirAnak";
        public static final String GENDER = "jenisKelamin";
        public static final String IS_HIGH_RISK_CHILD = "isHighRiskChild";
        public static final String IMMUNIZATION_HB_0_7_DATES = "tanggalpemberianimunisasiHb07";
        public static final String IMMUNIZATION_BCG_AND_POLIO1 = "tanggalpemberianimunisasiBCGdanPolio1";
        public static final String IMMUNIZATION_DPT_HB1_POLIO2 = "tanggalpemberianimunisasiDPTHB1Polio2";
        public static final String IMMUNIZATION_DPT_HB2_POLIO3 = "tanggalpemberianimunisasiDPTHB2Polio3";
        public static final String IMMUNIZATION_DPT_HB3_POLIO4 = "tanggalpemberianimunisasiDPTHB3Polio4";
        public static final String IMMUNIZATION_MEASLES = "tanggalpemberianimunisasiCampak";
        public static final String HB_GIVEN = "saatLahirsd5JamPemberianImunisasihbJikaDilakukan";
        public static final String BIRTH_PLACE = "tempatBersalin";
        public static final String CHILD_VISIT_DATE = "tanggalKunjunganBayiPerbulan";
        public static final String CHILD_CURRENT_WEIGTH = "beratBadanBayiSetiapKunjunganBayiPerbulan";
    }
}

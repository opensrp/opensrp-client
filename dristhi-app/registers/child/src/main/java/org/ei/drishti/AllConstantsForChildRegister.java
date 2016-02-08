package org.ei.drishti;

public class AllConstantsForChildRegister {
    public class ChildRegistrationECFields {
        public static final String BCG_DATE = "bcgDate";
        public static final String MEASLES_DATE = "measlesDate";
        public static final String MEASLESBOOSTER_DATE = "measlesboosterDate";
        public static final String OPV_0_DATE = "opv0Date";
        public static final String OPV_1_DATE = "opv1Date";
        public static final String OPV_2_DATE = "opv2Date";
        public static final String OPV_3_DATE = "opv3Date";
        public static final String OPVBOOSTER_DATE = "opvboosterDate";
        public static final String DPTBOOSTER_1_DATE = "dptbooster1Date";
        public static final String DPTBOOSTER_2_DATE = "dptbooster2Date";
        public static final String PENTAVALENT_1_DATE = "pentavalent1Date";
        public static final String PENTAVALENT_2_DATE = "pentavalent2Date";
        public static final String PENTAVALENT_3_DATE = "pentavalent3Date";
        public static final String HEPB_BIRTH_DOSE_DATE = "hepb0Date";
        public static final String SHOULD_CLOSE_MOTHER = "shouldCloseMother";
        public static final String MMR_DATE = "mmrDate";
        public static final String JE_DATE = "jeDate";
    }

    public class ChildRegistrationFields {
        public static final String MOTHER_ID = "motherId";
        public static final String CHILD_ID = "childId";
        public static final String DATE_OF_BIRTH = "dateOfBirth";
        public static final String WEIGHT = "weight";
        public static final String IMMUNIZATIONS_GIVEN = "immunizationsGiven";
        public static final String GENDER = "gender";
        public static final String NAME = "name";
        public static final String HIGH_RISK_REASON = "childHighRiskReason";
        public static final String IS_CHILD_HIGH_RISK = "isChildHighRisk";
    }

    public class ChildRegistrationOAFields {
        public static final String CHILD_ID = "id";
        public static final String DATE_OF_BIRTH = "dateOfBirth";
        public static final String WEIGHT = "weight";
        public static final String IMMUNIZATIONS_GIVEN = "immunizationsGiven";
        public static final String NAME = "name";
        public static final String THAYI_CARD_NUMBER = "thayiCardNumber";
    }

    public class ChildImmunizationsFields {
        public static final String PREVIOUS_IMMUNIZATIONS_GIVEN = "previousImmunizations";
        public static final String IMMUNIZATIONS_GIVEN = "immunizationsGiven";
        public static final String IMMUNIZATION_DATE = "immunizationDate";
    }

    public class ChildIllnessFields {
        public static final String CHILD_SIGNS = "childSigns";
        public static final String CHILD_SIGNS_OTHER = "childSignsOther";
        public static final String SICK_VISIT_DATE = "sickVisitDate";
        public static final String REPORT_CHILD_DISEASE = "reportChildDisease";
        public static final String REPORT_CHILD_DISEASE_OTHER = "reportChildDiseaseOther";
        public static final String REPORT_CHILD_DISEASE_DATE = "reportChildDiseaseDate";
        public static final String REPORT_CHILD_DISEASE_PLACE = "reportChildDiseasePlace";
        public static final String CHILD_REFERRAL = "childReferral";
    }
}

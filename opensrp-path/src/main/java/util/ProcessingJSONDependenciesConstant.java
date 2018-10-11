package util;

/**
 * Created by raihan on 10/10/18.
 */

public class ProcessingJSONDependenciesConstant {
    public static String ec_client_classification = "{\n" +
            "  \"case_classification_rules\": [\n" +
            "    {\n" +
            "      \"comment\": \"Child: This rule checks whether a given case belongs to Child register\",\n" +
            "      \"rule\": {\n" +
            "        \"type\": \"event\",\n" +
            "        \"fields\": [\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Household Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_household\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"New Woman Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_mother\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"New Woman Member Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_mother\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Birth Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_child\"\n" +
            "            ]\n" +
            "          },\n" +
            "          {\n" +
            "            \"field\": \"eventType\",\n" +
            "            \"field_value\": \"Update Birth Registration\",\n" +
            "            \"creates_case\": [\n" +
            "              \"ec_child\"\n" +
            "            ]\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static String ec_client_vaccine = "{\n" +
            "  \"name\": \"vaccines\",\n" +
            "  \"columns\": [\n" +
            "    {\n" +
            "      \"column_name\": \"base_entity_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"baseEntityId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"name\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value_field\": \"formSubmissionField\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"calculation\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"date\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"anmid\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"providerId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"location_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"locationId\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static String ec_client_weight = "{\n" +
            "  \"name\": \"vaccines\",\n" +
            "  \"columns\": [\n" +
            "    {\n" +
            "      \"column_name\": \"base_entity_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"baseEntityId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"kg\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"date\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"eventDate\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"anmid\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"providerId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"location_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"locationId\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static String ec_client_service = "{\n" +
            "  \"name\": \"vaccines\",\n" +
            "  \"columns\": [\n" +
            "    {\n" +
            "      \"column_name\": \"base_entity_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"baseEntityId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"name\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"1639AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
            "        \"value_field\": \"formSubmissionField\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"calculation\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"1639AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"date\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"eventDate\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"itn_date\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"obs.fieldCode\",\n" +
            "        \"concept\": \"159432AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\":\"itn_has_net\",\n" +
            "      \"json_mapping\":{\n" +
            "        \"field\":\"obs.fieldCode\",\n" +
            "        \"formSubmissionField\":\"Child_Has_Net\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"anmid\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"providerId\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"column_name\": \"location_id\",\n" +
            "      \"json_mapping\": {\n" +
            "        \"field\": \"locationId\"\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static String ec_client_fields = "{\n" +
            "  \"bindobjects\": [\n" +
            "    {\n" +
            "      \"name\": \"ec_household\",\n" +
            "\n" +
            "      \"columns\": [\n" +
            "        {\n" +
            "          \"column_name\": \"base_entity_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"baseEntityId\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"first_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"firstName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"lastName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"dob\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"birthdate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"hie_facilities\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"formSubmissionField\": \"HIE_FACILITIES\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"HHID\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.householdCode\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"Date_Of_Reg\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"formSubmissionField\": \"Date_Of_Reg\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address1\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressFields.address1\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address2\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressFields.address2\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_interacted_with\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"version\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ec_mother\",\n" +
            "      \"columns\": [\n" +
            "        {\n" +
            "          \"column_name\": \"base_entity_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"baseEntityId\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"openmrs_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"identifiers.OpenMRS_ID\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"relational_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"relationships.household\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"first_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"firstName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"husband_name\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"concept\": \"161135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"father_name\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"concept\": \"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"dob\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"birthdate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"epi_card_number\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.EPI Card Number\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"contact_phone_number\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"concept\": \"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"nrc_number\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.NRC_Number\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"lmp\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.lmp\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_uc\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_uc\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_town\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_town\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_id\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_location_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_location_id\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"client_reg_date\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"eventDate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"lastName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"gender\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"gender\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"marriage\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"marriage\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"town\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.town\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"union_council\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.subTown\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address1\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressFields.address1\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressType\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"date\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"eventDate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_interacted_with\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"version\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ec_child\",\n" +
            "      \"columns\": [\n" +
            "        {\n" +
            "          \"column_name\": \"base_entity_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"baseEntityId\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"openmrs_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"identifiers.OpenMRS_ID\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"inactive\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.inactive\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"lost_to_follow_up\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.lost_to_follow_up\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"relational_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"relationships.mother\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"first_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"firstName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_name\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"lastName\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"gender\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"gender\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"mother_first_name\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"formSubmissionField\": \"Mother_Guardian_First_Name\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"mother_last_name\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"formSubmissionField\": \"Mother_Guardian_Last_Name\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"father_name\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"concept\": \"1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"dob\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"birthdate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"dod\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"deathdate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"epi_card_number\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"attributes.Child_Register_Card_Number\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"contact_phone_number\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"formSubmissionField\": \"Mother_Guardian_Number\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"pmtct_status\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"obs.fieldCode\",\n" +
            "            \"concept\": \"1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_uc\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_uc\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_town\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_town\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_id\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"provider_location_id\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"provider_location_id\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"client_reg_date\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"eventDate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"town\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.town\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"union_council\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.subTown\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address1\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressFields.address1\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"address\",\n" +
            "          \"type\": \"Client\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"addresses.addressType\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"date\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"eventDate\"\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"column_name\": \"last_interacted_with\",\n" +
            "          \"type\": \"Event\",\n" +
            "          \"json_mapping\": {\n" +
            "            \"field\": \"version\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";



}

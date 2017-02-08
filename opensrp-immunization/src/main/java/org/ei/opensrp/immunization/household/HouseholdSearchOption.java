package org.ei.opensrp.immunization.household;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;

/**
 * Created by Safwan on 7/27/2016.
 */
public class HouseholdSearchOption implements SearchFilterOption {

        private String filter;

    public String getFilter(){
        return filter;
    }

        public HouseholdSearchOption(String filter) {
            this.filter = filter;
        }

        @Override
        public void setFilter(String filter) {
            this.filter = filter;
        }

        @Override
        public String getCriteria() {
            return " household_id = '" + filter + "' OR first_name LIKE '%"+filter+"%' " +
                    " OR contact_phone_number LIKE '%" + filter + "%' ";
        }

        @Override
        public boolean filter(SmartRegisterClient client) {
            CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
            if (currentclient.getDetails().get("first_name") != null
                    && currentclient.getDetails().get("first_name").toLowerCase().contains(filter.toLowerCase())) {
                return true;
            }
            if (currentclient.getDetails().get("program_client_id") != null
                    && currentclient.getDetails().get("program_client_id").equalsIgnoreCase(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("existing_program_client_id") != null
                    && currentclient.getDetails().get("existing_program_client_id").equalsIgnoreCase(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("epi_card_number") != null
                    && currentclient.getDetails().get("epi_card_number").contains(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("father_name") != null
                    && currentclient.getDetails().get("father_name").contains(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("mother_name") != null
                    && currentclient.getDetails().get("mother_name").contains(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("husband_name") != null
                    && currentclient.getDetails().get("husband_name").contains(filter)) {
                return true;
            }
            if (currentclient.getDetails().get("contact_phone_number") != null
                    && currentclient.getDetails().get("contact_phone_number").contains(filter)) {
                return true;
            }
       /* if(currentclient.getColumnmaps().get("existing_household_id") != null
                && currentclient.getColumnmaps().get("existing_household_id").toLowerCase().contains(criteria.toLowerCase())) {
            return true;
        }*/
            return false;
        }

        @Override
        public String name () {
            return Context.getInstance().applicationContext().getResources().getString(R.string.search_hint);
        }



}

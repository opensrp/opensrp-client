package org.ei.opensrp.path.option;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

public class BasicSearchOption implements FilterOption {
   public enum Type{
        CHILD, WOMAN;

        public static Type getByRegisterName(String registerName){
            for (Type t: values()) {
                if(registerName.toLowerCase().contains(t.name().toLowerCase())){
                    return t;
                }
            }
            throw new IllegalArgumentException("Register Name for SmartClientRegisterFragment implementer should contain entity type of enum Type");
        }
    }
    private String filter;
    private final Type type;

    public BasicSearchOption(String filter, Type type){
        this.filter = filter;
        this.type = type;
    }

    // FIXME path_conflict
    //@Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    // FIXME path_conflict
    //@Override
    public String getCriteria() {
        return filter;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
        if(currentclient.getDetails().get("first_name") != null
                && currentclient.getDetails().get("first_name").toLowerCase().contains(filter.toLowerCase())) {
            return true;
        }
        if(currentclient.getDetails().get("zeir_id") != null
                && currentclient.getDetails().get("zeir_id").equalsIgnoreCase(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("existing_zeir_id") != null
                && currentclient.getDetails().get("existing_zeir_id").equalsIgnoreCase(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("epi_card_number") != null
                && currentclient.getDetails().get("epi_card_number").contains(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("father_name") != null
                && currentclient.getDetails().get("father_name").contains(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("mother_first_name") != null
                && currentclient.getDetails().get("mother_first_name").contains(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("mother_last_name") != null
                && currentclient.getDetails().get("mother_last_name").contains(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("husband_name") != null
                && currentclient.getDetails().get("husband_name").contains(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("contact_phone_number") != null
                && currentclient.getDetails().get("contact_phone_number").contains(filter)) {
            return true;
        }
        return false;
    }

    @Override
    public String name() {
        return Context.getInstance().applicationContext().getResources().getString(R.string.search_hint);
    }
}

package com.example.opensrp_stock.field.util.common;

import com.example.opensrp_stock.R;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.view.contract.SmartRegisterClient;

public class BasicSearchOption implements SearchFilterOption {
   public enum Type{
        CHILD, WOMAN, t;

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

    public String getFilter(){
        return filter;
    }

    public BasicSearchOption(String filter, Type type){
        this.filter = filter;
        this.type = type;
    }

    @Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String getCriteria() {
        return " program_client_id = '"+filter+"' OR epi_card_number LIKE '%"+filter+"%' " +
                " OR first_name LIKE '%"+filter+"%' OR last_name LIKE '%"+filter+"%' " +
                " OR father_name LIKE '%"+filter+"%' " +
                (type.equals(Type.CHILD)?(" OR mother_name LIKE '%"+filter+"%' "):"" )+
                (type.equals(Type.WOMAN)? (" OR husband_name LIKE '%"+filter+"%' "):"" )+
                " OR contact_phone_number LIKE '%"+filter+"%'";
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
        if(currentclient.getDetails().get("first_name") != null
                && currentclient.getDetails().get("first_name").toLowerCase().contains(filter.toLowerCase())) {
            return true;
        }
        if(currentclient.getDetails().get("program_client_id") != null
                && currentclient.getDetails().get("program_client_id").equalsIgnoreCase(filter)) {
            return true;
        }
        if(currentclient.getDetails().get("existing_program_client_id") != null
                && currentclient.getDetails().get("existing_program_client_id").equalsIgnoreCase(filter)) {
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
        if(currentclient.getDetails().get("mother_name") != null
                && currentclient.getDetails().get("mother_name").contains(filter)) {
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

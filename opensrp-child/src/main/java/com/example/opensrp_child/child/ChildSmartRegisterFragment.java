package com.example.opensrp_child.child;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.opensrp_child.R;
import com.example.opensrp_stock.field.util.common.BasicSearchOption;
import com.example.opensrp_stock.field.util.common.SmartClientRegisterFragment;
import com.example.opensrp_stock.field.util.common.VaccinationServiceModeOption;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.domain.Address;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.handler.RegisterDataCursorLoaderHandler;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
import org.ei.opensrp.core.db.utils.RegisterQuery;
import org.ei.opensrp.core.template.CommonSortingOption;
import org.ei.opensrp.core.template.DefaultOptionsProvider;
import org.ei.opensrp.core.template.FilterOption;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.template.SearchType;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.template.SortingOption;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.getObsValue;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.nonEmptyValue;

public class ChildSmartRegisterFragment extends SmartClientRegisterFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private RegisterDataLoaderHandler loaderHandler;

    public ChildSmartRegisterFragment(){   super(null);   }

    @SuppressLint("ValidFragment")
    public ChildSmartRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    public String bindType() {
        return "pkchild";
    }

    @Override
    public RegisterDataLoaderHandler loaderHandler() {
        if (loaderHandler == null){
            loaderHandler = new RegisterDataCursorLoaderHandler(getActivity(),
                    new RegisterQuery("pkchild", "id", null, null).limitAndOffset(7, 0),
                    new RegisterCursorAdapter(getActivity(), clientsProvider()));
        }
        return loaderHandler;
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public SearchFilterOption searchFilterOption() {
                return new BasicSearchOption("", BasicSearchOption.Type.getByRegisterName(getDefaultOptionsProvider().nameInShortFormForTitle()));
            }
            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(null, "Vaccine", new int[]{
                        R.string.child_profile , R.string.birthdate_age,
                        R.string.child_last_vaccine, R.string.child_next_vaacine
                }, new int[]{8,4,4,4});
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortingOption sortOption() {
                return new CommonSortingOption(getResources().getString(R.string.woman_alphabetical_sort), "first_name");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.child_register_title);
            }

            @Override
            public SearchType searchType() {
                return SearchType.PASSIVE;
            }
        };
    }

    @Override
    protected RegisterClientsProvider clientsProvider() {
        return new ChildSmartClientsProvider(getActivity(), clientActionHandler, context.alertService());
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void onCreation() { }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.child_profile_info_layout) {/*DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), ChildDetailActivity.class);
                    getActivity().finish();*/
                ((RegisterActivity) getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), false);

            } else if (i == R.id.child_next_visit_holder) {
                HashMap<String, String> map = new HashMap<>();
                CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                map.putAll(followupOverrides(client));
                map.putAll(VaccinatorUtils.providerDetails());
                startForm("child_followup", ((SmartRegisterClient) view.getTag()).entityId(), map);

            }
        }
    }

    private HashMap<String, String> getPreloadVaccineData(CommonPersonObjectClient client) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_bcg", nonEmptyValue(client.getColumnmaps(), true, false, "bcg", "bcg_retro"));
        map.put("e_opv0", nonEmptyValue(client.getColumnmaps(), true, false, "opv0", "opv0_retro"));
        map.put("e_penta1", nonEmptyValue(client.getColumnmaps(), true, false, "penta1", "penta1_retro"));
        map.put("e_opv1", nonEmptyValue(client.getColumnmaps(), true, false, "opv1", "opv1_retro"));
        map.put("e_pcv1", nonEmptyValue(client.getColumnmaps(), true, false, "pcv1", "pcv1_retro"));
        map.put("e_penta2", nonEmptyValue(client.getColumnmaps(), true, false, "penta2", "penta2_retro"));
        map.put("e_opv2", nonEmptyValue(client.getColumnmaps(), true, false, "opv2", "opv2_retro"));
        map.put("e_pcv2", nonEmptyValue(client.getColumnmaps(), true, false, "pcv2", "pcv2_retro"));
        map.put("e_penta3", nonEmptyValue(client.getColumnmaps(), true, false, "penta3", "penta3_retro"));
        map.put("e_opv3", nonEmptyValue(client.getColumnmaps(), true, false, "opv3", "opv3_retro"));
        map.put("e_pcv3", nonEmptyValue(client.getColumnmaps(), true, false, "pcv3", "pcv3_retro"));
        map.put("e_ipv", nonEmptyValue(client.getColumnmaps(), true, false, "ipv", "ipv_retro"));
        map.put("e_measles1", nonEmptyValue(client.getColumnmaps(), true, false, "measles1", "measles1_retro"));
        map.put("e_measles2", nonEmptyValue(client.getColumnmaps(), true, false, "measles2", "measles2_retro"));
        return map;
    }

    private HashMap<String, String> getPreloadVaccineData(Client client) throws JSONException, ParseException {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_bcg", getObsValue(getClientEventDb(), client, true, "bcg", "bcg_retro"));
        map.put("e_opv0", getObsValue(getClientEventDb(), client, true, "opv0", "opv0_retro"));
        map.put("e_penta1", getObsValue(getClientEventDb(), client, true, "penta1", "penta1_retro"));
        map.put("e_opv1", getObsValue(getClientEventDb(), client, true, "opv1", "opv1_retro"));
        map.put("e_pcv1", getObsValue(getClientEventDb(), client, true, "pcv1", "pcv1_retro"));
        map.put("e_penta2", getObsValue(getClientEventDb(), client, true, "penta2", "penta2_retro"));
        map.put("e_opv2", getObsValue(getClientEventDb(), client, true, "opv2", "opv2_retro"));
        map.put("e_pcv2", getObsValue(getClientEventDb(), client, true, "pcv2", "pcv2_retro"));
        map.put("e_penta3", getObsValue(getClientEventDb(), client, true, "penta3", "penta3_retro"));
        map.put("e_opv3", getObsValue(getClientEventDb(), client, true, "opv3", "opv3_retro"));
        map.put("e_pcv3", getObsValue(getClientEventDb(), client, true, "pcv3", "pcv3_retro"));
        map.put("e_ipv", getObsValue(getClientEventDb(), client, true, "ipv", "ipv_retro"));
        map.put("e_measles1", getObsValue(getClientEventDb(), client, true, "measles1", "measles1_retro"));
        map.put("e_measles2", getObsValue(getClientEventDb(), client, true, "measles2", "measles2_retro"));

        return map;
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overrides) {
        return "child_enrollment";
    }

    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        overridemap.putAll(followupOverrides(client));
        return "child_offsite_followup";
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        Map<String, String> m = new HashMap<>();
        return m;
    }

    private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_full_address", getValue(client.getColumnmaps(), "address1", true)
                +", UC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", Town: "+ getValue(client.getColumnmaps(), "town", true)
                +", City: "+ getValue(client, "city_village", true)
                +", Province: "+ getValue(client, "province", true));
        map.put("existing_program_client_id", getValue(client.getColumnmaps(), "program_client_id", false));
        map.put("program_client_id", getValue(client.getColumnmaps(), "program_client_id", false));

        int days = 0;
        try{
            days = Days.daysBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getDays();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        map.put("existing_first_name", getValue(client.getColumnmaps(), "first_name", true));
        map.put("existing_last_name", getValue(client.getColumnmaps(), "last_name", true));
        map.put("existing_gender", getValue(client.getColumnmaps(), "gender", true));
        map.put("existing_mother_name", getValue(client.getColumnmaps(), "mother_name", true));
        map.put("existing_father_name", getValue(client.getColumnmaps(), "father_name", true));
        map.put("existing_birth_date", getValue(client.getColumnmaps(), "dob", false));
        map.put("existing_age", days+"");
        map.put("existing_epi_card_number", getValue(client.getColumnmaps(), "epi_card_number", false));
        map.put("reminders_approval", getValue(client.getColumnmaps(), "reminders_approval", false));
        map.put("contact_phone_number", getValue(client.getColumnmaps(), "contact_phone_number", false));

        map.putAll(getPreloadVaccineData(client));

        return map;
    }

    private Map<String, String> followupOverrides(Client client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_full_address", client.getAddress("usual_residence").getAddressField("address1")+
                ", UC: "+client.getAddress("usual_residence").getSubTown()+
                ", Town: "+client.getAddress("usual_residence").getTown()+
                ", City: "+client.getAddress("usual_residence").getCityVillage()+
                " - "+client.getAddress("usual_residence").getAddressField("landmark"));

        map.put("existing_program_client_id", client.getIdentifier("Program Client ID"));
        map.put("program_client_id", client.getIdentifier("Program Client ID"));

        map.put("existing_first_name", client.getFirstName());
        map.put("first_name", client.getFirstName());
        map.put("gender", client.getGender());
        map.put("dob", client.getBirthdate().toString("yyyy-MM-dd"));

        map.put("existing_birth_date", client.getBirthdate().toString("yyyy-MM-dd"));
        int days = 0;
        try{
            days = Days.daysBetween(client.getBirthdate(), DateTime.now()).getDays();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        map.put("existing_age", days+"");

        try{
            String ethnicity = getObsValue(getClientEventDb(), client, false, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            map.put("ethnicity", ethnicity);

            Address ad = client.getAddress("usual_residence");

            map.put("province", ad.getStateProvince());
            map.put("city_village", ad.getCityVillage());
            map.put("town", ad.getTown());
            map.put("union_council", ad.getSubTown());
            map.put("address1", ad.getAddressField("address1"));

            map.put("father_name", getObsValue(getClientEventDb(), client, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("mother_name", getObsValue(getClientEventDb(), client, true, "mother_name", "1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.put("reminders_approval", getObsValue(getClientEventDb(), client, false, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("contact_phone_number", getObsValue(getClientEventDb(), client, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.putAll(getPreloadVaccineData(client));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}

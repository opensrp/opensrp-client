package org.ei.opensrp.path.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.LoginActivity;
import org.ei.opensrp.path.activity.WomanDetailActivity;
import org.ei.opensrp.path.activity.WomanSmartRegisterActivity;
import org.ei.opensrp.path.db.Client;
import org.ei.opensrp.path.option.BasicSearchOption;
import org.ei.opensrp.path.option.DateSort;
import org.ei.opensrp.path.option.StatusSort;
import org.ei.opensrp.path.provider.WomanSmartClientsProvider;
import org.ei.opensrp.path.servicemode.VaccinationServiceModeOption;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;
import static util.VaccinatorUtils.providerDetails;

public class WomanSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            // FIXME path_conflict
            //@Override
            public FilterOption searchFilterOption() {
                return new BasicSearchOption("", BasicSearchOption.Type.getByRegisterName(getDefaultOptionsProvider().nameInShortFormForTitle()));
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(null, "TT", new int[]{
                        R.string.woman_profile , R.string.age, R.string.epi_number,
                        R.string.woman_edd, R.string.woman_contact_number, R.string.woman_last_vaccine, R.string.woman_next_vaacine
                }, new int[]{6,2,2,3,3,3,4});
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new CursorCommonObjectSort(getResources().getString(R.string.woman_alphabetical_sort), "first_name");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.woman_register_title);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.woman_alphabetical_sort), "first_name"),
                        new DateSort("Age", "dob"),
                        new StatusSort("Due Status"),
                        new CursorCommonObjectSort(getResources().getString(R.string.id_sort), "zeir_id")
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.search_hint);
            }
        };
    }//end of method

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {
    }

    @Override
    protected void startRegistration() {
        ((WomanSmartRegisterActivity) getActivity()).startFormActivity("woman_enrollment", null, null);
    }

    @Override
    protected void onCreation() {
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        updateSearchView();
        try {
            LoginActivity.setLanguage();
        } catch (Exception e) {

        }

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
    }

    public void initializeQueries() {
        String tableName = "ec_mother";

        WomanSmartClientsProvider hhscp = new WomanSmartClientsProvider(getActivity(),
                clientActionHandler, context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, Context.getInstance().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        countSelect = countqueryBUilder.mainCondition("");
        mainCondition = "";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{"relationalid", "details", "zeir_id", "first_name", "husband_name", "father_name", "dob", "epi_card_number", "contact_phone_number", "vaccines", "vaccines_2", "provider_uc", "provider_town", "provider_id", "provider_location_id", "client_reg_date", "last_name", "gender", "marriage", "town", "union_council", "address1", "address", "reminders_approval", "final_edd", "final_ga", "tt1_retro", "tt2_retro", "tt3_retro", "tt4_retro", "tt1", "tt2", "tt3", "tt4", "tt5", "date", "pregnant"});
        mainSelect = queryBUilder.mainCondition("");
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        updateSearchView();
        refresh();
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            HashMap<String, String> map = new HashMap<>();
            CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
            map.putAll(followupOverrides(client));
            map.putAll(providerDetails());

            String formName = "woman_followup";
            String registerFormName = "woman_enrollment";

            switch (view.getId()) {
                case R.id.woman_profile_info_layout:
                    WomanDetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), map, formName, registerFormName, WomanDetailActivity.class);
                    getActivity().finish();
                    break;
                case R.id.woman_next_visit_holder:
                    showFragmentDialog(new EditDialogOptionModel(map), view.getTag());
                    break;
            }
        }
    }

    public void updateSearchView() {
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                if (cs.toString().equalsIgnoreCase("")) {
                    filters = "";
                } else {
                    filters = cs.toString();
                }
                joinTable = "";
                mainCondition = "";
                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
                filterandSortExecute();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        HashMap<String, String> map;

        EditDialogOptionModel(HashMap<String, String> map) {
            this.map = map;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions(map);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }

    }

    private DialogOption[] getEditOptions(HashMap<String, String> map) {
        return ((WomanSmartRegisterActivity) getActivity()).getEditOptions(map);
    }

    private HashMap<String, String> getPreloadVaccineData(CommonPersonObjectClient client) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", nonEmptyValue(client.getColumnmaps(), true, false, "tt1", "tt1_retro"));
        map.put("e_tt2", nonEmptyValue(client.getColumnmaps(), true, false, "tt2", "tt2_retro"));
        map.put("e_tt3", nonEmptyValue(client.getColumnmaps(), true, false, "tt3", "tt3_retro"));
        map.put("e_tt4", nonEmptyValue(client.getColumnmaps(), true, false, "tt4", "tt4_retro"));
        map.put("e_tt5", nonEmptyValue(client.getColumnmaps(), true, false, "tt5", "tt5_retro"));

        return map;
    }

    //TODO EC model
    /*
    private HashMap<String, String> getPreloadVaccineData(Client client) throws JSONException, ParseException {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", getObsValue(getClientEventDb(), client, true, "tt1", "tt1_retro"));
        map.put("e_tt2", getObsValue(getClientEventDb(), client, true, "tt2", "tt2_retro"));
        map.put("e_tt3", getObsValue(getClientEventDb(), client, true, "tt3", "tt3_retro"));
        map.put("e_tt4", getObsValue(getClientEventDb(), client, true, "tt4", "tt4_retro"));
        map.put("e_tt5", getObsValue(getClientEventDb(), client, true, "tt5", "tt5_retro"));

        return map;
    }*/

    //TODO path_conflict
    //@Override
    protected String getRegistrationForm(HashMap<String, String> overrides) {
        return "woman_enrollment";
    }

    //@Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        overridemap.putAll(followupOverrides(client));
        return "offsite_woman_followup";
    }

    //@Override
    protected Map<String, String> customFieldOverrides() {
        Map<String, String> m = new HashMap<>();
        m.put("gender", "female");
        return m;
    }

    private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_full_address", getValue(client.getColumnmaps(), "address1", true)
                +", UC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", Town: "+ getValue(client.getColumnmaps(), "town", true)
                +", City: "+ getValue(client, "city_village", true)
                +", Province: "+ getValue(client, "province", true));
        map.put("existing_zeir_id", getValue(client.getColumnmaps(), "zeir_id", false));
        map.put("zeir_id", getValue(client.getColumnmaps(), "zeir_id", false));

        map.put("existing_first_name", getValue(client.getColumnmaps(), "first_name", true));
        map.put("existing_father_name", getValue(client.getColumnmaps(), "father_name", true));
        map.put("existing_husband_name", getValue(client.getColumnmaps(), "husband_name", true));
        map.put("husband_name", getValue(client.getColumnmaps(), "husband_name", true));
        map.put("existing_birth_date", getValue(client.getColumnmaps(), "dob", false));
        int years = 0;
        try{
            years = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        }
        catch (Exception e){  }

        map.put("existing_age", years+"");
        map.put("existing_epi_card_number", getValue(client.getColumnmaps(), "epi_card_number", false));
        map.put("marriage", getValue(client.getColumnmaps(), "marriage", false));
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

        map.put("existing_zeir_id", client.getIdentifier("Program Client ID"));
        map.put("zeir_id", client.getIdentifier("Program Client ID"));

        map.put("existing_first_name", client.getFirstName());
        map.put("existing_birth_date", client.getBirthdate().toString("yyyy-MM-dd"));
        int years = 0;
        try{
            years = Years.yearsBetween(client.getBirthdate(), DateTime.now()).getYears();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        map.put("existing_age", years+"");
        Object epi = client.getAttribute("EPI Card Number");
        map.put("existing_epi_card_number", epi == null ? "" : epi.toString());

        //TODO EC model
        /* try{
            map.put("existing_father_name", getObsValue(getClientEventDb(), client, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_husband_name", getObsValue(getClientEventDb(), client, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("husband_name", getObsValue(getClientEventDb(), client, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("marriage", getObsValue(getClientEventDb(), client, false, "marriage"));
            map.put("reminders_approval", getObsValue(getClientEventDb(), client, false, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("contact_phone_number", getObsValue(getClientEventDb(), client, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.putAll(getPreloadVaccineData(client));
        }
        catch (Exception e){
            e.printStackTrace();
        } */
        return map;
    }


}
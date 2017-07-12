package org.ei.opensrp.mcare.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.household.CensusEnrollmentHandler;
import org.ei.opensrp.mcare.household.HHMauzaCommonObjectFilterOption;
import org.ei.opensrp.mcare.household.HouseHoldDetailActivity;
import org.ei.opensrp.mcare.household.HouseHoldServiceModeOption;
import org.ei.opensrp.mcare.household.HouseHoldSmartClientsProvider;
import org.ei.opensrp.mcare.household.HouseHoldSmartRegisterActivity;
import org.ei.opensrp.mcare.household.HouseholdCensusDueDateSort;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 10/12/15.
 */
public class HouseHoldSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private String locationDialogTAG = "locationDialogTAG";
    @Override
    protected void onCreation() {
        //
    }

//    @Override
//    protected SmartRegisterPaginatedAdapter adapter() {
//        return new SmartRegisterPaginatedAdapter(clientsProvider());
//    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new HouseHoldServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new HouseholdCensusDueDateSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.hh_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {

                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label),filterStringForAll()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_no_mwra),filterStringForNoElco()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_has_mwra),filterStringForOneOrMoreElco()));

                String locationjson = context().anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

                Map<String,TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();
                addChildToList(dialogOptionslist,locationMap);
                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0;i < dialogOptionslist.size();i++){
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return  dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                        new CursorCommonObjectSort(getResources().getString(R.string.due_status),filters.contains("nidImage")? householdSortByName() : sortByAlertmethod()),
                        new CursorCommonObjectSort(getResources().getString(R.string.hh_alphabetical_sort),householdSortByName()),
                        new CursorCommonObjectSort(getResources().getString(R.string.hh_fwGobhhid_sort),householdSortByFWGOBHHID()),
                        new CursorCommonObjectSort(getResources().getString(R.string.hh_fwJivhhid_sort),householdSortByFWJIVHHID())
//""
//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getResources().getString(R.string.hh_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
//        if (clientProvider == null) {
//            clientProvider = new HouseHoldSmartClientsProvider(
//                    getActivity(),clientActionHandler , context.alertService());
//        }
        return null;
    }

    private DialogOption[] getEditOptions() {
        return ((HouseHoldSmartRegisterActivity)getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
        context().formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new
                CensusEnrollmentHandler());
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }

    private String sortByAlertmethod() {
       return " CASE WHEN FW_CENSUS = 'urgent' THEN '1'\n" +
                "WHEN FW_CENSUS = 'upcoming' THEN '2'\n" +
                "WHEN FW_CENSUS = 'normal' THEN '3'\n" +
                "WHEN FW_CENSUS = 'expired' THEN '4'\n" +
                "WHEN FW_CENSUS is Null THEN '5'\n" +
                "Else FW_CENSUS END ASC";
    }

    public String houseHoldMainCount(){
        return "Select Count(*) from (Select *, " +
                "(Select count(*)  from ec_elco where ec_elco.relational_id = ec_household.base_entity_id) as ELCO " +
                "from ec_household) ec_household ";
    }

    public void initializeQueries(){
        try {
            HouseHoldSmartClientsProvider hhscp = new HouseHoldSmartClientsProvider(getActivity(),clientActionHandler,context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, new CommonRepository("ec_household",new String []{"FWHOHFNAME", "FWGOBHHID","FWJIVHHID","existing_Mauzapara", "ELCO"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_household");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder(houseHoldMainCount());
            mainCondition = " FWHOHFNAME is not null ";
            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            String elcoCountSubQuery = "(Select count(*)  from ec_elco where ec_elco.relational_id = ec_household.base_entity_id) as ELCO";

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable("ec_household", new String[]{"relationalid", elcoCountSubQuery, "FWHOHFNAME", "FWGOBHHID", "FWJIVHHID", "existing_Mauzapara"});
            mainSelect = queryBUilder.mainCondition(mainCondition);
            Sortqueries = sortByAlertmethod();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();

//        setServiceModeViewDrawableRight(null);
            updateSearchView();
            refresh();
//        checkforNidMissing(view);
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }


    }


    @Override
    public void startRegistration() {
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        McareLocationSelectorDialogFragment
                .newInstance((HouseHoldSmartRegisterActivity) getActivity(), new
                        EditDialogOptionModel(), context().anmLocationController().get(),
                        "new_household_registration")
                .show(ft, locationDialogTAG);
    }
    
    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),HouseHoldDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                case R.id.hh_due_date:
                    HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();

                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }


    private String filterStringForOneOrMoreElco(){
        return " and ELCO > 0";
    }
    private String filterStringForNoElco(){
        return " and ELCO = 0";
    }
    private String filterStringForAll(){
        return "";
    }
    private String householdSortByName() {
        return " FWHOHFNAME COLLATE NOCASE ASC";
    }
    private String householdSortByFWGOBHHID(){
        return " FWGOBHHID ASC";
    }
    private String householdSortByFWJIVHHID(){
      return " FWJIVHHID ASC";
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    @Override
    protected void onResumption() {
//        super.onResumption();
        getDefaultOptionsProvider();
        if(isPausedOrRefreshList()) {
            initializeQueries();
        }
//        updateSearchView();
        checkforNidMissing(mView);
//
        try{
            LoginActivity.setLanguage();
        }catch (Exception e){

        }

    }
    @Override
    public void setupSearchView(View view) {
        searchView = (EditText) view.findViewById(org.ei.opensrp.R.id.edt_search);
        searchView.setHint(getNavBarOptionsProvider().searchHint());
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                //filters = "and FWHOHFNAME Like '%" + cs.toString() + "%' or FWGOBHHID Like '%" + cs.toString() + "%'  or FWJIVHHID Like '%" + cs.toString() + "%' ";
                filters = cs.toString();
                joinTable = "";
                mainCondition = " FWHOHFNAME is not null ";

                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
                filterandSortExecute();

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        searchCancelView = view.findViewById(org.ei.opensrp.R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    public void updateSearchView(){
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                //filters = "and FWHOHFNAME Like '%"+cs.toString()+"%' or FWGOBHHID Like '%"+cs.toString()+"%'  or FWJIVHHID Like '%"+cs.toString()+"%' or household.id in (Select elco.relationalid from elco where FWWOMFNAME Like '%"+cs.toString()+"%' )";
                filters = cs.toString();
                joinTable = "ec_elco";
                mainCondition = " FWHOHFNAME is not null ";

                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                filterandSortExecute();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void addChildToList(ArrayList<DialogOption> dialogOptionslist,Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist,entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new HHMauzaCommonObjectFilterOption(name, "location_name", name, "ec_household"));

            }
        }
    }

    private void checkforNidMissing(View view) {
        RelativeLayout titlelayout = (RelativeLayout)view.findViewById(org.ei.opensrp.R.id.register_nav_bar_container);
        if(anyNIdmissing(controller)) {
            try {
                titlelayout.removeView(getActivity().findViewById(R.id.warnid)) ;

            }catch(Exception e){

            }
            ImageView border = new ImageView(getActivity());
            border.setImageDrawable(getResources().getDrawable(R.drawable.separator));
            RelativeLayout.LayoutParams layoutParams_separator = new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.smart_register_nav_bar_separator), RelativeLayout.LayoutParams.FILL_PARENT);
            border.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageButton warn = new ImageButton(getActivity());
            warn.setImageDrawable(getResources().getDrawable(R.mipmap.warning));
            warn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            warn.setBackground(null);
            warn.setId(R.id.warnid);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.LEFT_OF,R.id.sort_selection);
            layoutParams_separator.addRule(RelativeLayout.RIGHT_OF,warn.getId());
            titlelayout.addView(warn, layoutParams);
            titlelayout.addView(border, layoutParams_separator);
            warn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    getClientsAdapter()
//                            .refreshList(new noNIDFilter(), getCurrentServiceModeOption(),
//                                    getCurrentSearchFilter(), getCurrentSortOption());
                    filters = "and ec_household.id in ( Select Distinct ec_elco.relational_id from ec_elco where base_entity_id not in (select base_entity_id from ec_details where key MATCH 'nidImage' ) and base_entity_id  in (select base_entity_id from ec_details where key MATCH 'FWELIGIBLE2' INTERSECT select base_entity_id from ec_details where value MATCH '1') group by ec_elco.base_entity_id )";
                    joinTable = "";
                    Sortqueries = householdSortByName();
                    CountExecute();
                    filterandSortExecute();

                }
            });
        }else{
            titlelayout.removeView(getActivity().findViewById(R.id.warnid));
        }
    }

    private boolean anyNIdmissing(CommonPersonObjectController controller) {
        boolean toreturn = false;
//        List<CommonPersonObject> allchildelco = null;
//        CommonPersonObjectClients clients = controller.getClients();
//        ArrayList<String> list = new ArrayList<String>();
//        AllCommonsRepository allElcoRepository = Context.getInstance().allCommonsRepositoryobjects("elco");
//
//        for(int i = 0;i <clients.size();i++) {
//
//            list.add((clients.get(i).entityId()));
//
//        }
//        allchildelco = allElcoRepository.findByRelationalIDs(list);
//
//        if(allchildelco != null) {
//            for (int i = 0; i < allchildelco.size(); i++) {
//                if (allchildelco.get(i).getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {
//                    if (allchildelco.get(i).getDetails().get("nidImage") == null) {
//                        toreturn = true;
//                    }
//                }
//            }
//        }
        CommonRepository commonRepository = context().commonrepository("ec_household");
        setTablename("ec_household");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("ec_household");
        countqueryBUilder.joinwithALerts("ec_household", "FW CENSUS");
        countqueryBUilder.mainCondition(" FWHOHFNAME is not null ");
        String nidfilters = "and ec_household.id in ( Select Distinct ec_elco.relational_id from ec_elco where base_entity_id not in (select base_entity_id from ec_details where key MATCH 'nidImage' ) and base_entity_id  in (select base_entity_id from ec_details where key MATCH 'FWELIGIBLE2' INTERSECT select base_entity_id from ec_details where value MATCH '1') group by ec_elco.base_entity_id )";

        countqueryBUilder.addCondition(nidfilters);
        Cursor c = commonRepository.RawCustomQueryForAdapter(countqueryBUilder.Endquery(countqueryBUilder.toString()));
        c.moveToFirst();
        int missingnidCount = c.getInt(0);
        c.close();
        if(missingnidCount>0){
            toreturn = true;
        }
        return toreturn;
//        return false;
    }
}

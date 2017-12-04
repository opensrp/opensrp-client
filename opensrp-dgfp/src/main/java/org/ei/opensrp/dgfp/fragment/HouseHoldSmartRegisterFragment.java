package org.ei.opensrp.dgfp.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.dgfp.LoginActivity;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.dgfp.hh_member.HHWardCommonObjectFilterOption;
import org.ei.opensrp.dgfp.hh_member.HH_member_SmartRegisterActivity;
import org.ei.opensrp.dgfp.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.dgfp.hh_member.HouseHoldServiceModeOption;
import org.ei.opensrp.dgfp.hh_member.HouseHoldSmartClientsProvider;
import org.ei.opensrp.dgfp.hh_member.HouseholdCensusDueDateSort;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_male),filterStringForMale()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_female),filterStringForFemale()));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward1),filterStringForWard("Ward-1")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward2),filterStringForWard("Ward-2")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward3),filterStringForWard("Ward-3")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block1ka),filterStringForBlock("1-KA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block1kha),filterStringForBlock("1-KHA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block2ka),filterStringForBlock("2-KA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block2kha),filterStringForBlock("2-KHA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block3ka),filterStringForBlock("3-KA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block3kha),filterStringForBlock("3-KHA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block4ka),filterStringForBlock("4-KA")));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block4kha),filterStringForBlock("4-KHA")));

                String locationjson = context.anmLocationController().get();
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

                        new CursorCommonObjectSort(getResources().getString(R.string.due_status),sortByAlertmethod()),
                        new CursorCommonObjectSort(getResources().getString(R.string.hh_alphabetical_sort),householdSortByName()),
                        new CursorCommonObjectSort(getResources().getString(R.string.hh_fwGobhhid_sort),householdSortByGOBHHID())
//""
//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getResources().getString(R.string.search_register);
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
        return ((HH_member_SmartRegisterActivity)getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
//        controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("household"),
//                context.allBeneficiaries(), context.listCache(),
//                context.personObjectClientsCache(),"FWHOHFNAME","household","FWGOBHHID", CommonPersonObjectController.ByColumnAndByDetails.byDetails,new HouseholdCensusDueDateSort());

//        villageController = new VillageController(context.allEligibleCouples(),
//                context.listCache(), context.villagesCache());
//        dialogOptionMapper = new DialogOptionMapper();
//        context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new CensusEnrollmentHandler());
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }

    private String sortByAlertmethod() {
       return " CASE WHEN alerts.status = 'urgent' THEN '1'"
         +
        "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "Else alerts.status END ASC";
    }
    public void initializeQueries(){
        CommonRepository commonRepository = context.commonrepository("household");
        setTablename("household");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("household");
        countqueryBUilder.joinwithALerts("household","FW CENSUS");
        Sortqueries = sortByAlertmethod();
        countSelect = countqueryBUilder.mainCondition(" HoH_F_Name is not null ");
        CountExecute();


        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("household", new String[]{"relationalid", "details","HoH_F_Name","GoB_HHID"});
        queryBUilder.joinwithALerts("household","FW CENSUS");
        mainSelect = queryBUilder.mainCondition(" HoH_F_Name is not null ");
        queryBUilder.addCondition(filters);
        Sortqueries = sortByAlertmethod();
        currentquery  = queryBUilder.orderbyCondition(Sortqueries);


//        queryBUilder.queryForRegisterSortBasedOnRegisterAndAlert("household", new String[]{"relationalid" ,"details","FWHOHFNAME", "FWGOBHHID","FWJIVHHID"}, null, "FW CENSUS");
//        Cursor c = commonRepository.CustomQueryForAdapter(new String[]{"id as _id","relationalid","details"},"household",""+currentlimit,""+currentoffset);
        Cursor c = commonRepository.RawCustomQueryForAdapter(queryBUilder.Endquery(queryBUilder.addlimitandOffset(currentquery, 20, 0)));
        HouseHoldSmartClientsProvider hhscp = new HouseHoldSmartClientsProvider(getActivity(),clientActionHandler,context.alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), c, hhscp, new CommonRepository("household",new String []{"HoH_F_Name","GoB_HHID"}));
        clientsView.setAdapter(clientAdapter);
//        setServiceModeViewDrawableRight(null);
        updateSearchView();
        refresh();
//        checkforNidMissing(view);

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
                .newInstance((HH_member_SmartRegisterActivity) getActivity(), new EditDialogOptionModel(), context.anmLocationController().get(), "new_household_registration")
                .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();
                    ((HH_member_SmartRegisterActivity)getActivity()).showProfileView();
//                    Intent intent = new Intent(getActivity(),HouseHoldDetailActivity.class);
//                    startActivity(intent);
//                    getActivity().finish();
                    break;
                case R.id.next_visit_date:
//                    HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();
//
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }


    private String filterStringForFemale(){
        return "and details LIKE '%\"HoH_Gender\":\"2\"%'";
    }
    private String filterStringForMale(){
        return " and details LIKE '%\"HoH_Gender\":\"1\"%'";
    }
    private String filterStringForAll(){
        return "";
    }
    private String filterStringForWard(String ward){
        return filters +"and  details like '%\"WARD\":\""+ward+"\"%' ";
    }
    private String filterStringForBlock(String block){
        return filters +"and  details like '%\"BLOCK\":\""+block+"\"%' ";
    }
    private String householdSortByName() {
        return " HoH_F_Name COLLATE NOCASE ASC";
    }
    private String householdSortByGOBHHID(){
        return "CAST(GoB_HHID AS INTEGER) ASC";
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
        initializeQueries();
//        updateSearchView();
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

                (new AsyncTask() {
                    SmartRegisterClients filteredClients;

                    @Override
                    protected Object doInBackground(Object[] params) {
//                        currentSearchFilter =
//                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
//                        filteredClients = getClientsAdapter().getListItemProvider()
//                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
//                                        getCurrentSearchFilter(), getCurrentSortOption());
//
                        if (cs.toString().equalsIgnoreCase("")) {
                            filters = "";
                        } else{
                            filters = "and (HoH_F_Name Like '%" + cs.toString() + "%' or GoB_HHID Like '%" + cs.toString() + "%'  or details Like '%" + cs.toString() + "%'" + "or household.id in (Select members.relationalid from members where Mem_F_Name Like '%" + cs.toString() + "%' ))";
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
//                        getClientsAdapter().refreshClients(filteredClients);
//                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                        CountExecute();
                        filterandSortExecute();
                        super.onPostExecute(o);
                    }
                }).execute();
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
                (new AsyncTask() {
                    SmartRegisterClients filteredClients;

                    @Override
                    protected Object doInBackground(Object[] params) {
//                        currentSearchFilter =
//                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
//                        filteredClients = getClientsAdapter().getListItemProvider()
//                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
//                                        getCurrentSearchFilter(), getCurrentSortOption());
//
                        filters = "and HoH_F_Name Like '%" + cs.toString() + "%' or GoB_HHID Like '%" + cs.toString() + "%'  or details Like '%" + cs.toString() + "%'" +"or household.id in (Select members.relationalid from members where Mem_F_Name Like '%"+cs.toString()+"%' )";
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
//                        getClientsAdapter().refreshClients(filteredClients);
//                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                        filterandSortExecute();
                        super.onPostExecute(o);
                    }
                }).execute();
//                currentSearchFilter = new HHSearchOption(cs.toString());
//                clientsAdapter
//                        .refreshList(currentVillageFilter, currentServiceModeOption,
//                                currentSearchFilter, currentSortOption);
//
//                searchCancelView.setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);


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
                dialogOptionslist.add(new HHWardCommonObjectFilterOption(name,"existing_Mauzapara", name));

            }
        }
    }


}

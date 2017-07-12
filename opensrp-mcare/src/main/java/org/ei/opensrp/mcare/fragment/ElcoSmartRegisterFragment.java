package org.ei.opensrp.mcare.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.CursorFilterOption;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.elco.EcElcoSmartClientsProvider;
import org.ei.opensrp.mcare.elco.ElcoDetailActivity;
import org.ei.opensrp.mcare.elco.ElcoMauzaCommonObjectFilterOption;
import org.ei.opensrp.mcare.elco.ElcoPSRFDueDateSort;
import org.ei.opensrp.mcare.elco.ElcoServiceModeOption;
import org.ei.opensrp.mcare.elco.ElcoSmartClientsProvider;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.mcare.elco.PSRFHandler;
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
import java.util.HashMap;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 11/2/15.
 */
public class ElcoSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();


    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new ElcoServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new ElcoPSRFDueDateSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(org.ei.opensrp.R.string.ec_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label),""));
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
                        new CursorCommonObjectSort(getString(R.string.due_status),sortByAlertmethod()),
                        new CursorCommonObjectSort(getString(R.string.elco_alphabetical_sort),householdSortByName()),
                         new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.hh_fwGobhhid_sort),householdSortByFWGOBHHID()),
                        new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.hh_fwJivhhid_sort),householdSortByFWJIVHHID())

//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getString(org.ei.opensrp.R.string.str_ec_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
//            clientProvider = new ElcoSmartClientsProvider(
//                    getActivity(),clientActionHandler , controller);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {


        context().formSubmissionRouter().getHandlerMap().put("psrf_form", new PSRFHandler());
    }

    @Override
    protected void startRegistration() {
        ((ElcoSmartRegisterActivity)getActivity()).startRegistration();
    }

    @Override
    protected void onCreation() {
    }
    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if(isPausedOrRefreshList()) {
            initializeQueries();
        }
        try{
            LoginActivity.setLanguage();
        }catch (Exception e){

        }

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
//
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);
//
        ImageButton startregister = (ImageButton)view.findViewById(org.ei.opensrp.R.id.register_client);
        startregister.setVisibility(View.GONE);
        setServiceModeViewDrawableRight(null);
//        updateSearchView();
        getDefaultOptionsProvider();

//        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
           clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }
    private String householdSortByName() {
        return " FWWOMFNAME COLLATE NOCASE ASC";
    }
    private String householdSortByFWGOBHHID(){
        return " GOBHHID ASC";
    }
    private String householdSortByFWJIVHHID(){
        return " JiVitAHHID ASC";
    }


    private DialogOption[] getEditOptions(CommonPersonObjectClient tag) {
        return ((ElcoSmartRegisterActivity)getActivity()).getEditOptions(tag);
    }
    private DialogOption[] getEditOptionsForMIS_ELCO(CommonPersonObjectClient tag,String alertstate) {
        return ((ElcoSmartRegisterActivity)getActivity()).getEditOptionsForMISELCO(tag,alertstate);
    }


    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    ElcoDetailActivity.Elcoclient = (CommonPersonObjectClient)view.getTag();
                    ((ElcoSmartRegisterActivity)getActivity()).showProfileView();
                    break;
                case R.id.psrf_due_date:
                    showFragmentDialog(new EditDialogOptionModel((CommonPersonObjectClient) view.getTag()), view.getTag());
                    break;
                case R.id.mis_elco:
                    showFragmentDialog(new EditDialogOptionModelForMIS_ELCO((CommonPersonObjectClient)view.getTag(R.id.clientobject),(String)view.getTag(R.id.AlertStatustextforMIS_ELCO)), view.getTag());
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        CommonPersonObjectClient tag;
        public EditDialogOptionModel(CommonPersonObjectClient tag) {
            this.tag = tag;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions(tag);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
    private class EditDialogOptionModelForMIS_ELCO implements DialogOptionModel {
        CommonPersonObjectClient tag;
        String alertstatus;
        public EditDialogOptionModelForMIS_ELCO(CommonPersonObjectClient tag,String alertstate) {
            this.tag = tag;
            alertstatus = alertstate;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptionsForMIS_ELCO(tag,alertstatus);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
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

                if(cs.toString().equalsIgnoreCase("")){
                    filters = "";
                }else {
                    //filters = "and FWWOMFNAME Like '%" + cs.toString() + "%' or GOBHHID Like '%" + cs.toString() + "%'  or JiVitAHHID Like '%" + cs.toString() + "%' ";
                    filters = cs.toString();
                }
                joinTable = "";
                mainCondition = " FWWOMFNAME is not null and is_closed=0 ";

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

                if (cs.toString().equalsIgnoreCase("")) {
                    filters = "";
                } else {
                    //filters = "and FWWOMFNAME Like '%" + cs.toString() + "%' or GOBHHID Like '%" + cs.toString() + "%'  or JiVitAHHID Like '%" + cs.toString() + "%' ";
                    filters = cs.toString();
                }
                joinTable = "";
                mainCondition = " FWWOMFNAME is not null and is_closed=0 ";

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
                dialogOptionslist.add(new ElcoMauzaCommonObjectFilterOption(name,"location_name",name,"ec_elco"));

            }
        }
    }
    private void initializeQueries(){
        EcElcoSmartClientsProvider hhscp = new EcElcoSmartClientsProvider(getActivity(),clientActionHandler,context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, new CommonRepository("ec_elco",new String []{ "FWWOMFNAME","relational_id", "JiVitAHHID", "GOBHHID"}));
        clientsView.setAdapter(clientAdapter);

        setTablename("ec_elco");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("ec_elco");
        mainCondition = " FWWOMFNAME is not null and is_closed=0 ";
        countSelect = countqueryBUilder.mainCondition(mainCondition);

        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("ec_elco", new String[]{"relationalid", "relational_id", "details", "FWWOMFNAME", "JiVitAHHID", "GOBHHID", "base_entity_id", "FWHUSNAME", "FWWOMAGE", "FWWOMNID", "FWWOMBID", "FWPSRDATE", "FWPSRPREGSTS"});
        mainSelect = queryBUilder.mainCondition(mainCondition);
        Sortqueries = sortByAlertmethod();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

//        setServiceModeViewDrawableRight(null);
//        updateSearchView();
        refresh();

    }

    private String sortByAlertmethod() {
        return " CASE WHEN ELCO_PSRF = 'urgent' THEN '1'\n" +
                "WHEN ELCO_PSRF = 'upcoming' THEN '2'\n" +
                "WHEN ELCO_PSRF = 'normal' THEN '3'\n" +
                "WHEN ELCO_PSRF = 'expired' THEN '4'\n" +
                "WHEN ELCO_PSRF is Null THEN '5'\n" +
                "WHEN ELCO_PSRF = 'complete' THEN '6'\n" +
                "Else ELCO_PSRF END ASC";
    }

    /**
     * Override filter to capture fts filter by location
     * @param filter
     */
    @Override
    public void onFilterSelection(FilterOption filter) {
        super.onFilterSelection(filter);
    }
}

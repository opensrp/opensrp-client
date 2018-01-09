package org.ei.opensrp.mcare.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.CursorFilterOption;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.child.ChildDetailActivity;
import org.ei.opensrp.mcare.child.childSort;
import org.ei.opensrp.mcare.child.mCareChildServiceModeOption;
import org.ei.opensrp.mcare.child.mCareChildSmartClientsProvider;
import org.ei.opensrp.mcare.child.mCareChildSmartRegisterActivity;
import org.ei.opensrp.mcare.elco.ElcoPSRFDueDateSort;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.customControls.CustomFontTextView;
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
 * Created by koros on 11/2/15.
 */
public class mCareChildSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

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
                return new mCareChildServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new childSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.mcare_Child_register_title_in_short);
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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_encc1),filterStringForENCCRV1()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_encc2),filterStringForENCCRV2()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_encc3),filterStringForENCCRV3()));

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
//                        new ElcoPSRFDueDateSort(),
                        new CursorCommonObjectSort(getString(R.string.due_status),sortByAlertmethod()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.elco_alphabetical_sort),sortByFWWOMFNAME()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.hh_fwGobhhid_sort),sortByGOBHHID()),
                        new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.hh_fwJivhhid_sort),sortByJiVitAHHID()),

//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ec_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {

        return null;
    }


    @Override
    protected void onInitialization() {

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
        updateSearchView();
        try{
            LoginActivity.setLanguage();
        }catch (Exception e){

        }

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        ImageButton startregister = (ImageButton)view.findViewById(org.ei.opensrp.R.id.register_client);
        startregister.setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
    }

    private DialogOption[] getEditOptionsforChild(String childvisittext,String childvisitstatus) {
        return ((mCareChildSmartRegisterActivity)getActivity()).getEditOptionsforChild(childvisittext, childvisitstatus);
    }



    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    ChildDetailActivity.ChildClient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),ChildDetailActivity.class);
                    startActivity(intent);
                    break;
                case R.id.encc_reminder_due_date:
                    CustomFontTextView enccreminderDueDate = (CustomFontTextView)view.findViewById(R.id.encc_reminder_due_date);
                    Log.v("do as you will", (String) view.getTag(R.id.textforEnccRegister));
                    showFragmentDialog(new EditDialogOptionModelForChild((String)view.getTag(R.id.textforEnccRegister),(String)view.getTag(R.id.AlertStatustextforEnccRegister)), view.getTag(R.id.clientobject));
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }

    private class EditDialogOptionModelForChild implements DialogOptionModel {
        String childvisittext ;;
        String childvisitstatus;
        public EditDialogOptionModelForChild(String text,String status) {
            childvisittext = text;
            childvisitstatus = status;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptionsforChild(childvisittext,childvisitstatus);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
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
                    mainCondition = " FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ";
                } else {
                    //filters = "and FWWOMFNAME Like '%" + cs.toString() + "%' or GOBHHID Like '%" + cs.toString() + "%'  or JiVitAHHID Like '%" + cs.toString() + "%' ";
                    filters = cs.toString();
                    filters = "";
                    mainCondition = " FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ";
                    mainCondition += " AND (("+ CommonFtsObject.idColumn +" IN (SELECT "+CommonRepository.ID_COLUMN+ " FROM " + "mcarechild"+ " WHERE details LIKE '%" + cs.toString()+ "%' )) " +
                            "or ("+ CommonFtsObject.relationalIdColumn +" IN (SELECT "+CommonRepository.ID_COLUMN+ " FROM " + "mcaremother"+ " WHERE FWWOMFNAME LIKE '%" + cs.toString()+ "%' ))" +
                            "or ("+ CommonFtsObject.relationalIdColumn +" IN (SELECT "+CommonRepository.ID_COLUMN+ " FROM " + "mcaremother"+ " WHERE GOBHHID LIKE '%" + cs.toString()+ "%' ))" +
                            "or ("+ CommonFtsObject.relationalIdColumn +" IN (SELECT "+CommonRepository.ID_COLUMN+ " FROM " + "mcaremother"+ " WHERE JiVitAHHID LIKE '%" + cs.toString()+ "%' ))" +
                            ")";


                }
                joinTable = "";
//                mainCondition = " FWBNFGEN is not null ";


                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(name," and mcaremother.details like '%"+name +"%'"));

            }
        }
    }
    class pncControllerfiltermap extends ControllerFilterMap{

        @Override
        public boolean filtermapLogic(CommonPersonObject commonPersonObject) {
            boolean returnvalue = false;
            if(commonPersonObject.getDetails().get("FWWOMVALID") != null){
                if(commonPersonObject.getDetails().get("FWWOMVALID").equalsIgnoreCase("1")){
                    returnvalue = true;
                    if(commonPersonObject.getDetails().get("Is_PNC")!=null){
                        if(commonPersonObject.getDetails().get("Is_PNC").equalsIgnoreCase("1")){
                            returnvalue = true;
                        }

                    }else{
                        returnvalue = false;
                    }
                }
            }
            Log.v("the filter", "" + returnvalue);
            return returnvalue;
        }
    }
    public void initializeQueries(){
        mCareChildSmartClientsProvider hhscp = new mCareChildSmartClientsProvider(getActivity(),
                clientActionHandler,context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, new CommonRepository("mcarechild",new String []{ "FWBNFGEN"}));
        clientsView.setAdapter(clientAdapter);

        setTablename("mcarechild");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder(childMainCountWithJoins());
        countSelect = countqueryBUilder.mainCondition(" mcarechild.FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ");
        mainCondition = " FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder(childMainSelectWithJoins());
        mainSelect = queryBUilder.mainCondition(" mcarechild.FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ");
        Sortqueries = sortByAlertmethod();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

//        setServiceModeViewDrawableRight(null);
//        updateSearchView();
        refresh();

    }
    private String sortByAlertmethod() {
        return " CASE WHEN Essential_Newborn_Care_Checklist = 'urgent' THEN '1'\n" +
                "WHEN Essential_Newborn_Care_Checklist = 'upcoming' THEN '2'\n" +
                "WHEN Essential_Newborn_Care_Checklist = 'normal' THEN '3'\n" +
                "WHEN Essential_Newborn_Care_Checklist = 'expired' THEN '4'\n" +
                "WHEN Essential_Newborn_Care_Checklist is Null THEN '5'\n" +
                "WHEN Essential_Newborn_Care_Checklist = 'complete' THEN '6'\n" +
                "Else Essential_Newborn_Care_Checklist END ASC";
    }
    public String childMainSelectWithJoins(){
        return "Select mcarechild.id as _id,mcarechild.relationalid,mcarechild.details,mcarechild.FWBNFGEN \n" +
                "from mcarechild\n";
    }
    public String childMainCountWithJoins() {
        return "Select Count(*) \n" +
                "from mcarechild\n";
    }

    private String sortBySortValue(){
        return " ABS(FWSORTVALUE) DESC";
    }
    private String sortByFWWOMFNAME(){
        return " FWWOMFNAME ASC";
    }
    private String sortByJiVitAHHID(){
        return " JiVitAHHID ASC";
    }
    private String sortByGOBHHID(){
        return " GOBHHID ASC";
    }
    private String filterStringForENCCRV1(){
        return "enccrv_1";
    }
    private String filterStringForENCCRV2(){
        return "enccrv_2";
    }
    private String filterStringForENCCRV3(){
        return "enccrv_3";
    }

    /**
     * Override filter to capture fts filter by location
     * @param filter
     */
    @Override
    public void onFilterSelection(FilterOption filter) {
        appliedVillageFilterView.setText(filter.name());
        filters = ((CursorFilterOption)filter).filter();
        mainCondition = " FWBNFGEN is not null AND details NOT LIKE '%\"user_type\":\"FWA\"%' ";

        if(StringUtils.isNotBlank(filters) && filters.contains(" and mcaremother.details like ")){
            String searchString = filters.replace(" and mcaremother.details like ", "");
            mainCondition += " AND "+ CommonFtsObject.relationalIdColumn +" IN (SELECT "+CommonRepository.ID_COLUMN+ " FROM " + "mcaremother"+ " WHERE details LIKE " + searchString+ " ) ";
            filters = "";
        }
        CountExecute();
        filterandSortExecute();
    }

}

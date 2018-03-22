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
import org.ei.opensrp.mcare.anc.ANCRiskSort;
import org.ei.opensrp.mcare.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.mcare.elco.ElcoMauzaCommonObjectFilterOption;
import org.ei.opensrp.mcare.elco.ElcoPSRFDueDateSort;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.mcare.pnc.mCarePNCServiceModeOption;
import org.ei.opensrp.mcare.pnc.mCarePNCSmartClientsProvider;
import org.ei.opensrp.mcare.pnc.mCarePNCSmartRegisterActivity;
import org.ei.opensrp.mcare.pnc.mCarePncDetailActivity;
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
public class mCarePNCSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

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
                return new mCarePNCServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new ANCRiskSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.mcare_PNC_register_title_in_short);
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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_pncrv1),filterStringForPNCRV1withoutfts()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_pncrv2),filterStringForPNCRV2withoutfts()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_pncrv3),filterStringForPNCRV3withoutfts()));

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
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.due_status),sortByAlertmethodWithoutFTS()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.elco_alphabetical_sort),sortByFWWOMFNAME()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.hh_fwGobhhid_sort),sortByGOBHHID()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.hh_fwJivhhid_sort),sortByJiVitAHHID()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.pnc_date_of_outcome),sortByDateOfOutcome()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.pnc_outcome),sortByOutcomeStatis())

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
    }

    private DialogOption[] getEditOptions() {
        return ((mCareANCSmartRegisterActivity)getActivity()).getEditOptions();
    }
    private DialogOption[] getEditOptionsforanc(String pncvisittext,String pncvisitstatus) {
        return ((mCarePNCSmartRegisterActivity)getActivity()).getEditOptionsforpnc(pncvisittext, pncvisitstatus);
    }



    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    mCarePncDetailActivity.ancclient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),mCarePncDetailActivity.class);
                    startActivity(intent);
                    break;
                case R.id.pnc_reminder_due_date:
                    CustomFontTextView pncreminderDueDate = (CustomFontTextView)view.findViewById(R.id.pnc_reminder_due_date);
                    Log.v("do as you will", (String) view.getTag(R.id.textforPncRegister));
                    showFragmentDialog(new EditDialogOptionModelForPNC((String)view.getTag(R.id.textforPncRegister),(String)view.getTag(R.id.AlertStatustextforPncRegister)), view.getTag(R.id.clientobject));
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }
    private class EditDialogOptionModelfornbnf implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
    private class EditDialogOptionModelForPNC implements DialogOptionModel {
        String pncvisittext ;;
        String pncvisitstatus;
        public EditDialogOptionModelForPNC(String text,String status) {
            pncvisittext = text;
            pncvisitstatus = status;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptionsforanc(pncvisittext,pncvisitstatus);
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

                if(cs.toString().equalsIgnoreCase("")){
                    filters = "";
                }else {
                    filters = "and (FWWOMFNAME Like '%" + cs.toString() + "%' or GOBHHID Like '%" + cs.toString() + "%'  or JiVitAHHID Like '%" + cs.toString() + "%' )";
//                    filters = cs.toString();
                }
                joinTable = "";
                mainCondition = " Is_PNC = '1'  and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%'";

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
                dialogOptionslist.add(new ElcoMauzaCommonObjectFilterOption(name,"location_name",name));

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

    public String pncMainSelectWithJoins(){
        return "Select id as _id,relationalid,details,FWWOMFNAME,FWPSRLMP,FWSORTVALUE,JiVitAHHID,GOBHHID,Is_PNC,FWBNFSTS,FWBNFDTOO \n" +
                "from mcaremother\n";
    }
    public String pncMainCountWithJoins(){
        return "Select Count(*) \n" +
                "from mcaremother\n";
    }
    public void initializeQueries(){
        mCarePNCSmartClientsProvider hhscp = new mCarePNCSmartClientsProvider(getActivity(),
                clientActionHandler,context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, new CommonRepository("mcaremother",new String []{"FWWOMFNAME","FWPSRLMP","FWSORTVALUE","JiVitAHHID","GOBHHID","Is_PNC","FWBNFSTS","FWBNFDTOO"}));
        clientsView.setAdapter(clientAdapter);

        setTablename("mcaremother");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder(pncMainCountWithJoinsWithoutFTS());
        countSelect = countqueryBUilder.mainCondition(" mcaremother.Is_PNC = '1'  and mcaremother.FWWOMFNAME not null and mcaremother.FWWOMFNAME != \"\"   AND mcaremother.details  LIKE '%\"FWWOMVALID\":\"1\"%'");
        mainCondition = " Is_PNC = '1'  and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%'";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder(pncMainSelectWithJoinsWithoutFTS());
        mainSelect = queryBUilder.mainCondition(" mcaremother.Is_PNC = '1'  and mcaremother.FWWOMFNAME not null and mcaremother.FWWOMFNAME != \"\"   AND mcaremother.details  LIKE '%\"FWWOMVALID\":\"1\"%'");
        Sortqueries = sortByAlertmethodWithoutFTSandSortValue();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        updateSearchView();
        refresh();

    }
    private String sortByAlertmethod() {
        return " CASE WHEN Post_Natal_Care_Reminder_Visit = 'urgent' THEN '1'\n" +
                "WHEN Post_Natal_Care_Reminder_Visit = 'upcoming' THEN '2'\n" +
                "WHEN Post_Natal_Care_Reminder_Visit = 'normal' THEN '3'\n" +
                "WHEN Post_Natal_Care_Reminder_Visit = 'expired' THEN '4'\n" +
                "WHEN Post_Natal_Care_Reminder_Visit is Null THEN '5'\n" +
                "WHEN Post_Natal_Care_Reminder_Visit = 'complete' THEN '6'\n" +
                "Else Post_Natal_Care_Reminder_Visit END ASC";
    }
    private String sortBySortValue(){
        return " ABS(FWSORTVALUE) DESC";
    }
    private String sortByFWWOMFNAME(){
        return " FWWOMFNAME ASC";
    }
    private String sortByJiVitAHHID(){  return " JiVitAHHID ASC";
    }
    private String sortByGOBHHID(){
        return " GOBHHID ASC";
    }
    private String sortByDateOfOutcome(){
        return " FWBNFDTOO ASC";
    }

    private String sortByOutcomeStatis() {
        return " CASE WHEN FWBNFSTS = '3' THEN '1'"
                +
                "WHEN FWBNFSTS = '4' THEN '2'\n" +
                "Else FWBNFSTS END ASC";
    }
    private String filterStringForPNCRV1(){
        return "pncrv_1";
    }
    private String filterStringForPNCRV2(){
        return "pncrv_2";
    }
    private String filterStringForPNCRV3(){
        return "pncrv_3";
    }

    private String filterStringForPNCRV1withoutfts(){
        return "and alerts.visitCode LIKE '%pncrv_1%'";
    }
    private String filterStringForPNCRV2withoutfts(){
        return "and alerts.visitCode LIKE '%pncrv_2%'";
    }
    private String filterStringForPNCRV3withoutfts(){
        return "and alerts.visitCode LIKE '%pncrv_3%'";
    }

    private String sortByAlertmethodWithoutFTS() {
        return " CASE WHEN alerts.status = 'urgent' THEN '1'"
                +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "WHEN alerts.status = 'complete' THEN '6'\n" +
                "Else alerts.status END ASC";
    }
    private String sortByAlertmethodWithoutFTSandSortValue() {
        return " CASE WHEN alerts.status = 'urgent' THEN '1'"
                +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "WHEN alerts.status = 'complete' THEN '6'\n" +
                "Else alerts.status END ASC ,ABS(FWSORTVALUE) DESC";
    }
    public String pncMainSelectWithJoinsWithoutFTS(){
        return "Select id as _id,relationalid,details,FWWOMFNAME,FWPSRLMP,FWSORTVALUE,JiVitAHHID,GOBHHID,Is_PNC,FWBNFSTS,FWBNFDTOO \n" +
                "from mcaremother\n" +
                "Left Join alerts on alerts.caseID = mcaremother.id and alerts.scheduleName = 'Post Natal Care Reminder Visit' ";
    }
    public String pncMainCountWithJoinsWithoutFTS(){
        return "Select Count(*) \n" +
                "from mcaremother\n";
     }


    /**
     * Override filter to capture fts filter by location
     * @param filter
     */
//    @Override
//    public void onFilterSelection(FilterOption filter) {
//        appliedVillageFilterView.setText(filter.name());
//        filters = ((CursorFilterOption)filter).filter();
//        mainCondition = " Is_PNC = '1'  and FWWOMFNAME not null and FWWOMFNAME != \"\"   AND details  LIKE '%\"FWWOMVALID\":\"1\"%'";
//
//        if(StringUtils.isNotBlank(filters) && filters.contains(" and details LIKE ")){
//            mainCondition += filters;
//            filters = "";
//        }
//        CountExecute();
//        filterandSortExecute();
//    }

}

package org.ei.opensrp.dghs.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.dghs.HH_child.ChildDetailActivity;
import org.ei.opensrp.dghs.HH_child.ChildServiceModeOption;
import org.ei.opensrp.dghs.HH_child.HH_ChildSmartClientsProvider;
import org.ei.opensrp.dghs.HH_child.HH_child_member_SmartRegisterActivity;
import org.ei.opensrp.dghs.HH_child.child_followup_handler;
import org.ei.opensrp.dghs.HH_woman.HH_woman_member_SmartClientsProvider;
import org.ei.opensrp.dghs.HH_woman.HH_woman_member_SmartRegisterActivity;
import org.ei.opensrp.dghs.HH_woman.WomanServiceModeOption;
import org.ei.opensrp.dghs.HH_woman.tt1handler;
import org.ei.opensrp.dghs.LoginActivity;
import org.ei.opensrp.dghs.R;
import org.ei.opensrp.dghs.hh_member.HHWardCommonObjectFilterOption;
import org.ei.opensrp.dghs.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.dghs.hh_member.HouseholdCensusDueDateSort;
import org.ei.opensrp.domain.form.FieldOverrides;
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
import org.json.JSONException;
import org.json.JSONObject;
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
public class HH_child_member_SmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

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
                return new ChildServiceModeOption(clientsProvider());
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
                return getResources().getString(R.string.child_register_title_in_short);
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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_male),filterStringForMale()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_female),filterStringForFemale()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward1),filterStringForWard("Ward-1")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward2),filterStringForWard("Ward-2")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.ward3),filterStringForWard("Ward-3")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block1ka),filterStringForBlock("1-KA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block1kha),filterStringForBlock("1-KHA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block2ka),filterStringForBlock("2-KA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block2kha),filterStringForBlock("2-KHA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block3ka),filterStringForBlock("3-KA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block3kha),filterStringForBlock("3-KHA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block4ka),filterStringForBlock("4-KA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.block4kha),filterStringForBlock("4-KHA")));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.todayssession),filterStringForTodaySession()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.single_vaccine_miss),filterStringForSingleVaccineMiss()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.double_vaccine_miss),filterStringForDoubleVaccineMiss()));
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.triple_vaccine_miss),filterStringForMoreThanVaccineMiss()));

//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_anc2),filterStringForANCRV2()));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_anc3),filterStringForANCRV3()));
//                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_anc4),filterStringForANCRV4()));


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
//                        new CursorCommonObjectSort(getString(R.string.due_status),sortByAlertmethod()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.child_alphabetical_sort),sortByFWWOMFNAME()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.sort_by_mother_name),sortByChild_mother_name()),
                        new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.sort_by_child_age),sortByage()),

//                        new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.sort_by_child_birth),sortByage())
//                        new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.sortbyLmp),sortByLmp())

//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getString(R.string.search_register);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {

        return null;
    }


    @Override
    protected void onInitialization() {

//        context.formSubmissionRouter().getHandlerMap().put("psrf_form",new PSRFHandler());
    }

    @Override
    protected void startRegistration() {
        ((HH_woman_member_SmartRegisterActivity)getActivity()).startRegistration();
    }

    @Override
    protected void onCreation() {
    }
    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        initializeQueries();
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
//        list.setBackgroundColor(Color.RED);
        initializeQueries();
    }

    private DialogOption[] getEditOptions() {
        return ((HH_woman_member_SmartRegisterActivity)getActivity()).getEditOptions();
    }
//    private DialogOption[] getEditOptionsforanc(String ancvisittext,String ancvisitstatus) {
//        return ((HH_woman_member_SmartRegisterActivity)getActivity()).getEditOptionsforanc(ancvisittext,ancvisitstatus);
//    }



    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    ChildDetailActivity.childclient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),ChildDetailActivity.class);
                    startActivity(intent);
                    break;
                case R.id.next_vaccine:
                    CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
                    JSONObject overridejsonobject = new JSONObject();
                    try {
                        overridejsonobject.put("existing_Is_Reg_Today",((pc.getDetails().get("Is_Reg_Today")!=null?pc.getDetails().get("Is_Reg_Today"):"")));

                        overridejsonobject.put("e_bcg",((pc.getDetails().get("final_bcg")!=null?pc.getDetails().get("final_bcg"):"")));
                        overridejsonobject.put("e_opv0",((pc.getDetails().get("final_opv0")!=null?pc.getDetails().get("final_opv0"):"")));
                        overridejsonobject.put("e_penta2",((pc.getDetails().get("final_penta2")!=null?pc.getDetails().get("final_penta2"):"")));
                        overridejsonobject.put("e_penta1",((pc.getDetails().get("final_penta1")!=null?pc.getDetails().get("final_penta1"):"")));
                        overridejsonobject.put("e_penta3",((pc.getDetails().get("final_penta3")!=null?pc.getDetails().get("final_penta3"):"")));
                        overridejsonobject.put("e_opv1",((pc.getDetails().get("final_opv1")!=null?pc.getDetails().get("final_opv1"):"")));
                        overridejsonobject.put("e_opv2",((pc.getDetails().get("final_opv2")!=null?pc.getDetails().get("final_opv2"):"")));
                        overridejsonobject.put("e_opv3",((pc.getDetails().get("final_opv3")!=null?pc.getDetails().get("final_opv3"):"")));
                        overridejsonobject.put("e_pcv1",((pc.getDetails().get("final_pcv1")!=null?pc.getDetails().get("final_pcv1"):"")));
                        overridejsonobject.put("e_pcv2",((pc.getDetails().get("final_pcv2")!=null?pc.getDetails().get("final_pcv2"):"")));
                        overridejsonobject.put("e_pcv3",((pc.getDetails().get("final_pcv3")!=null?pc.getDetails().get("final_pcv3"):"")));
                        overridejsonobject.put("e_ipv",((pc.getDetails().get("final_ipv")!=null?pc.getDetails().get("final_ipv"):"")));
                        overridejsonobject.put("e_measles1",((pc.getDetails().get("final_measles1")!=null?pc.getDetails().get("final_measles1"):"")));
                        overridejsonobject.put("e_measles2",((pc.getDetails().get("final_measles2")!=null?pc.getDetails().get("final_measles2"):"")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());
                    context().formSubmissionRouter().getHandlerMap().put("child_vaccine_followup",new child_followup_handler());

                    ((HH_child_member_SmartRegisterActivity)getActivity()).startFormActivity("child_vaccine_followup", ((CommonPersonObjectClient) view.getTag()).entityId(), fieldOverrides.getJSONString());
                    break;
//                case R.id.anc_reminder_due_date:
//                    CustomFontTextView ancreminderDueDate = (CustomFontTextView)view.findViewById(R.id.anc_reminder_due_date);
//                    Log.v("do as you will", (String) view.getTag(R.id.textforAncRegister));
//                    showFragmentDialog(new EditDialogOptionModelForANC((String)view.getTag(R.id.textforAncRegister),(String)view.getTag(R.id.AlertStatustextforAncRegister)), view.getTag(R.id.clientobject));
//                    break;
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
                        if(cs.toString().equalsIgnoreCase("")){
                            filters = "";
                        }else {
                            filters = "and (Member_Fname Like '%" + cs.toString() + "%' or details Like '%" + cs.toString() + "%')   ";
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
    }
    public void addChildToList(ArrayList<DialogOption> dialogOptionslist,Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist,entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new HHWardCommonObjectFilterOption(name,"location_name", name));

            }
        }
    }
    class ancControllerfiltermap extends ControllerFilterMap{

        @Override
        public boolean filtermapLogic(CommonPersonObject commonPersonObject) {
            boolean returnvalue = false;
            if(commonPersonObject.getDetails().get("FWWOMVALID") != null){
                if(commonPersonObject.getDetails().get("FWWOMVALID").equalsIgnoreCase("1")){
                    returnvalue = true;
                    if(commonPersonObject.getDetails().get("Is_PNC")!=null){
                        if(commonPersonObject.getDetails().get("Is_PNC").equalsIgnoreCase("1")){
                            returnvalue = false;
                        }

                    }
                }
            }
            Log.v("the filter", "" + returnvalue);
            return returnvalue;
        }
    }

    public void initializeQueries(){
        CommonRepository commonRepository = context().commonrepository("members");
        setTablename("members");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("members");
        countqueryBUilder.joinwithALerts("members", "FW CENSUS");
        countSelect = countqueryBUilder.mainCondition(" details like '%\"Is_child\":\"1\"%' ");
        Sortqueries = sortByAlertmethod();

        CountExecute();


        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("members", new String[]{"relationalid", "details", "Member_Fname", "EDD", "calc_age_confirm","Child_mother_name", "Member_GOB_HHID", "Marital_status", "Pregnancy_Status","missedCount"});
        queryBUilder.joinwithALerts("members", "FW CENSUS");
        mainSelect = queryBUilder.mainCondition(" details like '%\"Is_child\":\"1\"%' ");
        queryBUilder.addCondition(filters);
        Sortqueries = sortByAlertmethod();
        currentquery  = queryBUilder.orderbyCondition(Sortqueries);
        Cursor c = commonRepository.RawCustomQueryForAdapter(queryBUilder.Endquery(queryBUilder.addlimitandOffset(currentquery, 20, 0)));
        HH_ChildSmartClientsProvider hhscp = new HH_ChildSmartClientsProvider(getActivity(),clientActionHandler,context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), c, hhscp, new CommonRepository("members",new String []{"Member_Fname","EDD","calc_age_confirm","Child_mother_name","Member_GOB_HHID","Marital_status","Pregnancy_Status","missedCount"}));
        clientsView.setAdapter(clientAdapter);
        updateSearchView();
        refresh();

    }
    private String sortByage(){
        return " Child_calc_age ASC";
    }
    private String sortByFWWOMFNAME(){
        return " Member_Fname COLLATE NOCASE ASC";
    }
    private String sortByChild_mother_name(){
        return " Child_mother_name COLLATE NOCASE ASC";
    }

    private String sortByGOBHHID(){
        return " CAST(Member_GOB_HHID AS INTEGER) ASC";
    }

    private String sortByMissedCount(){
        return " missedCount ASC";
    }
    private String filterStringForSingleVaccineMiss(){
        return "and missedCount = '1'";
    }
    private String filterStringForDoubleVaccineMiss(){
        return "and missedCount = '2'";
    }
    private String filterStringForMoreThanVaccineMiss(){
        return "and missedCount IS NOT NULL and missedCount NOT IN ('0','1','2')";
    }

    private String filterStringForFemale(){
        return "and details LIKE '%\"Child_gender\":\"2\"%'";
    }
    private String filterStringForMale(){
        return " and details LIKE '%\"Child_gender\":\"1\"%'";
    }
    private String filterStringForWard(String ward){
        return filters +"and  details like '%\"Member_WARD\":\""+ward+"\"%' ";
    }
    private String filterStringForBlock(String block){
        return filters +"and  details like '%\"Member_BLOCK\":\""+block+"\"%' ";
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
    private String filterStringForTodaySession() {
        Sortqueries = sortByMissedCount();
        String todays_vaccine_list = context().applicationContext().getSharedPreferences("vaccine", android.content.Context.MODE_PRIVATE).getString("todays_vaccine_list","");

        return "and members.id IN ("+todays_vaccine_list+")";
    }
}

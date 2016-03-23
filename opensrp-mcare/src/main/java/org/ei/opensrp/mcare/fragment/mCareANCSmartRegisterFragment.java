package org.ei.opensrp.mcare.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.anc.mCareANCServiceModeOption;
import org.ei.opensrp.mcare.anc.mCareANCSmartClientsProvider;
import org.ei.opensrp.mcare.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.mcare.anc.mCareAncDetailActivity;
import org.ei.opensrp.mcare.elco.ElcoMauzaCommonObjectFilterOption;
import org.ei.opensrp.mcare.elco.ElcoPSRFDueDateSort;
import org.ei.opensrp.mcare.elco.ElcoSearchOption;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
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
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Map;

import util.AsyncTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 11/2/15.
 */
public class mCareANCSmartRegisterFragment extends SecuredNativeSmartRegisterFragment {

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
                return new mCareANCServiceModeOption(clientsProvider());
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
                return getResources().getString(R.string.mcare_ANC_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();
                dialogOptionslist.add(new AllClientsFilter());
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
//                        new ElcoPSRFDueDateSort(),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,false,"FWWOMFNAME", Context.getInstance().applicationContext().getString(R.string.elco_alphabetical_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"GOBHHID", Context.getInstance().applicationContext().getString(R.string.hh_fwGobhhid_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"JiVitAHHID", Context.getInstance().applicationContext().getString(R.string.hh_fwJivhhid_sort))

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
            clientProvider = new mCareANCSmartClientsProvider(
                    getActivity(),clientActionHandler , controller);
        }
        return clientProvider;
    }


    @Override
    protected void onInitialization() {
        ArrayList <ControllerFilterMap> controllerFilterMapArrayList = new ArrayList<ControllerFilterMap>();

        ancControllerfiltermap filtermap = new ancControllerfiltermap();

        controllerFilterMapArrayList.add(filtermap);
//        controllerFilterMapArrayList.add(filterforpnc);
        controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("mcaremother"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(),"FWWOMFNAME","mcaremother",controllerFilterMapArrayList, CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails,"FWWOMFNAME", CommonPersonObjectController.ByColumnAndByDetails.byDetails);
//                context.personObjectClientsCache(),"FWWOMFNAME","elco","FWELIGIBLE","1", CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails,"FWWOMFNAME", CommonPersonObjectController.ByColumnAndByDetails.byDetails);

        villageController = new VillageController(context.allEligibleCouples(),
                context.listCache(), context.villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
//        context.formSubmissionRouter().getHandlerMap().put("psrf_form",new PSRFHandler());
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

        ImageButton startregister = (ImageButton)view.findViewById(org.ei.opensrp.R.id.register_client);
        startregister.setVisibility(View.GONE);
        setServiceModeViewDrawableRight(null);
        updateSearchView();
    }

    private DialogOption[] getEditOptions() {
        return ((mCareANCSmartRegisterActivity)getActivity()).getEditOptions();
    }
    private DialogOption[] getEditOptionsforanc(String ancvisittext,String ancvisitstatus) {
        return ((mCareANCSmartRegisterActivity)getActivity()).getEditOptionsforanc(ancvisittext,ancvisitstatus);
    }



    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    mCareAncDetailActivity.ancclient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),mCareAncDetailActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nbnf_due_date:
                    showFragmentDialog(new EditDialogOptionModelfornbnf(), view.getTag(R.id.clientobject));
                    break;
                case R.id.anc_reminder_due_date:
                    CustomFontTextView ancreminderDueDate = (CustomFontTextView)view.findViewById(R.id.anc_reminder_due_date);
                    Log.v("do as you will", (String) view.getTag(R.id.textforAncRegister));
                    showFragmentDialog(new EditDialogOptionModelForANC((String)view.getTag(R.id.textforAncRegister),(String)view.getTag(R.id.AlertStatustextforAncRegister)), view.getTag(R.id.clientobject));
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
    private class EditDialogOptionModelForANC implements DialogOptionModel {
        String ancvisittext ;;
        String Ancvisitstatus;
        public EditDialogOptionModelForANC(String text,String status) {
            ancvisittext = text;
            Ancvisitstatus = status;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptionsforanc(ancvisittext,Ancvisitstatus);
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
                        setCurrentSearchFilter(new ElcoSearchOption(cs.toString()));
                        filteredClients = getClientsAdapter().getListItemProvider()
                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                                        getCurrentSearchFilter(), getCurrentSortOption());


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
                        getClientsAdapter().refreshClients(filteredClients);
                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
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
                dialogOptionslist.add(new ElcoMauzaCommonObjectFilterOption(name,"location_name", ElcoMauzaCommonObjectFilterOption.ByColumnAndByDetails.byDetails,name));

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
            Log.v("the filter",""+returnvalue);
            return returnvalue;
        }
    }

}

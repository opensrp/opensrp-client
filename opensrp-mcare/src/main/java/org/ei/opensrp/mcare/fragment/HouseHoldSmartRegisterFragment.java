package org.ei.opensrp.mcare.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectClients;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.household.CensusEnrollmentHandler;
import org.ei.opensrp.mcare.household.HHMWRAEXISTFilterOption;
import org.ei.opensrp.mcare.household.HHMauzaCommonObjectFilterOption;
import org.ei.opensrp.mcare.household.HHSearchOption;
import org.ei.opensrp.mcare.household.HouseHoldDetailActivity;
import org.ei.opensrp.mcare.household.HouseHoldServiceModeOption;
import org.ei.opensrp.mcare.household.HouseHoldSmartClientsProvider;
import org.ei.opensrp.mcare.household.HouseHoldSmartRegisterActivity;
import org.ei.opensrp.mcare.household.HouseholdCensusDueDateSort;
import org.ei.opensrp.mcare.household.NOHHMWRAEXISTFilterOption;
import org.ei.opensrp.mcare.household.noNIDFilter;
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
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.AsyncTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 10/12/15.
 */
public class HouseHoldSmartRegisterFragment extends SecuredNativeSmartRegisterFragment {

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

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

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

                dialogOptionslist.add(new AllClientsFilter());
                dialogOptionslist.add( new NOHHMWRAEXISTFilterOption("0","ELCO", NOHHMWRAEXISTFilterOption.ByColumnAndByDetails.byDetails));
                dialogOptionslist.add(new HHMWRAEXISTFilterOption("0","ELCO", HHMWRAEXISTFilterOption.ByColumnAndByDetails.byDetails));

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

                        new HouseholdCensusDueDateSort(),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,false,"FWHOHFNAME",getResources().getString(R.string.hh_alphabetical_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"FWGOBHHID",getResources().getString(R.string.hh_fwGobhhid_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"FWJIVHHID",getResources().getString(R.string.hh_fwJivhhid_sort))
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
        if (clientProvider == null) {
            clientProvider = new HouseHoldSmartClientsProvider(
                    getActivity(),clientActionHandler , controller,context.alertService());
        }
        return clientProvider;
    }

    private DialogOption[] getEditOptions() {
        return ((HouseHoldSmartRegisterActivity)getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
        controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("household"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(),"FWHOHFNAME","household","FWGOBHHID", CommonPersonObjectController.ByColumnAndByDetails.byDetails,new HouseholdCensusDueDateSort());
        villageController = new VillageController(context.allEligibleCouples(),
                context.listCache(), context.villagesCache());
        dialogOptionMapper = new DialogOptionMapper();
        context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form",new CensusEnrollmentHandler());
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);

        setServiceModeViewDrawableRight(null);
        updateSearchView();
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
                .newInstance((HouseHoldSmartRegisterActivity) getActivity(), new EditDialogOptionModel(), context.anmLocationController().get(), "new_household_registration")
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
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();
        checkforNidMissing(mView);

        try{
            LoginActivity.setLanguage();
        }catch (Exception e){

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
                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
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
                dialogOptionslist.add(new HHMauzaCommonObjectFilterOption(name,"existing_Mauzapara", HHMauzaCommonObjectFilterOption.ByColumnAndByDetails.byDetails,name));

            }
        }
    }

    private void checkforNidMissing(View view) {
        LinearLayout titlelayout = (LinearLayout)view.findViewById(org.ei.opensrp.R.id.title_layout);
        if(anyNIdmissing(controller)) {
            try {
                titlelayout.removeView(getActivity().findViewById(900)) ;

            }catch(Exception e){

            }
            ImageButton warn = new ImageButton(getActivity());
            warn.setImageDrawable(getResources().getDrawable(R.mipmap.warning));
            warn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            warn.setBackground(null);
            warn.setId(900);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;

//        warn.setGravity(Gravity.CENTER);
//        warn.setB
            titlelayout.addView(warn, layoutParams);
            warn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getClientsAdapter()
                            .refreshList(new noNIDFilter(), getCurrentServiceModeOption(),
                                    getCurrentSearchFilter(), getCurrentSortOption());
                }
            });
        }else{
            titlelayout.removeView(getActivity().findViewById(900));
        }
    }

    private boolean anyNIdmissing(CommonPersonObjectController controller) {
        boolean toreturn = false;
        List<CommonPersonObject> allchildelco = null;
        CommonPersonObjectClients clients = controller.getClients();
        ArrayList<String> list = new ArrayList<String>();
        AllCommonsRepository allElcoRepository = Context.getInstance().allCommonsRepositoryobjects("elco");

        for(int i = 0;i <clients.size();i++) {

            list.add((clients.get(i).entityId()));

        }
        allchildelco = allElcoRepository.findByRelationalIDs(list);

        if(allchildelco != null) {
            for (int i = 0; i < allchildelco.size(); i++) {
                if (allchildelco.get(i).getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {
                    if (allchildelco.get(i).getDetails().get("nidImage") == null) {
                        toreturn = true;
                    }
                }
            }
        }
        return toreturn;
//        return false;
    }
}

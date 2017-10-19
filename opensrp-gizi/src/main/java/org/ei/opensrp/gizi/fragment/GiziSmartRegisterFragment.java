package org.ei.opensrp.gizi.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.gizi.LoginActivity;
import org.ei.opensrp.gizi.face.camera.SmartShutterActivity;
import org.ei.opensrp.gizi.gizi.GiziDetailActivity;
import org.ei.opensrp.gizi.gizi.GiziGrowthChartActivity;
import org.ei.opensrp.gizi.gizi.GiziServiceModeOption;
import org.ei.opensrp.gizi.gizi.GiziSmartClientsProvider;
import org.ei.opensrp.gizi.gizi.GiziSmartRegisterActivity;
import org.ei.opensrp.gizi.gizi.KICommonObjectFilterOption;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.gizi.R;

import org.ei.opensrp.sync.ClientProcessor;
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
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Map;

import util.AsyncTask;
import util.formula.Support;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 10/12/15.
 */
public class GiziSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private static final String TAG = GiziSmartRegisterFragment.class.getSimpleName();

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
    private ClientProcessor clientProcessor;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private String locationDialogTAG = "locationDialogTAG";

    public static String criteria;

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
                return new GiziServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.gizi);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                FlurryAgent.logEvent("click_filter_option_on_kohort_ibu_dashboard");
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label),filterStringForAll()));
                //     dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_no_mwra),filterStringForNoElco()));
                //      dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.hh_has_mwra),filterStringForOneOrMoreElco()));

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
               // FlurryFacade.logEvent("click_sorting_option_on_kohort_ibu_dashboard");
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),


                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label),KiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse),KiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_age),KiSortByAgeASC()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_age_reverse),KiSortByAgeDESC()),
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
//                    getActivity(),clientActionHandler , context().alertService());
//        }
        return null;
    }

    private DialogOption[] getEditOptions() {
        return ((GiziSmartRegisterActivity)getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
         //  context().formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new CensusEnrollmentHandler());
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
        initializeQueries(getCriteria());
    }

    private String filterStringForAll(){
        return "";
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
    public void initializeQueries(String s){
        GiziSmartClientsProvider kiscp = new GiziSmartClientsProvider(getActivity(),clientActionHandler,context().alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp, new CommonRepository("ec_anak",new String []{"tanggalLahirAnak","namaBayi"}));
        clientsView.setAdapter(clientAdapter);

        setTablename("ec_anak");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("ec_anak");

        if (s == null || s.equals("!") || s.equals("")) {
            Log.e(TAG, "initializeQueries: "+"Not Initialized" );
            mainCondition = "is_closed = 0 ";
        } else {
            Log.e(TAG, "initializeQueries: " + s);
            mainCondition = "is_closed = 0 AND object_id LIKE '%" + s + "%'";
        }

        countSelect = countqueryBUilder.mainCondition("is_closed = 0 ");
        //  mainCondition = " isClosed !='true' ";
        super.CountExecute();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("ec_anak", new String[]{"ec_anak.relationalid","ec_anak.is_closed","ec_anak.details","tanggalLahirAnak","namaBayi"});
        mainSelect = queryBUilder.mainCondition("is_closed = 0 ");
        //   Sortqueries = KiSortByNameAZ();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

//        setServiceModeViewDrawableRight(null);
        updateSearchView();
        refresh();


    }


    @Override
    public void startRegistration() {
        if(Support.ONSYNC) {
            Toast.makeText(getActivity(), "Data still Synchronizing, please wait", Toast.LENGTH_SHORT).show();
            return;
        }

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }

        String uniqueIdJson = LoginActivity.generator.uniqueIdController().getUniqueIdJson();
        if(uniqueIdJson == null || uniqueIdJson.isEmpty()){
            Toast.makeText(getActivity(),"No unique id",Toast.LENGTH_LONG).show();
            return;
        }

        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((GiziSmartRegisterActivity) getActivity(), new EditDialogOptionModel(), context().anmLocationController().get(), "registrasi_gizi")
                .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    CharSequence selections[] = new CharSequence[] {"Detail View", "Charts"};
                    GiziDetailActivity.childclient = (CommonPersonObjectClient) view.getTag();
                    GiziGrowthChartActivity.client = (CommonPersonObjectClient)view.getTag();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("");
                    builder.setItems(selections, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                            if(which == 0)
                            {
                                Intent intent = new Intent(getActivity(),GiziDetailActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else if(which == 1){
                                Intent intent = new Intent(getActivity(),GiziGrowthChartActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }
                    });
                    builder.show();
                  //  FlurryFacade.logEvent("click_detail_picture_vaksinator");

                    break;
                //untuk follow up button
                case R.id.btn_edit:
                  //  FlurryFacade.logEvent("click_button_edit_vaksinator");
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }



    private String KiSortByNameAZ() {
        return " namaBayi ASC";
    }
    private String KiSortByNameZA() {
        return " namaBayi DESC";
    }
    private String KiSortByAgeASC() {
        return " tanggalLahirAnak DESC";
    }
    private String KiSortByAgeDESC() {
        return " tanggalLahirAnak ASC";
    }

    private String KiSortByAge() {
        return " umur DESC";
    }
    private String KiSortByNoIbu() {
        return " noIbu ASC";
    }

    private String KiSortByEdd() {
        return " htp IS NULL, htp";
    }


    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }
        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {


            /*if(option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form)) ) {
                CommonPersonObjectClient pc = KIDetailActivity.kiclient;
                if(pc.getDetails().get("ibu.type")!= null) {
                    if (pc.getDetails().get("ibu.type").equals("anc") || pc.getDetails().get("ibu.type").equals("pnc")) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }*/
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    @Override
    protected void onResumption() {
//        super.onResumption();
        getDefaultOptionsProvider();
        if(isPausedOrRefreshList()) {
            initializeQueries("!");
        }
        //     updateSearchView();
//
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
//                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
//                        filteredClients = getClientsAdapter().getListItemProvider()
//                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
//                                        getCurrentSearchFilter(), getCurrentSortOption());
//

                        filters = cs.toString();
                        joinTable = "";
                        mainCondition = " is_closed = 0 AND namaBayi !='' ";
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
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
                dialogOptionslist.add(new KICommonObjectFilterOption(name,"desa", name));

            }
        }
    }

    //    WD
    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public static String getCriteria() {
        return criteria;
    }

    @Override
    public void setupSearchView(final View view) {
        searchView = (EditText) view.findViewById(org.ei.opensrp.R.id.edt_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SmartShutterActivity.isDevCompat) {
                    CharSequence selections[] = new CharSequence[]{"Name", "Photo"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please Choose one, Search by");
                    builder.setItems(selections, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int opt) {
                            if (opt == 0) searchTextChangeListener("");
                            else getFacialRecord(view);
                        }
                    });
                    builder.show();
                } else {
                    searchTextChangeListener("");
                }
            }
        });

        searchCancelView = view.findViewById(org.ei.opensrp.R.id.btn_search_cancel);
        searchCancelView.setOnClickListener(searchCancelHandler);
    }

    public void getFacialRecord(View view) {
        FlurryAgent.logEvent(TAG+" search_by_face", true);
        Log.e(TAG, "getFacialRecord: start ");
        SmartShutterActivity.kidetail = (CommonPersonObjectClient) view.getTag();
        FlurryAgent.logEvent(TAG + " search_by_face", true);

        Intent intent = new Intent(getActivity(), SmartShutterActivity.class);
        intent.putExtra("org.sid.sidface.ImageConfirmation.origin", GiziSmartRegisterFragment.class.getSimpleName());
        intent.putExtra("org.sid.sidface.ImageConfirmation.identify", true);
        intent.putExtra("org.sid.sidface.ImageConfirmation.kidetail", (Parcelable) SmartShutterActivity.kidetail);
        startActivityForResult(intent, 2);


    }

    public void searchTextChangeListener(String s) {
        Log.e(TAG, "searchTextChangeListener: " + s);
        if (s != null) {
            filters = s;
        } else {
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                    (new AsyncTask() {
//                    SmartRegisterClients filteredClients;

                        @Override
                        protected Object doInBackground(Object[] params) {
//                        currentSearchFilter =
//                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
//                        filteredClients = getClientsAdapter().getListItemProvider()
//                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
//                                        getCurrentSearchFilter(), getCurrentSortOption());
//

                            filters = cs.toString();
                            joinTable = "";
                            mainCondition = "nama_bayi !=''";
                            Log.e(TAG, "doInBackground: " + filters);
                            return null;
                        }
//
//                    @Override
//                    protected void onPostExecute(Object o) {
////                        clientsAdapter
////                                .refreshList(currentVillageFilter, currentServiceModeOption,
////                                        currentSearchFilter, currentSortOption);
////                        getClientsAdapter().refreshClients(filteredClients);
////                        getClientsAdapter().notifyDataSetChanged();
//                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
//                        CountExecute();
//                        filterandSortExecute();
//                        super.onPostExecute(o);
//                    }
                    }).execute();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 ) {

            if (resultCode != 0) {
                Intent myIntent = new Intent(getActivity(), GiziSmartRegisterActivity.class);
                if (data != null) {
                    myIntent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
                    myIntent.putExtra("org.ei.opensrp.indonesia.face.base_id", data.getStringExtra("org.ei.opensrp.indonesia.face.base_id"));
                }
                getActivity().startActivity(myIntent);
            } else {
                Log.e(TAG, "onActivityResult: "+ resultCode );
            }
        }

    }


}

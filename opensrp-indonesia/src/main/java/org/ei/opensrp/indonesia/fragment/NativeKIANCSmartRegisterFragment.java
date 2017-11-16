package org.ei.opensrp.indonesia.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import org.ei.opensrp.indonesia.LoginActivity;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.anc.ANCDetailActivity;
import org.ei.opensrp.indonesia.anc.KIANCClientsProvider;
import org.ei.opensrp.indonesia.anc.KIANCOverviewServiceMode;
import org.ei.opensrp.indonesia.anc.NativeKIANCSmartRegisterActivity;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.kartu_ibu.KICommonObjectFilterOption;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
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
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import util.AsyncTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 10/29/15.
 */
public class NativeKIANCSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private static final String TAG = NativeKIANCSmartRegisterFragment.class.getSimpleName();

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private String locationDialogTAG = "locationDialogTAG";

    public static String criteria;

    Date date = new Date();
    SimpleDateFormat sdf;
    Map<String, String> FS = new HashMap<>();


    @Override
    protected void onCreation() {
    }

   /* @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }*/

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new KIANCOverviewServiceMode(clientsProvider());
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
                return Context.getInstance().getStringResource(R.string.anc_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                FlurryFacade.logEvent("click_filter_option_on_kohort_anc_dashboard");
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
                FlurryFacade.logEvent("click_sorting_option_on_kohort_anc_dashboard");
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label),KiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse),KiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label),KiSortByAge()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label),KiSortByEdd()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_no_ibu_label),KiSortByNoIbu()),
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
        return ((NativeKIANCSmartRegisterActivity)getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
        //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new CensusEnrollmentHandler());
    }

    @Override
    public void setupViews(View view) {
        getDefaultOptionsProvider();

        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.register_client).setVisibility(View.GONE);
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
        return " CASE WHEN alerts.status = 'urgent' THEN '1'" +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "Else alerts.status END ASC";
    }
    public String ancMainSelectWithJoins(){
        return "Select ec_ibu.id as _id, ec_ibu.namalengkap,ec_ibu.namaSuami,ec_kartu_ibu.umur,ec_ibu.is_closed,ec_ibu.ancDate,ec_ibu.ancKe  \n" +
                "from ec_ibu Left Join ec_kartu_ibu on  ec_ibu.id = ec_kartu_ibu.id ";
    }
    public String ancCounts(){
        return "Select Count(*) \n" +
                "from ec_ibu Left Join ec_kartu_ibu on  ec_ibu.id = ec_kartu_ibu.id ";
    }
    /*public void initializeQueries(){
         try {
        KIANCClientsProvider kiscp = new KIANCClientsProvider(getActivity(),clientActionHandler,context.alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp, new CommonRepository("ec_ibu",new String []{"ec_ibu.isClosed", "ec_ibu.ancDate", "ec_ibu.ancKe","ec_ibu.namalengkap","ec_ibu.namaSuami"}));

        clientsView.setAdapter(clientAdapter);

        setTablename("ec_ibu");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts("ec_ibu");
     //   countqueryBUilder.customJoin("LEFT JOIN kartu_ibu ON ibu.id = kartu_ibu.id");
        mainCondition = " isClosed !=0 and pptest = 'Positive'";
             joinTable = "";
             countSelect = countqueryBUilder.mainCondition(mainCondition);
             super.CountExecute();


        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("ec_ibu", new String[]{"ec_ibu.isClosed", "ec_ibu.details", "ec_ibu.ancDate", "ec_ibu.ancKe","ec_ibu.namalengkap","ec_ibu.namaSuami"});
         //    countqueryBUilder.customJoin("LEFT JOIN kartu_ibu ON ibu.id = kartu_ibu.id");
             mainSelect = queryBUilder.mainCondition(mainCondition);
     //   Sortqueries = KiSortByNameAZ();


             currentlimit = 20;
             currentoffset = 0;

             super.filterandSortInInitializeQueries();

             updateSearchView();
             refresh();

         } catch (Exception e){
             e.printStackTrace();
         }
         finally {
         }
    }*/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initializeQueries(String s){
        try {
            KIANCClientsProvider kiscp = new KIANCClientsProvider(getActivity(),clientActionHandler,context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp, new CommonRepository("ec_ibu",new String []{"ec_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.namaSuami"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_ibu");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
            countqueryBUilder.SelectInitiateMainTableCounts("ec_ibu");
            countqueryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_ibu.id");

            if (s == null || Objects.equals(s, "!")) {
                mainCondition = "is_closed = 0";
                Log.e(TAG, "initializeQueries: "+"Not Initialized" );
            } else {
                Log.e(TAG, "initializeQueries: " + s);
                mainCondition = "is_closed = 0 AND object_id LIKE '%" + s + "%'";
            }

//            mainCondition = " is_closed = 0 ";
            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable("ec_ibu", new String[]{"ec_ibu.relationalid","ec_ibu.is_closed", "ec_ibu.details",  "ec_kartu_ibu.namalengkap","ec_kartu_ibu.namaSuami","imagelist.imageid"});
            queryBUilder.customJoin("LEFT JOIN ec_kartu_ibu on ec_kartu_ibu.id = ec_ibu.id LEFT JOIN ImageList imagelist ON ec_ibu.id=imagelist.entityID");
            mainSelect = queryBUilder.mainCondition(" ec_kartu_ibu.is_closed = 0");
            Sortqueries = KiSortByNameAZ();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();
            CountExecute();
            updateSearchView();
            refresh();
        } catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }

    }

    @Override
    public void startRegistration() {
     //   FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
     //   Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
     //   if (prev != null) {
    //        ft.remove(prev);
    //    }
    //    ft.addToBackStack(null);
    //    BidanLocationSelectorDialogFragment
    //            .newInstance((NativeKIAnakSmartRegisterActivity) getActivity(), new EditDialogOptionModel(), context.anmLocationController().get(), KOHORT_KB_REGISTER)
   //             .show(ft, locationDialogTAG);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    FlurryFacade.logEvent("click_detail_view_on_kohort_anc_dashboard");
                    ANCDetailActivity.ancclient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),ANCDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;
                 case R.id.btn_edit:
                    FlurryFacade.logEvent("click_visit_button_on_kohort_anc_dashboard");
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

        private void showProfileView(ECClient client) {
            navigationController.startEC(client.entityId());
        }
    }

    private String KiSortByName() {
        return " namalengkap ASC";
    }
    private String KiSortByNameAZ() {
        return " namalengkap ASC";
    }
    private String KiSortByNameZA() {
        return " namalengkap DESC";
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
        //   checkforNidMissing(mView);
        updateSearchView();
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


                filters = cs.toString();
                joinTable = "";
                mainCondition = " is_closed = 0 ";

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
                dialogOptionslist.add(new KICommonObjectFilterOption(name, "location_name", name, "ec_kartu_ibu"));

            }
        }
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public static String getCriteria(){
        return criteria;
    }

    SmartShutterActivity ssa = new SmartShutterActivity();
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

        FlurryAgent.logEvent(TAG+"search_by_face", true);
        Log.e(TAG, "getFacialRecord: ");
        sdf = new SimpleDateFormat("hh:mm:ss.SS", Locale.ENGLISH);
        String face_start = sdf.format(date);
        FS.put("face_start", face_start);

        SmartShutterActivity.kidetail = (CommonPersonObjectClient) view.getTag();

        FlurryAgent.logEvent(TAG + "search_by_face", FS, true);

        Intent intent = new Intent(getActivity(), SmartShutterActivity.class);
        intent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG);
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

                    Log.e(TAG, "onTextChanged: " + searchView.getText());
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
                            mainCondition = " isClosed !='true' and type = 'anc' ";
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

        Intent myIntent = new Intent(getActivity(), NativeKIANCSmartRegisterActivity.class);
        if (data != null) {
            myIntent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
            myIntent.putExtra("org.ei.opensrp.indonesia.face.base_id", data.getStringExtra("org.ei.opensrp.indonesia.face.base_id"));
        }
        getActivity().startActivity(myIntent);

    }

}
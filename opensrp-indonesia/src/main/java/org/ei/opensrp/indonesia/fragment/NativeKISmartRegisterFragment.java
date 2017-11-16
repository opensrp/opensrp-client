package org.ei.opensrp.indonesia.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.indonesia.LoginActivity;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.kartu_ibu.AllKartuIbuServiceMode;
import org.ei.opensrp.indonesia.kartu_ibu.KIClientsProvider;
import org.ei.opensrp.indonesia.kartu_ibu.KICommonObjectFilterOption;
import org.ei.opensrp.indonesia.kartu_ibu.KIDetailActivity;
import org.ei.opensrp.indonesia.kartu_ibu.NativeKISmartRegisterActivity;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import util.AsyncTask;
import util.formula.Support;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKISmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private static final String TAG = NativeKISmartRegisterFragment.class.getSimpleName();
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    Date date = new Date();
    SimpleDateFormat sdf;
    Map<String, String> FS = new HashMap<>();

    @Override
    protected void onCreation() {
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new AllKartuIbuServiceMode(clientsProvider());
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
                return Context.getInstance().getStringResource(R.string.ki_register_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                FlurryFacade.logEvent("click_filter_option_on_kohort_ibu_dashboard");
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<>();

                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label), filterStringForAll()));

                String locationjson = context().anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

                Map<String, TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();
                addChildToList(dialogOptionslist, locationMap);
                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0; i < dialogOptionslist.size(); i++) {
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                FlurryFacade.logEvent("click_sorting_option_on_kohort_ibu_dashboard");
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label), KiSortByNameAZ()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_name_label_reverse), KiSortByNameZA()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_wife_age_label), KiSortByAge()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_edd_label), KiSortByEdd()),
                        new CursorCommonObjectSort(getResources().getString(R.string.sort_by_no_ibu_label), KiSortByNoIbu()),
                        //    new CursorCommonObjectSort(getResources().getString(R.string.sort_by_high_risk_pregnancy_label),ShortByriskflag()),
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
        Log.e(TAG, "clientsProvider: here");
        return null;
    }

    private DialogOption[] getEditOptions() {
        return ((NativeKISmartRegisterActivity) getActivity()).getEditOptions();
    }

    @Override
    protected void onInitialization() {
        //  context.formSubmissionRouter().getHandlerMap().put("census_enrollment_form", new CensusEnrollmentHandler());
    }

    @Override
    public void startRegistration() {
        if(Support.ONSYNC) {
            Toast.makeText(getActivity(), "Data still Synchronizing, please wait", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniqueIdJson = LoginActivity.generator.uniqueIdController().getUniqueIdJson();
        if (uniqueIdJson == null || uniqueIdJson.isEmpty()) {
            Toast.makeText(getActivity(), "no unique id", Toast.LENGTH_LONG).show();
            return;
        }

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        String locationDialogTAG = "locationDialogTAG";
        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        LocationSelectorDialogFragment
                .newInstance((NativeKISmartRegisterActivity) getActivity(), new
                                EditDialogOptionModel(), context().anmLocationController().get(),
                        "kartu_ibu_registration")
                .show(ft, locationDialogTAG);
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

    private String filterStringForAll() {
        return "";
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initializeQueries(String s) {
        try {

            KIClientsProvider kiscp = new KIClientsProvider(getActivity(), clientActionHandler, context().alertService());
            clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, kiscp,
                    new CommonRepository("ec_kartu_ibu", new String[]{"ec_kartu_ibu.is_closed", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"}));
            clientsView.setAdapter(clientAdapter);

            setTablename("ec_kartu_ibu");
            SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
            countqueryBUilder.SelectInitiateMainTableCounts("ec_kartu_ibu");
            // countqueryBUilder.customJoin("LEFT JOIN ec_anak ON ec_kartu_ibu.id = ec_anak.relational_id ");

            if (s == null || Objects.equals(s, "!")) {
                mainCondition = "is_closed = 0 and namalengkap != '' ";
                Log.e(TAG, "initializeQueries: Not Initialized" );
            } else {
                Log.e(TAG, "initializeQueries: id " + s);
                mainCondition = "is_closed = 0 and namalengkap != '' AND object_id LIKE '%" + s + "%'";
            }
            joinTable = "";
            countSelect = countqueryBUilder.mainCondition(mainCondition);
            super.CountExecute();

            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();

            queryBUilder.SelectInitiateMainTable("ec_kartu_ibu", new String[]{"ec_kartu_ibu.relationalid", "ec_kartu_ibu.is_closed", "ec_kartu_ibu.details", "ec_kartu_ibu.isOutOfArea", "ec_kartu_ibu.namalengkap", "ec_kartu_ibu.umur", "ec_kartu_ibu.namaSuami", "noIbu"});
            //   queryBUilder.customJoin("LEFT JOIN ec_anak ON ec_kartu_ibu.id = ec_anak.relational_id ");
            mainSelect = queryBUilder.mainCondition(mainCondition);
            Sortqueries = KiSortByNameAZ();

            currentlimit = 20;
            currentoffset = 0;

            super.filterandSortInInitializeQueries();
            CountExecute();
            updateSearchView();
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    FlurryFacade.logEvent("click_detail_view_on_kohort_ibu_dashboard");
                    KIDetailActivity.kiclient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(getActivity(), KIDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;

                case R.id.btn_edit:
                    KIDetailActivity.kiclient = (CommonPersonObjectClient) view.getTag();
                    showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

    }

    private String KiSortByNameAZ() {
        return "namalengkap ASC";
    }

    private String KiSortByNameZA() {
        return "namalengkap DESC";
    }

    private String KiSortByAge() {
        return "umur DESC";
    }

    private String KiSortByNoIbu() {
        return "noIbu ASC";
    }

    private String KiSortByEdd() {
        return "htp IS NULL, htp";
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {

            if (option.name().equalsIgnoreCase(getString(R.string.str_register_anc_form))) {
                CommonPersonObjectClient pc = KIDetailActivity.kiclient;
                AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                if (ibuparent != null) {
                    short anc_isclosed = ibuparent.getClosed();
                    if (anc_isclosed == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            if(option.name().equalsIgnoreCase(getString(R.string.str_register_fp_form)) ) {
                CommonPersonObjectClient pc = KIDetailActivity.kiclient;

                if(!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered_in_fp), Toast.LENGTH_SHORT).show();
                    return;
                }

                AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
                final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
                if (ibuparent != null) {
                    short anc_isclosed = ibuparent.getClosed();
                    if (anc_isclosed == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.mother_already_registered), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    @Override
    protected void onResumption() {
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries("");
        }
        try {
            LoginActivity.setLanguage();
        } catch (Exception ignored) {

        }

    }

    public void updateSearchView() {
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {

                filters = cs.toString();
                joinTable = "";
                mainCondition = "is_closed = 0 ";

                getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                CountExecute();
                filterandSortExecute();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                dialogOptionslist.add(new KICommonObjectFilterOption(name, "location_name", name, "ec_kartu_ibu"));

            }
        }
    }

    //    WD
    public static String criteria;

    public void setCriteria(String criteria) {
        NativeKISmartRegisterFragment.criteria = criteria;
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

        FlurryAgent.logEvent(TAG + "search_by_face", true);
        Log.d(TAG, "getFacialRecord: ");
//        Log.e(TAG, "getFacialRecord: ");

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

                        @Override
                        protected Object doInBackground(Object[] params) {
                            filters = cs.toString();
                            return null;
                        }
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

        Intent myIntent = new Intent(getActivity(), NativeKISmartRegisterActivity.class);
        if (data != null) {
            myIntent.putExtra("org.ei.opensrp.indonesia.face.face_mode", true);
            myIntent.putExtra("org.ei.opensrp.indonesia.face.base_id", data.getStringExtra("org.ei.opensrp.indonesia.face.base_id"));
        }
        getActivity().startActivity(myIntent);

    }

}

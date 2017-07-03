package org.ei.opensrp.path.fragment;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.WanderingCubes;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildImmunizationActivity;
import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.path.activity.LoginActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.RegisterClickables;
import org.ei.opensrp.path.option.BasicSearchOption;
import org.ei.opensrp.path.option.DateSort;
import org.ei.opensrp.path.option.StatusSort;
import org.ei.opensrp.path.provider.ChildSmartClientsProvider;
import org.ei.opensrp.path.receiver.SyncStatusBroadcastReceiver;
import org.ei.opensrp.path.servicemode.VaccinationServiceModeOption;
import org.ei.opensrp.path.view.LocationPickerView;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.List;

import util.PathConstants;
import util.VaccinateActionUtils;

import static android.view.View.INVISIBLE;

public class ChildSmartRegisterFragment extends BaseSmartRegisterFragment implements SyncStatusBroadcastReceiver.SyncStatusListener {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private LocationPickerView clinicSelection;
    private static final long NO_RESULT_SHOW_DIALOG_DELAY = 1000l;
    private Handler showNoResultDialogHandler;
    private NotInCatchmentDialogFragment notInCatchmentDialogFragment;
    private TextView filterCount;
    private View filterSection;
    private ImageView backButton;
    private TextView nameInitials;
    private LinearLayout btnBackToHome;
    private ProgressBar syncProgressBar;
    private int dueOverdueCount = 0;

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            // FIXME path_conflict
            //@Override
            public FilterOption searchFilterOption() {
                return new BasicSearchOption("", BasicSearchOption.Type.getByRegisterName(getDefaultOptionsProvider().nameInShortFormForTitle()));
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(null, "Linda Clinic", new int[]{
                        R.string.child_profile, R.string.birthdate_age, R.string.epi_number, R.string.child_contact_number,
                        R.string.child_next_vaccine
                }, new int[]{5, 2, 2, 3, 3});
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new CursorCommonObjectSort(getResources().getString(R.string.woman_alphabetical_sort), "last_interacted_with desc");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.zeir);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CursorCommonObjectSort(getResources().getString(R.string.woman_alphabetical_sort), "first_name"),
                        new DateSort("Age", "dob"),
                        new StatusSort("Due Status"),
                        new CursorCommonObjectSort(getResources().getString(R.string.id_sort), "zeir_id")
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.str_search_hint);
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
        ((ChildSmartRegisterActivity) getActivity()).startFormActivity("child_enrollment", null, null);
    }

    @Override
    protected void onCreation() {
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        if (isPausedOrRefreshList()) {
            initializeQueries();
        }
        updateSearchView();
        try {
            LoginActivity.setLanguage();
        } catch (Exception e) {

        }

        updateLocationText();
        if (filterMode()) {
            toggleFilterSelection();
        }
        SyncStatusBroadcastReceiver.getInstance().addSyncStatusListener(this);
        refreshSyncStatusViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = inflater.inflate(R.layout.smart_register_activity_customized, container, false);
        mView = view;
        onInitialization();
        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        filterSection = view.findViewById(R.id.filter_selection);
        filterSection.setOnClickListener(clientActionHandler);

        filterCount = (TextView) view.findViewById(R.id.filter_count);
        filterCount.setVisibility(View.GONE);
        filterCount.setClickable(false);
        filterCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isClickable()) {
                    filterSection.performClick();
                }
            }
        });

        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
        populateClientListHeaderView(view);

        View qrCode = view.findViewById(R.id.scan_qr_code);
        qrCode.setOnClickListener(clientActionHandler);

        backButton = (ImageView) view.findViewById(R.id.back_button);
        nameInitials = (TextView) view.findViewById(R.id.name_inits);
        btnBackToHome = (LinearLayout) view.findViewById(R.id.btn_back_to_home);
        syncProgressBar = (ProgressBar) view.findViewById(R.id.sync_progress_bar);
        Circle circle = new Circle();
        syncProgressBar.setIndeterminateDrawable(circle);

        AllSharedPreferences allSharedPreferences = Context.getInstance().allSharedPreferences();
        String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
        if (!preferredName.isEmpty()) {
            String[] preferredNameArray = preferredName.split(" ");
            String initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
            nameInitials.setText(initials);
        }

        View globalSearchButton = mView.findViewById(R.id.global_search);
        globalSearchButton.setOnClickListener(clientActionHandler);
    }

    @Override
    protected void goBack() {
        if (filterMode()) {
            toggleFilterSelection();
        } else {
            DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (filterMode()) {
            toggleFilterSelection();
            return true;
        }
        return false;
    }

    public LocationPickerView getLocationPickerView() {
        return getClinicSelection();
    }

    public void initializeQueries() {
        String tableName = PathConstants.CHILD_TABLE_NAME;
        String parentTableName = PathConstants.MOTHER_TABLE_NAME;

        ChildSmartClientsProvider hhscp = new ChildSmartClientsProvider(getActivity(),
                clientActionHandler, context().alertService(), VaccinatorApplication.getInstance().vaccineRepository(), VaccinatorApplication.getInstance().weightRepository());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), null, hhscp, Context.getInstance().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        setTablename(tableName);
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(tableName);
        countSelect = countqueryBUilder.mainCondition("");
        mainCondition = "";
        super.CountExecute();
        countOverDue();
        countDueOverDue();

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + ".details",
                tableName + ".zeir_id",
                tableName + ".relational_id",
                tableName + ".first_name",
                tableName + ".last_name",
                tableName + ".gender",
                parentTableName + ".first_name as mother_first_name",
                parentTableName + ".last_name as mother_last_name",
                parentTableName + ".dob as mother_dob",
                parentTableName + ".nrc_number as mother_nrc_number",
                tableName + ".father_name",
                tableName + ".dob",
                tableName + ".epi_card_number",
                tableName + ".contact_phone_number",
                tableName + ".pmtct_status",
                tableName + ".provider_uc",
                tableName + ".provider_town",
                tableName + ".provider_id",
                tableName + ".provider_location_id",
                tableName + ".client_reg_date",
                tableName + ".last_interacted_with",
                tableName + ".inactive",
                tableName + ".lost_to_follow_up"
        });
        queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
        mainSelect = queryBUilder.mainCondition("");
        Sortqueries = ((CursorSortOption) getDefaultOptionsProvider().sortOption()).sort();

        currentlimit = 20;
        currentoffset = 0;

        super.filterandSortInInitializeQueries();

        updateSearchView();
        refresh();
    }

    private void refreshSyncStatusViews() {
        if (SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            syncProgressBar.setVisibility(View.VISIBLE);
            btnBackToHome.setVisibility(View.GONE);
        } else {
            syncProgressBar.setVisibility(View.GONE);
            btnBackToHome.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSyncStart() {
        refreshSyncStatusViews();
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        refreshSyncStatusViews();
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }
            RegisterClickables registerClickables = new RegisterClickables();

            switch (view.getId()) {
                case R.id.child_profile_info_layout:

                    ChildImmunizationActivity.launchActivity(getActivity(), client, null);
                    break;
                case R.id.record_weight:
                    registerClickables.setRecordWeight(true);
                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                    break;

                case R.id.record_vaccination:
                    registerClickables.setRecordAll(true);
                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                    break;
                case R.id.filter_selection:
                    toggleFilterSelection();
                    break;

                case R.id.global_search:
                    ((ChildSmartRegisterActivity) getActivity()).startAdvancedSearch();
                    break;

                case R.id.scan_qr_code:
                    ((ChildSmartRegisterActivity) getActivity()).startQrCodeScanner();
                    break;

            }
        }
    }

    public void updateSearchView() {
        getSearchView().removeTextChangedListener(textWatcher);
        getSearchView().addTextChangedListener(textWatcher);
    }

    public void triggerFilterSelection() {
        if (filterSection != null && !filterMode()) {
            filterSection.performClick();
        }
    }

    private void populateClientListHeaderView(View view) {
        LinearLayout clientsHeaderLayout = (LinearLayout) view.findViewById(org.ei.opensrp.R.id.clients_header_layout);
        clientsHeaderLayout.setVisibility(View.GONE);

        LinearLayout headerLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout.smart_register_child_header, null);
        clientsView.addHeaderView(headerLayout);
        clientsView.setEmptyView(getActivity().findViewById(R.id.empty_view));

    }

    /*@Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        super.onLoadFinished(loader, cursor);
        // Check if query was issued
        if (searchView != null && searchView.getText().toString().length() > 0) {
            if (cursor.getCount() == 0) {// No search result found
                if (showNoResultDialogHandler != null) {
                    showNoResultDialogHandler.removeCallbacksAndMessages(null);
                    showNoResultDialogHandler = null;
                }

                showNoResultDialogHandler = new Handler();
                showNoResultDialogHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (notInCatchmentDialogFragment == null) {
                            notInCatchmentDialogFragment = new NotInCatchmentDialogFragment();
                        }

                        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                        Fragment prev = getActivity().getFragmentManager().findFragmentByTag(DIALOG_TAG);
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        if(!notInCatchmentDialogFragment.isVisible()) {
                            notInCatchmentDialogFragment.show(ft, DIALOG_TAG);
                        }
                    }
                }, NO_RESULT_SHOW_DIALOG_DELAY);
            } else {
                if (showNoResultDialogHandler != null) {
                    showNoResultDialogHandler.removeCallbacksAndMessages(null);
                    showNoResultDialogHandler = null;
                }
            }
        }
    }*/

    private String filterSelectionCondition(boolean urgentOnly) {
        String mainCondition = " (inactive != 'true' and lost_to_follow_up != 'true') AND ( ";
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");

        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = 'urgent' ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = 'urgent' or ";
            }
        }

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += " or ";
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = 'normal' ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = 'normal' or ";
            }
        }

        return mainCondition + " ) ";
    }


    public void countOverDue() {
        String mainCondition = filterSelectionCondition(true);
        int count = count(mainCondition);

        if (filterCount != null) {
            if (count > 0) {
                filterCount.setText(String.valueOf(count));
                filterCount.setVisibility(View.VISIBLE);
                filterCount.setClickable(true);
            } else {
                filterCount.setVisibility(View.GONE);
                filterCount.setClickable(false);
            }
        }

        ((ChildSmartRegisterActivity) getActivity()).updateAdvancedSearchFilterCount(count);
    }

    public void countDueOverDue() {
        String mainCondition = filterSelectionCondition(false);
        int count = count(mainCondition);
        dueOverdueCount = count;
    }

    private int count(String mainConditionString) {

        int count = 0;

        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, "", mainConditionString, "");
                List<String> ids = commonRepository().findSearchIds(sql);
                query = sqb.toStringFts(ids, tablename + "." + CommonRepository.ID_COLUMN);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);
            }

            Log.i(getClass().getName(), query);
            c = commonRepository().RawCustomQueryForAdapter(query);
            c.moveToFirst();
            count = c.getInt(0);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return count;

    }

    private void switchViews(boolean filterSelected) {
        if (filterSelected) {
            if (titleLabelView != null) {
                titleLabelView.setText(String.format(getString(R.string.overdue_due), dueOverdueCount));
            }
            nameInitials.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
        } else {
            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.zeir));
            }
            nameInitials.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.GONE);
        }
    }

    private void toggleFilterSelection() {
        if (filterSection != null) {
            String tagString = "PRESSED";
            if (filterSection.getTag() == null) {
                filter("", "", filterSelectionCondition(false));
                filterSection.setTag(tagString);
                filterSection.setBackgroundResource(R.drawable.transparent_clicked_background);
                switchViews(true);
            } else if (filterSection.getTag().toString().equals(tagString)) {
                filter("", "", "");
                filterSection.setTag(null);
                filterSection.setBackgroundResource(R.drawable.transparent_gray_background);
                switchViews(false);
            }
        }
    }

    private boolean filterMode() {
        return filterSection != null && filterSection.getTag() != null;
    }

}

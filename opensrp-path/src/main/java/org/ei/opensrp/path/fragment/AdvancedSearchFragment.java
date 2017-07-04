package org.ei.opensrp.path.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.ChildImmunizationActivity;
import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.path.adapter.AdvancedSearchPaginatedCursorAdapter;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.RegisterClickables;
import org.ei.opensrp.path.provider.AdvancedSearchClientsProvider;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.GlobalSearchUtils;
import util.JsonFormUtils;
import util.MoveToMyCatchmentUtils;
import util.PathConstants;
import util.Utils;

public class AdvancedSearchFragment extends BaseSmartRegisterFragment {
    private View mView;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private Button search;
    private RadioButton outsideInside;
    private RadioButton myCatchment;
    private CheckBox active;
    private CheckBox inactive;
    private CheckBox lostToFollowUp;
    private MaterialEditText zeirId;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    private MaterialEditText motherGuardianName;
    private MaterialEditText motherGuardianNrc;
    private MaterialEditText motherGuardianPhoneNumber;
    private EditText startDate;
    private EditText endDate;

    private TextView searchCriteria;
    private TextView matchingResults;
    private View listViewLayout;
    private View advancedSearchForm;

    private TextView filterCount;

    private ProgressDialog progressDialog;

    //private List<Integer> editedList = new ArrayList<>();
    private Map<String, String> editMap = new HashMap<>();
    private boolean listMode = false;
    private int overdueCount = 0;
    private boolean outOfArea = false;
    private AdvancedMatrixCursor matrixCursor;

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    private static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";

    private static final String ZEIR_ID = "zeir_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String NRC_NUMBER = "nrc_number";

    private static final String CONTACT_PHONE_NUMBER = "contact_phone_number";
    private static final String BIRTH_DATE = "birth_date";

    private static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
    private static final String MOTHER_GUARDIAN_FIRST_NAME = "mother_first_name";
    private static final String MOTHER_GUARDIAN_LAST_NAME = "mother_last_name";
    private static final String MOTHER_GUARDIAN_NRC_NUMBER = "mother_nrc_number";
    private static final String MOTHER_GUARDIAN_PHONE_NUMBER = "mother_contact_phone_number";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";

    AdvancedSearchPaginatedCursorAdapter clientAdapter;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = inflater.inflate(R.layout.smart_register_activity_advanced_search, container, false);
        mView = view;
        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    protected void onCreation() {
    }

    @Override
    protected void onResumption() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            switchViews(false);
            updateLocationText();
            updateSeachLimits();
            resetForm();
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);
        advancedSearchForm = view.findViewById(R.id.advanced_search_form);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.global_search);
        imageButton.setBackgroundColor(getResources().getColor(R.color.transparent_dark_blue));
        imageButton.setOnClickListener(clientActionHandler);


        final View filterSection = view.findViewById(R.id.filter_selection);
        filterSection.setOnClickListener(clientActionHandler);

        filterCount = (TextView) view.findViewById(R.id.filter_count);
        filterCount.setVisibility(View.GONE);
        if (overdueCount > 0) {
            updateFilterCount(overdueCount);
        }
        filterCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterSection.performClick();
            }
        });

        if (titleLabelView != null) {
            titleLabelView.setText(getString(R.string.advanced_search));
        }

        View nameInitials = view.findViewById(R.id.name_inits);
        nameInitials.setVisibility(View.GONE);

        ImageView backButton = (ImageView) view.findViewById(R.id.back_button);
        backButton.setVisibility(View.VISIBLE);

        populateFormViews(view);

        initializeProgressDialog();
    }

    @Override
    public void setupSearchView(View view) {
    }

    @Override
    protected void startRegistration() {
        ((ChildSmartRegisterActivity) getActivity()).startFormActivity("child_enrollment", null, null);
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
                case R.id.global_search:
                    goBack();
                    break;
                case R.id.filter_selection:
                    ((ChildSmartRegisterActivity) getActivity()).filterSelection();
                    break;
                case R.id.search_layout:
                case R.id.search:
                    search(view);
                    break;
                case R.id.child_profile_info_layout:
                    ChildImmunizationActivity.launchActivity(getActivity(), client, null);
                    break;
                case R.id.record_weight:
                    if (client == null) {
                        if (view.getTag() != null && view.getTag() instanceof String) {
                            String zeirId = view.getTag().toString();
                            ((ChildSmartRegisterActivity) getActivity()).startFormActivity("out_of_catchment_service", zeirId, null);
                        }
                    } else {
                        registerClickables.setRecordWeight(true);
                        ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                    }
                    break;

                case R.id.record_vaccination:
                    if (client != null) {
                        registerClickables.setRecordAll(true);
                        ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                    }
                    break;
                case R.id.move_to_catchment:
                    if (client == null) {
                        if (view.getTag() != null && view.getTag() instanceof List) {
                            List<String> ids = (List<String>) view.getTag();
                            moveToMyCatchmentArea(ids);
                        }
                    }
                    break;
            }
        }
    }

    private void populateFormViews(View view) {
        searchCriteria = (TextView) view.findViewById(R.id.search_criteria);
        matchingResults = (TextView) view.findViewById(R.id.matching_results);
        search = (Button) view.findViewById(R.id.search);

        outsideInside = (RadioButton) view.findViewById(R.id.out_and_inside);
        myCatchment = (RadioButton) view.findViewById(R.id.my_catchment);

        outsideInside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myCatchment.setChecked(!isChecked);
            }
        });

        myCatchment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                outsideInside.setChecked(!isChecked);
            }
        });

        View outsideInsideLayout = view.findViewById(R.id.out_and_inside_layout);
        outsideInsideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideInside.performClick();
            }
        });

        View mycatchmentLayout = view.findViewById(R.id.my_catchment_layout);
        mycatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.performClick();
            }
        });

        active = (CheckBox) view.findViewById(R.id.active);
        inactive = (CheckBox) view.findViewById(R.id.inactive);
        lostToFollowUp = (CheckBox) view.findViewById(R.id.lost_to_follow_up);

        View activeLayout = view.findViewById(R.id.active_layout);
        activeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active.performClick();
            }
        });

        View inactiveLayout = view.findViewById(R.id.inactive_layout);
        inactiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inactive.performClick();
            }
        });

        View lostToFollowUpLayout = view.findViewById(R.id.lost_to_follow_up_layout);
        lostToFollowUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lostToFollowUp.performClick();
            }
        });

        zeirId = (MaterialEditText) view.findViewById(R.id.zeir_id);
        firstName = (MaterialEditText) view.findViewById(R.id.first_name);
        lastName = (MaterialEditText) view.findViewById(R.id.last_name);
        motherGuardianName = (MaterialEditText) view.findViewById(R.id.mother_guardian_name);
        motherGuardianNrc = (MaterialEditText) view.findViewById(R.id.mother_guardian_nrc);
        motherGuardianPhoneNumber = (MaterialEditText) view.findViewById(R.id.mother_guardian_phone_number);

        startDate = (EditText) view.findViewById(R.id.start_date);
        endDate = (EditText) view.findViewById(R.id.end_date);

        search.setOnClickListener(clientActionHandler);

        setDatePicker(startDate);
        setDatePicker(endDate);

        resetForm();
    }

    private void resetForm() {
        clearSearchCriteria();

        active.setChecked(true);
        inactive.setChecked(false);
        lostToFollowUp.setChecked(false);

        zeirId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianName.setText("");
        motherGuardianNrc.setText("");
        motherGuardianPhoneNumber.setText("");

        startDate.setText("");
        endDate.setText("");
    }

    private void clearSearchCriteria() {
        searchCriteria.setVisibility(View.GONE);
        searchCriteria.setText("");
    }

    private void updateSeachLimits() {
        if (Utils.isConnectedToNetwork(getActivity())) {
            outsideInside.setChecked(true);
            myCatchment.setChecked(false);
        } else {
            myCatchment.setChecked(true);
            outsideInside.setChecked(false);
        }

    }

    public void search(final View view) {
        android.util.Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        ((ChildSmartRegisterActivity) getActivity()).hideKeyboard();
        view.setClickable(false);

        if (!hasSearchParams()) {
            Toast.makeText(getActivity(), getString(R.string.update_search_params), Toast.LENGTH_LONG).show();
            return;
        }

        String tableName = PathConstants.CHILD_TABLE_NAME;
        String parentTableName = PathConstants.MOTHER_TABLE_NAME;

        editMap.clear();

        String searchCriteriaString = "Search criteria: Include: ";

        if (outsideInside.isChecked()) {
            outOfArea = true;
            searchCriteriaString += " \"Outside and Inside My Catchment Area\", ";
        } else if (myCatchment.isChecked()) {
            outOfArea = false;
            searchCriteriaString += " \"My Catchment Area\", ";
        }

        //Inactive
        boolean isInactive = inactive.isChecked();
        if (isInactive) {
            String inActiveKey = INACTIVE;
            if (!outOfArea) {
                inActiveKey = tableName + "." + INACTIVE;
            }
            editMap.put(inActiveKey, Boolean.toString(isInactive));
        }
        //Active
        boolean isActive = active.isChecked();
        if (isActive) {
            String activeKey = ACTIVE;
            if (!outOfArea) {
                activeKey = tableName + "." + ACTIVE;
            }
            editMap.put(activeKey, Boolean.toString(isActive));
        }

        //Lost To Follow Up
        boolean isLostToFollowUp = lostToFollowUp.isChecked();
        if (isLostToFollowUp) {
            String lostToFollowUpKey = LOST_TO_FOLLOW_UP;
            if (!outOfArea) {
                lostToFollowUpKey = tableName + "." + LOST_TO_FOLLOW_UP;
            }
            editMap.put(lostToFollowUpKey, Boolean.toString(isLostToFollowUp));
        }

        if (isActive || isInactive || isLostToFollowUp) {
            String statusString = " \"";
            if (isActive) {
                statusString += "Active";
            }
            if (isInactive) {
                if (statusString.contains("ctive")) {
                    statusString += ", Inactive";
                } else {
                    statusString += "Inactive";
                }
            }
            if (isLostToFollowUp) {
                if (statusString.contains("ctive")) {
                    statusString += ", Lost to Follow-up";
                } else {
                    statusString += "Lost to Follow-up";
                }
            }
            statusString += "\"; ";

            searchCriteriaString += statusString;
        }

        if (isActive == isInactive && isActive == isLostToFollowUp) {

            if (editMap.containsKey(INACTIVE)) {
                editMap.remove(INACTIVE);
            }

            if (editMap.containsKey(tableName + "." + INACTIVE)) {
                editMap.remove(tableName + "." + INACTIVE);
            }

            if (editMap.containsKey(ACTIVE)) {
                editMap.remove(ACTIVE);
            }

            if (editMap.containsKey(tableName + "." + ACTIVE)) {
                editMap.remove(tableName + "." + ACTIVE);
            }

            if (editMap.containsKey(LOST_TO_FOLLOW_UP)) {
                editMap.remove(LOST_TO_FOLLOW_UP);
            }

            if (editMap.containsKey(tableName + "." + LOST_TO_FOLLOW_UP)) {
                editMap.remove(tableName + "." + LOST_TO_FOLLOW_UP);
            }

        }

        String zeirIdString = zeirId.getText().toString();
        if (StringUtils.isNotBlank(zeirIdString))

        {
            searchCriteriaString += " ZEIR ID: \"" + bold(zeirIdString) + "\",";
            String key = ZEIR_ID;
            if (!outOfArea) {
                key = tableName + "." + ZEIR_ID;
            }
            editMap.put(key, zeirIdString.trim());
        }

        String firstNameString = firstName.getText().toString();
        if (StringUtils.isNotBlank(firstNameString))

        {
            searchCriteriaString += " First name: \"" + bold(firstNameString) + "\",";
            String key = FIRST_NAME;
            if (!outOfArea) {
                key = tableName + "." + FIRST_NAME;
            }
            editMap.put(key, firstNameString.trim());
        }

        String lastNameString = lastName.getText().toString();
        if (StringUtils.isNotBlank(lastNameString))

        {
            searchCriteriaString += " Last name: \"" + bold(lastNameString) + "\",";
            String key = LAST_NAME;
            if (!outOfArea) {
                key = tableName + "." + LAST_NAME;
            }
            editMap.put(key, lastNameString.trim());
        }

        String motherGuardianNameString = motherGuardianName.getText().toString();
        if (StringUtils.isNotBlank(motherGuardianNameString))

        {
            searchCriteriaString += " Mother/Guardian name: \"" + bold(motherGuardianNameString) + "\",";
            String key = MOTHER_GUARDIAN_FIRST_NAME;
            if (!outOfArea) {
                key = parentTableName + "." + FIRST_NAME;
            }
            editMap.put(key, motherGuardianNameString.trim());

            key = MOTHER_GUARDIAN_LAST_NAME;
            if (!outOfArea) {
                key = parentTableName + "." + LAST_NAME;
            }
            editMap.put(key, motherGuardianNameString.trim());
        }

        String motherGuardianNrcString = motherGuardianNrc.getText().toString();
        if (StringUtils.isNotBlank(motherGuardianNrcString))

        {
            searchCriteriaString += " Mother/Guardian nrc: \"" + bold(motherGuardianNrcString) + "\",";
            String key = MOTHER_GUARDIAN_NRC_NUMBER;
            if (!outOfArea) {
                key = parentTableName + "." + NRC_NUMBER;
            }
            editMap.put(key, motherGuardianNrcString.trim());
        }

        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();
        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString))

        {
            searchCriteriaString += " Mother/Guardian phone number: \"" + bold(motherGuardianPhoneNumberString) + "\",";
            String key = MOTHER_GUARDIAN_PHONE_NUMBER;
            if (!outOfArea) {
                key = parentTableName + "." + CONTACT_PHONE_NUMBER;
            }
            editMap.put(key, motherGuardianPhoneNumberString.trim());
        }

        String startDateString = startDate.getText().toString();
        if (StringUtils.isNotBlank(startDateString))

        {
            searchCriteriaString += " Start date: \"" + bold(startDateString) + "\",";
            editMap.put(START_DATE, startDateString.trim());
        }

        String endDateString = endDate.getText().toString();
        if (StringUtils.isNotBlank(endDateString))

        {
            searchCriteriaString += " End date: \"" + bold(endDateString) + "\",";
            editMap.put(END_DATE, endDateString.trim());
        }

        if (searchCriteria != null)

        {
            searchCriteria.setText(Html.fromHtml(removeLastComma(searchCriteriaString)));
            searchCriteria.setVisibility(View.VISIBLE);
        }

        if (outOfArea) {
            globalSearch();
        } else {
            localSearch();
        }

        view.setClickable(true);

    }

    private void initListMode() {
        switchViews(true);

        String tableName = PathConstants.CHILD_TABLE_NAME;
        setTablename(tableName);
        AdvancedSearchClientsProvider hhscp = new AdvancedSearchClientsProvider(getActivity(),
                clientActionHandler, context().alertService(), VaccinatorApplication.getInstance().vaccineRepository(), VaccinatorApplication.getInstance().weightRepository(), commonRepository());
        clientAdapter = new AdvancedSearchPaginatedCursorAdapter(getActivity(), null, hhscp, context().commonrepository(tableName));
        clientsView.setAdapter(clientAdapter);

        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder();
        countqueryBUilder.SelectInitiateMainTableCounts(getTablename());
        countSelect = countqueryBUilder.mainCondition("");

    }

    private void localSearch() {

        initListMode();

        CountExecute();

        refresh();

        filterandSortInInitializeQueries();
    }

    private void globalSearch() {

        initListMode();

        if (editMap.containsKey(START_DATE) || editMap.containsKey(END_DATE)) {

            Date date0 = new Date(0);
            String startDate = DateUtil.yyyyMMdd.format(date0);

            Date now = new Date();
            String endDate = DateUtil.yyyyMMdd.format(now);

            if (editMap.containsKey(START_DATE)) {
                startDate = editMap.remove(START_DATE);
            }
            if (editMap.containsKey(END_DATE)) {
                endDate = editMap.remove(END_DATE);
            }

            String bDate = startDate + ":" + endDate;
            editMap.put(BIRTH_DATE, bDate);
        }

        progressDialog.setTitle(getString(R.string.searching_dialog_title));
        progressDialog.setMessage(getString(R.string.searching_dialog_message));
        GlobalSearchUtils.backgroundSearch(editMap, listener, progressDialog);
    }

    @Override
    public void CountExecute() {

        Cursor c = null;

        try {

            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = sqb.mainCondition(getMainConditionString(getTablename()));
            query = sqb.Endquery(query);

            Log.i(getClass().getName(), query);
            c = commonRepository().RawCustomQueryForAdapter(query);
            c.moveToFirst();
            totalcount = c.getInt(0);
            Log.v("total count here", "" + totalcount);
            currentlimit = 20;
            currentoffset = 0;

            updateMatchingResults(totalcount);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public String filterandSortQuery() {
        String tableName = getTablename();
        String parentTableName = "ec_mother";

        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]

                        {
                                tableName + ".relationalid",
                                tableName + ".details",
                                tableName + ".zeir_id",
                                tableName + ".relational_id",
                                tableName + ".first_name",
                                tableName + ".last_name",
                                tableName + ".gender",
                                parentTableName + ".first_name as mother_first_name",
                                parentTableName + ".last_name as mother_last_name",
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
                        }

        );
        queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
        queryBUilder.mainCondition(getMainConditionString(tableName));
        String query = queryBUilder.orderbyCondition(sortByStatus());
        return queryBUilder.Endquery(queryBUilder.addlimitandOffset(query, currentlimit, currentoffset));
    }

    private String sortByStatus() {
        return " CASE WHEN " + PathConstants.CHILD_TABLE_NAME + ".inactive  != 'true' is null and " + PathConstants.CHILD_TABLE_NAME + ".lost_to_follow_up != 'true' THEN 1 "
                + " WHEN " + PathConstants.CHILD_TABLE_NAME + ".inactive = 'true' THEN 2 "
                + " WHEN " + PathConstants.CHILD_TABLE_NAME + ".lost_to_follow_up = 'true' THEN 3 END ";
    }

    @Override
    public boolean onBackPressed() {
        if (listMode) {
            switchViews(false);
            return true;
        }
        return false;
    }

    @Override
    protected void goBack() {
        if (listMode) {
            switchViews(false);
        } else {
            ((ChildSmartRegisterActivity) getActivity()).switchToBaseFragment(null);
        }
    }

    private String getMainConditionString(String tableName) {

        String startDateKey = START_DATE;
        String endDateKey = END_DATE;

        String mainConditionString = "";
        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!key.equals(startDateKey) && !key.equals(endDateKey) && !key.contains(ACTIVE) && !key.contains(INACTIVE) && !key.contains(LOST_TO_FOLLOW_UP)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + key + " Like '%" + value + "%'";
                } else {
                    mainConditionString += " AND " + key + " Like '%" + value + "%'";

                }
            }
        }

        if (StringUtils.isBlank(mainConditionString)) {
            if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                mainConditionString += " " + tableName + ".dob BETWEEN '" + editMap.get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
            }
        } else {
            if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                mainConditionString += " AND " + tableName + ".dob BETWEEN '" + editMap.get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " AND " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " AND " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
            }
        }


        String statusConditionString = "";
        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.contains(ACTIVE) || key.contains(INACTIVE) || key.contains(LOST_TO_FOLLOW_UP)) {

                if (StringUtils.isBlank(statusConditionString)) {
                    if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                        statusConditionString += " (" + tableName + "." + INACTIVE + " != '" + Boolean.TRUE.toString() + "' AND " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE.toString() + "') ";
                    } else {
                        statusConditionString += " " + key + " = '" + value + "'";
                    }
                } else {
                    if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                        statusConditionString += " OR (" + tableName + "." + INACTIVE + " != '" + Boolean.TRUE.toString() + "' AND " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE.toString() + "') ";

                    } else {
                        statusConditionString += " OR " + key + " = '" + value + "'";
                    }

                }
            }
        }

        if (!statusConditionString.isEmpty()) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += statusConditionString;
            } else {
                mainConditionString += " AND (" + statusConditionString + ")";
            }
        }

        return mainConditionString;

    }

    private void switchViews(boolean showList) {
        if (showList) {
            advancedSearchForm.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);
            clientsView.setVisibility(View.VISIBLE);

            updateMatchingResults(0);
            showProgressView();
            listMode = true;
        } else {
            clearSearchCriteria();
            advancedSearchForm.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);
            clientsView.setVisibility(View.INVISIBLE);
            listMode = false;
        }
    }


    private void setDatePicker(final EditText editText) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), android.app.AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, selectedyear);
                        calendar.set(Calendar.MONTH, selectedmonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedday);

                        String dateString = DateUtil.yyyyMMdd.format(calendar.getTime());
                        editText.setText(dateString);

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.getDatePicker().setCalendarViewShown(false);
                mDatePicker.show();

                DatePickerUtils.themeDatePicker(mDatePicker, new char[]{'d', 'm', 'y'});
            }
        });

    }

    private String removeLastComma(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        if (!outOfArea) {
                            String query = filterandSortQuery();
                            Cursor cursor = commonRepository().RawCustomQueryForAdapter(query);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressView();
                                }

                                ;
                            });

                            return cursor;
                        } else {
                            return matrixCursor;
                        }
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        clientAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clientAdapter.swapCursor(null);
    }

    private boolean hasSearchParams() {
        boolean hasSearchParams = false;
        if (inactive.isChecked()) {
            hasSearchParams = true;
        } else if (active.isChecked()) {
            hasSearchParams = true;
        } else if (lostToFollowUp.isChecked()) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(zeirId.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(firstName.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(lastName.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(motherGuardianName.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(motherGuardianNrc.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(motherGuardianPhoneNumber.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(startDate.getText().toString())) {
            hasSearchParams = true;
        } else if (StringUtils.isNotEmpty(endDate.getText().toString())) {
            hasSearchParams = true;
        }
        return hasSearchParams;
    }

    public void updateFilterCount(int count) {
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
        overdueCount = count;
    }

    private String getJsonString(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                String string = jsonObject.getString(field);
                if (string.equals("null")) {
                    return "";
                } else {
                    return string;
                }
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return "";

    }

    private JSONObject getJsonObject(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    private JSONObject getJsonObject(JSONArray jsonArray, int position) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                return jsonArray.getJSONObject(position);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    public class AdvancedMatrixCursor extends net.sqlcipher.MatrixCursor {
        public AdvancedMatrixCursor(String[] columnNames) {
            super(columnNames);
        }

        @Override
        public long getLong(int column) {
            try {
                return super.getLong(column);
            } catch (NumberFormatException e) {
                return (new Date()).getTime();
            }
        }

    }

    public EditText getZeirId() {
        return this.zeirId;
    }

    private void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(String.format(getString(R.string.matching_results), count));
        }
    }

    private void refreshBaseRegister() {
        ((ChildSmartRegisterActivity) getActivity()).refreshList(FetchStatus.fetched);
    }

    private void moveToMyCatchmentArea(final List<String> ids) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.PathAlertDialog)
                .setMessage(R.string.move_to_catchment_confirm_dialog_message)
                .setTitle(R.string.move_to_catchment_confirm_dialog_title)
                .setCancelable(false)
                .setPositiveButton(org.ei.opensrp.path.R.string.no_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                .setNegativeButton(org.ei.opensrp.path.R.string.yes_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                progressDialog.setTitle(getString(R.string.move_to_catchment_dialog_title));
                                progressDialog.setMessage(getString(R.string.move_to_catchment_dialog_message));
                                MoveToMyCatchmentUtils.moveToMyCatchment(ids, moveToMyCatchmentListener, progressDialog);
                            }
                        }).create();

        dialog.show();
    }

    private String bold(String textToBold) {
        return "<b>" + textToBold + "</b>";
    }

    final Listener<JSONArray> listener = new Listener<JSONArray>() {
        public void onEvent(final JSONArray jsonArray) {


            String[] columns = new String[]{"_id", "relationalid", FIRST_NAME, "middle_name", LAST_NAME, "gender", "dob", ZEIR_ID, "epi_card_number", MOTHER_BASE_ENTITY_ID, MOTHER_GUARDIAN_FIRST_NAME, MOTHER_GUARDIAN_LAST_NAME, "inactive", "lost_to_follow_up"};
            matrixCursor = new AdvancedMatrixCursor(columns);

            if (jsonArray != null) {

                List<JSONObject> jsonValues = new ArrayList<JSONObject>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonValues.add(getJsonObject(jsonArray, i));
                }

                Collections.sort(jsonValues, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {

                        if (!lhs.has("child") || !rhs.has("child")) {
                            return 0;
                        }

                        JSONObject lhsChild = getJsonObject(lhs, "child");
                        JSONObject rhsChild = getJsonObject(rhs, "child");

                        String lhsInactive = getJsonString(getJsonObject(lhsChild, "attributes"), "inactive");
                        String rhsInactive = getJsonString(getJsonObject(rhsChild, "attributes"), "inactive");

                        int aComp = 0;
                        if (lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                            aComp = 1;
                        } else if (!lhsInactive.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsInactive.equalsIgnoreCase(Boolean.TRUE.toString())) {
                            aComp = -1;
                        }

                        if (aComp != 0) {
                            return aComp;
                        } else {
                            String lhsLostToFollowUp = getJsonString(getJsonObject(lhsChild, "attributes"), "lost_to_follow_up");
                            String rhsLostToFollowUp = getJsonString(getJsonObject(rhsChild, "attributes"), "lost_to_follow_up");
                            if (lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && !rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
                                return 1;
                            } else if (!lhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString()) && rhsLostToFollowUp.equalsIgnoreCase(Boolean.TRUE.toString())) {
                                return -1;
                            }
                        }

                        return 0;

                    }
                });

                for (JSONObject client : jsonValues) {
                    String entityId = "";
                    String firstName = "";
                    String middleName = "";
                    String lastName = "";
                    String gender = "";
                    String dob = "";
                    String zeirId = "";
                    String epiCardNumber = "";
                    String inactive = "";
                    String lostToFollowUp = "";

                    if (client == null) {
                        continue;
                    }

                    if (client.has("child")) {
                        JSONObject child = getJsonObject(client, "child");
                        entityId = getJsonString(child, "baseEntityId");
                        firstName = getJsonString(child, "firstName");
                        middleName = getJsonString(child, "middleName");
                        lastName = getJsonString(child, "lastName");

                        gender = getJsonString(child, "gender");
                        dob = getJsonString(child, "birthdate");
                        if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                            try {
                                Long dobLong = Long.valueOf(dob);
                                Date date = new Date(dobLong);
                                dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                            } catch (Exception e) {
                                Log.e(getClass().getName(), e.toString(), e);
                            }
                        }
                        zeirId = getJsonString(getJsonObject(child, "identifiers"), JsonFormUtils.ZEIR_ID);
                        if (StringUtils.isNotBlank(zeirId)) {
                            zeirId = zeirId.replace("-", "");
                        }

                        epiCardNumber = getJsonString(getJsonObject(child, "attributes"), "Child_Register_Card_Number");

                        inactive = getJsonString(getJsonObject(child, "attributes"), "inactive");
                        lostToFollowUp = getJsonString(getJsonObject(child, "attributes"), "lost_to_follow_up");

                    }


                    String motherBaseEntityId = "";
                    String motherFirstName = "";
                    String motherLastName = "";

                    if (client.has("mother")) {
                        JSONObject mother = getJsonObject(client, "mother");
                        motherFirstName = getJsonString(mother, "firstName");
                        motherLastName = getJsonString(mother, "lastName");
                        motherBaseEntityId = getJsonString(mother, "baseEntityId");
                    }

                    matrixCursor.addRow(new Object[]{entityId, null, firstName, middleName, lastName, gender, dob, zeirId, epiCardNumber, motherBaseEntityId, motherFirstName, motherLastName, inactive, lostToFollowUp});
                }
            }

            totalcount = matrixCursor.getCount();
            Log.v("total count here", "" + totalcount);
            currentlimit = 20;
            if (totalcount > 0) {
                currentlimit = totalcount;
            }
            currentoffset = 0;

            updateMatchingResults(totalcount);

            refresh();

            filterandSortInInitializeQueries();
        }
    };


    final Listener<JSONObject> moveToMyCatchmentListener = new Listener<JSONObject>() {
        public void onEvent(final JSONObject jsonObject) {
            if (jsonObject != null) {
                if (MoveToMyCatchmentUtils.processMoveToCatchment(getActivity(), context().allSharedPreferences(), jsonObject)) {
                    clientAdapter.notifyDataSetChanged();
                    refreshBaseRegister();
                } else {
                    Toast.makeText(getActivity(), "Error Processing Records", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Unable to Move to My Catchment", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public void showProgressView() {
        progressDialog.setTitle(getString(R.string.searching_dialog_title));
        progressDialog.setMessage(getString(R.string.searching_dialog_message));
        progressDialog.show();
    }

    @Override
    public void hideProgressView() {
        progressDialog.hide();
    }
}

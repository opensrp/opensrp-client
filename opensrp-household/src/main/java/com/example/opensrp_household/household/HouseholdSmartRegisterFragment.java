package com.example.opensrp_household.household;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opensrp_household.R;
import com.example.opensrp_household.household.handler.HouseholdMemberRegistrationHandler;
import com.example.opensrp_stock.field.util.common.VaccinationServiceModeOption;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.handler.RegisterDataCursorLoaderHandler;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.db.utils.RegisterQuery;
import org.ei.opensrp.core.template.CommonSortingOption;
import org.ei.opensrp.core.template.DefaultOptionsProvider;
import org.ei.opensrp.core.template.FilterOption;
import org.ei.opensrp.core.template.NavBarOptionsProvider;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.template.SearchType;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.template.SortingOption;
import org.ei.opensrp.core.utils.ByColumnAndByDetails;
import org.ei.opensrp.core.utils.barcode.Barcode;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentResult;
import org.ei.opensrp.core.utils.barcode.ScanType;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.ageInYears;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.convertToCommonPersonObject;
import static org.ei.opensrp.core.utils.Utils.getValue;

public class HouseholdSmartRegisterFragment extends RegisterDataGridFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private RegisterDataLoaderHandler loaderHandler;
    private PromptView promptHH;
    private PromptView promptMember;
    private BarcodeIntentIntegrator integ;

    public HouseholdSmartRegisterFragment() {
        super(null);
    }

    @SuppressLint("ValidFragment")
    public HouseholdSmartRegisterFragment(FormController householdFormController) {
        super(householdFormController);
        integ = BarcodeIntentIntegrator.initBarcodeScanner(this);
    }

    @Override
    public String bindType() {
        return "pkhousehold";
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public SearchFilterOption searchFilterOption() {
                return new HouseholdSearchOption("");
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(null, "Household Register", new int[]{
                        R.string.household_profile, R.string.household_members, R.string.household_add_member
                }, new int[]{13, 5, 2});
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortingOption sortOption() {
                return new CommonSortingOption(getResources().getString(R.string.household_alphabetical_sort), "first_name");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.household_register_title);
            }

            @Override
            public SearchType searchType() {
                return SearchType.PASSIVE;
            }
        };
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        //  new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.child_id_sort))
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonSortingOption(getResources().getString(R.string.sort_name), "first_name"),
                        new CommonSortingOption(getResources().getString(R.string.sort_program_id), "household_id"),
                        new CommonSortingOption(getResources().getString(R.string.sort_dob_age), "dob DESC"),
                        new CommonSortingOption(getResources().getString(R.string.sort_num_members), "num_household_members DESC"),//todo
                        new CommonSortingOption(getResources().getString(R.string.sort_num_unregistered_members), "(num_household_members-registeredMembers) DESC")
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.search_hint);
            }
        };
    }//end of method

    @Override
    protected void onInitialization() {
        context.formSubmissionRouter().getHandlerMap().put("woman_enrollment", new HouseholdMemberRegistrationHandler(getActivity()));
        context.formSubmissionRouter().getHandlerMap().put("child_enrollment", new HouseholdMemberRegistrationHandler(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.formSubmissionRouter().getHandlerMap().remove("woman_enrollment");
        context.formSubmissionRouter().getHandlerMap().remove("child_enrollment");
    }

    @Override
    protected void onCreation() {
    }

    @Override
    protected void startRegistration() {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        integ.initiateScan(new ScanType("GROUP", "", null));
    }//end of method

    public void startMemberRegistration(String groupEntityId, CommonPersonObject data) {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        integ.initiateScan(new ScanType<>("MEMBER", groupEntityId, data));
    }

    public void startWomanRegistration(String entityId, CommonPersonObject data) {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        integ.initiateScan(new ScanType("WOMAN", entityId, data));
    }

    public void startChildRegistration(String entityId, CommonPersonObject data) {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        integ.initiateScan(new ScanType("CHILD", entityId, data));
    }

    public void startAncRegistration(String entityId, CommonPersonObject data) {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        integ.initiateScan(new ScanType("ANC", entityId, data));
    }

    @Override
    protected void onResumption() {
        ImageView filterView = (ImageView) mView.findViewById(org.ei.opensrp.core.R.id.filter_selection);
        promptHH = VaccinatorUtils.makePromptable(getActivity(), filterView, R.mipmap.qr_code_missing, "Enter Identifier", "Ok", "\\d{14}+", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(getClass().getName(), "PROMPT VALUE " + promptHH.inputValue());
                onQRCodeSucessfullyScanned(promptHH.inputValue(), "GROUP", null, null);
            }
        });

        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.btn_report_month).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.label_village).setVisibility(View.GONE);

        ImageView imv = ((ImageView) mView.findViewById(org.ei.opensrp.core.R.id.register_client));
        imv.setImageResource(R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }//end of method

    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "family_registration";
    }

    protected String getMemberRegistrationForm() {
        return "new_member_registration";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getClass().getName(), "REQUEST COODE " + requestCode);
        Log.i(getClass().getName(), "Result Code " + resultCode);

        if (requestCode == BarcodeIntentIntegrator.REQUEST_CODE) {
            BarcodeIntentResult res = integ.parseActivityResult(requestCode, resultCode, data);
            if (StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents(), res.getScanType().getType(), res.getScanType().getId(), (CommonPersonObject) res.getScanType().getData());
            } else Log.i("", "NO RESULT FOR QR CODE");
        }
    }//end of the method

    private void startGroupEnrollmentForm(HashMap<String, String> overrides) {
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    private void startNewMemberEnrollmentForm(HashMap<String, String> overrides, CommonPersonObject client) {
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getMemberRegistrationForm(), "", overrides);//todo check if entity ids are assigned correctly
    }

    private void startWomanEnrollmentForm(final String entityId, final HashMap<String, String> overrides) {
        overrides.putAll(VaccinatorUtils.providerDetails());
        Log.v(getClass().getName(), "Enrolling woman with id " + entityId);
        startForm("woman_enrollment", entityId, overrides);
    }

    private void startChildEnrollmentForm(final String entityId, final HashMap<String, String> overrides) {
        overrides.putAll(VaccinatorUtils.providerDetails());
        Log.v(getClass().getName(), "Enrolling child with id " + entityId);
        startForm("child_enrollment", entityId, overrides);
    }

    public void startAncEnrollmentForm(final String entityId, final HashMap<String, String> overrides) {
        overrides.putAll(VaccinatorUtils.providerDetails());
        Log.v(getClass().getName(), "Enrolling anc with id " + entityId);
        startForm("anc_visit_form", entityId, overrides);
    }


    private void onQRCodeSucessfullyScanned(String qrCode, String entityType, String linkId, CommonPersonObject data) {
        String hhId = findHouseholdOrPersonWithId(qrCode);
        if (!qrCode.matches("\\d+")) {
            Toast.makeText(getActivity(), "QR Code should be digits only", Toast.LENGTH_LONG).show();
            return;
        }
        if (hhId != null) {
            Toast.makeText(getActivity(), "Found a household with someone occupying the given ID", Toast.LENGTH_LONG).show();
            onFilterManual(qrCode);
            return;
        }

        CommonPersonObject child = vaccinatorTables(qrCode, "pkchild");
        if (child != null && entityType.equalsIgnoreCase("MEMBER") == false) {
            Toast.makeText(getActivity(), "Found a Child already registered for given ID with no Household information. Search in Child register", Toast.LENGTH_LONG).show();
            return;
        }
        CommonPersonObject woman = vaccinatorTables(qrCode, "pkwoman");
        if (woman != null && entityType.equalsIgnoreCase("MEMBER") == false) {
            Toast.makeText(getActivity(), "Found a Woman already registered for given ID with no Household information. Search in Woman register", Toast.LENGTH_LONG).show();
            return;
        }
        CommonPersonObject anc = vaccinatorTables(qrCode, "pkanc");
        if (anc != null && entityType.equalsIgnoreCase("MEMBER") == false) {
            Toast.makeText(getActivity(), "Found a Woman already registered for given ID with no Household information. Search in Anc register", Toast.LENGTH_LONG).show();
            return;
        }

        //todo search in clientDB

        if (VaccinatorUtils.providerRolesList().toLowerCase().contains("vaccinator")
                && entityType.equalsIgnoreCase("WOMAN") == false
                && entityType.equalsIgnoreCase("CHILD") == false
                && entityType.equalsIgnoreCase("ANC") == false) {
            Toast.makeText(getActivity(), "No household member found associated with given ID. Search in corresponding register", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        if (entityType.equalsIgnoreCase("MEMBER")) {
            if (woman != null && child != null) {
                Toast.makeText(getActivity(), "Given ID found associated with a child and a woman. Data is inconsistent. Search in corresponding register", Toast.LENGTH_LONG).show();
                return;
            }
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            Map<String, String> m = memberRegistrationOverrides(data, woman != null ? woman : child, filterHouseholdMembers(data.getColumnmaps().get("household_id")));
            map.putAll(m);

            startNewMemberEnrollmentForm(map, data);
        } else if (entityType.equalsIgnoreCase("WOMAN")) {//todo what about offsite enrollment
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);
            map.put("gender", "female");

            CommonPersonObject memberData = data;

            Log.v(getClass().getName(), "Going to Filter HH " + data.getColumnmaps());

            CommonPersonObject hhData = filterHousehold(memberData.getRelationalId()).get(0);
            map.put("first_name", getValue(memberData.getColumnmaps(), "first_name", false));
            map.put("birth_date", getValue(memberData.getColumnmaps(), "dob", false));
            map.put("contact_phone_number", getValue(memberData.getColumnmaps(), "contact_phone_number", false));
            map.put("ethnicity", getValue(memberData.getColumnmaps(), "ethnicity", false));
            map.put("ethnicity_other", getValue(memberData.getColumnmaps(), "ethnicity_other", false));
            map.put("province", getValue(hhData.getColumnmaps(), "province", false));
            map.put("city_village", getValue(hhData.getColumnmaps(), "city_village", false));
            map.put("town", getValue(hhData.getColumnmaps(), "town", false));
            map.put("union_council", getValue(hhData.getColumnmaps(), "union_council", false));
            map.put("address1", getValue(hhData.getColumnmaps(), "address1", false));

            if (memberData.getColumnmaps().get("relationship") != null) {
                if (memberData.getColumnmaps().get("relationship").equalsIgnoreCase("spjouse")
                        || memberData.getColumnmaps().get("relationship").equalsIgnoreCase("husband")
                        || memberData.getColumnmaps().get("relationship").equalsIgnoreCase("wife")) {
                    map.put("husband_name", hhData.getColumnmaps().get("first_name"));
                    map.put("marriage", "yes");
                }
            }


            // For filtering data after FS
            map.put("household_id", hhData.getColumnmaps().get("household_id"));

            startWomanEnrollmentForm(linkId, map);
        } else if (entityType.equalsIgnoreCase("CHILD")) {//todo what about offsite enrollment
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            CommonPersonObject memberData = data;
            CommonPersonObject hhData = filterHousehold(memberData.getRelationalId()).get(0);
            map.put("first_name", getValue(memberData.getColumnmaps(), "first_name", false));
            map.put("gender", memberData.getColumnmaps().get("gender"));
            map.put("birth_date", getValue(memberData.getColumnmaps(), "dob", false));
            map.put("contact_phone_number", getValue(memberData.getColumnmaps(), "contact_phone_number", false));
            map.put("ethnicity", getValue(memberData.getColumnmaps(), "ethnicity", false));
            map.put("ethnicity_other", getValue(memberData.getColumnmaps(), "ethnicity_other", false));
            map.put("province", getValue(hhData.getColumnmaps(), "province", false));
            map.put("city_village", getValue(hhData.getColumnmaps(), "city_village", false));
            map.put("town", getValue(hhData.getColumnmaps(), "town", false));
            map.put("union_council", getValue(hhData.getColumnmaps(), "union_council", false));
            map.put("address1", getValue(hhData.getColumnmaps(), "address1", false));
            // For filtering data after FS
            map.put("household_id", hhData.getColumnmaps().get("household_id"));

            startChildEnrollmentForm(linkId, map);
        } else if (entityType.equalsIgnoreCase("ANC")) {//todo what about offsite enrollment
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            CommonPersonObject memberData = data;
            CommonPersonObject hhData = filterHousehold(memberData.getRelationalId()).get(0);
            map.put("first_name", getValue(memberData.getColumnmaps(), "first_name", false));
            map.put("gender", memberData.getColumnmaps().get("gender"));
            map.put("birth_date", getValue(memberData.getColumnmaps(), "dob", false));
            map.put("contact_phone_number", getValue(memberData.getColumnmaps(), "contact_phone_number", false));
            map.put("ethnicity", getValue(memberData.getColumnmaps(), "ethnicity", false));
            map.put("ethnicity_other", getValue(memberData.getColumnmaps(), "ethnicity_other", false));
            map.put("province", getValue(hhData.getColumnmaps(), "province", false));
            map.put("city_village", getValue(hhData.getColumnmaps(), "city_village", false));
            map.put("town", getValue(hhData.getColumnmaps(), "town", false));
            map.put("union_council", getValue(hhData.getColumnmaps(), "union_council", false));
            map.put("address1", getValue(hhData.getColumnmaps(), "address1", false));
            // For filtering data after FS
            map.put("household_id", hhData.getColumnmaps().get("household_id"));

            map.put("existing_household_id", getValue(memberData.getColumnmaps(), "household_id", true));
            map.put("existing_full_address", getValue(memberData.getDetails(), "address", true));//
            map.put("existing_first_name", getValue(memberData.getColumnmaps(), "first_name", true));
            map.put("existing_father_name", getValue(memberData.getColumnmaps(), "father_name", true));//
            map.put("existing_husband_name", getValue(memberData.getColumnmaps(), "husband_name", true));//

            map.put("existing_birth_date", convertDateFormat(getValue(memberData.getColumnmaps(), "dob", true), true));
            map.put("existing_age", String.valueOf(ageInYears(memberData, "dob", ByColumnAndByDetails.byColumn, true)));
            map.put("existing_ethnicity", getValue(memberData.getColumnmaps(), "ethnicity", true));

            startAncEnrollmentForm(linkId, map);
        } else {
            map.put("household_id", qrCode);
            map.put("town", getValue(VaccinatorUtils.providerDetails(), "provider_town", false));
            map.put("union_council", getValue(VaccinatorUtils.providerDetails(), "provider_uc", false));

            startGroupEnrollmentForm(map);
        }
    }

    @Override
    public RegisterDataLoaderHandler loaderHandler() {
        if (loaderHandler == null) {
            loaderHandler = new RegisterDataCursorLoaderHandler(getActivity(),
                    new RegisterQuery(bindType(), "id", "pkindividual", "relationalid", bindType() + ".id",
                            Arrays.asList(new String[]
                                    {"count(pkindividual.id) registeredMembers",
                                            "SUM(CASE WHEN julianday(DATETIME('now'))-julianday(pkindividual.dob) < 365*5 THEN 1 ELSE 0 END) children",
                                            "SUM(CASE WHEN julianday(DATETIME('now'))-julianday(pkindividual.dob) BETWEEN 365*15 AND 365*49 AND pkindividual.gender IN ('female', 'f') THEN 1 ELSE 0 END) women"}), null).limitAndOffset(7, 0),
                    new RegisterCursorAdapter(getActivity(), clientsProvider()));
        }
        return loaderHandler;
    }

    @Override
    protected RegisterClientsProvider clientsProvider() {
        return new HouseholdSmartClientsProvider(getActivity(), clientActionHandler, context.alertService());
    }

    private List<CommonPersonObject> filterHousehold(String filterString) {
        Log.v(getClass().getName(), "Filtering HH " + filterString);
        return RegisterRepository.queryData(bindType(), null, new HouseholdIDSearchOption(filterString).getCriteria(), null, null);
    }

    private List<CommonPersonObject> filterHouseholdMembers(String householdId) {
        String memberExistQuery = "select * from pkindividual where household_id = '" + householdId + "' ";

        return context.allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(memberExistQuery, new String[]{}, "pkindividual");
    }

    private String findHouseholdOrPersonWithId(String id) {
        String sql = "SELECT MAX(household_id) FROM (select h.household_id, 'HHH' from pkhousehold h where h.household_id='" + id + "' OR h.program_client_id = '" + id + "' OR h.household_member_id = '" + id + "' " +
                " union " +
                " select i.household_id, 'MEMBER' from pkindividual i where i.household_id='" + id + "' OR i.program_client_id = '" + id + "' OR i.household_member_id = '" + id + "') e ";

        List<String> res = context.commonrepository("pkindividual").findSearchIds(sql);
        return res.size() > 0 ? res.get(0) : null;
    }

    private Integer personsInVaccinatorTableWithId(String id) {
        String sql = "SELECT SUM(c) FROM (SELECT count(1) c from pkwoman w where w.program_client_id = '" + id + "' " +
                " union " +
                " SELECT count(1) c from pkchild c where c.program_client_id = '" + id + "' ) e ";

        ArrayList<HashMap<String, String>> res = context.commonrepository("pkindividual").rawQuery(sql);
        return res.size() > 0 ? Integer.parseInt(res.get(0).get("c")) : null;
    }

    public CommonPersonObject filterHouseholdMember(String hhMemberId) {
        String memberExistQuery = "select * from pkindividual where program_client_id = '" + hhMemberId + "' " +
                " OR id = '" + hhMemberId + "' OR household_member_id = '" + hhMemberId + "'";

        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(memberExistQuery, new String[]{}, "pkindividual");
        CommonPersonObject householdMember;
        if (memberData.size() == 0) {
            return null;
        } else {
            householdMember = memberData.get(0);
            String householdId = householdMember.getColumnmaps().get("household_id");
            setCurrentSearchFilter(new HouseholdIDSearchOption(householdId));
            onFilterManual(householdId);
        }

        return householdMember;
    }

    public CommonPersonObject vaccinatorTables(String qrCode, String entity) {

        String q = "select * from " + entity + " where program_client_id = " + qrCode;
        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects(entity).customQueryForCompleteRow(q, new String[]{}, entity);
        if (memberData.size() == 0) {
            return null;
        }
        return memberData.get(0);
    }

    private class ClientActionHandler implements View.OnClickListener {
        private HouseholdSmartRegisterFragment householdSmartRegisterFragment;

        public ClientActionHandler() {
            this.householdSmartRegisterFragment = householdSmartRegisterFragment;
        }

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.household_profile_info_layout) {
                ((RegisterActivity) getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), false);

            } else if (i == R.id.household_add_member) {// change the below contains value according to your requirement
                //if(!Utils.userRoles.contains("Vaccinator")) {
                final CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LinearLayout ly = new LinearLayout(getActivity());
                ly.setOrientation(LinearLayout.VERTICAL);

                final RadioButton hasQRCode = new RadioButton(getActivity());
                final RadioButton hasPRId = new RadioButton(getActivity());
                final RadioButton noQRCode = new RadioButton(getActivity());
                hasQRCode.setText("Yes, member has a QR code ID. Scan!");
                hasPRId.setText("Yes, has a Program ID. Enter!");
                noQRCode.setText("No, member doesnot have ID");

                RadioGroup rG = new RadioGroup(getActivity());
                rG.setPadding(10, 10, 10, 5);
                rG.addView(hasQRCode);
                rG.addView(hasPRId);
                rG.addView(noQRCode);

                final LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView memberCodeQuestion = new TextView(getActivity());
                memberCodeQuestion.setText("Has this member ever been registered in any other OpenSRP program and assigned a QR / Program ID ?");
                memberCodeQuestion.setTextSize(20);
                layout.addView(memberCodeQuestion);

                ly.addView(layout);
                ly.addView(rG);

                builder.setView(ly);

                final AlertDialog alertDialog = builder.setPositiveButton("OK", null).setNegativeButton("Cancel", null).create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (noQRCode.isChecked()) {
                                    HashMap<String, String> map = new HashMap<>();
                                    Map<String, String> m = memberRegistrationOverrides(convertToCommonPersonObject(client), null, filterHouseholdMembers(client.getColumnmaps().get("household_id")));
                                    map.putAll(m);
                                    startNewMemberEnrollmentForm(map, convertToCommonPersonObject(client));
                                } else if (hasPRId.isChecked()) {
                                    promptMember = VaccinatorUtils.getPrompt(getActivity(), "Enter Identifier", "Ok", "\\d+", true, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.v(getClass().getName(), "PROMPT VALUE " + promptHH.inputValue());
                                            onQRCodeSucessfullyScanned(promptMember.inputValue(), "MEMBER", null, convertToCommonPersonObject(client));
                                        }
                                    });
                                    promptMember.show();
                                } else if (hasQRCode.isChecked()) {
                                    startMemberRegistration(client.entityId(), convertToCommonPersonObject(client));
                                }

                                dialog.dismiss();
                            }
                        });

                    }
                });
                alertDialog.show();
                //}

            }
        }
    }//end of method

    private Map<String, String> memberRegistrationOverrides(CommonPersonObject client
            , CommonPersonObject existingClient, List<CommonPersonObject> otherMembers) {
        Map<String, String> map = new HashMap<>();

        map.put("relationalid", client.getCaseId());
        map.put("existing_full_name_hhh", getValue(client.getColumnmaps(), "first_name", true));
        map.put("existing_household_id", getValue(client.getColumnmaps(), "household_id", true));
        map.put("existing_num_members", (otherMembers.size() + 1) + "");
        map.put("existing_num_household_members", getValue(client.getColumnmaps(), "num_household_members", false));

        map.put("province", getValue(client.getColumnmaps(), "province", false));
        map.put("city_village", getValue(client.getColumnmaps(), "city_village", false));
        map.put("town", getValue(client.getColumnmaps(), "town", false));
        map.put("union_council", getValue(client.getColumnmaps(), "union_council", false));
        map.put("address1", getValue(client.getColumnmaps(), "address1", false));

        map.put("existing_full_address", getValue(client.getColumnmaps(), "address1", true)
                + ", UC: " + getValue(client.getColumnmaps(), "union_council", true).replace("Uc", "UC")
                + ", Town: " + getValue(client.getColumnmaps(), "town", true)
                + ", City: " + getValue(client.getColumnmaps(), "city_village", true)
                + ", Province: " + getValue(client.getColumnmaps(), "province", true));

        if (existingClient != null) {
            map.put("first_name", getValue(existingClient.getColumnmaps(), "first_name", false));
            map.put("gender", existingClient.getColumnmaps().get("gender"));
            map.put("birth_date", getValue(existingClient.getColumnmaps(), "dob", false));
            map.put("contact_phone_number", getValue(existingClient.getColumnmaps(), "contact_phone_number", false));
            map.put("ethnicity", getValue(existingClient.getDetails(), "ethnicity", false));
            map.put("ethnicity_other", getValue(existingClient.getDetails(), "ethnicity_other", false));
        }
        return map;
    }
}
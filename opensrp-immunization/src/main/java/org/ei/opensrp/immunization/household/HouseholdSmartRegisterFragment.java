package org.ei.opensrp.immunization.household;

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
import org.ei.opensrp.core.template.NavBarOptionsProvider;
import org.ei.opensrp.core.template.RegisterActivity;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.template.SearchType;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.template.SortingOption;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.immunization.application.common.VaccinationServiceModeOption;
import org.ei.opensrp.immunization.handler.HouseholdMemberRegistrationHandler;
import org.ei.opensrp.util.IntegerUtil;
import org.ei.opensrp.util.VaccinatorUtils;
import org.ei.opensrp.util.barcode.Barcode;
import org.ei.opensrp.util.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.util.barcode.BarcodeIntentResult;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.*;

public class HouseholdSmartRegisterFragment extends RegisterDataGridFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private ScanType currentScanType;
    private RegisterDataLoaderHandler loaderHandler;
    private PromptView promptHH;
    private PromptView promptMember;

    private class ScanType{
        final String type;
        final String id;
        final CommonPersonObject data;

        public ScanType(String type, String id, CommonPersonObject data){
            this.type = type;
            this.id = id;
            this.data = data;
        }

        @Override
        public String toString() {
            return type+":"+id+"::"+data;
        }
    }

    public HouseholdSmartRegisterFragment() {
        super(null);
    }

    @SuppressLint("ValidFragment")
    public HouseholdSmartRegisterFragment(FormController householdFormController) {
        super(householdFormController);
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
                        R.string.household_profile , R.string.household_members, R.string.household_address, R.string.household_contactNumber, R.string.household_add_member
                }, new int[]{6,2,3,2,1});
            }
            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
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
    protected void onInitialization(){
        context.formSubmissionRouter().getHandlerMap().put("woman_enrollment", new HouseholdMemberRegistrationHandler(getActivity()));
        context.formSubmissionRouter().getHandlerMap().put("child_enrollment", new HouseholdMemberRegistrationHandler(getActivity()));
    }

    @Override
    protected void onCreation() {}

    @Override
    protected void startRegistration() {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        currentScanType = new ScanType("GROUP", "", null);
        integ.initiateScan();
    }//end of method

    public void startMemberRegistration(String groupEntityId, CommonPersonObject data) {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        currentScanType = new ScanType("MEMBER", groupEntityId, data);
        integ.initiateScan();
    }

    public void startWomanRegistration(String entityId, CommonPersonObject data) {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        currentScanType = new ScanType("WOMAN", entityId, data);
        integ.initiateScan();
    }

    public void startChildRegistration(String entityId, CommonPersonObject data) {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);

        currentScanType = new ScanType("CHILD", entityId, data);
        integ.initiateScan();
    }

    @Override
    protected void onResumption() {
        ImageView filterView = (ImageView) mView.findViewById(org.ei.opensrp.core.R.id.filter_selection);
        promptHH = VaccinatorUtils.makePromptable(getActivity(), filterView, R.mipmap.qr_code_missing, "Enter Identifier", "Ok", "\\d+", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(getClass().getName(), "PROMPT VALUE "+promptHH.inputValue());
                onQRCodeSucessfullyScanned(promptHH.inputValue(), "GROUP", null, null);
            }
        });

        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.btn_report_month).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.label_village).setVisibility(View.GONE);

        ImageView imv = ((ImageView)mView.findViewById(org.ei.opensrp.core.R.id.register_client));
        imv.setImageResource(R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }//end of method

    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "family_registration";
    }

    protected String getMemberRegistrationForm(){return "new_member_registration";}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(getClass().getName(), "REQUEST COODE " + requestCode);
        Log.i(getClass().getName(), "Result Code " + resultCode);
        Log.i(getClass().getName(), "currentScanType " + currentScanType);

        if(requestCode == BarcodeIntentIntegrator.REQUEST_CODE) {
            BarcodeIntentResult res = BarcodeIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents(), currentScanType.type, currentScanType.id, currentScanType.data);
            }
            else Log.i("", "NO RESULT FOR QR CODE");
        }
    }//end of the method

    private void startGroupEnrollmentForm(HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    private void startNewMemberEnrollmentForm(HashMap<String, String> overrides, CommonPersonObject client){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getMemberRegistrationForm(), "", overrides);//todo check if entity ids are assigned correctly
    }

    private void startWomanEnrollmentForm(final String entityId, final HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        Log.v(getClass().getName(), "Enrolling woman with id "+entityId);
        startForm("woman_enrollment", entityId, overrides);
    }

    private void startChildEnrollmentForm(final String entityId, final HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        Log.v(getClass().getName(), "Enrolling child with id "+entityId);
        startForm("child_enrollment", entityId, overrides);
    }

    private void onQRCodeSucessfullyScanned(String qrCode, String entityType, String linkId, CommonPersonObject data) {
        String hhId = findHouseholdOrPersonWithId(qrCode);
        if(hhId != null){
            Toast.makeText(getActivity(), "Found a household with someone occupying the given ID", Toast.LENGTH_LONG).show();
            onFilterManual(qrCode);
            return;
        }

        CommonPersonObject child = vaccinatorTables(qrCode, "pkchild");
        if(child != null && entityType.equalsIgnoreCase("MEMBER") == false){
            Toast.makeText(getActivity(), "Found a Child already registered for given ID with no Household information. Search in Child register", Toast.LENGTH_LONG).show();
            return;
        }
        CommonPersonObject woman = vaccinatorTables(qrCode, "pkwoman");
        if(woman != null && entityType.equalsIgnoreCase("MEMBER") == false){
            Toast.makeText(getActivity(), "Found a Woman already registered for given ID with no Household information. Search in Woman register", Toast.LENGTH_LONG).show();
            return;
        }

        //todo search in clientDB

        if (VaccinatorUtils.providerRolesList().toLowerCase().contains("vaccinator")
                && entityType.equalsIgnoreCase("WOMAN") == false
                && entityType.equalsIgnoreCase("CHILD") == false){
            Toast.makeText(getActivity(), "No household member found associated with given ID. Search in corresponding register", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String,String> map = new HashMap<>();

        if (entityType.equalsIgnoreCase("MEMBER")){
            if(woman != null && child != null){
                Toast.makeText(getActivity(), "Given ID found associated with a child and a woman. Data is inconsistent. Search in corresponding register", Toast.LENGTH_LONG).show();
                return;
            }
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            Map<String, String> m = memberRegistrationOverrides(data, woman != null?woman:child, filterHouseholdMembers(data.getColumnmaps().get("household_id")));
            map.putAll(m);

            startNewMemberEnrollmentForm(map, data);
        }
        else if(entityType.equalsIgnoreCase("WOMAN")){//todo what about offsite enrollment
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);
            map.put("gender", "female");

            CommonPersonObject memberData = data;
            CommonPersonObject hhData = filterHousehold(memberData.getColumnmaps().get("household_id")).get(0);
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
            if(memberData.getColumnmaps().get("relationship").equalsIgnoreCase("spouse")
                    || memberData.getColumnmaps().get("relationship").equalsIgnoreCase("husband")
                    || memberData.getColumnmaps().get("relationship").equalsIgnoreCase("wife")) {
                map.put("husband_name", hhData.getColumnmaps().get("first_name"));
                map.put("marriage", "yes");
            }

            // For filtering data after FS
            map.put("household_id", hhData.getColumnmaps().get("household_id"));

            startWomanEnrollmentForm(linkId, map);
        }
        else if(entityType.equalsIgnoreCase("CHILD")){//todo what about offsite enrollment
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            CommonPersonObject memberData = data;
            CommonPersonObject hhData = filterHousehold(memberData.getColumnmaps().get("household_id")).get(0);
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
        }
        else {
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
                    new RegisterQuery(bindType(), "id", "pkindividual", "relationalid", bindType()+".id",
                            Arrays.asList(new String[]
                                    {"count(pkindividual.id) registeredMembers",
                                    "SUM(CASE WHEN julianday(DATETIME('now'))-julianday(pkindividual.dob) < 365*5 THEN 1 ELSE 0 END) children",
                                    "SUM(CASE WHEN julianday(DATETIME('now'))-julianday(pkindividual.dob) BETWEEN 365*15 AND 365*49 AND pkindividual.gender IN ('female', 'f') THEN 1 ELSE 0 END) women"}), null).limitAndOffset(5, 0),
                    new RegisterCursorAdapter(getActivity(), clientsProvider()));
        }
        return loaderHandler;
    }

    @Override
    protected RegisterClientsProvider clientsProvider() {
        return new HouseholdSmartClientsProvider(getActivity(), clientActionHandler, context.alertService());
    }

    private List<CommonPersonObject> filterHousehold(String filterString) {
        return RegisterRepository.queryData(bindType(), null, new HouseholdIDSearchOption(filterString).getCriteria(), null, null);
    }

    private List<CommonPersonObject> filterHouseholdMembers(String householdId) {
        String memberExistQuery = "select * from pkindividual where household_id = '"+householdId+"' ";

        return context.allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(memberExistQuery, new String[]{}, "pkindividual");
    }

    private String findHouseholdOrPersonWithId(String id) {
        String sql = "SELECT MAX(household_id) FROM (select h.household_id, 'HHH' from pkhousehold h where h.household_id='"+id+"' OR h.program_client_id = '"+id+"' OR h.household_member_id = '"+id+"' " +
                " union " +
                " select i.household_id, 'MEMBER' from pkindividual i where i.household_id='"+id+"' OR i.program_client_id = '"+id+"' OR i.household_member_id = '"+id+"') e ";

        List<String> res = context.commonrepository("pkindividual").findSearchIds(sql);
        return res.size()>0?res.get(0):null;
    }

    private Integer personsInVaccinatorTableWithId(String id) {
        String sql = "SELECT SUM(c) FROM (SELECT count(1) c from pkwoman w where w.program_client_id = '"+id+"' " +
                " union " +
                " SELECT count(1) c from pkchild c where c.program_client_id = '"+id+"' ) e ";

        ArrayList<HashMap<String, String>> res = context.commonrepository("pkindividual").rawQuery(sql);
        return res.size()>0? Integer.parseInt(res.get(0).get("c")):null;
    }

    public CommonPersonObject filterHouseholdMember(String hhMemberId){
        String memberExistQuery = "select * from pkindividual where program_client_id = '"+hhMemberId+"' " +
                " OR id = '"+hhMemberId+"' OR household_member_id = '"+hhMemberId+"'";

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

    public CommonPersonObject vaccinatorTables(String qrCode, String entity){
        String q = "select * from "+entity+" where program_client_id = " + qrCode;
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
            switch (view.getId()) {
                case R.id.household_profile_info_layout:
                    ((RegisterActivity)getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), false);
                    break;
                case R.id.household_add_member:
                    // change the below contains value according to your requirement
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
                                    }
                                    else if (hasPRId.isChecked()){
                                        promptMember = VaccinatorUtils.getPrompt(getActivity(), "Enter Identifier", "Ok", "\\d+", true, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.v(getClass().getName(), "PROMPT VALUE " + promptHH.inputValue());
                                                onQRCodeSucessfullyScanned(promptMember.inputValue(), "MEMBER", null, convertToCommonPersonObject(client));
                                            }
                                        });
                                        promptMember.show();
                                    }
                                    else if (hasQRCode.isChecked()) {
                                        startMemberRegistration(client.entityId(), convertToCommonPersonObject(client));
                                    }

                                    dialog.dismiss();
                                }
                            });

                        }
                    });
                    alertDialog.show();
                    //}
                    break;
            }
        }
    }//end of method

    private Map<String, String> memberRegistrationOverrides(CommonPersonObject client
            , CommonPersonObject existingClient, List<CommonPersonObject> otherMembers){
        Map<String, String> map = new HashMap<>();

        map.put("relationalid", client.getCaseId());
        map.put("existing_full_name_hhh", getValue(client.getColumnmaps(), "first_name", true));
        map.put("existing_household_id", getValue(client.getColumnmaps(), "household_id", true));
        map.put("existing_num_members", (otherMembers.size()+1)+"");
        map.put("existing_num_household_members", getValue(client.getColumnmaps(), "num_household_members", false));

        map.put("province", getValue(client.getColumnmaps(), "province", false));
        map.put("city_village", getValue(client.getColumnmaps(), "city_village", false));
        map.put("town", getValue(client.getColumnmaps(), "town", false));
        map.put("union_council", getValue(client.getColumnmaps(), "union_council", false));
        map.put("address1", getValue(client.getColumnmaps(), "address1", false));

        map.put("existing_full_address", getValue(client.getColumnmaps(), "address1", true)
                +", UC: "+ getValue(client.getColumnmaps(), "union_council", true).replace("Uc", "UC")
                +", Town: "+ getValue(client.getColumnmaps(), "town", true)
                +", City: "+ getValue(client.getColumnmaps(), "city_village", true)
                +", Province: "+ getValue(client.getColumnmaps(), "province", true));

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
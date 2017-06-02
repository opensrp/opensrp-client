package com.example.opensrp_anc.anc;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.opensrp_anc.R;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.handler.RegisterDataCursorLoaderHandler;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
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
import org.ei.opensrp.core.utils.barcode.Barcode;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentResult;
import org.ei.opensrp.core.utils.barcode.ScanType;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.core.widget.RegisterCursorAdapter;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.HashMap;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.providerDetails;
import static org.ei.opensrp.core.utils.Utils.getValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class ANCSmartRegisterFragment extends RegisterDataGridFragment {


    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private RegisterDataLoaderHandler loaderHandler;
    private final BarcodeIntentIntegrator integ;
    private PromptView prompt;

    public ANCSmartRegisterFragment() {
        super(null);
        integ = BarcodeIntentIntegrator.initBarcodeScanner(this);;
    }

    @SuppressLint("ValidFragment")
    public ANCSmartRegisterFragment(FormController formController) {
        super(formController);
        integ = BarcodeIntentIntegrator.initBarcodeScanner(this);
    }

    @Override
    public String bindType() {
        return "pkanc";
    }

    @Override
    public RegisterDataLoaderHandler loaderHandler() {
        if (loaderHandler == null){
            loaderHandler = new RegisterDataCursorLoaderHandler(getActivity(),
                    new RegisterQuery("pkanc", "id", null, null).limitAndOffset(5, 0),
                    new RegisterCursorAdapter(getActivity(), clientsProvider()));
        }
        return loaderHandler;
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public SearchFilterOption searchFilterOption() {
                return new SearchFilterOption() {
                    public String filter;

                    @Override
                    public String getFilter() {
                        return filter;
                    }
                    @Override
                    public void setFilter(String filter) {
                        this.filter = filter;
                    }

                    @Override
                    public String getCriteria() {
                       return " program_client_id = '"+filter+"' OR contact_phone_number LIKE '%"+filter+"%'";
                    }

                    @Override
                    public boolean filter(SmartRegisterClient client) {
                        return false;
                    }
                    @Override
                    public String name() {
                        return "Search";
                    }
                };
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new AncServiceModeOption(null,"ANC",
                        new int[]{R.string.anc_profile,R.string.birthdate_age,R.string.contact,R.string.edd_ga,R.string.comments},
                        new int[]{7,4,3,4,3}
                );
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortingOption sortOption() {
                return new CommonSortingOption(getResources().getString(R.string.anc_alphabetical_sort), "existing_program_client_id desc");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.anc_register_title);
            }

            @Override
            public SearchType searchType() {
                return SearchType.PASSIVE;
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {
            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonSortingOption(getResources().getString(R.string.anc_alphabetical_sort), "existing_first_name")
                };
               }

            @Override
            public String searchHint() {
                return getString(R.string.anc_search);
            }
        };
    }

    @Override
    protected RegisterClientsProvider clientsProvider() {
        return new ANCSmartClientsProvider(getActivity(),clientActionHandler,context.alertService());
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {
      //  HashMap<String, String> map = new HashMap<>();
       // startForm("anc_visit_form", " ", map);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        integ.initiateScan(new ScanType("GROUP", "", null));
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(getClass().getName(), "REQUEST COODE " + requestCode);
        Log.i(getClass().getName(), "Result Code " + resultCode);

        if(requestCode == BarcodeIntentIntegrator.REQUEST_CODE) {
            BarcodeIntentResult res = integ.parseActivityResult(requestCode, resultCode, data);
            if(StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents(), res.getScanType().getType(), res.getScanType().getId(), (ClientEvent) res.getScanType().getData());
            }
            else Log.i("", "NO RESULT FOR QR CODE");
        }
    }

    private void onQRCodeSucessfullyScanned(String code, String type, String id, ClientEvent data) {
        HashMap<String,String> map = new HashMap<>();
        map.put("existing_program_client_id", code);
        map.put("program_client_id", code);
        map.putAll(providerDetails());
        startForm("anc_visit_form",id, map);
    }

    @Override
    protected void onCreation() {

    }

    private class ClientActionHandler implements View.OnClickListener {

        private ANCSmartRegisterFragment ancSmartRegisterFragment;

        public ClientActionHandler() {
            this.ancSmartRegisterFragment = ancSmartRegisterFragment;
        }
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.child_profile_info_layout) {
                ((RegisterActivity) getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), false);

            }
            else if (i == R.id.open_test_form1) {
                HashMap<String, String> map = new HashMap<>();
                CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                map.putAll(followupOverrides(client));
                map.putAll(providerDetails());
                Toast.makeText(getActivity(), "Open test Form", Toast.LENGTH_LONG).show();
                startForm("anc_visit_form", ((SmartRegisterClient) view.getTag()).entityId(), map);

            }
        }
    }
    private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_full_address", getValue(client.getColumnmaps(), "address1", true)
                +", UC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", Town: "+ getValue(client.getColumnmaps(), "town", true)
                +", City: "+ getValue(client, "city_village", true)
                +", Province: "+ getValue(client, "province", true));
        map.put("existing_program_client_id", getValue(client.getColumnmaps(), "program_client_id", false));
        map.put("program_client_id", getValue(client.getColumnmaps(), "program_client_id", false));

        int days = 0;
        try{
            days = Days.daysBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getDays();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        map.put("existing_first_name", getValue(client.getColumnmaps(), "first_name", true));
        map.put("existing_last_name", getValue(client.getColumnmaps(), "last_name", true));
        map.put("existing_gender", getValue(client.getColumnmaps(), "gender", true));
        map.put("existing_mother_name", getValue(client.getColumnmaps(), "mother_name", true));
        map.put("existing_father_name", getValue(client.getColumnmaps(), "father_name", true));
        map.put("existing_birth_date", getValue(client.getColumnmaps(), "dob", false));
        map.put("existing_age", days+"");
        map.put("existing_epi_card_number", getValue(client.getColumnmaps(), "epi_card_number", false));
        map.put("reminders_approval", getValue(client.getColumnmaps(), "reminders_approval", false));
        map.put("contact_phone_number", getValue(client.getColumnmaps(), "contact_phone_number", false));

       // map.putAll(getPreloadVaccineData(client));

        return map;
    }
    @Override
    protected void onResumption() {
        ImageView filterView = (ImageView) mView.findViewById(org.ei.opensrp.core.R.id.filter_selection);
        prompt = VaccinatorUtils.makePromptable(getActivity(), filterView, com.example.opensrp_stock.R.mipmap.qr_code_missing, "Enter Identifier", "Ok", "\\d+", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(getClass().getName(), "PROMPT VALUE "+prompt.inputValue());
                onQRCodeSucessfullyScanned(prompt.inputValue(), "GROUP", null, null);
            }
        });

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.label_village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.btn_report_month).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);

        ImageView imv = ((ImageView)mView.findViewById(org.ei.opensrp.core.R.id.register_client));
        imv.setImageResource(com.example.opensrp_stock.R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

}

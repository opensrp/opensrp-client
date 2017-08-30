package com.example.opensrp_stock.field.util.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.opensrp_stock.R;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.domain.Client;
import org.ei.opensrp.core.db.repository.CESQLiteHelper;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.CommonSortingOption;
import org.ei.opensrp.core.template.DefaultOptionsProvider;
import org.ei.opensrp.core.template.NavBarOptionsProvider;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.core.utils.barcode.Barcode;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentResult;
import org.ei.opensrp.core.widget.PromptView;

import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SmartClientRegisterFragment extends RegisterDataGridFragment {

    private final BarcodeIntentIntegrator integ;
    private PromptView prompt;

    public CESQLiteHelper getClientEventDb() {
        return ceDb;
    }

    private CESQLiteHelper ceDb;

    public SmartClientRegisterFragment(FormController formController) {
        super(formController);
        integ = BarcodeIntentIntegrator.initBarcodeScanner(this);
    }

    @Override
    protected abstract DefaultOptionsProvider getDefaultOptionsProvider() ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ceDb = CESQLiteHelper.getClientEventDb(activity);
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
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonSortingOption(getResources().getString(R.string.woman_alphabetical_sort), "first_name"),
                        new CommonSortingOption("Age (DESC)", "dob"),
                        new CommonSortingOption("Age (ASC)", "dob DESC"),
                        new CommonSortingOption(getResources().getString(R.string.id_sort), "program_client_id")
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.search_hint);
            }
        };
    }//end of method

    @Override
    protected abstract RegisterClientsProvider clientsProvider() ;

    @Override
    protected abstract void onInitialization() ;

    @Override
    protected void startRegistration() {
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        integ.initiateScan();
    }

    @Override
    protected void onResumption() {
        ImageView filterView = (ImageView) mView.findViewById(org.ei.opensrp.core.R.id.filter_selection);
        prompt = VaccinatorUtils.makePromptable(getActivity(), filterView, R.mipmap.qr_code_missing, "Enter Identifier", "Ok", "\\d{14}+", true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.v(getClass().getName(), "PROMPT VALUE "+prompt.inputValue());
                onQRCodeSucessfullyScanned(prompt.inputValue());
            }
        });
        
        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);
        mView.findViewById(org.ei.opensrp.core.R.id.label_village).setVisibility(View.GONE);
        
        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.btn_report_month).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.core.R.id.village).setVisibility(View.GONE);

        ImageView imv = ((ImageView)mView.findViewById(org.ei.opensrp.core.R.id.register_client));
        imv.setImageResource(R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    protected abstract String getRegistrationForm(HashMap<String, String> overridemap);

    protected abstract String getOAFollowupForm(Client client, HashMap<String, String> overridemap);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("", "REQUEST COODE " + requestCode);
        Log.i("", "Result Coode " + resultCode);
        if(requestCode == BarcodeIntentIntegrator.REQUEST_CODE) {
            BarcodeIntentResult res = integ.parseActivityResult(requestCode, resultCode, data);
            if(StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents());
            }
            else Log.i("", "NO RESULT FOR QR CODE");
        }
    }

    private void startEnrollmentForm(HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    private void startOffSiteFollowupForm(Client client, HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getOAFollowupForm(client, overrides), client.getBaseEntityId(), overrides);
    }

    private void onQRCodeSucessfullyScanned(String qrCode) {
      /* #TODO:after reading the code , app first search for that id in database if he it is there ,
      that client appears  on register only . if it doesnt then it shows two options
       */
        List<CommonPersonObject> fc = getFilteredClients(qrCode);
        if(fc.size() > 0) {
            //do nothing. let user select from filtered data
            onFilterManual(qrCode);
        }
        else {
            Client c = null;
            try{
                c = ceDb.getClient(qrCode);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            if(c != null && getDefaultOptionsProvider().nameInShortFormForTitle().toLowerCase().contains("woman")
                    && c.getBirthdate() != null && Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears() < 8){
                showMessageDialog("Scanned ID already exists and is not a woman.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return;
            }

            if(c != null && getDefaultOptionsProvider().nameInShortFormForTitle().toLowerCase().contains("child")
                    && c.getBirthdate() != null && Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears() >= 8){
                showMessageDialog("Scanned ID already exists and is not a child.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return;
            }

            HashMap<String,String> map = new HashMap<>();
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            HashMap<String, String> pd = VaccinatorUtils.providerDetails();
            map.put("province", pd.get("provider_province").toLowerCase().replace(" ", "_"));
            map.put("city_village", pd.get("provider_city").toLowerCase().replace(" ", "_"));
            map.put("town", pd.get("provider_town").toLowerCase().replace(" ", "_"));

            Map<String, String> m = customFieldOverrides();
            if(m != null){
                map.putAll(m);
            }

            if (c != null){
                startOffSiteFollowupForm(c, map);
            }
            else {
                startEnrollmentForm(map);
            }
        }
    }

    protected abstract Map<String, String> customFieldOverrides();

    private List<CommonPersonObject> getFilteredClients(String filterString) {
        BasicSearchOption.Type t = BasicSearchOption.Type.getByRegisterName(getDefaultOptionsProvider().nameInShortFormForTitle());
        return RegisterRepository.queryData(t.equals(BasicSearchOption.Type.CHILD)?"pkchild":"pkwoman", null, new BasicSearchOption(filterString, t).getCriteria(), null, null);
    }
}
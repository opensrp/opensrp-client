package com.example.opensrp_zm.zm;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.opensrp_stock.field.util.VaccinatorUtils;
import com.example.opensrp_stock.field.util.common.VaccinationServiceModeOption;
import com.example.opensrp_zm.R;
import com.example.opensrp_zm.zm.handler.ZMFormSubmissionHandler;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.core.db.domain.ClientEvent;
import org.ei.opensrp.core.db.handler.RegisterDataCursorLoaderHandler;
import org.ei.opensrp.core.db.handler.RegisterDataLoaderHandler;
import org.ei.opensrp.core.db.utils.CERegisterQuery;
import org.ei.opensrp.core.template.CommonSortingOption;
import org.ei.opensrp.core.template.DefaultOptionsProvider;
import org.ei.opensrp.core.template.FilterOption;
import org.ei.opensrp.core.template.NavBarOptionsProvider;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.core.template.RegisterDataGridFragment;
import org.ei.opensrp.core.template.SearchFilterOption;
import org.ei.opensrp.core.template.SearchType;
import org.ei.opensrp.core.template.ServiceModeOption;
import org.ei.opensrp.core.template.SortingOption;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.core.utils.barcode.BarcodeIntentResult;
import org.ei.opensrp.core.utils.barcode.ScanType;
import org.ei.opensrp.core.widget.CERegisterCursorAdapter;
import org.ei.opensrp.core.widget.PromptView;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;

import java.util.HashMap;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.fillChildVaccineEnrollmentOverrides;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.fillChildVaccineOffsiteOverrides;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.fillWomanTTEnrollmentOverrides;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.fillWomanTTOffsiteOverrides;

public class ZMSmartRegisterFragment extends RegisterDataGridFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private final BarcodeIntentIntegrator integ;
    private RegisterDataCursorLoaderHandler loaderHandler;
    private PromptView prompt;

    public ZMSmartRegisterFragment(){
        super(null);
        this.integ = null;
    }

    @SuppressLint("ValidFragment")
    public ZMSmartRegisterFragment(FormController formController) {
        super(formController);
        integ = BarcodeIntentIntegrator.initBarcodeScanner(this);
    }

    @Override
    public String bindType() {
        return "client";
    }

    @Override
    public RegisterDataLoaderHandler loaderHandler() {
        if (loaderHandler == null){
            loaderHandler = new RegisterDataCursorLoaderHandler(getActivity(),
                    new CERegisterQuery("client.baseEntityId", null).limitAndOffset(7, 0),
                    new CERegisterCursorAdapter(getActivity(), clientsProvider()));
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
                        return "(client.firstName LIKE '"+filter+"%' OR client.attributes LIKE '%"+filter+"%' " +
                                " OR client.identifiers LIKE '%"+filter+"%' " +
                                " OR address.cityVillage LIKE '"+filter+"%' OR address.town LIKE '"+filter+"%' OR address.subTown LIKE '"+filter+"%' " +
                                " OR (obs.formSubmissionField IN ('father_name', 'husband_name', 'contact_phone_number') AND obs.`values` LIKE '%"+filter+"%') )";
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
                return new VaccinationServiceModeOption(null, "", new int[]{
                        R.string.zm_profile , R.string.zm_last_events, R.string.zm_action
                }, new int[]{11,7,4});
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortingOption sortOption() {
                return new CommonSortingOption(getResources().getString(R.string.woman_alphabetical_sort), "firstName");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.zm_register_title);
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
                return new DialogOption[]{
                    new FilterOption() {
                        @Override
                        public String getCriteria() {
                            return "";
                        }

                        @Override
                        public boolean filter(SmartRegisterClient client) {
                            return false;
                        }

                        @Override
                        public String name() {
                            return "None";
                        }
                    },
                    new FilterOption() {
                        @Override
                        public String getCriteria() {
                            return "client.gender LIKE 'F%' AND julianday(DATETIME('now'))-julianday(client.birthdate) BETWEEN 15*365 AND 49*365";
                        }

                        @Override
                        public boolean filter(SmartRegisterClient client) {
                            return false;
                        }

                        @Override
                        public String name() {
                            return "Eligible Women";
                        }
                    },
                    new FilterOption() {
                        @Override
                        public String getCriteria() {
                            return "julianday(DATETIME('now'))-julianday(client.birthdate) BETWEEN 0 AND 5*365";
                        }

                        @Override
                        public boolean filter(SmartRegisterClient client) {
                            return false;
                        }

                        @Override
                        public String name() {
                            return "Children";
                        }
                    },
                    new FilterOption() {
                        @Override
                        public String getCriteria() {
                            return "client.attributes LIKE '%household_head%'";
                        }

                        @Override
                        public boolean filter(SmartRegisterClient client) {
                            return false;
                        }

                        @Override
                        public String name() {
                            return "Household Heads";
                        }
                    }
                };
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonSortingOption(getResources().getString(R.string.woman_alphabetical_sort), "firstName"),
                        new CommonSortingOption("Age (Desc)", "birthdate"),
                        new CommonSortingOption("Age (Asc)", "birthdate DESC")
                };
            }

            @Override
            public String searchHint() {
                return "Search";
            }
        };
    }

    @Override
    protected RegisterClientsProvider clientsProvider() {
        return new ZMSmartClientsProvider(this, clientActionHandler, context.alertService());
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
        map.putAll(VaccinatorUtils.providerDetails());

        Log.v(getClass().getName(), "opening form for entity "+id+" for code "+code);

        if (type.equalsIgnoreCase("woman_enrollment"))
        {
            fillWomanTTEnrollmentOverrides(map, data);

            startForm("woman_enrollment", id, map);
        }
        else if (type.equalsIgnoreCase("woman_offsite_followup")){
            fillWomanTTOffsiteOverrides(map, data);

            startForm("woman_offsite_followup", id, map);
        }
        else if (type.equalsIgnoreCase("child_enrollment"))
        {
            fillChildVaccineEnrollmentOverrides(map, data);

            startForm("child_enrollment", id, map);
        }
        else if (type.equalsIgnoreCase("child_offsite_followup")){
            fillChildVaccineOffsiteOverrides(map, data);

            startForm("child_offsite_followup", id, map);
        }
        else if(type.equalsIgnoreCase("search_id")){
            onFilterManual(code);
        }
    }

    @Override
    protected void onInitialization(){
        context.formSubmissionRouter().getHandlerMap().put("woman_enrollment", new ZMFormSubmissionHandler(getActivity()));
        context.formSubmissionRouter().getHandlerMap().put("woman_offsite_followup", new ZMFormSubmissionHandler(getActivity()));
        context.formSubmissionRouter().getHandlerMap().put("woman_enrollment", new ZMFormSubmissionHandler(getActivity()));
        context.formSubmissionRouter().getHandlerMap().put("child_offsite_followup", new ZMFormSubmissionHandler(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.formSubmissionRouter().getHandlerMap().remove("woman_enrollment");
        context.formSubmissionRouter().getHandlerMap().remove("woman_offsite_followup");
        context.formSubmissionRouter().getHandlerMap().remove("child_enrollment");
        context.formSubmissionRouter().getHandlerMap().remove("child_offsite_followup");
    }

    public void startEnrollmentForm(final String form, final String entityId, final ClientEvent object, boolean launchQRCode){
        if (launchQRCode) {
            integ.initiateScan(new ScanType(form, entityId, object));
        }
        else {
            prompt = new PromptView(getActivity(), "Enter Program Client ID", "OK", "Cancel", "\\d+", true, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onQRCodeSucessfullyScanned(prompt.inputValue(), form, entityId, object);
                }
            });
            prompt.show();
        }
    }

    public void startOffsiteFollowupForm(final String form, final String entityId, final ClientEvent object){
        onQRCodeSucessfullyScanned(object.getClient().getIdentifier("Program Client ID"), form, entityId, object);
    }

    @Override
    protected void startRegistration() {
        // just searching for QR code scanned
        integ.initiateScan(new ScanType("SEARCH_ID", null, null));
    }

    @Override
    protected void onCreation() { }

    @Override
    protected void onResumption() {
        mView.findViewById(org.ei.opensrp.core.R.id.service_mode_selection).setVisibility(View.GONE);
        ((TextView)mView.findViewById(org.ei.opensrp.core.R.id.label_village)).setText("Filter: ");

        ImageView imv = ((ImageView)mView.findViewById(org.ei.opensrp.core.R.id.register_client));
        imv.setImageResource(org.ei.opensrp.core.R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            /*switch (view.getId()) {
                case R.id.woman_profile_info_layout:
                case R.id.woman_profile_info_layout1:
                    ((RegisterActivity) getActivity()).showDetailFragment((CommonPersonObjectClient) view.getTag(), true);
                    break;
                case R.id.woman_next_visit_holder:
                    HashMap<String, String> map = new HashMap<>();
                    CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                    map.putAll(providerDetails());
                    startForm("woman_followup", ((SmartRegisterClient) view.getTag()).entityId(), map);
                    break;
            }*/
        }
    }
}
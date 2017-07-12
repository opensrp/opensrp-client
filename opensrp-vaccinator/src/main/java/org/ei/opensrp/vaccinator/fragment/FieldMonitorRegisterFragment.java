package org.ei.opensrp.vaccinator.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.child.ChildDateSort;
import org.ei.opensrp.vaccinator.child.ChildFollowupHandler;
import org.ei.opensrp.vaccinator.child.ChildSearchOption;
import org.ei.opensrp.vaccinator.child.ChildService;
import org.ei.opensrp.vaccinator.child.ChildServiceModeOption;
import org.ei.opensrp.vaccinator.child.ChildSmartClientsProvider;
import org.ei.opensrp.vaccinator.field.FieldMonitorDailyDetailActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorMonthlyDetailActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorServiceModeOption;
import org.ei.opensrp.vaccinator.field.FieldMonitorServiceModeOptionDaily;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartClientsProvider;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.vaccinator.field.FieldSort;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.HashMap;
import java.util.Map;

import util.ClientlessOpenFormOption;
import util.barcode.Barcode;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Safwan on 2/15/2016.
 */
public class FieldMonitorRegisterFragment extends SecuredNativeSmartRegisterFragment {

    private FormController formController1;
    private SmartRegisterClientsProvider clientProvider = null;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private CommonPersonObjectController controller;
    private DialogOptionMapper dialogOptionMapper;
    HashMap<String, String> map = new HashMap<>();
    public static boolean byMonth = false;

    public FieldMonitorRegisterFragment(boolean byMonth, FormController formController) {
        super();
        this.formController1 = formController;
        this.byMonth = byMonth;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                if (byMonth) {
                    return new FieldMonitorServiceModeOption(clientsProvider());
                } else {

                    return new FieldMonitorServiceModeOptionDaily(clientsProvider());
                }
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                //return new ChildDateSort(ChildDateSort.ByColumnAndByDetails.byDetails, "child_dob_confirm");
                if (byMonth)
                    return new FieldSort(FieldSort.ByMonthANDByDAILY.ByMonth, "report");
                else
                    return new FieldSort(FieldSort.ByMonthANDByDAILY.ByDaily, "report");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.field_title);
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
                        //  new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.child_id_sort))

                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                        // new FieldSort(FieldSort.ByMonthANDByDAILY.ByDaily, "child_dob_confirm"),
                        // new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "first_name", getResources().getString(R.string.child_alphabetical_sort)),
                        // new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "program_client_id", getResources().getString(R.string.child_id_sort))
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.str_field_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            if (byMonth)
                clientProvider = new FieldMonitorSmartClientsProvider(
                        getActivity().getApplicationContext(), clientActionHandler, controller,
                        context().alertService(), FieldMonitorSmartClientsProvider
                        .ByMonthANDByDAILY.ByMonth, context());
            else
                clientProvider = new FieldMonitorSmartClientsProvider(
                        getActivity().getApplicationContext(), clientActionHandler, controller,
                        context().alertService(), FieldMonitorSmartClientsProvider
                        .ByMonthANDByDAILY.ByDaily, context());
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        if (byMonth) {
            controller = new CommonPersonObjectController(context().allCommonsRepositoryobjects
                    ("field"),
                    context().allBeneficiaries(), context().listCache(),
                    context().personObjectClientsCache(), "date", "field", "report", "monthly",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn
                    , "date",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn);

        } else {
            controller = new CommonPersonObjectController(context().allCommonsRepositoryobjects
                    ("field"),
                    context().allBeneficiaries(), context().listCache(),
                    context().personObjectClientsCache(), "date", "field", "report", "daily",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn
                    , "date",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn);
        }
        // context.formSubmissionRouter().getHandlerMap().put("woman_followup_form", new ChildFollowupHandler(new ChildService(context.allBeneficiaries(), context.allTimelineEvents(), context.allCommonsRepositoryobjects("ec_child"), context.alertService())));
        dialogOptionMapper = new DialogOptionMapper();
    }

    @Override
    protected void startRegistration() {
        startForm("vaccine_stock_position_form", null, map, ByColumnAndByDetails.bydefault);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();
    }//end of method

    @Override
    protected void onCreation() {

    }

    public enum ByColumnAndByDetails {
        byColumn, byDetails, bydefault;
    }

    private void startForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, ByColumnAndByDetails byColumnAndByDetails) {

        // Log.d("form COntroller check", formController.toString() );
        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            if (null == client) {
                formController.startFormActivity(formName, null, null);

            } else {

                formController.startFormActivity(formName, client.entityId(), null);
            }
        } else {
            JSONObject overridejsonobject = new JSONObject();
            try {
                for (Map.Entry<String, String> entry : overrideStringmap.entrySet()) {
                    switch (byColumnAndByDetails) {
                        case byDetails:
                            overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getDetails().get(entry.getValue()));
                            break;
                        case byColumn:
                            overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getColumnmaps().get(entry.getValue()));
                            break;
                        case bydefault:
                            overridejsonobject.put(entry.getKey(), entry.getValue());
                            break;
                    }
                }
//                overridejsonobject.put("existing_MWRA", );
            } catch (Exception e) {
                e.printStackTrace();
            }
            // org.ei.opensrp.util.Log.logDebug("overrides data is : " + overrideStringmap);
            FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            //formController.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());

            if (null == client) {
                formController.startFormActivity(formName, null, fieldOverrides.getJSONString());

            } else {

                formController.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
            }

        }
    }

    private DialogOption[] getEditOptions(HashMap<String, String> overridemap) {
        //Log.d("form open ", "enrollments form by Fragment");
        return new DialogOption[]{

                new ClientlessOpenFormOption("Field", "vaccine_stock_position_form", formController1, overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault)
                //    new ClientlessOpenFormOption("Followup", "child_followup_fake_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.byDetails)
        };
    }

    private Map<String, String> locations = new HashMap<String, String>();


    public void addChildToList(Map<String, TreeNode<String, Location>> locationMap, String locationTag) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            boolean tagFound = false;
            if (entry.getValue() != null) {
                Location l = entry.getValue().getNode();

                for (String s : l.getTags()) {
                    if (s.equalsIgnoreCase(locationTag)) {
                        locations.put(locationTag, l.getName());
                        tagFound = true;
                        return;
                    }
                }


            }
            if (!tagFound) {
                if (entry.getValue().getChildren() != null) {
                    addChildToList(entry.getValue().getChildren(), locationTag);
                }
            }
        }
    }

    public void updateSearchView() {
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
                        setCurrentSearchFilter(new ChildSearchOption(cs.toString()));
                        filteredClients = getClientsAdapter().getListItemProvider()
                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                                        getCurrentSearchFilter(), getCurrentSortOption());


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
                        getClientsAdapter().refreshClients(filteredClients);
                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                        super.onPostExecute(o);
                    }
                }).execute();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }//end of method

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (byMonth) {
                FieldMonitorMonthlyDetailActivity.userMap = map;
                FieldMonitorMonthlyDetailActivity.fieldclient = (CommonPersonObjectClient) view.getTag(R.id.field_daymonth);
                FieldMonitorMonthlyDetailActivity.usedVaccines = (HashMap<String, String>) view.getTag(R.id.field_daymonth_layout);
                FieldMonitorMonthlyDetailActivity.wastedVaccines = (HashMap<String, String>) view.getTag(R.id.field_month_target_layout);

                Intent intent = new Intent(getActivity(), FieldMonitorMonthlyDetailActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                FieldMonitorDailyDetailActivity.userMap = map;
                FieldMonitorDailyDetailActivity.fieldclient = (CommonPersonObjectClient) view.getTag(R.id.field_day);
                FieldMonitorDailyDetailActivity.usedVaccines = (HashMap<String, String>) view.getTag(R.id.field_day_layout);
                Intent intent = new Intent(getActivity(), FieldMonitorDailyDetailActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        }
    }

    private class EditDialogOptionModel implements DialogOptionModel {
        private HashMap<String, String> overrides1;

        public EditDialogOptionModel(HashMap<String, String> overrides1) {
            this.overrides1 = overrides1;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions(this.overrides1);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {

            //     Toast.makeText(ChildSmartRegisterActivity.this,option.name()+"", Toast.LENGTH_LONG).show();
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }
}

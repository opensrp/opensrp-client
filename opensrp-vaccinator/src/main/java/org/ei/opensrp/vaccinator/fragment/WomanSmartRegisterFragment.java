package org.ei.opensrp.vaccinator.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectFilterOption;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.woman.WomanDateSort;
import org.ei.opensrp.vaccinator.woman.WomanDetailActivity;
import org.ei.opensrp.vaccinator.woman.WomanFollowupHandler;
import org.ei.opensrp.vaccinator.woman.WomanSearchOption;
import org.ei.opensrp.vaccinator.woman.WomanService;
import org.ei.opensrp.vaccinator.woman.WomanServiceModeOption;
import org.ei.opensrp.vaccinator.woman.WomanSmartClientsProvider;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.VillageController;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import util.ClientlessOpenFormOption;
import util.barcode.Barcode;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 05-Jan-16.
 */
public class WomanSmartRegisterFragment extends SecuredNativeSmartRegisterFragment {
    private SecuredNativeSmartRegisterActivity.DefaultOptionsProvider defaultOptionProvider;
    private SecuredNativeSmartRegisterActivity.NavBarOptionsProvider navBarOptionsProvider;

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private HashMap<String, String> overrides;
    private FormController formController1;

    public WomanSmartRegisterFragment(FormController formController) {
        super();
        this.formController1 = formController;

    }


    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new WomanServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new WomanDateSort(WomanDateSort.ByColumnAndByDetails.byDetails, "client_dob_confirm");

            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.woman_title);
            }
        };
    }//end of method


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

                        new WomanDateSort(WomanDateSort.ByColumnAndByDetails.byDetails, "client_dob_confirm"),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "first_name", getResources().getString(R.string.woman_alphabetical_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "program_client_id", getResources().getString(R.string.woman_id_sort))
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.woman_search_hint);
            }
        };
    }//end of method

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new WomanSmartClientsProvider(
                    getActivity(), clientActionHandler, controller, context().alertService());
        }
        return clientProvider;
    }//end of method

    @Override
    protected void onInitialization() {
        if (controller == null) {
            controller = new CommonPersonObjectController(context().allCommonsRepositoryobjects
                    ("ec_woman"),
                    context().allBeneficiaries(), context().listCache(),
                    context().personObjectClientsCache(), "first_name", "ec_woman",
                    "client_reg_date",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails);

        }
        context().formSubmissionRouter().getHandlerMap().put("woman_followup_form",
                new WomanFollowupHandler(new WomanService(context().allTimelineEvents(), context()
                        .allCommonsRepositoryobjects("ec_woman"), context().alertService())));
        dialogOptionMapper = new DialogOptionMapper();
    }//end of method

    @Override
    protected void onCreation() {

    }//end of method

    @Override
    protected void startRegistration() {
        Intent intent = new Intent(Barcode.BARCODE_INTENT);
        intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        startActivityForResult(intent, Barcode.BARCODE_REQUEST_CODE);
    }//end of method

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();
    }//end of method


    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.woman_profile_info_layout:
                    WomanDetailActivity.womanclient = (CommonPersonObjectClient) view.getTag();
                    Intent intent = new Intent(((WomanSmartRegisterActivity) getActivity()), WomanDetailActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                    break;
                case R.id.woman_next_visit:
                    HashMap<String, String> map = new HashMap<String, String>();
                    CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                    String locationjson = context().anmLocationController().get();
                    LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

                    Map<String, TreeNode<String, Location>> locationMap =
                            locationTree.getLocationsHierarchy();

                    addChildToList(locationMap, "Country");
                    addChildToList(locationMap, "province");
                    addChildToList(locationMap, "city");
                    addChildToList(locationMap, "town");
                    addChildToList(locationMap, "uc");


                    map.put("provider_uc", locations.get("uc"));
                    map.put("provider_town", locations.get("town"));
                    map.put("provider_city", locations.get("city"));
                    map.put("provider_province", locations.get("province"));

                    map.put("existing_program_client_id", client.getDetails().get("program_client_id"));
                    map.put("provider_location_id", locations.get("uc"));
                    map.put("existing_gender", "female");
                    map.put("provider_location_name", locations.get("uc"));
                    map.put("provider_id", context().anmService().fetchDetails().name());
                    map.put("existing_house_number", client.getDetails().get("house_number") != null ? client.getDetails().get("house_number") : "");
                    map.put("existing_street", client.getDetails().get("street") != null ? client.getDetails().get("street") : "");
                    map.put("existing_union_council", client.getDetails().get("union_council") != null ? client.getDetails().get("union_council") : "");
                    map.put("existing_town", client.getDetails().get("town") != null ? client.getDetails().get("town") : "");
                    map.put("existing_city_village", client.getDetails().get("city_village") != null ? client.getDetails().get("city_village") : "");
                    map.put("existing_province", client.getDetails().get("province") != null ? client.getDetails().get("province") : "");
                    map.put("existing_landmark", client.getDetails().get("landmark") != null ? client.getDetails().get("landmark") : "");

                    map.put("existing_first_name", client.getDetails().get("first_name") != null ? client.getDetails().get("first_name") : "");
                    map.put("existing_last_name", client.getDetails().get("last_name") != null ? client.getDetails().get("last_name") : "");
                    map.put("existing_father_name", client.getDetails().get("father_name") != null ? client.getDetails().get("father_name") : "");
                    map.put("existing_client_birth_date", client.getDetails().get("client_dob_confirm") != null ? client.getDetails().get("client_dob_confirm") : "");
                    map.put("existing_ethnicity", client.getDetails().get("ethnicity") != null ? client.getDetails().get("ethnicity") : "");
                    map.put("existing_client_reg_date", client.getDetails().get("client_reg_date") != null ? client.getDetails().get("client_reg_date") : "");
                    map.put("existing_epi_card_number", client.getDetails().get("epi_card_number") != null ? client.getDetails().get("epi_card_number") : "");
                    map.putAll(getPreloadVaccineData(client));
                    setOverrides(map);
                    startFollowupForms("woman_followup_form", (SmartRegisterClient) view.getTag(), map, ByColumnAndByDetails.bydefault);

                    break;
            }
        }

    }//end of method


    private HashMap<String, String> getPreloadVaccineData(CommonPersonObjectClient client) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (String s : client.getColumnmaps().keySet()) {
            if (s.contains("tt1")) {
                if (client.getColumnmaps().get(s) != null && client.getColumnmaps().get(s).toString() != null && !client.getColumnmaps().get(s).toString().equalsIgnoreCase("")) {
                    map.put("e_tt1", client.getColumnmaps().get(s));
                } else {
                    map.put("e_tt1", "");

                }
            } else if (s.contains("tt2")) {
                if (client.getColumnmaps().get(s) != null && client.getColumnmaps().get(s).toString() != null && !client.getColumnmaps().get(s).toString().equalsIgnoreCase("")) {
                    map.put("e_tt2", client.getColumnmaps().get(s));
                } else {
                    map.put("e_tt2", "");

                }
            } else if (s.contains("tt3")) {
                if (client.getColumnmaps().get(s) != null && client.getColumnmaps().get(s).toString() != null && !client.getColumnmaps().get(s).toString().equalsIgnoreCase("")) {
                    map.put("e_tt3", client.getColumnmaps().get(s));
                } else {
                    map.put("e_tt3", "");

                }
            } else if (s.contains("tt4")) {
                if (client.getColumnmaps().get(s) != null && client.getColumnmaps().get(s).toString() != null && !client.getColumnmaps().get(s).toString().equalsIgnoreCase("")) {
                    map.put("e_tt4", client.getColumnmaps().get(s));
                } else {
                    map.put("e_tt4", "");

                }
            } else if (s.contains("tt5")) {
               /* Log.d("Coulmn Name :-", s);
                ig()*/
                if (client.getColumnmaps().get(s) != null && client.getColumnmaps().get(s).toString() != null && !client.getColumnmaps().get(s).toString().equalsIgnoreCase("")) {
                    map.put("e_tt5", client.getColumnmaps().get(s));
                } else {
                    map.put("e_tt5", "");

                }
            }
        }
        return map;

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
                        setCurrentSearchFilter(new WomanSearchOption(cs.toString()));
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

    private String getLocationNameByAttribute(LocationTree locationTree, String tag) {

        Log.d("ANM LOCATION : ", "in getLocationName Method");

        //locationTree.
        Map<String, TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();
        Collection<TreeNode<String, Location>> collection = locationMap.values();
        Iterator iterator = collection.iterator();
        //if()
        while (iterator.hasNext()) {
            TreeNode<String, Location> treeNode = (TreeNode<String, Location>) iterator.next();
            Location location = (Location) treeNode.getNode();

            // Location location= (Location)iterator.next();
            for (String s : location.getTags()) {

                if (s.equalsIgnoreCase(tag)) {
                    Log.d("Amn Locations", location.getName());
                    return location.getName();
                }

                //location.getAttributes().get(s).toString().equalsIgnoreCase(attribute);
            }
        }

        Log.d("Amn Locations", "No location found");

        return null;
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist, Map<String, TreeNode<String, Location>> locationMap) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if (entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist, entry.getValue().getChildren());

            } else {
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                Log.d("ANM Details", "location name :" + name);
                dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ", "_"), "location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails, name));

            }
        }
    }

    public enum ByColumnAndByDetails {
        byColumn, byDetails, bydefault;
    }

    private void startFollowupForms(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, ByColumnAndByDetails byColumnAndByDetails) {


        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            formController1.startFormActivity(formName, client.entityId(), null);
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
                        default:
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
            formController1.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }

    private DialogOption[] getEditOptions(HashMap<String, String> overridemap) {
        Log.d("form open ", "enrollments form by Fragment");
        return new DialogOption[]{

                new ClientlessOpenFormOption("Enrollment", "woman_enrollment_form", formController1, overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault)
                //    new ClientlessOpenFormOption("Followup", "child_followup_fake_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.byDetails)
        };
    }

    public HashMap<String, String> getOverrides() {
        return overrides;
    }//end of method

    public void setOverrides(HashMap<String, String> overrides) {

        this.overrides = overrides;
    }//end of method

    private HashMap<String, String> overrides1;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        org.ei.opensrp.util.Log.logDebug("Result Coode" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            String qrcode = (String) extras.get(Barcode.SCAN_RESULT);

            onQRCodeSucessfullyScanned(qrcode);

        }


    }//end of the method

    private void onQRCodeSucessfullyScanned(String qrCode) {
        //    Toast.makeText(this, "QrCode is : " + qrcode, Toast.LENGTH_LONG).show();
       /*
       #TODO:after reading the code , app first search for that id in database if he it is there , that client appears  on register only . if it doesnt then it shows two options
       */
        //controller.getClients().
        org.ei.opensrp.util.Log.logDebug("ANM DETAILS" + context().anmController().get());
        String locationjson = context().anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();

        addChildToList(locationMap, "Country");
        addChildToList(locationMap, "province");
        addChildToList(locationMap, "city");
        addChildToList(locationMap, "town");
        addChildToList(locationMap, "uc");
        if (getfilteredClients(qrCode) <= 0) {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("provider_uc", locations.get("uc"));
            map.put("provider_town", locations.get("town"));
            map.put("provider_city", locations.get("city"));
            map.put("provider_province", locations.get("province"));
            map.put("existing_program_client_id", qrCode);
            map.put("provider_location_id", locations.get("uc"));
            map.put("gender", "female");
            map.put("provider_location_name", locations.get("uc"));
            map.put("provider_id", context().anmService().fetchDetails().name());


            showFragmentDialog(new EditDialogOptionModel(map), null);
        } else {
            getSearchView().setText(qrCode);

        }

    }

    private int getfilteredClients(String filterString) {
        int i = 0;
        setCurrentSearchFilter(new WomanSearchOption(filterString));
        SmartRegisterClients filteredClients = getClientsAdapter().getListItemProvider()
                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                        getCurrentSearchFilter(), getCurrentSortOption());
        i = filteredClients.size();

        return i;
    }//end of method


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
}
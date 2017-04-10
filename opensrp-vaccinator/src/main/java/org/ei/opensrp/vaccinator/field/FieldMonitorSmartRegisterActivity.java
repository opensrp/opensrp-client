package org.ei.opensrp.vaccinator.field;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.child.ChildSearchOption;
import org.ei.opensrp.vaccinator.child.ChildSmartClientsProvider;
import org.ei.opensrp.vaccinator.domain.Field;
import org.ei.opensrp.vaccinator.pageadapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.vaccinator.fragment.FieldMonitorRegisterFragment;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.ClientlessOpenFormOption;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Oct-15.
 */
public class FieldMonitorSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private CommonPersonObjectController controller;
    HashMap<String, String> map = new HashMap<>();
    private SmartRegisterClientsProvider clientProvider = null;
    private DefaultOptionsProvider defaultOptionProvider;
    private NavBarOptionsProvider navBarOptionsProvider;

    private android.support.v4.app.Fragment mBaseFragment = null;
    private android.support.v4.app.Fragment mProfileFragment = null;
    private FormController formControllerown;
    private int currentPage;
    private String[] formNames = new String[]{};

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;


    //private CommonRepository
    public static boolean sortbymonth;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getWindow().getDecorView().setBackgroundDrawable(null);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.formControllerown = new FormController(this);
        formNames = this.buildFormNameList();
        mBaseFragment = new FieldMonitorRegisterFragment(sortbymonth, this.formControllerown);

        Log.d("Fiedk", formNames[0]);
        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(getEditOptions(null).length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        if (sortbymonth) {
            controller = new CommonPersonObjectController(
                    context().allCommonsRepositoryobjects("field"),
                    context().allBeneficiaries(), context().listCache(),
                    context().personObjectClientsCache(), "date", "field", "report", "monthly",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn
                    , "date",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn);

        } else {
            controller = new CommonPersonObjectController(
                    context().allCommonsRepositoryobjects("field"),
                    context().allBeneficiaries(), context().listCache(),
                    context().personObjectClientsCache(), "date", "field", "report", "daily",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn
                    , "date",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn);
        }
    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }


    @Override
    protected void onInitialization() {

    }


    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }


    @Override
    public void startRegistration() {
    }


    @Override
    public void setupViews() {


    }//end of method

    @Override
    protected void onResumption() {
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            e.printStackTrace();
        }
    }


   /* public void updateSearchView() {
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
                        setCurrentSearchFilter(new FieldSearchOption(cs.toString()));
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
//                currentSearchFilter = new HHSearchOption(cs.toString());
//                clientsAdapter
//                        .refreshList(currentVillageFilter, currentServiceModeOption,
//                                currentSearchFilter, currentSortOption);
//
//                searchCancelView.setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }*/


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

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v("fieldoverride", metaData);

        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset

            if (entityId != null || metaData != null) {
                String data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);

                if (displayFormFragment != null) {


                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);

                    Log.v("in activity ", "startFormActivity method start");
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
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

    private DialogOption[] getEditOptions(HashMap<String, String> overridemap) {
     /*//  = new HashMap<String,String>();
        overridemap.put("existing_MWRA","MWRA");
        overridemap.put("existing_location", "existing_location");*/
        return new DialogOption[]{

                new ClientlessOpenFormOption("Vaccine Stock", "vaccine_stock_position_form", this.formControllerown, overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault)
                //    new ClientlessOpenFormOption("Followup", "child_followup_fake_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.byDetails)
        };
    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("vaccine_stock_position_form");

        DialogOption[] options = getEditOptions(null);
        for (int i = 0; i < options.length; i++) {
            formNames.add(((ClientlessOpenFormOption) options[i]).getFormName());
        }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    public void onBackPressed() {
        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    public void switchToBaseFragment(final String data) {
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(0, false);
                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                if (registerFragment != null && data != null) {
                    registerFragment.refreshListView();
                }

                //hack reset the form
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                }

                displayFormFragment.setRecordId(null);
            }
        });

    }

}

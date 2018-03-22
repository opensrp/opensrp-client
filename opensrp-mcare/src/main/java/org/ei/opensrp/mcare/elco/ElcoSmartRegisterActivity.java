package org.ei.opensrp.mcare.elco;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.fragment.ElcoSmartRegisterFragment;
import org.ei.opensrp.mcare.pageradapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.sync.ClientProcessor;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.AsyncTask;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ElcoSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;


    private android.support.v4.app.Fragment mProfileFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
        mBaseFragment = new ElcoSmartRegisterFragment();
        mProfileFragment = new ElcoDetailActivity();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment,mProfileFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        ziggyService = context().ziggyService();
    }
    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("psrf_form");
        formNames.add("mis_elco");
        formNames.add("mis_census");
//        DialogOption[] options = getEditOptions();
//        for (int i = 0; i < options.length; i++){
//            formNames.add(((OpenFormOption) options[i]).getFormName());
//        }
        return formNames.toArray(new String[formNames.size()]);
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page <2 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }
    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {return null;}

    @Override
    protected void setupViews() {}

    @Override
    protected void onResumption(){}

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {return null;}

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {return null;}

    @Override
    protected void onInitialization() {}

    @Override
    public void startRegistration() {
    }
    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        try {
            LoginActivity.setLanguage();
        }catch (Exception e){

        }
        super.showFragmentDialog(dialogOptionModel, tag);
    }

    public DialogOption[] getEditOptions(CommonPersonObjectClient elco) {
        //AllCommonsRepository allelcoRepository = context.getInstance().allCommonsRepositoryobjects("elco");
        //CommonPersonObject elcoobject = allelcoRepository.findByCaseID(elco.entityId());
        //AllCommonsRepository householdrep = context.getInstance().allCommonsRepositoryobjects("household");
        //CommonPersonObject householdparent = householdrep.findByCaseID(elcoobject.getRelationalId());
        String alertstate = "";
        alertstate = getalertstateofelco(elco);
        HashMap<String,String> overridemap = new HashMap<String,String>();

        Cursor cursor = null;
        Integer numberOfElcos = 0;
        try {
            String tableName = "ec_elco";
            String sql = "select * from " + tableName + " where relational_id = '" + elco.getDetails().get("relational_id") + "'";
            CommonRepository cr = org.ei.opensrp.Context.getInstance().commonrepository(tableName);
            cursor = cr.queryTable(sql);
            numberOfElcos = cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (cursor != null){
                cursor.close();
            }
        }
        overridemap.put("existing_ELCO", numberOfElcos.toString());
        overridemap.put("current_formStatus", alertstate);
        return new DialogOption[]{

                new OpenFormOption(getResources().getString(R.string.psrfform), "psrf_form", formController,overridemap, OpenFormOption.ByColumnAndByDetails.bydefault)
        };
    }
    public DialogOption[] getEditOptionsForMISELCO(CommonPersonObjectClient elco,String alertstatus) {
        String alertstate = alertstatus;
        HashMap<String,String> overridemap = new HashMap<String,String>();
        overridemap.put("mis_elco_current_formStatus", alertstate);

        return new DialogOption[]{

                new OpenFormOption(getResources().getString(R.string.mis_elco), "mis_elco", formController,overridemap,OpenFormOption.ByColumnAndByDetails.bydefault)
        };
    }

    private String getalertstateofelco(CommonPersonObjectClient elco) {
        List<Alert> alertlist_for_client = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(elco.entityId(), "ELCO PSRF");
        String alertstate = "upcoming";
        if(alertlist_for_client.size() == 0 ){

        }else {
            for (int i = 0; i < alertlist_for_client.size(); i++) {
//           psrfdue.setText(alertlist_for_client.get(i).expiryDate());
                Log.v("printing alertlist", alertlist_for_client.get(i).status().value());
                alertstate = alertlist_for_client.get(i).status().value();

            }
        }
        return alertstate;
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
//        Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + ((BaseRegisterActivityPagerAdapter)mPagerAdapter).offset; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
                //check if there is previously saved data for the form
                data = getPreviouslySavedDataForForm(formName, metaData, entityId);
                if (data == null){
                    data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                }

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);

                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void updateSearchView(){
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
//                        currentSearchFilter = new ElcoSearchOption(cs.toString());
                        setCurrentSearchFilter(new ElcoSearchOption(cs.toString()));
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
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        // save the form
        Log.e("----------",formSubmission.toString());
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            ziggyService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);

            switchToBaseFragment(formSubmission);

        }catch (Exception e){
            // TODO: show error dialog on the formfragment if the submission fails
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);

            if (displayFormFragment != null) {
                displayFormFragment.hideTranslucentProgressDialog();
            }
            Log.e("", "Form Submission Error", e);;
        }
    }

    public void switchToBaseFragment(final String data){
        Log.v("we are here","switchtobasegragment");
        final int prevPageIndex = currentPage;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                //hack reset the form
                if (prevPageIndex != 1) {
                    mPager.setCurrentItem(0, false);
                    SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && data != null) {
                        registerFragment.refreshListView();
                    }
                    DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                    if (displayFormFragment != null) {
                        displayFormFragment.hideTranslucentProgressDialog();
                        displayFormFragment.setFormData(null);
                    }

                    displayFormFragment.setRecordId(null);
                }else if (prevPageIndex == 4) {
                    mPager.setCurrentItem(1, false);
                    SecuredFragment registerFragment = (SecuredFragment) findFragmentByPosition(1);
                    DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(prevPageIndex);
                    if (displayFormFragment != null) {
                        displayFormFragment.hideTranslucentProgressDialog();
                        displayFormFragment.setFormData(null);
                    }


                    displayFormFragment.setRecordId(null);
                    ((ElcoDetailActivity)mProfileFragment).initiallize();
                    showProfileView();
                }else {
                    mPager.setCurrentItem(0, false);
                    SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && data != null) {
                        registerFragment.refreshListView();
                    }
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }else if (currentPage > 1){
            retrieveAndSaveUnsubmittedFormData();
            String BENGALI_LOCALE = "bn";
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(Context.getInstance().applicationContext()));

            String preferredLocale = allSharedPreferences.fetchLanguagePreference();
            if (BENGALI_LOCALE.equals(preferredLocale)) {
                new AlertDialog.Builder(this)
                        .setMessage("আপনি কি নিশ্চিত যে আপনি ফর্ম থেকে বের হয়ে যেতে চান? ")
                        .setTitle("ফর্ম বন্ধ নিশ্চিত করুন ")
                        .setCancelable(false)
                        .setPositiveButton("হাঁ",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        switchToBaseFragment(null);
                                    }
                                })
                        .setNegativeButton("না",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                })
                        .show();
            }else{
                new AlertDialog.Builder(this)
                        .setMessage(R.string.mcareform_back_confirm_dialog_message)
                        .setTitle(R.string.mcareform_back_confirm_dialog_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.mcareyes_button_label,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        switchToBaseFragment(null);
                                    }
                                })
                        .setNegativeButton(R.string.mcareno_button_label,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                    }
                                })
                        .show();
            }
//            switchToBaseFragment(null);
        }else if (currentPage == 1) {
            switchToBaseFragment(null); // allow back key only if we are
        }
    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }

    public void retrieveAndSaveUnsubmittedFormData(){
        if (currentActivityIsShowingForm()){
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private boolean currentActivityIsShowingForm(){
        return currentPage != 0;
    }
    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    public void showProfileView() {
        ElcoDetailActivity profile = (ElcoDetailActivity)findFragmentByPosition(1);
        profile.initiallize();
        mPager.setCurrentItem(1);
    }
}

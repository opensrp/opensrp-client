package org.ei.opensrp.indonesia.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.view.dialog.KIANCOverviewServiceMode;
import org.ei.opensrp.indonesia.view.fragment.MotherProfileViewFragment;
import org.ei.opensrp.indonesia.view.fragment.NativeKIANCSmartRegisterFragment;
import org.ei.opensrp.indonesia.view.pageradapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.NameSort;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.ei.opensrp.R.string.form_back_confirm_dialog_message;
import static org.ei.opensrp.R.string.form_back_confirm_dialog_title;
import static org.ei.opensrp.R.string.no_button_label;
import static org.ei.opensrp.R.string.yes_button_label;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_CLOSE;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_EDIT;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_RENCANA_PERSALINAN;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_INTEGRASI;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_VISIT_LABTEST;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_PNC_REGISTRATION;

/**
 * Created by Dimas Ciputra on 3/5/15.
 */
public class NativeKIANCSmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity {

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private Fragment mBaseFragment = null;
    private Fragment mProfileFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
        mBaseFragment = new NativeKIANCSmartRegisterFragment();
        mProfileFragment = new MotherProfileViewFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment, mProfileFragment);
        mPager.setOffscreenPageLimit(getEditOptions().length + ((BaseRegisterActivityPagerAdapter)mPagerAdapter).offset());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page < ((BaseRegisterActivityPagerAdapter)mPagerAdapter).offset() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                return new KIANCOverviewServiceMode(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new NameSort();
            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.anc_label);
            }
        };
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_anc_visit_form),
                        KARTU_IBU_ANC_VISIT, formController),
                new OpenFormOption(getString(R.string.anc_visit_integrasi), KARTU_IBU_ANC_VISIT_INTEGRASI, formController),
                new OpenFormOption(getString(R.string.anc_visit_labtest), KARTU_IBU_ANC_VISIT_LABTEST, formController),
                new OpenFormOption(getString(R.string.str_rencana_persalinan_anc_form),
                        KARTU_IBU_ANC_RENCANA_PERSALINAN, formController),
                new OpenFormOption(getString(R.string.str_register_pnc_form),
                        KARTU_IBU_PNC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.anc_edit),
                        KARTU_IBU_ANC_EDIT, formController),
                new OpenFormOption(getString(R.string.str_register_anc_close_form),
                        KARTU_IBU_ANC_CLOSE, formController),
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void setupViews() {}

    @Override
    protected void onResumption(){}

    @Override
    protected void onStart() {
        super.onStart();
        FlurryFacade.logEvent("anc_dashboard");
    }

    @Override
    public void startRegistration() {
        // if OA form needed again, uncomment this
        // FieldOverrides fieldOverrides = new FieldOverrides(context.anmLoc
        // ationController().getLocationJSON());
        // startFormActivity(AllConstantsINA.FormNames.KARTU_IBU_ANC_OA, null, fieldOverrides.getJSONString());
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + ((BaseRegisterActivityPagerAdapter)mPagerAdapter).offset(); // add the offset
            if (entityId != null || metaData != null){
                String data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.loadFormData();
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        //formNames.add(KARTU_IBU_ANC_OA);

        DialogOption[] options = getEditOptions();
        for (int i = 0; i < options.length; i++){
            formNames.add(((OpenFormOption) options[i]).getFormName());
        }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
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

    public void switchToBaseFragment(final String data){
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
                    displayFormFragment.loadFormData();
                }

                displayFormFragment.setRecordId(null);
            }
        });

    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }

    @Override
    public void onBackPressed() {
        if (currentPage > 1) {
            new AlertDialog.Builder(this)
                    .setMessage(form_back_confirm_dialog_message)
                    .setTitle(form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    goBack();
                                }
                            })
                    .setNegativeButton(no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            })
                    .show();
        } else if(currentPage == 1) {
            mPager.setCurrentItem(0, false);
            MotherProfileViewFragment fragment = (MotherProfileViewFragment)findFragmentByPosition(1);
            fragment.clearCard();
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private void goBack() {
        switchToBaseFragment(null);
    }

    public void startDetailFragment(String entityId) {
        if(entityId!=null) {
            MotherProfileViewFragment fragment = (MotherProfileViewFragment) findFragmentByPosition(1);
            fragment.setCaseId(entityId);
        }
        mPager.setCurrentItem(1, false);
    }
}
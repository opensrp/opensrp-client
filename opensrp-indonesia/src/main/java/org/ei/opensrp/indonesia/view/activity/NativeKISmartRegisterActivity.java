package org.ei.opensrp.indonesia.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.view.controller.BidanVillageController;
import org.ei.opensrp.indonesia.view.controller.KartuIbuRegisterController;
import org.ei.opensrp.indonesia.view.fragment.MotherProfileViewFragment;
import org.ei.opensrp.indonesia.view.fragment.NativeKISmartRegisterFragment;
import org.ei.opensrp.indonesia.view.pageradapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import static org.ei.opensrp.R.string.form_back_confirm_dialog_message;
import static org.ei.opensrp.R.string.form_back_confirm_dialog_title;
import static org.ei.opensrp.R.string.no_button_label;
import static org.ei.opensrp.R.string.yes_button_label;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_ANC_REGISTRATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_CLOSE;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_EDIT;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KARTU_IBU_REGISTRATION;
import static org.ei.opensrp.indonesia.AllConstantsINA.FormNames.KOHORT_KB_PELAYANAN;

/**
 * Created by Dimas Ciputra on 2/18/15.
 */
public class NativeKISmartRegisterActivity extends BidanSecuredNativeSmartRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener {

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
        mBaseFragment = new NativeKISmartRegisterFragment();
        mProfileFragment = new MotherProfileViewFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment, mProfileFragment);
        // + 1 for detail view
        mPager.setOffscreenPageLimit(getEditOptions().length + ((BaseRegisterActivityPagerAdapter) mPagerAdapter).offset());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        //error handling
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }

    public void onPageChanged(int page) {
        setRequestedOrientation(page < 2 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return null;
    }

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_register_kb_form), KOHORT_KB_PELAYANAN,
                        formController),
                new OpenFormOption(getString(R.string.str_register_anc_form), KARTU_IBU_ANC_REGISTRATION, formController),
                new OpenFormOption(getString(R.string.str_edit_ki_form), KARTU_IBU_EDIT, formController),
                new OpenFormOption(getString(R.string.str_close_ki_form), KARTU_IBU_CLOSE, formController),
        };
    }

    @Override
    protected void onResumption() {
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    public void setupViews() {
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
    protected void startRegistration() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryFacade.logEvent("kohort_ibu_dashboard");
    }


    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
            JSONObject uniqueId = new JSONObject(((Context) context).uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
            Iterator<String> iter = uniqueId.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                combined.put(key, uniqueId.get(key));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity(KARTU_IBU_REGISTRATION, null, fieldOverrides.getJSONString());
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + ((BaseRegisterActivityPagerAdapter)mPagerAdapter).offset(); // add the offset
            if (entityId != null || metaData != null) {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add(KARTU_IBU_REGISTRATION);

        DialogOption[] options = getEditOptions();
        for (int i = 0; i < options.length; i++) {
            formNames.add(((OpenFormOption) options[i]).getFormName());
        }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides) {
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            //switch to forms list fragment
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

        } catch (Exception e) {
            e.printStackTrace();
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
                    displayFormFragment.nullifyFormData();
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
        return (DisplayFormFragment) findFragmentByPosition(index);
    }

    private void goBack() {
        switchToBaseFragment(null);
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

    public void startDetailFragment(String entityId) {
        if(entityId!=null) {
            MotherProfileViewFragment fragment = (MotherProfileViewFragment) findFragmentByPosition(1);
            fragment.setCaseId(entityId);
        }
        mPager.setCurrentItem(1, false);
    }

}

package org.ei.opensrp.path.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.path.adapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;

public abstract class SmartRegisterSecuredActivity extends SecuredActivity {
    public static final String DIALOG_TAG = "dialog";

    OpenSRPViewPager mPager;
    private BaseRegisterActivityPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(org.ei.opensrp.path.R.layout.activity_main);

        mPager = (OpenSRPViewPager) findViewById(org.ei.opensrp.path.R.id.view_pager);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().hide();
//        getWindow().getDecorView().setBackgroundDrawable(null);

        formNames = this.buildFormNameList();
        mBaseFragment = getBaseFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        mPagerAdapter.switchToBaseFragment(mPager);
    }

    public abstract SecuredNativeSmartRegisterFragment getBaseFragment();

    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract String[] buildFormNameList() ;

    @Override
    protected void onCreation() {
    }


    public void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // FIXME path_conflict
        /* SmartRegisterDialogFragment
                .newInstance(this, dialogOptionModel, tag)
                .show(ft, DIALOG_TAG); */
    }

    protected String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, PENDING.value())
                        .map());
    }

    public void saveFormSubmission(final String formSubmission, String id, final String formName, JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            // Update Fts Tables
            CommonFtsObject commonFtsObject = context.commonFtsObject();
            if(commonFtsObject != null){
                String[] ftsTables =  commonFtsObject.getTables();
                for(String ftsTable: ftsTables){
                    AllCommonsRepository allCommonsRepository = context.allCommonsRepositoryobjects(ftsTable);
                    boolean updated = allCommonsRepository.updateSearch(submission.entityId());
                    if (updated) {
                        break;
                    }
                }
            }

            new AlertDialog.Builder(this)
                    .setMessage(org.ei.opensrp.path.R.string.form_saved_success_dialog_message)
                    .setTitle(org.ei.opensrp.path.R.string.form_saved_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(org.ei.opensrp.path.R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1;
                                switchToBaseFragment(submission, formIndex); // pass data to let fragment know which record to display
                            }
                        })
                    .show();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setMessage((getResources().getString(org.ei.opensrp.path.R.string.form_saved_failed_dialog_message))+" : "+e.getMessage())
                    .setTitle(org.ei.opensrp.path.R.string.form_saved_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(org.ei.opensrp.path.R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
                                if (displayFormFragment != null) {
                                    displayFormFragment.hideTranslucentProgressDialog();
                                }
                            }
                        }).show();
            e.printStackTrace();
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
            // FIXME path_conflict
            //displayFormFragment.showForm(mPager, formIndex, entityId, metaData, false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides){
        try {
            //Save the current form data into shared preferences
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String savedDataKey = formName + "savedPartialData";
            editor.putString(savedDataKey, formData);

            String overridesKey = formName + "overrides";
            editor.putString(overridesKey, fieldOverrides.toString());

            String idKey = formName + "id";
            if (id != null){
                editor.putString(idKey, id);
            }

            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getPreviouslySavedDataForForm(String formName, String overridesStr, String id){
        try {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String savedDataKey = formName + "savedPartialData";
            String overridesKey = formName + "overrides";
            String idKey = formName + "id";

            JSONObject overrides = new JSONObject();

            if (overrides != null){
                JSONObject json = new JSONObject(overridesStr);
                String s = json.getString("fieldOverrides");
                overrides = new JSONObject(s);
            }

            boolean idIsConsistent = id == null && !sharedPref.contains(idKey) ||
                    id != null && sharedPref.contains(idKey) && sharedPref.getString(idKey, null).equals(id);

            if (sharedPref.contains(savedDataKey) && sharedPref.contains(overridesKey) && idIsConsistent){
                String savedDataStr = sharedPref.getString(savedDataKey, null);
                String savedOverridesStr = sharedPref.getString(overridesKey, null);


                // the previously saved data is only returned if the overrides and id are the same ones used previously
                if (savedOverridesStr.equals(overrides.toString())) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    //after retrieving the value delete it from shared pref.
                    editor.remove(savedDataKey);
                    editor.remove(overridesKey);
                    editor.remove(idKey);
                    editor.apply();
                    return savedDataStr;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void switchToBaseFragment(final FormSubmission data, final int pageIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                if (registerFragment != null && data != null) {
                    String id = data.getFieldValue("zeir_id");
                    if (StringUtils.isBlank(id)){
                        id = data.getFieldValue("existing_zeir_id");
                    }
                    registerFragment.getSearchView().setText(id);
                    // FIXME path_conflict
                    //registerFragment.onFilterManual(id);
                }
                // FIXME path_conflict
                //else registerFragment.onFilterManual("");//clean up search filter

                //hack reset the form
                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(pageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                    displayFormFragment.setFormData(null);
                    displayFormFragment.setRecordId(null);
                    displayFormFragment.setFieldOverides(null);
                }

                mPager.setCurrentItem(0, false);
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
        if (currentPage != 0) {
            new AlertDialog.Builder(this)
                    .setMessage(org.ei.opensrp.path.R.string.form_back_confirm_dialog_message)
                    .setTitle(org.ei.opensrp.path.R.string.form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(org.ei.opensrp.path.R.string.yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switchToBaseFragment(null, currentPage);
                                }
                            })
                    .setNegativeButton(org.ei.opensrp.path.R.string.no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                    .show();
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }
}

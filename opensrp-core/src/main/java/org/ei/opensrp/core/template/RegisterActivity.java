package org.ei.opensrp.core.template;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.R;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;

public abstract class RegisterActivity extends SecuredActivity {
    public static final String DIALOG_TAG = "dialog";

    private RegisterActivityPagerAdapter mPagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().hide();

        String[] formNames = this.buildFormNameList();
        Fragment[] otherFragl = null;

        DetailFragment detailFrg = getDetailFragment();
        if (detailFrg != null){
            otherFragl = new Fragment[]{detailFrg};
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new RegisterActivityPagerAdapter((OpenSRPViewPager) findViewById(R.id.view_pager),
                getSupportFragmentManager(), formNames, makeBaseFragment(), otherFragl);
        mPagerAdapter.onPageChanged(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onPageChanged(position);
            }
        });
        onCreateActivity(savedInstanceState);
    }

    protected abstract RegisterDataGridFragment makeBaseFragment();

    public RegisterDataGridFragment getBaseFragment(){
        return (RegisterDataGridFragment) mPagerAdapter.getBaseFragment();
    }

    public DetailFragment getDetailFragment(){
        return null;
    }

    public void onPageChanged(int page) {
        Log.v(getClass().getName(), " onPageChanged "+page);
        if(page == 0 || mPagerAdapter.isFormFragment(page)){
            //setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected abstract String[] buildFormNameList() ;

    protected void onCreateActivity(Bundle savedInstanceState) {
    }

    @Override
    protected void onCreation() { }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }
//todo not tested
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        android.app.Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        RegisterDialogFragment.newInstance(this, dialogOptionModel, tag).show(ft, DIALOG_TAG);
    }

    public void showDetailFragment(CommonPersonObjectClient client, boolean landscape){
        showDetailFragment(client, landscape, null);
    }

    public void showDetailFragment(CommonPersonObjectClient client, boolean landscape, Map extras){
        setRequestedOrientation(landscape?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Fragment pf = mPagerAdapter.getOtherFragment(0);
        Log.v(getClass().getName(), "showDetailFragment "+pf.toString());

        if(extras != null) client.getDetails().putAll(extras);

        ((DetailFragment)pf).resetView(client);
        mPagerAdapter.showOtherFragment(0);
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

    public void saveFormSubmission(final String formSubmission, final String id, final String formName, final JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            ZiggyService ziggyService = org.ei.opensrp.Context.getInstance().ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            new AlertDialog.Builder(this)
                .setMessage(R.string.form_saved_success_dialog_message)
                .setTitle(R.string.form_saved_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button_label,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            closeForm(formName, submission, fieldOverrides); // pass data to let fragment know which record to display
                        }
                    })
                .show();
        } catch (Exception e) {
            e.printStackTrace();

            new AlertDialog.Builder(this)
                .setMessage((getResources().getString(R.string.form_saved_failed_dialog_message))+" : "+e.getMessage())
                .setTitle(R.string.form_saved_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button_label,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            closeForm(formName, null, fieldOverrides); // pass data to let fragment know which record to display
                        }
                    }).show();
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v(getClass().getName(), "Going to launch DisplayFormFragment for "+formName+" for entity "+entityId);
        Log.v(" with fieldoverride", metaData);
        try {
            FormFragment displayFormFragment = (FormFragment) mPagerAdapter.getFormFragment(formName);
            displayFormFragment.resetFormData(entityId, metaData, false);
            mPagerAdapter.showForm(formName);
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

    public void closeForm(final String formName, final FormSubmission data, final JSONObject overrides) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (StringUtils.isNotBlank(formName)){
                    FormFragment displayFormFragment = (FormFragment) mPagerAdapter.getFormFragment(formName);
                    if (displayFormFragment != null){
                        displayFormFragment.hideTranslucentProgressDialog();
                    }
                }
                RegisterDataGridFragment registerFragment = (RegisterDataGridFragment) mPagerAdapter.getBaseFragment();
                if (registerFragment != null && data != null) {
                    String id = data.getFieldValue(postFormSubmissionRecordFilterField());
                    if (StringUtils.isBlank(id)) {
                        try {
                            id = overrides.getString(postFormSubmissionRecordFilterField());
                        } catch (JSONException e) {
                            id = "";
                            e.printStackTrace();
                        }
                    }
                    registerFragment.onFilterManual(id);
                } else {
                    registerFragment.onFilterManual("");//clean up search filter
                }
              mPagerAdapter.showBaseFragment();
            }
        });
    }

    public abstract String postFormSubmissionRecordFilterField();

    /*public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }*/

   /* public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }*/

    private ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        if (mPagerAdapter.isFormFragment(mPagerAdapter.getCurrentPage())) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.form_back_confirm_dialog_message)
                .setTitle(R.string.form_back_confirm_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        closeForm(null, null, null);
                    }
                })
                .setNegativeButton(R.string.no_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                })
                .show();
        } else if (mPagerAdapter.isBaseFragment(mPagerAdapter.getCurrentPage())) {
            progressDialog = ProgressDialog.show(this, "Wait", "Going back to home...", true);
            super.onBackPressed(); // allow back key only if we are
        } else {
            mPagerAdapter.showBaseFragment();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.v(getClass().getName(), "Destroying Activity");

        if ( progressDialog != null && progressDialog.isShowing() ){
            progressDialog.dismiss();
        }

        mPagerAdapter.cleanup();
    }
}

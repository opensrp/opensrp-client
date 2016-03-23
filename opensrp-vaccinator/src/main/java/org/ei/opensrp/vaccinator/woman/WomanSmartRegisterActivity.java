package org.ei.opensrp.vaccinator.woman;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import org.ei.opensrp.commonregistry.CommonObjectFilterOption;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.fragment.WomanSmartRegisterFragment;
import org.ei.opensrp.vaccinator.pageadapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.ClientlessOpenFormOption;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 13-Oct-15.
 */
public class WomanSmartRegisterActivity extends SecuredNativeSmartRegisterActivity  {

    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;
    private android.support.v4.app.Fragment mProfileFragment = null;

    private FormController formControllerown;
    private HashMap<String,String> overrides;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getWindow().getDecorView().setBackgroundDrawable(null);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.formControllerown =new FormController(this);
        formNames = this.buildFormNameList();
        mBaseFragment = new WomanSmartRegisterFragment(this.formControllerown);


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
    }

    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return  null;
    }//end of method


    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }//end of method

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }


    @Override
    protected void onInitialization() {

    }//end of method

    @Override
    protected void onCreation() {


    }//end of method

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("woman_enrollment_form");
        formNames.add("woman_followup_form");
        DialogOption[] options = getEditOptions(null);
        for (int i = 0; i < options.length; i++){
            formNames.add(((ClientlessOpenFormOption) options[i]). getFormName());
        }
        return formNames.toArray(new String[formNames.size()]);
    }


    private DialogOption[] getEditOptions( HashMap<String,String> overridemap ) {
Log.d("form open", "enrollments form by activity");
        return new DialogOption[]{

                new ClientlessOpenFormOption("Enrollment", "woman_enrollment_form", this.formControllerown,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault)
                  };
    }
    @Override
    public void startRegistration() {

    }//end of method

    @Override
    public void setupViews() {


    }//end of method

    @Override
    protected void onResumption() {

    }//end of method






 /*   private class EditDialogOptionModel implements DialogOptionModel {
        private  HashMap<String,String> overrides1;

        public EditDialogOptionModel(HashMap<String,String> overrides1) {
            this.overrides1=overrides1;
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
    }*/



    public void addChildToList(ArrayList<DialogOption> dialogOptionslist,Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist,entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                Log.d("ANM Details", "location name :" + name);
                dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ", "_"), "location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails, name));

            }
        }
    }

  /*  public enum ByColumnAndByDetails{
        byColumn,byDetails,bydefault;
    }
*/
    private void startFollowupForms(String formName,SmartRegisterClient client ,HashMap<String , String> overrideStringmap , WomanDateSort.ByColumnAndByDetails byColumnAndByDetails) {


        if(overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");

        }else{
            JSONObject overridejsonobject = new JSONObject();
            try {
                for (Map.Entry<String, String> entry : overrideStringmap.entrySet()) {
                    switch (byColumnAndByDetails){
                        case byDetails:
                            overridejsonobject.put(entry.getKey() , ((CommonPersonObjectClient)client).getDetails().get(entry.getValue()));
                            break;
                        case byColumn:
                            overridejsonobject.put(entry.getKey() , ((CommonPersonObjectClient)client).getColumnmaps().get(entry.getValue()));
                            break;
                        default:
                            overridejsonobject.put(entry.getKey() ,entry.getValue());
                            break;
                    }
                }
//                overridejsonobject.put("existing_MWRA", );
            }catch (Exception e){
                e.printStackTrace();
            }
            // org.ei.opensrp.util.Log.logDebug("overrides data is : " + overrideStringmap);
            FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }

    private Map<String,String> locations =new HashMap<String,String>();


    public void addChildToList(Map<String,TreeNode<String, Location>> locationMap, String locationTag){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
boolean tagFound=false;
            if(entry.getValue() != null) {
                Location l= entry.getValue().getNode();

                for(String s:l.getTags()){
                    if(s.equalsIgnoreCase(locationTag))
                    {
                        locations.put(locationTag,l.getName());
                        tagFound=true;
                        return;
                    }
                }



            }
            if(!tagFound){
                if(entry.getValue().getChildren()!=null) {
                    addChildToList(entry.getValue().getChildren(), locationTag);
                }
                }
        }
    }
    /////////////////////////





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

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v("fieldoverride", metaData);

        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset

            if (entityId != null || metaData != null){
                String data = FormUtils.getInstance(getApplicationContext()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);

                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);

                if (displayFormFragment != null) {
                    displayFormFragment.setFormData(data);
                    displayFormFragment.loadFormData();
                    displayFormFragment.setRecordId(entityId);
                    displayFormFragment.setFieldOverides(metaData);

                    Log.v("in activity ", "startFormActivity method start");
                }
            }

            mPager.setCurrentItem(formIndex, false); //Don't animate the view on orientation change the view disapears

        }catch (Exception e){
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
        if (currentPage != 0){
            switchToBaseFragment(null);
        }else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }




}

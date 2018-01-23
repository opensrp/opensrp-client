package org.smartregister.indonesia.pnc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.domain.form.FieldOverrides;
import org.smartregister.domain.form.FormSubmission;
import org.smartregister.enketo.view.fragment.DisplayFormFragment;
import org.smartregister.indonesia.LoginActivity;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.fragment.NativeKIPNCSmartRegisterFragment;
import org.smartregister.indonesia.pageradapter.BaseRegisterActivityPagerAdapter;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.service.ZiggyService;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.util.FormUtils;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.dialog.DialogOption;
import org.smartregister.view.dialog.LocationSelectorDialogFragment;
import org.smartregister.view.dialog.OpenFormOption;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.formula.Support;

import static org.smartregister.indonesia.AllConstantsINA.FormNames.KARTU_IBU_PNC_CLOSE;
import static org.smartregister.indonesia.AllConstantsINA.FormNames.KARTU_IBU_PNC_OA;
import static org.smartregister.indonesia.AllConstantsINA.FormNames.KARTU_IBU_PNC_POSPARTUM_KB;
import static org.smartregister.indonesia.AllConstantsINA.FormNames.KARTU_IBU_PNC_VISIT;

/**
 * Created by Dimas Ciputra on 3/5/15.
 */
public class NativeKIPNCSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements LocationSelectorDialogFragment.OnLocationSelectedListener{
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    public static final String TAG = NativeKIPNCSmartRegisterActivity.class.getSimpleName();
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;


    ZiggyService ziggyService;

    NativeKIPNCSmartRegisterFragment nf = new NativeKIPNCSmartRegisterFragment();

    Map<String, String> FS = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        formNames = this.buildFormNameList();
//        mBaseFragment = new NativeKIPNCSmartRegisterFragment();
//        mBaseFragment = new NativeKISmartRegisterFragment(); // Relace by followed
//        WD
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            boolean mode_face = extras.getBoolean("org.smartregister.indonesia.face.face_mode");
            String base_id = extras.getString("org.smartregister.indonesia.face.base_id");
            double proc_time = extras.getDouble("org.smartregister.indonesia.face.proc_time");
//            Log.e(TAG, "onCreate: "+proc_time );

//            TEST
//            mode_face = true;
//            base_id = "eb3b415b-abf9-4a3d-902c-cdcd8307c7eb";
//            Log.e(TAG, "onCreate: mode_face "+mode_face );

            if (mode_face){
                nf.setCriteria(base_id);
                mBaseFragment = new NativeKIPNCSmartRegisterFragment();

//                CommonPersonObject cpo = new CommonPersonObject(base_id, null, null, null);
//                CommonPersonObjectClient pc = new CommonPersonObjectClient(base_id, null, null);
//                AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
//                final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());

                Log.e(TAG, "onCreate: id " + base_id);
                showToast("id "+base_id);
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("Is it Right Person ?");
//                builder.setTitle("Is it Right Clients ?" + base_id);
//                builder.setTitle("Is it Right Clients ?"+ pc.getName());

                // TODO : get name by base_id
//                builder.setMessage("Process Time : " + proc_time + " s");

                builder.setNegativeButton("CANCEL", listener);
                builder.setPositiveButton("YES", listener);
                builder.show();
            }
        } else {
            mBaseFragment = new NativeKIPNCSmartRegisterFragment();
        }

        String KIStart = timer.format(new Date());
        Map<String, String> KI = new HashMap<String, String>();
        KI.put("start", KIStart);
        FlurryAgent.logEvent("PNC_dashboard", KI, true);

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

        ziggyService = context().ziggyService();
    }
    public void onPageChanged(int page){
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LoginActivity.setLanguage();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {return null;}

    @Override
    protected void setupViews() {


    }

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

    public DialogOption[] getEditOptions() {
        return new DialogOption[]{
                new OpenFormOption(getString(R.string.str_pnc_visit_form), KARTU_IBU_PNC_VISIT, formController),
                new OpenFormOption(getString(R.string.str_pnc_postpartum_family_planning_form), KARTU_IBU_PNC_POSPARTUM_KB, formController),
              //  new OpenFormOption("Edit PNC ", KARTU_IBU_PNC_EDIT, formController),
                new OpenFormOption(getString(R.string.str_pnc_close_form), KARTU_IBU_PNC_CLOSE, formController),


        };


    }

    @Override
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
            //   JSONObject uniqueId = new JSONObject(context.uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
            //   Iterator<String> iter = uniqueId.keys();

            //  while (iter.hasNext()) {
            //      String key = iter.next();
            //       combined.put(key, uniqueId.get(key));
            //    }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity(KARTU_IBU_PNC_OA, null, fieldOverrides.getJSONString());
        }
    }
    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);
            ziggyService.saveForm(getParams(submission), submission.instance());
            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
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
        //end capture flurry log for FS
        String end = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("end", end);
        FlurryAgent.logEvent(formName,FS, true);
    }

    /*@Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        FlurryFacade.logEvent(formName);
//        Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
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

    }*/
    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
//        Log.v("fieldoverride", metaData);
        //  FlurryFacade.logEvent(formName);
        if(Support.ONSYNC) {
            Toast.makeText(this, "Data still Synchronizing, please wait", Toast.LENGTH_SHORT).show();
            return;
        }
        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("start", start);
        FlurryAgent.logEvent(formName,FS, true );
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
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
        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add(KARTU_IBU_PNC_VISIT);
        formNames.add(KARTU_IBU_PNC_POSPARTUM_KB);
      //  formNames.add(KARTU_IBU_PNC_EDIT);
        formNames.add(KARTU_IBU_PNC_CLOSE);
        formNames.add(KARTU_IBU_PNC_OA);

      //  formNames.add(KARTU_IBU_ANC_EDIT);
        formNames.add(KARTU_IBU_PNC_CLOSE);

        //    DialogOption[] options = getEditOptions();
        //  for (int i = 0; i < options.length; i++) {
        //       formNames.add(((OpenFormOption) options[i]).getFormName());
        //   }
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
        String KIEnd = timer.format(new Date());
        Map<String, String> KI = new HashMap<String, String>();
        KI.put("end", KIEnd);
        FlurryAgent.logEvent("PNC_dashboard",KI, true );
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

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String face_end = timer.format(new Date());
            FS.put("face_end", face_end);
            if (which == -1 ){
                nf.setCriteria("!");
                currentPage = 0;
                Log.e(TAG, "onClick: YES " + currentPage);
                FlurryAgent.logEvent(TAG+"search_by_face OK", FS, true);

            } else {
                nf.setCriteria("");
                onBackPressed();
                Log.e(TAG, "onClick: NO " + currentPage);
                FlurryAgent.logEvent(TAG + "search_by_face NOK", FS, true);

                Intent intent= new Intent(NativeKIPNCSmartRegisterActivity.this,NativeKIPNCSmartRegisterActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//            Toast.makeText(NativeKISmartRegisterActivity.this, mBaseFragment.toString(), Toast.LENGTH_SHORT).show();
            }


        }
    };

}

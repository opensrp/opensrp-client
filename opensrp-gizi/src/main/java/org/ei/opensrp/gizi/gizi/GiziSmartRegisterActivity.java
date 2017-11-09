package org.ei.opensrp.gizi.gizi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.gizi.LoginActivity;
import org.ei.opensrp.gizi.fragment.GiziSmartRegisterFragment;
import org.ei.opensrp.gizi.pageradapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.gizi.LoginActivity;
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.gizi.fragment.GiziSmartRegisterFragment;
import org.ei.opensrp.gizi.pageradapter.BaseRegisterActivityPagerAdapter;
import org.ei.opensrp.sync.ClientProcessor;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import util.ZScore.ZScoreSystemCalculation;
import util.formula.Support;

public class GiziSmartRegisterActivity extends SecuredNativeSmartRegisterActivity implements
        LocationSelectorDialogFragment.OnLocationSelectedListener{

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static final String TAG = GiziSmartRegisterActivity.class.getSimpleName();
    @Bind(R.id.view_pager)
    OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;
    private int currentPage;
    private String[] formNames = new String[]{};
    private android.support.v4.app.Fragment mBaseFragment = null;

    ZiggyService ziggyService;

    GiziSmartRegisterFragment nf = new GiziSmartRegisterFragment();

    Map<String, String> FS = new HashMap<>();

    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        String GiziStart = timer.format(new Date());
        Map<String, String> Gizi = new HashMap<>();
        Gizi.put("start", GiziStart);

        formNames = this.buildFormNameList();

        extras = getIntent().getExtras();

        if (extras != null) {
            boolean mode_face = extras.getBoolean("org.ei.opensrp.indonesia.face.face_mode");
            String base_id = extras.getString("org.ei.opensrp.indonesia.face.base_id");
            double proc_time = extras.getDouble("org.ei.opensrp.indonesia.face.proc_time");

            if (mode_face) {
                nf.setCriteria(base_id);
                mBaseFragment = new GiziSmartRegisterFragment();

                Log.e(TAG, "onCreate: id " + base_id);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Is it Right Person ?");

                // TODO : get name by base_id
                builder.setNegativeButton("CANCEL", listener );
                builder.setPositiveButton("YES", listener );
                builder.show();
            }

        } else {
            mBaseFragment = new GiziSmartRegisterFragment();
        }

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

        if(LoginActivity.generator.uniqueIdController().needToRefillUniqueId(LoginActivity.generator.UNIQUE_ID_LIMIT)) {
            String toastMessage =  String.format("need to refill unique id, its only %s remaining",
                                   LoginActivity.generator.uniqueIdController().countRemainingUniqueId());
            Toast.makeText(context().applicationContext(), toastMessage, Toast.LENGTH_LONG).show();
        }
        ziggyService = context().ziggyService();
    }
    public void onPageChanged(int page){
        setRequestedOrientation(page == 0
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
    protected void onInitialization() {
        context().formSubmissionRouter().getHandlerMap().put("kunjungan_gizi", new KmsHandler());
    }

    @Override
    public void startRegistration() {
    }

    public DialogOption[] getEditOptions() {
            return new DialogOption[]{
                new OpenFormOption("Kunjungan Per Bulan ", "kunjungan_gizi", formController),
                new OpenFormOption("Close Form","close_form",formController)
            };
    }


    @Override
    public void saveFormSubmission(String formSubmission, String id, String formName, JSONObject fieldOverrides){
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try{
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            ClientProcessor.getInstance(getApplicationContext()).processClient();

            context().formSubmissionService().updateFTSsearch(submission);
            context().formSubmissionRouter().handleSubmission(submission, formName);
            switchToBaseFragment(formSubmission); // Unnecessary!! passing on data

            if(formName.equals("registrasi_gizi")) {
                saveuniqueid();
            }
            //end capture flurry log for FS
            String end = timer.format(new Date());
            Map<String, String> FS = new HashMap<String, String>();
            FS.put("end", end);
            FlurryAgent.logEvent(formName, FS, true);
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
    public void OnLocationSelected(String locationJSONString) {
        JSONObject combined = null;

        try {
            JSONObject locationJSON = new JSONObject(locationJSONString);
            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());

            combined = locationJSON;
            Iterator<String> iter = uniqueId.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                combined.put(key, uniqueId.get(key));
            }

            System.out.println("injection string: " + combined.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (combined != null) {
            FieldOverrides fieldOverrides = new FieldOverrides(combined.toString());
            startFormActivity("registrasi_gizi", null, fieldOverrides.getJSONString());
        }
    }

    public void saveuniqueid() {
        try {
            JSONObject uniqueId = new JSONObject(LoginActivity.generator.uniqueIdController().getUniqueIdJson());
            String uniq = uniqueId.getString("unique_id");
            LoginActivity.generator.uniqueIdController().updateCurrentUniqueId(uniq);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static boolean out;

    @Override
    public void startFormActivity(final String formName, final String entityId, final String metaData) {
        if(Support.ONSYNC) {
            Toast.makeText(this, "Data still Synchronizing, please wait", Toast.LENGTH_SHORT).show();
            return;
        }

        String start = timer.format(new Date());
        Map<String, String> FS = new HashMap<String, String>();
        FS.put("start", start);
        FlurryAgent.logEvent(formName, FS, true);

        if(formName.equals("kunjungan_gizi")) {
            if(getNumOfChild()<4)
                activatingForm(formName,entityId,metaData);
            else {
                final int choice = new java.util.Random().nextInt(3);
                CharSequence[] selections = selections(choice, entityId);

                final AlertDialog.Builder builder = new AlertDialog.Builder(GiziSmartRegisterActivity.this);
                builder.setTitle(context().getStringResource(R.string.reconfirmChildName));
                builder.setItems(selections, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        if (which == choice) {
                            activatingForm(formName, entityId, metaData);
                        }
                    }
                });
                builder.show();
            }
        }
        else{
            activatingForm(formName,entityId,metaData);
        }
    }

    private void activatingForm(String formName, String entityId, String metaData){
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1; // add the offset
            if (entityId != null || metaData != null){
                String data = null;
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

    /**
     * Get 3 children name, 1 determined and 2 random. the determined one will be generated based on
     * @entityId and stored to index @choice of char sequence array.
     * @param choice
     * @param entityId
     * @return
     */
    private CharSequence[] selections(int choice, String entityId){
        String name = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak").findByCaseID(entityId).getColumnmaps().get("namaBayi");
        System.out.println("start form activity / nama = " + name);
        CharSequence selections[] = new CharSequence[]{name, name, name};

        selections[choice] = (CharSequence) name;

        String query = "SELECT namaBayi FROM ec_anak where ec_anak.is_closed = 0";
        Cursor cursor = context().commonrepository("ec_anak").RawCustomQueryForAdapter(query);
        cursor.moveToFirst();

        for (int i = 0; i < selections.length; i++) {
            if (i != choice) {
                cursor.move(new java.util.Random().nextInt(cursor.getCount()));
                String temp = cursor.getString(cursor.getColumnIndex("namaBayi"));
                if(temp==null)
                    i--;
                else if (temp.equals(name))
                    i--;
                else
                    selections[i] = (CharSequence) temp;
                cursor.moveToFirst();
            }
        }
        cursor.close();

        return selections;
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
        nf.setCriteria("!");
        Log.e(TAG, "onBackPressed: "+currentPage );

        if (currentPage != 0) {
            switchToBaseFragment(null);
        } else {
            super.onBackPressed(); // allow back key only if we are
        }
    }

    private String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("registrasi_gizi");
        formNames.add("kunjungan_gizi");
        formNames.add("close_form");
        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retrieveAndSaveUnsubmittedFormData();
        String GiziEnd = timer.format(new Date());
        Map<String, String> Gizi = new HashMap<String, String>();
        Gizi.put("end", GiziEnd);
        FlurryAgent.logEvent("Gizi_dashboard",Gizi, true );
    }

    public void retrieveAndSaveUnsubmittedFormData(){
        if (currentActivityIsShowingForm()){
            DisplayFormFragment formFragment = getDisplayFormFragmentAtIndex(currentPage);
            formFragment.saveCurrentFormData();
        }
    }

    private int getNumOfChild(){
        Cursor childcountcursor = context().commonrepository("ec_anak").RawCustomQueryForAdapter(new SmartRegisterQueryBuilder().queryForCountOnRegisters("ec_anak_search", "ec_anak_search.is_closed=0"));
        childcountcursor.moveToFirst();
        int childcount= childcountcursor.getInt(0);
        childcountcursor.close();
        return childcount;
    }

    private boolean currentActivityIsShowingForm(){
        return currentPage != 0;
    }

    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String face_end = timer.format(new Date());
            FS.put("face_end", face_end);

            Log.e(TAG, "onClick: which "+ which );
            nf.setCriteria("!");

            if (which == -1 ){
                currentPage = 0;
                Log.e(TAG, "onClick: YES ");
                FlurryAgent.logEvent(TAG+"search_by_face OK", true);

            } else {
                Log.e(TAG, "onClick: NO "+currentPage);
                FlurryAgent.logEvent(TAG+"search_by_face NOK", true);

                onBackPressed();

                Intent intent= new Intent(GiziSmartRegisterActivity.this, GiziSmartRegisterActivity.class);
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }


        }
    };


}

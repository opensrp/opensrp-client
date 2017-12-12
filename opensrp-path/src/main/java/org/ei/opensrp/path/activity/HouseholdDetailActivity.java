package org.ei.opensrp.path.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.Vaccine_typesRepository;
import org.ei.opensrp.path.toolbar.LocationSwitcherToolbar;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.DateUtils;
import util.ImageUtils;
import util.JsonFormUtils;
import util.PathConstants;
import util.Utils;

import static util.Utils.fillValue;
import static util.Utils.getValue;

/**
 * Created by habib on 25/07/17.
 */
public class HouseholdDetailActivity extends BaseActivity {
    ListView householdList;
    private LocationSwitcherToolbar toolbar;
    public org.ei.opensrp.Context context;
    public Vaccine_typesRepository VTR;


    private CommonPersonObjectClient householdDetails;
    private static final String EXTRA_HOUSEHOLD_DETAILS = "household_details";


    static final int REQUEST_TAKE_PHOTO = 1;
    public static Gender gender;
    private File currentfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());


        toolbar = (LocationSwitcherToolbar) getToolbar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseholdDetailActivity.this, ChildSmartRegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_HOUSEHOLD_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                householdDetails = (CommonPersonObjectClient) serializable;
            }
        }
        Logger.largeErrorLog("-------------",householdDetails.getDetails().toString());
        Logger.largeErrorLog("-------------",householdDetails.getDetails().get("_id"));


        DrawerLayout drawer = (DrawerLayout) findViewById(getDrawerLayoutId());
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        int toolbarResource = R.drawable.vertical_separator_male;
        toolbar.updateSeparatorView(toolbarResource);
        toolbar.init(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Household Details");


        ((TextView) findViewById(R.id.name_tv)).setText(householdDetails.getDetails().get("first_name"));
        ((TextView) findViewById(R.id.child_id_tv)).setText("HHID : " +householdDetails.getDetails().get("HHID"));
        String dobString = getValue(householdDetails.getDetails(), "dob", false);
        String durationString = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtils.getDuration(birthDateTime);
                if (duration != null) {
                    durationString = duration;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        ((TextView) findViewById(R.id.age_tv)).setText("Age : " + durationString);
        ImageView profileImageIV = (ImageView)findViewById(R.id.profile_image_iv);
        String entityid = householdDetails.getDetails().get("_id");
        if(entityid!=null) {
            profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, entityid);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(entityid, OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, R.drawable.houshold_register_placeholder, R.drawable.houshold_register_placeholder));

        }
        profileImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView nameInitials = (TextView)findViewById(R.id.name_inits);
        nameInitials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
                if (!drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        AllSharedPreferences allSharedPreferences = org.ei.opensrp.Context.getInstance().allSharedPreferences();
        String preferredName = allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
        if (!preferredName.isEmpty()) {
            String[] preferredNameArray = preferredName.split(" ");
            String initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
            nameInitials.setText(initials);
        }

//        toolbar.setOnLocationChangeListener(this);
//

        initQueries();


        context = org.ei.opensrp.Context.getInstance().updateApplicationContext(this.getApplicationContext());
        VTR = new Vaccine_typesRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(),VaccinatorApplication.createCommonFtsObject(),context.alertService());
        //get Household members repository
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentfile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshadapter();
//        if (requestCode == REQUEST_CODE_GET_JSON) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    String jsonString = data.getStringExtra("json");
//                    Log.d("JSONResult", jsonString);
//
//                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                    AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
//
//                    JSONObject form = new JSONObject(jsonString);
//                    if (form.getString("encounter_type").equals("Death")) {
//                        confirmReportDeceased(jsonString, allSharedPreferences);
//                    } else if (form.getString("encounter_type").equals("Birth Registration")) {
//                        JsonFormUtils.editsave(this, getOpenSRPContext(), jsonString, allSharedPreferences.fetchRegisteredANM(), "Child_Photo", "child", "mother");
//                    } else if (form.getString("encounter_type").equals("AEFI")) {
//                        JsonFormUtils.saveAdverseEvent(jsonString, location_name,
//                                childDetails.entityId(), allSharedPreferences.fetchRegisteredANM());
//                    }
//                    childDataFragment.childDetails = childDetails;
//                    childDataFragment.loadData();
//                } catch (Exception e) {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        } else
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                String imageLocation = currentfile.getAbsolutePath();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
                String entityid = householdDetails.getDetails().get("_id");
                JsonFormUtils.saveImage(this, allSharedPreferences.fetchRegisteredANM(), entityid, imageLocation);
                updateProfilePicture(entityid);
            }
        }
    }

    private void updateProfilePicture(String entityid) {
        if(entityid!=null) {
            ImageView profileImageIV = (ImageView)findViewById(R.id.profile_image_iv);
            profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, entityid);
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(entityid, OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, R.drawable.houshold_register_placeholder, R.drawable.houshold_register_placeholder));

        }
    }

    private void initQueries(){

    }

    private void refreshadapter() {
        //setAdapter data of Household member
        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
        String mother_id = householdDetails.getDetails().get("_id");

        String tableName = PathConstants.MOTHER_TABLE_NAME;
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                tableName + ".relationalid",
                tableName + ".details",
                tableName + ".openmrs_id",
                tableName + ".relational_id",
                tableName + ".first_name",
                tableName + ".last_name",
                tableName + ".gender",
                tableName + ".father_name",
                tableName + ".dob",
                tableName + ".epi_card_number",
                tableName + ".contact_phone_number",
                tableName + ".client_reg_date",
                tableName + ".last_interacted_with"
        });

        Cursor cursor = db.rawQuery(queryBUilder.mainCondition("relational_id = ?"),new String[]{mother_id});


        householdList = (ListView) findViewById(R.id.household_list);

        HouseholdCursorAdpater cursorAdpater = new HouseholdCursorAdpater(getApplicationContext(),cursor);

        householdList.setAdapter(cursorAdpater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshadapter();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        LinearLayout stockregister = (LinearLayout) drawer.findViewById(R.id.stockcontrol);
        ((CustomFontTextView)findViewById(R.id.title)).setText("Household Details");
        stockregister.setBackgroundColor(getResources().getColor(R.color.tintcolor));
    }

    @Override
    protected int getContentView() {
        return  R.layout.household_detail_activity;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        return true;
    }

    @Override
    protected int getDrawerLayoutId() {
        return  R.id.drawer_layout;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected Class onBackActivity() {
        return ChildSmartRegisterActivity.class;
    }

    class HouseholdCursorAdpater extends CursorAdapter {
        private Context context;
        private LayoutInflater inflater = null;

        public HouseholdCursorAdpater(Context context, Cursor c) {
            super(context, c);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public void bindView(View view, final Context context, Cursor cursor) {
            Log.e("------------","bind view call");
            CommonRepository commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(PathConstants.MOTHER_TABLE_NAME);
            CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
            final CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
            pClient.setColumnmaps(personinlist.getColumnmaps());
            TextView member_name = (TextView) view.findViewById(R.id.name_tv);
            TextView member_age = (TextView) view.findViewById(R.id.age_tv);
            member_name.setText("Name : " + cursor.getString(cursor.getColumnIndex("first_name")));
            String dobString = cursor.getString(cursor.getColumnIndex("dob"));
            String durationString = "";
            if (StringUtils.isNotBlank(dobString)) {
                try {
                    DateTime birthDateTime = new DateTime(dobString);
                    String duration = DateUtils.getDuration(birthDateTime);
                    if (duration != null) {
                        durationString = duration;
                    }
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.toString(), e);
                }
            }
            member_age.setText("Age : "+durationString);
            ((LinearLayout)view.findViewById(R.id.profile_name_layout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WomanImmunizationActivity.launchActivity(HouseholdDetailActivity.this,pClient,null);
                }
            });
            ImageView profileImageIV = (ImageView)view.findViewById(R.id.profile_image_iv);

            if (pClient.entityId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, pClient.entityId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pClient.entityId(), OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, R.drawable.woman_path_register_logo, R.drawable.woman_path_register_logo));

            }
            LinearLayout child_added = (LinearLayout) view.findViewById(R.id.children_added);
            child_added.removeAllViews();
            addChild(child_added,pClient.entityId());
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.e("------------","new view call");


            View view = inflater.inflate(R.layout.household_details_list_row,parent,false);

            return  view;
        }

        public void addChild(LinearLayout household_details_list_row, String mother_id){
            Log.e("--------------",mother_id);


            PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
            net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();


            String tableName = PathConstants.CHILD_TABLE_NAME;
            String parentTableName = PathConstants.MOTHER_TABLE_NAME;
            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            queryBUilder.SelectInitiateMainTable(tableName, new String[]{
                    tableName + ".relationalid",
                    tableName + ".details",
                    tableName + ".openmrs_id",
                    tableName + ".relational_id",
                    tableName + ".first_name",
                    tableName + ".last_name",
                    tableName + ".gender",
                    parentTableName + ".first_name as mother_first_name",
                    parentTableName + ".last_name as mother_last_name",
                    parentTableName + ".dob as mother_dob",
                    parentTableName + ".nrc_number as mother_nrc_number",
                    tableName + ".father_name",
                    tableName + ".dob",
                    tableName + ".epi_card_number",
                    tableName + ".contact_phone_number",
                    tableName + ".pmtct_status",
                    tableName + ".provider_uc",
                    tableName + ".provider_town",
                    tableName + ".provider_id",
                    tableName + ".provider_location_id",
                    tableName + ".client_reg_date",
                    tableName + ".last_interacted_with",
                    tableName + ".inactive",
                    tableName + ".lost_to_follow_up"
            });
            queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
            String mainCondition = " (dod is NULL OR dod = '' )";
            String mainSelect = queryBUilder.mainCondition(mainCondition);




            Cursor cursor = db.rawQuery(mainSelect+ "and "+tableName+".relational_id = ?",new String[]{mother_id});



            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                CommonRepository commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(tableName);
                CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
                final CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), personinlist.getDetails().get("FWHOHFNAME"));
                pClient.setColumnmaps(personinlist.getColumnmaps());
                LinearLayout childrenLayout = (LinearLayout)inflater.inflate(R.layout.household_details_child_row, null);
                ((TextView)childrenLayout.findViewById(R.id.name_tv)).setText("Name : " + cursor.getString(cursor.getColumnIndex("first_name")));

                String dobString = cursor.getString(cursor.getColumnIndex("dob"));
                String durationString = "";
                if (StringUtils.isNotBlank(dobString)) {
                    try {
                        DateTime birthDateTime = new DateTime(dobString);
                        String duration = DateUtils.getDuration(birthDateTime);
                        if (duration != null) {
                            durationString = duration;
                        }
                    } catch (Exception e) {
                        Log.e(getClass().getName(), e.toString(), e);
                    }
                }
                ((TextView)childrenLayout.findViewById(R.id.age_tv)).setText("Age : "+durationString);

                Gender gender = Gender.UNKNOWN;
                if (pClient != null && pClient.getDetails() != null) {
                    String genderString = Utils.getValue(pClient, "gender", false);
                    if (genderString != null && genderString.toLowerCase().equals("female")) {
                        gender = Gender.FEMALE;
                    } else if (genderString != null && genderString.toLowerCase().equals("male")) {
                        gender = Gender.MALE;
                    }
                    ImageView profileImageIV = (ImageView)childrenLayout.findViewById(R.id.profile_image_iv);

                    if (pClient.entityId() != null) {//image already in local storage most likey ):
                        //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                        profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, pClient.entityId());
                        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pClient.entityId(), OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, ImageUtils.profileImageResourceByGender(gender), ImageUtils.profileImageResourceByGender(gender)));

                    }
                }


                household_details_list_row.addView(childrenLayout);
                childrenLayout.findViewById(R.id.profile_name_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChildImmunizationActivity.launchActivity(HouseholdDetailActivity.this,pClient,null);
                    }
                });
                cursor.moveToNext();
            }
            cursor.close();

        }

    }
}

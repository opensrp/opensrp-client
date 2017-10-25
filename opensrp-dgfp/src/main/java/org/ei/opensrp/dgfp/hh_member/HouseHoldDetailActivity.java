package org.ei.opensrp.dgfp.hh_member;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.view.fragment.SecuredFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 5/11/15.
 */
public class HouseHoldDetailActivity extends SecuredFragment implements View.OnClickListener{

    //image retrieving



//    private static ImageFetcher mImageFetcher;




    //image retrieving

    public static CommonPersonObjectClient householdclient;
    public static CommonPersonObjectController householdcontroller;
    private SmartRegisterPaginatedCursorAdapter clientsAdapter;
    ListView Clientsview;
    Context context;
    public Button nidbutton;
    private View mView;


    protected void initiallize() {
        context = Context.getInstance();

        TextView householdhead_name = (TextView)mView.findViewById(R.id.name_household_head);
        TextView ward = (TextView)mView.findViewById(R.id.ward);
        TextView gobhhid = (TextView)mView.findViewById(R.id.gobhhid);
        TextView age = (TextView)mView.findViewById(R.id.age);
        TextView nid = (TextView)mView.findViewById(R.id.nid);
        TextView brid = (TextView)mView.findViewById(R.id.brid);
        TextView mobileno = (TextView)mView.findViewById(R.id.mobileno);
        TextView general = (TextView)mView.findViewById(R.id.general);

        ImageButton back = (ImageButton)mView.findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
//                startActivity(new Intent(HouseHoldDetailActivity.this, HH_member_SmartRegisterActivity.class));
//                overridePendingTransition(0, 0);
                ((HH_member_SmartRegisterActivity)getActivity()).switchToBaseFragment(null);
            }
        });


        householdhead_name.setText(householdclient.getColumnmaps().get("HoH_F_Name"));
        ward.setText("Address: "+(humanize((householdclient.getDetails().get("Subunit") != null ? householdclient.getDetails().get("Subunit") : "").replace("+", "_")))+", "+
                (humanize((householdclient.getDetails().get("Village_Name") != null ? householdclient.getDetails().get("Village_Name") : "").replace("+", "_")))+", "+
                (humanize((householdclient.getDetails().get("Mauzapara") != null ? householdclient.getDetails().get("Mauzapara") : "").replace("+", "_")))+", "+
                (humanize((householdclient.getDetails().get("Union") != null ? householdclient.getDetails().get("Union") : "").replace("+", "_")))+", "+
                (humanize((householdclient.getDetails().get("Upazilla") != null ? householdclient.getDetails().get("Upazilla") : "").replace("+", "_")))
        );
        gobhhid.setText(getResources().getString(R.string.hhid_gob) + (householdclient.getColumnmaps().get("GoB_HHID")!=null?householdclient.getColumnmaps().get("GoB_HHID"):""));
        age.setText(", Age: " + (householdclient.getDetails().get("Calc_HoH_Age_Confirm")!=null?householdclient.getDetails().get("Calc_HoH_Age_Confirm"):""));
        nid.setText("NID : " + (householdclient.getDetails().get("HoH_NID")!=null?householdclient.getDetails().get("HoH_NID"):""));
        brid.setText("BRID : " + (householdclient.getDetails().get("HoH_BRID")!=null?householdclient.getDetails().get("HoH_BRID"):""));
        mobileno.setText("Mobile No : " + (householdclient.getDetails().get("HoH_Mobile_number")!=null?householdclient.getDetails().get("HoH_Mobile_number"):""));

        final ImageView householdview = (ImageView)mView.findViewById(R.id.householdprofileview);

        if(householdclient.getDetails().get("profilepic")!= null){
            if((householdclient.getDetails().get("HoH_Gender")!=null?householdclient.getDetails().get("HoH_Gender"):"").equalsIgnoreCase("2")) {

                setImagetoHolderFromUri(getActivity(), householdclient.getDetails().get("profilepic"), householdview, R.mipmap.womanimageload);
            } else if ((householdclient.getDetails().get("HoH_Gender")!=null?householdclient.getDetails().get("HoH_Gender"):"").equalsIgnoreCase("1")){
                setImagetoHolderFromUri(getActivity(), householdclient.getDetails().get("profilepic"), householdview, R.mipmap.householdload);

            }
        }else{

            if((householdclient.getDetails().get("HoH_Gender")!=null?householdclient.getDetails().get("HoH_Gender"):"").equalsIgnoreCase("2")){
                householdview.setImageDrawable(getResources().getDrawable(R.drawable.woman_placeholder));
            }else if ((householdclient.getDetails().get("HoH_Gender")!=null?householdclient.getDetails().get("HoH_Gender"):"").equalsIgnoreCase("1")){
                householdview.setImageDrawable(getResources().getDrawable(R.mipmap.household_profile_thumb));
            }
        }
//        general.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        householdview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH_member_SmartRegisterActivity.bindobject = "household";
                HH_member_SmartRegisterActivity.entityid = householdclient.entityId();
                ((HH_member_SmartRegisterActivity)getActivity()).dispatchTakePictureIntent(householdview);

            }
        });


        Clientsview = (ListView)mView.findViewById(R.id.list);


        Clientsview.setAdapter(adapter(householdclient.getCaseId()));


//        clientsProgressView.setVisibility(View.GONE);


//        paginationViewHandler.refresh();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.household_detail_activity, container, false);
        mView = view;
//        view.findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);
        return view;
    }

    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {


    }

    protected SmartRegisterPaginatedCursorAdapter adapter(String relationalid) {
        CommonRepository commonRepository = context.commonrepository("members");
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable("members", new String[]{"relationalid", "details", "Mem_F_Name", "EDD","Member_Birth_Date", "Child_calc_age","calc_age_confirm", "Member_GOB_HHID", "Marital_status", "Pregnancy_Status"});
        queryBUilder.joinwithALerts("members", "FW CENSUS");
        String mainSelect = queryBUilder.mainCondition(" relationalid = '"+relationalid+"' and (details not like '%\"mother_UUID\"%') ");
        queryBUilder.addCondition("");
//        String Sortqueries = sortByAlertmethod();
//        currentquery  = queryBUilder.orderbyCondition(Sortqueries);
        Cursor c = commonRepository.RawCustomQueryForAdapter(queryBUilder.Endquery(queryBUilder.addlimitandOffset(mainSelect, 200, 0)));
        HH_member_detail_SmartClientsProvider hhscp = new HH_member_detail_SmartClientsProvider(getActivity(),this,context.alertService());
        SmartRegisterPaginatedCursorAdapter clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), c, hhscp, new CommonRepository("members",new String []{"Mem_F_Name","EDD","Member_Birth_Date","Child_calc_age","calc_age_confirm","Member_GOB_HHID","Marital_status","Pregnancy_Status"}));


       return  clientAdapter;
//        return new SmartRegisterPaginatedAdapter(new HouseholdDetailsSmartClientsProvider(this,paginationViewHandler ,householdcontroller));
    }




        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.profilepic:
                    HH_member_SmartRegisterActivity.entityid = ((CommonPersonObjectClient)view.getTag()).entityId();
                HH_member_SmartRegisterActivity.bindobject = "members";
                    mImageView = (ImageView)view;
//                    mImageView.setTag("womanpic");
                ((HH_member_SmartRegisterActivity)getActivity()).dispatchTakePictureIntent((ImageView) view);
                    break;
            case R.id.general:
                    ((HH_member_SmartRegisterActivity)getActivity()).startFormActivity("general", ((CommonPersonObjectClient) view.getTag()).entityId(), null);
                    break;
            case R.id.newborn_pr_fp:
                    CommonPersonObjectClient pc =  ((CommonPersonObjectClient) view.getTag());
                   if((pc.getDetails().get("Is_NewBorn")!=null?pc.getDetails().get("Is_NewBorn"):"").equalsIgnoreCase("1")) {
                       ((HH_member_SmartRegisterActivity) getActivity()).startFormActivity("new_born_information", pc.entityId(), null);
                   }else{
                       ((HH_member_SmartRegisterActivity) getActivity()).startFormActivity("family_planning", pc.entityId(), null);
                   }
                    break;
            }

        }



    String mCurrentPhotoPath;

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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_PHOTO_NID = 9000;
   static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;
    private void dispatchTakePictureIntent(ImageView imageView) {
        mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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


    private void dispatchTakePictureIntentforNId(View imageView) {
//        mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO_NID);
            }
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
////            Bundle extras = data.getExtras();
////            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
////            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
//            HashMap <String,String> details = new HashMap<String,String>();
//            details.put("profilepic",currentfile.getAbsolutePath());
//            saveimagereference(bindobject,entityid,details);
////            BitmapFactory.Options options = new BitmapFactory.Options();
////            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
////            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
////            mImageView.setImageBitmap(bitmap);
////            setImagetoHolder(this,currentfile.getAbsolutePath(),mImageView,R.drawable.householdload);
////            Log.v("see imageview",""+(String)mImageView.getTag());
//            Log.v("see imageview", "" + currentfile.getAbsolutePath());
//            setImagetoHolderFromUri(this, currentfile.getAbsolutePath(), mImageView, R.mipmap.householdload);
//            recalladapterinitialization();
//        }else  if (requestCode == REQUEST_TAKE_PHOTO_NID && resultCode == RESULT_OK) {
//
////            Bundle extras = data.getExtras();
////            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
////            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
//            HashMap <String,String> details = new HashMap<String,String>();
//            details.put("nidImage",currentfile.getAbsolutePath());
//            saveimagereferenceforNID(bindobject,entityid,details);
//            recalladapterinitialization();
//
////            BitmapFactory.Options options = new BitmapFactory.Options();
////            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
////            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
////            mImageView.setImageBitmap(bitmap);
////            setImagetoHolder(this,currentfile.getAbsolutePath(),mImageView,R.drawable.householdload);
//        }
//    }
    public void saveimagereference(String bindobject,String entityid,Map<String,String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid,details);
        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),anmId,entityid,"Image",details.get("profilepic"), ImageRepository.TYPE_Unsynced,"dp");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
//                householdclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public void saveimagereferenceforNID(String bindobject,String entityid,Map<String,String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid,details);
        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),anmId,entityid,"Image",details.get("nidImage"), ImageRepository.TYPE_Unsynced,"nidImage");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
        try {
            nidbutton.setText("");
            nidbutton.setBackground(getDrawableFromPath(details.get("nidImage")));
        }catch (Exception e){

        }
//                householdclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public Drawable getDrawableFromPath(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //Here you can make logic for decode bitmap for ignore oom error.
        return new BitmapDrawable(bitmap);
    }
    public static void setImagetoHolder(Activity activity,String file, ImageView view, int placeholder){
         String TAG = "ImageGridFragment";
         String IMAGE_CACHE_DIR = "thumbs";

        int mImageThumbSize;
        int mImageThumbSpacing;

        mImageThumbSize = 300;
        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR);
             cacheParams.setMemCacheSizePercent(0.80f); // Set memory cache to 25% of app memory
        ImageFetcher mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
        mImageFetcher.setLoadingImage(placeholder);
        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
//        Toast.makeText(activity,file,Toast.LENGTH_LONG).show();
        mImageFetcher.loadImage("file:///"+file,view);

    }
    public static void setImagetoHolderFromUri(Activity activity,String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        Uri external = Uri.fromFile(externalFile);
        view.setImageURI(external);


    }

    public void onBackPressed() {
//        finish();
//        startActivity(new Intent(this, HouseHoldSmartRegisterActivity.class));
//        overridePendingTransition(0, 0);


    }
    public void recalladapterinitialization(){
        Clientsview = null;
        Clientsview = (ListView)mView.findViewById(R.id.list);

    }
}

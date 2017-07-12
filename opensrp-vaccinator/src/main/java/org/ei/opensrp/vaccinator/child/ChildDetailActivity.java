package org.ei.opensrp.vaccinator.child;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.vaccinator.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.ImageCache;
import util.ImageFetcher;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 20-Oct-15.
 */
public class ChildDetailActivity extends Activity {


    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;
    static final int REQUEST_TAKE_PHOTO = 1;
    static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;

    String mCurrentPhotoPath;



    //image retrieving

    public static CommonPersonObjectClient childclient;
    public static CommonPersonObjectController childcontroller;
    private SmartRegisterPaginatedAdapter clientsAdapter;
   // private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Context context = Context.getInstance();
        //setting view
        setContentView(R.layout.child_detail_activity);

        //CHILD BASIC INFORMATION

        TextView childIdTextView =(TextView)findViewById(R.id.childdetail_childid);
        TextView childEpiTextView =(TextView) findViewById(R.id.childdetail_childepino);
        TextView childNameTextView =(TextView) findViewById(R.id.childdetail_childname);
        TextView fatherNameTextView =(TextView) findViewById(R.id.childdetail_fathername);
        TextView childDOBTextView =(TextView) findViewById(R.id.childdetail_childdob);

        //setting value in child basic information textviews
        childIdTextView.setText(childclient.getDetails().get("existing_program_client_id")!=null?childclient.getDetails().get("existing_program_client_id"):"");
        childEpiTextView.setText(childclient.getDetails().get("epi_card_number")!=null?childclient.getDetails().get("epi_card_number"):"");
        childNameTextView.setText(childclient.getDetails().get("first_name")!=null?childclient.getDetails().get("first_name"):"");
        fatherNameTextView.setText(childclient.getDetails().get("father_name")!=null?childclient.getDetails().get("father_name"):"");
        childDOBTextView.setText(childclient.getDetails().get("chid_dob_confirm")!=null?childclient.getDetails().get("chid_dob_confirm"):"");

        //VACCINES INFORMATION
        TextView bcgTextView =(TextView) findViewById(R.id.childdetail_bcg);
        TextView opvTextView =(TextView) findViewById(R.id.childdetail_opv0);
        TextView opv1TextView =(TextView) findViewById(R.id.childdetail_opv1);
        TextView opv2TextView =(TextView) findViewById(R.id.childdetail_opv2);
        TextView opv3TextView =(TextView) findViewById(R.id.childdetail_opv3);
        TextView measles1TextView =(TextView) findViewById(R.id.childdetail_measles1);
        TextView measles2TextView =(TextView) findViewById(R.id.childdetail_measles2);
        TextView pcv1TextView =(TextView) findViewById(R.id.childdetail_pcv1);
        TextView pcv2TextView =(TextView) findViewById(R.id.childdetail_pcv2);
        TextView pcv3TextView =(TextView) findViewById(R.id.childdetail_pcv3);
        TextView penta1TextView =(TextView) findViewById(R.id.childdetail_penta1);
        TextView penta2TextView =(TextView) findViewById(R.id.childdetail_penta2);
        TextView penta3TextView =(TextView) findViewById(R.id.childdetail_penta3);

        //setting vaccines dates
        bcgTextView.setText(childclient.getColumnmaps().get("bcg")!=""?childclient.getColumnmaps().get("bcg"):childclient.getColumnmaps().get("bcg_retro")!=""?childclient.getColumnmaps().get("bcg_retro"):"");
        opvTextView.setText(childclient.getColumnmaps().get("opv_0")!=""?childclient.getColumnmaps().get("opv_0"):childclient.getColumnmaps().get("opv_0_retro")!=""?childclient.getColumnmaps().get("opv_0_retro"):"");
        opv1TextView.setText(childclient.getColumnmaps().get("opv_1") != "" ? childclient.getColumnmaps().get("opv_1") : childclient.getColumnmaps().get("opv_1_retro")!=""?childclient.getColumnmaps().get("opv_1_retro"):"");
        opv2TextView.setText(childclient.getColumnmaps().get("opv_2") != "" ? childclient.getColumnmaps().get("opv_2") : childclient.getColumnmaps().get("opv_2_retro")!=""?childclient.getColumnmaps().get("opv_2_retro"):"");
        opv3TextView.setText(childclient.getColumnmaps().get("opv_3") != "" ? childclient.getColumnmaps().get("opv_3") : childclient.getColumnmaps().get("opv_3_retro")!=""?childclient.getColumnmaps().get("opv_3_retro"):"");
        measles1TextView.setText(childclient.getColumnmaps().get("measles_1") != "" ? childclient.getColumnmaps().get("measles_1") : childclient.getColumnmaps().get("measles_1_retro")!=""?childclient.getColumnmaps().get("measles_1_retro"):"");
        measles2TextView.setText(childclient.getColumnmaps().get("measles_2") != "" ? childclient.getColumnmaps().get("measles_2") : childclient.getColumnmaps().get("measles_2_retro")!=""?childclient.getColumnmaps().get("measles_2_retro"):"");
        pcv1TextView.setText(childclient.getColumnmaps().get("pentavalent_1") != "" ? childclient.getColumnmaps().get("pentavalent_1") : childclient.getColumnmaps().get("pentavalent_1_retro")!=""?childclient.getColumnmaps().get("pentavalent_1_retro"):"");
        pcv2TextView.setText(childclient.getColumnmaps().get("pentavalent_2") != "" ? childclient.getColumnmaps().get("pentavalent_2") : childclient.getColumnmaps().get("pentavalent_2_retro")!=""?childclient.getColumnmaps().get("pentavalent_2_retro"):"");
        pcv3TextView.setText(childclient.getColumnmaps().get("pentavalent_3") != "" ? childclient.getColumnmaps().get("pentavalent_3") : childclient.getColumnmaps().get("pentavalent_3_retro") != "" ? childclient.getColumnmaps().get("pentavalent_3_retro"):"");
        penta1TextView.setText(childclient.getColumnmaps().get("pcv_1") != "" ? childclient.getColumnmaps().get("pcv_1") : childclient.getColumnmaps().get("pcv_1_retro")!=""?childclient.getColumnmaps().get("pcv_1_retro"):"");
        penta2TextView.setText(childclient.getColumnmaps().get("pcv_2") != "" ? childclient.getColumnmaps().get("pcv_2") : childclient.getColumnmaps().get("pcv_2_retro")!=""?childclient.getColumnmaps().get("pcv_2_retro"):"");
        penta3TextView.setText(childclient.getColumnmaps().get("pcv_3") != "" ? childclient.getColumnmaps().get("pcv_3") : childclient.getColumnmaps().get("pcv_3_retro")!=""?childclient.getColumnmaps().get("pcv_3_retro"):"");


        //declaring back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ChildDetailActivity.this, ChildSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        //declaring child profile image
        final ImageView childview = (ImageView)findViewById(R.id.childdetailprofileview);
      //  if(childclient.getDetails().get("profilepic")!= null){
            if(childclient.getDetails().get("gender").equalsIgnoreCase("female")) {
                setImagetoHolder(ChildDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.drawable.child_girl_infant);
            }
            else  if(childclient.getDetails().get("gender").equalsIgnoreCase("male")) {
                setImagetoHolder(ChildDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.drawable.child_boy_infant);

            }
            else{
                setImagetoHolder(ChildDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.drawable.child_transgender_inflant);

            }
       // }
        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "ec_child";
                entityid = childclient.entityId();
                dispatchTakePictureIntent(childview);

            }
        });



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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent(ImageView imageView) {
        mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("child detail camera :",takePictureIntent+"  intent class");
        // Ensure that there's a camera activity to handle the intent
     //   PackageManager packageManager = context.getPackageManager();
       // if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
         if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            Log.d("child detail camera :","in camera");
            File photoFile = null;
            try {

                photoFile = createImageFile();
                Log.d("child detail camera :","in createImageFile");
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentfile = photoFile;
                Log.d("child detail camera :", "in start camera activity");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
       /*else {

           // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                //    Uri.fromFile(photoFile));
            Log.d("child detail camera :", "in else start camera activity");
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
            HashMap<String,String> details = new HashMap<String,String>();
            details.put("profilepic",currentfile.getAbsolutePath());
            saveimagereference(bindobject,entityid,details);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }
    public void saveimagereference(String bindobject,String entityid,Map<String,String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid,details);
//                householdclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public static void setImagetoHolder(Activity activity,String file, ImageView view, int placeholder){
        mImageThumbSize = 300;
        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 25% of app memory
        mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
        mImageFetcher.setLoadingImage(placeholder);
        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
//        Toast.makeText(activity,file,Toast.LENGTH_LONG).show();
        mImageFetcher.loadImage("file:///"+file,view);

//        Uri.parse(new File("/sdcard/cats.jpg")






//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
//        view.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, ChildSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

package org.ei.opensrp.vaccinator.woman;

import android.app.Activity;
import android.content.Intent;
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
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.ImageCache;
import util.ImageFetcher;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 11-Nov-15.
 */
public class WomanDetailActivity extends Activity {

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

    public static CommonPersonObjectClient womanclient;
    public static CommonPersonObjectController childcontroller;
    private SmartRegisterPaginatedAdapter clientsAdapter;
    // private final PaginationViewHandler paginationViewHandler = new PaginationViewHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //setting view
        setContentView(R.layout.woman_detail_activity);

        //WOMAN BASIC INFORMATION
        TextView womanIdTextView =(TextView)findViewById(R.id.womandetail_womanid);
        TextView womanNameTextView =(TextView)findViewById(R.id.womandetail_name);
        TextView womanHusbandTextView =(TextView)findViewById(R.id.womandetail_husbandname);
        TextView noChildrenTextView =(TextView)findViewById(R.id.womandetail_nochildren);
        TextView womanDOBTextView =(TextView)findViewById(R.id.womandetail_womandob);

        //setting value in WOMAN basic information textviews
        womanIdTextView.setText(womanclient.getDetails().get("existing_program_client_id")!=null?womanclient.getDetails().get("existing_program_client_id"):"");
        womanNameTextView.setText(womanclient.getDetails().get("first_name")!=null?womanclient.getDetails().get("first_name"):"");
        womanHusbandTextView.setText(womanclient.getDetails().get("husband_name")!=null?womanclient.getDetails().get("husband_name"):"");
        noChildrenTextView.setText(womanclient.getDetails().get("existing_program_client_id")!=null?womanclient.getDetails().get("existing_program_client_id"):"");
        womanDOBTextView.setText(womanclient.getDetails().get("client_dob_confirm")!=null?womanclient.getDetails().get("client_dob_confirm"):"");

        //VACCINES INFORMATION
        TextView tt1TextView =(TextView) findViewById(R.id.womandetail_tt1);
        TextView tt2TextView =(TextView) findViewById(R.id.womandetail_tt2);
        TextView tt3TextView =(TextView) findViewById(R.id.womandetail_tt3);
        TextView tt4TextView =(TextView) findViewById(R.id.womandetail_tt4);
        TextView tt5TextView =(TextView) findViewById(R.id.womandetail_tt5);

        tt1TextView.setText(womanclient.getColumnmaps().get("tt1") != "" ? womanclient.getColumnmaps().get("tt1") : womanclient.getColumnmaps().get("tt1_retro")!=""?womanclient.getColumnmaps().get("tt1_retro"):"");
        tt2TextView.setText(womanclient.getColumnmaps().get("tt2") != "" ? womanclient.getColumnmaps().get("tt2") : womanclient.getColumnmaps().get("tt2_retro")!=""?womanclient.getColumnmaps().get("tt2_retro"):"");
        tt3TextView.setText(womanclient.getColumnmaps().get("tt3") != "" ? womanclient.getColumnmaps().get("tt3") : womanclient.getColumnmaps().get("tt3_retro")!=""?womanclient.getColumnmaps().get("tt3_retro"):"");
        tt4TextView.setText(womanclient.getColumnmaps().get("tt4") != "" ? womanclient.getColumnmaps().get("tt4") : womanclient.getColumnmaps().get("tt4_retro")!=""?womanclient.getColumnmaps().get("tt4_retro"):"");
        tt5TextView.setText(womanclient.getColumnmaps().get("tt5") != "" ? womanclient.getColumnmaps().get("tt5") : womanclient.getColumnmaps().get("tt5_retro")!=""?womanclient.getColumnmaps().get("tt5_retro"):"");


        //declaring back button
        ImageButton back = (ImageButton)findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(WomanDetailActivity.this, WomanSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });


        //declaring child profile image
        final ImageView womanview = (ImageView)findViewById(R.id.womandetailprofileview);

        womanview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "ec_woman";
                entityid = womanclient.entityId();
                dispatchTakePictureIntent(womanview);

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
     //   Log.d("woman detail camera :", takePictureIntent + "  intent class");
        // Ensure that there's a camera activity to handle the intent
        //   PackageManager packageManager = context.getPackageManager();
        // if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
          //  Log.d("woman detail camera :","in camera");
            File photoFile = null;
            try {

                photoFile = createImageFile();
              //  Log.d("woman detail camera :","in createImageFile");
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentfile = photoFile;
               // Log.d("woman detail camera :", "in start camera activity");
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
            saveimagereference(bindobject, entityid, details);
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
        startActivity(new Intent(this, WomanSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

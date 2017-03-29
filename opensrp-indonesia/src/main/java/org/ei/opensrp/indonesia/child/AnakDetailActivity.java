package org.ei.opensrp.indonesia.child;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.application.BidanApplication;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.kartu_ibu.NativeKISmartRegisterActivity;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.OpenSRPImageLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.util.StringUtil.humanize;
/**
 * Created by Iq on 07/09/16.
 */
public class AnakDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = AnakDetailActivity.class.getSimpleName();
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    public static CommonPersonObjectClient childclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.child_detail_activity);

        final ImageView childview = (ImageView)findViewById(R.id.childdetailprofileview);
        //header
        TextView today = (TextView) findViewById(R.id.detail_today);
        
        //profile
        TextView nama = (TextView) findViewById(R.id.txt_child_name);
        TextView mother = (TextView) findViewById(R.id.txt_mother_name);
        TextView father = (TextView) findViewById(R.id.txt_father_number);
        TextView dob = (TextView) findViewById(R.id.txt_dob);
        
      //  TextView phone = (TextView) findViewById(R.id.txt_contact_phone_number);
        TextView risk1 = (TextView) findViewById(R.id.txt_risk1);
        TextView risk2 = (TextView) findViewById(R.id.txt_risk2);
        TextView risk3 = (TextView) findViewById(R.id.txt_risk3);
        TextView risk4 = (TextView) findViewById(R.id.txt_risk4);
        
        //detail data
        TextView txt_noBayi = (TextView) findViewById(R.id.txt_noBayi);
        TextView txt_jenisKelamin = (TextView) findViewById(R.id.txt_jenisKelamin);
        TextView txt_beratLahir = (TextView) findViewById(R.id.txt_beratLahir);
        TextView tinggi = (TextView) findViewById(R.id.txt_hasilPengukuranTinggiBayihasilPengukuranTinggiBayi);
        TextView berat = (TextView) findViewById(R.id.txt_indikatorBeratBedanBayi);
        TextView asi = (TextView) findViewById(R.id.txt_pemberianAsiEksklusif);
        TextView status_gizi = (TextView) findViewById(R.id.txt_statusGizi);
        TextView kpsp = (TextView) findViewById(R.id.txt_hasilDilakukannyaKPSP);
        TextView vita = (TextView) findViewById(R.id.txt_pelayananVita);
        TextView hb0 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiHb07);
        TextView pol1 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiBCGdanPolio1);
        TextView pol2 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB1Polio2);
        TextView pol3 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB2Polio3);
        TextView pol4 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB3Polio4);
        TextView campak = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiCampak);


        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AnakDetailActivity.this, NativeKIAnakSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });


        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        String gender=childclient.getDetails().containsKey("gender")?childclient.getDetails().get("gender"):"laki";


        //start profile image

        int placeholderDrawable= gender.equalsIgnoreCase("male")?R.drawable.child_boy_infant:R.drawable.child_girl_infant;
        childview.setTag(R.id.entity_id, childclient.getCaseId());//required when saving file to disk
        if(childclient.getCaseId()!=null){//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            BidanApplication.getInstance().getCachedImageLoaderInstance().getImageByClientId(childclient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(childview, placeholderDrawable, placeholderDrawable));

        }
        //end profile image

        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");

        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

        AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));
        
        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));


        nama.setText(getResources().getString(R.string.name)+humanize (childclient.getColumnmaps().get("namaBayi") != null ? childclient.getColumnmaps().get("namaBayi") : "-"));
        mother.setText(getResources().getString(R.string.child_details_mothers_name_label)+humanize (kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "-"));
        father.setText(getResources().getString(R.string.child_details_fathers_name_label)+ humanize(kiparent.getColumnmaps().get("namaSuami") != null ? kiparent.getColumnmaps().get("namaSuami") : "-"));
        dob.setText(getResources().getString(R.string.date_of_birth)+ humanize(childclient.getColumnmaps().get("tanggalLahirAnak") != null ? childclient.getColumnmaps().get("tanggalLahirAnak") : "-"));

        txt_noBayi.setText( ": "+humanize (childclient.getDetails().get("noBayi") != null ? childclient.getDetails().get("noBayi") : "-"));
        txt_jenisKelamin.setText(": "+ humanize (childclient.getDetails().get("jenisKelamin") != null ? childclient.getDetails().get("jenisKelamin") : "-"));
        txt_beratLahir.setText(": "+humanize (childclient.getDetails().get("beratLahir") != null ? childclient.getDetails().get("beratLahir") : "-"));
        tinggi.setText(": "+ humanize(childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") != null ? childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") : "-"));
        berat.setText(": "+humanize (childclient.getDetails().get("indikatorBeratBedanBayi") != null ? childclient.getDetails().get("indikatorBeratBedanBayi") : "-"));
        asi.setText(": "+humanize (childclient.getDetails().get("pemberianAsiEksklusif") != null ? childclient.getDetails().get("pemberianAsiEksklusif") : "-"));
        status_gizi.setText(": "+ humanize(childclient.getDetails().get("statusGizi") != null ? childclient.getDetails().get("statusGizi") : "-"));
        kpsp.setText(": "+ humanize(childclient.getDetails().get("hasilDilakukannyaKPSP") != null ? childclient.getDetails().get("hasilDilakukannyaKPSP") : "-"));
        hb0.setText(": "+ humanize(childclient.getDetails().get("hb0") != null ? childclient.getDetails().get("hb0") : "-"));
        pol1.setText(": "+ humanize(childclient.getDetails().get("polio1") != null ? childclient.getDetails().get("polio1") :childclient.getDetails().get("bcg") != null ? childclient.getDetails().get("bcg"): "-"));
        pol2.setText(": "+ humanize(childclient.getDetails().get("dptHb1") != null ? childclient.getDetails().get("dptHb1"):childclient.getDetails().get("polio2") != null ? childclient.getDetails().get("polio2"): "-"));
        pol3.setText(": "+ humanize(childclient.getDetails().get("dptHb2") != null ? childclient.getDetails().get("dptHb2") :childclient.getDetails().get("polio3") != null ? childclient.getDetails().get("polio3"): "-"));
        pol4.setText(": "+ humanize(childclient.getDetails().get("dptHb3") != null ? childclient.getDetails().get("dptHb3") :childclient.getDetails().get("polio4") != null ? childclient.getDetails().get("polio4"): "-"));
        campak.setText(": "+humanize (childclient.getDetails().get("campak") != null ? childclient.getDetails().get("campak") : "-"));
        vita.setText(": "+ humanize(childclient.getDetails().get("pelayananVita") != null ? childclient.getDetails().get("pelayananVita") : "-"));


        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "anak";
                entityid = childclient.entityId();
                dispatchTakePictureIntent(childview);

            }
        });

        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("taking_child_pictures_on_child_detail_view");
                bindobject = "anak";
                entityid = childclient.entityId();
                Log.e(TAG, "onClick: "+entityid );
                dispatchTakePictureIntent(childview);

            }
        });


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
    static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;
    private void dispatchTakePictureIntent(ImageView imageView) {
        Log.e(TAG, "dispatchTakePictureIntent: " + "klik");
        mImageView = imageView;
        Intent takePictureIntent = new Intent(this,SmartShutterActivity.class);
//        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.e(TAG, "dispatchTakePictureIntent: "+takePictureIntent.resolveActivity(getPackageManager()) );
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                currentfile = photoFile;
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//
            takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
            startActivityForResult(takePictureIntent, 1);
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
            HashMap<String,String> details = new HashMap<String,String>();
            details.put("profilepic",currentfile.getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }
    public static void setImagetoHolder(Activity activity, String file, ImageView view, int placeholder){
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
    public static void setImagetoHolderFromUri(Activity activity,String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        Uri external = Uri.fromFile(externalFile);
        view.setImageURI(external);


    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKISmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

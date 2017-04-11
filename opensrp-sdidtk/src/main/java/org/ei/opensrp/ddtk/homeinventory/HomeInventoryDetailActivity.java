package org.ei.opensrp.ddtk.homeinventory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.ddtk.R;
import org.ei.opensrp.repository.DetailsRepository;

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
public class HomeInventoryDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;




    //image retrieving

    public static CommonPersonObjectClient childclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.home_invent_detail_activity);


        //header
        TextView anakName = (TextView) findViewById(R.id.Nama);
        TextView anakJenisKelamin = (TextView) findViewById(R.id.Kelamin);
        TextView anakNamaIbu = (TextView) findViewById(R.id.txt_ortu);
        TextView anakUmur = (TextView) findViewById(R.id.Lahir);
        TextView anakBerat = (TextView) findViewById(R.id.Berat);
        TextView anakTinggi = (TextView) findViewById(R.id.childdetail_height);
        TextView anakLingKepala = (TextView) findViewById(R.id.childdetail_headcir);

        TextView Responsivitas = (TextView) findViewById(R.id.Responsivitas);
        TextView Penerimaan = (TextView) findViewById(R.id.Penerimaan);
        TextView Keteraturan = (TextView) findViewById(R.id.Keteraturan);
        TextView Sumber_belajar = (TextView) findViewById(R.id.Sumber_belajar);
        TextView Keterlibatan = (TextView) findViewById(R.id.Keterlibatan);
        TextView Varisai = (TextView) findViewById(R.id.Varisai);

        TextView Lingkungan_3 = (TextView) findViewById(R.id.Lingkungan_3);
        TextView berbicara_3 = (TextView) findViewById(R.id.berbicara_3);
        TextView Interaksi_3 = (TextView) findViewById(R.id.Interaksi_3);
        TextView Sikap_3 = (TextView) findViewById(R.id.Sikap_3);
        TextView umum_3 = (TextView) findViewById(R.id.umum_3);
        TextView Mainan_3 = (TextView) findViewById(R.id.Mainan_3);
        TextView pembelajaran_3 = (TextView) findViewById(R.id.pembelajaran_3);
        TextView Kebiasaan_3 = (TextView) findViewById(R.id.Kebiasaan_3);



        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(HomeInventoryDetailActivity.this, HomeInventorySmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        final ImageView childview = (ImageView)findViewById(R.id.childdetailprofileview);
        if(childclient.getDetails().get("profilepic")!= null){
            if((childclient.getDetails().get("jenis_kelamin")!=null?childclient.getDetails().get("jenis_kelamin"):"").equalsIgnoreCase("perempuan")) {
                setImagetoHolderFromUri(HomeInventoryDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.mipmap.womanimageload);
            } else if ((childclient.getDetails().get("jenis_kelamin")!=null?childclient.getDetails().get("jenis_kelamin"):"").equalsIgnoreCase("laki_laki")){
                setImagetoHolderFromUri(HomeInventoryDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.mipmap.householdload);

            }
        }else{

            if((childclient.getDetails().get("jenis_kelamin")!=null?childclient.getDetails().get("jenis_kelamin"):"").equalsIgnoreCase("perempuan")){
                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_girl_infant));
            }else if ((childclient.getDetails().get("jenis_kelamin")!=null?childclient.getDetails().get("jenis_kelamin"):"").equalsIgnoreCase("laki_laki")){
                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
            }
        }
        // }
       /* childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "anak";
                entityid = childclient.entityId();
                dispatchTakePictureIntent(childview);

            }
        });*/

        anakName.setText(getString(R.string.detailNamaAnak) +": "+ (childclient.getDetails().get("namaBayi") != null ? childclient.getDetails().get("namaBayi").replaceAll("_", " ") : "-"));
        anakJenisKelamin.setText(getString(R.string.detailJenisKelamin) +": "+ (childclient.getDetails().get("gender") != null ? childclient.getDetails().get("gender").replaceAll("_", " ") : "-"));

        String ages = childclient.getColumnmaps().get("tanggalLahirAnak").substring(0, childclient.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));

        anakUmur.setText(getString(R.string.detailUmur) +": "+ Integer.toString(monthRangeToToday(ages))+" Bulan" );


        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());
        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            anakNamaIbu.setText(namaibu + "," + namaayah);
            //   viewHolder.village_name.setText(kiparent.getDetails().get("address1")!=null?kiparent.getDetails().get("address1") :"-");
        }

        anakBerat.setText(getString(R.string.detailBerat) +": "+ (childclient.getDetails().get("berat") != null ? childclient.getDetails().get("berat").replaceAll("_", " ") : "-"));
        anakTinggi.setText(getString(R.string.detailTinggi) +": "+ (childclient.getDetails().get("tinggi") != null ? childclient.getDetails().get("tinggi").replaceAll("_", " ") : "-"));
        anakLingKepala.setText(getString(R.string.detailLingkarKepala) +": "+ (childclient.getDetails().get("lingkar_kepala") != null ? childclient.getDetails().get("lingkar_kepala").replaceAll("_", " ") : "-"));





        checkHome02(1,11,Responsivitas);
        checkHome02(12,19,Penerimaan);
        checkHome02(20,25,Keteraturan);
        checkHome02(26,34,Sumber_belajar);
        checkHome02(35,40,Keterlibatan);
        checkHome02(41,45,Varisai);

        checkHome36(1,5,Lingkungan_3);
        checkHome36(6,9,berbicara_3);
        checkHome36(10,15,Interaksi_3);
        checkHome36(16,21,Sikap_3);
        checkHome36(22,30,umum_3);
        checkHome36(31,37,Mainan_3);
        checkHome36(38,45,pembelajaran_3);
        checkHome36(48,55,Kebiasaan_3);




    }

    public void checkHome02 (int fisrt, int last , TextView total1 ) {
        int _endlinecount = 0;
        for (int i = fisrt ; i <=last ; i++){
            String home_endline = "home"+i+"_it";
            if(childclient.getDetails().get(home_endline) !=null) {
                if (childclient.getDetails().get(home_endline).equalsIgnoreCase("Yes")) {
                    _endlinecount = _endlinecount + 1;
                } else {

                }
            }
        }
        total1.setText(""+_endlinecount);
    }

    public void checkHome36 (int fisrt, int last , TextView total ) {
        int _endlinecount = 0;
        for (int i = fisrt ; i <=last ; i++){
            String home_endline = "home"+i+"_ec";
            if(childclient.getDetails().get(home_endline) !=null) {
                if (childclient.getDetails().get(home_endline).equalsIgnoreCase("Yes")) {
                    _endlinecount = _endlinecount + 1;
                } else {

                }
            }
        }
        total.setText(""+_endlinecount);
    }


    private int monthRangeToToday(String lastVisitDate){
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new java.util.Date()).substring(0,7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0,4)))*12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5,7))));
    }

    // NOT USING PICTURE AT THE MOMENT
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
        mImageView = imageView;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
            HashMap <String,String> details = new HashMap<String,String>();
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
//                childclient.entityId();
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
    public static void setImagetoHolderFromUri(Activity activity,String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        Uri external = Uri.fromFile(externalFile);
        view.setImageURI(external);


    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, HomeInventorySmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}
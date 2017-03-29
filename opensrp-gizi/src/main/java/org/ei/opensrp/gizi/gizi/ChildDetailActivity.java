package org.ei.opensrp.gizi.gizi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.gizi.face.camera.SmartShutterActivity;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.Log;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import util.ImageCache;
import util.ImageFetcher;
import util.KMS.KmsCalc;
import util.KMS.KmsPerson;
import util.growthChart.GrowthChartGenerator;

/**
 * Created by Iq on 26/04/16.
 */
public class ChildDetailActivity extends Activity {
    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
  //  private static KmsCalc  kmsCalc;
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
        setContentView(R.layout.gizi_detail_activity);
        String DetailStart = timer.format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("start", DetailStart);
                FlurryAgent.logEvent("gizi_detail_view",Detail, true );

        final ImageView childview = (ImageView)findViewById(R.id.detail_profilepic);
        //header
        TextView header_name = (TextView) findViewById(R.id.header_name);
        //sub header
        TextView subheader = (TextView) findViewById(R.id.txt_title_label);
        //profile
        TextView uniqueId = (TextView) findViewById(R.id.txt_profile_unique_id);
        TextView nama = (TextView) findViewById(R.id.txt_profile_child_name);
        TextView father_name = (TextView) findViewById(R.id.txt_profile_father_name);
        TextView mother_name = (TextView) findViewById(R.id.txt_profile_mother_name);
        TextView posyandu = (TextView) findViewById(R.id.txt_profile_posyandu);
        TextView village_name = (TextView) findViewById(R.id.txt_profile_village_name);
        TextView birth_date = (TextView) findViewById(R.id.txt_profile_birth_date);
        TextView gender = (TextView) findViewById(R.id.txt_profile_child_gender);
        TextView birthWeight = (TextView) findViewById(R.id.txt_profile_birth_weight);
        TextView weight = (TextView) findViewById(R.id.txt_profile_last_weight);
        TextView height = (TextView) findViewById(R.id.txt_profile_last_height);
        //child growth
        TextView nutrition_status = (TextView) findViewById(R.id.txt_profile_nutrition_status);
        TextView bgm = (TextView) findViewById(R.id.txt_profile_bgm);
        TextView dua_t = (TextView) findViewById(R.id.txt_profile_2t);
        TextView under_yellow_line = (TextView) findViewById(R.id.txt_profile_under_yellow_line);
        TextView breast_feeding = (TextView) findViewById(R.id.txt_profile_breastfeeding);
        TextView mpasi = (TextView) findViewById(R.id.txt_profile_mp_asi);
        TextView vitA = (TextView) findViewById(R.id.txt_vitA);
        TextView obatCacing = (TextView) findViewById(R.id.txt_anthelmintic);
        TextView lastVitA = (TextView) findViewById(R.id.txt_profile_last_vitA);
        TextView lastAnthelmintic = (TextView) findViewById(R.id.txt_profile_last_anthelmintic);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ChildDetailActivity.this, GiziSmartRegisterActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("gizi_detail_view", Detail, true);
            }
        });
        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        System.out.println("columnmaps: " + childclient.getColumnmaps().toString());
        System.out.println("details: "+childclient.getDetails().toString());

        if(childclient.getDetails().get("profilepic")!= null){
            if((childclient.getDetails().get("gender")!=null?childclient.getDetails().get("gender"):"").equalsIgnoreCase("female")) {
                setImagetoHolderFromUri(ChildDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.mipmap.child_boy_infant);
            } else if ((childclient.getDetails().get("gender")!=null?childclient.getDetails().get("gender"):"").equalsIgnoreCase("male")){
                setImagetoHolderFromUri(ChildDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.mipmap.child_boy_infant);

            }
        }
        else {
            if (childclient.getDetails().get("gender").equalsIgnoreCase("male") ) {
                childview.setImageDrawable(getResources().getDrawable(R.mipmap.child_boy_infant));
            } else {
                childview.setImageDrawable(getResources().getDrawable(R.mipmap.child_girl_infant));
            }
        }

        header_name.setText(R.string.child_profile);
        subheader.setText(R.string.child_profile);
        uniqueId.setText(getString(R.string.unique_id) + " " + (childclient.getDetails().get("UniqueId") != null ? childclient.getDetails().get("UniqueId"):"-"));
        nama.setText(getString(R.string.child_name) +" "+ (childclient.getDetails().get("namaBayi") != null ? childclient.getDetails().get("namaBayi") : "-"));

        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            father_name.setText(getString(R.string.father_name)+" " + namaayah);
            mother_name.setText(getString(R.string.mother_name) +" " +namaibu);
            village_name.setText(getString(R.string.village) +" "+ (kiparent.getDetails().get("cityVillage") != null ? kiparent.getDetails().get("cityVillage") : "-"));
            posyandu.setText(getString(R.string.posyandu) +" "+ (kiparent.getDetails().get("address1") != null ? kiparent.getDetails().get("address1") : "-"));
          //  village_name.setText(getString(R.string.village) + kiparent.getDetails().get("cityVillage") != null ? ": " + kiparent.getDetails().get("cityVillage") : "");
           // subVillage.setText(kiparent.getDetails().get("address1") != null ? ": " + kiparent.getDetails().get("address1") : "");
            // viewHolder.no_ibu.setText(kiparent.getDetails().get("noBayi") != null ? kiparent.getDetails().get("noBayi") : "");
        }

        /*father_name.setText(getString(R.string.father_name)+" "+(childclient.getDetails().get("namaAyah")!=null ? childclient.getDetails().get("namaAyah") : "-"));
        mother_name.setText(getString(R.string.mother_name) +" : "+ (childclient.getDetails().get("namaIbu") != null ? childclient.getDetails().get("namaIbu")
                : childclient.getDetails().get("namaOrtu")!=null
                    ? childclient.getDetails().get("namaOrtu")
                    : "-"));*/

        
      /*  village_name.setText(getString(R.string.village) +" "+ (childclient.getDetails().get("desa") != null ? childclient.getDetails().get("desa") : "-"));*/

        String dateOfBirth = childclient.getDetails().get("tanggalLahirAnak");
        birth_date.setText(getString(R.string.birth_date) +" "+ (dateOfBirth.contains("T") ? dateOfBirth.substring(0,10) : dateOfBirth));
        gender.setText(getString(R.string.gender) +" "+ (childclient.getDetails().get("gender") != null ? gender(childclient.getDetails().get("gender")) : "-"));
        birthWeight.setText(getString(R.string.birth_weight) + " " + (childclient.getDetails().get("beratLahir") != null ? childclient.getDetails().get("beratLahir") + " gr" : "-"));
        weight.setText(getString(R.string.weight) + " " + (childclient.getDetails().get("beratBadan") != null ? childclient.getDetails().get("beratBadan") + "Kg" : "- Kg"));
        height.setText(getString(R.string.height) +" "+ (childclient.getDetails().get("tinggiBadan") != null ? childclient.getDetails().get("tinggiBadan")+"Cm" : "- Cm"));
        vitA.setText(getString(R.string.vitamin_a) +" : "+ (inTheSameRegion(childclient.getDetails().get("lastVitA")) ? getString(R.string.yes) : getString(R.string.no)));
        mpasi.setText(getString(R.string.mpasi) + " "+(childclient.getDetails().get("mp_asi")!=null ? yesNo(childclient.getDetails().get("mp_asi")) : "-"));
        obatCacing.setText(getString(R.string.obatcacing)+ " "+(inTheSameRegionAnth(childclient.getDetails().get("lastAnthelmintic")) ? getString(R.string.yes) : getString(R.string.no)));
        lastVitA.setText(getString(R.string.lastVitA)+" "+(childclient.getDetails().get("lastVitA")!=null ? childclient.getDetails().get("lastVitA") : "-"));
        lastAnthelmintic.setText(getString(R.string.lastAnthelmintic)+" "+(childclient.getDetails().get("lastAnthelmintic")!=null ? childclient.getDetails().get("lastAnthelmintic") : "-"));
        //set value
        String[]data = childclient.getDetails().get("history_berat")!= null ? split(childclient.getDetails().get("history_berat")) : new String[]{"0","0"};
        String berats = data[1];
        String[] history_berat = berats.split(",");
        String tempUmurs = data[0];
        String[] history_umur = tempUmurs.split(",");
        String umurs="";
        for(int i=0;i<history_umur.length;i++){
            umurs = umurs + "," + Integer.toString(Integer.parseInt(history_umur[i])/30);
        }
//        ////System.out.println("status : bgm = "+", garis kuning = "+", 2T = ");
            dua_t.setText(getString(R.string.dua_t) +" "+ (childclient.getDetails().get("dua_t") != null ? yesNo(childclient.getDetails().get("dua_t")) : "-"));
            bgm.setText(getString(R.string.bgm) + " "+ (childclient.getDetails().get("bgm") != null ? yesNo(childclient.getDetails().get("bgm")) : "-"));
            under_yellow_line.setText(getString(R.string.under_yellow_line) + " "+ (childclient.getDetails().get("garis_kuning") != null ? yesNo(childclient.getDetails().get("garis_kuning")) : "-"));
            breast_feeding.setText(getString(R.string.asi) + " " + (childclient.getDetails().get("asi") != null ? yesNo(childclient.getDetails().get("asi")) : "-"));
            nutrition_status.setText(getString(R.string.nutrition_status) + " "+ (childclient.getDetails().get("nutrition_status") != null ? weightStatus(childclient.getDetails().get("nutrition_status")) : "-"));

        Log.logInfo("Berat :" +berats);
        Log.logInfo("umurs :" +umurs.substring(1,umurs.length()));
        GraphView graph = (GraphView) findViewById(R.id.graph);
        new GrowthChartGenerator(graph,childclient.getDetails().get("gender"),
                childclient.getDetails().get("tanggalLahirAnak").length() > 10
                ? childclient.getDetails().get("tanggalLahirAnak").substring(0,10)
                : childclient.getDetails().get("tanggalLahirAnak")
            ,umurs.substring(1,umurs.length()),berats);
        //set data for graph
       /* DataPoint dataPoint[] = new DataPoint[history_berat.length];
        for(int i=0;i<history_berat.length;i++){
            dataPoint[i]= new DataPoint(Double.parseDouble(history_umur[i]),Double.parseDouble(history_berat[i]));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoint);
        //add series data into chart
        graph.addSeries(series);

        //series.setTitle("Random Curve 1");
        series.setColor(Color.BLUE);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(6);
        series.setThickness(10);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        series.setCustomPaint(paint);*/
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return super.formatLabel(value, isValueX )+ " Month";
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " Kg";
                }
            }

        });

        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("taking_anak_pictures_on_child_detail_view");
                bindobject = "anak";
                entityid = childclient.entityId();
                android.util.Log.e(TAG, "onClick: " + entityid);
                dispatchTakePictureIntent(childview);

            }
        });


    }

    // english: fEMale, bahasa: perEMpuan, both have EM; since en: male, bhs: laki, both no EM
    private  String gender(String value){
        if (value.toLowerCase().contains("em"))
            return getString(R.string.child_female);
        else
            return getString(R.string.child_male);
    }

    private String yesNo(String value){
        if(value.toLowerCase().contains("yes") || value.toLowerCase().contains("ya"))
            return getString(R.string.yes);
        else
            return getString(R.string.no);
    }

    private String weightStatus(String value){
        if(value.toLowerCase().contains("gain") || value.toLowerCase().contains("idak"))
            return getString(R.string.weight_not_increase);
        else if(value.toLowerCase().contains("ncrea"))
            return getString(R.string.weight_increase);
        else if(value.toLowerCase().contains("atten"))
            return getString(R.string.weight_not_attend);
        else
            return getString(R.string.weight_new);
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
        android.util.Log.e(TAG, "dispatchTakePictureIntent: " + "klik");
        mImageView = imageView;
        Intent takePictureIntent = new Intent(this,SmartShutterActivity.class);
//        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        android.util.Log.e(TAG, "dispatchTakePictureIntent: " + takePictureIntent.resolveActivity(getPackageManager()));
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

/*
            HashMap<String,String> details = new HashMap<String,String>();
            details.put("profilepic",currentfile.getAbsolutePath());
            saveimagereference(bindobject,entityid,details);
*/

            Long tsLong = System.currentTimeMillis()/1000;
            DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
            System.out.println("image absolute path: "+currentfile.getAbsolutePath());
            detailsRepository.add(entityid, "profilepic", currentfile.getAbsolutePath(), tsLong);

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

    private boolean inTheSameRegion(String date){
        if(date==null || date.length()<6)
            return false;
        int currentDate = Integer.parseInt(new SimpleDateFormat("MM").format(new java.util.Date()));
        int visitDate = Integer.parseInt(date.substring(5, 7));

        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date()));
        int visitYear = Integer.parseInt(date.substring(0, 4));

        boolean date1 = currentDate < 2 || currentDate >=8;
        boolean date2 = visitDate < 2 || visitDate >=8;

        int indicator = currentDate == 1 ? 2:1;

        return (!((!date1 && date2) || (date1 && !date2)) && ((currentYear-visitYear)<indicator));
    }

    private boolean inTheSameRegionAnth(String date){
        if(date==null || date.length()<6)
            return false;
        int visitDate = Integer.parseInt(date.substring(5, 7));

        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date()));
        int visitYear = Integer.parseInt(date.substring(0, 4));

        return (((currentYear-visitYear)*12) + (8-visitDate)) <=12;
    }

    private String[]split(String data){
        if(!data.contains(":"))
            return new String[]{"0","0"};
        String []temp = data.split(",");
        String []result = {"",""};
        for(int i=0;i<temp.length;i++){
            result[0]=result[0]+","+temp[i].split(":")[0];
            result[1]=result[1]+","+temp[i].split(":")[1];
        }
        result[0]=result[0].substring(1,result[0].length());
        result[1]=result[1].substring(1,result[1].length());
        return result;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, GiziSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

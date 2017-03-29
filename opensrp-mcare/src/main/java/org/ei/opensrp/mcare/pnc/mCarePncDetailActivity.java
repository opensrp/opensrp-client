package org.ei.opensrp.mcare.pnc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 5/11/15.
 */
public class mCarePncDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;




    //image retrieving

    public static CommonPersonObjectClient ancclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.pnc_detail_activity);
        TextView name = (TextView) findViewById(R.id.name);
        TextView brid = (TextView) findViewById(R.id.brid);
        TextView nid = (TextView) findViewById(R.id.womannid);
        TextView husbandname = (TextView) findViewById(R.id.husbandname);
        TextView age = (TextView) findViewById(R.id.age);
        TextView jivitahhid = (TextView) findViewById(R.id.jivitahhid);
        TextView godhhid = (TextView) findViewById(R.id.gobhhid);
        TextView village = (TextView) findViewById(R.id.village);
        TextView typeofdelivery  = (TextView) findViewById(R.id.preganancy_type_of_delivery);
        TextView pregnancy_outcome  = (TextView) findViewById(R.id.preganancy_outcome);


        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name.setText(humanize((ancclient.getColumnmaps().get("FWWOMFNAME") != null ? ancclient.getColumnmaps().get("FWWOMFNAME") : "").replace("+", "_")));


        if((ancclient.getDetails().get("FWWOMBID") != null ? ancclient.getDetails().get("FWWOMBID") : "").length()>0) {
            brid.setText(Html.fromHtml(getString(R.string.BRID) + " " + humanize((ancclient.getDetails().get("FWWOMBID") != null ? ancclient.getDetails().get("FWWOMBID") : "").replace("+", "_")) ));
            brid.setVisibility(View.VISIBLE);
        }else{
            brid.setVisibility(View.GONE);
        }
        if((ancclient.getDetails().get("FWWOMNID") != null ? ancclient.getDetails().get("FWWOMNID") : "").length()>0) {
            nid.setText(Html.fromHtml(getString(R.string.NID) + " " + humanize((ancclient.getDetails().get("FWWOMNID") != null ? ancclient.getDetails().get("FWWOMNID") : "").replace("+", "_")) ));
            nid.setVisibility(View.VISIBLE);
        }else{
            nid.setVisibility(View.GONE);
        }
        husbandname.setText(Html.fromHtml(getString(R.string.elco_details_husband_name_label)+" "+humanize((ancclient.getDetails().get("FWHUSNAME") != null ? ancclient.getDetails().get("FWHUSNAME") : ""))));
        age.setText(Html.fromHtml(getString(R.string.elco_age_label)+" " + (ancclient.getDetails().get("FWWOMAGE") != null ? ancclient.getDetails().get("FWWOMAGE") : "")));

        if(ancclient.getDetails().get("FWBIRTHDATE") != null) {
            try {
                int days = DateUtil.dayDifference(DateUtil.getLocalDate((ancclient.getDetails().get("FWBIRTHDATE") != null ? ancclient.getDetails().get("FWBIRTHDATE") : "")), DateUtil.today());
                Log.v("days", "" + days);
                int calc_age = days / 365;
                age.setText(Html.fromHtml(getString(R.string.elco_age_label) + " " + calc_age));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        jivitahhid.setText(Html.fromHtml(getString(R.string.hhiid_jivita_elco_label)+" "+(ancclient.getColumnmaps().get("JiVitAHHID") != null ? ancclient.getColumnmaps().get("JiVitAHHID") : "")));
        godhhid.setText(Html.fromHtml(getString(R.string.hhid_gob_elco_label)+" " + (ancclient.getColumnmaps().get("GOBHHID") != null ? ancclient.getColumnmaps().get("GOBHHID") : "")));
        village.setText(Html.fromHtml(getString(R.string.elco_details_mauza) + " " + humanize(ancclient.getDetails().get("mauza") != null ? ancclient.getDetails().get("mauza") : "")));

        String type_of_delivery = ancclient.getDetails().get("FWPNC1DELTYPE") != null ? ancclient.getDetails().get("FWPNC1DELTYPE") : "";
        if (type_of_delivery.equalsIgnoreCase("1")){
            typeofdelivery.setText(getString(R.string.norma_birth));
        } else if (type_of_delivery.equalsIgnoreCase("2")){
            typeofdelivery.setText(getString(R.string.Caeserian_section));
        }else{
            typeofdelivery.setText(getString(R.string.dont_know));
        }
           String outcomevalue = ancclient.getColumnmaps().get("FWBNFSTS")!=null?ancclient.getColumnmaps().get("FWBNFSTS") : "";

        if (outcomevalue.equalsIgnoreCase("3")){
            pregnancy_outcome.setText(getString(R.string.mcare_pnc_liveBirth));
        } else if (outcomevalue.equalsIgnoreCase("4")){
            pregnancy_outcome.setText(getString(R.string.mcare_pnc_Stillbirth));
        }

        checkPnc1view(ancclient);
        checkPnc2view(ancclient);
        checkAnc3view(ancclient);
        checktempView(ancclient);
       doolay(ancclient);
        final ImageView householdview = (ImageView) findViewById(R.id.householdprofileview);

        if (ancclient.getDetails().get("profilepic") != null) {
            setImagetoHolder(mCarePncDetailActivity.this, ancclient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
        }



    }

    private void doolay(CommonPersonObjectClient ancclient) {
        TextView edd = (TextView)findViewById(R.id.date_of_outcome);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getColumnmaps().get("FWBNFDTOO")!=null?ancclient.getColumnmaps().get("FWBNFDTOO"):"");
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(edd_date);
            edd_date.setTime(calendar.getTime().getTime());
            edd.setText(format.format(edd_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void pregnancyin2years(CommonPersonObject ecclient) {
        String text = ecclient.getDetails().get("FWPSRPREGTWYRS")!=null?ecclient.getDetails().get("FWPSRPREGTWYRS"):"0";
        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
        stillbirth.setText(text);
    }

    private void historyofsb(CommonPersonObject ecclient) {
        String text = ecclient.getDetails().get("FWPSRPRSB")!=null?ecclient.getDetails().get("FWPSRPRSB"):"0";
        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
        stillbirth.setText(text);
    }

    private void historyofmr(CommonPersonObject ecclient) {
        String text = ecclient.getDetails().get("FWPSRPRMC")!=null?ecclient.getDetails().get("FWPSRPRMC"):"0";
        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
        stillbirth.setText(text);

    }

    private void numberofstillbirthview(CommonPersonObject ecclient) {
        String text = ecclient.getDetails().get("FWPSRNBDTH")!=null?ecclient.getDetails().get("FWPSRNBDTH"):"0";
        TextView stillbirth = (TextView)findViewById(R.id.preganancy_type_of_delivery);
        stillbirth.setText(text);
    }

    private void numberofChildrenView(CommonPersonObject ecclient) {
        String text = ecclient.getDetails().get("FWPSRTOTBIRTH")!=null?ecclient.getDetails().get("FWPSRTOTBIRTH"):"0";
        TextView numberofChildren = (TextView)findViewById(R.id.livechildren);
        numberofChildren.setText(text);

    }
    private void checktempView(CommonPersonObjectClient ecclient) {
        LinearLayout templayout = (LinearLayout)findViewById(R.id.temperature_layout);
        TextView anc1date = (TextView)findViewById(R.id.temperature);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_4");
        if(ecclient.getDetails().get("FWPNC3TEMP")!=null){
            anc1date.setText(ecclient.getDetails().get("FWPNC3TEMP")!=null?ecclient.getDetails().get("FWPNC3TEMP"):"");
        }else if(ecclient.getDetails().get("FWPNC2TEMP")!=null){
            anc1date.setText(ecclient.getDetails().get("FWPNC2TEMP")!=null?ecclient.getDetails().get("FWPNC2TEMP"):"");
        }else if(ecclient.getDetails().get("FWPNC1TEMP")!=null){
            anc1date.setText(ecclient.getDetails().get("FWPNC1TEMP")!=null?ecclient.getDetails().get("FWPNC1TEMP"):"");
        }else{
            templayout.setVisibility(View.GONE);
        }

    }
    private void checkAnc3view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.pnc3_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_3");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWPNC3DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWPNC3DATE")!=null?ecclient.getDetails().get("FWPNC3DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc3date);
                if((ecclient.getDetails().get("pnc3_current_formStatus")!=null?ecclient.getDetails().get("pnc3_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("pnc3_current_formStatus")!=null?ecclient.getDetails().get("pnc3_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }

    private void checkPnc2view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.pnc2_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "pncrv_2");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWPNC2DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWPNC2DATE")!=null?ecclient.getDetails().get("FWPNC2DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc2date);
                if((ecclient.getDetails().get("pnc2_current_formStatus")!=null?ecclient.getDetails().get("pnc2_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("pnc2_current_formStatus")!=null?ecclient.getDetails().get("pnc2_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }

    private void checkPnc1view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.pnc1_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "pncrv_1");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWPNC1DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWPNC1DATE")!=null?ecclient.getDetails().get("FWPNC1DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc1date);
                if((ecclient.getDetails().get("pnc1_current_formStatus")!=null?ecclient.getDetails().get("pnc1_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("pnc1_current_formStatus")!=null?ecclient.getDetails().get("pnc1_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
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
//                Elcoclient.entityId();
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
}

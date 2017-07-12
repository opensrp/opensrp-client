package org.ei.opensrp.mcare.anc;

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
import java.util.Calendar;
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
public class mCareAncDetailActivity extends Activity {

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
        setContentView(R.layout.anc_detail_activity);
        TextView name = (TextView) findViewById(R.id.name);
        TextView brid = (TextView) findViewById(R.id.brid);
        TextView nid = (TextView) findViewById(R.id.womannid);
        TextView husbandname = (TextView) findViewById(R.id.husbandname);
        TextView age = (TextView) findViewById(R.id.age);
        TextView jivitahhid = (TextView) findViewById(R.id.jivitahhid);
        TextView godhhid = (TextView) findViewById(R.id.gobhhid);
        TextView village = (TextView) findViewById(R.id.village);
//        TextView mw_reg_date = (TextView) findViewById(R.id.mw_reg_date);
//        TextView psf_due_date = (TextView) findViewById(R.id.last_psf_date);







        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name.setText(humanize((ancclient.getColumnmaps().get("FWWOMFNAME") != null ? ancclient.getColumnmaps().get("FWWOMFNAME") : "").replace("+", "_")));
        if((ancclient.getDetails().get("FWWOMBID") != null ? ancclient.getDetails().get("FWWOMBID") : "").length()>0) {
            brid.setText(Html.fromHtml(getString(R.string.BRID) + " " + humanize((ancclient.getDetails().get("FWWOMBID") != null ? ancclient.getDetails().get("FWWOMBID") : "").replace("+", "_"))));
            brid.setVisibility(View.VISIBLE);
        }else{
            brid.setVisibility(View.GONE);
        }
        if((ancclient.getDetails().get("FWWOMNID") != null ? ancclient.getDetails().get("FWWOMNID") : "").length()>0) {
            nid.setText(Html.fromHtml(getString(R.string.NID) + " " + humanize((ancclient.getDetails().get("FWWOMNID") != null ? ancclient.getDetails().get("FWWOMNID") : "").replace("+", "_"))));
            nid.setVisibility(View.VISIBLE);
        }else{
            nid.setVisibility(View.GONE);
        }
        husbandname.setText(Html.fromHtml(getString(R.string.elco_details_husband_name_label)+" "+humanize(ancclient.getDetails().get("FWHUSNAME") != null ? ancclient.getDetails().get("FWHUSNAME") : "")));
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


        jivitahhid.setText(Html.fromHtml(getString(R.string.hhiid_jivita_elco_label)+" " +(ancclient.getColumnmaps().get("JiVitAHHID") != null ? ancclient.getColumnmaps().get("JiVitAHHID") : "")));
        godhhid.setText(Html.fromHtml(getString(R.string.hhid_gob_elco_label) +" " + (ancclient.getColumnmaps().get("GOBHHID") != null ? ancclient.getColumnmaps().get("GOBHHID") : "")));
//        psf_due_date.setText(Elcoclient.getDetails().get("FWPSRDATE") != null ? Elcoclient.getDetails().get("FWPSRDATE") : "");


        village.setText(Html.fromHtml(getString(R.string.elco_details_mauza) + " " + humanize(ancclient.getDetails().get("mauza") != null ? ancclient.getDetails().get("mauza") : "")));
        AllCommonsRepository householdrep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_household");
        CommonPersonObject householdparent = householdrep.findByCaseID(ancclient.getDetails().get("relational_id"));

        if(householdparent.getColumnmaps().get("existing_Mauzapara") != null) {
            String location = householdparent.getColumnmaps().get("existing_Mauzapara");
            village.setText(humanize(getString(R.string.elco_details_mauza) + " " +location));
        }
            /////from househld
 //

        checkAnc1view(ancclient);
        checkAnc2view(ancclient);
        checkAnc3view(ancclient);
        checkAnc4view(ancclient);
        numberofChildrenView(ancclient);
        numberofstillbirthview(ancclient);
        historyofmr(ancclient);
        historyofsb(ancclient);
        pregnancyin2years(ancclient);
        eddlay(ancclient);



//        AllCommonsRepository allelcoRepository = Context.getInstance().allCommonsRepositoryobjects("elco");
//        CommonPersonObject elcoobject = allelcoRepository.findByCaseID(Elcoclient.entityId());
//        AllCommonsRepository householdrep = Context.getInstance().allCommonsRepositoryobjects("household");
//        CommonPersonObject householdparent = householdrep.findByCaseID(elcoobject.getRelationalId());
//        String location = "";
//        if(householdparent.getDetails().get("location_name") != null) {
//            location = householdparent.getDetails().get("location_name");
//        }
//        village.setText(getString(R.string.elco_details_mauza)+humanize(location));


//        mw_reg_date.setText((Elcoclient.getDetails().get("WomanREGDATE") != null ? Elcoclient.getDetails().get("WomanREGDATE") : ""));
        ///////////////////////////////////////////////////


        final ImageView householdview = (ImageView) findViewById(R.id.householdprofileview);

        if (ancclient.getDetails().get("profilepic") != null) {
            setImagetoHolder(mCareAncDetailActivity.this, ancclient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
        }
//        householdview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bindobject = "household";
//                entityid = Elcoclient.entityId();
//                dispatchTakePictureIntent(householdview);
//
//            }
//        });


    }

    private void eddlay(CommonPersonObjectClient ancclient) {
        TextView edd = (TextView)findViewById(R.id.edd_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getColumnmaps().get("FWPSRLMP")!=null?ancclient.getColumnmaps().get("FWPSRLMP"):"");
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(edd_date);
            calendar.add(Calendar.DATE, 259);
            edd_date.setTime(calendar.getTime().getTime());
            edd.setText(format.format(edd_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void pregnancyin2years(CommonPersonObjectClient ecclient) {
        String text = ecclient.getDetails().get("FWPSRPREGTWYRS")!=null?ecclient.getDetails().get("FWPSRPREGTWYRS"):"N/A";
        TextView stillbirth = (TextView)findViewById(R.id.number_of_pregnancy);
        stillbirth.setText(text);
    }

    private void historyofsb(CommonPersonObjectClient ecclient) {
        String text = ecclient.getDetails().get("FWPSRPRSB")!=null?ecclient.getDetails().get("FWPSRPRSB"):"N/A";
        TextView stillbirth = (TextView)findViewById(R.id.history_of_sb);
        stillbirth.setText(text);
    }

    private void historyofmr(CommonPersonObjectClient ecclient) {
        String text = ecclient.getDetails().get("FWPSRPRMC")!=null?ecclient.getDetails().get("FWPSRPRMC"):"N/A";
        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
        stillbirth.setText(text);

    }

    private void numberofstillbirthview(CommonPersonObjectClient ecclient) {
        String text = ecclient.getDetails().get("FWPSRNBDTH")!=null?ecclient.getDetails().get("FWPSRNBDTH"):"N/A";
        TextView stillbirth = (TextView)findViewById(R.id.stillbirths);
        stillbirth.setText(text);
    }

    private void numberofChildrenView(CommonPersonObjectClient ecclient) {
        String text = ecclient.getDetails().get("FWPSRTOTBIRTH")!=null?ecclient.getDetails().get("FWPSRTOTBIRTH"):"N/A";
        TextView numberofChildren = (TextView)findViewById(R.id.livechildren);
        numberofChildren.setText(text);
    }
    private void checkAnc4view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.anc4_layout);
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_4");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWANC4DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWANC4DATE")!=null?ecclient.getDetails().get("FWANC4DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc4date);
                if((ecclient.getDetails().get("ANC4_current_formStatus")!=null?ecclient.getDetails().get("ANC4_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("ANC4_current_formStatus")!=null?ecclient.getDetails().get("ANC4_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }
    private void checkAnc3view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.anc3_layout);
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_3");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWANC3DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWANC3DATE")!=null?ecclient.getDetails().get("FWANC3DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc3date);
                if((ecclient.getDetails().get("ANC3_current_formStatus")!=null?ecclient.getDetails().get("ANC3_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("ANC3_current_formStatus")!=null?ecclient.getDetails().get("ANC3_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }

    private void checkAnc2view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.anc2_layout);
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_2");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWANC2DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWANC2DATE")!=null?ecclient.getDetails().get("FWANC2DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc2date);
                if((ecclient.getDetails().get("ANC2_current_formStatus")!=null?ecclient.getDetails().get("ANC2_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("ANC2_current_formStatus")!=null?ecclient.getDetails().get("ANC2_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }

    private void checkAnc1view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.anc1_layout);
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_1");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWANC1DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWANC1DATE")!=null?ecclient.getDetails().get("FWANC1DATE"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc1date);
                if((ecclient.getDetails().get("anc1_current_formStatus")!=null?ecclient.getDetails().get("anc1_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("anc1_current_formStatus")!=null?ecclient.getDetails().get("anc1_current_formStatus"):"").equalsIgnoreCase("urgent")){
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

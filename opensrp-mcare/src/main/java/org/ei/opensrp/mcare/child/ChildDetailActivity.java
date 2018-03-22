package org.ei.opensrp.mcare.child;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
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
import org.ei.opensrp.mcare.household.HouseHoldDetailActivity;

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
public class ChildDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;




    //image retrieving

    public static CommonPersonObjectClient ChildClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.child_detail_activity);
        ImageView profilepic = (ImageView) findViewById(R.id.householdprofileview);
        TextView name = (TextView) findViewById(R.id.childid);
        TextView fathersname = (TextView) findViewById(R.id.fathersname);
        TextView mothersname = (TextView) findViewById(R.id.mothersname);

        TextView age = (TextView) findViewById(R.id.age);
        TextView jivitahhid = (TextView) findViewById(R.id.jivitahhid);
        TextView godhhid = (TextView) findViewById(R.id.gobhhid);
        TextView village = (TextView) findViewById(R.id.village);
        TextView TypeOfDelivery = (TextView) findViewById(R.id.preganancy_type_of_delivery);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AllCommonsRepository allchildRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcarechild");
        CommonPersonObject childobject = allchildRepository.findByCaseID(ChildClient.entityId());
        AllCommonsRepository motherrep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother");
        final CommonPersonObject mcaremotherObject = motherrep.findByCaseID(childobject.getRelationalId());


        name.setText(humanize((ChildClient.getDetails().get("FWBNFCHILDNAME") != null ? ChildClient.getDetails().get("FWBNFCHILDNAME") : "").replace("+", "_")));
        fathersname.setText(Html.fromHtml(getString(R.string.child_details_fathers_name_label) + "<b> " + humanize((mcaremotherObject.getDetails().get("FWHUSNAME") != null ? mcaremotherObject.getDetails().get("FWHUSNAME") : "")) + "</b>"));
        mothersname.setText(Html.fromHtml(getString(R.string.child_details_mothers_name_label) +"<b> "+ humanize((mcaremotherObject.getColumnmaps().get("FWWOMFNAME") != null ? mcaremotherObject.getColumnmaps().get("FWWOMFNAME") : ""))+ "</b>"));

        age.setText(Html.fromHtml(getString(R.string.elco_age_label) +"<b> "+ age(ChildClient) + " days "+ "</b>"));
        godhhid.setText(Html.fromHtml(getString(R.string.hhid_gob_elco_label) +"<b> "+ (mcaremotherObject.getColumnmaps().get("GOBHHID")!=null?mcaremotherObject.getColumnmaps().get("GOBHHID"):"")+ "</b>"));
        jivitahhid.setText(Html.fromHtml(getString(R.string.hhiid_jivita_elco_label)+"<b> "+(mcaremotherObject.getColumnmaps().get("JiVitAHHID")!=null?mcaremotherObject.getColumnmaps().get("JiVitAHHID"):"")+ "</b>"));
        village.setText(Html.fromHtml(getString(R.string.elco_details_mauza) + "<b> " + humanize((mcaremotherObject.getDetails().get("mauza") != null ? mcaremotherObject.getDetails().get("mauza") : "").replace("+", "_"))+ "</b>"));
        String type_of_delivery = mcaremotherObject.getDetails().get("FWPNC1DELTYPE") != null ? mcaremotherObject.getDetails().get("FWPNC1DELTYPE") : "";
        if (type_of_delivery.equalsIgnoreCase("1")){
            TypeOfDelivery.setText(getString(R.string.norma_birth));
        } else if (type_of_delivery.equalsIgnoreCase("2")){
            TypeOfDelivery.setText(getString(R.string.Caeserian_section));
        }else{
            TypeOfDelivery.setText(getString(R.string.dont_know));
        }





       checkEncc1view(ChildClient);
       checkEncc2view(ChildClient);
        checkEncc3view(ChildClient);
//        checktempView(ChildClient);
       doolay(ChildClient);
//        final ImageView householdview = (ImageView) findViewById(R.id.householdprofileview);
//
//        if (ChildClient.getDetails().get("profilepic") != null) {
//            setImagetoHolder(ChildDetailActivity.this, ChildClient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
//        }
        if((ChildClient.getColumnmaps().get("FWBNFGEN")!=null?ChildClient.getColumnmaps().get("FWBNFGEN"):"").equalsIgnoreCase("2")){
            profilepic.setImageResource(R.drawable.child_girl_infant);
        }else if((ChildClient.getColumnmaps().get("FWBNFGEN")!=null?ChildClient.getColumnmaps().get("FWBNFGEN"):"").equalsIgnoreCase("1")){
            profilepic.setImageResource(R.drawable.child_boy_infant);
        }
        if (childobject.getDetails().get("profilepic") != null) {
            HouseHoldDetailActivity.setImagetoHolderFromUri(ChildDetailActivity.this, childobject.getDetails().get("profilepic"), profilepic, R.drawable.child_boy_infant);
        }


    }

    private Long age(CommonPersonObjectClient ancclient) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getDetails().get("FWBNFDOB")!=null?ancclient.getDetails().get("FWBNFDOB"):"");
            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(edd_date);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();

            long days = diff / (24 * 60 * 60 * 1000);

            return days;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    private void doolay(CommonPersonObjectClient ancclient) {

        TextView edd = (TextView)findViewById(R.id.date_of_outcome);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getDetails().get("FWBNFDOB")!=null?ancclient.getDetails().get("FWBNFDOB"):"");
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
    private void checkEncc3view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.encc3_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "enccrv_3");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWENC3DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWENC3DATE")!=null?ecclient.getDetails().get("FWENC3DATE"):"";
                TextView encc3date = (TextView)findViewById(R.id.encc3date);
                if((ecclient.getDetails().get("encc3_current_formStatus")!=null?ecclient.getDetails().get("encc3_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    encc3date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("encc3_current_formStatus")!=null?ecclient.getDetails().get("encc3_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    encc3date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                encc3date.setText(text);

            }
        }else{
            anc1layout.setVisibility(View.GONE);
        }
    }

    private void checkEncc2view(CommonPersonObjectClient ecclient) {
        LinearLayout encc2layout = (LinearLayout)findViewById(R.id.encc2_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "enccrv_2");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWENC2DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWENC2DATE")!=null?ecclient.getDetails().get("FWENC2DATE"):"";
                TextView encc2date = (TextView)findViewById(R.id.encc2date);
                if((ecclient.getDetails().get("encc2_current_formStatus")!=null?ecclient.getDetails().get("encc2_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    encc2date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("encc2_current_formStatus")!=null?ecclient.getDetails().get("encc2_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    encc2date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                encc2date.setText(text);

            }
        }else{
            encc2layout.setVisibility(View.GONE);
        }
    }

    private void checkEncc1view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.encc1_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "enccrv_1");
        if(alertlist.size()!=0 && ecclient.getDetails().get("FWENC1DATE")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("FWENC1DATE")!=null?ecclient.getDetails().get("FWENC1DATE"):"";
                TextView encc1date = (TextView)findViewById(R.id.encc1date);
                if((ecclient.getDetails().get("encc1_current_formStatus")!=null?ecclient.getDetails().get("encc1_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    encc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("encc1_current_formStatus")!=null?ecclient.getDetails().get("encc1_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    encc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                encc1date.setText(text);

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

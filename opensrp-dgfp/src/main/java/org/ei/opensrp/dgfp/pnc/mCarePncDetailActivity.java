package org.ei.opensrp.dgfp.pnc;

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
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
public class mCarePncDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;

    private String[] where_Delivered = {
            "Home","Upazilla Health & Family Welfare Complex","Union Health & Family Welfare Center",
            "Mother & Child Welfare Center","District or other Govt. Hospital",
            "NGO Clinic or Hospital","Private Clinic or Hospital"
    };
    private String[] delivery_Type = {"Normal","Caesarean"};
    private String[] visit_Status = {"Live Birth","Still Birth/MC"};

    //image retrieving

    public static CommonPersonObjectClient ancclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.pnc_detail_activity);
        TextView name = (TextView) findViewById(R.id.womandetail_name);
        TextView brid = (TextView) findViewById(R.id.womandetail_womabrid);
        TextView nid = (TextView) findViewById(R.id.womandetail_womanid);
        TextView hid = (TextView) findViewById(R.id.womandetail_womahid);
        TextView husbandname = (TextView) findViewById(R.id.womandetail_husbandname);
        TextView marriagelife = (TextView) findViewById(R.id.womandetail_marriage_life);
        TextView womandob = (TextView) findViewById(R.id.womandetail_womandob);
        TextView address = (TextView) findViewById(R.id.womandetail_address);
        TextView today = (TextView)findViewById(R.id.woman_detail_today);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CommonPersonObjectClient womanclient = ancclient;
        ////////////////////////////////profile view /////////////////////////////////////////////////
        name.setText(humanize((womanclient.getColumnmaps().get("Mem_F_Name") != null ? womanclient.getColumnmaps().get("Mem_F_Name") : "").replace("+", "_")));

        brid.setText(humanize("BRID: "+(womanclient.getDetails().get("Mem_BRID") != null ? womanclient.getDetails().get("Mem_BRID") : "").replace("+", "_")));

        nid.setText(humanize("NID: "+(womanclient.getDetails().get("Mem_NID") != null ? womanclient.getDetails().get("Mem_NID") : "").replace("+", "_")));

        hid.setText(humanize("GOB HHID: "+(womanclient.getDetails().get("Member_GoB_HHID") != null ? womanclient.getDetails().get("Member_GoB_HHID") : "").replace("+", "_")));

        if(womanclient.getDetails().get("Spouse_Name") != null){
            husbandname.setText(humanize(womanclient.getDetails().get("Spouse_Name").length() > 0 ? womanclient.getDetails().get("Spouse_Name"):"N/A"));
        }

        marriagelife.setText("Marriage Life: "+((womanclient.getDetails().get("Married_Life") != null ? womanclient.getDetails().get("Married_Life") : "")));


        womandob.setText("Age: "+calculateage(getageindays(getdate((womanclient.getDetails().get("Calc_Dob_Confirm") != null ? womanclient.getDetails().get("Calc_Dob_Confirm") : "")))));

        address.setText("Address: "+(humanize((womanclient.getDetails().get("Mem_Subunit") != null ? womanclient.getDetails().get("Mem_Subunit") : "").replace("+", "_")))+", "+
                (humanize((womanclient.getDetails().get("Mem_Village_Name") != null ? womanclient.getDetails().get("Mem_Village_Name") : "").replace("+", "_")))+", "+
                (humanize((womanclient.getDetails().get("Mem_Mauzapara") != null ? womanclient.getDetails().get("Mem_Mauzapara") : "").replace("+", "_")))+", "+
                (humanize((womanclient.getDetails().get("Mem_Union") != null ? womanclient.getDetails().get("Mem_Union") : "").replace("+", "_")))+", "+
                (humanize((womanclient.getDetails().get("Mem_Upazilla") != null ? womanclient.getDetails().get("Mem_Upazilla") : "").replace("+", "_")))
        );

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        today.setText("Today: " +formattedDate+" ");


        DateUtil.setDefaultDateFormat("yyyy-MM-dd");
        AllCommonsRepository allmotherRepository = Context.getInstance().allCommonsRepositoryobjects("members");
        CommonPersonObject childobject = allmotherRepository.findByCaseID(ancclient.entityId());
        AllCommonsRepository elcorep = Context.getInstance().allCommonsRepositoryobjects("members");
        final CommonPersonObject elcoObject = elcorep.findByCaseID(childobject.getRelationalId());




      //        String type_of_delivery = ancclient.getDetails().get("FWPNC1DELTYPE") != null ? ancclient.getDetails().get("FWPNC1DELTYPE") : "";
//        if (type_of_delivery.equalsIgnoreCase("1")){
//            typeofdelivery.setText(getString(R.string.norma_birth));
//        } else if (type_of_delivery.equalsIgnoreCase("2")){
//            typeofdelivery.setText(getString(R.string.Caeserian_section));
//        }else{
//            typeofdelivery.setText(getString(R.string.dont_know));
//        }
//           String outcomevalue = ancclient.getColumnmaps().get("FWBNFSTS")!=null?ancclient.getColumnmaps().get("FWBNFSTS") : "";
//
//        if (outcomevalue.equalsIgnoreCase("3")){
//            pregnancy_outcome.setText(getString(R.string.mcare_pnc_liveBirth));
//        } else if (outcomevalue.equalsIgnoreCase("4")){
//            pregnancy_outcome.setText(getString(R.string.mcare_pnc_Stillbirth));
//        }


       checkPnc1view(ancclient);
        checkPnc2view(ancclient);
        checkAnc3view(ancclient);
       doolay(ancclient);
        final ImageView householdview = (ImageView) findViewById(R.id.householdprofileview);

        if (ancclient.getDetails().get("profilepic") != null) {
            setImagetoHolder(mCarePncDetailActivity.this, ancclient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
        }
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.outcome_type),"Visit_Status");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.deliverytype),"Delivery_Type");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.deliverylocation),"Where_Delivered");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.no_of_live_birth),"Num_Live_Birth");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.is_referred),"Is_Referred");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.child_wrapped_with_dry_clother),"Is_Cleaned");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.chlorhexidin),"Chlorhexidin");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.breastfeeding_initiated),"Breastmilk_Fed");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.baby_kept_away),"Not_Bathed");

    }

    private void assign_text_to_givenView(CommonPersonObjectClient ecclient,TextView tview,String detailvariable) {
        String text = ecclient.getDetails().get(detailvariable)!=null?ecclient.getDetails().get(detailvariable):"N/A";

        if(text.equalsIgnoreCase("N/A") || text.length() < 1){
            tview.setText("N/A");
            return;
        }
        try {
            switch (detailvariable) {
                case "Visit_Status":
                    text = visit_Status[Integer.parseInt(text) - 3];
                    break;
                case "Delivery_Type":
                    text = delivery_Type[Integer.parseInt(text) - 1];
                    break;
                case "Where_Delivered":
                    text = where_Delivered[Integer.parseInt(text) - 1];
                    break;
                case "Num_Live_Birth":

                    break;
                case "Is_Referred":
                    text = text.equalsIgnoreCase("0") ? "No" : "Yes";
                    break;
                case "Is_Cleaned":
                    text = text.equalsIgnoreCase("0") ? "No" : "Yes";
                    break;
                case "Chlorhexidin":
                    text = text.equalsIgnoreCase("0") ? "No" : "Yes";
                    break;
                case "Breastmilk_Fed":
                    text = text.equalsIgnoreCase("0") ? "No" : "Yes";
                    break;
                case "Not_Bathed":
                    text = text.equalsIgnoreCase("0") ? "No" : "Yes";
                    break;
                default:
                    break;
            }
        }catch (Exception e){}

        tview.setText(text);
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

//    private void pregnancyin2years(CommonPersonObject ecclient) {
//        String text = ecclient.getDetails().get("FWPSRPREGTWYRS")!=null?ecclient.getDetails().get("FWPSRPREGTWYRS"):"0";
//        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
//        stillbirth.setText(text);
//    }
//
//    private void historyofsb(CommonPersonObject ecclient) {
//        String text = ecclient.getDetails().get("FWPSRPRSB")!=null?ecclient.getDetails().get("FWPSRPRSB"):"0";
//        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
//        stillbirth.setText(text);
//    }
//
//    private void historyofmr(CommonPersonObject ecclient) {
//        String text = ecclient.getDetails().get("FWPSRPRMC")!=null?ecclient.getDetails().get("FWPSRPRMC"):"0";
//        TextView stillbirth = (TextView)findViewById(R.id.history_of_mr);
//        stillbirth.setText(text);
//
//    }



//    private void numberofChildrenView(CommonPersonObject ecclient) {
//        String text = ecclient.getDetails().get("FWPSRTOTBIRTH")!=null?ecclient.getDetails().get("FWPSRTOTBIRTH"):"0";
//        TextView numberofChildren = (TextView)findViewById(R.id.livechildren);
//        numberofChildren.setText(text);
//
//    }

    private void checkAnc3view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.pnc3_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_3");
        if(alertlist.size()!=0 && ecclient.getDetails().get("PNC3_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("PNC3_Due_Date")!=null?ecclient.getDetails().get("PNC3_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc3date);
                if((ecclient.getDetails().get("PNC3_current_formStatus")!=null?ecclient.getDetails().get("PNC3_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("PNC3_current_formStatus")!=null?ecclient.getDetails().get("PNC3_current_formStatus"):"").equalsIgnoreCase("urgent")){
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
        if(alertlist.size()!=0 && ecclient.getDetails().get("PNC2_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("PNC2_Due_Date")!=null?ecclient.getDetails().get("PNC2_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc2date);
                if((ecclient.getDetails().get("PNC2_current_formStatus")!=null?ecclient.getDetails().get("PNC2_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("PNC2_current_formStatus")!=null?ecclient.getDetails().get("PNC2_current_formStatus"):"").equalsIgnoreCase("urgent")){
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
//        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "pncrv_1");
        if(ecclient.getDetails().get("PNC1_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
//            for(int i = 0;i<alertlist.size();i++){
//                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("PNC1_Due_Date")!=null?ecclient.getDetails().get("PNC1_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.pnc1date);
                if((ecclient.getDetails().get("pnc1_current_formStatus")!=null?ecclient.getDetails().get("pnc1_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("pnc1_current_formStatus")!=null?ecclient.getDetails().get("pnc1_current_formStatus"):"").equalsIgnoreCase("urgent")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
                anc1date.setText(text);

//            }
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
    private String calculateage(int i) {



        return i/365 + " years";

    }
    public int getageindays(DateTime date){
        DateTime now = new DateTime();
        DateTimeZone dtz = DateTimeZone.getDefault();
        LocalDate today = now.toLocalDate();
        DateTime bday = date;
        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd");
        try {
            Log.v("today's date-birthdate", bday.toString(dtfOut));

            Log.v("today's date", startOfToday.toString(dtfOut));
        }catch (Exception e){

        }
//        LocalDate bday = new LocalDate(date);
//        LocalDate now = new LocalDate();
        Days days = Days.daysBetween(bday, startOfToday);
        return days.getDays();
    }
    public DateTime getdate(String date) {

//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime dt = formatter.parseDateTime(date+ " 00:00:00");
            return dt;
//            Date returndate = format.parse(date);
//            return returndate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

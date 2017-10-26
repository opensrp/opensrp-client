package org.ei.opensrp.dgfp.anc;

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
        TextView name = (TextView) findViewById(R.id.womandetail_name);
        TextView brid = (TextView) findViewById(R.id.womandetail_womabrid);
        TextView nid = (TextView) findViewById(R.id.womandetail_womanid);
        TextView hid = (TextView) findViewById(R.id.womandetail_womahid);
        TextView husbandname = (TextView) findViewById(R.id.womandetail_husbandname);
//        TextView pregnancystatus = (TextView) findViewById(R.id.womandetail_pregnancystatus);
        TextView marriagelife = (TextView) findViewById(R.id.womandetail_marriage_life);
//        TextView epicardno = (TextView) findViewById(R.id.womandetail_epicardno);
        TextView womandob = (TextView) findViewById(R.id.womandetail_womandob);
        TextView address = (TextView) findViewById(R.id.womandetail_address);
//        TextView mw_reg_date = (TextView) findViewById(R.id.mw_reg_date);
//        TextView psf_due_date = (TextView) findViewById(R.id.last_psf_date);
        TextView today = (TextView)findViewById(R.id.woman_detail_today);


        CommonPersonObjectClient womanclient = ancclient;
        ////////////////////////////////profile view /////////////////////////////////////////////////
        name.setText(humanize((womanclient.getColumnmaps().get("Mem_F_Name") != null ? womanclient.getColumnmaps().get("Mem_F_Name") : "").replace("+", "_")));

        brid.setText(humanize("BRID: "+(womanclient.getDetails().get("Mem_BRID") != null ? womanclient.getDetails().get("Mem_BRID") : "").replace("+", "_")));

        nid.setText(humanize("NID: "+(womanclient.getDetails().get("Mem_NID") != null ? womanclient.getDetails().get("Mem_NID") : "").replace("+", "_")));

        hid.setText(humanize("GOB HHID: "+(womanclient.getDetails().get("Member_GoB_HHID") != null ? womanclient.getDetails().get("Member_GoB_HHID") : "").replace("+", "_")));

        husbandname.setText("Spouse Name: "+((womanclient.getDetails().get("Spouse_Name") != null ? womanclient.getDetails().get("Spouse_Name") : "")));

        marriagelife.setText("Marriage Life: "+((womanclient.getDetails().get("Married_Life") != null ? womanclient.getDetails().get("Married_Life") : "")));


        womandob.setText("Age: "+calculateage(getageindays(getdate((womanclient.getColumnmaps().get("Calc_Dob_Confirm") != null ? womanclient.getColumnmaps().get("Calc_Dob_Confirm") : "")))));
//        SpannableString content = new SpannableString((womanclient.getDetails().get("contact_phone_number") != null ? womanclient.getDetails().get("contact_phone_number") : ""));
//        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//        contactno.setText(content);
//        contactno.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((womanclient.getDetails().get("contact_phone_number") != null ? womanclient.getDetails().get("contact_phone_number") : ""))));
////                startActivity(intent);
//            }
//        });
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



        /////////////////////////////////////////////////////////////////////////////////////////////


        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        DateUtil.setDefaultDateFormat("yyyy-MM-dd");
        AllCommonsRepository allmotherRepository = Context.getInstance().allCommonsRepositoryobjects("members");
        CommonPersonObject childobject = allmotherRepository.findByCaseID(ancclient.entityId());
        AllCommonsRepository elcorep = Context.getInstance().allCommonsRepositoryobjects("members");
        final CommonPersonObject elcoObject = elcorep.findByCaseID(childobject.getRelationalId());
//        try {
//            int days = DateUtil.dayDifference(DateUtil.getLocalDate((elcoObject.getDetails().get("FWBIRTHDATE") != null ?  elcoObject.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
//            Log.v("days",""+days);
//            int calc_age = days / 365;
//            age.setText(Html.fromHtml(getString(R.string.elco_age_label)+" " + calc_age));
//        }catch (Exception e){
//            e.printStackTrace();
//        }


        //        psf_due_date.setText(Elcoclient.getDetails().get("FWPSRDATE") != null ? Elcoclient.getDetails().get("FWPSRDATE") : "");


             /////from househld
        AllCommonsRepository allancRepository = Context.getInstance().allCommonsRepositoryobjects("members");
        CommonPersonObject ancobject = allancRepository.findByCaseID(ancclient.entityId());
        AllCommonsRepository allelcorep = Context.getInstance().allCommonsRepositoryobjects("members");
        CommonPersonObject elcoparent = allelcorep.findByCaseID(ancobject.getRelationalId());

//
        checkAnc1view(ancclient);
        checkAnc2view(ancclient);
        checkAnc3view(ancclient);
        checkAnc4view(ancclient);

        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.tt_given),"TT_Count");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.gravita),"Gravida");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.age_of_youngest_child),"Age_Youngest_Child");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.has_bled_during_pregnancy),"Bleeding");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.last_delivery_process),"Caesarean");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.prolonged_delivery),"Prolong_Delivery");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.child_died_within_hours),"Birth_Outcome");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.still_birth),"Dead_Child");
        assign_text_to_givenView(ancclient,(TextView)findViewById(R.id.heavy_bleeding_during_delivery),"Heavy_Blood_Flow");
//        numberofstillbirthview(elcoparent);
//        historyofmr(elcoparent);
//        historyofsb(elcoparent);
//        pregnancyin2years(elcoparent);
//        eddlay(ancclient);



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


        final ImageView householdview = (ImageView) findViewById(R.id.womandetailprofileview);

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


    private void assign_text_to_givenView(CommonPersonObjectClient ecclient,TextView tview,String detailvariable) {
        String text = ecclient.getDetails().get(detailvariable)!=null?ecclient.getDetails().get(detailvariable):"N/A";

        if(text.length() == 0) {
            text = "N/A";
            tview.setText(text);
            return;
        }
        switch (text){
            case "TT_Count":
                break;
            case "Gravida":
                break;
            case "Age_Youngest_Child":
                text += "m";
                break;
            case "Bleeding":
                text = isYes(Integer.parseInt(text));
                break;
            case "Caesarean":
                text = text == "0" ? "Normal" : "Caesarean";
                break;
            case "Prolong_Delivery":
                text = isYes(Integer.parseInt(text));
                break;
            case "Birth_Outcome":
                text = isYes(Integer.parseInt(text));
                break;
            case "Dead_Child":
                text = isYes(Integer.parseInt(text));
                break;
            case "Heavy_Blood_Flow":
                text = isYes(Integer.parseInt(text));
                break;
            default:
                break;
        }
        tview.setText(text);
    }

    private String isYes(int yes){
        return yes == 1 ? "Yes":"No";
    }


    private void checkAnc4view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout)findViewById(R.id.anc4_layout);
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_4");
        if(alertlist.size()!=0 && ecclient.getDetails().get("ANC4_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("ANC4_Due_Date")!=null?ecclient.getDetails().get("ANC4_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc4date);
                if((ecclient.getDetails().get("anc4_current_formStatus")!=null?ecclient.getDetails().get("anc4_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("anc4_current_formStatus")!=null?ecclient.getDetails().get("anc4_current_formStatus"):"").equalsIgnoreCase("urgent")){
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
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_3");
        if(alertlist.size()!=0 && ecclient.getDetails().get("ANC3_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("ANC3_Due_Date")!=null?ecclient.getDetails().get("ANC3_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc3date);
                if((ecclient.getDetails().get("anc3_current_formStatus")!=null?ecclient.getDetails().get("anc3_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("anc3_current_formStatus")!=null?ecclient.getDetails().get("anc3_current_formStatus"):"").equalsIgnoreCase("urgent")){
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
        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_2");
        if(alertlist.size()!=0 && ecclient.getDetails().get("ANC2_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
            for(int i = 0;i<alertlist.size();i++){
                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("ANC2_Due_Date")!=null?ecclient.getDetails().get("ANC2_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc2date);
                if((ecclient.getDetails().get("anc2_current_formStatus")!=null?ecclient.getDetails().get("anc2_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("anc2_current_formStatus")!=null?ecclient.getDetails().get("anc2_current_formStatus"):"").equalsIgnoreCase("urgent")){
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
//        List<Alert> alertlist = Context.getInstance().alertService().findByEntityIdAndAlertNames(ecclient.entityId(), "ancrv_1");
        if(ecclient.getDetails().get("ANC1_Due_Date")!=null){
//            alerttextstatus = setAlertStatus("ANC1",alertlist);
//            for(int i = 0;i<alertlist.size();i++){
//                String status = alertlist.get(i).status().value();
                String text = ecclient.getDetails().get("ANC1_Due_Date")!=null?ecclient.getDetails().get("ANC1_Due_Date"):"";
                TextView anc1date = (TextView)findViewById(R.id.anc1date);
                if((ecclient.getDetails().get("anc1_current_formStatus")!=null?ecclient.getDetails().get("anc1_current_formStatus"):"").equalsIgnoreCase("upcoming")){
                    anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
                }else if((ecclient.getDetails().get("anc1_current_formStatus")!=null?ecclient.getDetails().get("anc1_current_formStatus"):"").equalsIgnoreCase("urgent")){
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

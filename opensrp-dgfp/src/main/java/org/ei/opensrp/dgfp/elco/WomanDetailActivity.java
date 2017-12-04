package org.ei.opensrp.dgfp.elco;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.dgfp.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.domain.Alert;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
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
public class WomanDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;

    String [] arrayofbirthcontrol = new String []{"Referred","Oral Pill","Condom","Injectable","IUD","Implant","Pemananently Sterilized(Male)","Pemananently Sterilized(Female)","ECP","Referred for Side-effects","Referred for Methods"};



    //image retrieving

    public static CommonPersonObjectClient womanclient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.woman_detail_activity);
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
        TextView maritalstatus = (TextView) findViewById(R.id.womandetail_marital_status);
//        TextView contactno = (TextView) findViewById(R.id.womandetail_phone_number);
        TextView today = (TextView)findViewById(R.id.woman_detail_today);
//        TextView vaccination =(TextView) findViewById(R.id.womandetail_vaccinationstatus);

        //Woman summary/////////////////////////////////////////////////
        TextView registrationdate =(TextView) findViewById(R.id.womandetail_registration_date);
        TextView last_visit_date =(TextView) findViewById(R.id.womandetail_last_visit_date);
        TextView tt_status =(TextView) findViewById(R.id.womandetail_tt_status);
        TextView tt_doses =(TextView) findViewById(R.id.womandetail_tt_doses_given);
        TextView use_fp_method =(TextView) findViewById(R.id.womandetail_use_fp);
        TextView has_changed_fp_method =(TextView) findViewById(R.id.womandetail_has_changed_fp);
        TextView previous_fp_method =(TextView) findViewById(R.id.womandetail_previous_fp);
        TextView current_fp_method =(TextView) findViewById(R.id.womandetail_current_fp);
        TextView fp_given =(TextView) findViewById(R.id.womandetail_fp_product_given);
        ///////////////////////////////////////////////////////////////////////

        //PREGNANCY INFORMATION/////////////////////////////////////////////////

//        TextView pregnant =(TextView) findViewById(R.id.pregnanctdetail);
//        TextView lmp =(TextView) findViewById(R.id.womandetail_lmp);
//        TextView edd =(TextView) findViewById(R.id.womandetail_edd);
//        TextView ga =(TextView) findViewById(R.id.womandetail_ga);
        ///////////////////////////////////////////////////////////////////////


        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name.setText(humanize((womanclient.getColumnmaps().get("Mem_F_Name") != null ? womanclient.getColumnmaps().get("Mem_F_Name") : "").replace("+", "_")));

        brid.setText(humanize("BRID: "+(womanclient.getDetails().get("Mem_BRID") != null ? womanclient.getDetails().get("Mem_BRID") : "").replace("+", "_")));

        nid.setText(humanize("NID: "+(womanclient.getDetails().get("Mem_NID") != null ? womanclient.getDetails().get("Mem_NID") : "").replace("+", "_")));

        hid.setText(humanize("GOB HHID: "+(womanclient.getDetails().get("Member_GoB_HHID") != null ? womanclient.getDetails().get("Member_GoB_HHID") : "").replace("+", "_")));

        husbandname.setText("Spouse Name: "+((womanclient.getDetails().get("Spouse_Name") != null ? womanclient.getDetails().get("Spouse_Name") : "")));

        marriagelife.setText("Marriage Life: "+maritalStatus((womanclient.getDetails().get("Married_Life") != null ? womanclient.getDetails().get("Married_Life") : "")));


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
//        .setText("Address: "+(womanclient.getDetails().get("HH_Address") != null ? womanclient.getDetails().get("HH_Address") : ""));

        if(((womanclient.getColumnmaps().get("Marital_status")!=null?womanclient.getColumnmaps().get("Marital_status"):"")).equalsIgnoreCase("1")){
            maritalstatus.setText("Unmarried");
        }
        if((womanclient.getColumnmaps().get("Marital_status")!=null?womanclient.getColumnmaps().get("Marital_status"):"").equalsIgnoreCase("2")){
            maritalstatus.setText("Married");
        }
        if ((womanclient.getColumnmaps().get("Marital_status")!=null?womanclient.getColumnmaps().get("Marital_status"):"").equalsIgnoreCase("3")){
            maritalstatus.setText("Divorced/Widow/Widower");
        }
//        number_of_child.setText((womanclient.getDetails().get("calc_dob_confirm") != null ? womanclient.getDetails().get("calc_dob_confirm") : ""));

//        pregnancystatus.setText((womanclient.getDetails().get("pregnant") != null ? womanclient.getDetails().get("pregnant") : ""));
//       godhhid.setText(getString(R.string.hhid_gob_elco_label) + (womanclient.getColumnmaps().get("Member_GOB_HHID") != null ? womanclient.getColumnmaps().get("Member_GOB_HHID") : ""));
//        psf_due_date.setText(Elcoclient.getDetails().get("FWPSRDATE") != null ? Elcoclient.getDetails().get("FWPSRDATE") : "");


//        village.setText(humanize(womanclient.getDetails().get("Member_WARD") != null ? womanclient.getDetails().get("Member_WARD") : ""));



        final ImageView householdview = (ImageView) findViewById(R.id.womandetailprofileview);

        if (womanclient.getDetails().get("profilepic") != null) {
            HouseHoldDetailActivity.setImagetoHolder(this, womanclient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
        }
//        TextView lmp  = (TextView)findViewById(R.id.lmp_date);
//        lmp.setText(womanclient.getDetails().get("final_lmp")!=null?womanclient.getDetails().get("final_lmp"):"N/A");
//        edd.setText(womanclient.getDetails().get("final_edd")!=null?womanclient.getDetails().get("final_edd"):"N/A");
//        ga.setText(womanclient.getDetails().get("final_ga")!=null?womanclient.getDetails().get("final_ga")+" weeks":"N/A");



        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c.getTime());
        today.setText("Today: " +formattedDate+" ");
//        vaccination.setText(immunizationStatus(womanclient));


//        LinearLayout measleslayout = (LinearLayout)findViewById(R.id.measles_layout);
//        LinearLayout tt1layout = (LinearLayout)findViewById(R.id.tt1_layout);
//        LinearLayout tt2layout = (LinearLayout)findViewById(R.id.tt2_layout);
//        LinearLayout tt3layout = (LinearLayout)findViewById(R.id.tt3layout);
//        LinearLayout tt4layout = (LinearLayout)findViewById(R.id.tt4layout);
//        LinearLayout tt5layout = (LinearLayout)findViewById(R.id.tt5layout);
//
//        if(!(womanclient.getDetails().get("TT1_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT1_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView tt1_text = (TextView)findViewById(R.id.tt1date);
        //"██"+
//        ttcheck(womanclient,tt3TextView,(View)findViewById(R.id.womandetail_tt3_block),"tt3_final","Woman_TT3");
//        ttcheck(womanclient,tt4TextView,(View)findViewById(R.id.womandetail_tt4_block),"tt4_final","Woman_TT4");
//        ttcheck(womanclient,tt5TextView,(View)findViewById(R.id.womandetail_tt5_block),"tt5_final","Woman_TT5");


        registrationdate.setText((womanclient.getDetails().get("Member_Reg_Date")!=null?womanclient.getDetails().get("Member_Reg_Date"):"N/A"));
        last_visit_date.setText((womanclient.getDetails().get("ELCO_Date")!=null?womanclient.getDetails().get("ELCO_Date"):"N/A"));

        String ttstatusstring = (womanclient.getDetails().get("TT_Status")!=null?womanclient.getDetails().get("TT_Status"):"N/A");
        if(ttstatusstring.equalsIgnoreCase("0")){
            ttstatusstring = "No";
        }else if(ttstatusstring.equalsIgnoreCase("1")){
            ttstatusstring = "Yes";
        }
        tt_status.setText(ttstatusstring);

        tt_doses.setText((womanclient.getDetails().get("TT_Dose")!=null?womanclient.getDetails().get("TT_Dose"):"N/A"));

        String usingfp = (womanclient.getDetails().get("Using_FP")!=null?womanclient.getDetails().get("Using_FP"):"N/A");
        if(usingfp.equalsIgnoreCase("0")){
            usingfp = "No";
        }else if(usingfp.equalsIgnoreCase("1")){
            usingfp = "Yes";
        }
        use_fp_method.setText(usingfp);

         String has_changed_fp = (womanclient.getDetails().get("Has_Changed")!=null?womanclient.getDetails().get("Has_Changed"):"N/A");
        if(has_changed_fp.equalsIgnoreCase("0")){
            has_changed_fp = "No";
        }else if(has_changed_fp.equalsIgnoreCase("1")){
            has_changed_fp = "Yes";
        }
        has_changed_fp_method.setText(has_changed_fp);


        String lastfp = (womanclient.getDetails().get("Last_FP_Method")!=null?womanclient.getDetails().get("Last_FP_Method"):"N/A");
        if(!(lastfp.equalsIgnoreCase("N/A")||lastfp.equalsIgnoreCase("99")||lastfp.equalsIgnoreCase(""))){
            previous_fp_method.setText(arrayofbirthcontrol[Integer.parseInt(lastfp)]);
        }else if (lastfp.equalsIgnoreCase("99")){
            previous_fp_method.setText("None");
        }else{
            previous_fp_method.setText(lastfp);
        }

        String currentfp = (womanclient.getDetails().get("Changed_FP_Method")!=null?womanclient.getDetails().get("Changed_FP_Method"):"N/A");
        if(!(currentfp.equalsIgnoreCase("N/A")||currentfp.equalsIgnoreCase(""))){
            current_fp_method.setText(arrayofbirthcontrol[Integer.parseInt(currentfp)]);
        }else{
            current_fp_method.setText(currentfp);
        }
         fp_given.setText((womanclient.getDetails().get("Calc_FP_Given_Date")!=null?womanclient.getDetails().get("Calc_FP_Given_Date"):"N/A"));



//        tt2TextView.setText((womanclient.getDetails().get("tt2_final")!=null?womanclient.getDetails().get("tt2_final"):"N/A"));
//        tt3TextView.setText((womanclient.getDetails().get("tt3_final")!=null?womanclient.getDetails().get("tt3_final"):"N/A"));
//        tt4TextView.setText((womanclient.getDetails().get("tt4_final")!=null?womanclient.getDetails().get("tt4_final"):"N/A"));
//        tt5TextView.setText((womanclient.getDetails().get("tt5_final")!=null?womanclient.getDetails().get("tt5_final"):"N/A"));
////        }else{
//            tt1layout.setVisibility(View.GONE);
//        }
//        if(!(womanclient.getDetails().get("TT2_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT2_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView tt2_text = (TextView)findViewById(R.id.tt2date);
//            tt2_text.setText((womanclient.getDetails().get("TT2_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT2_Date_of_Vaccination"):""));
//        }else{
//            tt2layout.setVisibility(View.GONE);
//        }
//        if(!(womanclient.getDetails().get("TT3_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT3_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView tt3_text = (TextView)findViewById(R.id.tt3);
//            tt3_text.setText((womanclient.getDetails().get("TT3_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT3_Date_of_Vaccination"):""));
//        }else{
//            tt3layout.setVisibility(View.GONE);
//        }
//        if(!(womanclient.getDetails().get("TT4_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT4_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView tt4_text = (TextView)findViewById(R.id.tt4);
//            tt4_text.setText((womanclient.getDetails().get("TT4_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT4_Date_of_Vaccination"):""));
//        }else{
//            tt4layout.setVisibility(View.GONE);
//        }
//        if(!(womanclient.getDetails().get("TT5_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT5_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView tt5_text = (TextView)findViewById(R.id.tt5);
//            tt5_text.setText((womanclient.getDetails().get("TT5_Date_of_Vaccination")!=null?womanclient.getDetails().get("TT5_Date_of_Vaccination"):""));
//        }else{
//            tt5layout.setVisibility(View.GONE);
//        }
//        if(!(womanclient.getDetails().get("measles_Date_of_Vaccination")!=null?womanclient.getDetails().get("measles_Date_of_Vaccination"):"").equalsIgnoreCase("")){
//            TextView measles = (TextView)findViewById(R.id.measles_date);
//            measles.setText((womanclient.getDetails().get("measles_Date_of_Vaccination")!=null?womanclient.getDetails().get("measles_Date_of_Vaccination"):""));
//        }else{
//            measleslayout.setVisibility(View.GONE);
//        }



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

//    private void eddlay(CommonPersonObjectClient ancclient) {
//        TextView edd = (TextView)findViewById(R.id.lmp_date);
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date edd_date = format.parse(ancclient.getColumnmaps().get("FWPSRLMP")!=null?ancclient.getColumnmaps().get("FWPSRLMP"):"");
//            GregorianCalendar calendar = new GregorianCalendar();
//            calendar.setTime(edd_date);
//            calendar.add(Calendar.DATE, 259);
//            edd_date.setTime(calendar.getTime().getTime());
//            edd.setText(format.format(edd_date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//
    private String calculateage(int i) {

            return i/365 + " years";

    }
    public int getageindays(DateTime date){
        DateTime now = new DateTime();
        DateTimeZone dtz = DateTimeZone.getDefault();
        LocalDate today = now.toLocalDate();
        DateTime bday = date;
        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());

        Log.d("start : " , bday.toString());
        Log.d("end",startOfToday.toString());
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
    public void ttcheck(CommonPersonObjectClient womanclient, TextView tt1TextView,View block,String ttfinalKey,String ttschedulename){
        if(!(womanclient.getDetails().get(ttfinalKey)!=null?womanclient.getDetails().get(ttfinalKey):"").equalsIgnoreCase("")){
            block.setBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            tt1TextView.setText((womanclient.getDetails().get(ttfinalKey)!=null?womanclient.getDetails().get(ttfinalKey):""));
        }else{

            List<Alert> Woman_TT1alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(womanclient.entityId(), ttschedulename);
            if(Woman_TT1alertlist_for_client.size()>0) {
                for (int i = 0; i < Woman_TT1alertlist_for_client.size(); i++) {
                    if (Woman_TT1alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_upcoming_yellow));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,womanclient);
                        tt1TextView.setText(text);
                    } else if (Woman_TT1alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_urgent_red));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,womanclient);
                        tt1TextView.setText(text);
                    } else if (Woman_TT1alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                        block.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,womanclient);
                        tt1TextView.setText(text);
                    } else if (Woman_TT1alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_upcoming_light_blue));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,womanclient);
                        tt1TextView.setText(text);
                    }

                }
            }else{
                block.setBackgroundColor(getResources().getColor(R.color.status_bar_text_almost_white));
                String text = "";
                tt1TextView.setText(text);

            }

            }

    }

    private String getVaccineDateText(String Schedulename, CommonPersonObjectClient pc) {
        if (Schedulename.equalsIgnoreCase("Woman_TT1")) {
            return ((pc.getDetails().get("final_lmp") != null ? pc.getDetails().get("final_lmp") : ""));
        }
        if (Schedulename.equalsIgnoreCase("Woman_TT2")) {
            return ( setDate((pc.getDetails().get("tt1_final") != null ? pc.getDetails().get("tt1_final") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("Woman_TT3")) {
            return ( setDate((pc.getDetails().get("tt2_final") != null ? pc.getDetails().get("tt2_final") : ""),182));
        }
        if (Schedulename.equalsIgnoreCase("Woman_TT4")) {
            return (setDate((pc.getDetails().get("tt3_final") != null ? pc.getDetails().get("tt3_final") : ""),364));
        }
        if (Schedulename.equalsIgnoreCase("Woman_TT5")) {
            return (setDate((pc.getDetails().get("tt4_final") != null ? pc.getDetails().get("tt4_final") : ""),364));
        }
        return "";
    }
    public String setDate(String date, int daystoadd) {

        Date lastdate = converdatefromString(date);

        if(lastdate!=null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
            calendar.add(Calendar.DATE, daystoadd);//8 weeks
            lastdate.setTime(calendar.getTime().getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //            String result = String.format(Locale.ENGLISH, format.format(lastdate) );
            return (format.format(lastdate));
            //             due_visit_date.append(format.format(lastdate));

        }else{
            return "";
        }
    }
    public Date converdatefromString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        }catch (Exception e){
            return null;
        }
        return convertedDate;
    }


    public String immunizationStatus(CommonPersonObjectClient womanclient){
        boolean tt1 = !((womanclient.getDetails().get("tt1_final")!=null?womanclient.getDetails().get("tt1_final"):"")).equalsIgnoreCase("");
        boolean tt2 = !((womanclient.getDetails().get("tt2_final")!=null?womanclient.getDetails().get("tt2_final"):"")).equalsIgnoreCase("");
        boolean tt3 = !((womanclient.getDetails().get("tt3_final")!=null?womanclient.getDetails().get("tt3_final"):"")).equalsIgnoreCase("");
        boolean tt4 = !((womanclient.getDetails().get("tt4_final")!=null?womanclient.getDetails().get("tt4_final"):"")).equalsIgnoreCase("");
        boolean tt5 = !((womanclient.getDetails().get("tt5_final")!=null?womanclient.getDetails().get("tt5_final"):"")).equalsIgnoreCase("");
        String immunization_status = "";
        if(tt1||tt2||tt3||tt4||tt5){
            immunization_status = "Partially Immunized";
        }else{
            immunization_status = "Not Immunized";
        }
        if(tt1&&tt2&&tt3&&tt4&&tt5){
            immunization_status = "Fully Immunized";
        }
        return immunization_status;
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
    private String maritalStatus(String s) {
        if(!StringUtils.isBlank(s)){
            if(s.equalsIgnoreCase("1")){
                return getResources().getString(R.string.unmarried);
            }else if(s.equalsIgnoreCase("2")){
                return getResources().getString(R.string.married);
            }else if(s.equalsIgnoreCase("3")){
                return getResources().getString(R.string.widow);
            }else if(s.equalsIgnoreCase("4")){
                return getResources().getString(R.string.separated);
            }else if(s.equalsIgnoreCase("5")){
                return getResources().getString(R.string.divorced);
            }
        }
        return "";
    }
}

package org.ei.opensrp.dgfp.injectables;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.ei.opensrp.AllConstants;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.dgfp.BuildConfig;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.StringUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
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

import io.fabric.sdk.android.services.events.EventsFilesManager;
import util.ImageCache.ImageCacheParams;
import util.ImageFetcher;

public class McareInjectablesDetailActivity extends Activity {
    private static final String IMAGE_CACHE_DIR = "thumbs";
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "ImageGridFragment";
    static String bindobject;
    static File currentfile;
    static String entityid;
    public static CommonPersonObjectClient injectableClient;
    private static ImageFetcher mImageFetcher;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    static ImageView mImageView;
    String mCurrentPhotoPath;

    /* renamed from: org.ei.opensrp.dgfp.injectables.McareInjectablesDetailActivity.1 */


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.injectable_detail_activity);
        TextView name = (TextView) findViewById(R.id.woman_detail_information);
        TextView brid = (TextView) findViewById(R.id.womandetail_womabrid);
        TextView nid = (TextView) findViewById(R.id.womandetail_womanid);
        TextView hid = (TextView) findViewById(R.id.womandetail_womahid);
        TextView husbandname = (TextView) findViewById(R.id.womandetail_husbandname);
        TextView marriagelife = (TextView) findViewById(R.id.womandetail_marriage_life);
        TextView womandob = (TextView) findViewById(R.id.womandetail_womandob);
        TextView address = (TextView) findViewById(R.id.womandetail_address);
        TextView today = (TextView) findViewById(R.id.woman_detail_today);

        TextView dose1 = (TextView) findViewById(R.id.dose_one) ;
        TextView dose2 = (TextView) findViewById(R.id.dose_two) ;
        TextView dose3 = (TextView) findViewById(R.id.dose_three) ;
        TextView dose4 = (TextView) findViewById(R.id.dose_four) ;
        TextView dose5 = (TextView) findViewById(R.id.dose_five) ;



        CommonPersonObjectClient womanclient = injectableClient;
        name.setText(StringUtil.humanize((womanclient.getColumnmaps().get("Mem_F_Name") != null ? (String) womanclient.getColumnmaps().get("Mem_F_Name") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)));
        brid.setText(StringUtil.humanize("BRID: " + (womanclient.getDetails().get("Mem_BRID") != null ? (String) womanclient.getDetails().get("Mem_BRID") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)));
        nid.setText(StringUtil.humanize("NID: " + (womanclient.getDetails().get("Mem_NID") != null ? (String) womanclient.getDetails().get("Mem_NID") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)));
        hid.setText(StringUtil.humanize("GOB HHID: " + (womanclient.getDetails().get("Member_GoB_HHID") != null ? (String) womanclient.getDetails().get("Member_GoB_HHID") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)));
        husbandname.setText("Spouse Name: " + (womanclient.getDetails().get("Spouse_Name") != null ? (String) womanclient.getDetails().get("Spouse_Name") : BuildConfig.FLAVOR));
        marriagelife.setText("Marriage Life: " + (womanclient.getDetails().get("Married_Life") != null ? (String) womanclient.getDetails().get("Married_Life") : BuildConfig.FLAVOR));
        womandob.setText("Age: " + calculateage(getageindays(getdate(womanclient.getDetails().get("Calc_Dob_Confirm") != null ? (String) womanclient.getDetails().get("Calc_Dob_Confirm") : BuildConfig.FLAVOR))));
        address.setText("Address: " + StringUtil.humanize((womanclient.getDetails().get("Mem_Subunit") != null ? (String) womanclient.getDetails().get("Mem_Subunit") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)) + AllConstants.COMMA_WITH_SPACE + StringUtil.humanize((womanclient.getDetails().get("Mem_Village_Name") != null ? (String) womanclient.getDetails().get("Mem_Village_Name") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)) + AllConstants.COMMA_WITH_SPACE + StringUtil.humanize((womanclient.getDetails().get("Mem_Mauzapara") != null ? (String) womanclient.getDetails().get("Mem_Mauzapara") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)) + AllConstants.COMMA_WITH_SPACE + StringUtil.humanize((womanclient.getDetails().get("Mem_Union") != null ? (String) womanclient.getDetails().get("Mem_Union") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)) + AllConstants.COMMA_WITH_SPACE + StringUtil.humanize((womanclient.getDetails().get("Mem_Upazilla") != null ? (String) womanclient.getDetails().get("Mem_Upazilla") : BuildConfig.FLAVOR).replace("+", EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR)));



        dose1.setText(womanclient.getDetails().get("Injection_Date") != null ? womanclient.getDetails().get("Injection_Date") : "Not Given");
        dose2.setText(womanclient.getDetails().get("Injection_Date") != null ? womanclient.getDetails().get("Injection_Date") : "Not Given");
        dose3.setText(womanclient.getDetails().get("Injection_Date") != null ? womanclient.getDetails().get("Injection_Date") : "Not Given");
        dose4.setText(womanclient.getDetails().get("Injection_Date") != null ? womanclient.getDetails().get("Injection_Date") : "Not Given");
        dose5.setText(womanclient.getDetails().get("Injection_Date") != null ? womanclient.getDetails().get("Injection_Date") : "Not Given");

        Calendar c = Calendar.getInstance();

        setSideEffects(null,womanclient,"Side_Effects");
        Log.d("Side_Effects",womanclient.getDetails().get("Side_Effects"));

        today.setText("Today: " + new SimpleDateFormat("dd/MM/yyyy").format(c.getTime()) + AllConstants.SPACE);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView householdview = (ImageView) findViewById(R.id.householdprofileview);
        if (injectableClient.getDetails().get("profilepic") != null) {
            setImagetoHolder(this, (String) injectableClient.getDetails().get("profilepic"), householdview, R.mipmap.woman_placeholder);
        }
    }

    private void setSideEffects(TextView tv,CommonPersonObjectClient womanclient, String detailVariable){
        TextView menstruationstopped = (TextView) findViewById(R.id.menstruationstopped) ;
        TextView heavybloodflow = (TextView) findViewById(R.id.heavybloodflow) ;
        TextView blood_drop_between_two_menstrual = (TextView) findViewById(R.id.blood_drop_between_two_menstrual) ;
        TextView irregular_period = (TextView) findViewById(R.id.irregular_period) ;
        TextView slightlyweightgain = (TextView) findViewById(R.id.slightlyweightgain) ;

        String[] sideEffects = womanclient.getDetails().get(detailVariable) != null ? womanclient.getDetails().get(detailVariable).split(" ") : null;

        if(sideEffects == null)
            return;

        for(int i = 0; i < sideEffects.length; i++){

            switch (Integer.parseInt(sideEffects[i])){
                case 1:
                    menstruationstopped.setText("Yes");
                    break;
                case 2:
                    heavybloodflow.setText("Yes");
                    break;
                case 3:
                    blood_drop_between_two_menstrual.setText("Yes");
                    break;
                case 4:
                    irregular_period.setText("Yes");
                    break;
                case 5:
                    slightlyweightgain.setText("Yes");
                    break;
                default:
                    break;
            }

        }
    }

    private void eddlay(CommonPersonObjectClient ancclient) {
        TextView edd = (TextView) findViewById(R.id.edd_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getColumnmaps().get("FWPSRLMP") != null ? (String) ancclient.getColumnmaps().get("FWPSRLMP") : BuildConfig.FLAVOR);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(edd_date);
            calendar.add(5, 259);
            edd_date.setTime(calendar.getTime().getTime());
            edd.setText(format.format(edd_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void assign_text_to_givenView(CommonPersonObjectClient ecclient, TextView tview, String detailvariable) {
        tview.setText(ecclient.getDetails().get(detailvariable) != null ? (String) ecclient.getDetails().get(detailvariable) : "N/A");
    }

    private void checkAnc4view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout) findViewById(R.id.anc4_layout);
        AlertService alertService = Context.getInstance().alertService();
        String entityId = ecclient.entityId();
        String[] strArr = new String[REQUEST_TAKE_PHOTO];
        strArr[0] = "ancrv_4";
        List<Alert> alertlist = alertService.findByEntityIdAndAlertNames(entityId, strArr);
        if (alertlist.size() == 0 || ecclient.getDetails().get("ANC4_Due_Date") == null) {
            anc1layout.setVisibility(8);
            return;
        }
        for (int i = 0; i < alertlist.size(); i += REQUEST_TAKE_PHOTO) {
            String status = ((Alert) alertlist.get(i)).status().value();
            String text = ecclient.getDetails().get("ANC4_Due_Date") != null ? (String) ecclient.getDetails().get("ANC4_Due_Date") : BuildConfig.FLAVOR;
            TextView anc1date = (TextView) findViewById(R.id.anc4date);
            if ((ecclient.getDetails().get("anc4_current_formStatus") != null ? (String) ecclient.getDetails().get("anc4_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("upcoming")) {
                anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
            } else {
                if ((ecclient.getDetails().get("anc4_current_formStatus") != null ? (String) ecclient.getDetails().get("anc4_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("urgent")) {
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
            }
            anc1date.setText(text);
        }
    }

    private void checkAnc3view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout) findViewById(R.id.anc3_layout);
        AlertService alertService = Context.getInstance().alertService();
        String entityId = ecclient.entityId();
        String[] strArr = new String[REQUEST_TAKE_PHOTO];
        strArr[0] = "ancrv_3";
        List<Alert> alertlist = alertService.findByEntityIdAndAlertNames(entityId, strArr);
        if (alertlist.size() == 0 || ecclient.getDetails().get("ANC3_Due_Date") == null) {
            anc1layout.setVisibility(8);
            return;
        }
        for (int i = 0; i < alertlist.size(); i += REQUEST_TAKE_PHOTO) {
            String status = ((Alert) alertlist.get(i)).status().value();
            String text = ecclient.getDetails().get("ANC3_Due_Date") != null ? (String) ecclient.getDetails().get("ANC3_Due_Date") : BuildConfig.FLAVOR;
            TextView anc1date = (TextView) findViewById(R.id.anc3date);
            if ((ecclient.getDetails().get("anc3_current_formStatus") != null ? (String) ecclient.getDetails().get("anc3_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("upcoming")) {
                anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
            } else {
                if ((ecclient.getDetails().get("anc3_current_formStatus") != null ? (String) ecclient.getDetails().get("anc3_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("urgent")) {
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
            }
            anc1date.setText(text);
        }
    }

    private void checkAnc2view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout) findViewById(R.id.anc2_layout);
        AlertService alertService = Context.getInstance().alertService();
        String entityId = ecclient.entityId();
        String[] strArr = new String[REQUEST_TAKE_PHOTO];
        strArr[0] = "ancrv_2";
        List<Alert> alertlist = alertService.findByEntityIdAndAlertNames(entityId, strArr);
        if (alertlist.size() == 0 || ecclient.getDetails().get("ANC2_Due_Date") == null) {
            anc1layout.setVisibility(8);
            return;
        }
        for (int i = 0; i < alertlist.size(); i += REQUEST_TAKE_PHOTO) {
            String status = ((Alert) alertlist.get(i)).status().value();
            String text = ecclient.getDetails().get("ANC2_Due_Date") != null ? (String) ecclient.getDetails().get("ANC2_Due_Date") : BuildConfig.FLAVOR;
            TextView anc1date = (TextView) findViewById(R.id.anc2date);
            if ((ecclient.getDetails().get("anc2_current_formStatus") != null ? (String) ecclient.getDetails().get("anc2_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("upcoming")) {
                anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
            } else {
                if ((ecclient.getDetails().get("anc2_current_formStatus") != null ? (String) ecclient.getDetails().get("anc2_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("urgent")) {
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
            }
            anc1date.setText(text);
        }
    }

    private void checkAnc1view(CommonPersonObjectClient ecclient) {
        LinearLayout anc1layout = (LinearLayout) findViewById(R.id.anc1_layout);
        if (ecclient.getDetails().get("ANC1_Due_Date") != null) {
            String text = ecclient.getDetails().get("ANC1_Due_Date") != null ? (String) ecclient.getDetails().get("ANC1_Due_Date") : BuildConfig.FLAVOR;
            TextView anc1date = (TextView) findViewById(R.id.anc1date);
            if ((ecclient.getDetails().get("anc1_current_formStatus") != null ? (String) ecclient.getDetails().get("anc1_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("upcoming")) {
                anc1date.setTextColor(getResources().getColor(R.color.alert_complete_green));
            } else {
                if ((ecclient.getDetails().get("anc1_current_formStatus") != null ? (String) ecclient.getDetails().get("anc1_current_formStatus") : BuildConfig.FLAVOR).equalsIgnoreCase("urgent")) {
                    anc1date.setTextColor(getResources().getColor(R.color.alert_urgent_red));
                }
            }
            anc1date.setText(text);
            return;
        }
        anc1layout.setVisibility(8);
    }

    private File createImageFile() throws IOException {
        File image = File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + EventsFilesManager.ROLL_OVER_FILE_NAME_SEPARATOR, ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
        this.mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(ImageView imageView) {
        mImageView = imageView;
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }
            if (photoFile != null) {
                currentfile = photoFile;
                takePictureIntent.putExtra("output", Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == -1) {
            HashMap<String, String> details = new HashMap();
            details.put("profilepic", currentfile.getAbsolutePath());
            saveimagereference(bindobject, entityid, details);
            Options options = new Options();
            options.inPreferredConfig = Config.ARGB_8888;
            mImageView.setImageBitmap(BitmapFactory.decodeFile(currentfile.getPath(), options));
        }
    }

    public void saveimagereference(String bindobject, String entityid, Map<String, String> details) {
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid, details);
    }

    public static void setImagetoHolder(Activity activity, String file, ImageView view, int placeholder) {
        mImageThumbSize = HttpStatus.SC_MULTIPLE_CHOICES;
        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        ImageCacheParams cacheParams = new ImageCacheParams(activity, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.5f);
        mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
        mImageFetcher.setLoadingImage(placeholder);
        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
        mImageFetcher.loadImage(AllConstants.FILE_PATH_STARTING_STRING + file, view);
    }

    private String calculateage(int i) {
        return (i / 365) + " years";
    }

    public int getageindays(DateTime date) {
        DateTime now = new DateTime();
        DateTimeZone dtz = DateTimeZone.getDefault();
        ReadableInstant bday = date;
        ReadableInstant startOfToday = now.toLocalDate().toDateTimeAtStartOfDay(now.getZone());
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd");
        try {
            //Log.v("today's date-birthdate", bday.toString(dtfOut));
            //Log.v("today's date", startOfToday.toString(dtfOut));
        } catch (Exception e) {
        }
        return Days.daysBetween(bday, startOfToday).getDays();
    }

    public DateTime getdate(String date) {
        try {
            return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(date + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

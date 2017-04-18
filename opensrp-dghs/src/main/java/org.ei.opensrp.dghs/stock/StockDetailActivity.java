package org.ei.opensrp.dghs.stock;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.dghs.R;
import org.ei.opensrp.dghs.domain.VaccineRepo;
import org.ei.opensrp.dghs.domain.VaccineWrapper;
import org.ei.opensrp.dghs.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.dghs.vaccineFragment.UndoVaccinationDialogFragment;
import org.ei.opensrp.dghs.vaccineFragment.VaccinationActionListener;
import org.ei.opensrp.dghs.vaccineFragment.VaccinationDialogFragment;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.opensrp.api.domain.User;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 5/11/15.
 */
public class StockDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;

    HashMap<String,String> update =  new HashMap<String, String>();





    //image retrieving

    public static CommonPersonObjectClient childclient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.stock_detail_activity);
        ListView  listView = (ListView)findViewById(R.id.listViewofstock);



        TextView vaccinator_name = (TextView)findViewById(R.id.vaccinator_name);
        TextView facility_name = (TextView)findViewById(R.id.facility_name);
        TextView target_month = (TextView)findViewById(R.id.monthly_target);
        TextView target_year = (TextView)findViewById(R.id.yearly_target);


        vaccinator_name.setText("Vaccinator Name: "+context.allSharedPreferences().fetchRegisteredANM());
        facility_name.setText("Facility Name: " +"");
        target_month.setText("Monthly Target: "+(childclient.getDetails().get("Target_assigned_for_vaccination_at_each_month")!=null?childclient.getDetails().get("Target_assigned_for_vaccination_at_each_month"):"0"));
        target_year.setText("Yearly Target: "+(childclient.getDetails().get("Target_assigned_for_vaccination_for_the_year")!=null?childclient.getDetails().get("Target_assigned_for_vaccination_for_the_year"):"0"));



        ArrayList<vaccinestockRow> vaccines = new ArrayList<vaccinestockRow>();
//        {"name":"bcg_wasted"},
//        {"name":"bcg_used"},
//        {"name":"opv_wasted"},
//        {"name":"opv_used"},
//        {"name":"ipv_wasted"},
//        {"name":"ipv_used"},
//        {"name":"pcv_wasted"},
//        {"name":"pcv_used"},
//        {"name":"penta_wasted"},
//        {"name":"penta_used"},
//        {"name":"measles_wasted"},
//        {"name":"measles_used"},
//        {"name":"tt_wasted"},
//        {"name":"tt_used"},
//        {"name":"dilutants_wasted"},
//        {"name":"dilutants_used"}
        vaccines.add(new vaccinestockRow("Item Name","Used","Wasted"));
        vaccines.add(new vaccinestockRow("BCG",(childclient.getColumnmaps().get("bcg_used")!=null?childclient.getColumnmaps().get("bcg_used"):"0"),(childclient.getColumnmaps().get("bcg_wasted")!=null?childclient.getColumnmaps().get("bcg_wasted"):"0")));
        vaccines.add(new vaccinestockRow("OPV",(childclient.getColumnmaps().get("opv_used")!=null?childclient.getColumnmaps().get("opv_used"):"0"),(childclient.getColumnmaps().get("opv_wasted")!=null?childclient.getColumnmaps().get("opv_wasted"):"0")));
        vaccines.add(new vaccinestockRow("IPV",(childclient.getColumnmaps().get("ipv_used")!=null?childclient.getColumnmaps().get("ipv_used"):"0"),(childclient.getColumnmaps().get("ipv_wasted")!=null?childclient.getColumnmaps().get("ipv_wasted"):"0")));
        vaccines.add(new vaccinestockRow("PCV",(childclient.getColumnmaps().get("pcv_used")!=null?childclient.getColumnmaps().get("pcv_used"):"0"),(childclient.getColumnmaps().get("pcv_wasted")!=null?childclient.getColumnmaps().get("pcv_wasted"):"0")));
        vaccines.add(new vaccinestockRow("Pentavalent",(childclient.getColumnmaps().get("penta_used")!=null?childclient.getColumnmaps().get("penta_used"):"0"),(childclient.getColumnmaps().get("penta_wasted")!=null?childclient.getColumnmaps().get("penta_wasted"):"0")));
        vaccines.add(new vaccinestockRow("Measles",(childclient.getColumnmaps().get("measles_used")!=null?childclient.getColumnmaps().get("measles_used"):"0"),(childclient.getColumnmaps().get("measles_wasted")!=null?childclient.getColumnmaps().get("measles_wasted"):"0")));
        vaccines.add(new vaccinestockRow("Tetanus",(childclient.getColumnmaps().get("tt_used")!=null?childclient.getColumnmaps().get("tt_used"):"0"),(childclient.getColumnmaps().get("tt_wasted")!=null?childclient.getColumnmaps().get("tt_wasted"):"0")));
//        vaccines.add(new vaccinestockRow("BCG",(childclient.getColumnmaps().get("bcg_used")!=null?childclient.getColumnmaps().get("bcg_used"):"0"),(childclient.getColumnmaps().get("bcg_wasted")!=null?childclient.getColumnmaps().get("bcg_wasted"):"0")));
//        vaccines.add(new vaccinestockRow("BCG",(childclient.getColumnmaps().get("bcg_used")!=null?childclient.getColumnmaps().get("bcg_used"):"0"),(childclient.getColumnmaps().get("bcg_wasted")!=null?childclient.getColumnmaps().get("bcg_wasted"):"0")));




        stockListadapter stla = new stockListadapter(this,vaccines);
        stla.notifyDataSetChanged();
        stla.getCount();
        listView.setAdapter(stla);



    }
    public LinearLayout makevaccinerow (String vaccinename,String vaccinedate){
        LinearLayout vaccinerow = (LinearLayout) getLayoutInflater().inflate(R.layout.vaccine_row, null);
        ((TextView)vaccinerow.findViewById(R.id.vaccinename)).setText(vaccinename);
        ((TextView)vaccinerow.findViewById(R.id.vaccine_date)).setText(vaccinedate);

        return vaccinerow;
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

    class stockListadapter extends ArrayAdapter<vaccinestockRow>{
        ArrayList<vaccinestockRow> vaccines;


        public stockListadapter(android.content.Context context, ArrayList<vaccinestockRow> vaccines) {
            super(context, R.layout.stock_layout_row);
            this.vaccines = vaccines;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position

            // Check if an existing view is being reused, otherwise inflate the view

                // If there's no view to re-use, inflate a brand new view for row
//                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.stock_layout_row, parent, false);
            ((TextView)convertView.findViewById(R.id.date)).setText(vaccines.get(position).getItemname());
            ((TextView)convertView.findViewById(R.id.total_vaccine_stock_used)).setText(vaccines.get(position).getUsed());
            ((TextView)convertView.findViewById(R.id.total_vaccine_stock_wasted)).setText(vaccines.get(position).getWasted());
            return convertView;
        }

        @Override
        public int getCount() {
            return vaccines.size();
        }
    }
    class vaccinestockRow{
        String itemname;
        String used;
        String wasted;

        public vaccinestockRow(String itemname, String used, String wasted) {
            this.itemname = itemname;
            this.used = used;
            this.wasted = wasted;
        }

        public String getItemname() {
            return itemname;
        }

        public String getUsed() {
            return used;
        }

        public String getWasted() {
            return wasted;
        }
    }
    }










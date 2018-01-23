package org.smartregister.indonesia.child;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.Context;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.face.camera.SmartShutterActivity;
import org.smartregister.indonesia.face.camera.utils.Tools;
import org.smartregister.indonesia.kartu_ibu.KIDetailActivity;
import org.smartregister.indonesia.lib.FlurryFacade;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.util.HashMap;

import static org.smartregister.util.StringUtil.humanize;

/**
 * Created by Iq on 07/09/16.
 */
public class AnakDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = AnakDetailActivity.class.getSimpleName();
    public static CommonPersonObjectClient childclient;

    private static HashMap<String, String> hash;
    private boolean updateMode = false;
    static String entityid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.child_detail_activity);

        final ImageView childview = (ImageView)findViewById(R.id.childdetailprofileview);
        //header
//        TextView today = (TextView) findViewById(R.id.detail_today);
        
        //profile
        TextView nama = (TextView) findViewById(R.id.txt_child_name);
        TextView mother = (TextView) findViewById(R.id.txt_mother_name);
        TextView father = (TextView) findViewById(R.id.txt_father_number);
        TextView dob = (TextView) findViewById(R.id.txt_dob);
        
      //  TextView phone = (TextView) findViewById(R.id.txt_contact_phone_number);
//        TextView risk1 = (TextView) findViewById(R.id.txt_risk1);
//        TextView risk2 = (TextView) findViewById(R.id.txt_risk2);
//        TextView risk3 = (TextView) findViewById(R.id.txt_risk3);
//        TextView risk4 = (TextView) findViewById(R.id.txt_risk4);
        
        //detail data
        TextView txt_noBayi = (TextView) findViewById(R.id.txt_noBayi);
        TextView txt_jenisKelamin = (TextView) findViewById(R.id.txt_jenisKelamin);
        TextView txt_beratLahir = (TextView) findViewById(R.id.txt_beratLahir);
        TextView tinggi = (TextView) findViewById(R.id.txt_hasilPengukuranTinggiBayihasilPengukuranTinggiBayi);
        TextView berat = (TextView) findViewById(R.id.txt_indikatorBeratBedanBayi);
        TextView asi = (TextView) findViewById(R.id.txt_pemberianAsiEksklusif);
        TextView status_gizi = (TextView) findViewById(R.id.txt_statusGizi);
        TextView kpsp = (TextView) findViewById(R.id.txt_hasilDilakukannyaKPSP);
        TextView vita = (TextView) findViewById(R.id.txt_pelayananVita);
        TextView hb0 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiHb07);
        TextView pol1 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiBCGdanPolio1);
        TextView pol2 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB1Polio2);
        TextView pol3 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB2Polio3);
        TextView pol4 = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiDPTHB3Polio4);
        TextView campak = (TextView) findViewById(R.id.txt_tanggalpemberianimunisasiCampak);

        TextView growthChartButton = (TextView)findViewById(R.id.chart_label);
        ImageButton back = (ImageButton) findViewById(org.smartregister.R.id.btn_back_to_home);

        growthChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                ChildDetailActivity.childclient = childclient;
                startActivity(new Intent(AnakDetailActivity.this, ChildDetailActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AnakDetailActivity.this, NativeKIAnakSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });


        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

        String gender = childclient.getDetails().containsKey("gender") ? childclient.getDetails().get("gender"):"laki";


        //start profile image

        int placeholderDrawable= gender.equalsIgnoreCase("male") ? R.drawable.child_boy_infant:R.drawable.child_girl_infant;

        childview.setTag(R.id.entity_id, childclient.getCaseId());//required when saving file to disk
        if(childclient.getCaseId()!=null){//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(childclient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(childview, placeholderDrawable, placeholderDrawable));

            KIDetailActivity.setImagetoHolderFromUri(this,
                    DrishtiApplication.getAppDir() + File.separator + childclient.getDetails().get("base_entity_id") + ".JPEG",
                    childview, childclient.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        }

        //end profile image

        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");

        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

//        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
//        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));
        
        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));


        nama.setText(String.format("%s%s", getResources().getString(R.string.name), humanize(childclient.getColumnmaps().get("namaBayi") != null ? childclient.getColumnmaps().get("namaBayi") : "-")));
        mother.setText(String.format("%s%s", getResources().getString(R.string.child_details_mothers_name_label), humanize(kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "-")));
        father.setText(String.format("%s%s", getResources().getString(R.string.child_details_fathers_name_label), humanize(kiparent.getColumnmaps().get("namaSuami") != null ? kiparent.getColumnmaps().get("namaSuami") : "-")));
        dob.setText(String.format("%s%s", getResources().getString(R.string.date_of_birth), humanize(childclient.getColumnmaps().get("tanggalLahirAnak") != null ? childclient.getColumnmaps().get("tanggalLahirAnak") : "-")));

        txt_noBayi.setText(String.format("%s: ", humanize(childclient.getDetails().get("noBayi") != null ? childclient.getDetails().get("noBayi") : "-")));
        txt_jenisKelamin.setText(String.format(": %s", humanize(childclient.getDetails().get("gender") != null ? childclient.getDetails().get("gender") : "-")));
        txt_beratLahir.setText(String.format(": %s", humanize(childclient.getDetails().get("beratLahir") != null ? childclient.getDetails().get("beratLahir") : "-")));
        tinggi.setText(String.format(": %s", humanize(childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") != null ? childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") : "-")));
        berat.setText(String.format(": %s", humanize(childclient.getDetails().get("indikatorBeratBedanBayi") != null ? childclient.getDetails().get("indikatorBeratBedanBayi") : "-")));
        asi.setText(String.format(": %s", humanize(childclient.getDetails().get("pemberianAsiEksklusif") != null ? childclient.getDetails().get("pemberianAsiEksklusif") : "-")));
        status_gizi.setText(String.format(": %s", humanize(childclient.getDetails().get("statusGizi") != null ? childclient.getDetails().get("statusGizi") : "-")));
        kpsp.setText(String.format(": %s", humanize(childclient.getDetails().get("hasilDilakukannyaKPSP") != null ? childclient.getDetails().get("hasilDilakukannyaKPSP") : "-")));
        hb0.setText(String.format(": %s", humanize(childclient.getDetails().get("hb0") != null ? childclient.getDetails().get("hb0") : "-")));
        pol1.setText(String.format(": %s", humanize(childclient.getDetails().get("polio1") != null ? childclient.getDetails().get("polio1") : childclient.getDetails().get("bcg") != null ? childclient.getDetails().get("bcg") : "-")));
        pol2.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb1") != null ? childclient.getDetails().get("dptHb1") : childclient.getDetails().get("polio2") != null ? childclient.getDetails().get("polio2") : "-")));
        pol3.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb2") != null ? childclient.getDetails().get("dptHb2") : childclient.getDetails().get("polio3") != null ? childclient.getDetails().get("polio3") : "-")));
        pol4.setText(String.format(": %s", humanize(childclient.getDetails().get("dptHb3") != null ? childclient.getDetails().get("dptHb3") : childclient.getDetails().get("polio4") != null ? childclient.getDetails().get("polio4") : "-")));
        campak.setText(String.format(": %s", humanize(childclient.getDetails().get("campak") != null ? childclient.getDetails().get("campak") : "-")));
        vita.setText(String.format(": %s", humanize(childclient.getDetails().get("pelayananVita") != null ? childclient.getDetails().get("pelayananVita") : "-")));

        hash = Tools.retrieveHash(context.applicationContext());

        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FlurryFacade.logEvent("taking_child_pictures_on_anak_detail_view");
                entityid = childclient.entityId();
                if (hash.containsValue(entityid)) {
                    updateMode = true;
                }
                Intent takePictureIntent = new Intent(AnakDetailActivity.this, SmartShutterActivity.class);
                takePictureIntent.putExtra("org.sid.sidface.SmartShutterActivity.updated", updateMode);
                takePictureIntent.putExtra("IdentifyPerson", false);
                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.origin", TAG); // send Class Name
                startActivityForResult(takePictureIntent, 2);

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIAnakSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        refresh
        Log.e(TAG, "onActivityResult: refresh" );
        finish();
        startActivity(getIntent());

    }
}

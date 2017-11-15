package org.ei.opensrp.indonesia.child;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.face.camera.util.FaceConstants;
import org.ei.opensrp.indonesia.kartu_ibu.NativeKISmartRegisterActivity;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.repository.ImageRepository;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.util.StringUtil.humanize;
import static org.ei.opensrp.util.StringUtil.humanizeAndDoUPPERCASE;

/**
 * Created by Iq on 07/09/16.
 */
public class AnakDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    public static CommonPersonObjectClient childclient;
    static String bindobject;
    static String entityid;
    private String photo_path;
    private File tb_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.child_detail_activity);

        final ImageView childview = (ImageView) findViewById(R.id.childdetailprofileview);
        //header
        TextView today = (TextView) findViewById(R.id.detail_today);

        //profile
        TextView nama = (TextView) findViewById(R.id.txt_child_name);
        TextView mother = (TextView) findViewById(R.id.txt_mother_name);
        TextView father = (TextView) findViewById(R.id.txt_father_number);
        TextView dob = (TextView) findViewById(R.id.txt_dob);

        //  TextView phone = (TextView) findViewById(R.id.txt_contact_phone_number);
        TextView risk1 = (TextView) findViewById(R.id.txt_risk1);
        TextView risk2 = (TextView) findViewById(R.id.txt_risk2);
        TextView risk3 = (TextView) findViewById(R.id.txt_risk3);
        TextView risk4 = (TextView) findViewById(R.id.txt_risk4);

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

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(AnakDetailActivity.this, NativeKIAnakSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(childclient);

//        Profile Picture
//        Log.e(TAG, "onCreate: "+childclient.getDetails().get("profilepic_thumb") );
        photo_path = childclient.getDetails().get("profilepic_thumb");
        tb_photo = new File(photo_path);
        final int THUMBSIZE = FaceConstants.THUMBSIZE;

        if (photo_path != null) {
            if (!tb_photo.exists()) {
                Log.e(TAG, "onCreate: here " );
                childview.setImageDrawable(getResources().getDrawable(R.drawable.not_found_404));
            } else {
                Log.e(TAG, "onCreate: here exist " );
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(photo_path),
                        THUMBSIZE, THUMBSIZE);
                childview.setImageBitmap(ThumbImage);
            }

        } else {

            if (childclient.getDetails().get("gender") != null && childclient.getDetails().get("gender").equals("laki")) {
                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
            } else if (childclient.getDetails().get("gender") != null && childclient.getDetails().get("gender").equals("male")) {
                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
            } else {
                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_girl_infant));
            }
        }

//        if(childclient.getDetails().get("profilepic")!= null){
//                setImagetoHolderFromUri(AnakDetailActivity.this, childclient.getDetails().get("profilepic"), childview, R.drawable.child_boy_infant);
//        }
//        else {
//            if(childclient.getDetails().get("gender") != null && childclient.getDetails().get("gender").equals("laki")) {
//                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
//            }   else if(childclient.getDetails().get("gender") != null && childclient.getDetails().get("gender").equals("male")) {
//                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_boy_infant));
//            }
//            else {
//                childview.setImageDrawable(getResources().getDrawable(R.drawable.child_girl_infant));
//            }

//    }

        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");

        CommonPersonObject childobject = childRepository.findByCaseID(childclient.entityId());

        AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        nama.setText(getResources().getString(R.string.name) + humanize(childclient.getColumnmaps().get("namaBayi") != null ? childclient.getColumnmaps().get("namaBayi") : "-"));
        mother.setText(getResources().getString(R.string.child_details_mothers_name_label) + humanize(kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "-"));
        father.setText(getResources().getString(R.string.child_details_fathers_name_label) + humanize(kiparent.getColumnmaps().get("namaSuami") != null ? kiparent.getColumnmaps().get("namaSuami") : "-"));
        dob.setText(getResources().getString(R.string.date_of_birth) + humanize(childclient.getColumnmaps().get("tanggalLahirAnak") != null ? childclient.getColumnmaps().get("tanggalLahirAnak") : "-"));

        txt_noBayi.setText(": " + humanize(childclient.getDetails().get("noBayi") != null ? childclient.getDetails().get("noBayi") : "-"));
        txt_jenisKelamin.setText(": " + humanize(childclient.getDetails().get("jenisKelamin") != null ? childclient.getDetails().get("jenisKelamin") : "-"));
        txt_beratLahir.setText(": " + humanize(childclient.getDetails().get("beratLahir") != null ? childclient.getDetails().get("beratLahir") : "-"));
        tinggi.setText(": " + humanize(childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") != null ? childclient.getDetails().get("hasilPengukuranTinggiBayihasilPengukuranTinggiBayi") : "-"));
        berat.setText(": " + humanize(childclient.getDetails().get("indikatorBeratBedanBayi") != null ? childclient.getDetails().get("indikatorBeratBedanBayi") : "-"));
        asi.setText(": " + humanize(childclient.getDetails().get("pemberianAsiEksklusif") != null ? childclient.getDetails().get("pemberianAsiEksklusif") : "-"));
        status_gizi.setText(": " + humanize(childclient.getDetails().get("statusGizi") != null ? childclient.getDetails().get("statusGizi") : "-"));
        kpsp.setText(": " + humanize(childclient.getDetails().get("hasilDilakukannyaKPSP") != null ? childclient.getDetails().get("hasilDilakukannyaKPSP") : "-"));
        hb0.setText(": " + humanize(childclient.getDetails().get("hb0") != null ? childclient.getDetails().get("hb0") : "-"));
        pol1.setText(": " + humanize(childclient.getDetails().get("polio1") != null ? childclient.getDetails().get("polio1") : childclient.getDetails().get("bcg") != null ? childclient.getDetails().get("bcg") : "-"));
        pol2.setText(": " + humanize(childclient.getDetails().get("dptHb1") != null ? childclient.getDetails().get("dptHb1") : childclient.getDetails().get("polio2") != null ? childclient.getDetails().get("polio2") : "-"));
        pol3.setText(": " + humanize(childclient.getDetails().get("dptHb2") != null ? childclient.getDetails().get("dptHb2") : childclient.getDetails().get("polio3") != null ? childclient.getDetails().get("polio3") : "-"));
        pol4.setText(": " + humanize(childclient.getDetails().get("dptHb3") != null ? childclient.getDetails().get("dptHb3") : childclient.getDetails().get("polio4") != null ? childclient.getDetails().get("polio4") : "-"));
        campak.setText(": " + humanize(childclient.getDetails().get("campak") != null ? childclient.getDetails().get("campak") : "-"));
        vita.setText(": " + humanize(childclient.getDetails().get("pelayananVita") != null ? childclient.getDetails().get("pelayananVita") : "-"));


        childview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindobject = "anak";
                entityid = childclient.entityId();
                Intent intent = new Intent(AnakDetailActivity.this, SmartShutterActivity.class);
                intent.putExtra("IdentifyPerson", false);
                intent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIAnakSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

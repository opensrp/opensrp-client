package org.ei.opensrp.indonesia.kb;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import org.ei.opensrp.indonesia.lib.FlurryFacade;
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

/**
 * Created by Iq on 07/09/16.
 */
public class KBDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    //image retrieving

    public static CommonPersonObjectClient kiclient;

    private String photo_path;
    private File tb_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.kb_detail_activity);

        final ImageView kiview = (ImageView)findViewById(R.id.motherdetailprofileview);
        //header
        TextView today = (TextView) findViewById(R.id.detail_today);
        
        //profile
        TextView nama = (TextView) findViewById(R.id.txt_wife_name);
        TextView nik = (TextView) findViewById(R.id.txt_nik);
        TextView husband_name = (TextView) findViewById(R.id.txt_husband_name);
        TextView dob = (TextView) findViewById(R.id.txt_dob);
        TextView phone = (TextView) findViewById(R.id.txt_contact_phone_number);
        TextView risk1 = (TextView) findViewById(R.id.txt_risk1);
        TextView risk2 = (TextView) findViewById(R.id.txt_risk2);
        TextView risk3 = (TextView) findViewById(R.id.txt_risk3);
        TextView risk4 = (TextView) findViewById(R.id.txt_risk4);

        final TextView show_risk = (TextView) findViewById(R.id.show_more);
        final TextView show_detail = (TextView) findViewById(R.id.show_more_detail);

        //detail data
        TextView village = (TextView) findViewById(R.id.txt_village_name);
        TextView subvillage = (TextView) findViewById(R.id.txt_subvillage);
        TextView age = (TextView) findViewById(R.id.txt_age);
        TextView alamat = (TextView) findViewById(R.id.txt_alamat);
        TextView education = (TextView) findViewById(R.id.txt_edu);
        TextView religion = (TextView) findViewById(R.id.txt_agama);
        TextView job = (TextView) findViewById(R.id.txt_job);
        TextView gakin = (TextView) findViewById(R.id.txt_gakin);
        TextView blood_type = (TextView) findViewById(R.id.txt_blood);
        TextView asuransi = (TextView) findViewById(R.id.txt_asuransi);

        TextView jenisKontrasepsi = (TextView) findViewById(R.id.txt_jenisKontrasepsi);
        TextView td_diastolik = (TextView) findViewById(R.id.txt_td_diastolik);
        TextView tdSistolik = (TextView) findViewById(R.id.txt_tdSistolik);
        TextView alkilila  = (TextView) findViewById(R.id.txt_alkilila);
        TextView alkiPenyakitIms = (TextView) findViewById(R.id.txt_alkiPenyakitIms);
        TextView keteranganTentangPesertaKB   = (TextView) findViewById(R.id.txt_keteranganTentangPesertaKB);
        TextView keteranganTentangPesertaKB2 = (TextView) findViewById(R.id.txt_keteranganTentangPesertaKB2);
        TextView alkiPenyakitKronis = (TextView) findViewById(R.id.txt_alkiPenyakitKronis);
        TextView alkihb = (TextView) findViewById(R.id.txt_alkihb);
        TextView keteranganGantiCara = (TextView) findViewById(R.id.txt_keteranganGantiCara);

        //detail RISK
        TextView highRiskSTIBBVs = (TextView) findViewById(R.id.txt_highRiskSTIBBVs);
        TextView highRiskEctopicPregnancy = (TextView) findViewById(R.id.txt_highRiskEctopicPregnancy);
        TextView highRiskCardiovascularDiseaseRecord = (TextView) findViewById(R.id.txt_highRiskCardiovascularDiseaseRecord);
        TextView highRiskDidneyDisorder = (TextView) findViewById(R.id.txt_highRiskDidneyDisorder);
        TextView highRiskHeartDisorder = (TextView) findViewById(R.id.txt_highRiskHeartDisorder);
        TextView highRiskAsthma = (TextView) findViewById(R.id.txt_highRiskAsthma);
        TextView highRiskTuberculosis = (TextView) findViewById(R.id.txt_highRiskTuberculosis);
        TextView highRiskMalaria = (TextView) findViewById(R.id.txt_highRiskMalaria);
        TextView highRiskPregnancyPIH = (TextView) findViewById(R.id.txt_highRiskPregnancyPIH);
        TextView highRiskPregnancyProteinEnergyMalnutrition = (TextView) findViewById(R.id.txt_highRiskPregnancyProteinEnergyMalnutrition);

        TextView txt_highRiskLabourTBRisk = (TextView) findViewById(R.id.txt_highRiskLabourTBRisk);
        TextView txt_HighRiskLabourSectionCesareaRecord = (TextView) findViewById(R.id.txt_HighRiskLabourSectionCesareaRecord);
        TextView txt_highRisklabourFetusNumber = (TextView) findViewById(R.id.txt_highRisklabourFetusNumber);
        TextView txt_highRiskLabourFetusSize = (TextView) findViewById(R.id.txt_highRiskLabourFetusSize);
        TextView txt_lbl_highRiskLabourFetusMalpresentation = (TextView) findViewById(R.id.txt_lbl_highRiskLabourFetusMalpresentation);
        TextView txt_highRiskPregnancyAnemia = (TextView) findViewById(R.id.txt_highRiskPregnancyAnemia);
        TextView txt_highRiskPregnancyDiabetes = (TextView) findViewById(R.id.txt_highRiskPregnancyDiabetes);
        TextView HighRiskPregnancyTooManyChildren = (TextView) findViewById(R.id.txt_HighRiskPregnancyTooManyChildren);
        TextView highRiskPostPartumSectioCaesaria = (TextView) findViewById(R.id.txt_highRiskPostPartumSectioCaesaria);
        TextView highRiskPostPartumForceps = (TextView) findViewById(R.id.txt_highRiskPostPartumForceps);
        TextView highRiskPostPartumVacum = (TextView) findViewById(R.id.txt_highRiskPostPartumVacum);
        TextView highRiskPostPartumPreEclampsiaEclampsia = (TextView) findViewById(R.id.txt_highRiskPostPartumPreEclampsiaEclampsia);
        TextView highRiskPostPartumMaternalSepsis = (TextView) findViewById(R.id.txt_highRiskPostPartumMaternalSepsis);
        TextView highRiskPostPartumInfection = (TextView) findViewById(R.id.txt_highRiskPostPartumInfection);
        TextView highRiskPostPartumHemorrhage = (TextView) findViewById(R.id.txt_highRiskPostPartumHemorrhage);

        TextView highRiskPostPartumPIH = (TextView) findViewById(R.id.txt_highRiskPostPartumPIH);
        TextView highRiskPostPartumDistosia = (TextView) findViewById(R.id.txt_highRiskPostPartumDistosia);
        TextView txt_highRiskHIVAIDS = (TextView) findViewById(R.id.txt_highRiskHIVAIDS);


        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(KBDetailActivity.this, NativeKBSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(kiclient);

//        Profile Picture
        photo_path = kiclient.getDetails().get("profilepic_thumb");
        final int THUMBSIZE = FaceConstants.THUMBSIZE;

        if (photo_path != null) {
            tb_photo = new File(photo_path);
            if (!tb_photo.exists()) {
//                Log.e(TAG, "onCreate: here ");
                kiview.setImageDrawable(getResources().getDrawable(R.drawable.not_found_404));
            } else {
//                Log.e(TAG, "onCreate: here exist " );
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(photo_path),
                        THUMBSIZE, THUMBSIZE);
                kiview.setImageBitmap(ThumbImage);
            }
        } else {
            kiview.setImageDrawable(getResources().getDrawable(R.mipmap.woman_placeholder));
        }

        nama.setText(getResources().getString(R.string.name)+ (kiclient.getColumnmaps().get("namalengkap") != null ? kiclient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(getResources().getString(R.string.nik)+ (kiclient.getDetails().get("nik") != null ? kiclient.getDetails().get("nik") : "-"));
        husband_name.setText(getResources().getString(R.string.husband_name)+ (kiclient.getColumnmaps().get("namaSuami") != null ? kiclient.getColumnmaps().get("namaSuami") : "-"));
        dob.setText(getResources().getString(R.string.dob)+ (kiclient.getDetails().get("tanggalLahir") != null ? kiclient.getDetails().get("tanggalLahir") : "-"));
        phone.setText("No HP: "+ (kiclient.getDetails().get("NomorTelponHp") != null ? kiclient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if(kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null ){
            risk1.setText(getResources().getString(R.string.highRiskPregnancyYoungMaternalAge)+humanize(kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge")));
        }
        if(kiclient.getDetails().get("highRiskPregnancyOldMaternalAge") != null ){
            risk1.setText(getResources().getString(R.string.highRiskPregnancyOldMaternalAge)+humanize(kiclient.getDetails().get("highRiskPregnancyYoungMaternalAge")));
        }
        if(kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || kiclient.getDetails().get("HighRiskPregnancyAbortus") != null
                || kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord" ) != null
                ){
            risk2.setText(getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition)+humanize(kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition")));
            risk3.setText(getResources().getString(R.string.HighRiskPregnancyAbortus)+humanize(kiclient.getDetails().get("HighRiskPregnancyAbortus")));
            risk4.setText(getResources().getString(R.string.HighRiskLabourSectionCesareaRecord)+humanize(kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord")));

        }


        show_risk.setText(getResources().getString(R.string.show_more_button));
        show_detail.setText(getResources().getString(R.string.show_less_button));

        village.setText( ": "+humanize(kiclient.getDetails().get("cityVillage") != null ? kiclient.getDetails().get("cityVillage") : "-"));
        subvillage.setText( ": "+humanize(kiclient.getDetails().get("address1") != null ? kiclient.getDetails().get("address1") : "-"));
        age.setText(": "+humanize(kiclient.getColumnmaps().get("umur") != null ? kiclient.getColumnmaps().get("umur") : "-"));
        alamat.setText(": "+humanize(kiclient.getDetails().get("address3") != null ? kiclient.getDetails().get("address3") : "-"));
        education.setText(": "+humanize(kiclient.getDetails().get("pendidikan") != null ? kiclient.getDetails().get("pendidikan") : "-"));
        religion.setText(": "+humanize(kiclient.getDetails().get("agama") != null ? kiclient.getDetails().get("agama") : "-"));
        job.setText(": "+humanize(kiclient.getDetails().get("pekerjaan") != null ? kiclient.getDetails().get("pekerjaan") : "-"));
        gakin.setText(": "+humanize(kiclient.getDetails().get("gakinTidak") != null ? kiclient.getDetails().get("gakinTidak") : "-"));
        blood_type.setText(": "+humanize(kiclient.getDetails().get("golonganDarah") != null ? kiclient.getDetails().get("golonganDarah") : "-"));
        asuransi.setText(": "+humanize(kiclient.getDetails().get("jamkesmas") != null ? kiclient.getDetails().get("jamkesmas") : "-"));


        jenisKontrasepsi.setText(": "+ humanize(kiclient.getDetails().get("jenisKontrasepsi") != null ? kiclient.getDetails().get("jenisKontrasepsi") : "-"));
        alkihb.setText(": "+ humanize(kiclient.getDetails().get("alkihb") != null ? kiclient.getDetails().get("alkihb") : "-"));
        tdSistolik.setText(": "+ humanize(kiclient.getDetails().get("tdDiastolik") != null ? kiclient.getDetails().get("tdDiastolik") : "-"));
        td_diastolik.setText(": "+ humanize(kiclient.getDetails().get("tdDiastolik") != null ? kiclient.getDetails().get("tdDiastolik") : "-"));
        alkilila.setText(": "+humanize(kiclient.getDetails().get("alkilila") != null ? kiclient.getDetails().get("alkilila") : "-"));
        alkiPenyakitIms.setText(": "+humanize(kiclient.getDetails().get("alkiPenyakitIms") != null ? kiclient.getDetails().get("alkiPenyakitIms") : "-"));
        keteranganTentangPesertaKB.setText(": "+humanize(kiclient.getDetails().get("keteranganTentangPesertaKB") != null ? kiclient.getDetails().get("keteranganTentangPesertaKB") : "-"));
        keteranganTentangPesertaKB2.setText(": "+humanize(kiclient.getDetails().get("keterangantentangPesertaKB2") != null ? kiclient.getDetails().get("keterangantentangPesertaKB2") : "-"));
        alkiPenyakitKronis.setText(": "+humanize(kiclient.getDetails().get("alkiPenyakitKronis") != null ? kiclient.getDetails().get("alkiPenyakitKronis") : "-"));
        keteranganGantiCara.setText(": "+humanize(kiclient.getDetails().get("keteranganGantiCara") != null ? kiclient.getDetails().get("keteranganGantiCara") : "-"));


//risk detail
        highRiskSTIBBVs.setText(humanize(kiclient.getDetails().get("highRiskSTIBBVs") != null ? kiclient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize (kiclient.getDetails().get("highRiskEctopicPregnancy") != null ? kiclient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(kiclient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? kiclient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(kiclient.getDetails().get("highRiskDidneyDisorder") != null ? kiclient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(kiclient.getDetails().get("highRiskHeartDisorder") != null ? kiclient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(kiclient.getDetails().get("highRiskAsthma") != null ? kiclient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(kiclient.getDetails().get("highRiskTuberculosis") != null ? kiclient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(kiclient.getDetails().get("highRiskMalaria") != null ? kiclient.getDetails().get("highRiskMalaria") : "-"));

        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? kiclient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(kiclient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? kiclient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));

        txt_highRiskHIVAIDS.setText(humanize(kiclient.getDetails().get("highRiskHIVAIDS") != null ? kiclient.getDetails().get("highRiskHIVAIDS") : "-"));

        AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ibu");
        if(kiclient.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(kiclient.getColumnmaps().get("ibu.id"));

            txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") != null ? ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
            txt_highRisklabourFetusNumber.setText(humanize(ibuparent.getDetails().get("highRisklabourFetusNumber") != null ? ibuparent.getDetails().get("highRisklabourFetusNumber") : "-"));
            txt_highRiskLabourFetusSize.setText(humanize(ibuparent.getDetails().get("highRiskLabourFetusSize") != null ? ibuparent.getDetails().get("highRiskLabourFetusSize") : "-"));
            txt_highRiskLabourTBRisk.setText(humanize(ibuparent.getDetails().get("highRiskLabourTBRisk") != null ? ibuparent.getDetails().get("highRiskLabourTBRisk") : "-"));
            highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
            highRiskPregnancyPIH.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyPIH") != null ? ibuparent.getDetails().get("highRiskPregnancyPIH") : "-"));
            txt_highRiskPregnancyDiabetes.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyDiabetes") != null ? ibuparent.getDetails().get("highRiskPregnancyDiabetes") : "-"));
            txt_highRiskPregnancyAnemia.setText(humanize(ibuparent.getDetails().get("highRiskPregnancyAnemia") != null ? ibuparent.getDetails().get("highRiskPregnancyAnemia") : "-"));

            highRiskPostPartumSectioCaesaria.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? ibuparent.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
            highRiskPostPartumForceps.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumForceps") != null ? ibuparent.getDetails().get("highRiskPostPartumForceps") : "-"));
            highRiskPostPartumVacum.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumVacum") != null ? ibuparent.getDetails().get("highRiskPostPartumVacum") : "-"));
            highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? ibuparent.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
            highRiskPostPartumMaternalSepsis.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? ibuparent.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
            highRiskPostPartumInfection.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumInfection") != null ? ibuparent.getDetails().get("highRiskPostPartumInfection") : "-"));
            highRiskPostPartumHemorrhage.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumHemorrhage") != null ? ibuparent.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
            highRiskPostPartumPIH.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumPIH") != null ? ibuparent.getDetails().get("highRiskPostPartumPIH") : "-"));
            highRiskPostPartumDistosia.setText(humanize(ibuparent.getDetails().get("highRiskPostPartumDistosia") != null ? ibuparent.getDetails().get("highRiskPostPartumDistosia") : "-"));


        }
        show_risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.VISIBLE);
                findViewById(R.id.show_more_detail).setVisibility(View.VISIBLE);
                findViewById(R.id.show_more).setVisibility(View.GONE);
            }
        });

        show_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.VISIBLE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.show_more).setVisibility(View.VISIBLE);
                findViewById(R.id.show_more_detail).setVisibility(View.GONE);
            }
        });

        kiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("taking_mother_pictures_on_kohort_kb_detail_view");
//                bindobject = "kartu_ibu";
//                entityid = kiclient.entityId();
//                entityid = kiclient.getDetails().get("nik");
                entityid = kiclient.entityId();
//                dispatchTakePictureIntent(kiview);
//                Intent intent = new Intent(KIDetailActivity.this, LiveRecognition.class);
//                Intent intent = new Intent(KIDetailActivity.this, FacialActivity.class);
                Intent intent = new Intent(KBDetailActivity.this, SmartShutterActivity.class);
//                Intent intent = new Intent(KIDetailActivity.this, PhotoLive.class);
//                Intent intent = new Intent(KIDetailActivity.this, LiveRecognitionActivity.class);
                intent.putExtra("IdentifyPerson", false);
                intent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                startActivity(intent);
            }
        });
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
            HashMap<String,String> details = new HashMap<String,String>();
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
        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),anmId,entityid,"Image",details.get("profilepic"), ImageRepository.TYPE_Unsynced,"dp");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
//                kiclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public static void setImagetoHolder(Activity activity, String file, ImageView view, int placeholder){
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
    public static void setImagetoHolderFromUri(Activity activity,String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        Uri external = Uri.fromFile(externalFile);
        view.setImageURI(external);


    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKBSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

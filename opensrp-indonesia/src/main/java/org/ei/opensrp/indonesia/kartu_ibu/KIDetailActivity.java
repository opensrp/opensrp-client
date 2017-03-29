package org.ei.opensrp.indonesia.kartu_ibu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.application.BidanApplication;
import org.ei.opensrp.indonesia.face.camera.SmartShutterActivity;
import org.ei.opensrp.indonesia.face.camera.util.Tools;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
public class KIDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = KIDetailActivity.class.getSimpleName();
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");
    //image retrieving

    public static CommonPersonObjectClient kiclient;

    private static HashMap<String, String> hash;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.ki_detail_activity);


        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<String, String>();
        Detail.put("start", DetailStart);
        FlurryAgent.logEvent("KI_detail_view",Detail, true );

        final ImageView kiview = (ImageView)findViewById(R.id.motherdetailprofileview);
        //header
        //  TextView risk = (TextView) findViewById(R.id.detail_risk);

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
                startActivity(new Intent(KIDetailActivity.this, NativeKISmartRegisterActivity.class));
                overridePendingTransition(0, 0);

                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<String, String>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("KI_detail_view", Detail, true);
            }
        });

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(kiclient);

        //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
        Log.e(TAG, "onCreate: " );
        BidanApplication.getInstance().getCachedImageLoaderInstance().getImageByClientId(kiclient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));


        nama.setText(getResources().getString(R.string.name)+ (kiclient.getColumnmaps().get("namalengkap") != null ? kiclient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(getResources().getString(R.string.nik)+ (kiclient.getDetails().get("nik") != null ? kiclient.getDetails().get("nik") : "-"));
        husband_name.setText(getResources().getString(R.string.husband_name)+ (kiclient.getColumnmaps().get("namaSuami") != null ? kiclient.getColumnmaps().get("namaSuami") : "-"));
        String tgl = kiclient.getDetails().get("tanggalLahir") != null ? kiclient.getDetails().get("tanggalLahir") : "-";
        String tgl_lahir = tgl.substring(0, tgl.indexOf("T"));
        dob.setText(getResources().getString(R.string.dob)+ tgl_lahir);
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

        //detail
        village.setText(": "+humanize(kiclient.getDetails().get("cityVillage") != null ? kiclient.getDetails().get("cityVillage") : "-"));
        subvillage.setText(": "+humanize (kiclient.getDetails().get("address1") != null ? kiclient.getDetails().get("address1") : "-"));
        age.setText(": "+humanize(kiclient.getColumnmaps().get("umur") != null ? kiclient.getColumnmaps().get("umur") : "-"));
        alamat.setText(": "+humanize(kiclient.getDetails().get("address3") != null ? kiclient.getDetails().get("address3") : "-"));
        education.setText(": "+humanize(kiclient.getDetails().get("pendidikan") != null ? kiclient.getDetails().get("pendidikan") : "-"));
        religion.setText(": "+humanize(kiclient.getDetails().get("agama") != null ? kiclient.getDetails().get("agama") : "-"));
        job.setText(": "+humanize(kiclient.getDetails().get("pekerjaan") != null ? kiclient.getDetails().get("pekerjaan") : "-"));
        gakin.setText(": "+humanize(kiclient.getDetails().get("gakinTidak") != null ? kiclient.getDetails().get("gakinTidak") : "-"));
        blood_type.setText(": "+humanize(kiclient.getDetails().get("golonganDarah") != null ? kiclient.getDetails().get("golonganDarah") : "-"));
        asuransi.setText(": "+humanize(kiclient.getDetails().get("asuransiJiwa") != null ? kiclient.getDetails().get("asuransiJiwa") : "-"));

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


        txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(kiclient.getDetails().get("highRiskLabourFetusMalpresentation") != null ? kiclient.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
        txt_highRisklabourFetusNumber.setText(humanize(kiclient.getDetails().get("highRisklabourFetusNumber") != null ? kiclient.getDetails().get("highRisklabourFetusNumber") : "-"));
        txt_highRiskLabourFetusSize.setText(humanize(kiclient.getDetails().get("highRiskLabourFetusSize") != null ? kiclient.getDetails().get("highRiskLabourFetusSize") : "-"));
        txt_highRiskLabourTBRisk.setText(humanize(kiclient.getDetails().get("highRiskLabourTBRisk") != null ? kiclient.getDetails().get("highRiskLabourTBRisk") : "-"));
        highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? kiclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
        highRiskPregnancyPIH.setText(humanize(kiclient.getDetails().get("highRiskPregnancyPIH") != null ? kiclient.getDetails().get("highRiskPregnancyPIH") : "-"));
        txt_highRiskPregnancyDiabetes.setText(humanize(kiclient.getDetails().get("highRiskPregnancyDiabetes") != null ? kiclient.getDetails().get("highRiskPregnancyDiabetes") : "-"));
        txt_highRiskPregnancyAnemia.setText(humanize(kiclient.getDetails().get("highRiskPregnancyAnemia") != null ? kiclient.getDetails().get("highRiskPregnancyAnemia") : "-"));
        highRiskPostPartumSectioCaesaria.setText(humanize(kiclient.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? kiclient.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
        highRiskPostPartumForceps.setText(humanize(kiclient.getDetails().get("highRiskPostPartumForceps") != null ? kiclient.getDetails().get("highRiskPostPartumForceps") : "-"));
        highRiskPostPartumVacum.setText(humanize(kiclient.getDetails().get("highRiskPostPartumVacum") != null ? kiclient.getDetails().get("highRiskPostPartumVacum") : "-"));
        highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(kiclient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? kiclient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
        highRiskPostPartumMaternalSepsis.setText(humanize(kiclient.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? kiclient.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
        highRiskPostPartumInfection.setText(humanize(kiclient.getDetails().get("highRiskPostPartumInfection") != null ? kiclient.getDetails().get("highRiskPostPartumInfection") : "-"));
        highRiskPostPartumHemorrhage.setText(humanize(kiclient.getDetails().get("highRiskPostPartumHemorrhage") != null ? kiclient.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
        highRiskPostPartumPIH.setText(humanize(kiclient.getDetails().get("highRiskPostPartumPIH") != null ? kiclient.getDetails().get("highRiskPostPartumPIH") : "-"));
        highRiskPostPartumDistosia.setText(humanize(kiclient.getDetails().get("highRiskPostPartumDistosia") != null ? kiclient.getDetails().get("highRiskPostPartumDistosia") : "-"));


        //  }

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

        hash = Tools.retrieveHash(context.applicationContext());

        kiview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("taking_mother_pictures_on_kohort_ibu_detail_view");
                bindobject = "kartu_ibu";
                entityid = kiclient.entityId();
//                Log.e(TAG, "onClick: "+hash.size() );
//                Log.e(TAG, "onClick: "+hash.containsValue(entityid) );
                if(hash.containsValue(entityid)){
                    Log.e(TAG, "onClick: "+entityid+" updated" );
                    mode = "updated";

                }
                dispatchTakePictureIntent(kiview, mode);

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKISmartRegisterActivity.class));
        overridePendingTransition(0, 0);

    }



    String mCurrentPhotoPath;


    static final int REQUEST_TAKE_PHOTO = 1;
    static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;

    private void dispatchTakePictureIntent(ImageView imageView, String mode) {
        Log.e(TAG, "dispatchTakePictureIntent: "+"klik" );
        mImageView = imageView;
        Intent takePictureIntent = new Intent(this, SmartShutterActivity.class);
        takePictureIntent.putExtra("org.sid.sidface.SmartShutterActivity.updated", true);


        //FIXME USE SmartShutterActivity INSTEAD
//     Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");

        Log.e(TAG, "dispatchTakePictureIntent: "+takePictureIntent.resolveActivity(getPackageManager()) );
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                currentfile = photoFile;
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//
            takePictureIntent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                startActivityForResult(takePictureIntent, 1);
//            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "onActivityResult: "+requestCode );
        Log.e(TAG, "onActivityResult: "+resultCode );
        Log.e(TAG, "onActivityResult: "+intent );
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();

//            HashMap<String,String> details = new HashMap<String,String>();
//            details.put("profilepic",currentfile.getAbsolutePath());
//            saveimagereference(bindobject,entityid,details);

//            Long tsLong = System.currentTimeMillis()/1000;
//            DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
//            detailsRepository.add(entityid, "profilepic", currentfile.getAbsolutePath(), tsLong);
//
            Bundle extras = intent.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
//            saveStaticImageToDisk(kiclient.getCaseId(),mImageBitmap);
            mImageView.setImageBitmap(mImageBitmap);
        }
    }
//    public static void saveStaticImageToDisk(String entityId, Bitmap image) {
//        String anmId = Context.getInstance().allSharedPreferences().fetchRegisteredANM();
//
//        if (image != null) {
//            OutputStream os = null;
//            try {
//
//                if (entityId != null && !entityId.isEmpty()) {
//                    final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId+".JPEG";
//
//                    File outputFile = new File(absoluteFileName);
//                    os = new FileOutputStream(outputFile);
//                    Bitmap.CompressFormat compressFormat =  Bitmap.CompressFormat.JPEG;
//                    if (compressFormat != null) {
//                        image.compress(compressFormat, 100, os);
//                    } else {
//                        throw new IllegalArgumentException("Failed to save static image, could not retrieve image compression format from name "
//                                + absoluteFileName);
//                    }
//                    // insert into the db
//                    ProfileImage profileImage= new ProfileImage();
//                    profileImage.setImageid(UUID.randomUUID().toString());
//                    profileImage.setAnmId(anmId);
//                    profileImage.setEntityID(entityId);
//                    profileImage.setFilepath(absoluteFileName);
//                    profileImage.setFilecategory("profilepic");
//                    profileImage.setFilevector("[ve, ct, or]");
//                    profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
//                    ImageRepository imageRepo = (ImageRepository) org.ei.opensrp.Context.imageRepository();
//                    imageRepo.add(profileImage);
//                }
//
//            } catch (FileNotFoundException e) {
//                Log.e(TAG, "Failed to save static image to disk");
//            } finally {
//                if (os != null) {
//                    try {
//                        os.close();
//                    } catch (IOException e) {
//                        Log.e(TAG, "Failed to close static images output stream after attempting to write image");
//                    }
//                }
//            }
//        }
//    }

    public static void setImagetoHolderFromUri(Activity activity,String file, ImageView view, int placeholder){
        view.setImageDrawable(activity.getResources().getDrawable(placeholder));
        File externalFile = new File(file);
        Uri external = Uri.fromFile(externalFile);
        view.setImageURI(external);


    }
}

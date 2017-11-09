package org.ei.opensrp.indonesia.anc;

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
import org.ei.opensrp.view.activity.NativeANCSmartRegisterActivity;

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
public class ANCDetailActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;
    private static int mImageThumbSize;
    private static int mImageThumbSpacing;
    private static String showbgm;
    private static ImageFetcher mImageFetcher;

    //image retrieving

    public static CommonPersonObjectClient ancclient;

    static String bindobject;
    static String entityid;

    private String photo_path;
    private File tb_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.anc_detail_activity);

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
        TextView Keterangan_k1k4 = (TextView) findViewById(R.id.txt_Keterangan_k1k4);
        TextView ancDate = (TextView) findViewById(R.id.txt_ancDate);
        TextView tanggalHPHT = (TextView) findViewById(R.id.txt_tanggalHPHT);
        TextView usiaKlinis = (TextView) findViewById(R.id.txt_usiaKlinis);
        TextView trimesterKe = (TextView) findViewById(R.id.txt_trimesterKe);
        TextView kunjunganKe = (TextView) findViewById(R.id.txt_kunjunganKe);
        TextView ancKe = (TextView) findViewById(R.id.txt_ancKe);
        TextView bbKg = (TextView) findViewById(R.id.txt_bbKg);
        TextView tandaVitalTDSistolik = (TextView) findViewById(R.id.txt_tandaVitalTDSistolik);
        TextView tandaVitalTDDiastolik = (TextView) findViewById(R.id.txt_tandaVitalTDDiastolik);
        TextView hasilPemeriksaanLILA = (TextView) findViewById(R.id.txt_hasilPemeriksaanLILA);
        TextView statusGiziibu = (TextView) findViewById(R.id.txt_statusGiziibu);
        TextView tfu = (TextView) findViewById(R.id.txt_tfu);
        TextView refleksPatelaIbu = (TextView) findViewById(R.id.txt_refleksPatelaIbu);
        TextView djj = (TextView) findViewById(R.id.txt_djj);
        TextView kepalaJaninTerhadapPAP = (TextView) findViewById(R.id.txt_kepalaJaninTerhadapPAP);
        TextView taksiranBeratJanin = (TextView) findViewById(R.id.txt_taksiranBeratJanin);
        TextView persentasiJanin = (TextView) findViewById(R.id.txt_persentasiJanin);
        TextView jumlahJanin = (TextView) findViewById(R.id.txt_jumlahJanin);

        TextView statusImunisasitt = (TextView) findViewById(R.id.txt_statusImunisasitt);
        TextView pelayananfe = (TextView) findViewById(R.id.txt_pelayananfe);
        TextView komplikasidalamKehamilan = (TextView) findViewById(R.id.txt_komplikasidalamKehamilan);

        TextView integrasiProgrampmtctvct = (TextView) findViewById(R.id.txt_integrasiProgrampmtctvct);
        TextView integrasiProgrampmtctPeriksaDarah = (TextView) findViewById(R.id.txt_integrasiProgrampmtctPeriksaDarah);
        TextView integrasiProgrampmtctSerologi = (TextView) findViewById(R.id.txt_integrasiProgrampmtctSerologi);
        TextView integrasiProgrampmtctarvProfilaksis = (TextView) findViewById(R.id.txt_integrasiProgrampmtctarvProfilaksis);
        TextView integrasiProgramMalariaPeriksaDarah = (TextView) findViewById(R.id.txt_integrasiProgramMalariaPeriksaDarah);
        TextView integrasiProgramMalariaObat = (TextView) findViewById(R.id.txt_integrasiProgramMalariaObat);
        TextView integrasiProgramMalariaKelambuBerinsektisida = (TextView) findViewById(R.id.txt_integrasiProgramMalariaKelambuBerinsektisida);
        TextView integrasiProgramtbDahak = (TextView) findViewById(R.id.txt_integrasiProgramtbDahak);
        TextView integrasiProgramtbObat = (TextView) findViewById(R.id.txt_integrasiProgramtbObat);

        TextView laboratoriumPeriksaHbHasil = (TextView) findViewById(R.id.txt_laboratoriumPeriksaHbHasil);
        TextView laboratoriumPeriksaHbAnemia = (TextView) findViewById(R.id.txt_laboratoriumPeriksaHbAnemia);
        TextView laboratoriumProteinUria = (TextView) findViewById(R.id.txt_laboratoriumProteinUria);
        TextView laboratoriumGulaDarah = (TextView) findViewById(R.id.txt_laboratoriumGulaDarah);
        TextView laboratoriumThalasemia = (TextView) findViewById(R.id.txt_laboratoriumThalasemia);
        TextView laboratoriumSifilis = (TextView) findViewById(R.id.txt_laboratoriumSifilis);
        TextView laboratoriumHbsAg = (TextView) findViewById(R.id.txt_laboratoriumHbsAg);

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
                startActivity(new Intent(ANCDetailActivity.this, NativeKIANCSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancclient);
        
        Keterangan_k1k4.setText(": "+ humanize (ancclient.getDetails().get("Keterangan_k1k4") != null ? ancclient.getDetails().get("Keterangan_k1k4") : "-"));
        tanggalHPHT.setText(": "+ humanize(ancclient.getDetails().get("tanggalHPHT") != null ? ancclient.getDetails().get("tanggalHPHT") : "-"));
        usiaKlinis.setText(": "+ humanize(ancclient.getDetails().get("usiaKlinis") != null ? ancclient.getDetails().get("usiaKlinis") : "-"));
        trimesterKe.setText(": "+ humanize(ancclient.getDetails().get("trimesterKe") != null ? ancclient.getDetails().get("trimesterKe") : "-"));
        kunjunganKe.setText(": "+ humanize(ancclient.getDetails().get("kunjunganKe") != null ? ancclient.getDetails().get("kunjunganKe") : "-"));
        bbKg.setText(": "+ humanize(ancclient.getDetails().get("bbKg") != null ? ancclient.getDetails().get("bbKg") : "-"));
        tandaVitalTDSistolik.setText(": "+ humanize(ancclient.getDetails().get("tandaVitalTDSistolik") != null ? ancclient.getDetails().get("tandaVitalTDSistolik") : "-"));
        tandaVitalTDDiastolik.setText(": "+ humanize(ancclient.getDetails().get("tandaVitalTDDiastolik") != null ? ancclient.getDetails().get("tandaVitalTDDiastolik") : "-"));
        hasilPemeriksaanLILA.setText(": "+ humanize(ancclient.getDetails().get("hasilPemeriksaanLILA") != null ? ancclient.getDetails().get("hasilPemeriksaanLILA") : "-"));
        statusGiziibu.setText(": "+ humanize(ancclient.getDetails().get("statusGiziibu") != null ? ancclient.getDetails().get("statusGiziibu") : "-"));
        tfu.setText(": "+humanize (ancclient.getDetails().get("tfu") != null ? ancclient.getDetails().get("tfu") : "-"));
        refleksPatelaIbu.setText(": "+ humanize(ancclient.getDetails().get("refleksPatelaIbu") != null ? ancclient.getDetails().get("refleksPatelaIbu") : "-"));
        djj.setText(": "+ humanize(ancclient.getDetails().get("djj") != null ? ancclient.getDetails().get("djj") : "-"));
        kepalaJaninTerhadapPAP.setText(": "+ humanize(ancclient.getDetails().get("kepalaJaninTerhadapPAP") != null ? ancclient.getDetails().get("kepalaJaninTerhadapPAP") : "-"));
        taksiranBeratJanin.setText(": "+ humanize(ancclient.getDetails().get("taksiranBeratJanin") != null ? ancclient.getDetails().get("taksiranBeratJanin") : "-"));
        persentasiJanin.setText(": "+humanize (ancclient.getDetails().get("persentasiJanin") != null ? ancclient.getDetails().get("persentasiJanin") : "-"));
        jumlahJanin.setText(": "+ humanize(ancclient.getDetails().get("jumlahJanin") != null ? ancclient.getDetails().get("jumlahJanin") : "-"));

        statusImunisasitt.setText(": "+ humanizeAndDoUPPERCASE (ancclient.getDetails().get("statusImunisasitt") != null ? ancclient.getDetails().get("statusImunisasitt") : "-"));
        pelayananfe.setText(": "+humanize (ancclient.getDetails().get("pelayananfe") != null ? ancclient.getDetails().get("pelayananfe") : "-"));
        komplikasidalamKehamilan.setText(": "+humanize (ancclient.getDetails().get("komplikasidalamKehamilan") != null ? ancclient.getDetails().get("komplikasidalamKehamilan") : "-"));
        integrasiProgrampmtctvct.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgrampmtctvct") != null ? ancclient.getDetails().get("integrasiProgrampmtctvct") : "-"));
        integrasiProgrampmtctPeriksaDarah.setText(": "+humanize (ancclient.getDetails().get("integrasiProgrampmtctPeriksaDarah") != null ? ancclient.getDetails().get("integrasiProgrampmtctPeriksaDarah") : "-"));
        integrasiProgrampmtctSerologi.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgrampmtctSerologi") != null ? ancclient.getDetails().get("integrasiProgrampmtctSerologi") : "-"));
        integrasiProgrampmtctarvProfilaksis.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgrampmtctarvProfilaksis") != null ? ancclient.getDetails().get("integrasiProgrampmtctarvProfilaksis") : "-"));
        integrasiProgramMalariaPeriksaDarah.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgramMalariaPeriksaDarah") != null ? ancclient.getDetails().get("integrasiProgramMalariaPeriksaDarah") : "-"));
        integrasiProgramMalariaObat.setText(": "+humanize (ancclient.getDetails().get("integrasiProgramMalariaObat") != null ? ancclient.getDetails().get("integrasiProgramMalariaObat") : "-"));
        integrasiProgramMalariaKelambuBerinsektisida.setText(": "+ (ancclient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") != null ? ancclient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") : "-"));
        integrasiProgramtbDahak.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgramtbDahak") != null ? ancclient.getDetails().get("integrasiProgramtbDahak") : "-"));
        integrasiProgramtbObat.setText(": "+ humanize(ancclient.getDetails().get("integrasiProgramtbObat") != null ? ancclient.getDetails().get("integrasiProgramtbObat") : "-"));
        laboratoriumPeriksaHbHasil.setText(": "+ humanize(ancclient.getDetails().get("laboratoriumPeriksaHbHasil") != null ? ancclient.getDetails().get("laboratoriumPeriksaHbHasil") : "-"));
        laboratoriumPeriksaHbAnemia.setText(": "+humanize (ancclient.getDetails().get("laboratoriumPeriksaHbAnemia") != null ? ancclient.getDetails().get("laboratoriumPeriksaHbAnemia") : "-"));
        laboratoriumProteinUria.setText(": "+humanize (ancclient.getDetails().get("laboratoriumProteinUria") != null ? ancclient.getDetails().get("laboratoriumProteinUria") : "-"));
        laboratoriumGulaDarah.setText(": "+humanize (ancclient.getDetails().get("laboratoriumGulaDarah") != null ? ancclient.getDetails().get("laboratoriumGulaDarah") : "-"));
        laboratoriumThalasemia.setText(": "+ humanize(ancclient.getDetails().get("laboratoriumThalasemia") != null ? ancclient.getDetails().get("laboratoriumThalasemia") : "-"));
        laboratoriumSifilis.setText(": "+ humanize(ancclient.getDetails().get("laboratoriumSifilis") != null ? ancclient.getDetails().get("laboratoriumSifilis") : "-"));
        laboratoriumHbsAg.setText(": "+humanize (ancclient.getDetails().get("laboratoriumHbsAg") != null ? ancclient.getDetails().get("laboratoriumHbsAg") : "-"));

        //risk detail
        txt_lbl_highRiskLabourFetusMalpresentation.setText(humanize(ancclient.getDetails().get("highRiskLabourFetusMalpresentation") != null ? ancclient.getDetails().get("highRiskLabourFetusMalpresentation") : "-"));
        txt_highRisklabourFetusNumber.setText(humanize(ancclient.getDetails().get("highRisklabourFetusNumber") != null ? ancclient.getDetails().get("highRisklabourFetusNumber") : "-"));
        txt_highRiskLabourFetusSize.setText(humanize(ancclient.getDetails().get("highRiskLabourFetusSize") != null ? ancclient.getDetails().get("highRiskLabourFetusSize") : "-"));
        highRiskPregnancyProteinEnergyMalnutrition.setText(humanize(ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null ? ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") : "-"));
        highRiskPregnancyPIH.setText(humanize(ancclient.getDetails().get("highRiskPregnancyPIH") != null ? ancclient.getDetails().get("highRiskPregnancyPIH") : "-"));
        txt_highRiskPregnancyDiabetes.setText(humanize(ancclient.getDetails().get("highRiskPregnancyDiabetes") != null ? ancclient.getDetails().get("highRiskPregnancyDiabetes") : "-"));
        txt_highRiskPregnancyAnemia.setText(humanize(ancclient.getDetails().get("highRiskPregnancyAnemia") != null ? ancclient.getDetails().get("highRiskPregnancyAnemia") : "-"));

        highRiskPostPartumSectioCaesaria.setText(humanize(ancclient.getDetails().get("highRiskPostPartumSectioCaesaria") != null ? ancclient.getDetails().get("highRiskPostPartumSectioCaesaria") : "-"));
        highRiskPostPartumForceps.setText(humanize(ancclient.getDetails().get("highRiskPostPartumForceps") != null ? ancclient.getDetails().get("highRiskPostPartumForceps") : "-"));
        highRiskPostPartumVacum.setText(humanize(ancclient.getDetails().get("highRiskPostPartumVacum") != null ? ancclient.getDetails().get("highRiskPostPartumVacum") : "-"));
        highRiskPostPartumPreEclampsiaEclampsia.setText(humanize(ancclient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") != null ? ancclient.getDetails().get("highRiskPostPartumPreEclampsiaEclampsia") : "-"));
        highRiskPostPartumMaternalSepsis.setText(humanize(ancclient.getDetails().get("highRiskPostPartumMaternalSepsis") != null ? ancclient.getDetails().get("highRiskPostPartumMaternalSepsis") : "-"));
        highRiskPostPartumInfection.setText(humanize(ancclient.getDetails().get("highRiskPostPartumInfection") != null ? ancclient.getDetails().get("highRiskPostPartumInfection") : "-"));
        highRiskPostPartumHemorrhage.setText(humanize(ancclient.getDetails().get("highRiskPostPartumHemorrhage") != null ? ancclient.getDetails().get("highRiskPostPartumHemorrhage") : "-"));
        highRiskPostPartumPIH.setText(humanize(ancclient.getDetails().get("highRiskPostPartumPIH") != null ? ancclient.getDetails().get("highRiskPostPartumPIH") : "-"));
        highRiskPostPartumDistosia.setText(humanize(ancclient.getDetails().get("highRiskPostPartumDistosia") != null ? ancclient.getDetails().get("highRiskPostPartumDistosia") : "-"));

        ancKe.setText(": "+(ancclient.getDetails().get("ancKe") != null ? ancclient.getDetails().get("ancKe") : "-"));

        ancDate.setText(": "+ (ancclient.getDetails().get("ancDate") != null ? ancclient.getDetails().get("ancDate") : "-"));


//        Profile Picture

        photo_path = ancclient.getDetails().get("profilepic_thumb");
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

        nama.setText(getResources().getString(R.string.name)+ (ancclient.getColumnmaps().get("namalengkap") != null ? ancclient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(getResources().getString(R.string.nik)+ (ancclient.getDetails().get("nik") != null ? ancclient.getDetails().get("nik") : "-"));
        husband_name.setText(getResources().getString(R.string.husband_name)+ (ancclient.getColumnmaps().get("namaSuami") != null ? ancclient.getColumnmaps().get("namaSuami") : "-"));
        dob.setText(getResources().getString(R.string.dob)+ (ancclient.getDetails().get("tanggalLahir") != null ? ancclient.getDetails().get("tanggalLahir") : "-"));
        phone.setText("No HP: "+ (ancclient.getDetails().get("NomorTelponHp") != null ? ancclient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null ){
            risk1.setText(getResources().getString(R.string.highRiskPregnancyYoungMaternalAge)+humanize(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge")));
        }
        if(ancclient.getDetails().get("highRiskPregnancyOldMaternalAge") != null ){
            risk1.setText(getResources().getString(R.string.highRiskPregnancyOldMaternalAge)+humanize(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge")));
        }
        if(ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || ancclient.getDetails().get("HighRiskPregnancyAbortus") != null
                || ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord" ) != null
                ){
            risk2.setText(getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition)+humanize(ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition")));
            risk3.setText(getResources().getString(R.string.HighRiskPregnancyAbortus)+humanize(ancclient.getDetails().get("HighRiskPregnancyAbortus")));
            risk4.setText(getResources().getString(R.string.HighRiskLabourSectionCesareaRecord)+humanize(ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord")));

        }
        txt_highRiskLabourTBRisk.setText(humanize(ancclient.getDetails().get("highRiskLabourTBRisk") != null ? ancclient.getDetails().get("highRiskLabourTBRisk") : "-"));

        highRiskSTIBBVs.setText(humanize(ancclient.getDetails().get("highRiskSTIBBVs") != null ? ancclient.getDetails().get("highRiskSTIBBVs") : "-"));
        highRiskEctopicPregnancy.setText(humanize (ancclient.getDetails().get("highRiskEctopicPregnancy") != null ? ancclient.getDetails().get("highRiskEctopicPregnancy") : "-"));
        highRiskCardiovascularDiseaseRecord.setText(humanize(ancclient.getDetails().get("highRiskCardiovascularDiseaseRecord") != null ? ancclient.getDetails().get("highRiskCardiovascularDiseaseRecord") : "-"));
        highRiskDidneyDisorder.setText(humanize(ancclient.getDetails().get("highRiskDidneyDisorder") != null ? ancclient.getDetails().get("highRiskDidneyDisorder") : "-"));
        highRiskHeartDisorder.setText(humanize(ancclient.getDetails().get("highRiskHeartDisorder") != null ? ancclient.getDetails().get("highRiskHeartDisorder") : "-"));
        highRiskAsthma.setText(humanize(ancclient.getDetails().get("highRiskAsthma") != null ? ancclient.getDetails().get("highRiskAsthma") : "-"));
        highRiskTuberculosis.setText(humanize(ancclient.getDetails().get("highRiskTuberculosis") != null ? ancclient.getDetails().get("highRiskTuberculosis") : "-"));
        highRiskMalaria.setText(humanize(ancclient.getDetails().get("highRiskMalaria") != null ? ancclient.getDetails().get("highRiskMalaria") : "-"));

        txt_HighRiskLabourSectionCesareaRecord.setText(humanize(ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord") != null ? ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord") : "-"));
        HighRiskPregnancyTooManyChildren.setText(humanize(ancclient.getDetails().get("HighRiskPregnancyTooManyChildren") != null ? ancclient.getDetails().get("HighRiskPregnancyTooManyChildren") : "-"));

        txt_highRiskHIVAIDS.setText(humanize(ancclient.getDetails().get("highRiskHIVAIDS") != null ? ancclient.getDetails().get("highRiskHIVAIDS") : "-"));

        show_risk.setText(getResources().getString(R.string.show_more_button));
        show_detail.setText(getResources().getString(R.string.show_less_button));

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
                FlurryFacade.logEvent("taking_mother_pictures_on_kohort_ibu_detail_view");
                bindobject = "anc";
                entityid = ancclient.entityId();
                Intent intent = new Intent(ANCDetailActivity.this, SmartShutterActivity.class);
                intent.putExtra("IdentifyPerson", false);
                intent.putExtra("org.sid.sidface.ImageConfirmation.id", entityid);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIANCSmartRegisterActivity.class));
        overridePendingTransition(0, 0);


    }
}

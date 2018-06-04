package org.ei.opensrp.indonesia.anc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.indonesia.R;
//import org.ei.opensrp.indonesia.face.bpm.MainBPMActivity;
import org.ei.opensrp.indonesia.device.BpmTestMainActivity;
import org.ei.opensrp.indonesia.device.MainBPM;
import org.ei.opensrp.indonesia.device.TestBPM;
import org.ei.opensrp.indonesia.kartu_ibu.KIDetailActivity;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.ANCDetail;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;
//import org.opensrp.id.MainActivity;
//import org.opensrp.bpm.MainBPM;
//import org.opensrp.id.MainBPMActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

//import example.com.mylibrary.MainBPMActivity;

//import example.com.sidlibrary.MainBPMActivity;

import static org.ei.opensrp.util.StringUtil.humanize;
import static org.ei.opensrp.util.StringUtil.humanizeAndDoUPPERCASE;



/**
 * Created by Iq on 07/09/16.
 */
public class ANCDetailActivity extends Activity {

    private static final String TAG = ANCDetailActivity.class.getSimpleName();

    SimpleDateFormat timer = new SimpleDateFormat("hh:mm:ss");

    public static CommonPersonObjectClient ancclient;
    SimpleDateFormat bpm_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.anc_detail_activity);

        String DetailStart = timer.format(new Date());
        Map<String, String> Detail = new HashMap<String, String>();
        Detail.put("start", DetailStart);
        FlurryAgent.logEvent("ANC_detail_view",Detail, true );

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


        TextView details = (TextView) findViewById(R.id.child_detail_information);
        TextView risk = (TextView) findViewById(R.id.detail_today);
        TextView plan = (TextView) findViewById(R.id.detail_l);


        TextView mother_summary = (TextView) findViewById(R.id.mother_summary);
       // birth plan
        TextView txt_b_edd = (TextView) findViewById(R.id.txt_b_edd);
        TextView txt_b_lastvisit = (TextView) findViewById(R.id.txt_b_lastvisit);
        TextView txt_b_location = (TextView) findViewById(R.id.txt_b_location);

        TextView txt_b_edd2 = (TextView) findViewById(R.id.txt_b_edd2);
        TextView txt_b_attend = (TextView) findViewById(R.id.txt_b_attend);
        TextView txt_b_place = (TextView) findViewById(R.id.txt_b_place);
        TextView txt_b_person = (TextView) findViewById(R.id.txt_b_person);
        TextView txt_b_transport = (TextView) findViewById(R.id.txt_b_transport);
        TextView txt_b_blood = (TextView) findViewById(R.id.txt_b_blood);
        TextView txt_b_houser = (TextView) findViewById(R.id.txt_b_houser);
        TextView txt_b_stock = (TextView) findViewById(R.id.txt_b_stock);


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

        ImageView heart_bpm = (ImageView) findViewById(R.id.icon_device);
        heart_bpm.setVisibility(View.VISIBLE);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ANCDetailActivity.this, NativeKIANCSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
                String DetailEnd = timer.format(new Date());
                Map<String, String> Detail = new HashMap<>();
                Detail.put("end", DetailEnd);
                FlurryAgent.logEvent("ANC_detail_view", Detail, true);
            }
        });

        ImageView device = (ImageView) findViewById(R.id.icon_device);
        device.setOnClickListener(bpmListener);

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(ancclient);

        Keterangan_k1k4.setText(String.format(": %s", humanize(ancclient.getDetails().get("Keterangan_k1k4") != null ? ancclient.getDetails().get("Keterangan_k1k4") : "-")));
        tanggalHPHT.setText(String.format(": %s", humanize(ancclient.getDetails().get("tanggalHPHT") != null ? ancclient.getDetails().get("tanggalHPHT") : "-")));
        usiaKlinis.setText(String.format(": %s", humanize(ancclient.getDetails().get("usiaKlinis") != null ? ancclient.getDetails().get("usiaKlinis") : "-")));
        trimesterKe.setText(String.format(": %s", humanize(ancclient.getDetails().get("trimesterKe") != null ? ancclient.getDetails().get("trimesterKe") : "-")));
        kunjunganKe.setText(String.format(": %s", humanize(ancclient.getDetails().get("kunjunganKe") != null ? ancclient.getDetails().get("kunjunganKe") : "-")));
        bbKg.setText(String.format(": %s", humanize(ancclient.getDetails().get("bbKg") != null ? ancclient.getDetails().get("bbKg") : "-")));
        tandaVitalTDSistolik.setText(String.format(": %s", humanize(ancclient.getDetails().get("tandaVitalTDSistolik") != null ? ancclient.getDetails().get("tandaVitalTDSistolik") : "-")));
        tandaVitalTDDiastolik.setText(String.format(": %s", humanize(ancclient.getDetails().get("tandaVitalTDDiastolik") != null ? ancclient.getDetails().get("tandaVitalTDDiastolik") : "-")));
        hasilPemeriksaanLILA.setText(String.format(": %s", humanize(ancclient.getDetails().get("hasilPemeriksaanLILA") != null ? ancclient.getDetails().get("hasilPemeriksaanLILA") : "-")));
        statusGiziibu.setText(String.format(": %s", humanize(ancclient.getDetails().get("statusGiziibu") != null ? ancclient.getDetails().get("statusGiziibu") : "-")));
        tfu.setText(String.format(": %s", humanize(ancclient.getDetails().get("tfu") != null ? ancclient.getDetails().get("tfu") : "-")));
        refleksPatelaIbu.setText(String.format(": %s", humanize(ancclient.getDetails().get("refleksPatelaIbu") != null ? ancclient.getDetails().get("refleksPatelaIbu") : "-")));
        djj.setText(String.format(": %s", humanize(ancclient.getDetails().get("djj") != null ? ancclient.getDetails().get("djj") : "-")));
        kepalaJaninTerhadapPAP.setText(String.format(": %s", humanize(ancclient.getDetails().get("kepalaJaninTerhadapPAP") != null ? ancclient.getDetails().get("kepalaJaninTerhadapPAP") : "-")));
        taksiranBeratJanin.setText(String.format(": %s", humanize(ancclient.getDetails().get("taksiranBeratJanin") != null ? ancclient.getDetails().get("taksiranBeratJanin") : "-")));
        persentasiJanin.setText(String.format(": %s", humanize(ancclient.getDetails().get("persentasiJanin") != null ? ancclient.getDetails().get("persentasiJanin") : "-")));
        jumlahJanin.setText(String.format(": %s", humanize(ancclient.getDetails().get("jumlahJanin") != null ? ancclient.getDetails().get("jumlahJanin") : "-")));


        statusImunisasitt.setText(String.format(": %s", humanizeAndDoUPPERCASE(ancclient.getDetails().get("statusImunisasitt") != null ? ancclient.getDetails().get("statusImunisasitt") : "-")));
        pelayananfe.setText(String.format(": %s", humanize(ancclient.getDetails().get("pelayananfe") != null ? ancclient.getDetails().get("pelayananfe") : "-")));
        komplikasidalamKehamilan.setText(String.format(": %s", humanize(ancclient.getDetails().get("komplikasidalamKehamilan") != null ? ancclient.getDetails().get("komplikasidalamKehamilan") : "-")));
        integrasiProgrampmtctvct.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgrampmtctvct") != null ? ancclient.getDetails().get("integrasiProgrampmtctvct") : "-")));
        integrasiProgrampmtctPeriksaDarah.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgrampmtctPeriksaDarah") != null ? ancclient.getDetails().get("integrasiProgrampmtctPeriksaDarah") : "-")));
        integrasiProgrampmtctSerologi.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgrampmtctSerologi") != null ? ancclient.getDetails().get("integrasiProgrampmtctSerologi") : "-")));
        integrasiProgrampmtctarvProfilaksis.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgrampmtctarvProfilaksis") != null ? ancclient.getDetails().get("integrasiProgrampmtctarvProfilaksis") : "-")));
        integrasiProgramMalariaPeriksaDarah.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgramMalariaPeriksaDarah") != null ? ancclient.getDetails().get("integrasiProgramMalariaPeriksaDarah") : "-")));
        integrasiProgramMalariaObat.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgramMalariaObat") != null ? ancclient.getDetails().get("integrasiProgramMalariaObat") : "-")));
        integrasiProgramMalariaKelambuBerinsektisida.setText(String.format(": %s", ancclient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") != null ? ancclient.getDetails().get("integrasiProgramMalariaKelambuBerinsektisida") : "-"));
        integrasiProgramtbDahak.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgramtbDahak") != null ? ancclient.getDetails().get("integrasiProgramtbDahak") : "-")));
        integrasiProgramtbObat.setText(String.format(": %s", humanize(ancclient.getDetails().get("integrasiProgramtbObat") != null ? ancclient.getDetails().get("integrasiProgramtbObat") : "-")));
        laboratoriumPeriksaHbHasil.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumPeriksaHbHasil") != null ? ancclient.getDetails().get("laboratoriumPeriksaHbHasil") : "-")));
        laboratoriumPeriksaHbAnemia.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumPeriksaHbAnemia") != null ? ancclient.getDetails().get("laboratoriumPeriksaHbAnemia") : "-")));
        laboratoriumProteinUria.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumProteinUria") != null ? ancclient.getDetails().get("laboratoriumProteinUria") : "-")));
        laboratoriumGulaDarah.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumGulaDarah") != null ? ancclient.getDetails().get("laboratoriumGulaDarah") : "-")));
        laboratoriumThalasemia.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumThalasemia") != null ? ancclient.getDetails().get("laboratoriumThalasemia") : "-")));
        laboratoriumSifilis.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumSifilis") != null ? ancclient.getDetails().get("laboratoriumSifilis") : "-")));
        laboratoriumHbsAg.setText(String.format(": %s", humanize(ancclient.getDetails().get("laboratoriumHbsAg") != null ? ancclient.getDetails().get("laboratoriumHbsAg") : "-")));

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

        ancKe.setText(String.format(": %s", ancclient.getDetails().get("ancKe") != null ? ancclient.getDetails().get("ancKe") : "-"));

        ancDate.setText(String.format(": %s", ancclient.getDetails().get("ancDate") != null ? ancclient.getDetails().get("ancDate") : "-"));

        if(ancclient.getCaseId()!=null){//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(ancclient.getCaseId(), OpenSRPImageLoader.getStaticImageListener(kiview, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
            KIDetailActivity.setImagetoHolderFromUri(this,
                    DrishtiApplication.getAppDir() + File.separator + ancclient.getDetails().get("base_entity_id") + ".JPEG",
                    kiview, R.mipmap.woman_placeholder);
        }

        nama.setText(String.format("%s%s", getResources().getString(R.string.name), ancclient.getColumnmaps().get("namalengkap") != null ? ancclient.getColumnmaps().get("namalengkap") : "-"));
        nik.setText(String.format("%s%s", getResources().getString(R.string.nik), ancclient.getDetails().get("nik") != null ? ancclient.getDetails().get("nik") : "-"));
        husband_name.setText(String.format("%s%s", getResources().getString(R.string.husband_name), ancclient.getColumnmaps().get("namaSuami") != null ? ancclient.getColumnmaps().get("namaSuami") : "-"));
        String tglLahir = ancclient.getDetails().get("tanggalLahir");
        dob.setText(String.format("%s%s", getResources().getString(R.string.dob), (tglLahir != null && !tglLahir.isEmpty()) ? tglLahir.substring(0, tglLahir.indexOf("T")) : "-"));

     //   dob.setText(getResources().getString(R.string.dob)+ (ancclient.getDetails().get("tanggalLahir") != null ? ancclient.getDetails().get("tanggalLahir") : "-"));
        phone.setText(String.format("No HP: %s", ancclient.getDetails().get("NomorTelponHp") != null ? ancclient.getDetails().get("NomorTelponHp") : "-"));

        //risk
        if(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge") != null ){
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyYoungMaternalAge), humanize(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if(ancclient.getDetails().get("highRiskPregnancyOldMaternalAge") != null ){
            risk1.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyOldMaternalAge), humanize(ancclient.getDetails().get("highRiskPregnancyYoungMaternalAge"))));
        }
        if(ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null
                || ancclient.getDetails().get("HighRiskPregnancyAbortus") != null
                || ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord" ) != null
                ){
            risk2.setText(String.format("%s%s", getResources().getString(R.string.highRiskPregnancyProteinEnergyMalnutrition), humanize(ancclient.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"))));
            risk3.setText(String.format("%s%s", getResources().getString(R.string.HighRiskPregnancyAbortus), humanize(ancclient.getDetails().get("HighRiskPregnancyAbortus"))));
            risk4.setText(String.format("%s%s", getResources().getString(R.string.HighRiskLabourSectionCesareaRecord), humanize(ancclient.getDetails().get("HighRiskLabourSectionCesareaRecord"))));

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

        show_risk.setVisibility(View.GONE);
        show_detail.setVisibility(View.GONE);

      //  TextView details = (TextView) findViewById(R.id.child_detail_information);
      //  TextView risk = (TextView) findViewById(R.id.detail_today);
      //  TextView plan = (TextView) findViewById(R.id.detail_l);
        // birth plan
         txt_b_edd.setText(humanize(ancclient.getDetails().get("htp") != null ? ancclient.getDetails().get("htp") : "-"));
         txt_b_lastvisit.setText(humanize(ancclient.getDetails().get("tanggalKunjunganRencanaPersalinan") != null ? ancclient.getDetails().get("tanggalKunjunganRencanaPersalinan") : "-"));
         txt_b_location.setText(humanize(ancclient.getDetails().get("lokasiPeriksa") != null ? ancclient.getDetails().get("lokasiPeriksa") : "-"));

         txt_b_edd2.setText(humanize(ancclient.getDetails().get("htp") != null ? ancclient.getDetails().get("htp") : "-"));
         txt_b_attend.setText(humanize(ancclient.getDetails().get("rencanaPenolongPersalinan") != null ? ancclient.getDetails().get("rencanaPenolongPersalinan") : "-"));
         txt_b_place.setText(humanize(ancclient.getDetails().get("tempatRencanaPersalinan") != null ? ancclient.getDetails().get("tempatRencanaPersalinan") : "-"));
         txt_b_person.setText(humanize(ancclient.getDetails().get("rencanaPendampingPersalinan") != null ? ancclient.getDetails().get("rencanaPendampingPersalinan") : "-"));
         txt_b_transport.setText(humanize(ancclient.getDetails().get("transportasi") != null ? ancclient.getDetails().get("transportasi") : "-"));
         txt_b_blood.setText(humanize(ancclient.getDetails().get("pendonor") != null ? ancclient.getDetails().get("pendonor") : "-"));
         txt_b_houser.setText(humanize(ancclient.getDetails().get("kondisiRumah") != null ? ancclient.getDetails().get("kondisiRumah") : "-"));
         txt_b_stock.setText(humanize(ancclient.getDetails().get("persediaanPerlengkapanPersalinan") != null ? ancclient.getDetails().get("persediaanPerlengkapanPersalinan") : "-"));


        risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryFacade.logEvent("click_risk_detail");
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.VISIBLE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
              //  findViewById(R.id.show_more_detail).setVisibility(View.VISIBLE);
               // findViewById(R.id.show_more).setVisibility(View.GONE);
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.VISIBLE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
              //  findViewById(R.id.show_more).setVisibility(View.VISIBLE);
              //  findViewById(R.id.show_more_detail).setVisibility(View.GONE);
            }
        });

        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.id1).setVisibility(View.GONE);
                findViewById(R.id.id2).setVisibility(View.GONE);
                findViewById(R.id.id3).setVisibility(View.VISIBLE);
               // mother_summary.setText("Birth Plan Summary");
            }
        });

        tandaVitalTDDiastolik.setOnClickListener(bpmListener);
        tandaVitalTDSistolik.setOnClickListener(bpmListener);

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, NativeKIANCSmartRegisterActivity.class));
        overridePendingTransition(0, 0);
    }

    private View.OnClickListener bpmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            Intent intent = new Intent(ANCDetailActivity.this, MainBPM.class);
//            Intent intent = new Intent(ANCDetailActivity.this, MainActivity.class);
//            Intent intent = new Intent(ANCDetailActivity.this, BpmMainActivity.class);
//            startActivity(intent);
            bpmAction();
        }
    };

    private void bpmAction() {
        Intent i = new Intent(ANCDetailActivity.this, MainBPM.class);
//        Intent i = new Intent(ANCDetailActivity.this, TestBPM.class);
        bpm_timer = new SimpleDateFormat("hh:mm:ss.SS", Locale.ENGLISH);
        startActivityForResult(i, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        String imei = ((TelephonyManager) getSystemService(android.content.Context.TELEPHONY_SERVICE)).getDeviceId();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
//        System.out.println(dateFormat.format(cal.getTime()));

        if (requestCode == 2 && resultCode!=RESULT_CANCELED ){
//            Log.e(
//                    TAG, "onActivityResult: "+
//                    data.getStringExtra("HIGH") +
//                    data.getStringExtra("LOW") +
//                    data.getStringExtra("AHR") +
//                    data.getStringExtra("PULSE")
//            );
            DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
            Long tsLong = System.currentTimeMillis()/1000;
            detailsRepository.add(ancclient.entityId(), "tandaVitalTDSistolik", data.getStringExtra("HIGH"), tsLong);
            detailsRepository.add(ancclient.entityId(), "tandaVitalTDDiastolik", data.getStringExtra("LOW"), tsLong);
            detailsRepository.add(ancclient.entityId(), "tandaVitalPulse", data.getStringExtra("PULSE"), tsLong);
            try{
                Log.i(TAG, "onActivityResult: saveToserver" );
                SimpleDateFormat sdf;
                Date date = new Date();

                sdf = new SimpleDateFormat("hh:mm:ss.SS", Locale.ENGLISH);
                System.out.println(sdf.format(date));
                sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
                FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
                String formSubmission =
                        "<Blood_Test encounter_type=\"Blood Test\" id=\"blood_test\" version=\"201705080820\" _id=\""+ancclient.entityId()+"\">" +
                        "<formhub><uuid>"+ UUID.randomUUID().toString() +"</uuid></formhub>\n" +
                        "<start openmrs_entity=\"encounter\" openmrs_entity_id=\"encounter_start\">"+bpm_timer+"</start>" +
                        "<today openmrs_entity=\"encounter\" openmrs_entity_id=\"encounter_date\">"+ dateFormat.format(cal.getTime()) +"</today>" +
                        "<deviceid>"+ imei +"</deviceid>" +
                        "<simserial>no simserial property in enketo</simserial>" +
                        "<phonenumber>no phonenumber property in enketo</phonenumber>" +
                        "<Province>"+ ancclient.getDetails().get("stateProvince") +"</Province>" +
                        "<District>"+ ancclient.getDetails().get("countyDistrict") +"</District>" +
                        "<Sub-district>"+ ancclient.getDetails().get("countyDistrict") +"</Sub-district>" +
                        "<Village>"+ancclient.getDetails().get("countyDistrict")+"</Village>" +
                        "<Sub-village>"+ ancclient.getDetails().get("countyDistrict") +".</Sub-village>" +
                        "<existing_location openmrs_entity=\"encounter\" openmrs_entity_id=\"location_id\">"+ancclient.getDetails().get("cityVillage")+"</existing_location>" +
                        "<provinsi openmrs_entity=\"person_address\" openmrs_entity_id=\"stateProvince\" openmrs_entity_parent=\"usual_residence\">"+ ancclient.getDetails().get("stateProvince") +"</provinsi>" +
                        "<kabupaten openmrs_entity=\"person_address\" openmrs_entity_id=\"countyDistrict\" openmrs_entity_parent=\"usual_residence\">"+ ancclient.getDetails().get("countyDistrict") +"</kabupaten>" +
                        "<desa openmrs_entity=\"person_address\" openmrs_entity_id=\"cityVillage\" openmrs_entity_parent=\"usual_residence\">"+ ancclient.getDetails().get("countyDistrict") +"</desa>" +
                        "<dusun openmrs_entity=\"person_address\" openmrs_entity_id=\"address1\" openmrs_entity_parent=\"usual_residence\">"+ ancclient.getDetails().get("countyDistrict") +"</dusun>" +
                        "<kecamatan openmrs_entity=\"person_address\" openmrs_entity_id=\"address2\" openmrs_entity_parent=\"usual_residence\">"+ ancclient.getDetails().get("countyDistrict") +"</kecamatan>" +
                        "<td_sistolik openmrs_entity=\"concept\" openmrs_entity_id=\"5085AAAAAAAAAAAAAAAAAAAAAAAAAAAA\">"+ data.getStringExtra("HIGH")+"</td_sistolik>" +
                        "<td_diastolik openmrs_entity=\"concept\" openmrs_entity_id=\"5086AAAAAAAAAAAAAAAAAAAAAAAAAAAA\">"+data.getStringExtra("LOW")+"</td_diastolik>" +
                        "<pulse openmrs_entity=\"concept\" openmrs_entity_id=\"5087AAAAAAAAAAAAAAAAAAAAAAAAAAAA\">"+data.getStringExtra("PULSE")+"</pulse>" +
                        "<ahr openmrs_entity=\"concept\" openmrs_entity_id=\"160632AAAAAAAAAAAAAAAAAAAAAAAAAA\" openmrs_entity_parent=\"5087AAAAAAAAAAAAAAAAAAAAAAAAAAAA\">"+data.getStringExtra("AHR")+"</ahr>\n" +
                        "<end openmrs_entity=\"encounter\" openmrs_entity_id=\"encounter_end\">2017-05-08T17:21:47.000+08:00</end>" +
                        "<meta>" +
                        "<instanceID>uuid:"+UUID.randomUUID().toString()+"</instanceID>" +
                        "<deprecatedID/>" +
                        "</meta>" +
                        "</Blood_Test>";

                formUtils.generateFormSubmisionFromXMLString(ancclient.entityId(), formSubmission, "blood_test", new JSONObject());

            }catch (Exception e){
                // TODO: show error dialog on the formfragment if the submission fails
                e.printStackTrace();
            }
        } else{
            Log.e(TAG, "onActivityResult: Cancel " );
        }

        finish();
        startActivity(getIntent());
    }



}

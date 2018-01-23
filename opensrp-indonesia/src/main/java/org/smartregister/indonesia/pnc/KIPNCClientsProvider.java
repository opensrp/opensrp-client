package org.smartregister.indonesia.pnc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.kartu_ibu.KIDetailActivity;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;
import org.smartregister.util.OpenSRPImageLoader;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.io.File;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.StringUtil.humanize;
import static org.smartregister.util.StringUtil.humanizeAndDoUPPERCASE;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIPNCClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final OpenSRPImageLoader mImageLoader;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    public KIPNCClientsProvider(Context context,
                                View.OnClickListener onClickListener,
                                AlertService alertService) {
        this.onClickListener = onClickListener;
//        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.smartregister.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.smartregister.R.color.text_black);
        mImageLoader = DrishtiApplication.getCachedImageLoaderInstance();


    }

    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        ViewHolder viewholder;
        if (convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewholder = new ViewHolder();
            viewholder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);

            viewholder.wife_name = (TextView) convertView.findViewById(R.id.wife_name);
            viewholder.husband_name = (TextView) convertView.findViewById(R.id.txt_husband_name);
            viewholder.village_name = (TextView) convertView.findViewById(R.id.txt_village_name);
            viewholder.wife_age = (TextView) convertView.findViewById(R.id.wife_age);
            viewholder.pnc_id = (TextView) convertView.findViewById(R.id.pnc_id);
            // viewholder.unique_id = (TextView)convertView.findViewById(R.id.unique_id);

            viewholder.hr_badge = (ImageView) convertView.findViewById(R.id.img_hr_badge);
            viewholder.img_hrl_badge = (ImageView) convertView.findViewById(R.id.img_hrl_badge);
            viewholder.bpl_badge = (ImageView) convertView.findViewById(R.id.img_bpl_badge);
            viewholder.hrp_badge = (ImageView) convertView.findViewById(R.id.img_hrp_badge);
            viewholder.hrpp_badge = (ImageView) convertView.findViewById(R.id.img_hrpp_badge);
            // ViewHolder.img_hp_badge = ImageView.Set;   img_hrl_badge img_bpl_badge img_hrp_badge img_hrpp_badge

            viewholder.tanggal_bersalin = (TextView) convertView.findViewById(R.id.dok_tanggal_bersalin);
            viewholder.tempat_persalinan = (TextView) convertView.findViewById(R.id.txt_tempat_persalinan);
            viewholder.dok_tipe = (TextView) convertView.findViewById(R.id.txt_tipe);

            viewholder.komplikasi = (TextView) convertView.findViewById(R.id.txt_komplikasi);

            viewholder.tanggal_kunjungan = (TextView) convertView.findViewById(R.id.txt_tanggal_kunjungan_pnc);
            viewholder.KF = (TextView) convertView.findViewById(R.id.txt_kf);
            viewholder.vit_a = (TextView) convertView.findViewById(R.id.txt_vit_a);

            viewholder.td_sistolik = (TextView) convertView.findViewById(R.id.txt_td_sistolik);
            viewholder.td_diastolik = (TextView) convertView.findViewById(R.id.txt_td_diastolik);
            viewholder.td_suhu = (TextView) convertView.findViewById(R.id.txt_td_suhu);

            //  txt_kondisi_ibu txt_KF txt_vit_a

            viewholder.profilepic = (ImageView) convertView.findViewById(R.id.img_profile);
            viewholder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        viewholder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.woman_placeholder));

        viewholder.follow_up.setOnClickListener(onClickListener);
        viewholder.follow_up.setTag(smartRegisterClient);
        viewholder.profilelayout.setOnClickListener(onClickListener);
        viewholder.profilelayout.setTag(smartRegisterClient);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }
        viewholder.follow_up.setImageDrawable(iconPencilDrawable);
        viewholder.follow_up.setOnClickListener(onClickListener);
        //set image

        AllCommonsRepository allancRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
        CommonPersonObject pncobject = allancRepository.findByCaseID(pc.entityId());

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(pncobject.getColumnmaps());

        if (pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        } else {
            pc.setDetails(details);
        }

        System.out.println("client : "+pc.getColumnmaps().toString());
        System.out.println("event : "+pc.getDetails().toString());

        viewholder.tanggal_bersalin.setText(humanize(pc.getDetails().get("tanggalKalaIAktif") != null ? pc.getDetails().get("tanggalKalaIAktif") : ""));
        String tempat = pc.getDetails().get("tempatBersalin") != null ? pc.getDetails().get("tempatBersalin") : "";
        viewholder.tempat_persalinan.setText(tempat.equals("podok_bersalin_desa") ? "POLINDES" : tempat.equals("pusat_kesehatan_masyarakat_pembantu") ? "Puskesmas pembantu" : tempat.equals("pusat_kesehatan_masyarakat") ? "Puskesmas" : humanize(tempat));
        viewholder.dok_tipe.setText(humanize(pc.getDetails().get("caraPersalinanIbu") != null ? pc.getDetails().get("caraPersalinanIbu") : ""));
        viewholder.komplikasi.setText(humanize(pc.getDetails().get("komplikasi") != null ? translateComplication(pc.getDetails().get("komplikasi")) : ""));


        String date = pc.getDetails().get("PNCDate") != null ? pc.getDetails().get("PNCDate") : "";
        String vit_a = pc.getDetails().get("pelayananfe") != null ? pc.getDetails().get("pelayananfe") : "";
        viewholder.tanggal_kunjungan.setText(String.format("%s %s", context.getString(R.string.str_pnc_delivery_date), date));

        viewholder.vit_a.setText(String.format("%s %s", context.getString(R.string.fe), yesNo(vit_a)));

        viewholder.td_suhu.setText(humanize(pc.getDetails().get("tandaVitalSuhu") != null ? pc.getDetails().get("tandaVitalSuhu") : ""));


        viewholder.td_sistolik.setText(humanize(pc.getDetails().get("tandaVitalTDSistolik") != null ? pc.getDetails().get("tandaVitalTDSistolik") : ""));
        viewholder.td_diastolik.setText(humanize(pc.getDetails().get("tandaVitalTDDiastolik") != null ? pc.getDetails().get("tandaVitalTDDiastolik") : ""));

        viewholder.hr_badge.setVisibility(View.INVISIBLE);
        viewholder.hrp_badge.setVisibility(View.INVISIBLE);
        viewholder.img_hrl_badge.setVisibility(View.INVISIBLE);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"), pc.getDetails().get("highRiskEctopicPregnancy"), pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"), pc.getDetails().get("highRiskHeartDisorder"), pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"), pc.getDetails().get("highRiskMalaria"), pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"), viewholder.hr_badge);

        risk(pc.getDetails().get("highRiskPregnancyPIH"), pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"),
                pc.getDetails().get("HighRiskPregnancyTooManyChildren"),
                pc.getDetails().get("highRiskPregnancyDiabetes"), pc.getDetails().get("highRiskPregnancyAnemia"), null, null, null, null, null, viewholder.hrp_badge);

        risk(pc.getDetails().get("highRiskLabourFetusMalpresentation"), pc.getDetails().get("highRiskLabourFetusSize"),
                pc.getDetails().get("highRisklabourFetusNumber"), pc.getDetails().get("HighRiskLabourSectionCesareaRecord"),
                pc.getDetails().get("highRiskLabourTBRisk"), null, null, null, null, null, viewholder.img_hrl_badge);

        String kf_ke = pc.getDetails().get("hariKeKF")!=null?pc.getDetails().get("hariKeKF"):"";
        viewholder.KF.setText(String.format("%s %s", context.getString(R.string.hari_ke_kf), humanizeAndDoUPPERCASE(kf_ke)));
        viewholder.wife_name.setText(pc.getColumnmaps().get("namalengkap")!=null?pc.getColumnmaps().get("namalengkap"):"");
        viewholder.husband_name.setText(pc.getColumnmaps().get("namaSuami")!=null?pc.getColumnmaps().get("namaSuami"):"");
        viewholder.village_name.setText(pc.getDetails().get("address1")!=null?pc.getDetails().get("address1"):"");
        viewholder.wife_age.setText(pc.getDetails().get("umur")!=null?pc.getDetails().get("umur"):"");
        viewholder.pnc_id.setText(pc.getDetails().get("noIbu")!=null?pc.getDetails().get("noIbu"):"");

        //start profile image
        viewholder.profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
//        if (pc.getCaseId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            mImageLoader.getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(viewholder.profilepic, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
//        }

        KIDetailActivity.setImagetoHolderFromUri((Activity) context,
                DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                viewholder.profilepic, R.mipmap.woman_placeholder);


        //end profile image

        //   viewholder.unique_id.setText(pc.getDetails().get("unique_id")!=null?pc.getDetails().get("unique_id"):"");


        //  AllCommonsRepository anakrep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("anak");
        //    final CommonPersonObject anakparent = anakrep.findByCaseID(pc.getColumnmaps().get("id"));

        //  viewholder.KF.setText(anakparent.getDetails().get("saatLahirsd5JamKondisibayi")!=null?anakparent.getDetails().get("saatLahirsd5JamKondisibayi"):"");
        //  viewholder.KF.setText(anakparent.getDetails().get("saatLahirsd5JamKondisibayi")!=null?anakparent.getDetails().get("saatLahirsd5JamKondisibayi")+","+anakparent.getDetails().get("beratLahir"):"-");


        convertView.setLayoutParams(clientViewLayoutParams);
        //   return convertView;
    }
    // CommonPersonObjectController householdelcocontroller;


    //    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        getView(smartRegisterClient, view);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_ki_pnc_client, null);
        return View;
    }

    private String yesNo(String text){
        return context.getString(text.toLowerCase().contains("y")? R.string.mcareyes_button_label : R.string.mcareno_button_label);
    }

    private String translateComplication(String text){
        return text.toLowerCase().contains("dak_ada_kompli")? context.getString(R.string.no_complication):text;
    }

    public void risk(String risk1, String risk2, String risk3, String risk4, String risk5, String risk6, String risk7, String risk8, String risk9, String risk10, ImageView riskview) {
        if (risk1 != null && risk1.equals("yes")
                || risk2 != null && risk2.equals("yes")
                || risk3 != null && risk3.equals("yes")
                || risk4 != null && risk4.equals("yes")
                || risk5 != null && risk5.equals("yes")
                || risk6 != null && risk6.equals("yes")
                || risk7 != null && risk7.equals("yes")
                || risk8 != null && risk8.equals("yes")
                || risk9 != null && risk9.equals("yes")
                || risk10 != null && risk10.equals("yes")) {

            riskview.setVisibility(View.VISIBLE);
        }

    }

    class ViewHolder {

        LinearLayout profilelayout;

        TextView wife_name, husband_name, village_name, wife_age, tanggal_bersalin;
        TextView tempat_persalinan, dok_tipe;
        TextView tanggal_kunjungan, komplikasi;
        TextView KF, vit_a, pnc_id, td_sistolik, td_diastolik, td_suhu;

        ImageButton follow_up;

        ImageView profilepic;
        ImageView hr_badge;
        ImageView hrpp_badge;
        ImageView bpl_badge;
        ImageView hrp_badge;
        ImageView img_hrl_badge;

        TextView gravida;
        Button warnbutton;
        TextView parity;
        TextView number_of_abortus;
        TextView number_of_alive;
        TextView no_ibu;
        TextView unique_id;
        TextView beratbadan_tb;
        TextView anc_penyakit_kronis;
        TextView status_type;
        TextView status_date;
        TextView alert_status;
        RelativeLayout status_layout;
        TextView tanggal_kunjungan_anc;
        TextView anc_number;
        TextView kunjugan_ke;
        ImageView hp_badge;


        TextView kondisi_ibu;
    }


}
package org.smartregister.indonesia.child;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.indonesia.R;
import org.smartregister.indonesia.application.BidanApplication;
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
import org.smartregister.view.viewHolder.OnClickFormLauncher;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.smartregister.util.StringUtil.humanize;
import static org.joda.time.LocalDateTime.parse;

public class AnakRegisterClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = AnakRegisterClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final OpenSRPImageLoader mImageLoader;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    public AnakRegisterClientsProvider(Context context,
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
        mImageLoader = BidanApplication.getInstance().getCachedImageLoaderInstance();


    }

    @Override
    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        ViewHolder viewHolder;
        if (convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);

            viewHolder.childs_name = (TextView) convertView.findViewById(R.id.child_name);
            viewHolder.mother_name = (TextView) convertView.findViewById(R.id.mother_name);
            viewHolder.village_name = (TextView) convertView.findViewById(R.id.txt_village_name);
            viewHolder.childs_age = (TextView) convertView.findViewById(R.id.child_age);
            // viewHolder.no_ibu = (TextView) convertView.findViewById(R.id.txt_ibu_ki_no);
            // viewHolder.unique_id = (TextView)convertView.findViewById(R.id.unique_id);

            viewHolder.hp_badge = (ImageView) convertView.findViewById(R.id.img_hr_badge);

            //delivery documentation
            viewHolder.anak_register_dob = (TextView) convertView.findViewById(R.id.anak_register_dob);
            viewHolder.tempat_lahir = (TextView) convertView.findViewById(R.id.tempat_lahir);
            viewHolder.berat_lahir = (TextView) convertView.findViewById(R.id.berat_lahir);
            viewHolder.tipe_lahir = (TextView) convertView.findViewById(R.id.tipe_lahir);
            viewHolder.status_gizi = (TextView) convertView.findViewById(R.id.txt_status_gizi);

            viewHolder.hb0_no = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_no);
            viewHolder.hb0_yes = (ImageView) convertView.findViewById(R.id.icon_immuni_hb_yes);
            viewHolder.vitk_no = (ImageView) convertView.findViewById(R.id.icon_vit_k_no);
            viewHolder.vitk_yes = (ImageView) convertView.findViewById(R.id.icon_vit_k_yes);
            viewHolder.campak_no = (ImageView) convertView.findViewById(R.id.icon_campak_no);
            viewHolder.campak_yes = (ImageView) convertView.findViewById(R.id.icon_campak_yes);
            viewHolder.ivp_no = (ImageView) convertView.findViewById(R.id.icon_ivp_no);
            viewHolder.ivp_yes = (ImageView) convertView.findViewById(R.id.icon_ivp_yes);

            viewHolder.pol1_no = (ImageView) convertView.findViewById(R.id.icon_pol1_no);
            viewHolder.pol1_yes = (ImageView) convertView.findViewById(R.id.icon_pol1_yes);
            viewHolder.pol2_no = (ImageView) convertView.findViewById(R.id.icon_pol2_no);
            viewHolder.pol2_yes = (ImageView) convertView.findViewById(R.id.icon_pol2_yes);
            viewHolder.pol3_no = (ImageView) convertView.findViewById(R.id.icon_pol3_no);
            viewHolder.pol3_yes = (ImageView) convertView.findViewById(R.id.icon_pol3_yes);
            viewHolder.pol4_no = (ImageView) convertView.findViewById(R.id.icon_pol4_no);
            viewHolder.pol4_yes = (ImageView) convertView.findViewById(R.id.icon_pol4_yes);

            viewHolder.berat_badan = (TextView) convertView.findViewById(R.id.txt_current_weight);
            viewHolder.tanggal_kunjungan_anc = (TextView) convertView.findViewById(R.id.txt_visit_date);
            viewHolder.tinggi = (TextView) convertView.findViewById(R.id.txt_current_height);

            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.child_boy));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.follow_up.setOnClickListener(onClickListener);
        viewHolder.follow_up.setTag(smartRegisterClient);
        viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }

        viewHolder.follow_up.setImageDrawable(iconPencilDrawable);
        viewHolder.follow_up.setOnClickListener(onClickListener);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        viewHolder.childs_name.setText(pc.getColumnmaps().get("namaBayi") != null ? pc.getColumnmaps().get("namaBayi") : "");
        //delivery documentation
        viewHolder.anak_register_dob.setText(pc.getColumnmaps().get("tanggalLahirAnak") != null ? pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T")) : "");
        viewHolder.berat_lahir.setText(pc.getDetails().get("beratLahir") != null ? pc.getDetails().get("beratLahir") : "");

        //start profile image
        viewHolder.profilepic.setTag(R.id.entity_id, pc.getCaseId());//required when saving file to disk

//        Log.e(TAG, "getView: " + pc.getDetails().toString());
//        if (pc.getCaseId() != null) {//image already in local storage most likey ):
        //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            if (pc.getDetails().get("gender") != null) {
//                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(),
//                        OpenSRPImageLoader.getStaticImageListener(
//                                viewHolder.profilepic,
//                                pc.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant,
//                                0)
//                );
//            } else {
//                Log.e(TAG, "getView: Gender is NOT SET");
//            }
//        }

        if (pc.getDetails().get("gender") != null) {
            KIDetailActivity.setImagetoHolderFromUri((Activity) context,
                    DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                    viewHolder.profilepic, pc.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        } else {
            Log.e(TAG, "getView: Gender is NOT SET");
        }

        //end profile image

        //immunization
        checkVisibility(pc.getDetails().get("hb0"), null, viewHolder.hb0_no, viewHolder.hb0_yes);
        checkVisibility(pc.getDetails().get("polio1"), pc.getDetails().get("bcg"), viewHolder.pol1_no, viewHolder.pol1_yes);
        checkVisibility(pc.getDetails().get("dptHb1"), pc.getDetails().get("polio2"), viewHolder.pol2_no, viewHolder.pol2_yes);
        checkVisibility(pc.getDetails().get("dptHb2"), pc.getDetails().get("polio3"), viewHolder.pol3_no, viewHolder.pol3_yes);
        checkVisibility(pc.getDetails().get("dptHb3"), pc.getDetails().get("polio4"), viewHolder.pol4_no, viewHolder.pol4_yes);
        checkVisibility(pc.getDetails().get("jenisPelayanan"), null, viewHolder.vitk_no, viewHolder.vitk_yes);
        checkVisibility(pc.getDetails().get("campak"), null, viewHolder.campak_no, viewHolder.campak_yes);
        checkVisibility(pc.getDetails().get("ipv"), null, viewHolder.ivp_no, viewHolder.ivp_yes);

        //child visit status
        String berat = pc.getDetails().get("beratBayi") != null ? " " + pc.getDetails().get("beratBayi") : pc.getDetails().get("beratBadan") != null ? pc.getDetails().get("beratBadan") : "";
        String tanggal = pc.getDetails().get("tanggalKunjunganBayiPerbulan") != null ? " " + pc.getDetails().get("tanggalKunjunganBayiPerbulan") : pc.getDetails().get("tanggalPenimbangan") != null ? pc.getDetails().get("tanggalPenimbangan") : "";
        String tinggi = pc.getDetails().get("panjangBayi") != null ? " " + pc.getDetails().get("panjangBayi") : pc.getDetails().get("tinggiBadan") != null ? pc.getDetails().get("tinggiBadan") : "";
        String status_gizi = pc.getDetails().get("statusGizi") != null ? pc.getDetails().get("statusGizi") : "";
        String gizi = status_gizi.equals("GB") ? "Gizi Buruk" : status_gizi.equals("GK") ? "Gizi Kurang" : status_gizi.equals("GR") ? "Gizi Rendah" : "";
        viewHolder.berat_badan.setText(String.format("%s: %s", context.getString(R.string.str_weight), berat));
        viewHolder.tanggal_kunjungan_anc.setText(String.format("%s %s", context.getString(R.string.date_visit_title), tanggal));
        viewHolder.tinggi.setText(String.format("%s %s", context.getString(R.string.height), tinggi));
        viewHolder.status_gizi.setText(String.format("%s %s", context.getString(R.string.Nutrition_status), gizi));

        AllCommonsRepository childRepository = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());


        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        final CommonPersonObject ibuparent = iburep.findByCaseID(childobject.getColumnmaps().get("relational_id"));
        if (ibuparent != null) {
            detailsRepository.updateDetails(ibuparent);
            String tempat = ibuparent.getDetails().get("tempatBersalin") != null ? ibuparent.getDetails().get("tempatBersalin") : "";
            viewHolder.tempat_lahir.setText(tempat.equals("podok_bersalin_desa") ? "POLINDES" : tempat.equals("pusat_kesehatan_masyarakat_pembantu") ? "Puskesmas pembantu" : tempat.equals("pusat_kesehatan_masyarakat") ? "Puskesmas" : humanize(tempat));

        }

        AllCommonsRepository kirep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if (kiparent != null) {
            detailsRepository.updateDetails(kiparent);

            viewHolder.childs_name.setText(pc.getColumnmaps().get("namaBayi") != null
                    ? pc.getColumnmaps().get("namaBayi").length() > 1
                    ? pc.getColumnmaps().get("namaBayi")
                    : kiparent.getColumnmaps().get("namalengkap") != null ? "By. " + kiparent.getColumnmaps().get("namalengkap") : ""
                    : kiparent.getColumnmaps().get("namalengkap") != null ?
                    "By. " + kiparent.getColumnmaps().get("namalengkap") : ""
            );

            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";
            viewHolder.mother_name.setText(String.format("%s,%s", namaibu, namaayah));
            viewHolder.village_name.setText(kiparent.getDetails().get("address1") != null ? kiparent.getDetails().get("address1") : "");
//            viewHolder.no_ibu.setText(kiparent.getDetails().get("noBayi") != null ? kiparent.getDetails().get("noBayi") : "");
        }

        childBirth(childobject.getColumnmaps().get("tanggalLahirAnak"), viewHolder.childs_age);

        convertView.setLayoutParams(clientViewLayoutParams);
        //   return convertView;
    }

    //    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
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

    void checkVisibility(String immunization1, String immunization2, ImageView no, ImageView yes) {
        if (immunization1 != null || immunization2 != null) {
            no.setVisibility(View.INVISIBLE);
            yes.setVisibility(View.VISIBLE);
        } else {
            no.setVisibility(View.VISIBLE);
            yes.setVisibility(View.INVISIBLE);
        }

    }

    void childBirth(String date, TextView birth) {
        String childAge = date != null ? date : "-";
        if (date != null) {
            String age = childAge.substring(0, childAge.indexOf("T"));
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            LocalDate dates = parse(age, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();

            dates = dates.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);

            int months = Months.monthsBetween(dates, dateNow).getMonths();
            birth.setText(months + " " + context.getString(R.string.month));

        } else {
            birth.setText("-");
        }
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
        return inflater().inflate(R.layout.smart_register_anak_client, null);
    }

    class ViewHolder {

        TextView village_name, tanggal_kunjungan_anc, childs_age, mother_name, childs_name, anak_register_dob;
        TextView tempat_lahir, berat_lahir, tipe_lahir, berat_badan, tinggi, status_gizi;

        LinearLayout profilelayout;
        ImageButton follow_up;

        ImageView hp_badge, hb0_no, hb0_yes, pol1_no, pol1_yes, pol2_no, pol2_yes, pol3_no, pol3_yes;
        ImageView pol4_no, pol4_yes, vitk_no, vitk_yes, campak_no, campak_yes, ivp_no, ivp_yes, profilepic;
    }


}
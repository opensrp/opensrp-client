package org.smartregister.indonesia.anc;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.domain.Alert;
import org.smartregister.indonesia.AllConstantsINA;
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
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class KIANCClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = KIANCClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final OpenSRPImageLoader mImageLoader;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    public KIANCClientsProvider(Context context,
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
        if(convertView.getTag() == null || !(convertView.getTag() instanceof  ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);

            viewHolder.wife_name = (TextView) convertView.findViewById(R.id.wife_name);
            viewHolder.husband_name = (TextView) convertView.findViewById(R.id.txt_husband_name);
            viewHolder.village_name = (TextView) convertView.findViewById(R.id.txt_village_name);
            viewHolder.wife_age = (TextView) convertView.findViewById(R.id.wife_age);
            viewHolder.no_ibu = (TextView) convertView.findViewById(R.id.no_ibu);
            viewHolder.unique_id = (TextView) convertView.findViewById(R.id.unique_id);

            viewHolder.hr_badge = (ImageView) convertView.findViewById(R.id.img_hr_badge);
            viewHolder.img_hrl_badge = (ImageView) convertView.findViewById(R.id.img_hrl_badge);
            viewHolder.bpl_badge = (ImageView) convertView.findViewById(R.id.img_bpl_badge);
            viewHolder.hrp_badge = (ImageView) convertView.findViewById(R.id.img_hrp_badge);
            viewHolder.hrpp_badge = (ImageView) convertView.findViewById(R.id.img_hrpp_badge);
            // ViewHolder.img_hp_badge = ImageView.Set;   img_hrl_badge img_bpl_badge img_hrp_badge img_hrpp_badge
            viewHolder.usia_klinis = (TextView) convertView.findViewById(R.id.txt_usia_klinis);
            viewHolder.htpt = (TextView) convertView.findViewById(R.id.txt_htpt);
            viewHolder.edd_due = (TextView) convertView.findViewById(R.id.txt_edd_due);
            viewHolder.ki_lila_bb = (TextView) convertView.findViewById(R.id.txt_ki_lila_bb);
            viewHolder.beratbadan_tb = (TextView) convertView.findViewById(R.id.txt_ki_beratbadan_tb);
            viewHolder.tanggal_kunjungan_anc = (TextView) convertView.findViewById(R.id.txt_tanggal_kunjungan_anc);
            viewHolder.anc_number = (TextView) convertView.findViewById(R.id.txt_anc_number);
            viewHolder.kunjugan_ke = (TextView) convertView.findViewById(R.id.txt_kunjugan_ke);

            viewHolder.status_layout = (RelativeLayout) convertView.findViewById(R.id.anc_status_layout);
            viewHolder.status_type = (TextView) convertView.findViewById(R.id.txt_status_type);
            viewHolder.status_date = (TextView) convertView.findViewById(R.id.txt_status_date_anc);
            viewHolder.alert_status = (TextView) convertView.findViewById(R.id.txt_alert_status);

            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.woman_placeholder));

        viewHolder.follow_up.setOnClickListener(onClickListener);
        viewHolder.follow_up.setTag(smartRegisterClient);
        viewHolder.follow_up.setImageDrawable(iconPencilDrawable);

        viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (iconPencilDrawable == null) {
            iconPencilDrawable = context.getResources().getDrawable(R.drawable.ic_pencil);
        }

        //set image
        AllCommonsRepository allancRepository =  org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        Map<String, String> details =  detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(ancobject.getColumnmaps());

        if(pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        }else{
            pc.setDetails(details);
        }

        viewHolder.hr_badge.setVisibility(View.INVISIBLE);
        viewHolder.hrp_badge.setVisibility(View.INVISIBLE);
        viewHolder.img_hrl_badge.setVisibility(View.INVISIBLE);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"),pc.getDetails().get("highRiskEctopicPregnancy"),pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"),pc.getDetails().get("highRiskHeartDisorder"),pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"),pc.getDetails().get("highRiskMalaria"),pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"),viewHolder.hr_badge);

        risk(pc.getDetails().get("highRiskPregnancyPIH"),pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"),
                pc.getDetails().get("HighRiskPregnancyTooManyChildren"),
                pc.getDetails().get("highRiskPregnancyDiabetes"),pc.getDetails().get("highRiskPregnancyAnemia"),null,null,null,null,null,viewHolder.hrp_badge);

        risk(pc.getDetails().get("highRiskLabourFetusMalpresentation"),pc.getDetails().get("highRiskLabourFetusSize"),
                pc.getDetails().get("highRisklabourFetusNumber"),pc.getDetails().get("HighRiskLabourSectionCesareaRecord"),
                pc.getDetails().get("highRiskLabourTBRisk"),null,null,null,null,null,viewHolder.img_hrl_badge);

//        final ImageView kiview = (ImageView)convertView.findViewById(R.id.img_profile);
        //start profile image
        viewHolder.profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk

//        if(pc.getColumnmaps().get("_id")!=null){//image already in local storage most likey ):
//            Log.e(TAG, "getView: "+pc.getColumnmaps().get("_id") );
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getColumnmaps().get("_id"), OpenSRPImageLoader.getStaticImageListener(viewHolder.profilepic, R.drawable.woman_placeholder, R.drawable.woman_placeholder));
//        }

        KIDetailActivity.setImagetoHolderFromUri((Activity) context,
                DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                viewHolder.profilepic, R.mipmap.woman_placeholder);


        //end profile image

        viewHolder.wife_name.setText(pc.getColumnmaps().get("namalengkap")!=null?pc.getColumnmaps().get("namalengkap"):"");
        viewHolder.husband_name.setText(pc.getColumnmaps().get("namaSuami")!=null?pc.getColumnmaps().get("namaSuami"):"");
        viewHolder.village_name.setText(pc.getDetails().get("address1")!=null?pc.getDetails().get("address1"):"");
        viewHolder.wife_age.setText(pc.getDetails().get("umur")!=null?pc.getDetails().get("umur"):"");
        viewHolder.no_ibu.setText(pc.getDetails().get("noIbu")!=null?pc.getDetails().get("noIbu"):"");
        viewHolder.unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID)!=null?pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID):"");

        viewHolder.usia_klinis.setText(pc.getDetails().get("usiaKlinis")!=null?context.getString(R.string.usia)+pc.getDetails().get("usiaKlinis")+context.getString(R.string.str_weeks):"-");
        viewHolder.htpt.setText(pc.getDetails().get("htp")!=null?pc.getDetails().get("htp"):"-");

        String edd = pc.getDetails().get("htp");
        if(StringUtils.isNotBlank(pc.getDetails().get("htp"))) {
            String _edd = edd;
            String _dueEdd = "";
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
            LocalDate date = parse(_edd, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();
            date = date.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);
            int months = Months.monthsBetween(dateNow, date).getMonths();
            if(months >= 1) {
                viewHolder.edd_due.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
                _dueEdd = "" + months + " " + context.getString(R.string.months_away);
            } else if(months == 0){
                viewHolder.edd_due.setTextColor(context.getResources().getColor(R.color.light_blue));
                _dueEdd =  context.getString(R.string.this_month);
            }
            else if(months < 0) {
                viewHolder.edd_due.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                _dueEdd = context.getString(R.string.edd_passed);
            }

            viewHolder.edd_due.setText(_dueEdd);

        }
        else{
            viewHolder.edd_due.setText("-");
        }


        viewHolder.ki_lila_bb.setText(pc.getDetails().get("hasilPemeriksaanLILA")!=null?pc.getDetails().get("hasilPemeriksaanLILA"):"-");

        viewHolder.beratbadan_tb.setText(pc.getDetails().get("bbKg")!=null?pc.getDetails().get("bbKg"):"-");

        String AncDate = pc.getDetails().get("ancDate")!=null?pc.getDetails().get("ancDate"):"-";
        String AncKe = pc.getDetails().get("ancKe")!=null?pc.getDetails().get("ancKe"):"-";
        String KunjunganKe = pc.getDetails().get("kunjunganKe")!=null?pc.getDetails().get("kunjunganKe"):"-";

        viewHolder.tanggal_kunjungan_anc.setText(context.getString(R.string.last_visit_date)+ AncDate);
        viewHolder.anc_number.setText(context.getString(R.string.anc_ke) + AncKe);
        viewHolder.kunjugan_ke.setText(context.getString(R.string.visit_number) +KunjunganKe);

        viewHolder.status_type.setText("");
        viewHolder.status_date.setText("");
        viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
        viewHolder.alert_status.setText("");

        if(AncKe.equals("-") || AncKe.equals("")){
            viewHolder.status_type.setText("ANC");
        }
        if(AncKe.equals("1")){
            viewHolder.status_type.setText("ANC 2");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 2");
            //alertlist_for_client.get(i).
            if(alertlist_for_client.size() == 0 ){
                //  viewHolder.due_visit_date.setText("Not Synced to Server");
                viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for(int i = 0;i<alertlist_for_client.size();i++){

                //  viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).isComplete()){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        if(AncKe.equals("2")){
            viewHolder.status_type.setText("ANC 3");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 3");
            //alertlist_for_client.get(i).
            if(alertlist_for_client.size() == 0 ){
                //  viewHolder.due_visit_date.setText("Not Synced to Server");
                viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for(int i = 0;i<alertlist_for_client.size();i++){

                // viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).isComplete()){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }
        if(AncKe.equals("3")){
            viewHolder.status_type.setText("ANC 4");
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ANC 4");
            //alertlist_for_client.get(i).
            if(alertlist_for_client.size() == 0 ){
                //  viewHolder.due_visit_date.setText("Not Synced to Server");
                viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for(int i = 0;i<alertlist_for_client.size();i++){

                //   viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).isComplete()){
                    viewHolder.status_date.setText(alertlist_for_client.get(i).startDate());
                    viewHolder.status_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    viewHolder.alert_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }


        convertView.setLayoutParams(clientViewLayoutParams);
        //   return convertView;
    }
    // CommonPersonObjectController householdelcocontroller;


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

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_ki_anc_client, null);
        return View;
    }

    public void risk (String risk1,String risk2,String risk3,String risk4,String risk5,String risk6,String risk7,String risk8,String risk9,String risk10,ImageView riskview){
        if(risk1 != null && risk1.equals("yes")
                || risk2 != null && risk2.equals("yes")
                || risk3 != null && risk3.equals("yes")
                || risk4 != null && risk4.equals("yes")
                || risk5 != null && risk5.equals("yes")
                || risk6 != null && risk6.equals("yes")
                || risk7 != null && risk7.equals("yes")
                || risk8 != null && risk8.equals("yes")
                || risk9 != null && risk9.equals("yes")
                || risk10 != null && risk10.equals("yes")){

            riskview.setVisibility(View.VISIBLE);
        }

    }
    class ViewHolder {

        TextView wife_name ;
        TextView husband_name ;
        TextView village_name;
        TextView wife_age;
        LinearLayout profilelayout;
        ImageView profilepic;
        TextView gravida;
        Button warnbutton;
        ImageButton follow_up;
        TextView parity;
        TextView number_of_abortus;
        TextView number_of_alive;
        TextView no_ibu;
        TextView unique_id;
        TextView kb_method;
        TextView usia_klinis;
        TextView htpt;
        TextView ki_lila_bb;
        TextView beratbadan_tb;
        TextView anc_penyakit_kronis;
        TextView status_type;
        TextView status_date;
        TextView alert_status;
        RelativeLayout status_layout;
        TextView tanggal_kunjungan_anc;
        TextView anc_number;
        TextView kunjugan_ke;
        ImageView hr_badge  ;
        ImageView hp_badge;
        ImageView hrpp_badge;
        ImageView bpl_badge;
        ImageView hrp_badge;
        ImageView img_hrl_badge;
        TextView edd_due;
    }


}
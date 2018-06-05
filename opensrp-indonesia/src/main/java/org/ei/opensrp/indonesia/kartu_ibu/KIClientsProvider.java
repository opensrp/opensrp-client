package org.ei.opensrp.indonesia.kartu_ibu;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.joda.time.LocalDateTime.parse;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KIClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = KIClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    public KIClientsProvider(Context context,
                             View.OnClickListener onClickListener,
                             AlertService alertService) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));

    }

    @Override
    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {
        ViewHolder viewHolder;

        if (convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);
            viewHolder.wife_name = (TextView) convertView.findViewById(R.id.wife_name);
            viewHolder.husband_name = (TextView) convertView.findViewById(R.id.txt_husband_name);
            viewHolder.village_name = (TextView) convertView.findViewById(R.id.txt_village_name);
            viewHolder.wife_age = (TextView) convertView.findViewById(R.id.wife_age);
            viewHolder.no_ibu = (TextView) convertView.findViewById(R.id.no_ibu);
            viewHolder.unique_id = (TextView) convertView.findViewById(R.id.unique_id);
            viewHolder.gravida = (TextView) convertView.findViewById(R.id.txt_gravida);
            viewHolder.parity = (TextView) convertView.findViewById(R.id.txt_parity);
            viewHolder.number_of_abortus = (TextView) convertView.findViewById(R.id.txt_number_of_abortus);
            viewHolder.number_of_alive = (TextView) convertView.findViewById(R.id.txt_number_of_alive);
            viewHolder.hr_badge = (ImageView) convertView.findViewById(R.id.img_hr_badge);
            viewHolder.img_hrl_badge = (ImageView) convertView.findViewById(R.id.img_hrl_badge);
            viewHolder.bpl_badge = (ImageView) convertView.findViewById(R.id.img_bpl_badge);
            viewHolder.hrp_badge = (ImageView) convertView.findViewById(R.id.img_hrp_badge);
            viewHolder.hrpp_badge = (ImageView) convertView.findViewById(R.id.img_hrpp_badge);
            viewHolder.edd = (TextView) convertView.findViewById(R.id.txt_edd);
            viewHolder.edd_due = (TextView) convertView.findViewById(R.id.txt_edd_due);
            viewHolder.children_age_left = (TextView) convertView.findViewById(R.id.txt_children_age_left);
            viewHolder.children_age_right = (TextView) convertView.findViewById(R.id.txt_children_age_right);

            viewHolder.anc_status_layout = (TextView) convertView.findViewById(R.id.mother_status);
            viewHolder.date_status = (TextView) convertView.findViewById(R.id.last_visit_status);
            viewHolder.visit_status = (TextView) convertView.findViewById(R.id.visit_status);
            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);

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

        // set flag High Risk

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        System.out.println("client : " + pc.getColumnmaps().toString());
        System.out.println("event : " + pc.getDetails().toString());

        //start profile image
        viewHolder.profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
        KIDetailActivity.setImagetoHolderFromUri((Activity) context,
                DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                viewHolder.profilepic, R.mipmap.woman_placeholder);
//        viewHolder.profilepic.setTag(smartRegisterClient);


//        if(pc.getCaseId()!=null){
//            image already in local storage most likey ):
//            set profile image by passing the client id.
//            If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(viewHolder.profilepic, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
//        }
        //end profile image

        viewHolder.wife_name.setText(pc.getColumnmaps().get("namalengkap") != null ? pc.getColumnmaps().get("namalengkap") : "");
        viewHolder.husband_name.setText(pc.getColumnmaps().get("namaSuami") != null ? pc.getColumnmaps().get("namaSuami") : "");
        viewHolder.village_name.setText(pc.getDetails().get("address1") != null ? pc.getDetails().get("address1") : "");
        viewHolder.wife_age.setText(pc.getColumnmaps().get("umur") != null ? pc.getColumnmaps().get("umur") : "");
        viewHolder.no_ibu.setText(pc.getDetails().get("noIbu") != null ? pc.getDetails().get("noIbu") : "");
        viewHolder.unique_id.setText(pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) != null ? pc.getDetails().get(AllConstantsINA.CommonFormFields.UNIQUE_ID) : "");
        viewHolder.gravida.setText(pc.getDetails().get("gravida") != null ? pc.getDetails().get("gravida") : "-");
        viewHolder.parity.setText(pc.getDetails().get("partus") != null ? pc.getDetails().get("partus") : "-");
        viewHolder.number_of_abortus.setText(pc.getDetails().get("abortus") != null ? pc.getDetails().get("abortus") : "-");
        viewHolder.number_of_alive.setText(pc.getDetails().get("hidup") != null ? pc.getDetails().get("hidup") : "-");
        viewHolder.edd.setText(pc.getDetails().get("htp") != null ? pc.getDetails().get("htp") : "");

        viewHolder.edd_due.setText("");
        viewHolder.anc_status_layout.setText("");
        viewHolder.date_status.setText("");
        viewHolder.visit_status.setText("");
        viewHolder.children_age_left.setText("");
        viewHolder.children_age_right.setText("");

        AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");
        final CommonPersonObject ibuparent = iburep.findByCaseID(pc.entityId());
        if (ibuparent != null) {
            short anc_isclosed = ibuparent.getClosed();
            //check anc  status
            if (anc_isclosed == 0) {
                detailsRepository.updateDetails(ibuparent);
                if (pc.getDetails().get("htp") == null) {
                    checkMonth(pc.getDetails().get("htp"), viewHolder.edd_due);

                }
                checkLastVisit(pc.getDetails().get("ancDate"), context.getString(R.string.anc_ke) + ": " + pc.getDetails().get("ancKe"), context.getString(R.string.service_anc),
                        viewHolder.anc_status_layout, viewHolder.date_status, viewHolder.visit_status);
            }
            //if anc is 1(closed) set status to pnc
            else if (anc_isclosed == 1) {
                AllCommonsRepository pncrep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_pnc");
                final CommonPersonObject pncparent = pncrep.findByCaseID(pc.entityId());
                if (pncparent != null) {
                    short pnc_isclosed = pncparent.getClosed();
                    if (pnc_isclosed == 0) {
                        detailsRepository.updateDetails(pncparent);
                  /*  checkMonth("delivered",viewHolder.edd_due);*/
                        viewHolder.edd_due.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
                        String deliver = context.getString(R.string.delivered);
                        viewHolder.edd_due.setText(deliver);
                        checkLastVisit(pc.getDetails().get("PNCDate"), context.getString(R.string.pnc_ke) + " " + pc.getDetails().get("hariKeKF"), context.getString(R.string.service_pnc),
                                viewHolder.anc_status_layout, viewHolder.date_status, viewHolder.visit_status);
                    }
                }

            }
        }
        //last check if mother in PF (KB) service
        else if (!StringUtils.isNumeric(pc.getDetails().get("jenisKontrasepsi"))) {
            checkLastVisit(pc.getDetails().get("tanggalkunjungan"), context.getString(R.string.fp_methods) + ": " + pc.getDetails().get("jenisKontrasepsi"), context.getString(R.string.service_fp),
                    viewHolder.anc_status_layout, viewHolder.date_status, viewHolder.visit_status);
        }


        //anak
        AllCommonsRepository anakrep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        ArrayList<String> list = new ArrayList<>();
        list.add((pc.entityId()));
        List<CommonPersonObject> allchild = anakrep.findByRelational_IDs(list);
        for (int i = 0; i < allchild.size(); i++) {
            CommonPersonObject commonPersonObject = allchild.get(i);
            detailsRepository.updateDetails(commonPersonObject);
            viewHolder.children_age_left.setText(commonPersonObject.getColumnmaps().get("namaBayi") != null ? "Name : " + commonPersonObject.getColumnmaps().get("namaBayi") : "");
            viewHolder.children_age_right.setText(Utils.cleanStrDate(commonPersonObject.getColumnmaps().get("tanggalLahirAnak")));
        }

        viewHolder.hr_badge.setVisibility(View.INVISIBLE);
        viewHolder.hrp_badge.setVisibility(View.INVISIBLE);
        viewHolder.img_hrl_badge.setVisibility(View.INVISIBLE);
        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"), pc.getDetails().get("highRiskEctopicPregnancy"), pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"), pc.getDetails().get("highRiskHeartDisorder"), pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"), pc.getDetails().get("highRiskMalaria"), pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"), viewHolder.hr_badge);

        risk(pc.getDetails().get("highRiskPregnancyPIH"), pc.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition"),
                pc.getDetails().get("HighRiskPregnancyTooManyChildren"),
                pc.getDetails().get("highRiskPregnancyDiabetes"), pc.getDetails().get("highRiskPregnancyAnemia"), null, null, null, null, null, viewHolder.hrp_badge);

        risk(pc.getDetails().get("highRiskLabourFetusMalpresentation"), pc.getDetails().get("highRiskLabourFetusSize"),
                pc.getDetails().get("highRisklabourFetusNumber"), pc.getDetails().get("HighRiskLabourSectionCesareaRecord"),
                pc.getDetails().get("highRiskLabourTBRisk"), null, null, null, null, null, viewHolder.img_hrl_badge);


        convertView.setLayoutParams(clientViewLayoutParams);
        //  return convertView;

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

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = inflater().inflate(R.layout.smart_register_ki_client, null);
        return View;
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

    public void checkLastVisit(String date, String visitNumber, String Status, TextView visitStatus, TextView visitDate, TextView VisitNumber) {
        String visit_stat = "";
        String visit_date = date != null ? context.getString(R.string.date_visit_title) + " " + date : "";

        VisitNumber.setText(visitNumber);
        visitDate.setText(visit_date);
        visitStatus.setText(Status);
    }

    public void checkMonth(String htp, TextView TextMonth) {
        String edd = htp;
        String _edd = edd;
        String _dueEdd = "";
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        if (StringUtils.isNotBlank(htp) && !htp.equals("delivered")) {

            LocalDate date = parse(_edd, formatter).toLocalDate();
            LocalDate dateNow = LocalDate.now();
            date = date.withDayOfMonth(1);
            dateNow = dateNow.withDayOfMonth(1);
            int months = Months.monthsBetween(dateNow, date).getMonths();
            if (months >= 1) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
                _dueEdd = "" + months + " " + context.getString(R.string.months_away);
            } else if (months == 0) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.light_blue));
                _dueEdd = context.getString(R.string.this_month);
            } else if (months < 0) {
                TextMonth.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                _dueEdd = context.getString(R.string.edd_passed);
            }
            TextMonth.setText(_dueEdd);
        }/*else if(htp.equals("delivered")){
            TextMonth.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
            _dueEdd = context.getString(R.string.delivered);
            TextMonth.setText(_dueEdd);
        }*/ else {
            TextMonth.setText("-");
        }

    }


    class ViewHolder {

        TextView wife_name;
        TextView husband_name;
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
        TextView edd;
        TextView edd_due;
        TextView children_age_left;
        TextView anc_status_layout;
        TextView visit_status;
        TextView date_status;
        TextView children_age_right;
        ImageView hr_badge;
        ImageView hrpp_badge;
        ImageView bpl_badge;
        ImageView hrp_badge;
        ImageView img_hrl_badge;
    }


}
package org.ei.opensrp.indonesia.kb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.indonesia.R;

import org.ei.opensrp.indonesia.face.camera.util.FaceConstants;
import org.ei.opensrp.indonesia.kartu_ibu.KIDetailActivity;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.ECProfilePhotoLoader;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.ei.opensrp.view.viewHolder.ProfilePhotoLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.util.StringUtil.humanize;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_DATE_FIELD;
import static org.ei.opensrp.view.controller.ECSmartRegisterController.STATUS_TYPE_FIELD;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KBClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final String TAG = KBClientsProvider.class.getSimpleName();

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    private String photo_path;
    private File tb_photo;

    public KBClientsProvider(Context context,
                             View.OnClickListener onClickListener,
                             AlertService alertService) {
        this.onClickListener = onClickListener;
//        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);

    }

    @Override
    public void getView(SmartRegisterClient smartRegisterClient, View convertView) {

        ViewHolder viewHolder;

        if(convertView.getTag() == null || !(convertView.getTag() instanceof  ViewHolder)){
            viewHolder = new ViewHolder();
            viewHolder.profilelayout =  (LinearLayout)convertView.findViewById(R.id.profile_info_layout);
            viewHolder.wife_name = (TextView)convertView.findViewById(R.id.wife_name);
            viewHolder.husband_name = (TextView)convertView.findViewById(R.id.txt_husband_name);
            viewHolder.village_name = (TextView)convertView.findViewById(R.id.txt_village_name);
            viewHolder.wife_age = (TextView)convertView.findViewById(R.id.wife_age);
            viewHolder.no_ibu = (TextView)convertView.findViewById(R.id.no_ibu);
         //   viewHolder.unique_id = (TextView)convertView.findViewById(R.id.unique_id);

            viewHolder.gravida = (TextView)convertView.findViewById(R.id.txt_gravida);
            viewHolder.parity = (TextView)convertView.findViewById(R.id.txt_parity);
            viewHolder.number_of_abortus = (TextView)convertView.findViewById(R.id.txt_number_of_abortus);
            viewHolder.number_of_alive = (TextView)convertView.findViewById(R.id.txt_number_of_alive);

            viewHolder.hr_badge =(ImageView)convertView.findViewById(R.id.img_hr_badge);
            viewHolder.img_hrl_badge =(ImageView)convertView.findViewById(R.id.img_hrl_badge);
            viewHolder.bpl_badge =(ImageView)convertView.findViewById(R.id.img_bpl_badge);
            viewHolder.hrp_badge =(ImageView)convertView.findViewById(R.id.img_hrp_badge);
            viewHolder.hrpp_badge =(ImageView)convertView.findViewById(R.id.img_hrpp_badge);
            viewHolder.kb_method = (TextView)convertView.findViewById(R.id.kb_method);
            viewHolder.kb_mulai = (TextView)convertView.findViewById(R.id.kb_mulai);
            viewHolder.risk_HB = (TextView)convertView.findViewById(R.id.risk_HB);
            viewHolder.LILA =(TextView)convertView.findViewById(R.id.risk_LILA);

            viewHolder.risk_PenyakitKronis = (TextView)convertView.findViewById(R.id.risk_PenyakitKronis);
            viewHolder.risk_IMS = (TextView)convertView.findViewById(R.id.risk_IMS);

            viewHolder.follow_up_due = (TextView)convertView.findViewById(R.id.follow_due);
            viewHolder.follow_layout = (LinearLayout) convertView.findViewById(R.id. follow_layout);
            viewHolder.follow_status = (TextView) convertView.findViewById(R.id. follow_status);
            viewHolder.follow_due = (TextView) convertView.findViewById(R.id. follow_up_due);

            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton)convertView.findViewById(R.id.btn_edit);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.woman_placeholder));

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

        viewHolder.hr_badge.setVisibility(View.INVISIBLE);

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"),pc.getDetails().get("highRiskEctopicPregnancy"),pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"),pc.getDetails().get("highRiskHeartDisorder"),pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"),pc.getDetails().get("highRiskMalaria"),pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"),viewHolder.hr_badge);


        //set image
        final ImageView kiview = (ImageView)convertView.findViewById(R.id.img_profile);

        photo_path = pc.getDetails().get("profilepic_thumb");
        final int THUMBSIZE = FaceConstants.THUMBSIZE;

        if (photo_path != null) {
            tb_photo = new File(photo_path);
            if (!tb_photo.exists()) {
//                Log.e(TAG, "onCreate: here ");
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.not_found_404));
            } else {
//                Log.e(TAG, "onCreate: here exist " );
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                        BitmapFactory.decodeFile(photo_path),
                        THUMBSIZE, THUMBSIZE);
                kiview.setImageBitmap(ThumbImage);
            }
        } else {

            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.woman_placeholder));

        }

        viewHolder.wife_name.setText(pc.getColumnmaps().get("namalengkap")!=null?pc.getColumnmaps().get("namalengkap"):"");
        viewHolder.husband_name.setText(pc.getColumnmaps().get("namaSuami")!=null?pc.getColumnmaps().get("namaSuami"):"");
        viewHolder.village_name.setText(pc.getDetails().get("address1")!=null?pc.getDetails().get("address1"):"");
        viewHolder.wife_age.setText(pc.getColumnmaps().get("umur")!=null?pc.getColumnmaps().get("umur"):"");
        viewHolder.no_ibu.setText(pc.getDetails().get("noIbu")!=null?pc.getDetails().get("noIbu"):"");

        viewHolder.gravida.setText(pc.getDetails().get("gravida")!=null?pc.getDetails().get("gravida"):"-");
        viewHolder.parity.setText(pc.getDetails().get("partus")!=null?pc.getDetails().get("partus"):"-");
        viewHolder.number_of_abortus.setText(pc.getDetails().get("abortus")!=null?pc.getDetails().get("abortus"):"-");
        viewHolder.number_of_alive.setText(pc.getDetails().get("hidup")!=null?pc.getDetails().get("hidup"):"-");

        viewHolder.kb_method.setText(pc.getDetails().get("jenisKontrasepsi")!=null?pc.getDetails().get("jenisKontrasepsi"):"");
        viewHolder.kb_mulai.setText(pc.getDetails().get("tanggalkunjungan")!=null?pc.getDetails().get("tanggalkunjungan"):"");
        viewHolder.risk_HB.setText(pc.getDetails().get("alkihb")!=null?pc.getDetails().get("alkihb"):"-");
        viewHolder.LILA.setText(pc.getDetails().get("alkilila")!=null?pc.getDetails().get("alkilila"):"-");
       viewHolder.risk_IMS.setText(pc.getDetails().get("alkiPenyakitIms")!=null?pc.getDetails().get("alkiPenyakitIms"):"");
//        viewHolder.follow_up_due.setText(pc.getDetails().get("tanggalLahirAnak")!=null?pc.getDetails().get("tanggalLahirAnak"):"");
       viewHolder.risk_PenyakitKronis.setText(pc.getDetails().get("alkiPenyakitKronis")!=null?pc.getDetails().get("alkiPenyakitKronis"):"");

        viewHolder.hrp_badge.setVisibility(View.INVISIBLE);
        viewHolder.img_hrl_badge.setVisibility(View.INVISIBLE);

/*        AllCommonsRepository iburep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ibu");
        if(pc.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(pc.getColumnmaps().get("ibu.id"));*//*

            //Risk flag
            if (ibuparent.getDetails().get("highRiskPregnancyPIH") != null && ibuparent.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || pc.getDetails().get("highRiskPregnancyPIH") != null && pc.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null && ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition").equals("yes")
                    || pc.getDetails().get("HighRiskPregnancyTooManyChildren") != null && pc.getDetails().get("HighRiskPregnancyTooManyChildren").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyDiabetes") != null && ibuparent.getDetails().get("highRiskPregnancyDiabetes").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyAnemia") != null && ibuparent.getDetails().get("highRiskPregnancyAnemia").equals("yes")) {
                viewHolder.hrp_badge.setVisibility(View.VISIBLE);
            }
            if (ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") != null && ibuparent.getDetails().get("highRiskLabourFetusMalpresentation").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourFetusSize") != null && ibuparent.getDetails().get("highRiskLabourFetusSize").equals("yes")
                    || ibuparent.getDetails().get("highRisklabourFetusNumber") != null && ibuparent.getDetails().get("highRisklabourFetusNumber").equals("yes")
                    || pc.getDetails().get("HighRiskLabourSectionCesareaRecord") != null && pc.getDetails().get("HighRiskLabourSectionCesareaRecord").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourTBRisk") != null && ibuparent.getDetails().get("highRiskLabourTBRisk").equals("yes")) {
                viewHolder.img_hrl_badge.setVisibility(View.VISIBLE);
            }
        }*/

        viewHolder.follow_due.setText("");
        viewHolder.follow_up_due.setText("");
        viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
        viewHolder.follow_status.setText("");

        String jenis = pc.getDetails().get("jenisKontrasepsi")!=null?pc.getDetails().get("jenisKontrasepsi"):"-";
        if(jenis.equals("suntik")){
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "KB Injection Cyclofem");
            //alertlist_for_client.get(i).
            if(alertlist_for_client.size() == 0 ){
               // viewHolder.follow_up_due.setText("Not Synced");
                viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for(int i = 0;i<alertlist_for_client.size();i++){
                viewHolder.follow_due.setText("Follow up due");
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                    viewHolder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
                    viewHolder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                    viewHolder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
                    viewHolder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                    viewHolder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
                    viewHolder.follow_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                    viewHolder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
                    viewHolder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).isComplete()){
                    viewHolder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewHolder.follow_layout.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
                    viewHolder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
            }
        }

        convertView.setLayoutParams(clientViewLayoutParams);
     //   return convertView;
    }
    CommonPersonObjectController householdelcocontroller;


    //    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
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
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_kb_client, null);
        return View;
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
        TextView risk_HB;
        TextView LILA;
        TextView risk_PenyakitKronis;
        TextView risk_IMS;
        TextView follow_up_due;
        TextView kb_mulai;
        ImageView hr_badge;
        ImageView hrpp_badge;
        ImageView bpl_badge;
        ImageView hrp_badge;
        ImageView img_hrl_badge;
        LinearLayout follow_layout;
        TextView follow_status;
        TextView follow_due;
    }


}
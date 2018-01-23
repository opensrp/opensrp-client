package org.smartregister.indonesia.kb;

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
import android.widget.TextView;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonPersonObjectController;
import org.smartregister.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.smartregister.domain.Alert;
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
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Dimas Ciputra on 2/16/15.
 */
public class KBClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private final OpenSRPImageLoader mImageLoader;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    public KBClientsProvider(Context context,
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

        if(convertView.getTag() == null || !(convertView.getTag() instanceof  ViewHolder)){
            viewholder = new ViewHolder();
            viewholder.profilelayout =  (LinearLayout)convertView.findViewById(R.id.profile_info_layout);
            viewholder.wife_name = (TextView)convertView.findViewById(R.id.wife_name);
            viewholder.husband_name = (TextView)convertView.findViewById(R.id.txt_husband_name);
            viewholder.village_name = (TextView)convertView.findViewById(R.id.txt_village_name);
            viewholder.wife_age = (TextView)convertView.findViewById(R.id.wife_age);
            viewholder.no_ibu = (TextView)convertView.findViewById(R.id.no_ibu);
         //   viewholder.unique_id = (TextView)convertView.findViewById(R.id.unique_id);

            viewholder.gravida = (TextView)convertView.findViewById(R.id.txt_gravida);
            viewholder.parity = (TextView)convertView.findViewById(R.id.txt_parity);
            viewholder.number_of_abortus = (TextView)convertView.findViewById(R.id.txt_number_of_abortus);
            viewholder.number_of_alive = (TextView)convertView.findViewById(R.id.txt_number_of_alive);

            viewholder.hr_badge =(ImageView)convertView.findViewById(R.id.img_hr_badge);
            viewholder.img_hrl_badge =(ImageView)convertView.findViewById(R.id.img_hrl_badge);
            viewholder.bpl_badge =(ImageView)convertView.findViewById(R.id.img_bpl_badge);
            viewholder.hrp_badge =(ImageView)convertView.findViewById(R.id.img_hrp_badge);
            viewholder.hrpp_badge =(ImageView)convertView.findViewById(R.id.img_hrpp_badge);
            viewholder.kb_method = (TextView)convertView.findViewById(R.id.kb_method);
            viewholder.kb_mulai = (TextView)convertView.findViewById(R.id.kb_mulai);
            viewholder.risk_HB = (TextView)convertView.findViewById(R.id.risk_HB);
            viewholder.LILA =(TextView)convertView.findViewById(R.id.risk_LILA);

            viewholder.risk_PenyakitKronis = (TextView)convertView.findViewById(R.id.risk_PenyakitKronis);
            viewholder.risk_IMS = (TextView)convertView.findViewById(R.id.risk_IMS);

            viewholder.follow_up_due = (TextView)convertView.findViewById(R.id.follow_due);
            viewholder.follow_layout = (LinearLayout) convertView.findViewById(R.id. follow_layout);
            viewholder.follow_status = (TextView) convertView.findViewById(R.id. follow_status);
            viewholder.follow_due = (TextView) convertView.findViewById(R.id. follow_up_due);

            viewholder.profilepic =(ImageView) convertView.findViewById(R.id.img_profile);
            viewholder.follow_up = (ImageButton) convertView.findViewById(R.id.btn_edit);
            convertView.setTag(viewholder);
        }else{
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

        viewholder.hr_badge.setVisibility(View.INVISIBLE);

        DetailsRepository detailsRepository = org.smartregister.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        //Risk flag
        risk(pc.getDetails().get("highRiskSTIBBVs"),pc.getDetails().get("highRiskEctopicPregnancy"),pc.getDetails().get("highRiskCardiovascularDiseaseRecord"),
                pc.getDetails().get("highRiskDidneyDisorder"),pc.getDetails().get("highRiskHeartDisorder"),pc.getDetails().get("highRiskAsthma"),
                pc.getDetails().get("highRiskTuberculosis"),pc.getDetails().get("highRiskMalaria"),pc.getDetails().get("highRiskPregnancyYoungMaternalAge"),
                pc.getDetails().get("highRiskPregnancyOldMaternalAge"),viewholder.hr_badge);


        //set image
//        final ImageView kiview = (ImageView)convertView.findViewById(R.id.img_profile);
        //start profile image
        viewholder.profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk
//        if(pc.getCaseId()!=null){//image already in local storage most likey ):
//            set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
//            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.getCaseId(), OpenSRPImageLoader.getStaticImageListener(viewholder.profilepic, R.mipmap.woman_placeholder, R.mipmap.woman_placeholder));
//        }

        KIDetailActivity.setImagetoHolderFromUri((Activity) context,
                DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                viewholder.profilepic, R.mipmap.woman_placeholder);

        //end profile image

        viewholder.wife_name.setText(pc.getColumnmaps().get("namalengkap")!=null?pc.getColumnmaps().get("namalengkap"):"");
        viewholder.husband_name.setText(pc.getColumnmaps().get("namaSuami")!=null?pc.getColumnmaps().get("namaSuami"):"");
        viewholder.village_name.setText(pc.getDetails().get("address1")!=null?pc.getDetails().get("address1"):"");
        viewholder.wife_age.setText(pc.getColumnmaps().get("umur")!=null?pc.getColumnmaps().get("umur"):"");
        viewholder.no_ibu.setText(pc.getDetails().get("noIbu")!=null?pc.getDetails().get("noIbu"):"");

        viewholder.gravida.setText(pc.getDetails().get("gravida")!=null?pc.getDetails().get("gravida"):"-");
        viewholder.parity.setText(pc.getDetails().get("partus")!=null?pc.getDetails().get("partus"):"-");
        viewholder.number_of_abortus.setText(pc.getDetails().get("abortus")!=null?pc.getDetails().get("abortus"):"-");
        viewholder.number_of_alive.setText(pc.getDetails().get("hidup")!=null?pc.getDetails().get("hidup"):"-");

        viewholder.kb_method.setText(pc.getDetails().get("jenisKontrasepsi")!=null?pc.getDetails().get("jenisKontrasepsi"):"");
        viewholder.kb_mulai.setText(pc.getDetails().get("tanggalkunjungan")!=null?pc.getDetails().get("tanggalkunjungan"):"");
        viewholder.risk_HB.setText(pc.getDetails().get("alkihb")!=null?pc.getDetails().get("alkihb"):"-");
        viewholder.LILA.setText(pc.getDetails().get("alkilila")!=null?pc.getDetails().get("alkilila"):"-");
       viewholder.risk_IMS.setText(pc.getDetails().get("alkiPenyakitIms")!=null?pc.getDetails().get("alkiPenyakitIms"):"");
//        viewholder.follow_up_due.setText(pc.getDetails().get("tanggalLahirAnak")!=null?pc.getDetails().get("tanggalLahirAnak"):"");
       viewholder.risk_PenyakitKronis.setText(pc.getDetails().get("alkiPenyakitKronis")!=null?pc.getDetails().get("alkiPenyakitKronis"):"");

        viewholder.hrp_badge.setVisibility(View.INVISIBLE);
        viewholder.img_hrl_badge.setVisibility(View.INVISIBLE);

/*        AllCommonsRepository iburep = org.smartregister.Context.getInstance().allCommonsRepositoryobjects("ibu");
        if(pc.getColumnmaps().get("ibu.id") != null) {
            final CommonPersonObject ibuparent = iburep.findByCaseID(pc.getColumnmaps().get("ibu.id"));*//*

            //Risk flag
            if (ibuparent.getDetails().get("highRiskPregnancyPIH") != null && ibuparent.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || pc.getDetails().get("highRiskPregnancyPIH") != null && pc.getDetails().get("highRiskPregnancyPIH").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition") != null && ibuparent.getDetails().get("highRiskPregnancyProteinEnergyMalnutrition").equals("yes")
                    || pc.getDetails().get("HighRiskPregnancyTooManyChildren") != null && pc.getDetails().get("HighRiskPregnancyTooManyChildren").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyDiabetes") != null && ibuparent.getDetails().get("highRiskPregnancyDiabetes").equals("yes")
                    || ibuparent.getDetails().get("highRiskPregnancyAnemia") != null && ibuparent.getDetails().get("highRiskPregnancyAnemia").equals("yes")) {
                viewholder.hrp_badge.setVisibility(View.VISIBLE);
            }
            if (ibuparent.getDetails().get("highRiskLabourFetusMalpresentation") != null && ibuparent.getDetails().get("highRiskLabourFetusMalpresentation").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourFetusSize") != null && ibuparent.getDetails().get("highRiskLabourFetusSize").equals("yes")
                    || ibuparent.getDetails().get("highRisklabourFetusNumber") != null && ibuparent.getDetails().get("highRisklabourFetusNumber").equals("yes")
                    || pc.getDetails().get("HighRiskLabourSectionCesareaRecord") != null && pc.getDetails().get("HighRiskLabourSectionCesareaRecord").equals("yes")
                    || ibuparent.getDetails().get("highRiskLabourTBRisk") != null && ibuparent.getDetails().get("highRiskLabourTBRisk").equals("yes")) {
                viewholder.img_hrl_badge.setVisibility(View.VISIBLE);
            }
        }*/

        viewholder.follow_due.setText("");
        viewholder.follow_up_due.setText("");
        viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
        viewholder.follow_status.setText("");

        String jenis = pc.getDetails().get("jenisKontrasepsi")!=null?pc.getDetails().get("jenisKontrasepsi"):"-";
        if(jenis.equals("suntik")){
            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "KB Injection Cyclofem");
            //alertlist_for_client.get(i).
            if(alertlist_for_client.size() == 0 ){
               // viewholder.follow_up_due.setText("Not Synced");
                viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            }
            for(int i = 0;i<alertlist_for_client.size();i++){
                viewholder.follow_due.setText("Follow up due");
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                    viewholder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewholder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                    viewholder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_upcoming_light_blue));
                    viewholder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                    viewholder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.alert_urgent_red));
                    viewholder.follow_status.setText(alertlist_for_client.get(i).status().value());

                }
                if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                    viewholder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.client_list_header_dark_grey));
                    viewholder.follow_status.setText(alertlist_for_client.get(i).status().value());
                }
                if(alertlist_for_client.get(i).isComplete()){
                    viewholder.follow_up_due.setText(alertlist_for_client.get(i).expiryDate());
                    viewholder.follow_layout.setBackgroundColor(context.getResources().getColor(org.smartregister.R.color.status_bar_text_almost_white));
                    viewholder.follow_status.setText(alertlist_for_client.get(i).status().value());
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
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, View view) {
        getView(smartRegisterClient, view);
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
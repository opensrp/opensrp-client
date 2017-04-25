package org.ei.opensrp.ddtk.ddtk;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.ddtk.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;


import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class FormulirDdtkSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    public FormulirDdtkSmartClientsProvider(Context context,
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
            viewHolder.nama_anak = (TextView)convertView.findViewById(R.id.text_nama_anak);
            viewHolder.jenis_kelamin = (TextView)convertView.findViewById(R.id.text_jenis_kelamin);
            viewHolder.umur = (TextView)convertView.findViewById(R.id.text_umur);
            viewHolder.nama_ibu = (TextView)convertView.findViewById(R.id.text_nama_ibu);

            //antro
            viewHolder.anthropometry_date = (TextView)convertView.findViewById(R.id.text_anthropometry_date);
            viewHolder.berat = (TextView)convertView.findViewById(R.id.text_berat);
            viewHolder.tinggi = (TextView)convertView.findViewById(R.id.text_tinggi);
            viewHolder.lingkar_kepala = (TextView)convertView.findViewById(R.id.text_lingkar_kepala);
            viewHolder.kpsp_test_date1 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.status_kembang1 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.status_kembang2 = (TextView)convertView.findViewById(R.id.text_status_kembang2);
            viewHolder.status_kembang3 = (TextView)convertView.findViewById(R.id.text_status_kembang3);


            viewHolder.hear_test_date = (TextView)convertView.findViewById(R.id.text_hear_test_date);
            viewHolder.daya_dengar = (TextView)convertView.findViewById(R.id.text_daya_dengar);
            viewHolder.sight_test_date = (TextView)convertView.findViewById(R.id.text_sight_test_date);
            viewHolder.text_daya_lihat = (TextView)convertView.findViewById(R.id.text_daya_lihat);


            viewHolder.mental_test_date = (TextView)convertView.findViewById(R.id.text_mental_test_date);
            viewHolder.mental_emosional = (TextView)convertView.findViewById(R.id.text_mental_emosional);

            viewHolder.gpph_test_date = (TextView)convertView.findViewById(R.id.text_gpph_test_date);
            viewHolder.gpph = (TextView)convertView.findViewById(R.id.text_gpph);

            viewHolder.autis = (TextView)convertView.findViewById(R.id.text_autis);
            viewHolder.autis_test_date = (TextView)convertView.findViewById(R.id.text_autis_test_date);


            viewHolder.riskFlag[0] = (ImageView)convertView.findViewById(R.id.riskFlag01);
            viewHolder.riskFlag[1] = (ImageView)convertView.findViewById(R.id.riskFlag02);
            viewHolder.riskFlag[2] = (ImageView)convertView.findViewById(R.id.riskFlag03);


        /*    viewHolder.status_kembang2 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.kpsp_test_date2 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.status_kembang3 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.kpsp_test_date3 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.status_kembang4 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.kpsp_test_date4 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.status_kembang5 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.kpsp_test_date5 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.status_kembang6 = (TextView)convertView.findViewById(R.id.text_status_kembang);
            viewHolder.kpsp_test_date6 = (TextView)convertView.findViewById(R.id.text_kpsp_test_date);
            viewHolder.daya_dengar = (TextView)convertView.findViewById(R.id.text_daya_dengar);
            viewHolder.daya_lihat = (TextView)convertView.findViewById(R.id.text_daya_lihat);
            viewHolder.mental_emosional = (TextView)convertView.findViewById(R.id.text_mental_emosional);
            viewHolder.autis = (TextView)convertView.findViewById(R.id.text_autis);
            viewHolder.gpph = (TextView)convertView.findViewById(R.id.text_gpph);
            viewHolder.anthropometry_date = (TextView)convertView.findViewById(R.id.text_anthropometry_date);
            viewHolder.hear_test_date = (TextView)convertView.findViewById(R.id.text_hear_test_date);
            viewHolder.sight_test_date = (TextView)convertView.findViewById(R.id.text_sight_test_date);
            viewHolder.mental_test_date = (TextView)convertView.findViewById(R.id.text_mental_test_date);
            viewHolder.autis_test_date = (TextView)convertView.findViewById(R.id.text_autis_test_date);
            viewHolder.gpph_test_date = (TextView)convertView.findViewById(R.id.text_gpph_test_date);*/
            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton)convertView.findViewById(R.id.btn_edit);
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
        // viewHolder.follow_up.setTag(client);

        /*
      //  List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "FW CENSUS");

*/
        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);
        //set image picture
        final ImageView childview = (ImageView)convertView.findViewById(R.id.img_profile);
 /*       if (pc.getDetails().get("profilepic") != null) {
            HomeInventoryDetailActivity.setImagetoHolderFromUri((Activity) context, pc.getDetails().get("profilepic"), childview, R.drawable.child_boy_infant);
            childview.setTag(smartRegisterClient);
        }
        else {
            if(pc.getDetails().get("gender") != null && pc.getDetails().get("gender").equals("male")) {
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.child_boy_infant));
            }
            else if(pc.getDetails().get("gender") != null && pc.getDetails().get("gender").equals("laki")) {
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.child_boy_infant));
            }
            else
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.child_girl_infant));
        }*/

        //viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.child_boy_infant));
        //viewHolder.village.setText(pc.getDetails().get("village") != null ? pc.getDetails().get("village") : "");
        viewHolder.nama_anak.setText(pc.getDetails().get("namaBayi") != null ? pc.getDetails().get("namaBayi").replaceAll("_", " ") : "-");
        String ages = pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));
        viewHolder.umur.setText(pc.getDetails().get("tanggalLahirAnak") != null ? Integer.toString(monthRangeToToday(ages))+"B" : "");
        viewHolder.jenis_kelamin.setText(pc.getDetails().get("gender")!=null?pc.getDetails().get("gender") :"-");

        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            viewHolder.nama_ibu.setText(namaibu + "," + namaayah);
         //   viewHolder.village_name.setText(kiparent.getDetails().get("address1")!=null?kiparent.getDetails().get("address1") :"-");
        }



       viewHolder.berat.setText("Berat: "+ (pc.getDetails().get("berat")!=null?pc.getDetails().get("berat").replaceAll("_", " "):"-"));
        viewHolder.tinggi.setText("Tinggi: "+ (pc.getDetails().get("tinggi") != null ? pc.getDetails().get("tinggi").replaceAll("_", " ") : "-"));
        viewHolder.lingkar_kepala.setText("Lingkar Kepala: "+ (pc.getDetails().get("lingkar_kepala")!=null?pc.getDetails().get("lingkar_kepala").replaceAll("_", " "):"-"));
        viewHolder.anthropometry_date.setText("Tanggal: "+ (pc.getDetails().get("anthropometry_date")!=null?pc.getDetails().get("anthropometry_date").replaceAll("_", " "):"-"));

        //kpsp
        viewHolder.kpsp_test_date1.setText(pc.getDetails().get("kpsp_1thn_date")!=null?pc.getDetails().get("kpsp_1thn_date").replaceAll("_", " "):"-");
        viewHolder.status_kembang1.setText(pc.getDetails().get("status_kembang")!=null?"1 thn: "+pc.getDetails().get("status_kembang"):"-");
        viewHolder.status_kembang2.setText(pc.getDetails().get("status_kembang2")!=null?"2 thn: "+pc.getDetails().get("status_kembang2"):"-");
        viewHolder.status_kembang3.setText(pc.getDetails().get("status_kembang3")!=null?"3 thn: "+pc.getDetails().get("status_kembang3"):"-");


        viewHolder.hear_test_date.setText("Tgl: "+ (pc.getDetails().get("hear_test_date")!=null?pc.getDetails().get("hear_test_date").replaceAll("_", " "):"-"));
        viewHolder.daya_dengar.setText("Tes Hearing: "+ (pc.getDetails().get("daya_dengar")!=null?pc.getDetails().get("daya_dengar").replaceAll("_", " "):"-"));
        viewHolder.sight_test_date.setText("Tgl: "+ (pc.getDetails().get("sight_test_date")!=null?pc.getDetails().get("sight_test_date").replaceAll("_", " "):"-"));
        viewHolder.text_daya_lihat.setText("Tes Sight: "+ (pc.getDetails().get("daya_lihat")!=null?pc.getDetails().get("daya_lihat").replaceAll("_", " "):"-"));

        viewHolder.mental_emosional.setText("Tes Mental: "+ (pc.getDetails().get("mental_emosional")!=null?pc.getDetails().get("mental_emosional").replaceAll("_", " "):"-"));
        viewHolder.mental_test_date.setText("Tgl: "+ (pc.getDetails().get("mental_test_date")!=null?pc.getDetails().get("mental_test_date").replaceAll("_", " "):"-"));


        viewHolder.gpph_test_date.setText("Tgl: "+ (pc.getDetails().get("gpph_test_date")!=null?pc.getDetails().get("gpph_test_date").replaceAll("_", " "):"-"));
        viewHolder.gpph.setText("Tes GGPH: "+ (pc.getDetails().get("gpph")!=null?pc.getDetails().get("gpph").replaceAll("_", " "):"-"));

        viewHolder.autis_test_date.setText("Tgl: "+ (pc.getDetails().get("autis_test_date")!=null?pc.getDetails().get("autis_test_date").replaceAll("_", " "):"-"));
        viewHolder.autis.setText("Test Autist: "+ (pc.getDetails().get("autis")!=null ? pc.getDetails().get("autis").replaceAll("_", " "):"-"));

        int baselinecount = 0;
        for (int i = 1 ; i <=45 ; i++){
            String home = "home"+i+"_it";
            if(pc.getDetails().get(home) !=null) {
                if (pc.getDetails().get(home).equalsIgnoreCase("Yes")) {
                    baselinecount = baselinecount + 1;
                } else {

                }
            }

        }

        int _endlinecount = 0;
        for (int i = 1 ; i <=45 ; i++){
            String home = "home"+i+"_it";
            if(pc.getDetails().get(home) !=null) {
                if (pc.getDetails().get(home).equalsIgnoreCase("Yes")) {
                    _endlinecount = baselinecount + 1;
                } else {

                }
            }

        }

        int counter=0;
        if(isLowHomeScore(baselinecount,_endlinecount)){
            viewHolder.riskFlag[counter].setImageResource(R.drawable.risk_h);
            counter++;
        }
        if(pc.getDetails().get("bgm") != null && pc.getDetails().get("garis_kuning") != null){
            if(isMalnourished(pc.getDetails().get("bgm").toLowerCase().contains("y"),pc.getDetails().get("garis_kuning").toLowerCase().contains("y"))){
                viewHolder.riskFlag[counter].setImageResource(R.drawable.risk_m);
            }
        }


/*
        viewHolder.kpsp_test_date2.setText(pc.getDetails().get("kpsp_test_date2")!=null?pc.getDetails().get("kpsp_test_date2").replaceAll("_", " "):"-");
        viewHolder.status_kembang1.setText(pc.getDetails().get("status_kembang2")!=null?pc.getDetails().get("status_kembang2").replaceAll("_", " "):"-");
        viewHolder.kpsp_test_date3.setText(pc.getDetails().get("kpsp_test_date3")!=null?pc.getDetails().get("kpsp_test_date3").replaceAll("_", " "):"-");
        viewHolder.status_kembang3.setText(pc.getDetails().get("status_kembang3")!=null?pc.getDetails().get("status_kembang3").replaceAll("_", " "):"-");
        viewHolder.kpsp_test_date4.setText(pc.getDetails().get("kpsp_test_date4")!=null?pc.getDetails().get("kpsp_test_date4").replaceAll("_", " "):"-");
        viewHolder.status_kembang4.setText(pc.getDetails().get("status_kembang4")!=null?pc.getDetails().get("status_kembang4").replaceAll("_", " "):"-");
        viewHolder.kpsp_test_date5.setText(pc.getDetails().get("kpsp_test_date5")!=null?pc.getDetails().get("kpsp_test_date5").replaceAll("_", " "):"-");
        viewHolder.status_kembang5.setText(pc.getDetails().get("status_kembang5")!=null?pc.getDetails().get("status_kembang5").replaceAll("_", " "):"-");
        viewHolder.kpsp_test_date6.setText(pc.getDetails().get("kpsp_test_date6")!=null?pc.getDetails().get("kpsp_test_date6").replaceAll("_", " "):"-");
        viewHolder.status_kembang6.setText(pc.getDetails().get("status_kembang6")!=null?pc.getDetails().get("status_kembang6").replaceAll("_", " "):"-");

        viewHolder.daya_dengar.setText("Tes Hearing: "+ (pc.getDetails().get("daya_dengar") != null ? pc.getDetails().get("daya_dengar").replaceAll("_", " ") : "-"));
        viewHolder.daya_lihat.setText("Tes Visual: "+ (pc.getDetails().get("daya_lihat")!=null?pc.getDetails().get("daya_lihat").replaceAll("_", " "):"-"));
        viewHolder.mental_emosional.setText("Tes Mental: "+ (pc.getDetails().get("mental_emosional")!=null?pc.getDetails().get("mental_emosional").replaceAll("_", " "):"-"));
        viewHolder.autis.setText("Test Autist: "+ (pc.getDetails().get("autis")!=null ? pc.getDetails().get("autis").replaceAll("_", " "):"-"));
        viewHolder.gpph.setText("Tes GGPH: "+ (pc.getDetails().get("gpph")!=null?pc.getDetails().get("gpph").replaceAll("_", " "):"-"));
        viewHolder.anthropometry_date.setText("Tanggal: "+ (pc.getDetails().get("anthropometry_date")!=null ? pc.getDetails().get("anthropometry_date").replaceAll("_", " "):"-"));
        viewHolder.sight_test_date.setText("Tanggal: "+ (pc.getDetails().get("sight_test_date")!=null?pc.getDetails().get("sight_test_date").replaceAll("_", " "):"-"));
        viewHolder.mental_test_date.setText("Tanggal: "+ (pc.getDetails().get("mental_test_date")!=null?pc.getDetails().get("mental_test_date").replaceAll("_", " "):"-"));
        viewHolder.autis_test_date.setText("Tanggal: "+ (pc.getDetails().get("autis_test_date")!=null?pc.getDetails().get("autis_test_date").replaceAll("_", " "):"-"));
        viewHolder.gpph_test_date.setText("Tanggal: "+ (pc.getDetails().get("gpph_test_date")!=null?pc.getDetails().get("gpph_test_date").replaceAll("_", " "):"-"));
        viewHolder.kpsp_test_date1.setText("Tanggal: "+ (pc.getDetails().get("kpsp_test_date1")!=null?pc.getDetails().get("kpsp_test_date1").replaceAll("_", " "):"-"));
        viewHolder.status_kembang1.setText("Status Kembang: "+ (pc.getDetails().get("status_kembang1")!=null?pc.getDetails().get("status_kembang1").replaceAll("_", " "):"-"));
        viewHolder.hear_test_date.setText("Tanggal: "+ (pc.getDetails().get("hear_test_date")!=null?pc.getDetails().get("hear_test_date").replaceAll("_", " "):"-"));
      //  viewHolder.headofhouseholdname.setText(pc.getDetails().get("FWHOHFNAME")!=null?pc.getDetails().get("FWHOHFNAME"):"");
      //  viewHolder.no_of_mwra.setText(pc.getDetails().get("ELCO")!=null?pc.getDetails().get("ELCO"):"");
       // Date lastdate = null;*/



        convertView.setLayoutParams(clientViewLayoutParams);
    }
    CommonPersonObjectController householdelcocontroller;





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
        View View = inflater().inflate(R.layout.smart_register_ddtk_client, null);
        return View;
    }
    private int monthRangeToToday(String lastVisitDate){
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new java.util.Date()).substring(0,7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0,4)))*12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5,7))));
    }


    // ---------------------------- RISK FLAG HANDLER -------------------------------------------

    // ------------------------ RISK FLAG CLASSIFICATION ----------------------------------------

    private boolean isPrimigravida(String gravida){
        return Integer.parseInt(gravida)==1;
    }

    private boolean isLowEducated(String education){
        return !education.toLowerCase().contains("tinggi");
    }

    private boolean isTooManyChildren(String children){
        return Integer.parseInt(children)>3;
    }

    private boolean isTooYoungMother(String birthDate){
        return age(birthDate)<20;
    }

    private boolean isTooYoungMother(int age){
        return age<20;
    }

    private boolean isMalnourished(boolean bgm, boolean yellow){
        return bgm || yellow;
    }

    private boolean isLowHomeScore(CommonPersonObjectClient pc){
        int baselineCount_it = 0, baselineCount_ec = 0;
        for(int i=1;i<=45;i++){
            if(pc.getDetails().get("home"+i+"_it") != null){
                if(pc.getDetails().get("home"+i+"_it").toLowerCase().contains("yes"))
                    baselineCount_it++;
            }
            if(pc.getDetails().get("home"+i+"_ec") != null) {
                if (pc.getDetails().get("home" + i + "_ec").toLowerCase().contains("yes"))
                    baselineCount_ec++;
            }
        }

        return isLowHomeScore(baselineCount_it,baselineCount_ec);

    }

    private boolean isLowHomeScore(int baselineCount_it, int baselineCount_ec){
        return ((baselineCount_it>0 && baselineCount_it<22) || (baselineCount_ec>0 && baselineCount_ec<34));
    }

    private int age(String date){
        if(date.toLowerCase().contains("t"))
            date = date.substring(0,10);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return  (Integer.parseInt(today.substring(0,4)) - Integer.parseInt(date.substring(0,4))) -
                (Integer.parseInt(today.substring(5,7)) - Integer.parseInt(date.substring(5,7))<0 ? 1 : 0);
    }

    // ------------------------------- HIGH RISK FLAG MANAGER -------------------------------------

    private void flagColor(){

    }



    class ViewHolder {

        TextView today ;
        TextView umur;
        TextView village;
        TextView husbandname;
        LinearLayout profilelayout;
        LinearLayout antrolayout;
        ImageView profilepic;
        FrameLayout due_date_holder;
        Button warnbutton;
        ImageButton follow_up;
        TextView nama_anak;
        TextView jenis_kelamin;
        TextView nama_ibu;
        TextView berat;
        TextView tinggi;
        TextView lingkar_kepala;
        TextView status_kembang1;
        TextView status_kembang2;
        TextView status_kembang3;
        TextView status_kembang4;
        TextView status_kembang5;
        TextView status_kembang6;
        TextView daya_dengar;
        TextView daya_lihat;
        TextView mental_emosional;
        TextView autis;
        TextView gpph;
        TextView anthropometry_date;
        TextView hear_test_date;
        TextView sight_test_date;
        TextView mental_test_date;
        TextView autis_test_date;
        TextView kpsp_test_date1;
        TextView kpsp_test_date2;
        TextView kpsp_test_date3;
        TextView kpsp_test_date4;
        TextView kpsp_test_date5;
        TextView kpsp_test_date6;
        TextView gpph_test_date;
        TextView village_name;
        TextView tgl_kpsp;
        ImageView riskFlag[] = new ImageView[3];

        public TextView text_daya_lihat;
    }


}


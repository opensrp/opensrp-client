package org.ei.opensrp.ddtk.homeinventory;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
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
import org.ei.opensrp.ddtk.R;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
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
public class HomeInventoryClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    public HomeInventoryClientsProvider(Context context,
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
            viewHolder.name = (TextView)convertView.findViewById(R.id.txt_child_name);
            viewHolder.fatherName = (TextView) convertView.findViewById(R.id.ParentName);
            viewHolder.subVillage = (TextView) convertView.findViewById(R.id.txt_child_subVillage);
            viewHolder.age = (TextView)convertView.findViewById(R.id.txt_child_age);
            viewHolder.tgl_lahr = (TextView)convertView.findViewById(R.id.txt_child_date_of_birth);

            viewHolder.base_date = (TextView)convertView.findViewById(R.id.txt_base_date);
            viewHolder.base_skors = (TextView)convertView.findViewById(R.id.txt_base_skors);
            viewHolder.base_age = (TextView)convertView.findViewById(R.id.txt_base_age);

            viewHolder.end_date = (TextView)convertView.findViewById(R.id.txt_end_date);
            viewHolder.end_skors = (TextView)convertView.findViewById(R.id.txt_end_skors);
            viewHolder.end_age = (TextView)convertView.findViewById(R.id.txt_end_age);

            viewHolder.txt_parana_date = (TextView)convertView.findViewById(R.id.txt_parana_date);
            viewHolder.txt_parana_sesi = (TextView)convertView.findViewById(R.id.txt_parana_sesi);
            viewHolder.txt_parana_status = (TextView)convertView.findViewById(R.id.txt_parana_status);

            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.img_profile);
            viewHolder.follow_up = (ImageButton)convertView.findViewById(R.id.btn_edit);

            viewHolder.riskFlag[0] = (ImageView)convertView.findViewById(R.id.riskFlagHome01);
            viewHolder.riskFlag[1] = (ImageView)convertView.findViewById(R.id.riskFlagHome02);
            viewHolder.riskFlag[2] = (ImageView)convertView.findViewById(R.id.riskFlagHome03);

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
        viewHolder.name.setText(pc.getDetails().get("namaBayi") != null ? pc.getDetails().get("namaBayi").replaceAll("_", " ") : "-");
        String ages = pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));
        viewHolder.age.setText(pc.getDetails().get("tanggalLahirAnak") != null ? Integer.toString(monthRangeToToday(ages))+"B" : "");

        viewHolder.tgl_lahr.setText(pc.getDetails().get("tanggalLahirAnak") != null ? pc.getDetails().get("tanggalLahirAnak") : "-");
        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());
        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(childobject.getColumnmaps().get("relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            viewHolder.fatherName.setText(namaibu + "," + namaayah);
            viewHolder.subVillage.setText(kiparent.getDetails().get("address1")!=null?kiparent.getDetails().get("address1") :"-");
         //   viewHolder.village_name.setText(kiparent.getDetails().get("address1")!=null?kiparent.getDetails().get("address1") :"-");
            if( kiparent.getDetails().get("tanggal_sesi4")!=null){

                viewHolder.txt_parana_date.setText("Tanggal : "+kiparent.getDetails().get("tanggal_sesi4")!=null?kiparent.getDetails().get("tanggal_sesi4") :"-");
                viewHolder.txt_parana_sesi.setText("Sesi ke : 4");
                viewHolder.txt_parana_status.setText("Status Aktif : "+kiparent.getDetails().get("paranaStatus4")!=null?kiparent.getDetails().get("paranaStatus4") :"-");
            }
            else if( kiparent.getDetails().get("tanggal_sesi3")!=null){

                viewHolder.txt_parana_date.setText("Tanggal : "+kiparent.getDetails().get("tanggal_sesi3")!=null?kiparent.getDetails().get("tanggal_sesi3") :"-");
                viewHolder.txt_parana_sesi.setText("Sesi ke : 3");
                viewHolder.txt_parana_status.setText("Status Aktif : "+kiparent.getDetails().get("paranaStatus3")!=null?kiparent.getDetails().get("paranaStatus3") :"-");
            }

            else if( kiparent.getDetails().get("tanggal_sesi2")!=null){

                viewHolder.txt_parana_date.setText("Tanggal : "+kiparent.getDetails().get("tanggal_sesi2")!=null?kiparent.getDetails().get("tanggal_sesi2") :"-");
                viewHolder.txt_parana_sesi.setText("Sesi ke : 2");
                viewHolder.txt_parana_status.setText("Status Aktif : "+kiparent.getDetails().get("paranaStatus2")!=null?kiparent.getDetails().get("paranaStatus2") :"-");
            }
            else if( kiparent.getDetails().get("tanggal_sesi1")!=null){

                viewHolder.txt_parana_date.setText("Tanggal : "+kiparent.getDetails().get("tanggal_sesi1")!=null?kiparent.getDetails().get("tanggal_sesi1") :"-");
                viewHolder.txt_parana_sesi.setText("Sesi ke : 1");
                viewHolder.txt_parana_status.setText("Status Aktif : "+kiparent.getDetails().get("paranaStatus1")!=null?kiparent.getDetails().get("paranaStatus1") :"-");

            }
            else {
                viewHolder.txt_parana_date.setText("Tanggal : "+"-");
                viewHolder.txt_parana_sesi.setText("Sesi ke : -");
                viewHolder.txt_parana_status.setText("Status Aktif : -");

            }


        }
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

        String tanggl = pc.getDetails().get("tanggal_kunjungan_home") != null ? pc.getDetails().get("tanggal_kunjungan_home") : "-";
        String umurs = pc.getDetails().get("umurs") != null ? pc.getDetails().get("umurs") : "-";
        viewHolder.base_date.setText("Tanggal : "+tanggl);
        viewHolder.base_age.setText("Umur : "+umurs);
        viewHolder.base_skors.setText("Skor : "+baselinecount);

        int _endlinecount = 0;
        for (int i = 1 ; i <=45 ; i++){
            String home_endline = "home"+i+"_ec";
            if(pc.getDetails().get(home_endline) !=null) {
                if (pc.getDetails().get(home_endline).equalsIgnoreCase("Yes")) {
                    _endlinecount = _endlinecount + 1;
                } else {

                }
            }

        }

        String tanggl_endline = pc.getDetails().get("tanggal_kunjungan_endline") != null ? pc.getDetails().get("tanggal_kunjungan_endline") : "-";
        String umurs_endline = pc.getDetails().get("umur_3_6") != null ? pc.getDetails().get("umur_3_6") : "-";
        viewHolder.end_date.setText("Tanggal : "+tanggl_endline);
        viewHolder.end_age.setText("Umur : "+umurs_endline);
        viewHolder.end_skors.setText("Skor : "+_endlinecount);

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

      /*  String home1_it = pc.getDetails().get("home1_it") != null ? pc.getDetails().get("home1_it") : "";
        String home2_it =  pc.getDetails().get("home1_it") != null ? pc.getDetails().get("home1_it") : "";
        String home3_it =  pc.getDetails().get("home1_it") != null ? pc.getDetails().get("home1_it") : "";
        String home4_it =  pc.getDetails().get("home4_it") != null ? pc.getDetails().get("home4_it") : "";
        String home5_it =  pc.getDetails().get("home5_it") != null ? pc.getDetails().get("home5_it") : "";
        String home6_it =  pc.getDetails().get("home6_it") != null ? pc.getDetails().get("home6_it") : "";
        String home7_it =  pc.getDetails().get("home7_it") != null ? pc.getDetails().get("home7_it") : "";
        String home8_it =  pc.getDetails().get("home8_it") != null ? pc.getDetails().get("home8_it") : "";
        String home9_it =  pc.getDetails().get("home9_it") != null ? pc.getDetails().get("home9_it") : "";
        String home10_it =  pc.getDetails().get("home10_it") != null ? pc.getDetails().get("home10_it") : "";
        String home11_it =  pc.getDetails().get("home11_it") != null ? pc.getDetails().get("home11_it") : "";
        String home12_it =  pc.getDetails().get("home12_it") != null ? pc.getDetails().get("home12_it") : "";
        String home13_it = pc.getDetails().get("home13_it") != null ? pc.getDetails().get("home13_it") : "";
        String home14_it = pc.getDetails().get("home14_it") != null ? pc.getDetails().get("home14_it") : "";
        String home15_it = pc.getDetails().get("home15_it") != null ? pc.getDetails().get("home15_it") : "";
        String home16_it = pc.getDetails().get("home16_it") != null ? pc.getDetails().get("home16_it") : "";
        String home17_it = pc.getDetails().get("home17_it") != null ? pc.getDetails().get("home17_it") : "";
        String home18_it = pc.getDetails().get("home18_it") != null ? pc.getDetails().get("home18_it") : "";
        String home19_it = pc.getDetails().get("home19_it") != null ? pc.getDetails().get("home19_it") : "";
        String home20_it = pc.getDetails().get("home20_it") != null ? pc.getDetails().get("home20_it") : "";
        String home21_it = pc.getDetails().get("home21_it") != null ? pc.getDetails().get("home21_it") : "";
        String home22_it = pc.getDetails().get("home22_it") != null ? pc.getDetails().get("home22_it") : "";
        String home23_it = pc.getDetails().get("home23_it") != null ? pc.getDetails().get("home23_it") : "";
        String home24_it = pc.getDetails().get("home24_it") != null ? pc.getDetails().get("home24_it") : "";
        String home25_it = pc.getDetails().get("home25_it") != null ? pc.getDetails().get("home25_it") : "";
        String home26_it = pc.getDetails().get("home26_it") != null ? pc.getDetails().get("home26_it") : "";
        String home27_it = pc.getDetails().get("home27_it") != null ? pc.getDetails().get("home27_it") : "";
        String home28_it = pc.getDetails().get("home28_it") != null ? pc.getDetails().get("home28_it") : "";
        String home29_it = pc.getDetails().get("home29_it") != null ? pc.getDetails().get("home29_it") : "";
        String home30_it = pc.getDetails().get("home30_it") != null ? pc.getDetails().get("home30_it") : "";
        String home31_it = pc.getDetails().get("home31_it") != null ? pc.getDetails().get("home31_it") : "";
        String home32_it = pc.getDetails().get("home32_it") != null ? pc.getDetails().get("home32_it") : "";
        String home33_it = pc.getDetails().get("home33_it") != null ? pc.getDetails().get("home33_it") : "";
        String home34_it = pc.getDetails().get("home34_it") != null ? pc.getDetails().get("home34_it") : "";
        String home35_it = pc.getDetails().get("home35_it") != null ? pc.getDetails().get("home35_it") : "";
        String home36_it = pc.getDetails().get("home36_it") != null ? pc.getDetails().get("home36_it") : "";
        String home37_it = pc.getDetails().get("home37_it") != null ? pc.getDetails().get("home37_it") : "";
        String home38_it = pc.getDetails().get("home38_it") != null ? pc.getDetails().get("home38_it") : "";
        String home39_it = pc.getDetails().get("home39_it") != null ? pc.getDetails().get("home39_it") : "";
        String home40_it = pc.getDetails().get("home40_it") != null ? pc.getDetails().get("home40_it") : "";
        String home41_it = pc.getDetails().get("home41_it") != null ? pc.getDetails().get("home41_it") : "";
        String home42_it = pc.getDetails().get("home42_it") != null ? pc.getDetails().get("home42_it") : "";
        String home43_it = pc.getDetails().get("home43_it") != null ? pc.getDetails().get("home43_it") : "";
        String home44_it = pc.getDetails().get("home44_it") != null ? pc.getDetails().get("home44_it") : "";*/

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
        View View = inflater().inflate(R.layout.smart_register_home_client, null);
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

    private void flagColor(int risk, ImageView img){
        if(risk>2)
            img.setImageResource(R.drawable.risk_p_red);
        else
            img.setImageResource(R.drawable.risk_p_yellow);
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

        TextView fatherName;
                TextView subVillage;
        TextView age;
        public TextView text_daya_lihat;
        public TextView name;
        public TextView tgl_lahr;
        public TextView base_date;
        public TextView base_skors;
        public TextView base_age;

        public TextView end_date;
        public TextView end_skors;
        public TextView end_age;

        public TextView txt_parana_date;
        public TextView txt_parana_sesi;
        public TextView txt_parana_status;

        public ImageView riskFlag[] = new ImageView[3];

    }


}


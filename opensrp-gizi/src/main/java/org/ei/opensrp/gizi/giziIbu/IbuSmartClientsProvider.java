package org.ei.opensrp.gizi.giziIbu;

import android.app.Activity;
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
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.gizi.gizi.ChildDetailActivity;
import org.ei.opensrp.gizi.gizi.FlurryFacade;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.SimpleDateFormat;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class IbuSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = IbuSmartClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    private String bindobject;
    private String entityid;

    public IbuSmartClientsProvider(Context context,
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

            viewHolder.namaLengkap = (TextView)convertView.findViewById(R.id.giziIbuMotherName);
            viewHolder.namaSuami = (TextView) convertView.findViewById(R.id.giziIbuHusbandName);
            viewHolder.dusun = (TextView) convertView.findViewById(R.id.giziIbuSubVilage);
            viewHolder.umur = (TextView)convertView.findViewById(R.id.giziIbuAge);
            viewHolder.tanggalLahir = (TextView) convertView.findViewById(R.id.giziIbuDateOfBirth);

            viewHolder.lastANCVisit = (TextView)convertView.findViewById(R.id.giziIbuVisitDate);
            viewHolder.HPHT = (TextView)convertView.findViewById(R.id.gizi_usia_kandungan);
            viewHolder.lila = (TextView)convertView.findViewById(R.id.gizi_ibu_lila);
            viewHolder.hbLevel = (TextView)convertView.findViewById(R.id.gizi_ibu_HB);
            viewHolder.weight = (TextView)convertView.findViewById(R.id.gizi_ibu_bb);

            viewHolder.sistolik = (TextView)convertView.findViewById(R.id.gizi_ibu_sistolik);
            viewHolder.diastolik = (TextView)convertView.findViewById(R.id.gizi_ibu_diastolik);
            viewHolder.vitaminA2 = (TextView)convertView.findViewById(R.id.gizi_ibu_VitaminA2);
            viewHolder.vitaminA24 = (TextView)convertView.findViewById(R.id.gizi_ibu_VitaminA24);

//            viewHolder.weightText = (TextView)convertView.findViewById(R.id.weightSchedule);
//            viewHolder.weightLogo = (ImageView)convertView.findViewById(R.id.weightSymbol);
//            viewHolder.heightText = (TextView)convertView.findViewById(R.id.heightSchedule);
//            viewHolder.heightLogo = (ImageView)convertView.findViewById(R.id.heightSymbol);
//            viewHolder.vitALogo = (ImageView)convertView.findViewById(R.id.vitASymbol);
//            viewHolder.vitAText = (TextView)convertView.findViewById(R.id.vitASchedule);
//            viewHolder.antihelminticLogo = (ImageView)convertView.findViewById(R.id.antihelminticSymbol);
//            viewHolder.antihelminticText = (TextView)convertView.findViewById(R.id.antihelminticText);

            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.profilepic);
//            viewHolder.follow_up = (ImageButton)convertView.findViewById(R.id.btn_edit);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.follow_up.setOnClickListener(onClickListener);
//        viewHolder.follow_up.setTag(smartRegisterClient);
        viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(smartRegisterClient);

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        // IMPORTANT : data has 2 type: columnMaps and details

        AllCommonsRepository allancRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_ibu");// get all data from ec_ibu table
        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());                                        // get all data related to entity id and transform it into CommonPersonObject

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();                     // gather all details from repository
        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());                             // get specific detail from all details based on entity id
        details.putAll(ancobject.getColumnmaps());                                                                          // combine all columnMaps and details into 1 object.

        if (pc.getDetails() != null) {                                                                                       // used to update client details with details built on previous step
            pc.getDetails().putAll(details);
        } else {
            pc.setDetails(details);
        }

//        System.out.println(pc.getColumnmaps().toString());
//        System.out.println(pc.getDetails().toString());

        viewHolder.namaLengkap.setText(getColumnMaps("namalengkap", pc));
        viewHolder.namaSuami.setText(getColumnMaps("namaSuami", pc));
        viewHolder.dusun.setText(getDetails("posyandu", pc));
        viewHolder.tanggalLahir.setText(getDetails("tanggalLahir",pc).length()>10?getDetails("tanggalLahir",pc).substring(0,10) : "-");
        viewHolder.umur.setText(getDetails("umur", pc) + " "+context.getString(R.string.years_unit));

        viewHolder.lastANCVisit.setText(context.getString(R.string.kunjunganTerakhir) + ": " + getDetails("ancDate", pc));
        int usiaKandungan = usiaKandungan(getDetails("tanggalHPHT",pc),getDetails("ancDate",pc));
        viewHolder.HPHT.setText(context.getString(R.string.usiaKandungan)+": "+(usiaKandungan!= -1
                ? Integer.toString(usiaKandungan)
                : "-")+ context.getString(R.string.str_weeks));
        viewHolder.lila.setText(context.getString(R.string.lila)+": "+getDetails("hasilPemeriksaanLILA",pc) + " cm");
        viewHolder.hbLevel.setText(context.getString(R.string.hb_level)+": "+getDetails("laboratoriumPeriksaHbHasil",pc));
        viewHolder.weight.setText(context.getString(R.string.str_weight)+" "+getDetails("bbKg", pc)+" "+context.getString(R.string.weight_unit));

        viewHolder.sistolik.setText(context.getString(R.string.sistolik)+": "+getDetails("tandaVitalTDSistolik", pc));
        viewHolder.diastolik.setText(context.getString(R.string.diastolik)+": "+getDetails("tandaVitalTDDiastolik",pc));


        viewHolder.vitaminA2.setText(context.getString(R.string.vitamin_a_pnc_2)+getDetails("vitaminA2jamPP",pc));
        viewHolder.vitaminA24.setText(context.getString(R.string.vitamin_a_pnc_24)+getDetails("vitaminA24jamPP",pc));

        viewHolder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryFacade.logEvent("taking_anak_pictures_on_child_detail_view");
//                bindobject = "anak";
//                entityid = pc.entityId();
//                android.util.Log.e(TAG, "onClick: " + entityid);
//                dispatchTakePictureIntent(childview);

            }
        });
    }
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
        View View = inflater().inflate(R.layout.smart_register_gizi_ibu_client, null);
        return View;
    }

    private String getColumnMaps(String tag, CommonPersonObjectClient client){
        return client.getColumnmaps().get(tag) != null ? client.getColumnmaps().get(tag) : "-";
    }

    private String getDetails(String tag, CommonPersonObjectClient client){
        return client.getDetails().get(tag) != null ? client.getDetails().get(tag) : "-";
    }

    private int usiaKandungan(String hpht, String lastANC){
        return (hpht.equals("") || lastANC.equals("")) ? -1 : dailyUnitCalculationOf(hpht,lastANC);
    }

    private int dailyUnitCalculationOf(String dateFrom,String dateTo){
        if(dateFrom.length()<10 || dateTo.length()<10)
            return -1;
        String[]d1 = dateFrom.split("-");
        String[]d2 = dateTo.split("-");

        int day1=Integer.parseInt(d1[2]),month1=Integer.parseInt(d1[1]),year1=Integer.parseInt(d1[0]);
        int day2=Integer.parseInt(d2[2]),month2=Integer.parseInt(d2[1]),year2=Integer.parseInt(d2[0]);

        if (month2<month1){
            month2+=12;
            year2--;
        }
//        int[]dayLength = {31,28,31,30,31,30,31,31,30,31,30,31};
//        int counter = 0;
//        dayLength[1] = year1%4 == 0 ? 29 :28;
//
//        while(day1<=day2 || month1<month2 || year1<year2){
//            counter++;
//            day1++;
//            if (day1>dayLength[month1-1]){
//                day1 = 1;
//                month1++;
//            }
//            if (month1 > 12){
//                month1=1;
//                year1++;
//                dayLength[1] = year1 % 4 == 0 ? 29:28;
//            }
//        }

        return (((year2-year1)*52) + ((month2-month1)* 4 + ((month2-month1)/3)) + ((day2-day1)/7));
    }

     class ViewHolder {

         TextView namaLengkap ;
         TextView tanggalLahir;
         TextView umur;
         TextView dusun;
         TextView namaSuami;
         LinearLayout profilelayout;
         ImageView profilepic;

         TextView HPHT;
         TextView lastANCVisit;
         TextView lila;
         TextView hbLevel;
         TextView weight;

         TextView sistolik;
         TextView diastolik;
         TextView vitaminA2;
         TextView vitaminA24;

//         TextView stunting_status;
//         TextView wasting_status;
//         TextView absentAlert;
//         TextView weightText;
//         ImageView weightLogo;
//         TextView heightText;
//         ImageView heightLogo;
//         ImageView vitALogo;
//         TextView vitAText;
//         ImageView antihelminticLogo;
//         TextView antihelminticText;


         public void setVitAVisibility(){
             int month = Integer.parseInt(new SimpleDateFormat("MM").format(new java.util.Date()));
             int visibility = month == 2 || month == 8 ? View.VISIBLE : View.INVISIBLE;
//             vitALogo.setVisibility(visibility);
//             vitAText.setVisibility(visibility);
         }
//
//         public void setAntihelminticVisibility(int visibility){
//             antihelminticLogo.setVisibility(visibility);
//             antihelminticText.setVisibility(visibility);
//         }
     }
}

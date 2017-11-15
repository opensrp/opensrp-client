package org.ei.opensrp.gizi.gizi;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.io.File;
import java.text.SimpleDateFormat;

import util.formula.Support;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class GiziSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private static final String TAG = GiziSmartClientsProvider.class.getSimpleName();
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    private Drawable iconPencilDrawable;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;
    public GiziSmartClientsProvider(Context context,
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
            viewHolder.dateOfBirth = (TextView) convertView.findViewById(R.id.txt_child_date_of_birth);
            viewHolder.gender = (TextView)convertView.findViewById(R.id.txt_child_gender);
            viewHolder.visitDate = (TextView)convertView.findViewById(R.id.txt_child_visit_date);
            viewHolder.height = (TextView)convertView.findViewById(R.id.txt_child_height);
            viewHolder.weight = (TextView)convertView.findViewById(R.id.txt_child_weight);
            viewHolder.underweight = (TextView)convertView.findViewById(R.id.txt_child_underweight);
            viewHolder.stunting_status = (TextView)convertView.findViewById(R.id.txt_child_stunting);
            viewHolder.wasting_status = (TextView)convertView.findViewById(R.id.txt_child_wasting);

            viewHolder.absentAlert = (TextView)convertView.findViewById(R.id.absen);
            viewHolder.weightText = (TextView)convertView.findViewById(R.id.weightSchedule);
            viewHolder.weightLogo = (ImageView)convertView.findViewById(R.id.weightSymbol);
            viewHolder.heightText = (TextView)convertView.findViewById(R.id.heightSchedule);
            viewHolder.heightLogo = (ImageView)convertView.findViewById(R.id.heightSymbol);
            viewHolder.vitALogo = (ImageView)convertView.findViewById(R.id.vitASymbol);
            viewHolder.vitAText = (TextView)convertView.findViewById(R.id.vitASchedule);
            viewHolder.antihelminticLogo = (ImageView)convertView.findViewById(R.id.antihelminticSymbol);
            viewHolder.antihelminticText = (TextView)convertView.findViewById(R.id.antihelminticText);

            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.profilepic);
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

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        detailsRepository.updateDetails(pc);

        //start profile image
//        viewHolder.profilepic.setTag(R.id.entity_id, pc.getCaseId());//required when saving file to disk
        viewHolder.profilepic.setTag(R.id.entity_id, pc.getColumnmaps().get("_id"));//required when saving file to disk

        if (pc.getDetails().get("gender") != null) {
            util.formula.Support.setImagetoHolderFromUri((Activity) context,
                    DrishtiApplication.getAppDir() + File.separator + pc.getDetails().get("base_entity_id") + ".JPEG",
                    viewHolder.profilepic, pc.getDetails().get("gender").equals("female") ? R.drawable.child_girl_infant : R.drawable.child_boy_infant);
        } else {
            Log.e(TAG, "getView: Gender is NOT SET");
        }

        viewHolder.name.setText(Support.getDetails(pc,"namaBayi"));
        String ages = pc.getColumnmaps().get("tanggalLahirAnak").substring(0, pc.getColumnmaps().get("tanggalLahirAnak").indexOf("T"));
        viewHolder.age.setVisibility(View.INVISIBLE);//.setText(pc.getDetails().get("tanggalLahirAnak")!= null ? Integer.toString(monthRangeToToday(ages))+" bln" : "");

        AllCommonsRepository childRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_anak");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());

        AllCommonsRepository kirep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_kartu_ibu");
        final CommonPersonObject kiparent = kirep.findByCaseID(Support.getColumnmaps(childobject,"relational_id"));

        if(kiparent != null) {
            detailsRepository.updateDetails(kiparent);
            String namaayah = kiparent.getDetails().get("namaSuami") != null ? kiparent.getDetails().get("namaSuami") : "";
            String namaibu = kiparent.getColumnmaps().get("namalengkap") != null ? kiparent.getColumnmaps().get("namalengkap") : "";

            viewHolder.fatherName.setText(String.format("%s,%s", namaibu, namaayah));
            viewHolder.subVillage.setText(kiparent.getDetails().get("address1") != null ? kiparent.getDetails().get("address1") : "");
        }


        String Tgl = pc.getDetails().get("tanggalLahirAnak");

        if (Tgl != null && Tgl.contains("T"))
           Tgl = Tgl.substring(0, Tgl.indexOf("T"));

        viewHolder.setAntihelminticVisibility(
               dayRangeBetween(Tgl.split("-")
                       ,new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).split("-")
               ) >= 365 ? View.VISIBLE : View.INVISIBLE
        );
        viewHolder.dateOfBirth.setText(pc.getDetails().get("tanggalLahirAnak")!=null?Tgl:"");

//        viewHolder.gender.setText( pc.getDetails().get("gender") != null ? setGender(pc.getDetails().get("gender")):"-");
        int age = monthRangeToToday(ages);
        viewHolder.gender.setText(pc.getDetails().get("tanggalLahirAnak") != null
                ? age/12 + " " + context.getString(R.string.years_unit)+" "+age%12+" "+context.getString(R.string.month_unit) : "-");

/** collect history data and clean latest history data which contains no specific date or value,
 */
        if(!Support.getDetails(pc,"umur").toLowerCase().equals("nan")) {
            String[] history1 = pc.getDetails().get("history_berat") != null ? Support.insertionSort(pc.getDetails().get("history_berat")) : new String[]{"0:0"};
            if (history1[history1.length - 1].charAt(history1[history1.length - 1].length() - 1) == ':')
                history1[history1.length - 1] = history1[history1.length - 1] + "-";
            String[] history2 = pc.getDetails().get("history_tinggi") != null ? Support.insertionSort(pc.getDetails().get("history_tinggi")) : new String[]{"0:0"};
            if (history2[history2.length - 1].charAt(history2[history2.length - 1].length() - 1) == ':')
                history2[history2.length - 1] = history2[history2.length - 1] + "-";
            String newestDateonHistory = history1.length > 1
                    ? findDate(Tgl, Support.getAge(history1[history1.length - 1]))
                    : pc.getDetails().get("tanggalPenimbangan") != null
                    ? pc.getDetails().get("tanggalPenimbangan")
                    : Tgl;

            System.out.println("history1 : " + history1[history1.length - 1]);
            System.out.println("history2 : " + history2[history2.length - 1]);
            System.out.println("newest : " + newestDateonHistory);
/**
 */

            if (newestDateonHistory.equals(pc.getDetails().get("tanggalPenimbangan") != null ? pc.getDetails().get("tanggalPenimbangan") : "-")) {
                System.out.println("history = tglPenimbangan");
                viewHolder.visitDate.setText(context.getString(R.string.tanggal) + " " + (pc.getDetails().get("tanggalPenimbangan") != null ? pc.getDetails().get("tanggalPenimbangan") : "-"));
                viewHolder.height.setText(context.getString(R.string.height) + " " + (pc.getDetails().get("tinggiBadan") != null ? pc.getDetails().get("tinggiBadan") : "-") + " Cm");
                viewHolder.weight.setText(context.getString(R.string.weight) + " " + (pc.getDetails().get("beratBadan") != null ? pc.getDetails().get("beratBadan") : "-") + " Kg");
                viewHolder.weightText.setText(context.getString(R.string.label_weight));
                viewHolder.heightText.setText(context.getString(R.string.label_height));
                viewHolder.antihelminticText.setText(R.string.anthelmintic);
            } else {
                System.out.println("history != tglPenimbangan");
                viewHolder.visitDate.setText(context.getString(R.string.tanggal) + " " + (history1.length > 1 ? newestDateonHistory : "-"));
                viewHolder.height.setText(context.getString(R.string.height) + " "
                        + (pc.getDetails().get("tinggiBadan") != null
                        ? !pc.getDetails().get("tinggiBadan").equals(history2[history2.length - 1])
                        ? history2[history2.length - 1].split(":")[1]
                        : pc.getDetails().get("tinggiBadan")
                        : "-")
                        + " Cm");
                viewHolder.weight.setText(context.getString(R.string.weight) + " "
                        + (pc.getDetails().get("beratBadan") != null
                        ? !pc.getDetails().get("beratBadan").equals(history1[history1.length - 1])
                        ? history1[history1.length - 1].split(":")[1]
                        : pc.getDetails().get("beratBadan")
                        : "-")
                        + " Kg");
                viewHolder.weightText.setText(context.getString(R.string.label_weight));
                viewHolder.heightText.setText(context.getString(R.string.label_height));
                viewHolder.antihelminticText.setText(R.string.anthelmintic);
            }

//------VISIBLE AND INVISIBLE COMPONENT
            viewHolder.absentAlert.setVisibility(pc.getDetails().get("tanggalPenimbangan") != null
                            ? isLate(pc.getDetails().get("tanggalPenimbangan"), 1)
                            ? View.VISIBLE
                            : View.INVISIBLE
                            : View.INVISIBLE
            );
            viewHolder.setVitAVisibility();


//------CHILD DATA HAS BEEN SUBMITTED OR NOT
            System.out.println("latest date = " + returnLatestDate(pc.getDetails().get("tanggalPenimbangan"), newestDateonHistory));

            viewHolder.weightLogo.setImageDrawable(context.getResources().getDrawable(isLate(returnLatestDate(pc.getDetails().get("tanggalPenimbangan"), newestDateonHistory), 0) ? R.drawable.ic_remove : R.drawable.ic_yes_large));
            viewHolder.heightLogo.setImageDrawable(context.getResources().getDrawable(!isLate(returnLatestDate(pc.getDetails().get("tanggalPenimbangan"), newestDateonHistory), 0) && !Support.getDetails(pc, "tinggiBadan").equals("-") ? R.drawable.ic_yes_large : R.drawable.ic_remove));
            viewHolder.vitALogo.setImageDrawable(context.getResources().getDrawable(inTheSameRegion(pc.getDetails().get("lastVitA")) ? R.drawable.ic_yes_large : R.drawable.ic_remove));
            viewHolder.antihelminticLogo.setImageDrawable(context.getResources().getDrawable(isGiven(pc, "obatcacing") ? R.drawable.ic_yes_large : R.drawable.ic_remove));

            if (pc.getDetails().get("tanggalPenimbangan") != null) {
                viewHolder.stunting_status.setText(String.format("%s %s", context.getString(R.string.stunting), hasValue(pc.getDetails().get("stunting")) ? setStatus(pc.getDetails().get("stunting")) : "-"));
                viewHolder.underweight.setText(String.format("%s %s", context.getString(R.string.wfa), hasValue(pc.getDetails().get("underweight")) ? setStatus(pc.getDetails().get("underweight")) : "-"));
                viewHolder.wasting_status.setText(String.format("%s %s", context.getString(R.string.wasting), hasValue(pc.getDetails().get("wasting")) ? setStatus(pc.getDetails().get("wasting")) : "-"));
            } else {
                viewHolder.underweight.setText(String.format("%s ", context.getString(R.string.wfa)));
                viewHolder.stunting_status.setText(String.format("%s ", context.getString(R.string.stunting)));
                viewHolder.wasting_status.setText(String.format("%s ", context.getString(R.string.wasting)));
            }
            //================ END OF Z-SCORE==============================//
        }
        convertView.setLayoutParams(clientViewLayoutParams);
       // return convertView;
    }
    CommonPersonObjectController householdelcocontroller;

    private String setStatus(String status){
        switch (status.toLowerCase()){
            case "underweight" :
                return context.getString(R.string.underweight);
            case "severely underweight" :
                return context.getString(R.string.s_underweight);
            case "normal":
                return context.getString(R.string.normal);
            case "overweight":
                return context.getString(R.string.overweight);
            case "severely stunted" :
                return context.getString(R.string.s_stunted);
            case "stunted" :
                return context.getString(R.string.stunted);
            case "tall" :
                return context.getString(R.string.tall);
            case "severely wasted" :
                return context.getString(R.string.s_wasted);
            case "wasted" :
                return context.getString(R.string.wasted);
            default:
                return "";
        }
    }

    private String setGender(String gender){
        return gender.toLowerCase().contains("em") ? context.getString(R.string.child_female) : context.getString(R.string.child_male);
    }

    private String returnLatestDate(String date1, String date2){
        if(date1 == null || date2 == null){
            return date1==null && date2==null
                    ? null
                    : date1==null
                        ? date2
                        : date1
                    ;
        }
        return dayRangeBetween(date1.split("-"),date2.split("-"))>0 ? date2 : date1;
    }

    private boolean isLate(String lastVisitDate,int threshold){
        if (lastVisitDate==null || lastVisitDate.length()<6)
            return true;
        return  monthRangeToToday(lastVisitDate) > threshold;
    }

//    private boolean isDue(String lastVisitDate){
//        if(lastVisitDate==null || lastVisitDate.length()<6)
//            return true;
//
//        return dayRangeBetween(lastVisitDate.split("-"),new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()).split("-")) <= 30;
//    }

    private boolean isGiven(CommonPersonObjectClient pc, String details){
        if(pc.getDetails().get(details) != null)
            return pc.getDetails().get(details).equalsIgnoreCase("ya") || pc.getDetails().get(details).equalsIgnoreCase("yes");
        return false;
    }

    private boolean hasValue(String data){
        if(data == null)
            return false;
        return data.length() > 2;
    }

    private int monthRangeToToday(String lastVisitDate){
        String currentDate[] = new SimpleDateFormat("yyyy-MM").format(new java.util.Date()).substring(0,7).split("-");
        return ((Integer.parseInt(currentDate[0]) - Integer.parseInt(lastVisitDate.substring(0,4)))*12 +
                (Integer.parseInt(currentDate[1]) - Integer.parseInt(lastVisitDate.substring(5,7))));
    }

    /**
     *  The part of method that using to check is the last visit date was in the same region as the
     *  current vitamin A period
    **/
    private boolean inTheSameRegion(String date){
        if(date==null || date.length()<6)
            return false;
        int currentDate = Integer.parseInt(new SimpleDateFormat("MM").format(new java.util.Date()));
        int visitDate = Integer.parseInt(date.substring(5, 7));

        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new java.util.Date()));
        int visitYear = Integer.parseInt(date.substring(0, 4));

        boolean date1 = currentDate < 2 || currentDate >=8;
        boolean date2 = visitDate < 2 || visitDate >=8;

        int indicator = currentDate == 1 ? 2:1;

        return (!((!date1 && date2) || (date1 && !date2)) && ((currentYear-visitYear)<indicator));
    }

    private int dayRangeBetween(String[]startDate, String[]endDate){
        return (Integer.parseInt(endDate[0]) - Integer.parseInt(startDate[0]))*360 +
               (Integer.parseInt(endDate[1]) - Integer.parseInt(startDate[1]))*30 +
               (Integer.parseInt(endDate[2]) - Integer.parseInt(startDate[2]));
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
        View View = inflater().inflate(R.layout.smart_register_gizi_client, null);
        return View;
    }

     class ViewHolder {

         TextView name ;
         TextView age ;
         TextView village;
         TextView husbandname;
         TextView subVillage;
         LinearLayout profilelayout;
         ImageView profilepic;
         FrameLayout due_date_holder;
         Button warnbutton;
         ImageButton follow_up;
         TextView fatherName;
         TextView gender;
         TextView dateOfBirth;
         TextView visitDate;
         TextView height;
         TextView weight;
         TextView underweight;
         TextView stunting_status;
         TextView wasting_status;
         TextView absentAlert;
         TextView weightText;
         ImageView weightLogo;
         TextView heightText;
         ImageView heightLogo;
         ImageView vitALogo;
         TextView vitAText;
         ImageView antihelminticLogo;
         TextView antihelminticText;


         public void setVitAVisibility(){
             int month = Integer.parseInt(new SimpleDateFormat("MM").format(new java.util.Date()));
             int visibility = month == 2 || month == 8 ? View.VISIBLE : View.INVISIBLE;
//             vitALogo.setVisibility(visibility);
//             vitAText.setVisibility(visibility);
         }

         public void setAntihelminticVisibility(int visibility){
             antihelminticLogo.setVisibility(visibility);
             antihelminticText.setVisibility(visibility);
         }
     }

    public String findDate(String startDate, int dayAge){
        int[]dayLength = {31,28,31,30,31,30,31,31,30,31,30,31};
        int startYear = Integer.parseInt(startDate.substring(0,4));
        int startMonth = Integer.parseInt(startDate.substring(5,7));
        int startDay = Integer.parseInt(startDate.substring(8, 10));

        dayLength[1] = startYear % 4 == 0 ? 29 : 28;
        while(dayAge>dayLength[startMonth-1]){
            dayAge = dayAge - dayLength[startMonth-1];
            startMonth++;
            if(startMonth>12){
                startYear++;
                startMonth = 1;
                dayLength[1] = startYear % 4 == 0 ? 29 : 28;
            }
        }
        startDay+=dayAge;
        if(startDay > dayLength[startMonth-1]) {
            startDay=startDay - dayLength[startMonth-1];
            startMonth++;
        }
        if(startMonth>12) {
            startYear++;
            startMonth = 1;
        }

        String m = "" + (startMonth<10 ? "0"+startMonth : Integer.toString(startMonth));
        String d = "" + (startDay<10 ? "0"+startDay : Integer.toString(startDay));
        return Integer.toString(startYear)+"-"+m+"-"+d;
    }


}


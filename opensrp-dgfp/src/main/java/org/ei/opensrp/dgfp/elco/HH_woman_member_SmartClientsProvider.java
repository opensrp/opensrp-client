package org.ei.opensrp.dgfp.elco;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.dgfp.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class HH_woman_member_SmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;
    AlertService alertService;
    String [] arrayofbirthcontrol = new String []{"Oral Pill","Condom","Injectable","IUD","Implant","Pemananently Sterilized(Male)","Pemananently Sterilized(Female)","ECP","Referred for Side-effects","Referred for Methods"};

    public HH_woman_member_SmartClientsProvider(Context context,
                                                View.OnClickListener onClickListener, AlertService alertService) {
        this.onClickListener = onClickListener;
        this.alertService = alertService;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public void getView(final SmartRegisterClient smartRegisterClient, View convertView) {
        View itemView;
        itemView = convertView;
//        itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_mcare_anc_client, null);
        LinearLayout profileinfolayout = (LinearLayout) itemView.findViewById(R.id.profile_info_layout);

        ImageView profilepic = (ImageView) itemView.findViewById(R.id.profilepic);
        TextView name = (TextView) itemView.findViewById(R.id.name);
        TextView husband_name = (TextView) itemView.findViewById(R.id.husband_name);
        TextView gob_hhid = (TextView) itemView.findViewById(R.id.gob_hhid);
        TextView coupleno = (TextView) itemView.findViewById(R.id.coupleno);
//        TextView pregnancystatus = (TextView)itemView.findViewById(R.id.pregnancystatus);
        TextView village = (TextView) itemView.findViewById(R.id.village);
        TextView age = (TextView) itemView.findViewById(R.id.age);
        TextView nid = (TextView) itemView.findViewById(R.id.nid);
        TextView brid = (TextView) itemView.findViewById(R.id.brid);
        TextView lmp = (TextView) itemView.findViewById(R.id.lmp);
        TextView mobile_number = (TextView) itemView.findViewById(R.id.mobile_number);
        TextView fp_status = (TextView) itemView.findViewById(R.id.fp_status);
        TextView tt_dose_given = (TextView) itemView.findViewById(R.id.tt_dose_given);
        TextView last_vstatus = (TextView) itemView.findViewById(R.id.last_vstatus);
        TextView pvfdue = (TextView) itemView.findViewById(R.id.pvf);
//
//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;


        name.setText(pc.getColumnmaps().get("Mem_F_Name") != null ? pc.getColumnmaps().get("Mem_F_Name") : "");
//        coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));

        mobile_number.setText("Mobile No: " + (pc.getDetails().get("ELCO_Mobile_Number") != null ? pc.getDetails().get("ELCO_Mobile_Number") : ""));


        gob_hhid.setText((pc.getDetails().get("Member_GoB_HHID")!=null?pc.getDetails().get("Member_GoB_HHID"):""));
//        lmp.setText("LMP :" +(pc.getDetails().get("LMP")!=null?pc.getDetails().get("LMP"):""));

//        String gestationalage = pc.getDetails().get("GA")!=null?pc.getDetails().get("GA"):"";
//        if(!gestationalage.equalsIgnoreCase("")) {
//            ga.setText("GA :" + gestationalage + " weeks");
//        }else{
//            ga.setText("GA : Unavailabe" );
//
//        }

        if (pc.getDetails().get("profilepic") != null) {
            HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.householdload);
        } else {
            profilepic.setImageResource(R.drawable.woman_placeholder);
        }

        husband_name.setText(" H: " + (pc.getDetails().get("Spouse_Name") != null ? pc.getDetails().get("Spouse_Name") : ""));

        coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));

//        if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("0")){
//            pregnancystatus.setText(",Not Pregnant");
//        }
//        else if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("1")){
//            pregnancystatus.setText(",Pregnant");
//        }else if ((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("9")){
//            pregnancystatus.setText("");
//        }
        village.setText("v: " + humanize((pc.getDetails().get("Mem_Village_Name") != null ? pc.getDetails().get("Mem_Village_Name") : "").replace("+", "_")) + ", " + "M: " + humanize((pc.getDetails().get("Mem_Mauzapara") != null ? pc.getDetails().get("Mem_Mauzapara") : "").replace("+", "_")));

        lmp.setText(pc.getDetails().get("LMP") != null ? pc.getDetails().get("LMP") : "");

        age.setText(pc.getDetails().get("Calc_Age_Confirm") != null ? "("+pc.getDetails().get("Calc_Age_Confirm")+")" : "");
        String agetodisplay = "";
        try {
            String datetocalc = "";
            if(datetocalc.equalsIgnoreCase("")){
                datetocalc = (pc.getColumnmaps().get("Calc_Dob_Confirm") != null ?  pc.getColumnmaps().get("Calc_Dob_Confirm")  : "");
            }
            DateUtil.setDefaultDateFormat("yyyy-MM-dd");
            int days = DateUtil.dayDifference(DateUtil.getLocalDate(datetocalc), DateUtil.today());
            int calc_age = days / 365;
            agetodisplay = calculateage(days);
        }catch (Exception e){

        }
        age.setText(agetodisplay);
//        calc_HoH_dob_confirm
//        try {
//            int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("calc_dob_confirm") != null ?  pc.getDetails().get("calc_dob_confirm")  : "")), DateUtil.today());
//            int calc_age = days / 365;
//            age.setText(calc_age);
//        }catch (Exception e){
//
//        }
        nid.setText("NID: " + (pc.getDetails().get("ELCO_NID") != null ? pc.getDetails().get("ELCO_NID") : ""));
        brid.setText("BRID: " + (pc.getDetails().get("ELCO_BRID") != null ? pc.getDetails().get("ELCO_BRID") : ""));
        tt_dose_given.setText("TT Dose Given: " + (pc.getDetails().get("TT_Count") != null ? pc.getDetails().get("TT_Count") : ""));
        last_vstatus.setText("Last VStatus: " + (pc.getDetails().get("ELCO_Status") != null ? pc.getDetails().get("ELCO_Status") : ""));


        String birthcontrolstring = "";
        int birthcontrolindex = Integer.parseInt((pc.getDetails().get("Birth_Control") != null ? pc.getDetails().get("Birth_Control") : "99"));

        if(birthcontrolindex<99){
            birthcontrolstring = arrayofbirthcontrol[birthcontrolindex];
        }
        fp_status.setText("Fp Status: " + birthcontrolstring);



//        pvfdue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((HH_woman_member_SmartRegisterActivity) ((Activity) context)).startFormActivity("elco_register", pc.getCaseId(),null);
//
//            }
//
//        });
        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "ELCO PSRF");
//        Reg_Date
//        Census_Date
        String datetoconvert = "";
        String scheduledate = "";
        if(pc.getDetails().get("Member_Reg_Date")!=null){
          datetoconvert = pc.getDetails().get("Member_Reg_Date");
            scheduledate =  setDate(datetoconvert,0);
        }
        if(pc.getDetails().get("ELCO_Date")!=null){
            datetoconvert = pc.getDetails().get("ELCO_Date");
            scheduledate =  setDate(datetoconvert,56);
        }
//        if(!datetoconvert.equalsIgnoreCase("")) {
////            Date scheduledate = converdatefromString(datetoconvert);
//        }

        singleALertButtonView(alertlist_for_client,pvfdue,pc,datetoconvert,scheduledate);

        constructRiskFlagView(pc, itemView);
        itemView.setLayoutParams(clientViewLayoutParams);


        Log.e("------elco-------",pc.getDetails().toString());
    }

    public void singleALertButtonView(List<Alert> alertlist_for_client,TextView due_visit_date, CommonPersonObjectClient smartRegisterClient,String textforComplete,String textfornotcomplete){
        if(alertlist_for_client.size() == 0 ){
            if(smartRegisterClient.getDetails().get("ELCO_Date")==null){
                due_visit_date.setText(smartRegisterClient.getDetails().get("Member_Reg_Date"));
                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTag(smartRegisterClient);
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            }else{
                due_visit_date.setText(textforComplete);
                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTag(smartRegisterClient);
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green));
            }
//            due_visit_date.setText("Not Synced to Server");
//            due_visit_date.setTextColor(context.getResources().getColor(R.color.text_black));
//            due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
//            due_visit_date.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//            due_visit_date.setOnClickListener(onClickListener);
//            due_visit_date.setTag(smartRegisterClient);

        }
        for(int i = 0;i<alertlist_for_client.size();i++){
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
//                due_visit_date.setText(alertlist_for_client.get(i).expiryDate());
                due_visit_date.setText(textfornotcomplete);

                due_visit_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                due_visit_date.setTextColor(context.getResources().getColor(R.color.text_black));

                due_visit_date.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
//                due_visit_date.setText(alertlist_for_client.get(i).startDate());
                due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTag(smartRegisterClient);
                due_visit_date.setText(textfornotcomplete);
            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
//                due_visit_date.setText((alertlist_for_client.get(i).startDate()));
                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

                due_visit_date.setTag(smartRegisterClient);
                due_visit_date.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
                due_visit_date.setText(textfornotcomplete);

            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                due_visit_date.setTextColor(context.getResources().getColor(R.color.text_black));

                due_visit_date.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
                due_visit_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                due_visit_date.setText(textfornotcomplete);
            }
            if(alertlist_for_client.get(i).isComplete()){
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTag(smartRegisterClient);
                due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                due_visit_date.setText(textforComplete);
            }
        }
    }







    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }




    private void constructRiskFlagView(CommonPersonObjectClient pc, View itemView) {
//        AllCommonsRepository allancRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother");
//        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
//        AllCommonsRepository allelcorep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
//        CommonPersonObject elcoparent = allelcorep.findByCaseID(ancobject.getRelationalId());

        ImageView hrp = (ImageView) itemView.findViewById(R.id.hrp);
        ImageView hp = (ImageView) itemView.findViewById(R.id.hr);
        ImageView vg = (ImageView) itemView.findViewById(R.id.vg);
        if (pc.getDetails().get("FWVG") != null && pc.getDetails().get("FWVG").equalsIgnoreCase("1")) {

        } else {
            vg.setVisibility(View.GONE);
        }
        if (pc.getDetails().get("FWHRP") != null && pc.getDetails().get("FWHRP").equalsIgnoreCase("1")) {

        } else {
            hrp.setVisibility(View.GONE);
        }
        if (pc.getDetails().get("FWHR_PSR") != null && pc.getDetails().get("FWHR_PSR").equalsIgnoreCase("1")) {

        } else {
            hp.setVisibility(View.GONE);
        }

//        if(pc.getDetails().get("FWWOMAGE")!=null &&)

    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_dgfp_elco, null);
        return View;
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

    public String ancdate(String date, int day) {
        String ancdate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date anc_date = format.parse(date);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(anc_date);
            calendar.add(Calendar.DATE, day);
            anc_date.setTime(calendar.getTime().getTime());
            ancdate = format.format(anc_date);
        } catch (Exception e) {
            e.printStackTrace();
            ancdate = "";
        }
        return ancdate;
    }

    public String setDate(String date, int daystoadd) {

        Date lastdate = converdatefromString(date);

        if(lastdate!=null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
            calendar.add(Calendar.DATE, daystoadd);//8 weeks
            lastdate.setTime(calendar.getTime().getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    //            String result = String.format(Locale.ENGLISH, format.format(lastdate) );
            return (format.format(lastdate));
    //             due_visit_date.append(format.format(lastdate));

        }else{
            return "";
        }
    }
    public Date converdatefromString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        }catch (Exception e){
            return null;
        }
        return convertedDate;
    }
    public static String calculateage(int i) {
        if(i <= 15){
            return (i + " days");
        }
        if(i <= 141){
            return (i/7 + " weeks");
        }
        if(i <= 719){
            return (i/30 + " months");
        }
        if(i >719){
            String years = "("+i/365 + " y) ";


            return years ;
        }
        return "";
    }







}

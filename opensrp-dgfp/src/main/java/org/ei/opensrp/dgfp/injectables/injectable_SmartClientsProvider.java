package org.ei.opensrp.dgfp.injectables;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.dgfp.elco.HH_woman_member_SmartRegisterActivity;
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
import static org.ei.opensrp.dgfp.child.ChildDetailActivity.calculateage;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class injectable_SmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;
    AlertService alertService;

    public injectable_SmartClientsProvider(Context context,
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
        TextView husband_name_or_mothersname = (TextView) itemView.findViewById(R.id.husband_name_or_mothers_name);
        TextView gob_hhid = (TextView) itemView.findViewById(R.id.gob_hhid);
        TextView coupleno_or_fathersname = (TextView) itemView.findViewById(R.id.coupleno);
//        TextView pregnancystatus = (TextView)itemView.findViewById(R.id.pregnancystatus);
        TextView village = (TextView) itemView.findViewById(R.id.village_mauzapara);
        TextView age = (TextView) itemView.findViewById(R.id.age);
        TextView nid = (TextView) itemView.findViewById(R.id.nid);
        TextView brid = (TextView) itemView.findViewById(R.id.brid);
        ImageView lastInjectibleTick = (ImageView) itemView.findViewById(R.id.lastInjectibleTick);
        TextView lastinjectable_dose = (TextView) itemView.findViewById(R.id.last_injectable_dose);
        TextView follow_up = (TextView)itemView.findViewById(R.id.next_injectable_dose);
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

//        follow_up.setOnClickListener(onClickListener);
//        follow_up.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;


        name.setText(pc.getColumnmaps().get("Mem_F_Name") != null ? pc.getColumnmaps().get("Mem_F_Name") : "");
        gob_hhid.setText((pc.getDetails().get("Member_GoB_HHID") != null ? pc.getDetails().get("Member_GoB_HHID") : ""));



//        edd.setText("EDD :" +(pc.getColumnmaps().get("EDD")!=null?pc.getColumnmaps().get("EDD"):""));
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
            husband_name_or_mothersname.setText((pc.getDetails().get("Spouse_Name") != null ? pc.getDetails().get("Spouse_Name") : ""));
            coupleno_or_fathersname.setText((pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));

            //lastinjectable_dose.setText((pc.getDetails().get("Todays_Dose_No") != null ? pc.getDetails().get("Todays_Dose_No") : ""));
            lastInjectableDose(lastInjectibleTick,lastinjectable_dose,pc);
//        if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("0")){
//            pregnancystatus.setText(",Not Pregnant");
//        }
//        else if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("1")){
//            pregnancystatus.setText(",Pregnant");
//        }else if ((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("9")){
//            pregnancystatus.setText("");
//        }
        village.setText(humanize((pc.getDetails().get("Mem_Village_Name") != null ? (pc.getDetails().get("Mem_Village_Name")+",") : "").replace("+", "_")) + humanize((pc.getDetails().get("Mem_Mauzapara") != null ? pc.getDetails().get("Mem_Mauzapara") : "").replace("+", "_")));

//        date_of_last_visit.setText(pc.getDetails().get("nutrition_Visit_Date") != null ? pc.getDetails().get("nutrition_Visit_Date") : "");

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




        nid.setText("NID: " + (pc.getDetails().get("ELCO_NID") != null ? pc.getDetails().get("ELCO_NID") : ""));
        brid.setText("BRID: " + (pc.getDetails().get("ELCO_BRID") != null ? pc.getDetails().get("ELCO_BRID") : ""));



        String datetoconvert = "";
        String scheduledate = "";
        if(pc.getDetails().get("Format_Injection_Date")!=null){
            datetoconvert = pc.getDetails().get("Format_Injection_Date");
            scheduledate =  setDate(datetoconvert,90);
        }
        if(pc.getDetails().get("injectable_Today")!=null){
            datetoconvert = pc.getDetails().get("injectable_Today");
            scheduledate =  setDate(datetoconvert,7);
        }
        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Injectables");
        singleALertButtonView(alertlist_for_client,follow_up,pc,scheduledate,datetoconvert);
//        tt_dose_given.setText("TT Dose Given: " + (pc.getDetails().get("TT_Count") != null ? pc.getDetails().get("TT_Count") : ""));
//        last_vstatus.setText("Last VStatus: " + (pc.getDetails().get("ELCO_Status") != null ? pc.getDetails().get("ELCO_Status") : ""));


        itemView.setLayoutParams(clientViewLayoutParams);
    }

    private void lastInjectableDose(ImageView lastInjectibleTick, TextView lastInjectableText, CommonPersonObjectClient pc) {

        String lastInjectableNumberString = pc.getDetails().get("Todays_Dose_No") != null ? pc.getDetails().get("Todays_Dose_No") : "";
        if(lastInjectableNumberString.length() > 0){
            if((pc.getDetails().get("Is_Post_Due") != null ? pc.getDetails().get("Is_Post_Due"):"").equalsIgnoreCase("1")){
                lastInjectibleTick.setImageResource(R.mipmap.notdoneintime);
            }
            if((pc.getDetails().get("Is_On_Time") != null ? pc.getDetails().get("Is_On_Time"):"").equalsIgnoreCase("1")){
                lastInjectibleTick.setImageResource(R.mipmap.doneintime);
            }
            String value = "Dose" + lastInjectableNumberString + ", ";
            value += pc.getDetails().get("Injection_Date") != null ?pc.getDetails().get("Injection_Date"):"";
            lastInjectableText.setText(value);
            return;
        }
        lastInjectableText.setText("N/A");

    }

    public void singleALertButtonView(List<Alert> alertlist_for_client,TextView due_visit_date, CommonPersonObjectClient smartRegisterClient,String textforComplete,String textfornotcomplete){
        if(alertlist_for_client.size() == 0 ){
            due_visit_date.setText("Not Synced to Server");
            due_visit_date.setTextColor(context.getResources().getColor(R.color.text_black));
            due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            due_visit_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            due_visit_date.setOnClickListener(onClickListener);
            due_visit_date.setTag(smartRegisterClient);

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
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_dgfp_injectables, null);
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


    class alertTextandStatus{
        String alertText ,alertstatus;

        public alertTextandStatus(String alertText, String alertstatus) {
            this.alertText = alertText;
            this.alertstatus = alertstatus;
        }

        public String getAlertText() {
            return alertText;
        }

        public void setAlertText(String alertText) {
            this.alertText = alertText;
        }

        public String getAlertstatus() {
            return alertstatus;
        }

        public void setAlertstatus(String alertstatus) {
            this.alertstatus = alertstatus;
        }
    }

}

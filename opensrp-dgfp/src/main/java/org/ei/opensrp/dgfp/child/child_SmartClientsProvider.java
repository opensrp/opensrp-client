package org.ei.opensrp.dgfp.child;

import android.app.Activity;
import android.content.Context;
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
import org.ei.opensrp.dgfp.hh_member.HH_member_SmartRegisterActivity;
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
public class child_SmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;
    AlertService alertService;

    public child_SmartClientsProvider(Context context,
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
        TextView mothersname = (TextView) itemView.findViewById(R.id.mother_name);
        TextView fathersname = (TextView) itemView.findViewById(R.id.father_name);
        TextView coupleno = (TextView) itemView.findViewById(R.id.coupleno);
        TextView gobhhid = (TextView) itemView.findViewById(R.id.gobhhid);
//        TextView pregnancystatus = (TextView)itemView.findViewById(R.id.pregnancystatus);
        TextView village = (TextView) itemView.findViewById(R.id.village_mauzapara);
        TextView age = (TextView) itemView.findViewById(R.id.age);
        TextView cBRID = (TextView) itemView.findViewById(R.id.c_brid);
        TextView mBRID = (TextView) itemView.findViewById(R.id.m_brid);
        TextView mNID = (TextView) itemView.findViewById(R.id.m_nid);
        TextView date_of_birth = (TextView) itemView.findViewById(R.id.dateofbirth);
//        TextView nutrition_taken = (TextView) itemView.findViewById(R.id.nutrition_taken);
        TextView childVaccines = (TextView) itemView.findViewById(R.id.childVaccines);
        TextView follow_up = (TextView)itemView.findViewById(R.id.child_followup_form);
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

        follow_up.setOnClickListener(onClickListener);
        follow_up.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;


        name.setText(pc.getColumnmaps().get("Mem_F_Name") != null ? pc.getColumnmaps().get("Mem_F_Name") : "");
        mothersname.setText(pc.getDetails().get("Child_Mother") != null ? pc.getDetails().get("Child_Mother") : "");
        fathersname.setText(pc.getDetails().get("Child_Father") != null ? pc.getDetails().get("Child_Father") : "");

        coupleno.setText(pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : "");
        gobhhid.setText((pc.getDetails().get("Member_GoB_HHID") != null ? pc.getDetails().get("Member_GoB_HHID") : ""));

        if((pc.getDetails().get("Child") != null ? pc.getDetails().get("Child") : "").equalsIgnoreCase("1")){
            if ((pc.getDetails().get("Member_Gender") != null ? pc.getDetails().get("Member_Gender") : "").equalsIgnoreCase("1")) {
                profilepic.setImageResource(R.drawable.child_boy_infant);
//                newborn_or_fp.setText("Family Planning");

            } else {
                profilepic.setImageResource(R.drawable.child_girl_infant);
//                newborn_or_fp.setVisibility(View.INVISIBLE);

            }
            if (pc.getDetails().get("profilepic") != null) {
                HH_member_SmartRegisterActivity.setImagetoHolderFromUri((Activity)context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.householdload);
            }
       }
       village.setText(humanize((pc.getDetails().get("Mem_Village_Name") != null ? (pc.getDetails().get("Mem_Village_Name")+",") : "").replace("+", "_")) + humanize((pc.getDetails().get("Mem_Mauzapara") != null ? pc.getDetails().get("Mem_Mauzapara") : "").replace("+", "_")));

//        date_of_birth.setText(pc.getDetails().get("Member_Birth_Date") != null ? pc.getDetails().get("Member_Birth_Date") : "");

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
        date_of_birth.setText((pc.getColumnmaps().get("Calc_Dob_Confirm") != null ? pc.getColumnmaps().get("Calc_Dob_Confirm") : "")+"\n"+"("+agetodisplay+")");

        age.setVisibility(View.INVISIBLE);
        cBRID.setText("C_BRID: " + (pc.getDetails().get("Mem_BRID") != null ? pc.getDetails().get("Mem_BRID") : ""));
        mBRID.setText("M_BRID: " + (pc.getDetails().get("ELCO_BRID") != null ? pc.getDetails().get("ELCO_BRID") : ""));
        mNID.setText("M_NID:" + (pc.getDetails().get("ELCO_NID") != null ? pc.getDetails().get("ELCO_NID") : ""));

        String datetoconvert = "";
        String scheduledate = "";
        if(pc.getDetails().get("child_today")!=null){
            datetoconvert = pc.getDetails().get("child_today");
            scheduledate =  setDate(datetoconvert,7);
        }else{
            scheduledate = getDateToday();
        }

        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Child_05yr");
        singleALertButtonView(alertlist_for_client,follow_up,pc,datetoconvert,scheduledate);
        constructvaccineVisitStatusBlock(pc,itemView);
        constructRiskFlagView(pc,itemView);
        itemView.setLayoutParams(clientViewLayoutParams);

    }

    private void constructvaccineVisitStatusBlock(CommonPersonObjectClient pc, View itemview) {
//        vaccines_2
        ImageView lastvaccinestick = (ImageView) itemview.findViewById(R.id.childVaccinesTick);
        TextView lastvaccinetext = (TextView) itemview.findViewById(R.id.childVaccines);


        lastvaccinetext.setVisibility(View.INVISIBLE);
        lastvaccinestick.setVisibility(View.INVISIBLE);
        if (!(pc.getDetails().get("Vaccines") != null ? pc.getDetails().get("Vaccines") : "").equalsIgnoreCase("")) {
            String lastvaccine = (pc.getDetails().get("Vaccines") != null ? pc.getDetails().get("Vaccines") : "");
            lastvaccine = lastvaccine.trim();
            lastvaccine = lastvaccine.replace(" ",",");
            lastvaccinestick.setVisibility(View.VISIBLE);
            lastvaccinetext.setVisibility(View.VISIBLE);
            lastvaccinetext.setText(lastvaccine);
        }


    }


    public void singleALertButtonView(List<Alert> alertlist_for_client,TextView due_visit_date, CommonPersonObjectClient smartRegisterClient,String textforComplete,String textfornotcomplete){
        if(alertlist_for_client.size() == 0 ){
            if(smartRegisterClient.getDetails().get("child_today")==null){
                due_visit_date.setText(textfornotcomplete);
                due_visit_date.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                due_visit_date.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                due_visit_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                due_visit_date.setOnClickListener(onClickListener);
                due_visit_date.setTag(smartRegisterClient);
            }else {
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
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_dgfp_child_client, null);
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
    public String getDateToday() {

        Date lastdate = new Date();

        if(lastdate!=null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
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

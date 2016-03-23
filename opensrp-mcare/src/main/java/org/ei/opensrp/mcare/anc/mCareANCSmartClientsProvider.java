package org.ei.opensrp.mcare.anc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.ParseException;
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
public class mCareANCSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public mCareANCSmartClientsProvider(Context context,
                                        View.OnClickListener onClickListener,
                                        CommonPersonObjectController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public View getView(final SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewGroup itemView;

        itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_mcare_anc_client, null);
        LinearLayout profileinfolayout = (LinearLayout)itemView.findViewById(R.id.profile_info_layout);

//        ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);
        TextView name = (TextView)itemView.findViewById(R.id.name);
        TextView spousename = (TextView)itemView.findViewById(R.id.spousename);
        TextView gobhhid = (TextView)itemView.findViewById(R.id.gobhhid);
        TextView jivitahhid = (TextView)itemView.findViewById(R.id.jivitahhid);
        TextView village = (TextView)itemView.findViewById(R.id.village);
        TextView age = (TextView)itemView.findViewById(R.id.age);
        TextView nid = (TextView)itemView.findViewById(R.id.nid);
        TextView brid = (TextView)itemView.findViewById(R.id.brid);
        TextView edd = (TextView)itemView.findViewById(R.id.edd);
//        TextView psrfdue = (TextView)itemView.findViewById(R.id.psrf_due_date);
////        Button due_visit_date = (Button)itemView.findViewById(R.id.hh_due_date);
//
//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

//        if(pc.getDetails().get("profilepic")!=null){
//            HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.woman_placeholder);
//        }
//
//        id.setText(pc.getDetails().get("case_id")!=null?pc.getCaseId():"");
        name.setText(pc.getDetails().get("FWWOMFNAME")!=null?pc.getDetails().get("FWWOMFNAME"):"");
        spousename.setText(pc.getDetails().get("FWHUSNAME")!=null?pc.getDetails().get("FWHUSNAME"):"");
        gobhhid.setText(pc.getDetails().get("GOBHHID")!=null?pc.getDetails().get("GOBHHID"):"");
        jivitahhid.setText(pc.getDetails().get("JiVitAHHID")!=null?pc.getDetails().get("JiVitAHHID"):"");
        village.setText(humanize((pc.getDetails().get("mauza") != null ? pc.getDetails().get("mauza") : "").replace("+", "_")));
//
//
//
        age.setText(pc.getDetails().get("FWWOMAGE")!=null?pc.getDetails().get("FWWOMAGE"):"");
        nid.setText("NID :" +(pc.getDetails().get("FWWOMNID")!=null?pc.getDetails().get("FWWOMNID"):""));
        brid.setText("BRID :" +(pc.getDetails().get("FWWOMBID")!=null?pc.getDetails().get("FWWOMBID"):""));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(pc.getDetails().get("FWPSRLMP")!=null?pc.getDetails().get("FWPSRLMP"):"");
            GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(edd_date);
                calendar.add(Calendar.DATE, 259);
                edd_date.setTime(calendar.getTime().getTime());
            edd.setText("EDD :" + format.format(edd_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        constructRiskFlagView(pc,itemView);
        constructANCReminderDueBlock(pc, itemView);
        constructNBNFDueBlock(pc, itemView);
        constructAncVisitStatusBlock(pc,itemView);




        itemView.setLayoutParams(clientViewLayoutParams);
        return itemView;
    }

    private void constructAncVisitStatusBlock(CommonPersonObjectClient pc, View itemview) {
        TextView anc1tick = (TextView)itemview.findViewById(R.id.anc1tick);
        TextView anc1text = (TextView)itemview.findViewById(R.id.anc1text);
        TextView anc2tick = (TextView)itemview.findViewById(R.id.anc2tick);
        TextView anc2text = (TextView)itemview.findViewById(R.id.anc2text);
        TextView anc3tick = (TextView)itemview.findViewById(R.id.anc3tick);
        TextView anc3text = (TextView)itemview.findViewById(R.id.anc3text);
        TextView anc4tick = (TextView)itemview.findViewById(R.id.anc4tick);
        TextView anc4text = (TextView)itemview.findViewById(R.id.anc4text);
        checkAnc1StatusAndform(anc1tick,anc1text,pc);
        checkAnc2StatusAndform(anc2tick, anc2text, pc);
        checkAnc3StatusAndform(anc3tick, anc3text, pc);
        checkAnc4StatusAndform(anc4tick, anc4text, pc);


    }



    private void checkAnc1StatusAndform(TextView anc1tick, TextView anc1text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("FWANC1DATE")!=null){
            anc1text.setText("ANC1-"+pc.getDetails().get("FWANC1DATE"));
            if(pc.getDetails().get("anc1_current_formStatus")!=null){
                if(pc.getDetails().get("anc1_current_formStatus").equalsIgnoreCase("upcoming")){

                }else if(pc.getDetails().get("anc1_current_formStatus").equalsIgnoreCase("urgent")){
                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc1text.setText("urgent");
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_1");
            String alertstate = "";
            String alertDate = "";
            if(alertlist.size()!=0){
                for(int i = 0;i<alertlist.size();i++){
                    alertstate = alertlist.get(i).status().value();
                    alertDate = alertlist.get(i).startDate();
                }              ;
            }
            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc1tick.setText("✘");
                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc1text.setText( "ANC1-" + alertDate);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                }else {
                    anc1text.setVisibility(View.GONE);
                    anc1tick.setVisibility(View.GONE);
                }
            } else {
                anc1text.setVisibility(View.GONE);
                anc1tick.setVisibility(View.GONE);
            }
        }
    }
    private void checkAnc2StatusAndform(TextView anc2tick, TextView anc2text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("FWANC2DATE")!=null){
            anc2text.setText("ANC2-"+pc.getDetails().get("FWANC2DATE"));
            if(pc.getDetails().get("ANC2_current_formStatus")!=null){
                if(pc.getDetails().get("ANC2_current_formStatus").equalsIgnoreCase("upcoming")){

                }else if(pc.getDetails().get("ANC2_current_formStatus").equalsIgnoreCase("urgent")){
                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_2");
            String alertstate = "";
            String alertDate = "";
            if(alertlist.size()!=0){
                for(int i = 0;i<alertlist.size();i++){
                    alertstate = alertlist.get(i).status().value();
                    alertDate = alertlist.get(i).startDate();
                }              ;
            }
            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc2tick.setText("✘");
                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc2text.setText( "ANC2-" + alertDate);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                }else {
                    anc2text.setVisibility(View.GONE);
                    anc2tick.setVisibility(View.GONE);
                }
            } else {
                anc2text.setVisibility(View.GONE);
                anc2tick.setVisibility(View.GONE);
            }
        }
    }
    private void checkAnc3StatusAndform(TextView anc3tick, TextView anc3text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("FWANC3DATE")!=null){
            anc3text.setText("ANC3-"+pc.getDetails().get("FWANC3DATE"));
            if(pc.getDetails().get("ANC3_current_formStatus")!=null){
                if(pc.getDetails().get("ANC3_current_formStatus").equalsIgnoreCase("upcoming")){

                }else if(pc.getDetails().get("ANC3_current_formStatus").equalsIgnoreCase("urgent")){
                    anc3tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_3");
            String alertstate = "";
            String alertDate = "";
            if(alertlist.size()!=0){
                for(int i = 0;i<alertlist.size();i++){
                    alertstate = alertlist.get(i).status().value();
                    alertDate = alertlist.get(i).startDate();
                }              ;
            }
            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc3tick.setText("✘");
                    anc3tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc3text.setText( "ANC3-" + alertDate);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                }else {
                    anc3text.setVisibility(View.GONE);
                    anc3tick.setVisibility(View.GONE);
                }
            } else {
                anc3text.setVisibility(View.GONE);
                anc3tick.setVisibility(View.GONE);
            }
        }
    }
    private void checkAnc4StatusAndform(TextView anc4tick, TextView anc4text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("FWANC4DATE")!=null){
            anc4text.setText("ANC4-"+pc.getDetails().get("FWANC4DATE"));
            if(pc.getDetails().get("ANC4_current_formStatus")!=null){
                if(pc.getDetails().get("ANC4_current_formStatus").equalsIgnoreCase("upcoming")){

                }else if(pc.getDetails().get("ANC4_current_formStatus").equalsIgnoreCase("urgent")){
                    anc4tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_4");
            String alertstate = "";
            String alertDate = "";
            if(alertlist.size()!=0){
                for(int i = 0;i<alertlist.size();i++){
                    alertstate = alertlist.get(i).status().value();
                    alertDate = alertlist.get(i).startDate();
                }              ;
            }
            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc4tick.setText("✘");
                    anc4tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc4text.setText( "ANC4-" + alertDate);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                }else {
                    anc4text.setVisibility(View.GONE);
                    anc4tick.setVisibility(View.GONE);
                }
            } else {
                anc4text.setVisibility(View.GONE);
                anc4tick.setVisibility(View.GONE);
            }
        }
    }

    private void constructNBNFDueBlock(CommonPersonObjectClient pc, ViewGroup itemView) {
        alertTextandStatus alerttextstatus = null;
        List <Alert>alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(),"BirthNotificationPregnancyStatusFollowUp");
        if(alertlist.size() != 0){
            alerttextstatus = setAlertStatus("",alertlist);
        }else{
            alerttextstatus = new alertTextandStatus("Not synced","not synced");
        }
        CustomFontTextView NBNFDueDate = (CustomFontTextView)itemView.findViewById(R.id.nbnf_due_date);
        setalerttextandColorInView(NBNFDueDate, alerttextstatus, pc);

    }

    private void constructANCReminderDueBlock(CommonPersonObjectClient pc, ViewGroup itemView) {
        alertTextandStatus alerttextstatus = null;
        List <Alert>alertlist4 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(),"ancrv_4");
        if(alertlist4.size() != 0){
            alerttextstatus = setAlertStatus("ANC4",alertlist4);
        }else {
            List<Alert> alertlist3 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_3");
            if(alertlist3.size() != 0){
                alerttextstatus = setAlertStatus("ANC3",alertlist3);
            }else{
                List<Alert> alertlist2 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_2");
                if(alertlist2.size()!=0){
                    alerttextstatus = setAlertStatus("ANC2",alertlist2);
                }else{
                    List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "ancrv_1");
                    if(alertlist.size()!=0){
                        alerttextstatus = setAlertStatus("ANC1",alertlist);

                    }else{
                        alerttextstatus = new alertTextandStatus("Not synced","not synced");
                    }
                }
            }
        }
        CustomFontTextView ancreminderDueDate = (CustomFontTextView)itemView.findViewById(R.id.anc_reminder_due_date);
        setalerttextandColorInView(ancreminderDueDate, alerttextstatus,pc);


    }

    private void setalerttextandColorInView(CustomFontTextView customFontTextView, alertTextandStatus alerttextstatus, CommonPersonObjectClient pc) {
        customFontTextView.setText(alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("normal")){
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("upcoming")){
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTag(R.id.textforAncRegister, alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setTag(R.id.AlertStatustextforAncRegister,"upcoming");
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("urgent")){
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTag(R.id.textforAncRegister,alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
            customFontTextView.setTag(R.id.AlertStatustextforAncRegister, "urgent");

        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("expired")){
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
            customFontTextView.setText("expired");
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("complete")){
//               psrfdue.setText("visited");
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("not synced")){
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
//
        }
    }

    private alertTextandStatus setAlertStatus(String anc, List<Alert> alertlist) {
        alertTextandStatus alts = null;
        for(int i = 0;i<alertlist.size();i++){
            alts = new alertTextandStatus(anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value());
            }
        return alts;
    }

    private void constructRiskFlagView(CommonPersonObjectClient pc, ViewGroup itemView) {
//        AllCommonsRepository allancRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother");
//        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
//        AllCommonsRepository allelcorep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
//        CommonPersonObject elcoparent = allelcorep.findByCaseID(ancobject.getRelationalId());

        ImageView hrp = (ImageView)itemView.findViewById(R.id.hrp);
        ImageView hp = (ImageView)itemView.findViewById(R.id.hr);
        ImageView vg = (ImageView)itemView.findViewById(R.id.vg);
        if(pc.getDetails().get("FWVG") != null && pc.getDetails().get("FWVG").equalsIgnoreCase("1")){

        }else{
            vg.setVisibility(View.GONE);
        }
        if(pc.getDetails().get("FWHRP") != null && pc.getDetails().get("FWHRP").equalsIgnoreCase("1")){

        }else{
            hrp.setVisibility(View.GONE);
        }
        if(pc.getDetails().get("FWHR_PSR") != null && pc.getDetails().get("FWHR_PSR").equalsIgnoreCase("1")){

        }else{
            hp.setVisibility(View.GONE);
        }

//        if(pc.getDetails().get("FWWOMAGE")!=null &&)

    }

    @Override
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

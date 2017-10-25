package org.ei.opensrp.dgfp.pnc;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
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
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.dgfp.R;
import org.ei.opensrp.dgfp.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.dgfp.application.dgfpApplication;
import org.ei.opensrp.dgfp.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.dgfp.child.ChildDetailActivity.calculateage;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class mCarePNCSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final AlertService alertService;


    public mCarePNCSmartClientsProvider(Context context,
                                        View.OnClickListener onClickListener,
                                        AlertService alertService) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.alertService = alertService;
        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public void getView(final SmartRegisterClient smartRegisterClient, View convertView) {
        View itemView;

        itemView = convertView;

//        itemView = (ViewGroup) inflater().inflate(R.layout.smart_register_mcare_pnc_client, null);
        LinearLayout profileinfolayout = (LinearLayout)itemView.findViewById(R.id.profile_info_layout);

        ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);
        TextView name = (TextView)itemView.findViewById(R.id.name);
        TextView spousename = (TextView)itemView.findViewById(R.id.spousename);
        TextView gobhhid = (TextView)itemView.findViewById(R.id.gobhhid);
        TextView coupleno = (TextView)itemView.findViewById(R.id.coupleno);
        TextView village = (TextView)itemView.findViewById(R.id.village);
        TextView age = (TextView)itemView.findViewById(R.id.age);
        TextView nid = (TextView)itemView.findViewById(R.id.nid);
        TextView brid = (TextView)itemView.findViewById(R.id.brid);
        TextView delivery_outcome = (TextView)itemView.findViewById(R.id.deliveryoutcome);
        TextView dateofdelivery = (TextView)itemView.findViewById(R.id.date_of_outcome);
        TextView typeofdelivery = (TextView)itemView.findViewById(R.id.deliverytype);
        TextView typeofbirth = (TextView)itemView.findViewById(R.id.birthtype);
//        TextView psrfdue = (TextView)itemView.findViewById(R.id.psrf_due_date);
////        Button due_visit_date = (Button)itemView.findViewById(R.id.hh_due_date);
//
//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);


        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(pc);

        if (pc.getDetails().get("profilepic") != null) {
            HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.householdload);
        } else {
            profilepic.setImageResource(R.drawable.woman_placeholder);
        }

//        if(pc.getDetails().get("profilepic")!=null){
//            HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.mipmap.woman_placeholder);
//        }
//
//        id.setText(pc.getDetails().get("case_id")!=null?pc.getCaseId():"");
        name.setText(humanize(pc.getColumnmaps().get("Mem_F_Name")!=null?pc.getColumnmaps().get("Mem_F_Name"):""));
        if(pc.getDetails().get("Spouse_Name") != null){
            spousename.setText(humanize(pc.getDetails().get("Spouse_Name").length() > 0 ? pc.getDetails().get("Spouse_Name"):"N/A"));
        }
        gobhhid.setText(" "+(pc.getDetails().get("Member_GoB_HHID")!=null?pc.getDetails().get("Member_GoB_HHID"):""));
        coupleno.setText(pc.getDetails().get("Couple_No")!=null?pc.getDetails().get("Couple_No"):"");
        village.setText((humanize((pc.getDetails().get("Mem_Village_Name") != null ? pc.getDetails().get("Mem_Village_Name") : "").replace("+", "_")))+","+(humanize((pc.getDetails().get("Mem_Mauzapara") != null ? pc.getDetails().get("Mem_Mauzapara") : "").replace("+", "_"))));
//
//
//
        age.setText("("+(pc.getDetails().get("Calc_Age_Confirm")!=null?pc.getDetails().get("Calc_Age_Confirm"):"")+") ");
        String agetodisplay = "";
        try {
            String datetocalc = "";
            if(datetocalc.equalsIgnoreCase("")){
                datetocalc = (pc.getColumnmaps().get("Member_Birth_Date") != null ?  pc.getColumnmaps().get("Member_Birth_Date")  : "");
            }
            DateUtil.setDefaultDateFormat("yyyy-MM-dd");
            int days = DateUtil.dayDifference(DateUtil.getLocalDate(datetocalc), DateUtil.today());
            int calc_age = days / 365;
            agetodisplay = calculateage(days);
        }catch (Exception e){

        }
        age.setText(agetodisplay);
        DateUtil.setDefaultDateFormat("yyyy-MM-dd");



//         org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");
        CommonRepository allmemberRepository =org.ei.opensrp.Context.getInstance().commonrepository("members");
        ArrayList <CommonPersonObject> childrenmembers = (ArrayList<CommonPersonObject>) allmemberRepository.readAllcommon(allmemberRepository.RawCustomQueryForAdapter("Select * from members where  (members.Mem_F_Name not null ) AND details like '%\"Child\":\"1\"%'  and Not (details like '%\"Visit_Status\":\"10\"%' or details like '%\"Visit_Status\":\"11\"%')and (details like '%\"mother_UUID\":\""+pc.entityId()+"\"%')"));
        String premature_birth = "";
        for(int i = 0;i<childrenmembers.size();i++){
            premature_birth = (childrenmembers.get(i).getDetails().get("Premature_Birth")!=null?childrenmembers.get(i).getDetails().get("Premature_Birth"):"");
        }
        if(premature_birth.equalsIgnoreCase("0")){
            premature_birth = "Birth: "+"Normal";
        }else if(premature_birth.equalsIgnoreCase("1")){
            premature_birth = "Birth: "+"Premature";
        }

//        CommonPersonObject childobject = allmotherRepository.findByCaseID(smartRegisterClient.entityId());
//        AllCommonsRepository elcorep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
//        final CommonPersonObject elcoObject = elcorep.findByCaseID(childobject.getRelationalId());
//        try {
//            int days = DateUtil.dayDifference(DateUtil.getLocalDate((elcoObject.getDetails().get("FWBIRTHDATE") != null ?  elcoObject.getDetails().get("FWBIRTHDATE")  : "")), DateUtil.today());
//            Log.v("days",""+days);
//            int calc_age = days / 365;
//            age.setText("("+calc_age+") ");
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        dateofdelivery.setText("DOO: "+(pc.getDetails().get("DOO")!=null?pc.getDetails().get("DOO"):""));
        String outcomevalue = pc.getDetails().get("Visit_Status")!=null?pc.getDetails().get("Visit_Status"):"";

        if(outcomevalue.equalsIgnoreCase("3")){
            delivery_outcome.setText("Outcome: "+context.getString(R.string.mcare_pnc_liveBirth));
        }else if (outcomevalue.equalsIgnoreCase("4")){
            delivery_outcome.setText("Outcome: "+context.getString(R.string.mcare_pnc_Stillbirth));
        }

        String deliveryvalue = pc.getDetails().get("Delivery_Type")!=null?pc.getDetails().get("Delivery_Type"):"";
        if(deliveryvalue.equalsIgnoreCase("1")){
            typeofdelivery.setText("Delivery: "+"Normal");
        }else if (deliveryvalue.equalsIgnoreCase("2")){
            typeofdelivery.setText("Delivery: "+"Caesarean");
        }else{
            typeofdelivery.setText("");
        }
//        typeofdelivery.setText(pc.getDetails().get("Delivery_Type")!=null?pc.getDetails().get("Delivery_Type"):"");






        typeofbirth.setText(premature_birth);


//        delivery_outcome.setText(pc.getDetails().get("FWBNFSTS")!=null?pc.getDetails().get("FWBNFSTS"):"");

        if((pc.getDetails().get("Mem_NID") != null ? pc.getDetails().get("Mem_NID") : "").length()>0) {
            nid.setText(Html.fromHtml("NID:" + " " + (pc.getDetails().get("Mem_NID") != null ? pc.getDetails().get("Mem_NID") : "") ));
            nid.setVisibility(View.VISIBLE);
        }else{
            nid.setVisibility(View.GONE);
        }
        if((pc.getDetails().get("Mem_BRID") != null ? pc.getDetails().get("Mem_BRID") : "").length()>0) {
            brid.setText(Html.fromHtml("BRID:" + " " + (pc.getDetails().get("Mem_BRID") != null ? pc.getDetails().get("Mem_BRID") : "")));
            brid.setVisibility(View.VISIBLE);
        }else{
            brid.setVisibility(View.GONE);
        }
          try {
//                    dateofdelivery.setText(Html.fromHtml("DOO:" +"<b> "+ (pc.getColumnmaps().get("FWBNFDTOO") != null ? pc.getColumnmaps().get("FWBNFDTOO") : "")+ "</b>"));
                   dateofdelivery.setText(Html.fromHtml("DOO" +": "+ doolay(pc)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        itemView.setLayoutParams(clientViewLayoutParams);
        constructRiskFlagView(pc, itemView);
        constructPNCReminderDueBlock((pc.getDetails().get("DOO") != null ? pc.getDetails().get("DOO") : ""),pc, itemView);
        constructNBNFDueBlock(pc, itemView);
        constructPncVisitStatusBlock(pc,itemView);
    }
    private void constructNBNFDueBlock(final CommonPersonObjectClient pc, View itemView) {

        CustomFontTextView NBNFDueDate = (CustomFontTextView)itemView.findViewById(R.id.nbnf_due_date);
//        ChildRegistraton_Today
        AllCommonsRepository allmemberRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");
        CommonPersonObject childobject = allmemberRepository.findByCaseID(pc.entityId());
        allmemberRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("household");
        CommonPersonObject householdobject = allmemberRepository.findByCaseID(childobject.getRelationalId());

        if (pc.getDetails().get("Visit_Status").equalsIgnoreCase("3")&&(pc.getDetails().get("is_child_register_done")!=null)) {
            if(pc.getDetails().get("is_child_register_done").equalsIgnoreCase("0")){
                NBNFDueDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                NBNFDueDate.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                NBNFDueDate.setText("Register Child");
                NBNFDueDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mCarePNCSmartRegisterActivity.childtoRegisterEntityID = pc.entityId();
                        AllCommonsRepository allmemberRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");

                        CommonPersonObject childobject = allmemberRepository.findByCaseID(pc.entityId());

                        JSONObject overridejsonobject = new JSONObject();
                        try {
                            overridejsonobject.put("mother_relational_ID", pc.entityId());
                            overridejsonobject.put("existing_DOO", doolay(pc));
                            overridejsonobject.put("existing_Spouse_Name", pc.getDetails().get("Spouse_Name"));
                            overridejsonobject.put("existing_Mem_F_Name", pc.getColumnmaps().get("Mem_F_Name"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());


                        ((mCarePNCSmartRegisterActivity) ((Activity) context)).startFormActivity("childregistration", childobject.getRelationalId(), fieldOverrides.getJSONString());


                    }
                });
            }else if (pc.getDetails().get("is_child_register_done").equalsIgnoreCase("1")){
                NBNFDueDate.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green));
                NBNFDueDate.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                NBNFDueDate.setText("Child Registered");
            }
        }else{

                NBNFDueDate.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                NBNFDueDate.setTextColor(context.getResources().getColor(R.color.text_black));
                NBNFDueDate.setText("Not applicable");


        }
        NBNFDueDate.setText(dgfpApplication.convertToEnglishDigits(NBNFDueDate.getText().toString()));

    }
    private String doolay(CommonPersonObjectClient ancclient) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getDetails().get("DOO")!=null?ancclient.getDetails().get("DOO"):"");
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(edd_date);
            edd_date.setTime(calendar.getTime().getTime());
            return dgfpApplication.convertToEnglishDigits(format.format(edd_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }

    private void constructPncVisitStatusBlock(CommonPersonObjectClient pc, View itemview) {
        ImageView pnc1tick = (ImageView)itemview.findViewById(R.id.pnc1tick);
        TextView anc1text = (TextView)itemview.findViewById(R.id.pnc1text);
        ImageView pnc2tick = (ImageView)itemview.findViewById(R.id.pnc2tick);
        TextView anc2text = (TextView)itemview.findViewById(R.id.pnc2text);
        ImageView pnc3tick = (ImageView)itemview.findViewById(R.id.pnc3tick);
        TextView anc3text = (TextView)itemview.findViewById(R.id.pnc3text);

//        anc1tick.setVisibility(View.GONE);
//        anc1text.setVisibility(View.GONE);
//        anc2tick.setVisibility(View.GONE);
//        anc2text.setVisibility(View.GONE);
//        anc3tick.setVisibility(View.GONE);
//        anc3text.setVisibility(View.GONE);
//        TextView anc4tick = (TextView)itemview.findViewById(R.id.pnc4tick);
//        TextView anc4text = (TextView)itemview.findViewById(R.id.pnc4text);
        checkPnc1StatusAndform(pnc1tick, anc1text, pc);
        checkPnc2StatusAndform(pnc2tick, anc2text, pc);
        checkPnc3StatusAndform(pnc3tick, anc3text, pc);


    }



    private void checkPnc1StatusAndform(ImageView anc1tick, TextView anc1text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("PNC1_Due_Date")!=null){
            anc1text.setText("PNC1: "+pc.getDetails().get("PNC1_Due_Date"));
            if(pc.getDetails().get("pnc1_current_formStatus")!=null){
                if(pc.getDetails().get("pnc1_current_formStatus").equalsIgnoreCase("upcoming")){
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
//                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
                    anc1tick.setImageResource(R.mipmap.doneintime);
                }else if(pc.getDetails().get("pnc1_current_formStatus").equalsIgnoreCase("urgent")){
//                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc1tick.setImageResource(R.mipmap.notdoneintime);
                    anc1text.setText("urgent");
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_1");
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
                    anc1tick.setImageResource(R.mipmap.cross);
//                    anc1tick.setText("✘");
//                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc1text.setText( "PNC1: " + alertDate);
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
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
    private void checkPnc2StatusAndform(ImageView anc2tick, TextView anc2text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("PNC2_Due_Date")!=null){
            anc2text.setText("PNC2: "+pc.getDetails().get("PNC2_Due_Date"));
            if(pc.getDetails().get("pnc2_current_formStatus")!=null){
                if(pc.getDetails().get("pnc2_current_formStatus").equalsIgnoreCase("upcoming")){
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);
//                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_complete_green));
                    anc2tick.setImageResource(R.mipmap.doneintime);


                }else if(pc.getDetails().get("pnc2_current_formStatus").equalsIgnoreCase("urgent")){
                    anc2tick.setImageResource(R.mipmap.notdoneintime);
//                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_2");
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
                    anc2tick.setImageResource(R.mipmap.cross);
//                    anc2tick.setText("✘");
//                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc2text.setText( "PNC2: " + alertDate);
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);
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
    private void checkPnc3StatusAndform(ImageView anc3tick, TextView anc3text, CommonPersonObjectClient pc) {
        if(pc.getDetails().get("PNC3_Due_Date")!=null){
            anc3text.setText("PNC3: "+pc.getDetails().get("PNC3_Due_Date"));
            if(pc.getDetails().get("pnc3_current_formStatus")!=null){
                if(pc.getDetails().get("pnc3_current_formStatus").equalsIgnoreCase("upcoming")){
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);
                    anc3tick.setImageResource(R.mipmap.doneintime);
//                    anc3tick.setTextColor(context.getResources().getColor(R.color.alert_complete_green));


                }else if(pc.getDetails().get("pnc3_current_formStatus").equalsIgnoreCase("urgent")){
                    anc3tick.setImageResource(R.mipmap.notdoneintime);
//                    anc3tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);
                }
            }
        }else{
            List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_3");
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
                    anc3tick.setImageResource(R.mipmap.cross);
//                    anc3tick.setText("✘");
//                    anc3tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc3text.setText( "PNC3: " + alertDate);
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);
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

    private void constructPNCReminderDueBlock(String dateofoutcome,CommonPersonObjectClient pc, View itemView) {
        alertTextandStatus alerttextstatus = null;
            List<Alert> alertlist3 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_3");
            if(alertlist3.size() != 0){
                alerttextstatus = setAlertStatus(dateofoutcome,"PNC3",alertlist3);
            }else{
                List<Alert> alertlist2 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_2");
                if(alertlist2.size()!=0){
                    alerttextstatus = setAlertStatus(dateofoutcome,"PNC2",alertlist2);
                }else{
                    List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "pncrv_1");
                    if(alertlist.size()!=0){
                        alerttextstatus = setAlertStatus(dateofoutcome,"PNC1",alertlist);

                    }else{
                        alerttextstatus = new alertTextandStatus("Not synced","not synced");
                    }
                }
            }

        CustomFontTextView pncreminderDueDate = (CustomFontTextView)itemView.findViewById(R.id.pnc_reminder_due_date);
        setalerttextandColorInView(pncreminderDueDate, alerttextstatus,pc);
        pncreminderDueDate.setText(dgfpApplication.convertToEnglishDigits(pncreminderDueDate.getText().toString()));


    }

    private void setalerttextandColorInView(CustomFontTextView customFontTextView, alertTextandStatus alerttextstatus, CommonPersonObjectClient pc) {
        customFontTextView.setText(alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("normal")){
            customFontTextView.setTextColor(context.getResources().getColor(R.color.text_black));
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("upcoming")){
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            customFontTextView.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTag(R.id.textforPncRegister, alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setTag(R.id.AlertStatustextforPncRegister,"upcoming");
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("urgent")){
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            customFontTextView.setTag(R.id.textforPncRegister,alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
            customFontTextView.setTag(R.id.AlertStatustextforPncRegister, "urgent");

        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("expired")){
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
            customFontTextView.setText("expired");
            customFontTextView.setTextColor(context.getResources().getColor(R.color.text_black));
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("complete")){
//               psrfdue.setText("visited");
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
            customFontTextView.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("not synced")){
            customFontTextView.setText("Not Synced");
            customFontTextView.setTextColor(context.getResources().getColor(R.color.text_black));
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
//
        }
    }

    private alertTextandStatus setAlertStatus(String dateofoutcome,String pnc, List<Alert> alertlist) {
        alertTextandStatus alts = null;
        for(int i = 0;i<alertlist.size();i++){
            if(pnc.equalsIgnoreCase("PNC1")){
                alts = new alertTextandStatus(pnc+ "\n"+pncdate(dateofoutcome,0),alertlist.get(i).status().value());
            }else  if(pnc.equalsIgnoreCase("PNC2")){
                alts = new alertTextandStatus(pnc+ "\n"+pncdate(dateofoutcome,2),alertlist.get(i).status().value());
            }else  if(pnc.equalsIgnoreCase("PNC3")){
                alts = new alertTextandStatus(pnc+ "\n"+pncdate(dateofoutcome,6),alertlist.get(i).status().value());
            }
//            alts = new alertTextandStatus(pnc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value());
            }
        return alts;
    }

    private void constructRiskFlagView(CommonPersonObjectClient pc, View itemView) {


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
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return null;
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
        return (ViewGroup) inflater().inflate(R.layout.smart_register_dgfp_pnc_client, null);
    }
    public String pncdate(String date,int day){
        String pncdate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date pnc_date = format.parse(date);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(pnc_date);
            calendar.add(Calendar.DATE, day);
            pnc_date.setTime(calendar.getTime().getTime());
            pncdate = format.format(pnc_date);
        } catch (Exception e) {
            e.printStackTrace();
            pncdate = "";
        }
        return pncdate;
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

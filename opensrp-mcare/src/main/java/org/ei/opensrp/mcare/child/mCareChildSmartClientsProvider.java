package org.ei.opensrp.mcare.child;

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
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.mcare.application.McareApplication;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
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
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class mCareChildSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final AlertService alertService;


    public mCareChildSmartClientsProvider(Context context,
                                          View.OnClickListener onClickListener,
                                          AlertService alertService) {
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
        View itemView = convertView;

        LinearLayout profileinfolayout = (LinearLayout) itemView.findViewById(R.id.profile_info_layout);

//        ImageView profilepic = (ImageView)itemView.findViewById(R.id.profilepic);
        TextView mothername = (TextView) itemView.findViewById(R.id.mother_name);
        TextView fathername = (TextView) itemView.findViewById(R.id.father_name);
        TextView gobhhid = (TextView) itemView.findViewById(R.id.gobhhid);
        TextView jivitahhid = (TextView) itemView.findViewById(R.id.jivitahhid);
        TextView village = (TextView) itemView.findViewById(R.id.village);
        TextView age = (TextView) itemView.findViewById(R.id.age);
        TextView nid = (TextView) itemView.findViewById(R.id.nid);
        TextView brid = (TextView) itemView.findViewById(R.id.brid);
        TextView dateofbirth = (TextView) itemView.findViewById(R.id.dateofbirth);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;


//        TextView psrfdue = (TextView)itemView.findViewById(R.id.psrf_due_date);
////        Button due_visit_date = (Button)itemView.findViewById(R.id.hh_due_date);
//
        profileinfolayout.setTag(smartRegisterClient);

//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);
        profileinfolayout.setOnClickListener(onClickListener);

        AllCommonsRepository childRepository =  org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("ec_mcarechild");
        CommonPersonObject childobject = childRepository.findByCaseID(pc.entityId());

        DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
        Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
        details.putAll(childobject.getColumnmaps());

        if(pc.getDetails() != null) {
            pc.getDetails().putAll(details);
        }else{
            pc.setDetails(details);
        }

        String dob = pc.getColumnmaps().get("FWBNFDTOO") != null ? pc.getColumnmaps().get("FWBNFDTOO") : null;
        fathername.setText(humanize(pc.getColumnmaps().get("FWWOMFNAME")!=null?pc.getColumnmaps().get("FWWOMFNAME"):""));
        mothername.setText(humanize(pc.getDetails().get("FWBNFCHILDNAME") != null ? pc.getDetails().get("FWBNFCHILDNAME"):""));
        gobhhid.setText(" " + (pc.getColumnmaps().get("GOBHHID") != null ? pc.getColumnmaps().get("GOBHHID") : ""));
        jivitahhid.setText(pc.getColumnmaps().get("JiVitAHHID") != null ? pc.getColumnmaps().get("JiVitAHHID") : "");
        village.setText(humanize((pc.getDetails().get("existing_Mauzapara") != null ? pc.getDetails().get("existing_Mauzapara") : "").replace("+", "_")));
        age.setText(""+age(pc)+ "d ");
        dateofbirth.setText(pc.getDetails().get("FWBNFDOB")!=null?pc.getDetails().get("FWBNFDOB"):"");
        if((pc.getDetails().get("FWWOMNID")!=null?pc.getDetails().get("FWWOMNID"):"").length()>0) {
            nid.setText("NID: " + (pc.getDetails().get("FWWOMNID") != null ? pc.getDetails().get("FWWOMNID") : ""));
            nid.setVisibility(View.VISIBLE);
        }else{
            nid.setVisibility(View.GONE);
        }
        if((pc.getDetails().get("FWWOMBID")!=null?pc.getDetails().get("FWWOMBID"):"").length()>0) {
            brid.setText("BRID: " + (pc.getDetails().get("FWWOMBID") != null ? pc.getDetails().get("FWWOMBID") : ""));
            brid.setVisibility(View.VISIBLE);
        }else{
            brid.setVisibility(View.GONE);
        }

        if(dob != null && dob.contains("T")){
            dob = dob.substring(0, dob.indexOf("T"));
        }
        dateofbirth.setText(dob);
        nid.setText("NID :" + (pc.getColumnmaps().get("FWWOMNID") != null ? pc.getColumnmaps().get("FWWOMNID") : ""));
        brid.setText("BRID :" + (pc.getColumnmaps().get("FWWOMBID") != null ? pc.getColumnmaps().get("FWWOMBID") : ""));

        Map<String, String> mcaremotherObject = detailsRepository.getAllDetailsForClient(pc.getColumnmaps().get("relationalid"));

        constructRiskFlagView(pc, mcaremotherObject, itemView);
        constructENCCReminderDueBlock(pc, itemView);
////        constructNBNFDueBlock(pc, itemView);s
        constructENCCVisitStatusBlock(pc, itemView);


        itemView.setLayoutParams(clientViewLayoutParams);
    }

    private void constructENCCVisitStatusBlock(CommonPersonObjectClient pc, View itemview) {
        ImageView encc1tick = (ImageView)itemview.findViewById(R.id.encc1tick);
        TextView encc1text = (TextView)itemview.findViewById(R.id.encc1text);
        ImageView encc2tick = (ImageView)itemview.findViewById(R.id.encc2tick);
        TextView encc2text = (TextView)itemview.findViewById(R.id.encc2text);
        ImageView encc3tick = (ImageView)itemview.findViewById(R.id.encc3tick);
        TextView encc3text = (TextView)itemview.findViewById(R.id.encc3text);

        encc1tick.setVisibility(View.GONE);
        encc1text.setVisibility(View.GONE);
        encc2tick.setVisibility(View.GONE);
        encc2text.setVisibility(View.GONE);
        encc3tick.setVisibility(View.GONE);
        encc3text.setVisibility(View.GONE);

        checkEncc1StatusAndform(encc1tick,encc1text,pc);
        checkEncc2StatusAndform(encc2tick, encc2text, pc);
        checkEncc3StatusAndform(encc3tick, encc3text, pc);


    }

    private Long age(CommonPersonObjectClient ancclient) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getColumnmaps().get("FWBNFDTOO")!=null?ancclient.getColumnmaps().get("FWBNFDTOO") :"");
            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(edd_date);


            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();

            long days = diff / (24 * 60 * 60 * 1000);

            return days;
        } catch (ParseException e) {
            Log.e(getClass().getName(), "Exception", e);
            return 0l;
        }

    }



    private void checkEncc1StatusAndform(ImageView anc1tick, TextView anc1text, CommonPersonObjectClient pc) {
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_1");
        String alertstate = "";
        String alertDate = "";
        if (alertlist.size() != 0) {
            for (int i = 0; i < alertlist.size(); i++) {
                alertstate = alertlist.get(i).status().value();
                alertDate = alertlist.get(i).startDate();
            }
            ;
        }

        if(pc.getDetails().get("FWENC1DATE")!=null){
            anc1text.setText("ENCC1: "+pc.getDetails().get("FWENC1DATE"));
            if(!alertstate.isEmpty()){
                if(alertstate.equalsIgnoreCase("upcoming")){
                    anc1tick.setImageResource(R.mipmap.doneintime);
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
                }else if(alertstate.equalsIgnoreCase("urgent")){
                    anc1tick.setImageResource(R.mipmap.notdoneintime);
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
//                    anc1tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                    anc1text.setText("urgent");
                }
            }
        } else {

            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc1tick.setImageResource(R.mipmap.cross);
                    anc1tick.setVisibility(View.VISIBLE);
                    anc1text.setVisibility(View.VISIBLE);
                    anc1text.setText( "ENCC1: " + alertDate);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                } else {
                    anc1text.setVisibility(View.GONE);
                    anc1tick.setVisibility(View.GONE);
                }
            } else {
                anc1text.setVisibility(View.GONE);
                anc1tick.setVisibility(View.GONE);
            }
        }
    }
    private void checkEncc2StatusAndform(ImageView anc2tick, TextView anc2text, CommonPersonObjectClient pc) {
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_2");
        String alertstate = "";
        String alertDate = "";
        if (alertlist.size() != 0) {
            for (int i = 0; i < alertlist.size(); i++) {
                alertstate = alertlist.get(i).status().value();
                alertDate = alertlist.get(i).startDate();
            }
            ;
        }

        if(pc.getDetails().get("FWENC2DATE")!=null){
            anc2text.setText("ENCC2: "+pc.getDetails().get("FWENC2DATE"));
            if(!alertstate.isEmpty()){
                if(alertstate.equalsIgnoreCase("upcoming")){
                    anc2tick.setImageResource(R.mipmap.doneintime);
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);
                }else if(alertstate.equalsIgnoreCase("urgent")){
                    anc2tick.setImageResource(R.mipmap.notdoneintime);
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);

//                    anc2tick.setTextColor(context.getResources().getColor(R.color.alert_urgent_red));
                }
            }
        } else {

            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc2tick.setImageResource(R.mipmap.cross);
                    anc2text.setText( "ENCC2: " + alertDate);
                    anc2tick.setVisibility(View.VISIBLE);
                    anc2text.setVisibility(View.VISIBLE);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                } else {
                    anc2text.setVisibility(View.GONE);
                    anc2tick.setVisibility(View.GONE);
                }
            } else {
                anc2text.setVisibility(View.GONE);
                anc2tick.setVisibility(View.GONE);
            }
        }
    }
    private void checkEncc3StatusAndform(ImageView anc3tick, TextView anc3text, CommonPersonObjectClient pc) {
        List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_3");
        String alertstate = "";
        String alertDate = "";
        if (alertlist.size() != 0) {
            for (int i = 0; i < alertlist.size(); i++) {
                alertstate = alertlist.get(i).status().value();
                alertDate = alertlist.get(i).startDate();
            }
            ;
        }

        if(pc.getDetails().get("FWENC3DATE")!=null){
            anc3text.setText("ENCC3: "+pc.getDetails().get("FWENC3DATE"));
            if(!alertstate.isEmpty()){
                if(alertstate.equalsIgnoreCase("upcoming")){
                    anc3tick.setImageResource(R.mipmap.doneintime);
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);
                }else if(alertstate.equalsIgnoreCase("urgent")){
                    anc3tick.setImageResource(R.mipmap.notdoneintime);
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);

                }
            }
        } else {

            if(alertstate != null && !(alertstate.trim().equalsIgnoreCase(""))){
                if(alertstate.equalsIgnoreCase("expired")){
                    anc3tick.setImageResource(R.mipmap.cross);
                    anc3text.setText( "ENCC3: " + alertDate);
                    anc3tick.setVisibility(View.VISIBLE);
                    anc3text.setVisibility(View.VISIBLE);
//                    (anc+ "-"+alertlist.get(i).startDate(),alertlist.get(i).status().value())
                } else {
                    anc3text.setVisibility(View.GONE);
                    anc3tick.setVisibility(View.GONE);
                }
            } else {
                anc3text.setVisibility(View.GONE);
                anc3tick.setVisibility(View.GONE);
            }
        }
    }

    private void constructENCCReminderDueBlock(CommonPersonObjectClient pc, View itemView) {
        alertTextandStatus alerttextstatus = null;
        List<Alert> alertlist3 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_3");
        if (alertlist3.size() != 0) {
            alerttextstatus = setAlertStatus("ENCC3", alertlist3);
        } else {
            List<Alert> alertlist2 = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_2");
            if (alertlist2.size() != 0) {
                alerttextstatus = setAlertStatus("ENCC2", alertlist2);
            } else {
                List<Alert> alertlist = org.ei.opensrp.Context.getInstance().alertService().findByEntityIdAndAlertNames(pc.entityId(), "enccrv_1");
                if (alertlist.size() != 0) {
                    alerttextstatus = setAlertStatus("ENCC1", alertlist);

                } else {
                    alerttextstatus = new alertTextandStatus("Not synced", "not synced");
                }
            }
        }

        CustomFontTextView pncreminderDueDate = (CustomFontTextView)itemView.findViewById(R.id.encc_reminder_due_date);
        pncreminderDueDate.setVisibility(View.VISIBLE);
        setalerttextandColorInView(pncreminderDueDate, alerttextstatus, pc);
        pncreminderDueDate.setText(McareApplication.convertToEnglishDigits(pncreminderDueDate.getText().toString()));


    }

    private void setalerttextandColorInView(CustomFontTextView customFontTextView, alertTextandStatus alerttextstatus, CommonPersonObjectClient pc) {
        customFontTextView.setText(alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
        if (alerttextstatus.getAlertstatus().equalsIgnoreCase("normal")) {
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
            customFontTextView.setTextColor(context.getResources().getColor(org.ei.opensrp.R.color.text_black));

        }
        if (alerttextstatus.getAlertstatus().equalsIgnoreCase("upcoming")) {
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            customFontTextView.setTextColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTag(R.id.textforEnccRegister, alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setTag(R.id.AlertStatustextforEnccRegister, "upcoming");
        }
        if (alerttextstatus.getAlertstatus().equalsIgnoreCase("urgent")) {
            customFontTextView.setOnClickListener(onClickListener);
            customFontTextView.setTag(R.id.clientobject, pc);
            customFontTextView.setTag(R.id.textforEnccRegister, alerttextstatus.getAlertText() != null ? alerttextstatus.getAlertText() : "");
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
            customFontTextView.setTextColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
            customFontTextView.setTag(R.id.AlertStatustextforEnccRegister, "urgent");

        }
        if(alerttextstatus.getAlertstatus().equalsIgnoreCase("expired")){
            customFontTextView.setTextColor(context.getResources().getColor(org.ei.opensrp.R.color.text_black));
            customFontTextView.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
            customFontTextView.setText("expired");
            customFontTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (alerttextstatus.getAlertstatus().equalsIgnoreCase("complete")) {
//               psrfdue.setText("visited");
            customFontTextView.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
            customFontTextView.setTextColor(context.getResources().getColor(org.ei.opensrp.R.color.status_bar_text_almost_white));
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

    private alertTextandStatus setAlertStatus(String anc, List<Alert> alertlist) {
        alertTextandStatus alts = null;
        for(int i = 0;i<alertlist.size();i++){
            alts = new alertTextandStatus(anc+ "\n"+alertlist.get(i).startDate(),alertlist.get(i).status().value());
            }
        return alts;
    }

    private void constructRiskFlagView(CommonPersonObjectClient pc, Map<String,String> mcaremotherObject, View itemView) {
//        AllCommonsRepository allancRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother");
//        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
//        AllCommonsRepository allelcorep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
//        CommonPersonObject elcoparent = allelcorep.findByCaseID(ancobject.getRelationalId());

        ImageView hrp = (ImageView) itemView.findViewById(R.id.hrp);
        ImageView hp = (ImageView) itemView.findViewById(R.id.hr);
        ImageView vg = (ImageView) itemView.findViewById(R.id.vg);
        if (mcaremotherObject.get("FWVG") != null && mcaremotherObject.get("FWVG").equalsIgnoreCase("1")) {

        } else {
            vg.setVisibility(View.GONE);
        }
        if (mcaremotherObject.get("FWHRP") != null && mcaremotherObject.get("FWHRP").equalsIgnoreCase("1")) {

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
        return (ViewGroup) inflater().inflate(R.layout.smart_register_mcare_child_client, null);
    }

    class alertTextandStatus {
        String alertText, alertstatus;

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

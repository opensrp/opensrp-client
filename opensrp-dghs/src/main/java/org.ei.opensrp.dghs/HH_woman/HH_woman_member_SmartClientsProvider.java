package org.ei.opensrp.dghs.HH_woman;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.dghs.R;
import org.ei.opensrp.dghs.hh_member.HouseHoldDetailActivity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
        TextView father_name = (TextView) itemView.findViewById(R.id.father_name);
        TextView husband_name = (TextView) itemView.findViewById(R.id.husband_name);
        TextView maritalstatus = (TextView) itemView.findViewById(R.id.maritalstatus);
        TextView coupleno = (TextView) itemView.findViewById(R.id.coupleno);
//        TextView pregnancystatus = (TextView)itemView.findViewById(R.id.pregnancystatus);
        TextView village = (TextView) itemView.findViewById(R.id.village);
        TextView age = (TextView) itemView.findViewById(R.id.age);
        TextView nid = (TextView) itemView.findViewById(R.id.nid);
        TextView brid = (TextView) itemView.findViewById(R.id.brid);
        TextView hid = (TextView) itemView.findViewById(R.id.hid);
        RatingBar vaccinebar = (RatingBar)itemView.findViewById(R.id.ratingBar);

        TextView pvfdue = (TextView) itemView.findViewById(R.id.pvf);
        TextView vaccinebutton = (TextView) itemView.findViewById(R.id.next_vaccine_date);
//
//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);
        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;


        name.setText(pc.getColumnmaps().get("Member_Fname") != null ? pc.getColumnmaps().get("Member_Fname") : "");
//        coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));
        father_name.setText("F: " + (pc.getDetails().get("Father_name") != null ? pc.getDetails().get("Father_name") : ""));

        hid.setText("HID: " + (pc.getDetails().get("Member_HID") != null ? pc.getDetails().get("Member_HID") : ""));
        if(!(pc.getColumnmaps().get("missedCount") != null ? pc.getColumnmaps().get("missedCount") : "").equalsIgnoreCase("")){
            int rating = Integer.parseInt((pc.getColumnmaps().get("missedCount") != null ? pc.getColumnmaps().get("missedCount") : ""));
            if(rating == 1){
                vaccinebar.setRating(1.0f);
            }else if (rating == 2){
                vaccinebar.setRating(2.0f);
            }else if (rating >= 3){
                vaccinebar.setRating(3.0f);
            }else if (rating == 0){
                vaccinebar.setRating(0.0f);
            }
        }else {
            vaccinebar.setRating(0.0f);
        }

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
        maritalstatus.setText(pc.getColumnmaps().get("Marital_status") != null ? pc.getColumnmaps().get("Marital_status") : "");


        coupleno.setVisibility(View.INVISIBLE);
        husband_name.setVisibility(View.INVISIBLE);
        if (((pc.getColumnmaps().get("Marital_status") != null ? pc.getColumnmaps().get("Marital_status") : "")).equalsIgnoreCase("1")) {
            maritalstatus.setText("M: Unmarried");
            coupleno.setVisibility(View.VISIBLE);
            husband_name.setVisibility(View.VISIBLE);
            coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));
            husband_name.setText("H: " + (pc.getDetails().get("Husband_name") != null ? pc.getDetails().get("Husband_name") : ""));

        }
        if ((pc.getColumnmaps().get("Marital_status") != null ? pc.getColumnmaps().get("Marital_status") : "").equalsIgnoreCase("2")) {
            maritalstatus.setText("M: Married");
            coupleno.setVisibility(View.VISIBLE);
            husband_name.setVisibility(View.VISIBLE);
            coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));
            husband_name.setText("H: " + (pc.getDetails().get("Husband_name") != null ? pc.getDetails().get("Husband_name") : ""));

        }
        if ((pc.getColumnmaps().get("Marital_status") != null ? pc.getColumnmaps().get("Marital_status") : "").equalsIgnoreCase("3")) {
            maritalstatus.setText("M: Divorced/Widow/Widower");
            coupleno.setVisibility(View.VISIBLE);
            husband_name.setVisibility(View.VISIBLE);
            coupleno.setText(" C: " + (pc.getDetails().get("Couple_No") != null ? pc.getDetails().get("Couple_No") : ""));
            husband_name.setText("H: " + (pc.getDetails().get("Husband_name") != null ? pc.getDetails().get("Husband_name") : ""));

        }
//
//        if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("0")){
//            pregnancystatus.setText(",Not Pregnant");
//        }
//        else if((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("1")){
//            pregnancystatus.setText(",Pregnant");
//        }else if ((pc.getColumnmaps().get("Pregnancy_Status")!=null?pc.getColumnmaps().get("Pregnancy_Status"):"").equalsIgnoreCase("9")){
//            pregnancystatus.setText("");
//        }
        village.setText("W: " + humanize((pc.getDetails().get("Member_WARD") != null ? pc.getDetails().get("Member_WARD") : "").replace("+", "_")) + ", " + "B: " + humanize((pc.getDetails().get("Member_BLOCK") != null ? pc.getDetails().get("Member_BLOCK") : "").replace("+", "_")));


        age.setText(pc.getColumnmaps().get("calc_age_confirm") != null ? "("+pc.getColumnmaps().get("calc_age_confirm")+")" : "");
//        calc_HoH_dob_confirm
        try {
            int days = DateUtil.dayDifference(DateUtil.getLocalDate((pc.getDetails().get("calc_dob_confirm") != null ?  pc.getDetails().get("calc_dob_confirm")  : "")), DateUtil.today());
            int calc_age = days / 365;
            age.setText(calc_age);
        }catch (Exception e){

        }
        nid.setText("NID: " + (pc.getDetails().get("Member_NID") != null ? pc.getDetails().get("Member_NID") : ""));
        brid.setText("BRID: " + (pc.getDetails().get("Member_BRID") != null ? pc.getDetails().get("Member_BRID") : ""));


        List<Alert> BNFalertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_BNF");
        bnfButton(BNFalertlist_for_client, pvfdue, pc);


        List<Alert> Vaccinealertlist_for_client = checkAlertListForVaccine(pc);


        vaccineButton(Vaccinealertlist_for_client, pc, vaccinebutton);


//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date edd_date = format.parse(pc.getColumnmaps().get("FWPSRLMP")!=null?pc.getColumnmaps().get("FWPSRLMP"):"");
//            GregorianCalendar calendar = new GregorianCalendar();
//                calendar.setTime(edd_date);
//                calendar.add(Calendar.DATE, 259);
//                edd_date.setTime(calendar.getTime().getTime());
//            edd.setText("EDD :" + format.format(edd_date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        constructRiskFlagView(pc, itemView);
//        constructANCReminderDueBlock(pc.getColumnmaps().get("FWPSRLMP")!=null?pc.getColumnmaps().get("FWPSRLMP"):"",pc, itemView);
//        constructNBNFDueBlock(pc, itemView);
        constructvaccineVisitStatusBlock(pc, itemView);
//        contstructNextVaccinedateBlock(pc,itemView);


        itemView.setLayoutParams(clientViewLayoutParams);
    }

    private List<Alert> checkAlertListForVaccine(CommonPersonObjectClient pc) {
        List<Alert> Woman_TT1alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_TT1");
        List<Alert> Woman_TT2alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_TT2");
        List<Alert> Woman_TT3alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_TT3");
        List<Alert> Woman_TT4alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_TT4");
        List<Alert> Woman_TT5alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "Woman_TT5");
        if (Woman_TT5alertlist_for_client.size() > 0) {
            return Woman_TT5alertlist_for_client;
        } else if (Woman_TT4alertlist_for_client.size() > 0) {
            return Woman_TT4alertlist_for_client;
        } else if (Woman_TT3alertlist_for_client.size() > 0) {
            return Woman_TT3alertlist_for_client;
        } else if (Woman_TT2alertlist_for_client.size() > 0) {
            return Woman_TT2alertlist_for_client;
        } else {
            return Woman_TT1alertlist_for_client;
        }

    }


    private void vaccineButton(List<Alert> vaccinealertlist_for_client, CommonPersonObjectClient pc, TextView vaccinebutton) {
        if (vaccinealertlist_for_client.size() == 0) {
            if((pc.getDetails().get("Is_Reg_Today") != null ? pc.getDetails().get("Is_Reg_Today") : "").equalsIgnoreCase("1")){
                vaccinebutton.setText("Register Vaccines");
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
//            pvfdue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
                vaccinebutton.setOnClickListener(onClickListener);
                vaccinebutton.setTag(R.id.clientobject, pc);
            }else {
                vaccinebutton.setText("Not Synced to Server");
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.text_black));
                vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
//            pvfdue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//                vaccinebutton.setOnClickListener(onClickListener);
//                vaccinebutton.setTag(R.id.clientobject, pc);
            }

        }
        for (int i = 0; i < vaccinealertlist_for_client.size(); i++) {
            String Schedulename = vaccinealertlist_for_client.get(i).scheduleName();
            if (Schedulename.equalsIgnoreCase("Woman_TT1")) {
                vaccinebutton.setText("TT1 \n" + (pc.getDetails().get("final_lmp") != null ? pc.getDetails().get("final_lmp") : ""));
            }
            if (Schedulename.equalsIgnoreCase("Woman_TT2")) {
                vaccinebutton.setText("TT2 \n" + setDate((pc.getDetails().get("tt1_final") != null ? pc.getDetails().get("tt1_final") : ""),28));
            }
            if (Schedulename.equalsIgnoreCase("Woman_TT3")) {
                vaccinebutton.setText("TT3 \n" + setDate((pc.getDetails().get("tt2_final") != null ? pc.getDetails().get("tt2_final") : ""),182));
            }
            if (Schedulename.equalsIgnoreCase("Woman_TT4")) {
                vaccinebutton.setText("TT4 \n" + setDate((pc.getDetails().get("tt3_final") != null ? pc.getDetails().get("tt3_final") : ""),364));
            }
            if (Schedulename.equalsIgnoreCase("Woman_TT5")) {
                vaccinebutton.setText("TT5 \n" + setDate((pc.getDetails().get("tt4_final") != null ? pc.getDetails().get("tt4_final") : ""),364));
            }

//            vaccinebutton.setText(vaccinealertlist_for_client.get(i).expiryDate());
            if (vaccinealertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                vaccinebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.text_black));
                vaccinebutton.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
            }
            if (vaccinealertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                vaccinebutton.setOnClickListener(onClickListener);
                vaccinebutton.setTag(R.id.clientobject,pc);
                vaccinebutton.setTag(R.id.clientTTSchedulename,Schedulename);
            }
            if (vaccinealertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                vaccinebutton.setOnClickListener(onClickListener);
                vaccinebutton.setTag(R.id.clientobject,pc);
                vaccinebutton.setTag(R.id.clientTTSchedulename,Schedulename);
                vaccinebutton.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
            }
            if (vaccinealertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                vaccinebutton.setTextColor(context.getResources().getColor(R.color.text_black));
                vaccinebutton.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
                vaccinebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
//            if (vaccinealertlist_for_client.get(i).isComplete()) {
//                vaccinebutton.setText("visited");
                if (Schedulename.equalsIgnoreCase("Woman_TT1") && !(pc.getDetails().get("tt1_final") != null ? pc.getDetails().get("tt1_final") : "").equalsIgnoreCase("")) {
                    vaccinebutton.setText("TT1 \n" + (pc.getDetails().get("tt1_final") != null ? pc.getDetails().get("tt1_final") : ""));
                    vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }
                if (Schedulename.equalsIgnoreCase("Woman_TT2")&& !(pc.getDetails().get("tt2_final") != null ? pc.getDetails().get("tt2_final") : "").equalsIgnoreCase("")) {
                    vaccinebutton.setText("TT2 \n" + (pc.getDetails().get("tt2_final") != null ? pc.getDetails().get("tt2_final") : ""));
                    vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }
                if (Schedulename.equalsIgnoreCase("Woman_TT3")&& !(pc.getDetails().get("tt3_final") != null ? pc.getDetails().get("tt3_final") : "").equalsIgnoreCase("")) {
                    vaccinebutton.setText("TT3 \n" + (pc.getDetails().get("tt3_final") != null ? pc.getDetails().get("tt3_final") : ""));
                    vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }
                if (Schedulename.equalsIgnoreCase("Woman_TT4")&& !(pc.getDetails().get("tt4_final") != null ? pc.getDetails().get("tt4_final") : "").equalsIgnoreCase("")) {
                    vaccinebutton.setText("TT4 \n" + (pc.getDetails().get("tt4_final") != null ? pc.getDetails().get("tt4_final") : ""));
                    vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }
                if (Schedulename.equalsIgnoreCase("Woman_TT5")&&!(pc.getDetails().get("tt5_final") != null ? pc.getDetails().get("tt5_final") : "").equalsIgnoreCase("")) {
                    vaccinebutton.setText("TT5 \n" + (pc.getDetails().get("tt5_final") != null ? pc.getDetails().get("tt5_final") : ""));
                    vaccinebutton.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    vaccinebutton.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }

//            }
        }

    }

    private void bnfButton(List<Alert> bnFalertlist_for_client, TextView pvfdue, final CommonPersonObjectClient pc) {
        if ((pc.getDetails().get("Visit_status") != null ? pc.getDetails().get("Visit_status") : "").equalsIgnoreCase("3")) {
            pvfdue.setText("Launch Birth outcome Form");
            pvfdue.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            pvfdue.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            pvfdue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AllCommonsRepository allchildRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("members");
                    CommonPersonObject childobject = allchildRepository.findByCaseID(pc.entityId());
                    AllCommonsRepository houserep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("household");
                    final CommonPersonObject householdObject = houserep.findByCaseID(childobject.getRelationalId());
                    JSONObject overridejsonobject = new JSONObject();
                    try {
                        overridejsonobject.put("existing_doo", ((pc.getDetails().get("DOO") != null ? pc.getDetails().get("DOO") : "")));
                        overridejsonobject.put("current_woman_id", ((pc.getCaseId())));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());


                    ((HH_woman_member_SmartRegisterActivity) ((Activity) context)).startFormActivity("birthoutcome", householdObject.getCaseId(), fieldOverrides.getJSONString());

                }
            });

            if (((pc.getDetails().get("outcome_active") != null ? pc.getDetails().get("outcome_active") : "").equalsIgnoreCase("1"))) {
                pvfdue.setText("Complete");
                pvfdue.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                pvfdue.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                pvfdue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        } else {
            if (bnFalertlist_for_client.size() == 0) {
                pvfdue.setText("Not Synced to Server");
                pvfdue.setTextColor(context.getResources().getColor(R.color.text_black));
                pvfdue.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
//            pvfdue.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
                pvfdue.setOnClickListener(onClickListener);
                pvfdue.setTag(pc);

            }
            for (int i = 0; i < bnFalertlist_for_client.size(); i++) {
                if (bnFalertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    pvfdue.setText(bnFalertlist_for_client.get(i).expiryDate());

                    pvfdue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    pvfdue.setTextColor(context.getResources().getColor(R.color.text_black));
                    pvfdue.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
                }
                if (bnFalertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    pvfdue.setText(bnFalertlist_for_client.get(i).startDate());
                    pvfdue.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    pvfdue.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                    pvfdue.setOnClickListener(onClickListener);
                    pvfdue.setTag(pc);

                }
                if (bnFalertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    pvfdue.setOnClickListener(onClickListener);
                    pvfdue.setTag(pc);
                    pvfdue.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    pvfdue.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
                }
                if (bnFalertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    pvfdue.setTextColor(context.getResources().getColor(R.color.text_black));
                    pvfdue.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
                    pvfdue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }
                if (bnFalertlist_for_client.get(i).isComplete()) {
                    pvfdue.setText("visited");
                    pvfdue.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
                    pvfdue.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
                }
            }
        }
    }

    private void contstructNextVaccinedateBlock(CommonPersonObjectClient pc, View itemView) {

        TextView nextVaccineDate = (TextView) itemView.findViewById(R.id.next_vaccine_date);
        if (!(pc.getDetails().get("TT5_Date_of_Vaccination") != null ? pc.getDetails().get("TT5_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green));
            nextVaccineDate.setText("Completed");
        } else if (!(pc.getDetails().get("TT4_Date_of_Vaccination") != null ? pc.getDetails().get("TT4_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("TT5-" + (pc.getDetails().get("Date_of_TT5") != null ? pc.getDetails().get("Date_of_TT5") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_tt5");
        } else if (!(pc.getDetails().get("TT3_Date_of_Vaccination") != null ? pc.getDetails().get("TT3_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("TT4-" + (pc.getDetails().get("Date_of_TT4") != null ? pc.getDetails().get("Date_of_TT4") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_tt4");
        } else if (!(pc.getDetails().get("TT2_Date_of_Vaccination") != null ? pc.getDetails().get("TT2_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("TT3-" + (pc.getDetails().get("Date_of_TT3") != null ? pc.getDetails().get("Date_of_TT3") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_tt3");
        } else if (!(pc.getDetails().get("TT1_Date_of_Vaccination") != null ? pc.getDetails().get("TT1_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("TT2-" + (pc.getDetails().get("Date_of_TT2") != null ? pc.getDetails().get("Date_of_TT2") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_tt2");
        } else if (!(pc.getDetails().get("measles_Date_of_Vaccination") != null ? pc.getDetails().get("measles_Date_of_Vaccination") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("TT1-" + (pc.getDetails().get("Date_of_TT1") != null ? pc.getDetails().get("Date_of_TT1") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_tt1");
        } else if (!(pc.getDetails().get("Date_of_Measles") != null ? pc.getDetails().get("Date_of_Measles") : "").equalsIgnoreCase("")) {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
            nextVaccineDate.setText("Measles-" + (pc.getDetails().get("Date_of_Measles") != null ? pc.getDetails().get("Date_of_Measles") : ""));
            nextVaccineDate.setTag(R.id.clientobject, pc);
            nextVaccineDate.setTag(R.id.formname, "woman_vaccination_measles");
        } else {
            nextVaccineDate.setBackgroundColor(context.getResources().getColor(R.color.client_list_header_dark_grey));
            nextVaccineDate.setText("not applicable");
        }
        nextVaccineDate.setOnClickListener(onClickListener);
        nextVaccineDate.setTag(pc);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    private void constructvaccineVisitStatusBlock(CommonPersonObjectClient pc, View itemview) {
//        vaccines_2
        ImageView lastvaccinestick = (ImageView) itemview.findViewById(R.id.measlestick);
        TextView lastvaccinetext = (TextView) itemview.findViewById(R.id.lastvaccinetext);


        lastvaccinetext.setVisibility(View.INVISIBLE);
        lastvaccinestick.setVisibility(View.INVISIBLE);
        if (!(pc.getDetails().get("vaccines_2") != null ? pc.getDetails().get("vaccines_2") : "").equalsIgnoreCase("")) {
            String lastvaccine = (pc.getDetails().get("vaccines_2") != null ? pc.getDetails().get("vaccines_2") : "");
            lastvaccine = lastvaccine.trim();
            lastvaccine = lastvaccine.replace(" ",",");
            lastvaccinestick.setVisibility(View.VISIBLE);
            lastvaccinetext.setVisibility(View.VISIBLE);
            lastvaccinetext.setText(lastvaccine);
        }else if (!(pc.getDetails().get("vaccines1_2") != null ? pc.getDetails().get("vaccines1_2") : "").equalsIgnoreCase("")) {
            String lastvaccine = (pc.getDetails().get("vaccines1_2") != null ? pc.getDetails().get("vaccines1_2") : "");
            lastvaccine = lastvaccine.trim();
            lastvaccine = lastvaccine.replace(" ",",");
            lastvaccinestick.setVisibility(View.VISIBLE);
            lastvaccinetext.setVisibility(View.VISIBLE);
            lastvaccinetext.setText(lastvaccine);
        }


//        if (!(pc.getDetails().get("tt1_final") != null ? pc.getDetails().get("tt1_final") : "").equalsIgnoreCase("")) {
//            lastvaccinestick.setVisibility(View.VISIBLE);
//            lastvaccinetext.setVisibility(View.VISIBLE);
//            lastvaccinetext.setText("TT1-" + pc.getDetails().get("tt1_final"));
//        }
//        if (!(pc.getDetails().get("tt2_final") != null ? pc.getDetails().get("tt2_final") : "").equalsIgnoreCase("")) {
//            lastvaccinestick.setVisibility(View.VISIBLE);
//            lastvaccinetext.setVisibility(View.VISIBLE);
//            lastvaccinetext.setText("TT2-" + pc.getDetails().get("tt2_final"));
//        }
//        if (!(pc.getDetails().get("tt3_final") != null ? pc.getDetails().get("tt3_final") : "").equalsIgnoreCase("")) {
//            lastvaccinestick.setVisibility(View.VISIBLE);
//            lastvaccinetext.setVisibility(View.VISIBLE);
//            lastvaccinetext.setText("TT3-" + pc.getDetails().get("tt3_final"));
//        }
//        if (!(pc.getDetails().get("tt4_final") != null ? pc.getDetails().get("tt4_final") : "").equalsIgnoreCase("")) {
//            lastvaccinestick.setVisibility(View.VISIBLE);
//            lastvaccinetext.setVisibility(View.VISIBLE);
//            lastvaccinetext.setText("TT4-" + pc.getDetails().get("tt4_final"));
//        }
//        if (!(pc.getDetails().get("tt5_final") != null ? pc.getDetails().get("tt5_final") : "").equalsIgnoreCase("")) {
//            lastvaccinestick.setVisibility(View.VISIBLE);
//            lastvaccinetext.setVisibility(View.VISIBLE);
//            lastvaccinetext.setText("TT5-" + pc.getDetails().get("tt5_final"));
//        }


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
        View View = (ViewGroup) inflater().inflate(R.layout.smart_register_dghs_woman, null);
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

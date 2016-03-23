package org.ei.opensrp.vaccinator.child;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartClientsProvider implements SmartRegisterClientsProvider {


    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public ChildSmartClientsProvider(Context context,
                                     View.OnClickListener onClickListener,
                                     CommonPersonObjectController controller, AlertService alertService) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        //   org.ei.opensrp.util.Log.logDebug("in childclientsmartprovider");
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_child_client_custom, null);
            viewHolder = new ViewHolder();
            viewHolder.profilelayout = (LinearLayout) convertView.findViewById(R.id.profile_info_layout);
            viewHolder.childId = (TextView) convertView.findViewById(R.id.child_id);
            viewHolder.childName = (TextView) convertView.findViewById(R.id.child_name);
            viewHolder.fatherName = (TextView) convertView.findViewById(R.id.child_father_name);
            viewHolder.childDOB = (TextView) convertView.findViewById(R.id.child_dob);
            viewHolder.profilepic = (ImageView) convertView.findViewById(R.id.child_profilepic);
            viewHolder.last_visit_date = (TextView) convertView.findViewById(R.id.child_last_visit_date);
            viewHolder.last_vaccine = (TextView) convertView.findViewById(R.id.child_last_vaccine);
            viewHolder.next_visit_date = (TextView) convertView.findViewById(R.id.child_next_visit);
            viewHolder.next_visit_date_holder = (FrameLayout) convertView.findViewById(R.id.child_next_visit_holder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (pc.getDetails().get("profilepic") == null) {
            if (pc.getDetails().get("gender").equalsIgnoreCase("male")) {
                viewHolder.profilepic.setImageResource(org.ei.opensrp.R.drawable.child_boy_infant);

            } else if (pc.getDetails().get("gender").equalsIgnoreCase("female")) {
                viewHolder.profilepic.setImageResource(org.ei.opensrp.R.drawable.child_girl_infant);

            } else {
                viewHolder.profilepic.setImageResource(R.drawable.child_transgender_inflant);

            }
        } else {


        }

        //setting date for next visit
        //viewHolder.next_visit_date.setText("no alerts available");

        viewHolder.childId.setText(pc.getDetails().get("existing_program_client_id") != null ? pc.getDetails().get("existing_program_client_id") : "N/A");
        viewHolder.childName.setText(pc.getDetails().get("first_name") != null ? pc.getDetails().get("first_name") : "");
        viewHolder.fatherName.setText(pc.getDetails().get("father_name") != null ? pc.getDetails().get("father_name") : "");
        viewHolder.childDOB.setText(pc.getDetails().get("chid_dob_confirm") != null ? pc.getDetails().get("chid_dob_confirm") : "");

        viewHolder.profilepic.setOnClickListener(onClickListener);
        viewHolder.profilepic.setTag(client);

        //viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);
        //    viewHolder.next_visit_date_holder.setTag(client);
        //setting previous vaccanies
        viewHolder.last_visit_date.setText(pc.getColumnmaps().get("child_reg_date") != null ? pc.getDetails().get("child_reg_date") : "");
        String retroVaccinces = pc.getColumnmaps().get("vaccines") != null ? pc.getColumnmaps().get("vaccines") : "";
        String currentVaccinces = pc.getColumnmaps().get("vaccines2") != null ? pc.getColumnmaps().get("vaccines2") : "";


        viewHolder.last_vaccine.setText(retroVaccinces + " " + currentVaccinces);


        Date lastdate = null;

        if (pc.getDetails().get("chid_dob_confirm") != null) {
            viewHolder.last_visit_date.setText(pc.getDetails().get("chid_dob_confirm") != null ? pc.getDetails().get("chid_dob_confirm") : "");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date regdate = format.parse(pc.getDetails().get("chid_dob_confirm"));
                lastdate = regdate;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            viewHolder.last_visit_date.setText(pc.getDetails().get("child_birth_date") != null ? pc.getDetails().get("child_birth_date") : "");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date regdate = format.parse(pc.getDetails().get("child_birth_date"));
                lastdate = regdate;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //alertService.
//            List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "BCG","OPV_0_AND_1","PENTAVALENT 3" ,"PCV_1","PCV_2","PCV_3","OPV 1","OPV 2","OPV 3","PENTAVALENT 1","PENTAVALENT 2","Measles 1","Measles 2");
        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "bcg", "opv_1", "pentavalent_3", "pcv_1", "pcv_2", "pcv_3", "opv_2", "opv_3", "pentavalent_1", "pentavalent_2", "measles 2", "measles 2");

        //     Log.d("alert list :" , alertlist_for_client.size()+"") ;
        // int e=3;
        viewHolder.next_visit_date.setText("");
//        viewHolder.next_visit_due_TextView.setText("");
        if (alertlist_for_client.size() == 0) {
            viewHolder.next_visit_date.setText("Schedule Not Generated");
            viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            viewHolder.next_visit_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        for (int i = 0; i < alertlist_for_client.size(); i++) {
            //   viewHolder.next_visit_date.setText(alertlist_for_client.get(i).expiryDate());
            if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {

                String v = alertlist_for_client.get(i).visitCode();
                viewHolder.next_visit_date.setText(v);
                viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);
                viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.background_floating_material_dark));
            }
            if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                String vaccine = alertlist_for_client.get(i).visitCode();
                viewHolder.next_visit_date.setText(vaccine + " ");

                viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_light_blue));
                viewHolder.next_visit_date.setOnClickListener(onClickListener);
                viewHolder.next_visit_date_holder.setTag(client);

            }
            if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                viewHolder.next_visit_date.setText(alertlist_for_client.get(i).scheduleName());
                viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);
                viewHolder.next_visit_date_holder.setTag(client);
                viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.alert_urgent_red));
            }
            if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.client_list_header_dark_grey));
                viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);

            }
            if (alertlist_for_client.get(i).isComplete()) {
                viewHolder.last_vaccine.append(alertlist_for_client.get(i).visitCode());
                viewHolder.last_visit_date.setText(alertlist_for_client.get(i).completionDate());
                viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);
                //    viewHolder.next_visit_date_holder.setBackgroundColor(context.getResources().getColor(R.color.alert_urgent_red));
            }
        }

        viewHolder.next_visit_date_holder.setOnClickListener(onClickListener);
        viewHolder.next_visit_date_holder.setTag(client);
        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }

    @Override
    public SmartRegisterClients getClients() {


        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }


    public LayoutInflater inflater() {
        return inflater;
    }

    class ViewHolder {

        TextView childId;
        TextView childName;
        TextView fatherName;
        TextView childDOB;
        TextView last_vaccine;
        TextView last_visit_date;
        TextView next_visit_date;
        FrameLayout follow_up;
        LinearLayout profilelayout;
        ImageView profilepic;
        FrameLayout next_visit_date_holder;

    }


}

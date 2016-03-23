package org.ei.opensrp.mcare.household;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class HouseHoldSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    AlertService alertService;

    public HouseHoldSmartClientsProvider(Context context,
                                         View.OnClickListener onClickListener,
                                         CommonPersonObjectController controller, AlertService alertService) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);

    }

    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (convertView == null){
           convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_household_client, null);
            viewHolder = new ViewHolder();
            viewHolder.profilelayout =  (LinearLayout)convertView.findViewById(R.id.profile_info_layout);
            viewHolder.gobhhid = (TextView)convertView.findViewById(R.id.gobhhid);
            viewHolder.jvitahhid = (TextView)convertView.findViewById(R.id.jvitahhid);
            viewHolder.village = (TextView)convertView.findViewById(R.id.village);
            viewHolder.profilepic =(ImageView)convertView.findViewById(R.id.profilepic);

            viewHolder.headofhouseholdname = (TextView)convertView.findViewById(R.id.householdheadname);
            viewHolder.no_of_mwra = (TextView)convertView.findViewById(R.id.no_of_mwra);
              viewHolder.last_visit_date = (TextView)convertView.findViewById(R.id.last_visit_date);
            viewHolder.due_visit_date = (TextView)convertView.findViewById(R.id.hh_due_date);
            viewHolder.due_date_holder = (FrameLayout)convertView.findViewById(R.id.hh_due_date_holder);
            viewHolder.follow_up = (ImageButton)convertView.findViewById(R.id.btn_edit);
            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.household_profile_thumb));
            viewHolder.warnbutton = (Button)convertView.findViewById(R.id.warnbutton);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.household_profile_thumb));
        }

        viewHolder.follow_up.setOnClickListener(onClickListener);
        viewHolder.follow_up.setTag(smartRegisterClient);
           viewHolder.profilelayout.setOnClickListener(onClickListener);
        viewHolder.profilelayout.setTag(smartRegisterClient);
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "FW CENSUS");

        if(pc.getDetails().get("profilepic")!=null){
            if((pc.getDetails().get("FWHOHGENDER")!=null?pc.getDetails().get("FWHOHGENDER"):"").equalsIgnoreCase("2")) {
                HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), viewHolder.profilepic, R.mipmap.womanimageload);
            }else if ((pc.getDetails().get("FWHOHGENDER")!=null?pc.getDetails().get("FWHOHGENDER"):"").equalsIgnoreCase("1")){
                HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), viewHolder.profilepic, R.mipmap.householdload);
            }

        }else{
            if((pc.getDetails().get("FWHOHGENDER")!=null?pc.getDetails().get("FWHOHGENDER"):"").equalsIgnoreCase("2")){
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.drawable.woman_placeholder));
            }else if ((pc.getDetails().get("FWHOHGENDER")!=null?pc.getDetails().get("FWHOHGENDER"):"").equalsIgnoreCase("1")){
                viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(R.mipmap.household_profile_thumb));
            }
        }

        boolean noNIDPic = getIfHouseholdHasElcoWithoutNationalID(pc);
        if(!noNIDPic){
            viewHolder.warnbutton.setVisibility(View.VISIBLE);
        }else{
            viewHolder.warnbutton.setVisibility(View.INVISIBLE);
        }
//
        viewHolder.gobhhid.setText(pc.getDetails().get("FWGOBHHID")!=null?pc.getDetails().get("FWGOBHHID"):"");
        viewHolder.jvitahhid.setText(pc.getDetails().get("FWJIVHHID")!=null?pc.getDetails().get("FWJIVHHID"):"");
        viewHolder.village.setText((humanize((pc.getDetails().get("existing_Mauzapara")!=null?pc.getDetails().get("existing_Mauzapara"):"").replace("+","_"))));
        viewHolder.headofhouseholdname.setText(pc.getDetails().get("FWHOHFNAME")!=null?pc.getDetails().get("FWHOHFNAME"):"");
        viewHolder.no_of_mwra.setText(pc.getDetails().get("ELCO")!=null?pc.getDetails().get("ELCO"):"");
        Date lastdate = null;
        if(pc.getDetails().get("FWNHREGDATE")!= null && pc.getDetails().get("FWCENDATE")!= null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date regdate = format.parse(pc.getDetails().get("FWNHREGDATE"));
                Date cendate = format.parse(pc.getDetails().get("FWCENDATE"));

                if(regdate.before(cendate)){
                    viewHolder.last_visit_date.setText(pc.getDetails().get("FWCENDATE")!=null?pc.getDetails().get("FWCENDATE"):"");
                    lastdate = cendate;
                }else{
                    viewHolder.last_visit_date.setText(pc.getDetails().get("FWNHREGDATE")!=null?pc.getDetails().get("FWNHREGDATE"):"");
                    lastdate = regdate;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if (pc.getDetails().get("FWNHREGDATE")!= null && pc.getDetails().get("FWCENDATE")== null){
            viewHolder.last_visit_date.setText(pc.getDetails().get("FWNHREGDATE")!=null?pc.getDetails().get("FWNHREGDATE"):"");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date regdate = format.parse(pc.getDetails().get("FWNHREGDATE"));
                lastdate = regdate;
            }catch (Exception e){

            }

        }else {
            viewHolder.last_visit_date.setText(pc.getDetails().get("FWNHREGDATE") != null ? pc.getDetails().get("FWNHREGDATE") : "");
        }

        if(alertlist_for_client.size() == 0 ){
            viewHolder.due_visit_date.setText("Not Synced to Server");
            viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(R.color.status_bar_text_almost_white));
            viewHolder.due_visit_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        for(int i = 0;i<alertlist_for_client.size();i++){
            viewHolder.due_visit_date.setText(alertlist_for_client.get(i).expiryDate());
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")){
                viewHolder.due_visit_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_upcoming_light_blue));
            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")){
                viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming_yellow));
                viewHolder.due_visit_date.setOnClickListener(onClickListener);
                viewHolder.due_visit_date.setTag(smartRegisterClient);

            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")){
                viewHolder.due_visit_date.setOnClickListener(onClickListener);
                viewHolder.due_visit_date.setTag(smartRegisterClient);
                viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.alert_urgent_red));
            }
            if(alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")){
                viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(org.ei.opensrp.R.color.client_list_header_dark_grey));
                viewHolder.due_visit_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            if(alertlist_for_client.get(i).isComplete()){
                viewHolder.due_visit_date.setText("visited");
                viewHolder.due_date_holder.setBackgroundColor(context.getResources().getColor(R.color.alert_complete_green_mcare));
            }
        }
        if(lastdate!= null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
            calendar.add(Calendar.DATE, 56);//8 weeks
            lastdate.setTime(calendar.getTime().getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            String result = String.format(Locale.ENGLISH, format.format(lastdate) );
            viewHolder.due_visit_date.setText( format.format(lastdate) );
//            viewHolder.due_visit_date.append(format.format(lastdate));

        }


        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }
    CommonPersonObjectController householdelcocontroller;
    private boolean getIfHouseholdHasElcoWithoutNationalID(CommonPersonObjectClient pc) {
        boolean toreturn = true;

        AllCommonsRepository allElcoRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
        ArrayList<String> list = new ArrayList<String>();
        list.add((pc.entityId()));
        List<CommonPersonObject> allchildelco = allElcoRepository.findByRelationalIDs(list);
        for (int i = 0; i < allchildelco.size(); i++) {
            if (allchildelco.get(i).getDetails().get("FWELIGIBLE").equalsIgnoreCase("1")) {
                if (allchildelco.get(i).getDetails().get("nidImage") == null) {
                    toreturn = false;
                }
            }
        }

        return toreturn;
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

     class ViewHolder {

         TextView gobhhid ;
         TextView jvitahhid ;
         TextView village;
         TextView headofhouseholdname;
         TextView no_of_mwra;
         TextView last_visit_date;
         TextView due_visit_date;
         ImageButton follow_up;
         LinearLayout profilelayout;
         ImageView profilepic;
         FrameLayout due_date_holder;
         Button warnbutton;
    }


}


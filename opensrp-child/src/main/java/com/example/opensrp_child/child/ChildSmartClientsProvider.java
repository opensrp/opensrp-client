package com.example.opensrp_child.child;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.opensrp_child.R;
import com.example.opensrp_stock.field.util.VaccineRepo;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.generateSchedule;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.nextVaccineDue;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.fillValue;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.hasAnyEmptyValue;
import static org.ei.opensrp.core.utils.Utils.humanizeAndUppercase;
import static org.ei.opensrp.core.utils.Utils.nonEmptyValue;
import static org.ei.opensrp.core.utils.Utils.setProfiePic;
import static org.ei.opensrp.core.utils.Utils.toDate;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartClientsProvider implements RegisterClientsProvider <CommonPersonObjectClient>{
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    public ChildSmartClientsProvider(Context context, View.OnClickListener onClickListener,
             AlertService alertService) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.core.R.dimen.list_item_height));
    }

    @Override
    public View getView(CommonPersonObjectClient client, View convertView, ViewGroup viewGroup) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        fillValue((TextView) convertView.findViewById(R.id.child_id), pc.getColumnmaps(), "program_client_id", false);
        fillValue((TextView) convertView.findViewById(R.id.child_name), getValue(pc.getColumnmaps(), "first_name", true)+" "+getValue(pc, "last_name", true));
        fillValue((TextView) convertView.findViewById(R.id.child_mothername), getValue(pc.getColumnmaps(), "mother_name", true));
        fillValue((TextView) convertView.findViewById(R.id.child_fathername), getValue(pc.getColumnmaps(), "father_name", true));
        String gender = getValue(pc.getColumnmaps(), "gender", true);
        if(gender.equalsIgnoreCase("male")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.infant_male);
        }
        else if(gender.equalsIgnoreCase("female")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.infant_female);
        }
        else if(gender.toLowerCase().contains("trans")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.infant_intersex);
        }

        int agey = -1;
        try{
            agey = Years.yearsBetween(new DateTime(getValue(pc.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        }
        catch (Exception e){ }

        fillValue((TextView) convertView.findViewById(R.id.child_birthdate), convertDateFormat(getValue(pc.getColumnmaps(), "dob", false), "No DoB", true));
        int months = -1;
        try{
            months = Months.monthsBetween(new DateTime(getValue(pc.getColumnmaps(), "dob", false)), DateTime.now()).getMonths();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        fillValue((TextView) convertView.findViewById(R.id.child_age), (months < 0?"":(months+ " months") ));
        fillValue((TextView) convertView.findViewById(R.id.child_epi_number), pc.getColumnmaps(), "epi_card_number", false);
        //fillValue((TextView) convertView.findViewById(R.id.child_contact_number), pc.getColumnmaps(), "contact_phone_number", false);

        String vaccineretro = getValue(pc.getColumnmaps(), "vaccines", false);
        String vaccine2 = getValue(pc.getColumnmaps(), "vaccines_2", false);

        fillValue((TextView) convertView.findViewById(R.id.child_last_vaccine), humanizeAndUppercase(vaccine2, "Penta", "Measles").replaceAll(" ", ", "));

        String[] vaccineList = new String[]{"bcg", "opv0",
                "penta1", "opv1", "pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2"};
        String lastVaccine = convertDateFormat(nonEmptyValue(pc.getColumnmaps(), false, false, vaccineList), true);

        fillValue((TextView) convertView.findViewById(R.id.child_last_visit_date), lastVaccine);
        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(),
                "BCG", "OPV 0", "Penta 1", "OPV 1","PCV 1", "Penta 2", "OPV 2", "PCV 2",
                "Penta 3", "OPV 3", "PCV 3", "IPV", "Measles 1", "Measles2",
                "bcg", "opv0", "penta1", "opv1","pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2");

        if(agey < 0){
            deactivateNextVaccine("Invalid DoB", "", R.color.alert_na, convertView);
        }
        else if(!hasAnyEmptyValue(pc.getColumnmaps(), "_retro", vaccineList)){
            deactivateNextVaccine("Fully Immunized", "", R.color.alert_complete_green, convertView);
        }
        else if(agey >= 5 && hasAnyEmptyValue(pc.getColumnmaps(), "_retro", vaccineList)){
            deactivateNextVaccine("Partially Immunized", "", R.color.alert_na, convertView);
        }
        else {
            List<Map<String, Object>> sch = generateSchedule(pc, "child", new DateTime(pc.getColumnmaps().get("dob")), pc.getColumnmaps(), alertlist_for_client);
            Map<String, Object> nv = nextVaccineDue(sch, toDate(lastVaccine, true));
            if(nv != null){
                DateTime dueDate = (DateTime)nv.get("date");
                VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get("vaccine");
                if(nv.get("alert") == null){
                    activateNextVaccine(dueDate, (VaccineRepo.Vaccine)nv.get("vaccine"), Color.BLACK, R.color.alert_na, onClickListener, client, convertView);
                }
                else if (((Alert)nv.get("alert")).status().value().equalsIgnoreCase("normal")) {
                    activateNextVaccine(dueDate, vaccine, Color.WHITE, R.color.alert_normal, onClickListener, client, convertView);
                }
                else if (((Alert)nv.get("alert")).status().value().equalsIgnoreCase("upcoming")) {
                    activateNextVaccine(dueDate, vaccine, Color.BLACK, R.color.alert_upcoming, onClickListener, client, convertView);
                }
                else if (((Alert)nv.get("alert")).status().value().equalsIgnoreCase("urgent")) {
                    activateNextVaccine(dueDate, vaccine, Color.WHITE, R.color.alert_urgent, onClickListener, client, convertView);
                }
                else if (((Alert)nv.get("alert")).status().value().equalsIgnoreCase("expired")) {
                    deactivateNextVaccine(vaccine + " Expired", "", R.color.alert_expired, convertView);
                }
            }
            else {
                fillValue((TextView) convertView.findViewById(R.id.child_next_visit_vaccine), "Waiting");
                deactivateNextVaccine("Waiting", "", R.color.alert_na, convertView);
            }
        }

        setProfiePic(convertView.getContext(), (ImageView) convertView.findViewById(R.id.child_profilepic), "pkchild", client.entityId(), null);

        convertView.findViewById(R.id.child_profile_info_layout).setTag(client);
        convertView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }

    private void deactivateNextVaccine(String vaccineViewText, String vaccineDateText, int color, View convertView){
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_vaccine), vaccineViewText);
        ((TextView) convertView.findViewById(R.id.child_next_visit_date)).setText(convertDateFormat(vaccineDateText, true));
        ((TextView) convertView.findViewById(R.id.child_next_visit_vaccine)).setTextColor(Color.BLACK);
        ((TextView) convertView.findViewById(R.id.child_next_visit_date)).setTextColor(Color.BLACK);
        convertView.findViewById(R.id.child_next_visit_holder).setBackgroundColor(context.getResources().getColor(color));
        convertView.findViewById(R.id.child_next_visit_holder).setOnClickListener(null);
        convertView.findViewById(R.id.child_next_visit_holder).setTag(null);
        convertView.findViewById(R.id.next_vaccine_image).setVisibility(View.INVISIBLE);
    }

    private void activateNextVaccine(String dueDate, String vaccine, int foreColor, int backColor, View.OnClickListener onClickListener,
                                     SmartRegisterClient client, View convertView){
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_vaccine), vaccine==null?"":vaccine.replaceAll(" ", ""));
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_date), convertDateFormat(dueDate, true));
        ((TextView) convertView.findViewById(R.id.child_next_visit_vaccine)).setTextColor(foreColor);
        ((TextView) convertView.findViewById(R.id.child_next_visit_date)).setTextColor(foreColor);

        convertView.findViewById(R.id.child_next_visit_holder).setBackgroundColor(context.getResources().getColor(backColor));
        convertView.findViewById(R.id.child_next_visit_holder).setOnClickListener(onClickListener);
        convertView.findViewById(R.id.child_next_visit_holder).setTag(client);
    }

    private void activateNextVaccine(DateTime dueDate, VaccineRepo.Vaccine vaccine, int foreColor, int backColor, View.OnClickListener onClickListener,
                                     SmartRegisterClient client, View convertView){
        activateNextVaccine(dueDate==null?"":dueDate.toString("yyyy-MM-dd"), vaccine==null?"":StringUtil.humanize(vaccine.display().replaceAll(" ","")), foreColor, backColor, onClickListener, client, convertView);
    }

    @Override
    public List<CommonPersonObjectClient> getClients() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(org.ei.opensrp.core.template.ServiceModeOption serviceModeOption) {

    }

    @Override
    public View inflateLayoutForAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_child_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

}
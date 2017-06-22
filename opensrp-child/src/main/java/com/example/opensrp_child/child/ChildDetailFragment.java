package com.example.opensrp_child.child;

import android.widget.TableLayout;
import android.widget.TextView;

import com.example.opensrp_child.R;
import com.example.opensrp_stock.field.util.VaccineRepo;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.domain.Alert;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.VACCINE_SCHEDULE_COMPARATOR;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.addVaccineRow;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.generateSchedule;
import static org.ei.opensrp.core.utils.Utils.Size;
import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.hasAnyEmptyValue;
import static org.ei.opensrp.core.utils.Utils.nonEmptyValue;

public class ChildDetailFragment extends DetailFragment {
    @Override
    protected int layoutResId() {
        return R.layout.child_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Child Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.child_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        if(client == null || client.getColumnmaps().get("gender") == null){
            return null;
        }
        String gender = getValue(client.getColumnmaps(), "gender", false);
        if(gender.equalsIgnoreCase("female")){
            return R.drawable.infant_female;
        }
        else if(gender.toLowerCase().contains("trans")){
            return R.drawable.infant_intersex;
        }

        return R.drawable.infant_male;
    }

    @Override
    protected String bindType() {
        return "pkchild";
    }

    @Override
    protected boolean allowImageCapture() {
        return true;
    }

    public String getEntityIdentifier() {
        return nonEmptyValue(client.getColumnmaps(), true, false, "existing_program_client_id", "program_client_id");
    }

    @Override
    protected void generateView() {
        ((TableLayout) currentView.findViewById(R.id.child_detail_info_table2)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.child_detail_info_table1)).removeAllViews();

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) currentView.findViewById(R.id.child_detail_info_table1);

        //setting value in basic information textviews
        addRow(getActivity(), dt, "Program ID", getEntityIdentifier(), Size.MEDIUM);
        addRow(getActivity(), dt, "EPI Card Number", getValue(client.getColumnmaps(), "epi_card_number", false), Size.MEDIUM);
        addRow(getActivity(), dt, "Child's Name", getValue(client.getColumnmaps(), "first_name", true)+" "+getValue(client.getColumnmaps(), "last_name", true), Size.MEDIUM);
        int months = -1;
        try{
            months = Months.monthsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getMonths();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        addRow(getActivity(), dt, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), "No DoB", true) +" \n" +" (" + (months < 0? "":(months+"")) + " months" + ")", Size.MEDIUM);
        addRow(getActivity(), dt, "Gender", getValue(client.getColumnmaps(), "gender", true), Size.MEDIUM);
        addRow(getActivity(), dt, "Ethnicity", getValue(client, "ethnicity", true), Size.MEDIUM);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.child_detail_info_table2);

        addRow(getActivity(), dt2, "Mother's Name", getValue(client.getColumnmaps(), "mother_name", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Father's Name", getValue(client.getColumnmaps(), "father_name", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Contact Number", getValue(client.getColumnmaps(), "contact_phone_number", false), Size.MEDIUM);
        addRow(getActivity(), dt2, "Address", getValue(client.getColumnmaps(), "address1", true)
                +", \nUC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", \nTown: "+ getValue(client.getColumnmaps(), "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", "+ getValue(client, "province", true), Size.MEDIUM);

        String[] vl = new String[]{"bcg", "opv0", "penta1", "opv1","pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2"};

        //VACCINES INFORMATION
        TableLayout table = (TableLayout) getActivity().findViewById(R.id.vaccine_details);
        table.removeAllViews();

        List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(),
                "BCG", "OPV 0", "Penta 1", "OPV 1", "PCV 1", "Penta 2", "OPV 2", "PCV 2",
                "Penta 3", "OPV 3", "PCV 3", "IPV", "Measles 1", "Measles2",
                "bcg", "opv0", "penta1", "opv1", "pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2");

        List<Map<String, Object>> sch = generateSchedule(client, "child", months < 0 ? null:new DateTime(client.getColumnmaps().get("dob")), client.getColumnmaps(), al);

        Collections.sort(sch, VACCINE_SCHEDULE_COMPARATOR);

        addVaccineRow(getActivity(), table, "0  W", sch, Size.MEDIUM, VaccineRepo.Vaccine.bcg, VaccineRepo.Vaccine.opv0);
        addVaccineRow(getActivity(), table, "6  W", sch, Size.MEDIUM, VaccineRepo.Vaccine.penta1, VaccineRepo.Vaccine.opv1, VaccineRepo.Vaccine.pcv1);

        table = (TableLayout) getActivity().findViewById(R.id.vaccine_details2);
        table.removeAllViews();

        addVaccineRow(getActivity(), table, "10 W", sch, Size.MEDIUM, VaccineRepo.Vaccine.penta2, VaccineRepo.Vaccine.opv2, VaccineRepo.Vaccine.pcv2);

        table = (TableLayout) getActivity().findViewById(R.id.vaccine_details3);
        table.removeAllViews();

        addVaccineRow(getActivity(), table, "14 W", sch, Size.MEDIUM, VaccineRepo.Vaccine.penta3, VaccineRepo.Vaccine.opv3, VaccineRepo.Vaccine.pcv3, VaccineRepo.Vaccine.ipv);
        addVaccineRow(getActivity(), table, "9  M", sch, Size.MEDIUM, VaccineRepo.Vaccine.measles1);
        addVaccineRow(getActivity(), table, "15 M", sch, Size.MEDIUM, VaccineRepo.Vaccine.measles2);

        int i = 0;
        for (Map<String, Object> m : sch){
            if (i <= 7) {
               // table = (TableLayout) currentView.findViewById(R.id.child_vaccine_table1);
            } else {
               // table = (TableLayout) currentView.findViewById(R.id.child_vaccine_table2);
            }
           //todo addVaccineDetail(getActivity(), table, m.get("status").toString(), (VaccineRepo.Vaccine)m.get("vaccine"), (DateTime)m.get("date"), (Alert)m.get("alert"), true);
            i++;
        }

        int agey = -1;
        try{
            agey = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        ((TextView)getActivity().findViewById(R.id.childdetail_status_tag)).setText("");
        if(agey < 0){
            ((TextView)getActivity().findViewById(R.id.childdetail_status_tag)).setText("No DoB");
        }
        else if(!hasAnyEmptyValue(client.getColumnmaps(), "_retro", vl)){
            ((TextView)getActivity().findViewById(R.id.childdetail_status_tag)).setText("Fully Immunized");
        }
        else if(agey >= 5 && hasAnyEmptyValue(client.getColumnmaps(), "_retro", vl)){
            ((TextView)getActivity().findViewById(R.id.childdetail_status_tag)).setText("Partially Immunized");
        }
    }
}

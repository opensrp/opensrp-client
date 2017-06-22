package com.example.opensrp_woman.woman;

import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.opensrp_stock.field.util.VaccineRepo;
import com.example.opensrp_woman.R;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.domain.Alert;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.List;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.addStatusTag;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.addVaccineDetail;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.generateSchedule;
import static org.ei.opensrp.core.utils.Utils.*;
import static org.ei.opensrp.core.utils.Utils.addRow;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.nonEmptyValue;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 11-Nov-15.
 */
public class WomanDetailFragment extends DetailFragment {

    @Override
    protected int layoutResId() {
        return R.layout.woman_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Woman Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.woman_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        return R.drawable.pk_woman_avtar;
    }

    @Override
    protected String bindType() {
        return "pkwoman";
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
        TableRow vaccineHeader = (TableRow) currentView.findViewById(R.id.woman_vaccine_table_header);
        TableRow pregnancyHeader = (TableRow) currentView.findViewById(R.id.woman_pregnancy_table_header);

        ((TableLayout) currentView.findViewById(R.id.woman_detail_info_table1)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.woman_detail_info_table2)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.woman_vaccine_table)).removeAllViews();

        ((TableLayout) currentView.findViewById(R.id.woman_pregnancy_table)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.woman_detail_info_table1)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.woman_detail_info_table2)).removeAllViews();
        ((TableLayout) currentView.findViewById(R.id.woman_vaccine_table)).removeAllViews();

        //WOMAN BASIC INFORMATION
        TableLayout dt = (TableLayout) currentView.findViewById(R.id.woman_detail_info_table1);

       //setting value in WOMAN basic information textviews
        addRow(getActivity(), dt, "Program ID", getEntityIdentifier(), Size.MEDIUM);
        addRow(getActivity(), dt, "EPI Card Number", getValue(client.getColumnmaps(), "epi_card_number", false), Size.MEDIUM);
        addRow(getActivity(), dt, "Woman's Name", getValue(client.getColumnmaps(), "first_name", true), Size.MEDIUM);

        int age = -1;
        try{
            age = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        addRow(getActivity(), dt, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), "No DoB", true) +" \n "+"(" + age + " years)", Size.MEDIUM);
        addRow(getActivity(), dt, "Father's Name", getValue(client.getColumnmaps(), "father_name", true), Size.MEDIUM);
        addRow(getActivity(), dt, "Husband's Name", getValue(client.getColumnmaps(), "husband_name", true), Size.MEDIUM);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.woman_detail_info_table2);
        addRow(getActivity(), dt2, "Ethnicity", getValue(client, "ethnicity", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Married", getValue(client.getColumnmaps(), "marriage", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Contact Number", getValue(client.getColumnmaps(), "contact_phone_number", true), Size.MEDIUM);
        addRow(getActivity(), dt2, "Address", getValue(client.getColumnmaps(), "address1", true)
                +", \nUC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", \nTown: "+ getValue(client.getColumnmaps(), "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", "+ getValue(client, "province", true), Size.MEDIUM);

        //VACCINES INFORMATION
        TableLayout table = (TableLayout) currentView.findViewById(R.id.woman_vaccine_table);
        table.addView(vaccineHeader);
        List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");
        List<Map<String, Object>> sch = generateSchedule(client, "woman", null, client.getColumnmaps(), al);
        for (Map<String, Object> m : sch){
            addVaccineDetail(getActivity(), table, m.get("status").toString(), (VaccineRepo.Vaccine) m.get("vaccine"), (DateTime) m.get("date"), (Alert) m.get("alert"), false);
        }

        if(age < 0){
            addStatusTag(getActivity(), table, "No DoB", true);
        }
        if(StringUtils.isNotBlank(getValue(client.getColumnmaps(), "tt5", false))){
            addStatusTag(getActivity(), table, "Fully Immunized", true);
        }
        else if(age > 49 && StringUtils.isBlank(getValue(client.getColumnmaps(), "tt5", false))){
            addStatusTag(getActivity(), table, "Partially Immunized", true);
        }

        TableLayout pt = (TableLayout) currentView.findViewById(R.id.woman_pregnancy_table);
        pt.addView(pregnancyHeader);
        addRow(getActivity(), pt, "Pregnant", getValue(client.getColumnmaps(), "pregnant", true), Size.MEDIUM);
        addRow(getActivity(), pt, "EDD", convertDateFormat(getValue(client.getColumnmaps(), "final_edd", false), "N/A", true), Size.MEDIUM);
        addRow(getActivity(), pt, "LMP", convertDateFormat(getValue(client, "final_lmp", false), "N/A", true), Size.MEDIUM);
        addRow(getActivity(), pt, "GA (weeks)", getValue(client.getColumnmaps(), "final_ga", "N/A", false), Size.MEDIUM);
    }
}

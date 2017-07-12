package org.ei.opensrp.path.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.EditFormSubmissionWrapper;
import org.ei.opensrp.path.domain.Photo;
import org.ei.opensrp.path.domain.VaccinateFormSubmissionWrapper;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.listener.VaccinationActionListener;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.ImageUtils;
import util.VaccinateActionUtils;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.hasAnyEmptyValue;
import static util.Utils.nonEmptyValue;
import static util.VaccinatorUtils.addStatusTag;
import static util.VaccinatorUtils.addVaccineDetail;
import static util.VaccinatorUtils.generateSchedule;

public class ChildDetailActivity extends DetailActivity implements VaccinationActionListener {

    Set<TableLayout> tables;

    private VaccinateFormSubmissionWrapper vaccinateFormSubmissionWrapper;

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
        return getEntityIdentifier(retrieveCommonPersonObjectClient());
    }

    @Override
    protected Class onBackActivity() {
        return ChildSmartRegisterActivity.class;
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.child_profilepic;
    }

    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj, HashMap<String, String> overrideStringmap, String formName, String registerFormName, Class<? extends DetailActivity> detailActivity) {

        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            overrideStringmap = new HashMap<>();
        }

        String metaData = new FieldOverrides(new JSONObject(overrideStringmap).toString()).getJSONString();
        org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + metaData);

        //String data = VaccinateActionUtils.formData(context, clientobj.entityId(), formName, metaData);
        VaccinateFormSubmissionWrapper vaccinateFormSubmissionWrapper = new VaccinateFormSubmissionWrapper(null, clientobj.entityId(), formName, metaData, "child");

        //String editData = VaccinateActionUtils.formData(context, clientobj.entityId(), registerFormName, null);
        EditFormSubmissionWrapper editFormSubmissionWrapper = new EditFormSubmissionWrapper(null, clientobj.entityId(), registerFormName, null, "child");

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_VACCINATE_OBJECT, vaccinateFormSubmissionWrapper);
        bundle.putSerializable(EXTRA_EDIT_OBJECT, editFormSubmissionWrapper);
        bundle.putSerializable(EXTRA_CLIENT, clientobj);
        Intent intent = new Intent(context, detailActivity);
        intent.putExtras(bundle);

        context.startActivity(intent);

    }

    @Override
    protected Integer defaultProfilePicResId(CommonPersonObjectClient client) {
        String gender = getValue(client, "gender", true);
        return ImageUtils.profileImageResourceByGender(gender);
    }

    @Override
    protected String bindType() {
        return "ec_child";
    }

    @Override
    protected boolean allowImageCapture() {
        return true;
    }

    public String getEntityIdentifier(CommonPersonObjectClient client) {
        if (client == null) {
            return "";
        }
        return nonEmptyValue(client.getColumnmaps(), true, false, "existing_zeir_id", "zeir_id");
    }

    @Override
    protected void generateView(CommonPersonObjectClient client) {

        this.vaccinateFormSubmissionWrapper = retrieveFormSubmissionWrapper();

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.child_detail_info_table1);

        //setting value in basic information textviews
        TableRow tr = getDataRow(this, "Program ID", getEntityIdentifier(client), null);
        dt.addView(tr);

        tr = getDataRow(this, "EPI Card Number", getValue(client.getColumnmaps(), "epi_card_number", false), "epi_card_number", null);
        dt.addView(tr);

        tr = getDataRow(this, "Child's Name", getValue(client.getColumnmaps(), "first_name", true) + " " + getValue(client.getColumnmaps(), "last_name", true), "first_name last_name", null);
        dt.addView(tr);

        int months = -1;
        try {
            months = Months.monthsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getMonths();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tr = getDataRow(this, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), "No DoB", true) + " (" + (months < 0 ? "" : (months + "")) + " months" + ")", "dob", null);
        dt.addView(tr);

        String gender = getValue(client.getColumnmaps(), "gender", true);
        tr = getDataRow(this, "Gender", gender, "gender", null);
        dt.addView(tr);

        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity", true), "ethnicity", null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.child_detail_info_table2);

        tr = getDataRow(this, "Mother's Name", getValue(client.getColumnmaps(), "mother_name", true), "mother_name", null);
        dt2.addView(tr);

        tr = getDataRow(this, "Father's Name", getValue(client.getColumnmaps(), "father_name", true), "father_name", null);
        dt2.addView(tr);

        tr = getDataRow(this, "Contact Number", getValue(client.getColumnmaps(), "contact_phone_number", false), "contact_phone_number", null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client.getColumnmaps(), "address1", true)
                + ", \nUC: " + getValue(client.getColumnmaps(), "union_council", true)
                + ", \nTown: " + getValue(client.getColumnmaps(), "town", true)
                + ", \nCity: " + getValue(client, "city_village", true)
                + ", \nProvince: " + getValue(client, "province", true), null);
        dt2.addView(tr);

        String[] vl = new String[]{"bcg", "opv0", "penta1", "opv1", "pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2"};

        //VACCINES INFORMATION
        TableLayout table = null;
        tables = new HashSet<>();

        List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(),
                "BCG", "OPV 0", "Penta 1", "OPV 1", "PCV 1", "Penta 2", "OPV 2", "PCV 2",
                "Penta 3", "OPV 3", "PCV 3", "IPV", "Measles 1", "Measles2",
                "bcg", "opv0", "penta1", "opv1", "pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2");

        List<Map<String, Object>> sch = generateSchedule("child", months < 0 ? null : new DateTime(client.getColumnmaps().get("dob")), client.getColumnmaps(), al);
        int i = 0;

        String previousVaccine = "";
        for (Map<String, Object> m : sch) {
            if (i <= 3) {
                table = (TableLayout) findViewById(R.id.child_vaccine_table1);
            } else if (i <= 8) {
                table = (TableLayout) findViewById(R.id.child_vaccine_table2);
            } else {
                table = (TableLayout) findViewById(R.id.child_vaccine_table3);
            }

            Photo photo = ImageUtils.profilePhotoByClient(client);

            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setStatus(m.get("status").toString());
            vaccineWrapper.setVaccine((VaccineRepo.Vaccine) m.get("vaccine"));
            vaccineWrapper.setVaccineDate((DateTime) m.get("date"));
            vaccineWrapper.setAlert((Alert) m.get("alert"));
            vaccineWrapper.setPreviousVaccine(previousVaccine);
            vaccineWrapper.setPhoto(photo);
            vaccineWrapper.setCompact(false);

            vaccineWrapper.setPatientNumber(getValue(client.getColumnmaps(), "zeir_id", false));
            vaccineWrapper.setPatientName(getValue(client.getColumnmaps(), "first_name", true) + " " + getValue(client.getColumnmaps(), "last_name", true));

            String existingAge = VaccinateActionUtils.retrieveExistingAge(vaccinateFormSubmissionWrapper);
            if (StringUtils.isNotBlank(existingAge)) {
                vaccineWrapper.setExistingAge(existingAge);
            }

            addVaccineDetail(this, table, vaccineWrapper);
            previousVaccine = vaccineWrapper.getId();
            tables.add(table);
            i++;
        }

        int agey = -1;
        try {
            agey = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (agey < 0) {
            addStatusTag(this, table, "No DoB", true);
        } else if (!hasAnyEmptyValue(client.getColumnmaps(), "_retro", vl)) {
            addStatusTag(this, table, "Fully Immunized", true);
        } else if (agey >= 5 && hasAnyEmptyValue(client.getColumnmaps(), "_retro", vl)) {
            addStatusTag(this, table, "Partially Immunized", true);
        }

        final List<TableLayout> tableLayouts = new ArrayList<>();
        tableLayouts.add(dt);
        tableLayouts.add(dt2);

        final EditFormSubmissionWrapper editFormSubmissionWrapper = retrieveEditFormSubmissionWrapper();
        Button edtBtn = (Button) findViewById(R.id.child_edit_btn);
        edtBtn.setTag(getString(R.string.edit));
        edtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEditView(view, tableLayouts, editFormSubmissionWrapper);
            }
        });
    }

    @Override
    public void onVaccinateToday(ArrayList<VaccineWrapper> tags, View view) {
        for (VaccineWrapper tag : tags) {
            TableRow tableRow = findRow(tag);
            if (tableRow != null) {
                VaccinateActionUtils.vaccinateToday(tableRow, tag);
            }
        }
    }

    @Override
    public void onVaccinateEarlier(ArrayList<VaccineWrapper> tags, View view) {
        for (VaccineWrapper tag : tags) {
            TableRow tableRow = findRow(tag);
            if (tableRow != null) {
                VaccinateActionUtils.vaccinateEarlier(tableRow, tag);
            }
        }
    }

    @Override
    public void onUndoVaccination(VaccineWrapper tag, View view) {
        TableRow tableRow = findRow(tag);
        if (tableRow != null) {
            VaccinateActionUtils.undoVaccination(this, tableRow, tag);
        }
    }

    private TableRow findRow(VaccineWrapper tag) {
        return VaccinateActionUtils.findRow(tables, tag.getId());
    }

    public VaccinateFormSubmissionWrapper getVaccinateFormSubmissionWrapper() {
        return vaccinateFormSubmissionWrapper;
    }

    @Override
    public void finish() {
        saveFormSubmission(vaccinateFormSubmissionWrapper);
        super.finish();
    }

}

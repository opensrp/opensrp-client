package util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccinateFormSubmissionWrapper;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.fragment.VaccinationDialogFragment;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;
import static util.Utils.convertDateFormat;

/**
 * Created by keyman on 17/11/2016.
 */
public class VaccinateActionUtils {

    public static String formData(Context context, String entityId, String formName, String metaData) {
        try {
            return FormUtils.getInstance(context).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
        } catch (Exception e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
            return null;
        }
    }

    public static void updateJson(JSONObject jsonObject, String field, String value) {
        try {
            if (jsonObject.has(field)) {
                JSONObject fieldJson = jsonObject.getJSONObject(field);
                fieldJson.put("content", value);
            }
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
    }

    public static JSONObject find(JSONObject jsonObject, String field) {
        try {
            if (jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);

            }
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }

        return null;
    }


    public static TableRow findRow(Set<TableLayout> tables, String tag) {
        for (TableLayout table : tables) {
            View view = table.findViewWithTag(tag);
            if (view != null && view instanceof TableRow) {
                return (TableRow) view;
            }
        }
        return null;
    }

    public static TableRow findRow(TableLayout table, String tag) {
        View view = table.findViewWithTag(tag);
        if (view != null && view instanceof TableRow) {
            return (TableRow) view;
        }
        return null;
    }

    public static void vaccinateToday(TableRow tableRow, VaccineWrapper tag) {
        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(R.string.done_today);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        String color = "#31B404";
        Button status = (Button) tableRow.findViewById(R.id.status);
        status.setBackgroundColor(Color.parseColor(color));

        tableRow.setOnClickListener(null);
    }

    public static void vaccinateEarlier(TableRow tableRow, VaccineWrapper tag) {
        String vaccineDate = convertDateFormat(tag.getUpdatedVaccineDateAsString(), true);

        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(vaccineDate);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        String color = "#31B404";
        Button status = (Button) tableRow.findViewById(R.id.status);
        status.setBackgroundColor(Color.parseColor(color));

        tableRow.setOnClickListener(null);
    }

    public static void undoVaccination(final Context context, TableRow tableRow, final VaccineWrapper tag) {
        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.GONE);

        String color = tag.getColor();
        Button status = (Button) tableRow.findViewById(R.id.status);
        status.setBackgroundColor(Color.parseColor(color));

        TextView v = (TextView) tableRow.findViewById(R.id.date);
        v.setText(tag.getFormattedVaccineDate());

        if (tag.getStatus().equalsIgnoreCase("due")) {
            tableRow.setOnClickListener(new TableRow.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(VaccinationDialogFragment.DIALOG_TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    ArrayList<VaccineWrapper> list = new ArrayList<VaccineWrapper>();
                    list.add(tag);
                    VaccinationDialogFragment vaccinationDialogFragment = VaccinationDialogFragment.newInstance(null, list);
                    vaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);

                }
            });
        }

    }

    public static void saveFormSubmission(Context appContext, final String formSubmission, String id, final String formName, JSONObject fieldOverrides) {

        Log.v("fieldoverride", fieldOverrides.toString());

        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(appContext);
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            // Update Fts Tables
            CommonFtsObject commonFtsObject = context.commonFtsObject();
            if (commonFtsObject != null) {
                String[] ftsTables = commonFtsObject.getTables();
                for (String ftsTable : ftsTables) {
                    AllCommonsRepository allCommonsRepository = context.allCommonsRepositoryobjects(ftsTable);
                    boolean updated = allCommonsRepository.updateSearch(submission.entityId());
                    if (updated) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
            e.printStackTrace();
        }
    }

    private static String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, PENDING.value())
                        .map());
    }

    public static JSONObject retrieveFieldOverides(String overrides) {
        try {
            //get the field overrides map
            if (overrides != null) {
                JSONObject json = new JSONObject(overrides);
                String overridesStr = json.getString("fieldOverrides");
                return new JSONObject(overridesStr);
            }
        } catch (Exception e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
        return new JSONObject();

    }

    public static String retrieveExistingAge(VaccinateFormSubmissionWrapper vaccinateFormSubmissionWrapper) {
        try {
            if (vaccinateFormSubmissionWrapper != null) {
                JSONObject fieldOverrides = vaccinateFormSubmissionWrapper.getOverrides();
                if (fieldOverrides.has("existing_age")) {
                    return fieldOverrides.getString("existing_age");
                }
            }
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
        return null;
    }

    public static boolean addDialogHookCustomFilter(VaccineWrapper tag) {
        boolean addHook = false;

        int age = 0;
        String existingAge = tag.getExistingAge();
        if (StringUtils.isNumeric(existingAge)) {
            age = Integer.valueOf(existingAge);
        }

        VaccineRepo.Vaccine vaccine = tag.getVaccine();
        switch (vaccine) {
            case penta1:
            case pcv1:
            case opv1:
                if (age > 35)
                    addHook = true;
                break;
            case penta2:
            case pcv2:
            case opv2:
                if (age > 63)
                    addHook = true;
                break;
            case penta3:
            case pcv3:
            case opv3:
            case ipv:
                if (age > 91)
                    addHook = true;
                break;
            case measles1:
                if (age > 250)
                    addHook = true;
                break;
            case measles2:
                if (age > 340)
                    addHook = true;
                break;
            default:
                addHook = true;
                break;
        }

        return addHook;

    }


    public static String stateKey(VaccineRepo.Vaccine vaccine) {

        switch (vaccine) {
            case opv0:
            case bcg:
                return "at birth";

            case opv1:
            case penta1:
            case pcv1:
            case rota1:
                return "6 weeks";

            case opv2:
            case penta2:
            case pcv2:
            case rota2:
                return "10 weeks";

            case opv3:
            case penta3:
            case pcv3:
                return "14 weeks";

            case measles1:
            case mr1:
            case opv4:
            case ipv:
                return "9 months";

            case measles2:
            case mr2:
                return "18 months";
        }

        return "";
    }

    public static String previousStateKey(String category, Vaccine v) {
        if (v == null || category == null) {
            return null;
        }
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines(category);

        VaccineRepo.Vaccine vaccine = null;
        for (VaccineRepo.Vaccine vrp : vaccines) {
            if (vrp.display().toLowerCase().equalsIgnoreCase(v.getName().toLowerCase())) {
                vaccine = vrp;
            }
        }

        if (vaccine == null) {
            return null;
        } else {
            String stateKey =  stateKey(vaccine);
            if(stateKey.equals( "at birth")){
                stateKey = "Birth";
            }
            return stateKey;
        }
    }

    public static String[] allAlertNames(String category) {
        if (category == null) {
            return null;
        }
        if (category.equals("child")) {

            ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");
            List<String> names = new ArrayList<>();

            for (VaccineRepo.Vaccine vaccine : vaccines) {
                names.add(vaccine.display());
                names.add(vaccine.name());
            }

            return names.toArray(new String[names.size()]);
        }
        return null;
    }

    public static String addHyphen(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replace(" ", "_");
        }
        return s;
    }

    public static String removeHyphen(String s) {
        if (StringUtils.isNotBlank(s)) {
            return s.replace("_", " ");
        }
        return s;
    }

    public static void populateDefaultAlerts(AlertService alertService, List<Vaccine> vaccineList,
                                             List<Alert> alertList, String entityId,
                                             DateTime birthDateTime, VaccineRepo.Vaccine[] vList) {

        if (vList == null || vList.length == 0) {
            return;
        }

        List<Alert> defaultAlerts = new ArrayList<Alert>();
        for (VaccineRepo.Vaccine v : vList) {
            if (!VaccinateActionUtils.hasVaccine(vaccineList, v)) {
                if (!VaccinateActionUtils.hasAlert(alertList, v)) {
                    defaultAlerts.add(VaccinateActionUtils.createDefaultAlert(v, entityId, birthDateTime));
                }
            }
        }

        for (Alert alert : defaultAlerts) {
            alertService.create(alert);
            alertService.updateFtsSearch(alert, true);
        }
    }

    public static boolean hasAlert(List<Alert> alerts, VaccineRepo.Vaccine vaccine) {
        if (alerts == null || alerts.isEmpty() || vaccine == null) {
            return false;
        }

        for (Alert alert : alerts) {
            if (alert.scheduleName().replaceAll(" ", "").equalsIgnoreCase(vaccine.name())
                    || alert.visitCode().replaceAll(" ", "").equalsIgnoreCase(vaccine.name())) {
                return true;
            }
        }
        return false;
    }

    public static Alert getAlert(List<Alert> alerts, VaccineRepo.Vaccine vaccine) {
        if (alerts == null || alerts.isEmpty() || vaccine == null) {
            return null;
        }

        for (Alert alert : alerts) {
            if (alert.scheduleName().replaceAll(" ", "").equalsIgnoreCase(vaccine.name())
                    || alert.visitCode().replaceAll(" ", "").equalsIgnoreCase(vaccine.name())) {
                return alert;
            }
        }
        return null;
    }

    public static boolean hasVaccine(List<Vaccine> vaccineList, VaccineRepo.Vaccine v) {
        if (vaccineList == null || vaccineList.isEmpty() || v == null) {
            return false;
        }

        for (Vaccine vaccine : vaccineList) {
            if (vaccine.getName().equalsIgnoreCase(v.display().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static Alert createDefaultAlert(VaccineRepo.Vaccine vaccine, String entityId, DateTime birthDateTime) {


        AlertStatus alertStatus = AlertStatus.upcoming;
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date birthDate = birthDateTime.toDate();
        if (birthDateTime != null && birthDateTime.plusDays(vaccine.expiryDays()).isBefore(DateTime.now())) {
            alertStatus = AlertStatus.expired;
        } else if (birthDate.getTime() > (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS))) {
            // Vaccination due more than one day from today
            alertStatus = AlertStatus.upcoming;
        } else if (birthDate.getTime() < (today.getTimeInMillis() - TimeUnit.MILLISECONDS.convert(10, TimeUnit.DAYS))) {
            // Vaccination overdue
            alertStatus = AlertStatus.urgent;
        } else {
            alertStatus = AlertStatus.normal;
        }

        return new Alert(entityId, vaccine.display(), vaccine.name(), alertStatus, DateUtil.yyyyMMdd.format(birthDate), null);

    }
}

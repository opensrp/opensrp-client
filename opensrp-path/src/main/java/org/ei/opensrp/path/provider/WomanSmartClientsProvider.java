package org.ei.opensrp.path.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.logger.Logger;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.repository.UniqueIdRepository;
import org.ei.opensrp.path.repository.VaccineRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.path.view.LocationPickerView;
import org.ei.opensrp.repository.DetailsRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import util.DateUtils;
import util.ImageUtils;
import util.JsonFormUtils;
import util.Utils;
import util.VaccinateActionUtils;
import widget.FlowIndicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.ei.opensrp.path.activity.WomanImmunizationActivity.DATE_FORMAT;
import static org.ei.opensrp.path.activity.WomanSmartRegisterActivity.REQUEST_CODE_GET_JSON;
import static util.Utils.fillValue;
import static util.Utils.getName;
import static util.Utils.getValue;
import static util.VaccinatorUtils.generateScheduleList;
import static util.VaccinatorUtils.nextVaccineDue;
import static util.VaccinatorUtils.receivedVaccines;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class WomanSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    VaccineRepository vaccineRepository;
    WeightRepository weightRepository;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private static final String VACCINES_FILE = "vaccines.json";
    private DetailsRepository detailsRepository = null;

    public WomanSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                     AlertService alertService, VaccineRepository vaccineRepository, WeightRepository weightRepository) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.vaccineRepository = vaccineRepository;
        this.weightRepository = weightRepository;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
    }

    @Override
    public void getView(SmartRegisterClient client, final View convertView) {
        final CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        Logger.largeLog("-----------",pc.getDetails().toString());
        Logger.largeLog("-----------",pc.getColumnmaps().toString());

        String name = pc.getDetails().get("first_name") ;
        ((TextView) convertView.findViewById(R.id.name)).setText(name);

        ImageView profileImageIV = (ImageView) convertView.findViewById(R.id.profilepic);
        if (pc.entityId() != null) {//image already in local storage most likey ):
            //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
            profileImageIV.setTag(org.ei.opensrp.R.id.entity_id, pc.entityId());
            DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(pc.entityId(), OpenSRPImageLoader.getStaticImageListener((ImageView) profileImageIV, R.drawable.woman_path_register_logo, R.drawable.woman_path_register_logo));

        }

        View profileview = convertView.findViewById(R.id.profile_info_layout);
        profileview.setTag(pc);
        profileview.setOnClickListener(onClickListener);

        String dobString = getValue(pc.getColumnmaps(), "dob", false);
        String durationString = "";
        if (StringUtils.isNotBlank(dobString)) {
            try {
                DateTime birthDateTime = new DateTime(dobString);
                String duration = DateUtils.getDuration(birthDateTime);
                if (duration != null) {
                    durationString = duration;
                }
            } catch (Exception e) {
                Log.e(getClass().getName(), e.toString(), e);
            }
        }
        fillValue((TextView) convertView.findViewById(R.id.age), durationString);


        String address1 = getValue(pc.getColumnmaps(), "address1", false);
        fillValue((TextView) convertView.findViewById(R.id.address), address1);

        detailsRepository = detailsRepository == null ? org.ei.opensrp.Context.getInstance().updateApplicationContext(context.getApplicationContext()).detailsRepository() : detailsRepository;
        Map<String, String> detailmaps = detailsRepository.getAllDetailsForClient(pc.entityId());
        pc.getColumnmaps().putAll(detailmaps);

        String husbandname = getValue(detailmaps, "spouseName", false);
        fillValue((TextView) convertView.findViewById(R.id.spousename), husbandname);

        fillValue((TextView) convertView.findViewById(R.id.nid), "NID: "+getValue(detailmaps, "nationalId", false));

        final String lmpstring = Utils.getValue(pc.getColumnmaps(), "lmp", false);
        Log.v("lmpstring",lmpstring);
        View recordVaccination = convertView.findViewById(R.id.record_vaccination);
        recordVaccination.setTag(client);
        recordVaccination.setOnClickListener(onClickListener);
        recordVaccination.setVisibility(View.INVISIBLE);


        View add_child = convertView.findViewById(R.id.add_member);
        add_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String metadata = getmetaDataForEditForm(pc);
                Intent intent = new Intent(context, PathJsonFormActivity.class);

                intent.putExtra("json", metadata);

                ((Activity)context).startActivityForResult(intent, REQUEST_CODE_GET_JSON);

            }
        });
//        Intent intent = new Intent(context, PathJsonFormActivity.class);
//
//        intent.putExtra("json", metadata);

        try {
//            Utils.startAsyncTask(new ChildSmartClientsProvider.WeightAsyncTask(convertView, pc.entityId(), lostToFollowUp, inactive), null);
            Utils.startAsyncTask(new WomanSmartClientsProvider.VaccinationAsyncTask(convertView, pc.entityId(), lmpstring), null);
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption
            serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String
            metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_woman_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    private void updateRecordVaccination(View convertView, List<Vaccine> vaccines, List<Alert> alertList, String dobString) {
        View recordVaccination = convertView.findViewById(R.id.record_vaccination);
        recordVaccination.setVisibility(View.VISIBLE);

        TextView recordVaccinationText = (TextView) convertView.findViewById(R.id.record_vaccination_text);
        ImageView recordVaccinationCheck = (ImageView) convertView.findViewById(R.id.record_vaccination_check);
        recordVaccinationCheck.setVisibility(View.GONE);

        convertView.setLayoutParams(clientViewLayoutParams);

        // Alerts
        Map<String, Date> recievedVaccines = receivedVaccines(vaccines);
        Date dateTime = null;
        DateTime dateTimetoSend = null;

        if (!TextUtils.isEmpty(dobString)) {
            SimpleDateFormat lmp_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
            try {
                dateTime = lmp_DATE_FORMAT.parse(dobString);
           } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(dateTime != null){
            dateTimetoSend = new DateTime(dateTime.getTime());
        }

        List<Map<String, Object>> sch = generateScheduleList("mother", dateTimetoSend, recievedVaccines, alertList);

        State state = State.FULLY_IMMUNIZED;
        String stateKey = null;

        Map<String, Object> nv = null;
        if (vaccines.isEmpty()) {
            List<VaccineRepo.Vaccine> vList = Arrays.asList(VaccineRepo.Vaccine.values());
            nv = nextVaccineDue(sch, vList);
        }

        if (nv == null) {
            Date lastVaccine = null;
            if (!vaccines.isEmpty()) {
                Vaccine vaccine = vaccines.get(vaccines.size() - 1);
                lastVaccine = vaccine.getDate();
            }

            nv = nextVaccineDue(sch, lastVaccine);
        }

        if (nv != null) {
            DateTime dueDate = (DateTime) nv.get("date");
            VaccineRepo.Vaccine vaccine = (VaccineRepo.Vaccine) nv.get("vaccine");
            stateKey = VaccinateActionUtils.stateKey(vaccine);
            if (nv.get("alert") == null) {
                state = State.NO_ALERT;
            } else if (((Alert) nv.get("alert")).status().value().equalsIgnoreCase("normal")) {
                state = State.DUE;
            } else if (((Alert) nv.get("alert")).status().value().equalsIgnoreCase("upcoming")) {
                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);

                if (dueDate.getMillis() >= (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) && dueDate.getMillis() < (today.getTimeInMillis() + TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS))) {
                    state = State.UPCOMING_NEXT_7_DAYS;
                } else {
                    state = State.UPCOMING;
                }
            } else if (((Alert) nv.get("alert")).status().value().equalsIgnoreCase("urgent")) {
                state = State.OVERDUE;
            } else if (((Alert) nv.get("alert")).status().value().equalsIgnoreCase("expired")) {
                state = State.EXPIRED;
            }
        } else {
            state = State.WAITING;
        }


//        // Update active/inactive/lostToFollowup status
//        if (lostToFollowUp.equals(Boolean.TRUE.toString())) {
//            state = State.LOST_TO_FOLLOW_UP;
//        }
//
//        if (inactive.equals(Boolean.TRUE.toString())) {
//            state = State.INACTIVE;
//        }

        if (state.equals(State.FULLY_IMMUNIZED)) {
            recordVaccinationText.setText("Fully\nimmunized");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccinationCheck.setImageResource(R.drawable.ic_action_check);
            recordVaccinationCheck.setVisibility(View.VISIBLE);

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);

        } else if (state.equals(State.INACTIVE)) {
            recordVaccinationText.setText("Inactive");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccinationCheck.setImageResource(R.drawable.ic_icon_status_inactive);
            recordVaccinationCheck.setVisibility(View.VISIBLE);

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);


        } else if (state.equals(State.LOST_TO_FOLLOW_UP)) {
            recordVaccinationText.setText("Lost to\nFollow-Up");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccinationCheck.setImageResource(R.drawable.ic_icon_status_losttofollowup);
            recordVaccinationCheck.setVisibility(View.VISIBLE);

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);

        } else if (state.equals(State.WAITING)) {
            recordVaccinationText.setText("Waiting");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);
        } else if (state.equals(State.EXPIRED)) {
            recordVaccinationText.setText("Expired");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);
        } else if (state.equals(State.UPCOMING)) {
            recordVaccinationText.setText("Due\n" + stateKey);
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);
        } else if (state.equals(State.UPCOMING_NEXT_7_DAYS)) {
            recordVaccinationText.setText("Record\n" + stateKey);
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_light_blue_bg));
            recordVaccination.setEnabled(true);
        } else if (state.equals(State.DUE)) {
            recordVaccinationText.setText("Record\n" + stateKey);
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

            recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_blue_bg));
            recordVaccination.setEnabled(true);
        } else if (state.equals(State.OVERDUE)) {
            recordVaccinationText.setText("Record\n" + stateKey);
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.status_bar_text_almost_white));

            recordVaccination.setBackground(context.getResources().getDrawable(R.drawable.due_vaccine_red_bg));
            recordVaccination.setEnabled(true);
        } else if (state.equals(State.NO_ALERT)) {
            if (StringUtils.isNotBlank(stateKey) && (StringUtils.containsIgnoreCase(stateKey, "week") || StringUtils.containsIgnoreCase(stateKey, "month")) && !vaccines.isEmpty()) {
                Vaccine vaccine = vaccines.isEmpty() ? null : vaccines.get(vaccines.size() - 1);
                String previousStateKey = VaccinateActionUtils.previousStateKey("mother", vaccine);
                if (previousStateKey != null) {
                    recordVaccinationText.setText(previousStateKey);
                } else {
                    recordVaccinationText.setText(stateKey);
                }
                recordVaccinationCheck.setImageResource(R.drawable.ic_action_check);
                recordVaccinationCheck.setVisibility(View.VISIBLE);
            } else {
                recordVaccinationText.setText("Due\n" + stateKey);
            }
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);
        } else {
            recordVaccinationText.setText("");
            recordVaccinationText.setTextColor(context.getResources().getColor(R.color.client_list_grey));

            recordVaccination.setBackgroundColor(context.getResources().getColor(R.color.white));
            recordVaccination.setEnabled(false);
        }
    }

    public enum State {
        DUE,
        OVERDUE,
        UPCOMING_NEXT_7_DAYS,
        UPCOMING,
        INACTIVE,
        LOST_TO_FOLLOW_UP,
        EXPIRED,
        WAITING,
        NO_ALERT,
        FULLY_IMMUNIZED
    }
    private class VaccinationAsyncTask extends AsyncTask<Void, Void, Void> {
        private View convertView;
        private String entityId;
        private String dobString;
        private List<Vaccine> vaccines = new ArrayList<>();
        private List<Alert> alerts = new ArrayList<>();

        public VaccinationAsyncTask(View convertView,
                                    String entityId,
                                    String dobString
                                   ) {
            this.convertView = convertView;
            this.entityId = entityId;
            this.dobString = dobString;
        }


        @Override
        protected Void doInBackground(Void... params) {

            vaccines = vaccineRepository.findByEntityId(entityId);
            alerts = alertService.findByEntityIdAndAlertNames(entityId, VaccinateActionUtils.allAlertNames("mother"));
            if(alerts.size() == 0){
                if (!TextUtils.isEmpty(dobString)) {
                    SimpleDateFormat lmp_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
                    Date dateTime = null;
                    Log.v("dobstring",dobString);
                    try {
                        dateTime = lmp_DATE_FORMAT.parse(dobString);
                        VaccineSchedule.updateOfflineAlerts(entityId, new DateTime(dateTime.getTime()), "mother");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                alerts = alertService.findByEntityIdAndAlertNames(entityId, VaccinateActionUtils.allAlertNames("mother"));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            updateRecordVaccination(convertView, vaccines, alerts, dobString);
        }
    }


    private String getmetaDataForEditForm(CommonPersonObjectClient pc) {
        org.ei.opensrp.Context context = VaccinatorApplication.getInstance().context();
        try {
            JSONObject form = FormUtils.getInstance(this.context).getFormJson("child_enrollment");
            LocationPickerView lpv = new LocationPickerView(this.context);
            lpv.init(context);
            JsonFormUtils.addHouseholdRegLocHierarchyQuestions(form, context);
            Log.d("add child form", "Form is " + form.toString());
            if (form != null) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "mother");
                lookup.put("value", pc.entityId());

                UniqueIdRepository uniqueIdRepo = VaccinatorApplication.getInstance().uniqueIdRepository();
                String entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                if (entityId.isEmpty()) {
                    Toast.makeText(context.applicationContext(), context.getInstance().applicationContext().getString(R.string.no_openmrs_id), Toast.LENGTH_SHORT).show();
                }
//
//                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
//                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    if (jsonObject.getString(JsonFormUtils.KEY)
//                            .equalsIgnoreCase(JsonFormUtils.OpenMRS_ID)) {
//                        jsonObject.remove(JsonFormUtils.VALUE);
//                        jsonObject.put(JsonFormUtils.VALUE, entityId);
//                        continue;
//                    }
//                }
                String locationid = "";
                DetailsRepository detailsRepository;
                detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();
                Map<String, String> details = detailsRepository.getAllDetailsForClient(pc.entityId());
                locationid = JsonFormUtils.getOpenMrsLocationId(context,getValue(details, "address4", false) );

                String birthFacilityHierarchy = JsonFormUtils.getOpenMrsLocationHierarchy(
                        context,locationid ).toString();
                //inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(JsonFormUtils.OpenMRS_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                    }
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("HIE_FACILITIES")) {
                        jsonObject.put(JsonFormUtils.VALUE, birthFacilityHierarchy);

                    }
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("Mother_Guardian_First_Name")) {
                        jsonObject.put(JsonFormUtils.READ_ONLY, true);
                        jsonObject.put(JsonFormUtils.VALUE, (Utils.getValue(pc.getDetails(), "first_name", true).isEmpty() ? Utils.getValue(pc.getDetails(), "first_name", true) : Utils.getValue(pc.getDetails(), "first_name", true)));

                    }
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("Mother_Guardian_Last_Name")) {
                        jsonObject.put(JsonFormUtils.READ_ONLY, true);
                        jsonObject.put(JsonFormUtils.VALUE, (Utils.getValue(pc.getDetails(), "last_name", true).isEmpty() ? Utils.getValue(pc.getDetails(), "last_name", true) : Utils.getValue(pc.getDetails(), "last_name", true)));
                    }
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase("Mother_Guardian_Date_Birth")) {
                        jsonObject.put(JsonFormUtils.READ_ONLY, true);
                        if (!TextUtils.isEmpty(Utils.getValue(pc.getDetails(), "dob", true))) {
                            try {
                                DateTime dateTime = new DateTime(Utils.getValue(pc.getDetails(), "dob", true));
                                Date dob = dateTime.toDate();
                                Date defaultDate = DATE_FORMAT.parse(JsonFormUtils.MOTHER_DEFAULT_DOB);
                                long timeDiff = Math.abs(dob.getTime() - defaultDate.getTime());
                                if (timeDiff > 86400000) {// Mother's date of birth occurs more than a day from the default date
                                    jsonObject.put(JsonFormUtils.VALUE, DATE_FORMAT.format(dob));
                                }
                            } catch (Exception e) {
                            }
                        }
                    }





                }
//            intent.putExtra("json", form.toString());
//            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
                return form.toString();
            }
        } catch (Exception e) {
            Log.e("exception in addchild", e.getMessage());
        }

        return "";
    }
}
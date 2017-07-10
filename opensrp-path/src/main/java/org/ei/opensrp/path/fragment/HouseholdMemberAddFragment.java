package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.clientandeventmodel.Event;
import org.ei.opensrp.clientandeventmodel.Obs;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseRegisterActivity;
import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.path.activity.HouseholdSmartRegisterActivity;
import org.ei.opensrp.path.activity.PathJsonFormActivity;
import org.ei.opensrp.path.activity.WomanSmartRegisterActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.WeightWrapper;
import org.ei.opensrp.path.listener.WeightActionListener;
import org.ei.opensrp.path.repository.UniqueIdRepository;
import org.ei.opensrp.path.repository.WeightRepository;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import util.ImageUtils;
import util.JsonFormUtils;

@SuppressLint("ValidFragment")
public class HouseholdMemberAddFragment extends DialogFragment {
    private final Context context;
    private WeightActionListener listener;
    private static final int REQUEST_CODE_GET_JSON = 3432;
    private static final String TAG = "AddFragment";
    public static final String DIALOG_TAG = "HouseholdMemberAddFragment";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String HouseholdEnitityID  = "";
    String locationId;
    org.ei.opensrp.Context opensrpcontext;

    private HouseholdMemberAddFragment(Context context) {
        this.context = context;

    }

    public static HouseholdMemberAddFragment newInstance(
            Context context, String locationId, String householdid, org.ei.opensrp.Context context1) {
        HouseholdMemberAddFragment householdMemberAddFragment = new HouseholdMemberAddFragment(context);
        householdMemberAddFragment.HouseholdEnitityID = householdid;
        householdMemberAddFragment.locationId = locationId;
        householdMemberAddFragment.opensrpcontext = context1;
        return householdMemberAddFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.household_add_member_dialog_view, container, false);

        Button addChild = (Button) dialogView.findViewById(R.id.add_child);
        Button addWoman = (Button) dialogView.findViewById(R.id.add_woman);
        Button cancel = (Button) dialogView.findViewById(R.id.cancel);

        addChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("------------","addChild");
                ((ChildSmartRegisterActivity) getActivity()).startFormActivity("child_enrollment", null, null);
            }
        });

        addWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject form = FormUtils.getInstance(getActivity().getApplicationContext()).getFormJson("woman_member_registration");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    startForm((Activity) context,opensrpcontext,REQUEST_CODE_GET_JSON,"woman_member_registration", null, null,locationId,HouseholdEnitityID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialogView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the WeightActionListener so we can send events to the host
//            listener = (WeightActionListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement WeightActionListener");
//        }
    }



    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getDialog().getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            }
        });

    }
    public static void startForm(Activity context, org.ei.opensrp.Context openSrpContext,
                                 int jsonFormActivityRequestCode,
                                 String formName, String entityId, String metaData,
                                 String currentLocationId,String HouseholdEnitityID) throws Exception {
        Intent intent = new Intent(context, PathJsonFormActivity.class);

        JSONObject form = FormUtils.getInstance(context).getFormJson(formName);
        if (form != null) {
            form.getJSONObject("metadata").put("encounter_location", currentLocationId);

            if (formName.equals("child_enrollment")) {
                if (StringUtils.isBlank(entityId)) {
                    UniqueIdRepository uniqueIdRepo = VaccinatorApplication.getInstance().uniqueIdRepository();
                    entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                    if (entityId.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.no_openmrs_id), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                }

                JsonFormUtils.addChildRegLocHierarchyQuestions(form, openSrpContext);

                // Inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY)
                            .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                        continue;
                    }
                }
            } else if (formName.equals("out_of_catchment_service")) {
                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                } else {
                    JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        if (fields.getJSONObject(i).getString("key").equals("ZEIR_ID")) {
                            fields.getJSONObject(i).put(JsonFormUtils.READ_ONLY, false);
                            break;
                        }
                    }
                }

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY)
                            .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                        continue;
                    }
                }

                JsonFormUtils.addAddAvailableVaccines(context, form);
            } else if (formName.equals("household_registration")) {
                JsonFormUtils.addHouseholdRegLocHierarchyQuestions(form, openSrpContext);

            } else if (formName.equals("woman_member_registration")) {
                JSONObject metaDataJson = form.getJSONObject("metadata");
                JSONObject lookup = metaDataJson.getJSONObject("look_up");
                lookup.put("entity_id", "household");
                lookup.put("value", HouseholdEnitityID);
                JsonFormUtils.addHouseholdRegLocHierarchyQuestions(form, openSrpContext);

            } else {
                Log.w(TAG, "Unsupported form requested for launch " + formName);
            }

            intent.putExtra("json", form.toString());
            Log.d(TAG, "form is " + form.toString());
            context.startActivityForResult(intent, jsonFormActivityRequestCode);
        }

    }

    public static Event addMetaData(Context context, Event event, Date start) throws JSONException {
        Map<String, String> metaFields = new HashMap<String, String>();
        metaFields.put("deviceid", "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Calendar calendar = Calendar.getInstance();

        String end = DATE_TIME_FORMAT.format(calendar.getTime());

        Obs obs = new Obs();
        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(DATE_TIME_FORMAT.format(start));
        obs.setFieldType("concept");
        obs.setFieldDataType("start");
        event.addObs(obs);


        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(end);
        obs.setFieldDataType("end");
        event.addObs(obs);

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceId = mTelephonyManager.getSimSerialNumber();

        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(deviceId);
        obs.setFieldDataType("deviceid");
        event.addObs(obs);
        return event;
    }
}

package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.TimeUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.DatePickerDialog;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 25/01/2017
 */
public class DatePickerFactory implements FormWidgetFactory {
    private static final String TAG = "DatePickerFactory";
    private static final long DAY_MILLSECONDS = 86400000;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String DATE_FORMAT_REGEX = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)|\\s*";

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        try {

            RelativeLayout dateViewRelativeLayout = (RelativeLayout) LayoutInflater
                    .from(context).inflate(R.layout.item_date_picker, null);

            MaterialEditText editText = (MaterialEditText) dateViewRelativeLayout.findViewById(R.id.edit_text);

            TextView duration = (TextView) dateViewRelativeLayout.findViewById(R.id.duration);

            attachJson(stepName, context, formFragment, jsonObject, editText, duration);

            JSONArray canvasIds = new JSONArray();
            dateViewRelativeLayout.setId(ViewUtil.generateViewId());
            canvasIds.put(dateViewRelativeLayout.getId());
            editText.setTag(R.id.canvas_ids, canvasIds.toString());

            ((JsonApi) context).addFormDataView(editText);
            views.add(dateViewRelativeLayout);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    protected void attachJson(String stepName, final Context context, JsonFormFragment formFragment, JSONObject jsonObject, final MaterialEditText editText, final TextView duration){

        try {
            String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
            String openMrsEntity = jsonObject.getString("openmrs_entity");
            String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
            String relevance = jsonObject.optString("relevance");
            String constraints = jsonObject.optString("constraints");

            duration.setTag(R.id.key, jsonObject.getString("key"));
            duration.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            duration.setTag(R.id.openmrs_entity, openMrsEntity);
            duration.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            if (jsonObject.has("duration")) {
                duration.setTag(R.id.label, jsonObject.getJSONObject("duration").getString("label"));
            }

            editText.setHint(jsonObject.getString("hint"));
            editText.setFloatingLabelText(jsonObject.getString("hint"));
            editText.setId(ViewUtil.generateViewId());
            editText.setTag(R.id.key, jsonObject.getString("key"));
            editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            editText.setTag(R.id.openmrs_entity, openMrsEntity);
            editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            editText.setTag(R.id.address, stepName + ":" + jsonObject.getString("key"));
            if (jsonObject.has("v_required")) {
                JSONObject requiredObject = jsonObject.optJSONObject("v_required");
                String requiredValue = requiredObject.getString("value");
                if (!TextUtils.isEmpty(requiredValue)) {
                    if (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                        editText.addValidator(new RequiredValidator(requiredObject.getString("err")));
                    }
                }
            }

            if (!TextUtils.isEmpty(jsonObject.optString("value"))) {
                updateDateText(editText, duration, jsonObject.optString("value"));
            } else if (jsonObject.has("default")) {
                updateDateText(editText, duration,
                        DATE_FORMAT.format(getDate(jsonObject.getString("default")).getTime()));
            }

            if (jsonObject.has("read_only")) {
                boolean readOnly = jsonObject.getBoolean("read_only");
                editText.setEnabled(!readOnly);
                editText.setFocusable(!readOnly);
            }

            editText.addValidator(new RegexpValidator(
                    context.getResources().getString(R.string.badly_formed_date),
                    DATE_FORMAT_REGEX));

            Calendar date = getDate(editText.getText().toString());
            final DatePickerDialog datePickerDialog = new DatePickerDialog(context);

            datePickerDialog.setOnDateSetListener(new android.app.DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar calendarDate = Calendar.getInstance();
                    calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    calendarDate.set(Calendar.MONTH, monthOfYear);
                    calendarDate.set(Calendar.YEAR, year);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                            && calendarDate.getTimeInMillis() >= view.getMinDate()
                            && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                        updateDateText(editText, duration,
                                DATE_FORMAT.format(calendarDate.getTime()));
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        updateDateText(editText, duration, "");
                    }
                }
            });

            if (jsonObject.has("min_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Calendar minDate = getDate(jsonObject.getString("min_date"));
                minDate.set(Calendar.HOUR_OF_DAY, 0);
                minDate.set(Calendar.MINUTE, 0);
                minDate.set(Calendar.SECOND, 0);
                minDate.set(Calendar.MILLISECOND, 0);
                datePickerDialog.setMinDate(minDate.getTimeInMillis());
            }

            if (jsonObject.has("max_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Calendar maxDate = getDate(jsonObject.getString("max_date"));
                maxDate.set(Calendar.HOUR_OF_DAY, 23);
                maxDate.set(Calendar.MINUTE, 59);
                maxDate.set(Calendar.SECOND, 59);
                maxDate.set(Calendar.MILLISECOND, 999);
                datePickerDialog.setMaxDate(maxDate.getTimeInMillis());
            }

            if (jsonObject.has("expanded") && jsonObject.getBoolean("expanded") == true
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.setCalendarViewShown(true);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.setCalendarViewShown(false);
            }

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog((Activity) context, datePickerDialog, editText);
                }
            });

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    updateDateText(editText, duration, "");
                    return true;
                }
            });

            GenericTextWatcher genericTextWatcher = new GenericTextWatcher(stepName, formFragment, editText);
            genericTextWatcher.addOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showDatePickerDialog((Activity) context, datePickerDialog, editText);
                    }
                }
            });
            editText.addTextChangedListener(genericTextWatcher);

            if (relevance != null && context instanceof JsonApi) {
                editText.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(editText);
            }

            if (constraints != null && context instanceof JsonApi) {
                editText.setTag(R.id.constraints, constraints);
                ((JsonApi) context).addConstrainedView(editText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private static void updateDateText(MaterialEditText editText, TextView duration, String date) {
        editText.setText(date);
        String durationLabel = (String) duration.getTag(R.id.label);
        if (!TextUtils.isEmpty(durationLabel)) {
            String durationText = getDuration(date);
            if (!TextUtils.isEmpty(durationText)) {
                durationText = String.format("(%s: %s)", durationLabel, durationText);
            }
            duration.setText(durationText);
        }
    }

    private static String getDuration(String date) {
        if (!TextUtils.isEmpty(date)) {
            Calendar calendar = getDate(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);

            long timeDiff = Math.abs(now.getTimeInMillis() - calendar.getTimeInMillis());
            StringBuilder builder = new StringBuilder();
            TimeUtils.formatDuration(timeDiff, builder);
            String duration = "";
            if (timeDiff >= 0
                    && timeDiff <= TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)) {
                // Represent in days
                long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                duration = days + "d";
            } else if (timeDiff > TimeUnit.MILLISECONDS.convert(13, TimeUnit.DAYS)
                    && timeDiff <= TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)) {
                // Represent in weeks and days
                int weeks = (int) Math.floor((float) timeDiff /
                        TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));
                int days = (int) Math.floor((float) (timeDiff -
                        TimeUnit.MILLISECONDS.convert(weeks * 7, TimeUnit.DAYS)) /
                        TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

                if (days >= 7) {
                    days = 0;
                    weeks++;
                }

                duration = weeks + "w";
                if (days > 0) {
                    duration += " " + days + "d";
                }
            } else if (timeDiff > TimeUnit.MILLISECONDS.convert(97, TimeUnit.DAYS)
                    && timeDiff <= TimeUnit.MILLISECONDS.convert(363, TimeUnit.DAYS)) {
                // Represent in months and weeks
                int months = (int) Math.floor((float) timeDiff /
                        TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
                int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                        months * 30, TimeUnit.DAYS)) /
                        TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

                if (weeks >= 4) {
                    weeks = 0;
                    months++;
                }

                if(months < 12) {
                    duration = months + "m";
                    if (weeks > 0 && months < 12) {
                        duration += " " + weeks + "w";
                    }
                }
                else if (months >= 12) {
                    duration = "1y";
                }
            } else {
                // Represent in years and months
                int years = (int) Math.floor((float) timeDiff
                        / TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
                int months = (int) Math.floor((float) (timeDiff -
                        TimeUnit.MILLISECONDS.convert(years * 365, TimeUnit.DAYS)) /
                        TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

                if (months >= 12) {
                    months = 0;
                    years++;
                }

                duration = years + "y";
                if (months > 0) {
                    duration += " " + months + "m";
                }
            }

            return duration;
        }
        return null;
    }

    private static void showDatePickerDialog(Activity context,
                                             DatePickerDialog datePickerDialog,
                                             MaterialEditText editText) {
        FragmentTransaction ft = context.getFragmentManager().beginTransaction();
        Fragment prev = context.getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        datePickerDialog.show(ft, TAG);
        Calendar date = getDate(editText.getText().toString());
        datePickerDialog.setDate(date.getTime());
    }

    /**
     * This method returns a {@link Calendar} object at mid-day corresponding to a date matching
     * the format specified in {@code DATE_FORMAT} or a day in reference to today e.g today,
     * today-1, today+10
     *
     * @param dayString The string to be converted to a date
     * @return The calendar object corresponding to the day, or object corresponding to today's
     * date if an error occurred
     */
    private static Calendar getDate(String dayString) {
        Calendar calendarDate = Calendar.getInstance();

        if (dayString != null && dayString.trim().length() > 0) {
            dayString = dayString.trim().toLowerCase();
            if (!dayString.equals("today")) {
                Pattern pattern = Pattern.compile("today\\s*([-\\+])\\s*(\\d+)([dmyDMY]{1})");
                Matcher matcher = pattern.matcher(dayString);
                if (matcher.find()) {
                    int timeValue = Integer.parseInt(matcher.group(2));
                    if (matcher.group(1).equals("-")) {
                        timeValue = timeValue * -1;
                    }

                    int field = Calendar.DATE;
                    if (matcher.group(3).toLowerCase().equals("y")) {
                        field = Calendar.YEAR;
                    } else if (matcher.group(3).toLowerCase().equals("m")) {
                        field = Calendar.MONTH;
                    }

                    calendarDate.add(field, timeValue);
                } else {
                    try {
                        calendarDate.setTime(DATE_FORMAT.parse(dayString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //set time to mid-day
        calendarDate.set(Calendar.HOUR_OF_DAY, 12);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        return calendarDate;
    }
}

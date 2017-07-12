package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.listener.VaccinationActionListener;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import util.ImageUtils;

@SuppressLint("ValidFragment")
public class VaccinationEditDialogFragment extends DialogFragment {
    private final Context context;
    private final ArrayList<VaccineWrapper> tags;
    private final View viewGroup;
    private VaccinationActionListener listener;
    private Date dateOfBirth;
    private List<Vaccine> issuedVaccines;
    public static final String DIALOG_TAG = "VaccinationEditDialogFragment";

    private VaccinationEditDialogFragment(Context context, Date dateOfBirth,
                                          List<Vaccine> issuedVaccines,
                                          List<VaccineWrapper> tags, View viewGroup) {
        this.context = context;
        this.dateOfBirth = dateOfBirth;
        this.issuedVaccines = issuedVaccines;
        this.tags = new ArrayList<>(tags);
        this.viewGroup = viewGroup;
    }

    public static VaccinationEditDialogFragment newInstance(
            Context context, Date dateOfBirth,
            List<Vaccine> issuedVaccines,
            List<VaccineWrapper> tags, View viewGroup) {
        return new VaccinationEditDialogFragment(context, dateOfBirth, issuedVaccines, tags, viewGroup);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if (tags == null || tags.isEmpty()) {
            return null;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.vaccination_edit_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tags.get(0).getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tags.get(0).getPatientNumber());
        TextView service_date = (TextView) dialogView.findViewById(R.id.service_date);
        service_date.setText("Service date: "+tags.get(0).getUpdatedVaccineDateAsString()+"");
        final LinearLayout vaccinationNameLayout = (LinearLayout) dialogView.findViewById(R.id.vaccination_name_layout);

        if (tags.size() == 1) {
            View vaccinationName = inflater.inflate(R.layout.vaccination_name_edit_dialog, null);
            TextView vaccineView = (TextView) vaccinationName.findViewById(R.id.vaccine);

            VaccineWrapper vaccineWrapper = tags.get(0);
            VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
            if (vaccine != null) {
                vaccineView.setText(vaccine.display());
            } else {
                vaccineView.setText(vaccineWrapper.getName());
            }
            ImageView select = (ImageView) vaccinationName.findViewById(R.id.imageView);
//            select.setVisibility(View.GONE);

            vaccinationNameLayout.addView(vaccinationName);
        } else {
            for (VaccineWrapper vaccineWrapper : tags) {

                View vaccinationName = inflater.inflate(R.layout.vaccination_name_edit_dialog, null);
                TextView vaccineView = (TextView) vaccinationName.findViewById(R.id.vaccine);

                VaccineRepo.Vaccine vaccine = vaccineWrapper.getVaccine();
                if (vaccineWrapper.getVaccine() != null) {
                    vaccineView.setText(vaccine.display());
                } else {
                    vaccineView.setText(vaccineWrapper.getName());
                }

                vaccinationNameLayout.addView(vaccinationName);
            }

            Button vaccinateToday = (Button) dialogView.findViewById(R.id.vaccinate_today);
            vaccinateToday.setText(vaccinateToday.getText().toString().replace("Vaccination", "Vaccinations"));

            Button vaccinateEarlier = (Button) dialogView.findViewById(R.id.vaccinate_earlier);
            vaccinateEarlier.setText(vaccinateEarlier.getText().toString().replace("Vaccination", "Vaccinations"));
        }

        if (tags.get(0).getId() != null) {
            ImageView mImageView = (ImageView) dialogView.findViewById(R.id.child_profilepic);
            if (tags.get(0).getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(org.ei.opensrp.R.id.entity_id, tags.get(0).getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(tags.get(0).getId(), OpenSRPImageLoader.getStaticImageListener((ImageView) mImageView, ImageUtils.profileImageResourceByGender(tags.get(0).getGender()), ImageUtils.profileImageResourceByGender(tags.get(0).getGender())));
            }
        }

        final DatePicker earlierDatePicker = (DatePicker) dialogView.findViewById(R.id.earlier_date_picker);

        String color = tags.get(0).getColor();
        Button status = (Button) dialogView.findViewById(R.id.status);
        if (status != null) {
            status.setBackgroundColor(StringUtils.isBlank(color) ? Color.WHITE : Color.parseColor(color));
        }

        final Button set = (Button) dialogView.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                int day = earlierDatePicker.getDayOfMonth();
                int month = earlierDatePicker.getMonth();
                int year = earlierDatePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,day);
//                calendar.set(year, month, day);
//                calendar.setTimeZone(calendar.getTimeZone());
                DateTime dateTime = new DateTime(calendar.getTime());

                if (tags.size() == 1) {
                    if (validateVaccinationDate(tags.get(0), dateTime.toDate())) {
                        tags.get(0).setUpdatedVaccineDate(dateTime, true);
                    } else {
                        Toast.makeText(getActivity(),
                                String.format(getString(R.string.cannot_record_vaccine),
                                        tags.get(0).getName()), Toast.LENGTH_LONG).show();
                    }
                } else
                    for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
                        View chilView = vaccinationNameLayout.getChildAt(i);
                        CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
                        if (selectChild.isChecked()) {
                            TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                            String checkedName = childVaccineView.getText().toString();
                            VaccineWrapper tag = searchWrapperByName(checkedName);
                            if (tag != null) {
                                if (validateVaccinationDate(tag, dateTime.toDate())) {
                                    tag.setUpdatedVaccineDate(dateTime, false);
                                } else {
                                    Toast.makeText(getActivity(),
                                            String.format(getString(R.string.cannot_record_vaccine),
                                                    tag.getName()), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                listener.onVaccinateEarlier(tags, viewGroup);

            }
        });

        final Button vaccinateToday = (Button) dialogView.findViewById(R.id.vaccinate_today);
        vaccinateToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaccinateToday.setVisibility(View.GONE);
                earlierDatePicker.setVisibility(View.VISIBLE);
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});

//                dismiss();
//
//                Calendar calendar = Calendar.getInstance();
//                DateTime dateTime = new DateTime(calendar.getTime());
//                if (tags.size() == 1) {
//                    tags.get(0).setUpdatedVaccineDate(dateTime, true);
//                } else
//                    for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
//                        View chilView = vaccinationNameLayout.getChildAt(i);
//                        CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
//                        if (selectChild.isChecked()) {
//                            TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
//                            String checkedName = childVaccineView.getText().toString();
//                            VaccineWrapper tag = searchWrapperByName(checkedName);
//                            if (tag != null) {
//                                tag.setUpdatedVaccineDate(dateTime, true);
//                            }
//                        }
//                    }
//
//                listener.onVaccinateToday(tags, viewGroup);

            }
        });

        final Button vaccinateEarlier = (Button) dialogView.findViewById(R.id.vaccinate_earlier);
        vaccinateEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (tags.size() == 1) {
//                    tags.get(0).setUpdatedVaccineDate(dateTime, true);
                    listener.onUndoVaccination(tags.get(0),viewGroup);
                } else
                    for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
                        View chilView = vaccinationNameLayout.getChildAt(i);
                        CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
                        if (selectChild.isChecked()) {
                            TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                            String checkedName = childVaccineView.getText().toString();
                            VaccineWrapper tag = searchWrapperByName(checkedName);
                            listener.onUndoVaccination(tag,viewGroup);
                        }
                    }
//                listener.onUndoVaccination(tags,viewGroup);
            }
        });

        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
            View chilView = vaccinationNameLayout.getChildAt(i);
            chilView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox childSelect = (CheckBox) view.findViewById(R.id.select);
                    childSelect.toggle();
                }
            });
        }

        updateDateRanges(earlierDatePicker, set);


        return dialogView;
    }

    private boolean validateVaccinationDate(VaccineWrapper vaccine, Date date) {
        // Assuming that the vaccine wrapper provided to this method isn't one where there's more than one vaccine in a wrapper
        Date minDate = getMinVaccineDate(vaccine.getName());
        Date maxDate = getMaxVaccineDate(vaccine.getName());
        Calendar vaccineDate = Calendar.getInstance();
        vaccineDate.setTime(date);
        VaccineSchedule.standardiseCalendarDate(vaccineDate);
        boolean result = true;

        // A null min date means the vaccine is not due (probably because the prerequisite hasn't been done yet)
        result = result && minDate != null;

        // Check if vaccination was done before min date
        if (minDate != null) {
            Calendar min = Calendar.getInstance();
            min.setTime(minDate);
            VaccineSchedule.standardiseCalendarDate(min);

            result = result && min.getTimeInMillis() <= vaccineDate.getTimeInMillis();
        }

        // A null max date means the vaccine doesn't have a max date check
        //Check if vaccination was done after the max date
        if (maxDate != null) {
            Calendar max = Calendar.getInstance();
            max.setTime(maxDate);
            VaccineSchedule.standardiseCalendarDate(max);

            result = result && vaccineDate.getTimeInMillis() <= max.getTimeInMillis();
        }

        return result;
    }

    /**
     * This method updates the allowed date ranges in the views
     *
     * @param datePicker Date picker for selecting a previous date for a vaccine
     */
    private void updateDateRanges(DatePicker datePicker, Button set) {
        Calendar today = Calendar.getInstance();
        VaccineSchedule.standardiseCalendarDate(today);
        Calendar minDate = null;
        Calendar maxDate = null;

        for (VaccineWrapper curVaccine : tags) {
            if (!curVaccine.getName().contains("/")) {
                minDate = updateMinVaccineDate(minDate, curVaccine.getName());
                maxDate = updateMaxVaccineDate(maxDate, curVaccine.getName());
            } else {
                String[] sisterVaccines = curVaccine.getName().split(" / ");
                for (int i = 0; i < sisterVaccines.length; i++){
                    minDate = updateMinVaccineDate(minDate, sisterVaccines[i]);
                    maxDate = updateMaxVaccineDate(maxDate, sisterVaccines[i]);
                }
            }
        }

        VaccineSchedule.standardiseCalendarDate(minDate);
        VaccineSchedule.standardiseCalendarDate(maxDate);

        if (maxDate.getTimeInMillis() >= minDate.getTimeInMillis()) {
            set.setVisibility(View.INVISIBLE);
            datePicker.setMinDate(minDate.getTimeInMillis());
            datePicker.setMaxDate(maxDate.getTimeInMillis());
        } else {
            set.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), R.string.problem_applying_vaccine_constraints, Toast.LENGTH_LONG).show();
        }
    }

    private Calendar updateMinVaccineDate(Calendar minDate, String vaccineName) {
        Date dueDate = getMinVaccineDate(vaccineName);
        if (dueDate == null
                || dueDate.getTime() < dateOfBirth.getTime()) {
            dueDate = dateOfBirth;
        }

        if (minDate == null) {
            minDate = Calendar.getInstance();
            minDate.setTime(dueDate);
        } else if (dueDate.getTime() > minDate.getTimeInMillis()) {
            minDate.setTime(dueDate);
        }

        return minDate;
    }

    private Calendar updateMaxVaccineDate(Calendar maxDate, String vaccineName) {
        Date expiryDate = getMaxVaccineDate(vaccineName);
        if (expiryDate == null
                || expiryDate.getTime() > Calendar.getInstance().getTimeInMillis()) {
            expiryDate = Calendar.getInstance().getTime();
        }

        if (maxDate == null) {
            maxDate = Calendar.getInstance();
            maxDate.setTime(expiryDate);
        } else if (expiryDate.getTime() < maxDate.getTimeInMillis()) {
            maxDate.setTime(expiryDate);
        }

        return maxDate;
    }

    private Date getMinVaccineDate(String vaccineName) {
        VaccineSchedule curVaccineSchedule = VaccineSchedule.getVaccineSchedule("child",
                vaccineName);
        Date minDate = null;

        if (curVaccineSchedule != null) {
            minDate = curVaccineSchedule.getDueDate(issuedVaccines, dateOfBirth);
        }

        return minDate;
    }

    private Date getMaxVaccineDate(String vaccineName) {
        VaccineSchedule curVaccineSchedule = VaccineSchedule.getVaccineSchedule("child",
                vaccineName);
        Date maxDate = null;

        if (curVaccineSchedule != null) {
            maxDate = curVaccineSchedule.getExpiryDate(issuedVaccines, dateOfBirth);
        }

        return maxDate;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VaccinationActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement VaccinationActionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Window window = getDialog().getWindow();
                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                int width = size.x;

                window.setLayout((int) (width * 0.7), FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
            }
        });
    }

    private VaccineWrapper searchWrapperByName(String name) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        for (VaccineWrapper tag : tags) {
            if (tag.getVaccine() != null) {
                if (tag.getVaccine().display().equals(name)) {
                    return tag;
                }
            } else {
                if (tag.getName().equals(name)) {
                    return tag;
                }
            }
        }
        return null;
    }
}

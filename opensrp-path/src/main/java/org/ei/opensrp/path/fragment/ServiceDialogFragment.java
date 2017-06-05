package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.ServiceSchedule;
import org.ei.opensrp.path.domain.ServiceWrapper;
import org.ei.opensrp.path.listener.ServiceActionListener;
import org.ei.opensrp.path.service.intent.RecurringIntentService;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import util.ImageUtils;
import util.VaccinatorUtils;

@SuppressLint("ValidFragment")
public class ServiceDialogFragment extends DialogFragment {
    private ServiceWrapper tag;
    private ServiceActionListener listener;
    public static final String DIALOG_TAG = "ServiceDialogFragment";
    public static final String WRAPPER_TAG = "tag";
    private List<ServiceRecord> issuedServices;

    public static ServiceDialogFragment newInstance(
            List<ServiceRecord> issuedServices,
            ServiceWrapper tag) {

        ServiceDialogFragment serviceDialogFragment = new ServiceDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(WRAPPER_TAG, tag);
        serviceDialogFragment.setArguments(args);
        serviceDialogFragment.setIssuedServices(issuedServices);

        return serviceDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Serializable serializable = bundle.getSerializable(WRAPPER_TAG);
        if (serializable != null && serializable instanceof ServiceWrapper) {
            tag = (ServiceWrapper) serializable;
        }

        if (tag == null) {
            return null;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.service_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tag.getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tag.getPatientNumber());

        // service name
        final LinearLayout nameLayout = (LinearLayout) dialogView.findViewById(R.id.service_name_layout);

        View serviceName = inflater.inflate(R.layout.service_name, null);

        String name = tag.getName();
        if (name.contains("Vit")) {
            name = name.replace("Vit", "Vitamin");
        }
        TextView serviceView = (TextView) serviceName.findViewById(R.id.service);
        serviceView.setText(name);

        TextView unitsView = (TextView) serviceName.findViewById(R.id.units);
        unitsView.setVisibility(View.GONE);
        if (StringUtils.isNotBlank(tag.getUnits())) {
            unitsView.setText(tag.getUnits());
            unitsView.setVisibility(View.VISIBLE);
        }

        nameLayout.addView(serviceName);

        // image view
        if (tag.getId() != null) {
            ImageView mImageView = (ImageView) dialogView.findViewById(R.id.child_profilepic);
            if (tag.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(org.ei.opensrp.R.id.entity_id, tag.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(tag.getId(), OpenSRPImageLoader.getStaticImageListener((ImageView) mImageView, ImageUtils.profileImageResourceByGender(tag.getGender()), ImageUtils.profileImageResourceByGender(tag.getGender())));
            }
        }

        String color = tag.getColor();
        Button status = (Button) dialogView.findViewById(R.id.status);
        if (status != null) {
            status.setBackgroundColor(StringUtils.isBlank(color) ? Color.WHITE : Color.parseColor(color));
        }

        // Actions
        View defaultActions = dialogView.findViewById(R.id.default_actions);
        defaultActions.setVisibility(View.GONE);

        View itnActions = dialogView.findViewById(R.id.itn_actions);
        itnActions.setVisibility(View.GONE);

        if (tag.getType().equalsIgnoreCase("ITN")) {
            itnActions.setVisibility(View.VISIBLE);

            final View step1 = itnActions.findViewById(R.id.step_1);
            final View step2 = itnActions.findViewById(R.id.step_2);
            final View step3 = itnActions.findViewById(R.id.step_3);

            step1.setVisibility(View.VISIBLE);

            step2.setVisibility(View.GONE);
            step3.setVisibility(View.GONE);

            // step 1
            Button yes1 = (Button) step1.findViewById(R.id.yes_1);
            yes1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step2.setVisibility(View.VISIBLE);

                    step1.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                }
            });


            Button no1 = (Button) step1.findViewById(R.id.no_1);
            no1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step3.setVisibility(View.VISIBLE);

                    step1.setVisibility(View.GONE);
                    step2.setVisibility(View.GONE);
                }
            });

            Button cancel1 = (Button) step1.findViewById(R.id.cancel_1);
            cancel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            // step 2
            final DatePicker itnDatePicker = (DatePicker) step2.findViewById(R.id.itn_date_picker);
            Button recordItn = (Button) step2.findViewById(R.id.record_itn);
            recordItn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                    int day = itnDatePicker.getDayOfMonth();
                    int month = itnDatePicker.getMonth();
                    int year = itnDatePicker.getYear();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    DateTime dateTime = new DateTime(calendar.getTime());

                    tag.setUpdatedVaccineDate(dateTime, false);
                    tag.setValue(RecurringIntentService.ITN_PROVIDED);
                    listener.onGiveEarlier(tag, v);
                }
            });


            Button goBack2 = (Button) step2.findViewById(R.id.go_back_2);
            goBack2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step1.setVisibility(View.VISIBLE);

                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                }
            });

            // step 3
            Button yes3 = (Button) step3.findViewById(R.id.yes_3);
            yes3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                    Calendar calendar = Calendar.getInstance();
                    DateTime dateTime = new DateTime(calendar.getTime());

                    tag.setUpdatedVaccineDate(dateTime, true);
                    tag.setValue(RecurringIntentService.CHILD_HAS_NET);
                    listener.onGiveToday(tag, v);
                }
            });

            Button no_3 = (Button) step3.findViewById(R.id.no_3);
            no_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step1.setVisibility(View.VISIBLE);

                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                }
            });

            Button goBack3 = (Button) step3.findViewById(R.id.go_back_3);
            goBack3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    step1.setVisibility(View.VISIBLE);

                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                }
            });

            updateDateRanges(itnDatePicker, recordItn);


        } else {
            defaultActions.setVisibility(View.VISIBLE);

            final DatePicker earlierDatePicker = (DatePicker) defaultActions.findViewById(R.id.earlier_date_picker);

            final Button set = (Button) defaultActions.findViewById(R.id.set);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();

                    int day = earlierDatePicker.getDayOfMonth();
                    int month = earlierDatePicker.getMonth();
                    int year = earlierDatePicker.getYear();

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    DateTime dateTime = new DateTime(calendar.getTime());

                    tag.setUpdatedVaccineDate(dateTime, false);
                    listener.onGiveEarlier(tag, view);

                }
            });

            Button givenToday = (Button) defaultActions.findViewById(R.id.given_today);
            givenToday.setText(String.format(getString(R.string.given_today), tag.getName()));
            givenToday.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();

                    Calendar calendar = Calendar.getInstance();
                    DateTime dateTime = new DateTime(calendar.getTime());
                    tag.setUpdatedVaccineDate(dateTime, true);
                    listener.onGiveToday(tag, view);

                }
            });

            final Button givenEarlier = (Button) defaultActions.findViewById(R.id.given_earlier);
            givenEarlier.setText(String.format(getString(R.string.given_earlier), tag.getName()));
            givenEarlier.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    givenEarlier.setVisibility(View.GONE);
                    earlierDatePicker.setVisibility(View.VISIBLE);
                    set.setVisibility(View.VISIBLE);

                    DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});
                }
            });

            updateDateRanges(givenToday, givenEarlier, set, earlierDatePicker);

            Button cancel = (Button) defaultActions.findViewById(R.id.cancel);
            cancel.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        return dialogView;
    }

    /**
     * This method updates the allowed date ranges in the views
     *
     * @param givenToday        The 'Given done today' button
     * @param givenEarlier      The 'Given earlier' button
     * @param set               The 'Set' Button
     * @param earlierDatePicker Date picker for selecting a previous date for a vaccine
     */
    private void updateDateRanges(Button givenToday, Button givenEarlier, Button set, DatePicker earlierDatePicker) {
        if (tag == null || tag.getDob() == null || tag.getServiceType() == null || issuedServices == null) {
            return;
        }


        DateTime today = ServiceSchedule.standardiseDateTime(DateTime.now());
        DateTime minDate = null;
        DateTime maxDate = null;

        minDate = ServiceSchedule.standardiseDateTime(updateMinVaccineDate(minDate));
        maxDate = ServiceSchedule.standardiseDateTime(updateMaxVaccineDate(maxDate));

        if (maxDate.getMillis() >= minDate.getMillis()) {
            givenToday.setTextColor(getActivity().getResources().getColor(R.color.white));
            givenToday.setBackground(getActivity().getResources().getDrawable(R.drawable.vaccination_today_bg));
            givenEarlier.setBackground(getActivity().getResources().getDrawable(R.drawable.vaccination_earlier_bg));
            if (today.getMillis() >= minDate.getMillis()
                    && today.getMillis() <= maxDate.getMillis()) {
                givenToday.setClickable(true);
                givenToday.setVisibility(View.VISIBLE);

                givenEarlier.setVisibility(View.VISIBLE);
                earlierDatePicker.setVisibility(View.GONE);
                set.setVisibility(View.GONE);
            } else {
                givenToday.setClickable(false);
                givenToday.setVisibility(View.GONE);

                givenEarlier.setVisibility(View.GONE);
                earlierDatePicker.setVisibility(View.VISIBLE);
                set.setVisibility(View.VISIBLE);

                DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});
            }

            earlierDatePicker.setMinDate(minDate.getMillis());
            earlierDatePicker.setMaxDate(maxDate.getMillis());
        } else {
            givenToday.setClickable(false);
            givenToday.setTextColor(getActivity().getResources().getColor(R.color.client_list_grey));
            givenToday.setBackground(getActivity().getResources().getDrawable(R.drawable.vaccination_today_bg_disabled));
            givenEarlier.setClickable(false);
            givenEarlier.setBackground(getActivity().getResources().getDrawable(R.drawable.vaccination_earlier_bg_disabled));
            Toast.makeText(getActivity(), R.string.problem_applying_vaccine_constraints, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method updates the allowed date ranges in the views
     *
     * @param datePicker Date picker for selecting a previous date for a vaccine
     */
    private void updateDateRanges(DatePicker datePicker, Button set) {
        if (tag == null || tag.getDob() == null || tag.getServiceType() == null || issuedServices == null) {
            return;
        }

        DateTime minDate = null;
        DateTime maxDate = null;

        minDate = ServiceSchedule.standardiseDateTime(updateMinVaccineDate(minDate));
        maxDate = ServiceSchedule.standardiseDateTime(updateMaxVaccineDate(maxDate));

        if (maxDate.getMillis() >= minDate.getMillis()) {
            set.setVisibility(View.VISIBLE);
            datePicker.setMinDate(minDate.getMillis());
            datePicker.setMaxDate(maxDate.getMillis());
        } else {
            set.setVisibility(View.INVISIBLE);
            Toast.makeText(getActivity(), R.string.problem_applying_vaccine_constraints, Toast.LENGTH_LONG).show();
        }
    }

    private DateTime updateMinVaccineDate(DateTime minDate) {
        DateTime dueDate = getMinVaccineDate();
        if (dueDate == null
                || dueDate.getMillis() < tag.getDob().getMillis()) {
            dueDate = tag.getDob();
        }

        if (minDate == null) {
            minDate = dueDate;
        } else if (dueDate.getMillis() > minDate.getMillis()) {
            minDate = dueDate;
        }

        return minDate;
    }

    private DateTime updateMaxVaccineDate(DateTime maxDate) {
        DateTime expiryDate = getMaxVaccineDate();
        if (expiryDate == null
                || expiryDate.getMillis() > DateTime.now().getMillis()) {
            expiryDate = DateTime.now();
        }

        if (maxDate == null) {
            maxDate = expiryDate;
        } else if (expiryDate.getMillis() < maxDate.getMillis()) {
            maxDate = expiryDate;
        }

        return maxDate;
    }

    private DateTime getMinVaccineDate() {
        return VaccinatorUtils.getServiceDueDate(tag.getServiceType(), tag.getDob(), issuedServices);
    }

    private DateTime getMaxVaccineDate() {
        return VaccinatorUtils.getServiceExpiryDate(tag.getServiceType(), tag.getDob());
    }

    public void setIssuedServices(List<ServiceRecord> issuedServices) {
        this.issuedServices = issuedServices;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ServiceActionListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceActionListener");
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

}

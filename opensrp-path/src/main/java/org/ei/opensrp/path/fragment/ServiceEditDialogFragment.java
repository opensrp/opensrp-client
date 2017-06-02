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
import android.widget.CheckBox;
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
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import util.ImageUtils;
import util.VaccinatorUtils;

@SuppressLint("ValidFragment")
public class ServiceEditDialogFragment extends DialogFragment {
    private final ServiceWrapper tag;
    private final View viewGroup;
    private ServiceActionListener listener;
    public static final String DIALOG_TAG = "ServiceEditDialogFragment";

    private List<ServiceRecord> issuedServices;

    private ServiceEditDialogFragment(List<ServiceRecord> issuedServices, ServiceWrapper tag, View viewGroup) {
        this.issuedServices = issuedServices;
        this.tag = tag;
        this.viewGroup = viewGroup;
    }

    public static ServiceEditDialogFragment newInstance(
            List<ServiceRecord> issuedServices,
            ServiceWrapper tag, View viewGroup) {
        return new ServiceEditDialogFragment(issuedServices, tag, viewGroup);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if (tag == null) {
            return null;
        }

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.vaccination_edit_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tag.getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tag.getPatientNumber());
        TextView service_date = (TextView) dialogView.findViewById(R.id.service_date);
        service_date.setText("Service date: " + tag.getUpdatedVaccineDateAsString() + "");
        final LinearLayout vaccinationNameLayout = (LinearLayout) dialogView.findViewById(R.id.vaccination_name_layout);

        View vaccinationName = inflater.inflate(R.layout.vaccination_name_edit_dialog, null);
        TextView vaccineView = (TextView) vaccinationName.findViewById(R.id.vaccine);

        vaccineView.setText(tag.getName());
        vaccinationNameLayout.addView(vaccinationName);


        if (tag.getId() != null) {
            ImageView mImageView = (ImageView) dialogView.findViewById(R.id.child_profilepic);
            if (tag.getId() != null) {//image already in local storage most likey ):
                //set profile image by passing the client id.If the image doesn't exist in the image repository then download and save locally
                mImageView.setTag(org.ei.opensrp.R.id.entity_id, tag.getId());
                DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(tag.getId(), OpenSRPImageLoader.getStaticImageListener((ImageView) mImageView, ImageUtils.profileImageResourceByGender(tag.getGender()), ImageUtils.profileImageResourceByGender(tag.getGender())));
            }
        }

        final DatePicker earlierDatePicker = (DatePicker) dialogView.findViewById(R.id.earlier_date_picker);

        String color = tag.getColor();
        Button status = (Button) dialogView.findViewById(R.id.status);
        if (status != null)

        {
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
                                       calendar.set(Calendar.YEAR, year);
                                       calendar.set(Calendar.MONTH, month);
                                       calendar.set(Calendar.DAY_OF_MONTH, day);
//
                                       DateTime dateTime = new DateTime(calendar.getTime());
                                       tag.setUpdatedVaccineDate(dateTime, true);


                                       listener.onGiveEarlier(tag, viewGroup);

                                   }
                               }


        );

        final Button vaccinateToday = (Button) dialogView.findViewById(R.id.vaccinate_today);
        vaccinateToday.setText(vaccinateToday.getText().toString().replace("vaccination", "service"));


        vaccinateToday.setOnClickListener(new Button.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  vaccinateToday.setVisibility(View.GONE);
                                                  earlierDatePicker.setVisibility(View.VISIBLE);
                                                  set.setVisibility(View.VISIBLE);

                                                  DatePickerUtils.themeDatePicker(earlierDatePicker, new char[]{'d', 'm', 'y'});


                                              }
                                          }

        );

        final Button vaccinateEarlier = (Button) dialogView.findViewById(R.id.vaccinate_earlier);
        vaccinateEarlier.setText(vaccinateEarlier.getText().toString().replace("vaccination", "service"));
        vaccinateEarlier.setOnClickListener(new Button.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dismiss();

                                                    listener.onUndoService(tag, viewGroup);

                                                }
                                            }

        );

        updateDateRanges(earlierDatePicker, set);

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


        return dialogView;
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
            set.setVisibility(View.INVISIBLE);
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

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

import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.listener.VaccinationActionListener;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import util.ImageUtils;

@SuppressLint("ValidFragment")
public class VaccinationEditDialogFragment extends DialogFragment {
    private final Context context;
    private final ArrayList<VaccineWrapper> tags;
    private final View viewGroup;
    private VaccinationActionListener listener;
    public static final String DIALOG_TAG = "VaccinationEditDialogFragment";

    private VaccinationEditDialogFragment(Context context,
                                          List<VaccineWrapper> tags, View viewGroup) {
        this.context = context;
        this.tags = new ArrayList<>(tags);
        this.viewGroup = viewGroup;
    }

    public static VaccinationEditDialogFragment newInstance(
            Context context,
            List<VaccineWrapper> tags, View viewGroup) {
        return new VaccinationEditDialogFragment(context, tags, viewGroup);
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
                    tags.get(0).setUpdatedVaccineDate(dateTime, true);
                } else
                    for (int i = 0; i < vaccinationNameLayout.getChildCount(); i++) {
                        View chilView = vaccinationNameLayout.getChildAt(i);
                        CheckBox selectChild = (CheckBox) chilView.findViewById(R.id.select);
                        if (selectChild.isChecked()) {
                            TextView childVaccineView = (TextView) chilView.findViewById(R.id.vaccine);
                            String checkedName = childVaccineView.getText().toString();
                            VaccineWrapper tag = searchWrapperByName(checkedName);
                            if (tag != null) {
                                tag.setUpdatedVaccineDate(dateTime, false);
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


        return dialogView;
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

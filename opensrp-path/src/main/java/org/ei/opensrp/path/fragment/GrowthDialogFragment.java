package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.utils.DatePickerUtils;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineSchedule;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.domain.ZScore;
import org.ei.opensrp.path.listener.VaccinationActionListener;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;
import util.DateUtils;
import util.ImageUtils;
import util.Utils;

@SuppressLint("ValidFragment")
public class GrowthDialogFragment extends DialogFragment {
    private CommonPersonObjectClient personDetails;
    private List<Weight> weights;
    public static final String DIALOG_TAG = "VaccinationDialogFragment";
    public static final String WRAPPER_TAG = "tag";
    private static final HashMap<Integer, Integer> Z_SCORE_COLORS;

    static {
        Z_SCORE_COLORS = new HashMap<>();
        Z_SCORE_COLORS.put(-3, R.color.z_score_3);
        Z_SCORE_COLORS.put(-2, R.color.z_score_2);
        Z_SCORE_COLORS.put(0, R.color.z_score_0);
        Z_SCORE_COLORS.put(2, R.color.z_score_2);
        Z_SCORE_COLORS.put(3, R.color.z_score_3);
    }

    public static GrowthDialogFragment newInstance(CommonPersonObjectClient personDetails,
                                                   List<Weight> weights) {

        GrowthDialogFragment vaccinationDialogFragment = new GrowthDialogFragment();
        vaccinationDialogFragment.setPersonDetails(personDetails);
        vaccinationDialogFragment.setWeights(weights);

        return vaccinationDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    public void setWeights(List<Weight> weights) {
        this.weights = weights;
    }

    public void setPersonDetails(CommonPersonObjectClient personDetails) {
        this.personDetails = personDetails;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        String firstName = Utils.getValue(personDetails.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(personDetails.getColumnmaps(), "last_name", true);
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.growth_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.child_name);
        nameView.setText(Utils.getName(firstName, lastName));

        String personId = Utils.getValue(personDetails.getColumnmaps(), "zeir_id", false);
        ;
        TextView numberView = (TextView) dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(personId)) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), personId));
        } else {
            numberView.setText("");
        }

        String formattedAge = "";
        String dobString = Utils.getValue(personDetails.getColumnmaps(), "dob", false);
        if (!TextUtils.isEmpty(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            Date dob = dateTime.toDate();
            long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();

            if (timeDiff >= 0) {
                formattedAge = DateUtils.getDuration(timeDiff);
            }
        }

        TextView ageView = (TextView) dialogView.findViewById(R.id.child_age);
        if (StringUtils.isNotBlank(formattedAge)) {
            ageView.setText(String.format("%s: %s", getString(R.string.age), formattedAge));
        } else {
            ageView.setText("");
        }

        TextView pmtctStatus = (TextView) dialogView.findViewById(R.id.pmtct_status);
        String pmtctStatusString = Utils.getValue(personDetails.getColumnmaps(), "pmtct_status", true);
        if (!TextUtils.isEmpty(pmtctStatusString)) {
            pmtctStatus.setText(pmtctStatusString);
        } else {
            pmtctStatus.setText("");
        }

        refreshGrowthChart(dialogView);

        return dialogView;
    }

    private void refreshGrowthChart(ViewGroup parent) {
        Gender gender = Gender.UNKNOWN;
        String genderString = Utils.getValue(personDetails, "gender", false);
        if (genderString != null && genderString.toLowerCase().equals("female")) {
            gender = Gender.FEMALE;
        } else if (genderString != null && genderString.toLowerCase().equals("male")) {
            gender = Gender.MALE;
        }

        String dobString = Utils.getValue(personDetails.getColumnmaps(), "dob", false);
        Date dob = null;
        if (StringUtils.isNotBlank(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            dob = dateTime.toDate();
        }

        Calendar minWeighingDate = getMinWeighingDate(dob);
        if (gender != Gender.UNKNOWN && dob != null && minWeighingDate != null) {
            LineChartView growthChart = (LineChartView) parent.findViewById(R.id.growth_chart);
            int minAge = ZScore.getAgeInMonths(dob, minWeighingDate.getTime());
            int maxAge = minAge + 12;
            List<Line> lines = new ArrayList<>();
            for (int z = -3; z <= 3; z++) {
                if (z != 1 && z != -1) {
                    Line curLine = getZScoreLine(gender, minAge, maxAge, z,
                            getActivity().getResources().getColor(Z_SCORE_COLORS.get(z)));
                    if (z == -3) {
                        curLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    }
                    lines.add(curLine);
                }
            }

            lines.add(getTodayLine(gender, dob, minAge, maxAge));
            lines.add(getPersonZScoreLine(gender, dob));

            List<AxisValue> bottomAxisValues = new ArrayList<>();
            for (int i = minAge; i <= maxAge; i++) {
                bottomAxisValues.add(new AxisValue(minAge));
            }

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis bottomAxis = new Axis(bottomAxisValues);
            bottomAxis.setHasLines(true);
            bottomAxis.setHasTiltedLabels(true);
            bottomAxis.setAutoGenerated(true);
            bottomAxis.setName(getString(R.string.months));
            data.setAxisXBottom(bottomAxis);

            Axis leftAxis = new Axis();
            leftAxis.setHasLines(true);
            leftAxis.setHasTiltedLabels(true);
            leftAxis.setAutoGenerated(true);
            leftAxis.setName(getString(R.string.kg));
            data.setAxisYLeft(leftAxis);

            Axis topAxis = new Axis();
            topAxis.setHasTiltedLabels(false);
            topAxis.setAutoGenerated(false);
            data.setAxisXTop(topAxis);

            Axis rightAxis = new Axis();
            rightAxis.setHasTiltedLabels(false);
            rightAxis.setAutoGenerated(false);
            data.setAxisYRight(rightAxis);

            growthChart.setLineChartData(data);
        }
    }

    private Line getTodayLine(Gender gender, Date dob, int minAge, int maxAge) {
        int personsAgeInMonthsToday = ZScore.getAgeInMonths(dob, Calendar.getInstance().getTime());
        double maxY = getMaxY(maxAge, gender);
        double minY = getMinY(minAge, gender);

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) minY));
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) maxY));

        Line todayLine = new Line(values);
        todayLine.setColor(getResources().getColor(R.color.growth_today_color));
        todayLine.setHasPoints(false);
        todayLine.setHasLabels(false);
        todayLine.setStrokeWidth(3);

        return todayLine;
    }

    private double getMaxY(int maxAge, Gender gender) {
        return ZScore.reverse(gender, maxAge, 3d);
    }

    private double getMinY(int minAge, Gender gender) {
        return ZScore.reverse(gender, minAge, -3d);
    }

    private Line getPersonZScoreLine(Gender gender, Date dob) {
        Calendar minWeighingDate = getMinWeighingDate(dob);
        Calendar maxWeighingDate = getMaxWeighingDate(dob);

        List<PointValue> values = new ArrayList<>();
        if (minWeighingDate != null && maxWeighingDate != null
                && minWeighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis()) {
            for (Weight curWeight : weights) {
                if (curWeight.getDate() != null) {
                    Calendar weighingDate = Calendar.getInstance();
                    weighingDate.setTime(curWeight.getDate());
                    standardiseCalendarDate(weighingDate);

                    if (weighingDate.getTimeInMillis() >= minWeighingDate.getTimeInMillis()
                            && weighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis()) {
                        double x = ZScore.getAgeInMonths(dob, weighingDate.getTime());
                        double y = curWeight.getKg();
                        values.add(new PointValue((float) x, (float) y));
                    }
                }
            }
        }

        Line line = new Line(values);
        line.setColor(getResources().getColor(android.R.color.black));
        line.setStrokeWidth(3);
        line.setHasPoints(true);
        line.setHasLabels(false);
        return line;
    }

    private Calendar getMinWeighingDate(Date dob) {
        Calendar minCalendar = null;
        if (dob != null) {
            Calendar dobCalendar = Calendar.getInstance();
            dobCalendar.setTime(dob);
            standardiseCalendarDate(dobCalendar);

            Calendar minGraphTime = Calendar.getInstance();
            minGraphTime.add(Calendar.MONTH, 6);

            if (ZScore.getAgeInMonths(dob, minGraphTime.getTime()) > ZScore.MAX_REPRESENTED_AGE) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dob);
                cal.add(Calendar.MONTH, 60);
                minGraphTime = cal;
            }

            minGraphTime.add(Calendar.MONTH, -12);
            standardiseCalendarDate(minGraphTime);

            for (Weight curWeight : weights) {
                if (curWeight.getDate() != null) {
                    Calendar curWeightCal = Calendar.getInstance();
                    curWeightCal.setTime(curWeight.getDate());
                    standardiseCalendarDate(curWeightCal);

                    if (curWeightCal.getTimeInMillis() >= dobCalendar.getTimeInMillis()
                            && curWeightCal.getTimeInMillis() >= minGraphTime.getTimeInMillis()) {
                        if (minCalendar == null
                                || curWeightCal.getTimeInMillis() < minCalendar.getTimeInMillis()) {
                            minCalendar = curWeightCal;
                        }
                    }
                }
            }

            if (minCalendar == null) {
                minCalendar = minGraphTime;
            }
        }

        return minCalendar;
    }

    private Calendar getMaxWeighingDate(Date dob) {
        Calendar maxGraphTime = Calendar.getInstance();
        standardiseCalendarDate(maxGraphTime);

        return maxGraphTime;
    }

    private Line getZScoreLine(Gender gender, int startAgeInMonths, int endAgeInMonths, double z, int color) {
        List<PointValue> values = new ArrayList<>();
        while (startAgeInMonths <= endAgeInMonths) {
            Double weight = ZScore.reverse(gender, startAgeInMonths, z);

            if (weight != null) {
                values.add(new PointValue((float) startAgeInMonths, (float) weight.doubleValue()));
            }

            startAgeInMonths++;
        }

        Line line = new Line(values);
        line.setColor(color);
        line.setHasPoints(false);
        line.setHasLabels(true);
        line.setStrokeWidth(2);
        return line;
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

                window.setLayout((int) (width * 0.9), FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
            }
        });
    }

    private static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

}

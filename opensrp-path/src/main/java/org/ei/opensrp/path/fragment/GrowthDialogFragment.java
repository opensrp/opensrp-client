package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.graphics.DashPathEffect;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Weight;
import org.ei.opensrp.path.R;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.domain.ZScore;
import org.ei.opensrp.util.OpenSRPImageLoader;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    private static final String TAG = GrowthDialogFragment.class.getName();
    private CommonPersonObjectClient personDetails;
    private List<Weight> weights;
    public static final String DIALOG_TAG = "VaccinationDialogFragment";
    public static final String WRAPPER_TAG = "tag";

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
        sortWeights();
    }

    public void setPersonDetails(CommonPersonObjectClient personDetails) {
        this.personDetails = personDetails;
    }

    private void sortWeights() {
        HashMap<Long, Weight> weightHashMap = new HashMap<>();
        for (Weight curWeight : weights) {
            if (curWeight.getDate() != null) {
                Calendar curCalendar = Calendar.getInstance();
                curCalendar.setTime(curWeight.getDate());
                standardiseCalendarDate(curCalendar);

                if (!weightHashMap.containsKey(curCalendar.getTimeInMillis())) {
                    weightHashMap.put(curCalendar.getTimeInMillis(), curWeight);
                } else if(curWeight.getUpdatedAt() > weightHashMap.get(curCalendar.getTimeInMillis()).getUpdatedAt()) {
                    weightHashMap.put(curCalendar.getTimeInMillis(), curWeight);
                }
            }
        }

        List<Long> keys = new ArrayList<>(weightHashMap.keySet());
        Collections.sort(keys, Collections.<Long>reverseOrder());

        List<Weight> result = new ArrayList<>();
        for (Long curKey : keys) {
            result.add(weightHashMap.get(curKey));
        }

        this.weights = result;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        String firstName = Utils.getValue(personDetails.getColumnmaps(), "first_name", true);
        String lastName = Utils.getValue(personDetails.getColumnmaps(), "last_name", true);
        final ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.growth_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.child_name);
        nameView.setText(Utils.getName(firstName, lastName));

        String personId = Utils.getValue(personDetails.getColumnmaps(), "zeir_id", false);
        TextView numberView = (TextView) dialogView.findViewById(R.id.child_zeir_id);
        if (StringUtils.isNotBlank(personId)) {
            numberView.setText(String.format("%s: %s", getString(R.string.label_zeir), personId));
        } else {
            numberView.setText("");
        }

        String genderString = Utils.getValue(personDetails, "gender", false);
        String baseEntityId = personDetails.entityId();
        ImageView profilePic = (ImageView) dialogView.findViewById(R.id.child_profilepic);
        profilePic.setTag(org.ei.opensrp.R.id.entity_id, baseEntityId);
        DrishtiApplication.getCachedImageLoaderInstance().getImageByClientId(baseEntityId,
                OpenSRPImageLoader.getStaticImageListener(
                        (ImageView) profilePic,
                        ImageUtils.profileImageResourceByGender(genderString),
                        ImageUtils.profileImageResourceByGender(genderString)));

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

        Gender gender = Gender.UNKNOWN;
        if (genderString != null && genderString.toLowerCase().equals("female")) {
            gender = Gender.FEMALE;
        } else if (genderString != null && genderString.toLowerCase().equals("male")) {
            gender = Gender.MALE;
        }

        int genderStringRes = R.string.boys;
        if (gender == Gender.FEMALE) {
            genderStringRes = R.string.girls;
        }

        TextView weightForAge = (TextView) dialogView.findViewById(R.id.weight_for_age);
        weightForAge.setText(String.format(getString(R.string.weight_for_age), getString(genderStringRes).toUpperCase()));

        Date dob = null;
        if (StringUtils.isNotBlank(dobString)) {
            DateTime dateTime = new DateTime(dobString);
            dob = dateTime.toDate();
        }

        Button done = (Button) dialogView.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrowthDialogFragment.this.dismiss();
            }
        });

        final ScrollView weightScrollView = (ScrollView) dialogView.findViewById(R.id.weight_scroll_view);
        ImageButton scrollButton = (ImageButton) dialogView.findViewById(R.id.scroll_button);
        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weightScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        try {
            refreshGrowthChart(dialogView, gender, dob);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        try {
            refreshPreviousWeightsTable(dialogView, gender, dob);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return dialogView;
    }

    private void refreshPreviousWeightsTable(ViewGroup dialogView, Gender gender, Date dob) {
        TableLayout tableLayout = (TableLayout) dialogView.findViewById(R.id.weights_table);
        for (Weight weight : weights) {
            TableRow dividerRow = new TableRow(dialogView.getContext());
            View divider = new View(dialogView.getContext());
            TableRow.LayoutParams params = (TableRow.LayoutParams) divider.getLayoutParams();
            if (params == null) params = new TableRow.LayoutParams();
            params.width = TableRow.LayoutParams.MATCH_PARENT;
            params.height = getResources().getDimensionPixelSize(R.dimen.weight_table_divider_height);
            params.span = 3;
            divider.setLayoutParams(params);
            divider.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
            dividerRow.addView(divider);
            tableLayout.addView(dividerRow);

            TableRow curRow = new TableRow(dialogView.getContext());

            TextView ageTextView = new TextView(dialogView.getContext());
            ageTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            ageTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.weight_table_contents_text_size));
            ageTextView.setText(DateUtils.getDuration(weight.getDate().getTime() - dob.getTime()));
            ageTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            ageTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(ageTextView);

            TextView weightTextView = new TextView(dialogView.getContext());
            weightTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            weightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.weight_table_contents_text_size));
            weightTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            weightTextView.setText(
                    String.format("%s %s", String.valueOf(weight.getKg()), getString(R.string.kg)));
            weightTextView.setTextColor(getResources().getColor(R.color.client_list_grey));
            curRow.addView(weightTextView);

            TextView zScoreTextView = new TextView(dialogView.getContext());
            zScoreTextView.setHeight(getResources().getDimensionPixelSize(R.dimen.table_contents_text_height));
            zScoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.weight_table_contents_text_size));
            zScoreTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            double zScore = ZScore.calculate(gender, dob, weight.getDate(), weight.getKg());
            zScore = ZScore.roundOff(zScore);
            zScoreTextView.setTextColor(getResources().getColor(ZScore.getZScoreColor(zScore)));
            zScoreTextView.setText(String.valueOf(zScore));
            curRow.addView(zScoreTextView);
            tableLayout.addView(curRow);
        }
    }

    private void refreshGrowthChart(ViewGroup parent, Gender gender, Date dob) {
        Calendar minWeighingDate = getMinWeighingDate(dob);
        if (gender != Gender.UNKNOWN && dob != null && minWeighingDate != null) {
            LineChartView growthChart = (LineChartView) parent.findViewById(R.id.growth_chart);
            double minAge = ZScore.getAgeInMonths(dob, minWeighingDate.getTime());
            double maxAge = minAge + 12;
            List<Line> lines = new ArrayList<>();
            for (int z = -3; z <= 3; z++) {
                if (z != 1 && z != -1) {
                    Line curLine = getZScoreLine(gender, minAge, maxAge, z,
                            getActivity().getResources().getColor(ZScore.getZScoreColor(z)));
                    if (z == -3) {
                        curLine.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
                    }
                    lines.add(curLine);
                }
            }

            lines.add(getTodayLine(gender, dob, minAge, maxAge));
            lines.add(getPersonWeightLine(gender, dob));

            List<AxisValue> bottomAxisValues = new ArrayList<>();
            for (int i = (int) Math.round(Math.floor(minAge)); i <= (int) Math.round(Math.ceil(maxAge)); i++) {
                AxisValue curValue = new AxisValue((float) i);
                curValue.setLabel(String.valueOf(i));
                bottomAxisValues.add(curValue);
            }

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis bottomAxis = new Axis(bottomAxisValues);
            bottomAxis.setHasLines(true);
            bottomAxis.setHasTiltedLabels(false);
            bottomAxis.setName(getString(R.string.months));
            data.setAxisXBottom(bottomAxis);

            Axis leftAxis = new Axis();
            leftAxis.setHasLines(true);
            leftAxis.setHasTiltedLabels(false);
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

    private Line getTodayLine(Gender gender, Date dob, double minAge, double maxAge) {
        double personsAgeInMonthsToday = ZScore.getAgeInMonths(dob, Calendar.getInstance().getTime());
        double maxY = getMaxY(dob, maxAge, gender);
        double minY = getMinY(dob, minAge, gender);

        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) minY));
        values.add(new PointValue((float) personsAgeInMonthsToday, (float) maxY));

        Line todayLine = new Line(values);
        todayLine.setColor(getResources().getColor(R.color.growth_today_color));
        todayLine.setHasPoints(false);
        todayLine.setHasLabels(false);
        todayLine.setStrokeWidth(4);

        return todayLine;
    }

    private double getMaxY(Date dob, double maxAge, Gender gender) {
        double maxY = ZScore.reverse(gender, maxAge, 3d);
        Calendar minWeighingDate = getMinWeighingDate(dob);
        Calendar maxWeighingDate = getMaxWeighingDate(dob);

        for (Weight curWeight : weights) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight)) {
                if (curWeight.getKg() > maxY) {
                    maxY = curWeight.getKg();
                }
            }
        }

        return maxY;
    }

    private double getMinY(Date dob, double minAge, Gender gender) {
        double minY = ZScore.reverse(gender, minAge, -3d);
        Calendar minWeighingDate = getMinWeighingDate(dob);
        Calendar maxWeighingDate = getMaxWeighingDate(dob);

        for (Weight curWeight : weights) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight)) {
                if (curWeight.getKg() < minY) {
                    minY = curWeight.getKg();
                }
            }
        }

        return minY;
    }

    private Line getPersonWeightLine(Gender gender, Date dob) {
        Calendar minWeighingDate = getMinWeighingDate(dob);
        Calendar maxWeighingDate = getMaxWeighingDate(dob);

        List<PointValue> values = new ArrayList<>();
        for (Weight curWeight : weights) {
            if (isWeightOkToDisplay(minWeighingDate, maxWeighingDate, curWeight)) {
                Calendar weighingDate = Calendar.getInstance();
                weighingDate.setTime(curWeight.getDate());
                standardiseCalendarDate(weighingDate);
                double x = ZScore.getAgeInMonths(dob, weighingDate.getTime());
                double y = curWeight.getKg();
                values.add(new PointValue((float) x, (float) y));
            }
        }

        Line line = new Line(values);
        line.setColor(getResources().getColor(android.R.color.black));
        line.setStrokeWidth(4);
        line.setHasPoints(true);
        line.setHasLabels(false);
        return line;
    }

    private boolean isWeightOkToDisplay(Calendar minWeighingDate, Calendar maxWeighingDate,
                                        Weight weight) {
        if (minWeighingDate != null && maxWeighingDate != null
                && minWeighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis()
                && weight.getDate() != null) {
            Calendar weighingDate = Calendar.getInstance();
            weighingDate.setTime(weight.getDate());
            standardiseCalendarDate(weighingDate);

            if (weighingDate.getTimeInMillis() >= minWeighingDate.getTimeInMillis()
                    && weighingDate.getTimeInMillis() <= maxWeighingDate.getTimeInMillis()) {
                return true;
            }
        }

        return false;
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
                cal.add(Calendar.MONTH, (int) Math.round(ZScore.MAX_REPRESENTED_AGE));
                minGraphTime = cal;
            }

            minGraphTime.add(Calendar.MONTH, -12);
            standardiseCalendarDate(minGraphTime);

            if (minGraphTime.getTimeInMillis() < dobCalendar.getTimeInMillis()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dobCalendar.getTime());
                minCalendar = cal;
            }

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

    private Line getZScoreLine(Gender gender, double startAgeInMonths, double endAgeInMonths, double z, int color) {
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

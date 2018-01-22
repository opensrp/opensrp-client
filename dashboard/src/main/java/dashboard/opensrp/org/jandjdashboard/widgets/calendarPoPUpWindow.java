package dashboard.opensrp.org.jandjdashboard.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;

/**
 * Created by raihan on 1/17/18.
 */

public class calendarPoPUpWindow extends PopupWindow {
    public static Date todate;
    public static Date fromdate;
    private final CalendarView tocalendarView;
    private final CalendarView fromcalendarView;;
    TextView daily,weekly,monthly,cycle,yearly;
    EditText fromdateselected,todateselected;
    TextView date_in_words_label;
    TextView [] periodSelector ;
    Context mContext;
    View popupView;
    private Button apply;

    public calendarPoPUpWindow(Context context){
        mContext = context;
        popupView = ((Activity)context).getLayoutInflater().inflate(R.layout.advanced_date_picker, null);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(popupView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        tocalendarView = (CalendarView)popupView.findViewById(R.id.tocalendarView);
        fromcalendarView = (CalendarView)popupView.findViewById(R.id.fromcalendarView);
        date_in_words_label = (TextView)popupView.findViewById(R.id.date_in_words_label);
        assignfontTOCalendarMonth(tocalendarView,context);
        assignfontTOCalendarMonth(fromcalendarView,context);
        setOutsideTouchable(true);
        setFocusable(true);
        setPeriodSelector(context);


    }
    public void setPeriodSelector(final Context context){
        daily = (TextView) popupView.findViewById(R.id.date_picker_daily_label);
        weekly = (TextView)popupView.findViewById(R.id.date_picker_weekly_label);
        weekly = (TextView)popupView.findViewById(R.id.date_picker_weekly_label);
        monthly = (TextView)popupView.findViewById(R.id.date_picker_monthly_label);
        cycle = (TextView)popupView.findViewById(R.id.date_picker_cyle_label);
        yearly = (TextView)popupView.findViewById(R.id.date_picker_year_label);
        fromdateselected = (EditText) popupView.findViewById(R.id.from_date_selected);
        todateselected = (EditText) popupView.findViewById(R.id.to_date_selected);
        apply = (Button) popupView.findViewById(R.id.applybutton);

        periodSelector = new TextView[]{daily,weekly,monthly,cycle,yearly};
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                daily.setBackgroundColor(context.getResources().getColor(R.color.mainblue));
                daily.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
                addToDate(1);
                for(int i = 0 ;i < periodSelector.length;i++){
                    if(!periodSelector[i].equals(daily)){
                        periodSelector[i].setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        periodSelector[i].setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                    }
                }
            }
        });
        weekly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weekly.setBackgroundColor(context.getResources().getColor(R.color.mainblue));
                weekly.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
                addToDate(7);
                for(int i = 0 ;i < periodSelector.length;i++){
                    if(!periodSelector[i].equals(weekly)){
                        periodSelector[i].setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        periodSelector[i].setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                    }
                }
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                monthly.setBackgroundColor(context.getResources().getColor(R.color.mainblue));
                monthly.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
                addToDate(30);
                for(int i = 0 ;i < periodSelector.length;i++){
                    if(!periodSelector[i].equals(monthly)){
                        periodSelector[i].setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        periodSelector[i].setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                    }
                }
            }
        });
        cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cycle.setBackgroundColor(context.getResources().getColor(R.color.mainblue));
                cycle.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
                for(int i = 0 ;i < periodSelector.length;i++){
                    if(!periodSelector[i].equals(cycle)){
                        periodSelector[i].setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        periodSelector[i].setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                    }
                }
            }
        });
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yearly.setBackgroundColor(context.getResources().getColor(R.color.mainblue));
                yearly.setTextColor(context.getResources().getColor(R.color.cardview_light_background));
                addToDate(365);
                for(int i = 0 ;i < periodSelector.length;i++){
                    if(!periodSelector[i].equals(yearly)){
                        periodSelector[i].setBackgroundColor(context.getResources().getColor(R.color.cardview_light_background));
                        periodSelector[i].setTextColor(context.getResources().getColor(R.color.secondary_text_default_material_light));
                    }
                }
            }
        });
        daily.performClick();
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd");
                if(mContext instanceof dashboardCategoryListActivity) {
                    ((dashboardCategoryListActivity) mContext).refresh(simpleformat.format(fromdate), simpleformat.format(todate));
                }
                dismiss();
            }
        });
    }

    private void addToDate(int daysToAdd) {
        Date date = new Date(fromcalendarView.getDate()+ TimeUnit.DAYS.toMillis(daysToAdd));
        tocalendarView.setDate(date.getTime());
        todate = new Date(tocalendarView.getDate());
        fromdate = new Date(fromcalendarView.getDate());
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        date_in_words_label.setText(format.format(fromdate)+ " - "+format.format(todate));
        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd");
        fromdateselected.setText(simpleformat.format(fromdate));
        todateselected.setText(simpleformat.format(todate));
    }

    private void assignfontTOCalendarMonth(CalendarView calendarView,Context context) {
        ViewGroup vg = (ViewGroup)calendarView.getChildAt(0);
        View child = vg.getChildAt(0);
        if(child instanceof TextView){
            ((TextView)child).setTextSize(12);
            ((TextView)child).setTextColor(context.getResources().getColor(R.color.focuseddatemonthcolor));
        }
    }
}

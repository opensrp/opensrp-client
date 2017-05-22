package com.example.opensrp_thrivepk.report;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.opensrp_thrivepk.R;

import org.ei.opensrp.Context;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.core.utils.Utils.addToRow;
import static org.ei.opensrp.core.utils.Utils.convertDateFormat;
import static org.ei.opensrp.core.utils.Utils.getDataRow;
import static org.ei.opensrp.core.utils.Utils.getValue;
import static org.ei.opensrp.core.utils.Utils.toDisplayDate;
import static org.ei.opensrp.core.utils.Utils.toSqlDate;

public class VaccineReport extends Activity implements View.OnClickListener{
    private ProgressDialog pd;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private int currentSelection = R.id.woman_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_vaccine_report);

        ((TextView)findViewById(R.id.detail_heading)).setText("Vaccination Summary Report");
        ((TextView)findViewById(R.id.details_id_label)).setText("");
        ((TextView)findViewById(R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        dateFrom().setText(toDisplayDate(DateTime.now().dayOfMonth().withMinimumValue().toDate()));
        dateTo().setText(toDisplayDate(DateTime.now().dayOfMonth().withMaximumValue().toDate()));

        findViewById(R.id.woman_button).setOnClickListener(this);
        findViewById(R.id.child_button).setOnClickListener(this);

        setDateTimeField();

        reloadData(findViewById(currentSelection));

        ImageButton back = (ImageButton)findViewById(org.ei.opensrp.core.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setDateTimeField() {
        Log.v(getClass().getName(), "Setting up date pickers");

        final TextView fromDateEtxt = dateFrom();
        final TextView toDateEtxt = dateTo();

        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(toDisplayDate(newDate.getTime()));
                reloadData(findViewById(currentSelection));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(toDisplayDate(newDate.getTime()));
                reloadData(findViewById(currentSelection));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        Log.v(getClass().getName(), "Setting up date pickers");
    }

    private TextView dateFrom(){
        return ((TextView)findViewById(R.id.datefrom));
    }

    private TextView dateTo(){
        return ((TextView)findViewById(R.id.dateto));
    }

    @Override
    protected void onResume() {
        super.onResume();
        pd = new ProgressDialog(this);
        pd.setMessage("Building Report....");
        pd.setTitle("Wait");
        pd.setIndeterminate(true);
    }

    @Override
    public void onBackPressed() {
        pd.setTitle("Wait");
        pd.setMessage("Going back to home...");

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pd != null && pd.isShowing()) pd.dismiss();
    }

    public void showWomanTable(ArrayList<HashMap<String, String>> data){
        final TableLayout tb = (TableLayout) findViewById(R.id.woman_vaccine_table);
        findViewById(R.id.woman_button).setBackgroundColor(Utils.getColor(this, R.color.dull_blue_light));

        tb.setVisibility(View.VISIBLE);

        while (tb.getChildCount() > 1) {
            tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
        }


        for (Map<String, String> d: data) {

            TableRow tr = getDataRow(this, 1, 1);
            addToRow(this, getValue(d, "preg", true), tr, false, 1);
            addToRow(this, getValue(d, "tt1", false), tr, false, 1);
            addToRow(this, getValue(d, "tt2", false), tr, false, 1);
            addToRow(this, getValue(d, "tt3", false), tr, false, 1);
            addToRow(this, getValue(d, "tt4", false), tr, false, 1);
            addToRow(this, getValue(d, "tt5", false), tr, false, 1);

            tb.addView(tr);
        }
    }

    public void showChildTable(ArrayList<HashMap<String, String>> data){
        final TableLayout tb = (TableLayout) findViewById(R.id.child_vaccine_table);
        findViewById(R.id.child_button).setBackgroundColor(Utils.getColor(this, R.color.dull_blue_light));

        tb.setVisibility(View.VISIBLE);

        while (tb.getChildCount() > 1) {
            tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
        }


        for (Map<String, String> d: data) {

            TableRow tr = getDataRow(this, 1, 1);
            addToRow(this, getValue(d, "vaccine", true), tr, false, 1);
            addToRow(this, getValue(d, "lt1", "0", false), tr, false, 1);
            addToRow(this, getValue(d, "b12", "0", false), tr, false, 1);
            addToRow(this, getValue(d, "gt2", "0", false), tr, false, 1);
            addToRow(this, getValue(d, "total", "0", false), tr, false, 1);

            tb.addView(tr);
        }
    }

    private String getQuery(int viewId){
        if (R.id.woman_button == viewId){
            return getResources().getString(R.string.sql_woman_vaccine_report);
        }
        else if (R.id.child_button == viewId) {
            return getResources().getString(R.string.sql_child_vaccine_report);
        }
        return null;
    }

    private void reloadData(final View view){
        final VaccineReport ac = this;

        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                if(!isFinishing()) {
                    pd.setTitle("Wait");
                    pd.setMessage("Building report...");
                    pd.show();
                }
            }
            @Override
            public void setInvisible() {

                if(!isFinishing()) {
                    pd.dismiss();
                }
            }
        });

        task.doActionInBackground(new BackgroundAction<ArrayList<HashMap<String,String>>>() {
            public ArrayList<HashMap<String, String>> actionToDoInBackgroundThread() {
                Log.v(getClass().getName(), "Loading report query");

                String datefrom = toSqlDate(dateFrom().getText().toString(), true);
                String dateto = toSqlDate(dateTo().getText().toString(), true);

                String sql = getQuery(view.getId()).replace(":datefrom", "'"+datefrom+"'").replace(":dateto", "'"+dateto+"'");

                Log.v(getClass().getName(), sql);

                if (view.getId() == R.id.child_button){
                    RegisterRepository.createTemporaryTable("stock", "age", getResources().getString(R.string.sql_age_table));
                }
                ArrayList<HashMap<String, String>> data = Context.getInstance().commonrepository("stock").rawQuery(sql);

                Log.v(getClass().getName(), "RESULTSET:"+data.toString());
                return data;
            }

            public void postExecuteInUIThread(ArrayList<HashMap<String, String>> result) {
                if (view.getId() == R.id.woman_button) {
                    findViewById(R.id.child_vaccine_table).setVisibility(View.GONE);
                    findViewById(R.id.child_button).setBackgroundColor(Utils.getColor(ac, R.color.silver));

                    showWomanTable(result);
                } else {
                    findViewById(R.id.woman_vaccine_table).setVisibility(View.GONE);
                    findViewById(R.id.woman_button).setBackgroundColor(Utils.getColor(ac, R.color.silver));

                    showChildTable(result);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.v(getClass().getName(), "View clicked "+view.getId());

        if (view.getId() == R.id.woman_button ||
                view.getId() == R.id.child_button){
            currentSelection = view.getId();
            reloadData(view);
        }
        else if(view.getId() == R.id.datefrom) {
            fromDatePickerDialog.show();
        }
        else if(view.getId() == R.id.dateto) {
            toDatePickerDialog.show();
        }
    }
}

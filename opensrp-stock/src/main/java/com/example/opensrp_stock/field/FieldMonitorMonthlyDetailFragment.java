package com.example.opensrp_stock.field;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opensrp_stock.R;
import com.example.opensrp_stock.field.util.VaccinatorUtils;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.util.IntegerUtil;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.calculateEndingBalance;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.calculateStartingBalance;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.calculateWasted;
import static org.ei.opensrp.core.utils.Utils.*;

public class FieldMonitorMonthlyDetailFragment extends DetailFragment {
    public ProgressDialog pd;

    @Override
    protected int layoutResId() {
        return R.layout.field_detail_monthly_activity;
    }

    @Override
    protected String pageTitle() {
        return "Report Detail (Monthly)";
    }

    @Override
    protected String titleBarId() {
        return "Today: "+convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true);
    }

    @Override
    protected Integer profilePicContainerId() { return null; }

    @Override
    protected Integer defaultProfilePicResId() { return null; }

    @Override
    protected String bindType() {
        return "stock";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }

    @Override
    public void onResumeFragmentView() {
        super.onResumeFragmentView();

        if(client != null) client.getDetails().put("reportType", FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth.name());
        ((RadioGroup)currentView.findViewById(R.id.radioReportType)).check(R.id.radioMonthly);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(pd != null && pd.isShowing()){
            pd.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Building Report....");
        pd.setTitle("Wait");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        ((RadioGroup)currentView.findViewById(R.id.radioReportType)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (client == null){
                    return;
                }

                if (checkedId == R.id.radioMonthly){
                    client.getDetails().put("reportType", FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth.name());
                    generateView();
                }
                else if (checkedId == R.id.radioDaily){
                    client.getDetails().put("reportType", FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay.name());
                    generateView();
                }
            }
        });
    }

    @Override
    protected void generateView() {

        HashMap provider = VaccinatorUtils.providerDetails();

        final String reportType = client.getDetails().get("reportType");
        Log.v(getClass().getName(), "REPORT TYPE::" + reportType);

        currentView.findViewById(R.id.statuts_bar_container).setVisibility(View.GONE);

        TableLayout dt = (TableLayout) currentView.findViewById(R.id.field_detail_info_table1);
        dt.removeAllViews();

        Log.i("ANM", "DETIALS ANM :" + Context.getInstance().anmController().get());

        addRow(getActivity(), dt, "Center", getValue(provider, "provider_location_id", true), Utils.Size.MEDIUM);
        addRow(getActivity(), dt, "UC", getValue(provider, "provider_uc", true), Utils.Size.MEDIUM);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.field_detail_info_table2);
        dt2.removeAllViews();

        addRow(getActivity(), dt2, "Monthly Target", getValue(client.getColumnmaps(), "Target_assigned_for_vaccination_at_each_month", false), Utils.Size.MEDIUM);
        addRow(getActivity(), dt2, "Yearly Target", getValue(client.getDetails(), "Target_assigned_for_vaccination_for_the_year", false), Utils.Size.MEDIUM);

        String date_entered = client.getColumnmaps().get("date");

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(date_entered);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((TextView) currentView.findViewById(R.id.reporting_period)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));
        ((TextView) currentView.findViewById(R.id.reporting_period_d)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));

        final Date finalDate = date;

        Boolean isSyncInProgress = Context.getInstance().allSharedPreferences().fetchIsSyncInProgress();

        if (isSyncInProgress != null && isSyncInProgress){
            Toast.makeText(getActivity(), "Forms Sync is in progress at the moment... Wait until sync has been completed...", Toast.LENGTH_LONG).show();
        }

        new ReportLoader(new Listener() {
            @Override
            public void onEvent(Object result) {
                if (getActivity().isFinishing()){
                    return;
                }

                if (reportType == null || reportType.equalsIgnoreCase(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth.name())) {
                    currentView.findViewById(R.id.stock_vaccine_table).setVisibility(View.VISIBLE);
                    currentView.findViewById(R.id.stock_vaccine_table_daily).setVisibility(View.GONE);

                    showMonthlyReport(finalDate, (List<CommonPersonObject>) result);
                } else {
                    currentView.findViewById(R.id.stock_vaccine_table).setVisibility(View.GONE);
                    currentView.findViewById(R.id.stock_vaccine_table_daily).setVisibility(View.VISIBLE);

                    showDailyReport(finalDate, (List<CommonPersonObject>) result);
                }
            }
        }, finalDate, reportType).execute();
    }

    private void addMonthlyRow(TableLayout table, String item, String startingBalance, String used, String inhandCurrentMonth, String wasted, String endingBalance){
        TableRow tr = getDataRow(getActivity(), 1, 1);
        tr.setBackgroundColor(Color.LTGRAY);
        addToRow(getActivity(), Html.fromHtml("<small>"+item+"</small>"), tr, false, 3);
        addToRow(getActivity(), startingBalance, tr, false, 4);
        addToRow(getActivity(), used, tr, false, 2);
        addToRow(getActivity(), inhandCurrentMonth, tr, false, 2);
        addToRow(getActivity(), wasted, tr, false, 4);
        addToRow(getActivity(), endingBalance, tr, false, 4);
        table.addView(tr);
    }

    private void showMonthlyReport(Date date, List<CommonPersonObject> nextMonthRpt){
        final TableLayout tb = (TableLayout) currentView.findViewById(R.id.stock_vaccine_table);
        while (tb.getChildCount() > 3) {
            tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
        }

        int bcgBalanceInHand = Integer.parseInt(getValue(client.getColumnmaps(), "bcg_balance_in_hand", "0", false));
        int bcgReceived = Integer.parseInt(getValue(client.getColumnmaps(), "bcg_received", "0", false));

        int opv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "opv_balance_in_hand", "0", false));
        int opv_received = Integer.parseInt(getValue(client.getColumnmaps(), "opv_received", "0", false));

        int ipv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "ipv_balance_in_hand", "0", false));
        int ipv_received = Integer.parseInt(getValue(client.getColumnmaps(), "ipv_received", "0", false));

        int pcv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "pcv_balance_in_hand", "0", false));
        int pcv_received = Integer.parseInt(getValue(client.getColumnmaps(), "pcv_received", "0", false));

        int penta_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "penta_balance_in_hand", "0", false));
        int penta_received = Integer.parseInt(getValue(client.getColumnmaps(), "penta_received", "0", false));

        int measles_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "measles_balance_in_hand", "0", false));
        int measles_received = Integer.parseInt(getValue(client.getColumnmaps(), "measles_received", "0", false));

        int tt_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "tt_balance_in_hand", "0", false));
        int tt_received = Integer.parseInt(getValue(client.getColumnmaps(), "tt_received", "0", false));

        //#TODO get Total balance,wasted and received from total variables instead of calculating here.
        int totalBalanceInHand = bcgBalanceInHand + opv_balance_in_hand + ipv_balance_in_hand +
                pcv_balance_in_hand + penta_balance_in_hand + measles_balance_in_hand + tt_balance_in_hand;

        int totalReceived = bcgReceived + opv_received + ipv_received + pcv_received + penta_received +
                measles_received + tt_received ;

        Map<String, String> m = client.getColumnmaps();

        int totalUsed = addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"));

        addMonthlyRow(tb, "BCG", calculateStartingBalance(bcgBalanceInHand, bcgReceived), m.get("bcg"), bcgBalanceInHand+"",
                calculateWasted(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0), nextMonthRpt, "bcg"),
                calculateEndingBalance(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0)));

        addMonthlyRow(tb, "OPV", calculateStartingBalance(opv_balance_in_hand, opv_received),
                addAsInts(true, m, "opv0","opv1","opv2","opv3")+"", opv_balance_in_hand+"",
                calculateWasted(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3"), nextMonthRpt, "opv"),
                calculateEndingBalance(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3")));

        addMonthlyRow(tb, "IPV", calculateStartingBalance(ipv_balance_in_hand, ipv_received), m.get("ipv"), ipv_balance_in_hand+"",
                calculateWasted(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv"), nextMonthRpt, "ipv"),
                calculateEndingBalance(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv")));

        addMonthlyRow(tb, "PCV", calculateStartingBalance(pcv_balance_in_hand, pcv_received),
                addAsInts(true, m, "pcv1","pcv2","pcv3")+"", pcv_balance_in_hand+"",
                calculateWasted(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3"), nextMonthRpt, "pcv"),
                calculateEndingBalance(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3")));

        addMonthlyRow(tb, "PENTA", calculateStartingBalance(penta_balance_in_hand, penta_received),
                addAsInts(true, m, "penta1","penta2","penta3")+"", penta_balance_in_hand+"",
                calculateWasted(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3"), nextMonthRpt, "penta"),
                calculateEndingBalance(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3")));

        addMonthlyRow(tb, "MEASLES", calculateStartingBalance(measles_balance_in_hand, measles_received),
                addAsInts(true, m, "measles1","measles2")+"", measles_balance_in_hand+"",
                calculateWasted(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2"), nextMonthRpt, "measles"),
                calculateEndingBalance(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2")));

        addMonthlyRow(tb, "TETNUS", calculateStartingBalance(tt_balance_in_hand, tt_received),
                addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")+"", tt_balance_in_hand+"",
                calculateWasted(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5"), nextMonthRpt, "tt"),
                calculateEndingBalance(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")));

        addMonthlyRow(tb, "DILUTANTS",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "dilutants_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "dilutants_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                getValue(client.getColumnmaps(), "dilutants_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "SYRINGES",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "syringes_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "syringes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                getValue(client.getColumnmaps(), "syringes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "SAFETY BOXES",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "safety_boxes_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "safety_boxes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                getValue(client.getColumnmaps(), "safety_boxes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A", "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "TOTAL", calculateStartingBalance(totalBalanceInHand, totalReceived), totalUsed+"", totalBalanceInHand+"",
                calculateWasted(totalBalanceInHand, totalReceived, totalUsed, nextMonthRpt, "bcg", "opv", "ipv", "pcv", "penta", "measles", "tt"),
                calculateEndingBalance(totalBalanceInHand, totalReceived, totalUsed));
    }

    private void showDailyReport(Date date, List<CommonPersonObject> result){
        TableLayout tbd = (TableLayout) currentView.findViewById(R.id.stock_vaccine_table_daily);
        while (tbd.getChildCount() > 2) {
            tbd.removeView(tbd.getChildAt(tbd.getChildCount() - 1));
        }

        for (CommonPersonObject o: result){
            Map<String, String> cm = o.getColumnmaps();

            int used = addAsInts(true, cm.get("bcg"),
                    cm.get("opv0"), cm.get("opv1"), cm.get("opv2"), cm.get("opv3"),
                    cm.get("ipv"),
                    cm.get("penta1"), cm.get("penta2"), cm.get("penta3"),
                    cm.get("measles1"), cm.get("measles2"),
                    cm.get("pcv1"), cm.get("pcv2"), cm.get("pcv3"),
                    cm.get("tt1"), cm.get("tt2"), cm.get("tt3"), cm.get("tt4"), cm.get("tt5"));

            DateTime sdt = new DateTime(cm.get("dom"));

            TableRow tr = getDataRow(getActivity(), 1, 1);
            tr.setBackgroundColor(Color.LTGRAY);

            addToRow(getActivity(), sdt.toString("dd"), tr, true, 1);
            addToRow(getActivity(), cm.get("bcg"), tr, true, 2);
            addToRow(getActivity(), addAsInts(true, cm.get("opv0"), cm.get("opv1"), cm.get("opv2"), cm.get("opv3"))+"", tr, true, 2);
            addToRow(getActivity(), cm.get("ipv"), tr, true, 2);
            addToRow(getActivity(), addAsInts(true, cm.get("pcv1"), cm.get("pcv2"), cm.get("pcv3"))+"", tr, true, 2);
            addToRow(getActivity(), addAsInts(true, cm.get("penta1"), cm.get("penta2"), cm.get("penta3"))+"", tr, true, 2);
            addToRow(getActivity(), addAsInts(true, cm.get("measles1"), cm.get("measles2"))+"", tr, true, 2);
            addToRow(getActivity(), addAsInts(true, cm.get("tt1"), cm.get("tt2"), cm.get("tt3"), cm.get("tt4"), cm.get("tt5"))+"",
                    tr, true, 2);
            addToRow(getActivity(), used+"", tr, true, 2);

            if (sdt.getDayOfWeek() == DateTimeConstants.SUNDAY){
                for (int i = 0; i < tr.getChildCount(); i++) {
                    tr.getChildAt(i).setBackgroundColor(getColor(getActivity(), R.color.client_list_header_dark_grey));
                }
            }

            tbd.addView(tr);
        }
    }

    private String getQuery(Date finalDate, String reportType){
        String sql = reportType==null||reportType.equalsIgnoreCase(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth.name())?
                ("SELECT * FROM stock WHERE report='monthly' AND date LIKE '" + new DateTime(finalDate.getTime()).plusMonths(1).toString("yyyy-MM") + "%' "):
                (getString(R.string.sql_daily_report).replace(":reportingDate", " '"+new DateTime(finalDate.getTime()).toString("yyyy-MM") + "%' "));

        return sql;
    }

    private class ReportLoader extends AsyncTask<Void, Void, List<CommonPersonObject>> {
        private Listener listener;
        private Date finalDate;
        private String reportType;

        ReportLoader(Listener listener, Date finalDate, String reportType){
            this.listener = listener;
            this.finalDate = finalDate;
            this.reportType = reportType;
        }

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected List<CommonPersonObject> doInBackground(Void... voids) {
            Log.v(getClass().getName(), "Loading report query");

            String sql = getQuery(finalDate, reportType);

            Log.v(getClass().getName(), sql);

            return RegisterRepository.rawQueryData("stock", sql);
        }

        @Override
        protected void onPostExecute(List<CommonPersonObject> data) {
            Log.v(getClass().getName(), "Loaded report data. Now returing result");

            if(pd != null && pd.isShowing()){
                pd.dismiss();
            }
            listener.onEvent(data);
        }
    }
}

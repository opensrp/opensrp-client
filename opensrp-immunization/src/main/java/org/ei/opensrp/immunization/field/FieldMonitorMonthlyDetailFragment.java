package org.ei.opensrp.immunization.field;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.core.db.repository.RegisterRepository;
import org.ei.opensrp.core.template.DetailFragment;
import org.ei.opensrp.immunization.R;
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

import static org.ei.opensrp.core.utils.Utils.*;
import static org.ei.opensrp.util.VaccinatorUtils.calculateEndingBalance;
import static org.ei.opensrp.util.VaccinatorUtils.calculateStartingBalance;
import static org.ei.opensrp.util.VaccinatorUtils.calculateWasted;
import static org.ei.opensrp.util.VaccinatorUtils.getWasted;
import static org.ei.opensrp.util.VaccinatorUtils.providerDetails;

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
    public void onResume() {
        super.onResume();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Building Report....");
        pd.setTitle("Wait");
        pd.setIndeterminate(true);

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

        HashMap provider =  providerDetails();

        final String reportType = client.getDetails().get("reportType");
        Log.v(getClass().getName(), "REPORT TYPE::"+reportType);

        currentView.findViewById(R.id.statuts_bar_container).setVisibility(View.GONE);

        TableLayout dt = (TableLayout) currentView.findViewById(R.id.field_detail_info_table1);
        dt.removeAllViews();

        Log.i("ANM", "DETIALS ANM :"+Context.getInstance().anmController().get());

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

        ((TextView)currentView.findViewById(R.id.reporting_period)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));
        ((TextView)currentView.findViewById(R.id.reporting_period_d)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));

        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                pd.show();
            }
            @Override
            public void setInvisible() {
                pd.dismiss();
            }
        });

        final Date finalDate = date;
        task.doActionInBackground(new BackgroundAction<List<CommonPersonObject>>() {
            public List<CommonPersonObject> actionToDoInBackgroundThread() {
                Log.v(getClass().getName(), "Loading report query");

                String sql = getQuery(finalDate, reportType);

                Log.v(getClass().getName(), sql);

                List<CommonPersonObject> dl = RegisterRepository.rawQueryData("stock", sql);

                Log.v(getClass().getName(), "RESULTSET:"+dl.toString());
                return dl;
            }

            public void postExecuteInUIThread(List<CommonPersonObject> result) {
                if (reportType == null || reportType.equalsIgnoreCase(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth.name())) {
                    currentView.findViewById(R.id.stock_vaccine_table).setVisibility(View.VISIBLE);
                    currentView.findViewById(R.id.stock_vaccine_table_daily).setVisibility(View.GONE);

                    showMonthlyReport(finalDate, result);
                } else {
                    currentView.findViewById(R.id.stock_vaccine_table).setVisibility(View.GONE);
                    currentView.findViewById(R.id.stock_vaccine_table_daily).setVisibility(View.VISIBLE);

                    showDailyReport(finalDate, result);
                }
            }
        });
    }

    private void addMonthlyRow(TableLayout table, String item, String startingBalance, String used, String wasted, String endingBalance){
        TableRow tr = getDataRow(getActivity(), 1, 1);
        tr.setBackgroundColor(Color.LTGRAY);
        addToRow(getActivity(), item, tr, false, 2);
        addToRow(getActivity(), startingBalance, tr, false, 2);
        addToRow(getActivity(), used, tr, false, 1);
        addToRow(getActivity(), wasted, tr, false, 2);
        addToRow(getActivity(), endingBalance, tr, false, 2);
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

        addMonthlyRow(tb, "BCG", calculateStartingBalance(bcgBalanceInHand, bcgReceived), m.get("bcg"),
                calculateWasted(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0), nextMonthRpt, "bcg"),
                calculateEndingBalance(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0)));

        addMonthlyRow(tb, "OPV", calculateStartingBalance(opv_balance_in_hand, opv_received),
                addAsInts(true, m, "opv0","opv1","opv2","opv3")+"",
                calculateWasted(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3"), nextMonthRpt, "opv"),
                calculateEndingBalance(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3")));

        addMonthlyRow(tb, "IPV", calculateStartingBalance(ipv_balance_in_hand, ipv_received), m.get("ipv"),
                calculateWasted(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv"), nextMonthRpt, "ipv"),
                calculateEndingBalance(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv")));

        addMonthlyRow(tb, "PCV", calculateStartingBalance(pcv_balance_in_hand, pcv_received),
                addAsInts(true, m, "pcv1","pcv2","pcv3")+"",
                calculateWasted(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3"), nextMonthRpt, "pcv"),
                calculateEndingBalance(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3")));

        addMonthlyRow(tb, "PENTAVALENT", calculateStartingBalance(penta_balance_in_hand, penta_received),
                addAsInts(true, m, "penta1","penta2","penta3")+"",
                calculateWasted(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3"), nextMonthRpt, "penta"),
                calculateEndingBalance(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3")));

        addMonthlyRow(tb, "MEASLES", calculateStartingBalance(measles_balance_in_hand, measles_received),
                addAsInts(true, m, "measles1","measles2")+"",
                calculateWasted(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2"), nextMonthRpt, "measles"),
                calculateEndingBalance(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2")));

        addMonthlyRow(tb, "TETNUS", calculateStartingBalance(tt_balance_in_hand, tt_received),
                addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")+"",
                calculateWasted(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5"), nextMonthRpt, "tt"),
                calculateEndingBalance(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")));

        addMonthlyRow(tb, "DILUTANTS",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "dilutants_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "dilutants_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "SYRINGES",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "syringes_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "syringes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A",
                "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "SAFETY BOXES",
                "<font color='gray'>"+getValue(client.getColumnmaps(), "safety_boxes_received", "0" , false)
                        +"+"+getValue(client.getColumnmaps(), "safety_boxes_balance_in_hand", "0" , false),
                "<font color='gray'>"+"N/A", "<font color='gray'>"+"N/A", "<font color='gray'>"+"N/A");

        addMonthlyRow(tb, "TOTAL", calculateStartingBalance(totalBalanceInHand, totalReceived), totalUsed+"",
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
                ("SELECT t.*, SUBSTR(t.date,1,7) || '-' || dts.dom AS dom, " +
                "       (select count(id) c from pkwoman where date(tt1) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt1, " +
                "       (select count(id) c from pkwoman where date(tt2) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt2, " +
                "       (select count(id) c from pkwoman where date(tt3) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt3, " +
                "       (select count(id) c from pkwoman where date(tt4) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt4, " +
                "       (select count(id) c from pkwoman where date(tt5) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt5, " +
                "       (select count(id) c from pkchild where date(bcg) = SUBSTR(t.date,1,7) || '-' || dts.dom) bcg, " +
                "       (select count(id) c from pkchild where date(opv0) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv0, " +
                "       (select count(id) c from pkchild where date(opv1) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv1, " +
                "       (select count(id) c from pkchild where date(opv2) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv2, " +
                "       (select count(id) c from pkchild where date(opv3) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv3, " +
                "       (select count(id) c from pkchild where date(ipv) = SUBSTR(t.date,1,7) || '-' || dts.dom) ipv, " +
                "       (select count(id) c from pkchild where date(pcv1) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv1, " +
                "       (select count(id) c from pkchild where date(pcv2) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv2, " +
                "       (select count(id) c from pkchild where date(pcv3) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv3, " +
                "       (select count(id) c from pkchild where date(measles1) = SUBSTR(t.date,1,7) || '-' || dts.dom) measles1, " +
                "       (select count(id) c from pkchild where date(measles2) = SUBSTR(t.date,1,7) || '-' || dts.dom) measles2, " +
                "       (select count(id) c from pkchild where date(penta1) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta1, " +
                "       (select count(id) c from pkchild where date(penta2) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta2, " +
                "       (select count(id) c from pkchild where date(penta3) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta3 " +
                "    FROM stock t " +
                "    JOIN (SELECT '01' dom UNION SELECT '02' UNION SELECT '03' UNION SELECT '04' " +
                " UNION SELECT '05' UNION SELECT '06' UNION SELECT '07' UNION SELECT '08' " +
                " UNION SELECT '09' UNION SELECT '10' UNION SELECT '11' UNION SELECT '12' " +
                " UNION SELECT '13' UNION SELECT '14' UNION SELECT '15' UNION SELECT '16' " +
                " UNION SELECT '17' UNION SELECT '18' UNION SELECT '19' UNION SELECT '20' " +
                " UNION SELECT '21' UNION SELECT '22' UNION SELECT '23' UNION SELECT '24' " +
                " UNION SELECT '25' UNION SELECT '26' UNION SELECT '27' UNION SELECT '28' " +
                " UNION SELECT '29' UNION SELECT '30' UNION SELECT '31') dts ON dts.dom <= strftime('%d', date(t.date,'start of month','+1 month','-1 day'))" +
                " WHERE t.report='monthly' AND t.date LIKE '" + new DateTime(finalDate.getTime()).toString("yyyy-MM") + "%' ");

        return sql;
    }
}

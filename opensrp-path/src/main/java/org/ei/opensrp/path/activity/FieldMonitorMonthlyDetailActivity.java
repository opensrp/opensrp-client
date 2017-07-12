package org.ei.opensrp.path.activity;

import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static util.Utils.addToRow;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.VaccinatorUtils.getTotalUsed;
import static util.VaccinatorUtils.getWasted;
import static util.VaccinatorUtils.providerDetails;

public class FieldMonitorMonthlyDetailActivity extends DetailActivity {
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
        return "";
    }

    @Override
    protected Class onBackActivity() {
        return FieldMonitorSmartRegisterActivity.class;
    }

    @Override
    protected Integer profilePicContainerId() { return null; }

    @Override
    protected Integer defaultProfilePicResId(CommonPersonObjectClient client) { return null; }

    @Override
    protected String bindType() {
        return "stock";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }



    @Override
    protected void generateView(CommonPersonObjectClient client) {
        HashMap provider =  providerDetails();

        TableLayout dt = (TableLayout) findViewById(R.id.field_detail_info_table1);

        Log.i("ANM", "DETIALS ANM :"+Context.getInstance().anmController().get());

        TableRow tr = getDataRow(this, "Vaccinator ID", getValue(provider, "provider_id", false), null);
        dt.addView(tr);
        tr = getDataRow(this, "Vaccinator Name", getValue(provider, "provider_name", true), null);
        dt.addView(tr);
        tr = getDataRow(this, "Center", getValue(provider, "provider_location_id", true), null);
        dt.addView(tr);
        tr = getDataRow(this, "UC", getValue(provider, "provider_uc", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.field_detail_info_table2);

        TableRow tr2 = getDataRow(this, "Monthly Target", getValue(client.getColumnmaps(), "Target_assigned_for_vaccination_at_each_month", false), null);
        dt2.addView(tr2);
        tr2 = getDataRow(this, "Yearly Target", getValue(client.getColumnmaps(), "Target_assigned_for_vaccination_for_the_year", false), null);
        dt2.addView(tr2);

        String date_entered = client.getColumnmaps().get("date");

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(date_entered);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String startDate = new DateTime(date.getTime()).withDayOfMonth(1).toString("yyyy-MM-dd");
        String endDate = new DateTime(date.getTime()).withDayOfMonth(1).plusMonths(1).minusDays(1).toString("yyyy-MM-dd");

        String childTable = "ec_child";
        String womanTable = "ec_mother";
        String wastedDataReport = "daily";

        ((TextView)findViewById(R.id.reporting_period)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));

        TableLayout tb = (TableLayout) findViewById(R.id.stock_vaccine_table);
        tr = getDataRow(this);
        addToRow(this, "BCG", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "bcg_received", "0", false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "bcg")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "bcg_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "bcg_balance_in_hand", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "OPV", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "opv_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "opv0","opv1","opv2","opv3")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "opv_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "opv_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "IPV", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "ipv_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "ipv")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "ipv_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "ipv_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "PCV", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "pcv_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "pcv1","pcv2","pcv3")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "pcv_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "pcv_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "PENTAVALENT", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "penta_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "penta1","penta2","penta3")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "penta_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "penta_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "MEASLES", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "measles_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "measles1","measles2")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "measles_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "measles_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "TETNUS", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "tt_received", "0" , false), tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, womanTable, "tt1","tt2","tt3","tt4","tt5")+"", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "tt_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "tt_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "DILUTANTS", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "dilutants_received", "0" , false), tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "dilutants_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "dilutants_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "SYRINGES", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "syringes_received", "0" , false), tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "syringes_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "syringes_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "SAFETY BOXES", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "safety_boxes_received", "0" , false), tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getWasted(startDate, endDate, wastedDataReport, "safety_boxes_wasted")+"", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "safety_boxes_balance_in_hand", "0" , false), tr, true);
        tb.addView(tr);
    }
}

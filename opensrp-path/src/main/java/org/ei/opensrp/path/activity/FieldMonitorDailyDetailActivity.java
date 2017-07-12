package org.ei.opensrp.path.activity;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.path.R;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import static util.Utils.addToRow;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.VaccinatorUtils.getTotalUsed;
import static util.VaccinatorUtils.providerDetails;

public class FieldMonitorDailyDetailActivity extends DetailActivity {
    @Override
    protected int layoutResId() {
        return R.layout.field_detail_daily_activity;
    }

    @Override
    protected String pageTitle() {
        return "Report Detail (Daily)";
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
    protected Integer defaultProfilePicResId(CommonPersonObjectClient client){ return null; };

    @Override
    protected String bindType() {
        return "stock";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }

    @Override
    protected void  generateView(CommonPersonObjectClient client) {
        HashMap<String, String> provider =  providerDetails();

        TableLayout dt = (TableLayout) findViewById(R.id.field_detail_info_table1);

        TableRow tr = getDataRow(this, "Vaccinator ID", getValue(provider, "provider_id", false), null);
        dt.addView(tr);
        tr = getDataRow(this, "Vaccinator Name", getValue(provider, "provider_name", true), null);
        dt.addView(tr);
        tr = getDataRow(this, "Center", getValue(provider, "provider_location_id", true), null);
        dt.addView(tr);
        tr = getDataRow(this, "UC", getValue(provider, "provider_uc", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.field_detail_info_table2);

        String date_entered = client.getColumnmaps().get("date");

        DateTime date = DateTime.now().minusYears(200);
        try {
            date = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(date_entered).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<CommonPersonObject> cl = Context.getInstance().allCommonsRepositoryobjects("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE date LIKE '" + date.toString("yyyy-MM") + "%' AND report='monthly' ", null, "stock");
        CommonPersonObject mr = cl.size() == 0 ? null : cl.get(0);
        
        TableRow tr2 = getDataRow(this, "Monthly Target", mr==null?"No monthly report submitted":getValue(mr.getDetails(), "Target_assigned_for_vaccination_at_each_month", false), null);
        dt2.addView(tr2);
        tr2 = getDataRow(this, "Yearly Target", mr==null?"No monthly report submitted":getValue(mr.getDetails(), "Target_assigned_for_vaccination_for_the_year", false), null);
        dt2.addView(tr2);
        
        String startDate = date.toString("yyyy-MM-dd");
        String endDate = date.toString("yyyy-MM-dd");

        String childTable = "ec_child";
        String womanTable = "ec_mother";

        ((TextView)findViewById(R.id.reporting_period)).setText(date.toString("dd/MM/yyyy"));

        TableLayout tb = (TableLayout) findViewById(R.id.stock_vaccine_table);
        tr = getDataRow(this);
        addToRow(this, "BCG", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "bcg") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "bcg_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "OPV", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "opv0", "opv1", "opv2", "opv3") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "opv_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "IPV", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "ipv") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "ipv_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "PCV", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "pcv1", "pcv2", "pcv3") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "pcv_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "PENTAVALENT", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "penta1", "penta2", "penta3") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "penta_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "MEASLES", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, childTable, "measles1", "measles2") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "measles_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "TETNUS", tr, true);
        addToRow(this, getTotalUsed(startDate, endDate, womanTable, "tt1", "tt2", "tt3", "tt4", "tt5") + "", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "tt_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "DILUTANTS", tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "dilutants_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "SYRINGES", tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "syringes_wasted", "0", false), tr, true);
        tb.addView(tr);

        tr = getDataRow(this);
        addToRow(this, "SAFETY BOXES", tr, true);
        addToRow(this, "N/A", tr, true);
        addToRow(this, getValue(client.getColumnmaps(), "safety_boxes_wasted", "0" , false), tr, true);
        tb.addView(tr);
    }
}

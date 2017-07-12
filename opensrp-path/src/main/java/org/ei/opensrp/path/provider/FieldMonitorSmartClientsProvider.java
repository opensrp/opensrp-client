package org.ei.opensrp.path.provider;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.path.R;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static util.Utils.addAsInts;
import static util.Utils.fillValue;
import static util.Utils.getValue;

public class FieldMonitorSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener onClickListener;
    AlertService alertService;

    private ByMonthByDay byMonthlyAndByDaily;

    public enum ByMonthByDay {ByMonth, ByDay}

    public FieldMonitorSmartClientsProvider(Context context, OnClickListener onClickListener,
              AlertService alertService, ByMonthByDay byMonthlyAndByDaily) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.byMonthlyAndByDaily = byMonthlyAndByDaily;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int getTotalWasted(String startDate, String endDate, String type){
        int totalWasted = 0;
        for (HashMap<String, String> v: getWasted(startDate, endDate, type))
        {
            for (String k: v.keySet()) {
                totalWasted += Integer.parseInt(v.get(k) == null?"0":v.get(k));
            }
        }
        return totalWasted;
    }

    private ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type){
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    private ArrayList<HashMap<String, String>> getWastedByVaccine(String startDate, String endDate, String type){
        String sqlWasted = "select " +
                " sum(ifnull(bcg_wasted, 0)) bcg, sum(ifnull(opv_wasted, 0)) opv," +
                " sum(ifnull(ipv_wasted, 0)) ipv, sum(ifnull(penta_wasted, 0)) penta," +
                " sum(ifnull(measles_wasted, 0)) measles, sum(ifnull(pcv_wasted, 0)) pcv," +
                " sum(ifnull(tt_wasted, 0)) tt, sum(ifnull(total_wasted, 0)) total" +
                " from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    private int countTotalUsed(Map<String, String> vul){
        int totalUsed = 0;

        for (String k: vul.keySet()) {
            totalUsed += Integer.parseInt(vul.get(k) == null?"0":vul.get(k));
        }

        return totalUsed;
    }

    private ArrayList<HashMap<String, String>> getUsed(String startDate, String endDate){
        String sql = "select " +
                "(select count(*) c from ec_mother where tt1 between  '" + startDate + "' and '" + endDate + "') tt1," +
                "(select count(*) c from ec_mother where tt2 between '" + startDate + "' and '" + endDate + "') tt2," +
                "(select count(*) c from ec_mother where tt3 between '" + startDate + "' and '" + endDate + "') tt3," +
                "(select count(*) c from ec_mother where tt4 between '" + startDate + "' and '" + endDate + "') tt4," +
                "(select count(*) c from ec_mother where tt5 between '" + startDate + "' and '" + endDate + "') tt5,"+
                "(select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "') bcg," +
                "(select count(*) c from ec_child where opv0 between '" + startDate + "' and '" + endDate + "') opv0," +
                "(select count(*) c from ec_child where opv1 between '" + startDate + "' and '" + endDate + "') opv1," +
                "(select count(*) c from ec_child where opv2 between '" + startDate + "' and '" + endDate + "') opv2," +
                "(select count(*) c from ec_child where opv3 between '" + startDate + "' and '" + endDate + "') opv3, " +
                "(select count(*) c from ec_child where ipv between '" + startDate + "' and '" + endDate + "') ipv, " +
                "(select count(*) c from ec_child where pcv1 between '" + startDate + "' and '" + endDate + "') pcv1," +
                "(select count(*) c from ec_child where pcv2 between '" + startDate + "' and '" + endDate + "') pcv2," +
                "(select count(*) c from ec_child where pcv3 between '" + startDate + "' and '" + endDate + "') pcv3, " +
                "(select count(*) c from ec_child where measles1 between '" + startDate + "' and '" + endDate + "') measles1, " +
                "(select count(*) c from ec_child where measles2 between '" + startDate + "' and '" + endDate + "') measles2," +
                "(select count(*) c from ec_child where penta1 between '" + startDate + "' and '" + endDate + "') penta1," +
                "(select count(*) c from ec_child where penta2 between '" + startDate + "' and '" + endDate + "') penta2," +
                "(select count(*) c from ec_child where penta3 between '" + startDate + "' and '" + endDate + "') penta3  " +
                "from ec_child limit 1 ;";

        return org.ei.opensrp.Context.getInstance().commonrepository("ec_child").rawQuery(sql);
    }

    //todo refactor above method
    private Map<String, String> getUsedByVaccine(String startDate, String endDate){
        Map<String, String> m = new HashMap<>();
        ArrayList<HashMap<String, String>> al = getUsed(startDate, endDate);
        for (HashMap<String, String> s : al){
            m.putAll(s);
        }
        return m;
    }

    @Override
    public void getView(SmartRegisterClient client, View parentView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        String dateentered = pc.getColumnmaps().get("date");

        DateTime date = null;
        try {
            date = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateentered).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(byMonthlyAndByDaily.equals(ByMonthByDay.ByMonth)){
            Map<String,String> m = pc.getColumnmaps();

            String startDate = date.withDayOfMonth(1).toString("yyyy-MM-dd");
            String endDate = date.withDayOfMonth(1).plusMonths(1).minusDays(1).toString("yyyy-MM-dd");

            int bcgBalanceInHand = Integer.parseInt(getValue(pc.getColumnmaps(), "bcg_balance_in_hand", "0", false));
            int bcgReceived = Integer.parseInt(getValue(pc.getColumnmaps(), "bcg_received", "0", false));

            int opv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "opv_balance_in_hand", "0", false));
            int opv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "opv_received", "0", false));

            int ipv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "ipv_balance_in_hand", "0", false));
            int ipv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "ipv_received", "0", false));

            int pcv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "pcv_balance_in_hand", "0", false));
            int pcv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "pcv_received", "0", false));

            int penta_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "penta_balance_in_hand", "0", false));
            int penta_received = Integer.parseInt(getValue(pc.getColumnmaps(), "penta_received", "0", false));

            int measles_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "measles_balance_in_hand", "0", false));
            int measles_received = Integer.parseInt(getValue(pc.getColumnmaps(), "measles_received", "0", false));

            int tt_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "tt_balance_in_hand", "0", false));
            int tt_received = Integer.parseInt(getValue(pc.getColumnmaps(), "tt_received", "0", false));

            //#TODO get Total balance,wasted and received from total variables instead of calculating here.
            int balanceInHand = bcgBalanceInHand + opv_balance_in_hand + ipv_balance_in_hand +
                    pcv_balance_in_hand + penta_balance_in_hand + measles_balance_in_hand + tt_balance_in_hand;

            int received = bcgReceived + opv_received + ipv_received + pcv_received + penta_received +
                    measles_received + tt_received ;

            fillValue((TextView) parentView.findViewById(R.id.month), date.toString("MMMM (yyyy)"));
            fillValue((TextView) parentView.findViewById(R.id.monthly_target), pc.getColumnmaps().get("Target_assigned_for_vaccination_at_each_month"));
            fillValue((TextView) parentView.findViewById(R.id.total_received), received+"");
            fillValue((TextView) parentView.findViewById(R.id.total_used), addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                    m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                    m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                    m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"))+"");
            fillValue((TextView) parentView.findViewById(R.id.total_wasted), m.get("total_monthly_wasted")+"");
            fillValue((TextView) parentView.findViewById(R.id.total_balance_in_hand), balanceInHand+"");
        }
        else if(byMonthlyAndByDaily.equals(ByMonthByDay.ByDay)){
            Map<String, String> m = pc.getColumnmaps();

            fillValue((TextView) parentView.findViewById(R.id.day), date.toString("dd-MM-yyyy"));

            fillValue((TextView) parentView.findViewById(R.id.bcg_used), m.get("bcg"));

            fillValue((TextView) parentView.findViewById(R.id.opv_used), addAsInts(true, m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"))+"");

            fillValue((TextView) parentView.findViewById(R.id.ipv_used), addAsInts(true, m.get("ipv"))+"");

            fillValue((TextView) parentView.findViewById(R.id.penta_used), addAsInts(true, m.get("penta1"), m.get("penta2"), m.get("penta3"))+"");

            fillValue((TextView) parentView.findViewById(R.id.measles_used), addAsInts(true, m.get("measles1"), m.get("measles2"))+"");

            fillValue((TextView) parentView.findViewById(R.id.pcv_used), addAsInts(true, m.get("pcv1"), m.get("pcv2"), m.get("pcv3"))+"");

            fillValue((TextView) parentView.findViewById(R.id.tt_used), addAsInts(true, m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"))+"");

            fillValue((TextView) parentView.findViewById(R.id.total_used),
                    addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                            m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                            m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                            m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5")) + "");
            fillValue((TextView) parentView.findViewById(R.id.total_wasted), m.get("total_wasted"));
        }

        parentView.setTag(R.id.client_details_tag, client);
        parentView.setOnClickListener(onClickListener);

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
               FilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        Log.i("", "NOTHING TO DO IN CLIENT PROVIDER WHEN SERVICE CHANGES");
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        if(byMonthlyAndByDaily.equals(ByMonthByDay.ByDay)){
            return inflater().inflate(R.layout.smart_register_field_daily_client, null);
        }
        return inflater().inflate(R.layout.smart_register_field_monthly_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}

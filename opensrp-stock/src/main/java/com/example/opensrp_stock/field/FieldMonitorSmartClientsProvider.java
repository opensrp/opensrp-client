package com.example.opensrp_stock.field;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.opensrp_stock.R;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.core.template.RegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.opensrp_stock.field.util.VaccinatorUtils.calculateEndingBalance;
import static com.example.opensrp_stock.field.util.VaccinatorUtils.calculateStartingBalance;
import static org.ei.opensrp.core.utils.Utils.addAsInts;
import static org.ei.opensrp.core.utils.Utils.fillValue;
import static org.ei.opensrp.core.utils.Utils.getValue;

public class FieldMonitorSmartClientsProvider implements RegisterClientsProvider <CommonPersonObjectClient>{

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
                "(select count(*) c from pkwoman where tt1 between  '" + startDate + "' and '" + endDate + "') tt1," +
                "(select count(*) c from pkwoman where tt2 between '" + startDate + "' and '" + endDate + "') tt2," +
                "(select count(*) c from pkwoman where tt3 between '" + startDate + "' and '" + endDate + "') tt3," +
                "(select count(*) c from pkwoman where tt4 between '" + startDate + "' and '" + endDate + "') tt4," +
                "(select count(*) c from pkwoman where tt5 between '" + startDate + "' and '" + endDate + "') tt5,"+
                "(select count(*) c from pkchild where bcg between '" + startDate + "' and '" + endDate + "') bcg," +
                "(select count(*) c from pkchild where opv0 between '" + startDate + "' and '" + endDate + "') opv0," +
                "(select count(*) c from pkchild where opv1 between '" + startDate + "' and '" + endDate + "') opv1," +
                "(select count(*) c from pkchild where opv2 between '" + startDate + "' and '" + endDate + "') opv2," +
                "(select count(*) c from pkchild where opv3 between '" + startDate + "' and '" + endDate + "') opv3, " +
                "(select count(*) c from pkchild where ipv between '" + startDate + "' and '" + endDate + "') ipv, " +
                "(select count(*) c from pkchild where pcv1 between '" + startDate + "' and '" + endDate + "') pcv1," +
                "(select count(*) c from pkchild where pcv2 between '" + startDate + "' and '" + endDate + "') pcv2," +
                "(select count(*) c from pkchild where pcv3 between '" + startDate + "' and '" + endDate + "') pcv3, " +
                "(select count(*) c from pkchild where measles1 between '" + startDate + "' and '" + endDate + "') measles1, " +
                "(select count(*) c from pkchild where measles2 between '" + startDate + "' and '" + endDate + "') measles2," +
                "(select count(*) c from pkchild where penta1 between '" + startDate + "' and '" + endDate + "') penta1," +
                "(select count(*) c from pkchild where penta2 between '" + startDate + "' and '" + endDate + "') penta2," +
                "(select count(*) c from pkchild where penta3 between '" + startDate + "' and '" + endDate + "') penta3  " +
                "from pkchild limit 1 ;";

        return org.ei.opensrp.Context.getInstance().commonrepository("pkchild").rawQuery(sql);
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
    public View getView(CommonPersonObjectClient client, View parentView, ViewGroup viewGroup) {
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

            int totalUsed = addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                    m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                    m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                    m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"));

            List<CommonPersonObject> nextMonthRpt = org.ei.opensrp.Context.getInstance().commonrepository("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE report='monthly' AND date LIKE '" + date.plusMonths(1).toString("yyyy-MM") + "%' ", null, "stock");

          /*  if (nextMonthRpt.isEmpty()) {
                fillValue((TextView) parentView.findViewById(R.id.total_wasted), "N/A");
            }
            else {
                fillValue((TextView) parentView.findViewById(R.id.total_wasted), calculateWasted(balanceInHand, received, totalUsed, nextMonthRpt, "bcg", "opv", "ipv", "pcv", "penta", "measles", "tt"));
            }*/
            fillValue((TextView) parentView.findViewById(R.id.month), date.toString("MMM, yy"));
            fillValue((TextView) parentView.findViewById(R.id.monthly_target), pc.getColumnmaps().get("Target_assigned_for_vaccination_at_each_month"));
            fillValue((TextView) parentView.findViewById(R.id.total_received), received+"<font color='silver'>/"+pc.getColumnmaps().get("total_received")+"</font>");
          //  fillValue((TextView) parentView.findViewById(R.id.total_used), totalUsed+"");
            fillValue((TextView) parentView.findViewById(R.id.total_balance_in_hand), balanceInHand+"<font color='silver'>/"+pc.getColumnmaps().get("total_balanceInHand")+"</font>");
            fillValue((TextView) parentView.findViewById(R.id.starting_balance_in_hand), calculateStartingBalance(balanceInHand, received));
            fillValue((TextView) parentView.findViewById(R.id.current_balance_in_hand), calculateEndingBalance(balanceInHand, received, totalUsed));
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

            int totalUsed = addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                    m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                    m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                    m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"));
            fillValue((TextView) parentView.findViewById(R.id.total_used),
                    totalUsed + "");
            fillValue((TextView) parentView.findViewById(R.id.total_inhand), totalUsed +""+m.get("total_inhand"));
        }

        parentView.setTag(client);
        parentView.setOnClickListener(onClickListener);
        return parentView;
    }


    @Override
    public List<CommonPersonObjectClient> getClients() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(org.ei.opensrp.core.template.ServiceModeOption serviceModeOption) {

    }

    @Override
    public View inflateLayoutForAdapter() {
        if(byMonthlyAndByDaily.equals(ByMonthByDay.ByDay)){
            return inflater().inflate(R.layout.smart_register_field_daily_client, null);
        }
        return inflater().inflate(R.layout.smart_register_field_monthly_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}

package org.ei.opensrp.vaccinator.field;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 12-Nov-15.
 */
public class FieldMonitorSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    //private CommonRepository commonRepository=new CommonRepository();
    private org.ei.opensrp.Context context1;
    ByMonthANDByDAILY byMonthlyAndByDaily;

    public enum ByMonthANDByDAILY {ByMonth, ByDaily;}


    protected CommonPersonObjectController controller;


    public FieldMonitorSmartClientsProvider(Context context,
                                            OnClickListener onClickListener, CommonPersonObjectController controller,
                                            AlertService alertService, ByMonthANDByDAILY byMonthlyAndByDaily, org.ei.opensrp.Context context1) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.context1 = context1;
        this.alertService = alertService;
        this.byMonthlyAndByDaily = byMonthlyAndByDaily;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (ByMonthANDByDAILY.ByMonth.equals(byMonthlyAndByDaily)) {

            if (parentView == null) {
                parentView = (ViewGroup) inflater().inflate(R.layout.smart_register_field_client, null);
                viewHolder = new ViewHolder();
                viewHolder.daymonthTextView = (TextView) parentView.findViewById(R.id.field_daymonth);
                viewHolder.daymonthLayout = (LinearLayout) parentView.findViewById(R.id.field_daymonth_layout);

                viewHolder.monthTargetTextView = (TextView) parentView.findViewById(R.id.field_month_target);
                viewHolder.monthTargetLayout = (LinearLayout) parentView.findViewById(R.id.field_month_target_layout);

                viewHolder.monthreceivedTextView = (TextView) parentView.findViewById(R.id.field_vaccine_recieved);
                viewHolder.monthreceivedLayout = (LinearLayout) parentView.findViewById(R.id.field_vaccine_recieved_layout);


                viewHolder.monthusedTextView = (TextView) parentView.findViewById(R.id.field_vaccine_used);
                viewHolder.monthusedLayout = (LinearLayout) parentView.findViewById(R.id.field_vaccine_used_layout);

                viewHolder.monthwastedTextView = (TextView) parentView.findViewById(R.id.field_vaccine_wasted);
                viewHolder.monthwastedLayout = (LinearLayout) parentView.findViewById(R.id.field_vaccine_wasted_layout);
                parentView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) parentView.getTag();

            }


            viewHolder.daymonthLayout.setOnClickListener(onClickListener);

            String date_entered = pc.getDetails().get("date_formatted");

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = format.parse(date_entered);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date != null) {
                // GregorianCalendar calendar = new GregorianCalendar();
                //  calendar.setTime(date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                Formatter fmt = new Formatter();
                //Calendar cal = Calendar.getInstance();
                //fmt = new Formatter();

                String startDate = year + "-" + month + "-" + "01";
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                String endDate = year + "-" + month + "-" + cal.get(Calendar.DAY_OF_MONTH);

                String sqlWoman = "select (" +
                        "select count(*) c from ec_woman where tt1 between  '" + startDate + "' and '" + endDate + "') tt1," +
                        "(select count(*) c from ec_woman where tt2 between '" + startDate + "' and '" + endDate + "') tt2," +
                        "(select count(*) c from ec_woman where tt3 between '" + startDate + "' and '" + endDate + "') tt3," +
                        "(select count(*) c from ec_woman where tt4 between '" + startDate + "' and '" + endDate + "') tt4," +
                        "(select count(*) c from ec_woman where tt5 between '" + startDate + "' and '" + endDate + "') tt5 " +
                        "from ec_woman limit 1; ";


                String sqlChild = "select (" +
                        "select count(*) c from ec_child where bcg between '" + startDate + "' and '" + endDate + "') bcg," +
                        "(select count(*) c from ec_child where opv_0 between '" + startDate + "' and '" + endDate + "') opv_0," +
                        "(select count(*) c from ec_child where opv_1 between '" + startDate + "' and '" + endDate + "') opv_1," +
                        "(select count(*) c from ec_child where opv_2 between '" + startDate + "' and '" + endDate + "') opv_2," +
                        "(select count(*) c from ec_child where opv_3 between '" + startDate + "' and '" + endDate + "') opv_3, " +
                        "(select count(*) c from ec_child where pcv_1 between '" + startDate + "' and '" + endDate + "') pcv_1," +
                        "(select count(*) c from ec_child where pcv_2 between '" + startDate + "' and '" + endDate + "') pcv_2," +
                        "(select count(*) c from ec_child where pcv_3 between '" + startDate + "' and '" + endDate + "') pcv_3, " +
                        "(select count(*) c from ec_child where measles_1 between '" + startDate + "' and '" + endDate + "') measles_1, " +
                        "(select count(*) c from ec_child where measles_2 between '" + startDate + "' and '" + endDate + "') measles_2," +
                        "(select count(*) c from ec_child where pentavalent_1 between '" + startDate + "' and '" + endDate + "') pentavalent_1," +
                        "(select count(*) c from ec_child where pentavalent_2 between '" + startDate + "' and '" + endDate + "') pentavalent_2," +
                        "(select count(*) c from ec_child where pentavalent_3 between '" + startDate + "' and '" + endDate + "') pentavalent_3 " +
                        "from ec_child limit 1 ;";

                String sqlWasted = "select sum (total_wasted)as total_wasted from field where date between '" + startDate + "' and '" + endDate + "'";
                List<CommonPersonObject> ttVaccinesUsed = null;
                List<CommonPersonObject> childVaccinesUsed = null;
                List<CommonPersonObject> wastedVaccines = null;

                int totalTTUsed = 0;
                int totalChildVaccinesUsed = 0;
                int totalWasted = 0;
                if (context1 != null) {


                    ttVaccinesUsed = context1.allCommonsRepositoryobjects("ec_woman").customQuery(sqlWoman, new String[]{}, "ec_woman");
                    childVaccinesUsed = context1.allCommonsRepositoryobjects("ec_child").customQuery(sqlChild, new String[]{}, "ec_child");
                    wastedVaccines = context1.allCommonsRepositoryobjects("field").customQuery(sqlWasted, new String[]{}, "field");
                    Map<String, Integer> usedmap = new HashMap<String, Integer>();
                    Map<String, Integer> wastedmap = new HashMap<String, Integer>();

                    //Log.d("TT Vaccines",  ttVaccinesUsed.size() +"tt vaccines used");
                    //Log.d("child Vaccines",  childVaccinesUsed.size() +"tt vaccines used");
                    for (String s : ttVaccinesUsed.get(0).getColumnmaps().keySet()) {
                        totalTTUsed = totalTTUsed + Integer.parseInt(ttVaccinesUsed.get(0).getColumnmaps().get(s));
                        usedmap.put(s, Integer.parseInt(ttVaccinesUsed.get(0).getColumnmaps().get(s)));
                    }

                    for (String s : childVaccinesUsed.get(0).getColumnmaps().keySet()) {
                        totalChildVaccinesUsed = totalChildVaccinesUsed + Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s));
                        Log.d("child Vaccines-", s);
                        //   Log.d("child Vaccines", wastedVaccines.get(i).getColumnmaps().get(s) + " -wasted vaccines used");

                        if (usedmap.containsKey(s)) {


                            usedmap.put(s, usedmap.get(s) + Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s)));
                        } else {

                            usedmap.put(s, Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s)));

                        }

                    }
                    for (int i = 0; i < wastedVaccines.size(); i++) {
                        for (String s : wastedVaccines.get(i).getColumnmaps().keySet()) {

                            Log.d("child Vaccines-", s);
                            Log.d("child Vaccines", wastedVaccines.get(i).getColumnmaps().get(s) + " -wasted vaccines used");

                            totalWasted = totalWasted + Integer.parseInt(wastedVaccines.get(i).getColumnmaps().get(s));
                            wastedmap.put(s, Integer.parseInt(wastedVaccines.get(i).getColumnmaps().get(s)));
                        }

                    }
                    HashMap<String, String> map2 = new HashMap<String, String>();
                    for (Map.Entry<String, Integer> entry : usedmap.entrySet()) {

                        map2.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }

                    HashMap<String, String> wastedmap2 = new HashMap<String, String>();
                    for (Map.Entry<String, Integer> entry : wastedmap.entrySet()) {

                        map2.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    viewHolder.daymonthLayout.setTag(R.id.field_daymonth, pc);
                    viewHolder.daymonthLayout.setTag(R.id.field_daymonth_layout, map2);
                    viewHolder.daymonthLayout.setTag(R.id.field_month_target_layout, wastedmap2);

                }


                viewHolder.daymonthTextView.setText(fmt.format("%tB %tY", cal, cal).toString());

                viewHolder.monthTargetTextView.setText(pc.getDetails().get("Target_assigned_for_vaccination_at_each_month") != null ? pc.getDetails().get("Target_assigned_for_vaccination_at_each_month") : "Not Defined");
                viewHolder.monthreceivedTextView.setText(pc.getDetails().get("Target_assigned_for_vaccination_at_each_month") != null ? pc.getDetails().get("Target_assigned_for_vaccination_at_each_month") : "Not Defined");

                int bcgBalanceInHand = Integer.parseInt(pc.getDetails().get("bcg_balance_in_hand") != null ? pc.getDetails().get("bcg_balance_in_hand") : "0");
                int bcgReceived = Integer.parseInt(pc.getDetails().get("bcg_received") != null ? pc.getDetails().get("bcg_received") : "0");

                int opv_balance_in_hand = Integer.parseInt(pc.getDetails().get("opv_balance_in_hand") != null ? pc.getDetails().get("opv_balance_in_hand") : "0");
                int opv_received = Integer.parseInt(pc.getDetails().get("opv_received") != null ? pc.getDetails().get("opv_received") : "0");


                int ipv_balance_in_hand = Integer.parseInt(pc.getDetails().get("ipv_balance_in_hand") != null ? pc.getDetails().get("ipv_balance_in_hand") : "0");
                int ipv_received = Integer.parseInt(pc.getDetails().get("ipv_received") != null ? pc.getDetails().get("ipv_received") : "0");

                int pcv_balance_in_hand = Integer.parseInt(pc.getDetails().get("pcv_balance_in_hand") != null ? pc.getDetails().get("pcv_balance_in_hand") : "0");
                int pcv_received = Integer.parseInt(pc.getDetails().get("pcv_received") != null ? pc.getDetails().get("pcv_received") : "0");

                int penta_balance_in_hand = Integer.parseInt(pc.getDetails().get("penta_balance_in_hand") != null ? pc.getDetails().get("penta_balance_in_hand") : "0");
                int penta_received = Integer.parseInt(pc.getDetails().get("penta_received") != null ? pc.getDetails().get("penta_received") : "0");

                int measles_balance_in_hand = Integer.parseInt(pc.getDetails().get("measles_balance_in_hand") != null ? pc.getDetails().get("measles_balance_in_hand") : "0");
                int measles_received = Integer.parseInt(pc.getDetails().get("measles_received") != null ? pc.getDetails().get("measles_received") : "0");


                int tt_balance_in_hand = Integer.parseInt(pc.getDetails().get("tt_balance_in_hand") != null ? pc.getDetails().get("tt_balance_in_hand") : "0");
                int tt_received = Integer.parseInt(pc.getDetails().get("tt_received") != null ? pc.getDetails().get("tt_received") : "0");

                int dilutants_balance_in_hand = Integer.parseInt(pc.getDetails().get("dilutants_balance_in_hand") != null ? pc.getDetails().get("dilutants_balance_in_hand") : "0");
                int dilutants_received = Integer.parseInt(pc.getDetails().get("dilutants_received") != null ? pc.getDetails().get("dilutants_received") : "0");

                int syringes_balance_in_hand = Integer.parseInt(pc.getDetails().get("syringes_balance_in_hand") != null ? pc.getDetails().get("syringes_balance_in_hand") : "0");
                int syringes_received = Integer.parseInt(pc.getDetails().get("syringes_received") != null ? pc.getDetails().get("syringes_received") : "0");


                int safety_boxes_balance_in_hand = Integer.parseInt(pc.getDetails().get("safety_boxes_balance_in_hand") != null ? pc.getDetails().get("safety_boxes_balance_in_hand") : "0");
                int safety_boxes_received = Integer.parseInt(pc.getDetails().get("safety_boxes_received") != null ? pc.getDetails().get("safety_boxes_received") : "0");

                //#TODO get Total balance,wasted and received from total variables instead of calculating here.
                int balanceInHand = bcgBalanceInHand + opv_balance_in_hand + ipv_balance_in_hand + pcv_balance_in_hand + penta_balance_in_hand + measles_balance_in_hand + tt_balance_in_hand + dilutants_balance_in_hand +
                        syringes_balance_in_hand + safety_boxes_balance_in_hand;

                int Received = bcgReceived + opv_received + ipv_received + pcv_received + penta_received + measles_received + tt_received +
                        dilutants_received + syringes_received + safety_boxes_received;
                viewHolder.monthwastedTextView.setText(String.valueOf(totalWasted));
                viewHolder.monthreceivedTextView.setText(String.valueOf(Received));
                //  viewHolder.monthusedTextView.setText(String.valueOf(balanceInHand));
                viewHolder.monthusedTextView.setText(String.valueOf(totalChildVaccinesUsed + totalTTUsed));


            }
        } else {
            //#TODO instiante  views for daily


            if (parentView == null) {
                parentView = (ViewGroup) inflater().inflate(R.layout.smart_register_field_client_daily, null);


                viewHolder = new ViewHolder();

                viewHolder.dateTextView = (TextView) parentView.findViewById(R.id.field_day);
                viewHolder.dateLayout = (LinearLayout) parentView.findViewById(R.id.field_day_layout);

                viewHolder.dailyUsedTextView = (TextView) parentView.findViewById(R.id.field_date_used);
                viewHolder.dailyUsedLayout = (LinearLayout) parentView.findViewById(R.id.field_date_used_layout);

                viewHolder.dailyWastedTextView = (TextView) parentView.findViewById(R.id.field_date_vaccine_wasted);
                viewHolder.dailyWastedLayout = (LinearLayout) parentView.findViewById(R.id.field_date_vaccine_wasted_layout);
                parentView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) parentView.getTag();

            }

            viewHolder.dateLayout.setOnClickListener(onClickListener);
            viewHolder.dateLayout.setTag(R.id.field_day, client);
            String totalWaste = pc.getColumnmaps().get("total_wasted") != null ? pc.getColumnmaps().get("total_wasted") : "0";
            viewHolder.dailyWastedTextView.setText(totalWaste);

            String date = pc.getColumnmaps().get("date") != null ? pc.getColumnmaps().get("date") : "";
            viewHolder.dateTextView.setText(date);

            String sqlWomanforDaily = "select (" +
                    "select count(*) c from ec_woman where tt1 =  '" + date + "') tt1," +
                    "(select count(*) c from ec_woman where tt2 = '" + date + "') tt2," +
                    "(select count(*) c from ec_woman where tt3 = '" + date + "') tt3," +
                    "(select count(*) c from ec_woman where tt4 = '" + date + "') tt4," +
                    "(select count(*) c from ec_woman where tt5 = '" + date + "') tt5 " +
                    "from ec_woman limit 1; ";


            String sqlChildforDaily = "select (" +
                    "select count(*) c from ec_child where bcg like '" + date + "') bcg," +
                    "(select count(*) c from ec_child where opv_0 like '" + date + "') opv_0," +
                    "(select count(*) c from ec_child where opv_1 like '" + date + "') opv_1," +
                    "(select count(*) c from ec_child where opv_2 like '" + date + "') opv_2," +
                    "(select count(*) c from ec_child where opv_3 like '" + date + "') opv_3, " +
                    "(select count(*) c from ec_child where pcv_1 like '" + date + "') pcv_1," +
                    "(select count(*) c from ec_child where pcv_2 like '" + date + "') pcv_2," +
                    "(select count(*) c from ec_child where pcv_3 like '" + date + "') pcv_3, " +
                    "(select count(*) c from ec_child where measles_1 like '" + date + "') measles_1, " +
                    "(select count(*) c from ec_child where measles_2 like '" + date + "') measles_2," +
                    "(select count(*) c from ec_child where pentavalent_1 like '" + date + "') pentavalent_1," +
                    "(select count(*) c from ec_child where pentavalent_2 like '" + date + "') pentavalent_2," +
                    "(select count(*) c from ec_child where pentavalent_3 like '" + date + "') pentavalent_3 " +
                    "from ec_child limit 1 ;";

            List<CommonPersonObject> ttVaccinesUsed = null;
            List<CommonPersonObject> childVaccinesUsed = null;


            int totalTTUsed = 0;
            int totalChildVaccinesUsed = 0;
            ttVaccinesUsed = context1.allCommonsRepositoryobjects("ec_woman").customQuery(sqlWomanforDaily, new String[]{}, "ec_woman");
            childVaccinesUsed = context1.allCommonsRepositoryobjects("ec_child").customQuery(sqlChildforDaily, new String[]{}, "ec_child");

            //Log.d("TT Vaccines",  ttVaccinesUsed.size() +"tt vaccines used");
            //Log.d("child Vaccines",  childVaccinesUsed.size() +"tt vaccines used");
            HashMap<String, Integer> map = new HashMap<String, Integer>();

            for (String s : ttVaccinesUsed.get(0).getColumnmaps().keySet()) {
                totalTTUsed = totalTTUsed + Integer.parseInt(ttVaccinesUsed.get(0).getColumnmaps().get(s));

                map.put(s, Integer.parseInt(ttVaccinesUsed.get(0).getColumnmaps().get(s)));
            }

            for (String s : childVaccinesUsed.get(0).getColumnmaps().keySet()) {
                totalChildVaccinesUsed = totalChildVaccinesUsed + Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s));
                if (map.containsKey(s)) {

                    map.put(s, map.get(s) + Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s)));
                } else {

                    map.put(s, Integer.parseInt(childVaccinesUsed.get(0).getColumnmaps().get(s)));

                }
            }
            HashMap<String, String> map2 = new HashMap<String, String>();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {

                map2.put(entry.getKey(), String.valueOf(entry.getValue()));
            }

            viewHolder.dailyUsedTextView.setText(String.valueOf(totalChildVaccinesUsed + totalTTUsed));
            viewHolder.dateLayout.setTag(R.id.field_day_layout, map2);

        }


        parentView.setLayoutParams(clientViewLayoutParams);
        return parentView;
    }


    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }


    class ViewHolder {
        //for monthly
        TextView daymonthTextView;
        TextView monthTargetTextView;
        TextView monthreceivedTextView;
        TextView monthusedTextView;
        TextView monthwastedTextView;

        LinearLayout daymonthLayout;
        LinearLayout monthTargetLayout;
        LinearLayout monthreceivedLayout;
        LinearLayout monthusedLayout;
        LinearLayout monthwastedLayout;


        //for daily
        TextView dateTextView;
        TextView dailyUsedTextView;
        TextView dailyWastedTextView;

        LinearLayout dateLayout;
        LinearLayout dailyUsedLayout;
        LinearLayout dailyWastedLayout;


    }

    private int calculateVaccineUsed(String date) {
        int totalUsed = 0;
        String sql = "select * from ec_child where date like  ?";
        //    List<CommonPersonObject> used=null;

        // Log.d("Vaccinator", "Context is not null");

        Log.d("Vaccinator", "Pk child repo not null");
        List<CommonPersonObject> used = context1.allCommonsRepositoryobjects("ec_child").customQuery(sql, new String[]{date + "%"}, "ec_child");

        for (CommonPersonObject o : used) {
            totalUsed += Integer.parseInt(o.getColumnmaps().get("total_used"));

        }
        String sql1 = "select * from ec_woman where date like  ?";
        List<CommonPersonObject> used1 = context1.allCommonsRepositoryobjects("ec_woman").customQuery(sql1, new String[]{date + "%"}, "ec_woman");

        for (CommonPersonObject o : used1) {
            totalUsed += Integer.parseInt(o.getColumnmaps().get("total_used"));

        }

        String sqlEvents = "select * from TimelineEvent where date like  ?";

        // #TODO include TimeLineevent table data also .
        //  TimelineEvent time= context1.allCommonsRepositoryobjects("TimelineEvent").customQuery(sqlEvents,new String[]{},"TimelineEvent");

        return totalUsed;
    }
}

package org.ei.opensrp.dghs.stock;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;
import org.ei.opensrp.dghs.R;
import org.ei.opensrp.dghs.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class StockClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final AlertService alertService;


    public StockClientsProvider(Context context,
                                View.OnClickListener onClickListener,
                                AlertService alertService) {
        this.onClickListener = onClickListener;
        this.alertService = alertService;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                WRAP_CONTENT);
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public void getView(final SmartRegisterClient smartRegisterClient, View convertView) {
        View itemView = convertView;


        TextView date = (TextView)itemView.findViewById(R.id.date);
        TextView vaccine_used = (TextView)itemView.findViewById(R.id.total_vaccine_stock_used);
        TextView vaccine_wasted = (TextView)itemView.findViewById(R.id.total_vaccine_stock_wasted);






//        TextView psrfdue = (TextView)itemView.findViewById(R.id.psrf_due_date);
////        Button due_visit_date = (Button)itemView.findViewById(R.id.hh_due_date);
//
//        ImageButton follow_up = (ImageButton)itemView.findViewById(R.id.btn_edit);


        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

        date.setText(pc.getColumnmaps().get("date")!=null?pc.getColumnmaps().get("date"):"");
        vaccine_wasted.setText(pc.getColumnmaps().get("total_wasted")!=null?pc.getColumnmaps().get("total_wasted"):"");
        vaccine_used.setText(""+totalVaccineUsedCound(pc));

//        constructENCCReminderDueBlock(pc, itemView);
////        constructNBNFDueBlock(pc, itemView);s
//        constructENCCVisitStatusBlock(pc,itemView);




        itemView.setLayoutParams(clientViewLayoutParams);
    }

    private int totalVaccineUsedCound(CommonPersonObjectClient pc) {
//        {"name":"bcg_wasted"},
//        {"name":"bcg_used"},
//        {"name":"opv_wasted"},
//        {"name":"opv_used"},
//        {"name":"ipv_wasted"},
//        {"name":"ipv_used"},
//        {"name":"pcv_wasted"},
//        {"name":"pcv_used"},
//        {"name":"penta_wasted"},
//        {"name":"penta_used"},
//        {"name":"measles_wasted"},
//        {"name":"measles_used"},
//        {"name":"tt_wasted"},
//        {"name":"tt_used"},
//        {"name":"dilutants_wasted"},
//        {"name":"dilutants_used"}
        int total = Integer.parseInt(pc.getColumnmaps().get("opv_used")!=null?pc.getColumnmaps().get("opv_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("bcg_used")!=null?pc.getColumnmaps().get("bcg_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("ipv_used")!=null?pc.getColumnmaps().get("ipv_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("pcv_used")!=null?pc.getColumnmaps().get("pcv_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("penta_used")!=null?pc.getColumnmaps().get("penta_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("measles_used")!=null?pc.getColumnmaps().get("measles_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("tt_used")!=null?pc.getColumnmaps().get("tt_used"):"0");
        total += Integer.parseInt(pc.getColumnmaps().get("dilutants_used")!=null?pc.getColumnmaps().get("dilutants_used"):"0");

        return total;
    }

    private String calculateage(int i) {
        if(i <= 15){
            return (i + " days");
        }
        if(i <= 141){
            return (i/7 + " weeks");
        }
        if(i <= 719){
            return (i/30 + " months");
        }
        if(i >719){
            String years = 719/365 + " years ";
            String months = "";
            if((719%365)!=0) {
                months = (719 % 365)/30 + " months";
            }

            return years + months;
        }
      return "";
    }
    public int getageindays(DateTime date){
        DateTime now = new DateTime();
        DateTimeZone dtz = DateTimeZone.getDefault();
        LocalDate today = now.toLocalDate();
        DateTime bday = date;
        DateTime startOfToday = today.toDateTimeAtStartOfDay(now.getZone());
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("yyyy-MM-dd");
        try {
            Log.v("today's date-birthdate", bday.toString(dtfOut));

            Log.v("today's date", startOfToday.toString(dtfOut));
        }catch (Exception e){

        }
//        LocalDate bday = new LocalDate(date);
//        LocalDate now = new LocalDate();
        Days days = Days.daysBetween(bday, startOfToday);
        return days.getDays();
    }
    public DateTime getdate(String date) {

//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime dt = formatter.parseDateTime(date+ " 00:00:00");
            return dt;
//            Date returndate = format.parse(date);
//            return returndate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public View inflatelayoutForCursorAdapter() {
        return (ViewGroup) inflater().inflate(R.layout.stock_layout_row, null);
    }








    public String setDate(String date, int daystoadd) {

        Date lastdate = converdatefromString(date);

        if(lastdate!=null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
            calendar.add(Calendar.DATE, daystoadd);//8 weeks
            lastdate.setTime(calendar.getTime().getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //            String result = String.format(Locale.ENGLISH, format.format(lastdate) );
            return (format.format(lastdate));
            //             due_visit_date.append(format.format(lastdate));

        }else{
            return "";
        }
    }
    public Date converdatefromString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        }catch (Exception e){
            return null;
        }
        return convertedDate;
    }
}

package org.ei.opensrp.path.tabfragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.StockControlActivity;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.Stock;
import org.ei.opensrp.path.repository.PathRepository;
import org.ei.opensrp.path.repository.StockRepository;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.StringUtil;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import util.DateUtils;
import util.Utils;
import util.VaccinatorUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Planning_Stock_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Planning_Stock_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Planning_Stock_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public View mainview;
    public Planning_Stock_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Planning_Stock_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Planning_Stock_fragment newInstance(String param1, String param2) {
        Planning_Stock_fragment fragment = new Planning_Stock_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_planning__stock_fragment, container, false);
        mainview = view;
//        creatgraphview(mainview);
        loatDataView(mainview);
        return view;
    }

    private void creatgraphview(View view) {
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime threemonthEarlierIterator = now.minusMonths(3);

        GraphView graph = (GraphView)view.findViewById(R.id.graph);
        ViewGroup graphparent = (ViewGroup)graph.getParent();
        final int index = graphparent.indexOfChild(graph);
        graphparent.removeView(graph);
        graph = new GraphView(getActivity());
        graph.setId(R.id.graph);

        graphparent.addView(graph,index);


        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        String [] montharry;
        int arraymonthslabelsize = 0;
        if(now.minusMonths(1).monthOfYear() != now.monthOfYear()){
            arraymonthslabelsize = 4;
//            montharry = new String [arraymonthslabelsize];
            montharry = new String[]{now.minusMonths(3).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(2).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(1).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(0).monthOfYear().getAsShortText()+" "+now.year().getAsShortText()
            };
        }else{
            arraymonthslabelsize = 3;
            montharry = new String[]{now.minusMonths(3).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(2).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(1).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),
                    now.minusMonths(0).monthOfYear().getAsShortText()+" "+now.year().getAsShortText(),

            };
        }
        staticLabelsFormatter.setHorizontalLabels(montharry);
//        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        DateFormat month_date = new SimpleDateFormat("MMM yyyy");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(),month_date));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        graph.removeAllSeries();
        graph.getViewport().setMinX(now.minusMonths(3).toDate().getTime());
        graph.getViewport().setMaxX(now.toDate().getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0.0);
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    public void loatDataView (View view){
        createTitle(view);
        createActiveChildrenStatsView(view);
        creatgraphview(view);
        createGraphDataAndView(view);
        createStockInfoForlastThreeMonths(view);
        getValueForStock(view);
        getLastThreeMonthStockIssued(view);
        waste_rate_Calculate(view);
        VaccinesDueNextMonth(view);
    }

    private void VaccinesDueNextMonth(View view) {
        int dosespervial = ((StockControlActivity)getActivity()).vaccine_type.getDoses();
        ((TextView)view.findViewById(R.id.due_vacc_next_month_value)).setText(""+(int) Math.ceil((double)processVaccinesDueNextMonth()/dosespervial)+ " vials");
    }

    private void waste_rate_Calculate(View view) {
        double wastepercent = 0.0;
        StockRepository stockRepository = new StockRepository((PathRepository) VaccinatorApplication.getInstance().getRepository(), VaccinatorApplication.createCommonFtsObject(), VaccinatorApplication.getInstance().context().alertService());
        int vaccinegiven = stockRepository.getVaccineUsedUntildate(System.currentTimeMillis(),((StockControlActivity)getActivity()).vaccine_type.getName().toLowerCase().trim());
        int vaccineissued = -1*getStockIssuedIntimeFrame(DateTime.now().yearOfEra().withMinimumValue(),DateTime.now())*(((StockControlActivity)getActivity()).vaccine_type.getDoses());
        if(vaccinegiven == 0 || vaccinegiven>vaccineissued){
            wastepercent = 0.0;
        }else{
            wastepercent = (1- ((double)vaccinegiven/vaccineissued))*100;
        }
        DecimalFormat df = new DecimalFormat("####0");
        ((TextView)view.findViewById(R.id.avg_vacc_waste_rate_value)).setText(""+df.format(Math.ceil(wastepercent))+ "%");

    }

    private void getLastThreeMonthStockIssued(View view) {
        TextView lastmonthlabel = (TextView)view.findViewById(R.id.month1);
        TextView lastmonthvialsUsed = (TextView)view.findViewById(R.id.month1vials);
        TextView secondlastmonthlabel = (TextView)view.findViewById(R.id.month2);
        TextView secondlastmonthvialsUsed = (TextView)view.findViewById(R.id.month2vials);
        TextView thirdmonthlabel = (TextView)view.findViewById(R.id.month3);
        TextView thirdmonthvialsUsed = (TextView)view.findViewById(R.id.month3vials);
        TextView threemonthaverage = (TextView)view.findViewById(R.id.month3average);





        DateTime today = new DateTime(System.currentTimeMillis());
        DateTime startofthismonth = today.dayOfMonth().withMinimumValue();

        //////////////////////last month///////////////////////////////////////////////////////////
        DateTime startofLastMonth = today.minusMonths(1).dayOfMonth().withMinimumValue();
        String lastmonth = startofLastMonth.monthOfYear().getAsShortText();
        String lastmonthyear = startofLastMonth.year().getAsShortText();

        int stockissuedlastmonth = -1*getStockIssuedIntimeFrame(startofLastMonth,startofthismonth);

        lastmonthlabel.setText(lastmonth + " "+ lastmonthyear);
        lastmonthvialsUsed.setText(""+stockissuedlastmonth + " vials");
        //////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////2nd last month///////////////////////////////////////////////////////////
        DateTime startof2ndLastMonth = startofLastMonth.minusDays(1).dayOfMonth().withMinimumValue();
        String secondlastmonth = startof2ndLastMonth.monthOfYear().getAsShortText();
        String secondlastmonthyear = startof2ndLastMonth.year().getAsShortText();

        int stockissued2ndlastmonth = -1*getStockIssuedIntimeFrame(startof2ndLastMonth,startofLastMonth);

        secondlastmonthlabel.setText(secondlastmonth +" "+secondlastmonthyear);
        secondlastmonthvialsUsed.setText(""+stockissued2ndlastmonth + " vials");
        //////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////3rd last month///////////////////////////////////////////////////////////
        DateTime startof3rdLastMonth = startof2ndLastMonth.minusDays(1).dayOfMonth().withMinimumValue();
        String thirdlastmonth = startof3rdLastMonth.monthOfYear().getAsShortText();
        String thirdlastmonthyear = startof3rdLastMonth.year().getAsShortText();
        int stockissued3rdlastmonth = -1*getStockIssuedIntimeFrame(startof3rdLastMonth,startof2ndLastMonth);

        thirdmonthlabel.setText(thirdlastmonth +" "+thirdlastmonthyear);
        thirdmonthvialsUsed.setText(""+stockissued3rdlastmonth + " vials");

        //////////////////////////////////////////////////////////////////////////////////////////

        int threemonthaveragevalue = (int) Math.ceil((double)(stockissuedlastmonth+stockissued2ndlastmonth+stockissued3rdlastmonth)/3);
        threemonthaverage.setText(threemonthaveragevalue+" vials");

    }

    private int getStockIssuedIntimeFrame(DateTime startofLastMonth, DateTime startofthismonth) {
        int sum = 0;
        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();

        Cursor c = db.rawQuery("Select sum(value) from Stocks where " + StockRepository.DATE_CREATED + " >= " + startofLastMonth.toDate().getTime()+" and "+
                StockRepository.DATE_CREATED + " < " + startofthismonth.toDate().getTime()+" and "+
                StockRepository.TRANSACTION_TYPE + " = '"+ Stock.issued+"' and "+
                StockRepository.VACCINE_TYPE_ID + " = "+ ((StockControlActivity)getActivity()).vaccine_type.getId(), null);
        String stockvalue = "0";
        if(c.getCount()>0) {
            c.moveToFirst();
            if(c.getString(0)!=null && !StringUtils.isBlank(c.getString(0)))
                stockvalue = c.getString(0);
            c.close();
        }else{
            c.close();
        }
        sum = sum + Integer.parseInt(stockvalue);
        return sum;
    }

    private void getValueForStock(View view) {
        TextView stockvalue = (TextView)view.findViewById(R.id.vials);
        stockvalue.setText(""+((StockControlActivity)getActivity()).getcurrentVialNumber()+ " vials");

    }

    private void createTitle(View view) {
        TextView titleview = (TextView)view.findViewById(R.id.name);
        TextView graphtitletext = (TextView)view.findViewById(R.id.graph_label_text);
        TextView current_stock_label = (TextView)view.findViewById(R.id.current_stock);
        TextView avg_vacc_waste_rate_label = (TextView)view.findViewById(R.id.avg_vacc_waste_rate_label);
        TextView due_vacc_description = (TextView)view.findViewById(R.id.due_vacc_description);
        TextView lastthreemonthstocktitle = (TextView)view.findViewById(R.id.last_three_months_stock_title);


        String vaccineName = ((StockControlActivity)getActivity()).vaccine_type.getName();

        titleview.setText(vaccineName+ " Planning");
        graphtitletext.setText("3 month "+vaccineName + " stock levels");
        current_stock_label.setText("Current "+ vaccineName+ " stock: ");
        avg_vacc_waste_rate_label.setText("Average "+ vaccineName+ " waste rate: ");
        due_vacc_description.setText("Calculated from current active children that will be due for "+vaccineName+" next month.");
        lastthreemonthstocktitle.setText("3 month "+vaccineName+" stock used");

        DateTime NextMonth = new DateTime(System.currentTimeMillis()).plusMonths(1);
        ((TextView)view.findViewById(R.id.due_vacc_next_month_label)).setText("Due "+vaccineName+" next month "+NextMonth.monthOfYear().getAsShortText()+" "+NextMonth.year().getAsShortText()+": ");

    }

    private void createStockInfoForlastThreeMonths(View view) {

    }

    private LineGraphSeries<DataPoint> createGraphDataAndView(View view) {
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime threemonthEarlierIterator = now.minusMonths(3).withTimeAtStartOfDay();
        ArrayList<DataPoint> datapointsforgraphs = new ArrayList<DataPoint>();
        while(threemonthEarlierIterator.isBefore(now)){
            PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
            net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();

            Cursor c = db.rawQuery("Select sum(value) from Stocks where " + StockRepository.DATE_CREATED + " <= " + threemonthEarlierIterator.toDate().getTime()+" and "+StockRepository.VACCINE_TYPE_ID + " = "+ ((StockControlActivity)getActivity()).vaccine_type.getId(), null);
            String stockvalue = "0";
            if(c.getCount()>0) {
                c.moveToFirst();
                if(c.getString(0)!=null && !StringUtils.isBlank(c.getString(0)))
                stockvalue = c.getString(0);
                c.close();
            }else{
                c.close();
            }
            datapointsforgraphs.add(new DataPoint(threemonthEarlierIterator.toDate(),Double.parseDouble(stockvalue)));
            threemonthEarlierIterator = threemonthEarlierIterator.plusDays(1);

        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(
                datapointsforgraphs.toArray(new DataPoint[datapointsforgraphs.size()])
        );
        GraphView graph = (GraphView)view.findViewById(R.id.graph);

        graph.removeAllSeries();
        series.setThickness(3);
        series.setColor(getResources().getColor(R.color.bluetext));
        graph.addSeries(series);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(series.getHighestValueY());
        return series;

    }

    public int processVaccinesDueNextMonth(){
        int vaccinesDueNextMonth = 0;
        String vaccinename = ((StockControlActivity)getActivity()).vaccine_type.getName();
        if(vaccinename.equalsIgnoreCase("M/MR")){
            vaccinename = "Measles / MR";
        }
        ArrayList <JSONObject> vaccineArray = readvaccineFileAndReturnVaccinesofSameType(vaccinename);
        for(int i = 0;i<vaccineArray.size();i++){
           vaccinesDueNextMonth = vaccinesDueNextMonth+ getVaccinesDueBasedOnSchedule(vaccineArray.get(i));
        }
        return vaccinesDueNextMonth;
    }

    private int getVaccinesDueBasedOnSchedule(JSONObject vaccineobject) {
        int countofNextMonthVaccineDue = 0;
        try {
        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();

            DateTime today = new DateTime(System.currentTimeMillis());

            //////////////////////next month///////////////////////////////////////////////////////////
            DateTime startofNextMonth = today.plusMonths(1).dayOfMonth().withMinimumValue();
//            DateTime EndofNextMonth = today.plusMonths(1).dayOfMonth().withMaximumValue();
            DecimalFormat mFormat= new DecimalFormat("00");
            String monthstring = mFormat.format(startofNextMonth.getMonthOfYear());
            mFormat= new DecimalFormat("0000");

            String yearstring = mFormat.format(startofNextMonth.getYear());
            String nextmonthdateString = yearstring + "-" + monthstring;

        Cursor c =  db.rawQuery("Select count(*) from alerts where scheduleName = '"+vaccineobject.getString("name")+"' and startDate like '%"+nextmonthdateString+"%'",null);
        c.moveToFirst();
            if(c.getCount()>0){
            countofNextMonthVaccineDue = Integer.parseInt(c.getString(0));
            }
            c.close();




        } catch (Exception e) {
            e.printStackTrace();
        }
        return countofNextMonthVaccineDue;
    }

    public  ArrayList <JSONObject> readvaccineFileAndReturnVaccinesofSameType(String vaccinetypename){
        ArrayList <JSONObject> vaccinesofsametype = new ArrayList<JSONObject>();
        String vaccinejsonstring = VaccinatorUtils.getSupportedVaccines(VaccinatorApplication.getInstance());
        try {
            JSONArray vaccineentry = new JSONArray(vaccinejsonstring);

            for(int i = 0 ;i < vaccineentry.length();i++){
                JSONObject objectatindex = vaccineentry.getJSONObject(i);
                if(objectatindex.has("vaccines")){
                    JSONArray vaccinearray = objectatindex.getJSONArray("vaccines");
                    for(int j = 0;j<vaccinearray.length();j++){
                        if(vaccinearray.getJSONObject(j).has("type")){
                            if(vaccinearray.getJSONObject(j).getString("type").equalsIgnoreCase(vaccinetypename)){
                                vaccinesofsametype.add(vaccinearray.getJSONObject(j));
                            }
                        }
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vaccinesofsametype;
    }




    private void createActiveChildrenStatsView(View view) {

        TextView zerotoelevenlastmonth = (TextView)view.findViewById(R.id.zerotoelevenlastmonth);
        TextView zerotoeleventhismonth = (TextView)view.findViewById(R.id.zerotoeleventhismonth);
        TextView twelvetofiftyninethismonth = (TextView)view.findViewById(R.id.twelvetofiftyninethismonth);
        TextView twelvetofiftyninelastmont = (TextView)view.findViewById(R.id.twelvetofiftyninelastmont);
        TextView twelvetofiftyninedifference = (TextView)view.findViewById(R.id.twelvetofiftyninedifference);
        TextView zerotoelevendifference = (TextView)view.findViewById(R.id.zerotoelevendifference);
        TextView last_month_total = (TextView)view.findViewById(R.id.last_month_total);
        TextView this_month_total = (TextView)view.findViewById(R.id.this_month_total);
        TextView difference_total = (TextView)view.findViewById(R.id.difference_total);
        String stringDifference0to11 = "";
        String stringDifference12to59 = "";
        activeChildrenStats activeChildrenStats = getActivechildrenStat();


        zerotoelevenlastmonth.setText(""+activeChildrenStats.getChildrenLastMonthZeroToEleven());
        zerotoeleventhismonth.setText(""+activeChildrenStats.getChildrenThisMonthZeroToEleven());
        twelvetofiftyninelastmont.setText(""+activeChildrenStats.getChildrenLastMonthtwelveTofiftyNine());
        twelvetofiftyninethismonth.setText(""+activeChildrenStats.getChildrenThisMonthtwelveTofiftyNine());

        this_month_total.setText(""+(activeChildrenStats.getChildrenThisMonthtwelveTofiftyNine()+activeChildrenStats.getChildrenThisMonthZeroToEleven()));
        last_month_total.setText(""+(activeChildrenStats.getChildrenLastMonthtwelveTofiftyNine()+activeChildrenStats.getChildrenLastMonthZeroToEleven()));

        Long difference0to11 = activeChildrenStats.getChildrenLastMonthZeroToEleven() - activeChildrenStats.getChildrenThisMonthZeroToEleven();
        if(difference0to11<0){
            stringDifference0to11 = ""+ difference0to11;
        }else{
            stringDifference0to11 = "+" + difference0to11;
        }
        Long difference12to59 = activeChildrenStats.getChildrenLastMonthtwelveTofiftyNine() - activeChildrenStats.getChildrenThisMonthtwelveTofiftyNine();
        if(difference12to59<0){
            stringDifference12to59 = ""+difference12to59;
        }else{
            stringDifference12to59 = "+"+difference12to59;
        }



        twelvetofiftyninedifference.setText(stringDifference12to59);
        zerotoelevendifference.setText(stringDifference0to11);
        if((difference0to11+difference12to59)<0) {
            difference_total.setText("" + (difference0to11 + difference12to59));
        }else{
            difference_total.setText("+" + (difference0to11 + difference12to59));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public activeChildrenStats getActivechildrenStat(){
        activeChildrenStats activeChildrenStats = new activeChildrenStats();
        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
        Cursor c = db.rawQuery("Select dob,client_reg_date from ec_child where inactive != 'true' and lost_to_follow_up != 'true' ",null);
        c.moveToFirst();
        boolean thismonth = false;

        while(!c.isAfterLast()){
            thismonth = false;
            String dobString = c.getString(0);
            String createdString = c.getString(1);
            if (!TextUtils.isEmpty(dobString)) {
                DateTime dateTime = new DateTime(dobString);
                Date dob = dateTime.toDate();
                DateTime dateTime2 = new DateTime(createdString);
                Date createddate = dateTime.toDate();
                DateTime now = new DateTime(System.currentTimeMillis());
                if(now.getMonthOfYear() == dateTime2.getMonthOfYear() && now.getYear() == dateTime2.getYear()){
                    thismonth = true;
                }


                long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();
                long createdtimeDiff = Calendar.getInstance().getTimeInMillis() - createddate.getTime();

                if (timeDiff >= 0) {
                    long days = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
                    int months = (int) Math.floor((float) timeDiff /
                            TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));
                    int weeks = (int) Math.floor((float) (timeDiff - TimeUnit.MILLISECONDS.convert(
                            months * 30, TimeUnit.DAYS)) /
                            TimeUnit.MILLISECONDS.convert(7, TimeUnit.DAYS));

                    if (weeks >= 4) {
                        weeks = 0;
                        months++;
                    }
                    if(months<12){
                        if(thismonth){
                            activeChildrenStats.setChildrenThisMonthZeroToEleven(activeChildrenStats.getChildrenThisMonthZeroToEleven()+1);
                        }else{
                            activeChildrenStats.setChildrenLastMonthZeroToEleven(activeChildrenStats.getChildrenLastMonthZeroToEleven()+1);
                        }
                    }else if(months>11 && months < 60){
                        if(thismonth){
                            activeChildrenStats.setChildrenThisMonthtwelveTofiftyNine(activeChildrenStats.getChildrenThisMonthtwelveTofiftyNine()+1);
                        }else{
                            activeChildrenStats.setChildrenLastMonthtwelveTofiftyNine(activeChildrenStats.getChildrenLastMonthtwelveTofiftyNine()+1);
                        }
                    }
                }
//                if (createdtimeDiff >= 0) {
//                    long daysSinceCreated = TimeUnit.DAYS.convert(createdtimeDiff, TimeUnit.MILLISECONDS);
//                }
            }
            c.moveToNext();
        }
        c.close();
        return activeChildrenStats;
    }
    class activeChildrenStats{
        Long childrenLastMonthZeroToEleven = 0l;
        Long childrenLastMonthtwelveTofiftyNine = 0l;
        Long childrenThisMonthZeroToEleven = 0l;
        Long childrenThisMonthtwelveTofiftyNine = 0l;

        public Long getChildrenLastMonthZeroToEleven() {
            return childrenLastMonthZeroToEleven;
        }

        public void setChildrenLastMonthZeroToEleven(Long childrenLastMonthZeroToEleven) {
            this.childrenLastMonthZeroToEleven = childrenLastMonthZeroToEleven;
        }

        public Long getChildrenLastMonthtwelveTofiftyNine() {
            return childrenLastMonthtwelveTofiftyNine;
        }

        public void setChildrenLastMonthtwelveTofiftyNine(Long childrenLastMonthtwelveTofiftyNine) {
            this.childrenLastMonthtwelveTofiftyNine = childrenLastMonthtwelveTofiftyNine;
        }

        public Long getChildrenThisMonthZeroToEleven() {
            return childrenThisMonthZeroToEleven;
        }

        public void setChildrenThisMonthZeroToEleven(Long childrenThisMonthZeroToEleven) {
            this.childrenThisMonthZeroToEleven = childrenThisMonthZeroToEleven;
        }

        public Long getChildrenThisMonthtwelveTofiftyNine() {
            return childrenThisMonthtwelveTofiftyNine;
        }

        public void setChildrenThisMonthtwelveTofiftyNine(Long childrenThisMonthtwelveTofiftyNine) {
            this.childrenThisMonthtwelveTofiftyNine = childrenThisMonthtwelveTofiftyNine;
        }
    }


//    public int getDueVaccinesForNextMonthBasedOnDependantVaccineGiven(String dependentvaccinename ,long days ){
//        int toreturn = 0;
//        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
//        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
//        DateTime today = new DateTime(System.currentTimeMillis());
//        DateTime startofthismonth = today.dayOfMonth().withMinimumValue();
//
//        //////////////////////next month///////////////////////////////////////////////////////////
//        DateTime startofNextMonth = today.plusMonths(1).dayOfMonth().withMinimumValue();
//        DateTime EndofNextMonth = today.plusMonths(1).dayOfMonth().withMaximumValue();
//
//        Cursor c = db.rawQuery("Select Count (*) from vaccines where ("+StockRepository.DATE_CREATED+" + "+days +") => "+startofNextMonth.toDate().getTime()+"and ("+StockRepository.DATE_CREATED+" + "+days +") <= "+startofNextMonth.toDate().getTime()+ " and name ='"+dependentvaccinename+"'",null);
//        if(c.getColumnCount()>0){
//            c.moveToFirst();
//            toreturn = Integer.parseInt(c.getString(0));
//            c.close();
//        }
//        return toreturn;
//    }
//    public int getDueVaccinesForNextMonthBasedOnBirth(String dependentvaccinename ,long days,long expiry ){
//        int toreturn = 0;
//        PathRepository repo = (PathRepository) VaccinatorApplication.getInstance().getRepository();
//        net.sqlcipher.database.SQLiteDatabase db = repo.getReadableDatabase();
//        DateTime today = new DateTime(System.currentTimeMillis());
//        DateTime startofthismonth = today.dayOfMonth().withMinimumValue();
//
//
//        //////////////////////next month///////////////////////////////////////////////////////////
//        DateTime startofNextMonth = today.plusMonths(1).dayOfMonth().withMinimumValue();
//        DateTime EndofNextMonth = today.plusMonths(1).dayOfMonth().withMaximumValue();
//
//        DateTime startofNextMonthlowerlimit = startofNextMonth.minus(days);
//        DateTime EndofNextMonthlowerlimit = EndofNextMonth.minus(days);
//        DateTime startofNextMonthbeforeExpiry = startofNextMonth.minus(expiry);
////        select * from ec_child where ec_child.dob  < ('2012-02-27T05:00:00.000' + '6 years') and ec_child.base_entity_id not in (Select base_entity_id from vaccines where name = 'penta_1')
//        Cursor c = db.rawQuery("Select Count (*) from ec_child where ec_child.dob <= '"+startofNextMonthlowerlimit+"' and ec_child.dob >= '"+EndofNextMonthlowerlimit+"'  and ec_child.dob >= '"+startofNextMonthbeforeExpiry+"' and ec_child.base_entity_id not in (Select base_entity_id from vaccines where name = '"+dependentvaccinename+"'",null);
////        Cursor c = db.rawQuery("Select Count (*) from vaccines where ("+StockRepository.DATE_CREATED+" + "+days +") => "+startofNextMonth+"and ("+StockRepository.DATE_CREATED+" + "+days +") <= "+startofNextMonth+ " and name ='"+dependentvaccinename+"'",null);
//        if(c.getColumnCount()>0){
//            c.moveToFirst();
//            toreturn = Integer.parseInt(c.getString(0));
//            c.close();
//        }
//        c.close();
//        return toreturn;
//    }
}

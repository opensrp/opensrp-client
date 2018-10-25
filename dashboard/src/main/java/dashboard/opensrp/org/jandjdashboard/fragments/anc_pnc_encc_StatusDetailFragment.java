package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.reminderVisitStatusController;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryDetailActivity;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;

/**
 * A fragment representing a single dashboardCategory detail screen.
 * This fragment is either contained in a {@link dashboardCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link dashboardCategoryDetailActivity}
 * on handsets.
 */
public class anc_pnc_encc_StatusDetailFragment extends dashboardFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private RecyclerView recyclerView;
    private scheduleCardAdapter adapter;
    private ArrayList<Drawable> iconList;
    private ArrayList<String> titleList;
    private Spinner risk_status;
    private String controller_holder_key = "controller_holder";
    private String reminderVisitStatusControllerKey = "reminderVisitStatusController";
    reminderVisitStatusController rVSController;
    public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private Date to;
    private Date from;
    private View rootView;
    private String riskFlag = "normal";
    private HashMap<String, HashMap<String, String>> ancMap;
    private HashMap<String, HashMap<String, String>> pncMap;
    private HashMap<String, HashMap<String, String>> enccmap;
    private TextView filtertitle;
    private int ancdue;
    private int TotalScheduled;
    private int anccompleted;
    private int ancpostdue;
    private int ancexpired;
    private int pncdue;
    private int pnccompleted;
    private int pncpostdue;
    private int pncexpired;
    private int enccdue;
    private int enccompleted;
    private int enccpostdue;
    private int enccexpired;
    private int totalexpired;
    private int totaldue;
    private int totalpostdue;
    private int totalcompleted;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public anc_pnc_encc_StatusDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
        if (getArguments().containsKey(controller_holder_key)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            rVSController = (reminderVisitStatusController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(reminderVisitStatusControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.anc_pnc_encc_reminder_status_detail, container, false);
        addItemsOnRiskStatusSpinner(rootView);
//        addItemsOnScheduleTypeSpinner(rootView);
        to = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.add(Calendar.DATE, -(365*2));
        from = cal.getTime();
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);
        refresh(format.format(from),format.format(to));
        return rootView;
    }

    public void addItemsOnRiskStatusSpinner(View view) {

        risk_status = (Spinner)view.findViewById(R.id.risk_status);
        final List<String> list = new ArrayList<String>();
        list.add("ALL");
        list.add("High Risk");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        risk_status.setAdapter(dataAdapter);
        risk_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(list.get(i).equalsIgnoreCase("ALL")){
                    riskFlag = "normal";
                    refresh(format.format(from),format.format(to));
                }
                if(list.get(i).equalsIgnoreCase("High Risk")){
                    riskFlag = "high_risk";
                    refresh(format.format(from),format.format(to));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void addItemsOnScheduleTypeSpinner(final View mainview) {

        risk_status = (Spinner)mainview.findViewById(R.id.schedule_type);
        final List<String> list = new ArrayList<String>();
        list.add("ALL");
        list.add("ANC");
        list.add("PNC");
        list.add("ENCC");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        risk_status.setAdapter(dataAdapter);
        risk_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(list.get(i).equalsIgnoreCase("ALL")){

                    launchAllGraphs(mainview,from,to,"normal");
                }
                if(list.get(i).equalsIgnoreCase("ANC")){

                    launchAncGraphs(mainview,from,to,"normal");
                }
                if(list.get(i).equalsIgnoreCase("PNC")){
                    launchPncGraphs(mainview,from,to,"normal");
                }
                if(list.get(i).equalsIgnoreCase("ENCC")){
                    launchEnccGraphs(mainview,from,to,"normal");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void launchEnccGraphs(View view, Date from, Date to, String riskFlag) {
//        enccmap = rVSController.neonatalVisitQuery(from, to, riskFlag);
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        HashMap<String,DataPoint> encc1stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc1stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(enccmap.get("ENCC1").get("Completed"))));
        encc1stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(enccmap.get("ENCC1").get("Due"))));
        encc1stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(enccmap.get("ENCC1").get("Post Due"))));
        encc1stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(enccmap.get("ENCC1").get("Expired"))));
        HashMap<String,DataPoint> encc2stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc2stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(enccmap.get("ENCC2").get("Completed"))));
        encc2stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(enccmap.get("ENCC2").get("Due"))));
        encc2stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(enccmap.get("ENCC2").get("Post Due"))));
        encc2stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(enccmap.get("ENCC2").get("Expired"))));
        HashMap<String,DataPoint> encc3stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc3stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(enccmap.get("ENCC3").get("Completed"))));
        encc3stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(enccmap.get("ENCC3").get("Due"))));
        encc3stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(enccmap.get("ENCC3").get("Post Due"))));
        encc3stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(enccmap.get("ENCC3").get("Expired"))));

        int enccmax = findHighestInENCCMAP(enccmap);
        graphHolder.addView(addGraphs(encc1stringDataPointHashMap,"ENCC 1",enccmax),layoutParams);
        graphHolder.addView(addGraphs(encc2stringDataPointHashMap,"ENCC 2",enccmax),layoutParams);
        graphHolder.addView(addGraphs(encc3stringDataPointHashMap,"ENCC 3",enccmax),layoutParams);

        ((TextView)rootView.findViewById(R.id.scheduled_count)).setText(""+(enccompleted+enccdue+enccpostdue+enccexpired));
        ((TextView)rootView.findViewById(R.id.completed_count)).setText(""+enccompleted);
        ((TextView)rootView.findViewById(R.id.due_count)).setText(""+enccdue);
        ((TextView)rootView.findViewById(R.id.post_due_count)).setText(""+enccpostdue);
        ((TextView)rootView.findViewById(R.id.expired_count)).setText(""+enccexpired);
    }

    private void launchPncGraphs(View view, Date from, Date to, String riskFlag) {
//        pncMap = rVSController.pncVisitQuery(from, to, riskFlag);
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        HashMap<String,DataPoint> pnc1stringDataPointHashMap = new HashMap<String,DataPoint>();
        pnc1stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("PNC1").get("Completed"))));
        pnc1stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("PNC1").get("Due"))));
        pnc1stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("PNC1").get("Post Due"))));
        pnc1stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("PNC1").get("Expired"))));
        HashMap<String,DataPoint> pnc2stringDataPointHashMap = new HashMap<String,DataPoint>();
        pnc2stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("PNC2").get("Completed"))));
        pnc2stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("PNC2").get("Due"))));
        pnc2stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("PNC2").get("Post Due"))));
        pnc2stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("PNC2").get("Expired"))));
        HashMap<String,DataPoint> pnc3stringDataPointHashMap = new HashMap<String,DataPoint>();
        pnc3stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("PNC3").get("Completed"))));
        pnc3stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("PNC3").get("Due"))));
        pnc3stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("PNC3").get("Post Due"))));
        pnc3stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("PNC3").get("Expired"))));

        int pncmax = findHighestInPNCMAP(pncMap);
        graphHolder.addView(addGraphs(pnc1stringDataPointHashMap,"PNC 1",pncmax),layoutParams);
        graphHolder.addView(addGraphs(pnc2stringDataPointHashMap,"PNC 2",pncmax),layoutParams);
        graphHolder.addView(addGraphs(pnc3stringDataPointHashMap,"PNC 3",pncmax),layoutParams);

        ((TextView)rootView.findViewById(R.id.scheduled_count)).setText(""+(pnccompleted+pncdue+pncpostdue+pncexpired));
        ((TextView)rootView.findViewById(R.id.completed_count)).setText(""+pnccompleted);
        ((TextView)rootView.findViewById(R.id.due_count)).setText(""+pncdue);
        ((TextView)rootView.findViewById(R.id.post_due_count)).setText(""+pncpostdue);
        ((TextView)rootView.findViewById(R.id.expired_count)).setText(""+pncexpired);
    }

    private void launchAncGraphs(View view, Date from, Date to, String riskFlag) {
//        ancMap = rVSController.ancVisitQuery(from, to, riskFlag);
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        HashMap<String,DataPoint> anc1stringDataPointHashMap = new HashMap<String,DataPoint>();
        anc1stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(ancMap.get("ANC1").get("Completed"))));
        anc1stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(ancMap.get("ANC1").get("Due"))));
        anc1stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(ancMap.get("ANC1").get("Post Due"))));
        anc1stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(ancMap.get("ANC1").get("Expired"))));
        HashMap<String,DataPoint> anc2stringDataPointHashMap = new HashMap<String,DataPoint>();
        anc2stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(ancMap.get("ANC2").get("Completed"))));
        anc2stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(ancMap.get("ANC2").get("Due"))));
        anc2stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(ancMap.get("ANC2").get("Post Due"))));
        anc2stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(ancMap.get("ANC2").get("Expired"))));

        HashMap<String,DataPoint> anc3stringDataPointHashMap = new HashMap<String,DataPoint>();
        anc3stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(ancMap.get("ANC3").get("Completed"))));
        anc3stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(ancMap.get("ANC3").get("Due"))));
        anc3stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(ancMap.get("ANC3").get("Post Due"))));
        anc3stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(ancMap.get("ANC3").get("Expired"))));

        HashMap<String,DataPoint> anc4stringDataPointHashMap = new HashMap<String,DataPoint>();
        anc4stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(ancMap.get("ANC4").get("Completed"))));
        anc4stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(ancMap.get("ANC4").get("Due"))));
        anc4stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(ancMap.get("ANC4").get("Post Due"))));
        anc4stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(ancMap.get("ANC4").get("Expired"))));
        int ancmax = findHighestInANCMAP(ancMap);

        graphHolder.addView(addGraphs(anc1stringDataPointHashMap,"ANC 1",ancmax),layoutParams);
        graphHolder.addView(addGraphs(anc2stringDataPointHashMap,"ANC 2",ancmax),layoutParams);
        graphHolder.addView(addGraphs(anc3stringDataPointHashMap,"ANC 3",ancmax),layoutParams);
        graphHolder.addView(addGraphs(anc4stringDataPointHashMap,"ANC 4",ancmax),layoutParams);

        ((TextView)rootView.findViewById(R.id.scheduled_count)).setText(""+(anccompleted+ancdue+ancpostdue+ancexpired));
        ((TextView)rootView.findViewById(R.id.completed_count)).setText(""+anccompleted);
        ((TextView)rootView.findViewById(R.id.due_count)).setText(""+ancdue);
        ((TextView)rootView.findViewById(R.id.post_due_count)).setText(""+ancpostdue);
        ((TextView)rootView.findViewById(R.id.expired_count)).setText(""+ancexpired);

    }

    private void launchAllGraphs(View view, Date from, Date to, String riskFlag) {
//        ancMap = rVSController.ancVisitQuery(from, to, riskFlag);
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();

        HashMap<String,DataPoint> ancstringDataPointHashMap = new HashMap<String,DataPoint>();
        ancstringDataPointHashMap.put("Completed",new DataPoint(0, anccompleted));
        ancstringDataPointHashMap.put("Due",new DataPoint(1,ancdue));
        ancstringDataPointHashMap.put("Post Due",new DataPoint(2,ancpostdue));
        ancstringDataPointHashMap.put("Expired",new DataPoint(3,ancexpired));

        HashMap<String,DataPoint> pncstringDataPointHashMap = new HashMap<String,DataPoint>();
        pncstringDataPointHashMap.put("Completed",new DataPoint(0,pnccompleted));
        pncstringDataPointHashMap.put("Due",new DataPoint(1,pncdue));
        pncstringDataPointHashMap.put("Post Due",new DataPoint(2,pncpostdue));
        pncstringDataPointHashMap.put("Expired",new DataPoint(3,pncexpired));

        HashMap<String,DataPoint> enccstringDataPointHashMap = new HashMap<String,DataPoint>();
        enccstringDataPointHashMap.put("Completed",new DataPoint(0,enccompleted));
        enccstringDataPointHashMap.put("Due",new DataPoint(1,enccdue));
        enccstringDataPointHashMap.put("Post Due",new DataPoint(2,enccpostdue));
        enccstringDataPointHashMap.put("Expired",new DataPoint(3,enccexpired));

//       int ancmax = findHighestInANCMAP(ancMap);
//       int pncmax = findHighestInPNCMAP(pncMap);
//       int enccmax = findHighestInENCCMAP(enccmap);
        Integer[] numbers = { anccompleted,
                ancdue,ancpostdue,ancexpired,pnccompleted,
                pncdue,pncpostdue,pncexpired,enccompleted,
                enccdue,enccpostdue,enccexpired};

        int max = (int) Collections.max(Arrays.asList(numbers));

//       int max = anccompleted;
//       if(pnccompleted>max){
//           max = pnccompleted;
//       }
//       if(enccompleted>max){
//           max = enccompleted;
//       }

        graphHolder.addView(addGraphs(ancstringDataPointHashMap,"ANC",max),layoutParams);
        graphHolder.addView(addGraphs(pncstringDataPointHashMap,"PNC",max),layoutParams);
        graphHolder.addView(addGraphs(enccstringDataPointHashMap,"ENCC",max),layoutParams);

        ((TextView)rootView.findViewById(R.id.scheduled_count)).setText(""+TotalScheduled);
        ((TextView)rootView.findViewById(R.id.completed_count)).setText(""+totalcompleted);
        ((TextView)rootView.findViewById(R.id.due_count)).setText(""+totaldue);
        ((TextView)rootView.findViewById(R.id.post_due_count)).setText(""+totalpostdue);
        ((TextView)rootView.findViewById(R.id.expired_count)).setText(""+totalexpired);

    }

    public GraphView addGraphs(final HashMap<String,DataPoint> stringDataPointHashMap, String Label , int yAxisMax){
        GraphView graph = new GraphView(getActivity());
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setDrawBorder(true);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
               stringDataPointHashMap.get("Completed"),
                stringDataPointHashMap.get("Due"),
                stringDataPointHashMap.get("Post Due"),
                stringDataPointHashMap.get("Expired"),
        });
        graph.addSeries(series);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if(data.equals(stringDataPointHashMap.get("Completed"))){
                    return getResources().getColor(R.color.completedgraphbarcolor_beta);
                }
                if(data.equals(stringDataPointHashMap.get("Due"))){
                    return getResources().getColor(R.color.duegraphbarcolor_beta);
                }
                if(data.equals(stringDataPointHashMap.get("Post Due"))){
                    return getResources().getColor(R.color.postduegraphbarcolor_beta);
                }
                if(data.equals(stringDataPointHashMap.get("Expired"))){
                    return getResources().getColor(R.color.expiredgraphbarcolor_beta);
                }
                return 0;
            }
        });
        double xInterval=1.0;
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        if (series instanceof BarGraphSeries ) {
            // Hide xLabels for now as no longer centered in the grid, but left aligned per the other types
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxY(yAxisMax);
            // Shunt the viewport, per v3.1.3 to show the full width of the first and last bars.
            graph.getViewport().setMinX(series.getLowestValueX() - (xInterval/2.0));
            graph.getViewport().setMaxX(series.getHighestValueX() + (xInterval/2.0));
        } else {
            graph.getViewport().setMinX(series.getLowestValueX() );
            graph.getViewport().setMaxX(series.getHighestValueX());
        }

        graph.getGridLabelRenderer().setHorizontalAxisTitle(Label);

        series.setSpacing(10);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);



        return graph;
    }

    @Override
    public void refresh(String fromdate, String todate) {
        try {
            from = format.parse(fromdate);
            to = format.parse(todate);

            filtertitle.setText(fromdate+" to "+todate);

            ancMap = rVSController.ancVisitQuery(from, to, riskFlag);
            pncMap = rVSController.pncVisitQuery(from, to, riskFlag);
            enccmap = rVSController.neonatalVisitQuery(from, to, riskFlag);

            findHighestInANCMAP(ancMap);

            ancdue = Integer.parseInt(ancMap.get("ANC1").get("Due"))+
                    Integer.parseInt(ancMap.get("ANC2").get("Due"))+
                    Integer.parseInt(ancMap.get("ANC3").get("Due"))+
                    Integer.parseInt(ancMap.get("ANC4").get("Due"));
            ancpostdue = Integer.parseInt(ancMap.get("ANC1").get("Post Due"))+
                    Integer.parseInt(ancMap.get("ANC2").get("Post Due"))+
                    Integer.parseInt(ancMap.get("ANC3").get("Post Due"))+
                    Integer.parseInt(ancMap.get("ANC4").get("Post Due"));
            ancexpired = Integer.parseInt(ancMap.get("ANC1").get("Expired"))+
                    Integer.parseInt(ancMap.get("ANC2").get("Expired"))+
                    Integer.parseInt(ancMap.get("ANC3").get("Expired"))+
                    Integer.parseInt(ancMap.get("ANC4").get("Expired"));
            anccompleted = Integer.parseInt(ancMap.get("ANC1").get("Completed"))+
                    Integer.parseInt(ancMap.get("ANC2").get("Completed"))+
                    Integer.parseInt(ancMap.get("ANC3").get("Completed"))+
                    Integer.parseInt(ancMap.get("ANC4").get("Completed"));

            pncdue = Integer.parseInt(pncMap.get("PNC1").get("Due"))+
                    Integer.parseInt(pncMap.get("PNC2").get("Due"))+
                    Integer.parseInt(pncMap.get("PNC3").get("Due"));
            pncpostdue = Integer.parseInt(pncMap.get("PNC1").get("Post Due"))+
                    Integer.parseInt(pncMap.get("PNC2").get("Post Due"))+
                    Integer.parseInt(pncMap.get("PNC3").get("Post Due"));
            pncexpired = Integer.parseInt(pncMap.get("PNC1").get("Expired"))+
                    Integer.parseInt(pncMap.get("PNC2").get("Expired"))+
                    Integer.parseInt(pncMap.get("PNC3").get("Expired"));
            pnccompleted = Integer.parseInt(pncMap.get("PNC1").get("Completed"))+
                    Integer.parseInt(pncMap.get("PNC2").get("Completed"))+
                    Integer.parseInt(pncMap.get("PNC3").get("Completed"));

            enccdue = Integer.parseInt(enccmap.get("ENCC1").get("Due"))+
                    Integer.parseInt(enccmap.get("ENCC2").get("Due"))+
                    Integer.parseInt(enccmap.get("ENCC3").get("Due"));
            enccpostdue = Integer.parseInt(enccmap.get("ENCC1").get("Post Due"))+
                    Integer.parseInt(enccmap.get("ENCC2").get("Post Due"))+
                    Integer.parseInt(enccmap.get("ENCC3").get("Post Due"));
            enccexpired = Integer.parseInt(enccmap.get("ENCC1").get("Expired"))+
                    Integer.parseInt(enccmap.get("ENCC2").get("Expired"))+
                    Integer.parseInt(enccmap.get("ENCC3").get("Expired"));
            enccompleted = Integer.parseInt(enccmap.get("ENCC1").get("Completed"))+
                    Integer.parseInt(enccmap.get("ENCC2").get("Completed"))+
                    Integer.parseInt(enccmap.get("ENCC3").get("Completed"));
            TotalScheduled = ancdue
                    +anccompleted
                    +ancpostdue
                    +ancexpired
                    +pncdue
                    +pnccompleted
                    +pncpostdue
                    +pncexpired
                    +enccdue
                    +enccompleted
                    +enccpostdue
                    +enccexpired;
            totalexpired = ancexpired
                    +pncexpired
                    +enccexpired;
            totaldue = ancdue
                    +pncdue
                    +enccdue;
            totalpostdue = ancpostdue
                    +pncpostdue
                    +enccpostdue;
            totalcompleted = anccompleted
                    +pnccompleted
                    +enccompleted;

            ((TextView)rootView.findViewById(R.id.scheduled_count)).setText(""+TotalScheduled);
            ((TextView)rootView.findViewById(R.id.completed_count)).setText(""+totalcompleted);
            ((TextView)rootView.findViewById(R.id.due_count)).setText(""+totaldue);
            ((TextView)rootView.findViewById(R.id.post_due_count)).setText(""+totalpostdue);
            ((TextView)rootView.findViewById(R.id.expired_count)).setText(""+totalexpired);

//            addItemsOnRiskStatusSpinner(rootView);
            addItemsOnScheduleTypeSpinner(rootView);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private int findHighestInANCMAP(HashMap<String, HashMap<String, String>> ancMap) {
        int highest = 0;
        for(int i = 1; i<5;i++) {
            int [] temparray = new int [4];
            temparray [0] = Integer.parseInt(ancMap.get("ANC"+i).get("Due"));
            temparray [1] = Integer.parseInt(ancMap.get("ANC"+i).get("Post Due"));
            temparray [2]= Integer.parseInt(ancMap.get("ANC"+i).get("Completed"));
            temparray [3] = Integer.parseInt(ancMap.get("ANC"+i).get("Expired"));
            for(int j = 0; j<temparray.length;j++){
                if(temparray[j]>highest){
                    highest = temparray[j];
                }
            }
        }
        return highest;
    }

    private int findHighestInPNCMAP(HashMap<String, HashMap<String, String>> pncMap) {
        int highest = 0;
        for(int i = 1; i<4;i++) {
            int [] temparray = new int [4];
            temparray [0] = Integer.parseInt(pncMap.get("PNC"+i).get("Due"));
            temparray [1] = Integer.parseInt(pncMap.get("PNC"+i).get("Post Due"));
            temparray [2]= Integer.parseInt(pncMap.get("PNC"+i).get("Completed"));
            temparray [3] = Integer.parseInt(pncMap.get("PNC"+i).get("Expired"));
            for(int j = 0; j<temparray.length;j++){
                if(temparray[j]>highest){
                    highest = temparray[j];
                }
            }
        }
        return highest;
    }

    private int findHighestInENCCMAP(HashMap<String, HashMap<String, String>> enccMap) {
        int highest = 0;
        for(int i = 1; i<4;i++) {
            int [] temparray = new int [4];
            temparray [0] = Integer.parseInt(enccMap.get("ENCC"+i).get("Due"));
            temparray [1] = Integer.parseInt(enccMap.get("ENCC"+i).get("Post Due"));
            temparray [2]= Integer.parseInt(enccMap.get("ENCC"+i).get("Completed"));
            temparray [3] = Integer.parseInt(enccMap.get("ENCC"+i).get("Expired"));
            for(int j = 0; j<temparray.length;j++){
                if(temparray[j]>highest){
                    highest = temparray[j];
                }
            }
        }
        return highest;
    }
}

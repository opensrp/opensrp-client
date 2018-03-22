package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.reminderVisitStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.upcomingScheduleStatusController;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryDetailActivity;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;

/**
 * A fragment representing a single dashboardCategory detail screen.
 * This fragment is either contained in a {@link dashboardCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link dashboardCategoryDetailActivity}
 * on handsets.
 */
public class anc_pnc_encc_StatusDetailFragment extends Fragment {
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
        addItemsOnScheduleTypeSpinner(rootView);
        to = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(to);
        cal.add(Calendar.DATE, -(365*10));
        from = cal.getTime();


        return rootView;
    }

    public void addItemsOnRiskStatusSpinner(View view) {

        risk_status = (Spinner)view.findViewById(R.id.risk_status);
        List<String> list = new ArrayList<String>();
        list.add("Normal");
        list.add("High Risk");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        risk_status.setAdapter(dataAdapter);

    }

    public void addItemsOnScheduleTypeSpinner(final View mainview) {

        risk_status = (Spinner)mainview.findViewById(R.id.schedule_type);
        final List<String> list = new ArrayList<String>();
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
        HashMap<String,HashMap<String,String>> pncMap = rVSController.neonatalVisitQuery(from, to, riskFlag);
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        HashMap<String,DataPoint> encc1stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc1stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("ENCC1").get("Completed"))));
        encc1stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("ENCC1").get("Due"))));
        encc1stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("ENCC1").get("Post Due"))));
        encc1stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("ENCC1").get("Expired"))));
        HashMap<String,DataPoint> encc2stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc2stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("ENCC2").get("Completed"))));
        encc2stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("ENCC2").get("Due"))));
        encc2stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("ENCC2").get("Post Due"))));
        encc2stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("ENCC2").get("Expired"))));
        HashMap<String,DataPoint> encc3stringDataPointHashMap = new HashMap<String,DataPoint>();
        encc3stringDataPointHashMap.put("Completed",new DataPoint(0, Integer.parseInt(pncMap.get("ENCC3").get("Completed"))));
        encc3stringDataPointHashMap.put("Due",new DataPoint(1, Integer.parseInt(pncMap.get("ENCC3").get("Due"))));
        encc3stringDataPointHashMap.put("Post Due",new DataPoint(2, Integer.parseInt(pncMap.get("ENCC3").get("Post Due"))));
        encc3stringDataPointHashMap.put("Expired",new DataPoint(3, Integer.parseInt(pncMap.get("ENCC3").get("Expired"))));

        graphHolder.addView(addAncGraphs(encc1stringDataPointHashMap,"ENCC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(encc2stringDataPointHashMap,"ENCC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(encc3stringDataPointHashMap,"ENCC 3"),layoutParams);
    }

    private void launchPncGraphs(View view, Date from, Date to, String riskFlag) {
        HashMap<String,HashMap<String,String>> pncMap = rVSController.pncVisitQuery(from, to, riskFlag);
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

        graphHolder.addView(addAncGraphs(pnc1stringDataPointHashMap,"PNC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(pnc2stringDataPointHashMap,"PNC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(pnc3stringDataPointHashMap,"PNC 3"),layoutParams);
    }

    private void launchAncGraphs(View view, Date from, Date to, String riskFlag) {
        HashMap<String,HashMap<String,String>> ancMap = rVSController.ancVisitQuery(from, to, riskFlag);
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


        graphHolder.addView(addAncGraphs(anc1stringDataPointHashMap,"ANC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(anc2stringDataPointHashMap,"ANC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(anc3stringDataPointHashMap,"ANC 3"),layoutParams);
        graphHolder.addView(addAncGraphs(anc4stringDataPointHashMap,"ANC 4"),layoutParams);

    }

    public GraphView addAncGraphs(final HashMap<String,DataPoint> stringDataPointHashMap,String Label){
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
                    return getResources().getColor(R.color.completedgraphbarcolor);
                }
                if(data.equals(stringDataPointHashMap.get("Due"))){
                    return getResources().getColor(R.color.duegraphbarcolor);
                }
                if(data.equals(stringDataPointHashMap.get("Post Due"))){
                    return getResources().getColor(R.color.postduegraphbarcolor);
                }
                if(data.equals(stringDataPointHashMap.get("Expired"))){
                    return getResources().getColor(R.color.expiredgraphbarcolor);
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
            // Shunt the viewport, per v3.1.3 to show the full width of the first and last bars.
            graph.getViewport().setMinX(series.getLowestValueX() - (xInterval/2.0));
            graph.getViewport().setMaxX(series.getHighestValueX() + (xInterval/2.0));
        } else {
            graph.getViewport().setMinX(series.getLowestValueX() );
            graph.getViewport().setMaxX(series.getHighestValueX());
        }

        graph.getGridLabelRenderer().setHorizontalAxisTitle(Label);

        series.setSpacing(10);
        return graph;
    }


    public void refresh(String fromdate, String todate) {
        try {
            from = format.parse(fromdate);
            to = format.parse(todate);
            addItemsOnRiskStatusSpinner(rootView);
            addItemsOnScheduleTypeSpinner(rootView);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

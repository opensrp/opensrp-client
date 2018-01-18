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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.anc_pnc_encc_reminder_status_detail, container, false);
        addItemsOnRiskStatusSpinner(rootView);
        addItemsOnScheduleTypeSpinner(rootView);


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
                    launchAncGraphs(mainview);
                }
                if(list.get(i).equalsIgnoreCase("PNC")){
                    launchPncGraphs(mainview);
                }
                if(list.get(i).equalsIgnoreCase("ENCC")){
                    launchEnccGraphs(mainview);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void launchEnccGraphs(View view) {
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        final HashMap<String,DataPoint> stringDataPointHashMap = new HashMap<String,DataPoint>();
        stringDataPointHashMap.put("Completed",new DataPoint(0, 20));
        stringDataPointHashMap.put("Due",new DataPoint(1, 10));
        stringDataPointHashMap.put("Post Due",new DataPoint(2, 14));
        stringDataPointHashMap.put("Expired",new DataPoint(3, 10));
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ENCC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ENCC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ENCC 3"),layoutParams);
    }

    private void launchPncGraphs(View view) {
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        final HashMap<String,DataPoint> stringDataPointHashMap = new HashMap<String,DataPoint>();
        stringDataPointHashMap.put("Completed",new DataPoint(0, 20));
        stringDataPointHashMap.put("Due",new DataPoint(1, 10));
        stringDataPointHashMap.put("Post Due",new DataPoint(2, 14));
        stringDataPointHashMap.put("Expired",new DataPoint(3, 10));
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"PNC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"PNC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"PNC 3"),layoutParams);
    }

    private void launchAncGraphs(View view) {
        LinearLayout graphHolder = (LinearLayout) view.findViewById(R.id.graph_holder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        graphHolder.removeAllViews();
        final HashMap<String,DataPoint> stringDataPointHashMap = new HashMap<String,DataPoint>();
        stringDataPointHashMap.put("Completed",new DataPoint(0, 20));
        stringDataPointHashMap.put("Due",new DataPoint(1, 10));
        stringDataPointHashMap.put("Post Due",new DataPoint(2, 14));
        stringDataPointHashMap.put("Expired",new DataPoint(3, 10));
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ANC 1"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ANC 2"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ANC 3"),layoutParams);
        graphHolder.addView(addAncGraphs(stringDataPointHashMap,"ANC 4"),layoutParams);

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






}

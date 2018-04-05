package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
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
public class upcomingScheduleStatusDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    upcomingScheduleStatusController uSSController;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private RecyclerView recyclerView;
    private scheduleCardAdapter adapter;
    private ArrayList<Drawable> iconList;
    private ArrayList<String> titleList;
    private ArrayList<String> counts;
    private String controller_holder_key = "controller_holder";
    private String upcomingScheduleStatusControllerKey = "upcomingScheduleStatusController";
    private TextView filtertitle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public upcomingScheduleStatusDetailFragment() {
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
            uSSController = (upcomingScheduleStatusController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(upcomingScheduleStatusControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.upcoming_schedule_status_detail, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);

        iconList = new ArrayList<>();
        titleList = new ArrayList<>();
        counts = new ArrayList<>();
        adapter = new scheduleCardAdapter(getActivity(), iconList, titleList, counts);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView.setLayoutManager(mLayoutManager);;
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(2), true));
        recyclerView.setAdapter(adapter);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);

        prepareAlbums(uSSController.format.format(yesterday.getTime()),uSSController.format.format(today.getTime()));



        return rootView;
    }
    private void prepareAlbums(String fromdate,String todate) {
        filtertitle.setText(fromdate+" to "+todate);

        recyclerView.removeAllViews();
        titleList.removeAll(titleList);
        counts.removeAll(counts);
        iconList.removeAll(iconList);

        titleList.add("Household Visit");
        try {
            counts.add(uSSController.houseHoldVisitQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("ELCO Visit");
        try {
            counts.add(uSSController.elcoVisitQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("EDD");
        try {
            counts.add(uSSController.eddQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("ANC Visit");
        try {
            counts.add(uSSController.ancVisitQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("PNC Visit");
        try {
            counts.add(uSSController.pncVisitQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("Neonatal Visit");
        try {
            counts.add(uSSController.neonatalVisitQuery(uSSController.format.parse(fromdate),uSSController.format.parse(todate)));
        } catch (ParseException e) {
            counts.add("N/A");
            e.printStackTrace();
        }
        titleList.add("TT Vaccine");
        counts.add("N/A");
        titleList.add("Vaccine For Child");
        counts.add("N/A");

        iconList.add(getResources().getDrawable(R.drawable.householdschedulecard));
        iconList.add(getResources().getDrawable(R.drawable.elcovisit));
        iconList.add(getResources().getDrawable(R.drawable.edd));
        iconList.add(getResources().getDrawable(R.drawable.ancvisit));
        iconList.add(getResources().getDrawable(R.drawable.pncvisit));
        iconList.add(getResources().getDrawable(R.drawable.neonatalvisit));
        Drawable ttvaccine = getResources().getDrawable(R.drawable.ttvaccine);
        Drawable wrapDrawable = DrawableCompat.wrap(ttvaccine);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.unfocuseddatemonthcolor));
        iconList.add(ttvaccine);
        Drawable vaccine = getResources().getDrawable(R.drawable.vaccine);
        Drawable wrapDrawablechild = DrawableCompat.wrap(vaccine);
        DrawableCompat.setTint(wrapDrawablechild, getResources().getColor(R.color.unfocuseddatemonthcolor));
        iconList.add(vaccine);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void refresh(String fromdate, String todate) {
        prepareAlbums(fromdate,todate);
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}

package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.familyPlanningStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryDetailActivity;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;

/**
 * A fragment representing a single dashboardCategory detail screen.
 * This fragment is either contained in a {@link dashboardCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link dashboardCategoryDetailActivity}
 * on handsets.
 */
public class familyPlanningStatusDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private String controller_holder_key = "controller_holder";
    private String familyPlanningStatusControllerKey = "familyPlanningStatusController";
    familyPlanningStatusController fPSController;

    TextView pill_old,pill_new,pill_unit_total,pill_not_using_any_method,pill_using_other_method,pill_referred_for_method,pill_referred_for_side_effects;
    TextView condom_old,condom_new,condom_unit_total,condom_not_using_any_method,condom_using_other_method,condom_referred_for_method,condom_referred_for_side_effects;
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public familyPlanningStatusDetailFragment() {
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
            fPSController = (familyPlanningStatusController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(familyPlanningStatusControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.family_planning_status_detail, container, false);

        setupViews(rootView);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        refresh(fPSController.format.format(yesterday.getTime()),fPSController.format.format(today.getTime()));


        return rootView;
    }

    private void setupViews(View rootView) {
        ///////////pill textviews ///////////////////////////////
        pill_old = (TextView)rootView.findViewById(R.id.pill_old);
        pill_new = (TextView)rootView.findViewById(R.id.pill_new);
        pill_unit_total = (TextView)rootView.findViewById(R.id.pill_unit_total);
        pill_not_using_any_method = (TextView)rootView.findViewById(R.id.pill_left_not_using_any_method);
        pill_using_other_method = (TextView)rootView.findViewById(R.id.pill_left_using_other_method);
        pill_referred_for_method = (TextView)rootView.findViewById(R.id.pill_referred_for_method);
        pill_referred_for_side_effects = (TextView)rootView.findViewById(R.id.pill_referred_for_side_effects);
        ////////////////////////////////////////////////////////

        //////////condom textviews ////////////////////////////
        condom_old = (TextView)rootView.findViewById(R.id.condom_old);
        condom_new = (TextView)rootView.findViewById(R.id.condom_new);
        condom_unit_total = (TextView)rootView.findViewById(R.id.condom_unit_total);
        condom_not_using_any_method = (TextView)rootView.findViewById(R.id.condom_left_not_using_any_method);
        condom_using_other_method = (TextView)rootView.findViewById(R.id.condom_left_using_other_method);
        condom_referred_for_method = (TextView)rootView.findViewById(R.id.condom_referred_for_method);
        condom_referred_for_side_effects = (TextView)rootView.findViewById(R.id.condom_referred_for_side_effects);
        //////////////////////////////////////////////////////

    }
    public void refresh(String from, String to) {
        try {
            Date fromdate = fPSController.format.parse(from);
            Date todate = fPSController.format.parse(to);

            pill_old.setText(fPSController.pill_old_Query(fromdate,todate));
            pill_new.setText(fPSController.pill_new_Query(fromdate,todate));
            pill_unit_total.setText(fPSController.pill_unit_totalQuery(fromdate,todate));
            pill_not_using_any_method.setText(fPSController.pill_not_using_any_methodQuery(fromdate,todate));
            pill_using_other_method.setText(fPSController.pill_using_other_methodQuery(fromdate,todate));
            pill_referred_for_method.setText(fPSController.pill_referred_for_methodQuery(fromdate,todate));
            pill_referred_for_side_effects.setText(fPSController.pill_referred_for_side_effectsQuery(fromdate,todate));

        }catch (Exception e){
        }
    }
}

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.contraceptiveSupplyStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.reproductiveHealthServiceController;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryDetailActivity;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;

/**
 * A fragment representing a single dashboardCategory detail screen.
 * This fragment is either contained in a {@link dashboardCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link dashboardCategoryDetailActivity}
 * on handsets.
 */
public class contraceptiveSupplyStatusDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    contraceptiveSupplyStatusController cssController;
    private String controller_holder_key = "controller_holder";
    private String contraceptiveSupplyStatusControllerKey = "contraceptiveSupplyStatusController";

    TextView total_elco,total_new_elco,total_elco_visited,contraceptive_acceptance_rate,referred_for_contraceptive_side_effects;
    TextView oralpillshukhiCurrentMonth,oralpillAponCurrentMonth,condomNirapodCurrentMonth;
    TextView filtertitle;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public contraceptiveSupplyStatusDetailFragment() {
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
            cssController = (contraceptiveSupplyStatusController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(contraceptiveSupplyStatusControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contraceptive_supply_status_detail, container, false);


        setupViews(rootView);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE,1);
        today = cal.getTime();
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        refresh(cssController.format.format(yesterday.getTime()),cssController.format.format(today.getTime()));


        return rootView;
    }

    private void setupViews(View rootView) {
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);
        total_elco = (TextView)rootView.findViewById(R.id.elco_count);
        total_new_elco = (TextView)rootView.findViewById(R.id.newly_registered_count);
        total_elco_visited = (TextView)rootView.findViewById(R.id.total_elco_visited_count);
        contraceptive_acceptance_rate = (TextView)rootView.findViewById(R.id.contraceptive_acceptance_count);
        referred_for_contraceptive_side_effects = (TextView)rootView.findViewById(R.id.referred_for_contraceptive_side_effects_count);

        oralpillshukhiCurrentMonth = (TextView)rootView.findViewById(R.id.found_in_current_month_shukhi);
        oralpillAponCurrentMonth = (TextView)rootView.findViewById(R.id.found_in_current_month_apon);
        condomNirapodCurrentMonth = (TextView)rootView.findViewById(R.id.found_in_current_month_condom);
    }
    public void refresh(String from,String to) {
        try {
            Date fromdate = cssController.format.parse(from);
            Date todate = cssController.format.parse(to);
            filtertitle.setText(from+" to "+to);

            total_elco.setText(cssController.total_elco_Query(fromdate,todate));
            total_new_elco.setText(cssController.total_new_elco_Query(fromdate,todate));
            total_elco_visited.setText(cssController.total_elco_visited_Query(fromdate,todate));
            contraceptive_acceptance_rate.setText(cssController.contraceptive_acceptance_rate_Query(fromdate,todate));
            referred_for_contraceptive_side_effects.setText(cssController.referred_for_contraceptive_side_effects_Query(fromdate,todate));

            oralpillshukhiCurrentMonth.setText(cssController.oralpillshukhiCurrentMonthQuery(fromdate,todate));
            oralpillAponCurrentMonth.setText(cssController.oralpillAponCurrentMonthQuery(fromdate,todate));
            condomNirapodCurrentMonth.setText(cssController.condomNirapodCurrentMonthQuery(fromdate,todate));

        }catch (Exception e){
        }
    }

}

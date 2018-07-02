package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
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
public class contraceptiveSupplyStatusDetailFragment extends dashboardFragment {
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
    public static Date fromdate_forFragment = new Date(), todate_forFragment = new Date();

    TextView total_elco,total_new_elco,total_elco_visited,contraceptive_acceptance_rate,referred_for_contraceptive_side_effects;
    TextView oralpillshukhiCurrentMonth,oralpillAponCurrentMonth,condomNirapodCurrentMonth;
    TextView filtertitle;

    static String var_total_elco,var_total_new_elco,var_total_elco_visited,var_contraceptive_acceptance_rate,var_referred_for_contraceptive_side_effects;
    static String var_oralpillshukhiCurrentMonth,var_oralpillAponCurrentMonth,var_condomNirapodCurrentMonth;

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
        cal.add(Calendar.DATE, -(365*2));
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
    boolean datechanged = true;
    @Override
    public void refresh(String from,String to) {
        try {
            final Date fromdate = cssController.format.parse(from);
            final Date todate = cssController.format.parse(to);
            datechanged = true;
            if(samedate(todate_forFragment,todate)  && samedate(fromdate,fromdate_forFragment)){
                datechanged = false;
            }else{
                fromdate_forFragment = fromdate;
                todate_forFragment = todate;
            }
            filtertitle.setText(from+" to "+to);


            (new AsyncTask(){
                Snackbar snackbar;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Processing Data Please Wait", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Submit", null)
                            .setActionTextColor(Color.RED);
                    snackbar.show();
                }

                @Override
                protected Object doInBackground(Object[] objects) {
                    if(datechanged) {
                        var_total_elco = cssController.total_elco_Query(fromdate, todate);
                        var_total_new_elco = cssController.total_new_elco_Query(fromdate, todate);
                        var_total_elco_visited = cssController.total_elco_visited_Query(fromdate, todate);
                        var_contraceptive_acceptance_rate = cssController.contraceptive_acceptance_rate_Query(fromdate, todate);
                        var_referred_for_contraceptive_side_effects = cssController.referred_for_contraceptive_side_effects_Query(fromdate, todate);

                        var_oralpillshukhiCurrentMonth = cssController.oralpillshukhiCurrentMonthQuery(fromdate, todate);
                        var_oralpillAponCurrentMonth = cssController.oralpillAponCurrentMonthQuery(fromdate, todate);
                        var_condomNirapodCurrentMonth = cssController.condomNirapodCurrentMonthQuery(fromdate, todate);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    total_elco.setText(var_total_elco);
                    total_new_elco.setText(var_total_new_elco);
                    total_elco_visited.setText(var_total_elco_visited);
                    contraceptive_acceptance_rate.setText(var_contraceptive_acceptance_rate);
                    referred_for_contraceptive_side_effects.setText(var_referred_for_contraceptive_side_effects);

                    oralpillshukhiCurrentMonth.setText(var_oralpillshukhiCurrentMonth);
                    oralpillAponCurrentMonth.setText(var_oralpillAponCurrentMonth);
                    condomNirapodCurrentMonth.setText(var_condomNirapodCurrentMonth);
                    snackbar.dismiss();
                }
            }).execute();
        }catch (Exception e){
        }
    }

}

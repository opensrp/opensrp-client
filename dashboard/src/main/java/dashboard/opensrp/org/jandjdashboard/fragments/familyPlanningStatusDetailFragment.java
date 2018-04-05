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

    TextView total_elco,total_new_elco,total_elco_visited,contraceptive_acceptance_rate,referred_for_contraceptive_side_effects;
    TextView pill_old,pill_new,pill_unit_total,pill_not_using_any_method,pill_using_other_method,pill_referred_for_method,pill_referred_for_side_effects;
    TextView condom_old,condom_new,condom_unit_total,condom_not_using_any_method,condom_using_other_method,condom_referred_for_method,condom_referred_for_side_effects;
    TextView injectable_old,injectable_new,injectable_unit_total,injectable_not_using_any_method,injectable_using_other_method,injectable_referred_for_method,injectable_referred_for_side_effects;
    TextView iud_old,iud_new,iud_unit_total,iud_not_using_any_method,iud_using_other_method,iud_referred_for_method,iud_referred_for_side_effects;
    TextView implant_old,implant_new,implant_unit_total,implant_not_using_any_method,implant_using_other_method,implant_referred_for_method,implant_referred_for_side_effects;
    TextView pm_male_old,pm_male_new,pm_male_unit_total,pm_male_not_using_any_method,pm_male_using_other_method,pm_male_referred_for_method,pm_male_referred_for_side_effects;
    TextView pm_female_old,pm_female_new,pm_female_unit_total,pm_female_not_using_any_method,pm_female_using_other_method,pm_female_referred_for_method,pm_female_referred_for_side_effects;
    TextView total_old,total_new,total_unit_total,total_not_using_any_method,total_using_other_method,total_referred_for_method,total_referred_for_side_effects;

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private TextView filtertitle;

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
        cal.add(Calendar.DATE,1);
        today = cal.getTime();
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        refresh(fPSController.format.format(yesterday.getTime()),fPSController.format.format(today.getTime()));


        return rootView;
    }

    private void setupViews(View rootView) {

        total_elco = (TextView)rootView.findViewById(R.id.elco_count);
        total_new_elco = (TextView)rootView.findViewById(R.id.newly_registered_count);
        total_elco_visited = (TextView)rootView.findViewById(R.id.total_elco_visited_count);
        contraceptive_acceptance_rate = (TextView)rootView.findViewById(R.id.contraceptive_acceptance_count);
        referred_for_contraceptive_side_effects = (TextView)rootView.findViewById(R.id.referred_for_contraceptive_side_effects_count);
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

        //////////injectable textviews ////////////////////////////
        injectable_old = (TextView)rootView.findViewById(R.id.injectable_old);
        injectable_new = (TextView)rootView.findViewById(R.id.injectable_new);
        injectable_unit_total = (TextView)rootView.findViewById(R.id.injectable_unit_total);
        injectable_not_using_any_method = (TextView)rootView.findViewById(R.id.injectable_left_not_using_any_method);
        injectable_using_other_method = (TextView)rootView.findViewById(R.id.injectable_left_using_other_method);
        injectable_referred_for_method = (TextView)rootView.findViewById(R.id.injectable_referred_for_method);
        injectable_referred_for_side_effects = (TextView)rootView.findViewById(R.id.injectable_referred_for_side_effects);
        //////////////////////////////////////////////////////

        //////////iud textviews ////////////////////////////
        iud_old = (TextView)rootView.findViewById(R.id.iud_old);
        iud_new = (TextView)rootView.findViewById(R.id.iud_new);
        iud_unit_total = (TextView)rootView.findViewById(R.id.iud_unit_total);
        iud_not_using_any_method = (TextView)rootView.findViewById(R.id.iud_left_not_using_any_method);
        iud_using_other_method = (TextView)rootView.findViewById(R.id.iud_left_using_other_method);
        iud_referred_for_method = (TextView)rootView.findViewById(R.id.iud_referred_for_method);
        iud_referred_for_side_effects = (TextView)rootView.findViewById(R.id.iud_referred_for_side_effects);
        //////////////////////////////////////////////////////

        //////////implant textviews ////////////////////////////
        implant_old = (TextView)rootView.findViewById(R.id.implant_old);
        implant_new = (TextView)rootView.findViewById(R.id.implant_new);
        implant_unit_total = (TextView)rootView.findViewById(R.id.implant_unit_total);
        implant_not_using_any_method = (TextView)rootView.findViewById(R.id.implant_left_not_using_any_method);
        implant_using_other_method = (TextView)rootView.findViewById(R.id.implant_left_using_other_method);
        implant_referred_for_method = (TextView)rootView.findViewById(R.id.implant_referred_for_method);
        implant_referred_for_side_effects = (TextView)rootView.findViewById(R.id.implant_referred_for_side_effects);
        //////////////////////////////////////////////////////

        //////////pm_male textviews ////////////////////////////
        pm_male_old = (TextView)rootView.findViewById(R.id.pm_male_old);
        pm_male_new = (TextView)rootView.findViewById(R.id.pm_male_new);
        pm_male_unit_total = (TextView)rootView.findViewById(R.id.pm_male_unit_total);
        pm_male_not_using_any_method = (TextView)rootView.findViewById(R.id.pm_male_left_not_using_any_method);
        pm_male_using_other_method = (TextView)rootView.findViewById(R.id.pm_male_left_using_other_method);
        pm_male_referred_for_method = (TextView)rootView.findViewById(R.id.pm_male_referred_for_method);
        pm_male_referred_for_side_effects = (TextView)rootView.findViewById(R.id.pm_male_referred_for_side_effects);
        //////////////////////////////////////////////////////

        //////////pm_female textviews ////////////////////////////
        pm_female_old = (TextView)rootView.findViewById(R.id.pm_female_old);
        pm_female_new = (TextView)rootView.findViewById(R.id.pm_female_new);
        pm_female_unit_total = (TextView)rootView.findViewById(R.id.pm_female_unit_total);
        pm_female_not_using_any_method = (TextView)rootView.findViewById(R.id.pm_female_left_not_using_any_method);
        pm_female_using_other_method = (TextView)rootView.findViewById(R.id.pm_female_left_using_other_method);
        pm_female_referred_for_method = (TextView)rootView.findViewById(R.id.pm_female_referred_for_method);
        pm_female_referred_for_side_effects = (TextView)rootView.findViewById(R.id.pm_female_referred_for_side_effects);
        //////////////////////////////////////////////////////

        total_old = (TextView)rootView.findViewById(R.id.total_old);
        total_new = (TextView)rootView.findViewById(R.id.total_new);
        total_unit_total = (TextView)rootView.findViewById(R.id.total_unit_total);
        total_not_using_any_method = (TextView)rootView.findViewById(R.id.total_left_not_using_any_method);
        total_using_other_method = (TextView)rootView.findViewById(R.id.total_left_using_other_method);
        total_referred_for_method = (TextView)rootView.findViewById(R.id.total_referred_for_method);
        total_referred_for_side_effects = (TextView)rootView.findViewById(R.id.total_referred_for_side_effects);

        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);

    }
    public void refresh(String from, String to) {
        try {
            Date fromdate = fPSController.format.parse(from);
            Date todate = fPSController.format.parse(to);

            filtertitle.setText(from+" to "+to);

            total_elco.setText(fPSController.total_elco_Query(fromdate,todate));
            total_new_elco.setText(fPSController.total_new_elco_Query(fromdate,todate));
            total_elco_visited.setText(fPSController.total_elco_visited_Query(fromdate,todate));
            contraceptive_acceptance_rate.setText(fPSController.contraceptive_acceptance_rate_Query(fromdate,todate));
            referred_for_contraceptive_side_effects.setText(fPSController.referred_for_contraceptive_side_effects_Query(fromdate,todate));

            pill_old.setText(fPSController.pill_old_Query(fromdate,todate));
            pill_new.setText(fPSController.pill_new_Query(fromdate,todate));
            pill_unit_total.setText(fPSController.pill_unit_totalQuery(fromdate,todate));
            pill_not_using_any_method.setText(fPSController.pill_not_using_any_methodQuery(fromdate,todate));
            pill_using_other_method.setText(fPSController.pill_using_other_methodQuery(fromdate,todate));
            pill_referred_for_method.setText(fPSController.pill_referred_for_methodQuery(fromdate,todate));
            pill_referred_for_side_effects.setText(fPSController.pill_referred_for_side_effectsQuery(fromdate,todate));

            condom_old.setText(fPSController.condom_old_Query(fromdate,todate));
            condom_new.setText(fPSController.condom_new_Query(fromdate,todate));
            condom_unit_total.setText(fPSController.condom_unit_totalQuery(fromdate,todate));
            condom_not_using_any_method.setText(fPSController.condom_not_using_any_methodQuery(fromdate,todate));
            condom_using_other_method.setText(fPSController.condom_using_other_methodQuery(fromdate,todate));
            condom_referred_for_method.setText(fPSController.condom_referred_for_methodQuery(fromdate,todate));
            condom_referred_for_side_effects.setText(fPSController.condom_referred_for_side_effectsQuery(fromdate,todate));

            injectable_old.setText(fPSController.injectable_old_Query(fromdate,todate));
            injectable_new.setText(fPSController.injectable_new_Query(fromdate,todate));
            injectable_unit_total.setText(fPSController.injectable_unit_totalQuery(fromdate,todate));
            injectable_not_using_any_method.setText(fPSController.injectable_not_using_any_methodQuery(fromdate,todate));
            injectable_using_other_method.setText(fPSController.injectable_using_other_methodQuery(fromdate,todate));
            injectable_referred_for_method.setText(fPSController.injectable_referred_for_methodQuery(fromdate,todate));
            injectable_referred_for_side_effects.setText(fPSController.injectable_referred_for_side_effectsQuery(fromdate,todate));

            iud_old.setText(fPSController.iud_old_Query(fromdate,todate));
            iud_new.setText(fPSController.iud_new_Query(fromdate,todate));
            iud_unit_total.setText(fPSController.iud_unit_totalQuery(fromdate,todate));
            iud_not_using_any_method.setText(fPSController.iud_not_using_any_methodQuery(fromdate,todate));
            iud_using_other_method.setText(fPSController.iud_using_other_methodQuery(fromdate,todate));
            iud_referred_for_method.setText(fPSController.iud_referred_for_methodQuery(fromdate,todate));
            iud_referred_for_side_effects.setText(fPSController.iud_referred_for_side_effectsQuery(fromdate,todate));

            implant_old.setText(fPSController.implant_old_Query(fromdate,todate));
            implant_new.setText(fPSController.implant_new_Query(fromdate,todate));
            implant_unit_total.setText(fPSController.implant_unit_totalQuery(fromdate,todate));
            implant_not_using_any_method.setText(fPSController.implant_not_using_any_methodQuery(fromdate,todate));
            implant_using_other_method.setText(fPSController.implant_using_other_methodQuery(fromdate,todate));
            implant_referred_for_method.setText(fPSController.implant_referred_for_methodQuery(fromdate,todate));
            implant_referred_for_side_effects.setText(fPSController.implant_referred_for_side_effectsQuery(fromdate,todate));


            pm_male_old.setText(fPSController.pm_male_old_Query(fromdate,todate));
            pm_male_new.setText(fPSController.pm_male_new_Query(fromdate,todate));
            pm_male_unit_total.setText(fPSController.pm_male_unit_totalQuery(fromdate,todate));
            pm_male_not_using_any_method.setText(fPSController.pm_male_not_using_any_methodQuery(fromdate,todate));
            pm_male_using_other_method.setText(fPSController.pm_male_using_other_methodQuery(fromdate,todate));
            pm_male_referred_for_method.setText(fPSController.pm_male_referred_for_methodQuery(fromdate,todate));
            pm_male_referred_for_side_effects.setText(fPSController.pm_male_referred_for_side_effectsQuery(fromdate,todate));

            pm_female_old.setText(fPSController.pm_female_old_Query(fromdate,todate));
            pm_female_new.setText(fPSController.pm_female_new_Query(fromdate,todate));
            pm_female_unit_total.setText(fPSController.pm_female_unit_totalQuery(fromdate,todate));
            pm_female_not_using_any_method.setText(fPSController.pm_female_not_using_any_methodQuery(fromdate,todate));
            pm_female_using_other_method.setText(fPSController.pm_female_using_other_methodQuery(fromdate,todate));
            pm_female_referred_for_method.setText(fPSController.pm_female_referred_for_methodQuery(fromdate,todate));
            pm_female_referred_for_side_effects.setText(fPSController.pm_female_referred_for_side_effectsQuery(fromdate,todate));


            total_old.setText(""+(Integer.parseInt(pill_old.getText().toString())+
                    Integer.parseInt(condom_old.getText().toString())+
                    Integer.parseInt(injectable_old.getText().toString())+
                    Integer.parseInt(iud_old.getText().toString())+
                    Integer.parseInt(implant_old.getText().toString())+
                    Integer.parseInt(pm_male_old.getText().toString())+
                    Integer.parseInt(pm_female_old.getText().toString())));
            total_new.setText(""+(Integer.parseInt(pill_new.getText().toString())+
                    Integer.parseInt(condom_new.getText().toString())+
                    Integer.parseInt(injectable_new.getText().toString())+
                    Integer.parseInt(iud_new.getText().toString())+
                    Integer.parseInt(implant_new.getText().toString())+
                    Integer.parseInt(pm_male_new.getText().toString())+
                    Integer.parseInt(pm_female_new.getText().toString())));
            total_unit_total.setText(""+(Integer.parseInt(pill_unit_total.getText().toString())+
                    Integer.parseInt(condom_unit_total.getText().toString())+
                    Integer.parseInt(injectable_unit_total.getText().toString())+
                    Integer.parseInt(iud_unit_total.getText().toString())+
                    Integer.parseInt(implant_unit_total.getText().toString())+
                    Integer.parseInt(pm_male_unit_total.getText().toString())+
                    Integer.parseInt(pm_female_unit_total.getText().toString())));
            total_not_using_any_method.setText(""+(Integer.parseInt(pill_not_using_any_method.getText().toString())+
                    Integer.parseInt(condom_not_using_any_method.getText().toString())+
                    Integer.parseInt(injectable_not_using_any_method.getText().toString())+
                    Integer.parseInt(iud_not_using_any_method.getText().toString())+
                    Integer.parseInt(implant_not_using_any_method.getText().toString())+
                    Integer.parseInt(pm_male_not_using_any_method.getText().toString())+
                    Integer.parseInt(pm_female_not_using_any_method.getText().toString())));
            total_using_other_method.setText(""+(Integer.parseInt(pill_using_other_method.getText().toString())+
                    Integer.parseInt(condom_using_other_method.getText().toString())+
                    Integer.parseInt(injectable_using_other_method.getText().toString())+
                    Integer.parseInt(iud_using_other_method.getText().toString())+
                    Integer.parseInt(implant_using_other_method.getText().toString())+
                    Integer.parseInt(pm_male_using_other_method.getText().toString())+
                    Integer.parseInt(pm_female_using_other_method.getText().toString())));
            total_referred_for_method.setText("");
            total_referred_for_side_effects.setText("");



        }catch (Exception e){
        }
    }
}

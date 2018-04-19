package dashboard.opensrp.org.jandjdashboard.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.familyPlanningStatusController;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryDetailActivity;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;
import dashboard.opensrp.org.jandjdashboard.dummy.DummyContent;

/**
 * A fragment representing a single dashboardCategory detail screen.
 * This fragment is either contained in a {@link dashboardCategoryListActivity}
 * in two-pane mode (on tablets) or a {@link dashboardCategoryDetailActivity}
 * on handsets.
 */
public class familyPlanningStatusDetailFragment extends dashboardFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private String controller_holder_key = "controller_holder";
    private String familyPlanningStatusControllerKey = "familyPlanningStatusController";
    familyPlanningStatusController fPSController;
    public static Date fromdate_forFragment = new Date(), todate_forFragment = new Date();

    TextView total_elco,total_new_elco,total_elco_visited,contraceptive_acceptance_rate,referred_for_contraceptive_side_effects;
    TextView pill_old,pill_new,pill_unit_total,pill_not_using_any_method,pill_using_other_method,pill_referred_for_method,pill_referred_for_side_effects;
    TextView condom_old,condom_new,condom_unit_total,condom_not_using_any_method,condom_using_other_method,condom_referred_for_method,condom_referred_for_side_effects;
    TextView injectable_old,injectable_new,injectable_unit_total,injectable_not_using_any_method,injectable_using_other_method,injectable_referred_for_method,injectable_referred_for_side_effects;
    TextView iud_old,iud_new,iud_unit_total,iud_not_using_any_method,iud_using_other_method,iud_referred_for_method,iud_referred_for_side_effects;
    TextView implant_old,implant_new,implant_unit_total,implant_not_using_any_method,implant_using_other_method,implant_referred_for_method,implant_referred_for_side_effects;
    TextView pm_male_old,pm_male_new,pm_male_unit_total,pm_male_not_using_any_method,pm_male_using_other_method,pm_male_referred_for_method,pm_male_referred_for_side_effects;
    TextView pm_female_old,pm_female_new,pm_female_unit_total,pm_female_not_using_any_method,pm_female_using_other_method,pm_female_referred_for_method,pm_female_referred_for_side_effects;
    TextView total_old,total_new,total_unit_total,total_not_using_any_method,total_using_other_method,total_referred_for_method,total_referred_for_side_effects;

    static String var_total_elco,var_total_new_elco,var_total_elco_visited,var_contraceptive_acceptance_rate,var_referred_for_contraceptive_side_effects,
            var_pill_old,var_pill_new,var_pill_unit_total,var_pill_not_using_any_method,var_pill_using_other_method,var_pill_referred_for_method,var_pill_referred_for_side_effects,
            var_condom_old,var_condom_new,var_condom_unit_total,var_condom_not_using_any_method,var_condom_using_other_method,var_condom_referred_for_method,var_condom_referred_for_side_effects,
            var_injectable_old,var_injectable_new,var_injectable_unit_total,var_injectable_not_using_any_method,var_injectable_using_other_method,var_injectable_referred_for_method,var_injectable_referred_for_side_effects,
            var_iud_old,var_iud_new,var_iud_unit_total,var_iud_not_using_any_method,var_iud_using_other_method,var_iud_referred_for_method,var_iud_referred_for_side_effects,
            var_implant_old,var_implant_new,var_implant_unit_total,var_implant_not_using_any_method,var_implant_using_other_method,var_implant_referred_for_method,var_implant_referred_for_side_effects,
            var_pm_male_old,var_pm_male_new,var_pm_male_unit_total,var_pm_male_not_using_any_method,var_pm_male_using_other_method,var_pm_male_referred_for_method,var_pm_male_referred_for_side_effects,
            var_pm_female_old,var_pm_female_new,var_pm_female_unit_total,var_pm_female_not_using_any_method,var_pm_female_using_other_method,var_pm_female_referred_for_method,var_pm_female_referred_for_side_effects,
            var_total_old,var_total_new,var_total_unit_total,var_total_not_using_any_method,var_total_using_other_method,var_total_referred_for_method,var_total_referred_for_side_effects;

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
        cal.add(Calendar.DATE, -(365*2));
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
    boolean datechanged = true;

    @Override
    public void refresh(String from, String to) {
        try {
            final Date fromdate = fPSController.format.parse(from);
            final Date todate = fPSController.format.parse(to);
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
                        var_total_elco = fPSController.total_elco_Query(fromdate, todate);
                        var_total_new_elco = fPSController.total_new_elco_Query(fromdate, todate);
                        var_total_elco_visited = fPSController.total_elco_visited_Query(fromdate, todate);
                        var_contraceptive_acceptance_rate = fPSController.contraceptive_acceptance_rate_Query(fromdate, todate);
                        var_referred_for_contraceptive_side_effects = fPSController.referred_for_contraceptive_side_effects_Query(fromdate, todate);

                        var_pill_old = fPSController.pill_old_Query(fromdate, todate);
                        var_pill_new = fPSController.pill_new_Query(fromdate, todate);
                        var_pill_unit_total = fPSController.pill_unit_totalQuery(fromdate, todate);
                        var_pill_not_using_any_method = fPSController.pill_not_using_any_methodQuery(fromdate, todate);
                        var_pill_using_other_method = fPSController.pill_using_other_methodQuery(fromdate, todate);
                        var_pill_referred_for_method = fPSController.pill_referred_for_methodQuery(fromdate, todate);
                        var_pill_referred_for_side_effects = fPSController.pill_referred_for_side_effectsQuery(fromdate, todate);

                        var_condom_old = fPSController.condom_old_Query(fromdate, todate);
                        var_condom_new = fPSController.condom_new_Query(fromdate, todate);
                        var_condom_unit_total = fPSController.condom_unit_totalQuery(fromdate, todate);
                        var_condom_not_using_any_method = fPSController.condom_not_using_any_methodQuery(fromdate, todate);
                        var_condom_using_other_method = fPSController.condom_using_other_methodQuery(fromdate, todate);
                        var_condom_referred_for_method = fPSController.condom_referred_for_methodQuery(fromdate, todate);
                        var_condom_referred_for_side_effects = fPSController.condom_referred_for_side_effectsQuery(fromdate, todate);

                        var_injectable_old = fPSController.injectable_old_Query(fromdate, todate);
                        var_injectable_new = fPSController.injectable_new_Query(fromdate, todate);
                        var_injectable_unit_total = fPSController.injectable_unit_totalQuery(fromdate, todate);
                        var_injectable_not_using_any_method = fPSController.injectable_not_using_any_methodQuery(fromdate, todate);
                        var_injectable_using_other_method = fPSController.injectable_using_other_methodQuery(fromdate, todate);
                        var_injectable_referred_for_method = fPSController.injectable_referred_for_methodQuery(fromdate, todate);
                        var_injectable_referred_for_side_effects = fPSController.injectable_referred_for_side_effectsQuery(fromdate, todate);

                        var_iud_old = fPSController.iud_old_Query(fromdate, todate);
                        var_iud_new = fPSController.iud_new_Query(fromdate, todate);
                        var_iud_unit_total = fPSController.iud_unit_totalQuery(fromdate, todate);
                        var_iud_not_using_any_method = fPSController.iud_not_using_any_methodQuery(fromdate, todate);
                        var_iud_using_other_method = fPSController.iud_using_other_methodQuery(fromdate, todate);
                        var_iud_referred_for_method = fPSController.iud_referred_for_methodQuery(fromdate, todate);
                        var_iud_referred_for_side_effects = fPSController.iud_referred_for_side_effectsQuery(fromdate, todate);

                        var_implant_old = fPSController.implant_old_Query(fromdate, todate);
                        var_implant_new = fPSController.implant_new_Query(fromdate, todate);
                        var_implant_unit_total = fPSController.implant_unit_totalQuery(fromdate, todate);
                        var_implant_not_using_any_method = fPSController.implant_not_using_any_methodQuery(fromdate, todate);
                        var_implant_using_other_method = fPSController.implant_using_other_methodQuery(fromdate, todate);
                        var_implant_referred_for_method = fPSController.implant_referred_for_methodQuery(fromdate, todate);
                        var_implant_referred_for_side_effects = fPSController.implant_referred_for_side_effectsQuery(fromdate, todate);


                        var_pm_male_old = fPSController.pm_male_old_Query(fromdate, todate);
                        var_pm_male_new = fPSController.pm_male_new_Query(fromdate, todate);
                        var_pm_male_unit_total = fPSController.pm_male_unit_totalQuery(fromdate, todate);
                        var_pm_male_not_using_any_method = fPSController.pm_male_not_using_any_methodQuery(fromdate, todate);
                        var_pm_male_using_other_method = fPSController.pm_male_using_other_methodQuery(fromdate, todate);
                        var_pm_male_referred_for_method = fPSController.pm_male_referred_for_methodQuery(fromdate, todate);
                        var_pm_male_referred_for_side_effects = fPSController.pm_male_referred_for_side_effectsQuery(fromdate, todate);

                        var_pm_female_old = fPSController.pm_female_old_Query(fromdate, todate);
                        var_pm_female_new = fPSController.pm_female_new_Query(fromdate, todate);
                        var_pm_female_unit_total = fPSController.pm_female_unit_totalQuery(fromdate, todate);
                        var_pm_female_not_using_any_method = fPSController.pm_female_not_using_any_methodQuery(fromdate, todate);
                        var_pm_female_using_other_method = fPSController.pm_female_using_other_methodQuery(fromdate, todate);
                        var_pm_female_referred_for_method = fPSController.pm_female_referred_for_methodQuery(fromdate, todate);
                        var_pm_female_referred_for_side_effects = fPSController.pm_female_referred_for_side_effectsQuery(fromdate, todate);

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

                    pill_old.setText(var_pill_old);
                    pill_new.setText(var_pill_new);
                    pill_unit_total.setText(var_pill_unit_total);
                    pill_not_using_any_method.setText(var_pill_not_using_any_method);
                    pill_using_other_method.setText(var_pill_using_other_method);
                    pill_referred_for_method.setText(var_pill_referred_for_method);
                    pill_referred_for_side_effects.setText(var_pill_referred_for_side_effects);

                    condom_old.setText(var_condom_old);
                    condom_new.setText(var_condom_new);
                    condom_unit_total.setText(var_condom_unit_total);
                    condom_not_using_any_method.setText(var_condom_not_using_any_method);
                    condom_using_other_method.setText(var_condom_using_other_method);
                    condom_referred_for_method.setText(var_condom_referred_for_method);
                    condom_referred_for_side_effects.setText(var_condom_referred_for_side_effects);

                    injectable_old.setText(var_injectable_old);
                    injectable_new.setText(var_injectable_new);
                    injectable_unit_total.setText(var_injectable_unit_total);
                    injectable_not_using_any_method.setText(var_injectable_not_using_any_method);
                    injectable_using_other_method.setText(var_injectable_using_other_method);
                    injectable_referred_for_method.setText(var_injectable_referred_for_method);
                    injectable_referred_for_side_effects.setText(var_injectable_referred_for_side_effects);

                    iud_old.setText(var_iud_old);
                    iud_new.setText(var_iud_new);
                    iud_unit_total.setText(var_iud_unit_total);
                    iud_not_using_any_method.setText(var_iud_not_using_any_method);
                    iud_using_other_method.setText(var_iud_using_other_method);
                    iud_referred_for_method.setText(var_iud_referred_for_method);
                    iud_referred_for_side_effects.setText(var_iud_referred_for_side_effects);

                    implant_old.setText(var_implant_old);
                    implant_new.setText(var_implant_new);
                    implant_unit_total.setText(var_implant_unit_total);
                    implant_not_using_any_method.setText(var_implant_not_using_any_method);
                    implant_using_other_method.setText(var_implant_using_other_method);
                    implant_referred_for_method.setText(var_implant_referred_for_method);
                    implant_referred_for_side_effects.setText(var_implant_referred_for_side_effects);


                    pm_male_old.setText(var_pm_male_old);
                    pm_male_new.setText(var_pm_male_new);
                    pm_male_unit_total.setText(var_pm_male_unit_total);
                    pm_male_not_using_any_method.setText(var_pm_male_not_using_any_method);
                    pm_male_using_other_method.setText(var_pm_male_using_other_method);
                    pm_male_referred_for_method.setText(var_pm_male_referred_for_method);
                    pm_male_referred_for_side_effects.setText(var_pm_male_referred_for_side_effects);

                    pm_female_old.setText(var_pm_female_old);
                    pm_female_new.setText(var_pm_female_new);
                    pm_female_unit_total.setText(var_pm_female_unit_total);
                    pm_female_not_using_any_method.setText(var_pm_female_not_using_any_method);
                    pm_female_using_other_method.setText(var_pm_female_using_other_method);
                    pm_female_referred_for_method.setText(var_pm_female_referred_for_method);
                    pm_female_referred_for_side_effects.setText(var_pm_female_referred_for_side_effects);


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
                    snackbar.dismiss();
                }
            }).execute();

        }catch (Exception e){
        }
    }

}

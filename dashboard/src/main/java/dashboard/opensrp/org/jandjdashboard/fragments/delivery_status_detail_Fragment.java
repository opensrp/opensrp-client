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
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.deliveryStatusController;
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
public class delivery_status_detail_Fragment extends dashboardFragment {
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

    deliveryStatusController dsController;
    public static Date fromdate_forFragment = new Date(), todate_forFragment = new Date();

    private String controller_holder_key = "controller_holder";
    private String deliveryStatusControllerKey = "deliveryStatusController";
    TextView filtertitle;
    TextView unittotalnumberoflivebirth,unitstotalnumberofdeath,totalnumberoflivebirth,totalnumberofdeath;
    TextView totalNumberOfLiveBirth,numberofNewBornswithLowBirthWeight,numberofImmatureBirth,
            numberofStillBirth,numberofDeathUnder7Days,numberofDeathBetween8to28Days,
            numberofDeathBetween29daysto1year,numberofTotalDeath,numberofChildDeathBetween1to5year,
            numberofMotherDeath,numberofOtherDeath;

    static String var_unittotalnumberoflivebirth,var_unitstotalnumberofdeath,var_totalnumberoflivebirth,var_totalnumberofdeath;

    static String var_totalNumberOfLiveBirth,
            var_numberofNewBornswithLowBirthWeight,
            var_numberofImmatureBirth,
            var_numberofStillBirth,
            var_numberofDeathUnder7Days,
            var_numberofDeathBetween8to28Days,
            var_numberofDeathBetween29daysto1year,
            var_numberofTotalDeath,
            var_numberofChildDeathBetween1to5year,
            var_numberofMotherDeath,
            var_numberofOtherDeath;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public delivery_status_detail_Fragment() {
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
            dsController = (deliveryStatusController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(deliveryStatusControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.delivery_status_detail, container, false);
        setupviews(rootView);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -(365*2));
        Date yesterday = cal.getTime();
        refresh(dsController.format.format(yesterday.getTime()),dsController.format.format(today.getTime()));

        return rootView;
    }

    private void setupviews(View rootView) {
        unittotalnumberoflivebirth = (TextView)rootView.findViewById(R.id.unit_total_livebirth);
        unitstotalnumberofdeath= (TextView)rootView.findViewById(R.id.unit_total_death);
        totalnumberoflivebirth = (TextView)rootView.findViewById(R.id.total_livebirth);
        totalnumberofdeath= (TextView)rootView.findViewById(R.id.total_death);
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);

        totalNumberOfLiveBirth = (TextView)rootView.findViewById(R.id.total_live_birth_info);
        numberofNewBornswithLowBirthWeight = (TextView)rootView.findViewById(R.id.low_birthweight_info);
        numberofImmatureBirth = (TextView)rootView.findViewById(R.id.immature_info);
        numberofStillBirth =  (TextView)rootView.findViewById(R.id.still_birth_info);
        numberofDeathUnder7Days = (TextView)rootView.findViewById(R.id.zero_to_seven_days_info);
        numberofDeathBetween8to28Days =  (TextView)rootView.findViewById(R.id.eight_to28days_info);
        numberofDeathBetween29daysto1year =  (TextView)rootView.findViewById(R.id.twentyninedays_to_year_info);
        numberofTotalDeath =  (TextView)rootView.findViewById(R.id.total_info);
        numberofChildDeathBetween1to5year =  (TextView)rootView.findViewById(R.id.number_of_child_death_1_to_5_info);
        numberofMotherDeath = (TextView)rootView.findViewById(R.id.number_of_mother_death_info);
        numberofOtherDeath =  (TextView)rootView.findViewById(R.id.number_of_other_death_info);
    }
    boolean datechanged = true;

    @Override
    public void refresh(String from,String to) {
        try {
            final Date fromdate = dsController.format.parse(from);
            final Date todate = dsController.format.parse(to);

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
                        var_unittotalnumberoflivebirth = dsController.numberofLiveBirth(fromdate, todate);
                        var_unitstotalnumberofdeath = dsController.numberofTotalDeath(fromdate, todate);
                        var_totalnumberoflivebirth = dsController.totalnumberofLiveBirth(fromdate, todate);
                        var_totalnumberofdeath = dsController.overallnumberofTotalDeath(fromdate, todate);

                        var_totalNumberOfLiveBirth = dsController.numberofLiveBirth(fromdate, todate);
                        var_numberofNewBornswithLowBirthWeight = dsController.numberofNewBornswithLowBirthWeight(fromdate, todate);
                        var_numberofImmatureBirth = dsController.numberofImmatureBirth(fromdate, todate);
                        var_numberofStillBirth = dsController.numberofStillBirth(fromdate, todate);
                        var_numberofDeathUnder7Days = dsController.numberofDeathUnder7Days(fromdate, todate);
                        var_numberofDeathBetween8to28Days = dsController.numberofDeathBetween8to28Days(fromdate, todate);
                        var_numberofDeathBetween29daysto1year = dsController.numberofDeathBetween29daysto1year(fromdate, todate);
                        var_numberofTotalDeath = dsController.numberofTotalDeath(fromdate, todate);
                        var_numberofChildDeathBetween1to5year = dsController.numberofChildDeathBetween1to5year(fromdate, todate);
                        var_numberofMotherDeath = dsController.numberofMotherDeath(fromdate, todate);
                        var_numberofOtherDeath = dsController.numberofOtherDeath(fromdate, todate);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    unittotalnumberoflivebirth.setText(var_unittotalnumberoflivebirth);
                    unitstotalnumberofdeath.setText(var_unitstotalnumberofdeath);
                    totalnumberoflivebirth.setText(var_totalnumberoflivebirth);
                    totalnumberofdeath.setText(var_totalnumberofdeath);


                    totalNumberOfLiveBirth.setText(var_totalNumberOfLiveBirth);
                    numberofNewBornswithLowBirthWeight.setText(var_numberofNewBornswithLowBirthWeight);
                    numberofImmatureBirth.setText(var_numberofImmatureBirth);
                    numberofStillBirth.setText(var_numberofStillBirth);
                    numberofDeathUnder7Days.setText(var_numberofDeathUnder7Days);
                    numberofDeathBetween8to28Days.setText(var_numberofDeathBetween8to28Days);
                    numberofDeathBetween29daysto1year.setText(var_numberofDeathBetween29daysto1year);
                    numberofTotalDeath.setText(var_numberofTotalDeath);
                    numberofChildDeathBetween1to5year.setText(var_numberofChildDeathBetween1to5year);
                    numberofMotherDeath.setText(var_numberofMotherDeath);
                    numberofOtherDeath.setText(var_numberofOtherDeath);
                    snackbar.dismiss();
                }
            }).execute();

        }catch (Exception e){

        }

    }

}

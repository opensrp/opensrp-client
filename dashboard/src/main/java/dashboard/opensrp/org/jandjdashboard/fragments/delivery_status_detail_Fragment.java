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
public class delivery_status_detail_Fragment extends Fragment {
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
    private String controller_holder_key = "controller_holder";
    private String deliveryStatusControllerKey = "deliveryStatusController";
    TextView filtertitle;
    TextView unittotalnumberoflivebirth,unitstotalnumberofdeath,totalnumberoflivebirth,totalnumberofdeath;
    TextView totalNumberOfLiveBirth,numberofNewBornswithLowBirthWeight,numberofImmatureBirth,
            numberofStillBirth,numberofDeathUnder7Days,numberofDeathBetween8to28Days,
            numberofDeathBetween29daysto1year,numberofTotalDeath,numberofChildDeathBetween1to5year,
            numberofMotherDeath,numberofOtherDeath;
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
        cal.add(Calendar.DATE, -(365*10));
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

    public void refresh(String from,String to) {
        try {
            Date fromdate = dsController.format.parse(from);
            Date todate = dsController.format.parse(to);

            filtertitle.setText(from+" to "+to);


            unittotalnumberoflivebirth.setText(dsController.numberofLiveBirth(fromdate,todate));
            unitstotalnumberofdeath.setText(dsController.numberofTotalDeath(fromdate,todate));
            totalnumberoflivebirth.setText(dsController.totalnumberofLiveBirth(fromdate,todate));
            totalnumberofdeath.setText(dsController.overallnumberofTotalDeath(fromdate,todate));


            totalNumberOfLiveBirth.setText(dsController.numberofLiveBirth(fromdate,todate));
            numberofNewBornswithLowBirthWeight.setText(dsController.numberofNewBornswithLowBirthWeight(fromdate,todate));
            numberofImmatureBirth.setText(dsController.numberofImmatureBirth(fromdate,todate));
            numberofStillBirth.setText(dsController.numberofStillBirth(fromdate,todate));
            numberofDeathUnder7Days.setText(dsController.numberofDeathUnder7Days(fromdate,todate));
            numberofDeathBetween8to28Days.setText(dsController.numberofDeathBetween8to28Days(fromdate,todate));
            numberofDeathBetween29daysto1year.setText(dsController.numberofDeathBetween29daysto1year(fromdate,todate));
            numberofTotalDeath.setText(dsController.numberofTotalDeath(fromdate,todate));
            numberofChildDeathBetween1to5year.setText(dsController.numberofChildDeathBetween1to5year(fromdate,todate));
            numberofMotherDeath.setText(dsController.numberofMotherDeath(fromdate,todate));
            numberofOtherDeath.setText(dsController.numberofOtherDeath(fromdate,todate));
        }catch (Exception e){

        }

    }

}

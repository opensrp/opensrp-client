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
import dashboard.opensrp.org.jandjdashboard.controller.nutritionDetailController;
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
public class nutritionDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private String controller_holder_key = "controller_holder";
    private String nutritionDetailControllerKey = "nutritionDetailController";
    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    TextView unittotalnumberoflivebirth,unitstotalnumberofdeath,totalnumberoflivebirth,totalnumberofdeath;
    TextView iron_and_folic_acid_pregnant_woman_info,
            iron_and_folic_acid_mother_info,
            distributed_iron_and_folic_acid_pregnant_woman_info,
            distributed_iron_and_folic_acid_mother_info,
            counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info,
            counselling_on_breastfeeding_and_complimentary_food_mother_info,
            counselling_on_feeding_mnp_to_children_pregnant_woman_info,
            counselling_on_feeding_mnp_to_children_mother_info,
            feed_colostrum_milk_zero_to_six_month_info,
            feed_colostrum_milk_six_to_24_month_info,
            feed_colostrum_milk_twentyfour_to_fifty_month_info,
            breastfeeding_up_to_6_months_zero_to_six_months_info,
            breastfeeding_up_to_6_months_six_to_twentyfour_months_info,
            breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info,
            providing_extra_complimentary_food_zero_to_6_months_info,
            providing_extra_complimentary_food_6_to_24_months_info,
            providing_extra_complimentary_food_24_to_50_months_info,
            received_multiple_mnr_0_to_6months_info,
            received_multiple_mnr_6_to_24months_info,
            received_multiple_mnr_24_to_50months_info;

    nutritionDetailController ndController;
    private TextView filtertitle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public nutritionDetailFragment() {
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
            ndController = (nutritionDetailController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(nutritionDetailControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nutrition_detail, container, false);
        setupviews(rootView);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        refresh(ndController.format.format(yesterday.getTime()),ndController.format.format(today.getTime()));
        return rootView;
    }

    public void refresh(String from, String to) {
        try {
            Date fromdate = ndController.format.parse(from);
            Date todate = ndController.format.parse(to);
            filtertitle.setText(from+" to "+to);

            unittotalnumberoflivebirth.setText(ndController.numberofLiveBirth(fromdate,todate));
            unitstotalnumberofdeath.setText(ndController.numberofTotalDeath(fromdate,todate));
            totalnumberoflivebirth.setText(ndController.totalnumberofLiveBirth(fromdate,todate));
            totalnumberofdeath.setText(ndController.overallnumberofTotalDeath(fromdate,todate));



            iron_and_folic_acid_pregnant_woman_info.setText(ndController.iron_and_folic_acid_pregnant_woman_info(fromdate,todate));
            iron_and_folic_acid_mother_info.setText(ndController.iron_and_folic_acid_mother_info(fromdate,todate));
            distributed_iron_and_folic_acid_pregnant_woman_info.setText(ndController.distributed_iron_and_folic_acid_pregnant_woman_info(fromdate,todate));
            distributed_iron_and_folic_acid_mother_info.setText(ndController.distributed_iron_and_folic_acid_mother_info(fromdate,todate));
            counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info.setText(ndController.counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info(fromdate,todate));
            counselling_on_breastfeeding_and_complimentary_food_mother_info.setText(ndController.counselling_on_breastfeeding_and_complimentary_food_mother_info(fromdate,todate));
            counselling_on_feeding_mnp_to_children_pregnant_woman_info.setText(ndController.counselling_on_feeding_mnp_to_children_pregnant_woman_info(fromdate,todate));
            counselling_on_feeding_mnp_to_children_mother_info.setText(ndController.counselling_on_feeding_mnp_to_children_mother_info(fromdate,todate));
            feed_colostrum_milk_zero_to_six_month_info.setText(ndController.feed_colostrum_milk_zero_to_six_month_info(fromdate,todate));
            feed_colostrum_milk_six_to_24_month_info.setText(ndController.feed_colostrum_milk_six_to_24_month_info(fromdate,todate));
            feed_colostrum_milk_twentyfour_to_fifty_month_info.setText(ndController.feed_colostrum_milk_twentyfour_to_fifty_month_info(fromdate,todate));
            breastfeeding_up_to_6_months_zero_to_six_months_info.setText(ndController.breastfeeding_up_to_6_months_zero_to_six_months_info(fromdate,todate));
            breastfeeding_up_to_6_months_six_to_twentyfour_months_info.setText(ndController.breastfeeding_up_to_6_months_six_to_twentyfour_months_info(fromdate,todate));
            breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info.setText(ndController.breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info(fromdate,todate));
            providing_extra_complimentary_food_zero_to_6_months_info.setText(ndController.providing_extra_complimentary_food_zero_to_6_months_info(fromdate,todate));
            providing_extra_complimentary_food_6_to_24_months_info.setText(ndController.providing_extra_complimentary_food_6_to_24_months_info(fromdate,todate));
            providing_extra_complimentary_food_24_to_50_months_info.setText(ndController.providing_extra_complimentary_food_24_to_50_months_info(fromdate,todate));
            received_multiple_mnr_0_to_6months_info.setText(ndController.received_multiple_mnr_0_to_6months_info(fromdate,todate));
            received_multiple_mnr_6_to_24months_info.setText(ndController.received_multiple_mnr_6_to_24months_info(fromdate,todate));
            received_multiple_mnr_24_to_50months_info.setText(ndController.received_multiple_mnr_24_to_50months_info(fromdate,todate));
        }catch (Exception e){}
    }

    private void setupviews(View rootView) {
        unittotalnumberoflivebirth = (TextView)rootView.findViewById(R.id.unit_total_livebirth);
        unitstotalnumberofdeath= (TextView)rootView.findViewById(R.id.unit_total_death);
        totalnumberoflivebirth = (TextView)rootView.findViewById(R.id.total_livebirth);
        totalnumberofdeath= (TextView)rootView.findViewById(R.id.total_death);
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);




        iron_and_folic_acid_pregnant_woman_info = (TextView)rootView.findViewById(R.id.iron_and_folic_acid_pregnant_woman_info);
        iron_and_folic_acid_mother_info = (TextView)rootView.findViewById(R.id.iron_and_folic_acid_mother_info);
        distributed_iron_and_folic_acid_pregnant_woman_info = (TextView)rootView.findViewById(R.id.distributed_iron_and_folic_acid_pregnant_woman_info);
        distributed_iron_and_folic_acid_mother_info = (TextView)rootView.findViewById(R.id.distributed_iron_and_folic_acid_mother_info);
        counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info = (TextView)rootView.findViewById(R.id.counselling_on_breastfeeding_and_complimentary_food_pregnant_woman_info);
        counselling_on_breastfeeding_and_complimentary_food_mother_info = (TextView)rootView.findViewById(R.id.counselling_on_breastfeeding_and_complimentary_food_mother_info);
        counselling_on_feeding_mnp_to_children_pregnant_woman_info = (TextView)rootView.findViewById(R.id.counselling_on_feeding_mnp_to_children_pregnant_woman_info);
        counselling_on_feeding_mnp_to_children_mother_info = (TextView)rootView.findViewById(R.id.counselling_on_feeding_mnp_to_children_mother_info);
        feed_colostrum_milk_zero_to_six_month_info = (TextView)rootView.findViewById(R.id.feed_colostrum_milk_zero_to_six_month_info);
        feed_colostrum_milk_six_to_24_month_info = (TextView)rootView.findViewById(R.id.feed_colostrum_milk_six_to_24_month_info);
        feed_colostrum_milk_twentyfour_to_fifty_month_info = (TextView)rootView.findViewById(R.id.feed_colostrum_milk_twentyfour_to_fifty_month_info);
        breastfeeding_up_to_6_months_zero_to_six_months_info = (TextView)rootView.findViewById(R.id.breastfeeding_up_to_6_months_zero_to_six_months_info);
        breastfeeding_up_to_6_months_six_to_twentyfour_months_info = (TextView)rootView.findViewById(R.id.breastfeeding_up_to_6_months_six_to_twentyfour_months_info);
        breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info = (TextView)rootView.findViewById(R.id.breastfeeding_up_to_6_months_twentyfour_to_fifty_months_info);
        providing_extra_complimentary_food_zero_to_6_months_info = (TextView)rootView.findViewById(R.id.providing_extra_complimentary_food_zero_to_6_months_info);
        providing_extra_complimentary_food_6_to_24_months_info = (TextView)rootView.findViewById(R.id.providing_extra_complimentary_food_6_to_24_months_info);
        providing_extra_complimentary_food_24_to_50_months_info = (TextView)rootView.findViewById(R.id.providing_extra_complimentary_food_24_to_50_months_info);
        received_multiple_mnr_0_to_6months_info = (TextView)rootView.findViewById(R.id.received_multiple_mnr_0_to_6months_info);
        received_multiple_mnr_6_to_24months_info = (TextView)rootView.findViewById(R.id.received_multiple_mnr_6_to_24months_info);
        received_multiple_mnr_24_to_50months_info = (TextView)rootView.findViewById(R.id.received_multiple_mnr_24_to_50months_info);
    }

}

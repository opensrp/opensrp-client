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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dashboard.opensrp.org.jandjdashboard.R;
import dashboard.opensrp.org.jandjdashboard.adapter.scheduleCardAdapter;
import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.reproductiveHealthServiceController;
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
public class reproductive_health_service_Fragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    reproductiveHealthServiceController rhsController;
    private String controller_holder_key = "controller_holder";
    private String reproductiveHealthServiceControllerKey = "reproductiveHealthServiceController";
    TextView anc1Service,anc1Info,anc2Service,anc2Info,anc3Service,anc3Info,anc4Service,anc4Info,
            pnc1Service,pnc1Info,pnc2Service,pnc2Info,pnc3Service,pnc3Info,encc1Service,encc1Info,
            encc2Service,encc2Info,encc3Service,encc3Info,tt1info,tt1service,tt2info,tt2service,tt3info,tt3service
            ,tt4info,tt4service,tt5info,tt5service,ecpreceptorservice,ecpreceptorinfo;
    private TextView filtertitle;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public reproductive_health_service_Fragment() {
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
            rhsController = (reproductiveHealthServiceController) ((controllerHolders)getArguments().getSerializable(controller_holder_key)).getControllersHashMap().get(reproductiveHealthServiceControllerKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reproductive_health_service, container, false);
        setupviews(rootView);
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -(365*10));
        Date yesterday = cal.getTime();
        refresh(rhsController.format.format(yesterday.getTime()),rhsController.format.format(today.getTime()));
        return rootView;
    }

    private void setupviews(View rootView) {
        anc1Info = (TextView)rootView.findViewById(R.id.anc_visit_1_info);
        anc1Service = (TextView)rootView.findViewById(R.id.anc_visit_1_service);
        anc2Info = (TextView)rootView.findViewById(R.id.anc_visit_2_info);
        anc2Service = (TextView)rootView.findViewById(R.id.anc_visit_2_service);
        anc3Info = (TextView)rootView.findViewById(R.id.anc_visit_3_info);
        anc3Service = (TextView)rootView.findViewById(R.id.anc_visit_3_service);
        anc4Info = (TextView)rootView.findViewById(R.id.anc_visit_4_info);
        anc4Service = (TextView)rootView.findViewById(R.id.anc_visit_4_service);

        pnc1Info = (TextView)rootView.findViewById(R.id.pnc_current_visit_one);
        pnc1Service = (TextView)rootView.findViewById(R.id.pnc_service_visit_one);
        pnc2Info = (TextView)rootView.findViewById(R.id.pnc_current_visit_two);
        pnc2Service = (TextView)rootView.findViewById(R.id.pnc_service_visit_two);
        pnc3Info = (TextView)rootView.findViewById(R.id.pnc_current_visit_three);
        pnc3Service = (TextView)rootView.findViewById(R.id.pnc_service_visit_three);

        encc1Info = (TextView)rootView.findViewById(R.id.encc_current_visit_one);
        encc1Service = (TextView)rootView.findViewById(R.id.encc_service_visit_one);
        encc2Info = (TextView)rootView.findViewById(R.id.encc_current_visit_two);
        encc2Service = (TextView)rootView.findViewById(R.id.encc_service_visit_two);
        encc3Info = (TextView)rootView.findViewById(R.id.encc_current_visit_three);
        encc3Service = (TextView)rootView.findViewById(R.id.encc_service_visit_three);

        tt1info = (TextView)rootView.findViewById(R.id.tt_received_1_info);
        tt1service = (TextView)rootView.findViewById(R.id.tt_received_1_service);
        tt2info = (TextView)rootView.findViewById(R.id.tt_received_2_info);
        tt2service = (TextView)rootView.findViewById(R.id.tt_received_2_service);
        tt3info = (TextView)rootView.findViewById(R.id.tt_received_3_info);
        tt3service = (TextView)rootView.findViewById(R.id.tt_received_3_service);
        tt4info = (TextView)rootView.findViewById(R.id.tt_received_4_info);
        tt4service = (TextView)rootView.findViewById(R.id.tt_received_4_service);
        tt5info = (TextView)rootView.findViewById(R.id.tt_received_5_info);
        tt5service = (TextView)rootView.findViewById(R.id.tt_received_5_service);

        ecpreceptorinfo = (TextView)rootView.findViewById(R.id.ecp_receptor_info);
        ecpreceptorservice = (TextView)rootView.findViewById(R.id.ecp_receptor_service);
        filtertitle = (TextView)rootView.findViewById(R.id.filtertitle);

    }

    public void refresh(String from,String to) {
        try {
        Date fromdate = rhsController.format.parse(from);
        Date todate =  rhsController.format.parse(to);

            filtertitle.setText(from+" to "+to);

        anc1Service.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc1visit"));
        anc1Info.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc1visit"));
        anc2Service.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc2visit"));
        anc2Info.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc2visit"));
        anc3Service.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc3visit"));
        anc3Info.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc3visit"));
        anc4Service.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc4visit"));
        anc4Info.setText(rhsController.ancVisitQuery(fromdate,todate).get("anc4visit"));

        pnc1Service.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc1visit"));
        pnc1Info.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc1visit"));
        pnc2Service.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc2visit"));
        pnc2Info.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc2visit"));
        pnc3Service.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc3visit"));
        pnc3Info.setText(rhsController.pncVisitQuery(fromdate,todate).get("pnc3visit"));

        encc1Service.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc1visit"));
        encc1Info.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc1visit"));
        encc2Service.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc2visit"));
        encc2Info.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc2visit"));
        encc3Service.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc3visit"));
        encc3Info.setText(rhsController.neonatalVisitQuery(fromdate,todate).get("encc3visit"));

        tt1service.setText(rhsController.ttquery(fromdate,todate).get("tt1given"));
        tt1info.setText(rhsController.ttquery(fromdate,todate).get("tt1given"));
        tt2service.setText(rhsController.ttquery(fromdate,todate).get("tt2given"));
        tt2info.setText(rhsController.ttquery(fromdate,todate).get("tt2given"));
        tt3service.setText(rhsController.ttquery(fromdate,todate).get("tt3given"));
        tt3info.setText(rhsController.ttquery(fromdate,todate).get("tt3given"));
        tt4service.setText(rhsController.ttquery(fromdate,todate).get("tt4given"));
        tt4info.setText(rhsController.ttquery(fromdate,todate).get("tt4given"));
        tt5service.setText(rhsController.ttquery(fromdate,todate).get("tt5given"));
        tt5info.setText(rhsController.ttquery(fromdate,todate).get("tt5given"));

        ecpreceptorinfo.setText(rhsController.ecpReceptors(fromdate,todate));
        ecpreceptorservice.setText(rhsController.ecpReceptors(fromdate,todate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}

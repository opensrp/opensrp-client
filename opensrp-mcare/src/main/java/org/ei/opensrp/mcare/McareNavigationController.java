package org.ei.opensrp.mcare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import org.ei.opensrp.mcare.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.mcare.child.mCareChildSmartRegisterActivity;
import org.ei.opensrp.mcare.dashboard.controllers.anc_pnc_encc_reminder_StatusControllerForDashBoardModule;
import org.ei.opensrp.mcare.dashboard.controllers.contraceptiveSupplyStatusControllerForDashBoardModule;
import org.ei.opensrp.mcare.dashboard.controllers.deliveryStatusControllerForDashBoardModule;
import org.ei.opensrp.mcare.dashboard.controllers.nutritionDetailControllerForDashBoardModule;
import org.ei.opensrp.mcare.dashboard.controllers.reproductiveHealthServiceControllerForDashBoardModule;
import org.ei.opensrp.mcare.dashboard.controllers.upcomingScheduleStatusControllerForDashBoardModule;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.mcare.household.HouseHoldSmartRegisterActivity;
import org.ei.opensrp.mcare.household.tutorial.tutorialCircleViewFlow;
import org.ei.opensrp.mcare.pnc.mCarePNCSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeANCSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativePNCSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;


import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.controllerHolders;
import dashboard.opensrp.org.jandjdashboard.controller.dashboardControllers;
import dashboard.opensrp.org.jandjdashboard.dashboardCategoryListActivity;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class McareNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public McareNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }
    @Override
    public void startECSmartRegistry() {

        activity.startActivity(new Intent(activity, HouseHoldSmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
            activity.startActivity(new Intent(activity, tutorialCircleViewFlow.class));
        }

    }
    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, ElcoSmartRegisterActivity.class));
    }
    @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, mCareANCSmartRegisterActivity.class));
    }
    @Override
    public void startReports() {
        Intent i = new Intent(activity, dashboardCategoryListActivity.class);
        i.putExtra("controller_holder", initializeControllersForDashboard());
        activity.startActivity(i);
    }

    public void startPNCSmartRegistry() {
        activity.startActivity(new Intent(activity, mCarePNCSmartRegisterActivity.class));
    }
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, mCareChildSmartRegisterActivity.class));
    }
    private controllerHolders initializeControllersForDashboard() {
        controllerHolders controllerHolder = new controllerHolders();
        upcomingScheduleStatusControllerForDashBoardModule usscontrollerForDashBoardModule = new upcomingScheduleStatusControllerForDashBoardModule();
        anc_pnc_encc_reminder_StatusControllerForDashBoardModule ancpnc_encc_rsForDashBoardModule = new anc_pnc_encc_reminder_StatusControllerForDashBoardModule();
        reproductiveHealthServiceControllerForDashBoardModule ReproductiveHealthServiceControllerForDashBoardModule = new reproductiveHealthServiceControllerForDashBoardModule();
        deliveryStatusControllerForDashBoardModule deliveryStatusControllerForDashBoardModule = new deliveryStatusControllerForDashBoardModule();
        nutritionDetailControllerForDashBoardModule nutritionDetailControllerForDashBoardModule = new nutritionDetailControllerForDashBoardModule();
        contraceptiveSupplyStatusControllerForDashBoardModule contraceptiveSupplyStatusControllerForDashBoardModule = new contraceptiveSupplyStatusControllerForDashBoardModule();


        HashMap<String,dashboardControllers> stringdashboardControllersHashMap = new HashMap<String,dashboardControllers>();
        stringdashboardControllersHashMap.put("upcomingScheduleStatusController",usscontrollerForDashBoardModule);
        stringdashboardControllersHashMap.put("reminderVisitStatusController",ancpnc_encc_rsForDashBoardModule);
        stringdashboardControllersHashMap.put("reproductiveHealthServiceController",ReproductiveHealthServiceControllerForDashBoardModule);
        stringdashboardControllersHashMap.put("deliveryStatusController",deliveryStatusControllerForDashBoardModule);
        stringdashboardControllersHashMap.put("nutritionDetailController",nutritionDetailControllerForDashBoardModule);
        stringdashboardControllersHashMap.put("contraceptiveSupplyStatusController",contraceptiveSupplyStatusControllerForDashBoardModule);

        controllerHolder.setControllersHashMap(stringdashboardControllersHashMap);
        return controllerHolder;
    }


}

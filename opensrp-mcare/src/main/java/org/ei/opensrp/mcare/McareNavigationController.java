package org.ei.opensrp.mcare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import org.ei.opensrp.mcare.anc.mCareANCSmartRegisterActivity;
import org.ei.opensrp.mcare.elco.ElcoSmartRegisterActivity;
import org.ei.opensrp.mcare.household.HouseHoldSmartRegisterActivity;
import org.ei.opensrp.mcare.household.tutorial.tutorialCircleViewFlow;
import org.ei.opensrp.view.controller.ANMController;

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

}

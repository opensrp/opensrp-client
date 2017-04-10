package org.ei.opensrp.ddtk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;


import org.ei.opensrp.ddtk.ddtk.FormulirDdtkSmartRegisterActivity;
import org.ei.opensrp.ddtk.homeinventory.HomeInventorySmartRegisterActivity;
import org.ei.opensrp.ddtk.parana.NativeKIParanaSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeChildSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;


import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SdidtkNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;
    private org.ei.opensrp.Context context;

    public SdidtkNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }
    public SdidtkNavigationController(Activity activity, ANMController anmController, org.ei.opensrp.Context context) {
        this(activity,anmController);
        this.context=context;
    }
    @Override
    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, FormulirDdtkSmartRegisterActivity.class));
        ///  activity.startActivity(new Intent(activity, HouseHoldSmartRegisterActivity.class));
        /*SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
            //     activity.startActivity(new Intent(activity, tutorialCircleViewFlow.class));
        }*/

    }
    @Override
    public void startFPSmartRegistry() {
           activity.startActivity(new Intent(activity, NativeKIParanaSmartRegisterActivity.class));
    }
  /*  @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, HomeInventorySmartRegisterActivity.class));
    }*/
    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, HomeInventorySmartRegisterActivity.class));
    }

}

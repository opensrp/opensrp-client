package org.ei.opensrp.dghs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;


import org.ei.opensrp.dghs.HH_child.HH_child_member_SmartRegisterActivity;
import org.ei.opensrp.dghs.HH_woman.HH_woman_member_SmartRegisterActivity;
import org.ei.opensrp.dghs.hh_member.HH_member_SmartRegisterActivity;
import org.ei.opensrp.dghs.stock.StockSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeANCSmartRegisterActivity;
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

        activity.startActivity(new Intent(activity, HH_member_SmartRegisterActivity.class));


    }
    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, HH_woman_member_SmartRegisterActivity.class));
    }
    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, HH_child_member_SmartRegisterActivity.class));
    }
    public void startStockSmartRegistry() {
        activity.startActivity(new Intent(activity, StockSmartRegisterActivity.class));

    }

}

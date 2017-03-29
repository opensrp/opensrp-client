package org.ei.opensrp.vaksinator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;


import org.ei.opensrp.vaksinator.imunisasiTT.TTSmartRegisterActivity;
import org.ei.opensrp.vaksinator.vaksinator.VaksinatorSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;
import org.json.JSONObject;


import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class TestNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;
    private org.ei.opensrp.Context context;

    public TestNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }

    public TestNavigationController(Activity activity, ANMController anmController, org.ei.opensrp.Context context) {
        this(activity,anmController);
        this.context=context;
    }

    @Override
    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, VaksinatorSmartRegisterActivity.class));
      ///  activity.startActivity(new Intent(activity, HouseHoldSmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
       //     activity.startActivity(new Intent(activity, tutorialCircleViewFlow.class));
        }
    }
    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, TTSmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true))
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
    }
    @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, VaksinatorSmartRegisterActivity.class));
    }

    @Override
    public void startReports() {
        String id, pass;
        try{
            id = new JSONObject(anmController.get()).get("anmName").toString();
            pass = context.allSettings().fetchANMPassword();
        }catch(org.json.JSONException ex){
            id="noname";
            pass="null";
        }
        String uri = "http://"+id+":"+pass+"@"+activity.getApplicationContext().getString(R.string.dho_site).replace("http://","");
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

}

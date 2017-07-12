package org.ei.opensrp.path.controller;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.path.activity.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.path.activity.ProviderProfileActivity;
import org.ei.opensrp.path.activity.VaccineReportActivity;
import org.ei.opensrp.path.activity.WomanSmartRegisterActivity;

public class VaccinatorNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;

    public VaccinatorNavigationController(Activity activity) {
        super(activity, null);
        this.activity = activity;
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReportActivity.class));
    }

    public void startFieldMonitor() {
        activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));
    }

    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));
    }

    public void startWomanSmartRegister() {
        activity.startActivity(new Intent(activity, WomanSmartRegisterActivity.class));
    }

    public void startProviderProfile() {
        activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
    }
}

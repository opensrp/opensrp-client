package org.ei.opensrp.vaccinator;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.vaccinator.report.VaccineReport;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;

public class VaccinatorNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;
    private ANMController anmController;

    public VaccinatorNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }


    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));
     }

    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));
    }

    @Override
    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, WomanSmartRegisterActivity.class));
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReport.class));
    }
}

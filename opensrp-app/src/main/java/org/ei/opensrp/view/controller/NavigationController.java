package org.ei.opensrp.view.controller;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.view.activity.NativeANCSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeChildSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeECSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeFPSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativePNCSmartRegisterActivity;
import org.ei.opensrp.view.activity.ReportsActivity;
import org.ei.opensrp.view.activity.VideosActivity;

import static org.ei.opensrp.view.controller.ProfileNavigationController.navigateToANCProfile;
import static org.ei.opensrp.view.controller.ProfileNavigationController.navigateToChildProfile;
import static org.ei.opensrp.view.controller.ProfileNavigationController.navigateToECProfile;
import static org.ei.opensrp.view.controller.ProfileNavigationController.navigateToPNCProfile;

public class NavigationController {
    private Activity activity;
    private ANMController anmController;

    public NavigationController(Activity activity, ANMController anmController) {
        this.activity = activity;
        this.anmController = anmController;
    }

    public void startReports() {
        activity.startActivity(new Intent(activity, ReportsActivity.class));
    }

    public void startVideos() {
        activity.startActivity(new Intent(activity, VideosActivity.class));
    }

    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeECSmartRegisterActivity.class));
    }

    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeFPSmartRegisterActivity.class));
    }

    public void startANCSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeANCSmartRegisterActivity.class));
    }

    public void startPNCSmartRegistry() {
        activity.startActivity(new Intent(activity, NativePNCSmartRegisterActivity.class));
    }

    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeChildSmartRegisterActivity.class));
    }

    public String get() {
        return anmController.get();
    }

    public void goBack() {
        activity.finish();
    }

    public void startEC(String entityId) {
        navigateToECProfile(activity, entityId);
    }

    public void startANC(String entityId) {
        navigateToANCProfile(activity, entityId);
    }

    public void startPNC(String entityId) {
        navigateToPNCProfile(activity, entityId);
    }

    public void startChild(String entityId) {
        navigateToChildProfile(activity, entityId);
    }
}

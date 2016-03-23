package org.ei.opensrp.view.activity;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import org.ei.opensrp.util.Log;

import static org.ei.opensrp.AllConstants.FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE;

public class FormWebInterface {
    private final String model;
    private final String form;
    private Activity activity;

    public FormWebInterface(String model, String form, Activity activity) {
        this.model = model;
        this.form = form;
        this.activity = activity;
    }

    @JavascriptInterface
    public String getModel() {
        return model;
    }

    @JavascriptInterface
    public String getForm() {
        return form;
    }

    @JavascriptInterface
    public void goBack() {
        activity.setResult(FORM_SUCCESSFULLY_SUBMITTED_RESULT_CODE);
        activity.finish();
    }

    @JavascriptInterface
    public void log(String message) {
        Log.logInfo(message);
    }

    @JavascriptInterface
    public void onLoadFinished(){
        ((SecuredWebActivity)activity).closeDialog();
    }
}

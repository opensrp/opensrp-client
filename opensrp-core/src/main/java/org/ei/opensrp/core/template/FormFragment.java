package org.ei.opensrp.core.template;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.ei.opensrp.core.R;
import org.ei.opensrp.core.utils.Utils;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.customControls.OpenSRPWebView;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Geoffrey Koros on 9/12/2015.
 */
public class FormFragment extends Fragment {

    public static final String TAG = "DisplayFormFragment";

    private boolean formDataLoaded = false;
    private ViewGroup formView;

    public boolean isFormDataLoaded(){return formDataLoaded;}

    private WebView webView;
    ProgressBar progressBar;

    public static String formInputErrorMessage = "Form contains errors please try again";// externalize this

    private static final String headerTemplate = "web/forms/header";
    private static final String footerTemplate = "web/forms/footer";
    private static final String scriptFile = "web/forms/js_include.js";

    private String formName;
    public static String okMessage = "ok";
    Dialog progressDialog;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    private String recordId;

    private boolean javascriptLoaded = false;

    private JSONObject fieldOverides = new JSONObject();

    public JSONObject getFieldOverides() {
        return fieldOverides;
    }

    public void setFieldOverides(String overrides) {
        try{
            //get the field overrides map
            if (overrides != null){
                JSONObject json = new JSONObject(overrides);
                String overridesStr = json.getString("fieldOverrides");
                this.fieldOverides = new JSONObject(overridesStr);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(getClass().getName(), "onDetach called");

        cleanup();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(getClass().getName(), "onPause called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(getClass().getName(), "onStop called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(getClass().getName(), "onDestroy called");

        cleanup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(getClass().getName(), "onDestroyView called");
    }

    private void cleanup(){
        Log.v(getClass().getName(), "cleanup called");

        if (webView != null){
            hideTranslucentProgressDialog();
            // webView.getSettings().setRenderPriority(WebSettings.RenderPriority.LOW);
            // webView.getSettings().setJavaScriptEnabled(false);
            // webView.getSettings().setGeolocationEnabled(false);
            // webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
            //webView.destroy();
            //webView = null;
        }
        if (progressDialog != null){
            progressDialog.dismiss();
        }
//        if (formView != null){
//            formView.removeAllViews();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(getClass().getName(), "Creating view for FormFragment");
        // Inflate the layout for this fragment
        formView = (ViewGroup) inflater.inflate(R.layout.form_fragment, container, false);
        webView = (OpenSRPWebView)formView.findViewById(R.id.webview);

        // Enable/disable hardware acceleration:
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // Disable the cache:
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        progressBar = (ProgressBar)formView.findViewById(R.id.progressBar);
        initWebViewSettings();
        loadHtml();
        initProgressDialog();
        return formView;
    }

    private void initWebViewSettings(){
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setWebViewClient(new AppWebViewClient(progressBar));
        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.getSettings().setGeolocationDatabasePath(getActivity().getFilesDir().getPath());
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        final MyJavaScriptInterface myJavaScriptInterface = new MyJavaScriptInterface(getActivity());
        webView.addJavascriptInterface(myJavaScriptInterface, "Android");

        Log.v(getClass().getName(), "Web View Settings inited");
    }

    /**
     * reset the form
     */
    public void resetForm(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:resetForm()");
                Log.d(TAG, "reseting form");
            }
        });
    }

    public void loadHtml(){
        showProgressDialog();
        String header = readFileAssets(headerTemplate);

        String script = readFileAssets(scriptFile);
        String modelString = readFileAssets("www/form/" + formName + "/model.xml").replaceAll("\"", "\\\\\"").replaceAll("\n", "").replaceAll("\r", "").replaceAll("/","\\\\/");
        String form = readFileAssets("www/form/" + formName + "/form.xml");
        String footer = readFileAssets(footerTemplate);

        // inject the model and form into html template
        script = script.replace("$model_string_placeholder", modelString);
        footer = footer.replace("<!-- $script_placeholder >", script);

        StringBuilder sb = new StringBuilder();
        sb.append(header).append(form).append(footer);
        webView.loadDataWithBaseURL("file:///android_asset/web/forms/", sb.toString(), "text/html", "utf-8", null);
        //webView.loadUrl("file:///android_asset/web/template.html");

        //resizeForm();
    }

    public String readFileAssets(String fileName) {
        String fileContents = null;
        try {
            InputStream is = getActivity().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        //Log.d("File", fileContents);
        return fileContents;
    }

    private void showProgressDialog(){
        webView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgressDialog(){
        //dialog.dismiss();
        webView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        //loadFormData();
    }

    public void showTranslucentProgressDialog(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }

    public void hideTranslucentProgressDialog(){
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.hide();
                }
            }
        });
    }

    public void resetFormData(String entityId, final String metaData, boolean loadPrevious){
        showTranslucentProgressDialog();

        if (metaData != null){
            entityId = entityId == null?"":entityId;
            String data = null;
            //check if there is previously saved data for the form
            if(loadPrevious){
                data = Utils.getPreviouslySavedDataForForm(getActivity(), formName, metaData, entityId);
            }

            if (data == null){
                data = FormUtils.getInstance(getActivity()).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
            }

            setRecordId(entityId);
            setFieldOverides(metaData);

            final String finalData = data;
            Thread tm = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait for the page to initialize
                        while (!javascriptLoaded) {
                            Thread.sleep(100);
                        }

                        if (finalData != null && !finalData.isEmpty()) {
                            postXmlDataToFormWWait(finalData);
                        } else {
                            resetForm();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString(), e);
                    }
                }
            });
            tm.start();
        }
    }

    public void setFormData(final String data){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    // Wait for the page to initialize
                    while (!javascriptLoaded){
                        Thread.sleep(100);
                    }

                    if (data != null && !data.isEmpty()){
                        postXmlDataToForm(data);
                    }else{
                        resetForm();
                    }
                }catch(Exception e){
                    Log.e(TAG, e.toString(), e);
                }
            }
        }).start();
    }

    private void postXmlDataToFormWWait(final String data) throws InterruptedException {
        Runnable t = new Runnable() {
            @Override
            public void run() {
                try {
                    webView.evaluateJavascript("javascript:loadDraft('" + data + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("RECEIVED VALUE : ", s); // Returns the value from the function
                            hideTranslucentProgressDialog();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        webView.post(t);
    }

    private void postXmlDataToForm(final String data){
        webView.post(new Runnable() {
            @Override
            public void run() {
                String formData = data.replaceAll("template=\"\"", "");
                webView.loadUrl("javascript:loadDraft('" + formData + "')");
                Log.d("posting data", data);
            }
        });
    }

    // override this on tha child classes to override specific fields
    public JSONObject getFormFieldsOverrides(){
        return fieldOverides;
    }

    public void saveCurrentFormData() {
        webView.loadUrl("javascript:savePartialData()");
    }

    public void reloadDateWidget() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:refreshDateFields()");
                Log.d(TAG, "date widgets reloaded");
            }
        });
    }

    public class AppWebViewClient extends WebViewClient {
        private View progressBar;

        public AppWebViewClient(ProgressBar progressBar) {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            if (webView != null) dismissProgressDialog();
        }
    }

    public class MyJavaScriptInterface {
        private static final String JAVASCRIPT_LOG_TAG = "Javascript";
        Context mContext;

        MyJavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showFormErrorToast(){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(formInputErrorMessage)
                    .setCancelable(false)
                    .setPositiveButton(okMessage, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things

                        }
                    });

//            AlertDialog alert = builder.create();
//
//            alert.show();
            AlertDialog dialog = builder.show();
            TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.CENTER);
            messageText.setPadding(20, 20, 20, 20);
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);
//            Toast.makeText(mContext, formInputErrorMessage, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void processFormSubmission(String formSubmission){
            showTranslucentProgressDialog();

            ((RegisterActivity)getActivity()).saveFormSubmission(formSubmission, recordId, formName, getFormFieldsOverrides());
        }

        @JavascriptInterface
        public void javascriptLoaded(){
            //Toast.makeText(mContext, "Javascript loaded", Toast.LENGTH_LONG).show();
            javascriptLoaded = true;
        }

        @JavascriptInterface
        public void savePartialFormData(String partialData){
            //Toast.makeText(mContext, "saving un-submitted form data", Toast.LENGTH_LONG).show();
            ((RegisterActivity)getActivity()).savePartialFormData(partialData, recordId, formName, getFormFieldsOverrides());
        }

        @JavascriptInterface
        public void log(String message){
            Log.d(JAVASCRIPT_LOG_TAG, message);
            //Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    private void resizeForm() {

        //      getActivity().runOnUiThread(new Runnable() {
        //         @Override
        //          public void run() {
        int landWidthPixels = 0;
        int landHeightPixels = 0;

        WindowManager w = getActivity().getWindowManager();
        Display d = w.getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                landWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                landHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(Build.VERSION.SDK_INT > 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                landWidthPixels = realSize.x;
                landHeightPixels = realSize.y;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        webView.setLayoutParams(new RelativeLayout.LayoutParams(landHeightPixels, landWidthPixels));
//            }
//        });
    }

    private void initProgressDialog() {
        progressDialog = new Dialog(getActivity(), R.style.progress_dialog_theme);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog);
    }
}
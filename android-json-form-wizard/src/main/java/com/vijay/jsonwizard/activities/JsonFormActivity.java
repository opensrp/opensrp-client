package com.vijay.jsonwizard.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.comparisons.Comparison;
import com.vijay.jsonwizard.comparisons.EqualToComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanComparison;
import com.vijay.jsonwizard.comparisons.GreaterThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.LessThanComparison;
import com.vijay.jsonwizard.comparisons.LessThanEqualToComparison;
import com.vijay.jsonwizard.comparisons.NotEqualToComparison;
import com.vijay.jsonwizard.comparisons.RegexComparison;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.interfaces.OnActivityResultListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.PropertyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

public class JsonFormActivity extends AppCompatActivity implements JsonApi {

    private static final String TAG = "JsonFormActivity";

    private Toolbar mToolbar;

    private JSONObject mJSONObject;
    private PropertyManager propertyManager;
    private HashMap<String, View> skipLogicViews;
    private HashMap<String, View> constrainedViews;
    private ArrayList<View> formDataViews;
    private String functionRegex;
    private HashMap<String, Comparison> comparisons;
    private HashMap<Integer, OnActivityResultListener> onActivityResultListeners;

    public void init(String json) {
        try {
            mJSONObject = new JSONObject(json);
            if (!mJSONObject.has("encounter_type")) {
                mJSONObject = new JSONObject();
                throw new JSONException("Form encounter_type not set");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Initialization error. Json passed is invalid : " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_form);
        mToolbar = (Toolbar) findViewById(R.id.tb_top);
        setSupportActionBar(mToolbar);
        skipLogicViews = new HashMap<>();
        onActivityResultListeners = new HashMap<>();
        if (savedInstanceState == null) {
            init(getIntent().getStringExtra("json"));
            initializeFormFragment();
            onFormStart();
        } else {
            init(savedInstanceState.getString("jsonState"));
        }
    }

    public void initializeFormFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, JsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (onActivityResultListeners.containsKey(requestCode)) {
            onActivityResultListeners.get(requestCode).onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public synchronized JSONObject getStep(String name) {
        synchronized (mJSONObject) {
            try {
                return mJSONObject.getJSONObject(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent,
                           String openMrsEntity, String openMrsEntityId) throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString("key");
                if (key.equals(keyAtIndex)) {
                    if(item.has("text")){
                        item.put("text", value);
                    }else{
                        item.put("value", value);

                    }

                    item.put("openmrs_entity_parent", openMrsEntityParent);
                    item.put("openmrs_entity", openMrsEntity);
                    item.put("openmrs_entity_id", openMrsEntityId);
                    refreshSkipLogic(key, null);
                    refreshConstraints(key, null);
                    return;
                }
            }
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey,
                           String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId)
            throws JSONException {
        synchronized (mJSONObject) {
            JSONObject jsonObject = mJSONObject.getJSONObject(stepName);
            JSONArray fields = fetchFields(jsonObject);
            for (int i = 0; i < fields.length(); i++) {
                JSONObject item = fields.getJSONObject(i);
                String keyAtIndex = item.getString("key");
                if (parentKey.equals(keyAtIndex)) {
                    JSONArray jsonArray = item.getJSONArray(childObjectKey);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject innerItem = jsonArray.getJSONObject(j);
                        String anotherKeyAtIndex = innerItem.getString("key");
                        if (childKey.equals(anotherKeyAtIndex)) {
                            innerItem.put("value", value);
                            refreshSkipLogic(parentKey, childKey);
                            refreshConstraints(parentKey, childKey);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeMetaDataValue(String metaDataKey, Map<String, String> values) throws JSONException {
        synchronized (mJSONObject) {
            if (mJSONObject.has(FormUtils.METADATA_PROPERTY) && !values.isEmpty()) {
                if (mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).has(metaDataKey)) {
                    JSONObject metaData = mJSONObject.getJSONObject(FormUtils.METADATA_PROPERTY).getJSONObject(metaDataKey);
                    for (Map.Entry<String, String> entry : values.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (value == null) value = "";
                        metaData.put(key, value);
                    }
                }

            }
        }
    }

    @Override
    public String currentJsonState() {
        synchronized (mJSONObject) {
            return mJSONObject.toString();
        }
    }

    @Override
    public String getCount() {
        synchronized (mJSONObject) {
            return mJSONObject.optString("count");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("jsonState", mJSONObject.toString());
    }

    @Override
    public void onFormStart() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(this);
            }
            FormUtils.updateStartProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFormFinish() {
        try {
            if (propertyManager == null) {
                propertyManager = new PropertyManager(this);
            }
            FormUtils.updateEndProperties(propertyManager, mJSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearSkipLogicViews() {
        skipLogicViews = new HashMap<>();
    }

    @Override
    public void clearConstrainedViews() {
        constrainedViews = new HashMap<>();
    }

    @Override
    public void clearFormDataViews() {
        formDataViews = new ArrayList<>();
        clearSkipLogicViews();
        clearConstrainedViews();
    }

    @Override
    public void refreshHiddenViews() {
        for (View curView : formDataViews) {
            String addressString = (String) curView.getTag(R.id.address);
            String[] address = addressString.split(":");
            try {
                JSONObject viewData = getObjectUsingAddress(address);
                if (viewData.has("hidden") && viewData.getBoolean("hidden")) {
                    toggleViewVisibility(curView, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void addSkipLogicView(View view) {
        skipLogicViews.put(getViewKey(view), view);
    }

    @Override
    public void addConstrainedView(View view) {
        constrainedViews.put(getViewKey(view), view);
    }

    private String getViewKey(View view) {
        String key = (String) view.getTag(R.id.key);
        if (view.getTag(R.id.childKey) != null) {
            key = key + ":" + (String) view.getTag(R.id.childKey);
        }

        return key;
    }

    @Override
    public void addFormDataView(View view) {
        formDataViews.add(view);
    }

    @Override
    public ArrayList<View> getFormDataViews() {
        return formDataViews;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog)
                .setTitle(R.string.confirm_form_close)
                .setMessage(R.string.confirm_form_close_explanation)
                .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JsonFormActivity.this.finish();
                    }
                })
                .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        dialog.show();
    }

    @Override
    public void refreshSkipLogic(String parentKey, String childKey) {
        initComparisons();
        for (View curView : skipLogicViews.values()) {
            String relevanceTag = (String) curView.getTag(R.id.relevance);
            if (relevanceTag != null && relevanceTag.length() > 0) {
                try {
                    JSONObject relevance = new JSONObject(relevanceTag);
                    Iterator<String> keys = relevance.keys();
                    boolean ok = true;
                    while (keys.hasNext()) {
                        String curKey = keys.next();
                        String[] address = curKey.split(":");
                        if (address.length == 2) {
                            JSONObject curRelevance = relevance.getJSONObject(curKey);
                            String curValue = getValueFromAddress(address);
                            try {
                                boolean comparison = doComparison(curValue, curRelevance);
                                ok = ok && comparison;
                                if (!ok) break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    toggleViewVisibility(curView, ok);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void toggleViewVisibility(View view, boolean visible) {
        try {
            JSONArray canvasViewIds = new JSONArray((String) view.getTag(R.id.canvas_ids));
            String addressString = (String) view.getTag(R.id.address);
            String[] address = addressString.split(":");
            JSONObject object = getObjectUsingAddress(address);
            boolean enabled = visible;
            if (object.has("read_only") && object.getBoolean("read_only") && visible) {
                enabled = false;
            }
            view.setEnabled(enabled);
            for (int i = 0; i < canvasViewIds.length(); i++) {
                int curId = canvasViewIds.getInt(i);
                View curCanvasView = findViewById(curId);
                if (visible) {
                    curCanvasView.setEnabled(true);
                    curCanvasView.setVisibility(View.VISIBLE);
                } else {
                    curCanvasView.setEnabled(false);
                    curCanvasView.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * This method checks if all views being watched for constraints enforce those constraints
     * This library currently only supports constraints on views that store the value in {@link MaterialEditText}
     * (ie TreeViews, DatePickers, and EditTexts), and {@link CheckBox}
     */
    @Override
    public void refreshConstraints(String parentKey, String childKey) {
        initComparisons();

        // Priorities constraints on the view that has just been changed
        String changedViewKey = parentKey;
        if (changedViewKey != null && childKey != null) {
            changedViewKey = changedViewKey + ":" + childKey;
        }

        if (changedViewKey != null && constrainedViews.containsKey(changedViewKey)) {
            checkViewConstraints(constrainedViews.get(changedViewKey));
        }

        for (View curView : constrainedViews.values()) {
            if (changedViewKey == null || !getViewKey(curView).equals(changedViewKey)) {
                checkViewConstraints(curView);
            }
        }
    }

    @Override
    public void addOnActivityResultListener(final Integer requestCode,
                                            OnActivityResultListener onActivityResultListener) {
        onActivityResultListeners.put(requestCode, onActivityResultListener);
    }

    @Override
    public void resetFocus() {
        EditText defaultFocusView = (EditText) findViewById(R.id.default_focus_view);
        defaultFocusView.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), HIDE_NOT_ALWAYS);
    }

    private void checkViewConstraints(View curView) {
        String constraintTag = (String) curView.getTag(R.id.constraints);
        if (constraintTag != null && constraintTag.length() > 0) {
            try {
                String addressString = (String) curView.getTag(R.id.address);
                String[] address = addressString.split(":");

                JSONArray constraint = new JSONArray(constraintTag);
                String errorMessage = null;
                for (int i = 0; i < constraint.length(); i++) {
                    JSONObject curConstraint = constraint.getJSONObject(i);
                    if (address.length == 2) {
                        String value = getValueFromAddress(address);
                        errorMessage = enforceConstraint(value, curView, curConstraint);
                        if (errorMessage != null) break;
                    }
                }

                if (errorMessage != null) {
                    if (curView instanceof MaterialEditText) {
                        ((MaterialEditText) curView).setText(null);
                        ((MaterialEditText) curView).setError(errorMessage);
                    } else if (curView instanceof CheckBox) {
                        ((CheckBox) curView).setChecked(false);
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        String checkBoxKey = (String) curView.getTag(R.id.childKey);
                        JSONObject questionObject = getObjectUsingAddress(address);
                        for (int i = 0; i < questionObject.getJSONArray("options").length(); i++) {
                            JSONObject curOption = questionObject.getJSONArray("options").getJSONObject(i);
                            if (curOption.getString("key").equals(checkBoxKey)) {
                                curOption.put("value", "false");
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getValueFromAddress(String[] address) throws Exception {
        String result = null;
        JSONObject object = getObjectUsingAddress(address);
        if (object != null) {
            if (object.getString("type").equals("check_box")) {
                JSONArray resultArray = new JSONArray();
                JSONArray options = object.getJSONArray("options");
                for (int j = 0; j < options.length(); j++) {
                    if (options.getJSONObject(j).getString("value").toLowerCase().equals("true")) {
                        resultArray.put(options.getJSONObject(j).getString("key"));
                    }
                }

                if (resultArray.length() > 0) {
                    result = resultArray.toString();
                }
            } else {
                result = object.optString("value");
            }
        }

        return result;
    }

    @Override
    public JSONObject getObjectUsingAddress(String[] address) throws JSONException {
        if (address != null && address.length == 2) {
            JSONArray fields = fetchFields(mJSONObject.getJSONObject(address[0]));
            for (int i = 0; i < fields.length(); i++) {
                if (fields.getJSONObject(i).getString("key").equals(address[1])) {
                    return fields.getJSONObject(i);
                }
            }
        }

        return null;
    }

    private void initComparisons() {
        if (comparisons == null) {
            functionRegex = "";
            comparisons = new HashMap<>();

            LessThanComparison lessThanComparison = new LessThanComparison();
            functionRegex += lessThanComparison.getFunctionName();
            comparisons.put(lessThanComparison.getFunctionName(), lessThanComparison);

            LessThanEqualToComparison lessThanEqualToComparison = new LessThanEqualToComparison();
            functionRegex += "|" + lessThanEqualToComparison.getFunctionName();
            comparisons.put(lessThanEqualToComparison.getFunctionName(), lessThanEqualToComparison);

            EqualToComparison equalToComparison = new EqualToComparison();
            functionRegex += "|" + equalToComparison.getFunctionName();
            comparisons.put(equalToComparison.getFunctionName(), equalToComparison);

            NotEqualToComparison notEqualToComparer = new NotEqualToComparison();
            functionRegex += "|" + notEqualToComparer.getFunctionName();
            comparisons.put(notEqualToComparer.getFunctionName(), notEqualToComparer);

            GreaterThanComparison greaterThanComparison = new GreaterThanComparison();
            functionRegex += "|" + greaterThanComparison.getFunctionName();
            comparisons.put(greaterThanComparison.getFunctionName(), greaterThanComparison);

            GreaterThanEqualToComparison greaterThanEqualToComparison = new GreaterThanEqualToComparison();
            functionRegex += "|" + greaterThanEqualToComparison.getFunctionName();
            comparisons.put(greaterThanEqualToComparison.getFunctionName(), greaterThanEqualToComparison);

            RegexComparison regexComparison = new RegexComparison();
            functionRegex += "|" + regexComparison.getFunctionName();
            comparisons.put(regexComparison.getFunctionName(), regexComparison);
        }
    }

    private boolean doComparison(String value, JSONObject comparison) throws Exception {
        String type = comparison.getString("type").toLowerCase();
        String ex = comparison.getString("ex");

        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);//functions arguments should be two, and should either be addresses or values (enclosed using "")
            String[] args = getFunctionArgs(b, value);
            return comparisons.get(functionName).compare(args[0], type, args[1]);
        }

        return false;
    }

    private String[] getFunctionArgs(String functionArgs, String value) {
        String[] args = new String[2];
        String[] splitArgs = functionArgs.split(",");
        if (splitArgs.length == 2) {
            Pattern valueRegex = Pattern.compile("\"(.*)\"");
            for (int i = 0; i < splitArgs.length; i++) {
                String curArg = splitArgs[i].trim();

                if (curArg.equals(".")) {
                    args[i] = value;
                } else {
                    Matcher valueMatcher = valueRegex.matcher(curArg);
                    if (valueMatcher.find()) {
                        args[i] = valueMatcher.group(1);
                    } else {
                        try {
                            args[i] = getValueFromAddress(curArg.split(":"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return args;
    }

    /**
     * This method checks whether a constraint has been enforced and returns an error message if not
     * The error message should be displayable to the user
     *
     * @param value      The value to be checked
     * @param constraint The constraint expression to use
     * @return An error message if constraint has not been enfored or NULL if constraint enforced
     * @throws Exception
     */
    private String enforceConstraint(String value, View view, JSONObject constraint) throws Exception {
        String type = constraint.getString("type").toLowerCase();
        String ex = constraint.getString("ex");
        String errorMessage = constraint.getString("err");
        Pattern pattern = Pattern.compile("(" + functionRegex + ")\\((.*)\\)");
        Matcher matcher = pattern.matcher(ex);
        if (matcher.find()) {
            String functionName = matcher.group(1);
            String b = matcher.group(2);
            String[] args = getFunctionArgs(b, value);

            boolean viewDoesntHaveValue = TextUtils.isEmpty(value);
            if (view instanceof CheckBox) {
                viewDoesntHaveValue = !((CheckBox) view).isChecked();
            }

            if (viewDoesntHaveValue
                    || TextUtils.isEmpty(args[0])
                    || TextUtils.isEmpty(args[1])
                    || comparisons.get(functionName).compare(args[0], type, args[1])) {
                return null;
            }
        } else {
            Log.d(TAG, "Matcher didn't work with function");
        }

        return errorMessage;
    }

    private JSONArray fetchFields(JSONObject parentJson) {
        JSONArray fields = new JSONArray();
        try {
            if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);

                for (int i = 0; i < sections.length(); i++) {
                    JSONObject sectionJson = sections.getJSONObject(i);
                    if (sectionJson.has(JsonFormConstants.FIELDS)) {
                        fields = concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                    }
                }
            } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
            }
        } catch (JSONException e) {

        }

        return fields;
    }

    private JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

}

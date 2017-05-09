package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.MaterialSpinner;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nipun on 30/05/15.
 */
public class SpinnerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        MaterialSpinner spinner = (MaterialSpinner) LayoutInflater.from(context).inflate(R.layout.item_spinner, null);

        String hint = jsonObject.optString("hint");
        if (!TextUtils.isEmpty(hint)) {
            spinner.setHint(jsonObject.getString("hint"));
            spinner.setFloatingLabelText(jsonObject.getString("hint"));
        }

        spinner.setId(ViewUtil.generateViewId());
        canvasIds.put(spinner.getId());

        spinner.setTag(R.id.key, jsonObject.getString("key"));
        spinner.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        spinner.setTag(R.id.openmrs_entity, openMrsEntity);
        spinner.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        spinner.setTag(R.id.type, jsonObject.getString("type"));
        spinner.setTag(R.id.address,  stepName + ":" + jsonObject.getString("key"));

        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                spinner.setTag(R.id.v_required, requiredValue);
                spinner.setTag(R.id.error, requiredObject.optString("err"));
            }
        }

        String valueToSelect = "";
        int indexToSelect = -1;
        if (!TextUtils.isEmpty(jsonObject.optString("value"))) {
            valueToSelect = jsonObject.optString("value");
        }

        if (jsonObject.has("read_only")) {
            spinner.setEnabled(!jsonObject.getBoolean("read_only"));
            spinner.setFocusable(!jsonObject.getBoolean("read_only"));
        }

        JSONArray valuesJson = jsonObject.optJSONArray("values");
        String[] values = null;
        if (valuesJson != null && valuesJson.length() > 0) {
            values = new String[valuesJson.length()];
            for (int i = 0; i < valuesJson.length(); i++) {
                values[i] = valuesJson.optString(i);
                if (valueToSelect.equals(values[i])) {
                    indexToSelect = i;
                }
            }
        }

        if (values != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.simple_list_item_1, values);

            spinner.setAdapter(adapter);

            spinner.setSelection(indexToSelect + 1, true);
            spinner.setOnItemSelectedListener(listener);
        }
        ((JsonApi) context).addFormDataView(spinner);
        views.add(spinner);
        spinner.setTag(R.id.canvas_ids, canvasIds.toString());
        if (relevance != null && context instanceof JsonApi) {
            spinner.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(spinner);
        }
        return views;
    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            MaterialSpinner spinner) {
        if (!(spinner.getTag(R.id.v_required) instanceof String) || !(spinner.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        Boolean isRequired = Boolean.valueOf((String) spinner.getTag(R.id.v_required));
        if (!isRequired || !spinner.isEnabled()) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        int selectedItemPosition = spinner.getSelectedItemPosition();
        if(selectedItemPosition > 0) {
            return new ValidationStatus(true, null, formFragmentView, spinner);
        }
        return new ValidationStatus(false, (String) spinner.getTag(R.id.error), formFragmentView, spinner);
    }
}

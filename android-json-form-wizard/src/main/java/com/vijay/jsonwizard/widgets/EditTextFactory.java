package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.validators.edittext.MaxLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MaxNumericValidator;
import com.vijay.jsonwizard.validators.edittext.MinLengthValidator;
import com.vijay.jsonwizard.validators.edittext.MinNumericValidator;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class EditTextFactory implements FormWidgetFactory {
    private static final String TAG = "EditTextFactory";
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 100;

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);

        RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        MaterialEditText editText = (MaterialEditText) rootLayout.findViewById(R.id.edit_text);

        attachJson(stepName, context, formFragment, jsonObject, editText);

        JSONArray canvasIds = new JSONArray();
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        editText.setTag(R.id.canvas_ids, canvasIds.toString());

        ((JsonApi) context).addFormDataView(editText);
        views.add(rootLayout);
        return views;
    }

    protected void attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, MaterialEditText editText) throws Exception {

        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");
        String constraints = jsonObject.optString("constraints");

        int minLength = MIN_LENGTH;
        int maxLength = MAX_LENGTH;


        editText.setHint(jsonObject.getString("hint"));
        editText.setFloatingLabelText(jsonObject.getString("hint"));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        editText.setTag(R.id.openmrs_entity, openMrsEntity);
        editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        editText.setTag(R.id.type, jsonObject.getString("type"));
        editText.setTag(R.id.address, stepName + ":" + jsonObject.getString("key"));

        if (!TextUtils.isEmpty(jsonObject.optString("value"))) {
            editText.setText(jsonObject.optString("value"));
        }

        if (jsonObject.has("read_only")) {
            boolean readyOnly = jsonObject.getBoolean("read_only");
            editText.setEnabled(!readyOnly);
            editText.setFocusable(!readyOnly);
        }

        //add validators
        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                    editText.addValidator(new RequiredValidator(requiredObject.getString("err")));
                }
            }
        }

        JSONObject minLengthObject = jsonObject.optJSONObject("v_min_length");
        if (minLengthObject != null) {
            String minLengthValue = minLengthObject.optString("value");
            if (!TextUtils.isEmpty(minLengthValue)) {
                minLength = Integer.parseInt(minLengthValue);
                editText.addValidator(new MinLengthValidator(minLengthObject.getString("err"), Integer.parseInt(minLengthValue)));
            }
        }

        JSONObject maxLengthObject = jsonObject.optJSONObject("v_max_length");
        if (maxLengthObject != null) {
            String maxLengthValue = maxLengthObject.optString("value");
            if (!TextUtils.isEmpty(maxLengthValue)) {
                maxLength = Integer.parseInt(maxLengthValue);
                editText.addValidator(new MaxLengthValidator(maxLengthObject.getString("err"), Integer.parseInt(maxLengthValue)));
            }
        }

        editText.setMaxCharacters(maxLength);
        editText.setMinCharacters(minLength);

        JSONObject regexObject = jsonObject.optJSONObject("v_regex");
        if (regexObject != null) {
            String regexValue = regexObject.optString("value");
            if (!TextUtils.isEmpty(regexValue)) {
                editText.addValidator(new RegexpValidator(regexObject.getString("err"), regexValue));
            }
        }

        JSONObject emailObject = jsonObject.optJSONObject("v_email");
        if (emailObject != null) {
            String emailValue = emailObject.optString("value");
            if (!TextUtils.isEmpty(emailValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(emailValue)) {
                    editText.addValidator(new RegexpValidator(emailObject.getString("err"), android.util.Patterns.EMAIL_ADDRESS.toString()));
                }
            }
        }

        JSONObject urlObject = jsonObject.optJSONObject("v_url");
        if (urlObject != null) {
            String urlValue = urlObject.optString("value");
            if (!TextUtils.isEmpty(urlValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(urlValue)) {
                    editText.addValidator(new RegexpValidator(urlObject.getString("err"), Patterns.WEB_URL.toString()));
                }
            }
        }

        JSONObject numericObject = jsonObject.optJSONObject("v_numeric");
        if (numericObject != null) {
            String numericValue = numericObject.optString("value");
            if (!TextUtils.isEmpty(numericValue)) {
                if (Boolean.TRUE.toString().equalsIgnoreCase(numericValue)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addValidator(new RegexpValidator(numericObject.getString("err"),
                            "[0-9]*\\.?[0-9]*"));

                    if (jsonObject.has("v_min")) {
                        JSONObject minValidation = jsonObject.getJSONObject("v_min");
                        editText.addValidator(new MinNumericValidator(minValidation.getString("err"),
                                Double.parseDouble(minValidation.getString("value"))));
                    }

                    if (jsonObject.has("v_max")) {
                        JSONObject minValidation = jsonObject.getJSONObject("v_min");
                        editText.addValidator(new MaxNumericValidator(minValidation.getString("err"),
                                Double.parseDouble(minValidation.getString("value"))));
                    }
                }
            }
        }

        // edit type check
        String editType = jsonObject.optString("edit_type");
        if (!TextUtils.isEmpty(editType)) {
            if (editType.equals("number")) {
                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if (editType.equals("name")) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            }
        }

        editText.addTextChangedListener(new GenericTextWatcher(stepName, formFragment, editText));
        if (relevance != null && context instanceof JsonApi) {
            editText.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(editText);
        }

        if (constraints != null && context instanceof JsonApi) {
            editText.setTag(R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(editText);
        }

    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            MaterialEditText editText) {
        if (editText.isEnabled()) {
            boolean validate = editText.validate();
            if (!validate) {
                return new ValidationStatus(false, editText.getError().toString(), formFragmentView, editText);
            }
        }
        return new ValidationStatus(true, null, formFragmentView, editText);
    }

}

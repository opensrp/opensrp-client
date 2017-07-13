package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 07/07/2017.
 */

public class ButtonFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       final JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.getString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        Button button = new Button(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = context.getResources()
                .getDimensionPixelSize(R.dimen.extra_bottom_margin);
        button.setLayoutParams(layoutParams);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.button_text_size));
        button.setTextColor(context.getResources().getColor(android.R.color.white));
        button.setHeight(context.getResources().getDimensionPixelSize(R.dimen.button_height));

        String hint = jsonObject.optString(JsonFormConstants.HINT);
        if (!TextUtils.isEmpty(hint)) {
            button.setText(hint);
        }

        button.setId(ViewUtil.generateViewId());
        canvasIds.put(button.getId());

        button.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        button.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        button.setTag(R.id.openmrs_entity, openMrsEntity);
        button.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        button.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        button.setTag(R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));

        if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
            button.setEnabled(!jsonObject.getBoolean(JsonFormConstants.READ_ONLY));
            button.setFocusable(!jsonObject.getBoolean(JsonFormConstants.READ_ONLY));
        }

        JSONObject action = jsonObject.optJSONObject(JsonFormConstants.ACTION);
        if (action != null) {
            jsonObject.put(JsonFormConstants.VALUE, Boolean.FALSE.toString());
            jsonObject.getJSONObject(JsonFormConstants.ACTION).put(JsonFormConstants.RESULT, false);
            final String behaviour = action.optString(JsonFormConstants.BEHAVIOUR);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String addressString = (String) v.getTag(R.id.address);
                        if (!TextUtils.isEmpty(addressString)) {
                            String[] address = addressString.split(":");
                            JSONObject jsonObject = ((JsonApi) context)
                                    .getObjectUsingAddress(address);
                            jsonObject.put(JsonFormConstants.VALUE, Boolean.TRUE.toString());

                            switch (behaviour) {
                                case JsonFormConstants.BEHAVIOUR_FINISH_FORM:
                                    formFragment.save();
                                    break;
                                case JsonFormConstants.BEHAVIOUR_NEXT_STEP:
                                    formFragment.next();
                                    break;
                                default:
                                    jsonObject.getJSONObject(JsonFormConstants.ACTION)
                                            .put(JsonFormConstants.RESULT, false);
                                    jsonObject.put(JsonFormConstants.VALUE, Boolean.FALSE.toString());
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        ((JsonApi) context).addFormDataView(button);
        views.add(button);
        button.setTag(R.id.canvas_ids, canvasIds.toString());
        if (relevance != null && context instanceof JsonApi) {
            button.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(button);
        }

        return views;
    }
}

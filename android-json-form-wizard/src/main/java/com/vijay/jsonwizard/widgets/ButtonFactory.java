package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.rey.material.util.ViewUtil;
import com.rey.material.widget.Button;
import com.vijay.jsonwizard.R;
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
    private static final String BEHAVIOUR_FINISH_FORM = "finish_form";
    private static final String BEHAVIOUR_NEXT_STEP = "next_step";

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context,
                                       final JsonFormFragment formFragment, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        List<View> views = new ArrayList<>(1);
        JSONArray canvasIds = new JSONArray();
        Button button = new Button(context);
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.button_text_size));
        button.setTextColor(context.getResources().getColor(android.R.color.white));
        button.setHeight(context.getResources().getDimensionPixelSize(R.dimen.button_height));

        String hint = jsonObject.optString("hint");
        if (!TextUtils.isEmpty(hint)) {
            button.setText(hint);
        }

        button.setId(ViewUtil.generateViewId());
        canvasIds.put(button.getId());

        button.setTag(R.id.key, jsonObject.getString("key"));
        button.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        button.setTag(R.id.openmrs_entity, openMrsEntity);
        button.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        button.setTag(R.id.type, jsonObject.getString("type"));
        button.setTag(R.id.address, stepName + ":" + jsonObject.getString("key"));

        if (jsonObject.has("read_only")) {
            button.setEnabled(!jsonObject.getBoolean("read_only"));
            button.setFocusable(!jsonObject.getBoolean("read_only"));
        }

        JSONObject action = jsonObject.optJSONObject("action");
        if (action != null) {
            jsonObject.put("value", "false");
            jsonObject.getJSONObject("action").put("result", false);
            final String behaviour = action.optString("behaviour");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String addressString = (String) v.getTag(R.id.address);
                        if (!TextUtils.isEmpty(addressString)) {
                            String[] address = addressString.split(":");
                            JSONObject jsonObject = ((JsonApi) context)
                                    .getObjectUsingAddress(address);
                            jsonObject.getJSONObject("action").put("result", false);
                            jsonObject.put("value", "false");
                            switch (behaviour) {
                                case BEHAVIOUR_FINISH_FORM:
                                    jsonObject.getJSONObject("action").put("result", true);
                                    jsonObject.put("value", "true");
                                    formFragment.save();
                                    break;
                                case BEHAVIOUR_NEXT_STEP:
                                    jsonObject.getJSONObject("action").put("result", true);
                                    jsonObject.put("value", "true");
                                    formFragment.next();
                                    break;
                            }
                        }
                    } catch (Exception e) {
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

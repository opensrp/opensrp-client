package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.*;

/**
 * Created by vijay on 24-05-2015.
 */
public class RadioButtonFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");

        List<View> views = new ArrayList<>(1);

        JSONArray canvasIds = new JSONArray();
        TextView textView = getTextViewWith(context, 27, jsonObject.getString("label"), jsonObject.getString("key"),
                jsonObject.getString("type"), openMrsEntityParent, openMrsEntity, openMrsEntityId,
                relevance,
                getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH);
        canvasIds.put(textView.getId());

        views.add(textView);
        boolean readOnly = false;
        if (jsonObject.has("read_only")) {
            readOnly = jsonObject.getBoolean("read_only");
        }
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        ArrayList<RadioButton> radioButtons = new ArrayList<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            RadioButton radioButton = (RadioButton) LayoutInflater.from(context).inflate(R.layout.item_radiobutton,
                    null);
            radioButton.setId(ViewUtil.generateViewId());
            radioButton.setText(item.getString("text"));
            radioButton.setTag(R.id.key, jsonObject.getString("key"));
            radioButton.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            radioButton.setTag(R.id.openmrs_entity, openMrsEntity);
            radioButton.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            radioButton.setTag(R.id.type, jsonObject.getString("type"));
            radioButton.setTag(R.id.childKey, item.getString("key"));
            radioButton.setGravity(Gravity.CENTER_VERTICAL);
            radioButton.setTag(R.id.address,  stepName + ":" + jsonObject.getString("key"));
//            radioButton.setTextSize(context.getResources().getDimension(R.dimen.default_text_size));
            radioButton.setOnCheckedChangeListener(listener);
            if (!TextUtils.isEmpty(jsonObject.optString("value"))
                    && jsonObject.optString("value").equals(item.getString("key"))) {
                radioButton.setChecked(true);
            }
            radioButton.setEnabled(!readOnly);
            radioButton.setFocusable(!readOnly);
            if (i == options.length() - 1) {
                radioButton.setLayoutParams(getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }
            ((JsonApi) context).addFormDataView(radioButton);

            canvasIds.put(radioButton.getId());
            radioButtons.add(radioButton);

            views.add(radioButton);
            if (relevance != null && context instanceof JsonApi) {
                radioButton.setTag(R.id.relevance, relevance);
                ((JsonApi) context).addSkipLogicView(radioButton);
            }
        }

        for (RadioButton radioButton : radioButtons) {
            radioButton.setTag(R.id.canvas_ids, canvasIds.toString());
        }

        return views;
    }
}

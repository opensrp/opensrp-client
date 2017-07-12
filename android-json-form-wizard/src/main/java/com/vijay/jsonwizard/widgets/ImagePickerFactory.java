package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.utils.ImageUtils;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.dpToPixels;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;

/**
 * Created by vijay on 24-05-2015.
 */
public class ImagePickerFactory implements FormWidgetFactory {

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
        String openMrsEntity = jsonObject.getString("openmrs_entity");
        String openMrsEntityId = jsonObject.getString("openmrs_entity_id");
        String relevance = jsonObject.optString("relevance");
        JSONArray canvasIds = new JSONArray();

        List<View> views = new ArrayList<>(1);
        ImageView imageView = new ImageView(context);
        imageView.setId(ViewUtil.generateViewId());
        canvasIds.put(imageView.getId());
        imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.grey_bg));
        imageView.setTag(R.id.key, jsonObject.getString("key"));
        imageView.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        imageView.setTag(R.id.openmrs_entity, openMrsEntity);
        imageView.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        imageView.setTag(R.id.type, jsonObject.getString("type"));
        imageView.setTag(R.id.address,  stepName + ":" + jsonObject.getString("key"));
        if (relevance != null && context instanceof JsonApi) {
            imageView.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(imageView);
        }

        JSONObject requiredObject = jsonObject.optJSONObject("v_required");
        if (requiredObject != null) {
            String requiredValue = requiredObject.getString("value");
            if (!TextUtils.isEmpty(requiredValue)) {
                imageView.setTag(R.id.v_required, requiredValue);
                imageView.setTag(R.id.error, requiredObject.optString("err"));
            }
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(getLayoutParams(MATCH_PARENT, dpToPixels(context, 200), 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.default_bottom_margin)));
        String imagePath = jsonObject.optString("value");
        Button uploadButton = new Button(context);
        if (!TextUtils.isEmpty(imagePath)) {
            imageView.setTag(R.id.imagePath, imagePath);
            imageView.setImageBitmap(ImageUtils.loadBitmapFromFile(context, imagePath, ImageUtils.getDeviceWidth(context), dpToPixels(context, 200)));
        }

        if (jsonObject.has("read_only")) {
            boolean readOnly = jsonObject.getBoolean("read_only");
            uploadButton.setEnabled(!readOnly);
            uploadButton.setFocusable(!readOnly);
        }

        ((JsonApi) context).addFormDataView(imageView);
        views.add(imageView);

        uploadButton.setText(jsonObject.getString("uploadButtonText"));
        uploadButton.setTextColor(context.getResources().getColor(android.R.color.white));
        uploadButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimension(R.dimen.button_text_size));
        uploadButton.setPadding(
                context.getResources().getDimensionPixelSize(R.dimen.button_padding),
                context.getResources().getDimensionPixelSize(R.dimen.button_padding),
                context.getResources().getDimensionPixelSize(R.dimen.button_padding),
                context.getResources().getDimensionPixelSize(R.dimen.button_padding));
        uploadButton.setLayoutParams(getLayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.default_bottom_margin)));
        uploadButton.setOnClickListener(listener);
        uploadButton.setTag(R.id.key, jsonObject.getString("key"));
        uploadButton.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        uploadButton.setTag(R.id.openmrs_entity, openMrsEntity);
        uploadButton.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        uploadButton.setTag(R.id.type, jsonObject.getString("type"));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp2px(context, 20));
        uploadButton.setLayoutParams(params);

        uploadButton.setId(ViewUtil.generateViewId());
        canvasIds.put(uploadButton.getId());
        uploadButton.setTag(R.id.canvas_ids, canvasIds.toString());

        views.add(uploadButton);
        if (relevance != null && context instanceof JsonApi) {
            uploadButton.setTag(R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(uploadButton);
        }
        return views;
    }

    public static int dp2px(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    public static ValidationStatus validate(JsonFormFragmentView formFragmentView,
                                            ImageView imageView) {
        if (!(imageView.getTag(R.id.v_required) instanceof String) || !(imageView.getTag(R.id.error) instanceof String)) {
            return new ValidationStatus(true, null, formFragmentView, imageView);
        }
        Boolean isRequired = Boolean.valueOf((String) imageView.getTag(R.id.v_required));
        if (!isRequired || !imageView.isEnabled()) {
            return new ValidationStatus(true, null, formFragmentView, imageView);
        }
        Object path = imageView.getTag(R.id.imagePath);
        if (path instanceof String && !TextUtils.isEmpty((String) path)) {
            return new ValidationStatus(true, null, formFragmentView, imageView);
        }
        return new ValidationStatus(false, (String) imageView.getTag(R.id.error), formFragmentView, imageView);
    }
}

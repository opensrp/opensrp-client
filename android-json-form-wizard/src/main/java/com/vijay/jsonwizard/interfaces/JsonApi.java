package com.vijay.jsonwizard.interfaces;

import android.view.View;

import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.views.JsonFormFragmentView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vijay on 5/16/15.
 */
public interface JsonApi {
    JSONObject getStep(String stepName);

    void writeValue(String stepName, String key, String value, String openMrsEntityParent,
                    String openMrsEntity, String openMrsEntityId) throws JSONException;

    void writeValue(String stepName, String prentKey, String childObjectKey, String childKey,
                    String value, String openMrsEntityParent, String openMrsEntity,
                    String openMrsEntityId)
            throws JSONException;

    void writeMetaDataValue(String metaDataKey, Map<String, String> values)
            throws JSONException;

    String currentJsonState();

    String getCount();

    void onFormStart();

    void onFormFinish();

    void clearSkipLogicViews();

    void clearConstrainedViews();

    void clearFormDataViews();

    void addSkipLogicView(View view);

    void addConstrainedView(View view);

    void refreshHiddenViews();

    void refreshSkipLogic(String parentKey, String childKey);

    void addFormDataView(View view);

    ArrayList<View> getFormDataViews();

    JSONObject getObjectUsingAddress(String[] address) throws JSONException;

    void refreshConstraints(String parentKey, String childKey);

    void addOnActivityResultListener(Integer requestCode, OnActivityResultListener onActivityResultListener);

    void resetFocus();
}

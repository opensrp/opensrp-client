package com.vijay.jsonwizard.interactors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.widgets.BarcodeFactory;
import com.vijay.jsonwizard.widgets.ButtonFactory;
import com.vijay.jsonwizard.widgets.CheckBoxFactory;
import com.vijay.jsonwizard.widgets.DatePickerFactory;
import com.vijay.jsonwizard.widgets.EditTextFactory;
import com.vijay.jsonwizard.widgets.ImagePickerFactory;
import com.vijay.jsonwizard.widgets.LabelFactory;
import com.vijay.jsonwizard.widgets.RadioButtonFactory;
import com.vijay.jsonwizard.widgets.SectionFactory;
import com.vijay.jsonwizard.widgets.SpinnerFactory;
import com.vijay.jsonwizard.widgets.TreeViewFactory;

/**
 * Created by vijay on 5/19/15.
 */
public class JsonFormInteractor {

    private static final String TAG = "JsonFormInteractor";
    protected static final Map<String, FormWidgetFactory> map = new HashMap<>();
    private static final JsonFormInteractor INSTANCE = new JsonFormInteractor();

    protected JsonFormInteractor() {
        registerWidgets();
    }

    protected void registerWidgets() {
        map.put(JsonFormConstants.SECTION_LABEL, new SectionFactory());
        map.put(JsonFormConstants.EDIT_TEXT, new EditTextFactory());
        map.put(JsonFormConstants.LABEL, new LabelFactory());
        map.put(JsonFormConstants.CHECK_BOX, new CheckBoxFactory());
        map.put(JsonFormConstants.RADIO_BUTTON, new RadioButtonFactory());
        map.put(JsonFormConstants.CHOOSE_IMAGE, new ImagePickerFactory());
        map.put(JsonFormConstants.SPINNER, new SpinnerFactory());
        map.put(JsonFormConstants.DATE_PICKER, new DatePickerFactory());
        map.put(JsonFormConstants.TREE, new TreeViewFactory());
        map.put(JsonFormConstants.BARCODE, new BarcodeFactory());
        map.put(JsonFormConstants.BUTTON, new ButtonFactory());
    }

    public List<View> fetchFormElements(String stepName, JsonFormFragment formFragment,
                                        JSONObject parentJson, CommonListener listener) {
        Log.d(TAG, "fetchFormElements called");
        List<View> viewsFromJson = new ArrayList<>(5);
        try {

            if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                fetchSections(viewsFromJson, stepName, formFragment, sections, listener);

            } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                JSONArray fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
                fetchFields(viewsFromJson, stepName, formFragment, fields, listener);
            }

        } catch (JSONException e) {
            Log.d(TAG, "Json exception occurred : " + e.getMessage());
            e.printStackTrace();
        }
        return viewsFromJson;
    }

    private void fetchSections(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                               JSONArray sections, CommonListener listener) {

        try {
            if (sections == null || sections.length() == 0) {
                return;
            }

            for (int i = 0; i < sections.length(); i++) {
                JSONObject sectionJson = sections.getJSONObject(i);

                if (sectionJson.has(JsonFormConstants.NAME)) {
                    fetchViews(viewsFromJson, stepName, formFragment, JsonFormConstants.SECTION_LABEL, sectionJson, listener);
                }

                if (sectionJson.has(JsonFormConstants.FIELDS)) {
                    JSONArray fields = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
                    fetchFields(viewsFromJson, stepName, formFragment, fields, listener);
                }


            }
        } catch (JSONException e) {
            Log.d(TAG, "Json exception occurred : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fetchFields(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                             JSONArray fields, CommonListener listener) {

        try {
            if (fields == null || fields.length() == 0) {
                return;
            }

            for (int i = 0; i < fields.length(); i++) {
                JSONObject childJson = fields.getJSONObject(i);
                fetchViews(viewsFromJson, stepName, formFragment, childJson.getString(JsonFormConstants.TYPE), childJson, listener);
            }
        } catch (JSONException e) {
            Log.d(TAG, "Json exception occurred : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fetchViews(List<View> viewsFromJson, String stepName, JsonFormFragment formFragment,
                            String type, JSONObject jsonObject, CommonListener listener) {

        try {
            List<View> views = map.get(type).getViewsFromJson(stepName, formFragment.getContext(), formFragment, jsonObject, listener);
            if (views.size() > 0) {
                viewsFromJson.addAll(views);
            }
        } catch (Exception e) {
            Log.d(TAG,
                    "Exception occurred in making view : Exception is : "
                            + e.getMessage());
            e.printStackTrace();
        }

    }

    public static JsonFormInteractor getInstance() {
        return INSTANCE;
    }
}

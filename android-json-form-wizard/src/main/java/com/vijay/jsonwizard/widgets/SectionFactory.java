package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class SectionFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);

        String text = jsonObject.getString("name");

        RelativeLayout rootLayout = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.item_section_label, null);
        TextView textView = (TextView) rootLayout.findViewById(R.id.section_label);
        textView.setText(text);

        views.add(rootLayout);
        return views;
    }

}

package com.vijay.jsonwizard.customviews;

import org.json.JSONException;

import android.support.v7.internal.widget.TintContextWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.widgets.EditTextFactory;

import java.util.ArrayList;

public class GenericTextWatcher implements TextWatcher, View.OnFocusChangeListener {

    private View   mView;
    private String mStepName;
    private ArrayList<View.OnFocusChangeListener> onFocusChangeListeners;
    private JsonFormFragment formFragment;

    public GenericTextWatcher(String stepName, JsonFormFragment formFragment, View view) {
        this.formFragment = formFragment;
        mView = view;
        mStepName = stepName;
        onFocusChangeListeners = new ArrayList<>();
        mView.setOnFocusChangeListener(this);
    }

    public void addOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        onFocusChangeListeners.add(onFocusChangeListener);
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        String text = (String) mView.getTag(R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        Log.d("RealtimeValidation", "afterTextChanged called");
        JsonApi api = null;
        if(mView.getContext() instanceof JsonApi) {
            api = (JsonApi) mView.getContext();
        } else if(mView.getContext() instanceof TintContextWrapper) {
            TintContextWrapper tintContextWrapper = (TintContextWrapper) mView.getContext();
            api = (JsonApi) tintContextWrapper.getBaseContext();
        } else {
            throw new RuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        String openMrsEntityParent = (String) mView.getTag(R.id.openmrs_entity_parent);
        String openMrsEntity = (String) mView.getTag(R.id.openmrs_entity);
        String openMrsEntityId = (String) mView.getTag(R.id.openmrs_entity_id);
        try {
            api.writeValue(mStepName, key, text, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        } catch (JSONException e) {
            // TODO- handle
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            JsonFormFragmentPresenter.validate(formFragment, mView, false);
        }
        for (View.OnFocusChangeListener curListener : onFocusChangeListeners) {
            curListener.onFocusChange(v, hasFocus);
        }
    }
}
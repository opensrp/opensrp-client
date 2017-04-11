package org.ei.opensrp.path.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.domain.EntityLookUp;
import org.ei.opensrp.path.fragment.PathJsonFormFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.MotherLookUpUtils;

public class LookUpTextWatcher implements TextWatcher {
    private static Map<String, EntityLookUp> lookUpMap;

    static {
        lookUpMap = new HashMap<>();

    }

    private View mView;
    private JsonFormFragment formFragment;
    private String mEntityId;
    private boolean mLookUp;


    public LookUpTextWatcher(JsonFormFragment formFragment, View view, String entityId, boolean lookUp) {
        this.formFragment = formFragment;
        mView = view;
        mEntityId = entityId;
        mLookUp = lookUp;
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

        String key = (String) mView.getTag(R.id.key);

        if (mLookUp) {
            EntityLookUp entityLookUp = new EntityLookUp();
            if (lookUpMap.containsKey(mEntityId)) {
                entityLookUp = lookUpMap.get(mEntityId);
            }

            entityLookUp.put(key, text);
            lookUpMap.put(mEntityId, entityLookUp);

            Context context = null;
            Listener<Map<String, List<CommonPersonObject>>> listener = null;
            if (formFragment instanceof PathJsonFormFragment) {
                PathJsonFormFragment pathJsonFormFragment = (PathJsonFormFragment) formFragment;
                context = pathJsonFormFragment.context();
                listener = pathJsonFormFragment.listener();
            }

            MotherLookUpUtils.motherLookUp(context, lookUpMap, listener, null);
        }
    }

}
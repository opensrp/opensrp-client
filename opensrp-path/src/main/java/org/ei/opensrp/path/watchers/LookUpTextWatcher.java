package org.ei.opensrp.path.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.apache.commons.lang3.StringUtils;
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

    private View mView;
    private JsonFormFragment formFragment;
    private String mEntityId;


    public LookUpTextWatcher(JsonFormFragment formFragment, View view, String entityId) {
        this.formFragment = formFragment;
        mView = view;
        mEntityId = entityId;
        lookUpMap = new HashMap<>();

    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    public void afterTextChanged(Editable editable) {
        String text = (String) mView.getTag(R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        String key = (String) mView.getTag(R.id.key);

        boolean afterLookUp = (Boolean) mView.getTag(R.id.after_look_up);
        if (afterLookUp) {
            mView.setTag(R.id.after_look_up, false);
            return;
        }

        EntityLookUp entityLookUp = new EntityLookUp();
        if (lookUpMap.containsKey(mEntityId)) {
            entityLookUp = lookUpMap.get(mEntityId);
        }

        if (StringUtils.isBlank(text)) {
            if (entityLookUp.containsKey(key)) {
                entityLookUp.remove(key);
            }
        } else {
            entityLookUp.put(key, text);
        }

        lookUpMap.put(mEntityId, entityLookUp);

        Context context = null;
        Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> listener = null;
        if (formFragment instanceof PathJsonFormFragment) {
            PathJsonFormFragment pathJsonFormFragment = (PathJsonFormFragment) formFragment;
            context = pathJsonFormFragment.context();
            listener = pathJsonFormFragment.motherLookUpListener();
        }

        if (mEntityId.equalsIgnoreCase("mother")) {
            MotherLookUpUtils.motherLookUp(context, lookUpMap.get(mEntityId), listener, null);
        }

    }

}
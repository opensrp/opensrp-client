package org.ei.opensrp.path.widgets;

import android.content.Context;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.EditTextFactory;

import org.ei.opensrp.path.watchers.LookUpTextWatcher;
import org.json.JSONObject;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathEditTextFactory extends EditTextFactory {

    @Override
    public void attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject, MaterialEditText editText) throws Exception {
        super.attachJson(stepName, context, formFragment, jsonObject, editText);

        // lookup hook
        boolean lookUp = false;
        if (jsonObject.has("look_up") && jsonObject.get("look_up").toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
            lookUp = true;
        }

        String entityId = "child";
        if (jsonObject.has("entity_id")) {
            entityId = jsonObject.getString("entity_id");
        }

        editText.addTextChangedListener(new LookUpTextWatcher(formFragment, editText, entityId, lookUp));
    }

}

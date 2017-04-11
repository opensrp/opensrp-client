package org.ei.opensrp.path.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.path.domain.EntityLookUp;
import org.ei.opensrp.path.interactors.PathJsonFormInteractor;

import java.util.List;
import java.util.Map;

import static util.Utils.getValue;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathJsonFormFragment extends JsonFormFragment {

    public static JsonFormFragment getFormFragment(String stepName) {
        PathJsonFormFragment jsonFormFragment = new PathJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, PathJsonFormInteractor.getInstance());
    }

    public Context context() {
        return Context.getInstance().updateApplicationContext(this.getActivity().getApplicationContext());
    }

    private Listener<Map<String, List<CommonPersonObject>>> lookUpListener = new Listener<Map<String, List<CommonPersonObject>>>() {
        @Override
        public void onEvent(Map<String, List<CommonPersonObject>> data) {

            for (Map.Entry<String, List<CommonPersonObject>> entry : data.entrySet()) {
                String entityId = entry.getKey();
                List<CommonPersonObject> list = entry.getValue();
                showLookUp(entityId, list);
            }
        }
    };

    public Listener<Map<String, List<CommonPersonObject>>> listener() {
        return lookUpListener;
    }

    public void showLookUp(String entityId, List<CommonPersonObject> list) {
        for (CommonPersonObject commonPersonObject : list) {
            String firstName = getValue(commonPersonObject.getColumnmaps(), "first_name", true);
            String lastName = getValue(commonPersonObject.getColumnmaps(), "last_name", true);
            Toast.makeText(getActivity(), commonPersonObject.getCaseId() + ":" + firstName + ":" + lastName, Toast.LENGTH_SHORT).show();
        }

    }
}



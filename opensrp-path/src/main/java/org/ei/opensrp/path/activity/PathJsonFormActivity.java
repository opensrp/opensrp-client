package org.ei.opensrp.path.activity;

import android.os.Bundle;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.ei.opensrp.path.fragment.PathJsonFormFragment;

/**
 * Created by keyman on 11/04/2017.
 */
public class PathJsonFormActivity extends JsonFormActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeFormFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, PathJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME)).commit();
    }
}

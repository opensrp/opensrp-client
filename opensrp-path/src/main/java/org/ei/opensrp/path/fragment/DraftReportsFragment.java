package org.ei.opensrp.path.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by coder on 6/7/17.
 */
public class DraftReportsFragment extends Fragment {

    public static DraftReportsFragment newInstance() {
        DraftReportsFragment fragment = new DraftReportsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
}

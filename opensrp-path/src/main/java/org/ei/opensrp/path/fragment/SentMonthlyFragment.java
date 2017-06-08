package org.ei.opensrp.path.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by coder on 6/7/17.
 */
public class SentMonthlyFragment extends Fragment {

    public static SentMonthlyFragment newInstance() {
        SentMonthlyFragment fragment = new SentMonthlyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
}

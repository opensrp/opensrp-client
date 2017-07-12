package org.ei.opensrp.path.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.ei.opensrp.path.R;
import org.ei.opensrp.path.activity.BaseRegisterActivity;
import org.ei.opensrp.path.activity.ChildSmartRegisterActivity;

/**
 * Created by Jason Rogena - jrogena@ona.io on 14/03/2017.
 */

@SuppressLint("ValidFragment")
public class NotInCatchmentDialogFragment extends DialogFragment implements View.OnClickListener {
    private final BaseRegisterActivity parentActivity;
    private final String zeirId;

    public NotInCatchmentDialogFragment(BaseRegisterActivity parentActivity, String zeirId) {
        this.parentActivity = parentActivity;
        this.zeirId = zeirId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    public static NotInCatchmentDialogFragment launchDialog(BaseRegisterActivity activity,
                                                            String dialogTag, String zeirId) {
        NotInCatchmentDialogFragment dialogFragment = new NotInCatchmentDialogFragment(activity,
                zeirId);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, dialogTag);

        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_not_in_catchment, container, false);
        Button cancelB = (Button) dialogView.findViewById(R.id.cancel_b);
        cancelB.setOnClickListener(this);
        Button recordB = (Button) dialogView.findViewById(R.id.record_b);
        recordB.setOnClickListener(this);
        Button searchB = (Button) dialogView.findViewById(R.id.search_b);
        searchB.setOnClickListener(this);
        return dialogView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_b) {
            if (parentActivity instanceof ChildSmartRegisterActivity) {
                this.dismiss();
                ((ChildSmartRegisterActivity) parentActivity).startAdvancedSearch();
                android.support.v4.app.Fragment currentFragment =
                        ((ChildSmartRegisterActivity) parentActivity).
                                findFragmentByPosition(ChildSmartRegisterActivity
                                        .ADVANCED_SEARCH_POSITION);
                ((AdvancedSearchFragment) currentFragment).getZeirId().setText(zeirId);
            }
        } else if (v.getId() == R.id.record_b) {
            parentActivity.startFormActivity("out_of_catchment_service", zeirId, "");
        } else if (v.getId() == R.id.cancel_b) {
            this.dismiss();
        }
    }
}

package org.ei.opensrp.indonesia.view.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Strings;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.lib.FlurryFacade;
import org.ei.opensrp.indonesia.view.controller.NavigationControllerINA;
import org.ei.opensrp.util.EasyMap;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.NavigationController;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.SmartRegisterDialogFragment;
import org.ei.opensrp.view.viewpager.SampleViewPager;

import butterknife.Bind;

/**
 * Created by Dimas Ciputra on 3/8/15.
 */
public abstract class BidanSecuredNativeSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private static final String DIALOG_TAG = "dialog";
    protected NavigationControllerINA navigationControllerINA;

    @Bind(R.id.view_pager)
    public SampleViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationControllerINA = new NavigationControllerINA(this, anmController);

        //error handling
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });
    }

    @Override
    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SmartRegisterDialogFragment
                .newInstance(this, dialogOptionModel, tag)
                .show(ft, DIALOG_TAG);
    }

    protected void onShowDialogOptionSelection(EditOption option, SmartRegisterClient client, CharSequence[] randomChars) {
        showDoubleSelectionDialog(option, client, randomChars, null);
    }

    protected void onShowDialogOptionSelectionWithMetadata(EditOption editOption, SmartRegisterClient client, CharSequence[] charSequences, String metadata) {
        showDoubleSelectionDialog(editOption, client, charSequences, metadata);
    }


    public void showDoubleSelectionDialog(final EditOption editOption, final SmartRegisterClient client, final CharSequence[] charSequences, final String metadata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_double_selection);

        FlurryFacade.logEvent(editOption.name().replace(" ", "_").toLowerCase(), EasyMap.create("nama", client.name()).put("id", client.entityId()).map());

        FlurryFacade.logEvent("on_double_selection_dialog_showed");

        builder.setItems(charSequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ((charSequences[which]).toString().toLowerCase().equals(client.name().toLowerCase())) {
                    FlurryFacade.logEvent("success_on_double_selection_dialog");
                    if (Strings.isNullOrEmpty(metadata)) {
                        onEditSelection(editOption, client);
                    }
                    else {
                        onEditSelectionWithMetadata(editOption, client, metadata);
                    }
                } else {
                    FlurryFacade.logEvent("fail_on_double_selection_dialog",
                            EasyMap.create("selected_name", (charSequences[which]).toString())
                                    .put("name", client.name())
                                    .map());
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FlurryFacade.logEvent("double_selection_dialog_dismissed");
            }
        });

        alertDialog.show();
    }

}

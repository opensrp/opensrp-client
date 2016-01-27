package org.ei.opensrp.indonesia.view.fragment;

import android.os.Bundle;

import org.ei.opensrp.indonesia.view.activity.BidanSecuredNativeSmartRegisterActivity;
import org.ei.opensrp.indonesia.view.controller.NavigationControllerINA;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;

/**
 * Created by koros on 10/23/15.
 */
public abstract class BidanSecuredNativeSmartRegisterFragment extends SecuredNativeSmartRegisterFragment {

    private static final String DIALOG_TAG = "dialog";
    protected NavigationControllerINA navigationControllerINA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationControllerINA = new NavigationControllerINA(getActivity(), anmController);

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
        ((BidanSecuredNativeSmartRegisterActivity)getActivity()).showFragmentDialog(dialogOptionModel,tag);
    }

    protected void onShowDialogOptionSelection(EditOption option, SmartRegisterClient client, CharSequence[] randomChars) {
        showDoubleSelectionDialog(option, client, randomChars, null);
    }

    protected void onShowDialogOptionSelectionWithMetadata(EditOption editOption, SmartRegisterClient client, CharSequence[] charSequences, String metadata) {
        showDoubleSelectionDialog(editOption, client, charSequences, metadata);
    }


    protected void showDoubleSelectionDialog(final EditOption editOption, final SmartRegisterClient client, final CharSequence[] charSequences, final String metadata) {
        ((BidanSecuredNativeSmartRegisterActivity)getActivity()).showDoubleSelectionDialog(editOption, client, charSequences, metadata);
    }
}

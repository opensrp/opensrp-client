package org.ei.opensrp.indonesia.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.activity.BidanSecuredNativeSmartRegisterActivity;
import org.ei.opensrp.indonesia.view.card.DetailsNativeCard;
import org.ei.opensrp.indonesia.view.card.RiskFlagsNativeCard;
import org.ei.opensrp.indonesia.view.controller.ProfileChildDetailController;
import org.ei.opensrp.view.fragment.SecuredFragment;

import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by Dimas on 12/10/2015.
 */
public class ChildProfileViewFragment extends SecuredFragment {
    private ProfileChildDetailController profileChildDetailController;
    private String caseId;

    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();
    DetailsNativeCard detailsCard;
    RiskFlagsNativeCard riskFlagsCard;

    @Override
    protected void onCreation() {
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
    protected void onResumption() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_main_card_view, container, false);
        view.findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);
        return view;
    }

    private void initialize() {

        // Create a card
        detailsCard = new DetailsNativeCard(getActivity(), controller().detailMap());
        detailsCard.setClient(controller().get());
        detailsCard.init();

        // Set card in cardView
        CardViewNative detailsCardView = (CardViewNative) getActivity().findViewById(R.id.detail_list_card);
        if(detailsCardView.getCard() != null) {
            detailsCardView.replaceCard(detailsCard);
        } else {
            detailsCardView.setCard(detailsCard);
        }

        riskFlagsCard = new RiskFlagsNativeCard(getActivity());
        riskFlagsCard.setBidanClient(controller().get());
        riskFlagsCard.init();

        // Set card in cardView
        CardViewNative riskFlagsCardView = (CardViewNative) getActivity().findViewById(R.id.risk_flags_list_card);
        if(riskFlagsCardView.getCard() != null) {
            riskFlagsCardView.replaceCard(riskFlagsCard);
        } else {
            riskFlagsCardView.setCard(riskFlagsCard);
        }

    }

    private ProfileChildDetailController controller() {
        if(profileChildDetailController == null) {
            profileChildDetailController = new ProfileChildDetailController(getActivity(), caseId(),
                    ((Context)context).allKartuIbus(),
                    ((Context)context).allKohort());
        }
        return profileChildDetailController;
    }

    private String caseId() {
        return this.caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
        initialize();
    }

    public void clearCard() {
        detailsCard = null;
        riskFlagsCard = null;
        caseId = null;
        profileChildDetailController = null;
    }

    public class NavBarActionsHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_layout:
                case R.id.btn_back_to_home:
                    goBack();
                    break;
            }
        }
    }

    private void goBack() {
        clearCard();
        ((BidanSecuredNativeSmartRegisterActivity)getActivity()).mPager.setCurrentItem(0);
    }

}

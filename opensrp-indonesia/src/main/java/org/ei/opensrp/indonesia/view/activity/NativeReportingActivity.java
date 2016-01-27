package org.ei.opensrp.indonesia.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ei.opensrp.indonesia.Context;
import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.view.card.ReportingNativeCard;
import org.ei.opensrp.indonesia.view.controller.ReportingController;
import org.ei.opensrp.view.activity.SecuredActivity;

import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by Dimas Ciputra on 6/23/15.
 */
public class NativeReportingActivity extends SecuredActivity {

    private final NavBarActionsHandler navBarActionsHandler = new NavBarActionsHandler();

    private ReportingController controller;

    private ReportingNativeCard reportingNativeCard;

    @Override
    protected void onCreation() {
        setContentView(R.layout.reporting_main_card_view);

        findViewById(R.id.btn_back_to_home).setOnClickListener(navBarActionsHandler);
        ((TextView) findViewById(R.id.txt_title_nav)).setText("Reporting");

        initialize();

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

    private void initialize() {

        controller = new ReportingController(
                ((Context)context).kartuIbuRegisterController(),
                ((Context)context).kartuIbuANCRegisterController(),
                ((Context)context).anakRegisterController());

        reportingNativeCard = new ReportingNativeCard(this, controller);
        reportingNativeCard.init();

        CardViewNative cardViewNative = (CardViewNative) findViewById(R.id.reporting_list_card);
        cardViewNative.setCard(reportingNativeCard);

    }

    public class NavBarActionsHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_layout:
                case R.id.btn_back_to_home:
                    finish();
                    break;
            }
        }
    }
}
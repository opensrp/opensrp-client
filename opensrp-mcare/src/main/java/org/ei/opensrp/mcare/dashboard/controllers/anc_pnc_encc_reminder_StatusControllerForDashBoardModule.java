package org.ei.opensrp.mcare.dashboard.controllers;

import android.database.Cursor;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;

import java.util.Date;
import java.util.HashMap;

import dashboard.opensrp.org.jandjdashboard.controller.reminderVisitStatusController;
import dashboard.opensrp.org.jandjdashboard.controller.upcomingScheduleStatusController;

/**
 * Created by raihan on 1/22/18.
 */

public class anc_pnc_encc_reminder_StatusControllerForDashBoardModule extends reminderVisitStatusController {


    @Override
    public HashMap<String, String> ancVisitQuery(Date from, Date to, String riskflag) {
        return null;
    }

    @Override
    public HashMap<String, String> pncVisitQuery(Date from, Date to, String riskflag) {
        return null;
    }

    @Override
    public HashMap<String, String> neonatalVisitQuery(Date from, Date to, String riskflag) {
        return null;
    }
}

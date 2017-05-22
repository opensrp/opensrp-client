package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;

import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;


/**
 * Created by keyman on 3/01/2017.
 */
public class RecurringIntentService extends IntentService {
    private static final String TAG = RecurringIntentService.class.getCanonicalName();
    public static final String ITN_PROVIDED = "ITN_Provided";
    public static final String ITN_PROVIDED_DATE = "ITN_Provided_Date";
    public static final String CHILD_HAS_NET = "Child_Has_Net";

    private RecurringServiceRecordRepository recurringServiceRecordRepository;

    public RecurringIntentService() {
        super("RecurringIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();
        return super.onStartCommand(intent, flags, startId);
    }
}

package util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.domain.ServiceWrapper;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;
import org.ei.opensrp.path.view.ServiceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyman on 23/05/2017.
 */
public class RecurringServiceUtils {

    public static void onUndoService(ServiceWrapper tag, View view, String baseEntityId) {
        if (tag != null) {

            if (tag.getDbKey() != null) {
                RecurringServiceRecordRepository recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();
                Long dbKey = tag.getDbKey();
                recurringServiceRecordRepository.deleteServiceRecord(dbKey);

                tag.setUpdatedVaccineDate(null, false);
                tag.setDbKey(null);

                List<ServiceRecord> serviceRecordList = recurringServiceRecordRepository.findByEntityId(baseEntityId);

                ArrayList<ServiceWrapper> wrappers = new ArrayList<>();
                wrappers.add(tag);
                updateServiceGroupViews(view, wrappers, serviceRecordList, true);
            }
        }
    }


    public static void updateServiceGroupViews(View view, final ArrayList<ServiceWrapper> wrappers, List<ServiceRecord> serviceRecordList) {
        updateServiceGroupViews(view, wrappers, serviceRecordList, false);
    }

    public static void updateServiceGroupViews(View view, final ArrayList<ServiceWrapper> wrappers, final List<ServiceRecord> serviceRecordList, final boolean undo) {
        if (view == null || !(view instanceof ServiceGroup)) {
            return;
        }
        final ServiceGroup serviceGroup = (ServiceGroup) view;
        serviceGroup.setModalOpen(false);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (undo) {
                serviceGroup.setServiceRecordList(serviceRecordList);
                serviceGroup.updateWrapperStatus(wrappers);
            }
            serviceGroup.updateViews(wrappers);

        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (undo) {
                        serviceGroup.setServiceRecordList(serviceRecordList);
                        serviceGroup.updateWrapperStatus(wrappers);
                    }
                    serviceGroup.updateViews(wrappers);
                }
            });
        }
    }

    public static ServiceGroup getLastOpenedServiceView(List<ServiceGroup> serviceGroups) {
        if (serviceGroups == null) {
            return null;
        }

        for (ServiceGroup serviceGroup : serviceGroups) {
            if (serviceGroup.isModalOpen()) {
                return serviceGroup;
            }
        }

        return null;
    }

    public static void saveService(ServiceWrapper tag, String baseEntityId, String providerId, String locationId) {
        if (tag.getUpdatedVaccineDate() == null) {
            return;
        }

        RecurringServiceRecordRepository recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();

        ServiceRecord serviceRecord = new ServiceRecord();
        if (tag.getDbKey() != null) {
            serviceRecord = recurringServiceRecordRepository.find(tag.getDbKey());
        }
        serviceRecord.setBaseEntityId(baseEntityId);
        serviceRecord.setRecurringServiceId(tag.getTypeId());
        serviceRecord.setDate(tag.getUpdatedVaccineDate().toDate());
        serviceRecord.setAnmId(providerId);
        serviceRecord.setValue(tag.getValue());
        serviceRecord.setLocationId(locationId);

        recurringServiceRecordRepository.add(serviceRecord);
        tag.setDbKey(serviceRecord.getId());
    }

}

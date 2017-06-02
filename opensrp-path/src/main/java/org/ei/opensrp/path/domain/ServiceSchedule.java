package org.ei.opensrp.path.domain;

import android.util.Log;

import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.AlertStatus;
import org.ei.opensrp.domain.ServiceRecord;
import org.ei.opensrp.domain.ServiceType;
import org.ei.opensrp.path.application.VaccinatorApplication;
import org.ei.opensrp.path.repository.RecurringServiceRecordRepository;
import org.ei.opensrp.path.repository.RecurringServiceTypeRepository;
import org.ei.opensrp.service.AlertService;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.VaccinateActionUtils;
import util.VaccinatorUtils;

/**
 * Created by Keyman on 26/05/2017.
 */

public class ServiceSchedule {

    private final ServiceTrigger dueTrigger;
    private final ServiceTrigger expiryTrigger;


    public static ServiceSchedule getServiceSchedule(JSONObject schedule)
            throws JSONException {
        ServiceTrigger dueTrigger = ServiceTrigger.init(schedule.getJSONObject("due"));
        ServiceTrigger expiryTrigger = ServiceTrigger.init(schedule.optJSONObject("expiry"));
        return new ServiceSchedule(dueTrigger, expiryTrigger);
    }

    public ServiceSchedule(ServiceTrigger dueTrigger, ServiceTrigger expiryTrigger) {
        this.dueTrigger = dueTrigger;
        this.expiryTrigger = expiryTrigger;
    }

    public static void updateOfflineAlerts(String baseEntityId, DateTime dob) {
        RecurringServiceTypeRepository recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
        List<String> types = recurringServiceTypeRepository.fetchTypes();
        for (String type : types) {
            updateOfflineAlerts(type, baseEntityId, dob);
        }
    }

    public static void updateOfflineAlerts(String type, String baseEntityId, DateTime dob) {
        try {
            if (baseEntityId == null || dob == null) {
                return;
            }

            RecurringServiceTypeRepository recurringServiceTypeRepository = VaccinatorApplication.getInstance().recurringServiceTypeRepository();
            RecurringServiceRecordRepository recurringServiceRecordRepository = VaccinatorApplication.getInstance().recurringServiceRecordRepository();
            AlertService alertService = VaccinatorApplication.getInstance().context().alertService();

            List<ServiceType> serviceTypes = recurringServiceTypeRepository.findByType(type);

            String[] alertArray = VaccinateActionUtils.allAlertNames(serviceTypes);

            List<Alert> newAlerts = new ArrayList<>();

            // Get all the administered services
            List<ServiceRecord> issuedServices = recurringServiceRecordRepository.findByEntityId(baseEntityId);
            alertService.deleteOfflineAlerts(baseEntityId, alertArray);

            List<Alert> existingAlerts = alertService.findByEntityIdAndAlertNames(baseEntityId, alertArray);

            for (ServiceType serviceType : serviceTypes) {
                Alert curAlert = getOfflineAlert(serviceType, issuedServices, baseEntityId, dob);

                if (curAlert == null) {
                    break;
                } else {
                    // Check if the current alert already exists for the entityId
                    boolean exists = false;
                    for (Alert curExistingAlert : existingAlerts) {
                        if (curExistingAlert.scheduleName().equalsIgnoreCase(curAlert.scheduleName())
                                && curExistingAlert.caseId().equalsIgnoreCase(curAlert.caseId())) {
                            exists = true;
                            break;
                        }
                    }

                    // Check if service is already given
                    if (!exists) {
                        for (ServiceRecord serviceRecord : issuedServices) {
                            if (curAlert.scheduleName().equalsIgnoreCase(serviceRecord.getName()) || curAlert.visitCode().equalsIgnoreCase(serviceRecord.getName())) {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (!exists) {
                        // Insert alert into table
                        newAlerts.add(curAlert);
                        alertService.create(curAlert);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(ServiceSchedule.class.getName(), e.toString(), e);
        }

    }


    public static Alert getOfflineAlert(final ServiceType serviceType, final List<ServiceRecord> issuedServices, final String baseEntityId, final DateTime dateOfBirth) {

        try {
            DateTime dueDateTime = VaccinatorUtils.getServiceDueDate(serviceType, dateOfBirth, issuedServices);
            DateTime expiryDateTime = VaccinatorUtils.getServiceExpiryDate(serviceType, dateOfBirth);

            // Use the trigger date as a reference, since that is what is mostly used
            AlertStatus alertStatus = calculateAlertStatus(dueDateTime);

            if (alertStatus != null) {
                Date startDate = dueDateTime == null ? dateOfBirth.toDate() : dueDateTime.toDate();
                Date expiryDate = expiryDateTime == null ? null : expiryDateTime.toDate();
                return new Alert(baseEntityId, serviceType.getName(), serviceType.getName().toLowerCase().replace(" ", ""),
                        alertStatus, startDate == null ? null : DateUtil.yyyyMMdd.format(startDate), expiryDate == null ? null : DateUtil.yyyyMMdd.format(expiryDate), true);
            }
            return null;
        } catch (Exception e) {
            Log.e(ServiceSchedule.class.getName(), e.toString(), e);
            return null;
        }
    }

    private static AlertStatus calculateAlertStatus(DateTime referenceDate) {
        if (referenceDate != null) {
            Calendar refCalendarDate = Calendar.getInstance();
            refCalendarDate.setTime(referenceDate.toDate());
            standardiseCalendarDate(refCalendarDate);

            Calendar today = Calendar.getInstance();
            standardiseCalendarDate(today);

            if (refCalendarDate.getTimeInMillis() <= today.getTimeInMillis()) {// Due
                return AlertStatus.normal;
            }
        }

        return null;
    }

    public static void standardiseCalendarDate(Calendar calendarDate) {
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);
    }

    public static DateTime standardiseDateTime(DateTime dateTime) {
        if (dateTime != null) {
            return dateTime.withTime(0, 0, 0, 0);
        }
        return null;
    }

    public ServiceTrigger getDueTrigger() {
        return dueTrigger;
    }

    public ServiceTrigger getExpiryTrigger() {
        return expiryTrigger;
    }

    public static DateTime addOffsetToDateTime(DateTime dateTime, List<String> offsets) {
        DateTime afterOffset = dateTime;
        if (dateTime != null && offsets != null && !offsets.isEmpty()) {
            for (String offset : offsets) {
                afterOffset = addOffsetToDateTime(afterOffset, offset);
            }
        }
        return afterOffset;
    }

    public static DateTime addOffsetToDateTime(DateTime dateTime, String offset) {
        try {
            DateTime afterOffset = dateTime;
            if (dateTime != null && offset != null) {
                offset = offset.replace(" ", "").toLowerCase();
                Pattern p1 = Pattern.compile("([-+]{1})(.*)");
                Matcher m1 = p1.matcher(offset);
                if (m1.find()) {
                    String comparitorString = m1.group(1);
                    String valueString = m1.group(2);

                    int comparitor = 1;
                    if (comparitorString.equals("-")) {
                        comparitor = -1;
                    }

                    String[] values = valueString.split(",");
                    for (int i = 0; i < values.length; i++) {
                        Pattern p2 = Pattern.compile("(\\d+)([dwmy]{1})");
                        Matcher m2 = p2.matcher(values[i]);

                        if (m2.find()) {
                            int curValue = comparitor * Integer.parseInt(m2.group(1));
                            String fieldString = m2.group(2);
                            if (fieldString.equals("d")) {
                                afterOffset = afterOffset.plusDays(curValue);
                            } else if (fieldString.equals("m")) {
                                afterOffset = afterOffset.plusMonths(curValue);
                            } else if (fieldString.equals("y")) {
                                afterOffset = afterOffset.plusYears(curValue);
                            }
                        }
                    }
                }
            }

            return afterOffset;
        } catch (Exception e) {
            Log.e(ServiceSchedule.class.getName(), e.toString(), e);
            return dateTime;
        }
    }
}


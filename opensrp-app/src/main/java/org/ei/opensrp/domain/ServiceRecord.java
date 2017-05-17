package org.ei.opensrp.domain;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by keyman on 3/1/17.
 */
public class ServiceRecord {
    Long id;
    String baseEntityId;
    Long recurringServiceId;
    String value;
    Date date;
    String anmId;
    String locationId;
    String syncStatus;
    String eventId;
    String formSubmissionId;
    Long updatedAt;

    ServiceType serviceType;


    public ServiceRecord() {
    }

    public ServiceRecord(Long id, String baseEntityId, Long recurringServiceId, String value, Date date, String anmId, String locationId, String syncStatus, String eventId, String formSubmissionId, Long updatedAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.recurringServiceId = recurringServiceId;
        this.value = value;
        this.date = date;
        this.anmId = anmId;
        this.locationId = locationId;
        this.syncStatus = syncStatus;
        this.eventId = eventId;
        this.formSubmissionId = formSubmissionId;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public Long getRecurringServiceId() {
        return recurringServiceId;
    }

    public void setRecurringServiceId(Long recurringServiceId) {
        this.recurringServiceId = recurringServiceId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAnmId() {
        return anmId;
    }

    public void setAnmId(String anmId) {
        this.anmId = anmId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getFormSubmissionId() {
        return formSubmissionId;
    }

    public void setFormSubmissionId(String formSubmissionId) {
        this.formSubmissionId = formSubmissionId;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getName(){
        if(serviceType == null){
            return null;
        }
        return serviceType.getName();
    }
}

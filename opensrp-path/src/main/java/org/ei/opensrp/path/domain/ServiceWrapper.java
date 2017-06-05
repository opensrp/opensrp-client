package org.ei.opensrp.path.domain;

import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.ServiceType;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by keyman on 16/11/2016.
 */
public class ServiceWrapper implements Serializable {
    private String id;
    private Long dbKey;
    private Photo photo;
    private String defaultName;
    private String gender;
    private String status;
    private DateTime vaccineDate;
    private Alert alert;
    private String previousVaccineId;
    private DateTime dob;

    private String color;

    private String patientName;
    private String patientNumber;

    private DateTime updatedVaccineDate;

    private boolean today;
    private boolean synced;

    private ServiceType serviceType;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DateTime getVaccineDate() {
        return vaccineDate;
    }

    public void setVaccineDate(DateTime vaccineDate) {
        this.vaccineDate = vaccineDate;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public String getPreviousVaccineId() {
        return previousVaccineId;
    }

    public void setPreviousVaccine(String previousVaccineId) {
        this.previousVaccineId = previousVaccineId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public DateTime getUpdatedVaccineDate() {
        return updatedVaccineDate;
    }

    public void setUpdatedVaccineDate(DateTime updatedVaccineDate, boolean today) {
        this.today = today;
        this.updatedVaccineDate = updatedVaccineDate;
    }

    public boolean isToday() {
        return today;
    }

    public String getVaccineDateAsString() {
        return vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "";
    }

    public String getUpdatedVaccineDateAsString() {
        return updatedVaccineDate != null ? updatedVaccineDate.toString("yyyy-MM-dd") : "";
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Photo getPhoto() {
        return photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public void setDob(DateTime dob) {
        this.dob = dob;
    }

    public DateTime getDob() {
        return dob;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getName() {
        if (serviceType == null) {
            return defaultName;
        }
        return serviceType.getName();
    }

    public String getUnits() {
        if (serviceType == null) {
            return null;
        }
        return serviceType.getUnits();
    }

    public String getType() {
        if (serviceType == null) {
            return null;
        }
        return serviceType.getType();
    }


    public Long getTypeId() {
        if (serviceType == null) {
            return null;
        }
        return serviceType.getId();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

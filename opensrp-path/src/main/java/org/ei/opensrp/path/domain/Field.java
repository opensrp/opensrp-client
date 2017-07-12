package org.ei.opensrp.path.domain;

import java.util.Map;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 16-Nov-15.
 */
public class Field {
    private String caseId ;
    private String reportType;
    private String centerName;
    private String date;
    private final Map<String ,String> wastedDetails;
    private final Map<String ,String> receivedDetails;
    private final Map<String ,String> balancedDetails;


    public Field(String caseId, String reportType, String centerName, String date, Map<String, String> wastedDetails, Map<String, String> receivedDetails, Map<String, String> balancedDetails) {
        this.caseId = caseId;
        this.reportType = reportType;
        this.centerName = centerName;
        this.date = date;
        this.wastedDetails = wastedDetails;
        this.receivedDetails = receivedDetails;
        this.balancedDetails = balancedDetails;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map<String, String> getWastedDetails() {
        return wastedDetails;
    }

    public Map<String, String> getReceivedDetails() {
        return receivedDetails;
    }

    public Map<String, String> getBalancedDetails() {
        return balancedDetails;
    }
}

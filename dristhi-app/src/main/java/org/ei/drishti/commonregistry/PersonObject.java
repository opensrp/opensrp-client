package org.ei.drishti.commonregistry;

import java.util.Map;

/**
 * Created by user on 2/12/15.
 */
public class PersonObject {
    private String caseId;
    private Map<String, String> details;
    private String type;
    private String relationalid;

    public PersonObject(String caseId, String relationalid ,Map<String, String> details, String type) {
        this.details = details;
        this.caseId = caseId;
        this.type = type;
        this.relationalid = relationalid;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getRelationalId() {
        return relationalid;
    }
}

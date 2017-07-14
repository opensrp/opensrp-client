package org.ei.opensrp.path.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by coder on 6/6/17.
 */
public class ReportHia2Indicator implements Serializable {


    @JsonProperty
    private String indicatorCode;

    @JsonProperty
    private String label;

    @JsonProperty
    private String dhisId;

    @JsonProperty
    private String description;

    @JsonProperty
    private String category;

    @JsonProperty
    private String value;

    @JsonProperty
    private String providerId;

    private String createdAt;
    private String updatedAt;


    public ReportHia2Indicator() {
    }

    public ReportHia2Indicator(String indicatorCode, String label, String dhisId, String description, String category, String value, String providerId, String createdAt, String updatedAt) {
        this.indicatorCode = indicatorCode;
        this.label = label;
        this.dhisId = dhisId;
        this.description = description;
        this.category = category;
        this.value = value;
        this.providerId = providerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDhisId() {
        return dhisId;
    }

    public void setDhisId(String dhisId) {
        this.dhisId = dhisId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setHia2Indicator(Hia2Indicator hia2Indicator) {
        if (hia2Indicator != null) {
            this.indicatorCode = hia2Indicator.getIndicatorCode();
            this.label = hia2Indicator.getLabel();
            this.dhisId = hia2Indicator.getDhisId();
            this.description = hia2Indicator.getDescription();
            this.category = hia2Indicator.getCategory();
        }
    }
}
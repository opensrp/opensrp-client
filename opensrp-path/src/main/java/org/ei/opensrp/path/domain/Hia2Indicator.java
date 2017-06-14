package org.ei.opensrp.path.domain;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * Created by coder on 6/6/17.
 */
public class Hia2Indicator {
    @JsonProperty
    private String providerId;
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
    private Date month;
    @JsonProperty
    private Date createdAt;
    @JsonProperty
    private Date updatedAt;

    public Hia2Indicator() {
    }

    public Hia2Indicator(String providerId, String indicatorCode, String label, String dhisId, String description, String category, String value, Date month, Date createdAt, Date updatedAt) {
        this.providerId = providerId;
        this.indicatorCode = indicatorCode;
        this.label = label;
        this.dhisId = dhisId;
        this.description = description;
        this.category = category;
        this.value = value;
        this.month = month;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
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

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
package org.ei.opensrp.path.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Jason Rogena - jrogena@ona.io on 15/06/2017.
 */

public class Tally implements Serializable {
    protected Hia2Indicator indicator;
    @JsonProperty
    protected long id;
    @JsonProperty
    protected String value;
    @JsonProperty
    protected String providerId;
    @JsonProperty
    protected Date updatedAt;


    @JsonProperty
    protected Date createdAt;

    public Tally() {
    }

    public Hia2Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Hia2Indicator indicator) {
        this.indicator = indicator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public JSONObject getJsonObject() throws JsonProcessingException, JSONException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject tally = new JSONObject(objectMapper.writeValueAsString(this));
        JSONObject hia2Indicator = new JSONObject(objectMapper.writeValueAsString(indicator));

        JSONObject combined = new JSONObject();
        Iterator hia2Iterator = hia2Indicator.keys();
        while (hia2Iterator.hasNext()) {
            String curKey = (String) hia2Iterator.next();
            combined.put(curKey, hia2Indicator.get(curKey));
        }

        Iterator tallyIterator = tally.keys();
        while (tallyIterator.hasNext()) {
            String curKey = (String) tallyIterator.next();
            combined.put(curKey, tally.get(curKey));
        }

        return combined;
    }
}

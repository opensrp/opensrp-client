package org.ei.opensrp.path.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 15/06/2017.
 */

public class Tally implements Serializable {
    public enum Type {DAILY, MONTHLY}

    private final Type type;
    private Hia2Indicator indicator;
    @JsonProperty
    private long id;
    @JsonProperty
    private String value;
    @JsonProperty
    private Date date;
    @JsonProperty
    private String providerId;
    @JsonProperty
    private Date updatedAt;

    public Tally(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public static List<JSONObject> getJsonObjects(List<Tally> tallies) throws JsonProcessingException, JSONException {
        List<JSONObject> result = new ArrayList<>();

        if (tallies != null) {
            for (Tally currTally : tallies) {
                result.add(currTally.getJsonObject());
            }
        }

        return result;
    }
}

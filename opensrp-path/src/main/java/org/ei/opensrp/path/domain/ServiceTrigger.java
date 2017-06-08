package org.ei.opensrp.path.domain;

import android.util.Pair;

import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Keyman on 26/05/2017.
 */

public class ServiceTrigger {
    public enum Reference {
        DOB,
        PREREQUISITE,
        MULTIPLE
    }

    private final Reference reference;
    private final String offset;
    private final String prerequisite;
    private final Multiple multiple;

    public static ServiceTrigger init(JSONObject data) throws JSONException {
        if (data != null) {
            if (data.getString("reference").equalsIgnoreCase(Reference.DOB.name())) {
                return new ServiceTrigger(data.getString("offset"));
            } else if (data.getString("reference").equalsIgnoreCase(Reference.PREREQUISITE.name())) {
                String prerequisite = data.getString("prerequisite");
                if (prerequisite != null) {
                    return new ServiceTrigger(data.getString("offset"), prerequisite);
                }
            } else if (data.getString("reference").equalsIgnoreCase(Reference.MULTIPLE.name())) {
                JSONObject multipleJson = data.getJSONObject("multiple");
                String condition = null;
                List<String> prerequisites = new ArrayList<>();
                if (multipleJson != null) {
                    if (multipleJson.has("condition")) {
                        condition = multipleJson.getString("condition");
                    }
                    if (multipleJson.has("prerequisites")) {
                        JSONArray jsonArray = multipleJson.getJSONArray("prerequisites");
                        if (jsonArray != null)
                            for (int i = 0; i < jsonArray.length(); i++) {
                                prerequisites.add(jsonArray.getString(i));
                            }
                    }
                }

                return new ServiceTrigger(data.getString("offset"), new Multiple(condition, prerequisites));
            }
        }

        return null;
    }

    public ServiceTrigger(String offset) {
        this.reference = Reference.DOB;
        this.offset = offset;
        this.prerequisite = null;
        this.multiple = null;
    }

    public ServiceTrigger(String offset, String prerequisite) {
        this.reference = Reference.PREREQUISITE;
        this.offset = offset;
        this.prerequisite = prerequisite;
        this.multiple = null;
    }

    public ServiceTrigger(String offset, Multiple multiple) {
        this.reference = Reference.MULTIPLE;
        this.offset = offset;
        this.prerequisite = null;
        this.multiple = multiple;
    }

    public Reference getReference() {
        return reference;
    }

    public Multiple getMultiple() {
        return multiple;
    }

    public String getOffset() {
        return offset;
    }

    public String getPrerequisite() {
        return prerequisite;
    }


    public static class Multiple {
        private String condition;
        private List<String> prerequisites;

        public Multiple(String condition, List<String> prerequisites) {
            this.condition = condition;
            this.prerequisites = prerequisites;
        }

        public String getCondition() {
            return condition;
        }

        public List<String> getPrerequisites() {
            return prerequisites;
        }
    }


}

package org.ei.opensrp.path.domain;

import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.VaccinateActionUtils;

/**
 * Created by keyman on 20/01/2017.
 */
public class EditFormSubmissionWrapper implements Serializable {

    private String formData;

    private String entityId;

    private String formName;

    private String metaData;

    private String category;

    private List<EditWrapper> edits;

    public EditFormSubmissionWrapper(String formData, String entityId, String formName, String metaData, String category) {
        this.formData = formData;
        this.entityId = entityId;
        this.formName = formName;
        this.metaData = metaData;
        this.category = category;
    }

    public void add(EditWrapper tag) {
        if (tag.getNewValue() != null) {
            edits().add(tag);
        }
    }

    public void addAll(List<EditWrapper> tags) {
        for(EditWrapper tag: tags) {
            if (tag.getNewValue() != null) {
                edits().add(tag);
            }
        }
    }

    public void remove(EditWrapper tag) {
        edits().remove(tag);
    }

    public void removeAll(List<EditWrapper> tags) {
        edits().removeAll(tags);
    }

    public int updates() {
        return edits().size();
    }

    public List<EditWrapper> edits() {
        if (edits == null) {
            edits = new ArrayList<>();
        }
        return edits;
    }

    public String updateFormSubmission() {
        try {
            if (updates() <= 0)
                return null;


            String parent = "";
            if (category.equals("child")) {
                parent = "Child_Vaccination_Enrollment";
            } else if (category.equals("woman")) {
                parent = "Woman_TT_Enrollment_Form";
            }

            JSONObject formSubmission = XML.toJSONObject(formData);

            JSONObject encounterJson = VaccinateActionUtils.find(formSubmission, parent);

            for (EditWrapper  editWrapper : edits()) {
                String field =  editWrapper.getField();
                String currentValue = editWrapper.getCurrentValue();
                String newValue = editWrapper.getNewValue();

                if(field.trim().contains(" ") && currentValue.trim().contains(" ") && newValue.trim().contains(" ")) {
                    String[] fields  =  field.split("\\s+", 2);
                    String[] currentValues = currentValue.split("\\s+", 2);
                    String[] newValues = newValue.split("\\s+", 2);

                    for(int i = 0; i< fields.length; i++){
                        editJson(encounterJson, fields[i], currentValues[i], newValues[i]);
                    }
                } else {
                    editJson(encounterJson, field, editWrapper.getCurrentValue(), editWrapper.getNewValue());
                }
            }

            DateTime currentDateTime = new DateTime(new Date());
            VaccinateActionUtils.updateJson(encounterJson, "start", currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            VaccinateActionUtils.updateJson(encounterJson, "end", currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            VaccinateActionUtils.updateJson(encounterJson, "today", currentDateTime.toString("yyyy-MM-dd"));

            VaccinateActionUtils.updateJson(encounterJson, "deviceid", "Error: could not determine deviceID");
            VaccinateActionUtils.updateJson(encounterJson, "subscriberid", "no subscriberid property in enketo");
            VaccinateActionUtils.updateJson(encounterJson, "simserial", "no simserial property in enketo");
            VaccinateActionUtils.updateJson(encounterJson, "phonenumber", "no phonenumber property in enketo");


            String data = XML.toString(formSubmission);
            return data;
        } catch (Exception e) {
            Log.e(EditFormSubmissionWrapper.class.getName(), "", e);
        }
        return null;
    }

    private void editJson(JSONObject encounterJson, String field, String currentValue, String newValue){
      try {
          JSONObject fieldJson = VaccinateActionUtils.find(encounterJson, field);
          if (fieldJson != null) {
              if(fieldJson.has("content")) {
                  if (fieldJson.getString("content").equals(currentValue)) {
                      VaccinateActionUtils.updateJson(encounterJson, field, newValue);
                  }
              } else {
                  VaccinateActionUtils.updateJson(encounterJson, field, newValue);
              }
          }
      }catch (JSONException e){
          Log.e(getClass().getName(), "", e);
      }
    }

    public String getEntityId() {
        return entityId;
    }

    public String getFormName() {
        return formName;
    }

    public JSONObject getOverrides() {
        return VaccinateActionUtils.retrieveFieldOverides(metaData);
    }

}

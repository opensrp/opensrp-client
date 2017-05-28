package org.ei.opensrp.path.domain;

import java.io.Serializable;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Nov-15.
 */
public class Vaccine_types implements Serializable {

    private Long id;
    private int doses;
    private String name;
    private String openmrs_parent_entity_id;
    private String openmrs_date_concept_id;
    private String openmrs_dose_concept_id;

    public Vaccine_types(Long id, int doses, String name, String openmrs_parent_entity_id, String openmrs_date_concept_id, String openmrs_dose_concept_id) {
        this.id = id;
        this.doses = doses;
        this.name = name;
        this.openmrs_parent_entity_id = openmrs_parent_entity_id;
        this.openmrs_date_concept_id = openmrs_date_concept_id;
        this.openmrs_dose_concept_id = openmrs_dose_concept_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDoses() {
        return doses;
    }

    public void setDoses(int doses) {
        this.doses = doses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenmrs_parent_entity_id() {
        return openmrs_parent_entity_id;
    }

    public void setOpenmrs_parent_entity_id(String openmrs_parent_entity_id) {
        this.openmrs_parent_entity_id = openmrs_parent_entity_id;
    }

    public String getOpenmrs_dose_concept_id() {
        return openmrs_dose_concept_id;
    }

    public void setOpenmrs_dose_concept_id(String openmrs_dose_concept_id) {
        this.openmrs_dose_concept_id = openmrs_dose_concept_id;
    }

    public String getOpenmrs_date_concept_id() {
        return openmrs_date_concept_id;
    }

    public void setOpenmrs_date_concept_id(String openmrs_date_concept_id) {
        this.openmrs_date_concept_id = openmrs_date_concept_id;
    }
}

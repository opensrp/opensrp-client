package org.ei.opensrp.path.domain;

/**
 * Created by raihan@mpower-social.com on 21-MAy-16.
 */
public class Vaccine_names {

    private long id;
    private String name;
    private String vaccine_type_id;
    private String reference_vaccine_id;
    private String due_days;
    private String Client_type;
    private String Dose_no;

    public Vaccine_names(long id, String name, String vaccine_type_id, String due_days, String reference_vaccine_id, String client_type, String dose_no) {
        this.id = id;
        this.name = name;
        this.vaccine_type_id = vaccine_type_id;
        this.due_days = due_days;
        this.reference_vaccine_id = reference_vaccine_id;
        Client_type = client_type;
        Dose_no = dose_no;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVaccine_type_id() {
        return vaccine_type_id;
    }

    public void setVaccine_type_id(String vaccine_type_id) {
        this.vaccine_type_id = vaccine_type_id;
    }

    public String getReference_vaccine_id() {
        return reference_vaccine_id;
    }

    public void setReference_vaccine_id(String reference_vaccine_id) {
        this.reference_vaccine_id = reference_vaccine_id;
    }

    public String getDue_days() {
        return due_days;
    }

    public void setDue_days(String due_days) {
        this.due_days = due_days;
    }

    public String getClient_type() {
        return Client_type;
    }

    public void setClient_type(String client_type) {
        Client_type = client_type;
    }

    public String getDose_no() {
        return Dose_no;
    }

    public void setDose_no(String dose_no) {
        Dose_no = dose_no;
    }
}

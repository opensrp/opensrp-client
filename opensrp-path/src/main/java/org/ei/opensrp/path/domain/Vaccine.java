package org.ei.opensrp.path.domain;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Nov-15.
 */
public class Vaccine {

    private int id;
    private String vaccineName;

    //default constructor
    public Vaccine(){    }


    public Vaccine(int id, String vaccineName){
        this.id=id;
        this.vaccineName=vaccineName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }
}

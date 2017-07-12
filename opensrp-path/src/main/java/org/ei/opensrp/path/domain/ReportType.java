package org.ei.opensrp.path.domain;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Nov-15.
 */
public class ReportType {

    private int id;
    private String type;

    public ReportType(){}

    public ReportType(int id ,String type){
    this.id=id;
        this.type=type;

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

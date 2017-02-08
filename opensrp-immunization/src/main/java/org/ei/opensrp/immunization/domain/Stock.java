package org.ei.opensrp.immunization.domain;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Nov-15.
 */
public class Stock {
    private int id ;
    private int vId;
    private int wasted ;
    private int received ;
    private int balanced;
    private int used;
    private String eventDate;
    private int typeReport ;
    private int target ;
    private Vaccine vaccine;
    private ReportType reportType;



    /**
     * default constructor
     */
    public Stock(){}

    /**
     *
     * @param id as integer
     * @param vId as integer
     */
    public Stock(int id , int vId ){
        this.id=id;
        this.vId=vId;
    }

    public Stock(int id , int vId , int wasted, int received , int balanced , int used ,String eventDate, int reportId, int target){

        this.id=id;
        this.vId=vId;
        this.wasted=wasted;
        this.received=received;
        this.balanced=balanced;
        this.used=used;
        this.eventDate=eventDate;
        this.typeReport=reportId;
        this.target=target;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getvId() {
        return vId;
    }

    public void setvId(int vId) {
        this.vId = vId;
    }


    public int getWasted() {
        return wasted;
    }

    public void setWasted(int wasted) {
        this.wasted = wasted;
    }

    public int getReceived() {
        return received;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getBalanced() {
        return balanced;
    }

    public void setBalanced(int balanced) {
        this.balanced = balanced;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getTypeReport() {
        return typeReport;
    }

    public void setTypeReport(int typeReport) {
        this.typeReport = typeReport;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }
}

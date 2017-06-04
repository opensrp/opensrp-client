package org.ei.opensrp.path.domain;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by raihan@mpower-social.com on 18-May-17.
 */
public class Stock {
    private Long id ;
    private String vaccine_type_id;
    private String transaction_type ;
    private String providerid ;
    private int value;
    private Long  date_created;
    private String to_from;
    private String sync_status ;
    private Long date_updated ;

    public static String issued = "issued";
    public static String received = "received";
    public static String loss_adjustment = "loss_adjustment";

    public Stock(Long id, String transaction_type, String providerid, int value, Long date_created, String to_from, String sync_status, Long date_updated, String vaccine_type_id) {
        this.id = id;
        this.transaction_type = transaction_type;
        this.providerid = providerid;
        this.value = value;
        this.date_created = date_created;
        this.to_from = to_from;
        this.sync_status = sync_status;
        this.date_updated = date_updated;
        this.vaccine_type_id = vaccine_type_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVaccine_type_id() {
        return vaccine_type_id;
    }

    public void setVaccine_type_id(String vaccine_type_id) {
        this.vaccine_type_id = vaccine_type_id;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Long  getDate_created() {
        return date_created;
    }

    public void setDate_created(Long date_created) {
        this.date_created = date_created;
    }

    public String getTo_from() {
        return to_from;
    }

    public void setTo_from(String to_from) {
        this.to_from = to_from;
    }

    public Long getUpdatedAt() {
        return date_updated;
    }

    public void setUpdatedAt(Long date_updated) {
        this.date_updated = date_updated;
    }

    public String getSyncStatus() {
        return sync_status;
    }

    public void setSyncStatus(String sync_status) {
        this.sync_status = sync_status;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }
}

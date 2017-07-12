package org.ei.opensrp.path.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jason Rogena - jrogena@ona.io on 15/06/2017.
 */

public class MonthlyTally extends Tally implements Serializable {
    @JsonProperty
    private Date dateSent;
    @JsonProperty
    private Date month;
    @JsonProperty
    private boolean edited;

    public MonthlyTally() {
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}

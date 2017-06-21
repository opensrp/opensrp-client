package org.ei.opensrp.path.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jason Rogena - jrogena@ona.io on 15/06/2017.
 */

public class DailyTally extends Tally implements Serializable {
    @JsonProperty
    protected Date day;

    public DailyTally() {
        super();
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }
}

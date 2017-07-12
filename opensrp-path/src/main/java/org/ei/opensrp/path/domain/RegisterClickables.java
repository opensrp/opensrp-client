package org.ei.opensrp.path.domain;

import java.io.Serializable;

/**
 * Created by keyman on 24/02/2017.
 */
public class RegisterClickables implements Serializable {

    private boolean recordWeight;

    private boolean recordAll;

    public void setRecordWeight(boolean recordWeight) {
        this.recordWeight = recordWeight;
    }

    public boolean isRecordWeight() {
        return recordWeight;
    }

    public void setRecordAll(boolean recordAll) {
        this.recordAll = recordAll;
    }

    public boolean isRecordAll() {
        return recordAll;
    }
}

package org.ei.opensrp.path.wrapper;



import org.ei.opensrp.domain.Vaccine;

import java.util.List;
import java.util.Map;

/**
 * Created by onaio on 14/09/2017.
 */

public class VaccineViewRecordUpdateWrapper extends BaseViewRecordUpdateWrapper {

    private List<Vaccine> vaccines;
    private Map<String, Object> nv = null;

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public void setNv(Map<String, Object> nv) {
        this.nv = nv;
    }

    public Map<String, Object> getNv() {
        return nv;
    }
}

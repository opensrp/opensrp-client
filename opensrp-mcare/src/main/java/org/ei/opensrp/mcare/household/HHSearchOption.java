package org.ei.opensrp.mcare.household;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

import java.util.ArrayList;
import java.util.List;

public class HHSearchOption implements FilterOption {
    private final String criteria;

    public HHSearchOption(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public String name() {
        return Context.getInstance().applicationContext().getResources().getString(R.string.hh_search_hint);
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        boolean result = false;
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
//        AllCommonsRepository allElcoRepository = new AllCommonsRepository("elco");
        if(!result) {
            if(currentclient.getDetails().get("FWHOHFNAME") != null) {
                if (currentclient.getDetails().get("FWHOHFNAME").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("FWJIVHHID") != null) {
                if (currentclient.getDetails().get("FWJIVHHID").contains(criteria)) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("FWGOBHHID") != null) {
                if (currentclient.getDetails().get("FWGOBHHID").contains(criteria)) {
                    result = true;
                }
            }
        }
        if(!result) {
            AllCommonsRepository allElcoRepository = Context.getInstance().allCommonsRepositoryobjects("ec_elco");
            ArrayList<String> list = new ArrayList<String>();
            list.add((currentclient.entityId()));
            List<CommonPersonObject> allchildelco = allElcoRepository.findByRelational_IDs(list);
            for (int i = 0; i < allchildelco.size(); i++) {
                if(allchildelco.get(i).getDetails().get("FWWOMFNAME") != null) {
                    if (allchildelco.get(i).getDetails().get("FWWOMFNAME").toLowerCase().contains(criteria.toLowerCase())) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}

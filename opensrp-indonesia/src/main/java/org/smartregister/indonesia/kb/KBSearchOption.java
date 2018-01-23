package org.smartregister.indonesia.kb;

import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.indonesia.R;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.dialog.FilterOption;

public class KBSearchOption implements FilterOption {
    private final String criteria;

    public KBSearchOption(String criteria) {
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
            if(currentclient.getDetails().get("namaLengkap") != null) {
                if (currentclient.getDetails().get("namaLengkap").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("namaSuami") != null) {
                if (currentclient.getDetails().get("namaSuami").contains(criteria)) {
                    result = true;
                }
            }
        }

        return result;
    }
}

package org.ei.opensrp.gizi.gizi;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.gizi.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

public class GiziSearchOption implements FilterOption {
    private final String criteria;

    public GiziSearchOption(String criteria) {
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
            if(currentclient.getDetails().get("namaBayi") != null) {
                if (currentclient.getDetails().get("namaBayi").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("namaIbu") != null) {
                if (currentclient.getDetails().get("namaIbu").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("namaOrtu") != null) {
                if (currentclient.getDetails().get("namaOrtu").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }
        if(!result) {
            if(currentclient.getDetails().get("dusun") != null) {
                if (currentclient.getDetails().get("dusun").toLowerCase().contains(criteria.toLowerCase())) {
                    result = true;
                }
            }
        }

        return result;
    }
}

package org.ei.opensrp.core.template;

import org.ei.opensrp.view.contract.SmartRegisterClients;

/**
 * Created by Maimoona on 1/12/2017.
 */

public class CommonSortingOption implements SortingOption {
    String sortOptionName;
    String query;

    public CommonSortingOption(String sortOptionName,String sortQuery) {
        this.query = sortQuery;
        this.sortOptionName = sortOptionName;
    }

    @Override
    public String name() {
        return sortOptionName;
    }

    @Override
    public String sort() {
        return query;
    }
}

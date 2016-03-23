package org.ei.opensrp.domain.mapper;

import static org.ei.opensrp.domain.ServiceProvided.TT_1_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.TT_2_SERVICE_PROVIDED_NAME;
import static org.ei.opensrp.domain.ServiceProvided.TT_BOOSTER_SERVICE_PROVIDED_NAME;

public enum TTMapper {
    ttbooster(TT_BOOSTER_SERVICE_PROVIDED_NAME), tt1(TT_1_SERVICE_PROVIDED_NAME), tt2(TT_2_SERVICE_PROVIDED_NAME);

    private String value;

    TTMapper(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

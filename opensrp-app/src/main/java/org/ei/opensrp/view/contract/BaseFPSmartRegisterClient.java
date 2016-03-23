package org.ei.opensrp.view.contract;

import org.ei.opensrp.domain.FPMethod;

public interface BaseFPSmartRegisterClient extends SmartRegisterClient {

    public FPMethod fpMethod();

    public String familyPlanningMethodChangeDate();

    public String numberOfOCPDelivered();

    public String numberOfCondomsSupplied();

    public String numberOfCentchromanPillsDelivered();

    public String iudPerson();

    public String iudPlace();

}

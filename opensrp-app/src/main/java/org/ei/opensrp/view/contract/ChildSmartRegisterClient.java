package org.ei.opensrp.view.contract;


import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.ChildServiceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.opensrp.AllConstants.COMMA_WITH_SPACE;
import static org.ei.opensrp.AllConstants.SPACE;
import static org.ei.opensrp.util.DateUtil.formatDate;
import static org.ei.opensrp.util.StringUtil.replaceAndHumanizeWithInitCapText;

public interface ChildSmartRegisterClient extends SmartRegisterClient {
List<String> illnessAcronyms = new ArrayList<String>(
        Arrays.asList(Context.getInstance().getStringResource(R.string.str_child_illness_ari),
                Context.getInstance().getStringResource(R.string.str_child_illness_sam)));

    public static class ChildSickStatus {
        public static ChildSickStatus noDiseaseStatus = new ChildSickStatus(null, null, null);

        private String diseases;
        private String otherDiseases;
        private String date;

        public ChildSickStatus(String diseases, String otherDiseases, String date) {
            this.diseases = diseases;
            this.otherDiseases = otherDiseases;
            this.date = date;
        }

        public String diseases() {
            return getDiseasesCapitalizeIfAcronymsOrHumanize() + (isBlank(otherDiseases) ? "" : (", " + replaceAndHumanizeWithInitCapText(otherDiseases, SPACE, COMMA_WITH_SPACE)));
        }

        private String getDiseasesCapitalizeIfAcronymsOrHumanize() {
            return replaceAndHumanizeWithInitCapText(illnessAcronyms.contains(diseases) ? diseases.toUpperCase() : diseases, SPACE, COMMA_WITH_SPACE);
        }

        public String date() {
            return formatDate(date);
        }
    }

    public String gender();

    public String weight();

    public String thayiCardNumber();

    public String motherEcNumber();

    public String dateOfBirth();

    public String fatherName();

    public String motherName();

    public String locationStatus();

    public List<ServiceProvidedDTO> serviceProvided();

    public ServiceProvidedDTO lastServiceProvided();

    public ServiceProvidedDTO illnessVisitServiceProvided();

    public ChildSickStatus sickStatus();

    public boolean isBcgDone();

    public boolean isOpvDone();

    public boolean isHepBDone();

    public boolean isPentavDone();

    public String bcgDoneDate();

    public String opvDoneDate();

    public String hepBDoneDate();

    public String pentavDoneDate();

    public boolean isMeaslesDone();

    public boolean isOpvBoosterDone();

    public boolean isDptBoosterDone();

    public boolean isVitaminADone();

    public String measlesDoneDate();

    public String opvBoosterDoneDate();

    public String dptBoosterDoneDate();

    public String vitaminADoneDate();

    public List<AlertDTO> alerts();

    public AlertDTO getAlert(ChildServiceType measles);

    public boolean isDataError();
}

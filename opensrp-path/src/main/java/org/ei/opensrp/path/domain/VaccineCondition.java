package org.ei.opensrp.path.domain;

import org.ei.opensrp.domain.Vaccine;
import org.ei.opensrp.path.db.VaccineRepo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public abstract class VaccineCondition {
    protected final VaccineRepo.Vaccine vaccine;
    private static final String TYPE_GIVEN = "given";
    public static final String TYPE_NOT_GIVEN = "not_given";

    public VaccineCondition(VaccineRepo.Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public static VaccineCondition init(String vaccineCategory, JSONObject conditionData) throws JSONException {
        if (conditionData.getString("type").equals(TYPE_GIVEN)) {
            GivenCondition.Comparison comparison = GivenCondition.getComparison(conditionData.getString("comparison"));
            VaccineRepo.Vaccine vaccine = VaccineRepo.getVaccine(conditionData.getString("vaccine"),
                    vaccineCategory);

            if (comparison != null && vaccine != null) {
                return new GivenCondition(vaccine, conditionData.getString("value"), comparison);
            }
        } else if (conditionData.getString("type").equals(TYPE_NOT_GIVEN)) {
            VaccineRepo.Vaccine vaccine = VaccineRepo.getVaccine(conditionData.getString("vaccine"),
                    vaccineCategory);

            if (vaccine != null) {
                return new NotGivenCondition(vaccine);
            }
        }

        return null;
    }

    public abstract boolean passes(List<Vaccine> issuedVaccines);

    public static class NotGivenCondition extends VaccineCondition {

        public NotGivenCondition(VaccineRepo.Vaccine vaccine) {
            super(vaccine);
        }

        @Override
        public boolean passes(List<Vaccine> issuedVaccines) {
            // Check if vaccine was not given
            boolean given = false;

            // TODO: Check if name used in VaccineRepo.Vaccine is the same as the one in Vaccine
            for (Vaccine curVaccine : issuedVaccines) {
                if (curVaccine.getName().equalsIgnoreCase(vaccine.display())) {
                    given = true;
                    break;
                }
            }

            return !given;
        }
    }

    public static class GivenCondition extends VaccineCondition {
        public static enum Comparison {
            EXACTLY("exactly"),
            AT_LEAST("at_least"),
            AT_MOST("at_most");

            private final String name;

            Comparison(String name) {
                this.name = name;
            }
        }

        public static Comparison getComparison(String name) {
            for (Comparison curComparison : Comparison.values()) {
                if (curComparison.name.equalsIgnoreCase(name)) {
                    return curComparison;
                }
            }

            return null;
        }

        private final Comparison comparison;
        private final String value;

        public GivenCondition(VaccineRepo.Vaccine vaccine, String value, Comparison comparison) {
            super(vaccine);
            this.value = value;
            this.comparison = comparison;
        }

        @Override
        public boolean passes(List<Vaccine> issuedVaccines) {
            boolean result = false;

            // Check if vaccine was given at all
            Vaccine comparisonVaccine = null;
            for (Vaccine curVaccine : issuedVaccines) {
                if (curVaccine.getName().equalsIgnoreCase(vaccine.display())) {
                    comparisonVaccine = curVaccine;
                    break;
                }
            }

            if (comparisonVaccine != null) {
                Calendar comparisonDate = Calendar.getInstance();
                VaccineSchedule.standardiseCalendarDate(comparisonDate);
                comparisonDate = VaccineSchedule.addOffsetToCalendar(comparisonDate, value);

                Calendar vaccinationDate = Calendar.getInstance();
                vaccinationDate.setTime(comparisonVaccine.getDate());
                VaccineSchedule.standardiseCalendarDate(vaccinationDate);

                switch (comparison) {
                    case EXACTLY:
                        result = comparisonDate.getTimeInMillis() == vaccinationDate.getTimeInMillis();
                        break;
                    case AT_LEAST:
                        result = vaccinationDate.getTimeInMillis() >= comparisonDate.getTimeInMillis();
                        break;
                    case AT_MOST:
                        result = vaccinationDate.getTimeInMillis() <= comparisonDate.getTimeInMillis();
                        break;
                }
            }

            return result;
        }
    }
}

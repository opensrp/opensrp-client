package org.ei.opensrp.path.domain;

import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.repository.VaccineRepository;

import java.util.List;

/**
 * Created by Jason Rogena - jrogena@ona.io on 19/05/2017.
 */

public abstract class VaccineCondition {
    private final VaccineRepo.Vaccine vaccine;

    public VaccineCondition(VaccineRepo.Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public abstract boolean passes(List<Vaccine> issuedVaccines);

    public static class NotGivenCondition extends VaccineCondition {

        public NotGivenCondition(VaccineRepo.Vaccine vaccine) {
            super(vaccine);
        }

        @Override
        public boolean passes(List<Vaccine> issuedVaccines) {
            return false;
        }
    }

    public static class GivenCondition extends VaccineCondition {
        public static enum When {
            EXACTLY("exactly"),
            AT_LEAST("at_least"),
            AT_MOST("at_most");

            private final String name;
            When(String name) {
                this.name = name;
            }
        }

        private final When when;

        public GivenCondition(VaccineRepo.Vaccine vaccine, When when) {
            super(vaccine);
            this.when = when;
        }

        @Override
        public boolean passes(List<Vaccine> issuedVaccines) {
            return false;
        }
    }
}
